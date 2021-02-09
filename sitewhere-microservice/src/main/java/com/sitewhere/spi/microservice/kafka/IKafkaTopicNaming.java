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
package com.sitewhere.spi.microservice.kafka;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Provides names for Kafka topics used in SiteWhere.
 */
public interface IKafkaTopicNaming {

    /**
     * Get prefix that uniquely identifies SiteWhere instance.
     * 
     * @return
     */
    String getInstancePrefix();

    /**
     * Get prefix used for global topics.
     * 
     * @return
     */
    String getGlobalPrefix();

    /**
     * Get prefix used for tenant-specific topics.
     * 
     * @param tenant
     * @return
     */
    String getTenantPrefix(SiteWhereTenant tenant);

    /**
     * Get topic name for tracking tenant model updates.
     * 
     * @return
     */
    String getTenantUpdatesTopic();

    /**
     * Get topic name for tracking microservice state updates.
     * 
     * @return
     */
    String getMicroserviceStateUpdatesTopic();

    /**
     * Get topic name for tracking instance topology updates.
     * 
     * @return
     */
    String getInstanceTopologyUpdatesTopic();

    /**
     * Get topic for log aggregation across all microservices.
     * 
     * @return
     */
    String getInstanceLoggingTopic();

    /**
     * Get name for topic that contains events that have been decoded from inbound
     * event sources.
     * 
     * @param tenant
     * @return
     */
    String getEventSourceDecodedEventsTopic(SiteWhereTenant tenant);

    /**
     * Get name for topic that contains events that could not be decoded from event
     * sources.
     * 
     * @param tenant
     * @return
     */
    String getEventSourceFailedDecodeTopic(SiteWhereTenant tenant);

    /**
     * Get topic for inbound events that have been validated by inbound processing
     * logic.
     * 
     * @param tenant
     * @return
     */
    String getInboundEventsTopic(SiteWhereTenant tenant);

    /**
     * Get topic for inbound events that were sent for out-of-band processing, then
     * returned to be reprocessed.
     * 
     * @param tenant
     * @return
     */
    String getInboundReprocessEventsTopic(SiteWhereTenant tenant);

    /**
     * Get name for topic that contains events for device registration requests
     * decoded by event sources.
     * 
     * @param tenant
     * @return
     */
    String getDeviceRegistrationEventsTopic(SiteWhereTenant tenant);

    /**
     * Get name for topic that contains events for devices that were not registered
     * in the system.
     * 
     * @param tenant
     * @return
     */
    String getUnregisteredDeviceEventsTopic(SiteWhereTenant tenant);

    /**
     * Get topic for events that have been persisted and enriched with
     * device/assignment data.
     * 
     * @param tenant
     * @return
     */
    String getOutboundEventsTopic(SiteWhereTenant tenant);

    /**
     * Get topic for device command invocations that have been persisted and
     * enriched with device/assignment data.
     * 
     * @param tenant
     * @return
     */
    String getOutboundCommandInvocationsTopic(SiteWhereTenant tenant);

    /**
     * Get topic for device command invocations that could not be delievered.
     * 
     * @param tenant
     * @return
     */
    String getUndeliveredCommandInvocationsTopic(SiteWhereTenant tenant);

    /**
     * Get topic for unprocessed batch operations.
     * 
     * @param tenant
     * @return
     */
    String getUnprocessedBatchOperationsTopic(SiteWhereTenant tenant);

    /**
     * Get topic for unprocessed batch elements.
     * 
     * @param tenant
     * @return
     */
    String getUnprocessedBatchElementsTopic(SiteWhereTenant tenant);

    /**
     * Get topic for failed batch elements.
     * 
     * @param tenant
     * @return
     */
    String getFailedBatchElementsTopic(SiteWhereTenant tenant);
}