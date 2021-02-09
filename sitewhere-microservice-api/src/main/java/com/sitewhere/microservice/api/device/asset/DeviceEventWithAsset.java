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

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.device.asset.IDeviceEventWithAsset;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Wraps a device event and provides extra information the associated asset from
 * its assignment.
 */
public class DeviceEventWithAsset implements IDeviceEventWithAsset {

    /** Serial version UID */
    private static final long serialVersionUID = 4865401913475898245L;

    /** Text shown when an asset is not assigned */
    public static final String UNASSOCIATED_ASSET_NAME = "Unassociated";

    /** Wrapped event */
    protected IDeviceEvent wrapped;

    /** Associated asset */
    protected IAsset asset;

    public DeviceEventWithAsset(IDeviceEvent wrapped, IAssetManagement assetManagement) throws SiteWhereException {
	this.wrapped = wrapped;
	if (wrapped.getAssetId() != null) {
	    this.asset = assetManagement.getAsset(wrapped.getAssetId());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.asset.IDeviceEventWithAsset#getAssetName()
     */
    @Override
    public String getAssetName() {
	if (asset != null) {
	    return asset.getName();
	}
	return UNASSOCIATED_ASSET_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return getWrapped().getMetadata();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getId()
     */
    @Override
    public UUID getId() {
	return getWrapped().getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getAlternateId()
     */
    @Override
    public String getAlternateId() {
	return getWrapped().getAlternateId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getEventType()
     */
    @Override
    public DeviceEventType getEventType() {
	return getWrapped().getEventType();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return getWrapped().getDeviceId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getDeviceAssignmentId()
     */
    @Override
    public UUID getDeviceAssignmentId() {
	return getWrapped().getDeviceAssignmentId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getCustomerId()
     */
    @Override
    public UUID getCustomerId() {
	return getWrapped().getCustomerId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getAreaId()
     */
    @Override
    public UUID getAreaId() {
	return getWrapped().getAreaId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getAssetId()
     */
    @Override
    public UUID getAssetId() {
	return getWrapped().getDeviceId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceEvent#getEventDate()
     */
    @Override
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEventDate() {
	return getWrapped().getEventDate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceEvent#getReceivedDate()
     */
    @Override
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getReceivedDate() {
	return getWrapped().getReceivedDate();
    }

    protected IDeviceEvent getWrapped() {
	return wrapped;
    }
}