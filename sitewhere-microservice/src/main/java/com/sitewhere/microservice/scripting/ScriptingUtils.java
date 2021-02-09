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
