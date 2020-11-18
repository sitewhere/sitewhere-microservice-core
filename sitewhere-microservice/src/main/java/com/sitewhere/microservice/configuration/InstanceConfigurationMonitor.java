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

import com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IInstanceConfigurationMonitor;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sitewhere.k8s.crd.ResourceContexts;
import io.sitewhere.k8s.crd.common.BootstrapState;
import io.sitewhere.k8s.crd.common.ComponentHelmSpec;
import io.sitewhere.k8s.crd.controller.ResourceChangeType;
import io.sitewhere.k8s.crd.controller.SiteWhereResourceController;
import io.sitewhere.k8s.crd.instance.DoneableSiteWhereInstance;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.SiteWhereInstanceList;
import io.sitewhere.k8s.crd.instance.SiteWhereInstanceSpec;
import io.sitewhere.k8s.crd.instance.SiteWhereInstanceStatus;

/**
 * Monitors instance resources for changes.
 */
@RegisterForReflection(targets = { SiteWhereInstance.class, SiteWhereInstanceList.class, SiteWhereInstanceSpec.class,
	SiteWhereInstanceStatus.class, DoneableSiteWhereInstance.class, ComponentHelmSpec.class, BootstrapState.class })
public class InstanceConfigurationMonitor extends SiteWhereResourceController<SiteWhereInstance>
	implements IInstanceConfigurationMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(InstanceConfigurationMonitor.class);

    /** Resync period in milliseconds */
    private static final int RESYNC_PERIOD_MS = 10 * 60 * 1000;

    /** Get current instance resource */
    private SiteWhereInstance instanceResource;

    /** Handles processing of queued updates */
    private ExecutorService queueProcessor = Executors.newSingleThreadExecutor(new MonitorThreadFactory());

    /** Listeners */
    private List<IInstanceConfigurationListener> listeners = new ArrayList<>();

    public InstanceConfigurationMonitor(KubernetesClient client, SharedInformerFactory informerFactory) {
	super(client, informerFactory);
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
    public SharedIndexInformer<SiteWhereInstance> createInformer() {
	return getInformerFactory().sharedIndexInformerForCustomResource(ResourceContexts.INSTANCE_CONTEXT,
		SiteWhereInstance.class, SiteWhereInstanceList.class, RESYNC_PERIOD_MS);
    }

    /*
     * @see io.sitewhere.operator.controller.SiteWhereResourceController#
     * reconcileResourceChange(io.sitewhere.operator.controller.ResourceChangeType,
     * io.fabric8.kubernetes.client.CustomResource)
     */
    @Override
    public void reconcileResourceChange(ResourceChangeType type, SiteWhereInstance instance) {
	// Skip changes that don't affect specification.
	if (this.instanceResource != null
		&& this.instanceResource.getMetadata().getGeneration() == instance.getMetadata().getGeneration()) {
	    return;
	}
	LOGGER.debug(String.format("Detected %s resource change in instance %s.", type.name(),
		instance.getMetadata().getName()));
	SiteWhereInstance previous = this.instanceResource;
	switch (type) {
	case CREATE: {
	    this.instanceResource = instance;
	    getListeners().forEach(listener -> listener.onInstanceAdded(instance));
	    break;
	}
	case UPDATE: {
	    this.instanceResource = instance;
	    InstanceSpecUpdates specUpdates = findSpecificationUpdates(previous, instance);
	    InstanceStatusUpdates statusUpdates = findStatusUpdates(previous, instance);
	    getListeners().forEach(listener -> listener.onInstanceUpdated(instance, specUpdates, statusUpdates));
	    break;
	}
	case DELETE: {
	    this.instanceResource = null;
	    getListeners().forEach(listener -> listener.onInstanceDeleted(instance));
	    break;
	}
	}
    }

    /**
     * Determine which updates were made to instance specification.
     * 
     * @param previous
     * @param updated
     * @return
     */
    protected InstanceSpecUpdates findSpecificationUpdates(SiteWhereInstance previous, SiteWhereInstance updated) {
	InstanceSpecUpdates updates = new InstanceSpecUpdates();
	SiteWhereInstanceSpec oldSpec = previous.getSpec();
	SiteWhereInstanceSpec newSpec = updated.getSpec();

	// Check for null in new spec.
	if (newSpec == null) {
	    LOGGER.warn("New instance spec set to NULL!");
	    return updates;
	}

	// Indicate if spec didn't exist before.
	if (oldSpec == null) {
	    updates.setFirstUpdate(true);
	}

	// Check whether configuration template was updated.
	if (oldSpec == null || (oldSpec.getConfigurationTemplate() != null
		&& !oldSpec.getConfigurationTemplate().equals(newSpec.getConfigurationTemplate()))) {
	    updates.setConfigurationTemplateUpdated(true);
	}

	// Check whether dataset template was updated.
	if (oldSpec == null || (oldSpec.getDatasetTemplate() != null
		&& !oldSpec.getDatasetTemplate().equals(newSpec.getDatasetTemplate()))) {
	    updates.setDatasetTemplateUpdated(true);
	}

	// Check whether configuration was updated.
	if (oldSpec == null || (oldSpec.getConfiguration() != null
		&& !oldSpec.getConfiguration().equals(newSpec.getConfiguration()))) {
	    updates.setConfigurationUpdated(true);
	}

	return updates;
    }

    /**
     * Determine which updates were made to instance status.
     * 
     * @param previous
     * @param updated
     * @return
     */
    protected InstanceStatusUpdates findStatusUpdates(SiteWhereInstance previous, SiteWhereInstance updated) {
	InstanceStatusUpdates updates = new InstanceStatusUpdates();
	SiteWhereInstanceStatus oldStatus = previous.getStatus();
	SiteWhereInstanceStatus newStatus = updated.getStatus();

	// Check for null in new status.
	if (newStatus == null) {
	    LOGGER.warn("New instance status set to NULL!");
	    return updates;
	}

	// Indicate if status didn't exist before.
	if (oldStatus == null) {
	    updates.setFirstUpdate(true);
	}

	// Detect tenant management bootstrap state updated.
	if (oldStatus == null
		|| (oldStatus.getTenantManagementBootstrapState() != newStatus.getTenantManagementBootstrapState())) {
	    updates.setTenantManagementBootstrapStateUpdated(true);
	}

	// Detect user management bootstrap state updated.
	if (oldStatus == null
		|| (oldStatus.getUserManagementBootstrapState() != newStatus.getUserManagementBootstrapState())) {
	    updates.setUserManagementBootstrapStateUpdated(true);
	}

	return updates;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceConfigurationMonitor#
     * getInstanceConfiguration()
     */
    @Override
    public SiteWhereInstance getInstanceResource() {
	return this.instanceResource;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceConfigurationMonitor#
     * getListeners()
     */
    @Override
    public List<IInstanceConfigurationListener> getListeners() {
	return this.listeners;
    }

    protected ExecutorService getQueueProcessor() {
	return queueProcessor;
    }

    /** Used for naming threads */
    private class MonitorThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Instance Cfg");
	}
    }
}
