/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;

public class GrpcUtils {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(GrpcUtils.class);

    /**
     * Handle server exception by logging it, then converting to a format that can
     * be passed back across the wire to a client.
     * 
     * @param method
     * @param t
     * @param observer
     */
    public static void handleServerMethodException(MethodDescriptor<?, ?> method, Throwable t,
	    StreamObserver<?> observer) {
	LOGGER.error("Server exception in call to " + method.getFullMethodName() + ".", t);
	Throwable thrown = convertServerException(t);
	observer.onError(thrown);
    }

    /**
     * Convert server exception to one that can be passed back via GRPC.
     * 
     * @param t
     * @return
     */
    public static StatusException convertServerException(Throwable t) {
	StatusException thrown = null;
	if (t instanceof SiteWhereSystemException) {
	    SiteWhereSystemException sysex = (SiteWhereSystemException) t;
	    Status status = Status.fromCode(Code.FAILED_PRECONDITION)
		    .withDescription(sysex.getCode().getCode() + ":" + sysex.getCode().getMessage());
	    thrown = status.asException();
	} else if (t instanceof TenantEngineNotAvailableException) {
	    TenantEngineNotAvailableException sw = (TenantEngineNotAvailableException) t;
	    Status status = Status.fromCode(Code.UNAVAILABLE).withDescription(sw.getMessage());
	    thrown = status.asException();
	} else if (t instanceof SiteWhereException) {
	    SiteWhereException sw = (SiteWhereException) t;
	    Status status = Status.fromCode(Code.FAILED_PRECONDITION)
		    .withDescription(ErrorCode.Error.getCode() + ":" + sw.getMessage());
	    thrown = status.asException();
	} else {
	    thrown = Status.fromThrowable(t).asException();
	}
	return thrown;
    }
}
