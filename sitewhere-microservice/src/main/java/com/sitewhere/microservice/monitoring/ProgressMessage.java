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
package com.sitewhere.microservice.monitoring;

import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.monitoring.IProgressMessage;

/**
 * Contains progress information provided by an
 * {@link ILifecycleProgressMonitor}.
 */
public class ProgressMessage implements IProgressMessage {

    /** Serial version UID */
    private static final long serialVersionUID = 293429181916222135L;

    /** Task name */
    private String taskName;

    /** Percentage complete (between 0 and 100) */
    private double progressPercentage;

    /** Operation message */
    private String message;

    /** Timestamp for message */
    private Long timeStamp;

    public ProgressMessage() {
    }

    public ProgressMessage(String taskName, double progressPercentage, String message) {
	this.taskName = taskName;
	this.progressPercentage = progressPercentage;
	this.message = message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressMessage#getTaskName()
     */
    @Override
    public String getTaskName() {
	return taskName;
    }

    public void setTaskName(String taskName) {
	this.taskName = taskName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressMessage#getProgressPercentage()
     */
    @Override
    public double getProgressPercentage() {
	return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
	this.progressPercentage = progressPercentage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressMessage#getMessage()
     */
    @Override
    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressMessage#getTimeStamp()
     */
    @Override
    public Long getTimeStamp() {
	return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
	this.timeStamp = timeStamp;
    }
}