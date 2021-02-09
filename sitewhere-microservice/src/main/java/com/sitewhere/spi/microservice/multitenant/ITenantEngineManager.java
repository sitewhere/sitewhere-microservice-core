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

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.ITenantEngineConfigurationListener;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages tenant engines for a multitenant microservice.
 */
public interface ITenantEngineManager<T extends IMicroserviceTenantEngine<?>>
	extends ITenantEngineConfigurationListener, ITenantEngineLifecycleComponent {

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
     * Shuts down and restarts the given tenant engine. Note that restart happens
     * asynchronously based on available tenant operation threads.
     * 
     * @param token
     */
    void restartTenantEngine(String token);

    /**
     * Restart all tenant engines. Note that restarts happen asynchronously based on
     * available tenant operation threads. If all threads are used, this method will
     * block.
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
