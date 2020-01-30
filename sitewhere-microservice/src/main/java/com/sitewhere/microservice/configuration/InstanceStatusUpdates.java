/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import com.sitewhere.spi.microservice.configuration.IInstanceStatusUpdates;

/**
 * Holds flags to indicate which aspects of instance status were updated.
 */
public class InstanceStatusUpdates implements IInstanceStatusUpdates {

    /** Indicates if there was not a previous instance */
    private boolean firstUpdate;

    /** Indicates whether tenant management bootstrap state was updated */
    private boolean tenantManagementBootstrapStateUpdated;

    /** Indicates whether user management bootstrap state was updated */
    private boolean userManagementBootstrapStateUpdated;

    /*
     * @see com.sitewhere.spi.microservice.configuration.IInstanceStatusUpdates#
     * isFirstUpdate()
     */
    @Override
    public boolean isFirstUpdate() {
	return firstUpdate;
    }

    public void setFirstUpdate(boolean firstUpdate) {
	this.firstUpdate = firstUpdate;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IInstanceStatusUpdates#
     * isTenantManagementBootstrapStateUpdated()
     */
    @Override
    public boolean isTenantManagementBootstrapStateUpdated() {
	return tenantManagementBootstrapStateUpdated;
    }

    public void setTenantManagementBootstrapStateUpdated(boolean tenantManagementBootstrapStateUpdated) {
	this.tenantManagementBootstrapStateUpdated = tenantManagementBootstrapStateUpdated;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IInstanceStatusUpdates#
     * isUserManagementBootstrapStateUpdated()
     */
    @Override
    public boolean isUserManagementBootstrapStateUpdated() {
	return userManagementBootstrapStateUpdated;
    }

    public void setUserManagementBootstrapStateUpdated(boolean userManagementBootstrapStateUpdated) {
	this.userManagementBootstrapStateUpdated = userManagementBootstrapStateUpdated;
    }
}
