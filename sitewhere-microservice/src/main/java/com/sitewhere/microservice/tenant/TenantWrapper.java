/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.tenant;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sitewhere.spi.tenant.ITenant;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenantSpec;
import io.sitewhere.k8s.crd.tenant.TenantBrandingSpecification;

/**
 * Exposes {@link SiteWhereTenant} data via {@link ITenant} interface.
 */
public class TenantWrapper implements ITenant {

    /** Serial version UID */
    private static final long serialVersionUID = -7252682399973068344L;

    /** Parser for ISO dates */
    private static DateTimeFormatter FORMAT = ISODateTimeFormat.dateTimeNoMillis();

    /** Wrapped tenant */
    private SiteWhereTenant tenant;

    public TenantWrapper(SiteWhereTenant tenant) {
	this.tenant = tenant;
    }

    /*
     * @see com.sitewhere.spi.common.IColorProvider#getBackgroundColor()
     */
    @Override
    public String getBackgroundColor() {
	return getBranding() != null ? getBranding().getBackgroundColor() : null;
    }

    /*
     * @see com.sitewhere.spi.common.IColorProvider#getForegroundColor()
     */
    @Override
    public String getForegroundColor() {
	return getBranding() != null ? getBranding().getForegroundColor() : null;
    }

    /*
     * @see com.sitewhere.spi.common.IColorProvider#getBorderColor()
     */
    @Override
    public String getBorderColor() {
	return getBranding() != null ? getBranding().getBorderColor() : null;
    }

    /*
     * @see com.sitewhere.spi.common.IIconProvider#getIcon()
     */
    @Override
    public String getIcon() {
	return getBranding() != null ? getBranding().getIcon() : null;
    }

    /*
     * @see com.sitewhere.spi.common.IImageProvider#getImageUrl()
     */
    @Override
    public String getImageUrl() {
	return getBranding() != null ? getBranding().getImageUrl() : null;
    }

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return getTenantSpec() != null ? getTenantSpec().getMetadata() : null;
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenant#getToken()
     */
    @Override
    public String getToken() {
	return getTenantSpec() != null ? getTenant().getMetadata().getName() : null;
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenant#getName()
     */
    @Override
    public String getName() {
	return getTenantSpec() != null ? getTenantSpec().getName() : null;
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenant#getAuthenticationToken()
     */
    @Override
    public String getAuthenticationToken() {
	return getTenantSpec() != null ? getTenantSpec().getAuthenticationToken() : null;
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenant#getAuthorizedUserIds()
     */
    @Override
    public List<String> getAuthorizedUserIds() {
	return Arrays.asList(getTenantSpec() != null ? getTenantSpec().getAuthorizedUserIds() : new String[0]);
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenant#getConfigurationTemplateId()
     */
    @Override
    public String getConfigurationTemplateId() {
	return getTenantSpec() != null ? getTenantSpec().getConfigurationTemplate() : null;
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenant#getDatasetTemplateId()
     */
    @Override
    public String getDatasetTemplateId() {
	return getTenant().getSpec().getDatasetTemplate();
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenant#getCreatedDate()
     */
    @Override
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getCreatedDate() {
	return FORMAT.parseDateTime(getTenant().getMetadata().getCreationTimestamp()).toDate();
    }

    protected SiteWhereTenant getTenant() {
	return tenant;
    }

    protected SiteWhereTenantSpec getTenantSpec() {
	return getTenant().getSpec() != null ? getTenant().getSpec() : null;
    }

    protected TenantBrandingSpecification getBranding() {
	return getTenantSpec() != null ? getTenantSpec().getBranding() : null;
    }
}
