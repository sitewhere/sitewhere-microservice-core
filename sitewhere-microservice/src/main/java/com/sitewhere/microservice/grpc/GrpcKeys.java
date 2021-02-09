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
