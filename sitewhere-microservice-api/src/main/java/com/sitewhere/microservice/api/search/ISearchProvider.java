/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.api.search;

import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;

/**
 * Implemented by external search providers that index SiteWhere data.
 */
public interface ISearchProvider extends ILifecycleComponent {

    /**
     * Get unique id of search provider.
     * 
     * @return
     */
    String getId();

    /**
     * Get a human-readable name for the search provider.
     * 
     * @return
     */
    String getName();
}