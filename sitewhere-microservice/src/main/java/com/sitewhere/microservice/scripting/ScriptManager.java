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
	getLogger().info(String.format("Script '%s' updated with source:\n%s\n\n", version.getMetadata().getName(),
		version.getSpec().getContent()));
	getScriptsById().put(script.getSpec().getScriptId(), active);
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptManager#removeScript(io.
     * sitewhere.k8s.crd.tenant.scripting.SiteWhereScript)
     */
    @Override
    public void removeScript(SiteWhereScript script) {
	getScriptsById().remove(script.getSpec().getScriptId());
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
	    Source found = script.getSource();
	    getLogger().info(String.format("Running '%s' from source:\n%s\n\n", identifier, found.getCharacters()));
	    return found;
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
	    SiteWhereScriptVersion version = getMicroservice().getSiteWhereKubernetesClient().getActiveVersion(script);
	    if (version != null) {
		addScript(script, version);
		getLogger().info(String.format("Added managed script '%s' with version '%s'.",
			script.getSpec().getName(), version.getMetadata().getName()));
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