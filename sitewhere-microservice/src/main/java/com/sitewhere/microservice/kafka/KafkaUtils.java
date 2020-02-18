/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import com.sitewhere.microservice.configuration.model.instance.infrastructure.KafkaConfiguration;
import com.sitewhere.spi.microservice.IMicroservice;

public class KafkaUtils {

    /**
     * Get bootstrap servers from microservice configuration.
     * 
     * @param microservice
     * @return
     */
    public static String getBootstrapServers(IMicroservice<?, ?> microservice) {
	String systemNamespace = microservice.getInstanceConfiguration().getInfrastructure().getNamespace();
	KafkaConfiguration kafka = microservice.getInstanceConfiguration().getInfrastructure().getKafka();
	return String.format("%s.%s:%d", kafka.getHostname(), systemNamespace, kafka.getPort());
    }
}
