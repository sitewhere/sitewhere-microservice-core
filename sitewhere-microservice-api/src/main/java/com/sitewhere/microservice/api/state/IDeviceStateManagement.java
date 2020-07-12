/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.api.state;

import java.util.List;
import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.device.state.request.IDeviceStateEventMergeRequest;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceStateSearchCriteria;

/**
 * Interface for device state management operations.
 */
public interface IDeviceStateManagement extends ITenantEngineLifecycleComponent {

    /**
     * Create device state.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IDeviceState createDeviceState(IDeviceStateCreateRequest request) throws SiteWhereException;

    /**
     * Get device state by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    IDeviceState getDeviceState(UUID id) throws SiteWhereException;

    /**
     * Get device state based on device assignment.
     * 
     * @param assignmentId
     * @return
     * @throws SiteWhereException
     */
    IDeviceState getDeviceStateByDeviceAssignment(UUID assignmentId) throws SiteWhereException;

    /**
     * Get list of device states (one per device assignment) for a device.
     * 
     * @param deviceId
     * @return
     * @throws SiteWhereException
     */
    List<? extends IDeviceState> getDeviceStatesForDevice(UUID deviceId) throws SiteWhereException;

    /**
     * Search for device states that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<? extends IDeviceState> searchDeviceStates(IDeviceStateSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Update existing device state.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IDeviceState updateDeviceState(UUID id, IDeviceStateCreateRequest request) throws SiteWhereException;

    /**
     * Merge one or more events into the device state.
     * 
     * @param id
     * @param events
     * @return
     * @throws SiteWhereException
     */
    IDeviceState merge(UUID id, IDeviceStateEventMergeRequest events) throws SiteWhereException;

    /**
     * Delete existing device state.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    IDeviceState deleteDeviceState(UUID id) throws SiteWhereException;
}
