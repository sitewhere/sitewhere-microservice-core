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

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.device.streaming.IDeviceStreamData;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages creation of {@link IDeviceStream} entities based on requests from
 * devices.
 */
public interface IDeviceStreamManager extends ITenantEngineLifecycleComponent {

    /**
     * Handle request for creating a new {@link IDeviceStream}.
     * 
     * @param deviceToken
     * @param request
     * @throws SiteWhereException
     */
    void handleDeviceStreamRequest(String deviceToken, IDeviceStreamCreateRequest request) throws SiteWhereException;

    /**
     * Handle request for creating new {@link IDeviceStreamData}.
     * 
     * @param deviceToken
     * @param request
     * @throws SiteWhereException
     */
    void handleDeviceStreamDataRequest(String deviceToken, IDeviceStreamDataCreateRequest request)
	    throws SiteWhereException;

    /**
     * Handle request for sending data from an {@link IDeviceStream} to a remote
     * device.
     * 
     * @param deviceToken
     * @param request
     * @throws SiteWhereException
     */
    void handleSendDeviceStreamDataRequest(String deviceToken, ISendDeviceStreamDataRequest request)
	    throws SiteWhereException;
}