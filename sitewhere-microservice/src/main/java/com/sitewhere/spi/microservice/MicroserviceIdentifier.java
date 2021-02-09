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
package com.sitewhere.spi.microservice;

/**
 * Provides a list of known identifiers for microservices.
 */
public enum MicroserviceIdentifier implements IFunctionIdentifier {

    AssetManagement("asset-management"),

    BatchOperations("batch-operations"),

    CommandDelivery("command-delivery"),

    DeviceManagement("device-management"),

    DeviceRegistration("device-registration"),

    EventManagement("event-management"),

    EventSearch("event-search"),

    EventSources("event-sources"),

    InboundProcessing("inbound-processing"),

    InstanceManagement("instance-management"),

    LabelGeneration("label-generation"),

    OutboundConnectors("outbound-connectors"),

    DeviceState("device-state"),

    RuleProcessing("rule-processing"),

    ScheduleManagement("schedule-management"),

    StreamingMedia("streaming-media"),

    WebRest("web-rest");

    /** Path */
    private String path;

    private MicroserviceIdentifier(String path) {
	this.path = path;
    }

    public static MicroserviceIdentifier getByPath(String path) {
	for (MicroserviceIdentifier value : MicroserviceIdentifier.values()) {
	    if (value.getPath().equals(path)) {
		return value;
	    }
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.IFunctionIdentifier#getPath()
     */
    @Override
    public String getPath() {
	return path;
    }

    /*
     * @see com.sitewhere.spi.microservice.IFunctionIdentifier#getShortName()
     */
    @Override
    public String getShortName() {
	return name();
    }
}