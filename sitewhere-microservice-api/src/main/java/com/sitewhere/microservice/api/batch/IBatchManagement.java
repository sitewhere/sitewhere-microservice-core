/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
    ISearchResults<IBatchOperation> listBatchOperations(IBatchOperationSearchCriteria criteria)
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
    ISearchResults<IBatchElement> listBatchElements(UUID batchOperationId, IBatchElementSearchCriteria criteria)
	    throws SiteWhereException;

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