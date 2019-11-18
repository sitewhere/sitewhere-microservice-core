/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.instance;

import java.util.Optional;

/**
 * Common settings used in a SiteWhere instance.
 */
public interface IInstanceSettings {

    /**
     * Get product identifier.
     * 
     * @return
     */
    String getProductId();

    /**
     * Get unique id for instance.
     * 
     * @return
     */
    String getInstanceId();

    /**
     * Get id of instance template to use.
     * 
     * @return
     */
    String getInstanceTemplateId();

    /**
     * Get Kafka bootstrap servers configuration string.
     * 
     * @return
     */
    String getKafkaBootstrapServers();

    /**
     * Get default number of partitions used for Kafka topics.
     * 
     * @return
     */
    int getKafkaDefaultTopicPartitions();

    /**
     * Get default replication factor used for Kafka topics.
     * 
     * @return
     */
    int getKafkaDefaultTopicReplicationFactor();

    /**
     * Get hostname used by microservices to connect to Apache Syncope API.
     * 
     * @return
     */
    String getSyncopeHost();

    /**
     * Get port used by microservices to connect to Apache Syncope API.
     * 
     * @return
     */
    int getSyncopePort();

    /**
     * Get port used to allow HTTP metrics scraping.
     * 
     * @return
     */
    int getMetricsHttpPort();

    /**
     * Get max retries for gRPC exponential backoff.
     * 
     * @return
     */
    double getGrpcMaxRetryCount();

    /**
     * Get initial wait time for exponential backoff on gRPC calls.
     * 
     * @return
     */
    int getGrpcInitialBackoffInSeconds();

    /**
     * Get max time for exponential backoff on gRPC calls.
     * 
     * @return
     */
    int getGrpcMaxBackoffInSeconds();

    /**
     * Get multiplier used for exponential backoff.
     * 
     * @return
     */
    double getGrpcBackoffMultiplier();

    /**
     * Indicates whether FQDN is used when resolving gRPC services.
     * 
     * @return
     */
    boolean isGrpcResolveFQDN();

    /**
     * Get root filesystem path where microservice resources may be stored.
     * 
     * @return
     */
    String getFileSystemStorageRoot();

    /**
     * Indicates whether to log metrics.
     * 
     * @return
     */
    boolean isLogMetrics();

    /**
     * Identifies public hostname used to access microservice instance.
     * 
     * @return
     */
    Optional<String> getPublicHostname();

    /**
     * Get Kubernetes namespace.
     * 
     * @return
     */
    Optional<String> getKubernetesNamespace();

    /**
     * Get IP address for Kubernetes Pod running microservice.
     * 
     * @return
     */
    Optional<String> getKubernetesPodAddress();
}