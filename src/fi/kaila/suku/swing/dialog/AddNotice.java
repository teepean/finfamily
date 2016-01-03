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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesModel;
import fi.kaila.suku.util.Utils;

/**
 * Dialog to request for notice to be added.
 *
 * @author Kalle
 */
public class AddNotice extends JDialog implements MouseListener {

	private static final long serialVersionUID = 1L;

	/** The koko map. */
	HashMap<String, String> kokoMap = new HashMap<String, String>();

	private final JList koko;

	/** The koko tags. */
	Vector<String> kokoTags = new Vector<String>();

	/** The koko lista. */
	Vector<String> kokoLista = new Vector<String>();

	private String selectedTag = null;

	/**
	 * Constructor.
	 *
	 * @param owner
	 *            the owner
	 * @throws SukuException
	 *             the suku exception
	 */
	public AddNotice(Suku owner) throws SukuException {
		super(owner, Resurses.getString("DIALOG_ADD_NOTICE"), true);

		// Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		//
		// setBounds(d.width/2-300,d.height/2-200,300,400);
		setLayout(null);
		final SukuTypesModel types = Utils.typeInstance();

		for (int i = 0; i < types.getTypesTagsCount(); i++) {
			final String tag = types.getTypesTags(i);
			final String value = types.getTypesName(i);
			kokoMap.put(tag, value);
		}

		for (int i = 0; i < types.getTypesTagsCount(); i++) {
			final String tag = types.getTypesTags(i);
			kokoTags.add(tag);

			final String value = types.getTypesName(i);
			kokoLista.add(value);

		}

		koko = new JList(kokoLista);
		koko.addMouseListener(this);
		final JScrollPane kokoScroll = new JScrollPane(koko);
		getContentPane().add(kokoScroll);
		kokoScroll.setBounds(10, 10, 120, 340);

	}

	/**
	 * Gets the selected tag.
	 *
	 * @return the tag selected to be added
	 */
	public String getSelectedTag() {
		return selectedTag;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		final int idx = koko.getSelectedIndex();
		selectedTag = kokoTags.get(idx);
		setVisible(false);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Not used
	}

}
