/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.api.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IRole;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IRoleCreateRequest;

import java.util.List;

/**
 * Holds fields needed to create a new granted authority.
 */
@JsonInclude(Include.NON_NULL)
public class RoleCreateRequest implements IRoleCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 2752477482696017875L;

    /** Rol name */
    private String role;

    /** Role description */
    private String description;

    /** Role authorities */
    private List authorities;

    @Override public String getRole() {
	return null;
    }

    @Override public String getDescription() {
	return null;
    }

    @Override public List<String> getAuthorities() {
	return null;
    }

    public void setRole(String role) {
	this.role = role;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public void setAuthorities(List authorities) {
	this.authorities = authorities;
    }

    public static class Builder {
	/** Request being built */
	private RoleCreateRequest request = new RoleCreateRequest();

	public Builder(String role) {
	    request.setRole(role);
	}

	public Builder(IRole existing) {
	    request.setRole(existing.getRole());
	    request.setDescription(existing.getDescription());
	    request.setAuthorities(existing.getAuthorities());
	}

	public Builder withDescription(String description) {
	    request.setDescription(description);
	    return this;
	}

	public Builder withAuthorities(String description) {
	    request.setDescription(description);
	    return this;
	}

	public RoleCreateRequest build() {
	    return request;
	}
    }
}