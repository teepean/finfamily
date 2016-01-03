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
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import fi.kaila.suku.ant.AntVersion;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class AboutDialog.
 *
 * @author FIKAAKAIL
 *
 *         About box for Suku
 */
public class AboutDialog extends JDialog implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final String OK = "OK";
	private static Logger logger = null;

	/**
	 * Instantiates a new about dialog.
	 *
	 * @param owner
	 *            the owner
	 */
	public AboutDialog(JFrame owner) {
		super(owner, Resurses.getString("ABOUT"), true);
		logger = Logger.getLogger(this.getClass().getName());
		setLayout(null);

		final JTextArea aboutArea = new JTextArea();
		aboutArea.setEditable(false);
		aboutArea.setLineWrap(true);

		add(aboutArea);
		aboutArea.setBounds(20, 20, 560, 200);

		final StringBuilder about = new StringBuilder();

		about.append(Resurses.getString(Resurses.ABOUT_SUKU_VERSION));
		about.append(": ");
		about.append(AntVersion.antVersion);
		about.append("\n");
		about.append("\n");
		about.append("Java Version: ");
		about.append(System.getProperty("java.version"));
		about.append(" from ");
		about.append(System.getProperty("java.vendor"));
		about.append("\n");
		about.append("OS: ");
		about.append(System.getProperty("os.name"));
		about.append("\n");

		if (Suku.serverVersion != null) {
			about.append(Resurses.getString(Resurses.ABOUT_SERVER_VERSION));
			about.append(": ");
			about.append(Suku.serverVersion);
			about.append("\n");
		}

		if (Suku.postServerVersion != null) {

			about.append(Resurses.getString(Resurses.ABOUT_DB_VERSION));
			about.append(": ");
			about.append(Suku.postServerVersion);
			about.append("\n");
		}

		SukuData stats = null;
		try {
			Utils.println(this, "fetching dbstats");
			stats = Suku.kontroller.getSukuData("cmd=dbstats");

		} catch (final Exception e) {
			Utils.println(this, "dbstats ex = " + e.toString());
			stats = new SukuData();
			if (e.getMessage() != null) {
				stats.generalText = e.getMessage();
			} else {
				stats.generalText = "no connection";
			}
		}
		about.append("\n");
		about.append(Resurses.getString("DBSTATS"));
		about.append(": ");
		about.append(stats.generalText);
		logger.info(about.toString());
		aboutArea.setText(about.toString());

		final JButton ok = new JButton(OK);
		getContentPane().add(ok);
		ok.setBounds(480, 235, 100, 24);
		ok.addActionListener(this);
		ok.setDefaultCapable(true);

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((d.width / 2) - 200, (d.height / 2) - 200, 600, 300);
		setResizable(false);

		getRootPane().setDefaultButton(ok);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.setVisible(false);

	}

}
