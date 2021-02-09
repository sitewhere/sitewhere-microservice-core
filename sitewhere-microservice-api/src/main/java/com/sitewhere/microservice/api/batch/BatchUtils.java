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
package com.sitewhere.microservice.api.batch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.batch.request.IInvocationByAssignmentCriteriaRequest;
import com.sitewhere.spi.batch.request.IInvocationByDeviceCriteriaRequest;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Utility methods for batch operations.
 */
public class BatchUtils {

    /**
     * Resolve device search criteria to a list of device tokens.
     * 
     * @param criteria
     * @param deviceManagement
     * @param assetManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<String> resolveDeviceTokensForDeviceCriteria(IInvocationByDeviceCriteriaRequest criteria,
	    IDeviceManagement deviceManagement, IAssetManagement assetManagement) throws SiteWhereException {
	if (criteria.getDeviceTypeToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}

	DeviceSearchCriteria search = new DeviceSearchCriteria(1, 0, null, null);
	search.setDeviceTypeToken(criteria.getDeviceTypeToken());
	List<? extends IDevice> matches = deviceManagement.listDevices(search).getResults();

	List<String> deviceTokens = new ArrayList<String>();
	for (IDevice match : matches) {
	    deviceTokens.add(match.getToken());
	}
	return deviceTokens;
    }

    /**
     * Resolve device assignment search criteria to a list of device tokens.
     * 
     * @param criteria
     * @param deviceManagement
     * @param assetManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<String> resolveDeviceTokensForAssignmentCriteria(IInvocationByAssignmentCriteriaRequest criteria,
	    IDeviceManagement deviceManagement, IAssetManagement assetManagement) throws SiteWhereException {
	if (criteria.getDeviceTypeToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}

	DeviceAssignmentSearchCriteria search = new DeviceAssignmentSearchCriteria(1, 0);
	search.setDeviceTypeTokens(Collections.singletonList(criteria.getDeviceTypeToken()));
	search.setCustomerTokens(criteria.getCustomerTokens());
	search.setAreaTokens(criteria.getAreaTokens());
	search.setAssetTokens(criteria.getAssetTokens());
	List<? extends IDeviceAssignment> matches = deviceManagement.listDeviceAssignments(search).getResults();

	List<String> deviceTokens = new ArrayList<String>();
	for (IDeviceAssignment match : matches) {
	    IDevice device = deviceManagement.getDevice(match.getDeviceId());
	    if (!deviceTokens.contains(device.getToken())) {
		deviceTokens.add(device.getToken());
	    }
	}
	return deviceTokens;
    }
}