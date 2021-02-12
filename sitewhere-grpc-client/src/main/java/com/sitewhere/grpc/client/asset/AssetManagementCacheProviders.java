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

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitewhere.grpc.asset.AssetModelConverter;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.model.AssetModel.GAsset;
import com.sitewhere.grpc.model.AssetModel.GAssetType;
import com.sitewhere.microservice.cache.RedisCacheProvider;
import com.sitewhere.spi.SiteWhereException;
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
    public static class AssetByTokenCache extends RedisCacheProvider<String, IAsset> {

	public AssetByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, ASSET_BY_TOKEN, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(String key) throws SiteWhereException {
	    return key;
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IAsset value) throws SiteWhereException {
	    GAsset message = AssetModelConverter.asGrpcAsset(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IAsset deserialize(byte[] value) throws SiteWhereException {
	    try {
		return AssetModelConverter.asApiAsset(GAsset.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }

    /**
     * Cache for assets by id.
     */
    public static class AssetByIdCache extends RedisCacheProvider<UUID, IAsset> {

	public AssetByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, ASSET_BY_ID, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(UUID key) throws SiteWhereException {
	    return key.toString();
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IAsset value) throws SiteWhereException {
	    GAsset message = AssetModelConverter.asGrpcAsset(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IAsset deserialize(byte[] value) throws SiteWhereException {
	    try {
		return AssetModelConverter.asApiAsset(GAsset.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }

    /**
     * Cache for asset types by token.
     */
    public static class AssetTypeByTokenCache extends RedisCacheProvider<String, IAssetType> {

	public AssetTypeByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, ASSET_TYPE_BY_TOKEN, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(String key) throws SiteWhereException {
	    return key;
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IAssetType value) throws SiteWhereException {
	    GAssetType message = AssetModelConverter.asGrpcAssetType(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IAssetType deserialize(byte[] value) throws SiteWhereException {
	    try {
		return AssetModelConverter.asApiAssetType(GAssetType.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }

    /**
     * Cache for asset types by id.
     */
    public static class AssetTypeByIdCache extends RedisCacheProvider<UUID, IAssetType> {

	public AssetTypeByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, ASSET_TYPE_BY_ID, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(UUID key) throws SiteWhereException {
	    return key.toString();
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IAssetType value) throws SiteWhereException {
	    GAssetType message = AssetModelConverter.asGrpcAssetType(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IAssetType deserialize(byte[] value) throws SiteWhereException {
	    try {
		return AssetModelConverter.asApiAssetType(GAssetType.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }
}
