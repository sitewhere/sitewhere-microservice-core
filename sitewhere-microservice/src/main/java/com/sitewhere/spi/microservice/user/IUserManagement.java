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
package com.sitewhere.spi.microservice.user;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IRole;
import com.sitewhere.spi.user.IRoleSearchCriteria;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IRoleCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Interface for user management operations.
 */
public interface IUserManagement extends ILifecycleComponent {

    /**
     * Create a new user based on the given input.
     *
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IUser createUser(IUserCreateRequest request) throws SiteWhereException;

    /**
     * Authenticate the given user and return an access token.
     * 
     * @param username
     * @param password
     * @return
     * @throws SiteWhereException
     */
    String getAccessToken(String username, String password) throws SiteWhereException;

    /**
     * Get public key used to validate access token.
     * 
     * @return
     * @throws SiteWhereException
     */
    String getPublicKey() throws SiteWhereException;

    /**
     * Update details for a user.
     *
     * @param username
     * @param request
     * @param encodePassword
     * @return
     * @throws SiteWhereException
     */
    IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword) throws SiteWhereException;

    /**
     * Get a user given unique username.
     *
     * @param username
     * @return
     * @throws SiteWhereException
     */
    IUser getUserByUsername(String username) throws SiteWhereException;

    /**
     * Find users that match the given search criteria.
     *
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete the user with the given username.
     *
     * @param username
     * @return
     * @throws SiteWhereException
     */
    IUser deleteUser(String username) throws SiteWhereException;

    /**
     * Create a new granted authority.
     *
     * @param request
     * @throws SiteWhereException
     */
    IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException;

    /**
     * Get a granted authority by name.
     *
     * @param name
     * @return
     * @throws SiteWhereException
     */
    IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException;

    /**
     * Update a granted authority.
     *
     * @param name
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException;

    /**
     * List granted authorities that match the given criteria.
     *
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Delete a granted authority.
     *
     * @param authority
     * @throws SiteWhereException
     */
    void deleteGrantedAuthority(String authority) throws SiteWhereException;

    /**
     * Get the Roles for a specific user.
     *
     * @param username
     * @return
     * @throws SiteWhereException
     */
    List<IRole> getRoles(String username) throws SiteWhereException;

    /**
     * Add user Roles. Duplicates are ignored.
     *
     * @param username
     * @param roles
     * @return
     * @throws SiteWhereException
     */
    List<IRole> addRoles(String username, List<String> roles) throws SiteWhereException;

    /**
     * Remove user roles.
     *
     * @param username
     * @param roles
     * @return
     * @throws SiteWhereException
     */
    List<IRole> removeRoles(String username, List<String> roles) throws SiteWhereException;

    /**
     * Create a new granted authority.
     *
     * @param request
     * @throws SiteWhereException
     */
    IRole createRole(IRoleCreateRequest request) throws SiteWhereException;

    /**
     * Get a granted authority by name.
     *
     * @param name
     * @return
     * @throws SiteWhereException
     */
    IRole getRoleByName(String name) throws SiteWhereException;

    /**
     * Update a role.
     *
     * @param name
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IRole updateRole(String name, IRoleCreateRequest request) throws SiteWhereException;

    /**
     * List roles that match the given criteria.
     *
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IRole> listRoles(IRoleSearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete a role.
     *
     * @param role
     * @throws SiteWhereException
     */
    void deleteRole(String role) throws SiteWhereException;
}