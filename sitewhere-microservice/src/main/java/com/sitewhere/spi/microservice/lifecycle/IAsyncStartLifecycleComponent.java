/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.lifecycle;

import com.sitewhere.spi.SiteWhereException;

/**
 * Lifecycle component which handles the "start" phase asynchronously and allows
 * interested external components to wait for the async start to complete.
 */
public interface IAsyncStartLifecycleComponent extends ILifecycleComponent {

    /**
     * Implements the functionality which will be executed asynchronously.
     * 
     * @throws SiteWhereException
     */
    void asyncStart() throws SiteWhereException;

    /**
     * Indicates whether the async startup tasks have completed.
     * 
     * @return
     */
    boolean isComponentStarted();

    /**
     * Blocks the current thread waiting for the "start" phase to complete.
     */
    void waitForComponentStarted();
}