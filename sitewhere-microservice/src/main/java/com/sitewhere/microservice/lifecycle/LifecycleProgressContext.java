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
package com.sitewhere.microservice.lifecycle;

import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressContext;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Provides context for a nesting level in an {@link ILifecycleProgressMonitor}.
 */
public class LifecycleProgressContext implements ILifecycleProgressContext {

    /** Get task name */
    private String taskName;

    /** Count of total operations for context */
    private int operationCount;

    /** Current operations index */
    private int currentOperationIndex;

    /** Current operation message */
    private String currentOperationMessage;

    public LifecycleProgressContext(int operationCount, String taskName) {
	this.operationCount = operationCount;
	this.taskName = taskName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#getTaskName( )
     */
    public String getTaskName() {
	return taskName;
    }

    public void setTaskName(String taskName) {
	this.taskName = taskName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#
     * getOperationCount()
     */
    @Override
    public int getOperationCount() {
	return operationCount;
    }

    public void setOperationCount(int operationCount) {
	this.operationCount = operationCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#
     * getCurrentOperationIndex()
     */
    @Override
    public int getCurrentOperationIndex() {
	return currentOperationIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#
     * setCurrentOperationIndex(int)
     */
    public void setCurrentOperationIndex(int currentOperationIndex) {
	this.currentOperationIndex = currentOperationIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#
     * getCurrentOperationMessage()
     */
    @Override
    public String getCurrentOperationMessage() {
	return currentOperationMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#
     * setCurrentOperationMessage(java.lang.String)
     */
    @Override
    public void setCurrentOperationMessage(String currentOperationMessage) {
	this.currentOperationMessage = currentOperationMessage;
    }
}