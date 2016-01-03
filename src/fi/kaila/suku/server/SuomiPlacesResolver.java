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

package fi.kaila.suku.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PlaceLocationData;

/**
 * Class used by SuomiMap view.
 *
 * @author Kalle
 */
public class SuomiPlacesResolver {

	/**
	 * Server class to fetch addresses to request list.
	 *
	 * @param con
	 *            the con
	 * @param request
	 *            the request
	 * @return array of places with coordinates
	 * @throws SukuException
	 *             the suku exception
	 */
	public static PlaceLocationData[] resolveSuomiPlaces(Connection con, PlaceLocationData[] request, boolean isH2)
			throws SukuException {

		if (request == null) {
			return request;
		}

		int idx;

		final PlaceLocationData[] response = request;

		final StringBuilder sql = new StringBuilder();
		if (isH2) {
			sql.append(
					"select location_X,location_Y,countrycode from placelocations where placename || ';' || countrycode  in ( ");
			sql.append("select placename || ';' || countrycode from placeothernames where othername = ?) ");
			sql.append("union ");
			sql.append("select location_X,location_Y,countrycode from placelocations where placename = ? ");
		} else {
			sql.append(
					"select location[0],location[1],countrycode from placelocations where placename || ';' || countrycode  in ( ");
			sql.append("select placename || ';' || countrycode from placeothernames where othername = ?) ");
			sql.append("union ");
			sql.append("select location[0],location[1],countrycode from placelocations where placename = ? ");
		}

		PreparedStatement pstm = null;
		ResultSet rs = null;
		String countryCode = null;
		try {
			pstm = con.prepareStatement(sql.toString());

			for (idx = 0; idx < response.length; idx++) {

				pstm.setString(1, response[idx].getName().toUpperCase());
				pstm.setString(2, response[idx].getName().toUpperCase());

				rs = pstm.executeQuery();
				while (rs.next()) {
					countryCode = rs.getString(3);
					if (countryCode.equalsIgnoreCase(response[idx].getCountryCode())
							|| (response[idx].getCountryCode() == null)) {
						response[idx].setLongitude(rs.getDouble(1));
						response[idx].setLatitude(rs.getDouble(2));
					}

				}
				rs.close();

			}
		} catch (final SQLException e) {
			e.printStackTrace();
			throw new SukuException("Placelocations error " + e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (final SQLException ex) {
				// SQLException
			}
			try {
				if (pstm != null) {
					pstm.close();
				}
			} catch (final SQLException ex) {
				// SQLException
			}
		}
		return response;

	}

}
