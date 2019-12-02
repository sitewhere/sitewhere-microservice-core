/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Common interface for scripting components.
 */
public interface IScriptingComponent<T> extends ITenantEngineLifecycleComponent {

    /**
     * Get unique id of script within scripting context.
     * 
     * @return
     */
    String getScriptId();

    /**
     * Run script in the given scope with the given binding.
     * 
     * @param scope
     * @param type
     * @param binding
     * @return
     * @throws SiteWhereException
     */
    T run(ScriptScope scope, ScriptType type, Binding binding) throws SiteWhereException;
}
