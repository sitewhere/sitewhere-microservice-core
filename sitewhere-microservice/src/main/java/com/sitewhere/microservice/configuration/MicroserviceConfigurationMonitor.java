/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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

import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.configuration.IMicroserviceConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IMicroserviceConfigurationMonitor;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sitewhere.k8s.crd.ResourceContexts;
import io.sitewhere.k8s.crd.controller.ResourceChangeType;
import io.sitewhere.k8s.crd.controller.SiteWhereResourceController;
import io.sitewhere.k8s.crd.microservice.DoneableSiteWhereMicroservice;
import io.sitewhere.k8s.crd.microservice.MicroserviceDebugSpecification;
import io.sitewhere.k8s.crd.microservice.MicroservicePodSpecification;
import io.sitewhere.k8s.crd.microservice.MicroserviceServiceSpecification;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroserviceList;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroserviceSpec;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroserviceStatus;

/**
 * Monitors microservice resources for changes.
 */
@RegisterForReflection(targets = { SiteWhereMicroservice.class, SiteWhereMicroserviceList.class,
	SiteWhereMicroserviceSpec.class, SiteWhereMicroserviceStatus.class, DoneableSiteWhereMicroservice.class,
	MicroserviceDebugSpecification.class, MicroservicePodSpecification.class,
	MicroserviceServiceSpecification.class })
public class MicroserviceConfigurationMonitor extends SiteWhereResourceController<SiteWhereMicroservice>
	implements IMicroserviceConfigurationMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(MicroserviceConfigurationMonitor.class);

    /** Resync period in milliseconds */
    private static final int RESYNC_PERIOD_MS = 10 * 60 * 1000;

    /** Get map of most recent microservice resources */
    private Map<String, SiteWhereMicroservice> microservicesByFunction = new HashMap<>();

    /** Handles processing of queued updates */
    private ExecutorService queueProcessor = Executors.newSingleThreadExecutor(new MonitorThreadFactory());

    /** Listeners */
    private List<IMicroserviceConfigurationListener> listeners = new ArrayList<>();

    public MicroserviceConfigurationMonitor(KubernetesClient client, SharedInformerFactory informerFactory) {
	super(client, informerFactory);
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
     * @see io.sitewhere.k8s.crd.controller.SiteWhereResourceController#
     * reconcileResourceChange(io.sitewhere.k8s.crd.controller.ResourceChangeType,
     * io.fabric8.kubernetes.client.CustomResource)
     */
    @Override
    public void reconcileResourceChange(ResourceChangeType type, SiteWhereMicroservice microservice) {
	String function = microservice.getSpec().getFunctionalArea();

	// Skip changes that don't affect specification.
	SiteWhereMicroservice previous = this.microservicesByFunction.get(function);
	if (previous != null && previous.getMetadata().getGeneration() == microservice.getMetadata().getGeneration()) {
	    return;
	}
	LOGGER.info(String.format("Detected %s resource change in microservice %s.", type.name(),
		microservice.getMetadata().getName()));
	switch (type) {
	case CREATE: {
	    this.microservicesByFunction.put(function, microservice);
	    getListeners().forEach(listener -> listener.onMicroserviceAdded(microservice));
	    break;
	}
	case UPDATE: {
	    this.microservicesByFunction.put(function, microservice);
	    getListeners().forEach(listener -> listener.onMicroserviceUpdated(microservice));
	    break;
	}
	case DELETE: {
	    this.microservicesByFunction.remove(function);
	    getListeners().forEach(listener -> listener.onMicroserviceDeleted(microservice));
	    break;
	}
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationMonitor#getMicroserviceResource(com.sitewhere.spi.
     * microservice.IMicroservice)
     */
    @Override
    public SiteWhereMicroservice getMicroserviceResource(IMicroservice<? extends IFunctionIdentifier, ?> microservice) {
	return microservicesByFunction.get(microservice.getIdentifier().getPath());
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * IMicroserviceConfigurationMonitor#getListeners()
     */
    @Override
    public List<IMicroserviceConfigurationListener> getListeners() {
	return this.listeners;
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
