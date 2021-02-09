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

import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.rest.model.batch.MarshaledBatchOperation;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchOperation;

/**
 * Configurable helper class that allows {@link BatchOperation} model objects to
 * be created from {@link IBatchOperation} SPI objects.
 */
public class BatchOperationMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(BatchOperationMarshalHelper.class);

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledBatchOperation convert(IBatchOperation source) throws SiteWhereException {
	if (source == null) {
	    return null;
	}
	MarshaledBatchOperation operation = new MarshaledBatchOperation();
	operation.setId(source.getId());
	operation.setToken(source.getToken());
	operation.setOperationType(source.getOperationType());
	operation.setParameters(source.getParameters());
	operation.setProcessingStatus(source.getProcessingStatus());
	operation.setProcessingStartedDate(source.getProcessingStartedDate());
	operation.setProcessingEndedDate(source.getProcessingEndedDate());

	PersistentEntity.copy(source, operation);
	return operation;
    }
}