package fi.kaila.suku.kontroller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

public class KontrollerUtils {

	private static final String hexi = "0123456789ABCDEF";
	private static Logger logger = Logger.getLogger(KontrollerUtils.class.getName());

	/**
	 * Open file.
	 *
	 * @param uri
	 *            the uri
	 * @param userno
	 *            the userno
	 * @param filename
	 *            the filename
	 * @param iis
	 *            the iis
	 * @return the int
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static int openFile(String uri, String userno, String filename, InputStream iis) throws IOException {

		final String lineEnd = "\r\n";
		final String twoHyphens = "--";
		final String boundary = "*****";
		DataOutputStream dos = null;

		String query;
		uri += "SukuServlet";
		query = "cmd=file";

		final byte[] bytes = query.getBytes();

		final URL url = new URL(uri);

		final HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

		con.setRequestProperty("Referer", "/SSS/" + userno + "/" + filename + "/");
		con.setRequestProperty("Content-Length", String.valueOf(bytes.length));
		con.setRequestMethod("POST");

		dos = new DataOutputStream(con.getOutputStream());

		dos.write(bytes);
		dos.writeBytes(lineEnd);
		dos.writeBytes(lineEnd);

		int nextByte;

		StringBuilder rivi = new StringBuilder();

		while ((nextByte = iis.read()) >= 0) {
			if (rivi.length() > 64) {
				dos.writeBytes(rivi.toString() + lineEnd);
				rivi = new StringBuilder();
			}
			rivi.append(hexi.charAt((nextByte >> 4) & 0xf));
			rivi.append(hexi.charAt(nextByte & 0xf));

		}

		if (rivi.length() > 0) {
			dos.writeBytes(rivi.toString() + lineEnd);
		}

		// send multipart form data necesssary after file data...

		dos.writeBytes(lineEnd);
		dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		// close streams

		dos.flush();
		dos.close();

		// Read the response
		final InputStream in = con.getInputStream();
		int inle = 0;
		while (true) {
			final int idata = in.read();
			if (idata == -1) {
				break;
			}
			inle++;
		}

		final int resu = con.getResponseCode();
		in.close();
		dos.close();
		con.disconnect();

		return resu;
	}

	/**
	 * Gets the suku data.
	 *
	 * @param uri
	 *            the uri
	 * @param userno
	 *            the userno
	 * @param params
	 *            the params
	 * @return the suku data
	 * @throws SukuException
	 *             the suku exception
	 */
	public static SukuData getSukuData(String uri, String userno, String... params) throws SukuException {
		final StringBuilder sb = new StringBuilder();
		sb.append(uri);
		String requri;
		int resu;
		int i;
		final SukuData errr = new SukuData();
		errr.resu = "ERROR";

		if (userno == null) {
			return errr;
		}
		final String paras[] = params;

		sb.append("SukuServlet?userno=" + userno);

		for (i = 0; i < paras.length; i++) {
			sb.append("&" + paras[i]);
		}

		requri = sb.toString();
		try {

			logger.fine("URILOG: " + requri);

			final URL url = new URL(requri);
			final HttpURLConnection uc = (HttpURLConnection) url.openConnection();

			resu = uc.getResponseCode();

			if (resu == 200) {
				final String coding = uc.getHeaderField("Content-Encoding");

				final StringBuilder xx = new StringBuilder();
				xx.append("Content-Encoding: " + coding);
				xx.append(";");
				for (final String param : params) {
					xx.append(param);
					xx.append(";");
				}

				InputStream in = null;
				if ("gzip".equals(coding)) {
					in = new GZIPInputStream(uc.getInputStream());
				} else {
					in = uc.getInputStream();
				}

				ObjectInputStream ois = null;
				SukuData fam = null;
				try {
					ois = new ObjectInputStream(in);
					fam = (SukuData) ois.readObject();
				} catch (final Exception e) {
					e.printStackTrace();
					throw new SukuException(e);
				} finally {
					if (ois != null) {
						try {
							ois.close();
						} catch (final IOException ignored) {
							// IOException ignored
						}
					}
					if (in != null) {
						try {
							in.close();
						} catch (final IOException ignored) {
							// IOException ignored
						}
					}
				}

				return fam;

			}
			throw new SukuException("Network error " + resu);

		} catch (final Exception e) {

			throw new SukuException(e);
		}

	}

	/**
	 * Gets the suku data.
	 *
	 * @param uri
	 *            the uri
	 * @param userno
	 *            the userno
	 * @param request
	 *            the request
	 * @param params
	 *            the params
	 * @return the suku data
	 * @throws SukuException
	 *             the suku exception
	 */
	public static SukuData getSukuData(String uri, String userno, SukuData request, String... params)
			throws SukuException {
		final SukuData errr = new SukuData();
		errr.resu = "ERROR";
		if (userno == null) {
			return errr;
		}

		if (request == null) {
			return getSukuData(uri, userno, params);
		}
		final StringBuilder query = new StringBuilder();

		final String paras[] = params;
		try {
			query.append("userno=" + userno);

			for (final String para : paras) {
				query.append("&" + URLEncoder.encode(para, "UTF-8"));
			}

			final String lineEnd = "\r\n";
			final String twoHyphens = "--";
			final String boundary = "*****";
			DataOutputStream dos = null;

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buff = null;

			final ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(request);
			buff = bos.toByteArray();
			oos.close();

			final InputStream gis = new ByteArrayInputStream(buff);

			final String urix = uri + "SukuServlet";

			final byte[] bytes = query.toString().getBytes();

			final URL url = new URL(urix);

			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			con.setRequestProperty("Referer", "/SSS/" + userno + "/");
			con.setRequestProperty("Content-Length", String.valueOf(bytes.length));
			con.setRequestMethod("POST");

			dos = new DataOutputStream(con.getOutputStream());

			dos.write(bytes);
			dos.writeBytes(lineEnd);

			dos.writeBytes(lineEnd);

			int nextByte;

			StringBuilder rivi = new StringBuilder();

			while ((nextByte = gis.read()) >= 0) {
				if (rivi.length() > 64) {
					dos.writeBytes(rivi.toString() + lineEnd);
					rivi = new StringBuilder();
				}
				rivi.append(hexi.charAt((nextByte >> 4) & 0xf));
				rivi.append(hexi.charAt(nextByte & 0xf));

			}

			if (rivi.length() > 0) {
				dos.writeBytes(rivi.toString() + lineEnd);
			}

			// send multipart form data necesssary after file data...

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams

			dos.flush();
			dos.close();

			// Read the response

			final String coding = con.getHeaderField("Content-Encoding");

			InputStream in = null;
			if ("gzip".equals(coding)) {
				in = new GZIPInputStream(con.getInputStream());
			} else {
				in = con.getInputStream();
			}

			final ObjectInputStream ois = new ObjectInputStream(in);
			SukuData fam = null;
			try {
				fam = (SukuData) ois.readObject();
				ois.close();
			} catch (final Exception e) {

				throw new SukuException(e);
			}

			return fam;

		} catch (final Throwable e) {

			throw new SukuException(e);

		}

	}

}
