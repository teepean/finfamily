package fi.kaila.suku.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.PlaceLocationData;

/**
 * Shows world map from OpenStreet maps service with locations of relatives.
 * 
 * @author halonmi
 */
public class WorldMap extends JFrame implements ActionListener,
		SukuMapInterface {

	private static final long serialVersionUID = 1L;

	private final ISuku parent;
	private final WorldMap me;

	private JXMapKit map;

	/** The current places. */
	JComboBox currentPlaces;

	/** The missing places list. */
	JTextArea missingPlacesList;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent
	 */
	public WorldMap(ISuku parent) {
		this.parent = parent;
		this.me = this;
		initMe();
	}

	private void initMe() {

		map = new JXMapKit();
		map.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
		map.setZoom(12);

		JLabel lblC = new JLabel(Resurses.getString(Resurses.PLACECURRENT));

		currentPlaces = new JComboBox();
		currentPlaces.setActionCommand(Resurses.SHOWGRID);
		currentPlaces.addActionListener(this);

		JLabel lblM = new JLabel(Resurses.getString(Resurses.PLACEMISSING));

		this.missingPlacesList = new JTextArea();
		this.missingPlacesList.setEditable(false);
		JScrollPane js = new JScrollPane(this.missingPlacesList);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING).addComponent(
								map, 0, GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(lblC, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(currentPlaces, 250, 250,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblM, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(js, 250, 250,
										GroupLayout.PREFERRED_SIZE)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(map, 0, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addGroup(
										layout.createSequentialGroup()
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
																.addComponent(
																		lblC))
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.LEADING)
																.addComponent(
																		currentPlaces,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.LEADING)
																.addComponent(
																		lblM))
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.LEADING)
																.addComponent(
																		js)))));

		setSize(new Dimension(650, 680));

		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (parent != null) {
					parent.SukuFormClosing(me);

				}
				e.getClass();

			}
		});
	}

	private PlaceLocationData[] places = null;

	/**
	 * display map with listed places.
	 * 
	 * @param places
	 *            the places
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void displayMap(PlaceLocationData[] places) {
		this.places = places;
		int x = this.places.length;
		quicksort(this.places, 0, x - 1);
		StringBuilder sb = new StringBuilder();

		for (int xx = 0; xx < x; xx++) {
			if (this.places[xx].getLatitude() == 0) {
				sb.append(this.places[xx].getName() + "("
						+ this.places[xx].getCount() + ")\n");
			} else {
				this.currentPlaces.addItem(makeObj(this.places[xx].getName()
						+ "(" + this.places[xx].getCount() + ")"));
			}
		}
		missingPlacesList.setText(sb.toString());
	}

	private Object makeObj(final String item) {
		return new Object() {
			@Override
			public String toString() {
				return item;
			}
		};
	}

	/**
	 * Quicksort.
	 * 
	 * @param array
	 *            the array
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 */
	private static void quicksort(PlaceLocationData array[], int left, int right) {
		int leftIdx = left;
		int rightIdx = right;
		PlaceLocationData temp;

		if (((right - left) + 1) > 1) {
			int pivot = (left + right) / 2;
			while ((leftIdx <= pivot) && (rightIdx >= pivot)) {
				while ((array[leftIdx].getName().compareTo(
						array[pivot].getName()) < 0)
						&& (leftIdx <= pivot)) {
					leftIdx = leftIdx + 1;
				}
				while ((array[rightIdx].getName().compareTo(
						array[pivot].getName()) > 0)
						&& (rightIdx >= pivot)) {
					rightIdx = rightIdx - 1;
				}
				temp = array[leftIdx];
				array[leftIdx] = array[rightIdx];
				array[rightIdx] = temp;
				leftIdx = leftIdx + 1;
				rightIdx = rightIdx - 1;
				if ((leftIdx - 1) == pivot) {
					pivot = rightIdx = rightIdx + 1;
				} else if ((rightIdx + 1) == pivot) {
					pivot = leftIdx = leftIdx - 1;
				}
			}
			quicksort(array, left, pivot - 1);
			quicksort(array, pivot + 1, right);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		int xy = 0;
		int x = this.currentPlaces.getSelectedIndex();

		Set<FFWaypoint> waypoints = new HashSet<FFWaypoint>();

		for (PlaceLocationData place : places) {
			if (place.getLatitude() > 0) {
				if (xy == x) {
					map.setCenterPosition(new GeoPosition(place.getLatitude(),
							place.getLongitude()));
					waypoints.add(new FFWaypoint(place.getLatitude(), place
							.getLongitude(), place.getCount(),place.getName() + " " + place.getCount(),Color.RED));
				} else {
					waypoints.add(new FFWaypoint(place.getLatitude(), place
							.getLongitude(), place.getCount(),place.getName() + " " + place.getCount(),Color.BLUE));					
				}
				xy++;
			}
		}

		// Create a WaypointPainter to draw the points
		WaypointPainter<FFWaypoint> painter = new WaypointPainter<FFWaypoint>();
        painter.setWaypoints(waypoints);

		// Use own waypoint renderer
        painter.setRenderer(new FFWaypointRenderer());

		map.getMainMap().setOverlayPainter(painter);
	}

}
