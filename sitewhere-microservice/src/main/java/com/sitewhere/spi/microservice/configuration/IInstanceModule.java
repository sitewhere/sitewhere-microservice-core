/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import com.google.inject.Module;
import com.sitewhere.microservice.configuration.model.instance.InstanceConfiguration;

/**
 * Guice module used to configure objects related to the instance-global model.
 */
public interface IInstanceModule extends Module {

    /**
     * Get configuration used for module.
     * 
     * @return
     */
    InstanceConfiguration getConfiguration();
}
