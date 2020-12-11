/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kubernetes;

import com.sitewhere.microservice.tenant.TenantWrapper;
import com.sitewhere.rest.model.microservice.MicroserviceSummary;
import com.sitewhere.spi.tenant.ITenant;

import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Conversions between k8s resources and SiteWhere API objects.
 */
public class K8sModelConverter {

    /**
     * Convert k8s microservice resource to SiteWhere API.
     * 
     * @param microservice
     * @return
     */
    public static MicroserviceSummary convert(SiteWhereMicroservice microservice) {
	MicroserviceSummary summary = new MicroserviceSummary();
	summary.setId(microservice.getMetadata().getName());
	summary.setName(microservice.getSpec().getName());
	summary.setDescription(microservice.getSpec().getDescription());
	summary.setFunctionalArea(microservice.getSpec().getFunctionalArea());
	summary.setIcon(microservice.getSpec().getIcon());
	summary.setMultitenant(microservice.getSpec().isMultitenant());
	if (microservice.getSpec().getPodSpec() != null) {
	    summary.setDockerImageTag(microservice.getSpec().getPodSpec().getImageTag());
	}
	if (microservice.getSpec().getDebug() != null) {
	    summary.setDebugEnabled(microservice.getSpec().getDebug().isEnabled());
	    summary.setDebugJdwpPort(microservice.getSpec().getDebug().getJdwpPort());
	    summary.setDebugJmxPort(microservice.getSpec().getDebug().getJmxPort());
	}
	return summary;
    }

    /**
     * Convert k8s tenant resource to SiteWhere API.
     * 
     * @param tenant
     * @return
     */
    public static ITenant convert(SiteWhereTenant tenant) {
	return new TenantWrapper(tenant);
    }
}
