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
package com.sitewhere.spi.microservice.monitoring;

import java.io.Serializable;

/**
 * Message sent to indicate progress for a long-running task.
 */
public interface IProgressMessage extends Serializable {

    /**
     * Get name of overall task being monitored.
     * 
     * @return
     */
    String getTaskName();

    /**
     * Get progress value as a number between 0 and 100.
     * 
     * @return
     */
    double getProgressPercentage();

    /**
     * Get message shown for current operation.
     * 
     * @return
     */
    String getMessage();

    /**
     * Get timestamp for message.
     * 
     * @return
     */
    Long getTimeStamp();
}