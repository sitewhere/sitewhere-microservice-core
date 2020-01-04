/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

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

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	Properties props = new Properties();
	String appId = String.format("%s-%s", getMicroservice().getInstanceSettings().getKubernetesName(),
		getMicroservice().getIdentifier().getShortName());
	props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
	props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
		getMicroservice().getInstanceSettings().getKafkaBootstrapServers());
	props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
	props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

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
	if (getPipeline() != null) {
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
