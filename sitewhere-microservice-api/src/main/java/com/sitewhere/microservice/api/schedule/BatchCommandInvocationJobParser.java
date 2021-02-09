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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.rest.model.batch.request.InvocationByAssignmentCriteriaRequest;
import com.sitewhere.rest.model.batch.request.InvocationByDeviceCriteriaRequest;
import com.sitewhere.spi.batch.request.IInvocationByAssignmentCriteriaRequest;
import com.sitewhere.spi.batch.request.IInvocationByDeviceCriteriaRequest;
import com.sitewhere.spi.scheduling.JobConstants;

public class BatchCommandInvocationJobParser {

    /**
     * Parse configuration data for a batch invocation based on device criteria.
     * 
     * @param data
     */
    public static IInvocationByDeviceCriteriaRequest parseInvocationByDeviceCriteria(Map<String, String> data) {

	String deviceTypeToken = null;
	String commandToken = null;
	Map<String, String> parameters = new HashMap<String, String>();

	for (String key : data.keySet()) {
	    String value = data.get(key);
	    if (JobConstants.InvocationByDeviceCriteria.DEVICE_TYPE_TOKEN.equals(key)) {
		deviceTypeToken = value;
	    } else if (JobConstants.CommandInvocation.COMMAND_TOKEN.equals(key)) {
		commandToken = value;
	    } else if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
		String paramKey = key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
		parameters.put(paramKey, value);
	    }
	}

	InvocationByDeviceCriteriaRequest request = new InvocationByDeviceCriteriaRequest();
	request.setDeviceTypeToken(deviceTypeToken);
	request.setCommandToken(commandToken);
	request.setParameterValues(parameters);
	return request;
    }

    /**
     * Parse configuration data for a batch invocation based on device assignment
     * criteria.
     * 
     * @param data
     */
    public static IInvocationByAssignmentCriteriaRequest parseInvocationByAssignmentCriteria(Map<String, String> data) {

	String deviceTypeToken = null;
	String commandToken = null;
	Map<String, String> parameters = new HashMap<String, String>();
	List<String> customerTokens = new ArrayList<>();
	List<String> areaTokens = new ArrayList<>();
	List<String> assetTokens = new ArrayList<>();

	for (String key : data.keySet()) {
	    String value = data.get(key);
	    if (JobConstants.InvocationByAssignmentCriteria.DEVICE_TYPE_TOKEN.equals(key)) {
		deviceTypeToken = value;
	    } else if (JobConstants.CommandInvocation.COMMAND_TOKEN.equals(key)) {
		commandToken = value;
	    } else if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
		String paramKey = key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
		parameters.put(paramKey, value);
	    } else if (key.startsWith(JobConstants.InvocationByAssignmentCriteria.CUSTOMER_TOKEN_PREFIX)) {
		customerTokens.add(value);
	    } else if (key.startsWith(JobConstants.InvocationByAssignmentCriteria.AREA_TOKEN_PREFIX)) {
		areaTokens.add(value);
	    } else if (key.startsWith(JobConstants.InvocationByAssignmentCriteria.ASSET_TOKEN_PREFIX)) {
		assetTokens.add(value);
	    }
	}

	InvocationByAssignmentCriteriaRequest request = new InvocationByAssignmentCriteriaRequest();
	request.setDeviceTypeToken(deviceTypeToken);
	request.setCommandToken(commandToken);
	request.setParameterValues(parameters);
	request.setCustomerTokens(customerTokens);
	request.setAreaTokens(areaTokens);
	request.setAssetTokens(assetTokens);
	return request;
    }
}
