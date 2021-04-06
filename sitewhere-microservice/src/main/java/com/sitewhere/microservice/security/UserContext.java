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
package com.sitewhere.microservice.security;

import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.spi.microservice.IMicroservice;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

public class UserContext {

    public static void clearContext() {
	SecurityContextHolder.clearContext();
    }

    public static SiteWhereAuthentication getCurrentUser() {
	if (SecurityContextHolder.getContext().getAuthentication() == null) {
	    return null;
	}
	if (SecurityContextHolder.getContext().getAuthentication() instanceof SiteWhereAuthentication) {
	    return (SiteWhereAuthentication) SecurityContextHolder.getContext().getAuthentication();
	}
	throw new RuntimeException(String.format("Authentication not of expected type: %s",
		SecurityContextHolder.getContext().getAuthentication().getClass().getName()));
    }

    public static SiteWhereTenant getCurrentTenant(IMicroservice<?, ?> microservice) {
	SiteWhereAuthentication auth = getCurrentUser();
	if (auth == null) {
	    return null;
	}
	return microservice.getSiteWhereKubernetesClient()
		.getTenantForToken(microservice.getInstanceSettings().getK8s().getNamespace(), auth.getTenantToken());
    }

    public static String getCurrentTenantId() {
	SiteWhereAuthentication auth = getCurrentUser();
	return auth != null ? auth.getTenantToken() : null;
    }

    public static void setContext(SiteWhereAuthentication context) {
	SecurityContextHolder.getContext().setAuthentication(context);
    }
}
