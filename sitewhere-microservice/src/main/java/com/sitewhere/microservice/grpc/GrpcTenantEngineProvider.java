/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.grpc.ITenantEngineCallback;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;

/**
 * Uses context tenant token and JWT to execute a call in the user context
 * passed in the JWT using the tenant engine specified by the token.
 * 
 * @param <T>
 */
public class GrpcTenantEngineProvider<T extends IMicroserviceTenantEngine<?>> {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(GrpcTenantEngineProvider.class);

    /** Microservice */
    private IMultitenantMicroservice<?, ?, T> microservice;

    public GrpcTenantEngineProvider(IMultitenantMicroservice<?, ?, T> microservice) {
	this.microservice = microservice;
    }

    /**
     * Execute callback in context of a tenant engine.
     * 
     * @param callback
     */
    public void executeInTenantEngine(ITenantEngineCallback<T> callback, StreamObserver<?> observer) {
	SiteWhereAuthentication previous = UserContext.getCurrentUser();
	try {
	    // Get JWT and tenant token from context.
	    String jwt = GrpcKeys.JWT_CONTEXT_KEY.get();
	    String tenantToken = GrpcKeys.TENANT_CONTEXT_KEY.get();

	    // Parse username and granted authorities.
	    Claims claims = getMicroservice().getTokenManagement().getClaimsForToken(jwt);
	    String username = getMicroservice().getTokenManagement().getUsernameFromClaims(claims);
	    List<String> auths = getMicroservice().getTokenManagement().getGrantedAuthoritiesFromClaims(claims);

	    // Set user context.
	    SiteWhereAuthentication auth = new SiteWhereAuthentication(username, auths, jwt);
	    UserContext.setContext(auth);
	    LOGGER.info(String.format("Executing gRPC call in context of user '%s'.", username));

	    // Execute callback on tenant engine within user context.
	    T engine = getMicroservice().assureTenantEngineAvailable(tenantToken);
	    callback.executeInTenantEngine(engine);
	} catch (SiteWhereException e) {
	    LOGGER.error("Unable to execute tenant engine method.", e);
	    observer.onError(GrpcUtils.convertServerException(e));
	} finally {
	    UserContext.setContext(previous);
	}
    }

    protected IMultitenantMicroservice<?, ?, T> getMicroservice() {
	return microservice;
    }
}
