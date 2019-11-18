/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.lifecycle;

/**
 * Parameter used to configure a lifecycle component.
 *
 * @param <T>
 */
public interface ILifecycleComponentParameter<T> {

    /**
     * Get component name.
     * 
     * @return
     */
    String getName();

    /**
     * Get configured value for component.
     * 
     * @return
     */
    T getValue();

    /**
     * Indicates whether the parameter is required.
     * 
     * @return
     */
    boolean isRequired();

    /**
     * Get parent component.
     * 
     * @return
     */
    ILifecycleComponent getParent();
}