/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Fixes for Kubernetes client when running in Graal native image.
 */
@RegisterForReflection(targets = { KubernetesDeserializer.class, IntOrString.Deserializer.class,
	Quantity.Deserializer.class })
public class KubernetesClientGraal {
}
