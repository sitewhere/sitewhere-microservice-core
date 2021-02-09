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
package com.sitewhere.microservice.api.asset;

import java.util.UUID;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.asset.IAssetSearchCriteria;
import com.sitewhere.spi.search.asset.IAssetTypeSearchCritiera;

/**
 * Wraps an asset management implementation. Subclasses can implement only the
 * methods they need to override.
 */
public class AssetManagementDecorator extends TenantEngineLifecycleComponentDecorator<IAssetManagement>
	implements IAssetManagement {

    public AssetManagementDecorator(IAssetManagement delegate) {
	super(delegate);
    }

    /*
     * @see com.sitewhere.microservice.api.asset.IAssetManagement#createAsset(com.
     * sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset createAsset(IAssetCreateRequest request) throws SiteWhereException {
	return getDelegate().createAsset(request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#updateAsset(java.util.
     * UUID, com.sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset updateAsset(UUID assetId, IAssetCreateRequest request) throws SiteWhereException {
	return getDelegate().updateAsset(assetId, request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#getAsset(java.util.
     * UUID)
     */
    @Override
    public IAsset getAsset(UUID assetId) throws SiteWhereException {
	return getDelegate().getAsset(assetId);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#getAssetByToken(java.
     * lang.String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	return getDelegate().getAssetByToken(token);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#deleteAsset(java.util.
     * UUID)
     */
    @Override
    public IAsset deleteAsset(UUID assetId) throws SiteWhereException {
	return getDelegate().deleteAsset(assetId);
    }

    /*
     * @see com.sitewhere.microservice.api.asset.IAssetManagement#listAssets(com.
     * sitewhere.spi.search.asset.IAssetSearchCriteria)
     */
    @Override
    public ISearchResults<? extends IAsset> listAssets(IAssetSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listAssets(criteria);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#createAssetType(com.
     * sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType createAssetType(IAssetTypeCreateRequest request) throws SiteWhereException {
	return getDelegate().createAssetType(request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#updateAssetType(java.
     * util.UUID, com.sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType updateAssetType(UUID assetTypeId, IAssetTypeCreateRequest request) throws SiteWhereException {
	return getDelegate().updateAssetType(assetTypeId, request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#getAssetType(java.util.
     * UUID)
     */
    @Override
    public IAssetType getAssetType(UUID assetTypeId) throws SiteWhereException {
	return getDelegate().getAssetType(assetTypeId);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#getAssetTypeByToken(
     * java.lang.String)
     */
    @Override
    public IAssetType getAssetTypeByToken(String token) throws SiteWhereException {
	return getDelegate().getAssetTypeByToken(token);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#deleteAssetType(java.
     * util.UUID)
     */
    @Override
    public IAssetType deleteAssetType(UUID assetTypeId) throws SiteWhereException {
	return getDelegate().deleteAssetType(assetTypeId);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#listAssetTypes(com.
     * sitewhere.spi.search.asset.IAssetTypeSearchCritiera)
     */
    @Override
    public ISearchResults<? extends IAssetType> listAssetTypes(IAssetTypeSearchCritiera criteria)
	    throws SiteWhereException {
	return getDelegate().listAssetTypes(criteria);
    }
}