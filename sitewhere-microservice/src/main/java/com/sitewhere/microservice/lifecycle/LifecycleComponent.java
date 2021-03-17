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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.slf4j.cal10n.LocLogger;
import org.slf4j.cal10n.LocLoggerFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.ServerStartupException;
import com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleConstraints;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleHierarchyRoot;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;

/**
 * Base class for implementing {@link ILifecycleComponent}.
 */
public class LifecycleComponent implements ILifecycleComponent {

    /** Provides localized messages for locale */
    private IMessageConveyor messageConveyor = new MessageConveyor(Locale.getDefault());

    /** Factory for localized logger */
    private LocLoggerFactory locLoggerFactory = new LocLoggerFactory(messageConveyor);

    /** Logger instance */
    private LocLogger logger = locLoggerFactory.getLocLogger(getClass());

    /** Unique component id */
    private UUID componentId = UUID.randomUUID();

    /** Component type */
    private LifecycleComponentType componentType;

    /** Owning microservice */
    private IMicroservice<?, ?> microservice;

    /** Date/time component was created */
    private Date createdDate = new Date();

    /** Lifecycle status indicator */
    private LifecycleStatus lifecycleStatus = LifecycleStatus.Stopped;

    /** Last error encountered in lifecycle operations */
    private SiteWhereException lifecycleError;

    /** Map of contained lifecycle components */
    private Map<UUID, ILifecycleComponent> lifecycleComponents = new HashMap<>();

    public LifecycleComponent() {
	this(LifecycleComponentType.Other);
    }

    public LifecycleComponent(LifecycleComponentType type) {
	this.componentType = type;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentId()
     */
    @Override
    public UUID getComponentId() {
	return componentId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
	return getClass().getSimpleName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentType()
     */
    public LifecycleComponentType getComponentType() {
	return componentType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getCreatedDate()
     */
    @Override
    public Date getCreatedDate() {
	return createdDate;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public LocLogger getLogger() {
	return logger;
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#initializeParameters()
     */
    @Override
    public void initializeParameters() throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleProvision(com
     * .sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleProvision(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	provision(monitor);
	for (ILifecycleComponent nested : getLifecycleComponents().values()) {
	    nested.lifecycleProvision(monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#provision(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void provision(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * lifecycleInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleInitialize(ILifecycleProgressMonitor monitor) {
	try {
	    // Verify that component can be initialized.
	    if (!canInitialize()) {
		return;
	    }
	    setLifecycleStatus(LifecycleStatus.Initializing);
	    getLogger().info(getComponentName() + " state transitioned to INITIALIZING.");

	    initialize(monitor);
	    setLifecycleStatus(LifecycleStatus.Stopped);
	    getLogger().info(getComponentName() + " state transitioned to INITIALIZED.");
	} catch (SiteWhereException e) {
	    setLifecycleError(e);
	    setLifecycleStatus(LifecycleStatus.InitializationError);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
	} catch (Throwable t) {
	    setLifecycleError(new SiteWhereException(t));
	    setLifecycleStatus(LifecycleStatus.InitializationError);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canInitialize()
     */
    @Override
    public boolean canInitialize() throws SiteWhereException {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * initializeNestedComponent(com.sitewhere.spi.server.lifecycle.
     * ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor, boolean)
     */
    @Override
    public void initializeNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor,
	    boolean require) throws SiteWhereException {
	if (getMicroservice() == null) {
	    throw new SiteWhereException("Microservice reference not set in parent component: " + getClass().getName());
	}
	component.setMicroservice(getMicroservice());
	component.lifecycleInitialize(monitor);
	if (require) {
	    if (component.getLifecycleStatus() == LifecycleStatus.InitializationError) {
		throw new ServerStartupException(component, "Error initializing '" + component.getComponentName() + "'",
			component.getLifecycleError());
	    }
	}
	getLifecycleComponents().put(component.getComponentId(), component);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStart(com
     * .sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleStart(ILifecycleProgressMonitor monitor) {
	try {
	    // Verify that component can be started.
	    if (!canStart()) {
		return;
	    }

	    LifecycleStatus old = getLifecycleStatus();
	    setLifecycleStatus(LifecycleStatus.Starting);
	    getLogger().info(getComponentName() + " state transitioned to STARTING.");

	    if (old != LifecycleStatus.Paused) {
		start(monitor);
	    }

	    LifecycleStatus status = LifecycleStatus.Started;
	    if (this instanceof IAsyncStartLifecycleComponent
		    && !((IAsyncStartLifecycleComponent) this).isComponentStarted()) {
		status = LifecycleStatus.StartingAsynchronously;
	    }

	    for (UUID id : getLifecycleComponents().keySet()) {
		ILifecycleComponent sub = getLifecycleComponents().get(id);
		if ((sub.getLifecycleStatus() == LifecycleStatus.LifecycleError)
			|| (sub.getLifecycleStatus() == LifecycleStatus.StartedWithErrors)) {
		    status = LifecycleStatus.StartedWithErrors;
		}
	    }

	    setLifecycleStatus(status);
	    if (status == LifecycleStatus.Started) {
		getLogger().info(getComponentName() + " state transitioned to STARTED.");
	    } else if (status == LifecycleStatus.StartedWithErrors) {
		getLogger().info(getComponentName() + " state transitioned to STARTED WITH ERRORS.");
	    }
	} catch (SiteWhereException e) {
	    setLifecycleError(e);
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
	} catch (Throwable t) {
	    setLifecycleError(new SiteWhereException(t));
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canStart()
     */
    @Override
    public boolean canStart() throws SiteWhereException {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * startNestedComponent(com.sitewhere.spi.server.lifecycle. ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor,
     * java.lang.String, boolean)
     */
    public void startNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor, boolean require)
	    throws SiteWhereException {
	component.lifecycleStart(monitor);
	if (require) {
	    if (component.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw new ServerStartupException(component, "Unable to start '" + component.getComponentName() + "'",
			component.getLifecycleError());
	    }
	}
	getLifecycleComponents().put(component.getComponentId(), component);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecyclePause(com
     * .sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecyclePause(ILifecycleProgressMonitor monitor) {
	setLifecycleStatus(LifecycleStatus.Pausing);
	getLogger().info(getComponentName() + " state transitioned to PAUSING.");
	try {
	    pause(monitor);
	    setLifecycleStatus(LifecycleStatus.Paused);
	    getLogger().info(getComponentName() + " state transitioned to PAUSED.");
	} catch (SiteWhereException e) {
	    setLifecycleError(e);
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
	} catch (Throwable t) {
	    setLifecycleError(new SiteWhereException(t));
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#pause(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void pause(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canPause()
     */
    public boolean canPause() throws SiteWhereException {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleStop(ILifecycleProgressMonitor monitor) {
	lifecycleStop(monitor, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor,
     * com.sitewhere.spi.server.lifecycle.ILifecycleConstraints)
     */
    @Override
    public void lifecycleStop(ILifecycleProgressMonitor monitor, ILifecycleConstraints constraints) {
	try {
	    // Verify that we are allowed to stop component.
	    if (!canStop()) {
		return;
	    }

	    setLifecycleStatus(LifecycleStatus.Stopping);
	    getLogger().info(getComponentName() + " state transitioned to STOPPING.");

	    if (constraints == null) {
		stop(monitor);
	    } else {
		stop(monitor, constraints);
	    }

	    LifecycleStatus status = LifecycleStatus.Stopped;
	    for (UUID id : getLifecycleComponents().keySet()) {
		ILifecycleComponent sub = getLifecycleComponents().get(id);
		if ((sub.getLifecycleStatus() == LifecycleStatus.LifecycleError)
			|| (sub.getLifecycleStatus() == LifecycleStatus.StoppedWithErrors)) {
		    status = LifecycleStatus.StoppedWithErrors;
		}
	    }

	    setLifecycleStatus(status);
	    if (status == LifecycleStatus.Stopped) {
		getLogger().info(getComponentName() + " state transitioned to STOPPED.");
	    } else if (status == LifecycleStatus.StoppedWithErrors) {
		getLogger().info(getComponentName() + " state transitioned to STOPPED WITH ERRORS.");
	    }
	} catch (SiteWhereException e) {
	    setLifecycleError(e);
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
	} catch (Throwable t) {
	    setLifecycleError(new SiteWhereException(t));
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canStop()
     */
    @Override
    public boolean canStop() throws SiteWhereException {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor,
     * com.sitewhere.spi.server.lifecycle.ILifecycleConstraints)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor, ILifecycleConstraints constraints) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stopNestedComponent(
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stopNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	if (component != null) {
	    component.lifecycleStop(monitor);
	    if (component.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		getLogger().error("Unable to stop '" + component.getComponentName() + "'",
			component.getLifecycleError());
	    }
	    getLifecycleComponents().remove(component.getComponentId(), component);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleTerminate
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleTerminate(ILifecycleProgressMonitor monitor) {
	setLifecycleStatus(LifecycleStatus.Terminating);
	getLogger().info(getComponentName() + " state transitioned to TERMINATING.");
	try {
	    terminate(monitor);
	    setLifecycleStatus(LifecycleStatus.Terminated);
	    getLogger().info(getComponentName() + " state transitioned to TERMINATED.");
	} catch (SiteWhereException e) {
	    setLifecycleError(e);
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
	} catch (Throwable t) {
	    setLifecycleError(new SiteWhereException(t));
	    setLifecycleStatus(LifecycleStatus.LifecycleError);
	    getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#terminate(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * lifecycleStatusChanged(com.sitewhere.spi.server.lifecycle. LifecycleStatus,
     * com.sitewhere.spi.server.lifecycle.LifecycleStatus)
     */
    @Override
    public void lifecycleStatusChanged(LifecycleStatus before, LifecycleStatus after) {
    }

    /**
     * Parse an exception into an error message stack.
     * 
     * @param e
     * @return
     */
    protected List<String> parseErrors(SiteWhereException e) {
	List<String> errors = new ArrayList<>();
	Throwable current = e;
	while (current != null) {
	    errors.add(current.toString());
	    current = current.getCause();
	}
	return errors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * findComponentsOfType(com
     * .sitewhere.spi.server.lifecycle.LifecycleComponentType)
     */
    @Override
    public List<ILifecycleComponent> findComponentsOfType(LifecycleComponentType type) throws SiteWhereException {
	List<ILifecycleComponent> matches = new ArrayList<ILifecycleComponent>();
	findComponentsOfType(this, matches, type);
	return matches;
    }

    /**
     * Recursive matching of nested components to find those of the given type.
     * 
     * @param current
     * @param matches
     * @param type
     * @throws SiteWhereException
     */
    public void findComponentsOfType(ILifecycleComponent current, List<ILifecycleComponent> matches,
	    LifecycleComponentType type) throws SiteWhereException {
	if (current.getComponentType() == type) {
	    matches.add(current);
	}
	for (ILifecycleComponent child : current.getLifecycleComponents().values()) {
	    findComponentsOfType(child, matches, type);
	}
    }

    public void logState() {
	getLogger().info("\n\n" + getComponentName() + " State:\n" + logState("", this) + "\n");
    }

    /**
     * Recursively log state for a component.
     * 
     * @param pad
     * @param component
     */
    protected String logState(String pad, ILifecycleComponent component) {
	String entry = "\n" + pad + "+ " + component.getComponentName() + " " + component.getLifecycleStatus();
	List<ILifecycleComponent> subcomponents = new ArrayList<ILifecycleComponent>(
		component.getLifecycleComponents().values());

	// Sort components by created date.
	Collections.sort(subcomponents, new Comparator<ILifecycleComponent>() {

	    @Override
	    public int compare(ILifecycleComponent o1, ILifecycleComponent o2) {
		return o1.getCreatedDate().compareTo(o2.getCreatedDate());
	    }
	});
	for (ILifecycleComponent subcomponent : subcomponents) {
	    entry = entry + logState("  " + pad, subcomponent);
	}
	return entry;
    }

    /**
     * Build a component map by recursively navigating the component tree.
     * 
     * @return
     */
    protected Map<UUID, ILifecycleComponent> buildComponentMap() {
	Map<UUID, ILifecycleComponent> map = new HashMap<>();
	buildComponentMap(this, map);
	return map;
    }

    /**
     * Recursively navigates component structure and creates a map of components by
     * id.
     * 
     * @param current
     * @param map
     */
    protected static void buildComponentMap(ILifecycleComponent current, Map<UUID, ILifecycleComponent> map) {
	map.put(current.getComponentId(), current);
	for (ILifecycleComponent sub : current.getLifecycleComponents().values()) {
	    // Root components have a separate hierarchy.
	    if (!(sub instanceof ILifecycleHierarchyRoot)) {
		buildComponentMap(sub, map);
	    }
	}
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getMicroservice()
     */
    @Override
    public IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> getMicroservice() {
	return microservice;
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#setMicroservice(com.
     * sitewhere.spi.microservice.IMicroservice)
     */
    @Override
    public void setMicroservice(
	    IMicroservice<? extends IFunctionIdentifier, ? extends IMicroserviceConfiguration> microservice) {
	this.microservice = microservice;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleStatus ()
     */
    @Override
    public LifecycleStatus getLifecycleStatus() {
	return lifecycleStatus;
    }

    public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
	LifecycleStatus old = this.lifecycleStatus;
	this.lifecycleStatus = lifecycleStatus;

	if (old != lifecycleStatus) {
	    lifecycleStatusChanged(old, lifecycleStatus);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleError( )
     */
    @Override
    public SiteWhereException getLifecycleError() {
	return lifecycleError;
    }

    public void setLifecycleError(SiteWhereException lifecycleError) {
	this.lifecycleError = lifecycleError;
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleComponents
     * ()
     */
    @Override
    public Map<UUID, ILifecycleComponent> getLifecycleComponents() {
	return lifecycleComponents;
    }

    public void setLifecycleComponents(Map<UUID, ILifecycleComponent> lifecycleComponents) {
	this.lifecycleComponents = lifecycleComponents;
    }
}