/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

import java.util.UUID;

/**
 * Contains information about the current state of a microservice tenant engine.
 */
public interface ITenantEngineState extends IMicroserviceStateElement {

    /**
     * Get tenant id.
     * 
     * @return
     */
    UUID getTenantId();

    /**
     * Get state of component tree.
     * 
     * @return
     */
    ILifecycleComponentState getComponentState();
}