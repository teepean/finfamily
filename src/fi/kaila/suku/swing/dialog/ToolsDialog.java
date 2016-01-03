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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesModel;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * This dialog is now defining notice order possible other tasks will be done
 * here later.
 *
 * @author Kalle
 */
public class ToolsDialog extends JDialog implements ActionListener, PropertyChangeListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ToolsDialog.class.getName());
	private static final String SORT = "NOTICES.SORT";
	private static final String CANCEL = "CANCEL";

	private final JLabel textContent;
	private final JButton ok;

	private JList koko;

	private JList setti;

	private final JProgressBar progressBar;
	private Task task;

	/**
	 * Gets the runner.
	 *
	 * @return handle to this instance
	 */
	public static ToolsDialog getRunner() {
		return runner;
	}

	/** The koko map. */
	HashMap<String, String> kokoMap = new HashMap<String, String>();

	/** The setti map. */
	HashMap<String, String> settiMap = new HashMap<String, String>();

	/** The koko tags. */
	Vector<String> kokoTags = new Vector<String>();

	/** The koko lista. */
	Vector<String> kokoLista = new Vector<String>();

	/** The setti tags. */
	Vector<String> settiTags = new Vector<String>();

	/** The setti lista. */
	Vector<String> settiLista = new Vector<String>();

	/** The has lista changed. */
	boolean hasListaChanged = false;

	private Suku owner = null;
	private static ToolsDialog runner = null;

	/**
	 * Instantiates a new tools dialog.
	 *
	 * @param owner
	 *            the owner
	 */
	public ToolsDialog(Suku owner) {
		super(owner, Resurses.getString("MENU_NOTICES_ORDER"), true);
		this.owner = owner;
		runner = this;

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds((d.width / 2) - 170, (d.height / 2) - 250, 340, 500);
		setResizable(false);
		setLayout(null);

		int y = 30;
		final JPanel nsort = new JPanel();
		String[] notorder = null;
		try {
			final SukuTypesModel types = Utils.typeInstance();

			final SukuData resp = Suku.kontroller.getSukuData("cmd=getsettings", "type=order", "name=notice");
			notorder = resp.generalArray;

			for (int i = 0; i < types.getTypesTagsCount(); i++) {
				final String tag = types.getTypesTag(i);
				final String value = types.getTypesName(i);
				kokoMap.put(tag, value);
			}

			settiTags.add("NAME");
			settiLista.add(kokoMap.get("NAME"));
			settiMap.put("NAME", kokoMap.get("NAME"));

			for (final String element : notorder) {
				final String tag = element;
				if (!tag.equals("NAME")) {
					settiTags.add(tag);
					final String value = kokoMap.get(tag);
					settiLista.add(value);
					settiMap.put(tag, value);
				}

			}

			for (int i = 0; i < types.getTypesTagsCount(); i++) {
				final String tag = types.getTypesTag(i);
				String value = settiMap.get(tag);
				if (value == null) {
					kokoTags.add(tag);
					value = types.getTypesName(i);
					kokoLista.add(value);
				}
			}

			nsort.setLayout(null);
			nsort.setBounds(10, 0, 300, 450);

			nsort.setBorder(BorderFactory.createTitledBorder(Resurses.getString("DIALOG_SORT_NOTICES")));
			getContentPane().add(nsort);

			JLabel lbl = new JLabel(Resurses.getString("DIALOG_NONSORT"));
			nsort.add(lbl);
			lbl.setBounds(10, y, 120, 20);

			lbl = new JLabel(Resurses.getString("DIALOG_YESSORT"));
			nsort.add(lbl);
			lbl.setBounds(150, y, 120, 20);

			y += 20;
			koko = new JList(kokoLista);
			koko.addMouseListener(this);
			final JScrollPane kokoScroll = new JScrollPane(koko);
			nsort.add(kokoScroll);
			kokoScroll.setBounds(10, y, 120, 200);

			setti = new JList(settiLista);
			setti.addMouseListener(this);
			final JScrollPane settiScroll = new JScrollPane(setti);
			nsort.add(settiScroll);
			settiScroll.setBounds(150, y, 120, 200);
			y += 206;
			lbl = new JLabel(Resurses.getString("DIALOG_CLICKINFO"));
			nsort.add(lbl);
			lbl.setBounds(10, y, 300, 20);
			y += 18;
			lbl = new JLabel(Resurses.getString("DIALOG_STATICNAME"));
			nsort.add(lbl);
			lbl.setBounds(10, y, 300, 20);
			y += 18;

		} catch (final SukuException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);

		}

		y += 30;
		textContent = new JLabel(Resurses.getString("DIALOG_SORTINFO"));
		nsort.add(textContent);
		this.textContent.setBounds(10, y, 300, 40);
		y += 30;

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(10, y, 260, 20);
		nsort.add(this.progressBar);

		y += 40;
		this.ok = new JButton(Resurses.getString(SORT));
		nsort.add(this.ok);
		this.ok.setBounds(10, y, 100, 24);
		this.ok.setActionCommand(SORT);
		this.ok.addActionListener(this);
		getRootPane().setDefaultButton(this.ok);

		final JButton cancel = new JButton(Resurses.getString(CANCEL));
		nsort.add(cancel);
		cancel.setBounds(140, y, 100, 24);
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(this);

		this.task = null;

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
		if (cmd.equals(SORT)) {

			final SukuData request = new SukuData();
			request.generalArray = settiTags.toArray(new String[0]);

			try {
				Suku.kontroller.getSukuData(request, "cmd=updatesettings", "type=order", "name=notice");

			} catch (final SukuException ee) {
				JOptionPane.showMessageDialog(this, ee.getMessage(), Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
				ee.printStackTrace();
			}

			this.ok.setEnabled(false);
			// we create new instances as needed.
			task = new Task();
			task.wn = settiTags;
			task.addPropertyChangeListener(this);
			task.execute();

		} else if (cmd.equals(CANCEL)) {
			if (this.task == null) {

				setVisible(false);
			} else {
				this.task.cancel(true);
			}
		}

	}

	/**
	 * progressbar text is split with ; before ; is number 0-100 to show on
	 * progressbar. After ; is shown in text field if no ; exists then text is
	 * shown in textfiels
	 *
	 * @param juttu
	 *            the new runner value
	 */
	public void setRunnerValue(String juttu) {
		final String[] kaksi = juttu.split(";");
		if (kaksi.length >= 2) {
			final int progress = Integer.parseInt(kaksi[0]);

			progressBar.setValue(progress);
			textContent.setText(kaksi[1]);

		} else {
			textContent.setText(juttu);

			progressBar.setIndeterminate(true);
			progressBar.setValue(0);
		}
	}

	/**
	 * The Class Task.
	 */
	class Task extends SwingWorker<Void, Void> {

		/** The wn. */
		Vector<String> wn = null;

		/*
		 * Main task. Executed in background thread.
		 */
		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		public Void doInBackground() {

			// Initialize progress property.
			setProgress(0);
			setRunnerValue("0;" + Resurses.getString(""));

			final int dbcount = owner.getDatabaseRowCount();
			int updateCount = 0;
			int prosent;
			for (int dbi = 0; dbi < dbcount; dbi++) {

				prosent = (dbi * 100) / dbcount;
				final PersonShortData sho = owner.getDatbasePerson(dbi);
				final String tmp = "" + prosent + "; " + sho.getAlfaName();
				// System.out.println(tmp);
				setRunnerValue(tmp);

				SukuData plong;
				try {
					plong = Suku.kontroller.getSukuData("cmd=person", "pid=" + sho.getPid());
				} catch (final SukuException e) {

					e.printStackTrace();
					return null;
				}
				final PersonLongData persLong = plong.persLong;

				final ArrayList<UnitNotice> un = new ArrayList<UnitNotice>();
				for (int j = 0; j < persLong.getNotices().length; j++) {
					un.add(persLong.getNotices()[j]);
				}

				int lastCheckedIndex = -1;

				boolean hasSorted = false;
				for (int tagIdx = 0; tagIdx < wn.size(); tagIdx++) {
					final String tag = wn.get(tagIdx);

					for (int k = 0; k < un.size(); k++) {
						final UnitNotice uun = un.get(k);
						if (uun.getTag().equals(tag)) {
							if (k > lastCheckedIndex) {
								lastCheckedIndex++;
								if (k > lastCheckedIndex) {
									final UnitNotice t = un.remove(k);
									un.add(lastCheckedIndex, t);
									hasSorted = true;
								}
							} else {
								lastCheckedIndex = k;
							}
						}
					}
				}

				if (hasSorted) {
					updateCount++;
					logger.fine("sorted notices for [" + sho.getPid() + "]: " + sho.getAlfaName());
					plong.persLong.setNotices(un.toArray(new UnitNotice[0]));

					try {
						Suku.kontroller.getSukuData(plong, "cmd=update", "type=person");
						logger.fine("person updated pid[" + persLong.getPid() + "]");
						// System.out.println("päivitys : " + resp.resu);
					} catch (final SukuException e) {
						logger.log(Level.WARNING, "person update failed", e);
						JOptionPane.showMessageDialog(null, e.getMessage(), Resurses.getString(Resurses.SUKU),
								JOptionPane.ERROR_MESSAGE);

					}
				}

			}

			logger.info("Sorted notices for [" + updateCount + "] persons from [" + dbcount + "] in set");

			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.SwingWorker#done()
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setVisible(false);

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			final String juttu = evt.getNewValue().toString();
			final String[] kaksi = juttu.split(";");
			if (kaksi.length >= 2) {
				final int progress = Integer.parseInt(kaksi[0]);
				progressBar.setIndeterminate(false);
				progressBar.setValue(progress);
				textContent.setText(kaksi[1]);
			} else {

				textContent.setText(juttu);
				int progre = progressBar.getValue();
				if (progre > 95) {
					progre = 0;

				} else {
					progre++;
				}
				progressBar.setIndeterminate(true);
				progressBar.setValue(progre);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if ((e.getClickCount() == 2) && (e.getButton() == 1)) {

			if (e.getSource() == koko) {
				final int rivi = koko.getSelectedIndex();
				settiTags.add(kokoTags.get(rivi));
				settiLista.add(kokoLista.get(rivi));
				kokoTags.remove(rivi);
				kokoLista.remove(rivi);
				koko.updateUI();
				setti.updateUI();
				hasListaChanged = true;

			} else if (e.getSource() == setti) {
				final int rivi = setti.getSelectedIndex();
				if (rivi > 0) {
					kokoTags.add(settiTags.get(rivi));
					kokoLista.add(settiLista.get(rivi));
					settiTags.remove(rivi);
					settiLista.remove(rivi);
					koko.updateUI();
					setti.updateUI();
					hasListaChanged = true;
				}

			} else {
				System.out.println("ME:" + e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Doesn't do anything just now
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// Doesn't do anything just now
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// Doesn't do anything just now
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Doesn't do anything just now
	}

}
