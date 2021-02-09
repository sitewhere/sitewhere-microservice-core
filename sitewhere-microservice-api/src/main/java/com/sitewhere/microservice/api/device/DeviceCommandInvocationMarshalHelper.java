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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Configurable helper class that allows {@link DeviceCommandInvocation} model
 * objects to be created from {@link IDeviceCommandInvocation} SPI objects.
 */
public class DeviceCommandInvocationMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceCommandInvocationMarshalHelper.class);

    /** Device Management */
    private IDeviceManagement deviceManagement;

    /** Indicates whether to include command information */
    private boolean includeCommand = false;

    public DeviceCommandInvocationMarshalHelper(IDeviceManagement deviceManagement) {
	this(deviceManagement, false);
    }

    public DeviceCommandInvocationMarshalHelper(IDeviceManagement deviceManagement, boolean includeCommand) {
	this.deviceManagement = deviceManagement;
	this.includeCommand = includeCommand;
    }

    /**
     * Convert an {@link IDeviceCommandInvocation} to a
     * {@link DeviceCommandInvocation}, populating command information if requested
     * so the marshaled data includes it.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledDeviceCommandInvocation convert(IDeviceCommandInvocation source) throws SiteWhereException {
	MarshaledDeviceCommandInvocation result = new MarshaledDeviceCommandInvocation();
	result.setInitiator(source.getInitiator());
	result.setInitiatorId(source.getInitiatorId());
	result.setTarget(source.getTarget());
	result.setTargetId(source.getTargetId());
	result.setDeviceCommandId(source.getDeviceCommandId());
	result.setParameterValues(source.getParameterValues());

	// Copy event fields.
	result.setId(source.getId());
	result.setAlternateId(source.getAlternateId());
	result.setEventType(source.getEventType());
	result.setDeviceId(source.getDeviceId());
	result.setDeviceAssignmentId(source.getDeviceAssignmentId());
	result.setAreaId(source.getAreaId());
	result.setAssetId(source.getAssetId());
	result.setEventDate(source.getEventDate());
	result.setReceivedDate(source.getReceivedDate());
	MetadataProvider.copy(source, result);

	if (isIncludeCommand()) {
	    if (source.getDeviceCommandId() == null) {
		LOGGER.warn("Device invocation is missing command id.");
		return result;
	    }
	    IDeviceCommand found = getDeviceManagement().getDeviceCommand(source.getDeviceCommandId());
	    if (found == null) {
		LOGGER.warn("Device invocation references a non-existent command token.");
		return result;
	    }
	    DeviceCommand command = DeviceCommand.copy(found);
	    if (command != null) {
		result.setCommand(command);
		result.setAsHtml(CommandHtmlHelper.getHtml(result));
	    }
	}
	return result;
    }

    public boolean isIncludeCommand() {
	return includeCommand;
    }

    public void setIncludeCommand(boolean includeCommand) {
	this.includeCommand = includeCommand;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}