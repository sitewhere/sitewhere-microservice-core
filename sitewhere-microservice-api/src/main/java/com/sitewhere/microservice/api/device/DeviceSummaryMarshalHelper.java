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
package com.sitewhere.microservice.api.device;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.rest.model.device.DeviceSummary;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignmentSummary;
import com.sitewhere.spi.device.IDeviceSummary;

/**
 * Configurable helper class that allows {@link DeviceSummary} model objects to
 * be created from {@link IDeviceSummary} SPI objects.
 */
public class DeviceSummaryMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceSummaryMarshalHelper.class);

    /** Flag for whether to include assignment asset information */
    private boolean includeAsset = false;

    public DeviceSummary convert(IDeviceSummary api, IAssetManagement assetManagement) throws SiteWhereException {
	DeviceAssignmentSummaryMarshalHelper helper = new DeviceAssignmentSummaryMarshalHelper();
	DeviceSummary summary = new DeviceSummary();
	summary.setComments(api.getComments());
	summary.setStatus(api.getStatus());
	summary.setParentDeviceId(api.getParentDeviceId());
	summary.setDeviceTypeId(api.getDeviceTypeId());
	summary.setDeviceTypeName(api.getDeviceTypeName());
	summary.setDeviceTypeImageUrl(api.getDeviceTypeImageUrl());
	summary.setDeviceAssignmentSummaries(new ArrayList<>());
	for (IDeviceAssignmentSummary assnApi : api.getDeviceAssignmentSummaries()) {
	    summary.getDeviceAssignmentSummaries().add(helper.convert(assnApi, assetManagement));
	}
	PersistentEntity.copy(api, summary);
	return summary;
    }

    public boolean isIncludeAsset() {
	return includeAsset;
    }

    public void setIncludeAsset(boolean includeAsset) {
	this.includeAsset = includeAsset;
    }
}
