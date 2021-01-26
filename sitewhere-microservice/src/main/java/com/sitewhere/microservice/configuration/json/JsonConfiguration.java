/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration.json;

import org.apache.commons.text.StringSubstitutor;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Base class for configuration based on parsing data from a {@link JsonNode}.
 * Supports parameter substitution so that environment variable and other data
 * may be injected.
 */
public class JsonConfiguration {

    /** Component for resolving variable references */
    private ITenantEngineLifecycleComponent component;

    public JsonConfiguration(ITenantEngineLifecycleComponent component) {
	this.component = component;
    }

    /**
     * Create {@link StringSubstitutor} subclass for given configuration type.
     * 
     * @param component
     * @return
     */
    public StringSubstitutor createStringSubstitutor(ITenantEngineLifecycleComponent component) {
	return new StringSubstitutor(new SiteWhereStringLookup(getComponent()));
    }

    /**
     * Parse an integer value using variable substitution.
     * 
     * @param fieldName
     * @param json
     * @param defaultValue
     * @return
     * @throws SiteWhereException
     */
    public int configurableInt(String fieldName, JsonNode json, int defaultValue) throws SiteWhereException {
	JsonNode field = json.get(fieldName);
	if (field == null) {
	    return defaultValue;
	}
	StringSubstitutor sub = createStringSubstitutor(getComponent());
	try {
	    return field.isTextual() ? Integer.parseInt(sub.replace(field.textValue())) : field.asInt();
	} catch (NumberFormatException e) {
	    throw new SiteWhereException(
		    String.format("Unable to parse integer configuration parameter '%s' with value of '%s'.", fieldName,
			    field.toString()));
	}
    }

    /**
     * Parse a string value using variable substitution.
     * 
     * @param fieldName
     * @param json
     * @param defaultValue
     * @return
     * @throws SiteWhereException
     */
    public String configurableString(String fieldName, JsonNode json, String defaultValue) throws SiteWhereException {
	JsonNode field = json.get(fieldName);
	if (field == null) {
	    return defaultValue;
	}
	StringSubstitutor sub = createStringSubstitutor(getComponent());
	return sub.replace(field.textValue());
    }

    /**
     * Parse a boolean value using variable substitution.
     * 
     * @param fieldName
     * @param json
     * @param defaultValue
     * @return
     * @throws SiteWhereException
     */
    public boolean configurableBoolean(String fieldName, JsonNode json, boolean defaultValue)
	    throws SiteWhereException {
	JsonNode field = json.get(fieldName);
	if (field == null) {
	    return defaultValue;
	}
	StringSubstitutor sub = createStringSubstitutor(getComponent());
	try {
	    return field.isBoolean() ? field.asBoolean() : Boolean.parseBoolean(sub.replace(field.textValue()));
	} catch (NumberFormatException e) {
	    throw new SiteWhereException(
		    String.format("Unable to parse boolean configuration parameter '%s' with value of '%s'.", fieldName,
			    field.toString()));
	}
    }

    protected ITenantEngineLifecycleComponent getComponent() {
	return component;
    }
}
