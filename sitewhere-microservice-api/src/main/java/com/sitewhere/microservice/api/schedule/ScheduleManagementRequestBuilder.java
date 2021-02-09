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

import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;

/**
 * Builder that supports creating schedule management entities.
 */
public class ScheduleManagementRequestBuilder {

    /** Asset management implementation */
    private IScheduleManagement scheduleManagement;

    public ScheduleManagementRequestBuilder(IScheduleManagement scheduleManagement) {
	this.scheduleManagement = scheduleManagement;
    }

    public ScheduleCreateRequest.Builder newSchedule(String name) {
	return new ScheduleCreateRequest.Builder(name);
    }

    public ScheduleCreateRequest.Builder newSchedule(String token, String name) {
	return new ScheduleCreateRequest.Builder(token, name);
    }

    public ISchedule persist(ScheduleCreateRequest.Builder builder) throws SiteWhereException {
	return getScheduleManagement().createSchedule(builder.build());
    }

    public IScheduleManagement getScheduleManagement() {
	return scheduleManagement;
    }
}