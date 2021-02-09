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

import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Builder that supports creating tenant management entities.
 */
public class TenantManagementRequestBuilder {

    /** Template that uses MongoDB for all persistence */
    private static final String MONGODB_TENANT_TEMPLATE_NAME = "mongodb";

    /** Template that does not load any data */
    private static final String EMPTY_DATASET_TEMPLATE_NAME = "empty";

    /** Device management implementation */
    private ITenantManagement tenantManagement;

    public TenantManagementRequestBuilder(ITenantManagement tenantManagement) {
	this.tenantManagement = tenantManagement;
    }

    /**
     * Create builder for new tenant request. Assumes tenant uses the "empty" tenant
     * template.
     * 
     * @param id
     * @param name
     * @param authenticationToken
     * @param logoUrl
     * @return
     */
    public TenantCreateRequest.Builder newTenant(String id, String name, String authenticationToken, String logoUrl) {
	return newTenant(id, name, authenticationToken, logoUrl, MONGODB_TENANT_TEMPLATE_NAME,
		EMPTY_DATASET_TEMPLATE_NAME);
    }

    /**
     * Create builder for new tenant request. Allows tenant template to be
     * specified.
     * 
     * @param token
     * @param name
     * @param authenticationToken
     * @param logoUrl
     * @param tenantTemplateId
     * @param datasetTemplateId
     * @return
     */
    public TenantCreateRequest.Builder newTenant(String token, String name, String authenticationToken, String logoUrl,
	    String tenantTemplateId, String datasetTemplateId) {
	return new TenantCreateRequest.Builder(token, name, authenticationToken, logoUrl, tenantTemplateId,
		datasetTemplateId);
    }

    /**
     * Persist tenant contructed via builder.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public ITenant persist(TenantCreateRequest.Builder builder) throws SiteWhereException {
	return getTenantManagement().createTenant(builder.build());
    }

    /**
     * Get tenant by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	return getTenantManagement().getTenant(token);
    }

    /**
     * Indicates whether a tenant exists for the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public boolean hasTenant(String token) throws SiteWhereException {
	return getTenantByToken(token) != null;
    }

    public ITenantManagement getTenantManagement() {
	return tenantManagement;
    }

    public void setTenantManagement(ITenantManagement tenantManagement) {
	this.tenantManagement = tenantManagement;
    }
}