/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import io.sitewhere.k8s.crd.instance.SiteWhereInstanceSpec;

/**
 * Listens for changes in instance configuration.
 */
public interface IInstanceConfigurationListener {

    /**
     * Called when instance specification is updated.
     * 
     * @param specification
     */
    void onInstanceSpecificationUpdated(SiteWhereInstanceSpec specification);
}