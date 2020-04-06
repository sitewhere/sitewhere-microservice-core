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
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sitewhere.microservice.Microservice;
import com.sitewhere.microservice.MicroserviceApplication;
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
import com.sitewhere.spi.microservice.configuration.IInstanceSpecUpdates;
import com.sitewhere.spi.microservice.configuration.IInstanceStatusUpdates;
import com.sitewhere.spi.microservice.configuration.IMicroserviceConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IMicroserviceConfigurationMonitor;
import com.sitewhere.spi.microservice.configuration.IMicroserviceModule;
import com.sitewhere.spi.microservice.configuration.IScriptConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IScriptConfigurationMonitor;
import com.sitewhere.spi.microservice.configuration.IScriptSpecUpdates;
import com.sitewhere.spi.microservice.configuration.IScriptVersionConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IScriptVersionConfigurationMonitor;
import com.sitewhere.spi.microservice.configuration.IScriptVersionSpecUpdates;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;

import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersion;

/**
 * Base class for microservices that monitor the configuration folder for
 * updates.
 */
public abstract class ConfigurableMicroservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration>
	extends Microservice<F, C> implements IConfigurableMicroservice<F, C>, IInstanceConfigurationListener,
	IMicroserviceConfigurationListener, IScriptConfigurationListener, IScriptVersionConfigurationListener {

    /** Instance configuration monitor */
    private IInstanceConfigurationMonitor instanceMonitor;

    /** Microservice configuration monitor */
    private IMicroserviceConfigurationMonitor microserviceMonitor;

    /** Script configuration monitor */
    private IScriptConfigurationMonitor scriptMonitor;

    /** Script version configuration monitor */
    private IScriptVersionConfigurationMonitor scriptVersionMonitor;

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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Wait for instance/microservice config available.
	waitForConfigurationReady();

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
	this.instanceMonitor = new InstanceConfigurationMonitor(getKubernetesClient(), informers);
	getInstanceConfigurationMonitor().getListeners().add(this);
	getInstanceConfigurationMonitor().start();

	// Add shared informer for microservice configuration monitoring.
	this.microserviceMonitor = new MicroserviceConfigurationMonitor(getKubernetesClient(), informers);
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

	handleInstanceUpdated(instance, specUpdates, statusUpdates, true);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener#
     * onInstanceUpdated(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onInstanceUpdated(SiteWhereInstance instance, IInstanceSpecUpdates specUpdates,
	    IInstanceStatusUpdates statusUpdates) {
	handleInstanceUpdated(instance, specUpdates, statusUpdates, false);
    }

    /**
     * Handle instance resource add/update.
     * 
     * @param instance
     * @param specUpdates
     * @param statusUpdates
     * @param isNew
     */
    protected void handleInstanceUpdated(SiteWhereInstance instance, IInstanceSpecUpdates specUpdates,
	    IInstanceStatusUpdates statusUpdates, boolean isNew) {
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

	// Restart microservice if config changed.
	if (!isNew) {
	    try {
		getLogger().info("Restarting microservice due to instance configuration update.");
		restart();
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to restart microservice.", e);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener#
     * onInstanceDeleted(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onInstanceDeleted(SiteWhereInstance instance) {
	this.lastInstanceResource = null;
	try {
	    MicroserviceApplication.stopMicroservice(this);
	} catch (SiteWhereException e) {
	    getLogger().error("Error stopping deleted microservice.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationListener#onMicroserviceAdded(io.sitewhere.k8s.crd.
     * microservice.SiteWhereMicroservice)
     */
    @Override
    public void onMicroserviceAdded(SiteWhereMicroservice microservice) {
	handleMicroserviceUpdated(microservice, true);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationListener#onMicroserviceUpdated(io.sitewhere.k8s.crd
     * .microservice.SiteWhereMicroservice)
     */
    @Override
    public void onMicroserviceUpdated(SiteWhereMicroservice microservice) {
	handleMicroserviceUpdated(microservice, false);
    }

    /**
     * Handle an add/update for the microservice k8s resource.
     * 
     * @param microservice
     * @param isNew
     */
    protected void handleMicroserviceUpdated(SiteWhereMicroservice microservice, boolean isNew) {
	// Only process updates for the functional area of this microservice.
	if (!getIdentifier().getPath()
		.equals(getMicroservice().getSiteWhereKubernetesClient().getFunctionalArea(microservice))) {
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

	// Restart microservice if config changed.
	if (wasConfigured && configUpdated) {
	    try {
		getLogger().info("Restarting microservice due to microservice configuration update.");
		restart();
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to restart microservice.", e);
	    }
	}
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

	// Signal that configuration is available.
	getConfigurationAvailable().countDown();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationListener#onMicroserviceDeleted(io.sitewhere.k8s.crd
     * .microservice.SiteWhereMicroservice)
     */
    @Override
    public void onMicroserviceDeleted(SiteWhereMicroservice microservice) {
	this.lastMicroserviceResource = null;
	try {
	    MicroserviceApplication.stopMicroservice(this);
	} catch (SiteWhereException e) {
	    getLogger().error("Error stopping deleted microservice.", e);
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
     * @see
     * com.sitewhere.spi.microservice.configuration.IScriptConfigurationListener#
     * onScriptAdded(io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript)
     */
    @Override
    public void onScriptAdded(SiteWhereScript script) {
	getLogger().info(String.format("Script %s was added.", script.getMetadata().getName()));
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IScriptConfigurationListener#
     * onScriptUpdated(io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript,
     * com.sitewhere.spi.microservice.configuration.IScriptSpecUpdates)
     */
    @Override
    public void onScriptUpdated(SiteWhereScript script, IScriptSpecUpdates updates) {
	getLogger().info(String.format("Script %s was updated..\n%s\n\n", script.getMetadata().getName(),
		MarshalUtils.marshalJsonAsPrettyString(updates)));
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IScriptConfigurationListener#
     * onScriptDeleted(io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript)
     */
    @Override
    public void onScriptDeleted(SiteWhereScript script) {
	getLogger().info(String.format("Script %s was deleted.", script.getMetadata().getName()));
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IScriptVersionConfigurationListener#onScriptVersionAdded(io.sitewhere.k8s.crd
     * .tenant.scripting.version.SiteWhereScriptVersion)
     */
    @Override
    public void onScriptVersionAdded(SiteWhereScriptVersion version) {
	getLogger().info(String.format("Script version %s was added.", version.getMetadata().getName()));
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IScriptVersionConfigurationListener#onScriptVersionUpdated(io.sitewhere.k8s.
     * crd.tenant.scripting.version.SiteWhereScriptVersion,
     * com.sitewhere.spi.microservice.configuration.IScriptVersionSpecUpdates)
     */
    @Override
    public void onScriptVersionUpdated(SiteWhereScriptVersion version, IScriptVersionSpecUpdates updates) {
	getLogger().info(String.format("Script version %s was updated.\n%s\n\n", version.getMetadata().getName(),
		MarshalUtils.marshalJsonAsPrettyString(updates)));
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IScriptVersionConfigurationListener#onScriptVersionDeleted(io.sitewhere.k8s.
     * crd.tenant.scripting.version.SiteWhereScriptVersion)
     */
    @Override
    public void onScriptVersionDeleted(SiteWhereScriptVersion version) {
	getLogger().info(String.format("Script version %s was deleted.", version.getMetadata().getName()));
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

    protected CountDownLatch getConfigurationAvailable() {
	return configurationAvailable;
    }
}