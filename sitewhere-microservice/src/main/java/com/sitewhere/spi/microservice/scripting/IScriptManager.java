/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages runtime scripting support for a microservice or tenant engine.
 */
public interface IScriptManager extends ITenantEngineLifecycleComponent {

    /**
     * Adds bootstrap script with a given identifier.
     * 
     * @param identifier
     * @param content
     * @throws SiteWhereException
     */
    void addBootstrapScript(String identifier, String content) throws SiteWhereException;

    /**
     * Adds content from a managed script.
     * 
     * @param metadata
     * @param content
     * @throws SiteWhereException
     */
    void addManagedScript(IScriptMetadata metadata, String content) throws SiteWhereException;

    /**
     * Resolve a bootstrap script based on identifier.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    String resolveBootstrapScript(String identifier) throws SiteWhereException;

    /**
     * Resolve a managed script based on identifier.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    String resolveManagedScript(String identifier) throws SiteWhereException;
}