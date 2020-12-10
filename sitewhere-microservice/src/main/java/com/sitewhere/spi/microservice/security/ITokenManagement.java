/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.security;

import java.util.List;

import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.spi.SiteWhereException;

import io.jsonwebtoken.Claims;

/**
 * Allows for creating and validation of JWT tokens.
 */
public interface ITokenManagement {

    /**
     * Get authentication details for a username/password combination.
     * 
     * @param username
     * @param password
     * @return
     * @throws SiteWhereException
     */
    SiteWhereAuthentication getAuthenticationForUser(String username, String password) throws SiteWhereException;

    /**
     * Get authentication/authorization details from a JWT.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    SiteWhereAuthentication getAuthenticationFromToken(String token) throws SiteWhereException;

    /**
     * Get claims for the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    Claims getClaimsForToken(String token) throws SiteWhereException;

    /**
     * Get username from the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    String getUsernameFromToken(String token) throws SiteWhereException;

    /**
     * Get username from claims.
     * 
     * @param claims
     * @return
     * @throws SiteWhereException
     */
    String getUsernameFromClaims(Claims claims) throws SiteWhereException;

    /**
     * Get granted authorities from given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    List<String> getGrantedAuthoritiesFromToken(String token) throws SiteWhereException;

    /**
     * Get granted authorities from claims.
     * 
     * @param claims
     * @return
     * @throws SiteWhereException
     */
    List<String> getGrantedAuthoritiesFromClaims(Claims claims) throws SiteWhereException;
}