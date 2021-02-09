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
package com.sitewhere.microservice.metrics;

import java.io.IOException;

import com.sitewhere.microservice.configuration.model.instance.infrastructure.MetricsConfiguration;
import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.metrics.IMetricsServer;

import io.prometheus.client.exporter.HTTPServer;

/**
 * Provides Prometheus metrics over HTTP.
 */
public class MetricsServer extends LifecycleComponent implements IMetricsServer {

    /** HTTP metric export server */
    private HTTPServer httpServer;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	MetricsConfiguration metrics = getMicroservice().getInstanceConfiguration().getInfrastructure().getMetrics();
	if (getHttpServer() != null) {
	    getHttpServer().stop();
	}
	try {
	    if (metrics.isEnabled()) {
		this.httpServer = new HTTPServer(metrics.getHttpPort());
		getLogger().info(
			String.format("Microservice metrics available via HTTP on port %s.", metrics.getHttpPort()));
	    } else {
		getLogger().info("Metrics HTTP server is configured as disabled.");
	    }
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to initialize metrics HTTP server.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getHttpServer() != null) {
	    getHttpServer().stop();
	}
    }

    protected HTTPServer getHttpServer() {
	return httpServer;
    }
}
