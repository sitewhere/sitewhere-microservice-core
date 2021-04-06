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
package com.sitewhere.microservice.tenant.persistence;

import com.sitewhere.microservice.kubernetes.K8sModelConverter;
import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.microservice.tenant.TenantWrapper;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
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
	tenant = getMicroservice().getSiteWhereKubernetesClient().getTenants()
		.inNamespace(getMicroservice().getInstanceSettings().getK8s().getNamespace()).create(tenant);
	return K8sModelConverter.convert(tenant);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#updateTenant(java.
     * lang.String, com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(String token, ITenantCreateRequest request) throws SiteWhereException {
	SiteWhereTenant tenant = getTenantResource(token);
	if (tenant == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantToken, ErrorLevel.ERROR);
	}
	updateSpecification(tenant, request);
	tenant = getMicroservice().getSiteWhereKubernetesClient().getTenants()
		.inNamespace(getMicroservice().getInstanceSettings().getK8s().getNamespace())
		.withName(tenant.getMetadata().getName()).replace(tenant);
	return K8sModelConverter.convert(tenant);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#getTenant(java.lang.
     * String)
     */
    @Override
    public ITenant getTenant(String token) throws SiteWhereException {
	SiteWhereTenant tenant = getTenantResource(token);
	return tenant == null ? null : K8sModelConverter.convert(tenant);
    }

    /*
     * @see com.sitewhere.spi.microservice.tenant.ITenantManagement#listTenants(com.
     * sitewhere.spi.search.tenant.ITenantSearchCriteria)
     */
    @Override
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
	SiteWhereTenantList list = getMicroservice().getSiteWhereKubernetesClient().getTenants()
		.inNamespace(getMicroservice().getInstanceSettings().getK8s().getNamespace()).list();
	Pager<ITenant> pager = new Pager<ITenant>(criteria);
	for (SiteWhereTenant tenant : list.getItems()) {
	    pager.process(K8sModelConverter.convert(tenant));
	}
	return new SearchResults<>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.tenant.ITenantManagement#deleteTenant(java.
     * util.UUID)
     */
    @Override
    public ITenant deleteTenant(String token) throws SiteWhereException {
	SiteWhereTenant tenant = getTenantResource(token);
	if (tenant != null) {
	    if (!getMicroservice().getSiteWhereKubernetesClient().getTenants()
		    .inNamespace(getMicroservice().getInstanceSettings().getK8s().getNamespace())
		    .withName(tenant.getMetadata().getName()).delete()) {
		throw new SiteWhereException(String.format("Unable to delete tenant '%s'.", token));
	    }
	}
	return new TenantWrapper(tenant);
    }

    /**
     * Get tenant resource based on token.
     * 
     * @param token
     * @return
     */
    protected SiteWhereTenant getTenantResource(String token) {
	return getMicroservice().getSiteWhereKubernetesClient().getTenants()
		.inNamespace(getMicroservice().getInstanceSettings().getK8s().getNamespace()).withName(token).get();
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

	String namespace = settings.getK8s().getNamespace();

	// Configure initial metadata.
	tenant.setMetadata(new ObjectMeta());
	tenant.getMetadata().setName(request.getToken());
	tenant.getMetadata().setNamespace(namespace);

	// Configure specification.
	updateSpecification(tenant, request);

	// Configure status.
	tenant.setStatus(new SiteWhereTenantStatus());

	return tenant;
    }

    /**
     * Update tenant specification based on request.
     * 
     * @param tenant
     * @param request
     */
    protected static void updateSpecification(SiteWhereTenant tenant, ITenantCreateRequest request) {
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
    }
}
