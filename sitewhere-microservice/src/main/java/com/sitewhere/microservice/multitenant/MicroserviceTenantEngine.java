/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.SimpleLifecycleStep;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.scripting.ScriptManager;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleStep;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;
import com.sitewhere.spi.microservice.scripting.IScriptManager;

import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.configuration.TenantEngineConfigurationTemplate;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Component within microservice which runs for a specific tenant. Each tenant
 * engine has its own configuration and data model.
 * 
 * @param <T>
 */
public abstract class MicroserviceTenantEngine<T extends ITenantEngineConfiguration>
	extends TenantEngineLifecycleComponent implements IMicroserviceTenantEngine<T> {

    /** Tenant resource */
    private SiteWhereTenant tenantResource;

    /** Tenant engine resource */
    private SiteWhereTenantEngine tenantEngineResource;

    /** Active configuration */
    private T activeConfiguration;

    /** Guice injector containing configured components */
    private Injector injector;

    /** Script manager */
    private IScriptManager scriptManager = new ScriptManager();

    /** Dataset bootstrap manager */
    private DatasetBootstrapManager bootstrapManager = new DatasetBootstrapManager();

    public MicroserviceTenantEngine(SiteWhereTenantEngine tenantEngineResource) {
	this.tenantEngineResource = tenantEngineResource;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#getName(
     * )
     */
    @Override
    public String getName() {
	return getTenantEngineResource().getMetadata().getName();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getTenantResource()
     */
    @Override
    public SiteWhereTenant getTenantResource() {
	return tenantResource;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getTenantEngineResource()
     */
    @Override
    public SiteWhereTenantEngine getTenantEngineResource() {
	return tenantEngineResource;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getActiveConfiguration()
     */
    @Override
    public T getActiveConfiguration() {
	return activeConfiguration;
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent#
     * getTenantEngine()
     */
    @Override
    public IMicroserviceTenantEngine<T> getTenantEngine() {
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	resolveTenantResource();

	// Refresh active configuration from k8s resource.
	refreshConfiguration();

	// Load tenant engine components from Guice injector.
	loadEngineComponents();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize tenant engine " + getName());

	// Initialize script manager.
	init.addInitializeStep(this, getScriptManager(), true);

	// Initialize bootstrap manager.
	init.addInitializeStep(this, getBootstrapManager(), true);

	// Execute initialization steps.
	init.execute(monitor);

	// Initialize discoverable beans.
	initializeDiscoverableBeans();

	// Allow subclass to execute initialization logic.
	tenantInitialize(monitor);
    }

    /**
     * Resolve the tenant resource references by tenant engine label.
     * 
     * @throws SiteWhereException
     */
    protected void resolveTenantResource() throws SiteWhereException {
	String tenantToken = getTenantEngineResource().getMetadata().getLabels()
		.get(ResourceLabels.LABEL_SITEWHERE_TENANT);
	if (tenantToken == null) {
	    throw new SiteWhereException("Tenant engine does not have a tenant label. Unable to resolve.");
	}
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	getLogger().debug(String.format("Resolving tenant '%s' in namespace '%s'.", tenantToken, namespace));
	SiteWhereTenant tenant = getMicroservice().getSiteWhereKubernetesClient().getTenants().inNamespace(namespace)
		.withName(tenantToken).get();
	if (tenant == null) {
	    throw new SiteWhereException(
		    String.format("Tenant engine label references a tenant '%s' which does not exist.", tenantToken));
	}
	this.tenantResource = tenant;
    }

    /**
     * Parse tenant engine configuraion into expected configuration type.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected T parseConfiguration() throws SiteWhereException {
	try {
	    JsonNode configuration = getTenantEngineResource().getSpec().getConfiguration();
	    return MarshalUtils.unmarshalJsonNode(configuration, getConfigurationClass());
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException("Unable to parse tenant engine configuration.", e);
	} catch (Throwable t) {
	    throw new SiteWhereException("Unhandled exception parsing tenant engine configuration.", t);
	}
    }

    /**
     * Refresh active configuration from k8s resource.
     * 
     * @throws SiteWhereException
     */
    protected void refreshConfiguration() throws SiteWhereException {
	this.activeConfiguration = parseConfiguration();
	if (getLogger().isDebugEnabled()) {
	    getLogger().debug(String.format("Refreshed tenant engine configuration: \n%s\n\n",
		    MarshalUtils.marshalJsonAsPrettyString(getActiveConfiguration())));
	}
	try {
	    this.injector = Guice.createInjector(getConfigurationModule());
	} catch (CreationException e) {
	    throw new SiteWhereException("Guice configuration module failed to initialize.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start tenant engine " + getName());

	// Start tenant script manager.
	start.addStartStep(this, getScriptManager(), true);

	// Execute startup steps.
	start.execute(monitor);

	// Start discoverable beans.
	startDiscoverableBeans();

	// Allow subclass to execute startup logic.
	tenantStart(monitor);

	// Execute bootstrap in background
	startNestedComponent(getBootstrapManager(), monitor, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {

	// Stop the bootstrap manager.
	stopNestedComponent(getBootstrapManager(), monitor);

	// Allow subclass to execute shutdown logic.
	tenantStop(monitor);

	// Stop discoverable beans.
	stopDiscoverableBeans();

	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop tenant engine " + getName());

	// Stop tenant script manager.
	stop.addStopStep(this, getScriptManager());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will terminate components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Terminate tenant engine " + getName());

	// Terminate tenant script manager.
	stop.addTerminateStep(this, getScriptManager());

	// Execute terminate steps.
	stop.execute(monitor);

	// Terminate discoverable beans.
	terminateDiscoverableBeans();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
    }

    /**
     * Initialize discoverable beans in configuration.
     * 
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep initializeDiscoverableBeans() throws SiteWhereException {
	return new SimpleLifecycleStep("Initialize discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Map<String, IDiscoverableTenantLifecycleComponent> components = context
		// .getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
		//
		// for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		// initializeNestedComponent(component, monitor, component.isRequired());
		// }
	    }
	};
    }

    /**
     * Start discoverable beans in configuration.
     * 
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep startDiscoverableBeans() throws SiteWhereException {
	return new SimpleLifecycleStep("Start discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Map<String, IDiscoverableTenantLifecycleComponent> components = context
		// .getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
		//
		// for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		// startNestedComponent(component, monitor, component.isRequired());
		// }
	    }
	};
    }

    /**
     * Stop discoverable beans in configuration.
     * 
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep stopDiscoverableBeans() throws SiteWhereException {
	return new SimpleLifecycleStep("Stop discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Map<String, IDiscoverableTenantLifecycleComponent> components = context
		// .getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
		//
		// for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		// component.lifecycleStop(monitor);
		// }
	    }
	};
    }

    /**
     * Terminate discoverable beans in configuration.
     * 
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep terminateDiscoverableBeans() throws SiteWhereException {
	return new SimpleLifecycleStep("Terminate discoverable beans") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Map<String, IDiscoverableTenantLifecycleComponent> components = context
		// .getBeansOfType(IDiscoverableTenantLifecycleComponent.class);
		//
		// for (IDiscoverableTenantLifecycleComponent component : components.values()) {
		// component.lifecycleTerminate(monitor);
		// }
	    }
	};
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getInjector()
     */
    @Override
    public Injector getInjector() {
	return injector;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationTemplate()
     */
    @Override
    public TenantEngineConfigurationTemplate getConfigurationTemplate() throws SiteWhereException {
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getDatasetTemplate()
     */
    @Override
    public TenantEngineDatasetTemplate getDatasetTemplate() throws SiteWhereException {
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getTenantBootstrapPrerequisites()
     */
    @Override
    public IFunctionIdentifier[] getTenantBootstrapPrerequisites() {
	return new IFunctionIdentifier[0];
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * waitForTenantDatasetBootstrapped(com.sitewhere.spi.microservice.
     * IFunctionIdentifier)
     */
    @Override
    public void waitForTenantDatasetBootstrapped(IFunctionIdentifier identifier) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getScriptManager()
     */
    @Override
    public IScriptManager getScriptManager() {
	return scriptManager;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getBootstrapManager()
     */
    @Override
    public DatasetBootstrapManager getBootstrapManager() {
	return bootstrapManager;
    }
}