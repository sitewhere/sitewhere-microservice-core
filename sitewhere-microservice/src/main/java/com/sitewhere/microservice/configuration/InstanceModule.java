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
package com.sitewhere.microservice.configuration;

import com.google.inject.AbstractModule;
import com.sitewhere.microservice.configuration.model.instance.InstanceConfiguration;
import com.sitewhere.microservice.configuration.model.instance.persistence.PersistenceConfigurations;
import com.sitewhere.spi.microservice.configuration.IInstanceModule;

/**
 * Guice module used to configure objects related to the instance-global model.
 */
public class InstanceModule extends AbstractModule implements IInstanceModule {

    /** Instance configuration */
    private InstanceConfiguration configuration;

    public InstanceModule(InstanceConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(PersistenceConfigurations.class).toInstance(getConfiguration().getPersistenceConfigurations());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.IInstanceModule#getConfiguration
     * ()
     */
    @Override
    public InstanceConfiguration getConfiguration() {
	return configuration;
    }
}
