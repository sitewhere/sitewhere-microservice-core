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
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.IDeviceAlert;

/**
 * Wraps a {@link DeviceAlert} so that information about the asset associated
 * with its assignment is available.
 */
public class DeviceAlertWithAsset extends DeviceEventWithAsset implements IDeviceAlert {

    /** Serial version UID */
    private static final long serialVersionUID = -8737823382691759826L;

    public DeviceAlertWithAsset(IDeviceAlert wrapped, IAssetManagement assetManagement) throws SiteWhereException {
	super(wrapped, assetManagement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getSource()
     */
    @Override
    public AlertSource getSource() {
	return ((IDeviceAlert) getWrapped()).getSource();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getLevel()
     */
    @Override
    public AlertLevel getLevel() {
	return ((IDeviceAlert) getWrapped()).getLevel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getType()
     */
    @Override
    public String getType() {
	return ((IDeviceAlert) getWrapped()).getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getMessage()
     */
    @Override
    public String getMessage() {
	return ((IDeviceAlert) getWrapped()).getMessage();
    }
}