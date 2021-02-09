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

import com.sitewhere.spi.SiteWhereException;

/**
 * Lifecycle component which handles the "start" phase asynchronously and allows
 * interested external components to wait for the async start to complete.
 */
public interface IAsyncStartLifecycleComponent extends ILifecycleComponent {

    /**
     * Implements the functionality which will be executed asynchronously.
     * 
     * @throws SiteWhereException
     */
    void asyncStart() throws SiteWhereException;

    /**
     * Indicates whether the async startup tasks have completed.
     * 
     * @return
     */
    boolean isComponentStarted();

    /**
     * Blocks the current thread waiting for the "start" phase to complete.
     */
    void waitForComponentStarted();
}