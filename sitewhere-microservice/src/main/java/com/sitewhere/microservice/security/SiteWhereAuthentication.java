/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import java.util.List;

import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Context information for an authenticated user.
 */
public class SiteWhereAuthentication {

    /** User details */
    private IUser user;

    /** List of granted authorities */
    private List<IGrantedAuthority> grantedAuthorities;

    /** JWT */
    private String jwt;

    /** Tenant */
    private SiteWhereTenant tenant;

    public SiteWhereAuthentication(IUser user, List<IGrantedAuthority> grantedAuthorities, String jwt) {
	this.user = user;
	this.grantedAuthorities = grantedAuthorities;
	this.jwt = jwt;
    }

    public void setTenant(SiteWhereTenant tenant) {
	this.tenant = tenant;
    }

    public IUser getUser() {
	return user;
    }

    public List<IGrantedAuthority> getGrantedAuthorities() {
	return grantedAuthorities;
    }

    public String getJwt() {
	return jwt;
    }

    public SiteWhereTenant getTenant() {
	return tenant;
    }
}
