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
package com.sitewhere.spi.microservice.configuration;

import com.google.inject.Injector;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;

import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;

/**
 * Microservice that supports dynamic monitoring of configuration.
 */
public interface IConfigurableMicroservice<F extends IFunctionIdentifier, C extends IMicroserviceConfiguration>
	extends IMicroservice<F, C> {

    /**
     * Get microservice configuration class.
     * 
     * @return
     */
    Class<C> getConfigurationClass();

    /**
     * Get instance configuration monitor.
     * 
     * @return
     */
    IInstanceConfigurationMonitor getInstanceConfigurationMonitor();

    /**
     * Get microservice configuration monitor.
     * 
     * @return
     */
    IMicroserviceConfigurationMonitor getMicroserviceConfigurationMonitor();

    /**
     * Get script configuration monitor.
     * 
     * @return
     */
    IScriptConfigurationMonitor getScriptConfigurationMonitor();

    /**
     * Get script version configuration monitor.
     * 
     * @return
     */
    IScriptVersionConfigurationMonitor getScriptVersionConfigurationMonitor();

    /**
     * Get scripting management interface.
     * 
     * @return
     */
    IScriptManagement getScriptManagement();

    /**
     * Get most recent k8s instance resource.
     * 
     * @return
     */
    SiteWhereInstance getLastInstanceResource();

    /**
     * Get most recent k8s microservice resource.
     * 
     * @return
     */
    SiteWhereMicroservice getLastMicroserviceResource();

    /**
     * Get the currently active configuration.
     * 
     * @return
     */
    C getMicroserviceConfiguration();

    /**
     * Creates a Guice module used to build microservice components based on the
     * active configuration.
     * 
     * @return
     */
    IMicroserviceModule<C> createConfigurationModule();

    /**
     * Get most recently configured microservice configuration module.
     * 
     * @return
     */
    IMicroserviceModule<C> getMicroserviceConfigurationModule();

    /**
     * Get most recently configured instance configuraion module.
     * 
     * @return
     */
    IInstanceModule getInstanceConfigurationModule();

    /**
     * Get Guice injector which allows access to tenant engine components which have
     * been configured via the module.
     */
    Injector getInjector();
}