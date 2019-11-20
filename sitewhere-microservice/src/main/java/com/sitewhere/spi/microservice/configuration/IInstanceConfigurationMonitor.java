/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import java.util.List;

import io.sitewhere.k8s.crd.instance.SiteWhereInstance;

/**
 * Monitors instance configuration and notifies listeners of changes.
 */
public interface IInstanceConfigurationMonitor {

    /**
     * Start configuration monitoring event loop.
     */
    void start();

    /**
     * Get instance resource.
     * 
     * @return
     */
    SiteWhereInstance getInstanceResource();

    /**
     * Get list of listeners.
     * 
     * @return
     */
    List<IInstanceConfigurationListener> getListeners();
}