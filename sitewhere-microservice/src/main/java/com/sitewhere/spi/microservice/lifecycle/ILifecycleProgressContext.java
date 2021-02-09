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
 * Provides context for a monitoring a lifecycle operation.
 */
public interface ILifecycleProgressContext {

    /**
     * Get task name for context.
     * 
     * @return
     */
    String getTaskName();

    /**
     * Get number of operations expected within context.
     * 
     * @return
     */
    int getOperationCount();

    /**
     * Get index of current operation.
     * 
     * @return
     */
    int getCurrentOperationIndex();

    /**
     * Set index of current operation.
     * 
     * @param index
     */
    void setCurrentOperationIndex(int index);

    /**
     * Get message associated with current operation.
     * 
     * @return
     */
    String getCurrentOperationMessage();

    /**
     * Set message associated with current operation.
     * 
     * @param message
     */
    void setCurrentOperationMessage(String message);
}