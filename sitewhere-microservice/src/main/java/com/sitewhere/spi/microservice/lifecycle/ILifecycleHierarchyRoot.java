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
package com.sitewhere.spi.microservice.lifecycle;

import java.util.List;

/**
 * Offers operations for a hierarchy of nested lifecycle components.
 */
public interface ILifecycleHierarchyRoot {

    /**
     * Get list of components that have registered to participate in the server
     * component lifecycle.
     * 
     * @return
     */
    List<ILifecycleComponent> getRegisteredLifecycleComponents();

    /**
     * Gets an {@link ILifecycleComponent} by unique id.
     * 
     * @param id
     * @return
     */
    ILifecycleComponent getLifecycleComponentById(String id);
}