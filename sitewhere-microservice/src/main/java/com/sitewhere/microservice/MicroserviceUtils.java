/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;

public class MicroserviceUtils {

    /**
     * Get instance name for a microservice.
     * 
     * @param microservice
     * @return
     */
    public static String getInstanceName(SiteWhereMicroservice microservice) {
	String instanceName = microservice.getMetadata().getLabels().get(ResourceLabels.LABEL_SITEWHERE_INSTANCE);
	if (instanceName == null) {
	    throw new RuntimeException(String.format("Microservice '%s' does not have an instance name label.",
		    microservice.getMetadata().getName()));
	}
	return instanceName;
    }

    /**
     * Get functional area for a microservice.
     * 
     * @param microservice
     * @return
     */
    public static String getFunctionalArea(SiteWhereMicroservice microservice) {
	String functionalArea = microservice.getMetadata().getLabels()
		.get(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA);
	if (functionalArea == null) {
	    throw new RuntimeException(String.format("Microservice '%s' does not have a functional area label.",
		    microservice.getMetadata().getName()));
	}
	return functionalArea;
    }
}
