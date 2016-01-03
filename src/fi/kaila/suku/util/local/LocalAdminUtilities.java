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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import fi.kaila.suku.swing.ISuku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Utils;

/**
 * Admin part of application uses database directly from user side This is not
 * available in webstart version.
 *
 * @author Kalle
 */
public class LocalAdminUtilities extends JFrame implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// JMenu mDb;

	private final ISuku parent;
	private final LocalAdminUtilities me;

	private JMenuItem mNewUser = null;
	private JMenuItem mChangePassword = null;

	private JMenuItem mNewDatabase = null;
	private JMenuItem mDropDatabase = null;

	private JList dblista;
	private JComboBox userCombo;
	private final String dbDriver = "org.postgresql.Driver";

	/**
	 * Constructor.
	 *
	 * @param parent
	 *            the parent
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public LocalAdminUtilities(ISuku parent) throws ClassNotFoundException {

		this.parent = parent;
		this.me = this;
		initMe();

		Class.forName(this.dbDriver);

	}

	private void initMe() {

		final JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		final JMenu mFile = new JMenu(Resurses.getString(Resurses.DATABASE));
		menubar.add(mFile);

		// JMenuItem mConn = new
		// JMenuItem(Resurses.getString(Resurses.CONNECT));
		// mFile.add(mConn);
		// mConn.setActionCommand(Resurses.CONNECT);
		// mConn.addActionListener(this);
		// mFile.addSeparator();
		this.mNewUser = new JMenuItem(Resurses.getString(Resurses.NEWUSER));
		this.mNewUser.setEnabled(false);
		mFile.add(this.mNewUser);
		this.mNewUser.setActionCommand(Resurses.NEWUSER);
		this.mNewUser.addActionListener(this);

		this.mChangePassword = new JMenuItem(Resurses.getString(Resurses.CHANGEPASSWORD));
		this.mChangePassword.setEnabled(false);
		mFile.add(this.mChangePassword);
		this.mChangePassword.setActionCommand(Resurses.CHANGEPASSWORD);
		this.mChangePassword.addActionListener(this);
		mFile.addSeparator();

		this.mNewDatabase = new JMenuItem(Resurses.getString(Resurses.NEWDB));
		this.mNewDatabase.setEnabled(false);
		mFile.add(this.mNewDatabase);
		this.mNewDatabase.setActionCommand(Resurses.NEWDB);
		this.mNewDatabase.addActionListener(this);

		this.mDropDatabase = new JMenuItem(Resurses.getString(Resurses.DROPDB));
		this.mDropDatabase.setEnabled(false);
		mFile.add(this.mDropDatabase);
		this.mDropDatabase.setActionCommand(Resurses.DROPDB);
		this.mDropDatabase.addActionListener(this);

		mFile.addSeparator();
		final JMenuItem mExit = new JMenuItem(Resurses.getString(Resurses.EXIT));
		mFile.add(mExit);
		mExit.setActionCommand(Resurses.EXIT);
		mExit.addActionListener(this);

		JLabel lbl = new JLabel(Resurses.getString(Resurses.DATABASES));
		lbl.setBounds(30, 60, 200, 20);
		this.getContentPane().add(lbl);

		lbl = new JLabel(Resurses.getString(Resurses.USERS));
		lbl.setBounds(30, 10, 200, 20);
		this.getContentPane().add(lbl);

		// mDb = new JMenu("DB");
		final String[] data = { "" };// {"one", "two", "three", "four"};
		this.dblista = new JList(data);
		this.dblista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// this.dblista.setBounds(30,30,200,100);
		// getContentPane().add(this.dblista);

		// this.missingPlaceList = new JTextArea();
		// this.missingPlaceList.setEditable(false);
		final JScrollPane js = new JScrollPane(this.dblista);
		js.setBounds(30, 80, 200, 200);
		this.getContentPane().add(js);

		this.userCombo = new JComboBox();
		this.userCombo.setBounds(30, 30, 200, 20);
		this.getContentPane().add(this.userCombo);

		setLayout(null);
		setLocation(200, 200);

		setSize(new Dimension(400, 480));
		// setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (parent != null) {
					parent.AdminFormClosing(me);
				}
				e.getClass();

			}
		});

	}

	private String postPassword = null;

	private Connection getConnection() throws SQLException {
		if (this.postPassword == null) {
			return null;
		}

		String dbConne = "jdbc:postgresql://localhost/postgres?user=postgres";
		dbConne += "&password=" + this.postPassword;

		return DriverManager.getConnection(dbConne);

	}

	/**
	 * Connect postgres.
	 *
	 * @return true is succeeded to connect to databse
	 */
	public boolean connectPostgres() {
		/** The is H2 database. */
		final boolean isH2 = false;

		final AdminConnectDialog pdlg = new AdminConnectDialog(this);
		pdlg.setVisible(true);
		postPassword = pdlg.getPassword();

		if ((postPassword != null) && !postPassword.isEmpty()) {
			// this.postPassword =
			// JOptionPane.showInputDialog(Resurses.getString(Resurses.CONNEADMIN));
			String[] datalista;
			String[] userlista;
			try {
				final Connection con = getConnection();
				datalista = LocalDatabaseUtility.getListOfDatabases(con, isH2);
				// this.dblista.removeAll();
				this.dblista.setListData(datalista);

				userlista = LocalDatabaseUtility.getListOfUsers(con, isH2);
				this.userCombo.removeAllItems();
				for (final String element : userlista) {
					this.userCombo.addItem(element);

				}

				con.close();

				this.mNewUser.setEnabled(true);
				this.mNewDatabase.setEnabled(true);
				this.mChangePassword.setEnabled(true);
				this.mDropDatabase.setEnabled(true);

				return true;

			} catch (final Exception e1) {
				postPassword = null;
				JOptionPane.showMessageDialog(this, "Error: " + e1.getMessage());
				return false;
			}
		} else {
			postPassword = null;
			JOptionPane.showMessageDialog(this, Resurses.getString("CONNEADMIN"),
					Resurses.getString("STAT_FOR_YOUR_INFORMATION"), JOptionPane.INFORMATION_MESSAGE);
			return false;
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
		/** The is H2 database. */
		final boolean isH2 = false;

		final String cmd = e.getActionCommand();

		if (cmd == null) {
			return;
		}

		if (cmd.equals(Resurses.NEWUSER)) {

			String newUser = null;
			String newPassword = null;

			final LocalUserAdmin ladm = new LocalUserAdmin(this);
			ladm.setVisible(true);

			newUser = Utils.toUsAscii(ladm.getUserid());
			newPassword = ladm.getPassword();

			if ((newUser != null) && (newPassword != null) && (newUser.length() > 2) && (newPassword.length() > 2)) {

				for (int i = 0; i < this.userCombo.getItemCount(); i++) {
					final String aux = (String) this.userCombo.getItemAt(i);

					if ((aux != null) && aux.equalsIgnoreCase(newUser)) {
						JOptionPane.showMessageDialog(this, Resurses.getString("ADMINUSEREXIST"));
						return;
					}
				}

				try {
					final String sql = "CREATE ROLE " + newUser + " WITH LOGIN PASSWORD '" + newPassword + "' ";

					final Connection con = getConnection();
					final Statement stm = con.createStatement();
					stm.executeUpdate(sql);

					final String[] userlista = LocalDatabaseUtility.getListOfUsers(con, isH2);
					this.userCombo.removeAllItems();
					for (final String element : userlista) {
						this.userCombo.addItem(element);

					}
					stm.close();
					con.close();

				} catch (final Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error: " + e1.getMessage());
				}

			}
		} else if (cmd.equals(Resurses.CHANGEPASSWORD)) {

			final String oldUser = (String) this.userCombo.getSelectedItem();
			String newPassword = null;

			final LocalUserAdmin ladm = new LocalUserAdmin(this);
			ladm.setUserid(oldUser);

			ladm.setVisible(true);

			newPassword = ladm.getPassword();

			if (newPassword != null) {
				try {
					final String sql = "alter user " + oldUser + " password  '" + newPassword + "'";
					final Connection con = getConnection();
					final Statement stm = con.createStatement();
					stm.executeUpdate(sql);
					stm.close();
					con.close();

				} catch (final SQLException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error: " + e1.getMessage());
				}

			}
		} else if (cmd.equals(Resurses.NEWDB)) {

			final String newDb = Utils
					.toUsAscii(JOptionPane.showInputDialog(this, Resurses.getString("NEWDATABASENAME")));
			final Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglassCursor);

			final String owner = (String) this.userCombo.getSelectedItem();

			if ((owner != null) && (newDb != null) && (newDb.length() > 0)) {
				try {
					final String sql = "create database " + newDb + " owner = " + owner + " encoding = 'UTF8'";
					final Connection con = getConnection();
					final Statement stm = con.createStatement();
					stm.executeUpdate(sql);
					final String[] datalista = LocalDatabaseUtility.getListOfDatabases(con, isH2);
					this.dblista.setListData(datalista);
					stm.close();
					con.close();

				} catch (final Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error: " + e1.getMessage());
				}
				final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(normalCursor);
			}

		} else if (cmd.equals(Resurses.DROPDB)) {

			final String dbname = (String) this.dblista.getSelectedValue();

			if (dbname == null) {
				return;
			}

			if (JOptionPane.showConfirmDialog(this,
					Resurses.getString("ASKTODROP") + " " + dbname) == JOptionPane.YES_OPTION) {

				try {
					final String sql = "drop database " + dbname + " ";
					final Connection con = getConnection();
					final Statement stm = con.createStatement();
					stm.executeUpdate(sql);
					final String[] datalista = LocalDatabaseUtility.getListOfDatabases(con, isH2);
					this.dblista.setListData(datalista);
					stm.close();
					con.close();

				} catch (final Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error: " + e1.getMessage());
				}

			}
		} else if (cmd.equals(Resurses.EXIT)) {
			if (parent != null) {
				parent.AdminFormClosing(me);
			}
			this.setVisible(false);
		}

	}

}
