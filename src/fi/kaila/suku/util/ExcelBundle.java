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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

/**
 * A FinFamily specific resource bundle class using Excel file for input.
 *
 * @author Kalle
 */
public class ExcelBundle {

	/** The bundle. */
	HashMap<String, String> bundle = null;

	private static Logger logger = Logger.getLogger(ExcelBundle.class.getName());

	/**
	 * Gets the bundle map.
	 *
	 * @return the map
	 */
	public HashMap<String, String> getBundleMap() {
		return bundle;
	}

	/**
	 * Gets the string.
	 *
	 * @param name
	 *            the name
	 * @return value of resource
	 */
	public String getString(String name) {
		if (bundle == null) {
			return name;
		}
		final String value = bundle.get(name);
		if (value == null) {
			return name;
		}
		return value;
	}

	private static String[] langCodes = null;
	private static String[] langNames = null;

	/**
	 * Gets the lang codes.
	 *
	 * @return the lang codes
	 */
	public static String[] getLangCodes() {
		return langCodes;
	}

	/**
	 * Gets the lang names.
	 *
	 * @return the lang names
	 */
	public static String[] getLangNames() {
		return langNames;
	}

	/**
	 * Gets the lang list.
	 *
	 * @return the lang list
	 */
	public static String[] getLangList() {
		if (langNames == null) {
			return null;
		}
		final Vector<String> vv = new Vector<String>();
		// String tmp[] = new String[langNames.length];
		for (int i = 0; i < langNames.length; i++) {
			if (!langCodes[i].isEmpty() && !langNames[i].isEmpty()) {
				vv.add(langCodes[i] + ";" + langNames[i]);
			}
		}
		return vv.toArray(new String[0]);
	}

	/**
	 * Import bundle.
	 *
	 * @param path
	 *            the path
	 * @param page
	 *            the page
	 * @param locale
	 *            the locale
	 */
	public void importBundle(String path, String page, Locale locale) {
		final WorkbookSettings ws = new WorkbookSettings();
		ws.setCharacterSet(0);
		ws.setEncoding("ISO-8859-1");
		// ws.setEncoding("UTF-8");

		ws.setCharacterSet(0);
		String myLocale = null;
		bundle = new HashMap<String, String>();
		if (locale != null) {
			myLocale = locale.getLanguage();
		}
		InputStream in = null;

		try {

			if (path == null) {
				// System.out.println("excelkbundle1: " + path);
				in = this.getClass().getResourceAsStream("/excel/FinFamily.xls");
			} else {
				in = new FileInputStream(path);
			}
			// System.out.println("excelkbundle2: " + path);
			final Workbook workbook = Workbook.getWorkbook(in, ws);

			final Sheet sheet = workbook.getSheet(page);
			int colCount;
			int rowCount;
			String[] header;
			int rivi;
			int col;
			int defCol = 1;
			if (sheet != null) {
				colCount = sheet.getColumns();

				rowCount = sheet.getRows();
				header = new String[colCount];
				for (col = 0; col < colCount; col++) {
					final Cell x0 = sheet.getCell(col, 0);
					header[col] = null;
					if (x0 != null) {
						header[col] = x0.getContents();
						if ((myLocale != null) && myLocale.equals(header[col])) {
							defCol = col;
						}
					}

				}
				// colCount = col - 1;
				langCodes = new String[colCount - 1];
				langNames = new String[colCount - 1];

				for (rivi = 0; rivi < rowCount; rivi++) {

					final Cell ac1 = sheet.getCell(0, rivi);

					final String a1 = ac1.getContents();

					if ((a1 != null) && !a1.isEmpty()) {
						Cell xc1 = sheet.getCell(defCol, rivi);
						String x1 = xc1.getContents();
						if ((x1 == null) || x1.isEmpty()) {
							xc1 = sheet.getCell(1, rivi);
							x1 = xc1.getContents();
						}

						bundle.put(a1, x1);

						if (a1.equals("LANCODE")) {
							for (int i = 1; i < colCount; i++) {
								final Cell acx = sheet.getCell(i, rivi);

								final String ax = acx.getContents();
								langCodes[i - 1] = ax;

							}
						}
						if (a1.equals("LANGUAGE")) {
							for (int i = 1; i < colCount; i++) {
								final Cell acx = sheet.getCell(i, rivi);

								final String ax = acx.getContents();
								langNames[i - 1] = ax;
							}
						}
					}
				}
			}

			workbook.close();
			in.close();
		} catch (final Throwable e) {

			logger.log(Level.WARNING, "Excel bundle", e);

		}

	}

}
