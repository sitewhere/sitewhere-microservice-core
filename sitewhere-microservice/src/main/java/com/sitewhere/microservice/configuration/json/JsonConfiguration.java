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
package com.sitewhere.microservice.configuration.json;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Base class for configuration based on parsing data from a {@link JsonNode}.
 * Supports parameter substitution so that environment variable and other data
 * may be injected.
 */
public abstract class JsonConfiguration {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(JsonConfiguration.class);

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
	    LOGGER.info(String.format("Using defaulted value '%s' for field '%s'", String.valueOf(defaultValue),
		    fieldName));
	    return defaultValue;
	}
	StringSubstitutor sub = createStringSubstitutor(getComponent());
	try {
	    int configured = field.isTextual() ? Integer.parseInt(sub.replace(field.textValue())) : field.asInt();
	    LOGGER.info(
		    String.format("Using configured value '%s' for field '%s'", String.valueOf(configured), fieldName));
	    return configured;
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
	StringSubstitutor sub = createStringSubstitutor(getComponent());
	if (field == null) {
	    String defaulted = sub.replace(defaultValue);
	    LOGGER.info(String.format("Using defaulted value '%s' for field '%s'", defaulted, fieldName));
	    return defaultValue;
	}
	String configured = sub.replace(field.textValue());
	LOGGER.info(String.format("Using configured value '%s' for field '%s'", configured, fieldName));
	return configured;
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
	    LOGGER.info(String.format("Using defaulted value '%s' for field '%s'", String.valueOf(defaultValue),
		    fieldName));
	    return defaultValue;
	}
	StringSubstitutor sub = createStringSubstitutor(getComponent());
	try {
	    boolean configured = field.isBoolean() ? field.asBoolean()
		    : Boolean.parseBoolean(sub.replace(field.textValue()));
	    LOGGER.info(
		    String.format("Using configured value '%s' for field '%s'", String.valueOf(configured), fieldName));
	    return configured;
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
