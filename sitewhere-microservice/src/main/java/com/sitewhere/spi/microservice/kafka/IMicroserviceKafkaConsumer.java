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

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Component that consumes messages that are sent to a Kafka topic.
 */
public interface IMicroserviceKafkaConsumer extends ITenantEngineLifecycleComponent {

    /**
     * Get unique consumer id.
     * 
     * @return
     * @throws SiteWhereException
     */
    String getConsumerId() throws SiteWhereException;

    /**
     * Get unique consumer group id.
     * 
     * @return
     * @throws SiteWhereException
     */
    String getConsumerGroupId() throws SiteWhereException;

    /**
     * Get wrapped consumer instance.
     * 
     * @return
     */
    KafkaConsumer<String, byte[]> getConsumer();

    /**
     * Get name of Kafka topics which will provide the messages.
     * 
     * @return
     * @throws SiteWhereException
     */
    List<String> getSourceTopicNames() throws SiteWhereException;

    /**
     * Process a batch of records for a partition.
     * 
     * @param topicPartition
     * @param records
     */
    void process(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records);
}