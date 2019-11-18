/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.common.converter;

import com.sitewhere.grpc.kafka.model.KafkaModel.GLifecycleComponentState;
import com.sitewhere.grpc.kafka.model.KafkaModel.GLifecycleStatus;
import com.sitewhere.grpc.kafka.model.KafkaModel.GMicroserviceState;
import com.sitewhere.grpc.kafka.model.KafkaModel.GStateUpdate;
import com.sitewhere.grpc.kafka.model.KafkaModel.GTenantEngineState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;
import com.sitewhere.spi.microservice.state.ILifecycleComponentState;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;

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

    /**
     * Convert microservice state from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GMicroserviceState asGrpcMicroserviceState(IMicroserviceState api) throws SiteWhereException {
	GMicroserviceState.Builder grpc = GMicroserviceState.newBuilder();
	grpc.setStatus(KafkaModelConverter.asGrpcLifecycleStatus(api.getLifecycleStatus()));
	return grpc.build();
    }

    /**
     * Convert tenant engine state from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GTenantEngineState asGrpcTenantEngineState(ITenantEngineState api) throws SiteWhereException {
	GTenantEngineState.Builder grpc = GTenantEngineState.newBuilder();
	grpc.setTenantId(CommonModelConverter.asGrpcUuid(api.getTenantId()));
	grpc.setState(KafkaModelConverter.asGrpcLifecycleComponentState(api.getComponentState()));
	return grpc.build();
    }

    /**
     * Convert microservice state to generic state update.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GStateUpdate asGrpcGenericStateUpdate(IMicroserviceState api) throws SiteWhereException {
	GStateUpdate.Builder grpc = GStateUpdate.newBuilder();
	grpc.setMicroserviceState(KafkaModelConverter.asGrpcMicroserviceState(api));
	return grpc.build();
    }

    /**
     * Convert tenant engine state to generic state update.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GStateUpdate asGrpcGenericStateUpdate(ITenantEngineState api) throws SiteWhereException {
	GStateUpdate.Builder grpc = GStateUpdate.newBuilder();
	grpc.setTenantEngineState(KafkaModelConverter.asGrpcTenantEngineState(api));
	return grpc.build();
    }
}