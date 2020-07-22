/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.event;

import java.util.List;
import java.util.UUID;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.MultitenantApiChannel;
import com.sitewhere.grpc.client.common.tracing.DebugParameter;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.grpc.service.GAddAlertsRequest;
import com.sitewhere.grpc.service.GAddAlertsResponse;
import com.sitewhere.grpc.service.GAddCommandInvocationsRequest;
import com.sitewhere.grpc.service.GAddCommandInvocationsResponse;
import com.sitewhere.grpc.service.GAddCommandResponsesRequest;
import com.sitewhere.grpc.service.GAddCommandResponsesResponse;
import com.sitewhere.grpc.service.GAddDeviceEventBatchRequest;
import com.sitewhere.grpc.service.GAddDeviceEventBatchResponse;
import com.sitewhere.grpc.service.GAddLocationsRequest;
import com.sitewhere.grpc.service.GAddLocationsResponse;
import com.sitewhere.grpc.service.GAddMeasurementsRequest;
import com.sitewhere.grpc.service.GAddMeasurementsResponse;
import com.sitewhere.grpc.service.GAddStateChangesRequest;
import com.sitewhere.grpc.service.GAddStateChangesResponse;
import com.sitewhere.grpc.service.GGetDeviceEventByAlternateIdRequest;
import com.sitewhere.grpc.service.GGetDeviceEventByAlternateIdResponse;
import com.sitewhere.grpc.service.GGetDeviceEventByIdRequest;
import com.sitewhere.grpc.service.GGetDeviceEventByIdResponse;
import com.sitewhere.grpc.service.GListAlertsForIndexRequest;
import com.sitewhere.grpc.service.GListAlertsForIndexResponse;
import com.sitewhere.grpc.service.GListCommandInvocationsForIndexRequest;
import com.sitewhere.grpc.service.GListCommandInvocationsForIndexResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForIndexRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForIndexResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationResponse;
import com.sitewhere.grpc.service.GListLocationsForIndexRequest;
import com.sitewhere.grpc.service.GListLocationsForIndexResponse;
import com.sitewhere.grpc.service.GListMeasurementsForIndexRequest;
import com.sitewhere.grpc.service.GListMeasurementsForIndexResponse;
import com.sitewhere.grpc.service.GListStateChangesForIndexRequest;
import com.sitewhere.grpc.service.GListStateChangesForIndexResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.grpc.GrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Supports SiteWhere device event management APIs on top of a
 * {@link DeviceEventManagementGrpcChannel}.
 */
public class DeviceEventManagementApiChannel extends MultitenantApiChannel<DeviceEventManagementGrpcChannel>
	implements IDeviceEventManagementApiChannel<DeviceEventManagementGrpcChannel> {

    public DeviceEventManagementApiChannel(IInstanceSettings settings) {
	super(settings, MicroserviceIdentifier.EventManagement, GrpcServiceIdentifier.EventManagement,
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
    public DeviceEventManagementGrpcChannel createGrpcChannel(IInstanceSettings settings,
	    IFunctionIdentifier identifier, IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	return new DeviceEventManagementGrpcChannel(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceEventBatch(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceEventBatch)
     */
    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(IDeviceEventContext context, IDeviceEventBatch batch)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(),
		    DebugParameter.create("Context", context), DebugParameter.create("Batch", batch));
	    GAddDeviceEventBatchRequest.Builder grequest = GAddDeviceEventBatchRequest.newBuilder();
	    grequest.setContext(EventModelConverter.asGrpcDeviceEventContext(context));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceEventBatch(batch));
	    GAddDeviceEventBatchResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addDeviceEventBatch(grequest.build());
	    IDeviceEventBatchResponse response = EventModelConverter
		    .asApiDeviceEventBatchResponse(gresponse.getResponse());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * getDeviceEventById(java.util.UUID)
     */
    @Override
    public IDeviceEvent getDeviceEventById(UUID eventId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getGetDeviceEventByIdMethod());
	    GGetDeviceEventByIdRequest.Builder grequest = GGetDeviceEventByIdRequest.newBuilder();
	    grequest.setEventId(CommonModelConverter.asGrpcUuid(eventId));
	    GGetDeviceEventByIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceEventById(grequest.build());
	    IDeviceEvent response = (gresponse.hasEvent())
		    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod());
	    GGetDeviceEventByAlternateIdRequest.Builder grequest = GGetDeviceEventByAlternateIdRequest.newBuilder();
	    grequest.setAlternateId(alternateId);
	    GGetDeviceEventByAlternateIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceEventByAlternateId(grequest.build());
	    IDeviceEvent response = (gresponse.hasEvent())
		    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(),
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceMeasurements(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest[])
     */
    @Override
    public List<? extends IDeviceMeasurement> addDeviceMeasurements(IDeviceEventContext context,
	    IDeviceMeasurementCreateRequest... measurement) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddMeasurementsMethod());
	    GAddMeasurementsRequest.Builder grequest = GAddMeasurementsRequest.newBuilder();
	    grequest.setContext(EventModelConverter.asGrpcDeviceEventContext(context));
	    for (IDeviceMeasurementCreateRequest request : measurement) {
		grequest.addRequests(EventModelConverter.asGrpcDeviceMeasurementCreateRequest(request));
	    }
	    GAddMeasurementsResponse gresponse = getGrpcChannel().getBlockingStub().addMeasurements(grequest.build());
	    List<? extends IDeviceMeasurement> response = EventModelConverter
		    .asApiDeviceMeasurements(gresponse.getMeasurementsList());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddMeasurementsMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddMeasurementsMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceMeasurementsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListMeasurementsForIndexMethod());
	    GListMeasurementsForIndexRequest.Builder grequest = GListMeasurementsForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListMeasurementsForIndexResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listMeasurementsForIndex(grequest.build());
	    ISearchResults<IDeviceMeasurement> response = EventModelConverter
		    .asApiDeviceMeasurementSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(),
		    t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceLocations(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest[])
     */
    @Override
    public List<? extends IDeviceLocation> addDeviceLocations(IDeviceEventContext context,
	    IDeviceLocationCreateRequest... location) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddLocationsMethod());
	    GAddLocationsRequest.Builder grequest = GAddLocationsRequest.newBuilder();
	    grequest.setContext(EventModelConverter.asGrpcDeviceEventContext(context));
	    for (IDeviceLocationCreateRequest request : location) {
		grequest.addRequests(EventModelConverter.asGrpcDeviceLocationCreateRequest(request));
	    }
	    GAddLocationsResponse gresponse = getGrpcChannel().getBlockingStub().addLocations(grequest.build());
	    List<? extends IDeviceLocation> response = EventModelConverter
		    .asApiDeviceLocations(gresponse.getLocationsList());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddLocationsMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddLocationsMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceLocationsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListLocationsForIndexMethod());
	    GListLocationsForIndexRequest.Builder grequest = GListLocationsForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListLocationsForIndexResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listLocationsForIndex(grequest.build());
	    ISearchResults<IDeviceLocation> response = EventModelConverter
		    .asApiDeviceLocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListLocationsForIndexMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getListLocationsForIndexMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.event.IDeviceEventManagement#addDeviceAlerts(
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest[])
     */
    @Override
    public List<? extends IDeviceAlert> addDeviceAlerts(IDeviceEventContext context, IDeviceAlertCreateRequest... alert)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddAlertsMethod());
	    GAddAlertsRequest.Builder grequest = GAddAlertsRequest.newBuilder();
	    grequest.setContext(EventModelConverter.asGrpcDeviceEventContext(context));
	    for (IDeviceAlertCreateRequest request : alert) {
		grequest.addRequests(EventModelConverter.asGrpcDeviceAlertCreateRequest(request));
	    }
	    GAddAlertsResponse gresponse = getGrpcChannel().getBlockingStub().addAlerts(grequest.build());
	    List<? extends IDeviceAlert> response = EventModelConverter.asApiDeviceAlerts(gresponse.getAlertsList());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddAlertsMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddAlertsMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceAlertsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListAlertsForIndexMethod());
	    GListAlertsForIndexRequest.Builder grequest = GListAlertsForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListAlertsForIndexResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listAlertsForIndex(grequest.build());
	    ISearchResults<IDeviceAlert> response = EventModelConverter
		    .asApiDeviceAlertSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListAlertsForIndexMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getListAlertsForIndexMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceCommandInvocations(com.sitewhere.spi.device.event.
     * IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest[
     * ])
     */
    @Override
    public List<? extends IDeviceCommandInvocation> addDeviceCommandInvocations(IDeviceEventContext context,
	    IDeviceCommandInvocationCreateRequest... invocation) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddCommandInvocationsMethod());
	    GAddCommandInvocationsRequest.Builder grequest = GAddCommandInvocationsRequest.newBuilder();
	    grequest.setContext(EventModelConverter.asGrpcDeviceEventContext(context));
	    for (IDeviceCommandInvocationCreateRequest request : invocation) {
		grequest.addRequests(EventModelConverter.asGrpcDeviceCommandInvocationCreateRequest(request));
	    }
	    GAddCommandInvocationsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addCommandInvocations(grequest.build());
	    List<? extends IDeviceCommandInvocation> response = EventModelConverter
		    .asApiDeviceCommandInvocations(gresponse.getInvocationsList());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddCommandInvocationsMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddCommandInvocationsMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this,
		    DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod());
	    GListCommandInvocationsForIndexRequest.Builder grequest = GListCommandInvocationsForIndexRequest
		    .newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandInvocationsForIndexResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandInvocationsForIndex(grequest.build());
	    ISearchResults<IDeviceCommandInvocation> response = EventModelConverter
		    .asApiDeviceCommandInvocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(),
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceCommandInvocationResponses(java.util.UUID)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID invocationId)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this,
		    DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod());
	    GListCommandResponsesForInvocationRequest.Builder grequest = GListCommandResponsesForInvocationRequest
		    .newBuilder();
	    grequest.setInvocationEventId(CommonModelConverter.asGrpcUuid(invocationId));
	    GListCommandResponsesForInvocationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForInvocation(grequest.build());
	    ISearchResults<IDeviceCommandResponse> response = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(),
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceCommandResponses(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest[])
     */
    @Override
    public List<? extends IDeviceCommandResponse> addDeviceCommandResponses(IDeviceEventContext context,
	    IDeviceCommandResponseCreateRequest... cresponse) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddCommandResponsesMethod());
	    GAddCommandResponsesRequest.Builder grequest = GAddCommandResponsesRequest.newBuilder();
	    grequest.setContext(EventModelConverter.asGrpcDeviceEventContext(context));
	    for (IDeviceCommandResponseCreateRequest request : cresponse) {
		grequest.addRequests(EventModelConverter.asGrpcDeviceCommandResponseCreateRequest(request));
	    }
	    GAddCommandResponsesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addCommandResponses(grequest.build());
	    List<? extends IDeviceCommandResponse> response = EventModelConverter
		    .asApiDeviceCommandResponses(gresponse.getResponsesList());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddCommandResponsesMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddCommandResponsesMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod());
	    GListCommandResponsesForIndexRequest.Builder grequest = GListCommandResponsesForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandResponsesForIndexResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForIndex(grequest.build());
	    ISearchResults<IDeviceCommandResponse> response = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(),
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceStateChanges(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest[])
     */
    @Override
    public List<? extends IDeviceStateChange> addDeviceStateChanges(IDeviceEventContext context,
	    IDeviceStateChangeCreateRequest... state) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddStateChangesMethod());
	    GAddStateChangesRequest.Builder grequest = GAddStateChangesRequest.newBuilder();
	    grequest.setContext(EventModelConverter.asGrpcDeviceEventContext(context));
	    for (IDeviceStateChangeCreateRequest request : state) {
		grequest.addRequests(EventModelConverter.asGrpcDeviceStateChangeCreateRequest(request));
	    }
	    GAddStateChangesResponse gresponse = getGrpcChannel().getBlockingStub().addStateChanges(grequest.build());
	    List<? extends IDeviceStateChange> response = EventModelConverter
		    .asApiDeviceStateChanges(gresponse.getStateChangesList());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddStateChangesMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddStateChangesMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceStateChangesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListStateChangesForIndexMethod());
	    GListStateChangesForIndexRequest.Builder grequest = GListStateChangesForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStateChangesForIndexResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listStateChangesForIndex(grequest.build());
	    ISearchResults<IDeviceStateChange> response = EventModelConverter
		    .asApiDeviceStateChangeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListStateChangesForIndexMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getListStateChangesForIndexMethod(),
		    t);
	}
    }
}