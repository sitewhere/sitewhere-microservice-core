/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kubernetes;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

/**
 * Provides singleton instance of k8s client.
 */
@ApplicationScoped
public class KubernetesClientProvider {

    @Produces
    @Singleton
    DefaultKubernetesClient newClient() {
	Config config = new ConfigBuilder().withNamespace(null).build();
	return new DefaultKubernetesClient(config);
    }
}
