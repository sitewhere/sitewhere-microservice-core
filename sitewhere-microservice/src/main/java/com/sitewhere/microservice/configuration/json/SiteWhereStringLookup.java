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

import org.apache.commons.text.lookup.StringLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Handles variable substitution in configuration attributes.
 */
public class SiteWhereStringLookup implements StringLookup {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(SiteWhereStringLookup.class);

    /** Replace with id of current instance */
    private static final String INSTANCE_ID = "instance.id";

    /** Replace with token of current tenant */
    private static final String TENANT_ID = "tenant.id";

    /** Replace with token of current tenant */
    private static final String TENANT_TOKEN = "tenant.token";

    /** Component to resolve against */
    private ITenantEngineLifecycleComponent component;

    public SiteWhereStringLookup(ITenantEngineLifecycleComponent component) {
	this.component = component;
    }

    /*
     * @see org.apache.commons.text.lookup.StringLookup#lookup(java.lang.String)
     */
    @Override
    public String lookup(String key) {
	// Handle variable reference.
	if (key.indexOf(':') > -1) {
	    String[] parts = key.split(":");
	    String env = parts[0];
	    String defaultValue = parts[1];
	    String envValue = System.getenv().get(env);
	    LOGGER.info(
		    String.format("Looked up '%s' as ENV value %s=%s default=%s", key, env, envValue, defaultValue));
	    return envValue != null ? envValue : defaultValue;
	}
	// Only resolve component-relative references if available.
	if (getComponent() != null) {
	    // Handle replacement for instance id.
	    if (INSTANCE_ID.equals(key)) {
		return getComponent().getMicroservice().getInstanceSettings().getK8s().getNamespace();
	    }
	    // Handle replacement for tenant token.
	    else if (TENANT_TOKEN.equals(key) || TENANT_ID.equals(key)) {
		return getComponent().getTenantEngine().getTenantResource().getMetadata().getName();
	    }
	} else {
	    LOGGER.warn("Skipping string resolution because tenant engine lifecycle component is not set.");
	}
	return null;
    }

    protected ITenantEngineLifecycleComponent getComponent() {
	return component;
    }
}
