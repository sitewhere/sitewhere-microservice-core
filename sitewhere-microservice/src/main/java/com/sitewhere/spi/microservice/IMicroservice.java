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

import org.redisson.api.RedissonClient;

import com.sitewhere.microservice.configuration.model.instance.InstanceConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSpecUpdateOperation;
import com.sitewhere.spi.microservice.instance.IInstanceStatusUpdateOperation;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.microservice.metrics.IMetricsServer;
import com.sitewhere.spi.microservice.scripting.IScriptManager;
import com.sitewhere.spi.microservice.scripting.IScriptTemplateManager;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.system.IVersion;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.sitewhere.k8s.crd.ISiteWhereKubernetesClient;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.dataset.InstanceDatasetTemplate;

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
     * Get microservice version information.
     * 
     * @return
     */
    IVersion getVersion();

    /**
     * Get microservice library version information.
     * 
     * @return
     */
    IVersion getMicroserviceLibraryVersion();

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
     * Get settings for SiteWhere instance.
     * 
     * @return
     */
    IInstanceSettings getInstanceSettings();

    /**
     * Get global instance configuration settings.
     * 
     * @return
     */
    InstanceConfiguration getInstanceConfiguration();

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
     * Get manager for lastest versions of scripts which may be executed by
     * components.
     * 
     * @return
     */
    IScriptManager getScriptManager();

    /**
     * Get manager for script templates which provide examples of
     * microservice-specific scripting funcionality.
     * 
     * @return
     */
    IScriptTemplateManager getScriptTemplateManager();

    /**
     * Performs initialization operations that should be executed outside of the
     * standard microservice lifecycle.
     * 
     * @throws SiteWhereException
     */
    void install() throws SiteWhereException;

    /**
     * Shuts down the microservice, then restarts all components.
     * 
     * @throws SiteWhereException
     */
    void restart() throws SiteWhereException;

    /**
     * Performs shutdown operations that should be executed outside of the standard
     * microservice lifecycle.
     * 
     * @throws SiteWhereException
     */
    void uninstall() throws SiteWhereException;

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
     * Get client for interacting with Redis cluster for caching.
     * 
     * @return
     */
    RedissonClient getRedissonClient();

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
     * Loads latest instance resource from Kubernetes.
     * 
     * @return
     * @throws SiteWhereException
     */
    SiteWhereInstance loadInstanceResource() throws SiteWhereException;

    /**
     * Update existing instance resource.
     * 
     * @param instance
     * @return
     * @throws SiteWhereException
     */
    SiteWhereInstance updateInstanceResource(SiteWhereInstance instance) throws SiteWhereException;

    /**
     * Update instance status information.
     * 
     * @param instance
     * @return
     * @throws SiteWhereException
     */
    SiteWhereInstance updateInstanceStatus(SiteWhereInstance instance) throws SiteWhereException;

    /**
     * Loads latest instance dataset template from Kubernetes.
     * 
     * @param instance
     * @return
     * @throws SiteWhereException
     */
    InstanceDatasetTemplate loadInstanceDatasetTemplate(SiteWhereInstance instance) throws SiteWhereException;

    /**
     * Executes an instance specification update operation in the context of this
     * microservice.
     * 
     * @param operation
     * @return
     * @throws SiteWhereException
     */
    SiteWhereInstance executeInstanceSpecUpdate(IInstanceSpecUpdateOperation operation) throws SiteWhereException;

    /**
     * Executes an instance status update operation in the context of this
     * microservice.
     * 
     * @param operation
     * @return
     * @throws SiteWhereException
     */
    SiteWhereInstance executeInstanceStatusUpdate(IInstanceStatusUpdateOperation operation) throws SiteWhereException;
}