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