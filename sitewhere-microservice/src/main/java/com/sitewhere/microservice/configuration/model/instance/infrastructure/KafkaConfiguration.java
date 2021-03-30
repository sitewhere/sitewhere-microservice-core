/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.microservice.configuration.model.instance.infrastructure;

/**
 * Configuration for Kafka connectivity.
 */
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
