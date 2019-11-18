/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

import java.util.List;

/**
 * Contains metadata about a script.
 */
public interface IScriptMetadata {

    /**
     * Get unique script id.
     * 
     * @return
     */
    String getId();

    /**
     * Get display name for script.
     * 
     * @return
     */
    String getName();

    /**
     * Get description of what script is used for.
     * 
     * @return
     */
    String getDescription();

    /**
     * Get script type (aka file suffix).
     * 
     * @return
     */
    String getType();

    /**
     * Get version identifier for active script.
     * 
     * @return
     */
    String getActiveVersion();

    /**
     * Get list of available script versions.
     * 
     * @return
     */
    List<IScriptVersion> getVersions();
}