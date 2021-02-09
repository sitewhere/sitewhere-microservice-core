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

import org.apache.kafka.streams.processor.ProcessorSupplier;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.kafka.IProcessorSupplierComponent;

/**
 * Base class for {@link ProcessorSupplier} components that also participate in
 * the SiteWhere lifecycle.
 * 
 * @param <K>
 * @param <V>
 */
public abstract class ProcessorSupplierComponent<K, V> extends TenantEngineLifecycleComponent
	implements IProcessorSupplierComponent<K, V> {
}
