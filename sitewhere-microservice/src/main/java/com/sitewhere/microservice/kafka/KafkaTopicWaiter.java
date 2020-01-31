/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
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
		getComponent().getMicroservice().getInstanceSettings().getKafkaBootstrapServers());
	return config;
    }

    @Override
    public void run() {
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
			IInstanceSettings settings = getComponent().getMicroservice().getInstanceSettings();
			NewTopic newTopic = new NewTopic(getTopicName(), settings.getKafkaDefaultTopicPartitions(),
				(short) settings.getKafkaDefaultTopicReplicationFactor().intValue());
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
