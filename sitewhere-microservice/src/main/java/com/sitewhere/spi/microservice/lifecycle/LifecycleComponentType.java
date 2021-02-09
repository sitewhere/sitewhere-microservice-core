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
package com.sitewhere.spi.microservice.lifecycle;

/**
 * Enumerates types of components in the system.
 */
public enum LifecycleComponentType {

    /** Includes the entire system */
    System,

    /** Engine for a single tenant */
    TenantEngine,

    /** Data store management */
    DataStore,

    /** Cache provider */
    CacheProvider,

    /** Asset module manager */
    AssetModuleManager,

    /** Asset module */
    AssetModule,

    /** Search provider manager */
    SearchProviderManager,

    /** Search provider */
    SearchProvider,

    /** Outbound connector */
    OutboundConnector,

    /** Outbound event processor filter */
    OutboundEventProcessorFilter,

    /** Inbound event processor */
    InboundEventProcessor,

    /** Device communication subsystem */
    DeviceCommunication,

    /** Event processing subsystem */
    EventProcessing,

    /** Command processing strategy */
    CommandProcessingStrategy,

    /** Command destination */
    CommandDestination,

    /** Command execution builder */
    CommandExecutionBuilder,

    /** Command execution encoder */
    CommandExecutionEncoder,

    /** Command target resolver */
    CommandTargetResolver,

    /** Command delivery provider */
    CommandDeliveryProvider,

    /** Command parameter extractor */
    CommandParameterExtractor,

    /** Command router */
    CommandRouter,

    /** Outbound processing strategy */
    OutboundProcessingStrategy,

    /** Registration manager */
    RegistrationManager,

    /** Label generator manager */
    LabelGeneratorManager,

    /** Label generator */
    LabelGenerator,

    /** Batch operation manager */
    BatchOperationManager,

    /** Device presence manager */
    DevicePresenceManager,

    /** Device stream manager */
    DeviceStreamManager,

    /** Schedule manager */
    ScheduleManager,

    /** Inbound processing strategy */
    InboundProcessingStrategy,

    /** Event source */
    InboundEventSource,

    /** Device event decoder */
    DeviceEventDecoder,

    /** Device event deduplicator */
    DeviceEventDeduplicator,

    /** Event receiver */
    InboundEventReceiver,

    /** Resource manager */
    ResourceManager,

    /** Tenant template manager */
    TenantTemplateManager,

    /** Dataset template manager */
    DatasetTemplateManager,

    /** Script template manager */
    ScriptTemplateManager,

    /** Unclassified component */
    Other,
}