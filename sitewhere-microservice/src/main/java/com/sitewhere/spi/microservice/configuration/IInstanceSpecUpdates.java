/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

/**
 * Holds flags to indicate which aspects of instance specification were updated.
 */
public interface IInstanceSpecUpdates {

    /**
     * Indicates if instance is considered new.
     * 
     * @return
     */
    boolean isFirstUpdate();

    /**
     * Indicates whether instance namespace was updated.
     * 
     * @return
     */
    boolean isInstanceNamespaceUpdated();

    /**
     * Indicates whether configuration template was updated.
     * 
     * @return
     */
    boolean isConfigurationTemplateUpdated();

    /**
     * Indicates whether dataset template was updated.
     * 
     * @return
     */
    boolean isDatasetTemplateUpdated();

    /**
     * Indicates whether configuration was updated.
     * 
     * @return
     */
    boolean isConfigurationUpdated();
}
