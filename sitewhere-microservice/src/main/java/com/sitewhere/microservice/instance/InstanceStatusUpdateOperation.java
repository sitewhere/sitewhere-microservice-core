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
import com.sitewhere.spi.microservice.instance.IInstanceStatusUpdateOperation;

import io.sitewhere.k8s.crd.common.BootstrapState;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.SiteWhereInstanceStatus;

/**
 * Base class for operations which update the Kubernetes
 * {@link SiteWhereInstance} resource status.
 */
public abstract class InstanceStatusUpdateOperation implements IInstanceStatusUpdateOperation {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(InstanceStatusUpdateOperation.class);

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceStatusUpdateOperation#
     * execute(com.sitewhere.spi.microservice.IMicroservice)
     */
    @Override
    public SiteWhereInstance execute(IMicroservice<?, ?> microservice) throws SiteWhereException {
	while (true) {
	    try {
		SiteWhereInstance instance = microservice.loadInstanceResource();
		if (instance.getStatus() == null) {
		    instance.setStatus(createDefaultStatus());
		}
		update(instance.getStatus());
		return microservice.updateInstanceStatus(instance);
	    } catch (SiteWhereException e) {
		LOGGER.info("Error updating instance status. Will retry.", e);
	    } catch (Throwable t) {
		throw new SiteWhereException("Instance status update failed.", t);
	    }
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		throw new SiteWhereException(
			"Failed to modify instance. Interrupted while waiting after concurrent update.");
	    }
	}
    }

    /**
     * Create default status values if none was set.
     * 
     * @return
     */
    protected SiteWhereInstanceStatus createDefaultStatus() {
	SiteWhereInstanceStatus status = new SiteWhereInstanceStatus();
	status.setTenantManagementBootstrapState(BootstrapState.NotBootstrapped);
	status.setUserManagementBootstrapState(BootstrapState.NotBootstrapped);
	return status;
    }
}
