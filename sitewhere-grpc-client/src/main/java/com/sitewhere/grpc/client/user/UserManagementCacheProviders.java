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