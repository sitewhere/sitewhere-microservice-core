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

import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponentParameter;

/**
 * Parameter associated with a lifecycle component.
 *
 * @param <T>
 */
public class LifecycleComponentParameter<T> implements ILifecycleComponentParameter<T> {

    /** Component name */
    private String name;

    /** Component value */
    private T value;

    /** Required */
    private boolean required;

    /** Parent component */
    private ILifecycleComponent parent;

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter#getValue()
     */
    @Override
    public T getValue() {
	return value;
    }

    public void setValue(T value) {
	this.value = value;
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter#isRequired()
     */
    @Override
    public boolean isRequired() {
	return required;
    }

    public void setRequired(boolean required) {
	this.required = required;
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter#getParent()
     */
    @Override
    public ILifecycleComponent getParent() {
	return parent;
    }

    public void setParent(ILifecycleComponent parent) {
	this.parent = parent;
    }
}