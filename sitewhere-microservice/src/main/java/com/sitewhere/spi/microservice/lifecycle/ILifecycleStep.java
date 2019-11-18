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
 * Single step executed as part of a lifecycle operation.
 */
public interface ILifecycleStep {

    /**
     * Get the step name.
     * 
     * @return
     */
    String getName();

    /**
     * Counts the number of operations that make up this step.
     * 
     * @return
     */
    int getOperationCount();

    /**
     * Execute the step.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException;
}