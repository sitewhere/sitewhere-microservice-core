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
package com.sitewhere.microservice.api.device.asset;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Wraps a {@link DeviceMeasurement} so that information about the asset
 * associated with its assignment is available.
 */
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class DeviceMeasurementsWithAsset extends DeviceEventWithAsset implements IDeviceMeasurement {

    /** Serial version UID */
    private static final long serialVersionUID = -732056996257170342L;

    public DeviceMeasurementsWithAsset(IDeviceMeasurement wrapped, IAssetManagement assetManagement)
	    throws SiteWhereException {
	super(wrapped, assetManagement);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceMeasurement#getName()
     */
    @Override
    public String getName() {
	return ((IDeviceMeasurement) getWrapped()).getName();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceMeasurement#getValue()
     */
    @Override
    public BigDecimal getValue() {
	return ((IDeviceMeasurement) getWrapped()).getValue();
    }
}