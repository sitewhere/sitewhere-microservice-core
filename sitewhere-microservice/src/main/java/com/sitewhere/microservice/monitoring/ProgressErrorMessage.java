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
package com.sitewhere.microservice.monitoring;

import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.monitoring.IProgressErrorMessage;

/**
 * Progress message that includes error information.
 */
public class ProgressErrorMessage extends ProgressMessage implements IProgressErrorMessage {

    /** Serial version UID */
    private static final long serialVersionUID = -7676418806779136207L;

    /** Error level for message */
    private ErrorLevel level;

    public ProgressErrorMessage() {
    }

    public ProgressErrorMessage(String taskName, double progressPercentage, String message, ErrorLevel level) {
	super(taskName, progressPercentage, message);
	this.level = level;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressErrorMessage#getLevel()
     */
    @Override
    public ErrorLevel getLevel() {
	return level;
    }

    public void setLevel(ErrorLevel level) {
	this.level = level;
    }
}