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

import org.graalvm.polyglot.Source;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptingComponent;

/**
 * Common base class for scripting components.
 */
public abstract class ScriptingComponent<T> extends TenantEngineLifecycleComponent implements IScriptingComponent<T> {

    /** Unique identifier for id within context */
    private String scriptId;

    public ScriptingComponent() {
    }

    public ScriptingComponent(LifecycleComponentType type) {
	super(type);
    }

    public static Binding createBindingFor(IScriptingComponent<?> component) {
	return new Binding();
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptingComponent#run(com.
     * sitewhere.microservice.scripting.Binding)
     */
    @Override
    public T run(Binding binding) throws SiteWhereException {
	Source source = getTenantEngine().getScriptManager().resolveScriptSource(getScriptId());
	return ScriptingUtils.run(source, binding);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptingComponent#getScriptId()
     */
    @Override
    public String getScriptId() {
	return scriptId;
    }

    public void setScriptId(String scriptId) {
	this.scriptId = scriptId;
    }
}
