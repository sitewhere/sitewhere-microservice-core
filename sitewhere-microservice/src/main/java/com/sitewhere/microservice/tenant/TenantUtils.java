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
package com.sitewhere.microservice.tenant;

import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

public class TenantUtils {

    /**
     * Copy fields from source to target.
     * 
     * @param source
     * @param target
     */
    public static void copy(ITenant source, Tenant target) throws SiteWhereException {
	target.setToken(source.getToken());
	target.setName(source.getName());
	target.setAuthenticationToken(source.getAuthenticationToken());
	target.setAuthorizedUserIds(source.getAuthorizedUserIds());
	target.setConfigurationTemplateId(source.getConfigurationTemplateId());
	target.setDatasetTemplateId(source.getDatasetTemplateId());
	target.setBackgroundColor(source.getBackgroundColor());
	target.setForegroundColor(source.getForegroundColor());
	target.setBorderColor(source.getBorderColor());
	target.setIcon(source.getIcon());
	target.setImageUrl(source.getImageUrl());
	target.setMetadata(source.getMetadata());
    }

    /**
     * Create a copy of the API object.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public static Tenant copy(ITenant source) throws SiteWhereException {
	Tenant tenant = new Tenant();
	TenantUtils.copy(source, tenant);
	return tenant;
    }
}
