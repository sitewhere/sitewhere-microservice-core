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
package com.sitewhere.grpc.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sitewhere.grpc.client.spi.multitenant.IMultitenantGrpcChannel;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

import io.grpc.ManagedChannelBuilder;

/**
 * Management wrapper for a GRPC channel that handles requests for multiple
 * tenants.
 *
 * @param <B>
 * @param <A>
 */
public abstract class MultitenantGrpcChannel<B, A> extends GrpcChannel<B, A> implements IMultitenantGrpcChannel<B, A> {

    /** Max threads used for executing GPRC requests */
    private static final int THREAD_POOL_SIZE = 25;

    /** Client interceptor for adding tenant token */
    private TenantTokenClientInterceptor tenantTokenInterceptor = new TenantTokenClientInterceptor();

    /** Executor service used to handle GRPC requests */
    private ExecutorService serverExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public MultitenantGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	super(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(getHostname(), getPort());
	builder.defaultServiceConfig(buildServiceConfiguration()).enableRetry();
	builder.executor(getServerExecutor());
	builder.usePlaintext().intercept(getTenantTokenInterceptor()).intercept(getJwtInterceptor());
	this.channel = builder.build();
	this.blockingStub = createBlockingStub();
	this.asyncStub = createAsyncStub();
	getLogger()
		.info(String.format("Creating gRPC client channel connected to %s:%d ...", getHostname(), getPort()));
    }

    protected TenantTokenClientInterceptor getTenantTokenInterceptor() {
	return tenantTokenInterceptor;
    }

    protected void setTenantTokenInterceptor(TenantTokenClientInterceptor tenantTokenInterceptor) {
	this.tenantTokenInterceptor = tenantTokenInterceptor;
    }

    public ExecutorService getServerExecutor() {
	return serverExecutor;
    }

    public void setServerExecutor(ExecutorService serverExecutor) {
	this.serverExecutor = serverExecutor;
    }
}