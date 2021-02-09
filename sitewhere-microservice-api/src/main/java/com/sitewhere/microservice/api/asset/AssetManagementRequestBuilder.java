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

import com.sitewhere.rest.model.asset.request.AssetCreateRequest;
import com.sitewhere.rest.model.asset.request.AssetTypeCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Builder that supports creating asset management entities.
 */
public class AssetManagementRequestBuilder {

    /** Asset management implementation */
    private IAssetManagement assetManagement;

    public AssetManagementRequestBuilder(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    public AssetTypeCreateRequest.Builder newAssetType(String token, String name) {
	return new AssetTypeCreateRequest.Builder(token, name);
    }

    public IAssetType persist(AssetTypeCreateRequest.Builder builder) throws SiteWhereException {
	return getAssetManagement().createAssetType(builder.build());
    }

    public AssetCreateRequest.Builder newAsset(String token, String assetTypeToken, String name) {
	return new AssetCreateRequest.Builder(token, assetTypeToken, name);
    }

    public IAsset persist(AssetCreateRequest.Builder builder) throws SiteWhereException {
	return getAssetManagement().createAsset(builder.build());
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }
}