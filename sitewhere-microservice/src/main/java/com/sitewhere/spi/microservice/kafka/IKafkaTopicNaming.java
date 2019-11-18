/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka;

import com.sitewhere.spi.tenant.ITenant;

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
    String getTenantPrefix(ITenant tenant);

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
    String getEventSourceDecodedEventsTopic(ITenant tenant);

    /**
     * Get name for topic that contains events that could not be decoded from event
     * sources.
     * 
     * @param tenant
     * @return
     */
    String getEventSourceFailedDecodeTopic(ITenant tenant);

    /**
     * Get topic for inbound events that have been validated by inbound processing
     * logic.
     * 
     * @param tenant
     * @return
     */
    String getInboundEventsTopic(ITenant tenant);

    /**
     * Get topic for inbound events that were sent for out-of-band processing, then
     * returned to be reprocessed.
     * 
     * @param tenant
     * @return
     */
    String getInboundReprocessEventsTopic(ITenant tenant);

    /**
     * Get name for topic that contains events for device registration requests
     * decoded by event sources.
     * 
     * @param tenant
     * @return
     */
    String getDeviceRegistrationEventsTopic(ITenant tenant);

    /**
     * Get name for topic that contains events for devices that were not registered
     * in the system.
     * 
     * @param tenant
     * @return
     */
    String getUnregisteredDeviceEventsTopic(ITenant tenant);

    /**
     * Get topic for events that have been persisted and enriched with
     * device/assignment data.
     * 
     * @param tenant
     * @return
     */
    String getOutboundEventsTopic(ITenant tenant);

    /**
     * Get topic for device command invocations that have been persisted and
     * enriched with device/assignment data.
     * 
     * @param tenant
     * @return
     */
    String getOutboundCommandInvocationsTopic(ITenant tenant);

    /**
     * Get topic for device command invocations that could not be delievered.
     * 
     * @param tenant
     * @return
     */
    String getUndeliveredCommandInvocationsTopic(ITenant tenant);

    /**
     * Get topic for unprocessed batch operations.
     * 
     * @param tenant
     * @return
     */
    String getUnprocessedBatchOperationsTopic(ITenant tenant);

    /**
     * Get topic for unprocessed batch elements.
     * 
     * @param tenant
     * @return
     */
    String getUnprocessedBatchElementsTopic(ITenant tenant);

    /**
     * Get topic for failed batch elements.
     * 
     * @param tenant
     * @return
     */
    String getFailedBatchElementsTopic(ITenant tenant);
}