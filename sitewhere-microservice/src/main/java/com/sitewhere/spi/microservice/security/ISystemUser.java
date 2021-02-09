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
package com.sitewhere.spi.microservice.security;

import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.spi.SiteWhereException;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Support access to a global "superuser" for authorizing calls between
 * microservices.
 */
public interface ISystemUser {

    /**
     * Get authentication information for superuser.
     * 
     * @return
     * @throws SiteWhereException
     */
    SiteWhereAuthentication getAuthentication() throws SiteWhereException;

    /**
     * Get authentication for superuser in context of a given tenant.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    SiteWhereAuthentication getAuthenticationForTenant(SiteWhereTenant tenant) throws SiteWhereException;
}