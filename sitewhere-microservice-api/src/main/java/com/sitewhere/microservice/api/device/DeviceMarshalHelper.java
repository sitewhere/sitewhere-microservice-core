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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.marshaling.MarshaledDevice;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceAssignment;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Configurable helper class that allows {@link Device} model objects to be
 * created from {@link IDevice} SPI objects.
 */
public class DeviceMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceMarshalHelper.class);

    /** Tenant */
    private IDeviceManagement deviceManagement;

    /** Indicates whether device type information is to be included */
    private boolean includeDeviceType = true;

    /** Indicates whether device assignment information is to be copied */
    private boolean includeAssignments = false;

    /**
     * Indicates whether device element mappings should include device details
     */
    private boolean includeNested = false;

    /** Helper for marshaling device type information */
    private DeviceTypeMarshalHelper deviceTypeHelper;

    /** Helper for marshaling device assignement information */
    private DeviceAssignmentMarshalHelper assignmentHelper;

    /** Helper for marshaling nested devices */
    private DeviceMarshalHelper nestedHelper;

    public DeviceMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert an IDevice SPI object into a model object for marshaling.
     * 
     * @param source
     * @param assetManagement
     * @return
     * @throws SiteWhereException
     */
    public MarshaledDevice convert(IDevice source, IAssetManagement assetManagement) throws SiteWhereException {
	MarshaledDevice result = new MarshaledDevice();
	result.setDeviceTypeId(source.getDeviceTypeId());
	result.setParentDeviceId(source.getParentDeviceId());
	result.setStatus(source.getStatus());
	result.setComments(source.getComments());
	PersistentEntity.copy(source, result);

	// Copy device element mappings.
	for (IDeviceElementMapping mapping : source.getDeviceElementMappings()) {
	    DeviceElementMapping cnvMapping = DeviceElementMapping.copy(mapping);
	    if (isIncludeNested()) {
		IDevice device = getDeviceManagement().getDeviceByToken(mapping.getDeviceToken());
		cnvMapping.setDevice(getNestedHelper().convert(device, assetManagement));
	    }
	    result.getDeviceElementMappings().add(cnvMapping);
	}

	// Look up device type information.
	if ((source.getDeviceTypeId() != null) && (isIncludeDeviceType())) {
	    IDeviceType deviceType = getDeviceManagement().getDeviceType(source.getDeviceTypeId());
	    if (deviceType == null) {
		throw new SiteWhereException("Device references non-existent device type.");
	    }
	    if (isIncludeDeviceType()) {
		result.setDeviceType(getDeviceTypeHelper().convert(deviceType));
	    }
	}
	if (isIncludeAssignments()) {
	    try {
		List<? extends IDeviceAssignment> assignments = getDeviceManagement()
			.getActiveDeviceAssignments(source.getId());
		List<MarshaledDeviceAssignment> converted = new ArrayList<>();
		for (IDeviceAssignment assignment : assignments) {
		    converted.add(getAssignmentHelper().convert(assignment, assetManagement));
		}
		result.setActiveDeviceAssignments(converted);
	    } catch (SiteWhereException e) {
		LOGGER.warn("Device has token for non-existent assignment.");
	    }
	}
	return result;
    }

    /**
     * Get helper class for marshaling device types .
     * 
     * @return
     */
    protected DeviceTypeMarshalHelper getDeviceTypeHelper() {
	if (deviceTypeHelper == null) {
	    deviceTypeHelper = new DeviceTypeMarshalHelper(getDeviceManagement());
	}
	return deviceTypeHelper;
    }

    /**
     * Get helper class for marshaling assignment.
     * 
     * @return
     */
    protected DeviceAssignmentMarshalHelper getAssignmentHelper() {
	if (assignmentHelper == null) {
	    assignmentHelper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	    assignmentHelper.setIncludeDevice(false);
	    assignmentHelper.setIncludeCustomer(true);
	    assignmentHelper.setIncludeArea(true);
	    assignmentHelper.setIncludeAsset(true);
	}
	return assignmentHelper;
    }

    /**
     * Get helper class for marshaling nested devices.
     * 
     * @return
     */
    protected DeviceMarshalHelper getNestedHelper() {
	if (nestedHelper == null) {
	    nestedHelper = new DeviceMarshalHelper(getDeviceManagement());
	}
	return nestedHelper;
    }

    public boolean isIncludeDeviceType() {
	return includeDeviceType;
    }

    public DeviceMarshalHelper setIncludeDeviceType(boolean includeDeviceType) {
	this.includeDeviceType = includeDeviceType;
	return this;
    }

    public boolean isIncludeAssignments() {
	return includeAssignments;
    }

    public DeviceMarshalHelper setIncludeAssignments(boolean includeAssignments) {
	this.includeAssignments = includeAssignments;
	return this;
    }

    public boolean isIncludeNested() {
	return includeNested;
    }

    public void setIncludeNested(boolean includeNested) {
	this.includeNested = includeNested;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}