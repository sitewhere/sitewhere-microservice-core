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

import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.configuration.IMicroserviceConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IMicroserviceConfigurationMonitor;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.sitewhere.k8s.crd.ResourceContexts;
import io.sitewhere.k8s.crd.controller.ResourceChangeType;
import io.sitewhere.k8s.crd.controller.SiteWhereResourceController;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroserviceList;

/**
 * Monitors microservice resources for changes.
 */
public class MicroserviceConfigurationMonitor extends SiteWhereResourceController<SiteWhereMicroservice>
	implements IMicroserviceConfigurationMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(MicroserviceConfigurationMonitor.class);

    /** Resync period in milliseconds */
    private static final int RESYNC_PERIOD_MS = 10 * 60 * 1000;

    /** Get instance resource */
    private IMicroservice<?, ?> microservice;

    /** Get instance resource */
    private SiteWhereMicroservice microserviceResource;

    /** Handles processing of queued updates */
    private ExecutorService queueProcessor = Executors.newSingleThreadExecutor(new MonitorThreadFactory());

    /** Listeners */
    private List<IMicroserviceConfigurationListener> listeners = new ArrayList<>();

    public MicroserviceConfigurationMonitor(IMicroservice<?, ?> microservice, KubernetesClient client,
	    SharedInformerFactory informerFactory) {
	super(client, informerFactory);
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationMonitor#start()
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
    public SharedIndexInformer<SiteWhereMicroservice> createInformer() {
	return getInformerFactory().sharedIndexInformerForCustomResource(ResourceContexts.MICROSERVICE_CONTEXT,
		SiteWhereMicroservice.class, SiteWhereMicroserviceList.class, RESYNC_PERIOD_MS);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationMonitor#getResource()
     */
    @Override
    public SiteWhereMicroservice getResource() {
	return this.microserviceResource;
    }

    /*
     * @see io.sitewhere.k8s.crd.controller.SiteWhereResourceController#
     * reconcileResourceChange(io.sitewhere.k8s.crd.controller.ResourceChangeType,
     * io.fabric8.kubernetes.client.CustomResource)
     */
    @Override
    public void reconcileResourceChange(ResourceChangeType type, SiteWhereMicroservice microservice) {
	// Skip changes for other microservices or that don't affect specification.
	boolean sameInstance = microservice.getMetadata().getNamespace()
		.equals(getMicroservice().getInstanceSettings().getKubernetesNamespace());
	boolean sameMicroservice = sameInstance
		&& microservice.getSpec().getFunctionalArea().equals(getMicroservice().getIdentifier().getPath());
	boolean differentGeneration = getResource() == null ? true
		: getResource().getMetadata().getGeneration() != microservice.getMetadata().getGeneration();
	if (!sameMicroservice) {
	    LOGGER.debug(String.format(
		    "Skipping %s resource change in microservice %s due to wrong instance/functional area.",
		    type.name(), microservice.getMetadata().getName()));
	    return;
	}
	if (!differentGeneration) {
	    LOGGER.debug(String.format("Skipping %s resource change in microservice %s due to same generation.",
		    type.name(), microservice.getMetadata().getName()));
	    return;
	}

	LOGGER.info(String.format("Detected %s resource change in microservice %s.", type.name(),
		microservice.getMetadata().getName()));
	this.microserviceResource = microservice;

	switch (type) {
	case CREATE: {
	    break;
	}
	case UPDATE: {
	    getListeners().forEach(listener -> listener.onMicroserviceSpecificationUpdated(microservice.getSpec()));
	    break;
	}
	case DELETE: {
	    break;
	}
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationMonitor#getListeners()
     */
    @Override
    public List<IMicroserviceConfigurationListener> getListeners() {
	return this.listeners;
    }

    protected IMicroservice<?, ?> getMicroservice() {
	return microservice;
    }

    protected ExecutorService getQueueProcessor() {
	return queueProcessor;
    }

    /** Used for naming threads */
    private class MonitorThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Microservice Cfg");
	}
    }
}
