/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration.model.instance;

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
    private Map<String, InfluxDbConfiguration> influxDbConfigurations = new HashMap<>();

    /** Cassandra configurations indexed by id */
    private Map<String, CassandraConfiguration> cassandraConfigurations = new HashMap<>();

    public Map<String, RdbConfiguration> getRdbConfigurations() {
	return rdbConfigurations;
    }

    public void setRdbConfigurations(Map<String, RdbConfiguration> rdbConfigurations) {
	this.rdbConfigurations = rdbConfigurations;
    }

    public Map<String, InfluxDbConfiguration> getInfluxDbConfigurations() {
	return influxDbConfigurations;
    }

    public void setInfluxDbConfigurations(Map<String, InfluxDbConfiguration> influxDbConfigurations) {
	this.influxDbConfigurations = influxDbConfigurations;
    }

    public Map<String, CassandraConfiguration> getCassandraConfigurations() {
	return cassandraConfigurations;
    }

    public void setCassandraConfigurations(Map<String, CassandraConfiguration> cassandraConfigurations) {
	this.cassandraConfigurations = cassandraConfigurations;
    }
}
