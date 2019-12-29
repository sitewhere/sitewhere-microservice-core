/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class ScriptingUtils {

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
}
