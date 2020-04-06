/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public class ScriptingUtils {

    /**
     * Run a standalone script.
     * 
     * @param <T>
     * @param script
     * @param binding
     * @return
     */
    public static <T> T run(String script, Binding binding) {
	try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
	    Value jsBindings = context.getBindings(ScriptingConstants.LANGUAGE_JAVASCRIPT);
	    for (String key : binding.getBoundObjects().keySet()) {
		jsBindings.putMember(key, binding.getBoundObjects().get(key));
	    }
	    Value result = context.eval(ScriptingConstants.LANGUAGE_JAVASCRIPT, script);
	    if (result.isHostObject()) {
		return result.asHostObject();
	    }
	    return null;
	}
    }

    /**
     * Run a cached script.
     * 
     * @param <T>
     * @param source
     * @param binding
     * @return
     */
    public static <T> T run(Source source, Binding binding) {
	try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
	    Value jsBindings = context.getBindings(ScriptingConstants.LANGUAGE_JAVASCRIPT);
	    for (String key : binding.getBoundObjects().keySet()) {
		jsBindings.putMember(key, binding.getBoundObjects().get(key));
	    }
	    Value result = context.eval(source);
	    if (result.isHostObject()) {
		return result.asHostObject();
	    }
	    return null;
	}
    }
}
