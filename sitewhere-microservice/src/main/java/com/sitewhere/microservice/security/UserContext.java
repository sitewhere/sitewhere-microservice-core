/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import com.sitewhere.spi.microservice.IMicroservice;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * {@link ThreadLocal} which tracks the credentials for the authenticated user.
 */
public class UserContext {

    /** Thread local variable */
    private static final ThreadLocal<SiteWhereAuthentication> CONTEXT = new ThreadLocal<>();

    public static void clearContext() {
	CONTEXT.remove();
    }

    public static SiteWhereAuthentication getCurrentUser() {
	return CONTEXT.get();
    }

    public static SiteWhereTenant getCurrentTenant(IMicroservice<?, ?> microservice) {
	SiteWhereAuthentication auth = getCurrentUser();
	if (auth == null) {
	    return null;
	}
	return microservice.getSiteWhereKubernetesClient()
		.getTenantForToken(microservice.getInstanceSettings().getKubernetesNamespace(), auth.getTenantToken());
    }

    public static String getCurrentTenantId() {
	SiteWhereAuthentication auth = getCurrentUser();
	return auth != null ? auth.getTenantToken() : null;
    }

    public static void setContext(SiteWhereAuthentication context) {
	CONTEXT.set(context);
    }
}
