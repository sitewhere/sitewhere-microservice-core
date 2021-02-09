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
