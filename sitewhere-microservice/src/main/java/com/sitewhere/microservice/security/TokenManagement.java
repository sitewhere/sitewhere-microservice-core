/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.security.ITokenManagement;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Manages creation and validation of JWT tokens.
 */
@ApplicationScoped
public class TokenManagement implements ITokenManagement {

    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getClaimsForToken(java.lang.String)
     */
    public Claims getClaimsForToken(String token) throws SiteWhereException {
	try {
	    return Jwts.parser().setSigningKey("").parseClaimsJws(token).getBody();
	} catch (ExpiredJwtException e) {
	    throw new JwtExpiredException("JWT has expired.", e);
	} catch (UnsupportedJwtException e) {
	    throw new InvalidJwtException("JWT not in supported format.", e);
	} catch (MalformedJwtException e) {
	    throw new InvalidJwtException("JWT not correctly formatted.", e);
	} catch (Throwable t) {
	    throw new SiteWhereException("Error decoding JWT.", t);
	}
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getUsernameFromToken(java.lang.String)
     */
    public String getUsernameFromToken(String token) throws SiteWhereException {
	return getUsernameFromClaims(getClaimsForToken(token));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getUsernameFromClaims(io.jsonwebtoken.Claims)
     */
    @Override
    public String getUsernameFromClaims(Claims claims) throws SiteWhereException {
	return claims.getSubject();
    }

    /*
     * @see com.sitewhere.spi.microservice.security.ITokenManagement#
     * getGrantedAuthoritiesFromToken(java.lang.String)
     */
    public List<String> getGrantedAuthoritiesFromToken(String token) throws SiteWhereException {
	return getGrantedAuthoritiesFromClaims(getClaimsForToken(token));
    }

    /*
     * @see com.sitewhere.spi.microservice.security.ITokenManagement#
     * getGrantedAuthoritiesFromClaims(io.jsonwebtoken.Claims)
     */
    @Override
    public List<String> getGrantedAuthoritiesFromClaims(Claims claims) throws SiteWhereException {
	return null;
    }

    public Date getExpirationDate(int expirationInMinutes) {
	return new Date(System.currentTimeMillis() + (expirationInMinutes * 60 * 1000));
    }
}