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
