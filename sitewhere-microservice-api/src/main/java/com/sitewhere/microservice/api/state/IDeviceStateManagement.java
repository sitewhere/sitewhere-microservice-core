/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.api.state;

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.IRecentStateEvent;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.device.state.request.IRecentStateEventCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceStateSearchCriteria;
import com.sitewhere.spi.search.device.IRecentStateEventSearchCriteria;

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
     * Delete existing device state.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    IDeviceState deleteDeviceState(UUID id) throws SiteWhereException;

    /**
     * Create a recent state event.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IRecentStateEvent createRecentStateEvent(IRecentStateEventCreateRequest request) throws SiteWhereException;

    /**
     * Get recent state event by id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    IRecentStateEvent getRecentStateEvent(UUID id) throws SiteWhereException;

    /**
     * Search recent state events based on criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<? extends IRecentStateEvent> searchRecentStateEvents(IRecentStateEventSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Update an existing recent state event.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IRecentStateEvent updateRecentStateEvent(UUID id, IRecentStateEventCreateRequest request) throws SiteWhereException;

    /**
     * Delete a recent state event based on id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    IRecentStateEvent deleteRecentStateEvent(UUID id) throws SiteWhereException;
}
