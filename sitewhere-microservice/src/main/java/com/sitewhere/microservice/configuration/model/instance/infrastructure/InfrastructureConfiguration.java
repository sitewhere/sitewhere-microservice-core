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
 * Configuration for infrastructure connectivity.
 */
public class InfrastructureConfiguration {

    /** System namespace */
    private String namespace;

    /** Redis configuration */
    private RedisConfiguration redis;

    /** Kafka configuration */
    private KafkaConfiguration kafka;

    /** Metrics configuration */
    private MetricsConfiguration metrics;

    /** gRPC configuration */
    private GrpcConfiguration grpc;

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

    public MetricsConfiguration getMetrics() {
	return metrics;
    }

    public void setMetrics(MetricsConfiguration metrics) {
	this.metrics = metrics;
    }

    public GrpcConfiguration getGrpc() {
	return grpc;
    }

    public void setGrpc(GrpcConfiguration grpc) {
	this.grpc = grpc;
    }
}
