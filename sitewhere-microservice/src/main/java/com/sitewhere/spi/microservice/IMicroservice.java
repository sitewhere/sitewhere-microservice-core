/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.microservice.metrics.IMetricsServer;
import com.sitewhere.spi.microservice.scripting.IScriptTemplateManager;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.system.IVersion;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.sitewhere.k8s.crd.ISiteWhereKubernetesClient;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.dataset.InstanceDatasetTemplate;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Functionality common to all SiteWhere microservices.
 */
public interface IMicroservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration>
	extends ILifecycleComponent, IMicroserviceClassification<F> {

    /**
     * Get unique id.
     * 
     * @return
     */
    UUID getId();

    /**
     * Get name shown for microservice.
     * 
     * @return
     */
    String getName();

    /**
     * Get version information.
     * 
     * @return
     */
    IVersion getVersion();

    /**
     * Get unique microservice identifier.
     * 
     * @return
     */
    F getIdentifier();

    /**
     * Get assigned hostname.
     * 
     * @return
     */
    String getHostname();

    /**
     * Get details that identify and describe the microservice.
     * 
     * @return
     */
    IMicroserviceDetails getMicroserviceDetails();

    /**
     * Get current state for microservice.
     * 
     * @return
     * @throws SiteWhereException
     */
    IMicroserviceState getCurrentState() throws SiteWhereException;

    /**
     * Called when state of managed tenant engine is updated.
     * 
     * @param state
     */
    void onTenantEngineStateChanged(ITenantEngineState state);

    /**
     * Get settings for SiteWhere instance.
     * 
     * @return
     */
    IInstanceSettings getInstanceSettings();

    /**
     * Get token management interface.
     * 
     * @return
     */
    ITokenManagement getTokenManagement();

    /**
     * Get tenant management API.
     * 
     * @return
     */
    ITenantManagement getTenantManagement();

    /**
     * Get system superuser.
     * 
     * @return
     */
    ISystemUser getSystemUser();

    /**
     * Get Kafka topic naming helper.
     * 
     * @return
     */
    IKafkaTopicNaming getKafkaTopicNaming();

    /**
     * Get manager for script templates which provide examples of
     * microservice-specific scripting funcionality.
     * 
     * @return
     */
    IScriptTemplateManager getScriptTemplateManager();

    /**
     * Get analytics processor.
     * 
     * @return
     */
    IMicroserviceAnalytics getMicroserviceAnalytics();

    /**
     * Code executed after microservice has been started.
     */
    void afterMicroserviceStarted();

    /**
     * Kubernetes for local connection.
     * 
     * @return
     */
    DefaultKubernetesClient getKubernetesClient();

    /**
     * Get SiteWhere k8s client wrapper.
     * 
     * @return
     */
    ISiteWhereKubernetesClient getSiteWhereKubernetesClient();

    /**
     * Create Kubernetes resource controllers which pull from shared informer
     * factory.
     * 
     * @param informers
     * @throws SiteWhereException
     */
    void createKubernetesResourceControllers(SharedInformerFactory informers) throws SiteWhereException;

    /**
     * Get metrics server.
     * 
     * @return
     */
    IMetricsServer getMetricsServer();

    /**
     * Get executor service that handles long-running microservice operations.
     * 
     * @return
     */
    ExecutorService getMicroserviceOperationsService();

    /**
     * Loads latest instance configuration from Kubernetes.
     * 
     * @return
     * @throws SiteWhereException
     */
    SiteWhereInstance loadInstanceConfiguration() throws SiteWhereException;

    /**
     * Loads latest instance dataset template from Kubernetes.
     * 
     * @param instance
     * @return
     * @throws SiteWhereException
     */
    InstanceDatasetTemplate loadInstanceDatasetTemplate(SiteWhereInstance instance) throws SiteWhereException;

    /**
     * Update instance configuration.
     * 
     * @param instance
     * @throws SiteWhereException
     */
    SiteWhereInstance updateInstanceConfiguration(SiteWhereInstance instance) throws SiteWhereException;

    /**
     * Get tenant engine configuration.
     * 
     * @param tenant
     * @param microservice
     * @return
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine getTenantEngineConfiguration(SiteWhereTenant tenant, SiteWhereMicroservice microservice)
	    throws SiteWhereException;

    /**
     * Set configuration for a tenant engine.
     * 
     * @param tenant
     * @param microservice
     * @param configuration
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine setTenantEngineConfiguration(SiteWhereTenant tenant, SiteWhereMicroservice microservice,
	    String configuration) throws SiteWhereException;
}