/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import com.google.inject.Injector;
import com.sitewhere.microservice.configuration.model.instance.InstanceConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;

import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;

/**
 * Microservice that supports dynamic monitoring of configuration.
 */
public interface IConfigurableMicroservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration>
	extends IMicroservice<F, C> {

    /**
     * Get microservice configuration class.
     * 
     * @return
     */
    Class<C> getConfigurationClass();

    /**
     * Get instance configuration monitor.
     * 
     * @return
     */
    IInstanceConfigurationMonitor getInstanceConfigurationMonitor();

    /**
     * Get microservice configuration monitor.
     * 
     * @return
     */
    IMicroserviceConfigurationMonitor getMicroserviceConfigurationMonitor();

    /**
     * Get scripting management interface.
     * 
     * @return
     */
    IScriptManagement getScriptManagement();

    /**
     * Wait for configuration to be loaded.
     * 
     * @throws SiteWhereException
     */
    void waitForConfigurationReady() throws SiteWhereException;

    /**
     * Get most recent k8s instance resource.
     * 
     * @return
     */
    SiteWhereInstance getLastInstanceResource();

    /**
     * Get most recent k8s microservice resource.
     * 
     * @return
     */
    SiteWhereMicroservice getLastMicroserviceResource();

    /**
     * Get the currently active configuration.
     * 
     * @return
     */
    C getMicroserviceConfiguration();

    /**
     * Creates a Guice module used to build microservice components based on the
     * active configuration.
     * 
     * @return
     */
    IMicroserviceModule<C> createConfigurationModule();

    /**
     * Get most recently configured microservice configuration module.
     * 
     * @return
     */
    IMicroserviceModule<C> getMicroserviceConfigurationModule();

    /**
     * Get most recently configured instance configuraion module.
     * 
     * @return
     */
    IInstanceModule getInstanceConfigurationModule();

    /**
     * Get Guice injector which allows access to tenant engine components which have
     * been configured via the module.
     */
    Injector getInjector();

    /**
     * Initialize configurable components.
     * 
     * @param instance
     * @param microservice
     * @param monitor
     * @throws SiteWhereException
     */
    void configurationInitialize(InstanceConfiguration instance, C microservice, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Start configurable components.
     * 
     * @param instance
     * @param microservice
     * @param monitor
     * @throws SiteWhereException
     */
    void configurationStart(InstanceConfiguration instance, C microservice, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Stop configurable components.
     * 
     * @param instance
     * @param microservice
     * @param monitor
     * @throws SiteWhereException
     */
    void configurationStop(InstanceConfiguration instance, C microservice, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Terminate configurable components.
     * 
     * @param instance
     * @param microservice
     * @param monitor
     * @throws SiteWhereException
     */
    void configurationTerminate(InstanceConfiguration instance, C microservice, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Initialize the current configuration.
     * 
     * @throws SiteWhereException
     */
    void initializeConfiguration() throws SiteWhereException;

    /**
     * Start the current configuration.
     * 
     * @throws SiteWhereException
     */
    void startConfiguration() throws SiteWhereException;

    /**
     * Stop the current configuration.
     * 
     * @throws SiteWhereException
     */
    void stopConfiguration() throws SiteWhereException;

    /**
     * Terminate the current configuration.
     * 
     * @throws SiteWhereException
     */
    void terminateConfiguration() throws SiteWhereException;

    /**
     * Restart the current configuration.
     * 
     * @throws SiteWhereException
     */
    void restartConfiguration() throws SiteWhereException;
}