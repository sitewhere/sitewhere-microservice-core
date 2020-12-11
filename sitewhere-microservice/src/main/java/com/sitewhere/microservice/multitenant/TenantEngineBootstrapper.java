/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import org.graalvm.polyglot.PolyglotException;

import com.sitewhere.microservice.lifecycle.AsyncStartLifecycleComponent;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.microservice.lifecycle.SimpleLifecycleStep;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingUtils;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineBootstrapper;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;

import io.sitewhere.k8s.crd.common.BootstrapState;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngineStatus;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Checks whether a tenant engine is bootstrapped with initial data and, if not,
 * handles the bootstrap process.
 */
public class TenantEngineBootstrapper extends AsyncStartLifecycleComponent implements ITenantEngineBootstrapper {

    public TenantEngineBootstrapper() {
	super(LifecycleComponentType.Other);
    }

    /*
     * @see com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent#
     * asyncStart()
     */
    @Override
    public void asyncStart() throws SiteWhereException {
	ILifecycleProgressMonitor monitor = LifecycleProgressMonitor.createFor("Start", getMicroservice());
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start");

	// Execute bootstrap logic.
	start.addStep(new SimpleLifecycleStep("Bootstrap") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		waitForPrerequisites();
		bootstrap();
	    }
	});

	// Execute start steps.
	start.execute(monitor);
    }

    /**
     * Wait for other external datasets required by this tenant engine to be loaded
     * first.
     * 
     * @throws SiteWhereException
     */
    protected void waitForPrerequisites() throws SiteWhereException {
	for (IFunctionIdentifier function : getTenantEngine().getTenantBootstrapPrerequisites()) {
	    getTenantEngine().waitForTenantDatasetBootstrapped(function);
	}
    }

    /**
     * Bootstrap tenant engine.
     * 
     * @throws SiteWhereException
     */
    protected void bootstrap() throws SiteWhereException {
	if (getTenantEngine().hasExistingDataset()) {
	    getLogger().info("Existing tenant dataset detected. Skipping data bootstrap processing.");
	    setTenantEngineBootstrapState(BootstrapState.Bootstrapped);
	    return;
	}

	// Load latest tenant engine resource from k8s.
	SiteWhereTenantEngine current = getTenantEngine().loadTenantEngineResource();
	if (current.getStatus() != null && current.getStatus().getBootstrapState() == BootstrapState.Bootstrapped) {
	    getLogger().info("Tenant engine already bootstrapped. Skipping data bootstrap processing.");
	    return;
	}

	setTenantEngineBootstrapState(BootstrapState.NotBootstrapped);

	// Load tenant engine dataset template.
	TenantEngineDatasetTemplate template = getTenantEngine().getDatasetTemplate();

	// Bootstrap tenant engine.
	bootstrapTenantEngine(current, template);

	// Execute callback on completion.
	getTenantEngine().onTenantBootstrapComplete();
    }

    /**
     * Bootstrap tenant engine.
     * 
     * @param engine
     * @param template
     * @throws SiteWhereException
     */
    protected void bootstrapTenantEngine(SiteWhereTenantEngine engine, TenantEngineDatasetTemplate template)
	    throws SiteWhereException {
	if (template == null) {
	    getLogger().info(String.format("No dataset template found for functional area '%s'. Skipping bootstrap.",
		    getMicroservice().getIdentifier().getPath()));
	    setTenantEngineBootstrapState(BootstrapState.Bootstrapped);
	    return;
	}
	switch (engine.getStatus().getBootstrapState()) {
	case BootstrapFailed: {
	    getLogger().warn("Skipping tenant engine bootstrap due to previous failure.");
	    break;
	}
	case Bootstrapped: {
	    getLogger().info("Tenant engine already bootstrapped.");
	    break;
	}
	case Bootstrapping: {
	    getLogger().info("Tenant engine already in bootstrapping state.");
	    break;
	}
	case NotBootstrapped: {
	    getLogger().info("Tenant engine not bootstrapped. Running initializer.");
	    runInitializer(template);
	    break;
	}
	}
    }

    /**
     * Bootstrap tenant management using script.
     * 
     * @param template
     * @throws SiteWhereException
     */
    protected void runInitializer(TenantEngineDatasetTemplate template) throws SiteWhereException {
	SiteWhereAuthentication previous = UserContext.getCurrentUser();
	try {
	    UserContext.setContext(getMicroservice().getSystemUser()
		    .getAuthenticationForTenant(getTenantEngine().getTenantResource()));
	    setTenantEngineBootstrapState(BootstrapState.Bootstrapping);

	    String script = template.getSpec().getConfiguration();
	    if (script != null) {
		getLogger().info(String.format("Initializing tenant dataset from template '%s'.",
			template.getMetadata().getName()));
		Binding binding = new Binding();
		binding.setVariable(IScriptVariables.VAR_LOGGER, getLogger());
		getTenantEngine().setDatasetBootstrapBindings(binding);
		ScriptingUtils.run(script, binding);
		getLogger().info(String.format("Completed execution of tenant dataset template '%s'.",
			template.getMetadata().getName()));
	    }
	    setTenantEngineBootstrapState(BootstrapState.Bootstrapped);
	} catch (PolyglotException e) {
	    setTenantEngineBootstrapState(BootstrapState.BootstrapFailed);
	    throw new SiteWhereException("Tenant engine bootstrap failed due to error executing dataset script.", e);
	} catch (Throwable t) {
	    setTenantEngineBootstrapState(BootstrapState.BootstrapFailed);
	    throw new SiteWhereException("Tenant engine bootstrap failed due to unhandled exception.", t);
	} finally {
	    UserContext.setContext(previous);
	}
    }

    /**
     * Attempts to set the tenant engine bootstrap indicator. Handles cases where
     * there is contention for the tenant engine resource update.
     * 
     * @param state
     * @throws SiteWhereException
     */
    protected void setTenantEngineBootstrapState(BootstrapState state) throws SiteWhereException {
	getTenantEngine().executeTenantEngineStatusUpdate(new TenantEngineStatusUpdateOperation() {

	    /*
	     * @see
	     * com.sitewhere.spi.microservice.multitenant.ITenantEngineStatusUpdateOperation
	     * #update(io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngineStatus)
	     */
	    @Override
	    public void update(SiteWhereTenantEngineStatus current) throws SiteWhereException {
		current.setBootstrapState(state);
	    }
	});
    }
}
