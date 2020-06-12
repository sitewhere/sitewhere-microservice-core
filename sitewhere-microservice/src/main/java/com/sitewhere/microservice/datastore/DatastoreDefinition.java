/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.datastore;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Holds information about a datastore using either a reference or a nested
 * configuration.
 */
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
