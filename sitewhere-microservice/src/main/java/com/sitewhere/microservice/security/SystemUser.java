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

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Provides a system "superuser" that allows microservices to authenticate with
 * other microservices via JWT.
 */
@Component
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