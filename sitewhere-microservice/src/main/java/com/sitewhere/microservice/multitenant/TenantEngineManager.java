/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.MapMaker;
import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.microservice.lifecycle.LifecycleProgressContext;
import com.sitewhere.microservice.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.configuration.ITenantEngineConfigurationListener;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineManager;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;

import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Tenant engine manager implementation.
 * 
 * @param <F>
 * @param <C>
 * @param <T>
 */
public class TenantEngineManager<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration, T extends IMicroserviceTenantEngine<?>>
	extends LifecycleComponent implements ITenantEngineManager<T>, ITenantEngineConfigurationListener {

    /** Max number of tenants being added/removed concurrently */
    private static final int MAX_CONCURRENT_TENANT_OPERATIONS = 5;

    /** List of engines waiting to be created */
    private BlockingDeque<SiteWhereTenantEngine> tenantInitializationQueue = new LinkedBlockingDeque<>();

    /** Map of tenant engines in the process of initializing */
    private ConcurrentMap<String, SiteWhereTenantEngine> initializingTenantEngines = new MapMaker().concurrencyLevel(4)
	    .makeMap();

    /** Map of tenant engines that failed to initialize */
    private ConcurrentMap<String, T> failedTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** Map of tenant engines that have been initialized */
    private ConcurrentMap<String, T> initializedTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** Map of tenant engines in the process of shutting down */
    private ConcurrentMap<String, SiteWhereTenantEngine> stoppingTenantEngines = new MapMaker().concurrencyLevel(4)
	    .makeMap();

    /** List of tenant ids waiting for an engine to be shut down */
    private BlockingDeque<SiteWhereTenantEngine> tenantShutdownQueue = new LinkedBlockingDeque<>();

    /** Executor for tenant operations */
    private ExecutorService tenantOperations;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Handles threading for tenant operations.
	this.tenantOperations = Executors.newFixedThreadPool(MAX_CONCURRENT_TENANT_OPERATIONS,
		new TenantOperationsThreadFactory());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	tenantOperations.execute(new TenantEngineStarter(getMicroservice()));
	tenantOperations.execute(new TenantEngineStopper(getMicroservice()));
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Stop and remove all tenant engines.
	removeAllTenantEngines();
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Shut down any tenant operations.
	if (tenantOperations != null) {
	    tenantOperations.shutdown();
	}

	super.terminate(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * ITenantEngineConfigurationListener#onTenantEngineCreated(io.sitewhere.k8s.crd
     * .tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public void onTenantEngineCreated(SiteWhereTenantEngine engine) {
	getLogger().info(
		String.format("Adding tenant engine to initialization queue: '%s'", engine.getMetadata().getName()));
	getTenantInitializationQueue().offer(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * ITenantEngineConfigurationListener#onTenantEngineUpdated(io.sitewhere.k8s.crd
     * .tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public void onTenantEngineUpdated(SiteWhereTenantEngine engine) {
	getLogger().info(String.format("Tenant engine updated for %s", engine.getMetadata().getName()));
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.
     * ITenantEngineConfigurationListener#onTenantEngineDeleted(io.sitewhere.k8s.crd
     * .tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public void onTenantEngineDeleted(SiteWhereTenantEngine engine) {
	getLogger().info(String.format("Tenant engine deleted for %s", engine.getMetadata().getName()));
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * getTenantEngineByToken(java.lang.String)
     */
    @Override
    public T getTenantEngineByToken(String token) throws SiteWhereException {
	T engine = getInitializedTenantEngines().get(token);
	if (engine == null) {
	    engine = getFailedTenantEngines().get(token);
	}
	return engine;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * assureTenantEngineAvailable(java.lang.String)
     */
    @Override
    public T assureTenantEngineAvailable(String token) throws TenantEngineNotAvailableException {
	try {
	    T engine = getTenantEngineByToken(token);
	    if (engine == null) {
		throw new TenantEngineNotAvailableException("No tenant engine found for tenant id.");
	    } else if (engine.getLifecycleStatus() == LifecycleStatus.InitializationError) {
		throw new TenantEngineNotAvailableException("Requested tenant engine failed initialization.");
	    } else if (engine.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw new TenantEngineNotAvailableException("Requested tenant engine failed to start.");
	    } else if (engine.getLifecycleStatus() != LifecycleStatus.Started) {
		throw new TenantEngineNotAvailableException("Requested tenant engine has not started.");
	    }
	    return engine;
	} catch (SiteWhereException e) {
	    throw new TenantEngineNotAvailableException(e);
	}
    }

    /**
     * Get tenant token from a tenant engine resource.
     * 
     * @param engine
     * @return
     * @throws SiteWhereException
     */
    public static String getTenantTokenForTenantEngine(SiteWhereTenantEngine engine) throws SiteWhereException {
	String token = engine.getMetadata().getLabels().get(ResourceLabels.LABEL_SITEWHERE_TENANT);
	if (token == null) {
	    throw new SiteWhereException("Tenant engine does not have a tenant label.");
	}
	return token;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * restartTenantEngine(java.lang.String)
     */
    @Override
    public void restartTenantEngine(String token) throws SiteWhereException {
	// Shut down and remove existing tenant engine.
	removeTenantEngine(token);
	getLogger().info("Tenant engine shut down successfully. Queueing for restart...");

	// Add to queue for restart.
	getTenantInitializationQueue().offer(null);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * restartAllTenantEngines()
     */
    @Override
    public void restartAllTenantEngines() throws SiteWhereException {
	if (getInitializedTenantEngines().size() > 0) {
	    getLogger().info(
		    String.format("Queueing %d tenant engines for restart...", getInitializedTenantEngines().size()));
	    getInitializedTenantEngines().forEach((tenantId, engine) -> {
		try {
		    restartTenantEngine(tenantId);
		} catch (SiteWhereException e) {
		    getLogger().error(String.format("Unable to restart tenant engine '%s'.", tenantId));
		}
	    });
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * removeTenantEngine(java.lang.String)
     */
    @Override
    public void removeTenantEngine(String token) throws SiteWhereException {
	IMicroserviceTenantEngine<?> engine = getInitializedTenantEngines().get(token);
	if (engine != null) {
	    // Remove initialized engine if one exists.
	    getTenantShutdownQueue().add(engine.getTenantEngineResource());
	} else {
	    // Remove failed engine if one exists.
	    engine = getFailedTenantEngines().get(token);
	    if (engine != null) {
		getFailedTenantEngines().remove(token);
	    }
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * removeAllTenantEngines()
     */
    @Override
    public void removeAllTenantEngines() throws SiteWhereException {
	if (getInitializedTenantEngines().size() > 0) {
	    getLogger().info(
		    String.format("Queueing %d tenant engines for shutdown...", getInitializedTenantEngines().size()));
	    getInitializedTenantEngines().forEach((tenantId, engine) -> {
		try {
		    removeTenantEngine(tenantId);
		} catch (SiteWhereException e) {
		    getLogger().error(String.format("Unable to remove tenant engine '%s'.", tenantId));
		}
	    });
	}
    }

    public ConcurrentMap<String, T> getInitializedTenantEngines() {
	return initializedTenantEngines;
    }

    public ConcurrentMap<String, T> getFailedTenantEngines() {
	return failedTenantEngines;
    }

    public void setFailedTenantEngines(ConcurrentMap<String, T> failedTenantEngines) {
	this.failedTenantEngines = failedTenantEngines;
    }

    public ConcurrentMap<String, SiteWhereTenantEngine> getInitializingTenantEngines() {
	return initializingTenantEngines;
    }

    public void setInitializingTenantEngines(ConcurrentMap<String, SiteWhereTenantEngine> initializingTenantEngines) {
	this.initializingTenantEngines = initializingTenantEngines;
    }

    public void setInitializedTenantEngines(ConcurrentMap<String, T> initializedTenantEngines) {
	this.initializedTenantEngines = initializedTenantEngines;
    }

    public ConcurrentMap<String, SiteWhereTenantEngine> getStoppingTenantEngines() {
	return stoppingTenantEngines;
    }

    public void setStoppingTenantEngines(ConcurrentMap<String, SiteWhereTenantEngine> stoppingTenantEngines) {
	this.stoppingTenantEngines = stoppingTenantEngines;
    }

    public BlockingDeque<SiteWhereTenantEngine> getTenantInitializationQueue() {
	return tenantInitializationQueue;
    }

    public void setTenantInitializationQueue(BlockingDeque<SiteWhereTenantEngine> tenantInitializationQueue) {
	this.tenantInitializationQueue = tenantInitializationQueue;
    }

    public BlockingDeque<SiteWhereTenantEngine> getTenantShutdownQueue() {
	return tenantShutdownQueue;
    }

    public void setTenantShutdownQueue(BlockingDeque<SiteWhereTenantEngine> tenantShutdownQueue) {
	this.tenantShutdownQueue = tenantShutdownQueue;
    }

    public ExecutorService getTenantOperations() {
	return tenantOperations;
    }

    public void setTenantOperations(ExecutorService tenantOperations) {
	this.tenantOperations = tenantOperations;
    }

    /**
     * Processes the list of tenants waiting for tenant engines to be started.
     */
    private class TenantEngineStarter extends SystemUserRunnable {

	public TenantEngineStarter(
		IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice) {
	    super(microservice, null);
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() {
	    getLogger().info("Starting to process tenant startup queue.");
	    while (true) {
		String token = null;
		SiteWhereTenantEngine engine = null;
		try {
		    engine = getTenantInitializationQueue().take();
		    token = getTenantTokenForTenantEngine(engine);
		} catch (InterruptedException e) {
		    getLogger().info("Tenant engine manager init processing shutting down...");
		    return;
		} catch (SiteWhereException e) {
		    getLogger().error("Error getting token for tenant engine.", e);
		    continue;
		}

		// Verify that multiple threads don't start duplicate engines.
		if (getInitializingTenantEngines().get(token) != null) {
		    getLogger().info(String.format("Skipping initialization for existing tenant engine '%s'.",
			    engine.getMetadata().getName()));
		    continue;
		}

		try {
		    // Start tenant initialization.
		    if (getTenantEngineByToken(token) == null) {
			startTenantEngine(engine);
		    } else {
			getLogger().debug(String.format("Tenant engine already exists for '%s'.",
				engine.getMetadata().getName()));
		    }
		} catch (SiteWhereException e) {
		    getLogger().warn("Exception starting tenant engine.", e);
		} catch (Throwable e) {
		    getLogger().warn("Unhandled exception starting tenant engine.", e);
		}
	    }
	}

	/**
	 * Start engine for a tenant.
	 * 
	 * @param tenant
	 * @throws SiteWhereException
	 */
	protected void startTenantEngine(SiteWhereTenantEngine engine) throws SiteWhereException {
	    T created = null;
	    String token = null;
	    try {
		// Mark that an engine is being initialized.
		token = getTenantTokenForTenantEngine(engine);
		getInitializingTenantEngines().put(token, engine);
		getLogger().info(String.format("Creating tenant engine for '%s'...", engine.getMetadata().getName()));

		created = getMultitenantMicroservice().createTenantEngine(engine);
		created.setTenantEngine(created); // Required for nested components.

		// Initialize new engine.
		getLogger().info(String.format("Intializing tenant engine for '%s'.", engine.getMetadata().getName()));
		ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Initialize tenant engine."), getMicroservice());
		long start = System.currentTimeMillis();
		getMicroservice().initializeNestedComponent(created, monitor, true);

		// Mark tenant engine as initialized and remove failed engine if present.
		getInitializedTenantEngines().put(token, created);
		getFailedTenantEngines().remove(token);

		getLogger().info(String.format("Tenant engine for '%s' initialized in %sms.",
			engine.getMetadata().getName(), String.valueOf(System.currentTimeMillis() - start)));

		// Start new engine.
		getLogger().info("Starting tenant engine for '" + created.getName() + "'.");
		monitor = new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Start tenant engine."),
			created.getMicroservice());
		start = System.currentTimeMillis();
		created.lifecycleStart(monitor);
		if (created.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    throw created.getLifecycleError();
		}
		getLogger().info("Tenant engine for '" + created.getName() + "' started in "
			+ (System.currentTimeMillis() - start) + "ms.");
	    } catch (Throwable t) {
		// Keep map of failed tenant engines.
		if (created != null && token != null) {
		    getFailedTenantEngines().put(token, created);
		}
		getLogger().error(
			String.format("Unable to initialize tenant engine for '%s'.", engine.getMetadata().getName()),
			t);
		throw new SiteWhereException(t);
	    } finally {
		// Make sure that tenant is cleared from the pending map.
		if (token != null) {
		    getInitializingTenantEngines().remove(token);
		}
	    }
	}
    }

    /**
     * Processes the list of tenants waiting for tenant engines to be stopped.
     */
    private class TenantEngineStopper extends SystemUserRunnable {

	public TenantEngineStopper(
		IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice) {
	    super(microservice, null);
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() {
	    getLogger().info("Starting to process tenant shutdown queue.");
	    while (true) {
		try {
		    // Get next tenant engine resource from the queue.
		    SiteWhereTenantEngine engine = getTenantShutdownQueue().take();
		    String token = getTenantTokenForTenantEngine(engine);

		    // Verify that multiple threads don't start duplicate engines.
		    if (getStoppingTenantEngines().get(token) != null) {
			getLogger().debug(String.format("Skipping shutdown for engine already stopping '%s'.", token));
			continue;
		    }

		    // Look up tenant and add it to initializing tenants map.
		    getStoppingTenantEngines().put(token, engine);

		    // Start tenant shutdown.
		    T existing = getTenantEngineByToken(token);
		    if (existing != null) {
			// Remove from list of initialized engines.
			getInitializedTenantEngines().remove(token);

			// Stop tenant engine.
			getLogger().info(String.format("Stopping tenant engine for '%s'.", existing.getName()));
			ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
				new LifecycleProgressContext(1, "Stop tenant engine."), existing.getMicroservice());
			long start = System.currentTimeMillis();
			existing.lifecycleStop(monitor);
			if (existing.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
			    throw existing.getLifecycleError();
			}
			getLogger().info(String.format("Tenant engine '%s' stopped in %sms.", existing.getName(),
				(System.currentTimeMillis() - start)));

			getLogger().info(String.format("Terminating tenant engine '%s'.", existing.getName()));
			monitor = new LifecycleProgressMonitor(
				new LifecycleProgressContext(1, "Terminate tenant engine."),
				existing.getMicroservice());
			start = System.currentTimeMillis();
			existing.lifecycleTerminate(monitor);
			if (existing.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
			    throw existing.getLifecycleError();
			}
			getLogger().info(String.format("Tenant engine '%s' terminated in %sms.", existing.getName(),
				(System.currentTimeMillis() - start)));
		    } else {
			getLogger().info("Tenant engine does not exist for '" + token + "'.");
		    }
		} catch (SiteWhereException e) {
		    getLogger().warn("Exception stopping tenant engine.", e);
		} catch (Throwable e) {
		    getLogger().warn("Unhandled exception stopping tenant engine.", e);
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    protected MultitenantMicroservice<F, C, T> getMultitenantMicroservice() {
	return ((MultitenantMicroservice<F, C, T>) getMicroservice());
    }

    protected ITenantManagement getTenantManagement() {
	return getMultitenantMicroservice().getTenantManagement();
    }

    /** Used for naming tenant operation threads */
    private class TenantOperationsThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Tenant Ops " + counter.incrementAndGet());
	}
    }
}
