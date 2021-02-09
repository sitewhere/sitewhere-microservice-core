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
package com.sitewhere.microservice.api.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.customer.Customer;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.marshaling.MarshaledCustomer;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Configurable helper class that allows {@link Customer} model objects to be
 * created from {@link ICustomer} SPI objects.
 */
public class CustomerMarshalHelper {

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Asset management */
    private IAssetManagement assetManagement;

    /** Include customer type information */
    private boolean includeCustomerType = false;

    /** Include parent customer information */
    private boolean includeParentCustomer = false;

    /** Indicates whether assignments for customer should be included */
    private boolean includeAssignments = false;

    /** Device assignment marshal helper */
    private DeviceAssignmentMarshalHelper assignmentHelper;

    public CustomerMarshalHelper(IDeviceManagement deviceManagement, IAssetManagement assetManagement) {
	this.deviceManagement = deviceManagement;
	this.assetManagement = assetManagement;

	this.assignmentHelper = new DeviceAssignmentMarshalHelper(deviceManagement);
	assignmentHelper.setIncludeDevice(true);
	assignmentHelper.setIncludeAsset(true);
	assignmentHelper.setIncludeArea(false);
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledCustomer convert(ICustomer source) throws SiteWhereException {
	if (source == null) {
	    return null;
	}
	MarshaledCustomer customer = new MarshaledCustomer();
	customer.setCustomerTypeId(source.getCustomerTypeId());
	customer.setParentId(source.getParentId());
	customer.setName(source.getName());
	customer.setDescription(source.getDescription());
	BrandedEntity.copy(source, customer);
	if (isIncludeCustomerType()) {
	    ICustomerType type = getDeviceManagement().getCustomerType(source.getCustomerTypeId());
	    customer.setCustomerType(new CustomerTypeMarshalHelper(getDeviceManagement()).convert(type));
	}
	if (isIncludeParentCustomer()) {
	    if (source.getParentId() != null) {
		ICustomer parent = getDeviceManagement().getCustomer(source.getParentId());
		customer.setParentCustomer(
			new CustomerMarshalHelper(getDeviceManagement(), getAssetManagement()).convert(parent));
	    }
	}
	if (isIncludeAssignments()) {
	    DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(1, 0);
	    criteria.setAssignmentStatuses(Collections.singletonList(DeviceAssignmentStatus.Active));
	    criteria.setCustomerTokens(Collections.singletonList(customer.getToken()));
	    ISearchResults<? extends IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	    List<DeviceAssignment> assignments = new ArrayList<DeviceAssignment>();
	    for (IDeviceAssignment match : matches.getResults()) {
		assignments.add(assignmentHelper.convert(match, getAssetManagement()));
	    }
	    customer.setDeviceAssignments(assignments);
	}
	return customer;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    public boolean isIncludeCustomerType() {
	return includeCustomerType;
    }

    public void setIncludeCustomerType(boolean includeCustomerType) {
	this.includeCustomerType = includeCustomerType;
    }

    public boolean isIncludeParentCustomer() {
	return includeParentCustomer;
    }

    public void setIncludeParentCustomer(boolean includeParentCustomer) {
	this.includeParentCustomer = includeParentCustomer;
    }

    public boolean isIncludeAssignments() {
	return includeAssignments;
    }

    public void setIncludeAssignments(boolean includeAssignments) {
	this.includeAssignments = includeAssignments;
    }
}
