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

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 * Pulls tenant token from call metadata and adds it to context for use in
 * implementation class.
 */
public class TenantTokenServerInterceptor implements ServerInterceptor {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(TenantTokenServerInterceptor.class);

    /** Parent microservice */
    private IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice;

    public TenantTokenServerInterceptor(
	    IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice) {
	this.microservice = microservice;
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
	if (headers.containsKey(GrpcKeys.TENANT_KEY)) {
	    String tenantToken = headers.get(GrpcKeys.TENANT_KEY);
	    LOGGER.trace("Server received tenant token key: " + tenantToken);
	    Context ctx = Context.current().withValue(GrpcKeys.TENANT_CONTEXT_KEY, tenantToken);
	    return Contexts.interceptCall(ctx, call, headers, next);
	} else {
	    call.close(Status.UNAUTHENTICATED.withDescription("Tenant token not passed in metadata."), headers);
	    return new ServerCall.Listener<ReqT>() {
	    };
	}
    }

    protected IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> getMicroservice() {
	return microservice;
    }
}