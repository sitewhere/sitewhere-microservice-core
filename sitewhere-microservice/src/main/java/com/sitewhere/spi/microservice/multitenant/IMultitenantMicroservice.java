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