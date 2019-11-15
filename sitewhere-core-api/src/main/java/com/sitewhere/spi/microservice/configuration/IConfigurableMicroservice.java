/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

import io.sitewhere.k8s.crd.instance.SiteWhereInstance;

/**
 * Microservice that supports dynamic monitoring of configuration.
 */
public interface IConfigurableMicroservice<T extends IFunctionIdentifier> extends IMicroservice<T> {

    /**
     * Get configuration monitor.
     * 
     * @return
     */
    IInstanceConfigurationMonitor getConfigurationMonitor();

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
     * Initialize configurable components.
     * 
     * @param global
     * @param local
     * @param monitor
     * @throws SiteWhereException
     */
    void configurationInitialize(Object global, Object local, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Start configurable components.
     * 
     * @param global
     * @param local
     * @param monitor
     * @throws SiteWhereException
     */
    void configurationStart(Object global, Object local, ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Stop configurable components.
     * 
     * @param global
     * @param local
     * @param monitor
     * @throws SiteWhereException
     */
    void configurationStop(Object global, Object local, ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Terminate configurable components.
     * 
     * @param global
     * @param local
     * @param monitor
     * @throws SiteWhereException
     */
    void configurationTerminate(Object global, Object local, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Get global application context.
     * 
     * @return
     * @throws SiteWhereException
     */
    Object getGlobalApplicationContext() throws SiteWhereException;

    /**
     * Set the global application context.
     * 
     * @param context
     * @throws SiteWhereException
     */
    void setGlobalApplicationContext(Object context) throws SiteWhereException;

    /**
     * Get local microservice application context.
     * 
     * @return
     * @throws SiteWhereException
     */
    Object getLocalApplicationContext() throws SiteWhereException;

    /**
     * Set the local application context.
     * 
     * @param context
     * @throws SiteWhereException
     */
    void setLocalApplicationContext(Object context) throws SiteWhereException;

    /**
     * Perform microservice initialization.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Called to start microservice after initialization.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Called to stop microservice before termination.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Initialize components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    ILifecycleStep initializeDiscoverableBeans(Object context) throws SiteWhereException;

    /**
     * Start components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    ILifecycleStep startDiscoverableBeans(Object context) throws SiteWhereException;

    /**
     * Stop components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    ILifecycleStep stopDiscoverableBeans(Object context) throws SiteWhereException;

    /**
     * Terminate components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    ILifecycleStep terminateDiscoverableBeans(Object context) throws SiteWhereException;

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