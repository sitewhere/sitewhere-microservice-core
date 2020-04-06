/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.polyglot.Source;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.scripting.IScriptManager;

import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript;
import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScriptList;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersion;

/**
 * Manages scripts for a tenant engine.
 */
public class ScriptManager extends TenantEngineLifecycleComponent implements IScriptManager {

    /** Map of active scripts by identifier */
    private Map<String, ActiveScript> scriptsById = new HashMap<>();

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptManager#addScript(io.
     * sitewhere.k8s.crd.tenant.scripting.SiteWhereScript,
     * io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersion)
     */
    @Override
    public void addScript(SiteWhereScript script, SiteWhereScriptVersion version) throws SiteWhereException {
	ActiveScript active = new ActiveScript();
	active.loadFrom(script, version);
	getScriptsById().put(script.getMetadata().getName(), active);
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptManager#removeScript(io.
     * sitewhere.k8s.crd.tenant.scripting.SiteWhereScript)
     */
    @Override
    public void removeScript(SiteWhereScript script) {
	getScriptsById().remove(script.getMetadata().getName());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManager#resolveScriptContent(
     * java.lang.String)
     */
    @Override
    public Source resolveScriptSource(String identifier) throws SiteWhereException {
	ActiveScript script = getScriptsById().get(identifier);
	if (script != null) {
	    return script.getSource();
	}
	throw new SiteWhereException(String.format("Unable to find script for identifier: '%s'", identifier));
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	String functionalArea = getMicroservice().getIdentifier().getPath();
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereScriptList scripts = getMicroservice().getSiteWhereKubernetesClient().getScripts()
		.inNamespace(namespace).withLabel(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA, functionalArea)
		.list();
	for (SiteWhereScript script : scripts.getItems()) {
	    String activeVersion = script.getSpec().getActiveVersion();
	    if (activeVersion != null) {
		SiteWhereScriptVersion version = getMicroservice().getSiteWhereKubernetesClient().getScriptsVersions()
			.inNamespace(namespace).withName(activeVersion).get();
		if (version != null) {
		    addScript(script, version);
		    getLogger().info(String.format("Added managed script '%s' with version '%s'.",
			    script.getSpec().getName(), version.getMetadata().getName()));
		}
	    }
	}
    }

    protected Map<String, ActiveScript> getScriptsById() {
	return scriptsById;
    }

    /**
     * Holds both script metadata and associated content.
     */
    protected class ActiveScript {

	/** Script metadata */
	private SiteWhereScript script;

	/** Script version */
	private SiteWhereScriptVersion version;

	/** GraalVM source */
	private Source source;

	protected Source createSource(SiteWhereScript script, SiteWhereScriptVersion version)
		throws SiteWhereException {
	    if ("js".equals(script.getSpec().getInterpreterType())) {
		try {
		    return Source.newBuilder(ScriptingConstants.LANGUAGE_JAVASCRIPT, version.getSpec().getContent(),
			    version.getMetadata().getName()).build();
		} catch (IOException e) {
		    throw new SiteWhereException("Unable to cache script context.", e);
		}
	    }
	    throw new SiteWhereException(
		    String.format("Unknown interpreter type: %s", script.getSpec().getInterpreterType()));
	}

	/**
	 * Load cached version of script content based on k8s resources.
	 * 
	 * @param script
	 * @param version
	 * @throws SiteWhereException
	 */
	public void loadFrom(SiteWhereScript script, SiteWhereScriptVersion version) throws SiteWhereException {
	    this.script = script;
	    this.version = version;
	    this.source = createSource(script, version);
	}

	public SiteWhereScript getScript() {
	    return script;
	}

	public void setScript(SiteWhereScript script) {
	    this.script = script;
	}

	public SiteWhereScriptVersion getVersion() {
	    return version;
	}

	public void setVersion(SiteWhereScriptVersion version) {
	    this.version = version;
	}

	public Source getSource() {
	    return source;
	}

	public void setSource(Source source) {
	    this.source = source;
	}
    }
}