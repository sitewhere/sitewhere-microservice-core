/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

import com.sitewhere.spi.SiteWhereException;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Operation that mutates a tenant engine specification.
 */
public interface ITenantEngineSpecUpdateOperation {

    /**
     * Executes the operation in the context of the given tenant engine.
     * 
     * @param engine
     * @return
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine execute(IMicroserviceTenantEngine<?> engine) throws SiteWhereException;

    /**
     * Makes an update to the current tenant engine configuration.
     * 
     * @param current
     * @throws SiteWhereException
     */
    void update(SiteWhereTenantEngine current) throws SiteWhereException;
}
