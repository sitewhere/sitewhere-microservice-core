/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sitewhere.microservice.Microservice;
import com.sitewhere.microservice.MicroserviceUtils;
import com.sitewhere.microservice.configuration.model.instance.InstanceConfiguration;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.LifecycleProgressContext;
import com.sitewhere.microservice.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.microservice.scripting.KubernetesScriptManagement;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IInstanceConfigurationMonitor;
import com.sitewhere.spi.microservice.configuration.IInstanceModule;
import com.sitewhere.spi.microservice.configuration.IInstanceSpecUpdates;
import com.sitewhere.spi.microservice.configuration.IInstanceStatusUpdates;
import com.sitewhere.spi.microservice.configuration.IMicroserviceConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IMicroserviceConfigurationMonitor;
import com.sitewhere.spi.microservice.configuration.IMicroserviceModule;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;

import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;

/**
 * Base class for microservices that monitor the configuration folder for
 * updates.
 */
public abstract class ConfigurableMicroservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration>
	extends Microservice<F, C>
	implements IConfigurableMicroservice<F, C>, IInstanceConfigurationListener, IMicroserviceConfigurationListener {

    /** Instance configuration monitor */
    private IInstanceConfigurationMonitor instanceMonitor;

    /** Microservice configuration monitor */
    private IMicroserviceConfigurationMonitor microserviceMonitor;

    /** Script management implementation */
    private IScriptManagement scriptManagement;

    /** Latest instance resource */
    private SiteWhereInstance lastInstanceResource;

    /** Latest microservice resource */
    private SiteWhereMicroservice lastMicroserviceResource;

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

    /** Latch for configuration availability */
    private CountDownLatch configurationAvailable = new CountDownLatch(1);

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getLastInstanceResource()
     */
    @Override
    public SiteWhereInstance getLastInstanceResource() {
	return this.lastInstanceResource;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getLastMicroserviceResource()
     */
    @Override
    public SiteWhereMicroservice getLastMicroserviceResource() {
	return this.lastMicroserviceResource;
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
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener#
     * onInstanceAdded(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onInstanceAdded(SiteWhereInstance instance) {
	// Reflect configuration updated if not null.
	InstanceSpecUpdates specUpdates = new InstanceSpecUpdates();
	specUpdates.setConfigurationUpdated(instance.getSpec().getConfiguration() != null);

	// No status updates.
	InstanceStatusUpdates statusUpdates = new InstanceStatusUpdates();

	onInstanceUpdated(instance, specUpdates, statusUpdates);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener#
     * onInstanceUpdated(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onInstanceUpdated(SiteWhereInstance instance, IInstanceSpecUpdates specUpdates,
	    IInstanceStatusUpdates statusUpdates) {
	// Save resource reference.
	this.lastInstanceResource = instance;

	// Skip partially configured instance.
	if (instance.getSpec().getConfiguration() == null) {
	    getLogger().info("Skipping instance configuration which has not yet been bootstrapped.");
	    return;
	}

	// Only interested in configuration updates.
	if (!specUpdates.isConfigurationUpdated()) {
	    getLogger().info("Skipping instance update where configuration was not updated.");
	    return;
	}

	// Save updated resource and parse configuration.
	try {
	    this.instanceConfiguration = MarshalUtils.unmarshalJsonNode(instance.getSpec().getConfiguration(),
		    InstanceConfiguration.class);
	    this.instanceConfigurationModule = new InstanceModule(getInstanceConfiguration());
	} catch (JsonProcessingException e) {
	    getLogger().error(String.format("Invalid instance configuration (%s). Content is: \n\n%s\n", e.getMessage(),
		    instance.getSpec().getConfiguration()));
	    return;
	}

	// Handle updated configuration.
	onConfigurationUpdated();
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener#
     * onInstanceDeleted(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onInstanceDeleted(SiteWhereInstance instance) {
	this.lastInstanceResource = null;
	onConfigurationDeleted();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationListener#onMicroserviceAdded(io.sitewhere.k8s.crd.
     * microservice.SiteWhereMicroservice)
     */
    @Override
    public void onMicroserviceAdded(SiteWhereMicroservice microservice) {
	onMicroserviceUpdated(microservice);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationListener#onMicroserviceUpdated(io.sitewhere.k8s.crd
     * .microservice.SiteWhereMicroservice)
     */
    @Override
    public void onMicroserviceUpdated(SiteWhereMicroservice microservice) {
	// Only process updates for the functional area of this microservice.
	if (!getIdentifier().getPath().equals(MicroserviceUtils.getFunctionalArea(microservice))) {
	    return;
	}

	boolean wasConfigured = getLastMicroserviceResource() != null
		&& getLastMicroserviceResource().getSpec().getConfiguration() != null;
	boolean configUpdated = wasConfigured && !getLastMicroserviceResource().getSpec().getConfiguration()
		.equals(microservice.getSpec().getConfiguration());

	getLogger().info(String.format("Handling microservice resource update. configured=%s updated=%s",
		String.valueOf(wasConfigured), String.valueOf(configUpdated)));

	// Save updated resource and parse configuration.
	this.lastMicroserviceResource = microservice;
	try {
	    C configuration = MarshalUtils.unmarshalJsonNode(microservice.getSpec().getConfiguration(),
		    getConfigurationClass());
	    this.microserviceConfiguration = configuration;
	    this.microserviceConfigurationModule = createConfigurationModule();
	} catch (JsonProcessingException e) {
	    getLogger().error(String.format("Invalid microservice configuration (%s). Content is: \n\n%s\n",
		    e.getMessage(), microservice.getSpec().getConfiguration()));
	}

	// If configuration was not updated, skip context restart.
	if (wasConfigured && !configUpdated) {
	    return;
	}

	// Handle updated configuration.
	if (!wasConfigured || configUpdated) {
	    onConfigurationUpdated();
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationListener#onMicroserviceDeleted(io.sitewhere.k8s.crd
     * .microservice.SiteWhereMicroservice)
     */
    @Override
    public void onMicroserviceDeleted(SiteWhereMicroservice microservice) {
	this.lastMicroserviceResource = null;
	onConfigurationDeleted();
    }

    /**
     * Called when configuration is added or updated.
     */
    protected void onConfigurationUpdated() {
	if (getInstanceConfigurationModule() == null) {
	    getLogger().info("Waiting for instance configuration to be loaded before starting.");
	    return;
	}
	if (getMicroserviceConfigurationModule() == null) {
	    getLogger().info("Waiting for microservice configuration to be loaded before starting.");
	    return;
	}

	// Create Guice injector with the instance and microservice bindings.
	try {
	    this.injector = Guice.createInjector(getInstanceConfigurationModule(),
		    getMicroserviceConfigurationModule());
	} catch (CreationException e) {
	    getLogger().error("Guice configuration module failed to initialize.", e);
	}

	getMicroserviceOperationsService().execute(new Runnable() {

	    @Override
	    public void run() {
		try {
		    if (getLifecycleStatus() == LifecycleStatus.Stopped) {
			getLogger().info("Initializing and starting microservice configuration...");
			initializeAndStart();
		    } else {
			getLogger().info("Detected configuration update. Restarting configuration...");
			restartConfiguration();
		    }
		    getConfigurationAvailable().countDown();
		} catch (SiteWhereException e) {
		    getLogger().error("Unable to restart microservice.", e);
		}
	    }
	});
    }

    /**
     * Called when configuration for instance or microservice has been deleted.
     */
    protected void onConfigurationDeleted() {
	getMicroserviceOperationsService().execute(new Runnable() {

	    @Override
	    public void run() {
		try {
		    stopAndTerminate();
		} catch (SiteWhereException e) {
		    getLogger().error("Unable to stop microservice.", e);
		}
	    }
	});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create script management support.
	this.scriptManagement = new KubernetesScriptManagement();

	// Wait for instance/microservice config available.
	waitForConfigurationReady();

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
	this.instanceMonitor = new InstanceConfigurationMonitor(getKubernetesClient(), informers);
	getInstanceConfigurationMonitor().getListeners().add(this);
	getInstanceConfigurationMonitor().start();

	// Add shared informer for microservice configuration monitoring.
	this.microserviceMonitor = new MicroserviceConfigurationMonitor(getKubernetesClient(), informers);
	getMicroserviceConfigurationMonitor().getListeners().add(this);
	getMicroserviceConfigurationMonitor().start();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInstanceConfiguration()
     */
    @Override
    public InstanceConfiguration getInstanceConfiguration() {
	return this.instanceConfiguration;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationInitialize(com.sitewhere.microservice.configuration.model.
     * instance.InstanceConfiguration,
     * com.sitewhere.spi.microservice.IMicroserviceConfiguration,
     * com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationInitialize(InstanceConfiguration instance, C microservice,
	    ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// if (local != null) {
	// initializeDiscoverableBeans(local).execute(monitor);
	// }
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationStart(com.sitewhere.microservice.configuration.model.instance.
     * InstanceConfiguration,
     * com.sitewhere.spi.microservice.IMicroserviceConfiguration,
     * com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationStart(InstanceConfiguration instance, C microservice, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	// if (local != null) {
	// startDiscoverableBeans(local).execute(monitor);
	// }
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationStop(com.sitewhere.microservice.configuration.model.instance.
     * InstanceConfiguration,
     * com.sitewhere.spi.microservice.IMicroserviceConfiguration,
     * com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationStop(InstanceConfiguration instance, C microservice, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	// if (local != null) {
	// stopDiscoverableBeans(local).execute(monitor);
	// }
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * configurationTerminate(com.sitewhere.microservice.configuration.model.
     * instance.InstanceConfiguration,
     * com.sitewhere.spi.microservice.IMicroserviceConfiguration,
     * com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationTerminate(InstanceConfiguration instance, C microservice,
	    ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// if (local != null) {
	// terminateDiscoverableBeans(local).execute(monitor);
	// }
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
	// Stop and terminate the configuration components.
	getLogger().info("Shutting down configurable microservice components...");
	stopConfiguration();
	terminateConfiguration();

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

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * initializeConfiguration()
     */
    @Override
    public void initializeConfiguration() throws SiteWhereException {
	try {
	    // Load microservice configuration.
	    SiteWhereInstance instance = getLastInstanceResource();
	    if (instance == null || instance.getSpec() == null || instance.getSpec().getConfiguration() == null) {
		throw new SiteWhereException("Global instance configuration not set. Unable to start microservice.");
	    }

	    // Load instance YAML configuration as JSON.
	    try {
		JsonNode instanceJson = instance.getSpec().getConfiguration();
		InstanceConfiguration instanceConfiguration = MarshalUtils.unmarshalJsonNode(instanceJson,
			InstanceConfiguration.class);
		if (getLogger().isDebugEnabled()) {
		    getLogger().debug(String.format("\nINSTANCE CONFIG:\n\n%s\n",
			    MarshalUtils.marshalJsonAsPrettyString(instanceConfiguration)));
		}
		this.instanceConfiguration = instanceConfiguration;
	    } catch (JsonProcessingException e) {
		throw new SiteWhereException(String.format("Invalid instance configuration (%s). Content is: \n\n%s\n",
			e.getMessage(), instance.getSpec().getConfiguration()));
	    }

	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Initialize microservice configuration."), getMicroservice());
	    long start = System.currentTimeMillis();
	    getLogger().info("Initializing from updated configuration...");
	    configurationInitialize(getInstanceConfiguration(), getMicroserviceConfiguration(), monitor);
	    if (getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw getMicroservice().getLifecycleError();
	    }
	    getLogger().info("Microservice configuration '" + getMicroservice().getName() + "' initialized in "
		    + (System.currentTimeMillis() - start) + "ms.");
	} catch (Throwable t) {
	    getLogger().error("Unable to initialize microservice configuration '" + getMicroservice().getName() + "'.",
		    t);
	    throw t;
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * startConfiguration()
     */
    @Override
    public void startConfiguration() throws SiteWhereException {
	try {
	    // Start microservice.
	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Start microservice configuration."), getMicroservice());
	    long start = System.currentTimeMillis();
	    configurationStart(getInstanceConfiguration(), getMicroserviceConfiguration(), monitor);
	    if (getMicroservice().getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw getMicroservice().getLifecycleError();
	    }
	    getLogger().info("Microservice configuration '" + getMicroservice().getName() + "' started in "
		    + (System.currentTimeMillis() - start) + "ms.");
	} catch (Throwable t) {
	    getLogger().error("Unable to start microservice configuration '" + getMicroservice().getName() + "'.", t);
	    throw t;
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * stopConfiguration()
     */
    @Override
    public void stopConfiguration() throws SiteWhereException {
	try {
	    // Stop microservice.
	    if (getLifecycleStatus() != LifecycleStatus.Stopped) {
		ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Stop microservice configuration."), getMicroservice());
		long start = System.currentTimeMillis();
		configurationStop(getInstanceConfiguration(), getMicroserviceConfiguration(), monitor);
		if (getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    throw getMicroservice().getLifecycleError();
		}
		getMicroservice().getLogger().info("Microservice configuration '" + getMicroservice().getName()
			+ "' stopped in " + (System.currentTimeMillis() - start) + "ms.");
	    }
	} catch (Throwable t) {
	    getLogger().error("Unable to stop microservice configuration '" + getMicroservice().getName() + "'.", t);
	    throw t;
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * terminateConfiguration()
     */
    @Override
    public void terminateConfiguration() throws SiteWhereException {
	try {
	    // Terminate microservice.
	    if (getLifecycleStatus() != LifecycleStatus.Terminated) {
		ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Terminate microservice configuration."), this);
		long start = System.currentTimeMillis();
		configurationTerminate(getInstanceConfiguration(), getMicroserviceConfiguration(), monitor);
		if (getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    throw getMicroservice().getLifecycleError();
		}
		getLogger().info("Microservice configuration '" + getName() + "' terminated in "
			+ (System.currentTimeMillis() - start) + "ms.");
	    }
	} catch (Throwable t) {
	    getLogger().error("Unable to terminate microservice configuration '" + getName() + "'.", t);
	    throw t;
	}
    }

    /**
     * Initialize and start the microservice components.
     * 
     * @throws SiteWhereException
     */
    protected void initializeAndStart() throws SiteWhereException {
	initializeConfiguration();
	startConfiguration();
    }

    /**
     * Stop and terminate the microservice components.
     * 
     * @throws SiteWhereException
     */
    protected void stopAndTerminate() throws SiteWhereException {
	stopConfiguration();
	terminateConfiguration();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * restartConfiguration()
     */
    @Override
    public void restartConfiguration() throws SiteWhereException {
	stopAndTerminate();
	initializeAndStart();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * waitForConfigurationReady()
     */
    @Override
    public void waitForConfigurationReady() throws SiteWhereException {
	if (getConfigurationAvailable().getCount() == 0) {
	    return;
	}
	try {
	    getLogger().info("Waiting for configuration to be loaded...");
	    getConfigurationAvailable().await();
	} catch (InterruptedException e) {
	    throw new SiteWhereException("Interrupted while waiting for instance configuration to become available.");
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getInstanceConfigurationMonitor()
     */
    @Override
    public IInstanceConfigurationMonitor getInstanceConfigurationMonitor() {
	return instanceMonitor;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getMicroserviceConfigurationMonitor()
     */
    @Override
    public IMicroserviceConfigurationMonitor getMicroserviceConfigurationMonitor() {
	return microserviceMonitor;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getScriptManagement()
     */
    @Override
    public IScriptManagement getScriptManagement() {
	return scriptManagement;
    }

    protected CountDownLatch getConfigurationAvailable() {
	return configurationAvailable;
    }
}