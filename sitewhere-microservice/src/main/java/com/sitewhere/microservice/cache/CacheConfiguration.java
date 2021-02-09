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
package com.sitewhere.microservice.cache;

import com.sitewhere.spi.microservice.cache.ICacheConfiguration;

/**
 * Provides settings which control how a cache is to be configured.
 */
public class CacheConfiguration implements ICacheConfiguration {

    /** Maximum number of cache entries */
    private int maximumSize;

    /** Max life of cache entries in seconds */
    private int ttlInSeconds;

    /** Indicates if cache is enabled */
    private boolean enabled;

    public CacheConfiguration(int maximumSize, int ttlInSeconds) {
	this.maximumSize = maximumSize;
	this.ttlInSeconds = ttlInSeconds;
	this.enabled = true;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#getMaximumSize()
     */
    @Override
    public int getMaximumSize() {
	return maximumSize;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#setMaximumSize(int)
     */
    @Override
    public void setMaximumSize(int maximumSize) {
	this.maximumSize = maximumSize;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#getTtlInSeconds()
     */
    @Override
    public int getTtlInSeconds() {
	return ttlInSeconds;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#setTtlInSeconds(int)
     */
    @Override
    public void setTtlInSeconds(int ttlInSeconds) {
	this.ttlInSeconds = ttlInSeconds;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#isEnabled()
     */
    @Override
    public boolean isEnabled() {
	return enabled;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }
}