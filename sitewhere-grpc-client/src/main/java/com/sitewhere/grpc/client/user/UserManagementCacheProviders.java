/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.user;

import java.util.List;

import com.sitewhere.microservice.cache.RedissonCacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.cache.ICacheConfiguration;
import com.sitewhere.spi.user.IUser;

/**
 * Cache providers for user management entities.
 */
public class UserManagementCacheProviders {

    public static final String USER_BY_USERNAME = "user_by_username";
    public static final String GRANTED_AUTHORITY_BY_TOKEN = "granted_auth_by_token";

    /**
     * Cache users by username.
     */
    public static class UserByTokenCache extends RedissonCacheProvider<String, IUser> {

	public UserByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, USER_BY_USERNAME, String.class, IUser.class, configuration);
	}
    }

    /**
     * Cache for user granted authorities.
     */
    @SuppressWarnings("rawtypes")
    public static class GrantedAuthorityByTokenCache extends RedissonCacheProvider<String, List> {

	public GrantedAuthorityByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, GRANTED_AUTHORITY_BY_TOKEN, String.class, List.class, configuration);
	}
    }
}