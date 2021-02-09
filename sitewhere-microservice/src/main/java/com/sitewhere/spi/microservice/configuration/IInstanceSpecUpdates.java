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

/**
 * Holds flags to indicate which aspects of instance specification were updated.
 */
public interface IInstanceSpecUpdates {

    /**
     * Indicates if instance is considered new.
     * 
     * @return
     */
    boolean isFirstUpdate();

    /**
     * Indicates whether instance namespace was updated.
     * 
     * @return
     */
    boolean isInstanceNamespaceUpdated();

    /**
     * Indicates whether configuration template was updated.
     * 
     * @return
     */
    boolean isConfigurationTemplateUpdated();

    /**
     * Indicates whether dataset template was updated.
     * 
     * @return
     */
    boolean isDatasetTemplateUpdated();

    /**
     * Indicates whether configuration was updated.
     * 
     * @return
     */
    boolean isConfigurationUpdated();
}
