/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Allows code to be run in a separate thread along with thread local security
 * credentials for the superuser account. This allows non-authenticated services
 * to interact with GRPC persistence APIs.
 */
public abstract class SystemUserRunnable implements Runnable {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(SystemUserRunnable.class);

    /** Tenant engine if tenant operation */
    private IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice;

    /** Tenant */
    private SiteWhereTenant tenant;

    public SystemUserRunnable(
	    IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice,
	    SiteWhereTenant tenant) {
	this.microservice = microservice;
	this.tenant = tenant;
    }

    /**
     * Implemented in subclasses to specifiy code that should be run as the system
     * user.
     * 
     * @throws SiteWhereException
     */
    public abstract void runAsSystemUser() throws SiteWhereException;

    /*
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	SiteWhereAuthentication previous = UserContext.getCurrentUser();
	try {
	    if (tenant != null) {
		SiteWhereAuthentication system = getMicroservice().getSystemUser()
			.getAuthenticationForTenant(getTenant());
		UserContext.setContext(system);
	    } else {
		SiteWhereAuthentication system = getMicroservice().getSystemUser().getAuthentication();
		UserContext.setContext(system);
	    }
	    runAsSystemUser();
	} catch (Throwable e) {
	    LOGGER.error("Unhandled exception.", e);
	} finally {
	    UserContext.setContext(previous);
	}
    }

    protected IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> getMicroservice() {
	return microservice;
    }

    protected SiteWhereTenant getTenant() {
	return tenant;
    }
}