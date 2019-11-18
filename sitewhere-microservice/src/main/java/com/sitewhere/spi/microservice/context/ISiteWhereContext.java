/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.context;

import java.util.List;

import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;

/**
 * Holds SiteWhere information associated with a reqeust.
 */
public interface ISiteWhereContext {

    /**
     * Get current assignment for device associated with the request.
     * 
     * @return
     */
    IDeviceAssignment getDeviceAssignment();

    /**
     * Get a list of device measurements that have not been persisted.
     * 
     * @return
     */
    List<IDeviceMeasurementCreateRequest> getUnsavedDeviceMeasurements();

    /**
     * Get a list of device locations that have not been persisted.
     * 
     * @return
     */
    List<IDeviceLocationCreateRequest> getUnsavedDeviceLocations();

    /**
     * Get a list of device alerts that have not been persisted.
     * 
     * @return
     */
    List<IDeviceAlertCreateRequest> getUnsavedDeviceAlerts();

    /**
     * Get the {@link IDeviceMeasurement} events.
     * 
     * @return
     */
    List<IDeviceMeasurement> getDeviceMeasurements();

    /**
     * Get the {@link IDeviceLocation} events.
     * 
     * @return
     */
    List<IDeviceLocation> getDeviceLocations();

    /**
     * Get the {@link IDeviceAlert} events.
     * 
     * @return
     */
    List<IDeviceAlert> getDeviceAlerts();

    /**
     * Get the {@link IDeviceCommandInvocation} events.
     * 
     * @return
     */
    List<IDeviceCommandInvocation> getDeviceCommandInvocations();

    /**
     * Get the {@link IDeviceCommandResponse} events.
     * 
     * @return
     */
    List<IDeviceCommandResponse> getDeviceCommandResponses();

    /**
     * Get information for replying to originator.
     * 
     * @return
     */
    String getReplyTo();
}
