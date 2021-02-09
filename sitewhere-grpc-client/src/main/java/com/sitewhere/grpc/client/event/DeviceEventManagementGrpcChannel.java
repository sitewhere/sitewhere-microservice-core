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
package com.sitewhere.grpc.client.event;

import com.sitewhere.grpc.client.MultitenantGrpcChannel;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc.DeviceEventManagementBlockingStub;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc.DeviceEventManagementStub;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Channel that allows for communication with a remote device event management
 * GRPC server.
 */
public class DeviceEventManagementGrpcChannel
	extends MultitenantGrpcChannel<DeviceEventManagementBlockingStub, DeviceEventManagementStub> {

    public DeviceEventManagementGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	super(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public DeviceEventManagementBlockingStub createBlockingStub() {
	return DeviceEventManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public DeviceEventManagementStub createAsyncStub() {
	return DeviceEventManagementGrpc.newStub(getChannel());
    }
}