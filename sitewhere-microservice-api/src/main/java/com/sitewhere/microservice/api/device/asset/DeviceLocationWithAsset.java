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
package com.sitewhere.microservice.api.device.asset;

import java.math.BigDecimal;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceLocation;

/**
 * Wraps a {@link DeviceLocation} so that information about the asset associated
 * with its assignment is available.
 */
public class DeviceLocationWithAsset extends DeviceEventWithAsset implements IDeviceLocation {

    /** Serial version UID */
    private static final long serialVersionUID = -8449689938042640635L;

    public DeviceLocationWithAsset(IDeviceLocation wrapped, IAssetManagement assetManagement)
	    throws SiteWhereException {
	super(wrapped, assetManagement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceLocation#getLatitude()
     */
    @Override
    public BigDecimal getLatitude() {
	return ((IDeviceLocation) getWrapped()).getLatitude();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceLocation#getLongitude()
     */
    @Override
    public BigDecimal getLongitude() {
	return ((IDeviceLocation) getWrapped()).getLongitude();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceLocation#getElevation()
     */
    @Override
    public BigDecimal getElevation() {
	return ((IDeviceLocation) getWrapped()).getElevation();
    }
}