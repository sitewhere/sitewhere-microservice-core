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

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.config.ConfigProperties;

/**
 * Common settings used in a SiteWhere instance.
 */
@ConfigProperties(prefix = "sitewhere.config")
public interface IInstanceSettings {

    @ConfigProperty(name = "k8s.name")
    public String getKubernetesName();

    @ConfigProperty(name = "k8s.namespace")
    public String getKubernetesNamespace();

    @ConfigProperty(name = "k8s.pod.ip")
    public String getKubernetesPodAddress();

    @ConfigProperty(name = "product.id", defaultValue = "sitewhere")
    String getProductId();

    @ConfigProperty(name = "keycloak.service.name", defaultValue = "sitewhere-keycloak-http")
    String getKeycloakServiceName();

    @ConfigProperty(name = "keycloak.api.port", defaultValue = "80")
    int getKeycloakApiPort();

    @ConfigProperty(name = "keycloak.realm", defaultValue = "sitewhere")
    String getKeycloakRealm();

    @ConfigProperty(name = "keycloak.master.realm", defaultValue = "master")
    String getKeycloakMasterRealm();

    @ConfigProperty(name = "keycloak.master.username", defaultValue = "sitewhere")
    String getKeycloakMasterUsername();

    @ConfigProperty(name = "keycloak.master.password", defaultValue = "sitewhere")
    String getKeycloakMasterPassword();

    @ConfigProperty(name = "keycloak.oidc.secret", defaultValue = "this-should-be-set-via-environment")
    String getKeycloakOidcSecret();

    @ConfigProperty(name = "keycloak.system.username", defaultValue = "system")
    String getKeycloakSystemUsername();

    @ConfigProperty(name = "keycloak.system.password", defaultValue = "system")
    String getKeycloakSystemPassword();

    @ConfigProperty(name = "redis.service.name", defaultValue = "sitewhere-redis-headless")
    String getRedisServiceName();

    @ConfigProperty(name = "redis.port", defaultValue = "6379")
    int getRedisPort();

    @ConfigProperty(name = "redis.password", defaultValue = "sitewhere")
    String getRedisPassword();
}