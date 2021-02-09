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

import java.util.Map;
import java.util.UUID;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

public class DeviceCommandInvocationWithAsset extends DeviceEventWithAsset implements IDeviceCommandInvocation {

    /** Serial version UID */
    private static final long serialVersionUID = 5274138683101218581L;

    public DeviceCommandInvocationWithAsset(IDeviceCommandInvocation wrapped, IAssetManagement assetManagement)
	    throws SiteWhereException {
	super(wrapped, assetManagement);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getInitiator()
     */
    @Override
    public CommandInitiator getInitiator() {
	return ((IDeviceCommandInvocation) getWrapped()).getInitiator();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getInitiatorId()
     */
    @Override
    public String getInitiatorId() {
	return ((IDeviceCommandInvocation) getWrapped()).getInitiatorId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getTarget()
     */
    @Override
    public CommandTarget getTarget() {
	return ((IDeviceCommandInvocation) getWrapped()).getTarget();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getTargetId()
     */
    @Override
    public String getTargetId() {
	return ((IDeviceCommandInvocation) getWrapped()).getTargetId();
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation#getDeviceCommandId()
     */
    @Override
    public UUID getDeviceCommandId() {
	return ((IDeviceCommandInvocation) getWrapped()).getDeviceCommandId();
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation#getParameterValues()
     */
    @Override
    public Map<String, String> getParameterValues() {
	return ((IDeviceCommandInvocation) getWrapped()).getParameterValues();
    }
}