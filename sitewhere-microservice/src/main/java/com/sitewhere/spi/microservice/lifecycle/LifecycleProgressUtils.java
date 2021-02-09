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
package com.sitewhere.spi.microservice.lifecycle;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.microservice.monitoring.ProgressMessage;
import com.sitewhere.spi.SiteWhereException;

/**
 * Common logic used in lifecycle progress monitoring.
 */
public class LifecycleProgressUtils {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(LifecycleProgressUtils.class);

    /**
     * Common logic for starting an operation on an
     * {@link ILifecycleProgressMonitor}.
     * 
     * @param monitor
     * @param operation
     * @throws SiteWhereException
     */
    public static void startProgressOperation(ILifecycleProgressMonitor monitor, String operation)
	    throws SiteWhereException {
	ILifecycleProgressContext context = monitor.getContextStack().peek();
	if (context == null) {
	    throw new SiteWhereException("Unable to start operation. No context available.");
	}
	int newIndex = context.getCurrentOperationIndex() + 1;
	if (newIndex > context.getOperationCount()) {
	    throw new SiteWhereException(
		    "Unable to start operation. Index will exceed expected operation count. Operation was: "
			    + operation);
	}
	context.setCurrentOperationIndex(newIndex);
	context.setCurrentOperationMessage(operation);
    }

    /**
     * Finish the currently executing progress operation.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public static void finishProgressOperation(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	ILifecycleProgressContext context = monitor.getContextStack().peek();
	if (context == null) {
	    throw new SiteWhereException("Unable to finish operation. No context available.");
	}

	// Report progress based on new operation.
	String task = monitor.getContextStack().getLast().getCurrentOperationMessage();
	String current = monitor.getContextStack().getFirst().getCurrentOperationMessage();
	Deque<ILifecycleProgressContext> queue = new ArrayDeque<>(monitor.getContextStack());
	double progress = computeSubprogressFor(queue, 100.0);
	monitor.reportProgress(new ProgressMessage(task, progress, current));
    }

    /**
     * Recursively computes progress based on nested contexts.
     * 
     * @param stack
     * @param current
     * @return
     */
    protected static double computeSubprogressFor(Deque<ILifecycleProgressContext> stack, double current) {
	if (stack.isEmpty()) {
	    return current;
	}
	ILifecycleProgressContext context = stack.removeLast();
	double opIndex = (double) context.getCurrentOperationIndex();
	double opCount = (double) context.getOperationCount();

	double finished = Math.floor((opIndex - 1.0) / opCount * current);
	double working = Math.floor(1.0 / opCount * current);

	return finished + computeSubprogressFor(stack, working);
    }
}