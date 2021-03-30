/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.microservice.configuration.model.instance.infrastructure;

/**
 * Configuration for gRPC connectivity.
 */
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
