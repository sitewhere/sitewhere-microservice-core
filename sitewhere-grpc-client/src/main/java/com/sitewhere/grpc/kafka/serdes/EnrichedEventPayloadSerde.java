/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.kafka.serdes;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes.WrapperSerde;
import org.apache.kafka.common.serialization.Serializer;

import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GEnrichedEventPayload;
import com.sitewhere.rest.model.device.event.kafka.EnrichedEventPayload;
import com.sitewhere.spi.SiteWhereException;

/**
 * Kafka {@link Serde} implementation for {@link EnrichedEventPayload}.
 */
public class EnrichedEventPayloadSerde extends WrapperSerde<EnrichedEventPayload> {

    public EnrichedEventPayloadSerde() {
	super(new EnrichedEventPayloadSerializer(), new EnrichedEventPayloadDeserializer());
    }

    /**
     * Serializer for {@link EnrichedEventPayload}.
     */
    public static class EnrichedEventPayloadSerializer implements Serializer<EnrichedEventPayload> {

	/*
	 * @see
	 * org.apache.kafka.common.serialization.Serializer#configure(java.util.Map,
	 * boolean)
	 */
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	/*
	 * @see
	 * org.apache.kafka.common.serialization.Serializer#serialize(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public byte[] serialize(String topic, EnrichedEventPayload data) {
	    try {
		GEnrichedEventPayload grpc = EventModelConverter.asGrpcEnrichedEventPayload(data);
		return EventModelMarshaler.buildEnrichedEventPayloadMessage(grpc);
	    } catch (SiteWhereException e) {
		throw new RuntimeException("Unable to deserialize payload.", e);
	    }
	}

	/*
	 * @see org.apache.kafka.common.serialization.Serializer#close()
	 */
	@Override
	public void close() {
	}
    }

    /**
     * Deserializer for {@link EnrichedEventPayload}.
     */
    public static class EnrichedEventPayloadDeserializer implements Deserializer<EnrichedEventPayload> {

	/*
	 * @see
	 * org.apache.kafka.common.serialization.Deserializer#configure(java.util.Map,
	 * boolean)
	 */
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	/*
	 * @see
	 * org.apache.kafka.common.serialization.Deserializer#deserialize(java.lang.
	 * String, byte[])
	 */
	@Override
	public EnrichedEventPayload deserialize(String topic, byte[] data) {
	    try {
		GEnrichedEventPayload grpc = EventModelMarshaler.parseEnrichedEventPayloadMessage(data);
		return EventModelConverter.asApiEnrichedEventPayload(grpc);
	    } catch (SiteWhereException e) {
		throw new RuntimeException("Unable to deserialize payload.", e);
	    }
	}

	/*
	 * @see org.apache.kafka.common.serialization.Deserializer#close()
	 */
	@Override
	public void close() {
	}
    }
}
