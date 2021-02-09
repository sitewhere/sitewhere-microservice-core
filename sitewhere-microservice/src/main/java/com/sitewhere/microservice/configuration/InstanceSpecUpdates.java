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

import com.sitewhere.spi.microservice.configuration.IInstanceSpecUpdates;

/**
 * Holds flags to indicate which aspects of instance specification were updated.
 */
public class InstanceSpecUpdates implements IInstanceSpecUpdates {

    /** Indicates if there was not a previous instance */
    private boolean firstUpdate;

    /** Indicates whether instance namespace was updated */
    private boolean instanceNamespaceUpdated;

    /** Indicates whether configuration template was updated */
    private boolean configurationTemplateUpdated;

    /** Indicates whether dataset template was updated */
    private boolean datasetTemplateUpdated;

    /** Indicates whether configuration was updated */
    private boolean configurationUpdated;

    /*
     * @see com.sitewhere.spi.microservice.configuration.IInstanceSpecUpdates#
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
     * @see com.sitewhere.spi.microservice.configuration.IInstanceSpecUpdates#
     * isInstanceNamespaceUpdated()
     */
    @Override
    public boolean isInstanceNamespaceUpdated() {
	return instanceNamespaceUpdated;
    }

    public void setInstanceNamespaceUpdated(boolean instanceNamespaceUpdated) {
	this.instanceNamespaceUpdated = instanceNamespaceUpdated;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IInstanceSpecUpdates#
     * isConfigurationTemplateUpdated()
     */
    @Override
    public boolean isConfigurationTemplateUpdated() {
	return configurationTemplateUpdated;
    }

    public void setConfigurationTemplateUpdated(boolean configurationTemplateUpdated) {
	this.configurationTemplateUpdated = configurationTemplateUpdated;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IInstanceSpecUpdates#
     * isDatasetTemplateUpdated()
     */
    @Override
    public boolean isDatasetTemplateUpdated() {
	return datasetTemplateUpdated;
    }

    public void setDatasetTemplateUpdated(boolean datasetTemplateUpdated) {
	this.datasetTemplateUpdated = datasetTemplateUpdated;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IInstanceSpecUpdates#
     * isConfigurationUpdated()
     */
    @Override
    public boolean isConfigurationUpdated() {
	return configurationUpdated;
    }

    public void setConfigurationUpdated(boolean configurationUpdated) {
	this.configurationUpdated = configurationUpdated;
    }
}
