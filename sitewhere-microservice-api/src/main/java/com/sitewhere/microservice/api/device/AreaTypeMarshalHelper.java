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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.area.AreaType;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.device.marshaling.MarshaledAreaType;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IAreaType;

/**
 * Configurable helper class that allows {@link AreaType} model objects to be
 * created from {@link IAreaType} SPI objects.
 */
public class AreaTypeMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(AreaTypeMarshalHelper.class);

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Indicates whether contained area types are to be included */
    private boolean includeContainedAreaTypes = false;

    public AreaTypeMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledAreaType convert(IAreaType source) throws SiteWhereException {
	MarshaledAreaType type = new MarshaledAreaType();
	type.setName(source.getName());
	type.setDescription(source.getDescription());
	BrandedEntity.copy(source, type);
	if (isIncludeContainedAreaTypes()) {
	    type.setContainedAreaTypes(getDeviceManagement().getContainedAreaTypes(source.getId()));
	}
	return type;
    }

    public boolean isIncludeContainedAreaTypes() {
	return includeContainedAreaTypes;
    }

    public void setIncludeContainedAreaTypes(boolean includeContainedAreaTypes) {
	this.includeContainedAreaTypes = includeContainedAreaTypes;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}