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
package com.sitewhere.grpc.client.asset;

import com.sitewhere.grpc.client.MultitenantGrpcChannel;
import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementBlockingStub;
import com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementStub;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IInstanceSettings;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;

/**
 * Channel that allows for communication with a remote asset management GRPC
 * server.
 */
public class AssetManagementGrpcChannel
	extends MultitenantGrpcChannel<AssetManagementBlockingStub, AssetManagementStub> {

    public AssetManagementGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	super(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public AssetManagementBlockingStub createBlockingStub() {
	return AssetManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public AssetManagementStub createAsyncStub() {
	return AssetManagementGrpc.newStub(getChannel());
    }
}
