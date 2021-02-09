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
package com.sitewhere.microservice.api.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Configurable helper class that allows {@link DeviceType} model objects to be
 * created from {@link IDeviceType} SPI objects.
 */
public class DeviceTypeMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceTypeMarshalHelper.class);

    /** Device Management */
    private IDeviceManagement deviceManagement;

    public DeviceTypeMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert a device type for marshaling.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public DeviceType convert(IDeviceType source) throws SiteWhereException {
	DeviceType deviceType = new DeviceType();
	deviceType.setName(source.getName());
	deviceType.setDescription(source.getDescription());
	deviceType.setContainerPolicy(source.getContainerPolicy());
	deviceType.setDeviceElementSchemaId(source.getDeviceElementSchemaId());
	BrandedEntity.copy(source, deviceType);
	return deviceType;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}