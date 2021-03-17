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
package com.sitewhere.microservice.lifecycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sitewhere.microservice.configuration.model.instance.debugging.Debugging;
import com.sitewhere.microservice.instance.EventPipelineLog;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.instance.EventPipelineLogLevel;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

/**
 * Base class for implementing {@link ITenantEngineLifecycleComponent}.
 */
public abstract class TenantEngineLifecycleComponent extends LifecycleComponent
	implements ITenantEngineLifecycleComponent {

    /** Namespace prefix added for metrics */
    private static final String METRIC_PREFIX = "sitewhere_";

    /** Metrics label for microservice type */
    private static final String LABEL_MICROSERVICE = "microservice";

    /** Metrics label for microservice pod IP */
    private static final String LABEL_POD_IP = "pod";

    /** Metrics label for microservice tenant id */
    private static final String LABEL_TENANT_ID = "tenant";

    /** Tenant engine associated with component */
    private IMicroserviceTenantEngine<?> tenantEngine;

    public TenantEngineLifecycleComponent() {
	super(LifecycleComponentType.Other);
    }

    public TenantEngineLifecycleComponent(LifecycleComponentType type) {
	super(type);
    }

    /**
     * Creates a gauge metric with labels for slicing by microservice and tenant.
     * 
     * @param name
     * @param description
     * @param labelNames
     * @return
     */
    public static Gauge createGaugeMetric(String name, String description, String... labelNames) {
	return Gauge.build().name(METRIC_PREFIX + name).help(description).labelNames(mergeLabels(labelNames))
		.register();
    }

    /**
     * Creates a counter metric with labels for slicing by microservice and tenant.
     * 
     * @param name
     * @param description
     * @param labelNames
     * @return
     */
    public static Counter createCounterMetric(String name, String description, String... labelNames) {
	return Counter.build().name(METRIC_PREFIX + name).help(description).labelNames(mergeLabels(labelNames))
		.register();
    }

    /**
     * Creates a histogram metric with labels for slicing by microservice and
     * tenant.
     * 
     * @param name
     * @param description
     * @param labelNames
     * @return
     */
    public static Histogram createHistogramMetric(String name, String description, String... labelNames) {
	return Histogram.build().name(METRIC_PREFIX + name).help(description).labelNames(mergeLabels(labelNames))
		.register();
    }

    /**
     * Get topic used for pub/sub on pipeline event log.
     * 
     * @param component
     * @return
     */
    public static String getLogPipelineTopic(ITenantEngineLifecycleComponent component) {
	return component.getTenantEngine().getTenantResource().getMetadata().getName() + ":pipeline-event-log";
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent#
     * logPipelineEvent(java.lang.String, java.lang.String,
     * com.sitewhere.spi.microservice.IFunctionIdentifier, java.lang.String,
     * java.lang.String,
     * com.sitewhere.spi.microservice.instance.EventPipelineLogLevel)
     */
    @Override
    public void logPipelineEvent(String source, String deviceToken, IFunctionIdentifier microservice, String message,
	    String detail, EventPipelineLogLevel level) {
	Debugging debugging = getMicroservice().getInstanceConfiguration().getDebugging();
	if (debugging != null && debugging.getEventPipeLine() != null
		&& debugging.getEventPipeLine().getDebugLevel() != null) {
	    EventPipelineLogLevel desired = EventPipelineLogLevel
		    .getLevelForPrefix(debugging.getEventPipeLine().getDebugLevel());
	    if (level.getLevel() >= desired.getLevel()) {
		EventPipelineLog log = new EventPipelineLog();
		log.setTimestamp(System.currentTimeMillis());
		log.setSource(source);
		log.setLevel(level);
		log.setDeviceToken(deviceToken);
		log.setMicroservice(microservice.getPath());
		log.setMessage(message);
		log.setDetail(detail);

		String topic = getLogPipelineTopic(this);
		getMicroservice().getRedisStreamConnection().sync().xadd(topic, log.toMap());
		if (getLogger().isDebugEnabled()) {
		    getLogger().debug(String.format("Sent pipeline event: %s %s %s", log.getSource(),
			    log.getDeviceToken(), log.getMessage()));
		}
	    } else {
		if (getLogger().isDebugEnabled()) {
		    getLogger().debug(String.format("Skipped pipeline event due to filtered error level: %s %s %s",
			    source, deviceToken, message));
		}
	    }
	} else {
	    if (getLogger().isDebugEnabled()) {
		getLogger().debug(String.format("Skipped pipeline event due to missing debug settings: %s %s %s",
			source, deviceToken, message));
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent#
     * logPipelineException(java.lang.String, java.lang.String,
     * com.sitewhere.spi.microservice.IFunctionIdentifier, java.lang.String,
     * java.lang.Throwable,
     * com.sitewhere.spi.microservice.instance.EventPipelineLogLevel)
     */
    @Override
    public void logPipelineException(String source, String deviceToken, IFunctionIdentifier microservice,
	    String message, Throwable throwable, EventPipelineLogLevel level) {
	logPipelineEvent(source, deviceToken, microservice, message, throwable.getMessage(), level);
	switch (level) {
	case Debug: {
	    getLogger().debug(message, throwable);
	    break;
	}
	case Error: {
	    getLogger().error(message, throwable);
	    break;
	}
	case Info: {
	    getLogger().info(message, throwable);
	    break;
	}
	case Warning: {
	    getLogger().warn(message, throwable);
	    break;
	}
	}
    }

    /**
     * Merge standard SiteWhere labels before extras.
     * 
     * @param labelNames
     * @return
     */
    protected static String[] mergeLabels(String... labelNames) {
	List<String> all = new ArrayList<>();
	all.addAll(Arrays.asList(labelNames));
	all.add(0, LABEL_TENANT_ID);
	all.add(0, LABEL_POD_IP);
	all.add(0, LABEL_MICROSERVICE);
	return all.toArray(new String[all.size()]);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * buildLabels(java.lang.String[])
     */
    @Override
    public String[] buildLabels(String... labels) {
	List<String> all = new ArrayList<>();
	all.addAll(Arrays.asList(labels));
	all.add(0, getTenantEngine().getTenantResource().getMetadata().getName());
	all.add(0, getMicroservice().getInstanceSettings().getKubernetesPodAddress());
	all.add(0, getMicroservice().getIdentifier().getPath());
	return all.toArray(new String[all.size()]);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initializeNestedComponent(
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor, boolean)
     */
    @Override
    public void initializeNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor,
	    boolean require) throws SiteWhereException {
	if (component instanceof ITenantEngineLifecycleComponent) {
	    ((ITenantEngineLifecycleComponent) component).setTenantEngine(getTenantEngine());
	}
	super.initializeNestedComponent(component, monitor, require);
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#startNestedComponent(
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor, boolean)
     */
    @Override
    public void startNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor, boolean require)
	    throws SiteWhereException {
	if (component instanceof ITenantEngineLifecycleComponent) {
	    ((ITenantEngineLifecycleComponent) component).setTenantEngine(getTenantEngine());
	}
	super.startNestedComponent(component, monitor, require);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent#
     * getTenantEngine()
     */
    @Override
    public IMicroserviceTenantEngine<?> getTenantEngine() {
	return tenantEngine;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent#
     * setTenantEngine(com.sitewhere.spi.microservice.multitenant.
     * IMicroserviceTenantEngine)
     */
    @Override
    public void setTenantEngine(IMicroserviceTenantEngine<?> tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}