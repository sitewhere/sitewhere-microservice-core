/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.microservice.exception.ConcurrentK8sUpdateException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineSpecUpdateOperation;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Base class for operations which update the Kubernetes
 * {@link SiteWhereTenantEngine} resource specification.
 */
public abstract class TenantEngineSpecUpdateOperation implements ITenantEngineSpecUpdateOperation {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(TenantEngineSpecUpdateOperation.class);

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.ITenantEngineSpecUpdateOperation#
     * execute(com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine)
     */
    @Override
    public SiteWhereTenantEngine execute(IMicroserviceTenantEngine<?> engine) throws SiteWhereException {
	while (true) {
	    try {
		SiteWhereTenantEngine current = engine.loadTenantEngineResource();
		update(current);
		return engine.updateTenantEngineResource(current);
	    } catch (ConcurrentK8sUpdateException e) {
		LOGGER.info("Tenant engine resource updated concurrently. Will retry.");
	    } catch (Throwable t) {
		throw new SiteWhereException("Tenant engine spec update failed.", t);
	    }
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		throw new SiteWhereException(
			"Failed to modify tenant engine resource. Interrupted while waiting after concurrent update.");
	    }
	}
    }
}
