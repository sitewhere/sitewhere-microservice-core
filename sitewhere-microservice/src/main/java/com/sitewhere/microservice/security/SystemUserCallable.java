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
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Allows code to be run in a separate thread along with thread local security
 * credentials for the superuser account. This allows non-authenticated services
 * to interact with GRPC persistence APIs.
 */
public abstract class SystemUserCallable<V> implements Callable<V> {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(SystemUserCallable.class);

    /** Tenant engine if tenant operation */
    private IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice;

    /** Tenant */
    private ITenant tenant;

    public SystemUserCallable(
	    IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice,
	    ITenant tenant) {
	this.microservice = microservice;
	this.tenant = tenant;
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
	// Authentication previous =
	// SecurityContextHolder.getContext().getAuthentication();
	// try {
	// if (tenant != null) {
	// Authentication system =
	// getMicroservice().getSystemUser().getAuthenticationForTenant(getTenant());
	// SecurityContextHolder.getContext().setAuthentication(system);
	// } else {
	// Authentication system =
	// getMicroservice().getSystemUser().getAuthentication();
	// SecurityContextHolder.getContext().setAuthentication(system);
	// }
	// return runAsSystemUser();
	// } finally {
	// SecurityContextHolder.getContext().setAuthentication(previous);
	// }
	return runAsSystemUser();
    }

    protected IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> getMicroservice() {
	return microservice;
    }

    protected void setMicroservice(
	    IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice) {
	this.microservice = microservice;
    }

    protected ITenant getTenant() {
	return tenant;
    }

    protected void setTenant(ITenant tenant) {
	this.tenant = tenant;
    }
}