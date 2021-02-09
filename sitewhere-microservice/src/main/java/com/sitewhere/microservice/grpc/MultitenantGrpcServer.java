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
package com.sitewhere.microservice.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

/**
 * Base class for GRPC servers that handle requests for multiple tenants.
 */
public class MultitenantGrpcServer extends GrpcServer {

    /** Interceptor for tenant token */
    private TenantTokenServerInterceptor tenantTokenInterceptor;

    public MultitenantGrpcServer(BindableService serviceImplementation, int apiPort, int healthPort) {
	super(serviceImplementation, apiPort, healthPort);
    }

    /**
     * Build server component based on configuration.
     * 
     * @return
     */
    protected Server buildServer() {
	this.tenantTokenInterceptor = new TenantTokenServerInterceptor(getMicroservice());
	NettyServerBuilder builder = NettyServerBuilder.forPort(getApiPort());
	builder.addService(getServiceImplementation()).intercept(getTenantTokenInterceptor())
		.intercept(getJwtInterceptor());
	return builder.build();
    }

    protected TenantTokenServerInterceptor getTenantTokenInterceptor() {
	return tenantTokenInterceptor;
    }

    protected void setTenantTokenInterceptor(TenantTokenServerInterceptor tenantTokenInterceptor) {
	this.tenantTokenInterceptor = tenantTokenInterceptor;
    }
}