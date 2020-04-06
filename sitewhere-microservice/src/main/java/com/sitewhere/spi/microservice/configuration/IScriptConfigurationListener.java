/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript;

/**
 * Listens for changes in script configuration.
 */
public interface IScriptConfigurationListener {

    /**
     * Called when script configuration is added.
     * 
     * @param script
     */
    void onScriptAdded(SiteWhereScript script);

    /**
     * Called when script configuration is updated.
     * 
     * @param script
     * @param updates
     */
    void onScriptUpdated(SiteWhereScript script, IScriptSpecUpdates updates);

    /**
     * Called when script configuration is deleted.
     * 
     * @param script
     */
    void onScriptDeleted(SiteWhereScript script);
}
