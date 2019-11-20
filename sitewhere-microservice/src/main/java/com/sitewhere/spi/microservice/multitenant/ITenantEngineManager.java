/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;

/**
 * Manages tenant engines for a multitenant microservice.
 */
public interface ITenantEngineManager<T extends IMicroserviceTenantEngine<?>> extends ILifecycleComponent {

    /**
     * Get tenant engine corresponding to the given id.
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
     * Get configuration for the given tenant.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    byte[] getTenantConfiguration(String token) throws SiteWhereException;

    /**
     * Update configuration for the given tenant.
     * 
     * @param token
     * @param content
     * @throws SiteWhereException
     */
    void updateTenantConfiguration(String token, byte[] content) throws SiteWhereException;

    /**
     * Shuts down and restarts the given tenant engine.
     * 
     * @param token
     * @throws SiteWhereException
     */
    void restartTenantEngine(String token) throws SiteWhereException;

    /**
     * Restart all tenant engines.
     * 
     * @throws SiteWhereException
     */
    void restartAllTenantEngines() throws SiteWhereException;

    /**
     * Shuts down and removes a tenant engine.
     * 
     * @param token
     * @throws SiteWhereException
     */
    void removeTenantEngine(String token) throws SiteWhereException;

    /**
     * Remove all tenant engines for the microservice.
     * 
     * @throws SiteWhereException
     */
    void removeAllTenantEngines() throws SiteWhereException;
}
