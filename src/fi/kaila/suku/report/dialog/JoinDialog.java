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

package fi.kaila.suku.report.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.PersonShortData;

/**
 * The Class JoinDialog.
 */
public class JoinDialog extends JDialog implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final PersonShortData main;
	private final PersonShortData sub;
	private final Suku parent;

	/**
	 * Instantiates a new join dialog.
	 *
	 * @param owner
	 *            the owner
	 * @param main
	 *            the main
	 * @param sub
	 *            the sub
	 */
	public JoinDialog(Suku owner, PersonShortData main, PersonShortData sub) {
		super(owner, Resurses.getString("JOIN_MENU"), true);

		this.parent = owner;

		this.main = main;
		this.sub = sub;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Turn off metal's use of bold fonts
				// UIManager.put("swing.boldMetal", Boolean.FALSE);
				initMe();
			}

		});
	}

	private void initMe() {

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension sz = new Dimension(d.width - 200, d.height - 150);
		sz = new Dimension(1000, 600);
		final int footery = sz.height - 125;
		setBounds((d.width - sz.width) / 2, (d.height - sz.height) / 2, sz.width, sz.height);
		setLayout(null);

		final int x1 = 20;
		final int x2 = (sz.width / 2) + 20;
		final int y1 = 20;
		final int y2 = 20;

		JLabel lbl = new JLabel(main.getName(true, true));
		lbl.setBounds(x1, y1, 200, 20);
		add(lbl);

		lbl = new JLabel(sub.getName(true, true));
		lbl.setBounds(x2, y2, 200, 20);
		add(lbl);

		final JButton dome = new JButton(Resurses.getString("JOIN_US"));
		dome.setBounds(x2, footery, 100, 22);
		dome.addActionListener(this);
		dome.setActionCommand("JOIN_US");
		add(dome);

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
		if (cmd == null) {
			return;
		}
		if (cmd.equals("JOIN_US")) {
			setVisible(false);
		}

	}

}
