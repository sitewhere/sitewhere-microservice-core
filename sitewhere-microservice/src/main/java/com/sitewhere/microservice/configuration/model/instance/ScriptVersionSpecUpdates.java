/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration.model.instance;

import com.sitewhere.spi.microservice.configuration.IScriptVersionSpecUpdates;

public class ScriptVersionSpecUpdates implements IScriptVersionSpecUpdates {

    /** Indicates if script content was updated */
    private boolean contentUpdated;

    public boolean isContentUpdated() {
	return contentUpdated;
    }

    public void setContentUpdated(boolean contentUpdated) {
	this.contentUpdated = contentUpdated;
    }
}
