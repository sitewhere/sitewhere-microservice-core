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
package com.sitewhere.microservice.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.configuration.IScriptConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IScriptConfigurationMonitor;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.sitewhere.k8s.crd.ResourceContexts;
import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.controller.ResourceChangeType;
import io.sitewhere.k8s.crd.controller.SiteWhereResourceController;
import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript;
import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScriptList;

/**
 * Monitors script resources for changes.
 */
public class ScriptConfigurationMonitor extends SiteWhereResourceController<SiteWhereScript>
	implements IScriptConfigurationMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(ScriptConfigurationMonitor.class);

    /** Resync period in milliseconds */
    private static final int RESYNC_PERIOD_MS = 10 * 60 * 1000;

    /** Parent microservice */
    private IMicroservice<?, ?> microservice;

    /** Script by k8s resource name */
    private Map<String, SiteWhereScript> scriptsByName = new HashMap<>();

    /** Handles processing of queued updates */
    private ExecutorService queueProcessor = Executors.newSingleThreadExecutor(new MonitorThreadFactory());

    /** Listeners */
    private List<IScriptConfigurationListener> listeners = new ArrayList<>();

    public ScriptConfigurationMonitor(KubernetesClient client, SharedInformerFactory informerFactory,
	    IMicroservice<?, ?> microservice) {
	super(client, informerFactory);
	this.microservice = microservice;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IScriptConfigurationMonitor#
     * start()
     */
    @Override
    public void start() {
	getQueueProcessor().execute(createEventLoop());
    }

    /*
     * @see
     * io.sitewhere.k8s.crd.controller.SiteWhereResourceController#createInformer()
     */
    @Override
    public SharedIndexInformer<SiteWhereScript> createInformer() {
	return getInformerFactory().sharedIndexInformerForCustomResource(ResourceContexts.SCRIPT_CONTEXT,
		SiteWhereScript.class, SiteWhereScriptList.class, RESYNC_PERIOD_MS);
    }

    /**
     * Determines whether the script is in the functional area for this
     * microservice.
     * 
     * @param script
     * @return
     */
    protected boolean isInFunctionalArea(SiteWhereScript script) {
	String area = getMicroservice().getIdentifier().getPath();
	String scriptArea = script.getMetadata().getLabels().get(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA);
	return area.equals(scriptArea);
    }

    /*
     * @see io.sitewhere.operator.controller.SiteWhereResourceController#
     * reconcileResourceChange(io.sitewhere.operator.controller.ResourceChangeType,
     * io.fabric8.kubernetes.client.CustomResource)
     */
    @Override
    public void reconcileResourceChange(ResourceChangeType type, SiteWhereScript script) {
	if (!isInFunctionalArea(script)) {
	    getMicroservice().getLogger().info(String.format("Skipping script update outside of functional area. %s",
		    script.getMetadata().getName()));
	    return;
	}

	SiteWhereScript previous = getScriptsByName().get(script.getMetadata().getName());

	// Skip changes that don't affect specification.
	if (previous != null && previous.getMetadata().getGeneration() == script.getMetadata().getGeneration()) {
	    return;
	}
	LOGGER.info(String.format("Detected %s resource change in script %s.", type.name(),
		script.getMetadata().getName()));

	ScriptSpecUpdates updates = new ScriptSpecUpdates();
	if (previous != null && !previous.getSpec().getActiveVersion().equals(script.getSpec().getActiveVersion())) {
	    updates.setActiveVersionUpdated(true);
	}

	getScriptsByName().put(script.getMetadata().getName(), script);
	switch (type) {
	case CREATE: {
	    getListeners().forEach(listener -> listener.onScriptAdded(script));
	    break;
	}
	case UPDATE: {
	    getListeners().forEach(listener -> listener.onScriptUpdated(script, updates));
	    break;
	}
	case DELETE: {
	    getListeners().forEach(listener -> listener.onScriptDeleted(script));
	    break;
	}
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IScriptConfigurationMonitor#
     * getListeners()
     */
    @Override
    public List<IScriptConfigurationListener> getListeners() {
	return this.listeners;
    }

    protected IMicroservice<?, ?> getMicroservice() {
	return microservice;
    }

    protected Map<String, SiteWhereScript> getScriptsByName() {
	return scriptsByName;
    }

    protected ExecutorService getQueueProcessor() {
	return queueProcessor;
    }

    /** Used for naming threads */
    private class MonitorThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Script Cfg");
	}
    }
}
