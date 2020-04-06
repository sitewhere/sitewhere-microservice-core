/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

import org.graalvm.polyglot.Source;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersion;

/**
 * Manages runtime scripting support for a microservice or tenant engine.
 */
public interface IScriptManager extends ITenantEngineLifecycleComponent {

    /**
     * Adds content for a managed script.
     * 
     * @param script
     * @param version
     * @throws SiteWhereException
     */
    void addScript(SiteWhereScript script, SiteWhereScriptVersion version) throws SiteWhereException;

    /**
     * Remove cached script information for the given script.
     * 
     * @param script
     */
    void removeScript(SiteWhereScript script);

    /**
     * Resolve script source based on identifier.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    Source resolveScriptSource(String identifier) throws SiteWhereException;
}