/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import com.sitewhere.spi.microservice.configuration.ITenantEngineSpecUpdates;

/**
 * Holds flags to indicate which aspects of tenant engine specification were
 * updated.
 */
public class TenantEngineSpecUpdates implements ITenantEngineSpecUpdates {

    /** Indicates whether configuration was updated */
    private boolean configurationUpdated;

    /*
     * @see com.sitewhere.spi.microservice.configuration.ITenantEngineSpecUpdates#
     * isConfigurationUpdated()
     */
    @Override
    public boolean isConfigurationUpdated() {
	return configurationUpdated;
    }

    public void setConfigurationUpdated(boolean configurationUpdated) {
	this.configurationUpdated = configurationUpdated;
    }
}
