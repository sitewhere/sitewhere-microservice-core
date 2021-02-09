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
package com.sitewhere.spi.microservice.multitenant;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exception thrown when a tenant engine is expected to be available but is not
 * created and/or started.
 */
public class TenantEngineNotAvailableException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = -441640534300277367L;

    public TenantEngineNotAvailableException() {
    }

    public TenantEngineNotAvailableException(String message) {
	super(message);
    }

    public TenantEngineNotAvailableException(Throwable cause) {
	super(cause);
    }

    public TenantEngineNotAvailableException(String message, Throwable cause) {
	super(message, cause);
    }
}