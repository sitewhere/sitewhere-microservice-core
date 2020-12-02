/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
}