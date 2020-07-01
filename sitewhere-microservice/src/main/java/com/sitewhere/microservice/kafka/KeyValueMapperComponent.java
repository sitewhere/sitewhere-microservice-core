/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import org.apache.kafka.streams.kstream.KeyValueMapper;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;

/**
 * Base class for {@link KeyValueMapper} components that also participate in the
 * SiteWhere lifecycle.
 * 
 * @param <K>
 * @param <V>
 * @param <VR>
 */
public abstract class KeyValueMapperComponent<K, V, VR> extends TenantEngineLifecycleComponent
	implements KeyValueMapper<K, V, VR> {
}
