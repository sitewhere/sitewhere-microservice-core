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

import java.util.List;

import com.sitewhere.microservice.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.user.IUserManagement;
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
 * Uses decorator pattern to allow behaviors to be injected around user
 * management API calls.
 */
public class UserManagementDecorator extends LifecycleComponentDecorator<IUserManagement> implements IUserManagement {

    public UserManagementDecorator(IUserManagement delegate) {
	super(delegate);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#createUser(com.sitewhere.
     * spi.user.request.IUserCreateRequest)
     */
    @Override
    public IUser createUser(IUserCreateRequest request) throws SiteWhereException {
	return getDelegate().createUser(request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#getAccessToken(java.lang.
     * String, java.lang.String)
     */
    @Override
    public String getAccessToken(String username, String password) throws SiteWhereException {
	return getDelegate().getAccessToken(username, password);
    }

    /*
     * @see com.sitewhere.microservice.api.user.IUserManagement#getPublicKey()
     */
    @Override
    public String getPublicKey() throws SiteWhereException {
	return getDelegate().getPublicKey();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.spi.user.IUserManagement#updateUser(java.lang.String,
     * com.sitewhere.spi.user.request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword)
	    throws SiteWhereException {
	return getDelegate().updateUser(username, request, encodePassword);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.spi.user.IUserManagement#getUserByUsername(java.lang.
     * String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	return getDelegate().getUserByUsername(username);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.
     * IUserSearchCriteria)
     */
    @Override
    public ISearchResults<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listUsers(criteria);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String)
     */
    @Override
    public IUser deleteUser(String username) throws SiteWhereException {
	return getDelegate().deleteUser(username);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.spi.user.IUserManagement#createGrantedAuthority(com.
     * sitewhere.spi. user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException {
	return getDelegate().createGrantedAuthority(request);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.
     * lang.String)
     */
    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	return getDelegate().getGrantedAuthorityByName(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.spi.user.IUserManagement#updateGrantedAuthority(java.lang.
     * String, com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().updateGrantedAuthority(name, request);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#listGrantedAuthorities(com.sitewhere.
     * spi.user.IGrantedAuthoritySearchCriteria)
     */
    @Override
    public ISearchResults<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().listGrantedAuthorities(criteria);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.spi.user.IUserManagement#deleteGrantedAuthority(java.lang.
     * String)
     */
    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	getDelegate().deleteGrantedAuthority(authority);
    }

    /*
     * @see com.sitewhere.microservice.api.user.IUserManagement#getRoles(java.lang.
     * String)
     */
    @Override
    public List<IRole> getRoles(String username) throws SiteWhereException {
	return getDelegate().getRoles(username);
    }

    /*
     * @see com.sitewhere.microservice.api.user.IUserManagement#addRoles(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IRole> addRoles(String username, List<String> roles) throws SiteWhereException {
	return getDelegate().addRoles(username, roles);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#removeRoles(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IRole> removeRoles(String username, List<String> roles) throws SiteWhereException {
	return getDelegate().removeRoles(username, roles);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#createRole(com.sitewhere.
     * spi.user.request.IRoleCreateRequest)
     */
    @Override
    public IRole createRole(IRoleCreateRequest request) throws SiteWhereException {
	return getDelegate().createRole(request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#getRoleByName(java.lang.
     * String)
     */
    @Override
    public IRole getRoleByName(String name) throws SiteWhereException {
	return getDelegate().getRoleByName(name);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#updateRole(java.lang.
     * String, com.sitewhere.spi.user.request.IRoleCreateRequest)
     */
    @Override
    public IRole updateRole(String name, IRoleCreateRequest request) throws SiteWhereException {
	return getDelegate().updateRole(name, request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#listRoles(com.sitewhere.
     * spi.user.IRoleSearchCriteria)
     */
    @Override
    public ISearchResults<IRole> listRoles(IRoleSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listRoles(criteria);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#deleteRole(java.lang.
     * String)
     */
    @Override
    public void deleteRole(String role) throws SiteWhereException {
	getDelegate().deleteRole(role);
    }
}