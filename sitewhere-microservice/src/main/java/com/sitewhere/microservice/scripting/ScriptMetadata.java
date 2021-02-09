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
import java.util.List;

import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.microservice.scripting.IScriptVersion;

/**
 * Contains metadata about a script.
 */
public class ScriptMetadata implements IScriptMetadata {

    /** Script id */
    private String id;

    /** Script name */
    private String name;

    /** Short description */
    private String description;

    /** Interpreter type */
    private String interpreterType;

    /** Script category */
    private String category;

    /** Active version identifier */
    private String activeVersion;

    /** List of available versions */
    private List<IScriptVersion> versions = new ArrayList<>();

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptMetadata#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptMetadata#getName()
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
     * com.sitewhere.spi.microservice.scripting.IScriptMetadata#getDescription()
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
     * com.sitewhere.spi.microservice.scripting.IScriptMetadata#getInterpreterType()
     */
    @Override
    public String getInterpreterType() {
	return interpreterType;
    }

    public void setInterpreterType(String interpreterType) {
	this.interpreterType = interpreterType;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptMetadata#getCategory()
     */
    @Override
    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptMetadata#getActiveVersion()
     */
    @Override
    public String getActiveVersion() {
	return activeVersion;
    }

    public void setActiveVersion(String activeVersion) {
	this.activeVersion = activeVersion;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptMetadata#getVersions()
     */
    @Override
    public List<IScriptVersion> getVersions() {
	return versions;
    }

    public void setVersions(List<ScriptVersion> versions) {
	getVersions().clear();
	getVersions().addAll(versions);
    }
}