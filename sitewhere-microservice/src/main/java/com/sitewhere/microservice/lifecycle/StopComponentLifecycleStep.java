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
package com.sitewhere.microservice.lifecycle;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleStep;

/**
 * Implementaton of {@link ILifecycleStep} that stops a single component.
 */
public class StopComponentLifecycleStep extends ComponentOperationLifecycleStep {

    protected StopComponentLifecycleStep(ILifecycleComponent owner, ILifecycleComponent component) {
	super(owner, component);
    }

    /*
     * @see com.sitewhere.server.lifecycle.ComponentOperationLifecycleStep#getName()
     */
    @Override
    public String getName() {
	return (getComponent() != null) ? "Stop " + getComponent().getComponentName() : "Stop";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleStep#execute(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getComponent() != null) {
	    try {
		getComponent().lifecycleStop(monitor);
	    } catch (Throwable t) {
		throw new SiteWhereException("Unhandled exception shutting down component.", t);
	    }
	}
    }
}
