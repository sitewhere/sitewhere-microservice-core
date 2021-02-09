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

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.asset.IAssetSearchCriteria;
import com.sitewhere.spi.search.asset.IAssetTypeSearchCritiera;

/**
 * Asset management interface implemented by datastores that can store assets.
 */
public interface IAssetManagement extends ITenantEngineLifecycleComponent {

    /**
     * Create a new asset.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IAsset createAsset(IAssetCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing asset.
     * 
     * @param assetId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IAsset updateAsset(UUID assetId, IAssetCreateRequest request) throws SiteWhereException;

    /**
     * Get asset by unique id.
     * 
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    IAsset getAsset(UUID assetId) throws SiteWhereException;

    /**
     * Get asset by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    IAsset getAssetByToken(String token) throws SiteWhereException;

    /**
     * Delete an existing asset based on unique id.
     * 
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    IAsset deleteAsset(UUID assetId) throws SiteWhereException;

    /**
     * List assets that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<? extends IAsset> listAssets(IAssetSearchCriteria criteria) throws SiteWhereException;

    /**
     * Create a new asset type.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IAssetType createAssetType(IAssetTypeCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing asset type.
     * 
     * @param assetTypeId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IAssetType updateAssetType(UUID assetTypeId, IAssetTypeCreateRequest request) throws SiteWhereException;

    /**
     * Get asset type by unique id.
     * 
     * @param assetTypeId
     * @return
     * @throws SiteWhereException
     */
    IAssetType getAssetType(UUID assetTypeId) throws SiteWhereException;

    /**
     * Get asset type by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    IAssetType getAssetTypeByToken(String token) throws SiteWhereException;

    /**
     * Delete an existing asset type based on unique id.
     * 
     * @param assetTypeId
     * @return
     * @throws SiteWhereException
     */
    IAssetType deleteAssetType(UUID assetTypeId) throws SiteWhereException;

    /**
     * List asset types that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<? extends IAssetType> listAssetTypes(IAssetTypeSearchCritiera criteria) throws SiteWhereException;
}