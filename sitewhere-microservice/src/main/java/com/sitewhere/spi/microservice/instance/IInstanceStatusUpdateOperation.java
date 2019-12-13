/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.instance;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;

import io.sitewhere.k8s.crd.instance.SiteWhereInstance;

/**
 * Operation that mutates the SiteWhere instance resource status.
 */
public interface IInstanceStatusUpdateOperation {

    /**
     * Executes the operation in the context of the given microservice.
     * 
     * @param microservice
     * @return
     * @throws SiteWhereException
     */
    SiteWhereInstance execute(IMicroservice<?, ?> microservice) throws SiteWhereException;

    /**
     * Makes an update to the current instance status.
     * 
     * @param current
     * @throws SiteWhereException
     */
    void update(SiteWhereInstance current) throws SiteWhereException;
}
