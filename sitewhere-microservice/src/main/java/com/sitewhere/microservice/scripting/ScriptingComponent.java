/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptingComponent;

/**
 * Common base class for scripting components.
 */
public abstract class ScriptingComponent<T> extends TenantEngineLifecycleComponent implements IScriptingComponent {

    public ScriptingComponent() {
    }

    public ScriptingComponent(LifecycleComponentType type) {
	super(type);
    }

    public static Binding createBindingFor(IScriptingComponent component) {
	return new Binding();
    }

    public T run(Binding binding) throws SiteWhereException {
	return null;
    }
}
