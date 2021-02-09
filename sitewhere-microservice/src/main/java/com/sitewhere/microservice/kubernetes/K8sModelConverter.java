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
