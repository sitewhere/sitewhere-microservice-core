/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.asset;

import java.util.UUID;

import com.sitewhere.microservice.cache.RedissonCacheProvider;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.cache.ICacheConfiguration;

/**
 * Cache providers for asset management entities.
 */
public class AssetManagementCacheProviders {

    public static final String ASSET_BY_TOKEN = "asset_by_token";
    public static final String ASSET_BY_ID = "asset_by_id";
    public static final String ASSET_TYPE_BY_TOKEN = "asset_type_by_token";
    public static final String ASSET_TYPE_BY_ID = "asset_type_by_id";

    /**
     * Cache for assets by token.
     */
    public static class AssetByTokenCache extends RedissonCacheProvider<String, IAsset> {

	public AssetByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, ASSET_BY_TOKEN, String.class, IAsset.class, configuration);
	}
    }

    /**
     * Cache for assets by id.
     */
    public static class AssetByIdCache extends RedissonCacheProvider<UUID, IAsset> {

	public AssetByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, ASSET_BY_ID, UUID.class, IAsset.class, configuration);
	}
    }

    /**
     * Cache for asset types by token.
     */
    public static class AssetTypeByTokenCache extends RedissonCacheProvider<String, IAssetType> {

	public AssetTypeByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, ASSET_TYPE_BY_TOKEN, String.class, IAssetType.class, configuration);
	}
    }

    /**
     * Cache for asset types by id.
     */
    public static class AssetTypeByIdCache extends RedissonCacheProvider<UUID, IAssetType> {

	public AssetTypeByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, ASSET_TYPE_BY_ID, UUID.class, IAssetType.class, configuration);
	}
    }
}
