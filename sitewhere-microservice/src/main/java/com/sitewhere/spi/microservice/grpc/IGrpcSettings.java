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
package com.sitewhere.spi.microservice.grpc;

/**
 * Default settings used for gRPC configuration.
 */
public interface IGrpcSettings {

    /** Default gRPC API Port */
    public static final int DEFAULT_API_PORT = 9000;

    /** Default gRPC Management Port */
    public static final int DEFAULT_MANAGEMENT_PORT = 9001;

    /** Default gRPC API Health Protocol Port */
    public static final int DEFAULT_API_HEALTH_PORT = 9002;

    /** Default gRPC Management Health Protocol Port */
    public static final int DEFAULT_MANAGEMENT_HEALTH_PORT = 9003;

    /** User management API port for instance management microservice */
    public static final int USER_MANAGEMENT_API_PORT = 9004;

    /** User management API port for instance management microservice */
    public static final int USER_MANAGEMENT_API_HEALTH_PORT = 9006;
}
