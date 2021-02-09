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
package com.sitewhere.microservice.api.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.rest.model.device.charting.ChartEntry;
import com.sitewhere.rest.model.device.charting.ChartSeries;
import com.sitewhere.spi.device.charting.IChartSeries;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Builds chart series from measurements.
 */
public class ChartBuilder {

    /** Map of measurement names to series */
    private Map<String, ChartSeries<Double>> seriesByMeasurementName;

    /**
     * Process measurements into a list of charts series.
     * 
     * @param matches
     * @return
     */
    public List<IChartSeries<Double>> process(List<IDeviceMeasurement> matches, String[] measurementIds) {
	seriesByMeasurementName = new HashMap<String, ChartSeries<Double>>();
	List<String> mxids = null;
	if ((measurementIds != null) && (measurementIds.length > 0)) {
	    mxids = Arrays.asList(measurementIds);
	}

	// Add all measurements.
	for (IDeviceMeasurement mx : matches) {
	    addSeriesEntry(mx.getName(), mx.getValue().doubleValue(), mx.getEventDate());
	}
	// Sort entries by date.
	List<IChartSeries<Double>> results = new ArrayList<IChartSeries<Double>>();
	for (IChartSeries<Double> series : seriesByMeasurementName.values()) {
	    if ((mxids == null) || (mxids.contains(series.getMeasurementId()))) {
		Collections.sort(series.getEntries());
		results.add(series);
	    }
	}
	return results;
    }

    /**
     * Add a new measurement entry. Create a new series if one does not already
     * exist.
     * 
     * @param key
     * @param value
     * @param date
     */
    protected void addSeriesEntry(String key, Double value, Date date) {
	ChartSeries<Double> series = seriesByMeasurementName.get(key);
	if (series == null) {
	    ChartSeries<Double> newSeries = new ChartSeries<Double>();
	    newSeries.setMeasurementId(key);
	    seriesByMeasurementName.put(key, newSeries);
	    series = newSeries;
	}
	ChartEntry<Double> seriesEntry = new ChartEntry<Double>();
	seriesEntry.setValue(value);
	seriesEntry.setMeasurementDate(date);
	series.getEntries().add(seriesEntry);
    }
}