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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.AbstractMessageLite;
import com.sitewhere.grpc.client.common.security.NotAuthorizedException;
import com.sitewhere.grpc.client.common.security.UnauthenticatedException;
import com.sitewhere.grpc.client.common.tracing.DebugParameter;
import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.ServiceNotAvailableException;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class GrpcUtils {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(GrpcUtils.class);

    public static void handleClientMethodEntry(IApiChannel<?> channel, MethodDescriptor<?, ?> method,
	    DebugParameter... parameters) {
	LOGGER.debug(channel.getClass().getSimpleName() + " connected to '" + channel.getFunctionIdentifier().getPath()
		+ "' sending call to  " + method.getFullMethodName() + ".");
	if (LOGGER.isTraceEnabled()) {
	    for (DebugParameter parameter : parameters) {
		if (parameter.getContent() instanceof String) {
		    LOGGER.trace(parameter.getName() + ":" + parameter.getContent());
		} else {
		    LOGGER.trace(parameter.getName() + ":\n\n"
			    + MarshalUtils.marshalJsonAsPrettyString(parameter.getContent()));
		}
	    }
	}
    }

    /**
     * Log the encoded GRPC request sent from client.
     * 
     * @param request
     * @return
     */
    public static <T> T logGrpcClientRequest(MethodDescriptor<?, ?> method, T request) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace(
		    "Encoded GRPC request being sent to " + method.getFullMethodName() + ":\n\n" + request.toString());
	}
	return request;
    }

    /**
     * Handle entry logic for a gRPC server method.
     * 
     * @param api
     * @param method
     */
    public static void handleServerMethodEntry(IGrpcApiImplementation api, MethodDescriptor<?, ?> method) {
	LOGGER.debug("Server received call to  " + method.getFullMethodName() + ".");
    }

    public static void logServerApiResult(MethodDescriptor<?, ?> method, Object result) throws SiteWhereException {
	if (result != null) {
	    if (LOGGER.isTraceEnabled()) {
		LOGGER.trace("API result for " + method.getFullMethodName() + ":\n\n"
			+ MarshalUtils.marshalJsonAsPrettyString(result));
	    }
	} else {
	    LOGGER.trace("Response to " + method.getFullMethodName() + " was NULL");
	}
    }

    public static void handleServerMethodExit(MethodDescriptor<?, ?> method) {
	LOGGER.debug("Server finished call to  " + method.getFullMethodName() + ".");
	UserContext.clearContext();
    }

    /**
     * Log response returned by client method invocation.
     * 
     * @param method
     * @param o
     * @throws SiteWhereException
     */
    public static void logClientMethodResponse(MethodDescriptor<?, ?> method, Object o) throws SiteWhereException {
	if (o != null) {
	    if (LOGGER.isTraceEnabled()) {
		LOGGER.trace("Response to " + method.getFullMethodName() + ":\n\n"
			+ MarshalUtils.marshalJsonAsPrettyString(o));
	    }
	} else {
	    LOGGER.trace("Response to " + method.getFullMethodName() + " was NULL");
	}
    }

    /**
     * Handle an exception encountered during a call from a GRPC client.
     * 
     * @param method
     * @param t
     * @return
     */
    public static SiteWhereException handleClientMethodException(MethodDescriptor<?, ?> method, Throwable t) {
	if (t instanceof StatusRuntimeException) {
	    StatusRuntimeException sre = (StatusRuntimeException) t;
	    switch (sre.getStatus().getCode()) {
	    case PERMISSION_DENIED: {
		return new NotAuthorizedException("Not authorized for operation.", sre);
	    }
	    case UNAUTHENTICATED: {
		return new UnauthenticatedException(sre.getStatus().getDescription(), sre);
	    }
	    case UNAVAILABLE: {
		return new ServiceNotAvailableException(
			String.format("The requested service is not available [%s]", sre.getMessage()), sre);
	    }
	    case FAILED_PRECONDITION: {
		String delimited = sre.getStatus().getDescription();
		String[] parts = delimited.split(":");
		ErrorCode code = ErrorCode.fromCode(Long.parseLong(parts[0]));
		if (ErrorCode.Error == code) {
		    return new SiteWhereException(parts[1]);
		} else {
		    return new SiteWhereSystemException(code, ErrorLevel.ERROR);
		}
	    }
	    default: {
	    }
	    }
	}
	LOGGER.error(String.format("Unhandled client exception while calling '%s'.", method.getFullMethodName()), t);
	return new SiteWhereException("Client exception in call to " + method.getFullMethodName() + ".", t);
    }

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

    /**
     * Marshal a gRPC message to a byte[].
     * 
     * @param message
     * @return
     * @throws SiteWhereException
     */
    public static byte[] marshal(AbstractMessageLite<?, ?> message) throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    message.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to marshal gRPC message.", e);
	} finally {
	    CommonModelConverter.closeQuietly(output);
	}
    }
}