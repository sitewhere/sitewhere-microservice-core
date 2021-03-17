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
package com.sitewhere.microservice.configuration.model.instance;

import com.sitewhere.microservice.configuration.model.instance.debugging.Debugging;
import com.sitewhere.microservice.configuration.model.instance.infrastructure.InfrastructureConfiguration;
import com.sitewhere.microservice.configuration.model.instance.persistence.PersistenceConfigurations;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Top level of instance configuration hierarchy.
 */
@RegisterForReflection
public class InstanceConfiguration {

    /** Infrastructure configuration */
    private InfrastructureConfiguration infrastructure;

    /** Persistence configurations */
    private PersistenceConfigurations persistenceConfigurations;

    /** Instance-level debug configuration */
    private Debugging debugging;

    public InfrastructureConfiguration getInfrastructure() {
	return infrastructure;
    }

    public void setInfrastructure(InfrastructureConfiguration infrastructure) {
	this.infrastructure = infrastructure;
    }

    public PersistenceConfigurations getPersistenceConfigurations() {
	return persistenceConfigurations;
    }

    public void setPersistenceConfigurations(PersistenceConfigurations persistenceConfigurations) {
	this.persistenceConfigurations = persistenceConfigurations;
    }

    public Debugging getDebugging() {
	return debugging;
    }

    public void setDebugging(Debugging debugging) {
	this.debugging = debugging;
    }
}
