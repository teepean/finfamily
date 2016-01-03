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

package fi.kaila.suku.util;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import fi.kaila.suku.util.SukuTextField.Field;

/**
 * The Class SukuSenser.
 *
 * @author Kalle A singleton class that displays an intellisens window below a
 *         SukuTextField
 */
public class SukuSenser implements MouseListener {

	private final JWindow sens;
	private final JList lista;
	private final Vector<String> model;

	private SukuSenser() {
		sens = new JWindow();
		model = new Vector<String>();

		lista = new JList(model);
		final JScrollPane scroller = new JScrollPane(lista);

		// lista.addListSelectionListener(this);
		lista.addMouseListener(this);
		sens.add(scroller, BorderLayout.CENTER);
	}

	private static SukuSenser single = null;

	// private String tag = null;
	// private SukuTextField.Field fld = Field.Fld_Null;
	// private SukuTextField parent;
	private String[] paikat = { "Helsinki", "Espoo", "Tampere", "Porvoo", "Bromarf", "Hangö", "Tyrvää", "Tammela",
			"Peuramaa", "Borgå", "Heinola", "Hämeenlinna" };

	private String[] givennames = null;
	private String[] patronymes = null;
	private String[] surnames = null;
	private String[] descriptions = null;
	private String[] noticeTypes = null;
	private String[] groups = null;

	/**
	 * Initialize the places.
	 *
	 * @param places
	 *            the new places
	 */
	public void setPlaces(String[] places) {
		this.paikat = places;
	}

	/**
	 * Initialize the givennames.
	 *
	 * @param givennames
	 *            the new givennames
	 */
	public void setGivennames(String[] givennames) {
		this.givennames = givennames;
	}

	/**
	 * Initialize the patronymes.
	 *
	 * @param patronymes
	 *            the new patronymes
	 */
	public void setPatronymes(String[] patronymes) {
		this.patronymes = patronymes;
	}

	/**
	 * Initialize the surnames.
	 *
	 * @param surnames
	 *            the new surnames
	 */
	public void setSurnames(String[] surnames) {
		this.surnames = surnames;
	}

	/**
	 * Initialize the groups.
	 *
	 * @param groups
	 *            the new groups
	 */
	public void setGroups(String[] groups) {
		this.groups = groups;
	}

	/**
	 * initialize the descriptions.
	 *
	 * @param descriptions
	 *            the new descriptions
	 */
	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

	/**
	 * initialize the noticetypes.
	 *
	 * @param noticeTypes
	 *            the new notice types
	 */
	public void setNoticeTypes(String[] noticeTypes) {
		this.noticeTypes = noticeTypes;
	}

	/**
	 * Gets the single instance of SukuSenser.
	 *
	 * @return the handle
	 */
	public static SukuSenser getInstance() {
		if (single == null) {
			single = new SukuSenser();
		}

		single.sens.setVisible(false);

		return single;
	}

	private SukuTextField parent;

	/**
	 * Show intellisens.
	 *
	 * @param parent
	 *            the parent
	 * @param tag
	 *            the tag
	 * @param fld
	 *            the fld
	 */
	public void showSens(SukuTextField parent, String tag, Field fld) {
		final Rectangle rt = parent.getBounds();
		final Point pt = new Point(rt.x, rt.y);
		SwingUtilities.convertPointToScreen(pt, parent.getParent());
		final Rectangle rs = new Rectangle();
		rs.x = pt.x;
		rs.y = pt.y + rt.height;
		rs.width = rt.width;
		rs.height = 100;
		this.parent = parent;
		sens.setBounds(rs);
		final String txt = parent.getText();
		if (txt.length() > 0) {
			model.clear();
			switch (fld) {
			case Fld_Place:
				for (final String element : paikat) {
					if (element.toLowerCase().startsWith(txt.toLowerCase())) {
						model.add(element);
					}
				}
				break;
			case Fld_Givenname:
				for (final String givenname : givennames) {
					if (givenname.toLowerCase().startsWith(txt.toLowerCase())) {
						model.add(givenname);
					}
				}
				break;
			case Fld_Patronyme:
				for (final String patronyme : patronymes) {
					if (patronyme.toLowerCase().startsWith(txt.toLowerCase())) {
						model.add(patronyme);
					}
				}
				break;
			case Fld_Surname:
				for (final String surname : surnames) {
					if (surname.toLowerCase().startsWith(txt.toLowerCase())) {
						model.add(surname);
					}
				}
				break;
			case Fld_Type:
				for (final String noticeType : noticeTypes) {
					if (noticeType.toLowerCase().startsWith(txt.toLowerCase())) {
						model.add(noticeType);
					}
				}
				break;
			case Fld_Description:
				if (tag != null) {
					for (final String description : descriptions) {
						final int iix = description.indexOf(';');
						if (iix > 0) {
							final String myTag = description.substring(0, iix);
							final String myText = description.substring(iix + 1);
							if (myTag.equals(tag)) {

								if (myText.toLowerCase().startsWith(txt.toLowerCase())) {
									model.add(myText);
								}
							}
						}
					}
				}
				break;
			case Fld_Group:
				for (final String group : groups) {
					if (group.toLowerCase().startsWith(txt.toLowerCase())) {
						model.add(group);
					}
				}
				break;
			case Fld_Country:
				// TODO: Fld_Country ?
				break;
			case Fld_Null:
				// TODO: Fld_Null ?
				break;
			}
			lista.updateUI();
			listIndex = 0;
			lista.setSelectedIndex(listIndex);
			// System.out.println("reset to -1");
			sens.setVisible(model.size() > 0);
		} else {
			sens.setVisible(false);
		}
	}

	/**
	 * return top element from list.
	 *
	 * @param parent
	 *            the parent
	 * @return the sens
	 */
	public void getSens(SukuTextField parent) {
		if (model.size() > 0) {
			parent.setText(model.get(0));
			model.clear();
		}
		sens.setVisible(false);
	}

	/**
	 * Hide.
	 */
	public void hide() {
		sens.setVisible(false);
	}

	/**
	 * Checks if is visible.
	 *
	 * @return visible state of senser
	 */
	public boolean isVisible() {
		return sens.isVisible();
	}

	private int listIndex = 0;

	/**
	 * move selection in sens-list forward or backward.
	 *
	 * @param direction
	 *            the direction
	 */
	public void selectList(int direction) {
		// System.out.println("d:" + direction);
		if (direction == 40) {
			listIndex++;
		} else if (direction == 38) {
			listIndex--;
		} else if (direction == 10) {
			final int indexi = lista.getSelectedIndex();
			if ((indexi >= 0) && (indexi < model.size())) {
				final String aux = (String) lista.getSelectedValue();
				if (parent != null) {
					parent.setText(aux);
				}

				parent = null;
				return;
			}
			sens.setVisible(false);

		}
		if (listIndex < 0) {
			listIndex = 0;
		}
		if (listIndex >= model.size()) {
			listIndex = model.size() - 1;
		}

		if (listIndex >= 0) {
			lista.setSelectedIndex(listIndex);
			lista.ensureIndexIsVisible(listIndex);

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent m) {
		final int indexi = lista.getSelectedIndex();
		if ((indexi >= 0) && (indexi < model.size())) {
			final String aux = (String) lista.getSelectedValue();
			if (parent != null) {
				parent.setText(aux);
			}
			sens.setVisible(false);
			parent = null;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Not used
	}

}
