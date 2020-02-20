/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.cache;

import java.util.HashMap;
import java.util.Map;

import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.cache.ICacheConfiguration;
import com.sitewhere.spi.microservice.cache.ICacheProvider;

/**
 * Base class for cache providers using Redission local cache for backing
 * storage.
 *
 * @param <K>
 * @param <V>
 */
public abstract class RedissonCacheProvider<K, V> implements ICacheProvider<K, V> {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(RedissonCacheProvider.class);

    /** Owning microservice */
    private IMicroservice<?, ?> microservice;

    /** Cache identifier */
    private String cacheIdentifier;

    /** Key type */
    private Class<K> keyType;

    /** Value type */
    private Class<V> valueType;

    /** Cache configuration */
    private ICacheConfiguration cacheConfiguration;

    /** Map of tenant-specific caches */
    private Map<String, RLocalCachedMap<K, V>> tenantCaches = new HashMap<>();

    public RedissonCacheProvider(IMicroservice<?, ?> microservice, String cacheIdentifier, Class<K> keyType,
	    Class<V> valueType, ICacheConfiguration cacheConfiguration) {
	this.microservice = microservice;
	this.cacheIdentifier = cacheIdentifier;
	this.keyType = keyType;
	this.valueType = valueType;
	this.cacheConfiguration = cacheConfiguration;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.cache.ICacheProvider#setCacheEntry(java.lang.
     * String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void setCacheEntry(String tenantId, K key, V value) throws SiteWhereException {
	LOGGER.info("Caching value for '" + key.toString() + "'.");
	if ((value != null) && (getCacheConfiguration().isEnabled())) {
	    getCache(tenantId).put(key, value);
	} else {
	    getCache(tenantId).remove(key);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.cache.ICacheProvider#getCacheEntry(java.lang.
     * String, java.lang.Object)
     */
    @Override
    public V getCacheEntry(String tenantId, K key) throws SiteWhereException {
	V result = getCache(tenantId).get(key);
	if (result != null) {
	    LOGGER.info("Found cached value for '" + key.toString() + "'.");
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.cache.ICacheProvider#removeCacheEntry(java.
     * lang.String, java.lang.Object)
     */
    @Override
    public void removeCacheEntry(String tenantId, K key) throws SiteWhereException {
	getCache(tenantId).remove(key);
    }

    /**
     * Get cache name based on tenant id and cache identifier.
     * 
     * @param tenantId
     * @return
     */
    protected String getCacheName(String tenantId) {
	return String.format("%s:%s", tenantId == null ? "_global_" : tenantId, getCacheIdentifier());
    }

    /**
     * Get cache (create if not found).
     * 
     * @return
     * @throws SiteWhereException
     */
    @SuppressWarnings("unchecked")
    protected RLocalCachedMap<K, V> getCache(String tenantId) throws SiteWhereException {
	String cacheName = getCacheName(tenantId);
	synchronized (getTenantCaches()) {
	    RLocalCachedMap<K, V> cache = getTenantCaches().get(tenantId);
	    if (cache == null) {
		cache = getMicroservice().getRedissonClient().getLocalCachedMap(cacheName, buildCacheConfiguration());
		getTenantCaches().put(tenantId, cache);
	    }
	    return cache;
	}
    }

    /**
     * Get cache configuration.
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected LocalCachedMapOptions buildCacheConfiguration() {
	return LocalCachedMapOptions.defaults().cacheSize(getCacheConfiguration().getMaximumSize())
		.timeToLive(getCacheConfiguration().getTtlInSeconds() * 1000);
    }

    /*
     * @see com.sitewhere.spi.microservice.cache.ICacheProvider#getCacheIdentifier()
     */
    @Override
    public String getCacheIdentifier() {
	return cacheIdentifier;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.ICacheProvider#getCacheConfiguration()
     */
    @Override
    public ICacheConfiguration getCacheConfiguration() {
	return cacheConfiguration;
    }

    protected IMicroservice<?, ?> getMicroservice() {
	return microservice;
    }

    protected Class<K> getKeyType() {
	return keyType;
    }

    protected Class<V> getValueType() {
	return valueType;
    }

    protected Map<String, RLocalCachedMap<K, V>> getTenantCaches() {
	return tenantCaches;
    }
}