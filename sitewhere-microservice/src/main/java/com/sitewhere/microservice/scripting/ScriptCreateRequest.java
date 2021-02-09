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

import com.sitewhere.spi.microservice.scripting.IScriptCreateRequest;

/**
 * Information required to create a new script.
 */
public class ScriptCreateRequest implements IScriptCreateRequest {

    /** Script id */
    private String id;

    /** Script name */
    private String name;

    /** Short description */
    private String description;

    /** Category */
    private String category;

    /** Interpreter type */
    private String interpreterType;

    /** Script content (base 64 encoded) */
    private String content;

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#getName()
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
     * com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#getDescription(
     * )
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#getCategory()
     */
    @Override
    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#
     * getInterpreterType()
     */
    @Override
    public String getInterpreterType() {
	return interpreterType;
    }

    public void setInterpreterType(String interpreterType) {
	this.interpreterType = interpreterType;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#getContent()
     */
    @Override
    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }
}