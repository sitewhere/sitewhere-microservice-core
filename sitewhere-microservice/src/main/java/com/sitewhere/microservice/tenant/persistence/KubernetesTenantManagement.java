/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.tenant.persistence;

import java.util.Arrays;
import java.util.UUID;

import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.tenant.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.sitewhere.k8s.crd.ApiConstants;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenantList;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenantSpec;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenantStatus;
import io.sitewhere.k8s.crd.tenant.TenantBrandingSpecification;

/**
 * Tenant management implementation which stores tenant metadata in Kubernetes
 * custom resources.
 */
public class KubernetesTenantManagement extends LifecycleComponent implements ITenantManagement {

    public KubernetesTenantManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#createTenant(com.
     * sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	SiteWhereTenant tenant = fromApiRequest(request, getMicroservice().getInstanceSettings());
	tenant = getMicroservice().getSiteWhereKubernetesClient().getTenants().create(tenant);
	return asApiTenant(tenant);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#updateTenant(java.
     * util.UUID, com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(UUID id, ITenantCreateRequest request) throws SiteWhereException {
	SiteWhereTenant tenant = fromApiRequest(request, getMicroservice().getInstanceSettings());
	tenant = getMicroservice().getSiteWhereKubernetesClient().getTenants().createOrReplace(tenant);
	return asApiTenant(tenant);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#getTenant(java.util.
     * UUID)
     */
    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	throw new RuntimeException("Not implemented yet.");
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#getTenantByToken(java
     * .lang.String)
     */
    @Override
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	SiteWhereTenantList list = getMicroservice().getSiteWhereKubernetesClient().getTenants().list();
	for (SiteWhereTenant tenant : list.getItems()) {
	    if (tenant.getMetadata().getName().equals(token)) {
		return asApiTenant(tenant);
	    }
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.tenant.ITenantManagement#listTenants(com.
     * sitewhere.spi.search.tenant.ITenantSearchCriteria)
     */
    @Override
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
	SiteWhereTenantList list = getMicroservice().getSiteWhereKubernetesClient().getTenants().list();
	Pager<ITenant> pager = new Pager<ITenant>(criteria);
	for (SiteWhereTenant tenant : list.getItems()) {
	    pager.process(asApiTenant(tenant));
	}
	return new SearchResults<>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#deleteTenant(java.
     * util.UUID)
     */
    @Override
    public ITenant deleteTenant(UUID tenantId) throws SiteWhereException {
	throw new RuntimeException("Not implemented yet.");
    }

    /**
     * Convert k8s tenant resource to SiteWhere API.
     * 
     * @param tenant
     * @return
     */
    protected static ITenant asApiTenant(SiteWhereTenant tenant) {
	Tenant api = new Tenant();
	api.setToken(tenant.getMetadata().getName());
	api.setName(tenant.getSpec().getName());
	api.setAuthenticationToken(tenant.getSpec().getAuthenticationToken());
	api.setAuthorizedUserIds(Arrays.asList(tenant.getSpec().getAuthorizedUserIds()));
	api.setBackgroundColor(tenant.getSpec().getBranding().getBackgroundColor());
	api.setForegroundColor(tenant.getSpec().getBranding().getForegroundColor());
	api.setBorderColor(tenant.getSpec().getBranding().getBorderColor());
	api.setIcon(tenant.getSpec().getBranding().getIcon());
	api.setImageUrl(tenant.getSpec().getBranding().getImageUrl());
	api.setLogoUrl(tenant.getSpec().getBranding().getImageUrl());
	api.setConfigurationTemplateId(tenant.getSpec().getConfigurationTemplate());
	api.setDatasetTemplateId(tenant.getSpec().getDatasetTemplate());
	api.setMetadata(tenant.getSpec().getMetadata());
	return api;
    }

    /**
     * Create k8s tenant resource from API request.
     * 
     * @param request
     * @param settings
     * @return
     * @throws SiteWhereException
     */
    protected static SiteWhereTenant fromApiRequest(ITenantCreateRequest request, IInstanceSettings settings)
	    throws SiteWhereException {
	SiteWhereTenant tenant = new SiteWhereTenant();
	String fullVersion = String.format("%s/%s", ApiConstants.SITEWHERE_API_GROUP,
		ApiConstants.SITEWHERE_API_VERSION);
	tenant.setApiVersion(fullVersion);
	tenant.setKind(SiteWhereTenant.class.getSimpleName());

	String namespace = settings.getKubernetesNamespace();

	// Configure initial metadata.
	tenant.setMetadata(new ObjectMeta());
	tenant.getMetadata().setName(request.getToken());
	tenant.getMetadata().setNamespace(namespace);

	// Configure specification.
	tenant.setSpec(new SiteWhereTenantSpec());
	tenant.getSpec().setName(request.getName());
	tenant.getSpec().setAuthenticationToken(request.getAuthenticationToken());
	tenant.getSpec().setAuthorizedUserIds(request.getAuthorizedUserIds().toArray(new String[0]));
	tenant.getSpec().setConfigurationTemplate(request.getConfigurationTemplateId());
	tenant.getSpec().setDatasetTemplate(request.getDatasetTemplateId());
	tenant.getSpec().setBranding(new TenantBrandingSpecification());
	tenant.getSpec().getBranding().setBackgroundColor(request.getBackgroundColor());
	tenant.getSpec().getBranding().setForegroundColor(request.getForegroundColor());
	tenant.getSpec().getBranding().setBorderColor(request.getBorderColor());
	tenant.getSpec().getBranding().setIcon(request.getIcon());
	tenant.getSpec().getBranding().setImageUrl(request.getImageUrl());

	// Configure status.
	tenant.setStatus(new SiteWhereTenantStatus());

	return tenant;
    }
}
