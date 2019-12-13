/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.microservice.exception.ConcurrentK8sUpdateException;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.microservice.lifecycle.SimpleLifecycleStep;
import com.sitewhere.microservice.metrics.MetricsServer;
import com.sitewhere.microservice.scripting.ScriptManager;
import com.sitewhere.microservice.scripting.ScriptTemplateManager;
import com.sitewhere.microservice.tenant.persistence.KubernetesTenantManagement;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceAnalytics;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSpecUpdateOperation;
import com.sitewhere.spi.microservice.instance.IInstanceStatusUpdateOperation;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.metrics.IMetricsServer;
import com.sitewhere.spi.microservice.scripting.IScriptManager;
import com.sitewhere.spi.microservice.scripting.IScriptTemplateManager;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.system.IVersion;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.fabric8.kubernetes.client.utils.URLUtils;
import io.quarkus.runtime.StartupEvent;
import io.sitewhere.k8s.crd.ApiConstants;
import io.sitewhere.k8s.crd.ISiteWhereKubernetesClient;
import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.SiteWhereKubernetesClient;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.dataset.InstanceDatasetTemplate;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngineList;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Common base class for all SiteWhere microservices.
 */
public abstract class Microservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration>
	extends LifecycleComponent implements IMicroservice<F, C> {

    /** Instance settings */
    @Inject
    IInstanceSettings instanceSettings;

    /** Kubernetes client */
    @Inject
    DefaultKubernetesClient kubernetesClient;

    /** SiteWhere Kubernetes client wrapper */
    private ISiteWhereKubernetesClient sitewhereKubernetesClient;

    /** Shared informer factory for k8s resources */
    private SharedInformerFactory sharedInformerFactory;

    /** Metrics server */
    private IMetricsServer metricsServer;

    /** JWT token management */
    private ITokenManagement tokenManagement;

    /** System superuser */
    private ISystemUser systemUser;

    /** Kafka topic naming */
    private IKafkaTopicNaming kafkaTopicNaming;

    /** Tenant management implementation */
    private ITenantManagement tenantManagement;

    /** Version information */
    private IVersion version = new Version();

    /** Script manager */
    private IScriptManager scriptManager = new ScriptManager();

    /** Script template manager instance */
    private IScriptTemplateManager scriptTemplateManager;

    /** Microservice runtime analytics interface */
    private IMicroserviceAnalytics microserviceAnalytics = new MicroserviceAnalytics();

    /** Lifecycle operations thread pool */
    private ExecutorService microserviceOperationsService;

    /** Unique id for microservice */
    private UUID id = UUID.randomUUID();

    /** Timestamp in milliseconds when service started */
    @SuppressWarnings("unused")
    private long startTime;

    public Microservice() {
	this.microserviceOperationsService = Executors
		.newSingleThreadExecutor(new MicroserviceOperationsThreadFactory());

	// Create script template manager.
	this.scriptTemplateManager = new ScriptTemplateManager();

	// Create Prometheus metrics server.
	this.metricsServer = new MetricsServer();
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#getMicroservice()
     */
    @Override
    public IMicroservice<F, C> getMicroservice() {
	return this;
    }

    /**
     * Called when microservice is started.
     * 
     * @param ev
     */
    void onStart(@Observes StartupEvent ev) {
	getLogger().info("Microservice starting...");

	// Initialize configuration model.
	try {
	    initializeK8sConnectivity();
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to start microservice.", e);
	}
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Initialize Kubernetes connectivity.
	initializeK8sConnectivity();

	// Initialize management APIs.
	initializeManagementApis();

	// Organizes steps for initializing microservice.
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize script manager.
	initialize.addInitializeStep(this, getScriptManager(), true);

	// Start script manager.
	initialize.addStartStep(this, getScriptManager(), true);

	// Initialize script template manager.
	initialize.addInitializeStep(this, getScriptTemplateManager(), true);

	// Start script template manager.
	initialize.addStartStep(this, getScriptTemplateManager(), true);

	// Initialize HTTP metrics server.
	initialize.addInitializeStep(this, getMetricsServer(), true);

	// Start HTTP metrics server.
	initialize.addStartStep(this, getMetricsServer(), true);

	// Initialize tenant management.
	initialize.addInitializeStep(this, getTenantManagement(), true);

	// Start tenant management.
	initialize.addStartStep(this, getTenantManagement(), true);

	// Execute initialization steps.
	initialize.execute(monitor);

	// Record start time.
	this.startTime = System.currentTimeMillis();
	getMicroserviceAnalytics().sendMicroserviceStarted(this);
    }

    /**
     * Initialize Kubernetes connectivity.
     * 
     * @throws SiteWhereException
     */
    protected void initializeK8sConnectivity() throws SiteWhereException {
	this.sitewhereKubernetesClient = new SiteWhereKubernetesClient(getKubernetesClient());
	this.sharedInformerFactory = getKubernetesClient().informers();

	// Create controllers and start informers.
	createKubernetesResourceControllers(getSharedInformerFactory());
	getSharedInformerFactory().startAllRegisteredInformers();
    }

    /**
     * Initialize management APIs.
     */
    protected void initializeManagementApis() {
	this.tenantManagement = new KubernetesTenantManagement();
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#
     * createKubernetesResourceControllers(io.fabric8.kubernetes.client.informers.
     * SharedInformerFactory)
     */
    @Override
    public void createKubernetesResourceControllers(SharedInformerFactory informers) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#afterMicroserviceStarted()
     */
    @Override
    public void afterMicroserviceStarted() {
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#terminate(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getMicroserviceAnalytics().sendMicroserviceStopped(this);

	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop tenant management.
	stop.addStopStep(this, getTenantManagement());

	// HTTP metrics server.
	stop.addStopStep(this, getMetricsServer());

	// Terminate script template manager.
	stop.addStopStep(this, getScriptTemplateManager());

	// Terminate script manager.
	stop.addStopStep(this, getScriptTemplateManager());

	// Add step for stopping k8s client.
	stop.addStep(new SimpleLifecycleStep("Stop Kubernetes client") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		getKubernetesClient().close();
	    }
	});

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getHostname()
     */
    @Override
    public String getHostname() {
	if (getInstanceSettings().getKubernetesPodAddress().isPresent()) {
	    return getInstanceSettings().getKubernetesPodAddress().get();
	}
	try {
	    InetAddress local = InetAddress.getLocalHost();
	    return local.getHostName();
	} catch (UnknownHostException e) {
	    throw new RuntimeException("Unable to find hostname.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getKubernetesClient()
     */
    @Override
    public DefaultKubernetesClient getKubernetesClient() {
	return this.kubernetesClient;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#getSiteWhereKubernetesClient()
     */
    @Override
    public ISiteWhereKubernetesClient getSiteWhereKubernetesClient() {
	return this.sitewhereKubernetesClient;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getMicroserviceDetails()
     */
    @Override
    public IMicroserviceDetails getMicroserviceDetails() {
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getCurrentState()
     */
    @Override
    public IMicroserviceState getCurrentState() throws SiteWhereException {
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#onTenantEngineStateChanged(com.
     * sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void onTenantEngineStateChanged(ITenantEngineState state) {
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#loadInstanceConfiguration()
     */
    @Override
    public SiteWhereInstance loadInstanceConfiguration() throws SiteWhereException {
	String instanceId = getInstanceSettings().getInstanceId();
	if (instanceId == null) {
	    throw new SiteWhereException("Instance id not set on microservice.");
	}
	SiteWhereInstance found = getSiteWhereKubernetesClient().getInstances().withName(instanceId).get();
	if (found == null) {
	    throw new SiteWhereException(String
		    .format("No instance descriptor found with name '%s'. Unable to load configuration.", instanceId));
	}
	return found;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#loadInstanceDatasetTemplate(io.
     * sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public InstanceDatasetTemplate loadInstanceDatasetTemplate(SiteWhereInstance instance) throws SiteWhereException {
	String dataset = instance.getSpec().getDatasetTemplate();
	if (dataset == null) {
	    throw new SiteWhereException("No dataset template specified for instance.");
	}
	InstanceDatasetTemplate template = getSiteWhereKubernetesClient().getInstanceDatasetTemplates()
		.withName(dataset).get();
	if (template == null) {
	    throw new SiteWhereException(String.format("No dataset template found for '%s'.", dataset));
	}
	return template;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#updateInstanceConfiguration(io.
     * sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public SiteWhereInstance updateInstanceConfiguration(SiteWhereInstance instance) throws SiteWhereException {
	String instanceId = getInstanceSettings().getInstanceId();
	if (!instanceId.equals(instance.getMetadata().getName())) {
	    throw new SiteWhereException(
		    String.format("Attempting to edit wrong instance: '%s'", instance.getMetadata().getName()));
	}
	try {
	    return getSiteWhereKubernetesClient().getInstances().withName(instanceId).createOrReplace(instance);
	} catch (KubernetesClientException e) {
	    throw new ConcurrentK8sUpdateException("Instance update failed due to concurrent update.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#updateInstanceStatus(io.
     * sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public SiteWhereInstance updateInstanceStatus(SiteWhereInstance instance) throws SiteWhereException {
	try {
	    final String statusUri = URLUtils.join(getKubernetesClient().getMasterUrl().toString(), "apis",
		    ApiConstants.SITEWHERE_API_GROUP, ApiConstants.SITEWHERE_API_VERSION,
		    ApiConstants.SITEWHERE_INSTANCE_CRD_PLURAL, instance.getMetadata().getName(), "status");
	    final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
		    MarshalUtils.marshalJson(instance));
	    Response response = getKubernetesClient().getHttpClient()
		    .newCall(new Request.Builder().method("PUT", requestBody).url(statusUri).build()).execute();
	    byte[] content = response.body().bytes();
	    response.close();
	    return MarshalUtils.unmarshalJson(content, SiteWhereInstance.class);
	} catch (Throwable e) {
	    throw new SiteWhereException("Unable to update instance status.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#getTenantEngineConfiguration(io.
     * sitewhere.k8s.crd.tenant.SiteWhereTenant,
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
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#setTenantEngineConfiguration(io.
     * sitewhere.k8s.crd.tenant.SiteWhereTenant,
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
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#executeInstanceSpecUpdate(com.
     * sitewhere.spi.microservice.instance.IInstanceSpecUpdateOperation)
     */
    @Override
    public SiteWhereInstance executeInstanceSpecUpdate(IInstanceSpecUpdateOperation operation)
	    throws SiteWhereException {
	return operation.execute(this);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#executeInstanceStatusUpdate(com.
     * sitewhere.spi.microservice.instance.IInstanceStatusUpdateOperation)
     */
    @Override
    public SiteWhereInstance executeInstanceStatusUpdate(IInstanceStatusUpdateOperation operation)
	    throws SiteWhereException {
	return operation.execute(this);
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getMetricsServer()
     */
    @Override
    public IMetricsServer getMetricsServer() {
	return metricsServer;
    }

    public void setMetricsServer(IMetricsServer metricsServer) {
	this.metricsServer = metricsServer;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getTokenManagement()
     */
    @Override
    public ITokenManagement getTokenManagement() {
	return tokenManagement;
    }

    public void setTokenManagement(ITokenManagement tokenManagement) {
	this.tokenManagement = tokenManagement;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getTenantManagement()
     */
    @Override
    public ITenantManagement getTenantManagement() {
	return tenantManagement;
    }

    public void setTenantManagement(ITenantManagement tenantManagement) {
	this.tenantManagement = tenantManagement;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getSystemUser()
     */
    @Override
    public ISystemUser getSystemUser() {
	return systemUser;
    }

    public void setSystemUser(ISystemUser systemUser) {
	this.systemUser = systemUser;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getKafkaTopicNaming()
     */
    @Override
    public IKafkaTopicNaming getKafkaTopicNaming() {
	return kafkaTopicNaming;
    }

    public void setKafkaTopicNaming(IKafkaTopicNaming kafkaTopicNaming) {
	this.kafkaTopicNaming = kafkaTopicNaming;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getScriptManager()
     */
    @Override
    public IScriptManager getScriptManager() {
	return scriptManager;
    }

    public void setScriptManager(IScriptManager scriptManager) {
	this.scriptManager = scriptManager;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getScriptTemplateManager()
     */
    @Override
    public IScriptTemplateManager getScriptTemplateManager() {
	return scriptTemplateManager;
    }

    public void setScriptTemplateManager(IScriptTemplateManager scriptTemplateManager) {
	this.scriptTemplateManager = scriptTemplateManager;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getMicroserviceAnalytics()
     */
    @Override
    public IMicroserviceAnalytics getMicroserviceAnalytics() {
	return microserviceAnalytics;
    }

    public void setMicroserviceAnalytics(IMicroserviceAnalytics microserviceAnalytics) {
	this.microserviceAnalytics = microserviceAnalytics;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getInstanceSettings()
     */
    @Override
    public IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    public void setInstanceSettings(IInstanceSettings instanceSettings) {
	this.instanceSettings = instanceSettings;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getVersion()
     */
    @Override
    public IVersion getVersion() {
	return version;
    }

    public void setVersion(IVersion version) {
	this.version = version;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#getMicroserviceOperationsService
     * ()
     */
    @Override
    public ExecutorService getMicroserviceOperationsService() {
	return microserviceOperationsService;
    }

    public void setMicroserviceOperationsService(ExecutorService microserviceOperationsService) {
	this.microserviceOperationsService = microserviceOperationsService;
    }

    protected SharedInformerFactory getSharedInformerFactory() {
	return sharedInformerFactory;
    }

    /** Used for naming microservice operation threads */
    private class MicroserviceOperationsThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Service Ops " + counter.incrementAndGet());
	}
    }
}