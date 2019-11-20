/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import java.util.List;

import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;

import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;

/**
 * Monitors microservice configuration and notifies listeners of changes.
 */
public interface IMicroserviceConfigurationMonitor {

    /**
     * Start configuration monitoring event loop.
     */
    void start();

    /**
     * Get latest resource for a microservice.
     * 
     * @param microservice
     * @return
     */
    SiteWhereMicroservice getMicroserviceResource(IMicroservice<? extends IFunctionIdentifier, ?> microservice);

    /**
     * Get list of listeners.
     * 
     * @return
     */
    List<IMicroserviceConfigurationListener> getListeners();
}
