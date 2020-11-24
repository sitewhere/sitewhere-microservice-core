/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import java.util.logging.Level;

import org.jboss.logmanager.LogManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sitewhere.microservice.Microservice;
import com.sitewhere.microservice.configuration.model.instance.InstanceConfiguration;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.scripting.KubernetesScriptManagement;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IInstanceConfigurationMonitor;
import com.sitewhere.spi.microservice.configuration.IInstanceModule;
import com.sitewhere.spi.microservice.configuration.IMicroserviceConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IMicroserviceConfigurationMonitor;
import com.sitewhere.spi.microservice.configuration.IMicroserviceModule;
import com.sitewhere.spi.microservice.configuration.IScriptConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IScriptConfigurationMonitor;
import com.sitewhere.spi.microservice.configuration.IScriptVersionConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IScriptVersionConfigurationMonitor;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;

import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.SiteWhereInstanceSpec;
import io.sitewhere.k8s.crd.microservice.MicroserviceLoggingEntry;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroserviceSpec;

/**
 * Base class for microservices that monitor the configuration folder for
 * updates.
 */
public abstract class ConfigurableMicroservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration>
	extends Microservice<F, C> implements IConfigurableMicroservice<F, C>, IInstanceConfigurationListener,
	IMicroserviceConfigurationListener, IScriptConfigurationListener, IScriptVersionConfigurationListener {

    /** Instance configuration monitor */
    private IInstanceConfigurationMonitor instanceConfigurationMonitor;

    /** Microservice configuration monitor */
    private IMicroserviceConfigurationMonitor microserviceConfigurationMonitor;

    /** Script configuration monitor */
    private IScriptConfigurationMonitor scriptMonitor;

    /** Script version configuration monitor */
    private IScriptVersionConfigurationMonitor scriptVersionMonitor;

    /** Script management implementation */
    private IScriptManagement scriptManagement;

    /** Latest instance resource */
    private SiteWhereInstance instanceResource;

    /** Latest microservice resource */
    private SiteWhereMicroservice microserviceResource;

    /** Instance configuration */
    private InstanceConfiguration instanceConfiguration;

    /** Active configuration */
    private C microserviceConfiguration;

    /** Active microservice configuration module */
    private IMicroserviceModule<C> microserviceConfigurationModule;

    /** Active instance configuration module */
    private IInstanceModule instanceConfigurationModule;

    /** Guice injector containing configured components */
    private Injector injector;

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getLastInstanceResource()
     */
    @Override
    public SiteWhereInstance getLastInstanceResource() {
	return this.instanceResource;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getLastMicroserviceResource()
     */
    @Override
    public SiteWhereMicroservice getLastMicroserviceResource() {
	return this.microserviceResource;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getMicroserviceConfiguration()
     */
    @Override
    public C getMicroserviceConfiguration() {
	return this.microserviceConfiguration;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#
     * getMicroserviceConfigurationModule()
     */
    @Override
    public IMicroserviceModule<C> getMicroserviceConfigurationModule() {
	return this.microserviceConfigurationModule;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#getInstanceConfigurationModule()
     */
    @Override
    public IInstanceModule getInstanceConfigurationModule() {
	return this.instanceConfigurationModule;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInjector()
     */
    @Override
    public Injector getInjector() {
	return this.injector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Load instance/microservice configuration from Kubernetes.
	loadConfigurationFromK8s();

	// Handle standard initialization.
	super.initialize(monitor);

	// Create script management support.
	this.scriptManagement = new KubernetesScriptManagement();

	// Organizes steps for initializing microservice.
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize script management.
	initialize.addInitializeStep(this, getScriptManagement(), true);

	// Start script management.
	initialize.addStartStep(this, getScriptManagement(), true);

	// Execute initialization steps.
	initialize.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.microservice.Microservice#createKubernetesResourceControllers(
     * io.fabric8.kubernetes.client.informers.SharedInformerFactory)
     */
    @Override
    public void createKubernetesResourceControllers(SharedInformerFactory informers) throws SiteWhereException {
	super.createKubernetesResourceControllers(informers);

	// Add shared informer for instance configuration monitoring.
	this.instanceConfigurationMonitor = new InstanceConfigurationMonitor(this.instanceResource,
		getKubernetesClient(), informers);
	getInstanceConfigurationMonitor().getListeners().add(this);
	getInstanceConfigurationMonitor().start();

	// Add shared informer for microservice configuration monitoring.
	this.microserviceConfigurationMonitor = new MicroserviceConfigurationMonitor(this.microserviceResource,
		getKubernetesClient(), informers);
	getMicroserviceConfigurationMonitor().getListeners().add(this);
	getMicroserviceConfigurationMonitor().start();

	// Add shared informer for script configuration monitoring.
	this.scriptMonitor = new ScriptConfigurationMonitor(getKubernetesClient(), informers, this);
	getScriptConfigurationMonitor().getListeners().add(this);
	getScriptConfigurationMonitor().start();

	// Add shared informer for script version configuration monitoring.
	this.scriptVersionMonitor = new ScriptVersionConfigurationMonitor(getKubernetesClient(), informers, this);
	getScriptVersionConfigurationMonitor().getListeners().add(this);
	getScriptVersionConfigurationMonitor().start();
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
	super.terminate(monitor);

	// Organizes steps for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop script management.
	stop.addStopStep(this, getScriptManagement());

	// Terminate script management.
	stop.addTerminateStep(this, getScriptManagement());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /**
     * Load the instance and microservice k8s metadata based on environment
     * settings.
     * 
     * @throws SiteWhereException
     */
    protected void loadConfigurationFromK8s() throws SiteWhereException {
	SiteWhereInstance instance = getSiteWhereKubernetesClient().getInstances()
		.withName(getInstanceSettings().getKubernetesNamespace()).get();
	if (instance == null) {
	    throw new SiteWhereException(String.format("No instance found with name '%s'. Aborting startup.",
		    getInstanceSettings().getKubernetesNamespace()));
	}
	handleInstanceUpdated(instance);

	SiteWhereMicroservice microservice = getSiteWhereKubernetesClient().getMicroservices()
		.inNamespace(getInstanceSettings().getKubernetesNamespace())
		.withName(getInstanceSettings().getKubernetesName()).get();
	if (microservice == null) {
	    throw new SiteWhereException(
		    String.format("No microservice found in namespace '%s' with name '%s'. Aborting startup.",
			    getInstanceSettings().getKubernetesNamespace(), getInstanceSettings().getKubernetesName()));
	}
	handleMicroserviceUpdated(microservice);

	// Handle updated configuration.
	onConfigurationUpdated();
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener#
     * onInstanceSpecificationUpdated(io.sitewhere.k8s.crd.instance.
     * SiteWhereInstanceSpec)
     */
    @Override
    public void onInstanceSpecificationUpdated(SiteWhereInstanceSpec specification) {
	try {
	    handleInstanceUpdated(getInstanceConfigurationMonitor().getResource());
	    onConfigurationUpdated();
	} catch (SiteWhereException e) {
	    getLogger().warn("Exception handling instance configuration update.", e);
	}
    }

    /**
     * Handle updated k8s metadata for instance.
     * 
     * @param instance
     * @throws SiteWhereException
     */
    protected void handleInstanceUpdated(SiteWhereInstance instance) throws SiteWhereException {
	// Save resource reference.
	this.instanceResource = instance;

	// Skip partially configured instance.
	if (instance.getSpec().getConfiguration() == null) {
	    getLogger().info("Skipping instance configuration which has not yet been bootstrapped.");
	    return;
	}

	// Save updated resource and parse configuration.
	try {
	    this.instanceConfiguration = MarshalUtils.unmarshalJsonNode(instance.getSpec().getConfiguration(),
		    InstanceConfiguration.class);
	    this.instanceConfigurationModule = new InstanceModule(getInstanceConfiguration());
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException(String.format("Invalid instance configuration (%s). Content is: \n\n%s\n",
		    e.getMessage(), instance.getSpec().getConfiguration()));
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationListener#onMicroserviceSpecificationUpdated(io.
     * sitewhere.k8s.crd.microservice.SiteWhereMicroserviceSpec)
     */
    @Override
    public void onMicroserviceSpecificationUpdated(SiteWhereMicroserviceSpec specification) {
	try {
	    handleMicroserviceUpdated(getMicroserviceConfigurationMonitor().getResource());
	    onConfigurationUpdated();
	} catch (SiteWhereException e) {
	    getLogger().warn("Exception handling microservice configuration update.", e);
	}
    }

    /**
     * Handle updated microservice k8s resource.
     * 
     * @param microservice
     * @throws SiteWhereException
     */
    protected void handleMicroserviceUpdated(SiteWhereMicroservice microservice) throws SiteWhereException {
	// Validate that functional area in k8s metadata matches expected value.
	if (!getIdentifier().getPath().equals(microservice.getSpec().getFunctionalArea())) {
	    throw new SiteWhereException(
		    String.format("Functional area in k8s metadata('%s') does not match expected value of %s.",
			    microservice.getSpec().getFunctionalArea()));
	}

	// Check for logging updates and process them.
	handleLoggingConfigurationUpdates(microservice);

	// Save updated resource and parse configuration.
	this.microserviceResource = microservice;
	try {
	    C configuration = MarshalUtils.unmarshalJsonNode(microservice.getSpec().getConfiguration(),
		    getConfigurationClass());
	    this.microserviceConfiguration = configuration;
	    this.microserviceConfigurationModule = createConfigurationModule();
	    getLogger().debug(String.format("Successfully handled configuraion update for '%s'.",
		    microservice.getMetadata().getName()));
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException(String.format("Invalid microservice configuration (%s). Content is: \n\n%s\n",
		    e.getMessage(), microservice.getSpec().getConfiguration()));
	}
    }

    /**
     * Perform delta against previous logging configuration. Process updates if
     * there were changes.
     * 
     * @param updated
     */
    protected void handleLoggingConfigurationUpdates(SiteWhereMicroservice updated) {
	// Flag for whether logging was updated.
	boolean loggingConfigured = updated.getSpec().getLogging() != null;

	if (loggingConfigured) {
	    getLogger().info("Processing logger overrides...");
	    for (MicroserviceLoggingEntry entry : updated.getSpec().getLogging().getOverrides()) {
		try {
		    Level level = Level.parse(entry.getLevel().toUpperCase());
		    LogManager.getLogManager().getLogger(entry.getLogger()).setLevel(level);
		    getLogger().info(String.format("Set log level for '%s' to %s", entry.getLogger(), level.getName()));
		} catch (IllegalArgumentException e) {
		    getLogger().warn(String.format("Invalid log level specifed for '%s': %s", entry.getLogger(),
			    entry.getLevel()));
		}
	    }
	    getLogger().info("Logger overrides applied.");
	} else {
	    getLogger().info("No logger overrides specified.");
	}
    }

    /**
     * Process updated configuration.
     * 
     * @throws SiteWhereException
     */
    protected void onConfigurationUpdated() throws SiteWhereException {
	// Create Guice injector with the instance and microservice bindings.
	try {
	    this.injector = Guice.createInjector(getInstanceConfigurationModule(),
		    getMicroserviceConfigurationModule());
	} catch (CreationException e) {
	    getLogger().error("Guice configuration module failed to initialize.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getInstanceConfiguration()
     */
    @Override
    public InstanceConfiguration getInstanceConfiguration() {
	return this.instanceConfiguration;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInstanceConfigurationMonitor()
     */
    @Override
    public IInstanceConfigurationMonitor getInstanceConfigurationMonitor() {
	return this.instanceConfigurationMonitor;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getMicroserviceConfigurationMonitor()
     */
    @Override
    public IMicroserviceConfigurationMonitor getMicroserviceConfigurationMonitor() {
	return this.microserviceConfigurationMonitor;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getScriptConfigurationMonitor()
     */
    @Override
    public IScriptConfigurationMonitor getScriptConfigurationMonitor() {
	return scriptMonitor;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getScriptVersionConfigurationMonitor()
     */
    @Override
    public IScriptVersionConfigurationMonitor getScriptVersionConfigurationMonitor() {
	return scriptVersionMonitor;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getScriptManagement()
     */
    @Override
    public IScriptManagement getScriptManagement() {
	return scriptManagement;
    }
}