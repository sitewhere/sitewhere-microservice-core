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

import java.util.Deque;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.monitoring.IProgressReporter;

/**
 * Allows progress to be monitored on long-running lifecycle tasks.
 */
public interface ILifecycleProgressMonitor extends IProgressReporter {

    /**
     * Get current list of nested contexts.
     * 
     * @return
     */
    Deque<ILifecycleProgressContext> getContextStack();

    /**
     * Push a new nested context onto the stack.
     * 
     * @param context
     * @throws SiteWhereException
     */
    void pushContext(ILifecycleProgressContext context) throws SiteWhereException;

    /**
     * Start progress on a new operation within the current nesting context.
     * 
     * @param operation
     * @throws SiteWhereException
     */
    void startProgress(String operation) throws SiteWhereException;

    /**
     * Finish progress for the current operation. This results in reporting of
     * progress message.
     * 
     * @throws SiteWhereException
     */
    void finishProgress() throws SiteWhereException;

    /**
     * Pop last context from the stack.
     * 
     * @return
     * @throws SiteWhereException
     */
    ILifecycleProgressContext popContext() throws SiteWhereException;

    /**
     * Get microservice associated with component.
     * 
     * @return
     */
    IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> getMicroservice();
}