/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.enterprise.event.Observes;

import com.sitewhere.microservice.lifecycle.LifecycleProgressContext;
import com.sitewhere.microservice.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.microservice.util.Boilerplate;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceApplication;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

/**
 * Base class for SiteWhere microservice application lifecycle.
 */
public abstract class MicroserviceApplication<T extends IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration>>
	implements IMicroserviceApplication<T> {

    /** Executor for background thread */
    private ExecutorService executor = Executors.newSingleThreadExecutor(new MicroserviceThreadFactory());

    /**
     * Called to initialize and start microservice components.
     */
    void onStart(@Observes StartupEvent ev) {
	getMicroservice().getLogger().info("Starting microservice...");
	try {
	    // Set up resources outside of normal lifecycle.
	    getMicroservice().install();

	    Future<Void> starting = executor.submit(new StartMicroservice());
	    starting.get();
	    getMicroservice().getLogger().info("Microservice startup completed.");
	} catch (InterruptedException | ExecutionException e) {
	    getMicroservice().getLogger().info("Exiting due to interrupted startup.", e);
	    System.exit(1);
	} catch (SiteWhereException e) {
	    getMicroservice().getLogger().info("Exiting due to exception on install.", e);
	    System.exit(1);
	}
    }

    /**
     * Called to shutdown and terminate microservice components.
     */
    void onStop(@Observes ShutdownEvent ev) {
	getMicroservice().getLogger().info("Shutdown signal received. Stopping microservice...");
	Future<Void> stopping = executor.submit(new StopMicroservice());
	try {
	    stopping.get();

	    // Clean up resources outside of normal lifecycle.
	    getMicroservice().uninstall();

	    getMicroservice().getLogger().info("Microservice shutdown complete.");
	} catch (InterruptedException | ExecutionException e) {
	    getMicroservice().getLogger().info("Exiting due to interrupted shutdown.");
	    System.exit(1);
	} catch (SiteWhereException e) {
	    getMicroservice().getLogger().info("Exiting due to exception on uninstall.", e);
	    System.exit(1);
	}
    }

    /**
     * Start a microservice.
     * 
     * @param microservice
     * @param showBanner
     * @throws SiteWhereException
     */
    public static void startMicroservice(IMicroservice<?, ?> microservice, boolean showBanner)
	    throws SiteWhereException {
	long start = System.currentTimeMillis();

	// Adds banner to the top of the log.
	String banner = showBanner ? getBanner(microservice) : "";

	// Display banner indicating service information.
	List<String> messages = new ArrayList<String>();
	messages.add(microservice.getName() + " Microservice");
	messages.add("Version: " + microservice.getVersion().getVersionIdentifier() + "."
		+ microservice.getVersion().getGitRevisionAbbrev());
	messages.add("Git Revision: " + microservice.getVersion().getGitRevision());
	messages.add("Build Date: " + microservice.getVersion().getBuildTimestamp());
	messages.add("Hostname: " + microservice.getHostname());
	String message = Boilerplate.boilerplate(messages, "*");
	microservice.getLogger().info(String.format("\n%s\n%s\n", banner, message));

	// Initialize microservice.
	LifecycleProgressMonitor initMonitor = new LifecycleProgressMonitor(
		new LifecycleProgressContext(1, "Initialize " + microservice.getName()), microservice);
	microservice.lifecycleInitialize(initMonitor);
	if (microservice.getLifecycleStatus() == LifecycleStatus.InitializationError) {
	    microservice.getLogger().info("Error initializing microservice.", microservice.getLifecycleError());
	    throw microservice.getLifecycleError();
	}

	// Start microservice.
	LifecycleProgressMonitor startMonitor = new LifecycleProgressMonitor(
		new LifecycleProgressContext(1, "Start " + microservice.getName()), microservice);
	microservice.lifecycleStart(startMonitor);
	if (microservice.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
	    microservice.getLogger().info("Error starting microservice.", microservice.getLifecycleError());
	    throw microservice.getLifecycleError();
	}

	long total = System.currentTimeMillis() - start;
	messages.clear();
	messages.add(microservice.getName() + " Microservice");
	messages.add("Startup time: " + total + "ms");
	message = Boilerplate.boilerplate(messages, "*");
	microservice.getLogger().info("\n" + message + "\n");

	// Execute any post-startup code.
	microservice.afterMicroserviceStarted();
    }

    /**
     * Runnable for starting microservice.
     */
    private class StartMicroservice implements Callable<Void> {

	/*
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call() throws Exception {
	    try {
		MicroserviceApplication.startMicroservice(getMicroservice(), true);
	    } catch (SiteWhereException e) {
		getMicroservice().getLogger().error("Exception on microservice startup.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Microservice failed to start !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		getMicroservice().getLogger().info("\n" + builder.toString() + "\n");
		System.exit(2);
	    } catch (Throwable e) {
		getMicroservice().getLogger().error("Unhandled exception in microservice startup.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Unhandled Exception !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		getMicroservice().getLogger().info("\n" + builder.toString() + "\n");
		System.exit(3);
	    }
	    return null;
	}

    }

    /**
     * Stop microservice.
     * 
     * @param microservice
     * @throws SiteWhereException
     */
    public static void stopMicroservice(IMicroservice<?, ?> microservice) throws SiteWhereException {
	// Stop microservice.
	LifecycleProgressMonitor stopMonitor = new LifecycleProgressMonitor(
		new LifecycleProgressContext(1, "Stop " + microservice.getName()), microservice);
	microservice.lifecycleStop(stopMonitor);
	if (microservice.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
	    throw microservice.getLifecycleError();
	}

	// Terminate microservice.
	LifecycleProgressMonitor termMonitor = new LifecycleProgressMonitor(
		new LifecycleProgressContext(1, "Terminate " + microservice.getName()), microservice);
	microservice.lifecycleTerminate(termMonitor);
	if (microservice.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
	    throw microservice.getLifecycleError();
	}
    }

    /**
     * Stop the microservice.
     */
    private class StopMicroservice implements Callable<Void> {

	/*
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call() throws Exception {
	    try {
		MicroserviceApplication.stopMicroservice(getMicroservice());
	    } catch (SiteWhereException e) {
		getMicroservice().getLogger().error("Exception on microservice shutdown.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Microservice failed to stop !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		getMicroservice().getLogger().info("\n" + builder.toString() + "\n");
		System.exit(2);
	    } catch (Throwable e) {
		getMicroservice().getLogger().error("Unhandled exception in microservice shutdown.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Unhandled Exception !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		getMicroservice().getLogger().info("\n" + builder.toString() + "\n");
		System.exit(3);
	    }
	    return null;
	}
    }

    /**
     * Get banner content from file.
     * 
     * @param microservice
     * @return
     */
    protected static String getBanner(IMicroservice<?, ?> microservice) {
	InputStream banner = microservice.getClass().getResourceAsStream("/banner.txt");
	if (banner != null) {
	    StringBuilder builder = new StringBuilder();
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(banner))) {
		String line = reader.readLine();
		while (line != null) {
		    builder.append(line).append("\n");
		    line = reader.readLine();
		}
		return builder.toString();
	    } catch (IOException e) {
		microservice.getLogger().warn("Unable to read banner.");
	    }
	} else {
	    microservice.getLogger().warn("No banner file found.");
	}
	return "";
    }

    /** Used for naming primary microservice thread */
    private class MicroserviceThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Service Main");
	}
    }
}