/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

/**
 * Information required to create a new script.
 */
public interface IScriptCreateRequest {

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
     * Get script category.
     * 
     * @return
     */
    String getCategory();

    /**
     * Get script interpreter type.
     * 
     * @return
     */
    String getInterpreterType();

    /**
     * Get (Base64 encoded) content for script.
     * 
     * @return
     */
    String getContent();
}