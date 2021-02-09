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

import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Base class for channels that uses SiteWhere APIs to communicate with GRPC
 * services.
 *
 * @param <T>
 */
public abstract class ApiChannel<T extends GrpcChannel<?, ?>> extends TenantEngineLifecycleComponent
	implements IApiChannel<T> {

    /** Instance settings */
    private IInstanceSettings settings;

    /** Function identifier */
    private IFunctionIdentifier functionIdentifier;

    /** gRPC service identifier */
    private IGrpcServiceIdentifier grpcServiceIdentifier;

    /** Binding port */
    private int port;

    /** Underlying GRPC channel */
    private T grpcChannel;

    public ApiChannel(IInstanceSettings settings, IFunctionIdentifier functionIdentifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	this.settings = settings;
	this.functionIdentifier = functionIdentifier;
	this.grpcServiceIdentifier = grpcServiceIdentifier;
	this.port = port;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiChannel#getGrpcChannel()
     */
    @Override
    public T getGrpcChannel() {
	return grpcChannel;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.grpcChannel = createGrpcChannel(getSettings(), getFunctionIdentifier(), getGrpcServiceIdentifier(),
		getPort());
	getLogger().info(String.format("Initializing gRPC channel for %s to '%s:%d'", getGrpcServiceIdentifier(),
		getGrpcChannel().getHostname(), getGrpcChannel().getPort()));
	initializeNestedComponent(getGrpcChannel(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info(String.format("Starting gRPC channel to '%s:%d'", getGrpcChannel().getHostname(),
		getGrpcChannel().getPort()));
	startNestedComponent(getGrpcChannel(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info(String.format("Stopping gRPC channel to '%s:%d'", getGrpcChannel().getHostname(),
		getGrpcChannel().getPort()));
	stopNestedComponent(getGrpcChannel(), monitor);
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#getFunctionIdentifier()
     */
    @Override
    public IFunctionIdentifier getFunctionIdentifier() {
	return functionIdentifier;
    }

    public void setFunctionIdentifier(IFunctionIdentifier functionIdentifier) {
	this.functionIdentifier = functionIdentifier;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#getGrpcServiceIdentifier()
     */
    @Override
    public IGrpcServiceIdentifier getGrpcServiceIdentifier() {
	return grpcServiceIdentifier;
    }

    public void setGrpcServiceIdentifier(IGrpcServiceIdentifier grpcServiceIdentifier) {
	this.grpcServiceIdentifier = grpcServiceIdentifier;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#getPort()
     */
    @Override
    public int getPort() {
	return port;
    }

    protected void setPort(int port) {
	this.port = port;
    }

    protected IInstanceSettings getSettings() {
	return settings;
    }

    protected void setSettings(IInstanceSettings settings) {
	this.settings = settings;
    }
}