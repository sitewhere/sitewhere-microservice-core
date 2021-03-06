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
package com.sitewhere.microservice.multitenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineStatusUpdateOperation;

import io.sitewhere.k8s.crd.common.BootstrapState;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngineStatus;

/**
 * Base class for operations which update the Kubernetes
 * {@link SiteWhereInstance} resource status.
 */
public abstract class TenantEngineStatusUpdateOperation implements ITenantEngineStatusUpdateOperation {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(TenantEngineStatusUpdateOperation.class);

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.ITenantEngineStatusUpdateOperation
     * #execute(com.sitewhere.spi.microservice.multitenant.
     * IMicroserviceTenantEngine)
     */
    @Override
    public SiteWhereTenantEngine execute(IMicroserviceTenantEngine<?> engine) throws SiteWhereException {
	while (true) {
	    try {
		SiteWhereTenantEngine current = engine.loadTenantEngineResource();
		if (current.getStatus() == null) {
		    current.setStatus(createDefaultStatus());
		}
		update(current.getStatus());
		return engine.updateTenantEngineStatus(current);
	    } catch (SiteWhereException e) {
		LOGGER.info("Unable to update tenant engine status. Will retry.", e);
	    } catch (Throwable t) {
		throw new SiteWhereException("Tenant engine status update failed.", t);
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
     * Create the default starting status for a tenant engine.
     * 
     * @return
     */
    protected SiteWhereTenantEngineStatus createDefaultStatus() {
	SiteWhereTenantEngineStatus status = new SiteWhereTenantEngineStatus();
	status.setBootstrapState(BootstrapState.NotBootstrapped);
	return status;
    }
}
