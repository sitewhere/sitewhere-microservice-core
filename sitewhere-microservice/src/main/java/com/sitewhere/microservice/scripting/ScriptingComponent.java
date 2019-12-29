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
import com.sitewhere.spi.microservice.scripting.IScriptManager;
import com.sitewhere.spi.microservice.scripting.IScriptingComponent;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;

/**
 * Common base class for scripting components.
 */
public abstract class ScriptingComponent<T> extends TenantEngineLifecycleComponent implements IScriptingComponent<T> {

    /** Unique identifier for id within context */
    private String scriptId;

    public ScriptingComponent() {
    }

    public ScriptingComponent(LifecycleComponentType type) {
	super(type);
    }

    public static Binding createBindingFor(IScriptingComponent<?> component) {
	return new Binding();
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptingComponent#run(com.
     * sitewhere.spi.microservice.scripting.ScriptScope,
     * com.sitewhere.spi.microservice.scripting.ScriptType,
     * com.sitewhere.microservice.scripting.Binding)
     */
    @Override
    public T run(ScriptScope scope, ScriptType type, Binding binding) throws SiteWhereException {
	IScriptManager manager = getScriptManagerForScope(scope);
	switch (type) {
	case Bootstrap: {
	    String script = manager.resolveBootstrapScript(getScriptId());
	    if (script == null) {
		throw new SiteWhereException(String.format("Bootstrap script not found for id: %s", getScriptId()));
	    }
	    return ScriptingUtils.run(script, binding);
	}
	case Managed: {
	    String script = manager.resolveManagedScript(getScriptId());
	    if (script == null) {
		throw new SiteWhereException(String.format("Managed script not found for id: %s", getScriptId()));
	    }
	    return ScriptingUtils.run(script, binding);
	}
	default: {
	    throw new SiteWhereException(String.format("Invalid script type used. %s", type.name()));
	}
	}
    }

    /**
     * Get script manager for the requested scope.
     * 
     * @param scope
     * @return
     * @throws SiteWhereException
     */
    protected IScriptManager getScriptManagerForScope(ScriptScope scope) throws SiteWhereException {
	if (scope == ScriptScope.Microservice) {
	    if (getMicroservice() == null) {
		throw new SiteWhereException("Scripting component has no associated microservice.");
	    }
	    return getMicroservice().getScriptManager();
	} else if (scope == ScriptScope.TenantEngine) {
	    if (getTenantEngine() == null) {
		throw new SiteWhereException("Scripting component has no associated tenant engine.");
	    }
	    return getTenantEngine().getScriptManager();
	} else {
	    throw new SiteWhereException("Invalid scope used in scripting component.");
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptingComponent#getScriptId()
     */
    @Override
    public String getScriptId() {
	return scriptId;
    }

    public void setScriptId(String scriptId) {
	this.scriptId = scriptId;
    }
}
