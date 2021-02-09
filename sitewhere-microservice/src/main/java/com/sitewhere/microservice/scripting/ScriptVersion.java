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
package com.sitewhere.microservice.scripting;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sitewhere.spi.microservice.scripting.IScriptVersion;

/**
 * Get information about a version of a script.
 */
public class ScriptVersion implements IScriptVersion {

    /** Version id */
    private String versionId;

    /** Comment */
    private String comment;

    /** Created date */
    private Date createdDate;

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptVersion#getVersionId()
     */
    @Override
    public String getVersionId() {
	return versionId;
    }

    public void setVersionId(String versionId) {
	this.versionId = versionId;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptVersion#getComment()
     */
    @Override
    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptVersion#getCreatedDate()
     */
    @Override
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
	this.createdDate = createdDate;
    }
}