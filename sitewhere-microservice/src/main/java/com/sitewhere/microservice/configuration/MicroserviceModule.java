/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import com.google.inject.AbstractModule;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.configuration.IMicroserviceModule;

/**
 * Guice module which uses the microservice configuration object to configure
 * components required to run the microservice.
 * 
 * @param <T>
 */
public class MicroserviceModule<T extends IMicroserviceConfiguration> extends AbstractModule
	implements IMicroserviceModule<T> {

    /** Microservice configuration */
    private T configuration;

    public MicroserviceModule(T configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroserviceModule#getConfiguration()
     */
    @Override
    public T getConfiguration() {
	return configuration;
    }
}
