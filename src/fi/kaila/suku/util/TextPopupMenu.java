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

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * popupmenu for Suku Text area.
 *
 * @author Kaarle Kaila
 */
public class TextPopupMenu {

	private static TextPopupMenu me = null;

	private JPopupMenu pMenu = null;
	private final JMenuItem pOpenHiskiPage;

	private final JMenuItem pCopy;

	/**
	 * add all action listeners for menu commands
	 *
	 * @param l
	 */
	private void addActionListener(ActionListener l) {
		pOpenHiskiPage.addActionListener(l);
		pCopy.addActionListener(l);

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
	 */
	public void show(MouseEvent e, int x, int y) {
		pMenu.show(e.getComponent(), x, y);
	}

	/**
	 * Enable hiski menu.
	 *
	 * @param value
	 *            true to enable hiskimenu
	 */
	public void enableHiskiMenu(boolean value) {
		pOpenHiskiPage.setVisible(value);
	}

	/**
	 * Enable copy menu.
	 *
	 * @param value
	 *            set true to enable copy menu
	 */
	public void enableCopyMenu(boolean value) {
		pCopy.setEnabled(value);
	}

	private TextPopupMenu() {
		// private so it can only be initiated from within

		pMenu = new JPopupMenu();

		pOpenHiskiPage = new JMenuItem(Resurses.getString("HISKI_OPEN"));
		// pShowPerson.addActionListener(popupListener);
		pOpenHiskiPage.setActionCommand("HISKI_OPEN");
		pMenu.add(pOpenHiskiPage);

		pMenu.addSeparator();
		pCopy = new JMenuItem(Resurses.getString(Resurses.MENU_COPY));
		// pShowFamily.addActionListener(popupListener);
		// pCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		// Event.CTRL_MASK ));
		pCopy.setActionCommand(Resurses.MENU_COPY);
		pMenu.add(pCopy);

	}

	/**
	 * This is a singleton class.
	 *
	 * @param l
	 *            the l
	 * @return the menu
	 */
	public static TextPopupMenu getInstance(ActionListener l) {
		if (me == null) {
			me = new TextPopupMenu();
			me.addActionListener(l);
		}
		return me;

	}

}
