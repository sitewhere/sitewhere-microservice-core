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
package com.sitewhere.spi.microservice;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;

/**
 * Exception on server startup that will prevent server from functioning.
 */
public class ServerStartupException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 3458605782783632700L;

    /** Component that caused startup to fail */
    private ILifecycleComponent component;

    public ServerStartupException(ILifecycleComponent component, String message, Throwable t) {
	super(message, t);
	this.component = component;
    }

    public ILifecycleComponent getComponent() {
	return component;
    }

    public void setComponent(ILifecycleComponent component) {
	this.component = component;
    }
}