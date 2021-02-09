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
