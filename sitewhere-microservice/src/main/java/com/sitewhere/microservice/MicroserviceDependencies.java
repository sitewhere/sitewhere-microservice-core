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
package com.sitewhere.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.microservice.instance.InstanceSettings;
import com.sitewhere.microservice.kafka.KafkaTopicNaming;
import com.sitewhere.microservice.security.SystemUser;
import com.sitewhere.microservice.security.TokenManagement;
import com.sitewhere.microservice.user.KeycloakUserManagement;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.user.IUserManagement;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

/**
 * Spring configuration for microservice dependencies which can be autowired.
 */
@Configuration
public class MicroserviceDependencies {

    @Bean
    public IInstanceSettings instanceSettings() {
	return new InstanceSettings();
    }

    @Bean
    public IKafkaTopicNaming kafkaTopicNaming() {
	return new KafkaTopicNaming();
    }

    @Bean
    public IUserManagement userManagement() {
	return new KeycloakUserManagement();
    }

    @Bean
    public ITokenManagement tokenManagement() {
	return new TokenManagement();
    }

    @Bean
    public ISystemUser systemUser() {
	return new SystemUser();
    }

    @Bean
    public DefaultKubernetesClient k8sClient() {
	Config config = new ConfigBuilder().withNamespace(null).build();
	return new DefaultKubernetesClient(config);
    }
}
