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
package com.sitewhere.microservice.api.device;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.group.IDeviceGroup;

/**
 * Configurable helper class that allows {@link DeviceGroup} model objects to be
 * created from {@link IDeviceGroup} SPI objects.
 */
public class DeviceGroupMarshalHelper {

    /**
     * Convert API object to model object.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public DeviceGroup convert(IDeviceGroup source) throws SiteWhereException {
	DeviceGroup group = new DeviceGroup();
	group.setName(source.getName());
	group.setDescription(source.getDescription());
	List<String> roles = new ArrayList<String>();
	roles.addAll(source.getRoles());
	group.setRoles(roles);
	BrandedEntity.copy(source, group);
	return group;
    }
}