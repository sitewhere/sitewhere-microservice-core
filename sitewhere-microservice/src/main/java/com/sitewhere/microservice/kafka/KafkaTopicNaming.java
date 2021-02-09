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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Class for locating SiteWhere Kafka topics.
 */
@ApplicationScoped
public class KafkaTopicNaming implements IKafkaTopicNaming {

    @Inject
    IInstanceSettings instanceSettings;

    /** Separator used to partition topic name */
    protected static final String SEPARATOR = ".";

    /** Global topic indicator */
    protected static final String GLOBAL_INDICATOR = "global";

    /** Tenant topic indicator */
    protected static final String TENANT_INDICATOR = "tenant";

    /** Topic suffix for microservice state updates */
    protected static final String MICROSERVICE_STATE_UPDATES_SUFFIX = "microservice-state-updates";

    /** Topic suffix for instance topology updates */
    protected static final String INSTANCE_TOPOLOGY_UPDATES_SUFFIX = "instance-topology-updates";

    /** Topic suffix for tenant model updates */
    protected static final String TENANT_MODEL_UPDATES_SUFFIX = "tenant-model-updates";

    /** Topic suffix for instance-wide logging */
    protected static final String INSTANCE_LOGGING_SUFFIX = "instance-logging";

    /** Topic suffix for events decoded by event sources for a tenant */
    protected static final String TENANT_TOPIC_EVENT_SOURCE_DECODED_EVENTS = "event-source-decoded-events";

    /** Topic suffix for events that could not be decoded for a tenant */
    protected static final String TENANT_TOPIC_EVENT_SOURCE_FAILED_DECODE_EVENTS = "event-source-failed-decode-events";

    /** Topic suffix for events that have completed inbound processing */
    protected static final String TENANT_TOPIC_INBOUND_EVENTS = "inbound-events";

    /** Topic suffix for events that should be reprocessed */
    protected static final String TENANT_TOPIC_INBOUND_REPROCESS_EVENTS = "inbound-reprocess-events";

    /** Topic suffix for device registration events from inbound event sources */
    protected static final String TENANT_TOPIC_INBOUND_DEVICE_REGISTRATION_EVENTS = "inbound-device-registration-events";

    /** Topic suffix for tenant events sent to unregistered devices */
    protected static final String TENANT_TOPIC_INBOUND_UNREGISTERED_DEVICE_EVENTS = "inbound-unregistered-device-events";

    /** Topic suffix for events that have been enriched and persisted */
    protected static final String TENANT_TOPIC_OUTBOUND_EVENTS = "outbound-events";

    /** Topic suffix for persisted and enriched command invocations */
    protected static final String TENANT_TOPIC_OUTBOUND_COMMAND_INVOCATIONS = "outbound-command-invocations";

    /** Topic suffix for undelivered command invocations */
    protected static final String TENANT_TOPIC_UNDELIVERED_COMMAND_INVOCATIONS = "undelivered-command-invocations";

    /** Topic suffix for unprocessed batch operations */
    protected static final String TENANT_TOPIC_UNPROCESSED_BATCH_OPERATIONS = "unprocessed-batch-operations";

    /** Topic suffix for unprocessed batch elements */
    protected static final String TENANT_TOPIC_UNPROCESSED_BATCH_ELEMENTS = "unprocessed-batch-elements";

    /** Topic suffix for failed batch elements */
    protected static final String TENANT_TOPIC_FAILED_BATCH_ELEMENTS = "failed-batch-elements";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.kafka.IKafkaTopicNaming#getInstancePrefix( )
     */
    @Override
    public String getInstancePrefix() {
	return getInstanceSettings().getProductId() + SEPARATOR + getInstanceSettings().getKubernetesNamespace();
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#getGlobalPrefix()
     */
    @Override
    public String getGlobalPrefix() {
	return getInstancePrefix() + SEPARATOR + GLOBAL_INDICATOR + SEPARATOR;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#getTenantPrefix(io.
     * sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public String getTenantPrefix(SiteWhereTenant tenant) {
	return getInstancePrefix() + SEPARATOR + TENANT_INDICATOR + SEPARATOR + tenant.getMetadata().getName()
		+ SEPARATOR;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getMicroserviceStateUpdatesTopic()
     */
    @Override
    public String getMicroserviceStateUpdatesTopic() {
	return getGlobalPrefix() + MICROSERVICE_STATE_UPDATES_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getInstanceTopologyUpdatesTopic()
     */
    @Override
    public String getInstanceTopologyUpdatesTopic() {
	return getGlobalPrefix() + INSTANCE_TOPOLOGY_UPDATES_SUFFIX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IKafkaTopicNaming#
     * getTenantUpdatesTopic()
     */
    @Override
    public String getTenantUpdatesTopic() {
	return getGlobalPrefix() + TENANT_MODEL_UPDATES_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getInstanceLoggingTopic()
     */
    @Override
    public String getInstanceLoggingTopic() {
	return getGlobalPrefix() + INSTANCE_LOGGING_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getEventSourceDecodedEventsTopic(io.sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public String getEventSourceDecodedEventsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_EVENT_SOURCE_DECODED_EVENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getEventSourceFailedDecodeTopic(io.sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public String getEventSourceFailedDecodeTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_EVENT_SOURCE_FAILED_DECODE_EVENTS;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#getInboundEventsTopic(
     * io.sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public String getInboundEventsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_INBOUND_EVENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getInboundReprocessEventsTopic(io.sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public String getInboundReprocessEventsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_INBOUND_REPROCESS_EVENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getDeviceRegistrationEventsTopic(io.sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public String getDeviceRegistrationEventsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_INBOUND_DEVICE_REGISTRATION_EVENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getUnregisteredDeviceEventsTopic(io.sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public String getUnregisteredDeviceEventsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_INBOUND_UNREGISTERED_DEVICE_EVENTS;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#getOutboundEventsTopic
     * (io.sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public String getOutboundEventsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_OUTBOUND_EVENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getOutboundCommandInvocationsTopic(io.sitewhere.k8s.crd.tenant.
     * SiteWhereTenant)
     */
    @Override
    public String getOutboundCommandInvocationsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_OUTBOUND_COMMAND_INVOCATIONS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getUndeliveredCommandInvocationsTopic(io.sitewhere.k8s.crd.tenant.
     * SiteWhereTenant)
     */
    @Override
    public String getUndeliveredCommandInvocationsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_UNDELIVERED_COMMAND_INVOCATIONS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getUnprocessedBatchOperationsTopic(io.sitewhere.k8s.crd.tenant.
     * SiteWhereTenant)
     */
    @Override
    public String getUnprocessedBatchOperationsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_UNPROCESSED_BATCH_OPERATIONS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getUnprocessedBatchElementsTopic(io.sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public String getUnprocessedBatchElementsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_UNPROCESSED_BATCH_ELEMENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getFailedBatchElementsTopic(io.sitewhere.k8s.crd.tenant.SiteWhereTenant)
     */
    @Override
    public String getFailedBatchElementsTopic(SiteWhereTenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_FAILED_BATCH_ELEMENTS;
    }

    protected IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }
}