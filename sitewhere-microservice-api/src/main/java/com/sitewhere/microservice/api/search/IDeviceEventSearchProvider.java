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
package com.sitewhere.microservice.api.search;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;

/**
 * Search provider that provides information about SiteWhere device events.
 */
public interface IDeviceEventSearchProvider extends ISearchProvider {

    /**
     * Executes an arbitrary event query against the search provider.
     * 
     * @param query
     * @return
     * @throws SiteWhereException
     */
    List<IDeviceEvent> executeQuery(String query) throws SiteWhereException;

    /**
     * Execute a query, returning a raw response from the provider.
     * 
     * @param query
     * @return
     * @throws SiteWhereException
     */
    JsonNode executeQueryWithRawResponse(String query) throws SiteWhereException;

    /**
     * Get a list of device locations near the given lat/long in the given time
     * period.
     * 
     * @param latitude
     * @param longitude
     * @param distance
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    List<IDeviceLocation> getLocationsNear(double latitude, double longitude, double distance,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;
}