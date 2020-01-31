/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Waits on multiple Kafka topics to be found as available or created.
 */
public class KafkaMultiTopicWaiter {

    /** Owner component */
    private ITenantEngineLifecycleComponent component;

    /** Topic names to verify/create */
    private List<String> topicNames;

    /** Latch for counting responses */
    private CountDownLatch latch;

    /** Threading */
    private Executor executor = Executors.newSingleThreadExecutor();

    public KafkaMultiTopicWaiter(ITenantEngineLifecycleComponent component, List<String> topicNames) {
	this.component = component;
	this.topicNames = topicNames;
	this.latch = new CountDownLatch(topicNames.size());
    }

    /**
     * Blocking function which waits for topics to be verified or created.
     */
    public void verify() {
	for (String topicName : getTopicNames()) {
	    executor.execute(new TopicWaiter(getComponent(), topicName));
	}
	try {
	    getLatch().await();
	} catch (InterruptedException e) {
	    getComponent().getLogger().info("Interrupted while waiting for Kafka topic to be verified/created.");
	}
    }

    /**
     * Waits on topic and counts down latch after verification.
     */
    protected class TopicWaiter extends KafkaTopicWaiter {

	public TopicWaiter(ITenantEngineLifecycleComponent component, String topicName) {
	    super(component, topicName);
	}

	/*
	 * @see com.sitewhere.microservice.kafka.KafkaTopicWaiter#onTopicAvailable()
	 */
	@Override
	protected void onTopicAvailable() {
	    getLatch().countDown();
	}
    }

    protected ITenantEngineLifecycleComponent getComponent() {
	return component;
    }

    protected List<String> getTopicNames() {
	return topicNames;
    }

    protected CountDownLatch getLatch() {
	return latch;
    }
}
