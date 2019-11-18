/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import io.grpc.Context;
import io.grpc.Metadata;

public class GrpcKeys {

    /** JWT metadata key */
    public static final Metadata.Key<String> JWT_KEY = Metadata.Key.of("jwt", Metadata.ASCII_STRING_MARSHALLER);

    /** Tenant metadata key */
    public static final Metadata.Key<String> TENANT_KEY = Metadata.Key.of("tenant", Metadata.ASCII_STRING_MARSHALLER);

    /** Key for accessing JWT */
    public static final Context.Key<String> JWT_CONTEXT_KEY = Context.key("jwt");

    /** Key for accessing requested tenant token */
    public static final Context.Key<String> TENANT_CONTEXT_KEY = Context.key("tenant");
}
