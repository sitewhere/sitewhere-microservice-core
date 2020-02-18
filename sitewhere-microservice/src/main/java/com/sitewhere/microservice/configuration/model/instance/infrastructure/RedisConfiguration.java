/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration.model.instance.infrastructure;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Configuration for Redis connectivity.
 */
@RegisterForReflection
public class RedisConfiguration {

    /** Hostname */
    private String hostname;

    /** Port */
    private int port;

    /** Number of nodes in cluster */
    private int nodeCount;

    /** Sentinel master group name */
    private String masterGroupName;

    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public int getNodeCount() {
	return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
	this.nodeCount = nodeCount;
    }

    public String getMasterGroupName() {
	return masterGroupName;
    }

    public void setMasterGroupName(String masterGroupName) {
	this.masterGroupName = masterGroupName;
    }
}
