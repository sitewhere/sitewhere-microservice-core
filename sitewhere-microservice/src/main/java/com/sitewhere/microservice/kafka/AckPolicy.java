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
package com.sitewhere.microservice.kafka;

/**
 * Indicates level of acknowledgement producer expects from Kafka.
 */
public enum AckPolicy {

    /** No acknowledgement required */
    FireAndForget("0"),

    /** Acknowledgement from leader only */
    Leader("1"),

    /** Acknowledgement from all replicas */
    All("all");

    /** Config passed to Kafka */
    private String config;

    private AckPolicy(String config) {
	this.config = config;
    }

    public String getConfig() {
	return config;
    }
}
