/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.instance;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.config.ConfigProperties;

/**
 * Common settings used in a SiteWhere instance.
 */
@ConfigProperties(prefix = "sitewhere.config")
public interface IInstanceSettings {

    @ConfigProperty(name = "k8s.name")
    public Optional<String> getKubernetesName();

    @ConfigProperty(name = "k8s.namespace")
    public Optional<String> getKubernetesNamespace();

    @ConfigProperty(name = "k8s.pod.ip")
    public Optional<String> getKubernetesPodAddress();

    @ConfigProperty(name = "product.id", defaultValue = "sitewhere")
    String getProductId();

    @ConfigProperty(name = "instance.id", defaultValue = "sitewhere1")
    String getInstanceId();

    @ConfigProperty(name = "instance.template.id", defaultValue = "default")
    String getInstanceTemplateId();

    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "cp-kafka:9092")
    String getKafkaBootstrapServers();

    @ConfigProperty(name = "kafka.defaultTopicPartitions", defaultValue = "8")
    Integer getKafkaDefaultTopicPartitions();

    @ConfigProperty(name = "kafka.defaultTopicReplicationFactor", defaultValue = "3")
    Integer getKafkaDefaultTopicReplicationFactor();

    @ConfigProperty(name = "metrics.port", defaultValue = "9090")
    Integer getMetricsHttpPort();

    @ConfigProperty(name = "filesystem.storage.root", defaultValue = "/var/sitewhere/")
    public String getFileSystemStorageRoot();

    @ConfigProperty(name = "log.metrics", defaultValue = "false")
    public Boolean getLogMetrics();

    @ConfigProperty(name = "service.public.hostname")
    public Optional<String> getPublicHostname();

    @ConfigProperty(name = "syncope.host", defaultValue = "syncope")
    String getSyncopeHost();

    @ConfigProperty(name = "syncope.port", defaultValue = "8080")
    Integer getSyncopePort();

    @ConfigProperty(name = "grpcMaxRetryCount", defaultValue = "6")
    Double getGrpcMaxRetryCount();

    @ConfigProperty(name = "grpcInitialBackoffSeconds", defaultValue = "10")
    public Integer getGrpcInitialBackoffInSeconds();

    @ConfigProperty(name = "grpcMaxBackoffSeconds", defaultValue = "600")
    public Integer getGrpcMaxBackoffInSeconds();

    @ConfigProperty(name = "grpcBackoffMultiplier", defaultValue = "1.5")
    public Double getGrpcBackoffMultiplier();

    @ConfigProperty(name = "grpcResolveFQDN", defaultValue = "false")
    public Boolean getGrpcResolveFQDN();
}