/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.kafka.serdes;

import org.apache.kafka.common.serialization.Serde;

import com.sitewhere.rest.model.device.event.kafka.EnrichedEventPayload;

/**
 * Kafka {@link Serde} implementations for standard payloads.
 */
public class SiteWhereSerdes {

    public static Serde<EnrichedEventPayload> forEnrichedEventPayload() {
	return new EnrichedEventPayloadSerde();
    }
}
