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

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.ITenantEngineConfigurationListener;
import com.sitewhere.spi.microservice.configuration.ITenantEngineConfigurationMonitor;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sitewhere.k8s.crd.ResourceContexts;
import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.controller.ResourceChangeType;
import io.sitewhere.k8s.crd.controller.SiteWhereResourceController;
import io.sitewhere.k8s.crd.tenant.engine.DoneableSiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngineList;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngineSpec;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngineStatus;

/**
 * Monitors tenant engines for changes.
 */
@RegisterForReflection(targets = { SiteWhereTenantEngine.class, SiteWhereTenantEngineList.class,
	SiteWhereTenantEngineSpec.class, SiteWhereTenantEngineStatus.class, DoneableSiteWhereTenantEngine.class })
public class TenantEngineConfigurationMonitor extends SiteWhereResourceController<SiteWhereTenantEngine>
	implements ITenantEngineConfigurationMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(TenantEngineConfigurationMonitor.class);

    /** Resync period in milliseconds */
    private static final int RESYNC_PERIOD_MS = 10 * 60 * 1000;

    /** Map of most recent tenant engine resources by tenant */
    private Map<String, SiteWhereTenantEngine> tenantEngineResourcesByTenant = new HashMap<>();

    /** Identifier for type of tenant engines to monitor */
    private IFunctionIdentifier functionIdentifier;

    /** Handles processing of queued updates */
    private ExecutorService queueProcessor = Executors.newSingleThreadExecutor(new MonitorThreadFactory());

    /** Listeners */
    private List<ITenantEngineConfigurationListener> listeners = new ArrayList<>();

    public TenantEngineConfigurationMonitor(IFunctionIdentifier functionIdentifier, KubernetesClient client,
	    SharedInformerFactory informerFactory) {
	super(client, informerFactory);
	this.functionIdentifier = functionIdentifier;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * ITenantEngineConfigurationMonitor#getFunctionIdentifier()
     */
    @Override
    public IFunctionIdentifier getFunctionIdentifier() {
	return functionIdentifier;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceConfigurationMonitor#
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
    public SharedIndexInformer<SiteWhereTenantEngine> createInformer() {
	return getInformerFactory().sharedIndexInformerForCustomResource(ResourceContexts.TENANT_ENGINE_CONTEXT,
		SiteWhereTenantEngine.class, SiteWhereTenantEngineList.class, RESYNC_PERIOD_MS);
    }

    /*
     * @see io.sitewhere.k8s.crd.controller.SiteWhereResourceController#
     * reconcileResourceChange(io.sitewhere.k8s.crd.controller.ResourceChangeType,
     * io.fabric8.kubernetes.client.CustomResource)
     */
    @Override
    public void reconcileResourceChange(ResourceChangeType type, SiteWhereTenantEngine tenantEngine) {
	try {
	    String function = tenantEngine.getMetadata().getLabels()
		    .get(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA);
	    if (function == null) {
		LOGGER.warn(String.format(
			"No functional area label found on tenant engine '%s'. Can not detect whether it belongs to this microservice.",
			tenantEngine.getMetadata().getName()));
		return;
	    }
	    // Ignore tenant engine updates not related to this microservice.
	    if (!getFunctionIdentifier().getPath().equals(function)) {
		LOGGER.debug(
			String.format("Ignoring tenant engine changes for unrelated microservice: '%s'", function));
		return;
	    }

	    LOGGER.info(String.format("Detected %s resource change in tenant engine %s.", type.name(),
		    tenantEngine.getMetadata().getName()));

	    // Check for existing tenant engine resource reference.
	    SiteWhereTenantEngine existing = getExistingForSameTenant(tenantEngine);

	    // Skip changes that don't affect specification.
	    if (type == ResourceChangeType.UPDATE && existing != null
		    && existing.getMetadata().getGeneration() == tenantEngine.getMetadata().getGeneration()) {
		LOGGER.info(
			String.format("Skipping %s resource change in tenant engine %s since generation not updated.",
				type.name(), tenantEngine.getMetadata().getName()));
		return;
	    }

	    switch (type) {
	    case CREATE: {
		addOrUpdateTenantEngineResource(tenantEngine);
		getListeners().forEach(listener -> listener.onTenantEngineCreated(tenantEngine));
		break;
	    }
	    case UPDATE: {
		addOrUpdateTenantEngineResource(tenantEngine);
		TenantEngineSpecUpdates specUpdates = findSpecificationUpdates(existing, tenantEngine);
		getListeners().forEach(listener -> listener.onTenantEngineUpdated(tenantEngine, specUpdates));
		break;
	    }
	    case DELETE: {
		addOrUpdateTenantEngineResource(tenantEngine);
		getListeners().forEach(listener -> listener.onTenantEngineDeleted(tenantEngine));
		break;
	    }
	    }
	} catch (SiteWhereException e) {
	    LOGGER.warn(String.format("Unable to process resource update of type %s for tenant engine %s.", type,
		    tenantEngine.getMetadata().getName()));
	}
    }

    /**
     * Determine which updates were made to tenant engine specification.
     * 
     * @param previous
     * @param updated
     * @return
     */
    protected TenantEngineSpecUpdates findSpecificationUpdates(SiteWhereTenantEngine previous,
	    SiteWhereTenantEngine updated) {
	TenantEngineSpecUpdates updates = new TenantEngineSpecUpdates();
	SiteWhereTenantEngineSpec oldSpec = previous.getSpec();
	SiteWhereTenantEngineSpec newSpec = updated.getSpec();

	// Check for null in new spec.
	if (newSpec == null) {
	    LOGGER.warn("New tenant engine spec set to NULL!");
	    return updates;
	}

	// Check whether configuration was updated.
	if (oldSpec == null || (oldSpec.getConfiguration() != null
		&& !oldSpec.getConfiguration().equals(newSpec.getConfiguration()))) {
	    updates.setConfigurationUpdated(true);
	}

	return updates;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * ITenantEngineConfigurationMonitor#getListeners()
     */
    @Override
    public List<ITenantEngineConfigurationListener> getListeners() {
	return listeners;
    }

    /**
     * Adds or updates tenant engine in map by tenant id.
     * 
     * @param engine
     * @throws SiteWhereException
     */
    protected void addOrUpdateTenantEngineResource(SiteWhereTenantEngine engine) throws SiteWhereException {
	String tenant = engine.getMetadata().getLabels().get(ResourceLabels.LABEL_SITEWHERE_TENANT);
	if (tenant == null) {
	    throw new SiteWhereException(
		    String.format("No tenant label found for tenant engine: %s", engine.getMetadata().getName()));
	}
	LOGGER.info(
		String.format("Caching new instance of tenant engine resource for %s", engine.getMetadata().getName()));
	getTenantEngineResourcesByTenant().put(tenant, engine);
    }

    /**
     * Get existing tenant engine resource based on tenant specified in updated
     * engine. (Null if none already registered).
     * 
     * @param updated
     * @return
     * @throws SiteWhereException
     */
    protected SiteWhereTenantEngine getExistingForSameTenant(SiteWhereTenantEngine updated) throws SiteWhereException {
	String tenant = updated.getMetadata().getLabels().get(ResourceLabels.LABEL_SITEWHERE_TENANT);
	if (tenant == null) {
	    throw new SiteWhereException(
		    String.format("No tenant label found for tenant engine: %s", updated.getMetadata().getName()));
	}
	return getTenantEngineResourcesByTenant().get(tenant);
    }

    protected Map<String, SiteWhereTenantEngine> getTenantEngineResourcesByTenant() {
	return tenantEngineResourcesByTenant;
    }

    protected ExecutorService getQueueProcessor() {
	return queueProcessor;
    }

    /** Used for naming threads */
    private class MonitorThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
	    return new Thread(r, "TenantEngine Cfg");
	}
    }
}
