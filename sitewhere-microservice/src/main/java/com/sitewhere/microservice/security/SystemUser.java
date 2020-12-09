/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Bean that provides a system "superuser" that allows microservices to
 * authenticate with other microservices.
 */
@ApplicationScoped
public class SystemUser implements ISystemUser {

    /** Number of seconds between renewing JWT */
    private static final int RENEW_INTERVAL_SEC = 60 * 60;

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
	    this.last = new SiteWhereAuthentication("system", Collections.emptyList(), null);
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
	SiteWhereAuthentication auth = getAuthentication();
	auth.setTenantToken(tenant.getMetadata().getName());
	return auth;
    }

    protected ITokenManagement getTokenManagement() {
	return tokenManagement;
    }
}