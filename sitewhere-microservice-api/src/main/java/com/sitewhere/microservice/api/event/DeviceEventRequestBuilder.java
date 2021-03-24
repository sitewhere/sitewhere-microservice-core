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
package com.sitewhere.microservice.api.event;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rest.model.device.event.DeviceEventContext;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.event.scripting.DeviceEventSupport;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Exposes builders for creating SiteWhere events.
 */
public class DeviceEventRequestBuilder {

    /** Device management implementation */
    private IDeviceManagement deviceManagement;

    /** Event management interface */
    private IDeviceEventManagement eventManagement;

    public DeviceEventRequestBuilder(IDeviceManagement deviceManagement, IDeviceEventManagement eventManagement) {
	this.deviceManagement = deviceManagement;
	this.eventManagement = eventManagement;
    }

    public DeviceLocationCreateRequest.Builder newLocation(BigDecimal latitude, BigDecimal longitude) {
	return new DeviceLocationCreateRequest.Builder(latitude, longitude);
    }

    public DeviceLocationCreateRequest.Builder newLocation(double latitude, double longitude) {
	return new DeviceLocationCreateRequest.Builder(latitude, longitude);
    }

    public DeviceMeasurementCreateRequest.Builder newMeasurements() {
	return new DeviceMeasurementCreateRequest.Builder();
    }

    public DeviceAlertCreateRequest.Builder newAlert(String type, String message) {
	return new DeviceAlertCreateRequest.Builder(type, message);
    }

    public DeviceCommandInvocationCreateRequest.Builder newCommandInvocation(String commandName, String target) {
	try {
	    IDeviceAssignment targetAssignment = deviceManagement.getDeviceAssignmentByToken(target);
	    if (targetAssignment == null) {
		throw new SiteWhereException("Target assignment not found: " + target);
	    }
	    IDeviceType type = deviceManagement.getDeviceType(targetAssignment.getDeviceTypeId());
	    DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	    criteria.setDeviceTypeToken(type.getToken());
	    ISearchResults<? extends IDeviceCommand> commands = deviceManagement.listDeviceCommands(criteria);
	    IDeviceCommand match = null;
	    for (IDeviceCommand command : commands.getResults()) {
		if (command.getName().equals(commandName)) {
		    match = command;
		}
	    }
	    if (match == null) {
		throw new SiteWhereException("Command not executed. No command found matching: " + commandName);
	    }
	    return new DeviceCommandInvocationCreateRequest.Builder(match.getToken(), target);
	} catch (SiteWhereException e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Source id passed in events generated as side-effect of REST calls.
     * 
     * @return
     */
    public static String createBuilderSourceId() {
	return "BUILDER:" + UUID.randomUUID().toString();
    }

    public AssignmentScope forSameAssignmentAs(DeviceEventSupport support) throws SiteWhereException {
	return new AssignmentScope(getEventManagement(),
		getContextForAssignment(createBuilderSourceId(), getDeviceManagement(), support.getDeviceAssignment()));
    }

    public AssignmentScope forSameAssignmentAs(IDeviceEvent event) throws SiteWhereException {
	return new AssignmentScope(getEventManagement(), getContextForAssignment(createBuilderSourceId(),
		getDeviceManagement(), getDeviceManagement().getDeviceAssignment(event.getDeviceAssignmentId())));
    }

    public AssignmentScope forAssignment(String assignmentToken) throws SiteWhereException {
	return new AssignmentScope(getEventManagement(), getContextForAssignment(createBuilderSourceId(),
		getDeviceManagement(), getDeviceManagement().getDeviceAssignmentByToken(assignmentToken)));
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public IDeviceEventManagement getEventManagement() {
	return eventManagement;
    }

    /**
     * Get context information based on a device assignment.
     * 
     * @param deviceManagement
     * @param assignment
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEventContext getContextForAssignment(String sourceId, IDeviceManagement deviceManagement,
	    IDeviceAssignment assignment) throws SiteWhereException {
	IDevice device = deviceManagement.getDevice(assignment.getDeviceId());
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceId, ErrorLevel.ERROR);
	}

	DeviceEventContext context = new DeviceEventContext();
	context.setSourceId(sourceId);
	context.setDeviceToken(device.getToken());
	context.setDeviceId(device.getId());
	context.setDeviceTypeId(device.getDeviceTypeId());
	context.setParentDeviceId(device.getParentDeviceId());
	context.setDeviceStatus(device.getStatus());
	context.setDeviceMetadata(device.getMetadata());
	context.setDeviceAssignmentId(assignment.getId());
	context.setCustomerId(assignment.getCustomerId());
	context.setAreaId(assignment.getAreaId());
	context.setAssetId(assignment.getAssetId());
	context.setDeviceAssignmentStatus(assignment.getStatus());
	context.setDeviceAssignmentMetadata(assignment.getMetadata());
	return context;
    }

    public static class AssignmentScope {

	/** Event management interface */
	private IDeviceEventManagement events;

	/** Device assignment */
	private IDeviceEventContext context;

	public AssignmentScope(IDeviceEventManagement events, IDeviceEventContext context) {
	    this.events = events;
	    this.context = context;
	}

	/**
	 * Persist a single location event.
	 * 
	 * @param builder
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceLocation persist(DeviceLocationCreateRequest.Builder builder) throws SiteWhereException {
	    DeviceLocationCreateRequest request = builder.build();
	    return events.addDeviceLocations(getContext(), request).get(0);
	}

	/**
	 * Persist multiple location events.
	 * 
	 * @param builders
	 * @return
	 * @throws SiteWhereException
	 */
	public List<? extends IDeviceLocation> persistLocations(List<DeviceLocationCreateRequest.Builder> builders)
		throws SiteWhereException {
	    List<DeviceLocationCreateRequest> requests = new ArrayList<>();
	    for (DeviceLocationCreateRequest.Builder builder : builders) {
		DeviceLocationCreateRequest request = builder.build();
		requests.add(request);
	    }
	    return events.addDeviceLocations(getContext(), requests.toArray(new DeviceLocationCreateRequest[0]));
	}

	/**
	 * Persist a single measurement event.
	 * 
	 * @param builder
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceMeasurement persist(DeviceMeasurementCreateRequest.Builder builder) throws SiteWhereException {
	    DeviceMeasurementCreateRequest request = builder.build();
	    return events.addDeviceMeasurements(getContext(), request).get(0);
	}

	/**
	 * Persist multiple measurement events.
	 * 
	 * @param builders
	 * @return
	 * @throws SiteWhereException
	 */
	public List<? extends IDeviceMeasurement> persistMeasurements(
		List<DeviceMeasurementCreateRequest.Builder> builders) throws SiteWhereException {
	    List<DeviceMeasurementCreateRequest> requests = new ArrayList<>();
	    for (DeviceMeasurementCreateRequest.Builder builder : builders) {
		DeviceMeasurementCreateRequest request = builder.build();
		requests.add(request);
	    }
	    return events.addDeviceMeasurements(getContext(), requests.toArray(new DeviceMeasurementCreateRequest[0]));
	}

	/**
	 * Persist a single alert event.
	 * 
	 * @param builder
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAlert persist(DeviceAlertCreateRequest.Builder builder) throws SiteWhereException {
	    DeviceAlertCreateRequest request = builder.build();
	    return events.addDeviceAlerts(getContext(), request).get(0);
	}

	/**
	 * Persist multiple alert events.
	 * 
	 * @param builders
	 * @return
	 * @throws SiteWhereException
	 */
	public List<? extends IDeviceAlert> persistAlerts(List<DeviceAlertCreateRequest.Builder> builders)
		throws SiteWhereException {
	    List<DeviceAlertCreateRequest> requests = new ArrayList<>();
	    for (DeviceAlertCreateRequest.Builder builder : builders) {
		DeviceAlertCreateRequest request = builder.build();
		requests.add(request);
	    }
	    return events.addDeviceAlerts(getContext(), requests.toArray(new DeviceAlertCreateRequest[0]));
	}

	public IDeviceCommandInvocation persist(DeviceCommandInvocationCreateRequest.Builder builder)
		throws SiteWhereException {
	    DeviceCommandInvocationCreateRequest request = builder.build();
	    return events.addDeviceCommandInvocations(getContext(), request).get(0);
	}

	public IDeviceEventContext getContext() {
	    return context;
	}
    }
}