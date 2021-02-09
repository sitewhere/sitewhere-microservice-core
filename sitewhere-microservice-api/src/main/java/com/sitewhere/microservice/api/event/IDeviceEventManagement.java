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
import com.sitewhere.spi.device.event.IDeviceEventContext;
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
     * Add a batch of events for the given context.
     * 
     * @param context
     * @param batch
     * @return
     * @throws SiteWhereException
     */
    IDeviceEventBatchResponse addDeviceEventBatch(IDeviceEventContext context, IDeviceEventBatch batch)
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
     * Add one or more measurements for a given context.
     * 
     * @param context
     * @param measurement
     * @return
     * @throws SiteWhereException
     */
    List<? extends IDeviceMeasurement> addDeviceMeasurements(IDeviceEventContext context,
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
     * Add one or more device locations for a given context.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    List<? extends IDeviceLocation> addDeviceLocations(IDeviceEventContext context,
	    IDeviceLocationCreateRequest... request) throws SiteWhereException;

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
     * Add one or more device alerts for a given context.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    List<? extends IDeviceAlert> addDeviceAlerts(IDeviceEventContext context, IDeviceAlertCreateRequest... request)
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
     * Add one or more device command invocations for the given context.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    List<? extends IDeviceCommandInvocation> addDeviceCommandInvocations(IDeviceEventContext context,
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
     * Adds one or more device command responses for the given context.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    List<? extends IDeviceCommandResponse> addDeviceCommandResponses(IDeviceEventContext context,
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
     * Adds one or more device state change events for the given context.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    List<? extends IDeviceStateChange> addDeviceStateChanges(IDeviceEventContext context,
	    IDeviceStateChangeCreateRequest... request) throws SiteWhereException;

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