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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.MouseInputListener;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Select existing or create new schema.
 *
 * @author kalle
 */
public class SelectSchema extends JDialog implements ActionListener, MouseInputListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// private JFrame owner = null;

	private JTextField schema = null;
	private boolean okSelected = false;
	private JList scList = null;
	private String postDb = "Koedatabase";
	private String[] schemaList = null;

	/**
	 * Constructor that always shows the schema textfield.
	 *
	 * @param owner
	 *            the owner
	 * @param db
	 *            the db
	 * @throws SukuException
	 *             the suku exception
	 */
	public SelectSchema(JFrame owner, String db) throws SukuException {
		super(owner, Resurses.getString("SCHEMA"), true);
		// this.owner = owner;
		this.postDb = db;
		constructMe(true);

	}

	/**
	 * Constructor.
	 *
	 * @param owner
	 *            the owner
	 * @param db
	 *            the db
	 * @param allowNew
	 *            if false then only list is shown
	 * @throws SukuException
	 *             the suku exception
	 */
	public SelectSchema(JFrame owner, String db, boolean allowNew) throws SukuException {
		super(owner, Resurses.getString("SCHEMA"), true);
		// this.owner = owner;
		this.postDb = db;
		constructMe(allowNew);

	}

	private void constructMe(boolean allowNew) throws SukuException {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds((d.width / 2) - 150, (d.height / 2) - 170, 300, 340);
		setLayout(null);
		int y = 10;
		if (postDb != null) {
			setTitle(postDb);
		}
		String labelValue = "SCHEMA_SELECT";
		if (allowNew) {
			labelValue = "SCHEMA_SELECTNEW";
		}

		final JLabel lbl = new JLabel(Resurses.getString(labelValue));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 200, 20);
		y += 20;
		schema = new JTextField();
		getContentPane().add(schema);
		schema.setBounds(10, y, 200, 20);
		schema.setVisible(allowNew);
		final SukuData schemas = Suku.kontroller.getSukuData("cmd=schema", "type=count");
		schemaList = schemas.generalArray;
		if (!allowNew) {
			if (schemaList.length == 1) {
				okSelected = true;
				schema.setText(schemaList[0]);
			}
		}
		scList = new JList(schemaList);

		scList.addMouseListener(this);

		final JScrollPane scroll = new JScrollPane(scList);
		getContentPane().add(scroll);
		scroll.setBounds(10, y + 20, 240, 200);

		y += 240;
		final JButton ok = new JButton(Resurses.getString("OK"));
		getContentPane().add(ok);
		ok.setBounds(30, y, 80, 24);
		ok.setActionCommand("OK");
		ok.addActionListener(this);
		ok.setDefaultCapable(true);

		getRootPane().setDefaultButton(ok);

		final JButton cancel = new JButton(Resurses.getString("CANCEL"));
		getContentPane().add(cancel);
		cancel.setBounds(120, y, 80, 24);
		cancel.setActionCommand("CANCEL");
		cancel.addActionListener(this);
		getRootPane().setDefaultButton(ok);
	}

	/**
	 * Gets the schema.
	 *
	 * @param isH2
	 *            the is h2
	 * @return schema selected
	 */
	public String getSchema(boolean isH2) {
		if (isH2) {
			return "finfamily";
		} else {
			if (okSelected) {
				return Utils.toUsAscii(schema.getText());
			}
			return null;
		}
	}

	/**
	 * Checks if is existing schema.
	 *
	 * @param isH2
	 *            the is h2
	 * @return true if selected schema already existed
	 */
	public boolean isExistingSchema(boolean isH2) {

		final String aux = getSchema(isH2);
		if (aux != null) {

			for (final String element : schemaList) {
				if (aux.equalsIgnoreCase(element)) {
					return true;
				}
			}
		}
		return false;
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
		if (cmd.equals("OK")) {
			okSelected = true;

		}
		setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		final int clickCount = e.getClickCount();
		final int idx = scList.getSelectedIndex();
		if (idx >= 0) {
			schema.setText(schemaList[idx]);
			// if (schema.isVisible()) {
			// scList.setSelectedIndices(new int[0]);
			// }
			if (clickCount > 1) {
				okSelected = true;
				setVisible(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(java.awt.event.MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(java.awt.event.MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(java.awt.event.MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(java.awt.event.MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {
		// Not used
	}

}
