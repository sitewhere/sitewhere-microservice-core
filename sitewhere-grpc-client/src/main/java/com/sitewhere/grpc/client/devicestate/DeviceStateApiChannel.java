/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.devicestate;

import java.util.UUID;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.MultitenantApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceStateApiChannel;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.devicestate.DeviceStateModelConverter;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.grpc.service.GCreateDeviceStateRequest;
import com.sitewhere.grpc.service.GCreateDeviceStateResponse;
import com.sitewhere.grpc.service.GCreateRecentStateEventRequest;
import com.sitewhere.grpc.service.GCreateRecentStateEventResponse;
import com.sitewhere.grpc.service.GDeleteDeviceStateRequest;
import com.sitewhere.grpc.service.GDeleteDeviceStateResponse;
import com.sitewhere.grpc.service.GDeleteRecentStateEventRequest;
import com.sitewhere.grpc.service.GDeleteRecentStateEventResponse;
import com.sitewhere.grpc.service.GGetDeviceStateRequest;
import com.sitewhere.grpc.service.GGetDeviceStateResponse;
import com.sitewhere.grpc.service.GGetRecentStateEventRequest;
import com.sitewhere.grpc.service.GGetRecentStateEventResponse;
import com.sitewhere.grpc.service.GSearchDeviceStatesRequest;
import com.sitewhere.grpc.service.GSearchDeviceStatesResponse;
import com.sitewhere.grpc.service.GSearchRecentStateEventsRequest;
import com.sitewhere.grpc.service.GSearchRecentStateEventsResponse;
import com.sitewhere.grpc.service.GUpdateDeviceStateRequest;
import com.sitewhere.grpc.service.GUpdateDeviceStateResponse;
import com.sitewhere.grpc.service.GUpdateRecentStateEventRequest;
import com.sitewhere.grpc.service.GUpdateRecentStateEventResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.IRecentStateEvent;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.device.state.request.IRecentStateEventCreateRequest;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.grpc.GrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceStateSearchCriteria;
import com.sitewhere.spi.search.device.IRecentStateEventSearchCriteria;

/**
 * Supports SiteWhere device state APIs on top of a
 * {@link DeviceStateGrpcChannel}.
 */
public class DeviceStateApiChannel extends MultitenantApiChannel<DeviceStateGrpcChannel>
	implements IDeviceStateApiChannel<DeviceStateGrpcChannel> {

    public DeviceStateApiChannel(IInstanceSettings settings) {
	super(settings, MicroserviceIdentifier.DeviceState, GrpcServiceIdentifier.DeviceState,
		IGrpcSettings.DEFAULT_API_PORT);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .microservice.instance.IInstanceSettings,
     * com.sitewhere.spi.microservice.IFunctionIdentifier,
     * com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier, int)
     */
    @Override
    public DeviceStateGrpcChannel createGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	return new DeviceStateGrpcChannel(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * @see
     * com.sitewhere.spi.device.state.IDeviceStateManagement#createDeviceState(com.
     * sitewhere.spi.device.state.request.IDeviceStateCreateRequest)
     */
    @Override
    public IDeviceState createDeviceState(IDeviceStateCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceStateGrpc.getCreateDeviceStateMethod());
	    GCreateDeviceStateRequest.Builder grequest = GCreateDeviceStateRequest.newBuilder();
	    grequest.setRequest(DeviceStateModelConverter.asGrpcDeviceStateCreateRequest(request));
	    GCreateDeviceStateResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceState(grequest.build());
	    IDeviceState response = (gresponse.hasDeviceState())
		    ? DeviceStateModelConverter.asApiDeviceState(gresponse.getDeviceState())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceStateGrpc.getCreateDeviceStateMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceStateGrpc.getCreateDeviceStateMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.state.IDeviceStateManagement#getDeviceState(java.
     * util.UUID)
     */
    @Override
    public IDeviceState getDeviceState(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceStateGrpc.getGetDeviceStateMethod());
	    GGetDeviceStateRequest.Builder grequest = GGetDeviceStateRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceStateResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceState(grequest.build());
	    IDeviceState response = (gresponse.hasDeviceState())
		    ? DeviceStateModelConverter.asApiDeviceState(gresponse.getDeviceState())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceStateGrpc.getGetDeviceStateMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceStateGrpc.getGetDeviceStateMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.state.IDeviceStateManagement#searchDeviceStates(com.
     * sitewhere.spi.search.device.IDeviceStateSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceState> searchDeviceStates(IDeviceStateSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceStateGrpc.getSearchDeviceStatesMethod());
	    GSearchDeviceStatesRequest.Builder grequest = GSearchDeviceStatesRequest.newBuilder();
	    grequest.setCriteria(DeviceStateModelConverter.asGrpcDeviceStateSearchCriteria(criteria));
	    GSearchDeviceStatesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .searchDeviceStates(grequest.build());
	    ISearchResults<IDeviceState> results = DeviceStateModelConverter
		    .asApiDeviceStateSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceStateGrpc.getSearchDeviceStatesMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceStateGrpc.getSearchDeviceStatesMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.state.IDeviceStateManagement#updateDeviceState(java.
     * util.UUID, com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest)
     */
    @Override
    public IDeviceState updateDeviceState(UUID id, IDeviceStateCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceStateGrpc.getUpdateDeviceStateMethod());
	    GUpdateDeviceStateRequest.Builder grequest = GUpdateDeviceStateRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceStateModelConverter.asGrpcDeviceStateCreateRequest(request));
	    GUpdateDeviceStateResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceState(grequest.build());
	    IDeviceState response = (gresponse.hasDeviceState())
		    ? DeviceStateModelConverter.asApiDeviceState(gresponse.getDeviceState())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceStateGrpc.getUpdateDeviceStateMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceStateGrpc.getUpdateDeviceStateMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.state.IDeviceStateManagement#deleteDeviceState(java.
     * util.UUID)
     */
    @Override
    public IDeviceState deleteDeviceState(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceStateGrpc.getDeleteDeviceStateMethod());
	    GDeleteDeviceStateRequest.Builder grequest = GDeleteDeviceStateRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteDeviceStateResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceState(grequest.build());
	    IDeviceState response = (gresponse.hasDeviceState())
		    ? DeviceStateModelConverter.asApiDeviceState(gresponse.getDeviceState())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceStateGrpc.getDeleteDeviceStateMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceStateGrpc.getDeleteDeviceStateMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * createRecentStateEvent(com.sitewhere.spi.device.state.request.
     * IRecentStateEventCreateRequest)
     */
    @Override
    public IRecentStateEvent createRecentStateEvent(IRecentStateEventCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceStateGrpc.getCreateRecentStateEventMethod());
	    GCreateRecentStateEventRequest.Builder grequest = GCreateRecentStateEventRequest.newBuilder();
	    grequest.setRequest(DeviceStateModelConverter.asGrpcRecentStateEventCreateRequest(request));
	    GCreateRecentStateEventResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createRecentStateEvent(grequest.build());
	    IRecentStateEvent response = (gresponse.hasRecentStateEvent())
		    ? DeviceStateModelConverter.asApiRecentStateEvent(gresponse.getRecentStateEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceStateGrpc.getCreateRecentStateEventMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceStateGrpc.getCreateRecentStateEventMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * getRecentStateEvent(java.util.UUID)
     */
    @Override
    public IRecentStateEvent getRecentStateEvent(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceStateGrpc.getGetRecentStateEventMethod());
	    GGetRecentStateEventRequest.Builder grequest = GGetRecentStateEventRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetRecentStateEventResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getRecentStateEvent(grequest.build());
	    IRecentStateEvent response = (gresponse.hasRecentStateEvent())
		    ? DeviceStateModelConverter.asApiRecentStateEvent(gresponse.getRecentStateEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceStateGrpc.getGetRecentStateEventMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceStateGrpc.getGetRecentStateEventMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * searchRecentStateEvents(com.sitewhere.spi.search.device.
     * IRecentStateEventSearchCriteria)
     */
    @Override
    public ISearchResults<IRecentStateEvent> searchRecentStateEvents(IRecentStateEventSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceStateGrpc.getSearchRecentStateEventsMethod());
	    GSearchRecentStateEventsRequest.Builder grequest = GSearchRecentStateEventsRequest.newBuilder();
	    grequest.setCriteria(DeviceStateModelConverter.asGrpcRecentStateEventSearchCriteria(criteria));
	    GSearchRecentStateEventsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .searchRecentStateEvents(grequest.build());
	    ISearchResults<IRecentStateEvent> results = DeviceStateModelConverter
		    .asApiRecentStateEventSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceStateGrpc.getSearchRecentStateEventsMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceStateGrpc.getSearchRecentStateEventsMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * updateRecentStateEvent(java.util.UUID,
     * com.sitewhere.spi.device.state.request.IRecentStateEventCreateRequest)
     */
    @Override
    public IRecentStateEvent updateRecentStateEvent(UUID id, IRecentStateEventCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceStateGrpc.getUpdateRecentStateEventMethod());
	    GUpdateRecentStateEventRequest.Builder grequest = GUpdateRecentStateEventRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceStateModelConverter.asGrpcRecentStateEventCreateRequest(request));
	    GUpdateRecentStateEventResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateRecentStateEvent(grequest.build());
	    IRecentStateEvent response = (gresponse.hasRecentStateEvent())
		    ? DeviceStateModelConverter.asApiRecentStateEvent(gresponse.getRecentStateEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceStateGrpc.getUpdateRecentStateEventMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceStateGrpc.getUpdateRecentStateEventMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * deleteRecentStateEvent(java.util.UUID)
     */
    @Override
    public IRecentStateEvent deleteRecentStateEvent(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceStateGrpc.getDeleteRecentStateEventMethod());
	    GDeleteRecentStateEventRequest.Builder grequest = GDeleteRecentStateEventRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteRecentStateEventResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteRecentStateEvent(grequest.build());
	    IRecentStateEvent response = (gresponse.hasRecentStateEvent())
		    ? DeviceStateModelConverter.asApiRecentStateEvent(gresponse.getRecentStateEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceStateGrpc.getDeleteRecentStateEventMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceStateGrpc.getDeleteRecentStateEventMethod(), t);
	}
    }
}