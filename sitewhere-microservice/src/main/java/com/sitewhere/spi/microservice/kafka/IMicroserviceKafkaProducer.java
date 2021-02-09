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
package com.sitewhere.spi.microservice.kafka;

import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.RecordMetadata;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;

/**
 * Component that produces messages that are sent to a Kafka topic.
 */
public interface IMicroserviceKafkaProducer<K, P> extends ILifecycleComponent {

    /**
     * Get key serializer class.
     * 
     * @return
     */
    public Class<?> getKeySerializer();

    /**
     * Get value serializer class.
     * 
     * @return
     */
    public Class<?> getValueSerializer();

    /**
     * Get name of Kafka topic which will receive the messages.
     * 
     * @return
     * @throws SiteWhereException
     */
    String getTargetTopicName() throws SiteWhereException;

    /**
     * Send a message to the topic.
     * 
     * @param key
     * @param message
     * @return
     * @throws SiteWhereException
     */
    Future<RecordMetadata> send(K key, P message) throws SiteWhereException;
}