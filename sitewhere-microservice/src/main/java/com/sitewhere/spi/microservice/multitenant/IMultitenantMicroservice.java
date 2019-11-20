/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that contains engines for multiple tenants.
 */
public interface IMultitenantMicroservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration, T extends IMicroserviceTenantEngine<?>>
	extends IConfigurableMicroservice<F, C> {

    /**
     * Get tenant engine manager.
     * 
     * @return
     */
    ITenantEngineManager<T> getTenantEngineManager();

    /**
     * Create tenant engine specific to microservice function.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    T createTenantEngine(ITenant tenant) throws SiteWhereException;

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
}