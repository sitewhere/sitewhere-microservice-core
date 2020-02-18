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
 * Configuration for infrastructure connectivity.
 */
@RegisterForReflection
public class InfrastructureConfiguration {

    /** System namespace */
    private String namespace;

    /** Redis configuration */
    private RedisConfiguration redis;

    /** Kafka configuration */
    private KafkaConfiguration kafka;

    public String getNamespace() {
	return namespace;
    }

    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }

    public RedisConfiguration getRedis() {
	return redis;
    }

    public void setRedis(RedisConfiguration redis) {
	this.redis = redis;
    }

    public KafkaConfiguration getKafka() {
	return kafka;
    }

    public void setKafka(KafkaConfiguration kafka) {
	this.kafka = kafka;
    }
}
