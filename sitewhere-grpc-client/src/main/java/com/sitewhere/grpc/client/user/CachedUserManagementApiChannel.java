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
package com.sitewhere.grpc.client.user;

import java.util.List;

import com.sitewhere.grpc.client.spi.client.IUserManagementApiChannel;
import com.sitewhere.microservice.cache.CacheConfiguration;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.cache.ICacheConfiguration;
import com.sitewhere.spi.microservice.cache.ICacheProvider;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
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
 * Adds caching support to user management API channel.
 */
public class CachedUserManagementApiChannel extends TenantEngineLifecycleComponent implements IUserManagement {

    /** Cache settings */
    private CacheSettings cacheSettings;

    /** Wrapped API channel */
    private IUserManagementApiChannel<?> wrapped;

    /** User cache */
    private ICacheProvider<String, IUser> userCache;

    public CachedUserManagementApiChannel(IUserManagementApiChannel<?> wrapped, CacheSettings cache) {
	this.wrapped = wrapped;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeNestedComponent(getWrapped(), monitor, true);
	this.userCache = new UserManagementCacheProviders.UserByTokenCache(getMicroservice(),
		getCacheSettings().getUserConfiguration());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getWrapped(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	stopNestedComponent(getWrapped(), monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.user.UserManagementApiChannel#getUserByUsername(
     * java.lang.String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	IUser user = getUserCache().getCacheEntry(null, username);
	if (user == null) {
	    user = getWrapped().getUserByUsername(username);
	    getUserCache().setCacheEntry(null, username, user);
	}
	return user;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#createUser(com.sitewhere.
     * spi.user.request.IUserCreateRequest)
     */
    @Override
    public IUser createUser(IUserCreateRequest request) throws SiteWhereException {
	return getWrapped().createUser(request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.user.IUserManagement#getAccessToken(java.lang.
     * String, java.lang.String)
     */
    @Override
    public String getAccessToken(String username, String password) throws SiteWhereException {
	return getWrapped().getAccessToken(username, password);
    }

    /*
     * @see com.sitewhere.microservice.api.user.IUserManagement#getPublicKey()
     */
    @Override
    public String getPublicKey() throws SiteWhereException {
	return getWrapped().getPublicKey();
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#updateUser(java.lang.String,
     * com.sitewhere.spi.user.request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword)
	    throws SiteWhereException {
	return getWrapped().updateUser(username, request, encodePassword);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.
     * IUserSearchCriteria)
     */
    @Override
    public ISearchResults<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	return getWrapped().listUsers(criteria);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String)
     */
    @Override
    public IUser deleteUser(String username) throws SiteWhereException {
	return getWrapped().deleteUser(username);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#createGrantedAuthority(com.sitewhere.
     * spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException {
	return getWrapped().createGrantedAuthority(request);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.lang.
     * String)
     */
    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	return getWrapped().getGrantedAuthorityByName(name);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#updateGrantedAuthority(java.lang.
     * String, com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	return getWrapped().updateGrantedAuthority(name, request);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#listGrantedAuthorities(com.sitewhere.
     * spi.user.IGrantedAuthoritySearchCriteria)
     */
    @Override
    public ISearchResults<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	return getWrapped().listGrantedAuthorities(criteria);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#deleteGrantedAuthority(java.lang.
     * String)
     */
    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	getWrapped().deleteGrantedAuthority(authority);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#getRoles(java.lang.String)
     */
    @Override
    public List<IRole> getRoles(String username) throws SiteWhereException {
	return getWrapped().getRoles(username);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#addRoles(java.lang.String,
     * java.util.List)
     */
    @Override
    public List<IRole> addRoles(String username, List<String> roles) throws SiteWhereException {
	return getWrapped().addRoles(username, roles);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#removeRoles(java.lang.String,
     * java.util.List)
     */
    @Override
    public List<IRole> removeRoles(String username, List<String> roles) throws SiteWhereException {
	return getWrapped().removeRoles(username, roles);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#createRole(com.sitewhere.spi.user.
     * request.IRoleCreateRequest)
     */
    @Override
    public IRole createRole(IRoleCreateRequest request) throws SiteWhereException {
	return getWrapped().createRole(request);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#getRoleByName(java.lang.String)
     */
    @Override
    public IRole getRoleByName(String name) throws SiteWhereException {
	return getWrapped().getRoleByName(name);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#updateRole(java.lang.String,
     * com.sitewhere.spi.user.request.IRoleCreateRequest)
     */
    @Override
    public IRole updateRole(String name, IRoleCreateRequest request) throws SiteWhereException {
	return getWrapped().updateRole(name, request);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#listRoles(com.sitewhere.spi.user.
     * IRoleSearchCriteria)
     */
    @Override
    public ISearchResults<IRole> listRoles(IRoleSearchCriteria criteria) throws SiteWhereException {
	return getWrapped().listRoles(criteria);
    }

    @Override
    public void deleteRole(String role) throws SiteWhereException {
	getWrapped().deleteRole(role);
    }

    /**
     * Contains default cache settings for user management entities.
     */
    public static class CacheSettings {

	/** Cache configuraton for users */
	private ICacheConfiguration userConfiguration = new CacheConfiguration(60);

	/** Cache configuraton for granted authorities */
	private ICacheConfiguration grantedAuthConfiguration = new CacheConfiguration(60);

	public ICacheConfiguration getUserConfiguration() {
	    return userConfiguration;
	}

	public ICacheConfiguration getGrantedAuthConfiguration() {
	    return grantedAuthConfiguration;
	}
    }

    protected ICacheProvider<String, IUser> getUserCache() {
	return userCache;
    }

    protected IUserManagementApiChannel<?> getWrapped() {
	return wrapped;
    }

    protected CacheSettings getCacheSettings() {
	return cacheSettings;
    }
}