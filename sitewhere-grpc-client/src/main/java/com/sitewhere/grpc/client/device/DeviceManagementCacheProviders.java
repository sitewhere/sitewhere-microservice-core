/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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