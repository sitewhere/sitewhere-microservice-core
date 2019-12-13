/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import com.sitewhere.spi.microservice.configuration.IInstanceSpecUpdates;

/**
 * Holds flags to indicate which aspects of instance specification were updated.
 */
public class InstanceSpecUpdates implements IInstanceSpecUpdates {

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
