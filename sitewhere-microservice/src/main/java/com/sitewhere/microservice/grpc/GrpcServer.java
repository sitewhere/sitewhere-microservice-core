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

import java.io.IOException;

import com.sitewhere.microservice.health.HealthServiceImpl;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.grpc.IGrpcServer;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

/**
 * Base class for GRPC servers used by microservices.
 */
public class GrpcServer extends TenantEngineLifecycleComponent implements IGrpcServer {

    /** Port for gRPC server */
    private int apiPort;

    /** Port for gRPC Health Protocol. */
    private int healthPort;

    /** Wrapped GRPC server */
    private Server server;

    /** Wrapped gRPC Health server */
    private Server healthServer;

    /** Service implementation */
    private BindableService serviceImplementation;

    /** Interceptor for JWT authentication */
    private JwtServerInterceptor jwtInterceptor;

    /** Health Service Implementation */
    private HealthServiceImpl healthService = new HealthServiceImpl();

    public GrpcServer(BindableService serviceImplementation, int apiPort, int healthPort) {
	this.serviceImplementation = serviceImplementation;
	this.apiPort = apiPort;
	this.healthPort = healthPort;
    }

    /**
     * Build server component based on configuration.
     * 
     * @return
     */
    protected Server buildServer() {
	NettyServerBuilder builder = NettyServerBuilder.forPort(getApiPort());
	builder.addService(getServiceImplementation()).intercept(getJwtInterceptor());
	return builder.build();
    }

    /**
     * Build gRPC Health Server.
     * 
     * @return
     */
    protected Server buildHealthServer() {
	NettyServerBuilder builder = NettyServerBuilder.forPort(getHealthPort());
	builder.addService(getHealthService());
	return builder.build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    this.jwtInterceptor = new JwtServerInterceptor(getMicroservice(), getServiceImplementation().getClass());
	    this.server = buildServer();
	    getLogger().info("Initialized gRPC API server on port " + getApiPort() + ".");
	    this.healthServer = buildHealthServer();
	    getLogger().info("Initialized gRPC Health Probe server on port " + getHealthPort() + ".");
	} catch (Throwable t) {
	    getLogger().error("Unhandled exception initializing gRPC server.", t);
	}
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
	try {
	    getLogger().debug("Starting gRPC API server on port " + getApiPort() + "...");
	    getServer().start();
	    getLogger().info("Started gRPC API server on port " + getApiPort() + ".");

	    getLogger().debug("Starting gRPC Health Probe server on port " + getHealthPort() + "...");
	    getHealthServer().start();
	    getLogger().info("Started gRPC Health Probe server on port " + getHealthPort() + ".");
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to start gRPC server.", e);
	} catch (Throwable t) {
	    getLogger().error("Unhandled exception starting gRPC server.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getServer() != null) {
	    try {
		getServer().shutdown();
		getServer().awaitTermination();
		getLogger().info("gRPC server terminated successfully.");

		getHealthServer().shutdown();
		getHealthServer().awaitTermination();
		getLogger().info("gRPC Health server terminated successfully.");
	    } catch (InterruptedException e) {
		getLogger().error("Interrupted while waiting for gRPC server to terminate.", e);
	    } catch (Throwable t) {
		getLogger().error("Unhandled exception stopping gRPC server.", t);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.grpc.IGrpcServer#getServiceImplementation()
     */
    @Override
    public BindableService getServiceImplementation() {
	return serviceImplementation;
    }

    public void setServiceImplementation(BindableService serviceImplementation) {
	this.serviceImplementation = serviceImplementation;
    }

    public Server getServer() {
	return server;
    }

    public void setServer(Server server) {
	this.server = server;
    }

    public Server getHealthServer() {
	return healthServer;
    }

    public void setHealthServer(Server healthServer) {
	this.healthServer = healthServer;
    }

    public int getApiPort() {
	return apiPort;
    }

    public void setApiPort(int apiPort) {
	this.apiPort = apiPort;
    }

    public int getHealthPort() {
	return healthPort;
    }

    public void setHealthPort(int healthPort) {
	this.healthPort = healthPort;
    }

    public JwtServerInterceptor getJwtInterceptor() {
	return jwtInterceptor;
    }

    public void setJwtInterceptor(JwtServerInterceptor jwtInterceptor) {
	this.jwtInterceptor = jwtInterceptor;
    }

    public HealthServiceImpl getHealthService() {
	return healthService;
    }

    public void setHealthService(HealthServiceImpl healthService) {
	this.healthService = healthService;
    }
}