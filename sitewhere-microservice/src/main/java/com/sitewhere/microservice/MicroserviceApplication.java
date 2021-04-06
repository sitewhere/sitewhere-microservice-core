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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import com.sitewhere.microservice.lifecycle.LifecycleProgressContext;
import com.sitewhere.microservice.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.microservice.util.Boilerplate;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceApplication;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;

/**
 * Base class for SiteWhere microservice application lifecycle.
 */
@SpringBootApplication(exclude = { FlywayAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
	DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class })
public abstract class MicroserviceApplication<T extends IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration>>
	implements IMicroserviceApplication<T>, InitializingBean, DisposableBean {

    /** Executor for background thread */
    private ExecutorService executor = Executors.newSingleThreadExecutor(new MicroserviceThreadFactory());

    /*
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
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

    /*
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
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
	String microserviceVersion = String.format("%s.%s (%s)", microservice.getVersion().getVersionIdentifier(),
		microservice.getVersion().getGitRevisionAbbrev(), microservice.getVersion().getBuildTimestamp());
	String corelibVersion = String.format("%s.%s (%s)",
		microservice.getMicroserviceLibraryVersion().getVersionIdentifier(),
		microservice.getMicroserviceLibraryVersion().getGitRevisionAbbrev(),
		microservice.getMicroserviceLibraryVersion().getBuildTimestamp());

	// Display banner indicating service information.
	List<String> messages = new ArrayList<String>();
	messages.add(microservice.getName() + " Microservice");
	messages.add("Microservice Version: " + microserviceVersion);
	messages.add("Core Library Version: " + corelibVersion);
	messages.add("");
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