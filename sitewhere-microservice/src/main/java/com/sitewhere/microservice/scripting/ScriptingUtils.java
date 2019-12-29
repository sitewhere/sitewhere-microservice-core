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
