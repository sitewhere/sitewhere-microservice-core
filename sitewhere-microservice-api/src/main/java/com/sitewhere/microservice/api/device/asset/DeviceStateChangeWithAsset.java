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

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceStateChange;

/**
 * Wraps a {@link IDeviceStateChange} so that information about the asset
 * associated with its assignment is available.
 */
public class DeviceStateChangeWithAsset extends DeviceEventWithAsset implements IDeviceStateChange {

    /** Serial version UID */
    private static final long serialVersionUID = -8012486373686574551L;

    public DeviceStateChangeWithAsset(IDeviceStateChange wrapped, IAssetManagement assetManagement)
	    throws SiteWhereException {
	super(wrapped, assetManagement);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getAttribute()
     */
    @Override
    public String getAttribute() {
	return ((IDeviceStateChange) getWrapped()).getAttribute();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getType()
     */
    @Override
    public String getType() {
	return ((IDeviceStateChange) getWrapped()).getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getPreviousState()
     */
    @Override
    public String getPreviousState() {
	return ((IDeviceStateChange) getWrapped()).getPreviousState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getNewState()
     */
    @Override
    public String getNewState() {
	return ((IDeviceStateChange) getWrapped()).getNewState();
    }
}