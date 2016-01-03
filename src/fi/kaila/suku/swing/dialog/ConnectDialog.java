package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import fi.kaila.suku.kontroller.SukuKontroller;
import fi.kaila.suku.util.Resurses;

/**
 * Dialog for connect to database.
 *
 * @author FIKAAKAIL
 */
public class ConnectDialog extends JDialog implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String OK = "OK";
	private static final String CANCEL = "CANCEL";

	private JLabel dbtypesLbl = null;
	private JComboBox dbtypes = null;
	private JLabel h2dbnameLbl = null;
	private JLabel h2dbname = null;
	private JLabel hostLbl = null;
	private JTextField host = null;
	private JLabel dbnameLbl = null;
	private JComboBox dbname = null;
	private JLabel useridLbl = null;
	private JTextField userid = null;
	private JLabel passwordLbl = null;
	private JTextField password = null;
	private JCheckBox rememberPwd = null;
	private final boolean isRemote;
	private boolean okPressed = false;
	private boolean rememberDatabase = false;
	private boolean needsInit = false;
	private SukuKontroller kontroller = null;

	/**
	 * Constructor for dialog.
	 *
	 * @param owner
	 *            the owner
	 * @param kontroller
	 *            the kontroller
	 */

	public ConnectDialog(JFrame owner, SukuKontroller kontroller) {
		super(owner, Resurses.getString("LOGIN_CONNECT"), true);
		this.isRemote = kontroller.isRemote();
		this.kontroller = kontroller;
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		final JLabel lbl;

		setLayout(null);
		int y = 20;
		if (!isRemote) {
			dbtypesLbl = new JLabel(Resurses.getString("LOGIN_DBTYPE"));
			getContentPane().add(dbtypesLbl);
			dbtypesLbl.setBounds(20, y, 100, 20);

			dbtypes = new JComboBox();
			dbtypes.setEditable(true);
			getContentPane().add(dbtypes);
			dbtypes.setBounds(120, y, 200, 20);
			dbtypes.setActionCommand("LOGIN_DBTYPE");
			dbtypes.addActionListener(this);
			y += 24;

			h2dbnameLbl = new JLabel(Resurses.getString("LOGIN_DBNAME"));
			getContentPane().add(h2dbnameLbl);
			h2dbnameLbl.setBounds(20, y, 100, 20);

			h2dbname = new JLabel("");
			getContentPane().add(h2dbname);
			h2dbname.setBounds(120, y, 200, 20);

			hostLbl = new JLabel(Resurses.getString("LOGIN_HOST"));
			getContentPane().add(hostLbl);
			hostLbl.setBounds(20, y, 100, 20);

			host = new JTextField();
			getContentPane().add(host);
			host.setBounds(120, y, 200, 20);
			y += 24;
			dbnameLbl = new JLabel(Resurses.getString("LOGIN_DBNAME"));
			getContentPane().add(dbnameLbl);
			dbnameLbl.setBounds(20, y, 100, 20);

			dbname = new JComboBox();
			dbname.setEditable(true);
			getContentPane().add(dbname);
			dbname.setBounds(120, y, 200, 20);
			y += 24;

		}
		useridLbl = new JLabel(Resurses.getString("LOGIN_USERID"));
		getContentPane().add(useridLbl);
		useridLbl.setBounds(20, y, 100, 20);

		userid = new JTextField();
		getContentPane().add(userid);
		userid.setBounds(120, y, 200, 20);
		y += 24;
		passwordLbl = new JLabel(Resurses.getString("LOGIN_PASSWORD"));
		getContentPane().add(passwordLbl);
		passwordLbl.setBounds(20, y, 100, 20);

		password = new JPasswordField();
		getContentPane().add(password);
		password.setBounds(120, y, 200, 20);

		y += 24;

		rememberPwd = new JCheckBox(Resurses.getString("LOGIN_REMEMBER"));
		getContentPane().add(rememberPwd);
		rememberPwd.setBounds(120, y, 200, 20);
		// y += 24;
		//
		// isRemote = new JCheckBox(Resurses.instance().getString("ISREMOTE"));
		// getContentPane().add(isRemote);
		// isRemote.setBounds(120,y,200,20);

		y += 40;
		final JButton ok = new JButton(Resurses.getString(OK));
		getContentPane().add(ok);
		ok.setBounds(100, y, 100, 24);
		ok.setActionCommand(OK);
		ok.addActionListener(this);
		ok.setDefaultCapable(true);
		getRootPane().setDefaultButton(ok);

		final JButton cancel = new JButton(Resurses.getString(CANCEL));
		getContentPane().add(cancel);
		cancel.setBounds(220, y, 100, 24);
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(this);
		this.okPressed = false;
		String aux;
		if (isRemote) {
			aux = this.kontroller.getPref(this, "WUSERID", "");
			userid.setText(aux);
			userid.setSelectionStart(aux.length());
			userid.setSelectionEnd(aux.length());

			aux = this.kontroller.getPref(this, "WPASSWORD", "");
			password.setText(aux);
			password.setSelectionStart(aux.length());
			password.setSelectionEnd(aux.length());
		} else {
			aux = this.kontroller.getPref(this, "DBTYPES", "postgresql;h2");
			if (aux != null) {
				final String[] names = aux.split(";");
				for (final String name2 : names) {
					dbtypes.addItem(name2);
				}
			}

			aux = this.kontroller.getPref(this, "HOST", "localhost");
			host.setText(aux);
			host.setSelectionStart(aux.length());
			host.setSelectionEnd(aux.length());

			aux = this.kontroller.getPref(this, "DBNAMES", "");
			if (aux != null) {
				final String[] names = aux.split(";");
				for (final String name2 : names) {

					dbname.addItem(name2);

				}

				// dbname.setText(aux);
				// dbname.setSelectionStart(aux.length());
				// dbname.setSelectionEnd(aux.length());
			}
			aux = this.kontroller.getPref(this, "USERID", "");
			userid.setText(aux);
			userid.setSelectionStart(aux.length());
			userid.setSelectionEnd(aux.length());

			aux = this.kontroller.getPref(this, "PASSWORD", "");
			password.setText(aux);
			password.setSelectionStart(aux.length());
			password.setSelectionEnd(aux.length());

			aux = this.kontroller.getPref(this, "REMEMBER", "false");
			rememberPwd.setSelected("true".equals(aux));
			aux = this.kontroller.getPref(this, "REMEMBER_DB", "true");
			rememberDatabase = "true".equals(aux) ? true : false;

		}
		setBounds((d.width / 2) - 200, (d.height / 2) - 100, 380, y + 80);
		setResizable(false);

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

		if (cmd.equals("LOGIN_DBTYPE")) {
			if (dbtypes.getSelectedItem().equals("h2")) {
				dbtypesLbl.setVisible(true);
				dbtypes.setVisible(true);
				h2dbnameLbl.setVisible(true);
				h2dbname.setVisible(true);
				hostLbl.setVisible(false);
				host.setVisible(false);
				dbnameLbl.setVisible(false);
				dbname.setVisible(false);
				useridLbl.setVisible(false);
				userid.setVisible(false);
				passwordLbl.setVisible(false);
				password.setVisible(false);
				rememberPwd.setVisible(false);
				this.h2dbname.setText(getDatabase());
			} else {
				dbtypesLbl.setVisible(true);
				dbtypes.setVisible(true);
				h2dbnameLbl.setVisible(false);
				h2dbname.setVisible(false);
				hostLbl.setVisible(true);
				host.setVisible(true);
				dbnameLbl.setVisible(true);
				dbname.setVisible(true);
				useridLbl.setVisible(true);
				userid.setVisible(true);
				passwordLbl.setVisible(true);
				password.setVisible(true);
				rememberPwd.setVisible(true);
				this.h2dbname.setText("");
			}
			return;
		}
		if (cmd.equals(CANCEL)) {
			this.okPressed = false;
			setVisible(false);
			return;
		}
		if (cmd.equals(OK)) {
			this.okPressed = true;
			rememberDatabase = true;
			String aux;
			if (dbtypes.getSelectedItem().equals("h2")) {
				aux = dbtypes.getSelectedItem().toString();
				this.kontroller.putPref(this, "DBTYPE", aux);

				aux = h2dbname.getText();
				this.kontroller.putPref(this, "DBNAME", aux + ".mv.db");
				this.kontroller.setDBType(true);
			} else {
				if (this.isRemote) {
					aux = userid.getText();
					this.kontroller.putPref(this, "WUSERID", aux);

					aux = password.getText();
					this.kontroller.putPref(this, "WPASSWORD", aux);

				} else {
					aux = host.getText();
					this.kontroller.putPref(this, "HOST", aux);

					aux = userid.getText();
					this.kontroller.putPref(this, "USERID", aux);

					if (rememberPwd.isSelected()) {

						this.kontroller.putPref(this, "REMEMBER", "true");

					} else {
						this.kontroller.putPref(this, "REMEMBER", "false");

					}
					aux = password.getText();
					if (rememberPwd.isSelected()) {
						this.kontroller.putPref(this, "PASSWORD", aux);
					} else {
						this.kontroller.putPref(this, "PASSWORD", "");
					}
				}
				this.kontroller.setDBType(false);
			}
			this.kontroller.putPref(this, "REMEMBER_DB", "true");

			setVisible(false);
		}
	}

	/**
	 * Was ok.
	 *
	 * @return true if ok was pressed
	 */
	public boolean wasOk() {
		return this.okPressed;
	}

	/**
	 * Gets the host.
	 *
	 * @return host
	 */
	public String getHost() {
		if (this.host == null) {
			return null;
		}
		return this.host.getText();
	}

	/**
	 * Gets the db type.
	 *
	 * @return database type
	 */
	public boolean isH2() {
		if (dbtypes.getSelectedItem().equals("h2")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the db type.
	 *
	 * @return database type
	 */
	public boolean hasInit() {
		return needsInit;
	}

	/**
	 * Gets the db name.
	 *
	 * @return database name
	 */
	public String getDbName() {

		if (dbtypes.getSelectedItem().equals("h2")) {
			if (this.h2dbname == null) {
				return null;
			}

			return this.h2dbname.getText();
		} else {
			if (this.dbname == null) {
				return null;
			}

			return (String) this.dbname.getSelectedItem();
		}
	}

	/**
	 * Gets the user id.
	 *
	 * @return userid
	 */
	public String getUserId() {
		return this.userid.getText();
	}

	/**
	 * Gets the password.
	 *
	 * @return database password
	 */
	public String getPassword() {
		final String pwd = this.password.getText();
		if (pwd.isEmpty()) {
			return null;
		}
		return pwd;
	}

	/**
	 * Remember database.
	 *
	 * @param value
	 *            the value
	 */
	public void rememberDatabase(boolean value) {
		rememberDatabase = value;
		this.kontroller.putPref(this, "REMEMBER_DB", rememberDatabase ? "true" : "false");
		// this.kontroller.putPref(this, "PASSWORD", "");
	}

	/**
	 * Checks for database.
	 *
	 * @return true is database is activated
	 */
	public boolean hasDatabase() {
		return rememberDatabase;
	}

	private String getDatabase() {
		String h2databaseName = "";
		final JFileChooser chooser = new JFileChooser();
		final FileNameExtensionFilter filter = new FileNameExtensionFilter("H2 database", "db");
		chooser.setFileFilter(filter);

		needsInit = false;
		final String defaultName = kontroller.getPref(this, "DBNAME", "");
		if (!defaultName.equals("")) {
			chooser.setSelectedFile(new File(defaultName));
		}

		final int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			h2databaseName = chooser.getSelectedFile().getPath();
			if (h2databaseName.endsWith(".mv.db")) {
				h2databaseName = h2databaseName.substring(0, h2databaseName.length() - 6);
			}
			if (!chooser.getSelectedFile().exists()) {
				needsInit = true;
				if (JOptionPane.showConfirmDialog(new JFrame(), Resurses.getString("CONFIRM_NEWH2DB"),
						h2databaseName + ".mv.db", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
					return h2databaseName;
				}
			}

		} else {
			return "";
		}
		return h2databaseName;
	}

}
