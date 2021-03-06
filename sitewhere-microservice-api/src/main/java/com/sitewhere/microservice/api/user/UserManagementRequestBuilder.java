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
package com.sitewhere.microservice.api.user;

import com.sitewhere.rest.model.search.user.UserSearchCriteria;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.user.IUserManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IRole;
import com.sitewhere.spi.user.IUser;

/**
 * Builder that supports creating user management entities.
 */
public class UserManagementRequestBuilder {

    /** Device management implementation */
    private IUserManagement userManagement;

    public UserManagementRequestBuilder(IUserManagement userManagement) {
	this.userManagement = userManagement;
    }

    /**
     * Create builder for new user request.
     *
     * @param username
     * @param password
     * @param firstName
     * @param lastName
     * @return
     */
    public UserCreateRequest.Builder newUser(String username, String password, String firstName, String lastName) {
	return new UserCreateRequest.Builder(username, password, firstName, lastName);
    }

    /**
     * Persist user contructed via builder.
     *
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IUser persist(UserCreateRequest.Builder builder) throws SiteWhereException {
	return getUserManagement().createUser(builder.build());
    }

    /**
     * Create builder for new granted authority request.
     *
     * @param authority
     * @return
     */
    public GrantedAuthorityCreateRequest.Builder newGrantedAuthority(String authority) {
	return new GrantedAuthorityCreateRequest.Builder(authority);
    }

    /**
     * Persist granted authority constructed via builder.
     *
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IGrantedAuthority persist(GrantedAuthorityCreateRequest.Builder builder) throws SiteWhereException {
	return getUserManagement().createGrantedAuthority(builder.build());
    }

    /**
     * Get an existing authority by name.
     *
     * @param authority
     * @return
     * @throws SiteWhereException
     */
    public IGrantedAuthority getAuthority(String authority) throws SiteWhereException {
	return getUserManagement().getGrantedAuthorityByName(authority);
    }

    /**
     * Indicates if the system already contains the given authority.
     *
     * @param authority
     * @return
     * @throws SiteWhereException
     */
    public boolean hasAuthority(String authority) throws SiteWhereException {
	return getAuthority(authority) != null;
    }

    /**
     * List all users.
     *
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IUser> listUsers() throws SiteWhereException {
	return getUserManagement().listUsers(new UserSearchCriteria());
    }

    /**
     * Indicates if the system has users defined.
     *
     * @return
     * @throws SiteWhereException
     */
    public boolean hasUsers() throws SiteWhereException {
	return listUsers().getNumResults() > 0;
    }

    public IUserManagement getUserManagement() {
	return userManagement;
    }

    public void setUserManagement(IUserManagement userManagement) {
	this.userManagement = userManagement;
    }

    /**
     * Create builder for new role request.
     *
     * @param role
     * @return
     */
    public RoleCreateRequest.Builder newRole(String role) {
	return new RoleCreateRequest.Builder(role);
    }

    /**
     * Indicates if the system already contains the given role.
     *
     * @param role
     * @return
     * @throws SiteWhereException
     */
    public boolean hasRole(String role) throws SiteWhereException {
	return getRole(role) != null;
    }

    /**
     * Get an existing role by name.
     *
     * @param role
     * @return
     * @throws SiteWhereException
     */
    public IRole getRole(String role) throws SiteWhereException {
	return getUserManagement().getRoleByName(role);
    }

    /**
     * Persist role constructed via builder.
     *
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IRole persist(RoleCreateRequest.Builder builder) throws SiteWhereException {
	return getUserManagement().createRole(builder.build());
    }
}