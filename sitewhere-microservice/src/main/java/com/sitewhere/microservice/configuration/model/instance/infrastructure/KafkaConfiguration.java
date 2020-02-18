/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration.model.instance.infrastructure;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Configuration for Kafka connectivity.
 */
@RegisterForReflection
public class KafkaConfiguration {

    /** Hostname */
    private String hostname;

    /** Port */
    private int port;

    /** Default number of topic partitions */
    private int defaultTopicPartitions;

    /** Default topic replication factor */
    private int defaultTopicReplicationFactor;

    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public int getDefaultTopicPartitions() {
	return defaultTopicPartitions;
    }

    public void setDefaultTopicPartitions(int defaultTopicPartitions) {
	this.defaultTopicPartitions = defaultTopicPartitions;
    }

    public int getDefaultTopicReplicationFactor() {
	return defaultTopicReplicationFactor;
    }

    public void setDefaultTopicReplicationFactor(int defaultTopicReplicationFactor) {
	this.defaultTopicReplicationFactor = defaultTopicReplicationFactor;
    }
}
