/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration.model.instance.infrastructure;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Configuration for Prometheus metrics.
 */
@RegisterForReflection
public class MetricsConfiguration {

    /** Enabled indicator */
    private boolean enabled;

    /** Metrics HTTP port */
    private int httpPort;

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    public int getHttpPort() {
	return httpPort;
    }

    public void setHttpPort(int httpPort) {
	this.httpPort = httpPort;
    }
}
