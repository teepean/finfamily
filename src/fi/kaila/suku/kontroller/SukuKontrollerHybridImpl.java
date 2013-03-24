package fi.kaila.suku.kontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Hybrid kontroller accesses the remote service from a locally installed
 * FinFamily
 * 
 * @author kalle
 * 
 */
public class SukuKontrollerHybridImpl implements SukuKontroller {

	private static Logger logger = null;
	private static Preferences sr = null;
	private File file = null;
	private Suku host = null;
	private final String url;
	private String userno = null;

	private String schema = null;

	private boolean isConnected = false;

	/**
	 * Instantiates a new suku kontroller hybrid impl.
	 * 
	 * @param host
	 *            the host
	 * @param url
	 *            the url
	 */
	public SukuKontrollerHybridImpl(Suku host, String url) {
		this.host = host;
		this.url = url;
		sr = Preferences.userRoot();
		logger = Logger.getLogger(this.getClass().getName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getConnection(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void getConnection(String host, String dbname, String userid,
			String passwd) throws SukuException {
		String requri = this.url + "SukuServlet?userid=" + userid + "&passwd="
				+ passwd;
		schema = null;
		isConnected = false;
		int resu;

		try {

			URL url = new URL(requri);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();

			String encoding = uc.getContentEncoding();

			resu = uc.getResponseCode();

			if (resu == 200) {

				InputStream in;
				if ("gzip".equals(encoding)) {
					in = new java.util.zip.GZIPInputStream(uc.getInputStream());
				} else {
					in = uc.getInputStream();
				}

				byte b[] = new byte[1024];

				int pit = in.read(b);
				for (int i = 0; i < pit; i++) {
					if ((b[i] == '\n') || (b[i] == '\r')) {
						pit = i;
						break;
					}
				}
				String aux = new String(b, 0, pit);

				String auxes[] = aux.split("/");

				this.userno = auxes[0];
				if (auxes.length > 1) {
					Suku.serverVersion = auxes[1];
				}

				in.close();
				isConnected = true;
				schema = userid;
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			throw new SukuException(Resurses.getString("ERR_NOT_CONNECTED")
					+ " [" + e.toString() + "]");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#resetConnection()
	 */
	@Override
	public void resetConnection() {
		isConnected = false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getSukuData(java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(String... params) throws SukuException {

		return KontrollerUtils.getSukuData(this.url, this.userno, params);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getSukuData(fi.kaila.suku.util
	 * .pojo.SukuData, java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException {
		return KontrollerUtils.getSukuData(this.url, this.userno, request,
				params);

	}

	private String openDiskFile(String filter) {
		Preferences sr = Preferences.userRoot();

		String[] filters = filter.split(";");

		String koe = sr.get(filters[0], ".");
		logger.fine("Hakemisto on: " + koe);

		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter(filter));
		chooser.setDialogTitle("Open " + filter + " file");
		chooser.setSelectedFile(new File(koe + "/."));

		if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		File f = chooser.getSelectedFile();
		if (f == null) {
			return null;
		}
		String filename = f.getAbsolutePath();
		file = new File(filename);

		logger.info("Valittiin: " + filename);

		String tmp = f.getAbsolutePath().replace("\\", "/");
		int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return tmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#openFile(java.lang.String)
	 */
	@Override
	public boolean openFile(String filter) {

		String path = openDiskFile(filter);
		if (path == null) {
			return false;
		}
		try {
			InputStream iis = new FileInputStream(path);
			int resu = KontrollerUtils.openFile(this.url, this.userno,
					getFileName(), iis);
			if (resu == 200) {
				return true;
			}
			logger.warning("openFile returnded response " + resu);
		} catch (Exception e) {
			logger.log(Level.WARNING, "open file", e);
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFileLength()
	 */
	@Override
	public long getFileLength() {
		if (file != null) {
			return file.length();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		if (file != null) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING,
						"Failed to get input stream for file", e);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFileName()
	 */
	@Override
	public String getFileName() {
		if (file != null) {
			return file.getName();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFilePath()
	 */
	@Override
	public String getFilePath() {
		return file.getAbsolutePath().replace("\\", "/");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getPref(java.lang.Object,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public String getPref(Object o, String key, String def) {
		return sr.get(o.getClass().getName() + "." + key, def);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#putPref(java.lang.Object,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void putPref(Object o, String key, String value) {
		sr.put(o.getClass().getName() + "." + key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#createLocalFile(java.lang.String)
	 */
	@Override
	public boolean createLocalFile(String filter) {
		Preferences sr = Preferences.userRoot();

		String[] filters = filter.split(";");

		String koe = sr.get(filters[0], ".");
		logger.fine("Hakemisto on: " + koe);

		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter(filter));
		chooser.setDialogTitle("Create " + filter + " file");
		chooser.setSelectedFile(new File(koe + "/."));

		if (chooser.showSaveDialog(host) != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		File f = chooser.getSelectedFile();
		if (f == null) {
			return false;
		}

		String filename = f.getAbsolutePath();
		if (filename == null) {
			return false;
		}
		if (f.exists()) {
			int answer = JOptionPane.showConfirmDialog(host,
					Resurses.getString("FILE_EXISTS") + " [" + filename
							+ "] \n" + Resurses.getString("FILE_OVERWRITE"),
					Resurses.getString(Resurses.SUKU),
					JOptionPane.YES_NO_OPTION);
			if (answer != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		if (filters.length == 1) {
			if (!filename.toLowerCase().endsWith(filters[0].toLowerCase())) {
				filename += "." + filters[0];
			}
		}

		file = new File(filename);

		logger.info("Valittiin: " + filename);

		String tmp = f.getAbsolutePath().replace("\\", "/");
		int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#saveFile(java.lang.String,
	 * java.io.InputStream)
	 */
	@Override
	public boolean saveFile(String filter, InputStream in) {
		if (createLocalFile(filter)) {
			try {
				byte[] buffer = new byte[1024];

				int readBytes = 0;
				FileOutputStream fos = new FileOutputStream(file);
				while ((readBytes = in.read(buffer)) >= 0) {
					fos.write(buffer, 0, readBytes);
				}

				in.close();
				fos.close();
				return true;
			} catch (Exception e) {
				logger.log(Level.INFO, "saveFile", e);

			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws FileNotFoundException {
		if (file != null) {
			return new FileOutputStream(file);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#openLocalFile(java.lang.String)
	 */
	@Override
	public InputStream openLocalFile(String path) {
		if (openFile(path)) {
			if (path != null) {
				if (file == null) {
					return null;
				}

				String mainPath = file.getAbsolutePath();

				try {
					return new FileInputStream(mainPath);
				} catch (FileNotFoundException e) {
					logger.log(Level.WARNING, e.getMessage());

				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#isRemote()
	 */
	@Override
	public boolean isRemote() {

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#isWebStart()
	 */
	@Override
	public boolean isWebStart() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#isConnected()
	 */
	@Override
	public boolean isConnected() {

		return isConnected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getSchema()
	 */
	@Override
	public String getSchema() {

		return schema;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#setSchema(java.lang.String)
	 */
	@Override
	public void setSchema(String schema) {

	}

}
