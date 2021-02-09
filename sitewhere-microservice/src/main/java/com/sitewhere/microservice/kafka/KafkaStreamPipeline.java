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

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.IKafkaStreamPipeline;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Builds a pipeline for interacting with data consumed from and produced into
 * Kafka topics.
 */
public abstract class KafkaStreamPipeline extends TenantEngineLifecycleComponent implements IKafkaStreamPipeline {

    /** Pipeline instance */
    private KafkaStreams pipeline;

    /**
     * Get unique name suffix for pipeline.
     * 
     * @return
     */
    public abstract String getPipelineName();

    /**
     * Get default key serde class.
     * 
     * @return
     */
    public Class<?> getDefaultKeySerdeClass() {
	return Serdes.String().getClass();
    }

    /**
     * Get default value serde class.
     * 
     * @return
     */
    public Class<?> getDefaultValueSerdeClass() {
	return Serdes.String().getClass();
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Wait for source topics to be verified or created.
	KafkaMultiTopicWaiter waiter = new KafkaMultiTopicWaiter(this, getSourceTopicNames());
	waiter.verify();

	Properties props = new Properties();
	String appId = String.format("%s-%s-%s-%s-%s", getMicroservice().getInstanceSettings().getProductId(),
		getMicroservice().getInstanceSettings().getKubernetesNamespace(),
		getTenantEngine().getTenantResource().getMetadata().getName(),
		getMicroservice().getIdentifier().getPath(), getPipelineName());
	props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
	props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaUtils.getBootstrapServers(getMicroservice()));
	props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, getDefaultKeySerdeClass());
	props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, getDefaultValueSerdeClass());
	props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
		LogAndContinueExceptionHandler.class);

	final StreamsBuilder builder = new StreamsBuilder();
	buildStreams(builder);
	this.pipeline = new KafkaStreams(builder.build(), props);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getPipeline() != null && getPipeline().state() == State.RUNNING) {
	    getPipeline().close();
	}
	getPipeline().start();
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getPipeline() != null) {
	    getPipeline().close();
	}
    }

    protected KafkaStreams getPipeline() {
	return pipeline;
    }
}
