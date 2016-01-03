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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JScrollPane;

import fi.kaila.suku.util.Resurses;

/**
 * A container for the contents of each tab on the right hand side.
 *
 * @author Kalle
 */
public class SukuTabPane extends JScrollPane {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The title. */
	String title;

	/** The icon. */
	Icon icon = null;

	/** The pnl. */
	Component pnl;

	/** The tip. */
	String tip;

	/**
	 * Instantiates a new suku tab pane.
	 *
	 * @param title
	 *            the title
	 * @param pnl
	 *            the pnl
	 */
	SukuTabPane(String title, Component pnl) {
		super(pnl);
		this.title = Resurses.getString(title);
		this.pnl = pnl;
		this.tip = Resurses.getString(title + "_TIP");

	}

	/**
	 * Instantiates a new suku tab pane.
	 *
	 * @param title
	 *            the title
	 * @param pnl
	 *            the pnl
	 * @param tip
	 *            the tip
	 */
	SukuTabPane(String title, Component pnl, String tip) {
		super(pnl);
		this.title = title;
		this.pnl = pnl;
		this.tip = tip;
	}

	/**
	 * Gets the pid.
	 *
	 * @return pid of pane
	 */
	public int getPid() {
		if (pnl instanceof PersonMainPane) {
			return ((PersonMainPane) pnl).getPersonPid();
		}
		return 0;
	}
}
