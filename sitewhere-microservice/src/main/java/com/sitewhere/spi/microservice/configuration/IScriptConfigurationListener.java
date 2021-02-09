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
package com.sitewhere.spi.microservice.configuration;

import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript;

/**
 * Listens for changes in script configuration.
 */
public interface IScriptConfigurationListener {

    /**
     * Called when script configuration is added.
     * 
     * @param script
     */
    void onScriptAdded(SiteWhereScript script);

    /**
     * Called when script configuration is updated.
     * 
     * @param script
     * @param updates
     */
    void onScriptUpdated(SiteWhereScript script, IScriptSpecUpdates updates);

    /**
     * Called when script configuration is deleted.
     * 
     * @param script
     */
    void onScriptDeleted(SiteWhereScript script);
}
