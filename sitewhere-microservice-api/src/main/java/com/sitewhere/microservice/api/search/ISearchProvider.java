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