/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Allows code to be run in a separate thread along with thread local security
 * credentials for the superuser account. This allows non-authenticated services
 * to interact with GRPC persistence APIs.
 */
public abstract class SystemUserCallable<V> implements Callable<V> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(SystemUserCallable.class);

    /** Tenant component executing runnable */
    private ITenantEngineLifecycleComponent component;

    public SystemUserCallable(ITenantEngineLifecycleComponent component) {
	this.component = component;
    }

    /**
     * Implemented in subclasses to specifiy code that should be run as the system
     * user.
     * 
     * @throws SiteWhereException
     */
    public abstract V runAsSystemUser() throws SiteWhereException;

    /*
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public V call() throws Exception {
	SiteWhereAuthentication previous = UserContext.getCurrentUser();
	IMicroservice<?, ?> microservice = getComponent().getMicroservice();
	SiteWhereTenant tenant = getComponent().getTenantEngine().getTenantResource();
	try {
	    if (tenant != null) {
		SiteWhereAuthentication system = microservice.getSystemUser().getAuthenticationForTenant(tenant);
		UserContext.setContext(system);
	    } else {
		SiteWhereAuthentication system = microservice.getSystemUser().getAuthentication();
		UserContext.setContext(system);
	    }
	    return runAsSystemUser();
	} catch (Throwable e) {
	    LOGGER.error("Unhandled exception.", e);
	    throw e;
	} finally {
	    UserContext.setContext(previous);
	}
    }

    protected ITenantEngineLifecycleComponent getComponent() {
	return component;
    }
}