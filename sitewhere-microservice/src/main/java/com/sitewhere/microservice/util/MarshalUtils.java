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
package com.sitewhere.microservice.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MarshalUtils {

    /** Singleton object mapper for JSON marshaling */
    public static ObjectMapper MAPPER = new ObjectMapper();

    /** Singleton mapper with pretty print turned on */
    public static ObjectMapper PRETTY_MAPPER = new ObjectMapper();

    // Enable pretty printing on the mapper.
    static {
	PRETTY_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Marshal an object to a byte array.
     * 
     * @param object
     * @return
     */
    public static byte[] marshalJson(Object object) {
	try {
	    return MAPPER.writeValueAsBytes(object);
	} catch (JsonProcessingException e) {
	    throw new RuntimeException("Could not marshal object as JSON: " + object.getClass().getName(), e);
	}
    }

    /**
     * Marshal an object to a JSON string.
     * 
     * @param object
     * @return
     */
    public static String marshalJsonAsString(Object object) {
	try {
	    return (object != null) ? MAPPER.writeValueAsString(object) : "NULL";
	} catch (JsonProcessingException e) {
	    throw new RuntimeException("Could not marshal object as JSON: " + object.getClass().getName(), e);
	}
    }

    /**
     * Marshal an object to a formatted JSON string.
     * 
     * @param object
     * @return
     */
    public static String marshalJsonAsPrettyString(Object object) {
	try {
	    return (object != null) ? PRETTY_MAPPER.writeValueAsString(object) : "NULL";
	} catch (JsonProcessingException e) {
	    throw new RuntimeException("Could not marshal object as JSON: " + object.getClass().getName(), e);
	}
    }

    /**
     * Unmarshal a JSON string to an object.
     * 
     * @param json
     * @param type
     * @return
     */
    public static <T> T unmarshalJson(byte[] json, Class<T> type) {
	try {
	    return MAPPER.readValue(json, type);
	} catch (Throwable e) {
	    throw new RuntimeException("Unable to parse JSON.", e);
	}
    }

    /**
     * Marshal a response to a JsonNode.
     * 
     * @param json
     * @return
     * @throws IOException
     */
    public static JsonNode marshalJsonNode(byte[] json) throws IOException {
	return MAPPER.readTree(json);
    }

    /**
     * Unmarshal a {@link JsonNode} to an object.
     * 
     * @param <T>
     * @param json
     * @param type
     * @return
     * @throws JsonProcessingException
     */
    public static <T> T unmarshalJsonNode(JsonNode json, Class<T> type) throws JsonProcessingException {
	return MAPPER.treeToValue(json, type);
    }
}
