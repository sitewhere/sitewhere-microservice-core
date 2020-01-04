/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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