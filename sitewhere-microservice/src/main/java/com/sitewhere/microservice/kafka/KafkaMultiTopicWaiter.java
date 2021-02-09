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
