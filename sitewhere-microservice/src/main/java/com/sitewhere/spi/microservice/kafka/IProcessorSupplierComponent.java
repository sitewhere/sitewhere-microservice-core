/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka;

import org.apache.kafka.streams.processor.ProcessorSupplier;

import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Adds SiteWhere lifecycle to the Kafka Streams {@link ProcessorSupplier}.
 * 
 * @param <K>
 * @param <V>
 */
public interface IProcessorSupplierComponent<K, V> extends ProcessorSupplier<K, V>, ITenantEngineLifecycleComponent {
}
