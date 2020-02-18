/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration.model.instance.persistence;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Details of an instance-level Cassandra configuration.
 */
@RegisterForReflection
public class CassandraConfiguration {

    /** Contact points */
    private String contactPoints;

    /** Keyspace */
    private String keyspace;

    public String getContactPoints() {
	return contactPoints;
    }

    public void setContactPoints(String contactPoints) {
	this.contactPoints = contactPoints;
    }

    public String getKeyspace() {
	return keyspace;
    }

    public void setKeyspace(String keyspace) {
	this.keyspace = keyspace;
    }
}
