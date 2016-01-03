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

package fi.kaila.suku.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import fi.kaila.suku.ant.AntVersion;
import fi.kaila.suku.exports.ExportFamilyDatabaseDialog;
import fi.kaila.suku.exports.ExportGedcomDialog;
import fi.kaila.suku.imports.Import2004Dialog;
import fi.kaila.suku.imports.ImportGedcomDialog;
import fi.kaila.suku.imports.ImportOtherDialog;
import fi.kaila.suku.kontroller.SukuKontroller;
import fi.kaila.suku.kontroller.SukuKontrollerHybridImpl;
import fi.kaila.suku.kontroller.SukuKontrollerLocalImpl;
import fi.kaila.suku.kontroller.SukuKontrollerWebstartImpl;
import fi.kaila.suku.report.dialog.JoinDialog;
import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.swing.dialog.AboutDialog;
import fi.kaila.suku.swing.dialog.ConnectDialog;
import fi.kaila.suku.swing.dialog.GenStat;
import fi.kaila.suku.swing.dialog.GroupMgrWindow;
import fi.kaila.suku.swing.dialog.LicenseDialog;
import fi.kaila.suku.swing.dialog.OrderChildren;
import fi.kaila.suku.swing.dialog.OwnerDialog;
import fi.kaila.suku.swing.dialog.SearchCriteria;
import fi.kaila.suku.swing.dialog.SearchCriteria.ColTable;
import fi.kaila.suku.swing.dialog.SelectSchema;
import fi.kaila.suku.swing.dialog.SettingsDialog;
import fi.kaila.suku.swing.dialog.SqlCommandDialog;
import fi.kaila.suku.swing.dialog.SukuPad;
import fi.kaila.suku.swing.dialog.ToolsDialog;
import fi.kaila.suku.swing.dialog.ViewMgrWindow;
import fi.kaila.suku.swing.panel.PersonView;
import fi.kaila.suku.swing.panel.SukuTabPane;
import fi.kaila.suku.swing.util.SukuPopupMenu;
import fi.kaila.suku.swing.util.SukuPopupMenu.MenuSource;
import fi.kaila.suku.util.ExcelBundle;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateComparator;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuModel;
import fi.kaila.suku.util.SukuNameComparator;
import fi.kaila.suku.util.SukuNumStringComparator;
import fi.kaila.suku.util.SukuPidComparator;
import fi.kaila.suku.util.SukuRow;
import fi.kaila.suku.util.SukuSenser;
import fi.kaila.suku.util.SukuStringComparator;
import fi.kaila.suku.util.SukuTypesModel;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.VersionChecker;
import fi.kaila.suku.util.local.LocalAdminUtilities;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.PlaceLocationData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 *
 *
 *
 * <h1>FinFamily main program</h1>
 *
 * <p>
 * Swing-based java-application for managing genealogical data.
 * </p>
 * <p>
 * The genealogical data is stored in a PostgreSQL database
 * </p>
 *
 * <h2>See <a href="../../../../overview.html#lic">Finfamily License</a></h2>
 *
 * <h2>Starting the application</h2>
 *
 * <p>
 * The FinFamily application is distributed as a zip file. unzip that file and
 * you are ready to go assuming you have installed the PostgreSQL database as
 * described in the guide.
 * </p>
 *
 * <h3>Windows</h3>
 *
 * <p>
 * For windows users there is a convenience application Suku.exe that you use to
 * start the main application. Suku.exe reads the suku.sh command. Changes the
 * java command to javaw and executes the command. You can rename suku.sh to
 * suku.bat if you like to start it showing the command line output.
 * </p>
 *
 * <p>
 * suku.sh contains something like the command below to start suku. If you are
 * familiar with java then this tells you how. Else you need not care for it.
 * </p>
 *
 * java -Xms64m -Xmx500m
 * -Djava.util.logging.config.file=properties/logging.properties -jar suku.jar
 *
 * <h3>Linux</h3>
 *
 * <p>
 * In Linux (or similar) execute the suku.sh command. You should first chmod it
 * to be an executable file unless you start it using the sh command
 * </p>
 *
 * @author Kaarle Kaila
 *
 *
 */
public class Suku extends JFrame implements ActionListener, ComponentListener, MenuListener, MouseListener,
		MouseMotionListener, KeyListener, ISuku, ClipboardOwner {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Application version moved to class fi.kaila.suku.ant.AntVersion
	 */
	// public static final String sukuVersion = "11.-3.0380";
	/**
	 * Server version
	 */
	public static String serverVersion = null;

	/** The is H2 database. */
	public boolean isH2 = false;

	private static Logger logger = Logger.getLogger(Suku.class.getName());

	private JMenuBar menubar;
	private JMenu mFile;
	private JMenu mImport;
	private JMenuItem mImport2004;
	private JMenuItem mImportGedcom;
	private JMenuItem mImportOther;
	private JMenu mExport;
	private JMenuItem mExportGedcom;
	private JMenuItem mExportBackup;
	private JMenuItem mQuery;
	private JMenuItem mSubjectDown;
	private JMenuItem mSubjectUp;
	private JMenuItem mConnect;
	private JMenuItem mNewDatabase;
	private JMenuItem mDropSchema;
	private JMenuItem mDisconnect;
	private JMenuItem mPrintPerson;
	private JMenuItem mNewPerson;
	private JMenuItem mRemPerson;
	private JMenuItem mOpenPerson;
	private JMenuItem mLista;
	// private JMenuItem mTestSave;
	// private JMenuItem mTestOpen;
	private JMenu mShowInMap;
	private JMenuItem mShowWithBirth;
	private JMenuItem mShowWithDeath;
	// private JMenuItem mReport;
	private JMenuItem mExit;
	private JMenuItem mAdmin;
	// private JMenu mEdit;
	// private JMenuItem mCopy;

	private JMenu mShow;
	private JCheckBoxMenuItem mShowNotices;
	private JCheckBoxMenuItem mShowNote;
	private JCheckBoxMenuItem mShowAddress;
	private JCheckBoxMenuItem mShowFarm;
	private JCheckBoxMenuItem mShowImage;
	private JCheckBoxMenuItem mShowPrivate;

	private JMenu mTools;
	private JMenuItem mSettings;
	private JMenuItem mGroupMgr;
	private JMenuItem mViewMgr;
	private JMenuItem mStatistics;
	private JMenuItem mExecSql;
	private JMenuItem mLoadCoordinates;
	private JMenuItem mLoadTypes;
	private JMenuItem mLoadConversions;
	private JMenuItem mStoreAllConversions;
	private JMenuItem mStoreConversions;
	private JMenuItem mDbWork;
	private JMenuItem mOrderChildren;
	private JMenuItem mOwner;
	private JMenuItem mDbUpdate;
	private JMenu mToolsAuxProgram;

	/** The m tools aux graphviz. */
	public JMenuItem mToolsAuxGraphviz;
	private JMenuItem mListDatabases;
	// private JMenuItem mStopPgsql;
	// private JMenuItem mStartPgsql;
	private JMenu mActions;
	private JCheckBoxMenuItem mImportHiski;
	private JMenu mHelp;
	private JMenuItem mAbout;
	private JToolBar toolbar;

	private JButton tQueryButton;
	private JButton tPersonButton;
	private JButton tSubjectButton;
	private JButton tSubjectPButton;
	// private JButton tSubjectName;
	private JButton tMapButton;
	private JButton tRemovePerson;
	private JButton tHiskiPane;
	private JButton tAddNotice;
	private JButton tDeleteNotice;
	private JButton tNoteButton;
	private JButton tAddressButton;
	private JButton tFarmButton;
	private JButton tImageButton;
	private JButton tNoticesButton;
	private JButton tPrivateButton;

	private final Vector<String> needle = new Vector<String>();
	private static final int maxNeedle = 32;
	// private int isConnected = 0; // 0 = disconnected, 1 = connect to non
	// suku, 2
	private int imageScalingIndex = 0;
	// = connect to suku
	/** The is exiting. */
	boolean isExiting = false;
	private String databaseName = null;
	private PopupListener popupListener;

	private static final int SPLITTER_HORIZ_MARGIN = 10;

	private SukuModel tableModel;
	private DbTable table = null;
	private JScrollPane scrollPane = null;
	private HashMap<Integer, PersonShortData> tableMap = null;

	private JSplitPane splitPane = null;

	private PersonView personView = null;

	private JTextField statusPanel = null;
	private SukuMapInterface suomi = null;
	private GroupMgrWindow groupWin = null;
	private ViewMgrWindow viewWin = null;
	// private HiskiImporter hiski=null;
	private LocalAdminUtilities adminUtilities = null;

	private String os = "web";

	/** A static variable that contains the Suku kontroller in use. */
	public static SukuKontroller kontroller = null;

	/** During connect to database the database version is stored here. */
	public static String postServerVersion = null;

	private static String[] repoLangList = null;

	/** A "clipboard" location where a person can be copied to. */
	public static Object sukuObject = null;
	private static SearchCriteria crit = null;
	private int activePersonPid = 0;
	// private boolean isWebStart = false;
	private String url = null;
	private static JFrame myFrame = null;
	private static final int needleSize = 4;
	private int joinPersonPid = 0;
	/**
	 * location for textfile FinFamily.xls or null if located at
	 * resources/excel/FinFamily.xls
	 */
	private static String finFamilyXls = null;

	/**
	 * FinFamily main program entry point when used as standard Swing
	 * application.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		final Suku w = new Suku();
		try {
			w.startMe(args);
		} catch (final SukuException e) {
			logger.log(Level.SEVERE, "Unable to start", e);
		}

	}

	private void startMe(String[] args) throws SukuException {
		myFrame = this;
		String arg1 = null;
		if (args.length > 0) {
			arg1 = args[0];

		}

		if ("web".equals(arg1)) {

			kontroller = new SukuKontrollerWebstartImpl();
		} else {
			final Preferences sr = Preferences.userRoot();
			url = sr.get(this.getClass().getName() + "." + "SERVERURL", "");
			if (url.isEmpty()) {
				url = null;
			}
			if (url == null) {
				kontroller = new SukuKontrollerLocalImpl(this);
			} else {
				kontroller = new SukuKontrollerHybridImpl(this, url);
			}
		}
		try {

			final String lfdef = Suku.kontroller.getPref(this, "LOOK_AND_FEEL", "");
			if (!lfdef.equals("")) {
				final UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
				int lfIdx = -1;
				for (int i = 0; i < lafInfo.length; i++) {
					if (lfdef.equalsIgnoreCase(lafInfo[i].getName())) {
						lfIdx = i;
						break;
					}
				}

				if (lfIdx >= 0) {
					UIManager.setLookAndFeel(lafInfo[lfIdx].getClassName());
				}

			} else {
				if ((args.length != 1) || !args[0].equals("web")) {

					os = System.getProperty("os.name");
					if ((args.length > 0) && !args[0].equals("$1")) {
						// if you want to experiment with another look and feel
						// you
						// can
						// write
						// its class name as argument to the program
						// set metal as name for
						// CrossPlatformLookAndFeelClassName
						if (args[0].equals("metal")) {
							UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
						} else {
							UIManager.setLookAndFeel(args[0]);
						}
					} else {

						// if (os.toLowerCase().indexOf("windows") >= 0
						// || os.toLowerCase().indexOf("mac") >= 0) {

						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						// } else if (os.toLowerCase().indexOf("linux") >= 0) {
						// UIManager
						// .setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
						// } else {
						// UIManager.setLookAndFeel(UIManager
						// .getCrossPlatformLookAndFeelClassName());
						// }
					}
				}
			}
			// * com.jgoodies.looks.windows.WindowsLookAndFeel
			// * com.jgoodies.looks.plastic.PlasticLookAndFeel
			// * com.jgoodies.looks.plastic.Plastic3DLookAndFeel
			// * com.jgoodies.looks.plastic.PlasticXPLookAndFeel
			// UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");

		} catch (final Exception e) {
			logger.log(Level.INFO, "look-and-feel virhe", e);

		}
		Suku.setFinFamilyXls(kontroller.getPref(this, "FINFAMILY.XLS", ""));
		String loca = kontroller.getPref(this, Resurses.LOCALE, "xx");
		if ((loca == null) || loca.equals("xx")) {
			logger.info("Locale " + loca + " encountered.");
			final String languas[] = { "English", "Suomi", "Svenska", "Deutsch" };
			final String langabr[] = { "en", "fi", "sv", "de" };
			final int locaresu = JOptionPane.showOptionDialog(this, "Select language / valitse kieli / välj språket",
					"FinFamily", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, languas, "en");
			loca = langabr[locaresu];
			kontroller.putPref(this, Resurses.LOCALE, loca);
		}

		final String scaleImageText = Suku.kontroller.getPref(this, "SCALE_IMAGE", "0");
		if (scaleImageText != null) {
			imageScalingIndex = Integer.parseInt(scaleImageText);
		}

		Resurses.setLocale(loca);

		final String langu = kontroller.getPref(this, Resurses.REPOLANG, "fi");
		Resurses.setLanguage(langu);
		final String datfo = kontroller.getPref(this, Resurses.DATEFORMAT, "FI");
		Resurses.setDateFormat(datfo);

		this.menubar = new JMenuBar();
		setJMenuBar(this.menubar);

		// File menu

		this.mFile = new JMenu(Resurses.getString(Resurses.FILE));
		this.menubar.add(this.mFile);

		this.mConnect = new JMenuItem(Resurses.getString(Resurses.CONNECT));
		this.mFile.add(this.mConnect);
		this.mConnect.setActionCommand(Resurses.CONNECT);
		this.mConnect.addActionListener(this);
		this.mFile.addMenuListener(this);

		if (!kontroller.isRemote()) {

			this.mAdmin = new JMenuItem(Resurses.getString(Resurses.ADMIN));
			this.mFile.add(this.mAdmin);
			this.mAdmin.setActionCommand(Resurses.ADMIN);
			this.mAdmin.addActionListener(this);
		}

		this.mFile.addSeparator();

		this.mNewDatabase = new JMenuItem(Resurses.getString("SCHEMA_INITIALIZE"));
		this.mFile.add(this.mNewDatabase);
		this.mNewDatabase.setActionCommand("SCHEMA_INITIALIZE");
		this.mNewDatabase.addActionListener(this);
		if (!kontroller.isRemote()) {
			this.mDropSchema = new JMenuItem(Resurses.getString("SCHEMA_DROP"));
			this.mFile.add(this.mDropSchema);
			this.mDropSchema.setActionCommand("SCHEMA_DROP");
			this.mDropSchema.addActionListener(this);
		}
		this.mFile.addSeparator();

		mImport = new JMenu(Resurses.getString("IMPORT"));
		this.mFile.add(mImport);

		this.mImport2004 = new JMenuItem(Resurses.getString(Resurses.IMPORT_SUKU));
		mImport.add(this.mImport2004);
		this.mImport2004.setActionCommand(Resurses.IMPORT_SUKU);
		this.mImport2004.addActionListener(this);

		this.mImportGedcom = new JMenuItem(Resurses.getString(Resurses.IMPORT_GEDCOM));
		mImport.add(this.mImportGedcom);
		this.mImportGedcom.setActionCommand(Resurses.IMPORT_GEDCOM);
		this.mImportGedcom.addActionListener(this);
		if (!kontroller.isRemote()) {
			this.mImportOther = new JMenuItem(Resurses.getString(Resurses.IMPORT_OTHER));
			mImport.add(this.mImportOther);
			this.mImportOther.setActionCommand(Resurses.IMPORT_OTHER);
			this.mImportOther.addActionListener(this);
		}
		mExport = new JMenu(Resurses.getString("EXPORT"));
		this.mFile.add(mExport);
		this.mExportGedcom = new JMenuItem(Resurses.getString(Resurses.EXPORT_GEDCOM));
		mExport.add(this.mExportGedcom);
		this.mExportGedcom.setActionCommand(Resurses.EXPORT_GEDCOM);
		this.mExportGedcom.addActionListener(this);

		this.mExportBackup = new JMenuItem(Resurses.getString(Resurses.EXPORT_BACKUP));
		mExport.add(this.mExportBackup);
		this.mExportBackup.setActionCommand(Resurses.EXPORT_BACKUP);
		this.mExportBackup.addActionListener(this);

		this.mFile.addSeparator();

		this.mPrintPerson = new JMenuItem(Resurses.getString(Resurses.PRINT_PERSON));
		this.mFile.add(this.mPrintPerson);
		this.mPrintPerson.setActionCommand(Resurses.PRINT_PERSON);
		this.mPrintPerson.addActionListener(this);

		this.mFile.addSeparator();

		this.mDisconnect = new JMenuItem(Resurses.getString(Resurses.DISCONNECT));
		this.mFile.add(this.mDisconnect);
		this.mDisconnect.setActionCommand(Resurses.DISCONNECT);
		this.mDisconnect.addActionListener(this);

		this.mExit = new JMenuItem(Resurses.getString(Resurses.EXIT));
		this.mFile.add(this.mExit);
		this.mExit.setActionCommand(Resurses.EXIT);
		this.mExit.addActionListener(this);

		// Actions menu

		this.mActions = new JMenu(Resurses.getString("MENU_ACTIONS"));
		this.menubar.add(this.mActions);

		this.mQuery = new JMenuItem(Resurses.getString(Resurses.QUERY));
		this.mActions.add(this.mQuery);
		this.mQuery.setActionCommand(Resurses.QUERY);
		this.mQuery.addActionListener(this);

		this.mActions.addSeparator();

		this.mSubjectDown = new JMenuItem(Resurses.getString("TOOLBAR.SUBJECT.TOOLTIP"));
		this.mActions.add(this.mSubjectDown);
		this.mSubjectDown.setActionCommand(Resurses.TOOLBAR_SUBJECT_DOWN_ACTION);
		this.mSubjectDown.addActionListener(this);

		this.mSubjectUp = new JMenuItem(Resurses.getString("TOOLBAR.SUBJECTP.TOOLTIP"));
		this.mActions.add(this.mSubjectUp);
		this.mSubjectUp.setActionCommand(Resurses.TOOLBAR_SUBJECT_UP_ACTION);
		this.mSubjectUp.addActionListener(this);

		this.mActions.addSeparator();

		this.mNewPerson = new JMenuItem(Resurses.getString("TOOLBAR.NEWPERSON.TOOLTIP"));
		this.mActions.add(this.mNewPerson);
		this.mNewPerson.setActionCommand(Resurses.TOOLBAR_NEWPERSON_ACTION);
		this.mNewPerson.addActionListener(this);

		this.mImportHiski = new JCheckBoxMenuItem(Resurses.getString(Resurses.IMPORT_HISKI));
		this.mActions.add(this.mImportHiski);
		this.mImportHiski.setActionCommand(Resurses.IMPORT_HISKI);
		this.mImportHiski.addActionListener(this);
		boolean openHiskiWhenReady = false;
		String tmp = kontroller.getPref(this, Resurses.IMPORT_HISKI, "false");
		if (tmp.equals("true")) {
			mImportHiski.setSelected(true);
			openHiskiWhenReady = true;
		}

		this.mRemPerson = new JMenuItem(Resurses.getString("TOOLBAR.REMPERSON.TOOLTIP"));
		this.mActions.add(this.mRemPerson);
		this.mRemPerson.setActionCommand(Resurses.TOOLBAR_REMPERSON_ACTION);
		this.mRemPerson.addActionListener(this);

		this.mOpenPerson = new JMenuItem(Resurses.getString("MENU_OPEN_PERSON"));
		this.mActions.add(this.mOpenPerson);
		this.mOpenPerson.setActionCommand(Resurses.MENU_OPEN_PERSON);
		this.mOpenPerson.addActionListener(this);

		this.mActions.addSeparator();

		this.mShowInMap = new JMenu(Resurses.getString(Resurses.SHOWINMAP));
		this.mActions.add(this.mShowInMap);
		this.mShowWithBirth = new JMenuItem(Resurses.getString("SHOWINMAPBIRTH"));
		this.mShowInMap.add(this.mShowWithBirth);
		this.mShowWithDeath = new JMenuItem(Resurses.getString("SHOWINMAPDEATH"));
		this.mShowInMap.add(this.mShowWithDeath);
		this.mShowWithBirth.setActionCommand("SHOWINMAPBIRTH");
		this.mShowWithBirth.addActionListener(this);
		this.mShowWithDeath.setActionCommand("SHOWINMAPDEATH");
		this.mShowWithDeath.addActionListener(this);

		this.mStatistics = new JMenuItem(Resurses.getString("MENU_TOOLS_STAT"));
		this.mActions.add(this.mStatistics);
		this.mStatistics.setActionCommand("MENU_TOOLS_STAT");
		this.mStatistics.addActionListener(this);

		this.mActions.addSeparator();

		mLista = new JMenuItem(Resurses.getString(Resurses.MENU_LISTA));
		mActions.add(mLista);
		mLista.setActionCommand(Resurses.MENU_LISTA);
		mLista.addActionListener(this);

		// Show menu

		this.mShow = new JMenu(Resurses.getString("MENU_SHOW"));
		this.menubar.add(this.mShow);

		this.mShowNotices = new JCheckBoxMenuItem(Resurses.getString("TOOLBAR.NOTICES.TOOLTIP"));
		this.mShow.add(this.mShowNotices);
		this.mShowNotices.setActionCommand(Resurses.TOOLBAR_NOTICES_ACTION);
		this.mShowNotices.addActionListener(this);
		tmp = kontroller.getPref(this, Resurses.NOTICES_BUTTON, "false");
		if (tmp.equals("true")) {
			mShowNotices.setSelected(true);
		}

		this.mShowNote = new JCheckBoxMenuItem(Resurses.getString("TOOLBAR.NOTE.TOOLTIP"));
		this.mShow.add(this.mShowNote);
		this.mShowNote.setActionCommand(Resurses.TOOLBAR_NOTE_ACTION);
		this.mShowNote.addActionListener(this);
		tmp = kontroller.getPref(this, Resurses.TOOLBAR_NOTE_ACTION, "false");
		if (tmp.equals("true")) {
			mShowNote.setSelected(true);
		}

		this.mShowAddress = new JCheckBoxMenuItem(Resurses.getString("TOOLBAR.ADDRESS.TOOLTIP"));
		this.mShow.add(this.mShowAddress);
		this.mShowAddress.setActionCommand(Resurses.TOOLBAR_ADDRESS_ACTION);
		this.mShowAddress.addActionListener(this);
		tmp = kontroller.getPref(this, Resurses.TOOLBAR_ADDRESS_ACTION, "false");
		if (tmp.equals("true")) {
			mShowAddress.setSelected(true);
		}

		this.mShowFarm = new JCheckBoxMenuItem(Resurses.getString("TOOLBAR.FARM.TOOLTIP"));
		this.mShow.add(this.mShowFarm);
		this.mShowFarm.setActionCommand(Resurses.TOOLBAR_FARM_ACTION);
		this.mShowFarm.addActionListener(this);
		tmp = kontroller.getPref(this, Resurses.TOOLBAR_FARM_ACTION, "false");
		if (tmp.equals("true")) {
			mShowFarm.setSelected(true);
		}

		this.mShowImage = new JCheckBoxMenuItem(Resurses.getString("TOOLBAR.IMAGE.TOOLTIP"));
		this.mShow.add(this.mShowImage);
		this.mShowImage.setActionCommand(Resurses.TOOLBAR_IMAGE_ACTION);
		this.mShowImage.addActionListener(this);
		tmp = kontroller.getPref(this, Resurses.TOOLBAR_IMAGE_ACTION, "false");
		if (tmp.equals("true")) {
			mShowImage.setSelected(true);
		}

		this.mShowPrivate = new JCheckBoxMenuItem(Resurses.getString("TOOLBAR.PRIVATE.TOOLTIP"));
		this.mShow.add(this.mShowPrivate);
		this.mShowPrivate.setActionCommand(Resurses.TOOLBAR_PRIVATE_ACTION);
		this.mShowPrivate.addActionListener(this);
		tmp = kontroller.getPref(this, Resurses.TOOLBAR_PRIVATE_ACTION, "false");
		if (tmp.equals("true")) {
			mShowPrivate.setSelected(true);
		}

		// Tools menu

		this.mTools = new JMenu(Resurses.getString("TOOLS"));
		this.menubar.add(this.mTools);
		this.mSettings = new JMenuItem(Resurses.getString(Resurses.SETTINGS));
		this.mTools.add(this.mSettings);
		this.mSettings.setActionCommand(Resurses.SETTINGS);
		this.mSettings.addActionListener(this);

		final JMenu auxCommands = new JMenu(Resurses.getString("MENU_TOOLS_DBWORK"));
		this.mTools.add(auxCommands);

		this.mDbWork = new JMenuItem(Resurses.getString("MENU_NOTICES_ORDER"));
		auxCommands.add(this.mDbWork);
		this.mDbWork.setActionCommand("MENU_NOTICES_ORDER");
		this.mDbWork.addActionListener(this);

		this.mOrderChildren = new JMenuItem(Resurses.getString("MENU_CHILDREN_ORDER"));
		auxCommands.add(this.mOrderChildren);
		this.mOrderChildren.setActionCommand("MENU_CHILDREN_ORDER");
		this.mOrderChildren.addActionListener(this);

		this.mExecSql = new JMenuItem(Resurses.getString("MENU_TOOLS_SQL"));
		auxCommands.add(this.mExecSql);
		this.mExecSql.setActionCommand("MENU_TOOLS_SQL");
		this.mExecSql.addActionListener(this);

		this.mDbUpdate = new JMenuItem(Resurses.getString(Resurses.updateDb));
		this.mTools.add(this.mDbUpdate);
		this.mDbUpdate.setActionCommand(Resurses.updateDb);
		this.mDbUpdate.addActionListener(this);

		this.mGroupMgr = new JMenuItem(Resurses.getString("MENU_TOOLS_GROUP_MGR"));
		this.mTools.add(this.mGroupMgr);
		this.mGroupMgr.setActionCommand("MENU_TOOLS_GROUP_MGR");
		this.mGroupMgr.addActionListener(this);

		this.mViewMgr = new JMenuItem(Resurses.getString("MENU_TOOLS_VIEW_MGR"));
		this.mTools.add(this.mViewMgr);
		this.mViewMgr.setActionCommand("MENU_TOOLS_VIEW_MGR");
		this.mViewMgr.addActionListener(this);

		final JMenu load = new JMenu(Resurses.getString("MENU_TOOLS_LOAD"));
		mTools.add(load);
		mLoadCoordinates = new JMenuItem(Resurses.getString("MENU_TOOLS_LOAD_COORDINATES"));
		load.add(mLoadCoordinates);
		mLoadCoordinates.setActionCommand("MENU_TOOLS_LOAD_COORDINATES");
		mLoadCoordinates.addActionListener(this);

		mLoadTypes = new JMenuItem(Resurses.getString("MENU_TOOLS_LOAD_TYPES"));
		load.add(mLoadTypes);
		mLoadTypes.setActionCommand("MENU_TOOLS_LOAD_TYPES");
		mLoadTypes.addActionListener(this);

		final JMenu cnv = new JMenu(Resurses.getString("MENU_TOOLS_CONVERSIONS"));
		this.mTools.add(cnv);

		mLoadConversions = new JMenuItem(Resurses.getString("MENU_TOOLS_LOAD_CONVERSIONS"));
		cnv.add(mLoadConversions);
		mLoadConversions.setActionCommand("MENU_TOOLS_LOAD_CONVERSIONS");
		mLoadConversions.addActionListener(this);

		mStoreConversions = new JMenuItem(Resurses.getString("MENU_TOOLS_STORE_CONVERSIONS"));
		cnv.add(mStoreConversions);
		mStoreConversions.setActionCommand("MENU_TOOLS_STORE_CONVERSIONS");
		mStoreConversions.addActionListener(this);

		mStoreAllConversions = new JMenuItem(Resurses.getString("MENU_TOOLS_STORE_ALL_CONVERSIONS"));
		cnv.add(mStoreAllConversions);
		mStoreAllConversions.setActionCommand("MENU_TOOLS_STORE_ALL_CONVERSIONS");
		mStoreAllConversions.addActionListener(this);
		mTools.addSeparator();
		if (!kontroller.isWebStart()) {
			mToolsAuxProgram = new JMenu(Resurses.getString("TOOLS_AUX_COMMANDS"));
			mTools.add(mToolsAuxProgram);
			mToolsAuxGraphviz = new JMenuItem(Resurses.getString("TOOLS_AUX_GRAPHVIZ"));
			mToolsAuxGraphviz.addActionListener(this);
			mToolsAuxGraphviz.setActionCommand("GRAPHVIZ");
			final String exeTask = kontroller.getPref(this, "GRAPHVIZ", "");
			if ("".equals(exeTask)) {
				mToolsAuxGraphviz.setEnabled(false);
			}
			mToolsAuxProgram.add(mToolsAuxGraphviz);

			mListDatabases = new JMenuItem(Resurses.getString("MENU_TOOLS_LIST_DATABASES"));
			mTools.add(mListDatabases);
			mListDatabases.setActionCommand("MENU_TOOLS_LIST_DATABASES");
			if (kontroller.isRemote()) {
				mListDatabases.setEnabled(false);
			}
			mListDatabases.addActionListener(this);
		}

		// Help menu

		this.mHelp = new JMenu(Resurses.getString(Resurses.HELP));

		this.menubar.add(this.mHelp);
		final JMenuItem swUpdate = new JMenuItem(Resurses.getString(Resurses.SW_UPDATE));
		this.mHelp.add(swUpdate);
		swUpdate.setActionCommand(Resurses.SW_UPDATE);
		swUpdate.addActionListener(this);

		final JMenuItem lic = new JMenuItem(Resurses.getString(Resurses.LICENSE));
		this.mHelp.add(lic);
		lic.setActionCommand(Resurses.LICENSE);
		lic.addActionListener(this);

		final JMenuItem wiki = new JMenuItem(Resurses.getString(Resurses.WIKI));
		this.mHelp.add(wiki);
		wiki.setActionCommand(Resurses.WIKI);
		wiki.addActionListener(this);

		this.mOwner = new JMenuItem(Resurses.getString("MENU_OWNER_INFO"));
		this.mHelp.add(this.mOwner);
		this.mOwner.setActionCommand("MENU_OWNER_INFO");
		this.mOwner.addActionListener(this);

		this.mHelp.addSeparator();
		this.mAbout = new JMenuItem(Resurses.getString(Resurses.ABOUT));
		this.mHelp.add(this.mAbout);
		this.mAbout.setActionCommand(Resurses.ABOUT);
		this.mAbout.addActionListener(this);

		popupListener = new PopupListener(this);
		final SukuPopupMenu pop = SukuPopupMenu.getInstance();
		pop.addActionListener(popupListener);

		crit = SearchCriteria.getCriteria(this);
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if (d.width > 1024) {
			d.width = 1024;
		}
		if (d.height > 600) {
			d.height = 600;
		}
		setLayout(null);
		setLocation(0, 0);
		setSize(d);

		if (os.toLowerCase().indexOf("windows") >= 0) {
			setExtendedState(MAXIMIZED_BOTH);
		} else {
			setExtendedState(NORMAL);
		}
		this.toolbar = new JToolBar("FinFamily tools");
		this.getContentPane().add(this.toolbar);
		this.toolbar.setBounds(0, 0, getWidth(), 50);
		this.toolbar.setFloatable(false);

		// first button
		try {

			tQueryButton = makeNavigationButton(Resurses.TOOLBAR_QUERY_IMAGE, Resurses.TOOLBAR_QUERY_ACTION,
					Resurses.getString("TOOLBAR.QUERY.TOOLTIP"), Resurses.getString("TOOLBAR.QUERY.ALTTEXT"));

			this.toolbar.add(tQueryButton);

			this.toolbar.addSeparator(new Dimension(20, 30));
			tSubjectButton = makeNavigationButton(Resurses.TOOLBAR_SUBJECT_DOWN_IMAGE,
					Resurses.TOOLBAR_SUBJECT_ON_IMAGE, Resurses.TOOLBAR_SUBJECT_DOWN_ACTION,
					Resurses.getString("TOOLBAR.SUBJECT.TOOLTIP"), Resurses.getString("TOOLBAR.SUBJECT.ALTTEXT"));
			this.toolbar.add(tSubjectButton);

			tSubjectPButton = makeNavigationButton(Resurses.TOOLBAR_SUBJECT_UP_IMAGE,
					Resurses.TOOLBAR_SUBJECT_UP_ACTION, Resurses.getString("TOOLBAR.SUBJECTP.TOOLTIP"),
					Resurses.getString("TOOLBAR.SUBJECTP.ALTTEXT"));
			tSubjectPButton.setEnabled(false);
			this.toolbar.add(tSubjectPButton);

			this.toolbar.addSeparator(new Dimension(20, 30));
			tPersonButton = makeNavigationButton(Resurses.TOOLBAR_PERSON_IMAGE, Resurses.TOOLBAR_NEWPERSON_ACTION,
					Resurses.getString("TOOLBAR.NEWPERSON.TOOLTIP"), Resurses.getString("TOOLBAR.NEWPERSON.ALTTEXT"));

			this.toolbar.add(tPersonButton);

			tHiskiPane = makeNavigationButton("hiski", Resurses.IMPORT_HISKI,
					Resurses.getString("TOOLBAR.HISKI.TOOLTIP"), Resurses.getString("TOOLBAR.HISKI.ALTTEXT"));
			this.toolbar.add(tHiskiPane);

			tmp = kontroller.getPref(this, Resurses.IMPORT_HISKI, "false");
			if (tmp.equals("true")) {
				tHiskiPane.setSelected(true);
			}

			tRemovePerson = makeNavigationButton(Resurses.TOOLBAR_REMPERSON_IMAGE, Resurses.TOOLBAR_REMPERSON_ACTION,
					Resurses.getString("TOOLBAR.REMPERSON.TOOLTIP"), Resurses.getString("TOOLBAR.REMPERSON.ALTTEXT"));

			this.toolbar.add(tRemovePerson);

			this.toolbar.addSeparator(new Dimension(20, 30));

			tMapButton = makeNavigationButton(Resurses.TOOLBAR_MAP_IMAGE, Resurses.TOOLBAR_MAP_ACTION,
					Resurses.getString("SHOWINMAP") + " " + Resurses.getString("SHOWINMAPBIRTH"),
					Resurses.getString("TOOLBAR.MAP.ALTTEXT"));

			this.toolbar.add(tMapButton);

			this.toolbar.addSeparator(new Dimension(20, 30));

			tNoticesButton = makeNavigationButton("Tietojaksot24", "Tietojaksot24_nega",
					Resurses.TOOLBAR_NOTICES_ACTION, Resurses.getString("TOOLBAR.NOTICES.TOOLTIP"),
					Resurses.getString("TOOLBAR.NOTICES.ALTTEXT"));

			tmp = kontroller.getPref(this, Resurses.NOTICES_BUTTON, "false");
			if (tmp.equals("true")) {
				tNoticesButton.setSelected(true);
			}
			this.toolbar.add(tNoticesButton);

			tNoteButton = makeNavigationButton("Teksti24", "Teksti24_nega", Resurses.TOOLBAR_NOTE_ACTION,
					Resurses.getString("TOOLBAR.NOTE.TOOLTIP"), Resurses.getString("TOOLBAR.NOTE.ALTTEXT"));
			tmp = kontroller.getPref(this, Resurses.TOOLBAR_NOTE_ACTION, "false");
			if (tmp.equals("true")) {
				tNoteButton.setSelected(true);
			}
			this.toolbar.add(tNoteButton);

			tAddressButton = makeNavigationButton("showAddress", "showAddress_nega", Resurses.TOOLBAR_ADDRESS_ACTION,
					Resurses.getString("TOOLBAR.ADDRESS.TOOLTIP"), Resurses.getString("TOOLBAR.ADDRESS.ALTTEXT"));
			tmp = kontroller.getPref(this, Resurses.TOOLBAR_ADDRESS_ACTION, "false");
			if (tmp.equals("true")) {
				tAddressButton.setSelected(true);
			}
			this.toolbar.add(tAddressButton);

			tFarmButton = makeNavigationButton("talo", "talo_nega", Resurses.TOOLBAR_FARM_ACTION,
					Resurses.getString("TOOLBAR.FARM.TOOLTIP"), Resurses.getString("TOOLBAR.FARM.ALTTEXT"));

			tmp = kontroller.getPref(this, Resurses.TOOLBAR_FARM_ACTION, "false");
			if (tmp.equals("true")) {
				tFarmButton.setSelected(true);
			}

			this.toolbar.add(tFarmButton);

			tImageButton = makeNavigationButton("kamera", "kamera_nega", Resurses.TOOLBAR_IMAGE_ACTION,
					Resurses.getString("TOOLBAR.IMAGE.TOOLTIP"), Resurses.getString("TOOLBAR.IMAGE.ALTTEXT"));
			tmp = kontroller.getPref(this, Resurses.TOOLBAR_IMAGE_ACTION, "false");
			if (tmp.equals("true")) {
				tImageButton.setSelected(true);
			}
			this.toolbar.add(tImageButton);

			tPrivateButton = makeNavigationButton("showPrivate", "showPrivate_nega", Resurses.TOOLBAR_PRIVATE_ACTION,
					Resurses.getString("TOOLBAR.PRIVATE.TOOLTIP"), Resurses.getString("TOOLBAR.PRIVATE.ALTTEXT"));
			tmp = kontroller.getPref(this, Resurses.TOOLBAR_PRIVATE_ACTION, "false");
			if (tmp.equals("true")) {
				tPrivateButton.setSelected(true);
			}
			this.toolbar.add(tPrivateButton);
			this.toolbar.addSeparator(new Dimension(20, 30));
			tAddNotice = makeNavigationButton(Resurses.TOOLBAR_ADDNOTICE_IMAGE, Resurses.TOOLBAR_ADDNOTICE_ACTION,
					Resurses.getString("TOOLBAR.ADDNOTICE.TOOLTIP"), Resurses.getString("TOOLBAR.ADDNOTICE.ALTTEXT"));

			this.toolbar.add(tAddNotice);

			this.toolbar.addSeparator(new Dimension(20, 30));
			tDeleteNotice = makeNavigationButton(Resurses.TOOLBAR_DELETENOTICE_IMAGE,
					Resurses.TOOLBAR_DELETENOTICE_ACTION, Resurses.getString("TOOLBAR.DELETENOTICE.TOOLTIP"),
					Resurses.getString("TOOLBAR.DELETENOTICE.ALTTEXT"));

			this.toolbar.add(tDeleteNotice);

		} catch (final IOException e2) {
			throw new SukuException("Failed to create toolbar", e2);
		}

		enableCommands();

		addComponentListener(this);

		initTable(crit);

		this.personView = new PersonView(this);

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, this.scrollPane, this.personView);

		getContentPane().add(this.splitPane);
		this.currentSize = new Dimension(getWidth() - (SPLITTER_HORIZ_MARGIN * 3), 400);
		int splitterValue = currentSize.width / 2;
		if (currentSize.width > 522) {
			splitterValue = currentSize.width - 522;
		}
		this.splitPane.setDividerLocation(splitterValue);

		this.splitPane.setBounds(10, 50, this.currentSize.width, this.currentSize.height);

		this.statusPanel = new JTextField("");
		this.getContentPane().add(this.statusPanel);
		this.statusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.statusPanel.setEditable(false);
		this.statusPanel.setBackground(Color.WHITE);
		this.statusPanel.setBounds(0, 420, 700, 20);
		this.setTitle(null);
		setVisible(true);

		if (os.toLowerCase().indexOf("windows") < 0) {
			//
			// this fixes on non windows versions problem with layout
			final javax.swing.Timer t = new javax.swing.Timer(4, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					myFrame.setExtendedState(MAXIMIZED_BOTH);
					myFrame.repaint();
				}
			});
			t.setRepeats(false);
			t.start();
		}

		final InputStream in = this.getClass().getResourceAsStream("/images/sukuicon.gif");

		BufferedImage icon;
		try {
			icon = ImageIO.read(in);
			setIconImage(icon);
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		if (openHiskiWhenReady) {
			importFromHiski(true);
		}
		logger.info("FinFamily [" + Resurses.getLanguage() + "] Version " + AntVersion.antVersion + " - Java Version: "
				+ System.getProperty("java.version") + " from " + System.getProperty("java.vendor") + " [" + os + "]");
		// if (!this.isWebApp){
		calcSize();
		connectDb();
		if (!kontroller.isWebStart()) {
			final File home = new File(".");
			statusPanel.setText(home.getAbsolutePath());
		}
		// }
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				e.getClass();

				personView.askAndClosePerson();
				disconnectDb();
				System.exit(0);
			}

		});

		if (!kontroller.isRemote()) {
			new VersionChecker(this);
		}
	}

	/**
	 * This can be used by some dialogs to attach it to the main program instead
	 * of null.
	 *
	 * @return the Suku instance
	 */
	public static JFrame getFrame() {
		return myFrame;
	}

	/**
	 * Report languages is a two dimensional array with 0 = lancode and 1 =
	 * langname.
	 *
	 * @return count of languages available
	 */
	public static int getRepoLanguageCount() {
		if (repoLangList == null) {
			return 0;
		}
		return repoLangList.length;
	}

	/**
	 * Report languages is a two dimensional array with.
	 *
	 * @param idx
	 *            index into report language list
	 * @param theCode
	 *            true = lancode and false = language name
	 * @return the tag or the name of the requested text
	 */
	public static String getRepoLanguage(int idx, boolean theCode) {
		if ((repoLangList == null) || (idx >= repoLangList.length)) {
			return null;
		}
		final String[] tmp = repoLangList[idx].split(";");
		if (tmp.length != 2) {
			System.out.println("kiinni");
		}
		if (theCode) {
			return tmp[0];
		}
		return tmp[1];
	}

	/**
	 * Get the index into the report language list for specified language.
	 *
	 * @param langCode
	 *            the lang code
	 * @return index to language list
	 */
	public static int getRepoLanguageIndex(String langCode) {
		for (int i = 0; i < repoLangList.length; i++) {
			final String[] tmp = repoLangList[i].split(";");
			if (langCode.equals(tmp[0])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * get absolute rectangle of current database window.
	 *
	 * @return the Rectangle in absolute coordinates
	 */
	public Rectangle getDbWindow() {

		final Rectangle r = scrollPane.getBounds();
		final Point pt = new Point(r.x, r.y);
		SwingUtilities.convertPointToScreen(pt, scrollPane);
		r.x = pt.x;
		r.y = pt.y;

		return r;
	}

	private void initTable(SearchCriteria crit) throws SukuException {
		this.tableModel = new SukuModel(this);
		this.tableMap = new HashMap<Integer, PersonShortData>();

		// TableModel myModel = createMyTableModel();
		// JTable table = new JTable(myModel);
		// table.setRowSorter(new TableRowSorter(myModel));

		this.table = new DbTable(this.tableModel) {
			private static final long serialVersionUID = 1L;

			// Implement table header tool tips.
			@Override
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(this.columnModel) {

					private static final long serialVersionUID = 1L;

					@Override
					public String getToolTipText(MouseEvent e) {

						final java.awt.Point p = e.getPoint();
						final int index = this.columnModel.getColumnIndexAtX(p.x);
						// System.out.print("COLUMNINDEX: [" + index);
						final int someIndex = table.convertColumnIndexToModel(index);

						// int realIndex = this.columnModel.getColumn(index)
						// .getModelIndex();
						// System.out.print(":" + realIndex + ":");
						// int someIndex = Suku.crit.getColAbsIndex(realIndex);
						// System.out.println(":" + someIndex + "]");
						return Suku.this.tableModel.getColumnName(someIndex);
						// int realIndex = this.columnModel.getColumn(index)
						// .getModelIndex();
						// return Suku.this.tableModel.getColumnName(realIndex);
					}
				};
			}

			// Implement table cell tool tips.
			@Override
			public String getToolTipText(MouseEvent e) {
				// System.out.println("TTT: " + e);
				// String tip = null;
				// java.awt.Point p = e.getPoint();
				// int rowIndex = rowAtPoint(p);
				// int colIndex = columnAtPoint(p);
				// int realColumnIndex = convertColumnIndexToModel(colIndex);

				final int yy = e.getY();
				final int rh = Suku.this.table.getRowHeight();
				final int ii = yy / rh;
				final SukuRow row = (SukuRow) Suku.this.tableModel.getValueAt(ii, SukuModel.SUKU_ROW);
				if (row == null) {
					return null;
				}
				return row.getUnkn();
			}
		};
		table.getTableHeader().setReorderingAllowed(false);
		final String databaseViewFontSize = Suku.kontroller.getPref(this, "DB_VIEW_FONTSIZE", "11");
		int dbFont = 10;

		try {
			dbFont = Integer.parseInt(databaseViewFontSize);
		} catch (final NumberFormatException ne) {
			// NumberFormatException ignored
		}
		final Font fff = table.getFont();
		final Font ff = new Font(fff.getName(), Font.PLAIN, dbFont);
		this.table.setFont(ff);
		this.table.setRowSorter(new TableRowSorter<SukuModel>(this.tableModel));

		this.table.setDragEnabled(true);
		final TransferHandler newHandler = new SukuTransferHandler();

		this.table.setTransferHandler(newHandler);

		initSorter(crit);

		final TableColumnModel tc = this.table.getColumnModel();
		TableColumn cc;
		for (int k = crit.getColTableCount() - 1; k >= 0; k--) {
			cc = tc.getColumn(k);
			// if (k == 1) {
			//
			// cc.setMinWidth(100);
			// }
			final String colid = crit.getColTable(k).getColName();
			if (colid.equals(Resurses.COLUMN_T_NAME)) {
				cc.setMinWidth(120);
			}
			final boolean bb = Utils.getBooleanPref(crit, colid, true);
			if (!bb) {
				crit.getColTable(k).setCurrentState(false);
				tc.removeColumn(cc);
			} else {
				if (colid.equals(Resurses.COLUMN_T_ISCHILD) || colid.equals(Resurses.COLUMN_T_ISMARR)
						|| colid.equals(Resurses.COLUMN_T_ISPARE) || colid.equals(Resurses.COLUMN_T_UNKN)
						|| colid.equals(Resurses.COLUMN_T_SEX)) {
					cc.setMaxWidth(35);
				}
				if ((!Resurses.getDateFormat().equals("SE")
						&& (colid.equals(Resurses.COLUMN_T_BIRT) || colid.equals(Resurses.COLUMN_T_DEAT)))
						|| colid.equals(Resurses.COLUMN_T_PID)) {
					cc.setCellRenderer(new RightTableCellRenderer());
				}
			}
		}

		this.scrollPane = new JScrollPane(this.table);
		this.scrollPane.setMinimumSize(new Dimension(0, 0));
		this.getContentPane().add(this.scrollPane);

		this.table.addMouseListener(popupListener);

		this.table.addMouseListener(this);

	}

	/**
	 * Convert to view.
	 *
	 * @param viewIdx
	 *            the view idx
	 * @return the int
	 */
	public int convertToView(int viewIdx) {
		return table.convertColumnIndexToView(viewIdx);
	}

	@SuppressWarnings("unchecked")
	private void initSorter(SearchCriteria crit) {
		final TableRowSorter<SukuModel> sorter = (TableRowSorter<SukuModel>) this.table.getRowSorter();

		int i;
		int curre = 0;

		@SuppressWarnings("rawtypes")
		Comparator sukucompa;
		for (i = 0; i < crit.getColTableCount(); i++) {
			final ColTable col = crit.getColTable(i);
			if (col.getCurrentState()) {
				curre = i;
				if (col.getColName().equals(Resurses.COLUMN_T_NAME)) {
					sukucompa = new SukuNameComparator(Resurses.getLanguage());
					sorter.setComparator(curre, sukucompa);

				} else if (col.getColName().equals(Resurses.COLUMN_T_PID)) {
					sukucompa = new SukuPidComparator();
					sorter.setComparator(curre, sukucompa);
					// this.table.setRowSorter(sorter);
				} else if (col.getColName().equals(Resurses.COLUMN_T_ISMARR)
						|| col.getColName().equals(Resurses.COLUMN_T_ISCHILD)
						|| col.getColName().equals(Resurses.COLUMN_T_ISPARE)) {
					sukucompa = new SukuNumStringComparator();
					sorter.setComparator(curre, sukucompa);
					// this.table.setRowSorter(sorter);
				} else if (col.getColName().equals(Resurses.COLUMN_T_BIRT)
						|| col.getColName().equals(Resurses.COLUMN_T_DEAT)) {
					sukucompa = new SukuDateComparator();
					sorter.setComparator(curre, sukucompa);
					// this.table.setRowSorter(sorter);
				} else {
					sukucompa = new SukuStringComparator();
					sorter.setComparator(curre, sukucompa);
					// this.table.setRowSorter(sorter);

				}
				// curre++;
			}
		}
		this.table.setRowSorter(sorter);

	}

	/**
	 * Gets the kontroller.
	 *
	 * @return the kontroller instance
	 */
	public static SukuKontroller getKontroller() {
		return kontroller;
	}

	/**
	 * Sets the status.
	 *
	 * @param text
	 *            the new status
	 */
	public void setStatus(String text) {
		this.statusPanel.setText(text);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.Frame#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		SukuData dat;
		String schema = null;

		final StringBuilder sb = new StringBuilder();
		sb.append(Resurses.getString(Resurses.SUKU));

		if (Suku.kontroller.isRemote()) {
			sb.append("-");
			if (Suku.kontroller.isWebStart()) {
				sb.append(Resurses.getString("WEBSTART_NAME"));
			} else {
				sb.append(url);
			}
		}

		if (kontroller.isConnected()) {

			try {

				dat = kontroller.getSukuData("cmd=schema", "type=get");
				if (dat.generalArray != null) {
					schema = dat.generalArray.length == 1 ? dat.generalArray[0] : null;
				}

			} catch (final SukuException e) {

				e.printStackTrace();
				return;
			}

			sb.append(" [");
			if (Suku.kontroller.isRemote()) {
				sb.append(" ! ");
				sb.append(schema);
			} else {

				sb.append(databaseName);
				if (schema != null) {
					sb.append(" ! ");
					sb.append(schema);
				}
			}
			sb.append("]");
		}
		if (title != null) {
			sb.append(" - ");
			sb.append(title);
		}
		this.statusPanel.setText("");
		super.setTitle(sb.toString());
		if (title == null) {
			try {
				this.personView.setSubjectForFamily(0);
				personView.setTextForPerson(null);
			} catch (final SukuException e) {
				logger.log(Level.WARNING, "resetting family tree", e);

			}
		}

	}

	/**
	 * Make navigation button.
	 *
	 * @param imageName
	 *            the image name
	 * @param selectedName
	 *            the selected name
	 * @param actionCommand
	 *            the action command
	 * @param toolTipText
	 *            the tool tip text
	 * @param altText
	 *            the alt text
	 * @return the j button
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected JButton makeNavigationButton(String imageName, String selectedName, String actionCommand,
			String toolTipText, String altText) throws IOException {
		// Look for the image.
		final String imgLocation = "/images/" + imageName + ".gif";
		final String selectedLocation = "/images/" + selectedName + ".gif";
		ImageIcon icon = null;
		ImageIcon selectedIcon = null;
		// System.out.println("NAV1: " + imageName);
		final byte imbytes[] = new byte[8192];

		InputStream in = null;
		int imsize;
		try {
			in = this.getClass().getResourceAsStream(imgLocation);
			// System.out.println("NAV2: " + imageName + ":"+in);
			imsize = in.read(imbytes);
			if (imsize < imbytes.length) {
				icon = new ImageIcon(imbytes, altText);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException ignored) {
					// IOException ignored
				}
			}

		}

		in = null;
		try {
			in = this.getClass().getResourceAsStream(selectedLocation);
			// System.out.println("NAV2: " + imageName + ":"+in);
			imsize = in.read(imbytes);
			if (imsize < imbytes.length) {
				selectedIcon = new ImageIcon(imbytes, altText);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException ignored) {
					// IOException ignored
				}
			}

		}

		// Create and initialize the button.
		final JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		if (selectedIcon != null) {
			button.setSelectedIcon(selectedIcon);
		}

		if (icon != null) { // image found
			button.setIcon(icon); // new ImageIcon(imbytes, altText));
		} else { // no image found
			button.setText(altText);
			logger.info("Resource not found: " + imgLocation);
		}

		return button;
	}

	/**
	 * Make navigation button.
	 *
	 * @param imageName
	 *            the image name
	 * @param actionCommand
	 *            the action command
	 * @param toolTipText
	 *            the tool tip text
	 * @param altText
	 *            the alt text
	 * @return the j button
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, String altText)
			throws IOException {
		// Look for the image.
		final String imgLocation = "/images/" + imageName + ".gif";
		// String negaLocation = "/images/" + imageName + "nega.gif";
		ImageIcon icon = null;
		// ImageIcon selectedIcon=null;
		// System.out.println("NAV1: " + imageName );
		final byte imbytes[] = new byte[8192];

		InputStream in = null;
		try {
			in = this.getClass().getResourceAsStream(imgLocation);
			// System.out.println("NAV2: " + imageName + ":"+in);
			final int imsize = in.read(imbytes);
			if (imsize < imbytes.length) {
				icon = new ImageIcon(imbytes, altText);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException ignored) {
					// IOException ignored
				}
			}

		}

		// Create and initialize the button.
		final JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		// if (selectedIcon != null) {
		// button.setSelectedIcon(selectedIcon);
		// }

		if (icon != null) { // image found
			button.setIcon(icon); // new ImageIcon(imbytes, altText));
		} else { // no image found
			button.setText(altText);
			logger.info("Resource not found: " + imgLocation);
		}

		return button;
	}

	private PersonShortData getJoinPerson() throws SukuException {
		if (joinPersonPid == 0) {
			return null;
		}

		final SukuData res = Suku.kontroller.getSukuData("cmd=person", "mode=short", "pid=" + joinPersonPid);

		if ((res.pers != null) && (res.pers.length == 1)) {
			return res.pers[0];
		}
		return null;

	}

	private void connectDb() {
		sukuObject = null;
		joinPersonPid = 0;
		final SukuPopupMenu pop = SukuPopupMenu.getInstance();
		pop.enableJoinAdd(null);

		final ConnectDialog cdlg = new ConnectDialog(this, kontroller);
		final boolean hasMemory = cdlg.hasDatabase();
		if (kontroller.isConnected() && hasMemory) {
			this.tableModel.resetModel(); // clear contents of table first
			table.getRowSorter().modelStructureChanged();
			this.table.clearSelection();
			// this.personView.reset();
			this.tableMap.clear();
			this.table.updateUI();
			this.scrollPane.updateUI();
			disconnectDb();
		}

		if ((cdlg.getPassword() == null) || !cdlg.hasDatabase()) {

			cdlg.setVisible(true);
			if (!cdlg.wasOk()) {
				return;
			}
		}
		// if (cdlg.wasOk()) {
		String name = cdlg.getHost();
		databaseName = cdlg.getDbName();
		String userid = cdlg.getUserId();
		String password = cdlg.getPassword();
		isH2 = cdlg.isH2();

		if (isH2) {
			userid = "";
			password = "";
			name = "";
			kontroller.putPref(this, "DBNAME", databaseName + ".mv.db");
			kontroller.setDBType(true);
		} else {
			kontroller.setDBType(false);
		}

		try {
			if (kontroller.isRemote()) {
				if ("demo".equals(userid) && "demo".equals(password)) {
					kontroller.getConnection(name, databaseName, userid, password, isH2);
				} else {
					throw new SukuException("UNKNOWN USER/PASSWORD");
				}
			} else {
				kontroller.getConnection(name, databaseName, userid, password, isH2);
				if (isH2) {
					if (cdlg.hasInit()) {
						this.newDatabaseInit();
					}
				}
				cdlg.rememberDatabase(true);
			}
		} catch (final SukuException e3) {
			final String e1 = e3.getMessage();
			String[] e2 = { "Connection failed" };
			if (e1 != null) {
				e2 = e1.split("\n");
			}

			cdlg.rememberDatabase(false);

			JOptionPane.showMessageDialog(this, e2[0], Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			this.statusPanel.setText(e2[0]);
			e3.printStackTrace();

			enableCommands();
			return;
		}
		try {
			String schema = null;
			if (isH2) {
				final SelectSchema schemas = new SelectSchema(this, databaseName, false);

				schema = schemas.getSchema(isH2);
			} else {
				if (!kontroller.isRemote()) {

					final SelectSchema schemas = new SelectSchema(this, databaseName, false);

					schema = schemas.getSchema(isH2);

					if ((schema == null) || schema.isEmpty()) {
						schemas.setVisible(true);

					}
					schema = schemas.getSchema(isH2);
					if (schema == null) {
						cdlg.rememberDatabase(false);
						return;
					}
					final SukuData dblist = kontroller.getSukuData("cmd=dblista");

					if (dblist.generalArray != null) {
						final StringBuilder sb = new StringBuilder();
						sb.append(databaseName);
						for (int i = 0; i < dblist.generalArray.length; i++) {
							if (!dblist.generalArray[i].equalsIgnoreCase(databaseName)) {
								sb.append(";");
								sb.append(dblist.generalArray[i]);
							}
						}

						kontroller.putPref(cdlg, "DBNAMES", sb.toString());
					}
					schema = schemas.getSchema(isH2);
					if ((schema == null) || schema.isEmpty()) {

						enableCommands();
						cdlg.rememberDatabase(false);
						return;
					}
					kontroller.getSukuData("cmd=schema", "type=set", "name=" + schema);
				}
			}

			repoLangList = ExcelBundle.getLangList();

			final SukuData resp = Suku.kontroller.getSukuData("cmd=getsettings", "type=needle", "name=needle");
			needle.clear();
			if (resp.generalArray != null) {

				for (final String element : resp.generalArray) {
					needle.add(element);
				}
				if (resp.generalArray.length > 0) {
					tSubjectPButton.setEnabled(true);
				}
			}

			final long startOfIntelli = System.currentTimeMillis();

			resetIntellisens();
			final long endOfIntelli = System.currentTimeMillis();
			final long timeOfIntelli = (endOfIntelli - startOfIntelli) / 1000;
			postServerVersion += ", Intellisens [" + timeOfIntelli + "] secs";
			kontroller.setSchema(schema);

			enableCommands();
			setTitle(null);
			final SukuData serverVersion = kontroller.getSukuData("cmd=dbversion");

			if ((serverVersion.generalArray != null) && (serverVersion.generalArray.length > 0)) {
				postServerVersion = serverVersion.generalArray[0];
				postServerVersion += " " + serverVersion.generalArray[1];
			}

			// if (dat != null && dat.generalArray != null
			// && dat.generalArray.length > 1) {
			// sens.setPaikat(dat.generalArray);
			// }

			return;

		} catch (final SukuException e) {
			final String e1 = e.getMessage();
			String[] e2 = { "Connection failed" };
			if (e1 != null) {
				e2 = e1.split("\n");
			}

			kontroller.setSchema(null);
			setTitle(null);
			this.statusPanel.setText(e2[0]);
			e.printStackTrace();

			enableCommands();

		}
		// }

	}

	private void resetIntellisens() throws SukuException {
		final SukuData dat = Suku.kontroller.getSukuData("cmd=intelli");

		final SukuSenser sens = SukuSenser.getInstance();

		if ((dat != null) && (dat.vvTexts != null) && (dat.vvTexts.size() > 6)) {
			sens.setPlaces(dat.vvTexts.get(0));
			sens.setGivennames(dat.vvTexts.get(1));
			sens.setPatronymes(dat.vvTexts.get(2));
			sens.setSurnames(dat.vvTexts.get(3));
			sens.setDescriptions(dat.vvTexts.get(4));
			sens.setNoticeTypes(dat.vvTexts.get(5));
			sens.setGroups(dat.vvTexts.get(6));

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

		final String cmd = e.getActionCommand();
		try {
			if (cmd.equals(Resurses.ABOUT)) {

				final AboutDialog about = new AboutDialog(this);
				about.setVisible(true);
				return;
			}

			if (cmd.equals(Resurses.SW_UPDATE)) {
				final String updateSite = "https://sourceforge.net/projects/finfamily/";
				Utils.openExternalFile(updateSite);
				setStatus(updateSite);
				return;
			}
			if (cmd.equals(Resurses.LICENSE)) {
				final LicenseDialog license = new LicenseDialog(this);
				license.setVisible(true);
				return;
			}
			if (cmd.equals(Resurses.WIKI)) {
				final String updateSite = Resurses.getString("WIKI_URL");
				Utils.openExternalFile(updateSite);
				setStatus(updateSite);
				return;
			}
			if (cmd.equals("GRAPHVIZ")) {
				final String exeTask = kontroller.getPref(this, "GRAPHVIZ", "");
				if (!"".equals(exeTask)) {
					if (!kontroller.openFile("txt;gv")) {
						return;
					}
					final String infile = kontroller.getFilePath();
					if (!kontroller.createLocalFile("jpg;png;svg")) {
						return;
					}
					final String endi = kontroller.getFilePath();

					Utils.graphvizDo(this, exeTask, infile, endi);

				}
			}
			if (cmd.equals("MENU_TOOLS_STAT")) {
				displayGenStats();

			}
			if (cmd.equals("MENU_TOOLS_SQL")) {

				final SqlCommandDialog sql = new SqlCommandDialog(this);
				sql.setVisible(true);

				// if (viewWin == null) {
				// viewWin = new ViewMgrWindow(this);
				// viewWin.setVisible(true);
				// } else {
				// viewWin.initViewlist();
				// viewWin.setVisible(true);
				// }

			}
			if (cmd.equals(Resurses.updateDb)) {
				SukuData resp = null;
				if (isH2) {
					resp = kontroller.getSukuData("cmd=initdb", "path=/sql/dbupdatesH2.sql");
				} else {
					resp = kontroller.getSukuData("cmd=initdb", "path=/sql/dbupdates.sql");
				}
				String resu = "OK";
				if (resp.resu != null) {
					resu = resp.resu;
				} else {
					kontroller.getSukuData("cmd=excel", "page=coordinates");
					kontroller.getSukuData("cmd=excel", "page=types");

				}

				JOptionPane.showMessageDialog(this, resu, Resurses.getString(Resurses.SUKU),
						JOptionPane.INFORMATION_MESSAGE);
				disconnectDb();
			} else if (cmd.equals("SCHEMA_INITIALIZE")) {
				if (isH2) {
					newDatabaseInit();
				} else {
					String selectedSchema = null;
					if (!kontroller.isRemote()) {

						try {
							SelectSchema schema = null;
							schema = new SelectSchema(this, databaseName);

							schema.setVisible(true);

							selectedSchema = schema.getSchema(isH2);
							if (selectedSchema == null) {
								return;
							}
							if (!schema.isExistingSchema(isH2)) {
								kontroller.getSukuData("cmd=schema", "type=create", "name=" + selectedSchema);

							}
							kontroller.getSukuData("cmd=schema", "type=set", "name=" + selectedSchema);
							setTitle(null);
						} catch (final SukuException e1) {

							e1.printStackTrace();
							JOptionPane.showMessageDialog(this, e1.getMessage(), Resurses.getString(Resurses.SUKU),
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					final SukuData resp = Suku.kontroller.getSukuData("cmd=unitCount");
					if (resp.resuCount > 0) {
						// if (schema.isExistingSchema()) {

						final int resu = JOptionPane.showConfirmDialog(this, Resurses.getString("CONFIRM_NEWDB"),
								Resurses.getString(Resurses.SUKU), JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (resu != JOptionPane.YES_OPTION) {
							return;
						}
					}
					try {
						this.tableModel.resetModel(); // clear contents of table
						table.getRowSorter().modelStructureChanged();
						// first
						this.personView.reset();
						this.table.updateUI();
						this.scrollPane.updateUI();

						kontroller.getSukuData("cmd=initdb");

						kontroller.getSukuData("cmd=excel", "page=coordinates");
						kontroller.getSukuData("cmd=excel", "page=types");

						JOptionPane.showMessageDialog(this, Resurses.getString("CREATED_NEWDB"),
								Resurses.getString(Resurses.SUKU), JOptionPane.INFORMATION_MESSAGE);
						resetIntellisens();
						kontroller.setSchema(selectedSchema);

						enableCommands();
						setTitle(null);
					} catch (final SukuException e1) {
						JOptionPane.showMessageDialog(this, Resurses.getString(Resurses.NEWDB),
								Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
						logger.log(Level.WARNING, Resurses.getString(Resurses.NEWDB), e1);

					}
				}
			} else if (cmd.equals("SCHEMA_DROP")) {
				final SukuData scm = kontroller.getSukuData("cmd=schema", "type=get");
				if (scm.generalArray[0].equals("public")) {
					JOptionPane.showMessageDialog(this, Resurses.getString("SCHEMA_PUBLIC_NOT_DROPPED"),
							Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
					return;
				}

				final SukuData resp = Suku.kontroller.getSukuData("cmd=unitCount");
				if (resp.resuCount > 0) {
					final int answer = JOptionPane.showConfirmDialog(this,
							Resurses.getString("SCHEMA_NOT_EMPTY") + " [" + scm.generalArray[0] + "] "
									+ Resurses.getString("SCHEMA_PERSONCOUNT") + "= " + resp.resuCount + " "
									+ Resurses.getString("DELETE_DATA_OK"),
							Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
					if (answer == 1) {
						throw new SukuException(Resurses.getString("SCHEMA_NOT_EMPTY"));
					}
				}
				kontroller.getSukuData("cmd=schema", "type=drop", "name=" + scm.generalArray[0]);
				disconnectDb();

			} else if (cmd.equals("MENU_NOTICES_ORDER")) {
				executeDbWork();
			} else if (cmd.equals("MENU_CHILDREN_ORDER")) {
				executeOrderChildren();
			} else if (cmd.equals("MENU_OWNER_INFO")) {
				showOwnerInformation();
			} else if (cmd.equals("MENU_TOOLS_LIST_DATABASES")) {
				listDatabaseStatistics();
			} else if (cmd.equals("MENU_COPY")) {
				Utils.println(this, "EDIT-COPY by ctrl/c");
			} else if (cmd.equals(Resurses.TOOLBAR_REMPERSON_ACTION)) {

				final int[] selection = table.getSelectedRows();
				for (int i = 0; i < selection.length; i++) {
					selection[i] = table.convertRowIndexToModel(selection[i]);
				}
				if (selection.length < 1) {
					return;
				}
				// int isele = table.getSelectedRow();
				final int isele = selection[0];
				if (isele < 0) {
					JOptionPane.showMessageDialog(this, Resurses.getString("MESSAGE_NO_PERSON_TO_DELETE"),
							Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
					return;

				}

				final SukuRow row = (SukuRow) tableModel.getValueAt(isele, SukuModel.SUKU_ROW);

				final PersonShortData p = tableMap.get(row.getPid());

				final int resu = JOptionPane.showConfirmDialog(this,
						Resurses.getString("CONFIRM_DELETE") + " " + p.getAlfaName(), Resurses.getString(Resurses.SUKU),
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (resu == JOptionPane.YES_OPTION) {

					try {
						final SukuData result = kontroller.getSukuData("cmd=delete", "pid=" + p.getPid());
						if (result.resu != null) {
							JOptionPane.showMessageDialog(this, result.resu, Resurses.getString(Resurses.SUKU),
									JOptionPane.ERROR_MESSAGE);
							logger.log(Level.WARNING, result.resu);
						}

						final int mainpaneidx = personView.getMainPaneIndex();
						if (mainpaneidx > 1) {
							final SukuTabPane pane = personView.getPane(mainpaneidx);
							if (p.getPid() == pane.getPid()) {
								personView.closeMainPane(false);
							} else {
								personView.refreshRelativesPane();

							}
						}

						for (int i = 0; i < needle.size(); i++) {
							final String[] dbl = needle.get(i).split(";");
							final int dblid = Integer.parseInt(dbl[0]);
							if (p.getPid() == dblid) {
								needle.remove(i);
								break;
							}
						}
						tSubjectPButton.setEnabled(needle.size() > 0);

						tableModel.removeRow(isele);
						table.getRowSorter().modelStructureChanged();
						table.updateUI();
						scrollPane.updateUI();

					} catch (final SukuException e1) {
						JOptionPane.showMessageDialog(this, e1.getMessage(), Resurses.getString(Resurses.SUKU),
								JOptionPane.ERROR_MESSAGE);
						logger.log(Level.WARNING, e1.getMessage(), e1);
						e1.printStackTrace();
					}
				}
			} else if (cmd.equals(Resurses.CONNECT)) {
				connectDb();
			} else if (cmd.equals(Resurses.DISCONNECT)) {
				disconnectDb();
			} else if (cmd.equals(Resurses.IMPORT_OTHER)) {
				importOther();
			} else if (cmd.equals(Resurses.IMPORT_SUKU)) {
				importSuku2004Backup();

				enableCommands();
			} else if (cmd.equals(Resurses.MENU_OPEN_PERSON)) {
				final String textPid = JOptionPane.showInputDialog(this, Resurses.getString("DIALOG_GIVE_PID"));
				// .showMessageDialog(this, Resurses
				// .getString(Resurses.PGSQL_STOP)
				// + ":" + "OK");
				if (textPid != null) {
					try {
						final int pid = Integer.parseInt(textPid);

						final SukuData res = Suku.kontroller.getSukuData("cmd=person", "pid=" + pid);
						if (res.pers != null) {
							showPerson(pid);
						} else {
							JOptionPane.showMessageDialog(this,
									Resurses.getString("DIALOG_PID_NOT_EXISTS") + " [" + pid + "]");
						}
					} catch (final NumberFormatException ne) {
						JOptionPane.showMessageDialog(this, Resurses.getString("DIALOG_BAD_PID"));
					}

				}
			} else if (cmd.equals("MENU_TOOLS_LOAD_COORDINATES")) {
				importDefaultCoordinates();
			} else if (cmd.equals("MENU_TOOLS_LOAD_CONVERSIONS")) {
				importConversions();
			} else if (cmd.equals("MENU_TOOLS_STORE_CONVERSIONS")) {
				exportConversions(false);
			} else if (cmd.equals("MENU_TOOLS_STORE_ALL_CONVERSIONS")) {
				exportConversions(true);
			} else if (cmd.equals("MENU_TOOLS_LOAD_TYPES")) {
				importDefaultTypes();
			} else if (cmd.equals(Resurses.IMPORT_GEDCOM)) {
				importGedcom();

				enableCommands();
			} else if (cmd.equals(Resurses.EXPORT_GEDCOM)) {
				exportGedcom();
			} else if (cmd.equals(Resurses.EXPORT_BACKUP)) {
				createFamilyBackup();
			} else if (cmd.equals("MENU_TOOLS_GROUP_MGR")) {
				openGroupWin();
			} else if (cmd.equals("MENU_TOOLS_VIEW_MGR")) {
				openViewWin();
			} else if (cmd.equals(Resurses.ADMIN)) {
				adminDb();
			} else if (cmd.equals(Resurses.EXIT)) {
				System.exit(0);
			} else if (cmd.equals(Resurses.SETTINGS)) {

				final int midx = personView.getMainPaneIndex();
				if (midx > 0) {
					final SukuTabPane tp = personView.getPane(midx);
					if (tp != null) {
						personView.askAndClosePerson();
					}
				}
				final SettingsDialog sets = new SettingsDialog(this);
				sets.setVisible(true);

			} else if (cmd.equals(Resurses.PRINT_PERSON)) {

				this.personView.testMe();

			} else if (cmd.equals(Resurses.MENU_LISTA)) {

				final ReportWorkerDialog dlg = new ReportWorkerDialog(this, kontroller, null);
				dlg.setVisible(true);
				for (final String repo : dlg.getReportVector()) {
					Utils.openExternalFile(repo);
				}

			} else if (cmd.startsWith("SHOWINMAP")) {
				displayMap(cmd);
			} else if (cmd.equals(Resurses.QUERY)) {
				queryDb();
			} else if (cmd.equals(Resurses.TOOLBAR_NEWPERSON_ACTION)) {

				if (!tSubjectButton.isSelected() && (activePersonPid > 0)) {

					tSubjectButton.setSelected(true);
					final PersonShortData pp = tableMap.get(activePersonPid);
					if (pp != null) {
						addToNeedle(pp);
					}
				}

				activePersonPid = 0;
				showPerson(activePersonPid);
			} else if (cmd.equals(Resurses.IMPORT_HISKI)) {
				boolean isDown = true;
				if (e.getSource() == tHiskiPane) {
					final boolean theButt = !tHiskiPane.isSelected();
					isDown = theButt;
				} else {
					final boolean theMenu = mImportHiski.isSelected();
					isDown = theMenu;
				}
				mImportHiski.setSelected(isDown);
				tHiskiPane.setSelected(isDown);
				kontroller.putPref(this, Resurses.IMPORT_HISKI, "" + isDown);
				importFromHiski(isDown);
			} else if (cmd.equals(Resurses.TOOLBAR_NOTE_ACTION)) {
				boolean isDown = true;
				if (e.getSource() == tNoteButton) {
					final boolean theButt = !tNoteButton.isSelected();
					isDown = theButt;
				} else {
					final boolean theMenu = mShowNote.isSelected();
					isDown = theMenu;
				}
				mShowNote.setSelected(isDown);
				tNoteButton.setSelected(isDown);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.TOOLBAR_NOTE_ACTION, "" + isDown);
			} else if (cmd.equals(Resurses.TOOLBAR_ADDRESS_ACTION)) {
				boolean isDown = true;
				if (e.getSource() == tAddressButton) {
					final boolean theButt = !tAddressButton.isSelected();
					isDown = theButt;
				} else {
					final boolean theMenu = mShowAddress.isSelected();
					isDown = theMenu;
				}
				mShowAddress.setSelected(isDown);
				tAddressButton.setSelected(isDown);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.TOOLBAR_ADDRESS_ACTION, "" + isDown);
			} else if (cmd.equals(Resurses.TOOLBAR_FARM_ACTION)) {
				boolean isDown = true;
				if (e.getSource() == tFarmButton) {
					final boolean theButt = !tFarmButton.isSelected();
					isDown = theButt;
				} else {
					final boolean theMenu = mShowFarm.isSelected();
					isDown = theMenu;
				}
				mShowFarm.setSelected(isDown);
				tFarmButton.setSelected(isDown);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.TOOLBAR_FARM_ACTION, "" + isDown);
			} else if (cmd.equals(Resurses.TOOLBAR_IMAGE_ACTION)) {
				boolean isDown = true;
				if (e.getSource() == tImageButton) {
					final boolean theButt = !tImageButton.isSelected();
					isDown = theButt;
				} else {
					final boolean theMenu = mShowImage.isSelected();
					isDown = theMenu;
				}
				mShowImage.setSelected(isDown);
				tImageButton.setSelected(isDown);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.TOOLBAR_IMAGE_ACTION, "" + isDown);
			} else if (cmd.equals(Resurses.TOOLBAR_PRIVATE_ACTION)) {
				boolean isDown = true;
				if (e.getSource() == tPrivateButton) {
					final boolean theButt = !tPrivateButton.isSelected();
					isDown = theButt;
				} else {
					final boolean theMenu = mShowPrivate.isSelected();
					isDown = theMenu;
				}
				mShowPrivate.setSelected(isDown);
				tPrivateButton.setSelected(isDown);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.TOOLBAR_PRIVATE_ACTION, "" + isDown);
			} else if (cmd.equals(Resurses.TOOLBAR_SUBJECT_DOWN_ACTION)) {

				if (activePersonPid > 0) {

					final PersonShortData pp = tableMap.get(activePersonPid);
					if (pp != null) {

						addToNeedle(pp);
					}
				}

			} else if (cmd.equals(Resurses.TOOLBAR_SUBJECT_UP_ACTION)) {
				if (needle.size() > 0) {
					final ArrayList<String> subvec = new ArrayList<String>();
					// String[] subjes = null;
					final HashMap<String, String> submap = new HashMap<String, String>();
					// subjes = new String[needle.size()];
					// for (int i = 0; i < needle.size(); i++) {
					int i = 0;
					while (needle.size() > i) {
						final String dbx = needle.get(i);
						final String dbs[] = dbx.split(";");

						if (submap.put(dbx, dbx) == null) {
							subvec.add(dbs[1]);
							i++;
						} else {

							needle.remove(i);
						}

						if (subvec.size() > needleSize) {
							break;
						}

					}

					submap.clear();

					final String[] subjes = subvec.toArray(new String[0]);
					final Object par = JOptionPane.showInputDialog(personView, Resurses.getString("SELECT_PERSON")

					, Resurses.getString(Resurses.SUKU), JOptionPane.QUESTION_MESSAGE, null, subjes, subjes[0]);

					if (par != null) {
						int subrow = -1;

						for (int j = 0; j < subjes.length; j++) {

							if (par == subjes[j]) {
								subrow = j;
								break;
							}

						}
						if (subrow >= 0) {

							final String[] dbl = needle.get(subrow).split(";");
							final int pid = Integer.parseInt(dbl[0]);
							final SukuData res = Suku.kontroller.getSukuData("cmd=person", "pid=" + pid);
							if (res.pers != null) {
								showPerson(pid);

								needle.add(0, dbl[0] + ";" + dbl[1]);
							} else {
								JOptionPane.showMessageDialog(this,
										Resurses.getString("DIALOG_PID_NOT_EXISTS") + " [" + pid + "]");
								needle.remove(subrow);
							}

						}
					}
				}
			} else if (cmd.equals(Resurses.TOOLBAR_NOTICES_ACTION)) {
				boolean isDown = true;
				if (e.getSource() == tNoticesButton) {
					final boolean theButt = !tNoticesButton.isSelected();
					isDown = theButt;
				} else {
					final boolean theMenu = mShowNotices.isSelected();
					isDown = theMenu;
				}
				mShowNotices.setSelected(isDown);
				tNoticesButton.setSelected(isDown);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.NOTICES_BUTTON, "" + isDown);
				personView.showNotices(tNoticesButton.isSelected());
				showAddNoticeButton();
			} else if (cmd.equals(Resurses.TOOLBAR_ADDNOTICE_ACTION)) {
				personView.addNewNotice();

			} else if (cmd.equals(Resurses.TOOLBAR_DELETENOTICE_ACTION)) {
				personView.deleteNotice();
			}

		} catch (final Throwable ex) {

			logger.log(Level.WARNING, "Suku action", ex);
			JOptionPane.showMessageDialog(personView.getSuku(), ex.toString());
		}
	}

	/**
	 * @throws SukuException
	 */
	private void newDatabaseInit() throws SukuException {
		{
			String selectedSchema = null;
			SelectSchema schema = null;
			schema = new SelectSchema(this, databaseName);

			selectedSchema = schema.getSchema(isH2);

			final SukuData resp = Suku.kontroller.getSukuData("cmd=unitCount");
			if (resp.resuCount > 0) {
				// if (schema.isExistingSchema()) {

				final int resu = JOptionPane.showConfirmDialog(this, Resurses.getString("CONFIRM_NEWDB"),
						Resurses.getString(Resurses.SUKU), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (resu != JOptionPane.YES_OPTION) {
					return;
				}
			}
			try {
				this.tableModel.resetModel(); // clear contents of table
				table.getRowSorter().modelStructureChanged();
				// first
				this.personView.reset();
				this.table.updateUI();
				this.scrollPane.updateUI();

				kontroller.getSukuData("cmd=initdb");

				kontroller.getSukuData("cmd=excel", "page=coordinates");
				kontroller.getSukuData("cmd=excel", "page=types");

				JOptionPane.showMessageDialog(this, Resurses.getString("CREATED_NEWDB"),
						Resurses.getString(Resurses.SUKU), JOptionPane.INFORMATION_MESSAGE);
				resetIntellisens();
				kontroller.setSchema(selectedSchema);

				enableCommands();
				setTitle(null);
			} catch (final SukuException e1) {
				JOptionPane.showMessageDialog(this, Resurses.getString(Resurses.NEWDB),
						Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
				logger.log(Level.WARNING, Resurses.getString(Resurses.NEWDB), e1);

			}

		}
	}

	private void executeOrderChildren() {
		OrderChildren dlg;
		if (personView.getMainPaneIndex() > 1) {
			JOptionPane.showMessageDialog(this, Resurses.getString("STORE_CLOSE_PERSON"));
			return;
		}
		try {
			dlg = new OrderChildren(this);
			dlg.setVisible(true);
		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(this, e.toString(), Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void importOther() {

		try {
			final ImportOtherDialog cdlg = new ImportOtherDialog(this);

			final StringBuilder sb = new StringBuilder();

			if (cdlg.getResult() != null) {
				sb.append(Resurses.getString("IMPORT_ERROR"));

				sb.append(": ");
				sb.append(cdlg.getSchema());
				if (cdlg.getViewId() >= 0) {
					sb.append("/");
					sb.append(cdlg.getViewName());
				}
				sb.append("\n");
				sb.append(cdlg.getResult());
				JOptionPane.showMessageDialog(this, sb.toString(), Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
			}

		} catch (final SukuException e) {
			e.printStackTrace();
		}

	}

	private void createFamilyBackup() {
		try {
			final SukuData dat = kontroller.getSukuData("cmd=schema", "type=get");
			final String schema = dat.generalArray.length == 1 ? dat.generalArray[0] : "finfamily";

			ExportFamilyDatabaseDialog dlg;

			dlg = new ExportFamilyDatabaseDialog(this, schema, schema + ".zip");
			dlg.setVisible(true);

		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			return;
		}
	}

	private void exportGedcom() {

		// try {
		// SukuData dat = kontroller.getSukuData("cmd=schema", "type=get");
		// String schema = dat.generalArray.length == 1 ? dat.generalArray[0]
		// : "finfamily";
		//
		// ExportFamilyDatabaseDialog dlg;
		//
		// dlg = new ExportFamilyDatabaseDialog(this, schema, schema + ".zip");
		// dlg.setVisible(true);
		//
		// } catch (SukuException e) {
		// JOptionPane.showMessageDialog(this, e.getMessage());
		// return;
		// }

		// if (this.isWebApp) {
		try {
			final SukuData dat = kontroller.getSukuData("cmd=schema", "type=get");
			final String schema = dat.generalArray.length == 1 ? dat.generalArray[0] : "demo";
			ExportGedcomDialog dlg;

			dlg = new ExportGedcomDialog(this, schema, schema + ".zip");

			dlg.setVisible(true);

			final String[] failedLines = dlg.getResult();
			if (failedLines != null) {
				final StringBuilder sb = new StringBuilder();
				for (final String failedLine : failedLines) {
					sb.append(failedLine);
				}
				if (sb.length() > 0) {

					if (dlg.getLang(true) != null) {
						sb.append("\n");
						sb.append(Resurses.getString("EXPORT_LANG"));
						sb.append(" ");
						sb.append(dlg.getLang(false));
					}
					if (dlg.getViewName() != null) {
						sb.append("\n");
						sb.append(Resurses.getString("EXPORTED_VIEW"));
						sb.append(" ");
						sb.append(dlg.getViewName());

					}
					final java.util.Date d = new java.util.Date();
					final SukuPad pad = new SukuPad(this,
							kontroller.getFileName() + "\n" + d.toString() + "\n\n" + sb.toString());
					pad.setVisible(true);
				}
			}
		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			return;
		}

		// } else {
		// boolean isCreated = kontroller.createLocalFile("zip");
		// String zipName = kontroller.getFileName();
		// logger.finest("Opened GEDCOM FILE status " + isCreated);
		// if (isCreated) {
		//
		// ExportGedcomDialog dlg;
		// try {
		// dlg = new ExportGedcomDialog(this, databaseName, zipName);
		// } catch (SukuException e) {
		// JOptionPane.showMessageDialog(this, e.getMessage());
		// return;
		// }
		// dlg.setVisible(true);
		//
		// String[] failedLines = dlg.getResult();
		// if (failedLines != null) {
		// StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < failedLines.length; i++) {
		// sb.append(failedLines[i]);
		// }
		// if (sb.length() > 0) {
		//
		// if (dlg.getLang(true) != null) {
		// sb.append("\n");
		// sb.append(Resurses.getString("EXPORT_LANG"));
		// sb.append(" ");
		// sb.append(dlg.getLang(false));
		// }
		// if (dlg.getViewName() != null) {
		// sb.append("\n");
		// sb.append(Resurses.getString("EXPORTED_VIEW"));
		// sb.append(" ");
		// sb.append(dlg.getViewName());
		//
		// }
		// java.util.Date d = new java.util.Date();
		// SukuPad pad = new SukuPad(this,
		// kontroller.getFileName() + "\n" + d.toString()
		// + "\n\n" + sb.toString());
		// pad.setVisible(true);
		// }
		//
		// }
		// }
		// }
	}

	private void listDatabaseStatistics() {

		final ConnectDialog cdlg = new ConnectDialog(this, kontroller);

		final String user = kontroller.getPref(cdlg, "USERID", "");
		final String pass = kontroller.getPref(cdlg, "PASSWORD", "");
		final String host = kontroller.getPref(cdlg, "HOST", "localhost");
		SukuData resp = null;
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			resp = Suku.kontroller.getSukuData("cmd=get", "type=dbstatistics", "user=" + user, "password=" + pass,
					"host=" + host);
		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), Resurses.getString(Resurses.SUKU),
					JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
			return;

		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
		// String[] statisticsLines = { "Some lines", "here", "and there" };
		if ((resp != null) && (resp.generalArray != null)) {
			final StringBuilder sb = new StringBuilder();
			for (final String element : resp.generalArray) {
				sb.append(element + "\n");
			}
			if (sb.length() > 0) {

				final java.util.Date d = new java.util.Date();
				final SukuPad pad = new SukuPad(this,
						d.toString() + "\n" + Resurses.getString("MENU_TOOLS_LIST_DATABASES") + "\n\n" + sb.toString());
				pad.setVisible(true);
			}

		}

	}

	private void exportConversions(boolean doAll) {
		if (!kontroller.createLocalFile("xls")) {
			return;
		}

		try {
			kontroller.getSukuData("cmd=excel", "page=conversions", "type=export", "all=" + doAll);
			JOptionPane.showMessageDialog(this, Resurses.getString("EXPORTED_CONVERSIONS"),
					Resurses.getString(Resurses.SUKU), JOptionPane.INFORMATION_MESSAGE);

		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	private void importConversions() {
		try {
			final boolean openedFile = Suku.kontroller.openFile("xls");
			if (openedFile) {
				kontroller.getSukuData("cmd=excel", "file=xls", "page=conversions", "type=import");

				JOptionPane.showMessageDialog(this, Resurses.getString("IMPORTED_CONVERSIONS"),
						Resurses.getString(Resurses.SUKU), JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void importGedcom() {

		boolean isOpened;

		isOpened = kontroller.openFile("ged;zip");
		final String dbname = kontroller.getFileName();
		logger.finest("Opened GEDCOM FILE status " + isOpened);
		Utils.println(this, "gedcom open=" + isOpened + ": " + dbname);
		if (isOpened) {
			this.tableModel.resetModel(); // clear contents of table first
			table.getRowSorter().modelStructureChanged();
			this.personView.reset();
			this.table.updateUI();
			this.scrollPane.updateUI();
			String selectedSchema = null;
			if (!kontroller.isWebStart()) {
				SelectSchema schema;
				try {
					schema = new SelectSchema(this, databaseName);
					if (isH2) {
						selectedSchema = schema.getSchema(isH2);
					} else {
						schema.setVisible(true);

						selectedSchema = schema.getSchema(isH2);
						if (selectedSchema == null) {
							return;
						}
						if (!schema.isExistingSchema(isH2)) {
							kontroller.getSukuData("cmd=schema", "type=create", "name=" + selectedSchema);

						}
						kontroller.getSukuData("cmd=schema", "type=set", "name=" + selectedSchema);
						setTitle(null);
					}
				} catch (final SukuException e1) {

					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, e1.getMessage(), Resurses.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			ImportGedcomDialog dlg;
			try {
				dlg = new ImportGedcomDialog(this, dbname);
			} catch (final SukuException e) {
				return;
			}
			dlg.setVisible(true);
			kontroller.setSchema(selectedSchema);
			final String[] failedLines = dlg.getResult();
			if (failedLines != null) {
				final StringBuilder sb = new StringBuilder();
				for (final String failedLine : failedLines) {
					sb.append(failedLine);
				}
				if (sb.length() > 0) {

					final java.util.Date d = new java.util.Date();
					final SukuPad pad = new SukuPad(this, kontroller.getFileName() + "\n" + d.toString() + "\n"
							+ Resurses.getString("GEDCOM_IMPORT_IGNORED") + "\n\n" + sb.toString());
					pad.setVisible(true);
				}

			}

		}

	}

	/**
	 * @param pp
	 */
	private void addToNeedle(PersonShortData pp) {
		final String dd = "" + pp.getPid() + ";" + pp.getAlfaName(true) + " " + Utils.nv4(pp.getBirtDate()) + "-"
				+ Utils.nv4(pp.getDeatDate());
		needle.add(0, dd);

		for (int i = needle.size() - 1; i > 0; i--) {
			if (i > 4) {
				needle.remove(i);
			} else {
				final String[] dbl = needle.get(i).split(";");
				final int dblid = Integer.parseInt(dbl[0]);
				if ((pp.getPid() == dblid) || (i >= maxNeedle)) {
					needle.remove(i);
				}
			}
		}
		tSubjectPButton.setEnabled(true);
	}

	private void importDefaultTypes() {
		try {
			final boolean openedFile = Suku.kontroller.openFile("xls");
			if (openedFile) {
				kontroller.getSukuData("cmd=excel", "file=xls", "page=types");
				final SukuTypesModel typesModel = Utils.typeInstance();
				typesModel.initTypes();
				JOptionPane.showMessageDialog(this, Resurses.getString("IMPORTED_TYPES"),
						Resurses.getString(Resurses.SUKU), JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	private void importDefaultCoordinates() {
		try {
			final boolean openedFile = Suku.kontroller.openFile("xls");
			if (openedFile) {
				final SukuData result = kontroller.getSukuData("cmd=excel", "file=xls", "page=coordinates");
				if (result.generalArray != null) {
					final StringBuilder sb = new StringBuilder();
					for (final String element : result.generalArray) {
						sb.append(element);
					}
					if (sb.length() > 0) {

						final java.util.Date d = new java.util.Date();
						final SukuPad pad = new SukuPad(this, kontroller.getFileName() + "\n" + d.toString() + "\n"
								+ Resurses.getString("COORDINATE_IMPORT_FAILED") + "\n\n" + sb.toString());
						pad.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(this, Resurses.getString("IMPORTED_COORDINATES"),
								Resurses.getString(Resurses.SUKU), JOptionPane.INFORMATION_MESSAGE);
					}

				}

			}
		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void openGroupWin() {

		if (this.groupWin == null) {
			try {
				groupWin = new GroupMgrWindow(this);
			} catch (final SukuException e) {

				e.printStackTrace();
				JOptionPane.showMessageDialog(this, Resurses.getString("SUKU") + ":" + e.getMessage());
				return;

			}
			groupWin.setVisible(true);
		} else {
			groupWin.setVisible(true);
		}

	}

	private void openViewWin() {
		try {
			if (viewWin == null) {
				viewWin = new ViewMgrWindow(this);
				viewWin.setVisible(true);
				viewWin = null;
			} else {
				viewWin.initViewlist();
				viewWin.setVisible(true);
			}
		} catch (final SukuException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, Resurses.getString("SUKU") + ":" + e.getMessage());
		}
	}

	private void executeDbWork() {
		final ToolsDialog dlg = new ToolsDialog(this);
		dlg.setVisible(true);
	}

	private void showOwnerInformation() {
		final OwnerDialog dlg = new OwnerDialog(this);
		dlg.setVisible(true);
	}

	/**
	 * The toolbox button that defines if Note field is to be visible.
	 *
	 * @return true if button is depressed
	 */
	public boolean isShowNote() {
		return tNoteButton.isSelected();
	}

	/**
	 * The toolbox button that defines if Address field is to be visible.
	 *
	 * @return true if button is depressed
	 */
	public boolean isShowAddress() {
		return tAddressButton.isSelected();
	}

	/**
	 * The toolbox button that defines if village, farm and Croft fields are to
	 * be visible.
	 *
	 * @return true if button is depressed
	 */
	public boolean isShowFarm() {
		return tFarmButton.isSelected();
	}

	/**
	 * The toolbox button that defines if Image field is to be visible.
	 *
	 * @return true if button is depressed
	 */
	public boolean isShowImage() {
		return tImageButton.isSelected();
	}

	/**
	 * The toolbox button that defines if Notices are to be visible.
	 *
	 * @return true if button is depressed
	 */
	public boolean isShowNotices() {
		showAddNoticeButton();
		return tNoticesButton.isSelected();

	}

	/**
	 * The toolbox button that defines if Private text field is to be visible.
	 *
	 * @return true if button is depressed
	 */
	public boolean isShowPrivate() {
		return tPrivateButton.isSelected();
	}

	/**
	 * Creates the report.
	 *
	 * @param pers
	 *            the pers
	 */
	public void createReport(PersonShortData pers) {
		final ReportWorkerDialog dlg = new ReportWorkerDialog(this, kontroller, pers);
		dlg.setVisible(true);

		for (final String repo : dlg.getReportVector()) {
			Utils.openExternalFile(repo);
		}

	}

	/**
	 * Start join.
	 *
	 * @param sub
	 *            the sub
	 */
	public void startJoin(PersonShortData sub) {
		PersonShortData main;
		try {
			main = getJoinPerson();
			final JoinDialog join = new JoinDialog(this, main, sub);
			join.setVisible(true);
		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(this, Resurses.getString("SUKU") + ":" + e.getMessage());
		}

	}

	private void importFromHiski(boolean theButt) {
		if (theButt) {
			personView.displayHiskiPane();
		} else {
			HiskiFormClosing();
		}

	}

	private void showPerson(int pid) throws SukuException {
		personView.displayNewPersonPane(pid, null);
	}

	private void showPerson(int pid, String sex) throws SukuException {
		personView.displayNewPersonPane(pid, sex);
	}

	/**
	 * Get's opened persons pid or 0 if none opened.
	 *
	 * @return pid of opened person
	 */
	public PersonLongData getSubject() {

		return personView.getMainPerson();

	}

	/**
	 * Whenever a PersonView is opened this stores the activePerson pid for use
	 * by Subject button.
	 *
	 * @param pid
	 *            the new active person
	 */
	public void setActivePerson(int pid) {
		this.activePersonPid = pid;

	}

	/**
	 * gets from db view the selected persons name if one person is selected.
	 *
	 * @return the selected name if only one row is selected
	 */
	public PersonShortData getSelectedPerson() {

		final int[] ii = table.getSelectedRows();
		if (ii.length == 0) {
			return null;
		}

		final int tabsize = table.getRowCount();
		if (ii.length == 1) {
			if (ii[0] < tabsize) {
				final SukuRow rivi = (SukuRow) table.getValueAt(ii[0], SukuModel.SUKU_ROW);
				if (rivi == null) {
					return null;
				}
				return rivi.getPerson();
			}
		}
		return null;

	}

	/**
	 * updates group id for person on database window.
	 *
	 * @param pid
	 *            the pid
	 * @param groupId
	 *            the group id
	 */
	public void updateDbGroup(int pid, String groupId) {
		final PersonShortData p = tableMap.get(pid);
		if (p != null) {
			p.setGroup(groupId);
		}
	}

	/**
	 * Refresh the DBView with updated data.
	 */
	public void refreshDbView() {
		table.updateUI();
		scrollPane.updateUI();
	}

	/**
	 * Get an array of pid's of the selected rows.
	 *
	 * @return an int[] array of selected pid's
	 */
	public int[] getSelectedPids() {
		final int[] pids = new int[table.getSelectedRows().length];
		final int[] rows = table.getSelectedRows();
		// System.out.print("(");
		// for (int ii=0;ii<pids.length; ii++){
		// if (ii>0) System.out.print(";");
		// System.out.print(""+rows[ii]);
		// }
		// System.out.println(")");
		for (int i = 0; i < pids.length; i++) {
			// System.out.println("i=" + i + ";ri=" + rows[i] );
			final SukuRow rivi = (SukuRow) table.getValueAt(rows[i], SukuModel.SUKU_ROW);
			if (rivi == null) {
				return new int[0];
			}
			pids[i] = rivi.getPerson().getPid();
		}
		return pids;
	}

	// private void copyPerson(int pid) {
	// String koe = "Leikepöydälle [" + pid + "] kamaa";
	private void copyToClip(String koe) {
		final StringSelection stringSelection = new StringSelection(koe);
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);

	}

	private void adminDb() {
		try {
			if (this.adminUtilities == null) {
				this.adminUtilities = new LocalAdminUtilities(this);
			}

			if (this.adminUtilities.connectPostgres()) {
				this.adminUtilities.setAlwaysOnTop(true);
				this.adminUtilities.setVisible(true);
			}

		} catch (final Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, Resurses.getString(Resurses.ADMIN) + ":" + e.getMessage());

		}

	}

	private void displayMap(String cmd) {
		final PersonShortData ppp[] = new PersonShortData[tableMap.size()];
		final Set<Map.Entry<Integer, PersonShortData>> entriesx = tableMap.entrySet();
		final Iterator<Map.Entry<Integer, PersonShortData>> eex = entriesx.iterator();
		int i = 0;
		while (eex.hasNext()) {
			final Map.Entry<Integer, PersonShortData> entrx = eex.next();
			ppp[i++] = entrx.getValue();
		}

		if (ppp.length == 0) {
			JOptionPane.showMessageDialog(this, Resurses.getString("STAT_MAKE_QUERY_FIRST"),
					Resurses.getString("STAT_FOR_YOUR_INFORMATION"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		if (this.suomi == null) {

			if (!kontroller.isWebStart() && kontroller.getPref(this, "USE_OPEN_STREETMAP", "false").equals("true")) {
				this.suomi = new WorldMap(this);
			} else {

				this.suomi = new SuomiMap(this);
			}
		}

		final HashMap<String, String> ccodes = new HashMap<String, String>();
		try {
			final SukuData countdata = kontroller.getSukuData("cmd=get", "type=ccodes");

			for (final String nxt : countdata.generalArray) {
				if (nxt != null) {
					final String parts[] = nxt.split(";");
					if (parts.length == 2) {
						ccodes.put(parts[0], parts[1]);
					}
				}
			}

		} catch (final SukuException e1) {
			logger.log(Level.WARNING, "Failed to get country codes", e1);

		}

		final HashMap<String, PlaceLocationData> paikat = new HashMap<String, PlaceLocationData>();
		int idx;
		String paikka;
		String maa;
		String ccode;
		final String defaultCountry = Resurses.getDefaultCountry();
		PlaceLocationData place;

		for (idx = 0; idx < ppp.length; idx++) {
			if (cmd.equals("SHOWINMAPDEATH")) {
				paikka = ppp[idx].getDeatPlace();
				if (paikka != null) {
					maa = ppp[idx].getDeatCountry();
					if (maa != null) {
						maa = maa.toUpperCase();
					}

					if (maa == null) {
						ccode = defaultCountry;
					} else {
						ccode = ccodes.get(maa.toUpperCase());
						if (ccode == null) {
							ccode = defaultCountry;
						}
					}
					place = paikat.get(paikka.toUpperCase() + ";" + ccode);
					if (place == null) {
						place = new PlaceLocationData(paikka, ccode);

						paikat.put(paikka.toUpperCase() + ";" + ccode, place);
					} else {
						place.increment();
					}
				}
			} else {
				paikka = ppp[idx].getBirtPlace();
				if (paikka != null) {

					maa = ppp[idx].getBirthCountry();
					if (maa != null) {
						maa = maa.toUpperCase();
					}

					if (maa == null) {
						ccode = defaultCountry;
					} else {
						ccode = ccodes.get(maa.toUpperCase());
						if (ccode == null) {
							ccode = defaultCountry;
						}
					}
					place = paikat.get(paikka.toUpperCase() + ";" + ccode);
					if (place == null) {
						place = new PlaceLocationData(paikka, ccode);

						paikat.put(paikka.toUpperCase() + ";" + ccode, place);
					} else {
						place.increment();
					}
				}
			}
		}

		final SukuData request = new SukuData();
		request.places = new PlaceLocationData[paikat.size()];

		final Iterator<String> it = paikat.keySet().iterator();
		idx = 0;
		while (it.hasNext()) {
			request.places[idx] = paikat.get(it.next());
			idx++;
		}

		try {
			final SukuData response = kontroller.getSukuData(request, "cmd=places");

			suomi.displayMap((response != null) ? response.places : new PlaceLocationData[0]);

		} catch (final SukuException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, Resurses.getString(Resurses.SHOWINMAP) + ":" + e.getMessage());

		}

	}

	private void displayGenStats() {
		final PersonShortData ppp[] = new PersonShortData[tableMap.size()];
		final Set<Map.Entry<Integer, PersonShortData>> entriesx = tableMap.entrySet();
		final Iterator<Map.Entry<Integer, PersonShortData>> eex = entriesx.iterator();
		int i = 0;
		while (eex.hasNext()) {
			final Map.Entry<Integer, PersonShortData> entrx = eex.next();
			ppp[i++] = entrx.getValue();
		}

		if (ppp.length == 0) {
			JOptionPane.showMessageDialog(this, Resurses.getString("STAT_MAKE_QUERY_FIRST"),
					Resurses.getString("STAT_FOR_YOUR_INFORMATION"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		final GenStat genStat = new GenStat(this, ppp);
		genStat.setVisible(true);

	}

	private PersonShortData[] databaseWindowPersons;

	/**
	 * <h1>Database window access</h1>.
	 *
	 * @return number of rows in databasewindow
	 */
	public int getDatabaseRowCount() {
		if (databaseWindowPersons == null) {
			return 0;
		}
		return databaseWindowPersons.length;
	}

	/**
	 * <h1>Database window access.</h1>
	 *
	 * database window consist of rows containing instances of class
	 * PersonShortData
	 *
	 * @param idx
	 *            the idx
	 * @return the PersonShortData of the window on row
	 */
	public PersonShortData getDatbasePerson(int idx) {
		return databaseWindowPersons[idx];
	}

	private void queryDb() {
		int i;
		try {

			sukuObject = null; // reset program "clipboard"
			final SearchCriteria crit = SearchCriteria.getCriteria(this);
			crit.populateFields();
			crit.setVisible(true);

			final TableColumnModel tc = this.table.getColumnModel();
			TableColumn cc;
			String colhdr, tabnm;
			int j, k;
			for (k = 0; k < crit.getColTableCount(); k++) {
				tabnm = Resurses.getString(crit.getColTable(k).getColName());

				for (j = 0; j < tc.getColumnCount(); j++) {
					cc = tc.getColumn(j);
					colhdr = (String) cc.getHeaderValue();
					if (tabnm.equals(colhdr)) {
						if (crit.getColTable(k).getCurrentState() == false) {
							tc.removeColumn(cc);
							logger.fine("let's poistaa " + tabnm);
						}
						break;
					}
				}
			}

			int newpos = 0;
			String tabId;
			for (k = 0; k < crit.getColTableCount(); k++) {
				tabId = crit.getColTable(k).getColName();
				tabnm = Resurses.getString(tabId);

				if (crit.getColTable(k).getCurrentState()) {
					newpos++;
				}

				for (j = 0; j < tc.getColumnCount(); j++) {
					cc = tc.getColumn(j);
					colhdr = (String) cc.getHeaderValue();
					if (tabnm.equals(colhdr)) {
						break;
					}
				}
				if (j == tc.getColumnCount()) {

					if (crit.getColTable(k).getCurrentState() == true) {
						logger.fine("let's add " + tabnm);

						final int colidx = tc.getColumnCount();

						final String colnm = crit.getColName(k);
						final int curidx = crit.getCurrentIndex(colnm);

						final TableColumn c = new TableColumn(k);
						if (tabId.equals(Resurses.COLUMN_T_ISCHILD) || tabId.equals(Resurses.COLUMN_T_ISMARR)
								|| tabId.equals(Resurses.COLUMN_T_ISPARE)) {
							c.setMaxWidth(35);
						}
						c.setHeaderValue(tabnm);

						if ((!Resurses.getDateFormat().equals("SE")
								&& (tabId.equals(Resurses.COLUMN_T_BIRT) || tabId.equals(Resurses.COLUMN_T_DEAT)))
								|| tabId.equals(Resurses.COLUMN_T_PID)) {
							c.setCellRenderer(new RightTableCellRenderer());
						}
						tc.addColumn(c);
						tc.moveColumn(colidx, curidx - 1);
						// initSorter(crit);
					}
				}

			}

			final ArrayList<String> v = new ArrayList<String>();
			v.add("cmd=plist");
			for (i = 0; i < crit.getFieldCount(); i++) {
				if ((crit.getCriteriaField(i) != null) && !crit.getCriteriaField(i).isEmpty()) {
					v.add(crit.getFieldName(i) + "=" + URLEncoder.encode(crit.getCriteriaField(i), "UTF-8"));
				}
			}
			final String[] auxes = v.toArray(new String[0]);
			final SukuData fam = kontroller.getSukuData(auxes);

			// System.out.println("ROWS: " + table.getRowCount());
			// System.out.println("MAP: " + tableMap.size());
			// System.out.println("MODEL: " + tableModel.getRowCount());

			// initSorter(crit);
			this.databaseWindowPersons = fam.pers;
			Arrays.sort(databaseWindowPersons);

			this.tableModel.resetModel(); // clear contents of table first
			table.getRowSorter().modelStructureChanged();
			// this.table.removeAll();
			this.table.clearSelection();
			// this.personView.reset();
			this.tableMap.clear();

			for (i = 0; i < this.databaseWindowPersons.length; i++) {

				String bdate = null, ddate = null;
				// String birtPlace=null, deatPlace=null;

				bdate = this.databaseWindowPersons[i].getBirtDate();
				if (bdate == null) {
					if (crit.isPropertySet(Resurses.COLUMN_T_BIRT_CHR)) {
						bdate = this.databaseWindowPersons[i].getChrDate();
					}
				}
				// birtPlace = this.databaseWindowPersons[i].getBirtPlace();
				ddate = this.databaseWindowPersons[i].getDeatDate();
				if (ddate == null) {
					if (crit.isPropertySet(Resurses.COLUMN_T_DEAT_BURI)) {
						ddate = this.databaseWindowPersons[i].getBuriedDate();
					}
				}
				// deatPlace=this.databaseWindowPersons[i].getDeatPlace();

				appendToLocalview(this.databaseWindowPersons[i]);
				// row = new
				// SukuRow(this.tableModel,this.databaseWindowPersons[i]);
				//
				// this.tableModel.addRow(row);
				// int key = this.databaseWindowPersons[i].getPid();
				// this.tableMap.put(key, this.databaseWindowPersons[i]);
			}

			table.getRowSorter().allRowsChanged();
			this.statusPanel.setText("" + this.databaseWindowPersons.length);
			this.table.setRowHeight(20);
			this.table.setShowVerticalLines(false);

			this.scrollPane.setVisible(true);
			this.table.updateUI();
			this.scrollPane.updateUI();
			initSorter(crit);
		} catch (final SukuException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, Resurses.getString(Resurses.QUERY) + ":" + e1.getMessage());
		} catch (final UnsupportedEncodingException ue) {
			ue.printStackTrace();
			JOptionPane.showMessageDialog(this, Resurses.getString(Resurses.QUERY) + ":" + ue.getMessage());
		}
	}

	/**
	 * <h1>Database window access.</h1>
	 *
	 * Update database window with data for person
	 *
	 * @param p
	 *            the p
	 * @throws SukuException
	 *             the suku exception
	 */
	public void updatePerson(PersonShortData p) throws SukuException {

		final int key = p.getPid();
		final PersonShortData ret = this.tableMap.put(key, p);

		final int midx = personView.getMainPaneIndex();
		if (midx > 0) {
			final SukuTabPane tp = personView.getPane(midx);
			if (tp.getPid() == key) {
				personView.closeMainPane(true);
			}
		}
		final SukuData resp = kontroller.getSukuData("cmd=virtual", "type=counts", "pid=" + key);

		if ((resp.pidArray != null) && (resp.pidArray.length == 3)) {
			p.setChildCount(resp.pidArray[0]);
			p.setMarrCount(resp.pidArray[1]);
			p.setPareCount(resp.pidArray[2]);

		}

		final SukuData resprela = kontroller.getSukuData("cmd=virtual", "type=relatives", "pid=" + key);

		for (final int element : resprela.pidArray) {

			final SukuData rex = kontroller.getSukuData("cmd=virtual", "type=counts", "pid=" + element);

			final PersonShortData px = this.tableMap.get(element);
			if ((rex != null) && (px != null)) {

				if ((rex.pidArray != null) && (rex.pidArray.length == 3)) {
					px.setChildCount(rex.pidArray[0]);
					px.setMarrCount(rex.pidArray[1]);
					px.setPareCount(rex.pidArray[2]);

				}
			}

		}

		if (ret == null) {
			final SukuRow row = new SukuRow(this, this.tableModel, p);
			tableModel.addRow(0, row);

		}
		table.updateUI();
		scrollPane.updateUI();

		//
		// also update familytree if person is a member there
		//

		this.personView.updateSubjectForFamily(key);
		//
		// and database draft
		//
		if (key == personView.getTextPersonPid()) {
			personView.setTextForPerson(p);
		}
	}

	/**
	 * <h1>Database window access.</h1>
	 *
	 * get PersonShortData from database window for person pid (pid = person
	 * identification number integer database specific identifier
	 *
	 * @param pid
	 *            the pid
	 * @return PersonShortData instance for requested person
	 */
	public PersonShortData getPerson(int pid) {
		return tableMap.get(pid);
	}

	/**
	 * Delete person.
	 *
	 * @param pid
	 *            the pid
	 */
	public void deletePerson(int pid) {
		final PersonShortData ss = tableMap.get(pid);
		if (ss != null) {

			for (int i = 0; i < tableModel.getRowCount(); i++) {
				final SukuRow rr = (SukuRow) tableModel.getValueAt(i, -1);
				if (rr.getPid() == ss.getPid()) {
					tableModel.removeRow(i);
					tableMap.remove(pid);
					table.updateUI();
					scrollPane.updateUI();
					break;
				}
			}

		}
	}

	private PersonShortData appendToLocalview(PersonShortData p) {
		final int key = p.getPid();
		final PersonShortData ret = this.tableMap.put(key, p);
		if (ret == null) {

			final SukuRow row = new SukuRow(this, this.tableModel, p);
			this.tableModel.addRow(row);
		}
		return ret;
	}

	private void disconnectDb() {

		final ConnectDialog cdlg = new ConnectDialog(this, kontroller);
		cdlg.rememberDatabase(false);
		if (!kontroller.isConnected()) {
			return;
		}

		final SukuData request = new SukuData();
		request.generalArray = needle.toArray(new String[0]);

		try {
			Suku.kontroller.getSukuData(request, "cmd=updatesettings", "type=needle", "name=needle");
		} catch (final SukuException ee) {
			if (kontroller.getSchema() != null) {
				JOptionPane.showMessageDialog(this, ee.getMessage(), Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
				ee.printStackTrace();
			}
		}
		needle.clear();

		this.personView.reset();
		this.tableModel.resetModel(); // clear contents of table first
		table.getRowSorter().modelStructureChanged();
		this.databaseWindowPersons = null;
		this.table.updateUI();
		this.scrollPane.updateUI();
		kontroller.resetConnection();
		sukuObject = null;
		enableCommands();
		setTitle(null);

	}

	private void importSuku2004Backup() {
		boolean isOpened;
		try {
			this.tableModel.resetModel(); // clear contents of table first
			table.getRowSorter().modelStructureChanged();
			this.personView.reset();
			this.table.updateUI();
			this.scrollPane.updateUI();

			isOpened = kontroller.openFile("xml;xml.gz;zip");
			if (!isOpened) {
				return;
			}
			logger.finest("Opened IMPORT FILE status " + isOpened);
			String selectedSchema = null;
			if (!kontroller.isRemote()) {
				final SelectSchema schema = new SelectSchema(this, databaseName);
				schema.setVisible(true);
				selectedSchema = schema.getSchema(isH2);
				if (selectedSchema == null) {
					return;
				}
				if (!schema.isExistingSchema(isH2)) {
					kontroller.getSukuData("cmd=schema", "type=create", "name=" + selectedSchema);
				}
				kontroller.getSukuData("cmd=schema", "type=set", "name=" + selectedSchema);
			}
			Import2004Dialog dlg = null;
			try {
				dlg = new Import2004Dialog(this, kontroller);
			} catch (final SukuException ex) {
				logger.log(Level.WARNING, "Import failed", ex);
				JOptionPane.showMessageDialog(this, ex.getMessage(), Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			dlg.setVisible(true);
			Utils.println(this, "import done");
			dlg.setRunnerValue(Resurses.getString("IMPORT_PAIKAT"));
			kontroller.setSchema(selectedSchema);
			// if (!kontroller.isWebStart()) {
			kontroller.getSukuData("cmd=excel", "page=coordinates");
			dlg.setRunnerValue(Resurses.getString("IMPORT_TYPES"));
			kontroller.getSukuData("cmd=excel", "page=types");
			// }
			setTitle(null);
			final String[] failedLines = dlg.getResult();
			if (failedLines != null) {
				final StringBuilder sb = new StringBuilder();
				for (final String failedLine : failedLines) {
					sb.append(failedLine + "\n");
				}
				if (sb.length() > 0) {

					final java.util.Date d = new java.util.Date();
					final SukuPad pad = new SukuPad(this, kontroller.getFileName() + "\n" + d.toString() + "\n"
							+ Resurses.getString("SUKU2004_IMPORT_ERRORS") + "\n\n" + sb.toString());
					pad.setVisible(true);
				}
			} else {
				queryDb();
			}
		} catch (final SukuException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

	private Dimension currentSize = new Dimension();

	private void calcSize() {
		if (this.scrollPane == null) {
			return;
		}
		int lastLoc = this.splitPane.getDividerLocation();
		if (lastLoc < 0) {
			lastLoc = 100;
		}
		final int lastWidth = this.currentSize.width;

		if ((getExtendedState() & ICONIFIED) != 0) {
			return;
		}

		this.currentSize = getSize();

		final Dimension splitterSize = new Dimension();

		splitterSize.height = this.currentSize.height - 120;
		splitterSize.width = this.currentSize.width - (SPLITTER_HORIZ_MARGIN * 3);

		int rooty = getRootPane().getLocation().y;
		rooty += this.menubar.getSize().height;

		final int rootw = getRootPane().getSize().width;

		this.statusPanel.setBounds(2, this.currentSize.height - rooty - 30, rootw - 3, 26);

		int splitWidth = splitterSize.width;
		if (splitWidth < 0) {
			splitWidth = 10;
		}
		this.splitPane.setBounds(10, 30, splitWidth, splitterSize.height - 30);

		this.toolbar.setBounds(10, 0, splitWidth, 30);

		// System.out.println("WIDTHN: " + currentSize.width + "/" +
		// this.splitPane.getSize().width + "/" + splitterSize.width);

		if (lastWidth > 0) {
			if ((Math.abs(lastWidth - this.currentSize.width) > 10)) {
				final int locaNew = (splitWidth * lastLoc) / lastWidth;

				// System.out.println("LOCAN/W: " + locaNew + "/" + lastLoc);
				this.splitPane.setDividerLocation(locaNew);
			}

			try {
				this.splitPane.updateUI();
			} catch (final NullPointerException ne) {
				logger.warning("splitPane NullPointerException");
			}

		}
		if (this.scrollPane != null) {
			try {
				this.scrollPane.updateUI();
			} catch (final NullPointerException ne) {
				logger.warning("scrollPane NullPointerException");
				// ne.printStackTrace();
			}
		}

	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void componentHidden(ComponentEvent e) {
		//

	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void componentMoved(ComponentEvent e) {
		//

	}

	/**
	 * recalculates sizes.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		calcSize();

	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void componentShown(ComponentEvent e) {
		// Does nothing.
	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void menuCanceled(MenuEvent e) {
		// Does nothing.
	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void menuDeselected(MenuEvent e) {
		// Does nothing.
	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void menuSelected(MenuEvent e) {
		enableCommands();
	}

	private void enableCommands() {
		// mConnect.setEnabled(isConnected == 0);
		mImport.setEnabled(kontroller.isConnected());
		mImport2004.setEnabled(kontroller.isConnected());
		mImportGedcom.setEnabled(kontroller.isConnected());
		mExport.setEnabled(kontroller.getSchema() != null);
		mExportGedcom.setEnabled(kontroller.getSchema() != null);
		mExportBackup.setEnabled(kontroller.getSchema() != null);
		mQuery.setEnabled(kontroller.getSchema() != null);
		mSubjectDown.setEnabled(kontroller.getSchema() != null);
		mSubjectUp.setEnabled(kontroller.getSchema() != null);
		// mSettings.setEnabled(isConnected > 0);
		if (kontroller.isH2() && kontroller.isConnected()) {
			mAdmin.setEnabled(false);
			mNewDatabase.setEnabled(false);
			mDropSchema.setEnabled(false);
		} else {
			mAdmin.setEnabled(true);
			mNewDatabase.setEnabled(kontroller.isConnected());
			if (!kontroller.isRemote()) {
				mDropSchema.setEnabled(kontroller.isConnected());
			}
		}
		mOpenPerson.setEnabled(kontroller.getSchema() != null);
		mPrintPerson.setEnabled(kontroller.getSchema() != null);
		mDisconnect.setEnabled(kontroller.getSchema() != null);
		mShowInMap.setEnabled(kontroller.getSchema() != null);
		mStatistics.setEnabled(kontroller.getSchema() != null);
		mShowWithBirth.setEnabled(kontroller.getSchema() != null);
		mShowWithDeath.setEnabled(kontroller.getSchema() != null);
		mLista.setEnabled(kontroller.getSchema() != null);
		// mDisconnect.setEnabled(isConnected != 0);
		tQueryButton.setEnabled(kontroller.getSchema() != null);
		if (kontroller.isWebStart()) {
			mImportHiski.setEnabled(false);
		} else {
			mImportHiski.setEnabled(kontroller.getSchema() != null);
		}
		mRemPerson.setEnabled(kontroller.getSchema() != null);
		mNewPerson.setEnabled(kontroller.getSchema() != null);
		mShowNotices.setEnabled(kontroller.getSchema() != null);
		mShowNote.setEnabled(kontroller.getSchema() != null);
		mShowAddress.setEnabled(kontroller.getSchema() != null);
		mShowFarm.setEnabled(kontroller.getSchema() != null);
		mShowImage.setEnabled(kontroller.getSchema() != null);
		mShowPrivate.setEnabled(kontroller.getSchema() != null);
		mDbWork.setEnabled(kontroller.getSchema() != null);
		mDbUpdate.setEnabled(kontroller.getSchema() != null);
		tPersonButton.setEnabled(kontroller.getSchema() != null);
		tQueryButton.setEnabled(kontroller.getSchema() != null);
		tMapButton.setEnabled(kontroller.getSchema() != null);
		tRemovePerson.setEnabled(kontroller.getSchema() != null);
		mSettings.setEnabled(kontroller.getSchema() != null);
		mOrderChildren.setEnabled(kontroller.getSchema() != null);
		mExecSql.setEnabled(kontroller.getSchema() != null);
		mGroupMgr.setEnabled(kontroller.getSchema() != null);
		mViewMgr.setEnabled(kontroller.getSchema() != null);
		mToolsAuxGraphviz.setEnabled(kontroller.getSchema() != null);
		mListDatabases.setEnabled(kontroller.getSchema() != null);
		mOwner.setEnabled(kontroller.getSchema() != null);
		tSubjectButton.setEnabled(kontroller.getSchema() != null);
		if (kontroller.getSchema() == null) {
			tAddNotice.setEnabled(false);
			tDeleteNotice.setEnabled(false);
		}

	}

	/**
	 * Enables/disables the add notice button.
	 */
	public void showAddNoticeButton() {

		final int isele = personView.getSelectedIndex();
		final int mnotice = personView.getMainPaneIndex();
		tAddNotice.setEnabled((kontroller.getSchema() != null) && tNoticesButton.isSelected() && (isele >= mnotice));
		tDeleteNotice.setEnabled(
				(kontroller.getSchema() != null) && tNoticesButton.isSelected() && (isele >= (mnotice + 2)));

	}

	/**
	 * mouse clicked on database window.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		final int ii = this.table.getSelectedRow();
		if (ii < 0) {
			return;
		}
		if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
			final SukuRow row = (SukuRow) this.table.getValueAt(ii, SukuModel.SUKU_ROW);
			if (row == null) {
				return;
			}
			try {
				this.personView.setSubjectForFamily(row.getPerson().getPid());
			} catch (final SukuException e1) {
				JOptionPane.showMessageDialog(this, "show " + row + " error " + e1.getMessage());
				e1.printStackTrace();
			}
			//
			logger.fine("Showed on familytree " + row);
		}
		// else if (e.getClickCount() == 1 && e.getButton() ==
		// MouseEvent.BUTTON1){
		// SukuRow row = (SukuRow)this.table.getValueAt(ii, SukuModel.SUKU_ROW);
		// if (row != null) {
		// PersonShortData perso = row.getPerson();
		// if (perso != null) {
		// Suku.sukuObject = perso;
		// logger.fine("Copied to clipboard [" + perso.getPid() + "]: " +
		// perso.getAlfaName());
		// }
		// }
		// }

	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// System.out.println("ENTER: " + e);
		// Does nothing.
	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// System.out.println("EXIT: " + e);
		// Does nothing.
	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// Does nothing.
	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// Does nothing.
		//
		// if (e.getButton()== MouseEvent.BUTTON3 && e.getClickCount()==1){
		//
		//
		// int yy = e.getY();
		// int rh = this.table.getRowHeight();
		// int ii = yy/rh;
		//
		// SukuRow row = (SukuRow)this.tableModel.getValueAt(ii,
		// SukuModel.SUKU_ROW);
		//
		// System.out.println("row has: " + row.getPid() + "/" + row.getName());
		//
		// System.out.println("rrR:" + yy + "/" + ii+"/" + e);
		//
		// }
		//

	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// Does nothing.
	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// int ii = this.table.getSelectedRow();
		// System.out.println("KKK: " + ii );
		// Does nothing.
	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// System.out.println("KTYP:" + e);
		// Does nothing.
	}

	/**
	 * does nothing.
	 *
	 * @param arg0
	 *            the arg0
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// Does nothing.
	}

	/**
	 * does nothing.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		// Does nothing.
	}

	/**
	 * release map window.
	 *
	 * @param me
	 *            the me
	 */
	@Override
	public void SukuFormClosing(JFrame me) {
		this.suomi = null;

	}

	/**
	 * release admin window.
	 *
	 * @param me
	 *            the me
	 */
	@Override
	public void AdminFormClosing(JFrame me) {
		this.adminUtilities = null;

	}

	/**
	 * release hiski window.
	 */
	@Override
	public void HiskiFormClosing() {
		personView.closeHiskiPane();

	}

	/**
	 * The listener interface for receiving popup events. The class that is
	 * interested in processing a popup event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's <code>addPopupListener<code> method. When the popup event
	 * occurs, that object's appropriate method is invoked.
	 *
	 * @see PopupEvent
	 */
	class PopupListener extends MouseAdapter implements ActionListener {

		private SukuRow activeRow = null;
		private Suku parent = null;

		/**
		 * Instantiates a new popup listener.
		 *
		 * @param suku
		 *            the suku
		 */
		public PopupListener(Suku suku) {
			parent = suku;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {

				final Point clickPoint = e.getPoint();

				final int rowAtPoint = table.rowAtPoint(clickPoint);
				if (rowAtPoint < 0) {
					return;
				}

				activeRow = (SukuRow) table.getValueAt(rowAtPoint, SukuModel.SUKU_ROW);

				final SukuPopupMenu pop = SukuPopupMenu.getInstance();
				pop.setPerson(activeRow.getPerson());
				pop.show(e, e.getX(), e.getY(), MenuSource.dbView);
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			final String cmd = e.getActionCommand();
			if (cmd == null) {
				return;
			}
			final SukuPopupMenu pop = SukuPopupMenu.getInstance();
			if (pop.getSource() == MenuSource.familyView) {
				// TODO:
			} else if (activeRow == null) {
				return;
			}
			if (pop.getPerson() != null) {
				if (cmd.equals(Resurses.TAB_PERSON_TEXT)) {

					try {
						personView.setTextForPerson(pop.getPerson());
					} catch (final SukuException e1) {
						JOptionPane.showMessageDialog(parent,
								"SHOW PERSON: " + pop.getPerson().getAlfaName() + " error " + e1.getMessage());
						e1.printStackTrace();
					}

				} else if (cmd.equals(Resurses.TAB_FAMILY)) {
					try {
						personView.setSubjectForFamily(pop.getPerson() == null ? 0 : pop.getPerson().getPid());

					} catch (final SukuException e1) {
						JOptionPane.showMessageDialog(null,
								"SHOW FAMILY: " + pop.getPerson().getAlfaName() + " error " + e1.getMessage());

						e1.printStackTrace();
					}
					//
				} else if (cmd.startsWith("HISKI") && (cmd.length() > 5)) {
					final int hiskino = Integer.parseInt(cmd.substring(5));
					personView.setHiskiPerson(hiskino, pop.getPerson());
				} else if (cmd.equals(Resurses.TOOLBAR_REMPERSON_ACTION)) {

					final PersonShortData p = pop.getPerson();
					final int resu = JOptionPane.showConfirmDialog(parent,
							Resurses.getString("CONFIRM_DELETE") + " " + p.getAlfaName(),
							Resurses.getString(Resurses.SUKU), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (resu == JOptionPane.YES_OPTION) {

						try {
							final SukuData result = Suku.kontroller.getSukuData("cmd=delete", "pid=" + p.getPid());
							if (result.resu != null) {
								JOptionPane.showMessageDialog(parent, result.resu, Resurses.getString(Resurses.SUKU),
										JOptionPane.ERROR_MESSAGE);
								logger.log(Level.WARNING, result.resu);
								return;
							}

							final int key = p.getPid();

							final PersonShortData ret = getPerson(key);
							if (ret != null) {
								// this says the person is in db view
								deletePerson(key);
							}
							final int midx = personView.getMainPaneIndex();
							if (midx > 1) {
								final int mpid = personView.getPane(midx).getPid();

								personView.closeMainPane(mpid != p.getPid());
								personView.refreshRelativesPane();

							}

						} catch (final SukuException e1) {
							JOptionPane.showMessageDialog(parent, e1.getMessage(), Resurses.getString(Resurses.SUKU),
									JOptionPane.ERROR_MESSAGE);
							logger.log(Level.WARNING, e1.getMessage(), e1);
							e1.printStackTrace();
						}
					}

				} else if (cmd.equals(Resurses.CREATE_REPORT)) {
					createReport(pop.getPerson());
				} else if (cmd.equals(Resurses.TAB_PERSON)) {
					final int pid = pop.getPerson().getPid();
					try {
						showPerson(pid);
					} catch (final SukuException e1) {

						String message = Resurses.getString(e1.getMessage());
						final int createdIdx = message.toLowerCase().indexOf("the column name");
						if (createdIdx > 0) {
							message += "\n" + Resurses.getString("SUGGEST_UPDATE");
						}

						JOptionPane.showMessageDialog(parent, message);
						logger.log(Level.SEVERE, "Failed to create person [" + pid + "]", e);

					}

				} else if (cmd.equals(Resurses.TAB_RELATIVES)) {
					final int pid = pop.getPerson().getPid();
					try {
						showPerson(pid);
					} catch (final SukuException e1) {
						JOptionPane.showMessageDialog(parent, e1.getMessage());
						logger.log(Level.SEVERE, "Failed to create person [" + pid + "]", e);
					}

					final int midx = personView.getMainPaneIndex();
					if (midx >= 2) {
						personView.setSelectedIndex(midx + 1);
					}

				} else if (cmd.equals(Resurses.MENU_COPY)) {
					if (pop.getSource() == MenuSource.familyView) {
						personView.copyFamilyToClipboardAsImage();
					} else {

						final PersonShortData perso = pop.getPerson();
						if (perso != null) {
							Suku.sukuObject = perso;
							logger.fine("Copied to clipboard [" + perso.getPid() + "]: " + perso.getAlfaName());
						} else {
							return;
						}

						final int[] ii = table.getSelectedRows();
						if (ii.length == 0) {
							return;
						}
						final StringBuilder sb = new StringBuilder();

						sb.append(perso.getHeader() + "\n");
						for (final int element : ii) {
							final SukuRow rivi = (SukuRow) table.getValueAt(element, SukuModel.SUKU_ROW);
							final PersonShortData pers = rivi.getPerson();

							sb.append(pers.toString() + "\n");

						}
						sb.append(Resurses.getString("TEXT_COPIED"));
						sb.append(" ");
						sb.append(Resurses.getString("SUKUOHJELMISTO"));
						sb.append(" ");
						final java.util.Date now = new java.util.Date();
						sb.append(now.toString());

						copyToClip(sb.toString());
					}
				} else if (cmd.equals(Resurses.MENU_NEEDLE)) {
					final PersonShortData pp = pop.getPerson();
					if (pp != null) {
						addToNeedle(pp);
					}
				} else if (cmd.startsWith("ADD")) {
					final PersonShortData pp = pop.getPerson();
					if (pp != null) {

						parent.activePersonPid = 0;
						try {

							if (cmd.equals("ADDCHILD")) {
								parent.showPerson(0, null);
								parent.personView.addParentToPerson(pp);
							} else if (cmd.equals("ADDSPOUSE")) {
								final String spouseSex = (pp.getSex().equals("M")) ? "F" : "M";
								parent.showPerson(0, spouseSex);
								parent.personView.addSpouseToPerson(pp);

							} else if (cmd.equals("ADDPARENT")) {
								parent.showPerson(0, null);
								parent.personView.addChildToPerson(pp);
							}

						} catch (final SukuException e1) {
							JOptionPane.showMessageDialog(parent, e1.getMessage());
							logger.log(Level.SEVERE, "Failed to create person ", e);
						}
					}
				} else if (cmd.equals("JOIN_PERSON")) {
					joinPersonPid = pop.getPerson().getPid();
					if (joinPersonPid > 0) {
						pop.enableJoinAdd(pop.getPerson());
					}
				} else if (cmd.equals("JOIN_ADD_PERSON")) {
					final StringBuilder messu = new StringBuilder();
					PersonShortData main = null;
					try {
						main = getJoinPerson();
					} catch (final SukuException e1) {
						messu.append(e1.toString());
					}
					if (main != null) {
						final String sexm = main.getSex();
						final String sexp = pop.getPerson().getSex();
						if (!sexm.equals("U") && !sexp.equals("U")) {
							if (!sexm.equals(sexp)) {
								messu.append(Resurses.getString("JOIN_DIFF_SEX"));
							}
						}

					}
					if (messu.length() > 0) {
						JOptionPane.showMessageDialog(parent, messu.toString());
					} else {
						// JOptionPane.showMessageDialog(parent,
						// ("Under construction\njoin ["
						// + pop.getPerson().getPid() + "]: "
						// + pop.getPerson().getName(false, true)
						// + " to [" + joinPersonPid + "] "));
						startJoin(pop.getPerson());
					}
				}
			}

		}
	}

	/**
	 * This is used to align columns in database view to the right dated,
	 * numbers etc.
	 *
	 * @author fikaakail
	 */
	class RightTableCellRenderer extends DefaultTableCellRenderer {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new right table cell renderer.
		 */
		protected RightTableCellRenderer() {
			setHorizontalAlignment(JLabel.RIGHT);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer
	 * .Clipboard, java.awt.datatransfer.Transferable)
	 */
	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// do nothing

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.swing.ISuku#GroupWindowClosing()
	 */
	@Override
	public void GroupWindowClosing() {
		groupWin = null;

	}

	/**
	 * The Class SukuTransferHandler.
	 */
	class SukuTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new suku transfer handler.
		 */
		SukuTransferHandler() {
			super();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
		 */
		@Override
		public int getSourceActions(JComponent c) {
			return COPY;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * javax.swing.TransferHandler#createTransferable(javax.swing.JComponent
		 * )
		 */
		@Override
		protected Transferable createTransferable(JComponent c) {
			if (c instanceof JTable) {
				final int midx = personView.getMainPaneIndex();
				if (midx >= 2) {
					personView.setSelectedIndex(midx + 1);
				}

				final PersonShortData ps = getSelectedPerson();
				if (ps != null) {
					ps.setDragSource(Utils.PersonSource.DATABASE);
					return ps;
				}

			}
			return null;

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent,
		 * java.awt.datatransfer.Transferable, int)
		 */
		@Override
		protected void exportDone(JComponent c, Transferable t, int action) {
			// nothing needs to be done here
		}

	}

	/**
	 * The Class DbTable.
	 */
	class DbTable extends JTable {

		private static final long serialVersionUID = 1L;

		/** The model. */
		SukuModel model = null;

		/**
		 * Instantiates a new db table.
		 *
		 * @param model
		 *            the model
		 */
		DbTable(SukuModel model) {
			super(model);
			this.model = model;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.JTable#convertColumnIndexToView(int)
		 */
		@Override
		public int convertColumnIndexToView(int modelColumnIndex) {
			return crit.getViewIndex(modelColumnIndex);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.JTable#convertColumnIndexToModel(int)
		 */
		@Override
		public int convertColumnIndexToModel(int viewColumnIndex) {
			return crit.getModelIndex(viewColumnIndex);
		}

	}

	/**
	 * Sets the image scaler index.
	 *
	 * @param imageScaler
	 *            the new image scaler index
	 */
	public void setImageScalerIndex(int imageScaler) {

		imageScalingIndex = imageScaler;
	}

	/**
	 * Gets the image scaler index.
	 *
	 * @return the image scaler index
	 */
	public int getImageScalerIndex() {
		return imageScalingIndex;
	}

	/**
	 * Gets the fin family xls.
	 *
	 * @return the finFamilyXls
	 */
	public static String getFinFamilyXls() {
		if (finFamilyXls != null) {
			final File f = new File(finFamilyXls);
			if (f.exists()) {
				return finFamilyXls;
			} else {
				logger.warning("FinFamily file does not exist at " + finFamilyXls);
			}
		}
		return null;
	}

	/**
	 * Sets the fin family xls.
	 *
	 * @param path
	 *            the new fin family xls
	 */
	public static void setFinFamilyXls(String path) {
		if ((path != null) && path.isEmpty()) {
			finFamilyXls = null;
		} else {
			finFamilyXls = path;
		}
	}

}
