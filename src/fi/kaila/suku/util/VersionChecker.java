package fi.kaila.suku.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import fi.kaila.suku.ant.AntVersion;
import fi.kaila.suku.swing.Suku;

/**
 * 
 * Called to check the version of calling program against version in file at
 * http://www.sukuohjelmisto.fi/version/version.properties
 * 
 * @author kalle
 * 
 */
public class VersionChecker {

	private Suku suku = null;
	private Logger logger = null;

	/**
	 * Instantiates a new version checker.
	 * 
	 * @param suku
	 *            the suku
	 */
	public VersionChecker(Suku suku) {
		logger = Logger.getLogger(this.getClass().getName());
		this.suku = suku;

		VersionTask task = new VersionTask();
		task.execute();

	}

	private void runMe() {

		//
		// first decide if now is good time to check
		//
		String progLoca = Suku.kontroller.getPref(suku, Resurses.LOCALE, "fi");
		String repoLang = Suku.kontroller
				.getPref(suku, Resurses.REPOLANG, "fi");
		String dateFormat = Suku.kontroller.getPref(suku, Resurses.DATEFORMAT,
				"FI");

		String country = Locale.getDefault().getCountry();
		String langu = Locale.getDefault().getLanguage();
		String os = System.getProperty("os.name");
		String lastRevision = Suku.kontroller.getPref(this, "Revision", "0");
		String lastTry = Suku.kontroller.getPref(this, "lastTime", "0");
		String ant = AntVersion.antVersion;

		String requri = "http://www.sukuohjelmisto.fi/version/version.properties?fl="
				+ progLoca // finfamily program language
				+ "&fr=" + repoLang // finfamily report language
				+ "&fd=" + dateFormat // finfamily date format
				+ "&fv=" + ant // finfamily version
				+ "&jc=" + country // java country
				+ "&jl=" + langu // java language
				+ "&je=" + os; // os
		long nowTime = System.currentTimeMillis();

		long lastTime = 0;
		try {
			lastTime = Long.parseLong(lastTry);
		} catch (NumberFormatException ne) {
			logger.info("failed to parse lastTry " + lastTry);
			return;
		}

		if ((lastTime + (60 * 60 * 1000)) > nowTime) {
			return;
		}

		int resu;
		String serverVer = null;
		String serverRevision = null;
		try {

			URL url = new URL(requri);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();

			resu = uc.getResponseCode();
			if (resu == 200) {

				InputStream in = uc.getInputStream();

				byte b[] = new byte[2048];

				int pit = in.read(b);

				String aux = new String(b, 0, pit);
				String auxes[] = aux.split("\n");

				for (String auxe : auxes) {
					String parts[] = auxe.split("=");
					if (parts.length == 2) {
						int plen = parts[1].length();
						if ((parts[1].charAt(plen - 1) == '\r') && (plen > 1)) {
							parts[1] = parts[1].substring(0, plen - 1);
						}

						if (parts[0].equalsIgnoreCase("app.version")) {
							serverVer = parts[1];
						}
						if (parts[0].equalsIgnoreCase("revision.version")) {
							serverRevision = parts[1];
						}
					}
				}

				in.close();

			}

		} catch (Exception e) {
			Suku.kontroller.putPref(this, "lastTime", ""
					+ (nowTime + (12 * 60 * 60 * 1000)));
			logger.info(e.toString());

		}

		if (serverRevision == null) {
			return;
		}
		int currRev = 0;
		int serRev = 0;
		int lastRev = 0;
		int revDot = ant.lastIndexOf(".");
		try {
			currRev = Integer.parseInt(ant.substring(revDot + 1));
			serRev = Integer.parseInt(serverRevision);
			lastRev = Integer.parseInt(lastRevision);
		} catch (NumberFormatException ne) {
			return;
		}
		Suku.kontroller.putPref(this, "lastTime", "" + nowTime);
		Suku.kontroller.putPref(this, "Revision", "" + serRev);
		if (lastRev >= serRev) {
			return;
		}

		if (serRev > currRev) {
			int resux = JOptionPane.showConfirmDialog(
					suku,
					Resurses.getString("CONFIRM_DOWNLOAD") + " [" + serverVer
							+ "." + serverRevision + "]\n"
							+ Resurses.getString("CONFIRM_NEW") + " [" + ant
							+ "]\n" + Resurses.getString("CONFIRM_GO"),
					Resurses.getString(Resurses.SUKU),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (resux == JOptionPane.YES_OPTION) {
				String updateSite = "https://sourceforge.net/projects/finfamily/";
				Utils.openExternalFile(updateSite);
			}
		}

	}

	/**
	 * The Class VersionTask.
	 */
	class VersionTask extends SwingWorker<Void, Void> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Void doInBackground() throws Exception {
			//
			// No version checks because www.sukuohjelmisto.fi site is not
			// active anymore (halonmi 20130326).
			//

			// runMe();
			return null;
		}

	}

}
