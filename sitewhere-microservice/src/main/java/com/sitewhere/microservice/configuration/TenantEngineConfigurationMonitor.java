/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /** Get current tenant engine resource */
    private SiteWhereTenantEngine tenantEngineResource;

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
	String function = tenantEngine.getMetadata().getLabels().get(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA);
	if (function == null) {
	    LOGGER.warn(String.format(
		    "No functional area label found on tenant engine '%s'. Can not detect whether it belongs to this microservice.",
		    tenantEngine.getMetadata().getName()));
	    return;
	}
	// Ignore tenant engine updates not related to this microservice.
	if (!getFunctionIdentifier().getPath().equals(function)) {
	    LOGGER.debug(String.format("Ignoring tenant engine changes for unrelated microservice: '%s'", function));
	    return;
	}

	// Skip changes that don't affect specification.
	if (this.tenantEngineResource != null && this.tenantEngineResource.getMetadata().getGeneration() == tenantEngine
		.getMetadata().getGeneration()) {
	    return;
	}

	LOGGER.info(String.format("Detected %s resource change in tenant engine %s.", type.name(),
		tenantEngine.getMetadata().getName()));
	SiteWhereTenantEngine previous = this.tenantEngineResource;
	switch (type) {
	case CREATE: {
	    this.tenantEngineResource = tenantEngine;
	    getListeners().forEach(listener -> listener.onTenantEngineCreated(tenantEngine));
	    break;
	}
	case UPDATE: {
	    this.tenantEngineResource = tenantEngine;
	    TenantEngineSpecUpdates specUpdates = findSpecificationUpdates(previous, tenantEngine);
	    getListeners().forEach(listener -> listener.onTenantEngineUpdated(tenantEngine, specUpdates));
	    break;
	}
	case DELETE: {
	    this.tenantEngineResource = null;
	    getListeners().forEach(listener -> listener.onTenantEngineDeleted(tenantEngine));
	    break;
	}
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

    protected ExecutorService getQueueProcessor() {
	return queueProcessor;
    }

    /** Used for naming threads */
    private class MonitorThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "TenantEngine Cfg");
	}
    }
}
