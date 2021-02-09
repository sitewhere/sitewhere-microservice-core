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
package com.sitewhere.spi.microservice.tenant;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.tenant.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Interface for tenant management operations.
 */
public interface ITenantManagement extends ILifecycleComponent {

    /**
     * Create a new tenant.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing tenant.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    ITenant updateTenant(String token, ITenantCreateRequest request) throws SiteWhereException;

    /**
     * Get a tenant by tenant id.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    ITenant getTenant(String token) throws SiteWhereException;

    /**
     * Find all tenants that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an existing tenant.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    ITenant deleteTenant(String token) throws SiteWhereException;
}