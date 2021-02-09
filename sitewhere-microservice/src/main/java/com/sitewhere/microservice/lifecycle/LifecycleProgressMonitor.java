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

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressContext;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleProgressUtils;
import com.sitewhere.spi.microservice.monitoring.IProgressErrorMessage;
import com.sitewhere.spi.microservice.monitoring.IProgressMessage;

/**
 * Default implementation of {@link ILifecycleProgressMonitor}.
 */
public class LifecycleProgressMonitor implements ILifecycleProgressMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(LifecycleProgressMonitor.class);

    /** Stack for nested progress tracking */
    private Deque<ILifecycleProgressContext> contextStack = new ArrayDeque<ILifecycleProgressContext>();

    /** Microservice associated with component */
    private IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice;

    public LifecycleProgressMonitor(ILifecycleProgressContext initialContext,
	    IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice) {
	this.microservice = microservice;
	contextStack.push(initialContext);
	try {
	    LifecycleProgressUtils.startProgressOperation(this, initialContext.getTaskName());
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Unable to create progress monitor.", e);
	}
    }

    public static LifecycleProgressMonitor createFor(String operation, IMicroservice<?, ?> microservice) {
	return new LifecycleProgressMonitor(new LifecycleProgressContext(1, operation), microservice);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressReporter#reportProgress(com.
     * sitewhere.spi.monitoring.IProgressMessage)
     */
    @Override
    public void reportProgress(IProgressMessage message) throws SiteWhereException {
	LOGGER.debug(
		"[" + message.getTaskName() + "]: (" + message.getProgressPercentage() + "%) " + message.getMessage());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.monitoring.IProgressReporter#reportError(com.sitewhere.
     * spi.monitoring.IProgressErrorMessage)
     */
    @Override
    public void reportError(IProgressErrorMessage error) throws SiteWhereException {
	LOGGER.info("[ERROR][" + error.getTaskName() + "]: (" + error.getProgressPercentage() + "%) "
		+ error.getMessage() + "[" + error.getLevel().name() + "]");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#pushContext(
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext)
     */
    @Override
    public void pushContext(ILifecycleProgressContext context) throws SiteWhereException {
	getContextStack().push(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * startProgress(java.lang.String)
     */
    @Override
    public void startProgress(String operation) throws SiteWhereException {
	LifecycleProgressUtils.startProgressOperation(this, operation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * finishProgress()
     */
    @Override
    public void finishProgress() throws SiteWhereException {
	LifecycleProgressUtils.finishProgressOperation(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#popContext()
     */
    @Override
    public ILifecycleProgressContext popContext() throws SiteWhereException {
	return getContextStack().pop();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * getContextStack()
     */
    @Override
    public Deque<ILifecycleProgressContext> getContextStack() {
	return contextStack;
    }

    public void setContextStack(Deque<ILifecycleProgressContext> contextStack) {
	this.contextStack = contextStack;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * getMicroservice()
     */
    @Override
    public IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> getMicroservice() {
	return microservice;
    }

    public void setMicroservice(
	    IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice) {
	this.microservice = microservice;
    }
}