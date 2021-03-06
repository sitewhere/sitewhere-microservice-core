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
package com.sitewhere.spi.microservice.multitenant;

import com.sitewhere.spi.SiteWhereException;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngineStatus;

/**
 * Operation that mutates the tenant engine resource status.
 */
public interface ITenantEngineStatusUpdateOperation {

    /**
     * Executes the operation in the context of the given tenant engine.
     * 
     * @param engine
     * @return
     * @throws SiteWhereException
     */
    SiteWhereTenantEngine execute(IMicroserviceTenantEngine<?> engine) throws SiteWhereException;

    /**
     * Makes an update to the current tenant engine status.
     * 
     * @param current
     * @throws SiteWhereException
     */
    void update(SiteWhereTenantEngineStatus current) throws SiteWhereException;
}
