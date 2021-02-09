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
