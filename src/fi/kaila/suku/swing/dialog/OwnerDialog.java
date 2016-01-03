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

package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * This dialog is for setting program owner information.
 *
 * @author halonmi
 */
public class OwnerDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final String CANCEL = "CANCEL";

	private final JTextField name;
	private final JTextField address;
	private final JTextField postalCode;
	private final JTextField postOffice;
	private final JTextField state;
	private final JTextField country;
	private final JTextField email;
	private final JTextField web;
	private final JTextArea text;

	/**
	 * Instantiates a new tools dialog.
	 *
	 * @param owner
	 *            the owner
	 */
	public OwnerDialog(Suku owner) {
		super(owner, Resurses.getString("DIALOG_OWNER"), true);

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds((d.width / 2) - 210, (d.height / 2) - 230, 420, 460);
		setResizable(false);
		setLayout(null);

		int y = 30;
		SukuData resp = new SukuData();
		try {
			resp = Suku.kontroller.getSukuData("cmd=variables", "type=get");
		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(this, Resurses.getString("DIALOG_UPDATE_DB") + ":" + e.getMessage(),
					Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			resp.generalArray = new String[10];

		}

		y = 20;

		JLabel lbl = new JLabel(Resurses.getString("DATA_NAME"));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 80, 20);

		name = new JTextField(Utils.nv(resp.generalArray[0]));
		getContentPane().add(name);
		name.setBounds(90, y, 280, 20);
		y += 24;
		lbl = new JLabel(Resurses.getString("DATA_ADDRESS"));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 80, 20);

		address = new JTextField(Utils.nv(resp.generalArray[1]));
		getContentPane().add(address);
		address.setBounds(90, y, 280, 20);

		y += 24;
		lbl = new JLabel(Resurses.getString("DATA_POSTCODE"));
		getContentPane().add(lbl);
		lbl.setBounds(90, y, 80, 20);
		lbl = new JLabel(Resurses.getString("DATA_POSTOFF"));
		getContentPane().add(lbl);
		lbl.setBounds(190, y, 80, 20);
		y += 20;
		postalCode = new JTextField(Utils.nv(resp.generalArray[2]));
		getContentPane().add(postalCode);
		postalCode.setBounds(90, y, 85, 20);
		postOffice = new JTextField(Utils.nv(resp.generalArray[3]));
		getContentPane().add(postOffice);
		postOffice.setBounds(180, y, 190, 20);

		y += 24;

		lbl = new JLabel(Resurses.getString("DATA_STATE"));
		getContentPane().add(lbl);
		lbl.setBounds(90, y, 80, 20);
		lbl = new JLabel(Resurses.getString("DATA_COUNTRY"));
		getContentPane().add(lbl);
		lbl.setBounds(190, y, 80, 20);
		y += 20;
		state = new JTextField(Utils.nv(resp.generalArray[4]));
		getContentPane().add(state);
		state.setBounds(90, y, 85, 20);
		country = new JTextField(Utils.nv(resp.generalArray[5]));
		getContentPane().add(country);
		country.setBounds(180, y, 190, 20);

		y += 24;
		lbl = new JLabel(Resurses.getString("DATA_EMAIL"));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 80, 20);

		email = new JTextField(Utils.nv(resp.generalArray[6]));
		getContentPane().add(email);
		email.setBounds(90, y, 280, 20);

		y += 24;
		lbl = new JLabel(Resurses.getString("DIALOG_WWW"));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 80, 20);

		web = new JTextField(Utils.nv(resp.generalArray[7]));
		getContentPane().add(web);
		web.setBounds(90, y, 280, 20);
		y += 24;
		lbl = new JLabel(Resurses.getString("DATA_NOTE"));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 80, 20);
		text = new JTextArea(Utils.nv(resp.generalArray[8]));
		text.setLineWrap(true);
		final JScrollPane scrollPrivate = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		getContentPane().add(scrollPrivate);
		scrollPrivate.setBounds(90, y, 280, 150);
		y += 170;
		final JButton update = new JButton(Resurses.getString("DIALOG_UPDATE"));
		getContentPane().add(update);
		update.setBounds(100, y, 160, 24);
		update.setActionCommand("DIALOG_UPDATE");
		update.addActionListener(this);
		getRootPane().setDefaultButton(update);

		final JButton cancel = new JButton(Resurses.getString(CANCEL));
		getContentPane().add(cancel);
		cancel.setBounds(270, y, 100, 24);
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		final String cmd = e.getActionCommand();
		if (cmd.equals("DIALOG_UPDATE")) {

			try {

				final SukuData req = new SukuData();
				req.generalArray = new String[9];
				req.generalArray[0] = Utils.vn(name.getText());
				req.generalArray[1] = Utils.vn(address.getText());
				req.generalArray[2] = Utils.vn(postalCode.getText());
				req.generalArray[3] = Utils.vn(postOffice.getText());
				req.generalArray[4] = Utils.vn(state.getText());
				req.generalArray[5] = Utils.vn(country.getText());
				req.generalArray[6] = Utils.vn(email.getText());
				req.generalArray[7] = Utils.vn(web.getText());
				req.generalArray[8] = Utils.vn(text.getText());

				final SukuData resp = Suku.kontroller.getSukuData(req, "cmd=variables", "type=update");
				setVisible(false);
			} catch (final SukuException ee) {
				JOptionPane.showMessageDialog(this, ee.getMessage(), Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (cmd.equals(CANCEL)) {
			setVisible(false);
		}

	}

}
