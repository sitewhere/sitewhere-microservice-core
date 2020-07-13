/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

import com.google.inject.Module;

/**
 * Guice module which uses the tenant configuration object to configure
 * components required to run the tenant engine.
 * 
 * @param <T>
 */
public interface ITenantEngineModule<T extends ITenantEngineConfiguration> extends Module {

    /**
     * Get tenant engine handle.
     * 
     * @return
     */
    IMicroserviceTenantEngine<T> getTenantEngine();

    /**
     * Get configuration used for module.
     * 
     * @return
     */
    T getConfiguration();
}