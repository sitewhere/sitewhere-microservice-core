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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Common settings used in a SiteWhere instance.
 */
@Configuration
@ConfigurationProperties(prefix = "sitewhere.config")
public interface IInstanceSettings {

    /** Kubernetes name */
    public String getK8sName();

    /** Kubernetes namespace */
    public String getK8sNamespace();

    /** Kubernetes pod ip */
    public String getK8sPodIp();

    /** Product id */
    @Value("sitewhere")
    String getProductId();

    /** Keycloak service name */
    @Value("sitewhere-keycloak-http")
    String getKeycloakServiceName();

    /** Keycloak API port */
    @Value("80")
    int getKeycloakApiPort();

    /** Keycloak realm */
    @Value("sitewhere")
    String getKeycloakRealm();

    /** Keycloak master realm */
    @Value("master")
    String getKeycloakMasterRealm();

    /** Keycloak master username */
    @Value("sitewhere")
    String getKeycloakMasterUsername();

    /** Keycloak master password */
    @Value("sitewhere")
    String getKeycloakMasterPassword();

    /** Keycloak OIDC secret */
    @Value("this-should-be-set-via-environment")
    String getKeycloakOidcSecret();

    /** Keycloak system username */
    @Value("system")
    String getKeycloakSystemUsername();

    /** Keycloak system password */
    @Value("system")
    String getKeycloakSystemPassword();

    /** Redis service name */
    @Value("sitewhere-redis-headless")
    String getRedisServiceName();

    /** Redis service port */
    @Value("6379")
    int getRedisPort();

    /** Redis password */
    @Value("sitewhere")
    String getRedisPassword();
}