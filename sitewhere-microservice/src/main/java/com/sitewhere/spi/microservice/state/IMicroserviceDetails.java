/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

/**
 * Contains details that allow a microservice to be uniquely identified and
 * described.
 */
public interface IMicroserviceDetails {

    /**
     * Get unique identifier for microservice function.
     * 
     * @return
     */
    String getIdentifier();

    /**
     * Get hostname for microservice.
     * 
     * @return
     */
    String getHostname();

    /**
     * Get display name for microservice.
     * 
     * @return
     */
    String getName();

    /**
     * Get icon for microservice.
     * 
     * @return
     */
    String getIcon();

    /**
     * Get description for microservice.
     * 
     * @return
     */
    String getDescription();

    /**
     * Indicates if microservice is global in scope.
     * 
     * @return
     */
    boolean isGlobal();
}