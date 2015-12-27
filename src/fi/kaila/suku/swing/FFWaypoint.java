
package fi.kaila.suku.swing;

import java.awt.Color;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * A waypoint that also has a color, label and count
  */
public class FFWaypoint extends DefaultWaypoint
{
	private final String label;
	private final Color color;
	private final int count;

	/**
	 * @param count the count
	 * @param label the label
	 * @param color the color
	 * @param coord the coordinate
	 */
	public FFWaypoint(int count, String label, Color color, GeoPosition coord)
	{
		super(coord);
		this.count = count;
		this.label = label;
		this.color = color;
	}

	/**
	 * Instantiates a new special waypoint.
	 * 
	 * @param latitude
	 *            the latitude
	 * @param longitude
	 *            the longitude
	 * @param count
	 *            the count
	 * @param label the label
	 * @param color the color
	 */
	public FFWaypoint(double latitude, double longitude, int count, String label, Color color) {
		super(latitude, longitude);
		this.count = count;
		this.label = label;
		this.color = color;
	}

	/**
	 * @return the label text
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @return the color
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * @return the count
	 */
	public int getCount()
	{
		return count;
	}

	
}
