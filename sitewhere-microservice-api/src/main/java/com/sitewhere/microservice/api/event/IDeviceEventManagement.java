/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.api.event;

import java.util.List;
import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Interface for device event management operations.
 */
public interface IDeviceEventManagement extends ITenantEngineLifecycleComponent {

    /**
     * Add a batch of events for the given assignment.
     * 
     * @param deviceAssignmentId
     * @param batch
     * @return
     * @throws SiteWhereException
     */
    IDeviceEventBatchResponse addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch)
	    throws SiteWhereException;

    /**
     * Get a device event by id.
     * 
     * @param eventId
     * @return
     * @throws SiteWhereException
     */
    IDeviceEvent getDeviceEventById(UUID eventId) throws SiteWhereException;

    /**
     * Get a device event by alternate (external) id.
     * 
     * @param alternateId
     * @return
     * @throws SiteWhereException
     */
    IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException;

    /**
     * Add one or more measurements for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param measurement
     * @return
     * @throws SiteWhereException
     */
    List<IDeviceMeasurement> addDeviceMeasurements(UUID deviceAssignmentId,
	    IDeviceMeasurementCreateRequest... measurement) throws SiteWhereException;

    /**
     * List device measurement entries for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Add one or more device locations for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    List<IDeviceLocation> addDeviceLocations(UUID deviceAssignmentId, IDeviceLocationCreateRequest... request)
	    throws SiteWhereException;

    /**
     * List device location entries for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Add one or more device alerts for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    List<IDeviceAlert> addDeviceAlerts(UUID deviceAssignmentId, IDeviceAlertCreateRequest... request)
	    throws SiteWhereException;

    /**
     * List device location entries for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Add one or more device command invocations for the given assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    List<IDeviceCommandInvocation> addDeviceCommandInvocations(UUID deviceAssignmentId,
	    IDeviceCommandInvocationCreateRequest... request) throws SiteWhereException;

    /**
     * List device command invocation events for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List responses associated with a command invocation.
     * 
     * @param invocationId
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID invocationId)
	    throws SiteWhereException;

    /**
     * Adds one or more device command responses for the given device assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    List<IDeviceCommandResponse> addDeviceCommandResponses(UUID deviceAssignmentId,
	    IDeviceCommandResponseCreateRequest... request) throws SiteWhereException;

    /**
     * List device command response events for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Adds one or more device state change events for the given assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    List<IDeviceStateChange> addDeviceStateChanges(UUID deviceAssignmentId, IDeviceStateChangeCreateRequest... request)
	    throws SiteWhereException;

    /**
     * List device state change events for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IDeviceStateChange> listDeviceStateChangesForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;
}