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
package com.sitewhere.microservice.tenant;

import com.sitewhere.microservice.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.tenant.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Uses decorator pattern to allow behaviors to be injected around tenant
 * management API calls.
 */
public class TenantManagementDecorator extends LifecycleComponentDecorator<ITenantManagement>
	implements ITenantManagement {

    public TenantManagementDecorator(ITenantManagement delegate) {
	super(delegate);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#createTenant(com.
     * sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	return getDelegate().createTenant(request);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#updateTenant(java.
     * lang.String, com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(String token, ITenantCreateRequest request) throws SiteWhereException {
	return getDelegate().updateTenant(token, request);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#getTenant(java.lang.
     * String)
     */
    @Override
    public ITenant getTenant(String token) throws SiteWhereException {
	return getDelegate().getTenant(token);
    }

    /*
     * @see com.sitewhere.spi.microservice.tenant.ITenantManagement#listTenants(com.
     * sitewhere.spi.search.tenant.ITenantSearchCriteria)
     */
    @Override
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listTenants(criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#deleteTenant(java.
     * lang.String)
     */
    @Override
    public ITenant deleteTenant(String token) throws SiteWhereException {
	return getDelegate().deleteTenant(token);
    }
}