/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.ServerInfoResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.KeysMetadataRepresentation.KeyMetadataRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.info.SystemInfoRepresentation;

import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.Role;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
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

import io.sitewhere.k8s.api.ISiteWhereKubernetesClient;

/**
 * Implementation of {@link IUserManagement} that interacts with an underlying
 * Keycloak instance.
 */
@ApplicationScoped
public class KeycloakUserManagement extends LifecycleComponent implements IUserManagement {

    /** Client id for OpenID Connect support */
    private static final String CLIENT_ID_OPENID_CONNECT = "sitewhere-openid";

    /** Keycloak client */
    private Keycloak keycloak;

    /** OpenID Connect client secret */
    private String clientSecret;

    public KeycloakUserManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /**
     * Get server URL for processing requests.
     * 
     * @return
     */
    protected String getServerUrl() {
	IInstanceSettings settings = getMicroservice().getInstanceSettings();
	String serviceName = settings.getKeycloakServiceName() + "." + ISiteWhereKubernetesClient.NS_SITEWHERE_SYSTEM;
	return String.format("http://%s:%s/auth", serviceName, settings.getKeycloakApiPort());
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.AsyncStartLifecycleComponent#start(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	IInstanceSettings settings = getMicroservice().getInstanceSettings();

	// Create Keycloak API client and test it.
	String url = getServerUrl();
	getLogger().info(String.format("Connecting to Keycloak API at '%s'.", url));
	this.keycloak = KeycloakBuilder.builder().serverUrl(url).realm(settings.getKeycloakMasterRealm())
		.username(settings.getKeycloakMasterUsername()).password(settings.getKeycloakMasterPassword())
		.clientId("admin-cli").build();

	// Wait for Keycloak connection to become available.
	boolean connected = false;
	while (!connected) {
	    ServerInfoResource server = null;
	    try {
		server = getKeycloak().serverInfo();
		if (server != null) {
		    SystemInfoRepresentation system = server.getInfo().getSystemInfo();
		    getLogger().info(String.format("Keycloak API validated as version '%s'.", system.getVersion()));
		    connected = true;
		} else {
		    getLogger().info("Received null response to Keycloak server info request.");
		}
	    } catch (ProcessingException e) {
		connected = false;
	    }

	    if (!connected) {
		try {
		    getLogger().info("Unable to connect to Keycloak. Waiting to retry...");
		    Thread.sleep(2000);
		} catch (InterruptedException e1) {
		    getLogger().warn("Interrupted while waiting for Keycloak connection.");
		    return;
		}
	    }
	}

	// Make sure that the expected realm exists.
	assureRealmExists();

	// Register an OpenID Connect client for the realm.
	assureOpenIdClient();
    }

    /**
     * Assure that the expected Keycloak realm exists.
     * 
     * @throws SiteWhereException
     */
    protected void assureRealmExists() throws SiteWhereException {
	String realmName = getMicroservice().getInstanceSettings().getKeycloakRealm();
	try {
	    getRealmResource().toRepresentation();
	    getLogger().info(String.format("Realm for instance was found (%s).", realmName));
	} catch (NotFoundException e) {
	    getLogger().info(String.format("Realm for instance was not found (%s). Creating...", realmName));
	    try {
		RealmRepresentation newRealm = new RealmRepresentation();
		newRealm.setId(realmName);
		newRealm.setRealm(realmName);
		newRealm.setDisplayName("SiteWhere");
		newRealm.setEnabled(true);
		getKeycloak().realms().create(newRealm);
		getLogger().info(String.format("Successfully created realm for instance (%s).", realmName));
	    } catch (ClientErrorException e1) {
		Response response = e1.getResponse();
		if (response.getStatus() == HttpStatus.SC_CONFLICT) {
		    getLogger().info(String.format("Realm for instance was found (%s).", realmName));
		}
	    } catch (Exception e1) {
		throw new SiteWhereException(String.format("Unable to create realm for instance (%s).", realmName), e1);
	    }
	}
    }

    /**
     * Assure that openid-connect client exists.
     * 
     * @throws SiteWhereException
     */
    protected void assureOpenIdClient() throws SiteWhereException {
	try {
	    ClientResource clientResource = getRealmResource().clients().get(CLIENT_ID_OPENID_CONNECT);
	    ClientRepresentation client = clientResource.toRepresentation();
	    getLogger().info(String.format("OpenID Connect client was found (%s).", client.getId()));
	} catch (NotFoundException e) {
	    getLogger().info(
		    String.format("OpenID Connect client was not found (%s). Creating...", CLIENT_ID_OPENID_CONNECT));
	    try {
		ClientRepresentation newClient = new ClientRepresentation();
		newClient.setId(CLIENT_ID_OPENID_CONNECT);
		newClient.setName("OpenId Connect");
		newClient.setStandardFlowEnabled(true);
		newClient.setDirectAccessGrantsEnabled(true);
		newClient.setProtocol("openid-connect");
		newClient.setPublicClient(false);
		newClient.setRedirectUris(Collections.singletonList("http://*"));
		newClient.setSecret(getMicroservice().getInstanceSettings().getKeycloakOidcSecret());
		newClient.setEnabled(true);
		Response result = getRealmResource().clients().create(newClient);
		if (result.getStatus() == HttpStatus.SC_CONFLICT) {
		    getLogger().info(String.format("Found existing OpenID Connect client (%s).", newClient.getId()));
		} else if (result.getStatus() != HttpStatus.SC_CREATED) {
		    throw new SiteWhereException(result.getStatusInfo().getReasonPhrase());
		} else {
		    getLogger().info(String.format("Created OpenID Connect client (%s).", newClient.getId()));
		}
	    } catch (Exception e1) {
		throw new SiteWhereException(
			String.format("Unable to create realm for instance (%s).", CLIENT_ID_OPENID_CONNECT), e1);
	    }
	} finally {
	    this.clientSecret = getRealmResource().clients().get(CLIENT_ID_OPENID_CONNECT).getSecret().getValue();
	}
    }

    /**
     * Get realm resource based on configured value.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected RealmResource getRealmResource() throws SiteWhereException {
	String realmName = getMicroservice().getInstanceSettings().getKeycloakRealm();
	return getKeycloak().realm(realmName);
    }

    /**
     * Convert Keycloak user to SW model representation.
     * 
     * @param kc
     * @param includeRoles
     * @param includeAuths
     * @return
     * @throws SiteWhereException
     */
    protected User convert(UserRepresentation kc, boolean includeRoles, boolean includeAuths)
	    throws SiteWhereException {
	User user = new User();
	user.setUsername(kc.getUsername());
	user.setFirstName(kc.getFirstName());
	user.setLastName(kc.getLastName());
	user.setEmail(kc.getEmail());
	user.setCreatedDate(new Date(kc.getCreatedTimestamp()));

	// Pull metadata.
	if (kc.getAttributes() != null) {
	    user.setMetadata(new HashMap<>());
	    for (String key : kc.getAttributes().keySet()) {
		List<String> value = kc.getAttributes().get(key);
		if (value.size() > 0) {
		    user.getMetadata().put(key, value.get(0));
		}
	    }
	}

	// Conditionally pull roles.
	if (includeRoles) {
	    user.setRoles(new ArrayList<>());
	    List<GroupRepresentation> groups = getRealmResource().users().get(kc.getId()).groups();
	    if (groups != null) {
		for (GroupRepresentation group : groups) {
		    Role role = convert(group, includeAuths);
		    user.getRoles().add(role);
		}
	    }
	}
	return user;
    }

    /**
     * Convert Keycloak role to SW granted authority.
     * 
     * @param kc
     * @return
     */
    protected GrantedAuthority convert(RoleRepresentation kc) {
	GrantedAuthority auth = new GrantedAuthority();
	auth.setAuthority(kc.getName());
	auth.setDescription(kc.getDescription());
	return auth;
    }

    /**
     * Convert a Keycloak group to SW role.
     * 
     * @param kc
     * @param includeAuthorities
     * @return
     * @throws SiteWhereException
     */
    protected Role convert(GroupRepresentation kc, boolean includeAuthorities) throws SiteWhereException {
	Role role = new Role();
	role.setRole(kc.getName());
	role.setDescription(kc.getPath());
	if (includeAuthorities) {
	    role.setAuthorities(new ArrayList<>());
	    List<RoleRepresentation> realmRoles = getRealmResource().groups().group(kc.getId()).roles().realmLevel()
		    .listEffective();
	    if (realmRoles != null) {
		for (RoleRepresentation realmRole : realmRoles) {
		    role.getAuthorities().add(convert(realmRole));
		}
	    }
	}
	return role;
    }

    /**
     * Create a user representation based on API request.
     * 
     * @param request
     * @return
     */
    protected UserRepresentation createUserFromRequest(IUserCreateRequest request) {
	UserRepresentation user = new UserRepresentation();
	user.setUsername(request.getUsername());
	user.setFirstName(request.getFirstName());
	user.setLastName(request.getLastName());
	user.setEmail(request.getEmail());
	user.setEnabled(request.isEnabled());

	if (request.getPassword() != null) {
	    CredentialRepresentation credential = new CredentialRepresentation();
	    credential.setType(CredentialRepresentation.PASSWORD);
	    credential.setValue(request.getPassword());
	    user.setCredentials(Arrays.asList(credential));
	}

	if (request.getMetadata() != null && request.getMetadata().size() > 0) {
	    Map<String, List<String>> attrs = new HashMap<>();
	    for (String key : request.getMetadata().keySet()) {
		String value = request.getMetadata().get(key);
		attrs.put(key, Collections.singletonList(value));
	    }
	    user.setAttributes(attrs);
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
	UserRepresentation user = createUserFromRequest(request);
	Response result = getRealmResource().users().create(user);
	if (result.getStatus() != HttpStatus.SC_CREATED) {
	    throw new SiteWhereException(result.getStatusInfo().getReasonPhrase());
	}

	UserRepresentation match = findSingleUserByUsername(request.getUsername());
	if (match != null && request.getRoles() != null) {
	    for (String role : request.getRoles()) {
		GroupRepresentation groupMatch = findSingleGroupByName(role);
		if (groupMatch != null) {
		    getRealmResource().users().get(match.getId()).joinGroup(groupMatch.getId());
		}
	    }
	}

	return getUserByUsername(request.getUsername());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#getAccessToken(java.lang.
     * String, java.lang.String)
     */
    @Override
    public String getAccessToken(String username, String password) throws SiteWhereException {
	IInstanceSettings settings = getMicroservice().getInstanceSettings();
	Keycloak instance = Keycloak.getInstance(getServerUrl(), settings.getKeycloakRealm(), username, password,
		CLIENT_ID_OPENID_CONNECT, getClientSecret());
	TokenManager tokenManager = instance.tokenManager();
	AccessTokenResponse accessToken = tokenManager.getAccessToken();
	String json = new String(MarshalUtils.marshalJson(accessToken));
	return json;
    }

    /*
     * @see com.sitewhere.spi.microservice.user.IUserManagement#getPublicKey()
     */
    @Override
    public String getPublicKey() throws SiteWhereException {
	List<KeyMetadataRepresentation> keys = getRealmResource().keys().getKeyMetadata().getKeys();
	for (KeyMetadataRepresentation key : keys) {
	    if (key.getType().equals("RSA")) {
		return key.getPublicKey();
	    }
	}
	throw new SiteWhereException("No RSA public key found.");
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#updateUser(java.lang.
     * String, com.sitewhere.spi.user.request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword)
	    throws SiteWhereException {
	UserRepresentation match = findSingleUserByUsername(username);
	if (match == null) {
	    throw new SiteWhereException(String.format("No user found for username: %s", username));
	}
	UserRepresentation user = createUserFromRequest(request);
	getRealmResource().users().get(match.getId()).update(user);
	return getUserByUsername(request.getUsername());
    }

    /**
     * Find a single user matching the given username.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    protected UserRepresentation findSingleUserByUsername(String username) throws SiteWhereException {
	List<UserRepresentation> matches = getRealmResource().users().search(username, true);
	if (matches.size() > 1) {
	    throw new SiteWhereException(String.format("Matched username: %s", matches.get(0).getUsername()));
	} else if (matches.size() == 0) {
	    return null;
	}
	return matches.get(0);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#getUserByUsername(java.
     * lang.String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	UserRepresentation match = findSingleUserByUsername(username);
	return match != null ? convert(match, true, true) : null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#listUsers(com.sitewhere.
     * spi.user.IUserSearchCriteria)
     */
    @Override
    public ISearchResults<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	List<UserRepresentation> kcUsers = getRealmResource().users().list();
	Pager<IUser> pager = new Pager<IUser>(criteria);
	for (UserRepresentation kcUser : kcUsers) {
	    IUser user = convert(kcUser, true, false);
	    pager.process(user);
	}
	return new SearchResults<IUser>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#deleteUser(java.lang.
     * String)
     */
    @Override
    public IUser deleteUser(String username) throws SiteWhereException {
	UserRepresentation match = findSingleUserByUsername(username);
	if (match == null) {
	    throw new SiteWhereException(String.format("No user found for username: %s", username));
	}
	Response result = getRealmResource().users().delete(match.getId());
	if (result.getStatus() != HttpStatus.SC_NO_CONTENT) {
	    throw new SiteWhereException(result.getStatusInfo().getReasonPhrase());
	}
	return convert(match, false, false);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#createGrantedAuthority(
     * com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException {
	RoleRepresentation role = new RoleRepresentation();
	role.setComposite(request.isGroup());
	role.setName(request.getAuthority());
	role.setDescription(request.getDescription());
	getRealmResource().roles().create(role);
	role = getKeycloakRoleByName(request.getAuthority());

	if (request.getParent() != null) {
	    try {
		RoleRepresentation parent = getKeycloakRoleByName(request.getParent());
		getRealmResource().rolesById().addComposites(parent.getId(), Collections.singletonList(role));
	    } catch (NotFoundException e) {
		getLogger().warn(
			String.format("Unable to composite role to non-existent parent: %s", request.getParent()));
	    }
	}

	return convert(role);
    }

    /**
     * Get Keycloak role by name.
     * 
     * @param name
     * @return
     * @throws SiteWhereException
     */
    protected RoleRepresentation getKeycloakRoleByName(String name) throws SiteWhereException {
	List<RoleRepresentation> all = getRealmResource().roles().list();
	for (RoleRepresentation current : all) {
	    if (current.getName().equals(name)) {
		return current;
	    }
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#getGrantedAuthorityByName
     * (java.lang.String)
     */
    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	RoleRepresentation match = getKeycloakRoleByName(name);
	return match == null ? null : convert(match);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#updateGrantedAuthority(
     * java.lang.String,
     * com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#listGrantedAuthorities(
     * com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria)
     */
    @Override
    public ISearchResults<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	List<RoleRepresentation> kcRoles = getRealmResource().roles().list();
	Pager<IGrantedAuthority> pager = new Pager<IGrantedAuthority>(criteria);
	for (RoleRepresentation kcRole : kcRoles) {
	    IGrantedAuthority authority = convert(kcRole);
	    pager.process(authority);
	}
	return new SearchResults<IGrantedAuthority>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#deleteGrantedAuthority(
     * java.lang.String)
     */
    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	getRealmResource().roles().deleteRole(authority);
    }

    /*
     * @see com.sitewhere.spi.microservice.user.IUserManagement#getRoles(java.lang.
     * String)
     */
    @Override
    public List<IRole> getRoles(String username) throws SiteWhereException {
	IUser user = getUserByUsername(username);
	if (user == null) {
	    throw new SiteWhereException(String.format("User not found: %s", username));
	}
	return user.getRoles();
    }

    /*
     * @see com.sitewhere.spi.microservice.user.IUserManagement#addRoles(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IRole> addRoles(String username, List<String> roles) throws SiteWhereException {
	UserRepresentation match = findSingleUserByUsername(username);
	if (match != null) {
	    List<IRole> created = new ArrayList<>();
	    for (String role : roles) {
		GroupRepresentation groupMatch = findSingleGroupByName(role);
		if (groupMatch != null) {
		    getRealmResource().users().get(match.getId()).joinGroup(groupMatch.getId());
		    created.add(convert(groupMatch, false));
		}
	    }
	    return created;
	}
	throw new SiteWhereException(String.format("User not found: %s", username));
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#removeRoles(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IRole> removeRoles(String username, List<String> roles) throws SiteWhereException {
	UserRepresentation match = findSingleUserByUsername(username);
	if (match != null) {
	    List<IRole> removed = new ArrayList<>();
	    for (String role : roles) {
		GroupRepresentation groupMatch = findSingleGroupByName(role);
		if (groupMatch != null) {
		    getRealmResource().users().get(match.getId()).joinGroup(groupMatch.getId());
		    removed.add(convert(groupMatch, false));
		}
	    }
	    return removed;
	}
	throw new SiteWhereException(String.format("User not found: %s", username));
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#createRole(com.sitewhere.
     * spi.user.request.IRoleCreateRequest)
     */
    @Override
    public IRole createRole(IRoleCreateRequest request) throws SiteWhereException {
	GroupRepresentation group = new GroupRepresentation();
	group.setName(request.getRole());
	Response result = getRealmResource().groups().add(group);
	if (result.getStatus() != HttpStatus.SC_CREATED) {
	    throw new SiteWhereException(result.getStatusInfo().getReasonPhrase());
	}
	String location = result.getLocation().getPath();
	String id = location.substring(location.lastIndexOf('/') + 1);
	List<RoleRepresentation> roles = new ArrayList<>();
	for (String authority : request.getAuthorities()) {
	    roles.add(getRealmResource().roles().get(authority).toRepresentation());
	}
	getRealmResource().groups().group(id).roles().realmLevel().add(roles);
	return getRoleByName(group.getName());
    }

    /**
     * Find single Keycloak group by name.
     * 
     * @param name
     * @return
     * @throws SiteWhereException
     */
    protected GroupRepresentation findSingleGroupByName(String name) throws SiteWhereException {
	List<GroupRepresentation> matches = getRealmResource().groups().groups(name, 0, 1);
	if (matches.size() > 1) {
	    throw new SiteWhereException(String.format("Matched multiple groups for: %s", name));
	} else if (matches.size() == 0) {
	    return null;
	}
	return matches.get(0);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#getRoleByName(java.lang.
     * String)
     */
    @Override
    public IRole getRoleByName(String name) throws SiteWhereException {
	GroupRepresentation match = findSingleGroupByName(name);
	return match != null ? convert(match, true) : null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#updateRole(java.lang.
     * String, com.sitewhere.spi.user.request.IRoleCreateRequest)
     */
    @Override
    public IRole updateRole(String name, IRoleCreateRequest request) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#listRoles(com.sitewhere.
     * spi.user.IRoleSearchCriteria)
     */
    @Override
    public ISearchResults<IRole> listRoles(IRoleSearchCriteria criteria) throws SiteWhereException {
	List<GroupRepresentation> kcGroups = getRealmResource().groups().groups();
	Pager<IRole> pager = new Pager<IRole>(criteria);
	for (GroupRepresentation kcGroup : kcGroups) {
	    IRole role = convert(kcGroup, false);
	    pager.process(role);
	}
	return new SearchResults<IRole>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.user.IUserManagement#deleteRole(java.lang.
     * String)
     */
    @Override
    public void deleteRole(String role) throws SiteWhereException {
	GroupRepresentation match = findSingleGroupByName(role);
	if (match != null) {
	    getRealmResource().groups().group(match.getId()).remove();
	}
    }

    protected Keycloak getKeycloak() {
	return keycloak;
    }

    protected String getClientSecret() {
	return clientSecret;
    }
}
