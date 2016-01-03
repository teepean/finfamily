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

package fi.kaila.suku.util.local;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fi.kaila.suku.util.Resurses;

/**
 * Dialog for user management as admin.
 *
 * @author Kaarle Kaila
 */
public class LocalUserAdmin extends JDialog implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String OK = "OK";
	private static final String CANCEL = "CANCEL";

	private JTextField userid = null;
	private JTextField password = null;
	private JTextField verifyPassword = null;

	/**
	 * Constructor for dialog.
	 *
	 * @param owner
	 *            the owner
	 */
	@SuppressWarnings("unqualified-field-access")
	public LocalUserAdmin(JFrame owner) {
		super(owner, Resurses.getString("ADMIN"), true);
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		JLabel lbl;

		setLayout(null);
		int y = 20;

		lbl = new JLabel(Resurses.getString("LOGIN_USERID"));
		getContentPane().add(lbl);
		lbl.setBounds(20, y, 100, 20);

		userid = new JTextField();
		getContentPane().add(userid);
		userid.setBounds(120, y, 200, 20);

		y += 30;

		lbl = new JLabel(Resurses.getString("LOGIN_PASSWORD"));
		getContentPane().add(lbl);
		lbl.setBounds(20, y, 100, 20);

		password = new JPasswordField();
		getContentPane().add(password);
		password.setBounds(120, y, 200, 20);

		y += 30;

		lbl = new JLabel(Resurses.getString(Resurses.VERIFYPWD));
		getContentPane().add(lbl);
		lbl.setBounds(20, y, 100, 20);

		verifyPassword = new JPasswordField();
		getContentPane().add(verifyPassword);
		verifyPassword.setBounds(120, y, 200, 20);

		y += 40;
		final JButton ok = new JButton(Resurses.getString(OK));
		getContentPane().add(ok);
		ok.setBounds(110, y, 100, 24);
		ok.setActionCommand(OK);
		ok.addActionListener(this);
		ok.setDefaultCapable(true);

		getRootPane().setDefaultButton(ok);

		final JButton cancel = new JButton(Resurses.getString(CANCEL));
		getContentPane().add(cancel);
		cancel.setBounds(230, y, 100, 24);
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(this);

		setBounds((d.width / 2) - 200, (d.height / 2) - 100, 400, y + 70);
	}

	//
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	@SuppressWarnings("unqualified-field-access")
	public void actionPerformed(ActionEvent e) {
		final String cmd = e.getActionCommand();

		if (cmd.equals(CANCEL)) {
			setVisible(false);
			return;
		}
		if (cmd.equals(OK)) {

			if (password.getText().equals(verifyPassword.getText()) && (password.getText().length() > 2)) {
				setVisible(false);
			} else {
				JOptionPane.showMessageDialog(this, Resurses.getString(Resurses.PASSWORDNOTVERIFY));
			}

		}
	}

	/** The is old user. */
	boolean isOldUser = false;

	/**
	 * Sets the userid.
	 *
	 * @param userid
	 *            the new userid
	 */
	public void setUserid(String userid) {
		isOldUser = true;

		this.userid.setText(userid);
		this.userid.setEditable(false);

	}

	/**
	 * Gets the userid.
	 *
	 * @return userid
	 */
	public String getUserid() {
		if (isOldUser) {
			return null;
		}
		final String usr = this.userid.getText();
		if ((usr != null) && (usr.length() > 2)) {
			return usr;
		}
		return null;
	}

	/**
	 * Gets the password.
	 *
	 * @return database password
	 */
	public String getPassword() {
		final String pwd1 = this.password.getText();
		final String pwd2 = this.verifyPassword.getText();

		if ((pwd1 != null) && (pwd2 != null) && pwd1.equals(pwd2) && (pwd1.length() > 2)) {

			return pwd1;
		}
		return null;
	}

}
