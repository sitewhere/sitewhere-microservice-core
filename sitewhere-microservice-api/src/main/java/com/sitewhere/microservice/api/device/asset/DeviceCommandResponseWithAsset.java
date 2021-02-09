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

import java.util.UUID;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;

/**
 * Wraps a {@link DeviceCommandResponse} so that information about the asset
 * associated with its assignment is available.
 */
public class DeviceCommandResponseWithAsset extends DeviceEventWithAsset implements IDeviceCommandResponse {

    /** Serial version UID */
    private static final long serialVersionUID = 6946071189269318157L;

    public DeviceCommandResponseWithAsset(IDeviceCommandResponse wrapped, IAssetManagement assetManagement)
	    throws SiteWhereException {
	super(wrapped, assetManagement);
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceCommandResponse#getOriginatingEventId()
     */
    @Override
    public UUID getOriginatingEventId() {
	return ((IDeviceCommandResponse) getWrapped()).getOriginatingEventId();
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceCommandResponse#getResponseEventId()
     */
    @Override
    public UUID getResponseEventId() {
	return ((IDeviceCommandResponse) getWrapped()).getResponseEventId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceCommandResponse#getResponse()
     */
    @Override
    public String getResponse() {
	return ((IDeviceCommandResponse) getWrapped()).getResponse();
    }
}