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