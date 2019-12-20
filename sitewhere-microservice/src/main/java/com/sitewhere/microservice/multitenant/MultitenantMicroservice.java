/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.microservice.configuration.ConfigurableMicroservice;
import com.sitewhere.microservice.configuration.TenantEngineConfigurationMonitor;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.configuration.ITenantEngineConfigurationMonitor;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineManager;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngineList;

/**
 * Microservice that contains engines for multiple tenants.
 */
public abstract class MultitenantMicroservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration, T extends IMicroserviceTenantEngine<? extends ITenantEngineConfiguration>>
	extends ConfigurableMicroservice<F, C> implements IMultitenantMicroservice<F, C, T> {

    /** Tenant engine configuration monitor */
    private ITenantEngineConfigurationMonitor tenantEngineConfigurationMonitor;

    /** Tenant engine manager */
    private ITenantEngineManager<T> tenantEngineManager = new TenantEngineManager<>();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * initialize(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize tenant engine manager.
	init.addInitializeStep(this, getTenantEngineManager(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start tenant engine manager.
	start.addStartStep(this, getTenantEngineManager(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop tenant engine manager.
	stop.addStopStep(this, getTenantEngineManager());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * createKubernetesResourceControllers(io.fabric8.kubernetes.client.informers.
     * SharedInformerFactory)
     */
    @Override
    public void createKubernetesResourceControllers(SharedInformerFactory informers) throws SiteWhereException {
	super.createKubernetesResourceControllers(informers);

	// Add shared informer for instance configuration monitoring.
	this.tenantEngineConfigurationMonitor = new TenantEngineConfigurationMonitor(getIdentifier(),
		getKubernetesClient(), informers);
	getTenantEngineConfigurationMonitor().getListeners().add(getTenantEngineManager());
	getTenantEngineConfigurationMonitor().start();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * getTenantEngineByToken(java.lang.String)
     */
    @Override
    public T getTenantEngineByToken(String token) throws SiteWhereException {
	return getTenantEngineManager().getTenantEngineByToken(token);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * assureTenantEngineAvailable(java.lang.String)
     */
    @Override
    public T assureTenantEngineAvailable(String token) throws TenantEngineNotAvailableException {
	return getTenantEngineManager().assureTenantEngineAvailable(token);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * getTenantEngineConfiguration(io.sitewhere.k8s.crd.tenant.SiteWhereTenant,
     * io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice)
     */
    @Override
    public SiteWhereTenantEngine getTenantEngineConfiguration(SiteWhereTenant tenant,
	    SiteWhereMicroservice microservice) throws SiteWhereException {
	SiteWhereTenantEngineList list = getSiteWhereKubernetesClient().getTenantEngines()
		.inNamespace(tenant.getMetadata().getNamespace())
		.withLabel(ResourceLabels.LABEL_SITEWHERE_TENANT, tenant.getMetadata().getName())
		.withLabel(ResourceLabels.LABEL_SITEWHERE_MICROSERVICE, microservice.getMetadata().getName()).list();
	if (list.getItems().size() == 0) {
	    return null;
	} else if (list.getItems().size() == 1) {
	    return list.getItems().get(0);
	} else {
	    getLogger().warn(String.format("Found multiple tenant engines for tenant/microservice combination. %s %s",
		    tenant.getMetadata().getName(), microservice.getMetadata().getName()));
	    return list.getItems().get(0);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * setTenantEngineConfiguration(io.sitewhere.k8s.crd.tenant.SiteWhereTenant,
     * io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice,
     * com.fasterxml.jackson.databind.JsonNode)
     */
    @Override
    public SiteWhereTenantEngine setTenantEngineConfiguration(SiteWhereTenant tenant,
	    SiteWhereMicroservice microservice, JsonNode configuration) throws SiteWhereException {
	SiteWhereTenantEngine tenantEngine = getTenantEngineConfiguration(tenant, microservice);
	if (tenantEngine == null) {
	    throw new SiteWhereException(
		    String.format("Unable to find tenant engine for tenant/microservice combination. %s %s",
			    tenant.getMetadata().getName(), microservice.getMetadata().getName()));
	}
	tenantEngine.getSpec().setConfiguration(configuration);
	return getSiteWhereKubernetesClient().getTenantEngines().createOrReplace(tenantEngine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * getTenantEngineConfigurationMonitor()
     */
    @Override
    public ITenantEngineConfigurationMonitor getTenantEngineConfigurationMonitor() {
	return tenantEngineConfigurationMonitor;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * getTenantEngineManager()
     */
    @Override
    public ITenantEngineManager<T> getTenantEngineManager() {
	return tenantEngineManager;
    }
}