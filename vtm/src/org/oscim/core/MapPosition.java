/*
 * Copyright 2012 Hannes Janetzek
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.core;

import org.oscim.utils.FastMath;

/** A MapPosition Container. */
public class MapPosition {

	/** projected position x 0..1 */
	public double x;
	/** projected position y 0..1 */
	public double y;
	/** absolute scale */
	public double scale;

	/** rotation angle */
	public float bearing;
	/** perspective tile */
	public float tilt;

	/** to be removed: FastMath.log2((int) scale) */
	public int zoomLevel;

	public MapPosition() {
		this.scale = 1;
		this.x = 0.5;
		this.y = 0.5;
		this.zoomLevel = 1;
		this.bearing = 0;
	}

	public MapPosition(double latitude, double longitude, double scale) {
		setPosition(latitude, longitude);
		setScale(scale);
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
		this.scale = 1 << zoomLevel;
	}

	public void setScale(double scale) {
		this.zoomLevel = FastMath.log2((int) scale);
		this.scale = scale;
	}

	public void setPosition(GeoPoint geoPoint) {
		setPosition(geoPoint.getLatitude(), geoPoint.getLongitude());
	}

	public void setPosition(double latitude, double longitude) {
		latitude = MercatorProjection.limitLatitude(latitude);
		longitude = MercatorProjection.limitLongitude(longitude);
		this.x = MercatorProjection.longitudeToX(longitude);
		this.y = MercatorProjection.latitudeToY(latitude);
	}

	public void copy(MapPosition other) {
		this.x = other.x;
		this.y = other.y;

		this.bearing = other.bearing;
		this.scale = other.scale;
		this.tilt = other.tilt;
		this.zoomLevel = other.zoomLevel;
	}

	public void set(double x, double y, double scale, float bearing, float tilt) {
		this.x = x;
		this.y = y;
		this.scale = scale;

		while (bearing > 180)
			bearing -= 360;
		while (bearing < -180)
			bearing += 360;
		this.bearing = bearing;

		this.tilt = tilt;
		this.zoomLevel = FastMath.log2((int) scale);
	}

	/**
	 * @return scale relative to zoom-level.
	 */
	public double getZoomScale() {
		return scale / (1 << zoomLevel);
	}

	public GeoPoint getGeoPoint() {
		return new GeoPoint(MercatorProjection.toLatitude(y),
		                    MercatorProjection.toLongitude(x));
	}

	public double getLatitude() {
		return MercatorProjection.toLatitude(y);
	}

	public double getLongitude() {
		return MercatorProjection.toLongitude(x);
	}

	public void setByBoundingBox(BoundingBox bbox, int viewWidth, int viewHeight) {
		double minx = MercatorProjection.longitudeToX(bbox.getMinLongitude());
		double miny = MercatorProjection.latitudeToY(bbox.getMaxLatitude());

		double dx = Math.abs(MercatorProjection.longitudeToX(bbox.getMaxLongitude()) - minx);
		double dy = Math.abs(MercatorProjection.latitudeToY(bbox.getMinLatitude()) - miny);
		double zx = viewWidth / (dx * Tile.SIZE);
		double zy = viewHeight / (dy * Tile.SIZE);

		scale = Math.min(zx, zy);
		x = minx + dx / 2;
		y = miny + dy / 2;
		bearing = 0;
		tilt = 0;
	}

	@Override
	public String toString() {
		return new StringBuilder()
		    .append("[X:").append(x)
		    .append(", Y:").append(y)
		    .append(", Z:").append(zoomLevel)
		    .append("] lat:")
		    .append(MercatorProjection.toLatitude(y))
		    .append(", lon:")
		    .append(MercatorProjection.toLongitude(x))
		    .toString();
	}
}
