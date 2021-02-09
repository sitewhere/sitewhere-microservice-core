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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.rest.model.batch.request.InvocationByAssignmentCriteriaRequest;
import com.sitewhere.rest.model.batch.request.InvocationByDeviceCriteriaRequest;
import com.sitewhere.rest.model.scheduling.request.ScheduledJobCreateRequest;
import com.sitewhere.spi.scheduling.JobConstants;
import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.ScheduledJobType;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;

/**
 * Helper class for building {@link IScheduledJobCreateRequest} instances based
 * on job types.
 */
public class ScheduledJobHelper {

    /**
     * Create job that will invoke a command on an assignment.
     * 
     * @param assignmentToken
     * @param commandToken
     * @param parameters
     * @param scheduleToken
     * @return
     */
    public static IScheduledJobCreateRequest createCommandInvocationJob(String assignmentToken, String commandToken,
	    Map<String, String> parameters, String scheduleToken) {
	ScheduledJobCreateRequest job = new ScheduledJobCreateRequest();
	job.setToken(UUID.randomUUID().toString());
	job.setJobType(ScheduledJobType.CommandInvocation);

	Map<String, String> config = new HashMap<String, String>();
	config.put(JobConstants.CommandInvocation.ASSIGNMENT_TOKEN, assignmentToken);
	config.put(JobConstants.CommandInvocation.COMMAND_TOKEN, commandToken);
	for (String key : parameters.keySet()) {
	    String value = parameters.get(key);
	    config.put(JobConstants.CommandInvocation.PARAMETER_PREFIX + key, value);
	}
	job.setJobConfiguration(config);
	job.setScheduleToken(scheduleToken);

	return job;
    }

    /**
     * Create request for a job that uses device criteria to choose a list of
     * devices on which a command will be invoked.
     * 
     * @param request
     * @param scheduleToken
     * @return
     */
    public static IScheduledJobCreateRequest createBatchCommandInvocationJobForDeviceCriteria(
	    InvocationByDeviceCriteriaRequest request, String scheduleToken) {
	ScheduledJobCreateRequest job = new ScheduledJobCreateRequest();
	job.setToken(UUID.randomUUID().toString());
	job.setJobType(ScheduledJobType.BatchCommandInvocation);
	job.setJobState(ScheduledJobState.Unsubmitted);

	Map<String, String> config = new HashMap<String, String>();

	// Store command information.
	config.put(JobConstants.InvocationByDeviceCriteria.DEVICE_TYPE_TOKEN, request.getDeviceTypeToken());
	config.put(JobConstants.CommandInvocation.COMMAND_TOKEN, request.getCommandToken());
	for (String key : request.getParameterValues().keySet()) {
	    String value = request.getParameterValues().get(key);
	    config.put(JobConstants.CommandInvocation.PARAMETER_PREFIX + key, value);
	}

	// Store criteria information.
	job.setJobConfiguration(config);
	job.setScheduleToken(scheduleToken);

	return job;
    }

    /**
     * Create request for a job that uses assignment criteria to choose a list of
     * devices on which a command will be invoked.
     * 
     * @param request
     * @param scheduleToken
     * @return
     */
    public static IScheduledJobCreateRequest createBatchCommandInvocationJobForAssignmentCriteria(
	    InvocationByAssignmentCriteriaRequest request, String scheduleToken) {
	ScheduledJobCreateRequest job = new ScheduledJobCreateRequest();
	job.setToken(UUID.randomUUID().toString());
	job.setJobType(ScheduledJobType.BatchCommandInvocation);
	job.setJobState(ScheduledJobState.Unsubmitted);

	Map<String, String> config = new HashMap<String, String>();

	// Store command information.
	config.put(JobConstants.InvocationByAssignmentCriteria.DEVICE_TYPE_TOKEN, request.getDeviceTypeToken());
	config.put(JobConstants.CommandInvocation.COMMAND_TOKEN, request.getCommandToken());
	for (String key : request.getParameterValues().keySet()) {
	    String value = request.getParameterValues().get(key);
	    config.put(JobConstants.CommandInvocation.PARAMETER_PREFIX + key, value);
	}

	for (String token : request.getCustomerTokens()) {
	    config.put(JobConstants.InvocationByAssignmentCriteria.CUSTOMER_TOKEN_PREFIX + token, token);
	}
	for (String token : request.getAreaTokens()) {
	    config.put(JobConstants.InvocationByAssignmentCriteria.AREA_TOKEN_PREFIX + token, token);
	}
	for (String token : request.getAssetTokens()) {
	    config.put(JobConstants.InvocationByAssignmentCriteria.ASSET_TOKEN_PREFIX + token, token);
	}

	// Store criteria information.
	job.setJobConfiguration(config);
	job.setScheduleToken(scheduleToken);

	return job;
    }
}