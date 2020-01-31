/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import org.apache.kafka.streams.processor.ProcessorSupplier;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.kafka.IProcessorSupplierComponent;

/**
 * Base class for {@link ProcessorSupplier} components that also use the
 * SiteWhere lifecycle.
 * 
 * @param <K>
 * @param <V>
 */
public abstract class ProcessorSupplierComponent<K, V> extends TenantEngineLifecycleComponent
	implements IProcessorSupplierComponent<K, V> {
}
