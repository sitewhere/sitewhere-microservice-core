/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.scripting.IScriptManager;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;

/**
 * Manages scripts for a microservice or tenant engine.
 */
public class ScriptManager extends TenantEngineLifecycleComponent implements IScriptManager {

    /** Map of bootstrap scripts by identifier */
    private Map<String, String> bootstrapScriptsById = new HashMap<>();

    /** Map of managed scripts by identifier */
    private Map<String, ScriptMetadataAndContent> managedScriptsById = new HashMap<>();

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManager#addBootstrapScript(
     * java.lang.String, java.lang.String)
     */
    @Override
    public void addBootstrapScript(String identifier, String content) throws SiteWhereException {
	getBootstrapScriptsById().put(identifier, content);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManager#addManagedScript(com.
     * sitewhere.spi.microservice.scripting.IScriptMetadata, java.lang.String)
     */
    @Override
    public void addManagedScript(IScriptMetadata metadata, String content) throws SiteWhereException {
	ScriptMetadataAndContent script = new ScriptMetadataAndContent(metadata, content);
	getManagedScriptsById().put(metadata.getId(), script);
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptManager#
     * resolveBootstrapScript(java.lang.String)
     */
    @Override
    public String resolveBootstrapScript(String identifier) throws SiteWhereException {
	return getBootstrapScriptsById().get(identifier);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManager#resolveManagedScript(
     * java.lang.String)
     */
    @Override
    public String resolveManagedScript(String identifier) throws SiteWhereException {
	ScriptMetadataAndContent script = getManagedScriptsById().get(identifier);
	if (script != null) {
	    return script.getContent();
	}
	return null;
    }

    protected Map<String, String> getBootstrapScriptsById() {
	return bootstrapScriptsById;
    }

    protected Map<String, ScriptMetadataAndContent> getManagedScriptsById() {
	return managedScriptsById;
    }

    /**
     * Holds both script metadata and associated content.
     */
    protected class ScriptMetadataAndContent {

	/** Script metadata */
	private IScriptMetadata metadata;

	/** Script content */
	private String content;

	public ScriptMetadataAndContent(IScriptMetadata metadata, String content) {
	    this.metadata = metadata;
	    this.content = content;
	}

	public IScriptMetadata getMetadata() {
	    return metadata;
	}

	public void setMetadata(IScriptMetadata metadata) {
	    this.metadata = metadata;
	}

	public String getContent() {
	    return content;
	}

	public void setContent(String content) {
	    this.content = content;
	}
    }
}