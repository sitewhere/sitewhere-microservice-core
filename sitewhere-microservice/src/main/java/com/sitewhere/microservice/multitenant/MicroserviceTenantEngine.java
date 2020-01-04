/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.CaseFormat;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.sitewhere.microservice.configuration.model.instance.PersistenceConfigurations;
import com.sitewhere.microservice.exception.ConcurrentK8sUpdateException;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.SimpleLifecycleStep;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptManager;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleStep;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineBootstrapper;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineSpecUpdateOperation;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineStatusUpdateOperation;
import com.sitewhere.spi.microservice.scripting.IScriptManager;

import io.fabric8.kubernetes.client.utils.URLUtils;
import io.sitewhere.k8s.crd.ApiConstants;
import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.common.BootstrapState;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.configuration.TenantConfigurationTemplate;
import io.sitewhere.k8s.crd.tenant.dataset.TenantDatasetTemplate;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngineList;
import io.sitewhere.k8s.crd.tenant.engine.configuration.TenantEngineConfigurationTemplate;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Component within microservice which runs for a specific tenant. Each tenant
 * engine has its own configuration and data model.
 * 
 * @param <T>
 */
public abstract class MicroserviceTenantEngine<T extends ITenantEngineConfiguration>
	extends TenantEngineLifecycleComponent implements IMicroserviceTenantEngine<T> {

    /** Number of milliseconds to wait between dataset bootstrap checks */
    private static final int DATASET_BOOTSTRAP_CHECK_INTERVAL = 5000;

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

    /** Tenant engine bootstrapper */
    private TenantEngineBootstrapper tenantEngineBootstrapper = new TenantEngineBootstrapper();

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

	// Initialize engine bootstrapper.
	init.addInitializeStep(this, getTenantEngineBootstrapper(), true);

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
	SiteWhereTenant tenant = getMicroservice().getSiteWhereKubernetesClient().getTenants().inNamespace(namespace)
		.withName(tenantToken).get();
	if (tenant == null) {
	    throw new SiteWhereException(
		    String.format("Tenant engine label references a tenant '%s' which does not exist.", tenantToken));
	}
	this.tenantResource = tenant;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * loadTenantEngineResource()
     */
    @Override
    public SiteWhereTenantEngine loadTenantEngineResource() throws SiteWhereException {
	String engineName = getTenantEngineResource().getMetadata().getName();
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereTenantEngine found = getMicroservice().getSiteWhereKubernetesClient().getTenantEngines()
		.inNamespace(namespace).withName(engineName).get();
	if (found == null) {
	    throw new SiteWhereException(String.format("No tenant engine resource found with name '%s'.", engineName));
	}
	return found;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * updateTenantEngineResource(io.sitewhere.k8s.crd.tenant.engine.
     * SiteWhereTenantEngine)
     */
    @Override
    public SiteWhereTenantEngine updateTenantEngineResource(SiteWhereTenantEngine engine) throws SiteWhereException {
	String engineName = getTenantEngineResource().getMetadata().getName();
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	return getMicroservice().getSiteWhereKubernetesClient().getTenantEngines().inNamespace(namespace)
		.withName(engineName).createOrReplace(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * updateTenantEngineStatus(io.sitewhere.k8s.crd.tenant.engine.
     * SiteWhereTenantEngine)
     */
    @Override
    public SiteWhereTenantEngine updateTenantEngineStatus(SiteWhereTenantEngine engine) throws SiteWhereException {
	try {
	    final String statusUri = URLUtils.join(getMicroservice().getKubernetesClient().getMasterUrl().toString(),
		    "apis", ApiConstants.SITEWHERE_API_GROUP, ApiConstants.SITEWHERE_API_VERSION, "namespaces",
		    engine.getMetadata().getNamespace(), ApiConstants.SITEWHERE_TENANT_ENGINE_CRD_PLURAL,
		    engine.getMetadata().getName(), "status");
	    final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
		    MarshalUtils.marshalJson(engine));
	    Response response = getMicroservice().getKubernetesClient().getHttpClient()
		    .newCall(new Request.Builder().method("PUT", requestBody).url(statusUri).build()).execute();
	    byte[] content = response.body().bytes();
	    response.close();
	    JsonNode json = MarshalUtils.marshalJsonNode(content);
	    SiteWhereTenantEngine result = MarshalUtils.unmarshalJsonNode(json, SiteWhereTenantEngine.class);
	    return result;
	} catch (JsonProcessingException e) {
	    throw new ConcurrentK8sUpdateException("Tenant engine status update failed due to conflict.", e);
	} catch (Throwable e) {
	    throw new SiteWhereException("Unhandled exception updating tenant engine status.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * executeTenantEngineSpecUpdate(com.sitewhere.spi.microservice.multitenant.
     * ITenantEngineSpecUpdateOperation)
     */
    @Override
    public SiteWhereTenantEngine executeTenantEngineSpecUpdate(ITenantEngineSpecUpdateOperation operation)
	    throws SiteWhereException {
	return operation.execute(this);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * executeTenantEngineStatusUpdate(com.sitewhere.spi.microservice.multitenant.
     * ITenantEngineStatusUpdateOperation)
     */
    @Override
    public SiteWhereTenantEngine executeTenantEngineStatusUpdate(ITenantEngineStatusUpdateOperation operation)
	    throws SiteWhereException {
	return operation.execute(this);
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
	    // Inherit existing bindings from the microsevice injector.
	    this.injector = ((IConfigurableMicroservice<?, ?>) getMicroservice()).getInjector()
		    .createChildInjector(createConfigurationModule());
	    PersistenceConfigurations configs = getInjector().getInstance(PersistenceConfigurations.class);
	    if (configs == null) {
		getLogger().info("Did not find persistence configurations.");
	    } else {
		getLogger().info(String.format("Found persistence configs:\n%s\n\n",
			MarshalUtils.marshalJsonAsPrettyString(configs)));
	    }
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
	startNestedComponent(getTenantEngineBootstrapper(), monitor, true);
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

	// Stop the engine bootstrapper.
	stopNestedComponent(getTenantEngineBootstrapper(), monitor);

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
	String tctName = getTenantResource().getSpec().getConfigurationTemplate();
	if (tctName == null) {
	    throw new SiteWhereException("No tenant configuration template specified.");
	}
	TenantConfigurationTemplate tct = getMicroservice().getSiteWhereKubernetesClient()
		.getTenantConfigurationTemplates().withName(tctName).get();
	if (tct == null) {
	    throw new SiteWhereException(String.format("No tenant configuration template found for '%s'.", tctName));
	}
	String functionName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,
		getMicroservice().getIdentifier().getPath());
	String tectName = tct.getSpec().getTenantEngineTemplates().get(functionName);
	if (tectName == null) {
	    throw new SiteWhereException(
		    String.format("No tenant engine configuration template listed for '%s'.", tectName));
	}
	TenantEngineConfigurationTemplate tect = getMicroservice().getSiteWhereKubernetesClient()
		.getTenantEngineConfigurationTemplates().withName(tectName).get();
	if (tect == null) {
	    throw new SiteWhereException(
		    String.format("No tenant engine configuration template found for '%s'.", tectName));
	}
	return tect;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getDatasetTemplate()
     */
    @Override
    public TenantEngineDatasetTemplate getDatasetTemplate() throws SiteWhereException {
	String tdtName = getTenantResource().getSpec().getDatasetTemplate();
	if (tdtName == null) {
	    throw new SiteWhereException("No tenant dataset template specified.");
	}
	TenantDatasetTemplate tdt = getMicroservice().getSiteWhereKubernetesClient().getTenantDatasetTemplates()
		.withName(tdtName).get();
	if (tdt == null) {
	    throw new SiteWhereException(String.format("No tenant dataset template found for '%s'.", tdtName));
	}
	String functionName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,
		getMicroservice().getIdentifier().getPath());
	String tedtName = tdt.getSpec().getTenantEngineTemplates().get(functionName);
	if (tedtName == null) {
	    throw new SiteWhereException(String.format("No tenant engine dataset template listed for '%s'.", tedtName));
	}
	TenantEngineDatasetTemplate tedt = getMicroservice().getSiteWhereKubernetesClient()
		.getTenantEngineDatasetTemplates().withName(tedtName).get();
	if (tedt == null) {
	    throw new SiteWhereException(String.format("No tenant engine dataset template found for '%s'.", tedtName));
	}
	return tedt;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getTenantEngineBootstrapper()
     */
    @Override
    public ITenantEngineBootstrapper getTenantEngineBootstrapper() throws SiteWhereException {
	return tenantEngineBootstrapper;
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
     * setDatasetBootstrapBindings(com.sitewhere.microservice.scripting.Binding)
     */
    @Override
    public void setDatasetBootstrapBindings(Binding binding) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * waitForTenantDatasetBootstrapped(com.sitewhere.spi.microservice.
     * IFunctionIdentifier)
     */
    @Override
    public void waitForTenantDatasetBootstrapped(IFunctionIdentifier identifier) throws SiteWhereException {
	while (true) {
	    Map<String, String> labels = new HashMap<>();
	    labels.put(ResourceLabels.LABEL_SITEWHERE_TENANT, getTenantResource().getMetadata().getName());
	    labels.put(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA, identifier.getPath());
	    SiteWhereTenantEngineList matches = getMicroservice().getSiteWhereKubernetesClient().getTenantEngines()
		    .inNamespace(getTenantResource().getMetadata().getNamespace()).withLabels(labels).list();
	    SiteWhereTenantEngine match = null;
	    if (matches.getItems().size() == 1) {
		match = matches.getItems().get(0);
	    } else if (matches.getItems().size() > 1) {
		throw new SiteWhereException(String.format("Multiple tenant engines found for dependent dataset '%s'",
			identifier.getPath()));
	    }
	    if (match != null) {
		getLogger().info(String.format("Using tenant engine for dataset bootstrap detection:\n%s\n\n",
			MarshalUtils.marshalJsonAsPrettyString(match)));
		BootstrapState state = match.getStatus() != null ? match.getStatus().getBootstrapState() : null;
		if (state != null && state == BootstrapState.Bootstrapped) {
		    getLogger().info(String.format("Dataset for '%s' has been bootstrapped.", identifier.getPath()));
		    return;
		}
		getLogger()
			.info(String.format("Waiting for dataset for '%s' to become available. Current state is '%s'.",
				identifier.getPath(), state));
	    } else {
		getLogger()
			.info(String.format("Waiting for dataset for '%s' to become available.", identifier.getPath()));
	    }

	    try {
		Thread.sleep(DATASET_BOOTSTRAP_CHECK_INTERVAL);
	    } catch (InterruptedException e) {
		getLogger().info("Interrupted while waiting for dataset to become available.");
	    }
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * onTenantBootstrapComplete()
     */
    @Override
    public void onTenantBootstrapComplete() {
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getScriptManager()
     */
    @Override
    public IScriptManager getScriptManager() {
	return scriptManager;
    }
}