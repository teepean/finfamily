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

import javax.swing.JComboBox;
import javax.swing.JPanel;

import fi.kaila.suku.util.Resurses;

/**
 * Surety field component.
 *
 * @author Kalle
 */
public class SukuSuretyField extends JPanel {

	private static final long serialVersionUID = 1L;

	/** The surety. */
	JComboBox surety;

	/**
	 * constructor to setup from resources.
	 */
	public SukuSuretyField() {

		setLayout(null);

		final String[] suretys = Resurses.getString("DATA_SURETY_VALUES").split(";");

		surety = new JComboBox(suretys);

		add(surety);

		surety.setBounds(0, 0, 100, 20);

	}

	/**
	 * Gets the surety.
	 *
	 * @return surtety value [0,20,40,60,80,100]
	 */
	public int getSurety() {
		return (5 - surety.getSelectedIndex()) * 20;
	}

	/**
	 * Sets the surety.
	 *
	 * @param value
	 *            surety value [0,20,40,60,80,100]
	 */
	public void setSurety(int value) {
		final int idx = 5 - ((value + 10) / 20);
		surety.setSelectedIndex(idx);

	}
}
