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
package com.sitewhere.microservice.datastore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Holds information about a datastore using either a reference or a nested
 * configuration.
 */
@JsonInclude(Include.NON_NULL)
public class DatastoreDefinition {

    /** Datastore type */
    private String type;

    /** Reference to a global definition */
    private String reference;

    /** Nested datasore configuration */
    private JsonNode configuration;

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getReference() {
	return reference;
    }

    public void setReference(String reference) {
	this.reference = reference;
    }

    public JsonNode getConfiguration() {
	return configuration;
    }

    public void setConfiguration(JsonNode configuration) {
	this.configuration = configuration;
    }
}
