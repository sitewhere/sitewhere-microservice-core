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
package com.sitewhere.microservice;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.sitewhere.microservice.cache.StringByteArrayCodec;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.microservice.metrics.MetricsServer;
import com.sitewhere.microservice.scripting.ScriptManager;
import com.sitewhere.microservice.scripting.ScriptTemplateManager;
import com.sitewhere.microservice.tenant.persistence.KubernetesTenantManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
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
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.microservice.user.IUserManagement;
import com.sitewhere.spi.system.IVersion;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.sitewhere.k8s.SiteWhereKubernetesClient;
import io.sitewhere.k8s.api.ISiteWhereKubernetesClient;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.dataset.InstanceDatasetTemplate;

/**
 * Common base class for all SiteWhere microservices.
 */
@Import({ MicroserviceDependencies.class })
public abstract class Microservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration>
	extends LifecycleComponent implements IMicroservice<F, C> {

    /** Instance settings */
    @Autowired
    IInstanceSettings instanceSettings;

    /** Kafka topic naming */
    @Autowired
    private IKafkaTopicNaming kafkaTopicNaming;

    /** User management */
    @Autowired
    private IUserManagement userManagement;

    /** JWT token management */
    @Autowired
    private ITokenManagement tokenManagement;

    /** System superuser */
    @Autowired
    private ISystemUser systemUser;

    /** Kubernetes client */
    @Autowired
    DefaultKubernetesClient kubernetesClient;

    /** SiteWhere Kubernetes client wrapper */
    private ISiteWhereKubernetesClient sitewhereKubernetesClient;

    /** Lettuce Redis client */
    private RedisClient redisClient;

    /** Current Redis cache connection */
    private StatefulRedisConnection<String, byte[]> redisCacheConnection;

    /** Current Redis cache connection */
    private StatefulRedisConnection<String, byte[]> redisStreamConnection;

    /** Shared informer factory for k8s resources */
    private SharedInformerFactory sharedInformerFactory;

    /** Metrics server */
    private IMetricsServer metricsServer = new MetricsServer();

    /** Tenant management implementation */
    private ITenantManagement tenantManagement;

    /** Version information */
    private IVersion version = new MicroserviceVersion();

    /** Version information */
    private IVersion microserviceLibraryVersion = new MicroserviceLibraryVersion();

    /** Script manager */
    private IScriptManager scriptManager = new ScriptManager();

    /** Script template manager instance */
    private IScriptTemplateManager scriptTemplateManager = new ScriptTemplateManager();

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

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#install()
     */
    @Override
    public void install() throws SiteWhereException {
	// Initialize Kubernetes connectivity.
	initializeK8sConnectivity();
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Initialize Redis connectivity.
	initializeRedisConnectivity();

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

	// Initialize user management.
	initialize.addInitializeStep(this, getUserManagement(), true);

	// Start user management.
	initialize.addStartStep(this, getUserManagement(), true);

	// Execute initialization steps.
	initialize.execute(monitor);

	// Record start time.
	this.startTime = System.currentTimeMillis();
    }

    /**
     * Initialize Kubernetes connectivity.
     * 
     * @throws SiteWhereException
     */
    protected void initializeK8sConnectivity() throws SiteWhereException {
	getLogger().info("Initializing Kubernetes connectivity...");
	this.sitewhereKubernetesClient = new SiteWhereKubernetesClient(getKubernetesClient());
	this.sharedInformerFactory = getKubernetesClient().informers();

	// Create controllers and start informers.
	createKubernetesResourceControllers(getSharedInformerFactory());
	getSharedInformerFactory().startAllRegisteredInformers();
	getLogger().info("Kubernetes connectivity initialized.");
    }

    /**
     * Initialize connectivity to Redis.
     * 
     * @throws SiteWhereException
     */
    protected void initializeRedisConnectivity() throws SiteWhereException {
	getLogger().info("Initializing Redis connectivity...");
	while (true) {
	    try {
		IInstanceSettings settings = getMicroservice().getInstanceSettings();
		String serviceName = settings.getRedis().getService().getName() + "."
			+ ISiteWhereKubernetesClient.NS_SITEWHERE_SYSTEM;
		String redisAddress = String.format("redis://%s@%s:%s", settings.getRedis().getPassword(), serviceName,
			String.valueOf(settings.getRedis().getPort()));
		getLogger().info(String.format("Connecting to Redis server using address: %s", redisAddress));
		this.redisClient = RedisClient.create(redisAddress);
		this.redisCacheConnection = getRedisClient().connect(StringByteArrayCodec.INSTANCE);
		this.redisStreamConnection = getRedisClient().connect(StringByteArrayCodec.INSTANCE);
		break;
	    } catch (Throwable t) {
		getLogger().warn("Unable to establish Redis connection.", t);
		try {
		    Thread.sleep(3000);
		} catch (InterruptedException e) {
		    getLogger().info("Interrupted while waiting for Redis connection.");
		    return;
		}
	    }
	}
	getLogger().info("Redis connectivity initialized.");
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
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop user management.
	stop.addStopStep(this, getUserManagement());

	// Stop tenant management.
	stop.addStopStep(this, getTenantManagement());

	// HTTP metrics server.
	stop.addStopStep(this, getMetricsServer());

	// Terminate script template manager.
	stop.addStopStep(this, getScriptTemplateManager());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#restart()
     */
    @Override
    public void restart() throws SiteWhereException {
	MicroserviceApplication.stopMicroservice(this);
	MicroserviceApplication.startMicroservice(this, false);
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#uninstall()
     */
    @Override
    public void uninstall() throws SiteWhereException {
	getKubernetesClient().informers().stopAllRegisteredInformers();
	getKubernetesClient().close();
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getHostname()
     */
    @Override
    public String getHostname() {
	return getInstanceSettings().getK8s().getPod().getIp();
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
     * @see com.sitewhere.spi.microservice.IMicroservice#getRedisClient()
     */
    @Override
    public RedisClient getRedisClient() {
	return redisClient;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getRedisCacheConnection()
     */
    @Override
    public StatefulRedisConnection<String, byte[]> getRedisCacheConnection() {
	return redisCacheConnection;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getRedisStreamConnection()
     */
    @Override
    public StatefulRedisConnection<String, byte[]> getRedisStreamConnection() {
	return redisStreamConnection;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#loadInstanceResource()
     */
    @Override
    public SiteWhereInstance loadInstanceResource() throws SiteWhereException {
	String instanceId = getInstanceSettings().getK8s().getNamespace();
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
     * @see com.sitewhere.spi.microservice.IMicroservice#updateInstanceResource(io.
     * sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public SiteWhereInstance updateInstanceResource(SiteWhereInstance instance) throws SiteWhereException {
	String instanceId = getInstanceSettings().getK8s().getNamespace();
	if (!instanceId.equals(instance.getMetadata().getName())) {
	    throw new SiteWhereException(
		    String.format("Attempting to edit wrong instance: '%s'", instance.getMetadata().getName()));
	}
	try {
	    return getSiteWhereKubernetesClient().getInstances().withName(instanceId).createOrReplace(instance);
	} catch (KubernetesClientException e) {
	    throw new SiteWhereException("Instance resource update failed due to concurrent update.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#updateInstanceStatus(io.
     * sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public SiteWhereInstance updateInstanceStatus(SiteWhereInstance instance) throws SiteWhereException {
	try {
	    return getSiteWhereKubernetesClient().getInstances().withName(instance.getMetadata().getName())
		    .updateStatus(instance);
	} catch (Throwable e) {
	    throw new SiteWhereException("Unhandled exception updating instance status.", e);
	}
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

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getTokenManagement()
     */
    @Override
    public ITokenManagement getTokenManagement() {
	return tokenManagement;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getTenantManagement()
     */
    @Override
    public ITenantManagement getTenantManagement() {
	return tenantManagement;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getUserManagement()
     */
    @Override
    public IUserManagement getUserManagement() {
	return this.userManagement;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getSystemUser()
     */
    @Override
    public ISystemUser getSystemUser() {
	return systemUser;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getKafkaTopicNaming()
     */
    @Override
    public IKafkaTopicNaming getKafkaTopicNaming() {
	return kafkaTopicNaming;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getScriptManager()
     */
    @Override
    public IScriptManager getScriptManager() {
	return scriptManager;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getScriptTemplateManager()
     */
    @Override
    public IScriptTemplateManager getScriptTemplateManager() {
	return scriptTemplateManager;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getInstanceSettings()
     */
    @Override
    public IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getVersion()
     */
    @Override
    public IVersion getVersion() {
	return version;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#getMicroserviceLibraryVersion()
     */
    @Override
    public IVersion getMicroserviceLibraryVersion() {
	return microserviceLibraryVersion;
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