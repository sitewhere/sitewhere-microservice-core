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
package com.sitewhere.microservice.kafka;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.errors.InvalidReplicationFactorException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.slf4j.Logger;

import com.sitewhere.microservice.configuration.model.instance.infrastructure.KafkaConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Base class for components which need to verify a Kafka topic exists or create
 * one if not.
 */
public abstract class KafkaTopicWaiter implements Runnable {

    /** Kafka availability check interval */
    private static final int KAFKA_RETRY_INTERVAL_MS = 5 * 1000;

    /** Owner component */
    private ITenantEngineLifecycleComponent component;

    /** Topic to be verified/created */
    private String topicName;

    /** Kafka admin client */
    private AdminClient kafkaAdmin;

    public KafkaTopicWaiter(ITenantEngineLifecycleComponent component, String topicName) {
	this.component = component;
	this.topicName = topicName;
    }

    /**
     * Called after topic is found to be available or has been created.
     */
    protected abstract void onTopicAvailable();

    /**
     * Build configuration settings used by admin client.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected Properties buildAdminConfiguration() throws SiteWhereException {
	Properties config = new Properties();
	config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
		KafkaUtils.getBootstrapServers(getComponent().getMicroservice()));
	return config;
    }

    @Override
    public void run() {
	KafkaConfiguration kafka = getComponent().getMicroservice().getInstanceConfiguration().getInfrastructure()
		.getKafka();
	getLogger().info("Attempting to connect to Kafka...");
	while (true) {
	    try {
		KafkaTopicWaiter.this.kafkaAdmin = AdminClient.create(buildAdminConfiguration());
		Map<String, TopicDescription> topicMap = getKafkaAdmin().describeTopics(Arrays.asList(getTopicName()))
			.all().get();
		TopicDescription topic = topicMap.get(getTopicName());
		if (topic != null) {
		    getLogger().info("Kafka detected as available.");
		    onTopicAvailable();
		    return;
		}
	    } catch (ExecutionException e) {
		Throwable t = e.getCause();
		if (t instanceof UnknownTopicOrPartitionException) {
		    try {
			NewTopic newTopic = new NewTopic(getTopicName(), kafka.getDefaultTopicPartitions(),
				(short) kafka.getDefaultTopicReplicationFactor());
			CreateTopicsResult result = getKafkaAdmin().createTopics(Collections.singletonList(newTopic));
			result.all().get();
			getLogger().info(String.format("Kafka topic '%s' created.", getTopicName()));
		    } catch (ExecutionException e1) {
			if (e1.getCause() instanceof TopicExistsException) {
			    getLogger().debug("Topic already existed.");
			} else if (e1.getCause() instanceof InvalidReplicationFactorException) {
			    getLogger().info("Not enough replicas are available to create topic. Waiting.");
			    try {
				Thread.sleep(1000);
			    } catch (InterruptedException e2) {
				getLogger().error("Interrupted while waiting for replicas.");
				return;
			    }
			} else {
			    getLogger().error("Kakfa exception creating topic.", e1);
			}
		    } catch (InterruptedException e1) {
			getLogger().error("Interrupted while creating topic.");
			return;
		    } catch (Throwable e1) {
			getLogger().error("Unhandled exception while creating topic.", e1);
		    }
		} else {
		    getLogger().warn("Execution exception connecting to Kafka. Will continue attempting to connect. ("
			    + e.getMessage() + ")", t);
		}
	    } catch (ConfigException e) {
		getLogger().warn("Configuration issue connecting to Kafka. Will continue attempting to connect.", e);
	    } catch (Throwable t) {
		getLogger().warn("Exception while connecting to Kafka. Will continue attempting to connect.", t);
	    }
	    try {
		Thread.sleep(KAFKA_RETRY_INTERVAL_MS);
	    } catch (InterruptedException e) {
		getLogger().warn("Interrupted while waiting for Kafka to become available.");
		return;
	    }
	}
    }

    protected Logger getLogger() {
	return getComponent().getLogger();
    }

    protected ITenantEngineLifecycleComponent getComponent() {
	return component;
    }

    protected String getTopicName() {
	return topicName;
    }

    protected AdminClient getKafkaAdmin() {
	return kafkaAdmin;
    }
}
