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
package com.sitewhere.grpc.client.spi;

import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IInstanceSettings;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Common interface for GRPC channels that handle API calls.
 */
public interface IApiChannel<T extends GrpcChannel<?, ?>> extends ITenantEngineLifecycleComponent {

    /**
     * Get function identifier.
     * 
     * @return
     */
    public IFunctionIdentifier getFunctionIdentifier();

    /**
     * Get gRPC service identifier.
     * 
     * @return
     */
    public IGrpcServiceIdentifier getGrpcServiceIdentifier();

    /**
     * Get target port.
     * 
     * @return
     */
    public int getPort();

    /**
     * Create underlying GRPC channel.
     * 
     * @param settings
     * @param identifier
     * @param grpcServiceIdentifier
     * @param port
     * @return
     */
    public T createGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port);

    /**
     * Get underlying GRPC channel.
     * 
     * @return
     */
    public T getGrpcChannel();
}