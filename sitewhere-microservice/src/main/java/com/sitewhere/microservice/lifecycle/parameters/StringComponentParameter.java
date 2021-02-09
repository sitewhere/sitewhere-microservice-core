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
package com.sitewhere.microservice.lifecycle.parameters;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponentParameter;

/**
 * Parameter which wraps a String value.
 */
public class StringComponentParameter extends LifecycleComponentParameter<String> {

    /**
     * Create a new builder.
     * 
     * @param parent
     * @param name
     * @return
     */
    public static Builder newBuilder(ILifecycleComponent parent, String name) {
	return new Builder(parent, name);
    }

    /**
     * Builder pattern for creating parameter.
     */
    public static class Builder {

	private StringComponentParameter parameter;

	public Builder(ILifecycleComponent parent, String name) {
	    this.parameter = new StringComponentParameter();
	    parameter.setParent(parent);
	    parameter.setName(name);
	}

	public Builder value(String value) throws SiteWhereException {
	    String resolved = ValueResolver.resolve(value, parameter.getParent());
	    parameter.setValue(resolved);
	    return this;
	}

	public Builder makeRequired() {
	    parameter.setRequired(true);
	    return this;
	}

	public ILifecycleComponentParameter<String> build() {
	    return parameter;
	}
    }
}