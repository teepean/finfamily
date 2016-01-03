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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * This tool orders the children in the database for all persons or all persons
 * in selected view
 *
 * If person has children without birth year that person is inserted into result
 * view.
 *
 * @author kalle
 */
public class OrderChildren extends JDialog implements ActionListener, PropertyChangeListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Suku owner = null;
	private static OrderChildren runner = null;

	private static final String SORT = "NOTICES.SORT";
	private static final String CANCEL = "CANCEL";

	private JCheckBox orderAll = null;
	private JComboBox viewList = null;
	private int[] viewIds = null;

	private final JButton ok;

	private final JLabel textContent;
	private final JProgressBar progressBar;
	private Task task;

	/**
	 * Instantiates a new order children.
	 *
	 * @param owner
	 *            the owner
	 * @throws SukuException
	 *             the suku exception
	 */
	public OrderChildren(Suku owner) throws SukuException {
		super(owner, Resurses.getString("MENU_CHILDREN_ORDER"), true);
		this.owner = owner;
		runner = this;
		int y = 0;
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds((d.width / 2) - 150, (d.height / 2) - 130, 300, 260);
		setResizable(false);
		setLayout(null);

		y += 50;

		JLabel lbl;

		orderAll = new JCheckBox(Resurses.getString("ORDER_ALL_CHILDREN"));
		add(orderAll);
		orderAll.setBounds(10, y, 260, 20);
		y += 24;
		// lbl = new JLabel();
		// add(lbl);
		// lbl.setBounds(10, y, 260, 20);
		// y += 20;

		lbl = new JLabel(Resurses.getString("STORE_NOT_SORTED"));
		add(lbl);
		lbl.setBounds(10, y, 260, 20);
		y += 20;
		final SukuData vlist = Suku.kontroller.getSukuData("cmd=viewlist");

		final String[] lista = vlist.generalArray;
		this.viewList = new JComboBox();
		add(this.viewList);
		viewList.setBounds(10, y, 260, 20);
		viewList.addItem("");
		viewIds = new int[lista.length + 1];
		viewIds[0] = 0;
		for (int i = 0; i < lista.length; i++) {
			final String[] pp = lista[i].split(";");
			if (pp.length > 1) {
				int vid = 0;
				try {
					vid = Integer.parseInt(pp[0]);
					viewIds[i + 1] = vid;
				} catch (final NumberFormatException ne) {
					viewIds[i + 1] = 0;
				}
				viewList.addItem(pp[1]);
			}
		}

		y += 30;
		textContent = new JLabel(Resurses.getString("DIALOG_SORTINFO"));
		add(textContent);
		this.textContent.setBounds(10, y, 300, 40);
		y += 30;

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(10, y, 260, 20);
		add(this.progressBar);

		y += 40;
		this.ok = new JButton(Resurses.getString(SORT));
		add(this.ok);
		this.ok.setBounds(10, y, 100, 24);
		this.ok.setActionCommand(SORT);
		this.ok.addActionListener(this);
		getRootPane().setDefaultButton(this.ok);

		final JButton cancel = new JButton(Resurses.getString(CANCEL));
		add(cancel);
		cancel.setBounds(140, y, 100, 24);
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(this);

		this.task = null;

	}

	/**
	 * Gets the runner.
	 *
	 * @return the runner
	 */
	public static OrderChildren getRunner() {
		return runner;
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
		if (cmd.equals(CANCEL)) {
			if (task == null) {
				setVisible(false);
				return;
			} else {
				task.stopMeNow = true;
			}
		}

		if (cmd.equals(SORT)) {

			final int ii = viewList.getSelectedIndex();
			if (ii <= 0) {
				JOptionPane.showMessageDialog(owner, Resurses.getString("STORE_VIEW_MISSING"),
						Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
				return;

			}

			task = new Task();
			task.orderAll = orderAll.isSelected();
			task.viewId = viewIds[ii];
			task.addPropertyChangeListener(this);
			task.execute();

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
			progressBar.setIndeterminate(false);
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

		/** The view id. */
		int viewId = 0;

		/** The stop me now. */
		boolean stopMeNow = false;

		/** The order all. */
		boolean orderAll = false;

		/** The not ordered. */
		int notOrdered = 0;

		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Void doInBackground() throws Exception {

			setProgress(0);
			setRunnerValue("0;" + Resurses.getString("MENU_CHILDREN_ORDER"));

			final int dbcount = owner.getDatabaseRowCount();
			final Vector<Integer> pidsnot = new Vector<Integer>();
			int prosent;
			for (int dbi = 0; dbi < dbcount; dbi++) {
				if (stopMeNow) {
					break;
				}
				prosent = (dbi * 100) / dbcount;
				final PersonShortData sho = owner.getDatbasePerson(dbi);
				final String tmp = "" + prosent + "; " + sho.getAlfaName();

				setRunnerValue(tmp);

				if (sho.getChildCount() > 1) {

					SukuData plong;
					try {
						plong = Suku.kontroller.getSukuData("cmd=sort", "all=" + orderAll, "pid=" + sho.getPid());
						if (plong.resuCount > 0) {
							pidsnot.add(sho.getPid());
						}
					} catch (final SukuException e) {

						JOptionPane.showMessageDialog(owner, e.toString(), Resurses.getString(Resurses.SUKU),
								JOptionPane.ERROR_MESSAGE);

						break;

					}

				}
			}

			if (pidsnot.size() > 0) {
				notOrdered = pidsnot.size();
				final SukuData request = new SukuData();
				request.pidArray = new int[pidsnot.size()];
				for (int i = 0; i < pidsnot.size(); i++) {
					request.pidArray[i] = pidsnot.get(i);
				}
				Suku.kontroller.getSukuData(request, "cmd=view", "action=add", "key=pidarray", "viewid=" + viewId,
						"empty=true");

			}

			return null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.SwingWorker#done()
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setVisible(false);
			if (notOrdered > 0) {
				JOptionPane.showMessageDialog(owner, Resurses.getString("STORE_VIEW_SIZE") + " " + notOrdered,
						Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			}
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

}
