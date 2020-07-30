package com.sitewhere.microservice.api.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.rest.model.device.DeviceAssignmentSummary;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.device.IDeviceAssignmentSummary;

/**
 * Configurable helper class that allows device assignment model objects to be
 * created from {@link IDeviceAssignmentSummary} SPI objects.
 */
public class DeviceAssignmentSummaryMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceAssignmentSummaryMarshalHelper.class);

    /** Indicates whether device asset information is to be included */
    private boolean includeAsset = true;

    /**
     * Convert the SPI object into a model object for marshaling.
     * 
     * @param source
     * @param assetManagement
     * @return
     * @throws SiteWhereException
     */
    public DeviceAssignmentSummary convert(IDeviceAssignmentSummary source, IAssetManagement assetManagement)
	    throws SiteWhereException {
	DeviceAssignmentSummary result = new DeviceAssignmentSummary();
	result.setDeviceId(source.getDeviceId());
	result.setDeviceToken(source.getDeviceToken());
	result.setDeviceTypeId(source.getDeviceTypeId());
	result.setDeviceTypeName(source.getDeviceTypeName());
	result.setDeviceTypeImageUrl(source.getDeviceTypeImageUrl());
	result.setCustomerId(source.getCustomerId());
	result.setCustomerName(source.getCustomerName());
	result.setCustomerImageUrl(source.getCustomerImageUrl());
	result.setAreaId(source.getAreaId());
	result.setAreaName(source.getAreaName());
	result.setAreaImageUrl(source.getAreaImageUrl());
	result.setActiveDate(source.getActiveDate());
	result.setReleasedDate(source.getReleasedDate());
	result.setStatus(source.getStatus());
	PersistentEntity.copy(source, result);

	// Add linked objects.
	addAssetInformation(source, assetManagement, result);
	return result;
    }

    /**
     * Add asset information.
     * 
     * @param source
     * @param assetManagement
     * @param result
     * @throws SiteWhereException
     */
    protected void addAssetInformation(IDeviceAssignmentSummary source, IAssetManagement assetManagement,
	    DeviceAssignmentSummary result) throws SiteWhereException {
	result.setAssetId(source.getAssetId());
	if (source.getAssetId() != null) {
	    IAsset asset = assetManagement.getAsset(source.getAssetId());
	    result.setAssetName(asset.getName());
	    result.setAssetImageUrl(asset.getImageUrl());
	}
    }

    public boolean isIncludeAsset() {
	return includeAsset;
    }

    public DeviceAssignmentSummaryMarshalHelper setIncludeAsset(boolean includeAsset) {
	this.includeAsset = includeAsset;
	return this;
    }
}
