/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.grpc;

import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Callback which executes code in the context of a tenant engine.
 * 
 * @param <T>
 */
public interface ITenantEngineCallback<T extends IMicroserviceTenantEngine<?>> {

    /**
     * Execute an operation in the context of a given tenant engine.
     * 
     * @param tenantEngine
     */
    void executeInTenantEngine(T tenantEngine);
}
