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

/**
 * Composite step used for initializing a component.
 */
public class InitializeComponentLifecycleStep extends ComponentOperationLifecycleStep {

    /** Indicates of required for parent component to function */
    private boolean require;

    protected InitializeComponentLifecycleStep(ILifecycleComponent owner, ILifecycleComponent component,
	    boolean require) {
	super(owner, component);
	this.require = require;
    }

    /*
     * @see com.sitewhere.server.lifecycle.ComponentOperationLifecycleStep#getName()
     */
    @Override
    public String getName() {
	return (getComponent() != null) ? "Initialize " + getComponent().getComponentName() : "Initialize";
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
		getOwner().initializeNestedComponent(getComponent(), monitor, isRequire());
	    } catch (SiteWhereException t) {
		throw t;
	    } catch (Throwable t) {
		throw new SiteWhereException("Unable to initialize " + getComponent().getComponentName(), t);
	    }
	} else {
	    throw new SiteWhereException("Attempting to initialize component '" + getComponent().getComponentName()
		    + "' but component is null.");
	}
    }

    public boolean isRequire() {
	return require;
    }

    public void setRequire(boolean require) {
	this.require = require;
    }
}