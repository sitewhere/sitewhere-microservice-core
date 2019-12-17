/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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