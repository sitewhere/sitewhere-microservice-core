/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.instance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.instance.IInstanceSpecUpdateOperation;

import io.sitewhere.k8s.crd.instance.SiteWhereInstance;

/**
 * Base class for operations which update the Kubernetes
 * {@link SiteWhereInstance} resource specification.
 */
public abstract class InstanceSpecUpdateOperation implements IInstanceSpecUpdateOperation {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(InstanceSpecUpdateOperation.class);

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSpecUpdateOperation#execute(
     * com.sitewhere.spi.microservice.IMicroservice)
     */
    @Override
    public SiteWhereInstance execute(IMicroservice<?, ?> microservice) throws SiteWhereException {
	while (true) {
	    try {
		SiteWhereInstance instance = microservice.loadInstanceResource();
		update(instance);
		return microservice.updateInstanceResource(instance);
	    } catch (SiteWhereException e) {
		LOGGER.info("Error updating instance configuration. Will retry.", e);
	    } catch (Throwable t) {
		throw new SiteWhereException("Instance spec update failed.", t);
	    }
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		throw new SiteWhereException(
			"Failed to modify instance. Interrupted while waiting after concurrent update.");
	    }
	}
    }
}
