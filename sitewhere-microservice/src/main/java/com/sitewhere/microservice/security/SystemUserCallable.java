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