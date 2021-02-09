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

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;

/**
 * Interface for batch management operations.
 */
public interface IBatchManagement extends ITenantEngineLifecycleComponent {

    /**
     * Creates an {@link IBatchOperation} to perform an operation on multiple
     * devices.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IBatchOperation createBatchOperation(IBatchOperationCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing {@link IBatchOperation}.
     * 
     * @param batchOperationId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IBatchOperation updateBatchOperation(UUID batchOperationId, IBatchOperationUpdateRequest request)
	    throws SiteWhereException;

    /**
     * Get an {@link IBatchOperation} by unique token.
     * 
     * @param batchOperationId
     * @return
     * @throws SiteWhereException
     */
    IBatchOperation getBatchOperation(UUID batchOperationId) throws SiteWhereException;

    /**
     * Get a batch operation by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    IBatchOperation getBatchOperationByToken(String token) throws SiteWhereException;

    /**
     * List batch operations based on the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<? extends IBatchOperation> listBatchOperations(IBatchOperationSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Deletes a batch operation and its elements.
     * 
     * @param batchOperationId
     * @return
     * @throws SiteWhereException
     */
    IBatchOperation deleteBatchOperation(UUID batchOperationId) throws SiteWhereException;

    /**
     * Create a batch element associated with a batch operation.
     * 
     * @param batchOperationId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IBatchElement createBatchElement(UUID batchOperationId, IBatchElementCreateRequest request)
	    throws SiteWhereException;

    /**
     * Lists elements for an {@link IBatchOperation} that meet the given criteria.
     * 
     * @param batchOperationId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    ISearchResults<? extends IBatchElement> listBatchElements(UUID batchOperationId,
	    IBatchElementSearchCriteria criteria) throws SiteWhereException;

    /**
     * Updates an existing batch operation element.
     * 
     * @param elementId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IBatchElement updateBatchElement(UUID elementId, IBatchElementCreateRequest request) throws SiteWhereException;

    /**
     * Creates an {@link ISearchResults} that will invoke a command on multiple
     * devices.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request) throws SiteWhereException;
}