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
package com.sitewhere.grpc.client.common.kafka;

import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.kafka.model.KafkaModel.GLifecycleComponentState;
import com.sitewhere.grpc.kafka.model.KafkaModel.GLifecycleStatus;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;
import com.sitewhere.spi.microservice.state.ILifecycleComponentState;

/**
 * Convert model objects passed on Kafka topics.
 */
public class KafkaModelConverter {

    /**
     * Convert lifecycle status from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static LifecycleStatus asApiLifecycleStatus(GLifecycleStatus grpc) throws SiteWhereException {
	switch (grpc) {
	case LIFECYCLE_STATUS_INITIALIZING:
	    return LifecycleStatus.Initializing;
	case LIFECYCLE_STATUS_INITIALIZATION_ERROR:
	    return LifecycleStatus.InitializationError;
	case LIFECYCLE_STATUS_STOPPED:
	    return LifecycleStatus.Stopped;
	case LIFECYCLE_STATUS_STOPPED_WITH_ERRORS:
	    return LifecycleStatus.StoppedWithErrors;
	case LIFECYCLE_STATUS_STARTING:
	    return LifecycleStatus.Starting;
	case LIFECYCLE_STATUS_STARTED:
	    return LifecycleStatus.Started;
	case LIFECYCLE_STATUS_STARTED_WITH_ERRORS:
	    return LifecycleStatus.StartedWithErrors;
	case LIFECYCLE_STATUS_PAUSING:
	    return LifecycleStatus.Pausing;
	case LIFECYCLE_STATUS_PAUSED:
	    return LifecycleStatus.Paused;
	case LIFECYCLE_STATUS_STOPPING:
	    return LifecycleStatus.Stopping;
	case LIFECYCLE_STATUS_TERMINATING:
	    return LifecycleStatus.Terminating;
	case LIFECYCLE_STATUS_TERMINATED:
	    return LifecycleStatus.Terminated;
	case LIFECYCLE_STATUS_ERROR:
	    return LifecycleStatus.LifecycleError;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown lifecycle status: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert lifecycle status from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GLifecycleStatus asGrpcLifecycleStatus(LifecycleStatus api) throws SiteWhereException {
	switch (api) {
	case Initializing:
	    return GLifecycleStatus.LIFECYCLE_STATUS_INITIALIZING;
	case InitializationError:
	    return GLifecycleStatus.LIFECYCLE_STATUS_INITIALIZATION_ERROR;
	case Stopped:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STOPPED;
	case StoppedWithErrors:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STOPPED_WITH_ERRORS;
	case Starting:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STARTING;
	case StartingAsynchronously:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STARTING;
	case Started:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STARTED;
	case StartedWithErrors:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STARTED_WITH_ERRORS;
	case Pausing:
	    return GLifecycleStatus.LIFECYCLE_STATUS_PAUSING;
	case Paused:
	    return GLifecycleStatus.LIFECYCLE_STATUS_PAUSED;
	case Stopping:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STOPPING;
	case Terminating:
	    return GLifecycleStatus.LIFECYCLE_STATUS_TERMINATING;
	case Terminated:
	    return GLifecycleStatus.LIFECYCLE_STATUS_TERMINATED;
	case LifecycleError:
	    return GLifecycleStatus.LIFECYCLE_STATUS_ERROR;
	}
	throw new SiteWhereException("Unknown lifecycle status: " + api.name());
    }

    /**
     * Convert lifecycle component state from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GLifecycleComponentState asGrpcLifecycleComponentState(ILifecycleComponentState api)
	    throws SiteWhereException {
	GLifecycleComponentState.Builder grpc = GLifecycleComponentState.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getComponentId()));
	grpc.setName(api.getComponentName());
	grpc.setStatus(KafkaModelConverter.asGrpcLifecycleStatus(api.getStatus()));
	if (api.getErrorStack() != null) {
	    grpc.addAllErrorFrames(api.getErrorStack());
	}
	if (api.getChildComponentStates() != null) {
	    for (ILifecycleComponentState child : api.getChildComponentStates()) {
		grpc.addChildComponentStates(KafkaModelConverter.asGrpcLifecycleComponentState(child));
	    }
	}
	return grpc.build();
    }
}