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
public abstract class RedisCacheProvider<K, V> implements ICacheProvider<K, V> {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(RedisCacheProvider.class);

    /** Owning microservice */
    private IMicroservice<?, ?> microservice;

    /** Cache identifier */
    private String cacheIdentifier;

    /** Cache configuration */
    private ICacheConfiguration cacheConfiguration;

    public RedisCacheProvider(IMicroservice<?, ?> microservice, String cacheIdentifier,
	    ICacheConfiguration cacheConfiguration) {
	this.microservice = microservice;
	this.cacheIdentifier = cacheIdentifier;
	this.cacheConfiguration = cacheConfiguration;
    }

    /**
     * Convert key to string.
     * 
     * @param key
     * @return
     * @throws SiteWhereException
     */
    public abstract String convertKey(K key) throws SiteWhereException;

    /**
     * Serialize a value.
     * 
     * @param value
     * @return
     * @throws SiteWhereException
     */
    public abstract byte[] serialize(V value) throws SiteWhereException;

    /**
     * Deserialize a value.
     * 
     * @param value
     * @return
     * @throws SiteWhereException
     */
    public abstract V deserialize(byte[] value) throws SiteWhereException;

    /*
     * @see
     * com.sitewhere.spi.microservice.cache.ICacheProvider#setCacheEntry(java.lang.
     * String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void setCacheEntry(String tenantId, K key, V value) throws SiteWhereException {
	String cacheKey = getCacheKey(tenantId, convertKey(key));
	LOGGER.info("Caching value for '" + cacheKey + "'.");
	if ((value != null) && (getCacheConfiguration().isEnabled())) {
	    getMicroservice().getRedisCacheConnection().sync().set(cacheKey, serialize(value));
	    getMicroservice().getRedisCacheConnection().sync().expire(cacheKey,
		    getCacheConfiguration().getTtlInSeconds());
	} else {
	    getMicroservice().getRedisCacheConnection().sync().del(cacheKey);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.cache.ICacheProvider#getCacheEntry(java.lang.
     * String, java.lang.Object)
     */
    @Override
    public V getCacheEntry(String tenantId, K key) throws SiteWhereException {
	String cacheKey = getCacheKey(tenantId, convertKey(key));
	byte[] result = getMicroservice().getRedisCacheConnection().sync().get(cacheKey);
	if (result != null) {
	    V converted = deserialize(result);
	    LOGGER.info("Found cached value for '" + cacheKey + "'.");
	    return converted;
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.cache.ICacheProvider#removeCacheEntry(java.
     * lang.String, java.lang.Object)
     */
    @Override
    public void removeCacheEntry(String tenantId, K key) throws SiteWhereException {
	String cacheKey = getCacheKey(tenantId, convertKey(key));
	getMicroservice().getRedisCacheConnection().sync().del(cacheKey);
    }

    /**
     * Get key name based on instance:tenant:cacheid:key.
     * 
     * @param tenantId
     * @param key
     * @return
     */
    protected String getCacheKey(String tenantId, String key) {
	return String.format("%s:%s:%s:%s", getMicroservice().getInstanceSettings().getKubernetesNamespace(),
		tenantId == null ? "_global_" : tenantId, getCacheIdentifier(), key);
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
}