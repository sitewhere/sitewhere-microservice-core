/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.api.stream;

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.streaming.IDeviceStreamData;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Interface for device stream data management operations.
 */
public interface IDeviceStreamDataManagement extends ITenantEngineLifecycleComponent {

    /**
     * Add data to an existing device stream.
     * 
     * @param streamId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IDeviceStreamData addDeviceStreamData(UUID streamId, IDeviceStreamDataCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get a single chunk of data from a device stream.
     * 
     * @param streamId
     * @param sequenceNumber
     * @return
     * @throws SiteWhereException
     */
    IDeviceStreamData getDeviceStreamData(UUID streamId, long sequenceNumber) throws SiteWhereException;

    /**
     * List all chunks of data for the given stream based on criteria.
     * 
     * @param streamId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IDeviceStreamData> listDeviceStreamDataForAssignment(UUID streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;
}
