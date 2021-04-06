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
package com.sitewhere.spi.microservice.instance;

/**
 * Common settings used in a SiteWhere instance.
 */
public interface IInstanceSettings {

    /**
     * Product id
     * 
     * @return
     */
    String getProductId();

    /**
     * Get Kubernetes settings.
     * 
     * @return
     */
    IKubernetesSettings getK8s();

    /**
     * Get Keycloak settings.
     * 
     * @return
     */
    IKeycloakSettings getKeycloak();

    /**
     * Get Redis settings.
     * 
     * @return
     */
    IRedisSettings getRedis();

    public interface IKubernetesSettings {

	/**
	 * Kubernetes name
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Kubernetes namespace
	 * 
	 * @return
	 */
	String getNamespace();

	/**
	 * Kubernetes pod settings.
	 * 
	 * @return
	 */
	IKubernetesPodSettings getPod();

	public interface IKubernetesPodSettings {

	    String getIp();
	}
    }

    public interface IKeycloakSettings {

	/**
	 * Keycloak service settings.
	 * 
	 * @return
	 */
	IKeycloakServiceSettings getService();

	public interface IKeycloakServiceSettings {

	    /**
	     * Get service name.
	     * 
	     * @return
	     */
	    String getName();
	}

	/**
	 * Keycloak API settings.
	 * 
	 * @return
	 */
	IKeycloakApiSettings getApi();

	public interface IKeycloakApiSettings {

	    /**
	     * Get API port.
	     * 
	     * @return
	     */
	    int getPort();
	}

	/**
	 * Keycloak realm
	 * 
	 * @return
	 */
	String getRealm();

	/**
	 * Get Keycloak master settings.
	 * 
	 * @return
	 */
	IKeycloakMasterSettings getMaster();

	public interface IKeycloakMasterSettings {

	    /**
	     * Keycloak master realm
	     * 
	     * @return
	     */
	    String getRealm();

	    /**
	     * Keycloak master username
	     * 
	     * @return
	     */
	    String getUsername();

	    /**
	     * Keycloak master password
	     * 
	     * @return
	     */
	    String getPassword();
	}

	/**
	 * Get Keycloak OIDC settings.
	 * 
	 * @return
	 */
	IKeycloakOidcSettings getOidc();

	public interface IKeycloakOidcSettings {

	    /**
	     * Keycloak OIDC secret
	     * 
	     * @return
	     */
	    String getSecret();
	}

	/**
	 * Get Keycloak system user settings.
	 * 
	 * @return
	 */
	IKeycloakSystemSettings getSystem();

	public interface IKeycloakSystemSettings {

	    /**
	     * Keycloak system username
	     * 
	     * @return
	     */
	    String getUsername();

	    /**
	     * Keycloak system password
	     * 
	     * @return
	     */
	    String getPassword();
	}
    }

    public interface IRedisSettings {

	IRedisServiceSettings getService();

	public interface IRedisServiceSettings {

	    /**
	     * Redis service name
	     * 
	     * @return
	     */
	    String getName();
	}

	/**
	 * Redis service port
	 * 
	 * @return
	 */
	int getPort();

	/**
	 * Redis password
	 * 
	 * @return
	 */
	String getPassword();
    }
}