/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;

import io.grpc.BindableService;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 * Interceptor that enforces JWT authentication constraints before invoking
 * service methods.
 */
public class JwtServerInterceptor implements ServerInterceptor {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(JwtServerInterceptor.class);

    /** Parent microservice */
    private IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice;

    /** Service implementation */
    private Class<? extends BindableService> implementation;

    public JwtServerInterceptor(
	    IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice,
	    Class<? extends BindableService> implementation) {
	this.microservice = microservice;
	this.implementation = implementation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.grpc.ServerInterceptor#interceptCall(io.grpc.ServerCall,
     * io.grpc.Metadata, io.grpc.ServerCallHandler)
     */
    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
	    ServerCallHandler<ReqT, RespT> next) {
	if (headers.containsKey(GrpcKeys.JWT_KEY)) {
	    String jwt = headers.get(GrpcKeys.JWT_KEY);
	    LOGGER.trace("Server received jwt key: " + jwt);
	    Context ctx = Context.current().withValue(GrpcKeys.JWT_CONTEXT_KEY, jwt);
	    return Contexts.interceptCall(ctx, call, headers, next);
	} else {
	    call.close(Status.UNAUTHENTICATED.withDescription("JWT not passed in metadata."), headers);
	    return new ServerCall.Listener<ReqT>() {
	    };
	}
    }

    protected IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> getMicroservice() {
	return microservice;
    }

    protected Class<? extends BindableService> getImplementation() {
	return implementation;
    }
}