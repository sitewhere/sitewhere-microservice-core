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
package com.sitewhere.microservice.configuration.model.instance.persistence;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Instance-level configuration for persistence implementations.
 */
@RegisterForReflection
public class PersistenceConfigurations {

    /** Relational database configurations indexed by id */
    private Map<String, RdbConfiguration> rdbConfigurations = new HashMap<>();

    /** InfluxDB configurations indexed by id */
    private Map<String, TimeSeriesConfiguration> timeSeriesConfigurations = new HashMap<>();

    public Map<String, RdbConfiguration> getRdbConfigurations() {
	return rdbConfigurations;
    }

    public void setRdbConfigurations(Map<String, RdbConfiguration> rdbConfigurations) {
	this.rdbConfigurations = rdbConfigurations;
    }

    public Map<String, TimeSeriesConfiguration> getTimeSeriesConfigurations() {
	return timeSeriesConfigurations;
    }

    public void setTimeSeriesConfigurations(Map<String, TimeSeriesConfiguration> timeSeriesConfigurations) {
	this.timeSeriesConfigurations = timeSeriesConfigurations;
    }
}
