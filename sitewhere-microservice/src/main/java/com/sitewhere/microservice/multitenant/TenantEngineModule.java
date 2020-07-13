/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import com.google.inject.AbstractModule;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

/**
 * Guice module which uses the tenant configuration object to configure
 * components required to run the tenant engine.
 * 
 * @param <T>
 */
public class TenantEngineModule<T extends ITenantEngineConfiguration> extends AbstractModule
	implements ITenantEngineModule<T> {

    /** Tenant engine */
    private IMicroserviceTenantEngine<T> tenantEngine;

    /** Tenant engine configuration */
    private T configuration;

    public TenantEngineModule(IMicroserviceTenantEngine<T> tenantEngine, T configuration) {
	this.tenantEngine = tenantEngine;
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineModule#
     * getTenantEngine()
     */
    @Override
    public IMicroserviceTenantEngine<T> getTenantEngine() {
	return tenantEngine;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineModule#
     * getConfiguration()
     */
    @Override
    public T getConfiguration() {
	return configuration;
    }
}
