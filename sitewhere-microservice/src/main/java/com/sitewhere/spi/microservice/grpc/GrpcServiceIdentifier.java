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
 * Enumerates gRPC services offered by microservices.
 */
public enum GrpcServiceIdentifier implements IGrpcServiceIdentifier {

    MicroserviceManagement("com.sitewhere.grpc.service.MicroserviceManagement"),

    AssetManagement("com.sitewhere.grpc.service.AssetManagement"),

    BatchOperations("com.sitewhere.grpc.service.BatchManagement"),

    DeviceManagement("com.sitewhere.grpc.service.DeviceManagement"),

    EventManagement("com.sitewhere.grpc.service.DeviceEventManagement"),

    InstanceManagement("com.sitewhere.grpc.service.InstanceManagement"),

    LabelGeneration("com.sitewhere.grpc.service.LabelGeneration"),

    DeviceState("com.sitewhere.grpc.service.DeviceState"),

    UserManagement("com.sitewhere.grpc.service.UserManagement"),

    TenantManagement("com.sitewhere.grpc.service.TenantManagement"),

    ScheduleManagement("com.sitewhere.grpc.service.ScheduleManagement");

    /** Service name */
    private String grpcServiceName;

    private GrpcServiceIdentifier(String grpcServiceName) {
	this.grpcServiceName = grpcServiceName;
    }

    public static GrpcServiceIdentifier getByServiceName(String grpcServiceName) {
	for (GrpcServiceIdentifier value : GrpcServiceIdentifier.values()) {
	    if (value.getGrpcServiceName().equals(grpcServiceName)) {
		return value;
	    }
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier#getGrpcServiceName
     * ()
     */
    @Override
    public String getGrpcServiceName() {
	return grpcServiceName;
    }
}
