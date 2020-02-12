/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.schedule;

import java.util.UUID;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.MultitenantApiChannel;
import com.sitewhere.grpc.client.spi.client.IScheduleManagementApiChannel;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.schedule.ScheduleModelConverter;
import com.sitewhere.grpc.service.GCreateScheduleRequest;
import com.sitewhere.grpc.service.GCreateScheduleResponse;
import com.sitewhere.grpc.service.GCreateScheduledJobRequest;
import com.sitewhere.grpc.service.GCreateScheduledJobResponse;
import com.sitewhere.grpc.service.GDeleteScheduleRequest;
import com.sitewhere.grpc.service.GDeleteScheduleResponse;
import com.sitewhere.grpc.service.GDeleteScheduledJobRequest;
import com.sitewhere.grpc.service.GDeleteScheduledJobResponse;
import com.sitewhere.grpc.service.GGetScheduleByTokenRequest;
import com.sitewhere.grpc.service.GGetScheduleByTokenResponse;
import com.sitewhere.grpc.service.GGetScheduleRequest;
import com.sitewhere.grpc.service.GGetScheduleResponse;
import com.sitewhere.grpc.service.GGetScheduledJobByTokenRequest;
import com.sitewhere.grpc.service.GGetScheduledJobByTokenResponse;
import com.sitewhere.grpc.service.GGetScheduledJobRequest;
import com.sitewhere.grpc.service.GGetScheduledJobResponse;
import com.sitewhere.grpc.service.GListScheduledJobsRequest;
import com.sitewhere.grpc.service.GListScheduledJobsResponse;
import com.sitewhere.grpc.service.GListSchedulesRequest;
import com.sitewhere.grpc.service.GListSchedulesResponse;
import com.sitewhere.grpc.service.GUpdateScheduleRequest;
import com.sitewhere.grpc.service.GUpdateScheduleResponse;
import com.sitewhere.grpc.service.GUpdateScheduledJobRequest;
import com.sitewhere.grpc.service.GUpdateScheduledJobResponse;
import com.sitewhere.grpc.service.ScheduleManagementGrpc;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.grpc.GrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Supports SiteWhere schedule management APIs on top of a
 * {@link ScheduleManagementGrpcChannel}.
 */
public class ScheduleManagementApiChannel extends MultitenantApiChannel<ScheduleManagementGrpcChannel>
	implements IScheduleManagementApiChannel<ScheduleManagementGrpcChannel> {

    public ScheduleManagementApiChannel(IInstanceSettings settings) {
	super(settings, MicroserviceIdentifier.ScheduleManagement, GrpcServiceIdentifier.ScheduleManagement,
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
    public ScheduleManagementGrpcChannel createGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	return new ScheduleManagementGrpcChannel(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#createSchedule(com.sitewhere
     * .spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getCreateScheduleMethod());
	    GCreateScheduleRequest.Builder grequest = GCreateScheduleRequest.newBuilder();
	    grequest.setRequest(ScheduleModelConverter.asGrpcScheduleCreateRequest(request));
	    GCreateScheduleResponse gresponse = getGrpcChannel().getBlockingStub().createSchedule(grequest.build());
	    ISchedule response = (gresponse.hasSchedule())
		    ? ScheduleModelConverter.asApiSchedule(gresponse.getSchedule())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getCreateScheduleMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getCreateScheduleMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#updateSchedule(
     * java.util.UUID, com.sitewhere.spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule updateSchedule(UUID scheduleId, IScheduleCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getUpdateScheduleMethod());
	    GUpdateScheduleRequest.Builder grequest = GUpdateScheduleRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(scheduleId));
	    grequest.setRequest(ScheduleModelConverter.asGrpcScheduleCreateRequest(request));
	    GUpdateScheduleResponse gresponse = getGrpcChannel().getBlockingStub().updateSchedule(grequest.build());
	    ISchedule response = (gresponse.hasSchedule())
		    ? ScheduleModelConverter.asApiSchedule(gresponse.getSchedule())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getUpdateScheduleMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getUpdateScheduleMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#getSchedule(java.
     * util.UUID)
     */
    @Override
    public ISchedule getSchedule(UUID scheduleId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getGetScheduleMethod());
	    GGetScheduleRequest.Builder grequest = GGetScheduleRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(scheduleId));
	    GGetScheduleResponse gresponse = getGrpcChannel().getBlockingStub().getSchedule(grequest.build());
	    ISchedule response = (gresponse.hasSchedule())
		    ? ScheduleModelConverter.asApiSchedule(gresponse.getSchedule())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getGetScheduleMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getGetScheduleMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#getScheduleByToken(java.lang
     * .String)
     */
    @Override
    public ISchedule getScheduleByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getGetScheduleByTokenMethod());
	    GGetScheduleByTokenRequest.Builder grequest = GGetScheduleByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetScheduleByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getScheduleByToken(grequest.build());
	    ISchedule response = (gresponse.hasSchedule())
		    ? ScheduleModelConverter.asApiSchedule(gresponse.getSchedule())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getGetScheduleByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getGetScheduleByTokenMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#listSchedules(com.sitewhere.
     * spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getListSchedulesMethod());
	    GListSchedulesRequest.Builder grequest = GListSchedulesRequest.newBuilder();
	    grequest.setCriteria(ScheduleModelConverter.asGrpcScheduleSearchCriteria(criteria));
	    GListSchedulesResponse gresponse = getGrpcChannel().getBlockingStub().listSchedules(grequest.build());
	    ISearchResults<ISchedule> results = ScheduleModelConverter
		    .asApiScheduleSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getListSchedulesMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getListSchedulesMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#deleteSchedule(
     * java.util.UUID)
     */
    @Override
    public ISchedule deleteSchedule(UUID scheduleId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getDeleteScheduleMethod());
	    GDeleteScheduleRequest.Builder grequest = GDeleteScheduleRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(scheduleId));
	    GDeleteScheduleResponse gresponse = getGrpcChannel().getBlockingStub().deleteSchedule(grequest.build());
	    ISchedule response = (gresponse.hasSchedule())
		    ? ScheduleModelConverter.asApiSchedule(gresponse.getSchedule())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getDeleteScheduleMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getDeleteScheduleMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#createScheduledJob(com.
     * sitewhere.spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getCreateScheduledJobMethod());
	    GCreateScheduledJobRequest.Builder grequest = GCreateScheduledJobRequest.newBuilder();
	    grequest.setRequest(ScheduleModelConverter.asGrpcScheduledJobCreateRequest(request));
	    GCreateScheduledJobResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createScheduledJob(grequest.build());
	    IScheduledJob response = (gresponse.hasScheduledJob())
		    ? ScheduleModelConverter.asApiScheduledJob(gresponse.getScheduledJob())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getCreateScheduledJobMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getCreateScheduledJobMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * updateScheduledJob(java.util.UUID,
     * com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob updateScheduledJob(UUID scheduledJobId, IScheduledJobCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getUpdateScheduledJobMethod());
	    GUpdateScheduledJobRequest.Builder grequest = GUpdateScheduledJobRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(scheduledJobId));
	    grequest.setRequest(ScheduleModelConverter.asGrpcScheduledJobCreateRequest(request));
	    GUpdateScheduledJobResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateScheduledJob(grequest.build());
	    IScheduledJob response = (gresponse.hasScheduledJob())
		    ? ScheduleModelConverter.asApiScheduledJob(gresponse.getScheduledJob())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getUpdateScheduledJobMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getUpdateScheduledJobMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#getScheduledJob(
     * java.util.UUID)
     */
    @Override
    public IScheduledJob getScheduledJob(UUID scheduledJobId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getGetScheduledJobMethod());
	    GGetScheduledJobRequest.Builder grequest = GGetScheduledJobRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(scheduledJobId));
	    GGetScheduledJobResponse gresponse = getGrpcChannel().getBlockingStub().getScheduledJob(grequest.build());
	    IScheduledJob response = (gresponse.hasScheduledJob())
		    ? ScheduleModelConverter.asApiScheduledJob(gresponse.getScheduledJob())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getGetScheduledJobMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getGetScheduledJobMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#getScheduledJobByToken(java.
     * lang.String)
     */
    @Override
    public IScheduledJob getScheduledJobByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getGetScheduledJobByTokenMethod());
	    GGetScheduledJobByTokenRequest.Builder grequest = GGetScheduledJobByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetScheduledJobByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getScheduledJobByToken(grequest.build());
	    IScheduledJob response = (gresponse.hasScheduledJob())
		    ? ScheduleModelConverter.asApiScheduledJob(gresponse.getScheduledJob())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getGetScheduledJobByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getGetScheduledJobByTokenMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#listScheduledJobs(com.
     * sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IScheduledJob> listScheduledJobs(ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getListScheduledJobsMethod());
	    GListScheduledJobsRequest.Builder grequest = GListScheduledJobsRequest.newBuilder();
	    grequest.setCriteria(ScheduleModelConverter.asGrpcScheduledJobSearchCriteria(criteria));
	    GListScheduledJobsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listScheduledJobs(grequest.build());
	    ISearchResults<IScheduledJob> results = ScheduleModelConverter
		    .asApiScheduledJobSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getListScheduledJobsMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getListScheduledJobsMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * deleteScheduledJob(java.util.UUID)
     */
    @Override
    public IScheduledJob deleteScheduledJob(UUID scheduledJobId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, ScheduleManagementGrpc.getDeleteScheduledJobMethod());
	    GDeleteScheduledJobRequest.Builder grequest = GDeleteScheduledJobRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(scheduledJobId));
	    GDeleteScheduledJobResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteScheduledJob(grequest.build());
	    IScheduledJob response = (gresponse.hasScheduledJob())
		    ? ScheduleModelConverter.asApiScheduledJob(gresponse.getScheduledJob())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.getDeleteScheduledJobMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.getDeleteScheduledJobMethod(), t);
	}
    }
}