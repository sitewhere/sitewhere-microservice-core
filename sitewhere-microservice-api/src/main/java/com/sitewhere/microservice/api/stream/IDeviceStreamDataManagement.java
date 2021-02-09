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
