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
import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;

/**
 * simple notepad dialog.
 *
 * @author Kalle
 */
public class SukuPad extends JDialog implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea txtArea = null;

	/**
	 * Instantiates a new suku pad.
	 *
	 * @param owner
	 *            frame
	 * @param text
	 *            the text
	 */
	public SukuPad(JFrame owner, String text) {
		super(owner, Resurses.getString("SUKUOHJELMISTO"));
		initMe(text);
	}

	/**
	 * Constructor from JDialog.
	 *
	 * @param owner
	 *            dialog
	 * @param text
	 *            the text
	 */
	public SukuPad(JDialog owner, String text) {
		super(owner, Resurses.getString("SUKUOHJELMISTO"));
		initMe(text);
	}

	/**
	 * @param text
	 */
	private void initMe(String text) {
		final JMenuBar menubar = new JMenuBar();

		setJMenuBar(menubar);
		final JMenu mFile = new JMenu(Resurses.getString(Resurses.FILE));
		menubar.add(mFile);

		final JMenuItem save = new JMenuItem(Resurses.getString("FILE_SAVE_AS"));
		save.addActionListener(this);
		save.setActionCommand("SAVE_AS");
		mFile.add(save);
		final JMenuItem print = new JMenuItem(Resurses.getString("FILE_PRINT"));
		print.addActionListener(this);
		print.setActionCommand("PRINT");
		mFile.add(print);
		final JMenuItem cl = new JMenuItem(Resurses.getString("CLOSE"));
		cl.addActionListener(this);
		cl.setActionCommand("CLOSE");
		mFile.add(cl);

		txtArea = new JTextArea(text);
		final JScrollPane sc = new JScrollPane(txtArea);
		txtArea.setLineWrap(true);
		txtArea.setWrapStyleWord(true);
		getContentPane().add(sc);
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((d.width / 2) - 300, (d.height / 2) - 200, 600, 400);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent a) {
		final String cmd = a.getActionCommand();

		if (cmd == null) {
			return;
		}
		if (cmd.equals("CLOSE")) {
			setVisible(false);
		} else if (cmd.equals("PRINT")) {
			try {
				txtArea.print();
			} catch (final PrinterException e) {
				JOptionPane.showMessageDialog(this, Resurses.getString("IMPORT_GEDCOM") + ":" + e.getMessage());
			}
		} else if (cmd.equals("SAVE_AS")) {
			final boolean isFile = Suku.kontroller.createLocalFile("txt");
			if (isFile) {
				try {
					final OutputStream fos = Suku.kontroller.getOutputStream();

					String tekst;
					if (java.io.File.pathSeparatorChar == ';') {
						tekst = txtArea.getText().replaceAll("\n", "\r\n");
					} else {
						tekst = txtArea.getText();
					}
					final byte[] buffi = tekst.getBytes();

					fos.write(buffi);
					fos.close();
				} catch (final IOException e) {
					JOptionPane.showMessageDialog(this, Resurses.getString("IMPORT_GEDCOM") + ":" + e.getMessage());
				}

			}

		}
	}

}
