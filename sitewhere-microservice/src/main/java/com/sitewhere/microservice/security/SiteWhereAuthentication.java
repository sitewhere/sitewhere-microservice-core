/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import java.util.List;

/**
 * Context information for an authenticated user.
 */
public class SiteWhereAuthentication {

    /** Username */
    private String username;

    /** List of granted authorities */
    private List<String> grantedAuthorities;

    /** JWT */
    private String jwt;

    /** Tenant */
    private String tenantToken;

    public SiteWhereAuthentication(String username, List<String> grantedAuthorities, String jwt) {
	this.username = username;
	this.grantedAuthorities = grantedAuthorities;
	this.jwt = jwt;
    }

    public String getUsername() {
	return username;
    }

    public List<String> getGrantedAuthorities() {
	return grantedAuthorities;
    }

    public String getJwt() {
	return jwt;
    }

    public String getTenantToken() {
	return tenantToken;
    }

    public void setTenantToken(String tenantToken) {
	this.tenantToken = tenantToken;
    }
}
