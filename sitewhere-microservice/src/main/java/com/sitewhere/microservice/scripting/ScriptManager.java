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

    /** Map of managed scripts by identifier */
    private Map<String, ScriptMetadataAndContent> scriptsById = new HashMap<>();

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptManager#addScript(com.
     * sitewhere.spi.microservice.scripting.IScriptMetadata, java.lang.String)
     */
    @Override
    public void addScript(IScriptMetadata metadata, String content) throws SiteWhereException {
	ScriptMetadataAndContent script = new ScriptMetadataAndContent(metadata, content);
	getScriptsById().put(metadata.getId(), script);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManager#resolveScript(java.
     * lang.String)
     */
    @Override
    public String resolveScript(String identifier) throws SiteWhereException {
	ScriptMetadataAndContent script = getScriptsById().get(identifier);
	if (script != null) {
	    return script.getContent();
	}
	return null;
    }

    protected Map<String, ScriptMetadataAndContent> getScriptsById() {
	return scriptsById;
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