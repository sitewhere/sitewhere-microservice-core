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

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Resolves parameter values that contain variables that should be substituted
 * at runtime.
 */
public class ValueResolver {

    /** Prefix for variables */
    private static final String PREFIX = "[[";

    /** Suffix for variables */
    private static final String SUFFIX = "]]";

    /** Tenant id marker */
    private static final String TENANT_ID = "tenant.id";

    /**
     * Resolve variable expressions within a value.
     * 
     * @param value
     * @param context
     * @return
     * @throws SiteWhereException
     */
    public static String resolve(String value, ILifecycleComponent context) throws SiteWhereException {
	IMicroserviceTenantEngine<?> engine = (context instanceof ITenantEngineLifecycleComponent)
		? ((ITenantEngineLifecycleComponent) context).getTenantEngine()
		: null;
	if ((engine == null) && (value.indexOf(asVariable(TENANT_ID)) != -1)) {
	    throw new SiteWhereException("Unable to resolve reference to tenant id in a global component.");
	}

	String result = value;
	Map<String, String> variables = new HashMap<String, String>();
	if (engine != null) {
	    variables.put(TENANT_ID, engine.getTenantResource().getMetadata().getName());
	}
	for (String key : variables.keySet()) {
	    result = result.replace(asVariable(key), variables.get(key));
	}
	return result;
    }

    /**
     * Get key value as it would look as a variable.
     * 
     * @param key
     * @return
     */
    protected static String asVariable(String key) {
	return PREFIX + key + SUFFIX;
    }
}
