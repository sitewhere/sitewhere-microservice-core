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
 * Details of an instance-level timeseries database configuration.
 */
@RegisterForReflection
public class TimeSeriesConfiguration extends DatastoreConfiguration {
}
