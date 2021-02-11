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

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.device.DeviceModelConverter;
import com.sitewhere.grpc.model.DeviceModel.GArea;
import com.sitewhere.grpc.model.DeviceModel.GDevice;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignment;
import com.sitewhere.grpc.model.DeviceModel.GDeviceType;
import com.sitewhere.microservice.cache.RedisCacheProvider;
import com.sitewhere.spi.SiteWhereException;
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
    public static class AreaByTokenCache extends RedisCacheProvider<String, IArea> {

	public AreaByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, AREA_BY_TOKEN, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(String key) throws SiteWhereException {
	    return key;
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IArea value) throws SiteWhereException {
	    GArea message = DeviceModelConverter.asGrpcArea(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IArea deserialize(byte[] value) throws SiteWhereException {
	    try {
		return DeviceModelConverter.asApiArea(GArea.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }

    /**
     * Cache for areas by id.
     */
    public static class AreaByIdCache extends RedisCacheProvider<UUID, IArea> {

	public AreaByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, AREA_BY_ID, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(UUID key) throws SiteWhereException {
	    return key.toString();
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IArea value) throws SiteWhereException {
	    GArea message = DeviceModelConverter.asGrpcArea(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IArea deserialize(byte[] value) throws SiteWhereException {
	    try {
		return DeviceModelConverter.asApiArea(GArea.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }

    /**
     * Cache for device types.
     */
    public static class DeviceTypeByTokenCache extends RedisCacheProvider<String, IDeviceType> {

	public DeviceTypeByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_TYPE_BY_TOKEN, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(String key) throws SiteWhereException {
	    return key;
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IDeviceType value) throws SiteWhereException {
	    GDeviceType message = DeviceModelConverter.asGrpcDeviceType(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IDeviceType deserialize(byte[] value) throws SiteWhereException {
	    try {
		return DeviceModelConverter.asApiDeviceType(GDeviceType.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }

    /**
     * Cache for device types by id.
     */
    public static class DeviceTypeByIdCache extends RedisCacheProvider<UUID, IDeviceType> {

	public DeviceTypeByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_TYPE_BY_ID, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(UUID key) throws SiteWhereException {
	    return key.toString();
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IDeviceType value) throws SiteWhereException {
	    GDeviceType message = DeviceModelConverter.asGrpcDeviceType(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IDeviceType deserialize(byte[] value) throws SiteWhereException {
	    try {
		return DeviceModelConverter.asApiDeviceType(GDeviceType.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }

    /**
     * Cache for devices by token.
     */
    public static class DeviceByTokenCache extends RedisCacheProvider<String, IDevice> {

	public DeviceByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_BY_TOKEN, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(String key) throws SiteWhereException {
	    return key;
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IDevice value) throws SiteWhereException {
	    GDevice message = DeviceModelConverter.asGrpcDevice(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IDevice deserialize(byte[] value) throws SiteWhereException {
	    try {
		return DeviceModelConverter.asApiDevice(GDevice.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }

    /**
     * Cache for devices by id.
     */
    public static class DeviceByIdCache extends RedisCacheProvider<UUID, IDevice> {

	public DeviceByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_BY_ID, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(UUID key) throws SiteWhereException {
	    return key.toString();
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IDevice value) throws SiteWhereException {
	    GDevice message = DeviceModelConverter.asGrpcDevice(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IDevice deserialize(byte[] value) throws SiteWhereException {
	    try {
		return DeviceModelConverter.asApiDevice(GDevice.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }

    /**
     * Cache for device assignments by token.
     */
    public static class DeviceAssignmentByTokenCache extends RedisCacheProvider<String, IDeviceAssignment> {

	public DeviceAssignmentByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_ASSIGNMENT_BY_TOKEN, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(String key) throws SiteWhereException {
	    return key;
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IDeviceAssignment value) throws SiteWhereException {
	    GDeviceAssignment message = DeviceModelConverter.asGrpcDeviceAssignment(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IDeviceAssignment deserialize(byte[] value) throws SiteWhereException {
	    try {
		return DeviceModelConverter.asApiDeviceAssignment(GDeviceAssignment.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }

    /**
     * Cache for device assignments by id.
     */
    public static class DeviceAssignmentByIdCache extends RedisCacheProvider<UUID, IDeviceAssignment> {

	public DeviceAssignmentByIdCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, DEVICE_ASSIGNMENT_BY_ID, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(UUID key) throws SiteWhereException {
	    return key.toString();
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IDeviceAssignment value) throws SiteWhereException {
	    GDeviceAssignment message = DeviceModelConverter.asGrpcDeviceAssignment(value);
	    return GrpcUtils.marshal(message);
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#deserialize(byte[])
	 */
	@Override
	public IDeviceAssignment deserialize(byte[] value) throws SiteWhereException {
	    try {
		return DeviceModelConverter.asApiDeviceAssignment(GDeviceAssignment.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }
}