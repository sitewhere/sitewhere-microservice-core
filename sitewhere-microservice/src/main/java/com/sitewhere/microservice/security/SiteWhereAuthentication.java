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

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Context information for an authenticated user.
 */
public class SiteWhereAuthentication extends AbstractAuthenticationToken {

    /** Serial version UID */
    private static final long serialVersionUID = -3932806611461422345L;

    /** Username */
    private String username;

    /** List of granted authorities */
    private List<String> grantedAuthorities;

    /** JWT */
    private String jwt;

    /** Tenant */
    private String tenantToken;

    public SiteWhereAuthentication(String username, List<String> grantedAuthorities, String jwt) {
	super(Grant.from(grantedAuthorities));
	this.username = username;
	this.grantedAuthorities = grantedAuthorities;
	this.jwt = jwt;
	setAuthenticated(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.Authentication#getCredentials()
     */
    @Override
    public Object getCredentials() {
	return jwt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.Authentication#getPrincipal()
     */
    @Override
    public Object getPrincipal() {
	return username;
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

    public static class Grant implements GrantedAuthority {

	private static final long serialVersionUID = 2129674652225690114L;

	private String authority;

	public Grant(String authority) {
	    this.authority = authority;
	}

	@Override
	public String getAuthority() {
	    return this.authority;
	}

	public static List<GrantedAuthority> from(List<String> values) {
	    List<GrantedAuthority> gauths = new ArrayList<>();
	    for (String auth : values) {
		gauths.add(new Grant(auth));
	    }
	    return gauths;
	}
    }
}
