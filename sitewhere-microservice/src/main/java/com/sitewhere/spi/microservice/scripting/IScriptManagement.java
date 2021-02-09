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
package com.sitewhere.spi.microservice.scripting;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;

/**
 * Management interface for interacting with scripts.
 */
public interface IScriptManagement extends ILifecycleComponent {

    /**
     * Get list of metadata entries for all scripts in a functional area.
     * 
     * @param identifier
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    List<IScriptMetadata> getScriptMetadataList(IFunctionIdentifier identifier, String tenantId)
	    throws SiteWhereException;

    /**
     * Get list of metadata entries for all scripts in a functional area which
     * belong to the given category.
     * 
     * @param identifier
     * @param tenantId
     * @param category
     * @return
     * @throws SiteWhereException
     */
    List<IScriptMetadata> getScriptMetadataListForCategory(IFunctionIdentifier identifier, String tenantId,
	    String category) throws SiteWhereException;

    /**
     * Get metadata for a given script.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    IScriptMetadata getScriptMetadata(IFunctionIdentifier identifier, String tenantId, String scriptId)
	    throws SiteWhereException;

    /**
     * Creates a new script.
     * 
     * @param identifier
     * @param tenantId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IScriptMetadata createScript(IFunctionIdentifier identifier, String tenantId, IScriptCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get content for script based on unique script id and version identifier.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    byte[] getScriptContent(IFunctionIdentifier identifier, String tenantId, String scriptId, String versionId)
	    throws SiteWhereException;

    /**
     * Update an existing script.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IScriptMetadata updateScript(IFunctionIdentifier identifier, String tenantId, String scriptId, String versionId,
	    IScriptCreateRequest request) throws SiteWhereException;

    /**
     * Creates a new version of a script that is a clone of the given version.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @param comment
     * @return
     * @throws SiteWhereException
     */
    IScriptVersion cloneScript(IFunctionIdentifier identifier, String tenantId, String scriptId, String versionId,
	    String comment) throws SiteWhereException;

    /**
     * Activate the given version of the script. This sets the active id and forces
     * the content to be copied into the scripts content folder whether it has been
     * updated or not.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    IScriptMetadata activateScript(IFunctionIdentifier identifier, String tenantId, String scriptId, String versionId)
	    throws SiteWhereException;

    /**
     * Delete an existing script including metadata and all versions.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    IScriptMetadata deleteScript(IFunctionIdentifier identifier, String tenantId, String scriptId)
	    throws SiteWhereException;
}