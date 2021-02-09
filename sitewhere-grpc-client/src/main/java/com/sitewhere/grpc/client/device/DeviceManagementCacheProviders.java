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
package com.sitewhere.grpc.client.device;

import java.util.UUID;

import com.sitewhere.microservice.cache.RedissonCacheProvider;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.cache.ICacheConfiguration;

/**
 * Cache providers for device management entities.
 */
public class DeviceManagementCacheProviders {

    public static final String AREA_BY_TOKEN = "area_by_token";
    public static final String AREA_BY_ID = "area_by_id";
    public static final String DEVICE_BY_TOKEN = "device_by_token";
    public static final String DEVICE_BY_ID = "device_by_id";
    public static final String DEVICE_ASSIGNMENT_BY_TOKEN = "device_assignment_by_token";
    public static final String DEVICE_ASSIGNMENT_BY_ID = "device_assignment_by_id";
    public static final String DEVICE_TYPE_BY_TOKEN = "device_type_by_token";
    public static final String DEVICE_TYPE_BY_ID = "device_type_by_id";

    /**
     * Cache for areas.
     */
    public static class AreaByTokenCache extends RedissonCacheProvider<String, IArea> {

	public AreaByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, AREA_BY_TOKEN, String.class, IArea.class, configuration);
	}
    }

    /**
     * Cache for areas by id.
     */
    public static class AreaByIdCache extends RedissonCacheProvider<UUID, IArea> {

	public AreaByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, AREA_BY_ID, UUID.class, IArea.class, configuration);
	}
    }

    /**
     * Cache for device types.
     */
    public static class DeviceTypeByTokenCache extends RedissonCacheProvider<String, IDeviceType> {

	public DeviceTypeByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_TYPE_BY_TOKEN, String.class, IDeviceType.class, configuration);
	}
    }

    /**
     * Cache for device types by id.
     */
    public static class DeviceTypeByIdCache extends RedissonCacheProvider<UUID, IDeviceType> {

	public DeviceTypeByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_TYPE_BY_ID, UUID.class, IDeviceType.class, configuration);
	}
    }

    /**
     * Cache for devices by token.
     */
    public static class DeviceByTokenCache extends RedissonCacheProvider<String, IDevice> {

	public DeviceByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_BY_TOKEN, String.class, IDevice.class, configuration);
	}
    }

    /**
     * Cache for devices by id.
     */
    public static class DeviceByIdCache extends RedissonCacheProvider<UUID, IDevice> {

	public DeviceByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_BY_ID, UUID.class, IDevice.class, configuration);
	}
    }

    /**
     * Cache for device assignments by token.
     */
    public static class DeviceAssignmentByTokenCache extends RedissonCacheProvider<String, IDeviceAssignment> {

	public DeviceAssignmentByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_ASSIGNMENT_BY_TOKEN, String.class, IDeviceAssignment.class, configuration);
	}
    }

    /**
     * Cache for device assignments by id.
     */
    public static class DeviceAssignmentByIdCache extends RedissonCacheProvider<UUID, IDeviceAssignment> {

	public DeviceAssignmentByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_ASSIGNMENT_BY_ID, UUID.class, IDeviceAssignment.class, configuration);
	}
    }
}