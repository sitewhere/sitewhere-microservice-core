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

import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.instance.EventPipelineLogLevel;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link ILifecycleComponent} with ability to access tenant engine.
 */
public interface ITenantEngineLifecycleComponent extends ILifecycleComponent {

    /**
     * Log an entry for event pipeline if level condition is satisfied.
     * 
     * @param source
     * @param deviceToken
     * @param microservice
     * @param message
     * @param detail
     * @param level
     */
    void logPipelineEvent(String source, String deviceToken, IFunctionIdentifier microservice, String message,
	    String detail, EventPipelineLogLevel level);

    /**
     * Log an exception for event pipeline if level condition is satisfied.
     * 
     * @param source
     * @param deviceToken
     * @param microservice
     * @param message
     * @param throwable
     * @param level
     */
    void logPipelineException(String source, String deviceToken, IFunctionIdentifier microservice, String message,
	    Throwable throwable, EventPipelineLogLevel level);

    /**
     * Build microservice/tenant-specific labels.
     * 
     * @param labels
     * @return
     */
    String[] buildLabels(String... labels);

    /**
     * Set tenant engine for component.
     * 
     * @param tenantEngine
     */
    void setTenantEngine(IMicroserviceTenantEngine<?> tenantEngine);

    /**
     * Get tenant engine for component.
     * 
     * @return
     */
    IMicroserviceTenantEngine<?> getTenantEngine();
}