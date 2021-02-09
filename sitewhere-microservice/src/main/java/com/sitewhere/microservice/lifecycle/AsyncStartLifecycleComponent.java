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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Lifecycle component which allows the "start" phase to be executed
 * asynchronously in order to prevent blocking other components from starting.
 * Components that require use of the component should call
 * "waitForComponentStarted" in order to verify that the async operation has
 * completed.
 */
public abstract class AsyncStartLifecycleComponent extends TenantEngineLifecycleComponent
	implements IAsyncStartLifecycleComponent {

    /** Thread used for executing async processing */
    ExecutorService executor = Executors.newSingleThreadExecutor();

    /** Latch used to block waiting components */
    private CountDownLatch latch = new CountDownLatch(1);

    public AsyncStartLifecycleComponent() {
    }

    public AsyncStartLifecycleComponent(LifecycleComponentType type) {
	super(type);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getExecutor().execute(new Waiter());
    }

    /*
     * @see com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent#
     * isComponentStarted()
     */
    @Override
    public boolean isComponentStarted() {
	return latch.getCount() == 0;
    }

    /*
     * @see com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent#
     * waitForComponentStarted()
     */
    @Override
    public void waitForComponentStarted() {
	try {
	    latch.await();
	} catch (InterruptedException e) {
	    getLogger().warn("Interrupted while waiting for component to start.");
	}
    }

    /**
     * Waits for asyncronous processing to complete.
     */
    protected class Waiter implements Runnable {

	@Override
	public void run() {
	    try {
		asyncStart();
		latch.countDown();
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to start asynchronous component.", e);
	    }
	}
    }

    protected ExecutorService getExecutor() {
	return executor;
    }
}
