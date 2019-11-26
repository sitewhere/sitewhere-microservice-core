/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration.model.instance;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Details of an instance-level InfluxDB configuration.
 */
@RegisterForReflection
public class InfluxDbConfiguration {

    /** Hostname */
    private String hostname;

    /** Port */
    private String port;

    /** Database name */
    private String databaseName;

    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public String getPort() {
	return port;
    }

    public void setPort(String port) {
	this.port = port;
    }

    public String getDatabaseName() {
	return databaseName;
    }

    public void setDatabaseName(String databaseName) {
	this.databaseName = databaseName;
    }
}
