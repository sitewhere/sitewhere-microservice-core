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

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;

/**
 * Component that manages the list of script templates that are available for a
 * microservice.
 */
public interface IScriptTemplateManager extends ILifecycleComponent {

    /**
     * Get list of templates that provide examples of various types of scripts.
     * 
     * @return
     * @throws SiteWhereException
     */
    List<IScriptTemplate> getScriptTemplates() throws SiteWhereException;

    /**
     * Get content for a script template.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    byte[] getScriptTemplateContent(String id) throws SiteWhereException;
}