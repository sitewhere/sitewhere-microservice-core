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
package com.sitewhere.spi.microservice.cache;

import com.sitewhere.spi.SiteWhereException;

/**
 * Provides access to a cache hosted in Hazelcast.
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheProvider<K, V> {

    /**
     * Get cache identifier.
     * 
     * @return
     */
    String getCacheIdentifier();

    /**
     * Get cache configuration settings.
     * 
     * @return
     */
    ICacheConfiguration getCacheConfiguration();

    /**
     * Set a cache entry.
     * 
     * @param tenantId
     * @param key
     * @param value
     * @throws SiteWhereException
     */
    void setCacheEntry(String tenantId, K key, V value) throws SiteWhereException;

    /**
     * Get a cache entry. Null if not found.
     * 
     * @param tenantId
     * @param key
     * @return
     * @throws SiteWhereException
     */
    V getCacheEntry(String tenantId, K key) throws SiteWhereException;

    /**
     * Remove an existing cache entry.
     * 
     * @param tenantId
     * @param key
     * @throws SiteWhereException
     */
    void removeCacheEntry(String tenantId, K key) throws SiteWhereException;
}