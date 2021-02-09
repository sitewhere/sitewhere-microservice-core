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
package com.sitewhere.microservice.security;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exception thrown when an expired JWT is used for a REST request.
 */
public class JwtExpiredException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = -5163059734369946339L;

    public JwtExpiredException() {
    }

    public JwtExpiredException(String message, Throwable cause) {
	super(message, cause);
    }

    public JwtExpiredException(String message) {
	super(message);
    }

    public JwtExpiredException(Throwable cause) {
	super(cause);
    }
}