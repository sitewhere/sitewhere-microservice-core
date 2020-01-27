/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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