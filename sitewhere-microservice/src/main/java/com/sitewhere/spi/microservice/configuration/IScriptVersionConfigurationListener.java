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

import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersion;

/**
 * Listens for changes in script version configuration.
 */
public interface IScriptVersionConfigurationListener {

    /**
     * Called when script version configuration is added.
     * 
     * @param version
     */
    void onScriptVersionAdded(SiteWhereScriptVersion version);

    /**
     * Called when script version configuration is updated.
     * 
     * @param version
     * @param updates
     */
    void onScriptVersionUpdated(SiteWhereScriptVersion version, IScriptVersionSpecUpdates updates);

    /**
     * Called when script version configuration is deleted.
     * 
     * @param version
     */
    void onScriptVersionDeleted(SiteWhereScriptVersion version);
}
