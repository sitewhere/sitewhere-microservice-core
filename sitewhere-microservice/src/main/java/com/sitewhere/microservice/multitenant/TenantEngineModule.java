/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
