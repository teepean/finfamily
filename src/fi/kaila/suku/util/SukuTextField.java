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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/**
 * The Class SukuTextField.
 *
 * @author Kalle
 *
 *         This will become a textfield with some intellisens feature
 */
public class SukuTextField extends JTextField implements FocusListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Enum for the field types that will be recognized.
	 */
	public enum Field {

		/** Field type givenname. */
		Fld_Givenname,

		/** Field type Patronyme. */
		Fld_Patronyme,

		/** Field type Surname. */
		Fld_Surname,

		/** Field type place. */
		Fld_Place,

		/** Field type country. */
		Fld_Country,

		/** Field type = type. */
		Fld_Type,

		/** Field type description. */
		Fld_Description,

		/** Field group. */
		Fld_Group,

		/** No field. */
		Fld_Null
	}

	/** The senser. */
	SukuSenser senser = null;
	private String tag = null;
	private Field type = Field.Fld_Null;

	/**
	 * Instantiates a new suku text field.
	 *
	 * @param tag
	 *            the tag
	 * @param type
	 *            the type
	 */
	public SukuTextField(String tag, Field type) {
		addFocusListener(this);
		// addKeyListener(this);
		this.tag = tag;
		this.type = type;
		senser = SukuSenser.getInstance();

	}

	/** The has focus. */
	boolean hasFocus = false;

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.JComponent#processKeyEvent(java.awt.event.KeyEvent)
	 */
	@Override
	protected void processKeyEvent(KeyEvent e) {

		final int cmd = e.getKeyCode();
		if ((cmd == 40) || (cmd == 38) || (cmd == 10)) {

			if (e.getID() == KeyEvent.KEY_RELEASED) {
				senser.selectList(cmd);
			}
			if (cmd != 10) {
				return;
			}

		}
		if (cmd == 10) {
			if (senser.isVisible()) {
				senser.hide();
				return;
			}
		}
		super.processKeyEvent(e);
		if ((e.getID() == KeyEvent.KEY_RELEASED) && (cmd != 10)) {
			senser.showSens(this, tag, type);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent arg0) {
		hasFocus = true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent arg0) {
		hasFocus = false;
		senser.hide();
	}

}
