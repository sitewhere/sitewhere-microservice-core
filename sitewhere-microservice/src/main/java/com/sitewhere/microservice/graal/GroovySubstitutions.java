/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.graal;

import org.codehaus.groovy.control.CompilationFailedException;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import groovy.lang.GroovyClassLoader;

@TargetClass(value = GroovyClassLoader.class)
final class Target_groovy_lang_GroovyClassLoader {

    @Substitute
    @SuppressWarnings("rawtypes")
    public Class loadClass(String name, boolean lookupScriptFiles, boolean preferClassOverScript, boolean resolve)
	    throws ClassNotFoundException, CompilationFailedException {
	return null;
    }

    @Substitute
    public Class<?> loadClass(String name) throws ClassNotFoundException {
	return null;
    }

    @Substitute
    @SuppressWarnings("rawtypes")
    protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
	return null;
    }
}

class GroovySubstitutions {
}
