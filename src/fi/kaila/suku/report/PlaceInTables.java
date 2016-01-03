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

package fi.kaila.suku.report;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Class PlaceInTables.
 *
 * @author Kaarle Kaila
 *
 *         Used to collect tables for the places in a report
 */
public class PlaceInTables implements Comparable<PlaceInTables> {

	private String place = null;
	private final LinkedHashMap<Long, Long> tableMap = new LinkedHashMap<Long, Long>();

	/**
	 * Instantiates a new place in tables.
	 *
	 * @param place
	 *            the place
	 */
	public PlaceInTables(String place) {
		this.place = place;
	}

	/**
	 * Gets the place.
	 *
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * Adds the table.
	 *
	 * @param tabNo
	 *            the tab no
	 */
	public void addTable(long tabNo) {
		final Long oldie = tableMap.get(tabNo);
		if (oldie == null) {
			tableMap.put(tabNo, tabNo);
		}
	}

	/**
	 * Gets the array.
	 *
	 * @return the array
	 */
	public long[] getArray() {

		final long temp[] = new long[tableMap.size()];
		final Set<Map.Entry<Long, Long>> entriesx = tableMap.entrySet();
		final Iterator<Map.Entry<Long, Long>> eex = entriesx.iterator();
		int i = 0;
		while (eex.hasNext()) {
			final Map.Entry<Long, Long> entrx = eex.next();
			final Long pit = entrx.getValue();
			temp[i++] = pit;
		}
		return temp;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final long temp[] = getArray();
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < temp.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append("" + temp[i]);
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PlaceInTables e) {

		return place.compareToIgnoreCase(e.place);

	}

}
