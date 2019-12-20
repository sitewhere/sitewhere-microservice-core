/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.configuration.ITenantEngineConfigurationMonitor;

import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Microservice that contains engines for multiple tenants.
 */
public interface IMultitenantMicroservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration, T extends IMicroserviceTenantEngine<?>>
	extends IConfigurableMicroservice<F, C> {

    /**
     * Get monitor which notifies when tenant engines change.
     * 
     * @return
     */
    ITenantEngineConfigurationMonitor getTenantEngineConfigurationMonitor();

    /**
     * Get tenant engine manager.
     * 
     * @return
     */
    ITenantEngineManager<T> getTenantEngineManager();

    /**
     * Create tenant engine based on k8s resource.
     * 
     * @param engine
     * @return
     * @throws SiteWhereException
     */
    T createTenantEngine(SiteWhereTenantEngine engine) throws SiteWhereException;

    /**
     * Get tenant engine corresponding to the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    T getTenantEngineByToken(String token) throws SiteWhereException;

    /**
     * Make sure the given tenant engine exists and is started.
     * 
     * @param token
     * @return
     * @throws TenantEngineNotAvailableException
     */
    T assureTenantEngineAvailable(String token) throws TenantEngineNotAvailableException;

    /**
     * Get tenant engine configuration.
     * 
     * @param tenant
     * @param microservice
     * @return
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine getTenantEngineConfiguration(SiteWhereTenant tenant, SiteWhereMicroservice microservice)
	    throws SiteWhereException;

    /**
     * Set configuration for a tenant engine.
     * 
     * @param tenant
     * @param microservice
     * @param configuration
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine setTenantEngineConfiguration(SiteWhereTenant tenant, SiteWhereMicroservice microservice,
	    JsonNode configuration) throws SiteWhereException;
}