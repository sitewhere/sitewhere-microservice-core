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

import com.sitewhere.microservice.grpc.GrpcKeys;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

/**
 * GRPC interceptor that pushes tenant token into GRPC call metadata.
 */
public class TenantTokenClientInterceptor implements ClientInterceptor {

    /*
     * (non-Javadoc)
     * 
     * @see io.grpc.ClientInterceptor#interceptCall(io.grpc.MethodDescriptor,
     * io.grpc.CallOptions, io.grpc.Channel)
     */
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
	    CallOptions callOptions, Channel next) {
	return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

	    /*
	     * (non-Javadoc)
	     * 
	     * @see io.grpc.ForwardingClientCall#start(io.grpc.ClientCall.Listener,
	     * io.grpc.Metadata)
	     */
	    @Override
	    public void start(Listener<RespT> responseListener, Metadata headers) {
		SiteWhereAuthentication authentication = UserContext.getCurrentUser();
		if (authentication != null) {
		    String tenantToken = authentication.getTenantToken();
		    if (tenantToken != null) {
			headers.put(GrpcKeys.TENANT_KEY, tenantToken);
		    }
		}
		super.start(responseListener, headers);
	    }
	};
    }
}