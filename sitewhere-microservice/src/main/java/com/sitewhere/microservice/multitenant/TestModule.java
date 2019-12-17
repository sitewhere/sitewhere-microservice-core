/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.UUID;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceType;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    IDevice provideDevice(IDeviceType deviceType) {
	Device device = new Device();
	device.setToken("MY-9384234-DEVICE");
	device.setId(UUID.randomUUID());
	device.setDeviceTypeId(deviceType.getId());
	return device;
    }

    @Provides
    IDeviceType provideDeviceType() {
	DeviceType type = new DeviceType();
	type.setToken("buzzer");
	type.setId(UUID.randomUUID());
	return type;
    }
}
