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
package com.sitewhere.microservice;

import com.sitewhere.spi.microservice.IInstanceSettings;

public class InstanceSettings implements IInstanceSettings {

    /** SiteWhere product id */
    private String productId = "sitewhere";

    /** Kubernetes settings */
    private IKubernetesSettings k8s = new KubernetesSettings();

    /** Keycloak settings */
    private IKeycloakSettings keycloak = new KeycloakSettings();

    /** Redis settings */
    private IRedisSettings redis = new RedisSettings();

    public String getProductId() {
	return productId;
    }

    public void setProductId(String productId) {
	this.productId = productId;
    }

    public IKubernetesSettings getK8s() {
	return k8s;
    }

    public void setK8s(IKubernetesSettings k8s) {
	this.k8s = k8s;
    }

    public IKeycloakSettings getKeycloak() {
	return keycloak;
    }

    public void setKeycloak(IKeycloakSettings keycloak) {
	this.keycloak = keycloak;
    }

    public IRedisSettings getRedis() {
	return redis;
    }

    public void setRedis(IRedisSettings redis) {
	this.redis = redis;
    }

    public static class KubernetesSettings implements IKubernetesSettings {

	/** Kuberenetes name */
	private String name = "";

	/** Kubernetes namespace */
	private String namespace = "sitewhere";

	/** Kubernetes pod settings */
	private IKubernetesPodSettings pod = new KubernetesPodSettings();

	public class KubernetesPodSettings implements IKubernetesPodSettings {

	    /** Kubernetes pod ip */
	    private String ip = "localhost";

	    public String getIp() {
		return ip;
	    }

	    public void setIp(String ip) {
		this.ip = ip;
	    }
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getNamespace() {
	    return namespace;
	}

	public void setNamespace(String namespace) {
	    this.namespace = namespace;
	}

	public IKubernetesPodSettings getPod() {
	    return pod;
	}

	public void setPod(IKubernetesPodSettings pod) {
	    this.pod = pod;
	}
    }

    public static class KeycloakSettings implements IKeycloakSettings {

	/** Keycloak service settings */
	private IKeycloakServiceSettings service = new KeycloakServiceSettings();

	public static class KeycloakServiceSettings implements IKeycloakServiceSettings {

	    /** Keycloak service name */
	    private String name = "sitewhere-keycloak-http";

	    public String getName() {
		return name;
	    }

	    public void setName(String name) {
		this.name = name;
	    }
	}

	/** Keycloak API settings */
	private IKeycloakApiSettings api = new KeycloakApiSettings();

	public static class KeycloakApiSettings implements IKeycloakApiSettings {

	    /** Keycloak API port */
	    private int port = 80;

	    public int getPort() {
		return port;
	    }

	    public void setPort(int port) {
		this.port = port;
	    }
	}

	/** Keycloak realm */
	private String realm = "sitewhere";

	/** Keycloak master settings */
	private IKeycloakMasterSettings master = new KeycloakMasterSettings();

	public static class KeycloakMasterSettings implements IKeycloakMasterSettings {

	    /** Keycloak master realm */
	    private String realm = "master";

	    /** Keycloak master username */
	    private String username = "sitewhere";

	    /** Keycloak master password */
	    private String password = "sitewhere";

	    public String getRealm() {
		return realm;
	    }

	    public void setRealm(String realm) {
		this.realm = realm;
	    }

	    public String getUsername() {
		return username;
	    }

	    public void setUsername(String username) {
		this.username = username;
	    }

	    public String getPassword() {
		return password;
	    }

	    public void setPassword(String password) {
		this.password = password;
	    }
	}

	/** Keycloak OIDC settings */
	private IKeycloakOidcSettings oidc = new KeycloakOidcSettings();

	public static class KeycloakOidcSettings implements IKeycloakOidcSettings {

	    /** Keycloak OIDC secret */
	    private String secret = "this-should-be-set-via-environment";

	    public String getSecret() {
		return secret;
	    }

	    public void setSecret(String secret) {
		this.secret = secret;
	    }
	}

	/** Keycloak system settings */
	private IKeycloakSystemSettings system = new KeycloakSystemSettings();

	public static class KeycloakSystemSettings implements IKeycloakSystemSettings {

	    /** Keycloak system username */
	    private String username = "system";

	    /** Keycloak system password */
	    private String password = "system";

	    public String getUsername() {
		return username;
	    }

	    public void setUsername(String username) {
		this.username = username;
	    }

	    public String getPassword() {
		return password;
	    }

	    public void setPassword(String password) {
		this.password = password;
	    }
	}

	public IKeycloakServiceSettings getService() {
	    return service;
	}

	public void setService(IKeycloakServiceSettings service) {
	    this.service = service;
	}

	public IKeycloakApiSettings getApi() {
	    return api;
	}

	public void setApi(IKeycloakApiSettings api) {
	    this.api = api;
	}

	public String getRealm() {
	    return realm;
	}

	public void setRealm(String realm) {
	    this.realm = realm;
	}

	public IKeycloakMasterSettings getMaster() {
	    return master;
	}

	public void setMaster(IKeycloakMasterSettings master) {
	    this.master = master;
	}

	public IKeycloakOidcSettings getOidc() {
	    return oidc;
	}

	public void setOidc(IKeycloakOidcSettings oidc) {
	    this.oidc = oidc;
	}

	public IKeycloakSystemSettings getSystem() {
	    return system;
	}

	public void setSystem(IKeycloakSystemSettings system) {
	    this.system = system;
	}
    }

    public static class RedisSettings implements IRedisSettings {

	/** Redis service settings */
	private IRedisServiceSettings service = new RedisServiceSettings();

	public static class RedisServiceSettings implements IRedisServiceSettings {

	    /** Redis service name */
	    private String name = "sitewhere-redis-headless";

	    public String getName() {
		return name;
	    }

	    public void setName(String name) {
		this.name = name;
	    }
	}

	/** Redis port */
	private int port = 6379;

	/** Redis password */
	private String password = "sitewhere";

	public IRedisServiceSettings getService() {
	    return service;
	}

	public void setService(IRedisServiceSettings service) {
	    this.service = service;
	}

	public int getPort() {
	    return port;
	}

	public void setPort(int port) {
	    this.port = port;
	}

	public String getPassword() {
	    return password;
	}

	public void setPassword(String password) {
	    this.password = password;
	}
    }
}
