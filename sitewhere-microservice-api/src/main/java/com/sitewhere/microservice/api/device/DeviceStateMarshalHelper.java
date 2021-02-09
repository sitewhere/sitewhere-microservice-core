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

import com.sitewhere.microservice.api.asset.AssetMarshalHelper;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceState;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.state.IDeviceState;

/**
 * Configurable helper class that allows {@link DeviceState} model objects to be
 * created from {@link IDeviceState} SPI objects.
 */
public class DeviceStateMarshalHelper {

    /** Indicates whether to include device information */
    private boolean includeDevice = false;

    /** Indicates whether to include device type */
    private boolean includeDeviceType = false;

    /** Indicates whether to include device assignment information */
    private boolean includeDeviceAssignment = false;

    /** Indicates whether to include customer information */
    private boolean includeCustomer = false;

    /** Indicates whether to include area information */
    private boolean includeArea = false;

    /** Indicates whether device asset information is to be included */
    private boolean includeAsset = false;

    /** Indicates whether event details should be included */
    private boolean includeRecentEvents = false;

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Device event management */
    private IDeviceEventManagement deviceEventManagement;

    /** Asset management */
    private IAssetManagement assetManagement;

    /** Controls marshaling of devices */
    private DeviceMarshalHelper deviceHelper;

    /** Controls marshaling of device types */
    private DeviceTypeMarshalHelper deviceTypeHelper;

    /** Controls marshaling of device assignments */
    private DeviceAssignmentMarshalHelper deviceAssignmentHelper;

    /** Controls marshaling of customers */
    private CustomerMarshalHelper customerHelper;

    /** Controls marshaling of areas */
    private AreaMarshalHelper areaHelper;

    /** Controls marshaling of assets */
    private AssetMarshalHelper assetHelper;

    public DeviceStateMarshalHelper(IDeviceManagement deviceManagement, IDeviceEventManagement deviceEventManagement,
	    IAssetManagement assetManagement) {
	this.deviceManagement = deviceManagement;
	this.deviceEventManagement = deviceEventManagement;
	this.assetManagement = assetManagement;
    }

    /**
     * Convert the SPI object into a model object for marshaling.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledDeviceState convert(IDeviceState source) throws SiteWhereException {
	MarshaledDeviceState result = new MarshaledDeviceState();
	result.setId(source.getId());
	result.setDeviceId(source.getDeviceId());
	result.setDeviceTypeId(source.getDeviceTypeId());
	result.setDeviceAssignmentId(source.getDeviceAssignmentId());
	result.setCustomerId(source.getCustomerId());
	result.setAreaId(source.getAreaId());
	result.setAssetId(source.getAssetId());
	result.setLastInteractionDate(source.getLastInteractionDate());
	result.setPresenceMissingDate(source.getPresenceMissingDate());

	addAssignmentDetail(source, result);
	if (isIncludeRecentEvents()) {
	    addRecentEvents(source);
	}

	return result;
    }

    /**
     * Allow detail for contained references to be returned.
     * 
     * @param source
     * @param result
     * @throws SiteWhereException
     */
    protected void addAssignmentDetail(IDeviceState source, MarshaledDeviceState result) throws SiteWhereException {
	// Add device information.
	if (isIncludeDevice()) {
	    IDevice device = getDeviceManagement().getDevice(source.getDeviceId());
	    if (device != null) {
		result.setDevice(getDeviceHelper().convert(device, getAssetManagement()));
	    }
	}

	// Add device type information.
	if (isIncludeDeviceType()) {
	    IDeviceType deviceType = getDeviceManagement().getDeviceType(source.getDeviceTypeId());
	    if (deviceType != null) {
		result.setDeviceType(getDeviceTypeHelper().convert(deviceType));
	    }
	}

	// Add device assignment information.
	if (isIncludeDeviceAssignment()) {
	    IDeviceAssignment deviceAssignment = getDeviceManagement()
		    .getDeviceAssignment(source.getDeviceAssignmentId());
	    if (deviceAssignment != null) {
		result.setDeviceAssignment(getDeviceAssignmentHelper().convert(deviceAssignment, getAssetManagement()));
	    }
	}

	// If customer is assigned, look it up.
	if ((isIncludeCustomer()) && (source.getCustomerId() != null)) {
	    ICustomer customer = getDeviceManagement().getCustomer(source.getCustomerId());
	    if (customer == null) {
		customer = new InvalidCustomer();
	    }
	    result.setCustomer(getCustomerHelper().convert(customer));
	}

	// If area is assigned, look it up.
	if ((isIncludeArea()) && (source.getAreaId() != null)) {
	    IArea area = getDeviceManagement().getArea(source.getAreaId());
	    if (area == null) {
		area = new InvalidArea();
	    }
	    result.setArea(getAreaHelper().convert(area));
	}

	// If asset is assigned, look it up.
	if (isIncludeAsset() && (source.getAssetId() != null)) {
	    IAsset asset = assetManagement.getAsset(source.getAssetId());
	    if (asset == null) {
		asset = new InvalidAsset();
	    }
	    result.setAsset(getAssetHelper().convert(asset));
	}
    }

    /**
     * Add recent events.
     * 
     * @param source
     */
    protected void addRecentEvents(IDeviceState source) {

    }

    /**
     * Get the helper for marshaling device information.
     * 
     * @return
     */
    protected DeviceMarshalHelper getDeviceHelper() {
	if (deviceHelper == null) {
	    deviceHelper = new DeviceMarshalHelper(getDeviceManagement());
	    deviceHelper.setIncludeAssignments(false);
	    deviceHelper.setIncludeDeviceType(false);
	}
	return deviceHelper;
    }

    protected DeviceTypeMarshalHelper getDeviceTypeHelper() {
	if (deviceTypeHelper == null) {
	    deviceTypeHelper = new DeviceTypeMarshalHelper(getDeviceManagement());
	}
	return deviceTypeHelper;
    }

    protected DeviceAssignmentMarshalHelper getDeviceAssignmentHelper() {
	if (deviceAssignmentHelper == null) {
	    this.deviceAssignmentHelper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	}
	return deviceAssignmentHelper;
    }

    protected CustomerMarshalHelper getCustomerHelper() {
	if (customerHelper == null) {
	    this.customerHelper = new CustomerMarshalHelper(getDeviceManagement(), getAssetManagement());
	}
	return customerHelper;
    }

    protected AreaMarshalHelper getAreaHelper() {
	if (areaHelper == null) {
	    this.areaHelper = new AreaMarshalHelper(getDeviceManagement(), getAssetManagement());
	}
	return areaHelper;
    }

    protected AssetMarshalHelper getAssetHelper() {
	if (assetHelper == null) {
	    this.assetHelper = new AssetMarshalHelper(getAssetManagement());
	}
	return assetHelper;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public IDeviceEventManagement getDeviceEventManagement() {
	return deviceEventManagement;
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    public boolean isIncludeDevice() {
	return includeDevice;
    }

    public void setIncludeDevice(boolean includeDevice) {
	this.includeDevice = includeDevice;
    }

    public boolean isIncludeDeviceType() {
	return includeDeviceType;
    }

    public void setIncludeDeviceType(boolean includeDeviceType) {
	this.includeDeviceType = includeDeviceType;
    }

    public boolean isIncludeDeviceAssignment() {
	return includeDeviceAssignment;
    }

    public void setIncludeDeviceAssignment(boolean includeDeviceAssignment) {
	this.includeDeviceAssignment = includeDeviceAssignment;
    }

    public boolean isIncludeCustomer() {
	return includeCustomer;
    }

    public void setIncludeCustomer(boolean includeCustomer) {
	this.includeCustomer = includeCustomer;
    }

    public boolean isIncludeArea() {
	return includeArea;
    }

    public void setIncludeArea(boolean includeArea) {
	this.includeArea = includeArea;
    }

    public boolean isIncludeAsset() {
	return includeAsset;
    }

    public void setIncludeAsset(boolean includeAsset) {
	this.includeAsset = includeAsset;
    }

    public boolean isIncludeRecentEvents() {
	return includeRecentEvents;
    }

    public void setIncludeRecentEvents(boolean includeRecentEvents) {
	this.includeRecentEvents = includeRecentEvents;
    }
}