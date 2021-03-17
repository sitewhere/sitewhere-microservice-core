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
package com.sitewhere.spi.microservice.instance;

/**
 * Log information for event pipeline processing.
 */
public interface IEventPipelineLog {

    /**
     * Event timestamp.
     * 
     * @return
     */
    long getTimestamp();

    /**
     * Get event source info.
     * 
     * @return
     */
    String getSource();

    /**
     * Get device token.
     * 
     * @return
     */
    String getDeviceToken();

    /**
     * Get log level.
     * 
     * @return
     */
    EventPipelineLogLevel getLevel();

    /**
     * Get microservice handling event.
     * 
     * @return
     */
    String getMicroservice();

    /**
     * Get log message.
     * 
     * @return
     */
    String getMessage();

    /**
     * Get additional detail information.
     * 
     * @return
     */
    String getDetail();
}
