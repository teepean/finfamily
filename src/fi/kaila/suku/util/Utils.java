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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fi.kaila.suku.kontroller.SukuKontroller;
import fi.kaila.suku.report.PersonInTables;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.dialog.SukuPad;
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;

/**
 * general static utilities.
 *
 * @author FIKAAKAIL
 */
public class Utils {

	/**
	 * enumerator for source of person for drag-and-drop.
	 */
	public enum PersonSource {

		/** Database table. */
		DATABASE,

		/** parent table. */
		PARENT,

		/** spouse table. */
		SPOUSE,

		/** child table. */
		CHILD
	}

	private static Logger logger = Logger.getLogger(Utils.class.getName());

	/**
	 * Get boolean preference from local repository.
	 *
	 * @param o
	 *            the o
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return true or false
	 */
	public static boolean getBooleanPref(Object o, String key, boolean def) {
		String resu;
		String sdef = "false";
		if (def) {
			sdef = "true";
		}

		final SukuKontroller kontroller = Suku.getKontroller();

		resu = kontroller.getPref(o, key, sdef);

		if (resu == null) {
			return def;
		}

		if (resu.equals("true")) {
			return true;
		}
		return false;

	}

	/**
	 * Put boolean preference into local repository.
	 *
	 * @param o
	 *            the o
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public static void putBooleanPref(Object o, String key, boolean value) {
		String svalue = "false";
		if (value) {
			svalue = "true";
		}
		final SukuKontroller kontroller = Suku.getKontroller();

		kontroller.putPref(o, key, svalue);
	}

	/**
	 * convert dbdate date to viewable textformat.
	 *
	 * @param dbDate
	 *            the db date
	 * @param trimDate
	 *            the trim date
	 * @return in text format
	 */
	public static String textDate(String dbDate, boolean trimDate) {
		final String df = Resurses.getDateFormat();
		if (dbDate == null) {
			return null;
		}
		if (dbDate.length() == 4) {
			return dbDate;
		}
		// int y=0;
		int m = 0;
		int d = 0;
		String yy = null;
		String mm = null;
		String dd = null;
		if (dbDate.length() == 6) {
			yy = dbDate.substring(0, 4);
			mm = dbDate.substring(4);
		} else if (dbDate.length() == 8) {
			yy = dbDate.substring(0, 4);
			mm = dbDate.substring(4, 6);
			dd = dbDate.substring(6);
		}
		try {
			// y = Integer.parseInt(yy);
			m = Integer.parseInt(mm);
			if (dd != null) {
				d = Integer.parseInt(dd);
			}
			if (!df.equals("SE") && trimDate) {
				mm = "" + m;
				if (dd != null) {
					dd = "" + d;
				}
			}
		} catch (final NumberFormatException ne) {
			// NumberFormatException ignored
		}

		if (dbDate.length() == 6) {

			if (df.equals("SE")) {
				return yy + "-" + mm;
			} else if (df.equals("FI")) {
				return mm + "." + yy;
			} else {
				return mm + "/" + yy;
			}
		} else if (dbDate.length() == 8) {
			if (df.equals("SE")) {
				return yy + "-" + mm + "-" + dd;
			} else if (df.equals("FI")) {
				return dd + "." + mm + "." + yy;
			} else if (df.equals("GB")) {
				return dd + "/" + mm + "/" + yy;
			} else {
				return mm + "/" + dd + "/" + yy;
			}

		}
		return dbDate;
	}

	/**
	 * convert dbdate date mont part to int.
	 *
	 * @param dbDate
	 *            the db date
	 *
	 * @return in text format
	 */
	public static int textDateMonth(String dbDate) {
		if (dbDate == null) {
			return 0;
		}
		if (dbDate.length() == 4) {
			return 0;
		}
		int m = 0;
		String mm = null;
		if (dbDate.length() == 6) {
			mm = dbDate.substring(4);
		} else if (dbDate.length() == 8) {
			mm = dbDate.substring(4, 6);
		}
		try {
			// y = Integer.parseInt(yy);
			m = Integer.parseInt(mm);
		} catch (final NumberFormatException ne) {
			// NumberFormatException ignored
		}

		return m;
	}

	/**
	 * Db try date.
	 *
	 * @param textDate
	 *            the text date
	 * @return db date or null if wrong date
	 */
	public static String dbTryDate(String textDate) {
		try {
			return dbDate(textDate);
		} catch (final SukuDateException e) {
			return null;
		}
	}

	/**
	 * convert viewable textdate to dbformat.
	 *
	 * @param textDate
	 *            the text date
	 * @return date in dbformat
	 * @throws SukuDateException
	 *             if bad dateformat
	 */
	public static String dbDate(String textDate) throws SukuDateException {
		if ((textDate == null) || textDate.isEmpty()) {
			return null;
		}
		final String df = Resurses.getDateFormat();

		final StringBuilder sb = new StringBuilder();

		sb.append(Resurses.getString("ERROR_WRONGDATE"));
		sb.append(" ");
		sb.append(textDate);

		final String parts[] = textDate.split("\\.|,|/|-");
		final int parti[] = { -1, -1, -1 };
		if (parts.length > 3) {
			throw new SukuDateException(sb.toString());
		}
		for (int i = 0; i < parts.length; i++) {
			try {

				parti[i] = Integer.parseInt(parts[i].trim());
			} catch (final NumberFormatException ne) {
				throw new SukuDateException(sb.toString());
			}
		}

		final SimpleDateFormat dfor = new SimpleDateFormat("yyyyMMdd");
		final String today = dfor.format(new java.util.Date());
		final int nowy = Integer.parseInt(today.substring(0, 4));
		int y = -1;
		int m = -1;
		int d = -1;

		if (parts.length == 1) {
			y = parti[0];
		}
		if (parts.length == 2) {
			if (df.equals("SE")) {
				y = parti[0];
				m = parti[1];
			} else {
				y = parti[1];
				m = parti[2];
			}
		}
		if (parts.length == 3) {
			if (df.equals("SE")) {
				y = parti[0];
				m = parti[1];
				d = parti[2];

			} else if (df.equals("US")) {
				y = parti[2];
				m = parti[0];
				d = parti[1];

			} else {
				y = parti[2];
				m = parti[1];
				d = parti[0];
			}
		}

		int leap = y % 4;
		if (leap == 0) {
			leap = 29;
		} else {
			leap = 28;
		}
		if (y == 1712) {
			leap = 30;
		}
		if (y > nowy) {
			throw new SukuDateException(Resurses.getString("ERROR_FUTURE") + " [" + textDate + "]");
		}

		if ((m >= 0) && ((m == 0) || (m > 12))) {
			throw new SukuDateException(Resurses.getString("ERROR_MONTH") + " [" + textDate + "]");
		}
		if (d >= 0) {
			if ((d > 0) && (d <= 31)) {
				switch (m) {
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12:
					break;
				case 2:
					if (d > leap) {
						d = -1;
					}
					break;
				case 4:
				case 6:
				case 9:
				case 11:
					if (d == 31) {
						d = -1;

					}
					break;

				}
			} else {
				d = -1;
			}
			if (d < 0) {
				throw new SukuDateException(Resurses.getString("ERROR_DAY") + " [" + textDate + "]");
			}
		}

		if (parts.length == 1) {
			return strl(parti[0], 4);
		}

		if (parts.length == 2) {
			if (df.equals("SE")) {
				return strl(parti[0], 4) + strl(parti[1], 2);
			} else {
				return strl(parti[1], 4) + strl(parti[0], 2);
			}
		}
		if (parts.length == 3) {
			if (df.equals("SE")) {
				return strl(parti[0], 4) + strl(parti[1], 2) + strl(parti[2], 2);
			} else if (df.equals("US")) {
				return strl(parti[2], 4) + strl(parti[0], 2) + strl(parti[1], 2);
			}
			return strl(parti[2], 4) + strl(parti[1], 2) + strl(parti[0], 2);

		}
		throw new SukuDateException(sb.toString());

	}

	/**
	 * Modify name to Proper name using capital latters as first character.
	 *
	 * @param names
	 *            the names
	 * @return the proper name
	 */
	public static String toProper(String names) {
		if (names == null) {
			return null;
		}
		if (names.isEmpty()) {
			return "";
		}
		final String name[] = names.split(" ");

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length; i++) {
			if (!name[i].isEmpty()) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				if (name[i].length() == 1) {
					sb.append(name[i].toUpperCase());
				} else {
					sb.append(name[i].substring(0, 1).toUpperCase());
					sb.append(name[i].substring(1));
				}
			}
		}
		return sb.toString();

	}

	private static String strl(int i, int len) {
		if (i == 0) {
			return null;
		}
		final String text = "" + i;

		if (text.length() == len) {
			return text;
		}
		final StringBuilder sb = new StringBuilder();
		for (int j = 0; j < len; j++) {
			sb.append("0");
		}
		sb.append(text);

		return sb.toString().substring(sb.length() - len);

	}

	/**
	 * Create from report tables a HasMap of persons in the report.
	 *
	 * @param tables
	 *            Vector that contains all tables for report
	 * @return a HashMap containing a list of all persons in report with
	 *         information on tables where they exist
	 */
	public static HashMap<Integer, PersonInTables> getDescendantToistot(Vector<ReportUnit> tables) {
		final HashMap<Integer, PersonInTables> personReferences = new HashMap<Integer, PersonInTables>();

		for (int i = 0; i < tables.size(); i++) {
			final ReportUnit tab = tables.get(i);
			PersonInTables ref;
			ReportTableMember member;

			for (int j = 0; j < tab.getChild().size(); j++) {
				member = tab.getChild().get(j);
				ref = personReferences.get(member.getPid());
				if (ref == null) {
					ref = new PersonInTables(member.getPid());

					if (tab.getChild().size() > 0) {
						ref.asChildren.add(Long.valueOf(tab.getTableNo()));
					}
					personReferences.put(Integer.valueOf(member.getPid()), ref);
				} else {
					if (tab.getChild().size() > 0) {
						ref.asChildren.add(Long.valueOf(tab.getTableNo()));
					}
				}

				for (int m = 0; m < member.getSubCount(); m++) {
					ref = personReferences.get(member.getSubPid(m));
					if (ref == null) {
						ref = new PersonInTables(member.getSubPid(m));
						ref.references.add(Long.valueOf(tab.getTableNo()));
						personReferences.put(Integer.valueOf(member.getSubPid(m)), ref);
					} else {
						ref.references.add(tab.getTableNo());
					}
				}

				if (member.getSpouses() != null) {
					final ReportTableMember[] spouseMembers = member.getSpouses();
					ReportTableMember spouseMember;
					for (final ReportTableMember spouseMember2 : spouseMembers) {
						spouseMember = spouseMember2;
						ref = personReferences.get(spouseMember.getPid());
						if (ref == null) {
							ref = new PersonInTables(spouseMember.getPid());
							ref.asParents.add(Long.valueOf(tab.getTableNo()));
							personReferences.put(Integer.valueOf(spouseMember.getPid()), ref);
						} else {
							ref.asParents.add(tab.getTableNo());
						}
						for (int m = 0; m < spouseMember.getSubCount(); m++) {
							ref = personReferences.get(spouseMember.getSubPid(m));
							if (ref == null) {
								ref = new PersonInTables(spouseMember.getSubPid(m));
								ref.references.add(Long.valueOf(tab.getTableNo()));
								personReferences.put(Integer.valueOf(spouseMember.getSubPid(m)), ref);
							} else {
								ref.references.add(tab.getTableNo());
							}
						}
					}
				}

			}
			for (int j = 0; j < tab.getParent().size(); j++) {
				member = tab.getParent().get(j);
				if (tab.getPid() == member.getPid()) {
					// were the owner here
					ref = personReferences.get(member.getPid());
					if (ref == null) {
						ref = new PersonInTables(member.getPid());
						ref.addOwner(tab.getTableNo());
						personReferences.put(Integer.valueOf(member.getPid()), ref);
					} else {
						ref.addOwner(tab.getTableNo());

					}

				}

				ref = personReferences.get(member.getPid());
				if (ref == null) {
					ref = new PersonInTables(member.getPid());

					if (tab.getChild().size() > 0) {
						ref.asParents.add(Long.valueOf(tab.getTableNo()));
					}
					personReferences.put(Integer.valueOf(member.getPid()), ref);
				} else {
					if (tab.getChild().size() > 0) {
						ref.asParents.add(Long.valueOf(tab.getTableNo()));
					}
				}
				for (int m = 0; m < member.getSubCount(); m++) {
					ref = personReferences.get(member.getSubPid(m));
					if (ref == null) {
						ref = new PersonInTables(member.getSubPid(m));
						ref.references.add(Long.valueOf(tab.getTableNo()));
						personReferences.put(Integer.valueOf(member.getSubPid(m)), ref);
					} else {
						ref.references.add(tab.getTableNo());
					}
				}
			}
		}
		return personReferences;
	}

	/**
	 * Nv.
	 *
	 * @param text
	 *            the text
	 * @return empty string if null or text
	 */
	public static String nv(String text) {
		if (text == null) {
			return "";
		}
		return text;
	}

	/**
	 * Vn.
	 *
	 * @param text
	 *            the text
	 * @return null if empty string or text
	 */
	public static String vn(String text) {
		if ((text == null) || text.isEmpty()) {
			return null;
		}
		return text;
	}

	/**
	 * used primarily to display year only.
	 *
	 * @param text
	 *            the text
	 * @return 4 first chars of string if exist
	 */
	public static String nv4(String text) {
		if (text == null) {
			return "";
		}
		if (text.length() < 4) {
			return text;
		}
		return text.substring(0, 4);
	}

	/**
	 * check if name begins with von part.
	 *
	 * @param name
	 *            to check
	 * @return parts
	 */
	public static int isKnownPrefix(String name) {
		final String[] prexesu = Resurses.getString("NAME_VON").split(";");
		final String[] prexes = new String[prexesu.length];
		int ii = prexes.length;
		int len = 0;
		// lets put them in order so that longest strings comes first
		while (ii > 0) {

			len++;
			for (final String element : prexesu) {
				if (element.length() == len) {
					ii--;
					prexes[ii] = element;
				}
			}
		}

		for (final String prexe : prexes) {
			if (name.equalsIgnoreCase(prexe)) {
				return prexe.length();
			}
			if (name.toLowerCase().startsWith(prexe + " ")) {
				return prexe.length();
			}
		}
		return 0;
	}

	private static String[] patronymeEnds = null; // {"poika","son","dotter","tytär"};

	/**
	 * Extract patronyme.
	 *
	 * @param noticeGivenName
	 *            the notice given name
	 * @param patronymePart
	 *            the patronyme part
	 * @return patronym for true, else givenname
	 */
	public static String extractPatronyme(String noticeGivenName, boolean patronymePart) {

		if (patronymeEnds == null) {

			final String tmp = Resurses.getString("PATRONYM_ENDINGS");
			patronymeEnds = tmp.split(";");
		}
		if (noticeGivenName == null) {
			return null;
		}
		int j;
		String trimmedName = null;
		final int nl = noticeGivenName.length();
		if (noticeGivenName.endsWith(".") && (nl > 1)) {
			trimmedName = noticeGivenName.substring(0, nl - 1);
		} else {
			trimmedName = noticeGivenName;
		}
		for (final String patronymeEnd : patronymeEnds) {
			if (trimmedName.endsWith(patronymeEnd)) {
				j = trimmedName.lastIndexOf(' ');
				if (j > 0) {
					if (patronymePart) {
						return noticeGivenName.substring(j + 1);
					}
					return noticeGivenName.substring(0, j).trim();
				} else {
					if (patronymePart) {
						return noticeGivenName;
					}
					return noticeGivenName;
				}
			}
		}
		if (patronymePart) {
			return null;
		}

		return noticeGivenName.trim();
	}

	/**
	 * command to start the external application for the file.
	 *
	 * @param url
	 *            the url
	 */
	public static void openExternalFile(String url) {

		if (Suku.kontroller.isRemote()) {
			final StringBuilder sb = new StringBuilder();
			sb.append(Resurses.getString("WEBSTART_OPEN"));
			sb.append("\n");
			sb.append(url);

			JOptionPane.showMessageDialog(Suku.getFrame(), sb.toString());
			return;

		}

		try {

			final String os = System.getProperty("os.name");
			// System.out.println("OS:" + os);
			if (os.toLowerCase().indexOf("windows") >= 0) {

				final String[] cmds = { "rundll32", "url.dll,FileProtocolHandler", url };
				final Process p = Runtime.getRuntime().exec(cmds);
				final int result = p.waitFor();
				if (result != 0) {
					final StringBuilder sb = new StringBuilder();
					sb.append(Resurses.getString("FAILED_TO_OPEN"));
					sb.append("\n");
					sb.append(url);

					JOptionPane.showMessageDialog(Suku.getFrame(), sb.toString());
					return;
				}

			} else if (os.toLowerCase().indexOf("mac") >= 0) {

				// this should work on mac

				final String[] macs = { "open", "" };
				macs[1] = url;
				final Process p = Runtime.getRuntime().exec(macs);
				p.waitFor();
			}

			// Properties props = System.getProperties();
			// Enumeration e = props.keys();
			//
			// while (e.hasMoreElements()) {
			// String key = (String) e.nextElement();
			// String value = props.getProperty(key);
			//
			// System.out.println(key + ":" + value);
			//
			// }

		} catch (final Throwable t) {
			logger.log(Level.INFO, "rundll32", t);

		}
	}

	private static SukuTypesModel types = null;

	/**
	 * reset the model so next request reloads it.
	 */
	public static void resetSukuModel() {
		types = null;
	}

	/**
	 * Type instance.
	 *
	 * @return the types model
	 */
	public static SukuTypesModel typeInstance() {

		if (types == null) {
			types = new SukuTypesModel();
		}
		return types;
	}

	/**
	 *
	 * filenames in the backup is prefixed by a number (numbers) like
	 *
	 * 76_myImage.jpg
	 *
	 * of 76_456_myImage.jpg
	 *
	 * The tidying removes the number and returns myImage.jpg
	 *
	 * @param name
	 *            raw filename
	 * @return tidy file name
	 */
	public static String tidyFileName(String name) {

		if (name == null) {
			return null;
		}
		int idx = 0;
		int point = name.lastIndexOf(".");
		if (point < 0) {
			return name;
		}
		while ((idx = name.indexOf("_")) > 0) {

			point = name.lastIndexOf(".");
			if (idx >= point) {
				return name;
			}

			try {
				Integer.parseInt(name.substring(0, idx));
				name = name.substring(idx + 1);
			} catch (final NumberFormatException ne) {
				return name;
			}

		}
		return name;
	}

	/**
	 * Scale image.
	 *
	 * @param immPath
	 *            the imm path
	 * @param image
	 *            the image
	 * @param pWidth
	 *            the p_width
	 * @param pHeight
	 *            the p_height
	 * @return the buffered image
	 * @throws Exception
	 *             the exception
	 */
	public static BufferedImage scaleImage(String immPath, BufferedImage image, int pWidth, int pHeight)
			throws Exception {
		return scaleImage(immPath, image, pWidth, pHeight, 0);
	}

	//
	/**
	 * This method has been copied from
	 * http://www.webmaster-talk.com/coding-forum
	 * /63227-image-resizing-in-java.html#post301529 made by Rick Palmer
	 * Modified to use java ImageIO for output
	 *
	 * @param immPath
	 *            the imm path
	 * @param image
	 *            the image
	 * @param pWidth
	 *            the p_width
	 * @param pHeight
	 *            the p_height
	 * @param trailerHeight
	 *            the trailer_height
	 * @return the scaled image
	 * @throws Exception
	 *             the exception
	 */
	public static BufferedImage scaleImage(String immPath, BufferedImage image, int pWidth, int pHeight,
			int trailerHeight) throws Exception {

		int thumbWidth = pWidth;
		int thumbHeight = pHeight;

		// Make sure the aspect ratio is maintained, so the image is not skewed
		final double thumbRatio = (double) thumbWidth / (double) thumbHeight;
		final int imageWidth = image.getWidth(null);
		final int imageHeight = image.getHeight(null);
		final double imageRatio = (double) imageWidth / (double) imageHeight;
		if (thumbRatio < imageRatio) {
			thumbHeight = (int) (thumbWidth / imageRatio);
		} else {
			thumbWidth = (int) (thumbHeight * imageRatio);
		}

		if ((immPath != null) && !immPath.isEmpty()) {
			return magickScaled(immPath, image, thumbWidth, thumbHeight, trailerHeight);
		}

		// Draw the scaled image
		final BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight + trailerHeight,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setColor(Color.white);
		graphics2D.fillRect(0, 0, thumbImage.getWidth(), thumbImage.getHeight());
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);

		final ByteArrayOutputStream outimg = new ByteArrayOutputStream();
		ImageWriter writer = null;
		final java.util.Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
		if (iter.hasNext()) {
			writer = iter.next();
		} else {
			return null;
		}
		final ImageOutputStream ios = ImageIO.createImageOutputStream(outimg);
		writer.setOutput(ios);
		final ImageWriteParam parimg = new JPEGImageWriteParam(java.util.Locale.getDefault());

		parimg.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

		// String typd[] = parimg.getCompressionQualityDescriptions();
		// String typs[] = parimg.getCompressionTypes();
		// float typf[] = parimg.getCompressionQualityValues();

		// parimg.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
		parimg.setCompressionQuality(1.0f);

		writer.write(null, new IIOImage(thumbImage, null, null), parimg);

		return thumbImage;

	}

	private static BufferedImage magickScaled(String immPath, BufferedImage image, int pWidth, int pHeight,
			int trailerHeight) throws IOException, InterruptedException {

		final File fin = File.createTempFile("Finfamily", ".jpg");

		final File fout = File.createTempFile("Finfamily", ".jpg");

		final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
		byte[] resultImageAsRawBytes = null;
		// W R I T E
		ImageIO.write(image, "jpeg", baos);
		// C L O S E
		baos.flush();
		resultImageAsRawBytes = baos.toByteArray();

		FileOutputStream fos;

		fos = new FileOutputStream(fin);
		// fos.write(pp.getMediaData());
		fos.write(resultImageAsRawBytes);
		fos.close();

		final Runtime rt = Runtime.getRuntime();

		final StringBuilder stv = new StringBuilder();
		final int spaceExist = immPath.indexOf(" ");
		if (spaceExist >= 0) {
			stv.append("\"");
			stv.append(immPath);
			stv.append("\" ");
		} else {
			stv.append(immPath);
			stv.append(" ");
		}
		if (trailerHeight == 0) {
			stv.append("-resize " + pWidth);
		} else {
			stv.append("-resize " + pWidth);
			stv.append("x" + (pHeight));
			stv.append(" -background white");
			stv.append(" -extent " + pWidth + "x" + (pHeight + trailerHeight));
		}
		stv.append(" ");
		stv.append(fin.getAbsolutePath());
		stv.append(" ");
		stv.append(fout.getAbsolutePath());

		logger.fine(stv.toString());

		final Process pr = rt.exec(stv.toString());

		// Vector<String> rtv = new Vector<String>();
		// rtv.add(immPath);
		// rtv.add("-resize ");
		// rtv.add(fin.getAbsolutePath());
		// rtv.add(fout.getAbsolutePath());
		// Process pr = rt.exec(rtv.toArray(new String[0]), null, null);

		final BufferedReader input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		String line = null;
		while ((line = input.readLine()) != null) {
			logger.info(line);
		}
		final int exitVal = pr.waitFor();

		logger.fine("conversion from " + fin.getAbsolutePath() + " to " + fout.getAbsolutePath() + " resulted in "
				+ exitVal);

		final BufferedImage nxtImg = ImageIO.read(fout);

		fin.delete();
		fout.delete();

		return nxtImg;
	}

	/**
	 * A Common method for debugging using System.out.println("text");
	 *
	 * @param source
	 *            the source
	 * @param text
	 *            to print
	 */
	@SuppressWarnings("unused")
	public static void println(Object source, String text) {
		if (false) {
			String name = (source == null) ? "" : source.getClass().getName() + " :";
			final int ii = name.lastIndexOf(".");
			if (ii > 0) {
				name = name.substring(ii + 1);
			}

			System.out.println(name + text);
		}
	}

	/**
	 * remove diacritcs from text.
	 *
	 * @param text
	 *            the text
	 * @return the string
	 */
	public static String toUsAscii(String text) {
		if (text == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < text.length(); i++) {
			String c = text.substring(i, i + 1);
			if (c.equals("å")) {
				c = "a";
			} else if (c.equals("ä")) {
				c = "a";
			} else if (c.equals("ö")) {
				c = "o";
			} else if (c.equals("Å")) {
				c = "A";
			} else if (c.equals("Ä")) {
				c = "A";
			} else if (c.equals("Ö")) {
				c = "O";
			} else if (c.compareTo(" ") <= 0) {
				c = "_";
			} else if (c.compareTo("z") > 0) {
				c = "x";
			}
			sb.append(c);

		}
		return sb.toString();
	}

	/**
	 * Graphviz do.
	 *
	 * @param parent
	 *            the parent
	 * @param exeTask
	 *            the exe task
	 * @param infile
	 *            the infile
	 * @param endi
	 *            the endi
	 * @return success status from process
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public static int graphvizDo(JFrame parent, String exeTask, String infile, String endi)
			throws IOException, InterruptedException {
		String filetype = "jpeg";
		final int lastDot = endi.lastIndexOf(".");
		if (lastDot > 0) {
			filetype = endi.substring(lastDot + 1);
			if (filetype.length() > 10) {
				filetype = "jpeg";
			}
		}

		final Runtime rt = Runtime.getRuntime();
		final Vector<String> rtv = new Vector<String>();
		rtv.add(exeTask);
		rtv.add("-T" + filetype);

		String dirname = null;
		File dir = null;
		final int dirIdx = infile.replace('\\', '/').lastIndexOf('/');
		if (dirIdx > 0) {
			dirname = infile.substring(0, dirIdx);
			dir = new File(dirname);
			// infile = infile.substring(dirIdx + 1);
		}

		rtv.add(infile);

		rtv.add("-o");
		rtv.add(endi);

		final Process pr = rt.exec(rtv.toArray(new String[0]), null, dir);
		// boolean dont = false;
		// if (dont) {
		// int counter = 0;
		// int exitVal = -1;
		// while (counter >= 0) {
		// //
		// // this loop is here because dot seemed to hang up
		// // sometimes. It happened with åäö in images path
		// try {
		// counter++;
		// if (counter > 250) {
		// counter = -1;
		// pr.destroy();
		// }
		// Thread.sleep(40);
		// exitVal = pr.exitValue();
		// break;
		//
		// } catch (Exception ie) {
		// if (counter > 240) {
		// logger.info(ie.getMessage() + " for "
		// + infile);
		// }
		// }
		// }
		// }
		final BufferedReader input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		String line = null;
		final StringBuilder respo = new StringBuilder();
		while ((line = input.readLine()) != null) {
			if (respo.length() > 0) {
				respo.append("\n");
			}
			respo.append(line);
			logger.info(line);
		}
		final int exitVal = pr.waitFor();

		logger.info("conversion to " + endi + " resulted in " + exitVal);
		if ((exitVal != 0) || (respo.length() > 0)) {
			final SukuPad pad = new SukuPad(parent, "Graphviz response [" + exitVal + "]\n\n" + respo.toString());
			pad.setVisible(true);

		} else {
			openExternalFile(endi);

		}
		return exitVal;
	}

	public static String dateToDb(String sdat) {
		if (sdat == null) {
			return "";
		}

		final Date dat = stringToDate(sdat);
		final String dff = "yyyy-MM-dd";
		final SimpleDateFormat sf = new SimpleDateFormat(dff);
		StringBuffer sb = new StringBuffer();
		sb = sf.format(dat, sb, new FieldPosition(0));
		return sb.toString();
	}

	public static String dateToString(Date dat) {
		if (dat == null) {
			return "";
		}

		final String df = Resurses.getDateFormat();
		String dff = "yyyy.MM.dd";
		if (df != null) {
			if (df.equals("FI")) {
				dff = "dd.MM.yyyy";
			} else if (df.equals("UK")) {
				dff = "dd/MM/yyyy";
			} else if (df.equals("US")) {
				dff = "MM/dd/yyyy";
			}
		}
		final SimpleDateFormat sf = new SimpleDateFormat(dff);
		StringBuffer sb = new StringBuffer();
		sb = sf.format(dat, sb, new FieldPosition(0));
		return sb.toString();
	}

	public static Date stringToDate(String dat) {
		final String df = Resurses.getDateFormat();
		String dff = "yyyy.MM.dd";
		if (df != null) {
			if (df.equals("FI")) {
				dff = "dd.MM.yyyy";
			} else if (df.equals("UK")) {
				dff = "dd/MM/yyyy";
			} else if (df.equals("US")) {
				dff = "MM/dd/yyyy";
			}
		}
		final SimpleDateFormat sf = new SimpleDateFormat(dff);
		try {
			return sf.parse(dat);
		} catch (final ParseException e) {
			return null;
		}
	}

}
