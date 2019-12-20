/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import com.google.inject.Module;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;

/**
 * Guice module which uses the microservice configuration object to configure
 * components required to run the microservice.
 * 
 * @param <T>
 */
public interface IMicroserviceModule<T extends IMicroserviceConfiguration> extends Module {

    /**
     * Get configuration used for module.
     * 
     * @return
     */
    T getConfiguration();
}
