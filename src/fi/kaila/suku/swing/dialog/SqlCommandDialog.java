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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class SqlCommandDialog.
 */
public class SqlCommandDialog extends JDialog implements ActionListener, ComponentListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private static final String OK = "OK";

	private JSplitPane splitPane = null;
	private JTextArea inputArea = null;
	private Vector<Vector<String>> rowData = null;
	private Vector<String> columnNames = null;
	private JTable outputTable = null;
	private JTextField errorField = null;

	private JComboBox selectExisting = null;

	private String[] preparedCommands = null;

	private JComboBox viewList = null;
	private int[] viewIds = null;
	private JCheckBox resetView = null;

	/** The ok. */
	JButton ok = null;

	/** The cancel. */
	JButton cancel = null;

	/**
	 * This class executes sql-commands to db.
	 *
	 * sql command is executed calling the server
	 *
	 * SukuData resu = Suku.kontroll
	 *
	 * @param parent
	 *            the parent
	 * @throws SukuException
	 *             the suku exception
	 */
	public SqlCommandDialog(Suku parent) throws SukuException {
		super(parent, Resurses.getString("MENU_TOOLS_SQL"), false);
		setLayout(null);
		getRootPane().addComponentListener(this);
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension size = new Dimension(650, 500);
		setBounds((d.width / 2) - 325, (d.height / 2) - 250, size.width, size.height);
		final String loca = Suku.kontroller.getPref(parent, Resurses.LOCALE, "xx");
		final int y = 30;
		inputArea = new JTextArea();
		inputArea.setLineWrap(true);
		final JScrollPane inputScroll = new JScrollPane(inputArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		inputArea.setWrapStyleWord(true);
		inputArea.setLineWrap(true);
		rowData = new Vector<Vector<String>>();
		columnNames = new Vector<String>();
		outputTable = new JTable(rowData, columnNames);

		final JScrollPane outputScroll = new JScrollPane(outputTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, inputScroll, outputScroll);
		add(this.splitPane);
		this.splitPane.setBounds(10, y, size.width - 40, size.height - 200);
		this.splitPane.setDividerLocation(0.5);
		this.errorField = new JTextField();
		add(this.errorField);
		this.errorField.setEditable(false);

		selectExisting = new JComboBox();
		selectExisting.addActionListener(this);
		add(selectExisting);

		selectExisting.addItem(Resurses.getString("TOOLS_SQL_SELECTCOMMAND"));

		extractCommands(loca);

		this.resetView = new JCheckBox(Resurses.getString("DIALOG_VIEW_ADD_EMPTY_VIEW"));
		add(this.resetView);

		SukuData vlist;

		vlist = Suku.kontroller.getSukuData("cmd=viewlist");

		final String[] lista = vlist.generalArray;
		this.viewList = new JComboBox();

		add(this.viewList);
		// viewList.removeAllItems();
		viewList.addItem(Resurses.getString("TOOLS_SQL_SELECTVIEW"));
		viewIds = new int[lista.length];
		for (int i = 0; i < lista.length; i++) {
			final String[] pp = lista[i].split(";");
			if (pp.length > 1) {
				int vid = 0;
				try {
					vid = Integer.parseInt(pp[0]);
					viewIds[i] = vid;
				} catch (final NumberFormatException ne) {
					viewIds[i] = 0;
				}
				viewList.addItem(pp[1]);
			}
		}

		ok = new JButton(OK);
		add(ok);

		ok.addActionListener(this);
		ok.setDefaultCapable(true);

		getRootPane().setDefaultButton(ok);

		cancel = new JButton(Resurses.getString("CANCEL"));
		add(cancel);

		cancel.addActionListener(this);

	}

	private void extractCommands(String loca) {
		final InputStream in = this.getClass().getResourceAsStream("/sql/queries.sql");

		String rivi = null;
		String defaultrivi = null;
		StringBuilder sb = new StringBuilder();
		final Vector<String> lista = new Vector<String>();
		try {
			while ((rivi = readRivi(in)) != null) {

				if (rivi.startsWith("--")) {
					if (rivi.startsWith("--en")) {
						defaultrivi = rivi;
					} else if (rivi.startsWith("--" + loca)) {
						defaultrivi = rivi.substring(4);
					}
				} else {
					if (rivi.trim().endsWith(";")) {
						final int lo = rivi.lastIndexOf(";");
						sb.append(rivi.substring(0, lo));
						lista.add(sb.toString());
						selectExisting.addItem(defaultrivi);
						// System.out.println("sb:" + sb.toString());
						sb = new StringBuilder();
					} else {
						sb.append(rivi);
						if (rivi.length() > 0) {
							sb.append(" ");
						}
					}
				}

			}
			preparedCommands = lista.toArray(new String[0]);
		} catch (final IOException e) {
			logger.log(Level.WARNING, "Creating prepared commands", e);
		}

	}

	private String readRivi(InputStream in) throws IOException {
		int nextByte = 0;

		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		while ((nextByte = in.read()) >= 0) {
			if (nextByte == '\n') {
				break;
			}
			if (nextByte != '\r') {
				if ((bout.size() > 0) || (nextByte != ' ')) {
					bout.write(nextByte);
				}
			}
		}
		if ((bout.size() == 0) && (nextByte < 0)) {
			return null;
		}
		return bout.toString("UTF-8");

	}

	private void executeSQL() {
		try {
			final String sql = inputArea.getText();
			final SukuData req = new SukuData();
			req.generalText = sql;
			this.columnNames.clear();
			this.rowData.clear();
			SukuData resu = null;
			final boolean emptyView = this.resetView.isSelected();
			final int viewIdx = this.viewList.getSelectedIndex();
			if (viewIdx > 0) {
				resu = Suku.kontroller.getSukuData(req, "cmd=sql", "type=select", "empty=" + emptyView,
						"vid=" + viewIds[viewIdx - 1]);
			} else {

				resu = Suku.kontroller.getSukuData(req, "cmd=sql", "type=select");
			}
			if (resu.resu != null) {
				errorField.setText(resu.resu);
				return;
			}
			if (resu.resuCount > 0) {
				errorField.setText(Resurses.getString("MENU_TOOLS_SQL_COUNT") + " " + resu.resuCount);
			} else {

				rowData = new Vector<Vector<String>>();
				columnNames = new Vector<String>();
				if (resu.vvTexts != null) {
					for (int i = 0; i < resu.vvTexts.size(); i++) {
						final String[] ss = resu.vvTexts.get(i);
						if (i == 0) {

							for (final String element : ss) {
								columnNames.add(element);
							}

						} else {
							final Vector<String> row = new Vector<String>();
							for (final String element : ss) {
								row.add(element);
							}
							rowData.add(row);

						}

					}

					outputTable = new JTable(rowData, columnNames);
					errorField.setText(Resurses.getString("MENU_TOOLS_SQL_COUNT") + " = " + rowData.size());
					outputTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					final TableColumnModel tc = outputTable.getColumnModel();
					TableColumn cc;
					for (int k = 0; k < columnNames.size(); k++) {
						cc = tc.getColumn(k);
						cc.setMinWidth(10);
					}

					final JScrollPane outputScroll = new JScrollPane(outputTable,
							ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
							ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

					splitPane.setRightComponent(outputScroll);

					// outputTable.updateUI();
					// outputScroll.updateUI();

				}
			}

		} catch (final SukuException e) {
			errorField.setText(e.getMessage());
			return;
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

		if (e.getSource() == ok) {
			executeSQL();
		}
		if (e.getSource() == cancel) {
			setVisible(false);
		}
		if (e.getSource() == selectExisting) {
			final int idx = selectExisting.getSelectedIndex();
			if (idx > 0) {
				splitPane.setDividerLocation(0.5);
				inputArea.setText(preparedCommands[idx - 1]);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent e) {

		final Dimension size = getSize();
		final int rootx = getRootPane().getLocation().x;
		final int rooty = getRootPane().getLocation().y;

		this.splitPane.setBounds(10, 30, size.width - 40, size.height - 200);
		this.errorField.setBounds(10, 30 + this.splitPane.getHeight() + 5, this.splitPane.getWidth(), 24);

		selectExisting.setBounds(10, 30 + this.splitPane.getHeight() + 34, this.splitPane.getWidth() - 20, 24);

		viewList.setBounds(10, 30 + this.splitPane.getHeight() + 64, this.splitPane.getWidth() - 200, 24);
		resetView.setBounds(this.splitPane.getWidth() - 190, 30 + this.splitPane.getHeight() + 64, 190, 24);
		ok.setBounds((rootx + size.width) - 250, (rooty + size.height) - 95, 100, 24);
		cancel.setBounds((rootx + size.width) - 140, (rooty + size.height) - 95, 100, 24);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.
	 * ComponentEvent )
	 */
	@Override
	public void componentMoved(ComponentEvent e) {
		// not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.
	 * ComponentEvent )
	 */
	@Override
	public void componentShown(ComponentEvent e) {
		// not used
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent e) {
		// not used
	}

}
