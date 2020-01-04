/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

import com.google.inject.Injector;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.scripting.IScriptManager;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.configuration.TenantEngineConfigurationTemplate;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Engine that manages operations for a single tenant within an
 * {@link IMultitenantMicroservice}.
 */
public interface IMicroserviceTenantEngine<T extends ITenantEngineConfiguration>
	extends ITenantEngineLifecycleComponent {

    /**
     * Get name displayed for tenant engine.
     * 
     * @return
     */
    String getName();

    /**
     * Get tenant resource associated with engine.
     * 
     * @return
     */
    SiteWhereTenant getTenantResource();

    /**
     * Get tenant engine resource associated with engine.
     * 
     * @return
     */
    SiteWhereTenantEngine getTenantEngineResource();

    /**
     * Get class used for parsing configuration.
     * 
     * @return
     */
    Class<T> getConfigurationClass();

    /**
     * Loads the latest tenant engine resource from k8s.
     * 
     * @return
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine loadTenantEngineResource() throws SiteWhereException;

    /**
     * Update tenant engine resource.
     * 
     * @param engine
     * @return
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine updateTenantEngineResource(SiteWhereTenantEngine engine) throws SiteWhereException;

    /**
     * Update tenant engine status information.
     * 
     * @param engine
     * @return
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine updateTenantEngineStatus(SiteWhereTenantEngine engine) throws SiteWhereException;

    /**
     * Executes a tenant engine specification update operation in the context of
     * this tenant engine.
     * 
     * @param operation
     * @return
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine executeTenantEngineSpecUpdate(ITenantEngineSpecUpdateOperation operation)
	    throws SiteWhereException;

    /**
     * Executes a tenant engine status update operation in the context of this
     * tenant engine.
     * 
     * @param operation
     * @return
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine executeTenantEngineStatusUpdate(ITenantEngineStatusUpdateOperation operation)
	    throws SiteWhereException;

    /**
     * Get the currently active configuration.
     * 
     * @return
     */
    T getActiveConfiguration();

    /**
     * Creates a Guice module used to build engine components based on the active
     * configuration.
     * 
     * @return
     */
    ITenantEngineModule<T> createConfigurationModule();

    /**
     * Get Guice injector which allows access to tenant engine components which have
     * been configured via the module.
     */
    Injector getInjector();

    /**
     * Get tenant engine configuration template.
     * 
     * @return
     * @throws SiteWhereException
     */
    TenantEngineConfigurationTemplate getConfigurationTemplate() throws SiteWhereException;

    /**
     * Get tenant engine dataset template.
     * 
     * @return
     * @throws SiteWhereException
     */
    TenantEngineDatasetTemplate getDatasetTemplate() throws SiteWhereException;

    /**
     * Get script manager.
     * 
     * @return
     * @throws SiteWhereException
     */
    IScriptManager getScriptManager() throws SiteWhereException;

    /**
     * Get component which bootstraps the tenant engine dataset.
     * 
     * @return
     * @throws SiteWhereException
     */
    ITenantEngineBootstrapper getTenantEngineBootstrapper() throws SiteWhereException;

    /**
     * Set any bindings required by scripts for bootstrapping tenant engine.
     * 
     * @param binding
     * @throws SiteWhereException
     */
    void setDatasetBootstrapBindings(Binding binding) throws SiteWhereException;

    /**
     * Get list of tenant engines in other microservices that must be bootstrapped
     * before bootstrap logic in this engine is executed.
     * 
     * @return
     */
    IFunctionIdentifier[] getTenantBootstrapPrerequisites();

    /**
     * Wait for dataset in another tenant engine to be bootstrapped using a backoff
     * policy.
     * 
     * @param identifier
     * @throws SiteWhereException
     */
    void waitForTenantDatasetBootstrapped(IFunctionIdentifier identifier) throws SiteWhereException;

    /**
     * Load tenant engine components from the Guice injector.
     * 
     * @throws SiteWhereException
     */
    void loadEngineComponents() throws SiteWhereException;

    /**
     * Executes tenant initialization code. Called after Spring context has been
     * loaded.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Executes tenant startup code.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Executes tenant shutdown code.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Called after tenant engine dataset has been bootstrapped.
     */
    void onTenantBootstrapComplete();
}