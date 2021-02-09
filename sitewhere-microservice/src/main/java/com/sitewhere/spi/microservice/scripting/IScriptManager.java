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
package com.sitewhere.spi.microservice.scripting;

import org.graalvm.polyglot.Source;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersion;

/**
 * Manages runtime scripting support for a microservice or tenant engine.
 */
public interface IScriptManager extends ITenantEngineLifecycleComponent {

    /**
     * Adds content for a managed script.
     * 
     * @param script
     * @param version
     * @throws SiteWhereException
     */
    void addScript(SiteWhereScript script, SiteWhereScriptVersion version) throws SiteWhereException;

    /**
     * Remove cached script information for the given script.
     * 
     * @param script
     */
    void removeScript(SiteWhereScript script);

    /**
     * Resolve script source based on identifier.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    Source resolveScriptSource(String identifier) throws SiteWhereException;
}