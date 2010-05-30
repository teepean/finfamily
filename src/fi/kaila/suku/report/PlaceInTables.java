package fi.kaila.suku.report;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Kaarle Kaila
 * 
 *         Used to collect tables for the places in a report
 * 
 */
public class PlaceInTables implements Comparable<PlaceInTables> {

	private String place = null;
	private LinkedHashMap<Long, Long> tableMap = new LinkedHashMap<Long, Long>();

	public PlaceInTables(String place) {
		this.place = place;
	}

	public String getPlace() {
		return place;
	}

	public void addTable(long tabNo) {
		Long oldie = tableMap.get(tabNo);
		if (oldie == null) {
			tableMap.put(tabNo, tabNo);
		}
	}

	public long[] getArray() {

		long temp[] = new long[tableMap.size()];
		Set<Map.Entry<Long, Long>> entriesx = tableMap.entrySet();
		Iterator<Map.Entry<Long, Long>> eex = entriesx.iterator();
		int i = 0;
		while (eex.hasNext()) {
			Map.Entry<Long, Long> entrx = (Map.Entry<Long, Long>) eex.next();
			Long pit = entrx.getValue();
			temp[i++] = pit;
		}
		return temp;
	}

	@Override
	public String toString() {
		long temp[] = getArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < temp.length; i++) {
			if (i > 0)
				sb.append(",");
			sb.append("" + temp[i]);
		}
		return sb.toString();
	}

	@Override
	public int compareTo(PlaceInTables e) {

		return place.compareToIgnoreCase(e.place);

	}

}