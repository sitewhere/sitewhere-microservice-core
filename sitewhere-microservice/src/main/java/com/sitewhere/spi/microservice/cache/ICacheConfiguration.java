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
package com.sitewhere.spi.microservice.cache;

/**
 * Provides settings which control how a cache is to be configured.
 */
public interface ICacheConfiguration {

    /**
     * Get time to live in seconds.
     * 
     * @return
     */
    public int getTtlInSeconds();

    /**
     * Set time to live in seconds.
     * 
     * @param value
     */
    public void setTtlInSeconds(int value);

    /**
     * Indicates whether cache is enabled.
     * 
     * @return
     */
    public boolean isEnabled();

    /**
     * Set cache enablement.
     * 
     * @param value
     */
    public void setEnabled(boolean value);
}