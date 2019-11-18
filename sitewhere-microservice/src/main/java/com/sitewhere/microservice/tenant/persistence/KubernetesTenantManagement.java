/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.tenant.persistence;

import java.util.UUID;

import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.tenant.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Tenant management implementation which stores tenant metadata in Kubernetes
 * custom resources.
 */
public class KubernetesTenantManagement extends LifecycleComponent implements ITenantManagement {

    public KubernetesTenantManagement() {
	super(LifecycleComponentType.DataStore);
    }

    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public ITenant updateTenant(UUID id, ITenantCreateRequest request) throws SiteWhereException {
	throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
	throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public ITenant deleteTenant(UUID tenantId) throws SiteWhereException {
	throw new RuntimeException("Not implemented yet.");
    }

    protected IConfigurableMicroservice<?> getConfigurableMicroservice() {
	return (IConfigurableMicroservice<?>) getMicroservice();
    }
}
