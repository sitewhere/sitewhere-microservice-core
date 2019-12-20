/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import com.google.inject.AbstractModule;
import com.sitewhere.microservice.configuration.model.instance.InstanceConfiguration;
import com.sitewhere.microservice.configuration.model.instance.PersistenceConfigurations;
import com.sitewhere.spi.microservice.configuration.IInstanceModule;

/**
 * Guice module used to configure objects related to the instance-global model.
 */
public class InstanceModule extends AbstractModule implements IInstanceModule {

    /** Instance configuration */
    private InstanceConfiguration configuration;

    public InstanceModule(InstanceConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(PersistenceConfigurations.class).toInstance(getConfiguration().getPersistenceConfigurations());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceModule#getConfiguration
     * ()
     */
    @Override
    public InstanceConfiguration getConfiguration() {
	return configuration;
    }
}
