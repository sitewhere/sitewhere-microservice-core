/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.scripting.IScriptCreateRequest;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.microservice.scripting.IScriptVersion;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScript;
import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScriptList;
import io.sitewhere.k8s.crd.tenant.scripting.SiteWhereScriptSpec;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersion;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersionList;
import io.sitewhere.k8s.crd.tenant.scripting.version.SiteWhereScriptVersionSpec;

/**
 * Default {@link IScriptManagement} implementation. Stores scripts in
 * Kubernetes.
 */
public class KubernetesScriptManagement extends LifecycleComponent implements IScriptManagement {

    /** Parser for ISO dates */
    private static DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTimeParser();

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptManagement#
     * getScriptMetadataList(com.sitewhere.spi.microservice.IFunctionIdentifier,
     * java.lang.String)
     */
    @Override
    public List<IScriptMetadata> getScriptMetadataList(IFunctionIdentifier identifier, String tenantId)
	    throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	Map<String, String> labels = new HashMap<>();
	labels.put(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA, identifier.getPath());
	labels.put(ResourceLabels.LABEL_SITEWHERE_TENANT, tenantId);
	SiteWhereScriptList list = getMicroservice().getSiteWhereKubernetesClient().getScripts().inNamespace(namespace)
		.withLabels(labels).list();

	// Convert scripts to SiteWhere API format.
	List<IScriptMetadata> results = new ArrayList<>();
	for (SiteWhereScript script : list.getItems()) {
	    results.add(convertScriptMetadata(script, false));
	}
	return results;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptManagement#
     * getScriptMetadataListForCategory(com.sitewhere.spi.microservice.
     * IFunctionIdentifier, java.lang.String, java.lang.String)
     */
    @Override
    public List<IScriptMetadata> getScriptMetadataListForCategory(IFunctionIdentifier identifier, String tenantId,
	    String category) throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	Map<String, String> labels = new HashMap<>();
	labels.put(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA, identifier.getPath());
	labels.put(ResourceLabels.LABEL_SITEWHERE_TENANT, tenantId);
	labels.put(ResourceLabels.LABEL_SCRIPTING_SCRIPT_CATEGORY, category);
	SiteWhereScriptList list = getMicroservice().getSiteWhereKubernetesClient().getScripts().inNamespace(namespace)
		.withLabels(labels).list();

	// Convert scripts to SiteWhere API format.
	List<IScriptMetadata> results = new ArrayList<>();
	for (SiteWhereScript script : list.getItems()) {
	    results.add(convertScriptMetadata(script, false));
	}
	return results;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#getScriptMetadata(
     * com.sitewhere.spi.microservice.IFunctionIdentifier, java.lang.String,
     * java.lang.String)
     */
    @Override
    public IScriptMetadata getScriptMetadata(IFunctionIdentifier identifier, String tenantId, String scriptId)
	    throws SiteWhereException {
	SiteWhereScript match = getK8sScript(identifier, tenantId, scriptId);
	return convertScriptMetadata(match, true);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#createScript(com.
     * sitewhere.spi.microservice.IFunctionIdentifier, java.lang.String,
     * com.sitewhere.spi.microservice.scripting.IScriptCreateRequest)
     */
    @Override
    public IScriptMetadata createScript(IFunctionIdentifier identifier, String tenantId, IScriptCreateRequest request)
	    throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereScript existing = getK8sScript(identifier, tenantId, request.getId());
	if (existing != null) {
	    throw new SiteWhereException("A script with that id already exists.");
	}

	// Create script k8s object and populate metadata.
	SiteWhereScript k8sScript = createK8sScript(identifier, tenantId, request);
	k8sScript = getMicroservice().getSiteWhereKubernetesClient().getScripts().inNamespace(namespace)
		.create(k8sScript);

	SiteWhereScriptVersion k8sVersion = createK8sScriptVersion(k8sScript, request, "Initial version.");
	k8sVersion = getMicroservice().getSiteWhereKubernetesClient().getScriptsVersions().inNamespace(namespace)
		.create(k8sVersion);

	ScriptMetadata created = convertScriptMetadata(k8sScript, false);
	IScriptVersion version = convertScriptVersion(k8sVersion);

	activateScript(identifier, tenantId, request.getId(), version.getVersionId());
	return created;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#getScriptContent(
     * com.sitewhere.spi.microservice.IFunctionIdentifier, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public byte[] getScriptContent(IFunctionIdentifier identifier, String tenantId, String scriptId, String versionId)
	    throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereScriptVersion version = getMicroservice().getSiteWhereKubernetesClient().getScriptsVersions()
		.inNamespace(namespace).withName(versionId).get();
	return version != null ? version.getSpec().getContent().getBytes() : null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#updateScript(com.
     * sitewhere.spi.microservice.IFunctionIdentifier, java.lang.String,
     * java.lang.String, java.lang.String,
     * com.sitewhere.spi.microservice.scripting.IScriptCreateRequest)
     */
    @Override
    public IScriptMetadata updateScript(IFunctionIdentifier identifier, String tenantId, String scriptId,
	    String versionId, IScriptCreateRequest request) throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereScript match = getK8sScript(identifier, tenantId, scriptId);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.Error, ErrorLevel.ERROR);
	}

	if (request.getName() != null) {
	    match.getSpec().setName(request.getName());
	}
	if (request.getDescription() != null) {
	    match.getSpec().setDescription(request.getDescription());
	}
	getMicroservice().getSiteWhereKubernetesClient().getScripts().inNamespace(namespace).createOrReplace(match);

	SiteWhereScriptVersion version = getMicroservice().getSiteWhereKubernetesClient().getScriptsVersions()
		.inNamespace(namespace).withName(versionId).get();
	if (version != null) {
	    version.getSpec().setContent(new String(Base64.getDecoder().decode(request.getContent())));
	    getMicroservice().getSiteWhereKubernetesClient().getScriptsVersions().inNamespace(namespace)
		    .createOrReplace(version);
	}

	return convertScriptMetadata(match, false);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#cloneScript(com.
     * sitewhere.spi.microservice.IFunctionIdentifier, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public IScriptVersion cloneScript(IFunctionIdentifier identifier, String tenantId, String scriptId,
	    String versionId, String comment) throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereScript match = getK8sScript(identifier, tenantId, scriptId);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.Error, ErrorLevel.ERROR);
	}

	SiteWhereScriptVersion version = getMicroservice().getSiteWhereKubernetesClient().getScriptsVersions()
		.inNamespace(namespace).withName(versionId).get();
	if (version == null) {
	    throw new SiteWhereSystemException(ErrorCode.Error, ErrorLevel.ERROR);
	}

	ScriptCreateRequest request = new ScriptCreateRequest();
	request.setContent(Base64.getEncoder().encodeToString(version.getSpec().getContent().getBytes()));
	SiteWhereScriptVersion k8sVersion = createK8sScriptVersion(match, request, comment);
	k8sVersion = getMicroservice().getSiteWhereKubernetesClient().getScriptsVersions().inNamespace(namespace)
		.create(k8sVersion);
	return convertScriptVersion(k8sVersion);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#activateScript(com
     * .sitewhere.spi.microservice.IFunctionIdentifier, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public IScriptMetadata activateScript(IFunctionIdentifier identifier, String tenantId, String scriptId,
	    String versionId) throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereScript match = getK8sScript(identifier, tenantId, scriptId);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.Error, ErrorLevel.ERROR);
	}

	SiteWhereScriptVersion version = getMicroservice().getSiteWhereKubernetesClient().getScriptsVersions()
		.inNamespace(namespace).withName(versionId).get();
	if (version == null) {
	    throw new SiteWhereSystemException(ErrorCode.Error, ErrorLevel.ERROR);
	}

	match.getSpec().setActiveVersion(versionId);
	match = getMicroservice().getSiteWhereKubernetesClient().getScripts().inNamespace(namespace)
		.createOrReplace(match);
	return convertScriptMetadata(match, false);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#deleteScript(com.
     * sitewhere.spi.microservice.IFunctionIdentifier, java.lang.String,
     * java.lang.String)
     */
    @Override
    public IScriptMetadata deleteScript(IFunctionIdentifier identifier, String tenantId, String scriptId)
	    throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereScript match = getK8sScript(identifier, tenantId, scriptId);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.Error, ErrorLevel.ERROR);
	}
	getMicroservice().getSiteWhereKubernetesClient().getScripts().inNamespace(namespace)
		.withName(match.getMetadata().getName()).delete();
	return convertScriptMetadata(match, false);
    }

    /**
     * Get k8s script based on criteria.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    protected SiteWhereScript getK8sScript(IFunctionIdentifier identifier, String tenantId, String scriptId)
	    throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	Map<String, String> labels = new HashMap<>();
	labels.put(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA, identifier.getPath());
	labels.put(ResourceLabels.LABEL_SITEWHERE_TENANT, tenantId);
	labels.put(ResourceLabels.LABEL_SCRIPTING_SCRIPT_ID, scriptId);
	SiteWhereScriptList list = getMicroservice().getSiteWhereKubernetesClient().getScripts().inNamespace(namespace)
		.withLabels(labels).list();
	if (list.getItems().size() > 1) {
	    throw new SiteWhereException("Multiple scripts found matching criteria.");
	}
	if (list.getItems().size() == 0) {
	    return null;
	}
	return list.getItems().get(0);
    }

    /**
     * Create k8s script from request information.
     * 
     * @param identifier
     * @param tenantId
     * @param request
     * @return
     */
    protected SiteWhereScript createK8sScript(IFunctionIdentifier identifier, String tenantId,
	    IScriptCreateRequest request) {
	SiteWhereScript script = new SiteWhereScript();

	ObjectMeta meta = new ObjectMeta();
	String name = String.format("%s-%s-%s", identifier.getPath(), request.getId(),
		String.valueOf(System.currentTimeMillis()));
	meta.setName(name);
	meta.setNamespace(getMicroservice().getInstanceSettings().getKubernetesNamespace());
	Map<String, String> labels = new HashMap<>();
	labels.put(ResourceLabels.LABEL_SITEWHERE_TENANT, tenantId);
	labels.put(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA, identifier.getPath());
	labels.put(ResourceLabels.LABEL_SCRIPTING_SCRIPT_ID, request.getId());
	labels.put(ResourceLabels.LABEL_SCRIPTING_SCRIPT_CATEGORY, request.getCategory());
	meta.setLabels(labels);
	script.setMetadata(meta);

	SiteWhereScriptSpec spec = new SiteWhereScriptSpec();
	spec.setScriptId(request.getId());
	spec.setName(request.getName());
	spec.setDescription(request.getDescription());
	spec.setInterpreterType(request.getInterpreterType());
	script.setSpec(spec);

	return script;
    }

    /**
     * Create k8s version from request information.
     * 
     * @param script
     * @param request
     * @param comment
     * @return
     */
    protected SiteWhereScriptVersion createK8sScriptVersion(SiteWhereScript script, IScriptCreateRequest request,
	    String comment) {
	Map<String, String> scriptLabels = script.getMetadata().getLabels();
	SiteWhereScriptVersion version = new SiteWhereScriptVersion();

	ObjectMeta meta = new ObjectMeta();
	String name = String.format("%s-v%s", script.getMetadata().getName(),
		String.valueOf(System.currentTimeMillis()));
	meta.setName(name);
	meta.setNamespace(getMicroservice().getInstanceSettings().getKubernetesNamespace());
	Map<String, String> labels = new HashMap<>();
	labels.put(ResourceLabels.LABEL_SITEWHERE_TENANT, scriptLabels.get(ResourceLabels.LABEL_SITEWHERE_TENANT));
	labels.put(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA,
		scriptLabels.get(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA));
	labels.put(ResourceLabels.LABEL_SCRIPTING_SCRIPT_ID,
		scriptLabels.get(ResourceLabels.LABEL_SCRIPTING_SCRIPT_ID));
	meta.setLabels(labels);
	version.setMetadata(meta);

	SiteWhereScriptVersionSpec spec = new SiteWhereScriptVersionSpec();
	spec.setComment(comment);
	spec.setContent(new String(Base64.getDecoder().decode(request.getContent())));
	version.setSpec(spec);

	return version;
    }

    /**
     * Create {@link ScriptMetadata} based on k8s {@link SiteWhereScript}.
     * 
     * @param script
     * @param includeVersions
     * @return
     */
    protected ScriptMetadata convertScriptMetadata(SiteWhereScript script, boolean includeVersions) {
	ScriptMetadata meta = new ScriptMetadata();
	meta.setId(script.getSpec().getScriptId());
	meta.setName(script.getSpec().getName());
	meta.setDescription(script.getSpec().getDescription());
	meta.setInterpreterType(script.getSpec().getInterpreterType());
	meta.setActiveVersion(script.getSpec().getActiveVersion());
	meta.setCategory(script.getMetadata().getLabels().get(ResourceLabels.LABEL_SCRIPTING_SCRIPT_CATEGORY));

	// TODO: This is expensive since it's a k8s API query for each script.
	if (includeVersions) {
	    meta.setVersions(new ArrayList<ScriptVersion>());
	    SiteWhereScriptVersionList k8sVersions = getVersionsForScript(script);
	    for (SiteWhereScriptVersion k8sVersion : k8sVersions.getItems()) {
		meta.getVersions().add(convertScriptVersion(k8sVersion));
	    }
	    Collections.sort(meta.getVersions(), new Comparator<IScriptVersion>() {

		@Override
		public int compare(IScriptVersion s1, IScriptVersion s2) {
		    return -1 * s1.getCreatedDate().compareTo(s2.getCreatedDate());
		}
	    });
	}

	return meta;
    }

    /**
     * Create {@link ScriptVersion} based on k8s {@link SiteWhereScriptVersion}.
     * 
     * @param k8s
     * @return
     */
    protected ScriptVersion convertScriptVersion(SiteWhereScriptVersion k8s) {
	ScriptVersion version = new ScriptVersion();
	version.setVersionId(k8s.getMetadata().getName());
	version.setCreatedDate(FORMATTER.parseDateTime(k8s.getMetadata().getCreationTimestamp()).toDate());
	version.setComment(k8s.getSpec().getComment());
	return version;
    }

    /**
     * Get versions for a given script.
     * 
     * @param script
     * @return
     */
    protected SiteWhereScriptVersionList getVersionsForScript(SiteWhereScript script) {
	String tenantId = script.getMetadata().getLabels().get(ResourceLabels.LABEL_SITEWHERE_TENANT);
	String functionalArea = script.getMetadata().getLabels().get(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA);
	String scriptId = script.getSpec().getScriptId();
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	Map<String, String> labels = new HashMap<>();
	labels.put(ResourceLabels.LABEL_SCRIPTING_SCRIPT_ID, scriptId);
	labels.put(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA, functionalArea);
	labels.put(ResourceLabels.LABEL_SITEWHERE_TENANT, tenantId);
	return getMicroservice().getSiteWhereKubernetesClient().getScriptsVersions().inNamespace(namespace)
		.withLabels(labels).list();
    }
}