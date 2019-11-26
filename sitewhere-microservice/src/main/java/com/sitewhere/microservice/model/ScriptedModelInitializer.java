/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.model;

import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.spi.microservice.model.IModelInitializer;

/**
 * Model initializer that uses a script to bootstrap model.
 * 
 * @param <T>
 */
public class ScriptedModelInitializer<T> extends ScriptingComponent<T> implements IModelInitializer {
}