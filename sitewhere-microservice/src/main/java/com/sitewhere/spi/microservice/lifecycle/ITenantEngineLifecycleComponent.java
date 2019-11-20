/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.lifecycle;

import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link ILifecycleComponent} with ability to access tenant engine.
 */
public interface ITenantEngineLifecycleComponent extends ILifecycleComponent {

    /**
     * Build microservice/tenant-specific labels.
     * 
     * @param labels
     * @return
     */
    String[] buildLabels(String... labels);

    /**
     * Set tenant engine for component.
     * 
     * @param tenantEngine
     */
    void setTenantEngine(IMicroserviceTenantEngine<?> tenantEngine);

    /**
     * Get tenant engine for component.
     * 
     * @return
     */
    IMicroserviceTenantEngine<?> getTenantEngine();
}