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
 * Configuration for gRPC connectivity.
 */
@RegisterForReflection
public class GrpcConfiguration {

    /** Max retry count */
    private double maxRetryCount;

    /** Initial backoff in seconds */
    private int initialBackoffSeconds;

    /** Max backoff in seconds */
    private int maxBackoffSeconds;

    /** Backoff multiplier */
    private double backoffMultiplier;

    /** Resolve FQDN */
    private boolean resolveFQDN;

    public double getMaxRetryCount() {
	return maxRetryCount;
    }

    public void setMaxRetryCount(double maxRetryCount) {
	this.maxRetryCount = maxRetryCount;
    }

    public int getInitialBackoffSeconds() {
	return initialBackoffSeconds;
    }

    public void setInitialBackoffSeconds(int initialBackoffSeconds) {
	this.initialBackoffSeconds = initialBackoffSeconds;
    }

    public int getMaxBackoffSeconds() {
	return maxBackoffSeconds;
    }

    public void setMaxBackoffSeconds(int maxBackoffSeconds) {
	this.maxBackoffSeconds = maxBackoffSeconds;
    }

    public double getBackoffMultiplier() {
	return backoffMultiplier;
    }

    public void setBackoffMultiplier(double backoffMultiplier) {
	this.backoffMultiplier = backoffMultiplier;
    }

    public boolean isResolveFQDN() {
	return resolveFQDN;
    }

    public void setResolveFQDN(boolean resolveFQDN) {
	this.resolveFQDN = resolveFQDN;
    }
}
