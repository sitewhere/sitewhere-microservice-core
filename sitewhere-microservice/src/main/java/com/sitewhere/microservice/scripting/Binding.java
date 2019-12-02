/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.util.HashMap;
import java.util.Map;

/**
 * Placeholder for variable binding.
 */
public class Binding {

    /** Map of bound objects */
    private Map<String, Object> boundObjects = new HashMap<>();

    public void setVariable(String name, Object value) {
	getBoundObjects().put(name, value);
    }

    public Map<String, Object> getBoundObjects() {
	return boundObjects;
    }

    public void setBoundObjects(Map<String, Object> boundObjects) {
	this.boundObjects = boundObjects;
    }
}
