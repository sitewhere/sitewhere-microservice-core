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
package com.sitewhere.microservice.health;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;

/**
 * gRPC Health Service Protocol Implementation
 */
public class HealthServiceImpl extends HealthGrpc.HealthImplBase {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(HealthServiceImpl.class);

    private HealthCheckResponse.ServingStatus servingStatus = HealthCheckResponse.ServingStatus.SERVING;

    public void setServingStatus(HealthCheckResponse.ServingStatus servingStatus) {
	this.servingStatus = servingStatus;
    }

    /*
     * @see io.grpc.health.v1.HealthGrpc.HealthImplBase#check(io.grpc.health.v1.
     * HealthCheckRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
	LOGGER.trace("Health check called");

	HealthCheckResponse response = HealthCheckResponse.newBuilder().setStatus(servingStatus).build();
	responseObserver.onNext(response);
	responseObserver.onCompleted();
    }

    /*
     * @see io.grpc.health.v1.HealthGrpc.HealthImplBase#watch(io.grpc.health.v1.
     * HealthCheckRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void watch(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
	LOGGER.trace("Health watch called");

	HealthCheckResponse response = HealthCheckResponse.newBuilder().setStatus(servingStatus).build();
	responseObserver.onNext(response);
	responseObserver.onCompleted();
    }
}
