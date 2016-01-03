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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateException;
import fi.kaila.suku.util.Utils;

/**
 * DateField is specific to genealogy app as dates need not be exact.
 *
 * @author Kalle
 */
public class SukuDateField extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	/** The date pref. */
	JComboBox datePref;

	/** The date from. */
	JTextField dateFrom;

	/** The date to. */
	JTextField dateTo;

	/** The date post. */
	JLabel datePost;

	/**
	 * Instantiates a new suku date field.
	 */
	public SukuDateField() {

		setLayout(null);
		final String dateprex[] = Resurses.getString("DATE_PREFS").split(";");
		datePref = new JComboBox(dateprex);
		add(datePref);
		datePref.addActionListener(this);
		dateFrom = new JTextField();
		add(dateFrom);
		datePost = new JLabel("TO");
		add(datePost);
		dateTo = new JTextField();
		add(dateTo);

		datePref.setBounds(0, 0, 78, 20);
		dateFrom.setBounds(80, 0, 80, 20);
		datePost.setBounds(162, 0, 20, 20);
		dateTo.setBounds(183, 0, 80, 20);

	}

	/**
	 * Gets the date pref tag.
	 *
	 * @return the prefix tag for date (See GEDCOM)
	 */
	public String getDatePrefTag() {

		final int idx = datePref.getSelectedIndex();
		switch (idx) {
		case 1:
			return "ABT";
		case 2:
			return "CAL";
		case 3:
			return "EST";
		case 4:
			return "BET";
		case 5:
			return "FROM";
		case 6:
			return "BEF";
		case 7:
			return "AFT";
		default:
			return null;
		}
	}

	/**
	 * Gets the from date.
	 *
	 * @return date in text format
	 * @throws SukuDateException
	 *             the suku date exception
	 */
	public String getFromDate() throws SukuDateException {
		return Utils.dbDate(dateFrom.getText());
	}

	/**
	 * Gets the date pref text.
	 *
	 * @return the date pref text
	 */
	public String getDatePrefText() {
		final int idx = datePref.getSelectedIndex();
		if (idx <= 0) {
			return null;
		}
		return (String) datePref.getSelectedItem();
	}

	/**
	 * Checks if is plain.
	 *
	 * @return true if no prefix exists for date
	 */
	public boolean isPlain() {
		if (datePref.getSelectedIndex() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the text from date.
	 *
	 * @return text from dfatefield
	 */
	public String getTextFromDate() {
		return dateFrom.getText();
	}

	/**
	 * sets datefield (text).
	 *
	 * @param text
	 *            the new text from date
	 */
	public void setTextFromDate(String text) {
		dateFrom.setText(text);
	}

	/**
	 * Gets the to date.
	 *
	 * @return text format of date
	 * @throws SukuDateException
	 *             the suku date exception
	 */
	public String getToDate() throws SukuDateException {
		return Utils.dbDate(dateTo.getText());
	}

	/**
	 * sets full date.
	 *
	 * @param pre
	 *            the pre
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	public void setDate(String pre, String from, String to) {
		int preIdx = 0;

		if (from == null) {
			datePost.setText("");
			// dateTo.setVisible(false);
			return;
		}
		if (pre != null) {
			if (pre.equals("ABT")) {
				preIdx = 1;
			}
			if (pre.equals("CAL")) {
				preIdx = 2;
			}
			if (pre.equals("EST")) {
				preIdx = 3;
			}
			if (pre.equals("BET")) {
				preIdx = 4;
			}
			if (pre.equals("FROM")) {
				preIdx = 5;
			}
			if (pre.equals("BEF")) {
				preIdx = 6;
			}
			if (pre.equals("AFT")) {
				preIdx = 7;
			}

		}
		datePref.setSelectedIndex(preIdx);
		dateFrom.setText(Utils.textDate(from, true));
		datePost.setText("");
		if ("FROM".equals(pre) || "BET".equals(pre)) {
			dateTo.setVisible(true);

			if ("FROM".equals(pre)) {
				datePost.setText(Resurses.getString("DATE_TO"));
			} else {
				datePost.setText(Resurses.getString("DATE_AND"));
			}
			if (to != null) {
				dateTo.setText(Utils.textDate(to, true));
			}
		} else {
			dateTo.setVisible(false);
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
		if ((e != null) && (e.getSource() == datePref)) {

			final int idx = datePref.getSelectedIndex();
			if (idx == 4) {
				datePost.setText(Resurses.getString("DATE_TO"));
				dateTo.setVisible(true);
			} else if (idx == 5) {
				datePost.setText(Resurses.getString("DATE_AND"));
				dateTo.setVisible(true);
			} else {
				datePost.setText("");
				dateTo.setText("");
				dateTo.setVisible(false);
			}

		}
	}

}
