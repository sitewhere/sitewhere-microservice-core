/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.api.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.customer.CustomerType;
import com.sitewhere.rest.model.device.marshaling.MarshaledCustomerType;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.customer.ICustomerType;

/**
 * Configurable helper class that allows {@link CustomerType} model objects to
 * be created from {@link ICustomerType} SPI objects.
 */
public class CustomerTypeMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(CustomerTypeMarshalHelper.class);

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Indicates whether contained customer types are to be included */
    private boolean includeContainedCustomerTypes = false;

    public CustomerTypeMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledCustomerType convert(ICustomerType source) throws SiteWhereException {
	MarshaledCustomerType type = new MarshaledCustomerType();
	type.setName(source.getName());
	type.setDescription(source.getDescription());
	BrandedEntity.copy(source, type);
	if (isIncludeContainedCustomerTypes()) {
	    type.setContainedCustomerTypes(getDeviceManagement().getContainedCustomerTypes(source.getId()));
	}
	return type;
    }

    public boolean isIncludeContainedCustomerTypes() {
	return includeContainedCustomerTypes;
    }

    public void setIncludeContainedCustomerTypes(boolean includeContainedCustomerTypes) {
	this.includeContainedCustomerTypes = includeContainedCustomerTypes;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}
