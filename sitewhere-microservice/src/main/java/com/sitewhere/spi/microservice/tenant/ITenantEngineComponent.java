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
package com.sitewhere.spi.microservice.tenant;

import java.io.Serializable;

import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;

/**
 * Interface for tenant engine components.
 */
public interface ITenantEngineComponent extends Serializable {

    /**
     * Get component id.
     * 
     * @return
     */
    String getId();

    /**
     * Get component name.
     * 
     * @return
     */
    String getName();

    /**
     * Get component type.
     * 
     * @return
     */
    LifecycleComponentType getType();

    /**
     * Get component status.
     * 
     * @return
     */
    LifecycleStatus getStatus();

    /**
     * Get parent component id.
     * 
     * @return
     */
    String getParentId();
}