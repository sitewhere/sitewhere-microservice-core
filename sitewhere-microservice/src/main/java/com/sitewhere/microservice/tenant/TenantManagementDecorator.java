/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.tenant;

import java.util.UUID;

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

    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	return getDelegate().createTenant(request);
    }

    @Override
    public ITenant updateTenant(UUID id, ITenantCreateRequest request) throws SiteWhereException {
	return getDelegate().updateTenant(id, request);
    }

    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	return getDelegate().getTenant(id);
    }

    @Override
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	return getDelegate().getTenantByToken(token);
    }

    @Override
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listTenants(criteria);
    }

    @Override
    public ITenant deleteTenant(UUID tenantId) throws SiteWhereException {
	return getDelegate().deleteTenant(tenantId);
    }
}