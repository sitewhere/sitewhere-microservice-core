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

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Allows code to be run in a separate thread along with thread local security
 * credentials for the superuser account. This allows non-authenticated services
 * to interact with GRPC persistence APIs.
 */
public abstract class SystemUserRunnable implements Runnable {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(SystemUserRunnable.class);

    /** Tenant component executing runnable */
    private ITenantEngineLifecycleComponent component;

    public SystemUserRunnable(ITenantEngineLifecycleComponent component) {
	this.component = component;
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
	    boolean systemUserFound = false;
	    while (!systemUserFound) {
		try {
		    if (getComponent().getTenantEngine() != null) {
			SiteWhereAuthentication system = getComponent().getMicroservice().getSystemUser()
				.getAuthenticationForTenant(getComponent().getTenantEngine().getTenantResource());
			UserContext.setContext(system);
		    } else {
			SiteWhereAuthentication system = getComponent().getMicroservice().getSystemUser()
				.getAuthentication();
			UserContext.setContext(system);
		    }
		    systemUserFound = true;
		} catch (NotFoundException e) {
		    LOGGER.info(String.format("No system user found. Waiting for another attempt."));
		    Thread.sleep(5000);
		} catch (NotAuthorizedException e) {
		    LOGGER.info(String.format("System user not available. Waiting for another attempt."));
		    Thread.sleep(5000);
		}
	    }
	    runAsSystemUser();
	} catch (Throwable e) {
	    LOGGER.error("Unhandled exception.", e);
	} finally {
	    UserContext.setContext(previous);
	}
    }

    protected ITenantEngineLifecycleComponent getComponent() {
	return component;
    }
}