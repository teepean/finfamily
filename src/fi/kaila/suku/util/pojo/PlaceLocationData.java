/**
 * Software License Agreement (BSD License)
 *
 * Copyright 2010-2016 Kaarle Kaila and Mika Halonen. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice, this list of
 *      conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice, this list
 *      of conditions and the following disclaimer in the documentation and/or other materials
 *      provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY KAARLE KAILA AND MIKA HALONEN ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL KAARLE KAILA OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Kaarle Kaila and Mika Halonen.
 */

package fi.kaila.suku.util.pojo;

import java.io.Serializable;

/**
 * Used by Map task to contain Place name and location data.
 *
 * @author Kalle
 */
public class PlaceLocationData implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String placeName = null;
	private String countryCode = null;
	private int counter = 0;
	private double latitude = 0;
	private double longitude = 0;

	/**
	 * Instantiates a new place location data.
	 *
	 * @param place
	 *            the place
	 * @param countryCode
	 *            the country code
	 */
	public PlaceLocationData(String place, String countryCode) {
		this.placeName = place;
		this.countryCode = countryCode;
		this.counter = 1;

	}

	/**
	 * add one to number of occurrences.
	 */
	public void increment() {
		this.counter++;
	}

	/**
	 * Gets the name.
	 *
	 * @return placename
	 */
	public String getName() {
		return this.placeName;
	}

	/**
	 * Gets the country code.
	 *
	 * @return country code for place
	 */
	public String getCountryCode() {
		return this.countryCode;
	}

	/**
	 * Gets the count.
	 *
	 * @return count of places
	 */
	public int getCount() {
		return this.counter;
	}

	/**
	 * Sets the latitude.
	 *
	 * @param latitude
	 *            the new latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Sets the longitude.
	 *
	 * @param longitude
	 *            the new longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Gets the latitude.
	 *
	 * @return latitude
	 */
	public double getLatitude() {
		return this.latitude;
	}

	/**
	 * Gets the longitude.
	 *
	 * @return longitude
	 */
	public double getLongitude() {
		return this.longitude;
	}

}
