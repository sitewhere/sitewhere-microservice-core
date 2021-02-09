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
package com.sitewhere.microservice.persistence;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.request.IBrandedEntityCreateRequest;
import com.sitewhere.spi.common.request.IPersistentEntityCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Common methods needed by device service provider implementations.
 */
public class Persistence {

    /**
     * Common logic for populating a persistent entity.
     * 
     * @param request
     * @param entity
     * @throws SiteWhereException
     */
    public static void entityCreateLogic(IPersistentEntityCreateRequest request, PersistentEntity entity)
	    throws SiteWhereException {
	if (!StringUtils.isEmpty(request.getToken())) {
	    entity.setToken(request.getToken());
	} else {
	    entity.setToken(UUID.randomUUID().toString());
	}
	entity.setCreatedDate(new Date());
	entity.setCreatedBy(UserContext.getCurrentUser().getUsername());
	MetadataProvider.copy(request.getMetadata(), entity);
    }

    /**
     * Common entity update logic.
     * 
     * @param request
     * @param entity
     * @throws SiteWhereException
     */
    public static void entityUpdateLogic(IPersistentEntityCreateRequest request, PersistentEntity entity)
	    throws SiteWhereException {
	if (!StringUtils.isEmpty(request.getToken())) {
	    entity.setToken(request.getToken());
	}
	entity.setUpdatedDate(new Date());
	entity.setUpdatedBy(UserContext.getCurrentUser().getUsername());

	if (request.getMetadata() != null) {
	    entity.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), entity);
	}
    }

    /**
     * Common logic for creating a branded entity.
     * 
     * @param request
     * @param entity
     * @throws SiteWhereException
     */
    public static void brandedEntityCreateLogic(IBrandedEntityCreateRequest request, BrandedEntity entity)
	    throws SiteWhereException {
	entityCreateLogic(request, entity);

	entity.setImageUrl(!StringUtils.isEmpty(request.getImageUrl()) ? request.getImageUrl() : null);
	entity.setIcon(!StringUtils.isEmpty(request.getIcon()) ? request.getIcon() : null);
	entity.setBackgroundColor(
		!StringUtils.isEmpty(request.getBackgroundColor()) ? request.getBackgroundColor() : null);
	entity.setForegroundColor(
		!StringUtils.isEmpty(request.getForegroundColor()) ? request.getForegroundColor() : null);
	entity.setBorderColor(!StringUtils.isEmpty(request.getBorderColor()) ? request.getBorderColor() : null);
    }

    /**
     * Common logic for updating a branded entity.
     * 
     * @param request
     * @param entity
     * @throws SiteWhereException
     */
    public static void brandedEntityUpdateLogic(IBrandedEntityCreateRequest request, BrandedEntity entity)
	    throws SiteWhereException {
	entityUpdateLogic(request, entity);

	entity.setImageUrl(!StringUtils.isEmpty(request.getImageUrl()) ? request.getImageUrl() : null);
	entity.setIcon(!StringUtils.isEmpty(request.getIcon()) ? request.getIcon() : null);
	entity.setBackgroundColor(
		!StringUtils.isEmpty(request.getBackgroundColor()) ? request.getBackgroundColor() : null);
	entity.setForegroundColor(
		!StringUtils.isEmpty(request.getForegroundColor()) ? request.getForegroundColor() : null);
	entity.setBorderColor(!StringUtils.isEmpty(request.getBorderColor()) ? request.getBorderColor() : null);
    }

    /**
     * Requires that a String field be a non null, non space-filled value.
     * 
     * @param field
     * @throws SiteWhereException
     */
    protected static void require(String fieldName, String field) throws SiteWhereException {
	if (StringUtils.isBlank(field)) {
	    throw new SiteWhereException("Field '" + fieldName + "' is required.");
	}
    }

    /**
     * Require that a non-String field be non-null.
     * 
     * @param field
     * @throws SiteWhereException
     */
    protected static void requireNotNull(String fieldName, Object field) throws SiteWhereException {
	if (field == null) {
	    throw new SiteWhereException("Field '" + fieldName + "' is required.");
	}
    }

    /**
     * Requires that a String field be a non null, non space-filled value.
     * 
     * @param field
     * @throws SiteWhereException
     */
    protected static void requireFormat(String fieldName, String field, String regex, ErrorCode ifFails)
	    throws SiteWhereException {
	require(fieldName, field);
	if (!field.matches(regex)) {
	    throw new SiteWhereSystemException(ifFails, ErrorLevel.ERROR);
	}
    }

    /**
     * Detect whether the request has an updated value.
     * 
     * @param request
     * @param target
     * @return
     */
    protected static boolean isUpdated(Object request, Object target) {
	return (request != null) && (!request.equals(target));
    }
}