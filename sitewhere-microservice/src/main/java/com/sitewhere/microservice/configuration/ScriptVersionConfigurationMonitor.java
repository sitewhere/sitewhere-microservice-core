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

import com.sitewhere.microservice.configuration.model.instance.ScriptVersionSpecUpdates;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.configuration.IScriptVersionConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IScriptVersionConfigurationMonitor;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sitewhere.k8s.crd.ResourceContexts;
import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.controller.ResourceChangeType;
import io.sitewhere.k8s.crd.controller.SiteWhereResourceController;
import io.sitewhere.k8s.crd.tenant.scripting.version.DoneableSiteWhereScriptVersion;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersion;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersionList;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersionSpec;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersionStatus;

/**
 * Monitors script version resources for changes.
 */
@RegisterForReflection(targets = { SiteWhereScriptVersion.class, SiteWhereScriptVersionList.class,
	SiteWhereScriptVersionSpec.class, SiteWhereScriptVersionStatus.class, DoneableSiteWhereScriptVersion.class })
public class ScriptVersionConfigurationMonitor extends SiteWhereResourceController<SiteWhereScriptVersion>
	implements IScriptVersionConfigurationMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(ScriptConfigurationMonitor.class);

    /** Resync period in milliseconds */
    private static final int RESYNC_PERIOD_MS = 10 * 60 * 1000;

    /** Parent microservice */
    private IMicroservice<?, ?> microservice;

    /** Script versions by k8s resource name */
    private Map<String, SiteWhereScriptVersion> scriptVersionsByName = new HashMap<>();

    /** Handles processing of queued updates */
    private ExecutorService queueProcessor = Executors.newSingleThreadExecutor(new MonitorThreadFactory());

    /** Listeners */
    private List<IScriptVersionConfigurationListener> listeners = new ArrayList<>();

    public ScriptVersionConfigurationMonitor(KubernetesClient client, SharedInformerFactory informerFactory,
	    IMicroservice<?, ?> microservice) {
	super(client, informerFactory);
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IScriptVersionConfigurationMonitor#start()
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
    public SharedIndexInformer<SiteWhereScriptVersion> createInformer() {
	return getInformerFactory().sharedIndexInformerForCustomResource(ResourceContexts.SCRIPT_VERSION_CONTEXT,
		SiteWhereScriptVersion.class, SiteWhereScriptVersionList.class, RESYNC_PERIOD_MS);
    }

    /**
     * Determines whether the script version is in the functional area for this
     * microservice.
     * 
     * @param version
     * @return
     */
    protected boolean isInFunctionalArea(SiteWhereScriptVersion version) {
	String area = getMicroservice().getIdentifier().getPath();
	String versionArea = version.getMetadata().getLabels().get(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA);
	return area.equals(versionArea);
    }

    /*
     * @see io.sitewhere.operator.controller.SiteWhereResourceController#
     * reconcileResourceChange(io.sitewhere.operator.controller.ResourceChangeType,
     * io.fabric8.kubernetes.client.CustomResource)
     */
    @Override
    public void reconcileResourceChange(ResourceChangeType type, SiteWhereScriptVersion version) {
	if (!isInFunctionalArea(version)) {
	    getMicroservice().getLogger().info(String.format(
		    "Skipping script version update outside of functional area. %s", version.getMetadata().getName()));
	    return;
	}

	SiteWhereScriptVersion previous = getScriptVersionsByName().get(version.getMetadata().getName());

	// Skip changes that don't affect specification.
	if (previous != null && previous.getMetadata().getGeneration() == version.getMetadata().getGeneration()) {
	    return;
	}
	LOGGER.info(String.format("Detected %s resource change in script version %s.", type.name(),
		version.getMetadata().getName()));

	ScriptVersionSpecUpdates updates = new ScriptVersionSpecUpdates();
	if (previous != null && !previous.getSpec().getContent().equals(version.getSpec().getContent())) {
	    updates.setContentUpdated(true);
	}

	getScriptVersionsByName().put(version.getMetadata().getName(), version);
	switch (type) {
	case CREATE: {
	    getListeners().forEach(listener -> listener.onScriptVersionAdded(version));
	    break;
	}
	case UPDATE: {
	    getListeners().forEach(listener -> listener.onScriptVersionUpdated(version, updates));
	    break;
	}
	case DELETE: {
	    getListeners().forEach(listener -> listener.onScriptVersionDeleted(version));
	    break;
	}
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IScriptVersionConfigurationMonitor#getListeners()
     */
    @Override
    public List<IScriptVersionConfigurationListener> getListeners() {
	return this.listeners;
    }

    protected IMicroservice<?, ?> getMicroservice() {
	return microservice;
    }

    protected ExecutorService getQueueProcessor() {
	return queueProcessor;
    }

    protected Map<String, SiteWhereScriptVersion> getScriptVersionsByName() {
	return scriptVersionsByName;
    }

    /** Used for naming threads */
    private class MonitorThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Scr Version Cfg");
	}
    }
}
