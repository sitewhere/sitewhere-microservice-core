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
package com.sitewhere.microservice.api.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rest.model.batch.BatchElement;
import com.sitewhere.rest.model.batch.MarshaledBatchElement;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.device.IDevice;

/**
 * Configurable helper class that allows {@link BatchElement} model objects to
 * be created from {@link IBatchElement} SPI objects.
 */
public class BatchElementMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(BatchElementMarshalHelper.class);

    /** Include device information */
    private boolean includeDevice;

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledBatchElement convert(IBatchElement source, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	if (source == null) {
	    return null;
	}
	MarshaledBatchElement element = new MarshaledBatchElement();
	element.setId(source.getId());
	element.setBatchOperationId(source.getBatchOperationId());
	element.setDeviceId(source.getDeviceId());
	element.setProcessingStatus(source.getProcessingStatus());
	element.setProcessedDate(source.getProcessedDate());

	if (isIncludeDevice()) {
	    IDevice device = deviceManagement.getDevice(source.getDeviceId());
	    if (device != null) {
		element.setDevice(device);
	    } else {
		LOGGER.warn("Invalid device reference in batch element.");
	    }
	}

	PersistentEntity.copy(source, element);
	return element;
    }

    public boolean isIncludeDevice() {
	return includeDevice;
    }

    public void setIncludeDevice(boolean includeDevice) {
	this.includeDevice = includeDevice;
    }
}