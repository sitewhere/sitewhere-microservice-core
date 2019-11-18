/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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