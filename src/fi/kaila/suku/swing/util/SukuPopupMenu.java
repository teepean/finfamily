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

package fi.kaila.suku.swing.util;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.PersonShortData;

/**
 * popupmenu for database view.
 *
 * @author Kalle
 */
public class SukuPopupMenu {

	private static SukuPopupMenu me = null;

	private JPopupMenu pMenu = null;
	private final JMenuItem pShowPerson;
	private final JMenuItem pShowTimeline;
	private final JMenuItem pShowRelatives;
	private final JMenuItem pShowFamily;
	private final JMenuItem pMainPerson;
	private final JMenuItem pDeletePerson;
	private final JMenu pAddPerson;
	private final JMenuItem pAddAsChild;
	private final JMenuItem pAddAsSpouse;
	private final JMenuItem pAddAsParent;
	private final JMenu pJoinMenu;
	private final JMenuItem pJoinPerson;
	private final JMenuItem pJoinAddPerson;
	private PersonShortData persToJoinTo = null;
	// private JMenuItem pPersonView;
	private final JMenuItem pCopy;
	private final JMenuItem pNeedle;
	// private JMenuItem pPaste;
	private JMenuItem[] pHiskiPerson = null;

	private JMenuItem pReport = null;
	private PersonShortData currentPerson = null;

	private MenuSource callerType;

	/**
	 * The Enum MenuSource.
	 */
	public enum MenuSource {

		/** The db view. */
		dbView,
		/** The family view. */
		familyView
	}

	/**
	 * enables the hiskpperson menu.
	 *
	 * @param idx
	 *            the idx
	 * @param b
	 *            the b
	 * @param name
	 *            the name
	 */
	public void enableHiskiPerson(int idx, boolean b, String name) {
		if ((idx >= 0) && (idx < pHiskiPerson.length)) {
			pHiskiPerson[idx].setVisible(b);
			if (name != null) {
				pHiskiPerson[idx].setText(name + " [" + idx + "]");
			}
		}
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public MenuSource getSource() {
		return callerType;
	}

	/**
	 * add all actionlisteners for menu commands.
	 *
	 * @param l
	 *            the l
	 */
	public void addActionListener(ActionListener l) {
		pShowPerson.addActionListener(l);
		pShowTimeline.addActionListener(l);
		pShowRelatives.addActionListener(l);
		pMainPerson.addActionListener(l);
		pShowFamily.addActionListener(l);
		pDeletePerson.addActionListener(l);
		pAddAsChild.addActionListener(l);
		pAddAsSpouse.addActionListener(l);
		pAddAsParent.addActionListener(l);
		pJoinPerson.addActionListener(l);
		pJoinAddPerson.addActionListener(l);
		// pPersonView.addActionListener(l);
		pCopy.addActionListener(l);
		pNeedle.addActionListener(l);
		pReport.addActionListener(l);
		for (int i = 0; i < 30; i++) {
			pHiskiPerson[i].addActionListener(l);
		}
	}

	/**
	 * sets person on whom menu is shown.
	 *
	 * @param person
	 *            the new person
	 */
	public void setPerson(PersonShortData person) {
		this.currentPerson = person;
		// pShowPerson.setText(person.getAlfaName());
		pMainPerson.setText(person.getAlfaName());
	}

	/**
	 * Gets the person.
	 *
	 * @return the person for the meny
	 */
	public PersonShortData getPerson() {
		return currentPerson;
	}

	/**
	 * Enable join add.
	 *
	 * @param firstPers
	 *            the first pers
	 */
	public void enableJoinAdd(PersonShortData firstPers) {
		if (firstPers == null) {
			pJoinAddPerson.setVisible(false);
			persToJoinTo = firstPers;
			pJoinPerson.setText(Resurses.getString("JOIN_PERSON"));
		} else {
			pJoinAddPerson.setVisible(true);
			persToJoinTo = firstPers;
			pJoinPerson.setText(Resurses.getString("JOIN_PERSON") + " " + persToJoinTo.getName(true, true));
		}
	}

	/**
	 * show menu at location.
	 *
	 * @param e
	 *            the e
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param callerType
	 *            the caller type
	 */
	public void show(MouseEvent e, int x, int y, MenuSource callerType) {
		this.callerType = callerType;
		pMenu.show(e.getComponent(), x, y);
	}

	private SukuPopupMenu() {
		// so it can only be initited from within

		pMenu = new JPopupMenu();

		pMainPerson = new JMenuItem("A popup menu item");
		// pShowPerson.addActionListener(popupListener);
		pMainPerson.setActionCommand(Resurses.TAB_PERSON);
		pMenu.add(pMainPerson);
		pMenu.addSeparator();
		// pPersonView = new JMenuItem(Resurses.getString(Resurses.TAB_PERSON));
		// // pShowPerson.addActionListener(popupListener);
		// pPersonView.setActionCommand(Resurses.TAB_PERSON);
		// pMenu.add(pPersonView);

		pShowRelatives = new JMenuItem(Resurses.getString(Resurses.TAB_RELATIVES));
		// pShowPerson.addActionListener(popupListener);
		pShowRelatives.setActionCommand(Resurses.TAB_RELATIVES);
		pMenu.add(pShowRelatives);

		pShowPerson = new JMenuItem(Resurses.getString(Resurses.TAB_PERSON_TEXT));
		// pShowPerson.addActionListener(popupListener);
		pShowPerson.setActionCommand(Resurses.TAB_PERSON_TEXT);
		pMenu.add(pShowPerson);

		pShowTimeline = new JMenuItem(Resurses.getString(Resurses.TAB_TIMELINE_TEXT));
		// pShowTimeline.addActionListener(popupListener);
		pShowTimeline.setActionCommand(Resurses.TAB_TIMELINE_TEXT);
		pMenu.add(pShowTimeline);

		pShowFamily = new JMenuItem(Resurses.getString(Resurses.TAB_FAMILY));
		// pShowFamily.addActionListener(popupListener);
		pShowFamily.setActionCommand(Resurses.TAB_FAMILY);
		pMenu.add(pShowFamily);

		pDeletePerson = new JMenuItem(Resurses.getString("TOOLBAR.REMPERSON.TOOLTIP"));
		// pShowFamily.addActionListener(popupListener);
		pDeletePerson.setActionCommand(Resurses.TOOLBAR_REMPERSON_ACTION);
		pMenu.add(pDeletePerson);

		pAddPerson = new JMenu(Resurses.getString("MENU.ADDPERSON"));
		// pShowFamily.addActionListener(popupListener);
		// pAddPerson.setActionCommand("ADDPERSON");
		pMenu.add(pAddPerson);
		pAddAsChild = new JMenuItem(Resurses.getString("MENU.ADDASCHILD"));
		// pShowFamily.addActionListener(popupListener);
		pAddAsChild.setActionCommand("ADDCHILD");
		pAddPerson.add(pAddAsChild);

		pAddAsSpouse = new JMenuItem(Resurses.getString("MENU.ADDASSPOUSE"));
		// pShowFamily.addActionListener(popupListener);
		pAddAsSpouse.setActionCommand("ADDSPOUSE");
		pAddPerson.add(pAddAsSpouse);
		pAddAsParent = new JMenuItem(Resurses.getString("MENU.ADDASPARENT"));
		// pShowFamily.addActionListener(popupListener);
		pAddAsParent.setActionCommand("ADDPARENT");
		pAddPerson.add(pAddAsParent);

		pJoinMenu = new JMenu(Resurses.getString("JOIN_MENU"));

		pMenu.add(pJoinMenu);

		pJoinPerson = new JMenuItem(Resurses.getString("JOIN_PERSON"));
		pJoinPerson.setActionCommand("JOIN_PERSON");
		pJoinMenu.add(pJoinPerson);
		// pJoinPerson.setVisible(false);
		pJoinAddPerson = new JMenuItem(Resurses.getString("JOIN_ADD_PERSON"));
		pJoinAddPerson.setActionCommand("JOIN_ADD_PERSON");
		pJoinMenu.add(pJoinAddPerson);
		pJoinAddPerson.setVisible(false);
		pMenu.addSeparator();
		pCopy = new JMenuItem(Resurses.getString(Resurses.MENU_COPY));
		// pShowFamily.addActionListener(popupListener);
		// pCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		// Event.CTRL_MASK ));
		pCopy.setActionCommand(Resurses.MENU_COPY);
		pMenu.add(pCopy);

		pNeedle = new JMenuItem(Resurses.getString(Resurses.MENU_NEEDLE));
		// pShowFamily.addActionListener(popupListener);
		pNeedle.setActionCommand(Resurses.MENU_NEEDLE);

		pMenu.add(pNeedle);
		pMenu.addSeparator();
		pHiskiPerson = new JMenuItem[30];
		final JMenu pHiskiConnect = new JMenu(Resurses.getString("HISKI_CONNECT"));
		pMenu.add(pHiskiConnect);
		for (int i = 0; i < 30; i++) {
			pHiskiPerson[i] = new JMenuItem(Resurses.getString("HISKI_PERSON") + " " + i);
			pHiskiPerson[i].setActionCommand("HISKI" + i);
			pHiskiConnect.add(pHiskiPerson[i]);
			pHiskiPerson[i].setVisible(false);
		}

		pReport = new JMenuItem(Resurses.getString(Resurses.CREATE_REPORT));
		// pReport.addActionListener(popupListener);
		pReport.setActionCommand(Resurses.CREATE_REPORT);
		pMenu.add(pReport);

	}

	/**
	 * This class is a semi-singleton.
	 *
	 * @return the menu
	 */
	public static SukuPopupMenu getInstance() {
		if (me == null) {
			me = new SukuPopupMenu();
		}
		return me;
	}

}
