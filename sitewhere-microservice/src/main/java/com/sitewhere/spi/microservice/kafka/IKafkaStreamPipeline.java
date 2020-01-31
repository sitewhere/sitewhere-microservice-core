/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka;

import java.util.List;

import org.apache.kafka.streams.StreamsBuilder;

import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Builds a pipeline for interacting with data consumed from and produced into
 * Kafka topics.
 */
public interface IKafkaStreamPipeline extends ITenantEngineLifecycleComponent {

    /**
     * Gets a list of source topic names.
     * 
     * @return
     */
    List<String> getSourceTopicNames();

    /**
     * Build streams used realize the pipeline.
     * 
     * @param builder
     */
    void buildStreams(StreamsBuilder builder);
}
