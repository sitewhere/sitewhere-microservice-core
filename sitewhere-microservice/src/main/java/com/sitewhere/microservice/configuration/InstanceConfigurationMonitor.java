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
import io.sitewhere.k8s.crd.ResourceContexts;
import io.sitewhere.k8s.crd.controller.ResourceChangeType;
import io.sitewhere.k8s.crd.controller.SiteWhereResourceController;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.SiteWhereInstanceList;

/**
 * Monitors instance resources for changes.
 */
public class InstanceConfigurationMonitor extends SiteWhereResourceController<SiteWhereInstance>
	implements IInstanceConfigurationMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(InstanceConfigurationMonitor.class);

    /** Resync period in milliseconds */
    private static final int RESYNC_PERIOD_MS = 10 * 60 * 1000;

    /** Get instance resource */
    private SiteWhereInstance instanceResource;

    /** Handles processing of queued updates */
    private ExecutorService queueProcessor = Executors.newSingleThreadExecutor(new MonitorThreadFactory());

    /** Listeners */
    private List<IInstanceConfigurationListener> listeners = new ArrayList<>();

    public InstanceConfigurationMonitor(SiteWhereInstance instance, KubernetesClient client,
	    SharedInformerFactory informerFactory) {
	super(client, informerFactory);
	this.instanceResource = instance;
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
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceConfigurationMonitor#
     * getResource()
     */
    @Override
    public SiteWhereInstance getResource() {
	return this.instanceResource;
    }

    /*
     * @see io.sitewhere.operator.controller.SiteWhereResourceController#
     * reconcileResourceChange(io.sitewhere.operator.controller.ResourceChangeType,
     * io.fabric8.kubernetes.client.CustomResource)
     */
    @Override
    public void reconcileResourceChange(ResourceChangeType type, SiteWhereInstance instance) {
	// Skip checks if init failed.
	if (getResource() == null) {
	    return;
	}

	// Skip changes for other instances or that don't affect specification.
	boolean sameInstance = instance.getMetadata().getUid() == getResource().getMetadata().getUid();
	boolean differentGeneration = getResource().getMetadata().getGeneration() != instance.getMetadata()
		.getGeneration();
	if (!sameInstance || !differentGeneration) {
	    return;
	}

	LOGGER.debug(String.format("Detected %s resource change in instance %s.", type.name(),
		instance.getMetadata().getName()));
	this.instanceResource = instance;

	switch (type) {
	case CREATE: {
	    break;
	}
	case UPDATE: {
	    getListeners().forEach(listener -> listener.onInstanceSpecificationUpdated(instance.getSpec()));
	    break;
	}
	case DELETE: {
	    break;
	}
	}
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
