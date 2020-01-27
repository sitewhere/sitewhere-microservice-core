/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.asset;

import java.util.UUID;

import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.cache.CacheConfiguration;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.microservice.cache.ICacheConfiguration;
import com.sitewhere.spi.microservice.cache.ICacheProvider;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.asset.IAssetSearchCriteria;
import com.sitewhere.spi.search.asset.IAssetTypeSearchCritiera;

/**
 * Adds caching support to asset management API channel.
 */
public class CachedAssetManagementApiChannel extends TenantEngineLifecycleComponent implements IAssetManagement {

    /** Cache settings */
    private CacheSettings cacheSettings;

    /** Wrapped API channel */
    private IAssetManagementApiChannel<?> wrapped;

    /** Asset type cache */
    private ICacheProvider<String, IAssetType> assetTypeCache;

    /** Asset type by id cache */
    private ICacheProvider<UUID, IAssetType> assetTypeByIdCache;

    /** Asset cache */
    private ICacheProvider<String, IAsset> assetCache;

    /** Asset by id cache */
    private ICacheProvider<UUID, IAsset> assetByIdCache;

    public CachedAssetManagementApiChannel(IAssetManagementApiChannel<?> wrapped, CacheSettings cacheSettings) {
	this.wrapped = wrapped;
	this.cacheSettings = cacheSettings;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeNestedComponent(getWrapped(), monitor, true);
	this.assetTypeCache = new AssetManagementCacheProviders.AssetTypeByTokenCache(getMicroservice(),
		getCacheSettings().getAssetTypeConfiguration());
	this.assetTypeByIdCache = new AssetManagementCacheProviders.AssetTypeByIdCache(getMicroservice(),
		getCacheSettings().getAssetTypeConfiguration());
	this.assetCache = new AssetManagementCacheProviders.AssetByTokenCache(getMicroservice(),
		getCacheSettings().getAssetConfiguration());
	this.assetByIdCache = new AssetManagementCacheProviders.AssetByIdCache(getMicroservice(),
		getCacheSettings().getAssetConfiguration());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getWrapped(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	stopNestedComponent(getWrapped(), monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.asset.AssetManagementApiChannel#getAsset(java.util.
     * UUID)
     */
    @Override
    public IAsset getAsset(UUID assetId) throws SiteWhereException {
	String tenantId = UserContext.getCurrentTenantId();
	IAsset asset = getAssetByIdCache().getCacheEntry(tenantId, assetId);
	if (asset == null) {
	    asset = getWrapped().getAsset(assetId);
	    getAssetByIdCache().setCacheEntry(tenantId, assetId, asset);
	}
	return asset;
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#getAssetByToken(java.lang.String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	String tenantId = UserContext.getCurrentTenantId();
	IAsset asset = getAssetCache().getCacheEntry(tenantId, token);
	if (asset == null) {
	    asset = getWrapped().getAssetByToken(token);
	    getAssetCache().setCacheEntry(tenantId, token, asset);
	}
	return asset;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.asset.AssetManagementApiChannel#getAssetType(java.
     * util.UUID)
     */
    @Override
    public IAssetType getAssetType(UUID assetTypeId) throws SiteWhereException {
	String tenantId = UserContext.getCurrentTenantId();
	IAssetType assetType = getAssetTypeByIdCache().getCacheEntry(tenantId, assetTypeId);
	if (assetType == null) {
	    assetType = getWrapped().getAssetType(assetTypeId);
	    getAssetTypeByIdCache().setCacheEntry(tenantId, assetTypeId, assetType);
	}
	return assetType;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.asset.AssetManagementApiChannel#getAssetTypeByToken
     * (java.lang.String)
     */
    @Override
    public IAssetType getAssetTypeByToken(String token) throws SiteWhereException {
	String tenantId = UserContext.getCurrentTenantId();
	IAssetType assetType = getAssetTypeCache().getCacheEntry(tenantId, token);
	if (assetType == null) {
	    assetType = getWrapped().getAssetTypeByToken(token);
	    getAssetTypeCache().setCacheEntry(tenantId, token, assetType);
	}
	return assetType;
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createAsset(com.sitewhere.spi.asset.
     * request.IAssetCreateRequest)
     */
    @Override
    public IAsset createAsset(IAssetCreateRequest request) throws SiteWhereException {
	return getWrapped().createAsset(request);
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#updateAsset(java.util.UUID,
     * com.sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset updateAsset(UUID assetId, IAssetCreateRequest request) throws SiteWhereException {
	return getWrapped().updateAsset(assetId, request);
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#deleteAsset(java.util.UUID)
     */
    @Override
    public IAsset deleteAsset(UUID assetId) throws SiteWhereException {
	return getWrapped().deleteAsset(assetId);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssets(com.sitewhere.spi.search.
     * asset.IAssetSearchCriteria)
     */
    @Override
    public ISearchResults<? extends IAsset> listAssets(IAssetSearchCriteria criteria) throws SiteWhereException {
	return getWrapped().listAssets(criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createAssetType(com.sitewhere.spi.
     * asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType createAssetType(IAssetTypeCreateRequest request) throws SiteWhereException {
	return getWrapped().createAssetType(request);
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#updateAssetType(java.util.UUID,
     * com.sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType updateAssetType(UUID assetTypeId, IAssetTypeCreateRequest request) throws SiteWhereException {
	return getWrapped().updateAssetType(assetTypeId, request);
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#deleteAssetType(java.util.UUID)
     */
    @Override
    public IAssetType deleteAssetType(UUID assetTypeId) throws SiteWhereException {
	return getWrapped().deleteAssetType(assetTypeId);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssetTypes(com.sitewhere.spi.
     * search.asset.IAssetTypeSearchCritiera)
     */
    @Override
    public ISearchResults<? extends IAssetType> listAssetTypes(IAssetTypeSearchCritiera criteria)
	    throws SiteWhereException {
	return getWrapped().listAssetTypes(criteria);
    }

    /**
     * Contains default cache settings for asset management entities.
     */
    public static class CacheSettings {

	/** Cache configuraton for asset types */
	private ICacheConfiguration assetTypeConfiguration = new CacheConfiguration(1000, 60);

	/** Cache configuraton for assets */
	private ICacheConfiguration assetConfiguration = new CacheConfiguration(10000, 60);

	public ICacheConfiguration getAssetTypeConfiguration() {
	    return assetTypeConfiguration;
	}

	public void setAssetTypeConfiguration(ICacheConfiguration assetTypeConfiguration) {
	    this.assetTypeConfiguration = assetTypeConfiguration;
	}

	public ICacheConfiguration getAssetConfiguration() {
	    return assetConfiguration;
	}

	public void setAssetConfiguration(ICacheConfiguration assetConfiguration) {
	    this.assetConfiguration = assetConfiguration;
	}
    }

    protected ICacheProvider<String, IAssetType> getAssetTypeCache() {
	return assetTypeCache;
    }

    protected ICacheProvider<UUID, IAssetType> getAssetTypeByIdCache() {
	return assetTypeByIdCache;
    }

    protected ICacheProvider<String, IAsset> getAssetCache() {
	return assetCache;
    }

    protected ICacheProvider<UUID, IAsset> getAssetByIdCache() {
	return assetByIdCache;
    }

    protected IAssetManagementApiChannel<?> getWrapped() {
	return wrapped;
    }

    protected CacheSettings getCacheSettings() {
	return cacheSettings;
    }
}