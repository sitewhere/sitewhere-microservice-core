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

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.keycloak.representations.AccessTokenResponse;

import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.user.IUserManagement;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Manages validation of JWT tokens.
 */
@ApplicationScoped
public class TokenManagement implements ITokenManagement {

    /** User management */
    @Inject
    private IUserManagement userManagement;

    /** Public key */
    private PublicKey publicKey;

    /**
     * Get or parse public key.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected PublicKey getPublicKey() throws SiteWhereException {
	if (this.publicKey == null) {
	    try {
		byte[] publicBytes = Base64.getDecoder().decode(getUserManagement().getPublicKey());
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		this.publicKey = keyFactory.generatePublic(keySpec);
	    } catch (NoSuchAlgorithmException e) {
		throw new SiteWhereException(e);
	    } catch (InvalidKeySpecException e) {
		throw new SiteWhereException(e);
	    }
	}
	return this.publicKey;
    }

    /*
     * @see com.sitewhere.spi.microservice.security.ITokenManagement#
     * getAuthenticationForUser(java.lang.String, java.lang.String)
     */
    @Override
    public SiteWhereAuthentication getAuthenticationForUser(String username, String password)
	    throws SiteWhereException {
	String accessTokenStr = getUserManagement().getAccessToken(username, password);
	AccessTokenResponse accessToken = MarshalUtils.unmarshalJson(accessTokenStr.getBytes(),
		AccessTokenResponse.class);
	return getAuthenticationFromToken(accessToken.getToken());
    }

    /*
     * @see com.sitewhere.spi.microservice.security.ITokenManagement#
     * getAuthenticationFromToken(java.lang.String)
     */
    @Override
    public SiteWhereAuthentication getAuthenticationFromToken(String token) throws SiteWhereException {
	Claims claims = getClaimsForToken(token);
	String username = getUsernameFromClaims(claims);
	List<String> auths = getGrantedAuthoritiesFromClaims(claims);
	return new SiteWhereAuthentication(username, auths, token);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getClaimsForToken(java.lang.String)
     */
    public Claims getClaimsForToken(String token) throws SiteWhereException {
	try {
	    return Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token).getBody();
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
	return claims.get("preferred_username", String.class);
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
    @SuppressWarnings("unchecked")
    public List<String> getGrantedAuthoritiesFromClaims(Claims claims) throws SiteWhereException {
	List<String> auths = new ArrayList<>();
	Map<String, Object> realmAccess = claims.get("realm_access", Map.class);
	if (realmAccess != null) {
	    List<String> claimRoles = (List<String>) realmAccess.get("roles");
	    if (claimRoles != null) {
		auths.addAll(claimRoles);
	    }
	}
	return auths;
    }

    protected IUserManagement getUserManagement() {
	return userManagement;
    }
}