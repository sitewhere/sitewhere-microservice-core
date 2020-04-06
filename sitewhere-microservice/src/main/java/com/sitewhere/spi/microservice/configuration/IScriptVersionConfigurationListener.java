/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersion;

/**
 * Listens for changes in script version configuration.
 */
public interface IScriptVersionConfigurationListener {

    /**
     * Called when script version configuration is added.
     * 
     * @param version
     */
    void onScriptVersionAdded(SiteWhereScriptVersion version);

    /**
     * Called when script version configuration is updated.
     * 
     * @param version
     * @param updates
     */
    void onScriptVersionUpdated(SiteWhereScriptVersion version, IScriptVersionSpecUpdates updates);

    /**
     * Called when script version configuration is deleted.
     * 
     * @param version
     */
    void onScriptVersionDeleted(SiteWhereScriptVersion version);
}
