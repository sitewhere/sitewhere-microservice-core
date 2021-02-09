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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.marshaling.MarshaledAsset;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Configurable helper class that allows {@link Asset} model objects to be
 * created from {@link IAsset} SPI objects.
 */
public class AssetMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(AssetMarshalHelper.class);

    /** Asset management */
    private IAssetManagement assetManagement;

    /** Indicates whether asset type information should be included */
    private boolean includeAssetType;

    public AssetMarshalHelper(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledAsset convert(IAsset source) throws SiteWhereException {
	if (source == null) {
	    return null;
	}
	MarshaledAsset asset = new MarshaledAsset();
	asset.setId(source.getId());
	asset.setToken(source.getToken());
	asset.setAssetTypeId(source.getAssetTypeId());
	asset.setName(source.getName());
	asset.setImageUrl(source.getImageUrl());
	PersistentEntity.copy(source, asset);

	if (isIncludeAssetType()) {
	    IAssetType assetType = getAssetManagement().getAssetType(source.getAssetTypeId());
	    if (assetType != null) {
		asset.setAssetType(new AssetTypeMarshalHelper(assetManagement).convert(assetType));
	    } else {
		LOGGER.warn("Asset references invalid asset type.");
	    }
	}
	return asset;
    }

    public boolean isIncludeAssetType() {
	return includeAssetType;
    }

    public void setIncludeAssetType(boolean includeAssetType) {
	this.includeAssetType = includeAssetType;
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }
}