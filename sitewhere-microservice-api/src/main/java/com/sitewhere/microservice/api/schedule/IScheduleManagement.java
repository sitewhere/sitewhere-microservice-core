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