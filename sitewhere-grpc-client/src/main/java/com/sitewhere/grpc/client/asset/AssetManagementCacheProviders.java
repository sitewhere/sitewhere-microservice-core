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
