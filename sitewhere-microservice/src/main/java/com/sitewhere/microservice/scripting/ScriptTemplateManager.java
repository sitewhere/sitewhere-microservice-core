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
package com.sitewhere.microservice.scripting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptTemplate;
import com.sitewhere.spi.microservice.scripting.IScriptTemplateManager;

/**
 * Manages the list of script templates for a microservice.
 */
public class ScriptTemplateManager extends LifecycleComponent implements IScriptTemplateManager {

    /** Map of script templates by template id */
    private Map<String, IScriptTemplate> scriptTemplatesById = new HashMap<String, IScriptTemplate>();

    public ScriptTemplateManager() {
	super(LifecycleComponentType.ScriptTemplateManager);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Initialize scripts templates.
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptTemplateManager#
     * getScriptTemplates()
     */
    @Override
    public List<IScriptTemplate> getScriptTemplates() throws SiteWhereException {
	List<IScriptTemplate> templates = new ArrayList<IScriptTemplate>(getScriptTemplatesById().values());
	Collections.sort(templates, new Comparator<IScriptTemplate>() {

	    @Override
	    public int compare(IScriptTemplate o1, IScriptTemplate o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
	return templates;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptTemplateManager#
     * getScriptTemplateContent(java.lang.String)
     */
    @Override
    public byte[] getScriptTemplateContent(String id) throws SiteWhereException {
	return new byte[0];
    }

    protected Map<String, IScriptTemplate> getScriptTemplatesById() {
	return scriptTemplatesById;
    }
}