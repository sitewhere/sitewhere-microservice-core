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
package com.sitewhere.microservice.api.event;

import java.util.List;

import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.common.ILocation;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Utility functions for dealing with geographic information.
 */
public class GeoUtils {

    /**
     * Creates a JTS point from a device location.
     * 
     * @param location
     * @return
     */
    public static Point createPointForLocation(IDeviceLocation location) {
	GeometryFactory fact = new GeometryFactory();
	return fact.createPoint(
		new Coordinate(location.getLongitude().doubleValue(), location.getLatitude().doubleValue()));
    }

    /**
     * Creates a JTS polygon based on zone definition.
     * 
     * @param zone
     * @return
     */
    public static Polygon createPolygonForZone(IZone zone) {
	return createPolygonForLocations(zone.getBounds());
    }

    /**
     * Create a polgon for a list of locations.
     * 
     * @param locations
     * @return
     */
    public static <T extends ILocation> Polygon createPolygonForLocations(List<T> locations) {
	Coordinate[] coords = new Coordinate[locations.size() + 1];
	for (int x = 0; x < locations.size(); x++) {
	    ILocation loc = locations.get(x);
	    coords[x] = new Coordinate(loc.getLongitude(), loc.getLatitude());
	}
	ILocation loc = locations.get(0);
	coords[locations.size()] = new Coordinate(loc.getLongitude(), loc.getLatitude());

	GeometryFactory fact = new GeometryFactory();
	LinearRing linear = new GeometryFactory().createLinearRing(coords);
	return new Polygon(linear, null, fact);
    }
}
