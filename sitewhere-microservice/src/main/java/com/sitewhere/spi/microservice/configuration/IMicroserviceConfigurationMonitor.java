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

import java.util.List;

import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;

/**
 * Monitors microservice configuration and notifies listeners of changes.
 */
public interface IMicroserviceConfigurationMonitor {

    /**
     * Start configuration monitoring event loop.
     */
    void start();

    /**
     * Get list of listeners.
     * 
     * @return
     */
    List<IMicroserviceConfigurationListener> getListeners();

    /**
     * Get resource being monitored.
     * 
     * @return
     */
    SiteWhereMicroservice getResource();
}
