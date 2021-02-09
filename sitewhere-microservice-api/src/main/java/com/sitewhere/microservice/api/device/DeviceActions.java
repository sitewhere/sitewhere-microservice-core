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

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.sitewhere.microservice.api.event.DeviceEventRequestBuilder;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceActions;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Handles underlying logic to make common actions simpler to invoke from
 * scripts.
 */
public class DeviceActions implements IDeviceActions {

    /** Device management implementation */
    private IDeviceManagement deviceManagement;

    /** Device event management implementation */
    private IDeviceEventManagement deviceEventManagement;

    public DeviceActions(IDeviceManagement deviceManagement, IDeviceEventManagement deviceEventManagement) {
	this.deviceManagement = deviceManagement;
	this.deviceEventManagement = deviceEventManagement;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceActions#createLocation(com.sitewhere.spi.
     * device.IDeviceAssignment, java.math.BigDecimal, java.math.BigDecimal,
     * java.math.BigDecimal, boolean)
     */
    @Override
    public void createLocation(IDeviceAssignment assignment, BigDecimal latitude, BigDecimal longitude,
	    BigDecimal elevation, boolean updateState) throws SiteWhereException {
	DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
	location.setLatitude(latitude);
	location.setLongitude(longitude);
	location.setElevation(elevation);
	location.setEventDate(new Date());
	location.setUpdateState(updateState);
	getDeviceEventManagement().addDeviceLocations(
		DeviceEventRequestBuilder.getContextForAssignment(getDeviceManagement(), assignment), location);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceActions#sendCommand(com.sitewhere.spi.
     * device.IDeviceAssignment, java.lang.String, java.util.Map)
     */
    @Override
    public void sendCommand(IDeviceAssignment assignment, String commandName, Map<String, String> parameters)
	    throws SiteWhereException {
	IDeviceType type = getDeviceManagement().getDeviceType(assignment.getDeviceTypeId());
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	criteria.setDeviceTypeToken(type.getToken());
	ISearchResults<? extends IDeviceCommand> commands = getDeviceManagement().listDeviceCommands(criteria);
	IDeviceCommand match = null;
	for (IDeviceCommand command : commands.getResults()) {
	    if (command.getName().equals(commandName)) {
		match = command;
	    }
	}
	if (match == null) {
	    throw new SiteWhereException("Command not executed. No command found matching: " + commandName);
	}
	DeviceCommandInvocationCreateRequest create = new DeviceCommandInvocationCreateRequest();
	create.setCommandToken(match.getToken());
	create.setParameterValues(parameters);
	create.setInitiator(CommandInitiator.Script);
	create.setTarget(CommandTarget.Assignment);
	create.setTargetId(assignment.getToken());
	create.setEventDate(new Date());
	getDeviceEventManagement().addDeviceCommandInvocations(
		DeviceEventRequestBuilder.getContextForAssignment(getDeviceManagement(), assignment), create);
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
}