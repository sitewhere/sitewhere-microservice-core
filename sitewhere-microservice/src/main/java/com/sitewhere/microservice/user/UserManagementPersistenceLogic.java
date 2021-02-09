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
package com.sitewhere.microservice.user;

import java.util.List;

import com.sitewhere.microservice.persistence.Persistence;
import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Persistence logic for user management components.
 */
public class UserManagementPersistenceLogic extends Persistence {

    /**
     * Common logic for creating a user based on an incoming request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static User userCreateLogic(IUserCreateRequest request) throws SiteWhereException {
	User user = new User();
	Persistence.entityCreateLogic(request, user);

	require("Username", request.getUsername());
	user.setUsername(request.getUsername());
	require("Password", request.getPassword());
	require("Last name", request.getFirstName());
	user.setFirstName(request.getFirstName());
	require("First name", request.getLastName());
	user.setLastName(request.getLastName());
	user.setEmail(request.getEmail());
	user.setEnabled(request.isEnabled());
	return user;
    }

    /**
     * Common code for copying information from an update request to an existing
     * user.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void userUpdateLogic(IUserCreateRequest request, User target) throws SiteWhereException {
	Persistence.entityUpdateLogic(request, target);

	if (request.getUsername() != null) {
	    target.setUsername(request.getUsername());
	}
	if (request.getFirstName() != null) {
	    target.setFirstName(request.getFirstName());
	}
	if (request.getLastName() != null) {
	    target.setLastName(request.getLastName());
	}
	if (request.getEmail() != null) {
	    target.setEmail(request.getEmail());
	}
	target.setEnabled(request.isEnabled());
    }

    /**
     * Common logic for deleting a user. Takes care of related tasks such as
     * deleting user id from tenant authorized users.
     *
     * @param username
     * @param tenantManagement
     * @throws SiteWhereException
     */
    public static void userDeleteLogic(String username, ITenantManagement tenantManagement) throws SiteWhereException {
	ISearchResults<ITenant> tenants = tenantManagement.listTenants(new TenantSearchCriteria(1, 0));
	for (ITenant tenant : tenants.getResults()) {
	    if (tenant.getAuthorizedUserIds().contains(username)) {
		TenantCreateRequest request = new TenantCreateRequest();
		List<String> ids = tenant.getAuthorizedUserIds();
		ids.remove(username);
		request.setAuthorizedUserIds(ids);
		tenantManagement.updateTenant(null, request);
	    }
	}
    }

    /**
     * Common logic for creating a granted authority based on an incoming request.
     *
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static GrantedAuthority grantedAuthorityCreateLogic(IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	GrantedAuthority auth = new GrantedAuthority();

	require("Authority", request.getAuthority());
	auth.setAuthority(request.getAuthority());

	auth.setDescription(request.getDescription());
	auth.setParent(request.getParent());
	auth.setGroup(request.isGroup());
	return auth;
    }
}