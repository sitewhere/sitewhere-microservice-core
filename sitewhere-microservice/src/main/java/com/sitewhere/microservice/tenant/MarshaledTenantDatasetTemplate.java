/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.tenant;

import com.sitewhere.spi.microservice.multitenant.ITenantDatasetTemplate;

public class MarshaledTenantDatasetTemplate implements ITenantDatasetTemplate {

    /** Template id */
    private String id;

    /** Template name */
    private String name;

    /** Template description */
    private String description;

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.ITenantDatasetTemplate#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.ITenantDatasetTemplate#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantDatasetTemplate#
     * getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }
}
