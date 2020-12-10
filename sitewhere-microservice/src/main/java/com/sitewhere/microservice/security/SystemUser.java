/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Provides a system "superuser" that allows microservices to authenticate with
 * other microservices via JWT.
 */
@ApplicationScoped
public class SystemUser implements ISystemUser {

    /** Number of seconds between renewing JWT */
    private static final int RENEW_INTERVAL_SEC = 60 * 3;

    /** Instance settings */
    @Inject
    IInstanceSettings instanceSettings;

    /** JWT token management */
    @Inject
    ITokenManagement tokenManagement;

    /** Last authentication result */
    private SiteWhereAuthentication last = null;

    /** Last time JWT was generated */
    private long lastGenerated = 0;

    /*
     * @see com.sitewhere.spi.microservice.security.ISystemUser#getAuthentication()
     */
    @Override
    public SiteWhereAuthentication getAuthentication() throws SiteWhereException {
	if ((System.currentTimeMillis() - lastGenerated) > (RENEW_INTERVAL_SEC * 1000)) {
	    this.last = getTokenManagement().getAuthenticationForUser(getInstanceSettings().getKeycloakSystemUsername(),
		    getInstanceSettings().getKeycloakSystemPassword());
	    this.lastGenerated = System.currentTimeMillis();
	}
	return this.last;
    }

    /*
     * @see com.sitewhere.spi.microservice.security.ISystemUser#
     * getAuthenticationForTenant(io.sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public SiteWhereAuthentication getAuthenticationForTenant(SiteWhereTenant tenant) throws SiteWhereException {
	SiteWhereAuthentication existing = getAuthentication();
	SiteWhereAuthentication clone = new SiteWhereAuthentication(existing.getUsername(),
		existing.getGrantedAuthorities(), existing.getJwt());
	clone.setTenantToken(tenant.getMetadata().getName());
	return clone;
    }

    protected IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    protected ITokenManagement getTokenManagement() {
	return tokenManagement;
    }
}