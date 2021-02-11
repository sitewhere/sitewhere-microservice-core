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

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.model.UserModel.GUser;
import com.sitewhere.grpc.user.UserModelConverter;
import com.sitewhere.microservice.cache.RedisCacheProvider;
import com.sitewhere.spi.SiteWhereException;
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
    public static class UserByTokenCache extends RedisCacheProvider<String, IUser> {

	public UserByTokenCache(IMicroservice<?, ?> microservice, ICacheConfiguration configuration) {
	    super(microservice, USER_BY_USERNAME, configuration);
	}

	/*
	 * @see
	 * com.sitewhere.microservice.cache.RedisCacheProvider#convertKey(java.lang.
	 * Object)
	 */
	@Override
	public String convertKey(String key) throws SiteWhereException {
	    return key;
	}

	/*
	 * @see com.sitewhere.microservice.cache.RedisCacheProvider#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(IUser value) throws SiteWhereException {
	    GUser message = UserModelConverter.asGrpcUser(value);
	    return GrpcUtils.marshal(message);
	}

	@Override
	public IUser deserialize(byte[] value) throws SiteWhereException {
	    try {
		return UserModelConverter.asApiUser(GUser.parseFrom(value));
	    } catch (InvalidProtocolBufferException e) {
		throw new SiteWhereException("Unable to parse gRPC message.", e);
	    }
	}
    }
}