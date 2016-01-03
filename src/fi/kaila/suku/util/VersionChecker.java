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

		final VersionTask task = new VersionTask();
		task.execute();

	}

	private void runMe() {

		//
		// first decide if now is good time to check
		//
		final String progLoca = Suku.kontroller.getPref(suku, Resurses.LOCALE, "fi");
		final String repoLang = Suku.kontroller.getPref(suku, Resurses.REPOLANG, "fi");
		final String dateFormat = Suku.kontroller.getPref(suku, Resurses.DATEFORMAT, "FI");

		final String country = Locale.getDefault().getCountry();
		final String langu = Locale.getDefault().getLanguage();
		final String os = System.getProperty("os.name");
		final String lastRevision = Suku.kontroller.getPref(this, "Revision", "0");
		final String lastTry = Suku.kontroller.getPref(this, "lastTime", "0");
		final String ant = AntVersion.antVersion;

		final String requri = "http://www.sukuohjelmisto.fi/version/version.properties?fl=" + progLoca // finfamily
																										// program
																										// language
				+ "&fr=" + repoLang // finfamily report language
				+ "&fd=" + dateFormat // finfamily date format
				+ "&fv=" + ant // finfamily version
				+ "&jc=" + country // java country
				+ "&jl=" + langu // java language
				+ "&je=" + os; // os
		final long nowTime = System.currentTimeMillis();

		long lastTime = 0;
		try {
			lastTime = Long.parseLong(lastTry);
		} catch (final NumberFormatException ne) {
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

			final URL url = new URL(requri);
			final HttpURLConnection uc = (HttpURLConnection) url.openConnection();

			resu = uc.getResponseCode();
			if (resu == 200) {

				final InputStream in = uc.getInputStream();

				final byte b[] = new byte[2048];

				final int pit = in.read(b);

				final String aux = new String(b, 0, pit);
				final String auxes[] = aux.split("\n");

				for (final String auxe : auxes) {
					final String parts[] = auxe.split("=");
					if (parts.length == 2) {
						final int plen = parts[1].length();
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

		} catch (final Exception e) {
			Suku.kontroller.putPref(this, "lastTime", "" + (nowTime + (12 * 60 * 60 * 1000)));
			logger.info(e.toString());

		}

		if (serverRevision == null) {
			return;
		}
		int currRev = 0;
		int serRev = 0;
		int lastRev = 0;
		final int revDot = ant.lastIndexOf(".");
		try {
			currRev = Integer.parseInt(ant.substring(revDot + 1));
			serRev = Integer.parseInt(serverRevision);
			lastRev = Integer.parseInt(lastRevision);
		} catch (final NumberFormatException ne) {
			return;
		}
		Suku.kontroller.putPref(this, "lastTime", "" + nowTime);
		Suku.kontroller.putPref(this, "Revision", "" + serRev);
		if (lastRev >= serRev) {
			return;
		}

		if (serRev > currRev) {
			final int resux = JOptionPane.showConfirmDialog(suku,
					Resurses.getString("CONFIRM_DOWNLOAD") + " [" + serverVer + "." + serverRevision + "]\n"
							+ Resurses.getString("CONFIRM_NEW") + " [" + ant + "]\n" + Resurses.getString("CONFIRM_GO"),
					Resurses.getString(Resurses.SUKU), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (resux == JOptionPane.YES_OPTION) {
				final String updateSite = "https://sourceforge.net/projects/finfamily/";
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
