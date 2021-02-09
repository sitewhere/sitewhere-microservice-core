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
