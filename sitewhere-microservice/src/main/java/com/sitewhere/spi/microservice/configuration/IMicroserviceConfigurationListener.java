/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;

/**
 * Listens for changes in microservice configuration.
 */
public interface IMicroserviceConfigurationListener {

    /**
     * Called when microservice configuration is added.
     * 
     * @param microservice
     */
    void onMicroserviceAdded(SiteWhereMicroservice microservice);

    /**
     * Called when microservice configuration is updated.
     * 
     * @param microservice
     */
    void onMicroserviceUpdated(SiteWhereMicroservice microservice);

    /**
     * Called when microservice configuration is deleted.
     * 
     * @param microservice
     */
    void onMicroserviceDeleted(SiteWhereMicroservice microservice);
}
