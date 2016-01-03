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

package fi.kaila.suku.report;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesModel;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 *
 * <h1>Descendant List</h1>
 *
 * <p>
 * The descendant list creates an excel report of the subjects descendans in a
 * compresssed format to give user a view of the database.
 * </p>
 *
 * <p>
 * The report includes persons generation, birthyear, name and information on
 * selected notices that person has.
 * </p>
 *
 * @author Kalle
 *
 */
public class DescendantLista extends CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Construcor for Descendant report.
	 *
	 * @param caller
	 *            the caller
	 * @param typesTable
	 *            the types table
	 * @param repoWriter
	 *            the repo writer
	 */
	public DescendantLista(ReportWorkerDialog caller, SukuTypesTable typesTable, ReportInterface repoWriter) {
		super(caller, typesTable, repoWriter);
	}

	/**
	 * execute the report.
	 */
	@Override
	public void executeReport() {
		SukuData vlist = null;

		if (!Suku.kontroller.createLocalFile("xls")) {
			return;
		}
		try {
			vlist = caller.getKontroller().getSukuData("cmd=crlista", "type=" + Resurses.CMD_DESC_TYPE,
					"pid=" + caller.getPid());
		} catch (final SukuException e) {
			logger.log(Level.INFO, Resurses.getString(Resurses.CREATE_REPORT), e);
			JOptionPane.showMessageDialog(caller, Resurses.getString(Resurses.CREATE_REPORT) + ":" + e.getMessage());
			return;
		}
		final SukuTypesModel types = Utils.typeInstance();
		final int alltags = types.getTypesTagsCount();
		final ArrayList<String> tname = new ArrayList<String>();
		final ArrayList<String> ttag = new ArrayList<String>();

		for (int i = 0; i < alltags; i++) {
			final String tag = typesTable.getTypesTag(i);
			if (typesTable.isType(tag, 2)) {
				ttag.add(tag);
				tname.add(typesTable.getTagName(tag));
			}
		}

		logger.info("Descendant lista for [" + caller.getPid() + "]");

		try {

			final BufferedOutputStream bstr = new BufferedOutputStream(Suku.kontroller.getOutputStream());
			final WritableWorkbook workbook = Workbook.createWorkbook(bstr);
			// WritableWorkbook workbook = Workbook.createWorkbook(new
			// File("output.xls"));

			final WritableSheet sheet = workbook.createSheet("DescLista", 0);

			// Create a cell format for Times 16, bold and italic
			final WritableFont arial10italic = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, true);
			final WritableCellFormat italic10format = new WritableCellFormat(arial10italic);

			final WritableFont arial10bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
			final WritableCellFormat italic10bold = new WritableCellFormat(arial10bold);

			Label label = new Label(0, 0, "Pid");
			sheet.addCell(label);
			label = new Label(1, 0, "Gen");
			sheet.addCell(label);
			label = new Label(2, 0, "Birt");
			sheet.addCell(label);
			label = new Label(3, 0, "Group");
			sheet.addCell(label);
			label = new Label(4, 0, "Refn");
			sheet.addCell(label);
			final WritableSheet wsh = sheet;

			int col = 0;
			final int tagcol = 5;
			for (col = 0; col < (tname.size() + 32); col++) {
				wsh.setColumnView(col, 5);
			}
			for (col = 0; col < tname.size(); col++) {
				label = new Label(tagcol + col, 0, tname.get(col));
				sheet.addCell(label);
			}

			col += tagcol;
			final int genpids[] = new int[64];
			final int genspids[] = new int[64];
			final String gensex[] = new String[64];
			for (int i = 0; i < genpids.length; i++) {
				genpids[i] = 0;
				genspids[i] = 0;
				gensex[i] = "U";
			}
			final ArrayList<ListPerson> lpp = new ArrayList<ListPerson>();
			final ArrayList<ListPerson> lspouses = new ArrayList<ListPerson>();
			for (int i = 0; i < vlist.pidArray.length; i++) {
				final ListPerson lp = new ListPerson(vlist.pers[i], vlist.pidArray[i], vlist.generalArray[i]);
				if (lp.tag.equals("WIFE") || lp.tag.equals("HUSB")) {
					lspouses.add(lp);
				} else {
					int mymp = -1;
					if (lp.gene > 0) {

						for (mymp = 0; mymp < lspouses.size(); mymp++) {
							final ListPerson ppp = lspouses.get(mymp);
							int morsa = lp.ps.getFatherPid();
							if (gensex[lp.gene - 1].equals("M")) {
								morsa = lp.ps.getMotherPid();
							}
							if (morsa == ppp.ps.getPid()) {
								// this is my "mother"
								break;
							}
						}
						if ((mymp >= 0) && (mymp < lspouses.size())) {

							final ListPerson lps = lspouses.get(mymp);
							lpp.add(lps);
							lspouses.remove(mymp);
							genspids[lps.gene] = lps.ps.getPid();
						}

					}

					// first flush all spouses for the generation
					for (mymp = 0; mymp < lspouses.size(); mymp++) {
						final ListPerson ppp = lspouses.get(mymp);
						if (ppp.gene >= lp.gene) {
							break;
						}
					}
					while (lspouses.size() > mymp) {
						final int lasidx = lspouses.size() - 1;
						lpp.add(lspouses.get(lasidx));
						lspouses.remove(lasidx);
					}

					lpp.add(lp);
					genpids[lp.gene] = lp.ps.getPid();
					gensex[lp.gene] = lp.ps.getSex();

					if ((lp.gene > 0) && (lp.ps.getFatherPid() != genspids[lp.gene - 1])
							&& (lp.ps.getMotherPid() != genspids[lp.gene - 1])) {
						lp.noParent = true;
					}
				}
			}

			Number number;
			for (int i = 0; i < lpp.size(); i++) {
				final int gen = lpp.get(i).gene;
				final PersonShortData pp = lpp.get(i).ps;
				final String text = lpp.get(i).tag;
				final boolean noPare = lpp.get(i).noParent;
				number = new Number(0, i + 1, pp.getPid());
				sheet.addCell(number);

				number = new Number(1, i + 1, gen);
				sheet.addCell(number);
				final String grp = pp.getGroup();
				if (grp != null) {
					label = new Label(3, i + 1, grp);
					sheet.addCell(label);
				}
				final String refn = pp.getRefn();
				if (refn != null) {
					label = new Label(4, i + 1, refn);
					sheet.addCell(label);
				}
				final int byear = pp.getBirtYear();
				if (byear > 0) {
					number = new Number(2, i + 1, byear);
					sheet.addCell(number);
				}
				// label = new Label(2, i+1, text);
				// sheet.addCell(label);
				final int coln = col + gen;
				final String bdate = pp.getBirtDate() == null ? null : Utils.textDate(pp.getBirtDate(), false);
				// label = new Label(col, i+1,date );
				// sheet.addCell(label);
				// col++;
				final String ddate = pp.getDeatDate() == null ? null : Utils.textDate(pp.getDeatDate(), false);
				// label = new Label(col, i+1, date);
				// sheet.addCell(label);
				//
				// col++;

				for (int jj = 0; jj < ttag.size(); jj++) {
					final String tagv = pp.tagValue(ttag.get(jj));
					if (tagv != null) {

						label = new Label(jj + tagcol, i + 1, tagv);
						sheet.addCell(label);
					}
				}
				if (noPare) {
					label = new Label(coln - 1, i + 1, "?", italic10bold);
					sheet.addCell(label);
				}
				final StringBuilder sb = new StringBuilder();
				sb.append(pp.getAlfaName());
				if (bdate != null) {
					sb.append(" ");
					sb.append(bdate);
				}
				if (ddate != null) {
					sb.append("-");
					sb.append(ddate);
				}
				if (text.equals("CHIL")) {
					label = new Label(coln, i + 1, sb.toString(), italic10bold);
				} else {
					label = new Label(coln, i + 1, sb.toString(), italic10format);
				}

				sheet.addCell(label);
			}

			// All sheets and cells added. Now write out the workbook
			workbook.write();
			workbook.close();
			bstr.close();

			final String report = Suku.kontroller.getFilePath();
			Utils.openExternalFile(report);
		} catch (final Exception e) {
			logger.log(Level.WARNING, "descendant lista", e);
			JOptionPane.showMessageDialog(caller, Resurses.getString("REPORT.LISTA.DESCLISTA") + ":" + e.getMessage());
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (RowsExceededException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (WriteException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
		}

		// repoWriter.createReport();
		//
		// repoWriter.closeReport();
		//
		//

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.report.CommonReport#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b) {
		// not implemented here

	}

	/**
	 * Temp Storage for one person info.
	 *
	 * @author kalle
	 */
	class ListPerson {

		/** The ps. */
		PersonShortData ps = null;

		/** The gene. */
		int gene = 0;

		/** The tag. */
		String tag = null;

		/** The no parent. */
		boolean noParent = false;

		/**
		 * Instantiates a new list person.
		 *
		 * @param ps
		 *            the ps
		 * @param gene
		 *            the gene
		 * @param tag
		 *            the tag
		 */
		ListPerson(PersonShortData ps, int gene, String tag) {
			this.ps = ps;
			this.gene = gene;
			this.tag = tag;
		}

	}

}
