/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import com.google.inject.Injector;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
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
     * Get script configuration monitor.
     * 
     * @return
     */
    IScriptConfigurationMonitor getScriptConfigurationMonitor();

    /**
     * Get script version configuration monitor.
     * 
     * @return
     */
    IScriptVersionConfigurationMonitor getScriptVersionConfigurationMonitor();

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
}