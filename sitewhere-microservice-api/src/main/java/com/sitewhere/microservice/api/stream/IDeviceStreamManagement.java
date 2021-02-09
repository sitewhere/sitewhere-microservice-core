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
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Interface for device stream management operations.
 */
public interface IDeviceStreamManagement {

    /**
     * Create a new {@link IDeviceStream} associated with an assignment.
     * 
     * @param assignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IDeviceStream createDeviceStream(UUID assignmentId, IDeviceStreamCreateRequest request) throws SiteWhereException;

    /**
     * Get an existing device stream by id.
     * 
     * @param streamId
     * @return
     * @throws SiteWhereException
     */
    IDeviceStream getDeviceStream(UUID streamId) throws SiteWhereException;

    /**
     * List device streams for the assignment that meet the given criteria.
     * 
     * @param assignmentId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<IDeviceStream> listDeviceStreams(UUID assignmentId, ISearchCriteria criteria)
	    throws SiteWhereException;
}
