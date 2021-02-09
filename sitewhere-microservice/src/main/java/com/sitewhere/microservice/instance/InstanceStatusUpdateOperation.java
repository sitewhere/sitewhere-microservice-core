/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
