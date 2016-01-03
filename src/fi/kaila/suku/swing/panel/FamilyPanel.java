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

package fi.kaila.suku.swing.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import fi.kaila.suku.swing.util.SukuPopupMenu;
import fi.kaila.suku.swing.util.SukuPopupMenu.MenuSource;
import fi.kaila.suku.util.FamilyParentRelationIndex;
import fi.kaila.suku.util.ImageSelection;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.TableShortData;

/**
 * FamilyPanel shows a simple graph of the subjects family, parents and
 * grandparents.
 *
 * @author Kalle
 */
public class FamilyPanel extends JPanel implements MouseListener, MouseMotionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final Vector<TableShortData> tabs = new Vector<TableShortData>();

	private final Vector<FamilyParentRelationIndex> pareRels = new Vector<FamilyParentRelationIndex>();
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private final PersonView parent;

	/**
	 * Instantiates a new family panel.
	 *
	 * @param parent
	 *            the parent
	 */
	public FamilyPanel(PersonView parent) {
		this.parent = parent;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	/**
	 * Copy to clip as image.
	 */
	public void copyToClipAsImage() {
		// Create a BufferedImage

		final Dimension dd = getPreferredSize();

		final BufferedImage image = new BufferedImage(dd.width, dd.height, BufferedImage.TYPE_INT_RGB);
		final Graphics g = image.getGraphics();
		final Graphics2D graphics = (Graphics2D) g;
		this.paint(graphics);
		final ImageSelection imgSel = new ImageSelection(image);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);

		try {
			ImageIO.write(image, "jpg", new File("component.jpg"));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// JAI allows Bitmap,
		// others only GIF,PNG,JSPEG - JPEG:correction

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {

		// }
		// public void paint(Graphics g) {

		final Rectangle d = this.getBounds();
		g.setColor(Color.white);
		g.fillRect(0, 0, d.width, d.height);

		g.setColor(Color.black);

		// for (int i = 0; i < tabs.size(); i++) {
		final Graphics2D gg = (Graphics2D) g;
		gg.setStroke(new BasicStroke(3));
		for (int i = 0; i < pareRels.size(); i++) {

			final FamilyParentRelationIndex rel = pareRels.get(i);

			final TableShortData child = tabs.get(rel.getChildIdx());
			final TableShortData parent = tabs.get(rel.getParentIdx());

			final Point cp = child.getLocation();
			final Point pp = parent.getLocation();
			final Dimension dd = child.getSize(g);
			final Dimension dp = parent.getSize(g);

			if (parent.getSubject().getSex().equals("M")) {
				gg.setColor(Color.blue);
			} else if (parent.getSubject().getSex().equals("F")) {
				gg.setColor(Color.red);
			} else {
				gg.setColor(Color.black);
			}

			if (rel.getSurety() == 100) {
				gg.setStroke(new BasicStroke(3));
			} else {
				gg.setStroke(new BasicStroke(2));
			}

			drawSuretyLine(gg, new Point(pp.x + (dp.width / 2), cp.y - 6 - (i * 4)),
					new Point(pp.x + (dp.width / 2), pp.y + dp.height), rel.getSurety());

			drawSuretyLine(gg, new Point(pp.x + (dp.width / 2), cp.y - 6 - (i * 4)),
					new Point(cp.x + (dd.width / 2), cp.y - 6 - (i * 4)), rel.getSurety());

			drawSuretyLine(gg, new Point(cp.x + (dd.width / 2), cp.y),
					new Point(cp.x + (dd.width / 2), cp.y - 6 - (i * 4)), rel.getSurety());

		}
		gg.setColor(Color.black);
		gg.setStroke(new BasicStroke(2));
		for (int i = tabs.size() - 1; i >= 0; i--) {
			final TableShortData t = tabs.get(i);
			if ((t == null) || (t.getSubject() == null) || (t.getSubject().getSex() == null)) {
				return;
			}
			Color color = null;
			if (t.getSubject().getSex().equals("M")) {
				color = Color.blue;
			} else if (t.getSubject().getSex().equals("F")) {
				color = Color.red;
			} else {
				color = Color.black;
			}

			t.drawMe(gg, color);
		}
		gg.setStroke(new BasicStroke());
		g.setColor(Color.blue);
		for (int x = 0; x < d.width; x += 100) {
			g.drawLine(x, 0, x, 20);
			g.drawString("" + x, x, 10);

		}
		final Dimension prefd = new Dimension();
		for (int i = 0; i < tabs.size(); i++) {
			final TableShortData t = tabs.get(i);
			final int x = t.getLocation().x + t.getSize(g).width;
			final int y = t.getLocation().y + t.getSize(g).height;
			if (prefd.width < x) {
				prefd.width = x;
			}
			if (prefd.height < y) {
				prefd.height = y;
			}

			logger.finer("END[" + i + "]=" + tabs.get(i));
		}
		prefd.width += 20;
		prefd.height += 20;
		logger.finer("PREFD[" + prefd.width + "," + prefd.height + "]");
		setPreferredSize(prefd);

	}

	private void drawSuretyLine(Graphics2D gg, Point aa, Point bb, int surety) {
		double piecelen = 20;
		final double ax = aa.x;
		final double ay = aa.y;
		final double bx = bb.x;
		final double by = bb.y;
		int piece = surety;
		final double hypoLen = Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(ay - by, 2));
		final Point2D points[] = new Point2D[(int) (hypoLen / (piecelen)) + 1];

		if (surety > 80) {
			gg.drawLine((int) ax, (int) ay, (int) bx, (int) by);
		} else {

			if (points.length < 2) {
				return;
			}
			for (int i = 0; i < points.length; i++) {
				points[i] = new Point();
			}
			if (surety > 50) {
				piece = (surety == 80 ? 80 : 40);
			} else {
				piece = (surety == 40 ? 50 : 100);
			}
			piecelen = (int) hypoLen / (points.length - 1);
			final double lineLen = (piece * piecelen) / 100;
			points[0].setLocation(ax, ay);

			points[points.length - 1].setLocation(bx, by);

			final double aux = (bx - ax) / (points.length - 1);
			final double auy = (by - ay) / (points.length - 1);
			for (int i = 1; i < (points.length - 1); i++) {
				points[i].setLocation(points[i - 1].getX() + aux, points[i - 1].getY() + auy);
			}
			// logger.info("surety=" + surety + " (" + a.x + "," + a.y + ");("
			// + b.x + "," + b.y + ")");
			for (int i = 0; i < (points.length - 1); i++) {

				final double auxx = (points[i + 1].getX() - points[i].getX()) * (lineLen / piecelen);

				final double auyy = (points[i + 1].getY() - points[i].getY()) * (lineLen / piecelen);

				if (surety > 50) {

					// gg.drawLine(points[i].x, points[i].y, e.x, e.y);
					gg.drawLine((int) points[i].getX(), (int) points[i].getY(), (int) (points[i].getX() + auxx),
							(int) (points[i].getY() + auyy));
				} else {
					if (surety > 10) {
						gg.drawString("?", (float) points[i].getX(), (float) points[i].getY());
						if (surety > 30) {
							gg.drawString("?", (float) (points[i].getX() + auxx), (float) (points[i].getY() + auyy));
						}
					} else {
						gg.drawString("☻", (float) points[i].getX(), (float) points[i].getY());
					}
				}
				// logger.info("part[" + i + "]= (" + points[i].x + ","
				// + points[i].y + ");(" + points[i + 1].x + ","
				// + points[i + 1].y + "):(" + (points[i].x + e.x) + ","
				// + (points[i].y + e.y) + ")");

			}

		}
	}

	/**
	 * empty the graph.
	 */
	public void resetTable() {
		tabs.clear();
		pareRels.clear();
	}

	/**
	 * add a table to the graph.
	 *
	 * @param data
	 *            the data
	 */
	public void addTable(TableShortData data) {
		tabs.add(data);

		updateUI();

	}

	/**
	 * Check if person exists in family tree.
	 *
	 * @param pid
	 *            the pid
	 * @return true if person exists
	 */
	public boolean containsPerson(int pid) {
		for (final TableShortData t : tabs) {
			if (t.existsPerson(pid)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the owner pid.
	 *
	 * @return owner pid of family tree
	 */
	public int getOwnerPid() {
		if (tabs.size() == 0) {
			return 0;
		}
		if (tabs.size() == 0) {
			return 0;
		}
		final TableShortData t = tabs.get(0);
		return t.getSubject().getPid();
	}

	/**
	 * Gets the tab size.
	 *
	 * @return table size = number of tables in list
	 */
	public int getTabSize() {
		return this.tabs.size();
	}

	/**
	 * Add parent relation.
	 *
	 * @param relIdx
	 *            the rel idx
	 */
	public void addRels(FamilyParentRelationIndex relIdx) {

		pareRels.add(relIdx);
		updateUI();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {

			final Point pp = e.getPoint();
			for (int i = 0; i < tabs.size(); i++) {
				final TableShortData subjectTable = tabs.get(i);

				final Rectangle dd = subjectTable.getArea();
				if (dd != null) {
					if (dd.contains(pp)) {
						final Point point = new Point(pp.x - dd.x, pp.y - dd.y);
						final PersonShortData person = subjectTable.getPersonAtPoint(point);
						if (person != null) {
							try {
								parent.setSubjectForFamily(person.getPid());
							} catch (final SukuException e1) {
								logger.log(Level.WARNING, "failed", e1);

							}
						}
					}
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		presTab = null;
		// System.out.println("ENT: " + e.toString());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {

		if ((e.getButton() == 1) && (presTab != null)) {
			// System.out.println("EXT: " + e.toString());
			presTab = null;
		}
	}

	/** The pres tab. */
	TableShortData presTab = null;

	/** The pres from. */
	Point presFrom = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		presTab = null;
		if (e.getButton() == 1) {
			final Point presPoint = e.getPoint();
			for (int i = 0; i < tabs.size(); i++) {
				final TableShortData t = tabs.get(i);
				final Rectangle rec = t.getArea();
				if (rec.contains(presPoint)) {
					presTab = t;
					presFrom = new Point(presPoint.x - t.getArea().x, presPoint.y - t.getArea().y);
					// System.out.println("PRS: " + t.toString());
					break;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if ((e.getButton() == 1) && (presTab != null)) {
			// System.out.println("DROP IT HERE: " + e.toString());
			final Point p = e.getPoint();
			p.x -= presFrom.x;
			p.y -= presFrom.y;
			presTab.setLocation(p);
			this.updateUI();
			presTab = null;
			presFrom = null;
		}
		if ((e.getButton() == MouseEvent.BUTTON3) && (e.getClickCount() == 1)) {

			final Point pp = e.getPoint();
			for (int i = 0; i < tabs.size(); i++) {
				final TableShortData subjectTable = tabs.get(i);

				final Rectangle dd = subjectTable.getArea();

				if (dd.contains(pp)) {
					// System.out.println("Osui: ");// +
					// subjectTable.getSubject().getTextName());
					final Point point = new Point(pp.x - dd.x, pp.y - dd.y);

					final PersonShortData person = subjectTable.getPersonAtPoint(point);
					if (person != null) {
						final SukuPopupMenu pop = SukuPopupMenu.getInstance();
						pop.setPerson(person);
						pop.show(e, pp.x, pp.y, MenuSource.familyView);
						//
						// parent.getSuku().pShowPerson.setText(person.getAlfaName());
						// parent.getSuku().pMenu.show(e.getComponent(),pp.x,pp.y);

						// pMenu.show(e.getComponent(),
						// e.getX(), e.getY());

						// System.out.println("Henkilöön: " +
						// person.getTextName());
					} else {
						// System.out.println("Tyhjään");
					}

				}

			}

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (presTab != null) {
			final Point p = e.getPoint();
			p.x -= presFrom.x;
			p.y -= presFrom.y;
			presTab.setLocation(p);
			this.updateUI();
			// System.out.println("DRG: " + e.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		// System.out.println("MOV: " + e.toString());

	}

	//
}
