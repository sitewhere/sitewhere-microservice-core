/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.api.schedule;

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Management interface for persistent scheduling implementations.
 */
public interface IScheduleManagement extends ITenantEngineLifecycleComponent {

    /**
     * Create a new schedule.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing schedule.
     * 
     * @param scheduleId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    ISchedule updateSchedule(UUID scheduleId, IScheduleCreateRequest request) throws SiteWhereException;

    /**
     * Get schedule by unique id.
     * 
     * @param scheduleId
     * @return
     * @throws SiteWhereException
     */
    ISchedule getSchedule(UUID scheduleId) throws SiteWhereException;

    /**
     * Get a schedule by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    ISchedule getScheduleByToken(String token) throws SiteWhereException;

    /**
     * List schedules that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<? extends ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an existing schedule.
     * 
     * @param scheduleId
     * @return
     * @throws SiteWhereException
     */
    ISchedule deleteSchedule(UUID scheduleId) throws SiteWhereException;

    /**
     * Create a new scheduled job.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing scheduled job.
     * 
     * @param scheduledJobId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IScheduledJob updateScheduledJob(UUID scheduledJobId, IScheduledJobCreateRequest request) throws SiteWhereException;

    /**
     * Get scheduled job by unique id.
     * 
     * @param scheduledJobId
     * @return
     * @throws SiteWhereException
     */
    IScheduledJob getScheduledJob(UUID scheduledJobId) throws SiteWhereException;

    /**
     * Get a scheduled job by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    IScheduledJob getScheduledJobByToken(String token) throws SiteWhereException;

    /**
     * List scheduled jobs that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<? extends IScheduledJob> listScheduledJobs(ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an existing scheduled job.
     * 
     * @param scheduledJobId
     * @return
     * @throws SiteWhereException
     */
    IScheduledJob deleteScheduledJob(UUID scheduledJobId) throws SiteWhereException;
}