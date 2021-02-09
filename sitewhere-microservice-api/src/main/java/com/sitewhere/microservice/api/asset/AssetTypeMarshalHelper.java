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

import com.sitewhere.rest.model.asset.AssetType;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Configurable helper class that allows {@link AssetType} model objects to be
 * created from {@link IAssetType} SPI objects.
 */
public class AssetTypeMarshalHelper {

    /** Asset management */
    private IAssetManagement assetManagement;

    public AssetTypeMarshalHelper(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public AssetType convert(IAssetType source) throws SiteWhereException {
	if (source == null) {
	    return null;
	}
	AssetType type = new AssetType();
	type.setName(source.getName());
	type.setDescription(source.getDescription());
	type.setAssetCategory(source.getAssetCategory());
	BrandedEntity.copy(source, type);
	return type;
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }
}