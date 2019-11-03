/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Listener for changes to tenant engine model.
 */
public interface ITenantEngineConfigurationListener {

    /**
     * Called when tenant engine configuration is added.
     * 
     * @param engine
     */
    public void onConfigurationAdded(SiteWhereTenantEngine engine);

    /**
     * Called when tenant engine configuration is updated.
     * 
     * @param engine
     */
    public void onConfigurationUpdated(SiteWhereTenantEngine engine);

    /**
     * Called when tenant engine configuration is deleted.
     * 
     * @param engine
     */
    public void onConfigurationDeleted(SiteWhereTenantEngine engine);
}
