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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.SukuData;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * <h1>GenGraph Report</h1>.
 */
public class GenGraphReport extends CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor for GenGraphReport.
	 *
	 * @param caller
	 *            the caller
	 * @param typesTable
	 *            the types table
	 * @param repoWriter
	 *            the repo writer
	 */
	public GenGraphReport(ReportWorkerDialog caller, SukuTypesTable typesTable, ReportInterface repoWriter) {
		super(caller, typesTable, repoWriter);
	}

	/**
	 * execute the report.
	 */
	@Override
	public void executeReport() {

		// this will create the output file stream
		if (!Suku.kontroller.createLocalFile("xls")) {
			return;
		}

		final int genAnc = caller.getOtherPane().getAncestors();
		final int genDesc = caller.getOtherPane().getDescendants();
		final int youngFrom = caller.getOtherPane().getYoungFrom();

		final boolean adopted = caller.getOtherPane().isAdopted();
		final boolean parents = caller.getOtherPane().isParents();
		final boolean spouses = caller.getOtherPane().isSpouses();
		final boolean givenName = caller.getOtherPane().isGivenname();
		final boolean surname = caller.getOtherPane().isSurname();
		final boolean occu = caller.getOtherPane().isOccupation();
		final boolean lived = caller.getOtherPane().isLived();
		final boolean place = caller.getOtherPane().isPlace();
		final boolean married = caller.getOtherPane().isMarried();

		logger.info("asked for " + genAnc + " ancestors, " + genDesc + " descendants and " + youngFrom
				+ " backwards generations");

		try {
			//
			// here read the subject person
			//
			// SukuData is a holder of certain classes used mainly to transfer
			// these from
			// server part to client part and vice versa
			//
			// below command fetches 1 PersonShortData item into array
			// vlist.pers
			// and all its Relation into array
			// vlist.relation
			final SukuData vlist = caller.getKontroller().getSukuData("cmd=person", "mode=relations",
					"pid=" + caller.getPid(), "lang=" + Resurses.getLanguage());

			// logger.info("GenGraphReport");

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

			final Label label = new Label(0, 0, "Tulos");
			sheet.addCell(label);
			int row = 2;
			for (int i = 0; i < vlist.pers.length; i++) {
				final PersonShortData pp = vlist.pers[i];

				row = addPersonToSheet(sheet, row, pp);

				// now for demo lets print name of father
				// not adopted ones

				if (vlist.relations != null) {
					// this is a sample demo to show how to get father for
					// person in vlist
					for (final Relation relation : vlist.relations) {
						final Relation r = relation; // here is relation j
						if (r.getTag().equals("FATH") && !isAdopted(r)) {
							// now r contains the Relation for father
							final SukuData fdata = caller.getKontroller().getSukuData("cmd=person", "mode=relations",
									"pid=" + r.getRelative(), "lang=" + Resurses.getLanguage());
							// read father into fdata
							final PersonShortData ppf = fdata.pers[i];
							row = addPersonToSheet(sheet, row, ppf); // print
							// father
							// to
							// sheet

						}
					}

				}
			}

			// All sheets and cells added. Now write out the workbook
			workbook.write();
			workbook.close();
			bstr.close();

			final String report = Suku.kontroller.getFilePath();
			Utils.openExternalFile(report);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SukuException e) {
			logger.log(Level.INFO, Resurses.getString(Resurses.CREATE_REPORT), e);
			JOptionPane.showMessageDialog(caller, Resurses.getString(Resurses.CREATE_REPORT) + ":" + e.getMessage());
			return;
		}

	}

	private int addPersonToSheet(WritableSheet sheet, int row, PersonShortData pp)
			throws WriteException, RowsExceededException {
		Label label;
		final String bdate = pp.getBirtDate() == null ? null : pp.getBirtDate().substring(0, 4);
		final String ddate = pp.getDeatDate() == null ? null : pp.getDeatDate().substring(0, 4);

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
		row++;
		label = new Label(1, row, sb.toString());

		sheet.addCell(label);
		return row;
	}

	/**
	 * Check if this describes an adoption
	 *
	 * @param rela
	 * @return true if relation describes an adoption
	 */
	private boolean isAdopted(Relation rela) {
		if (rela.getNotices() == null) {
			return false; // adoption is in the RelationNotice
		}
		for (int i = 0; i < rela.getNotices().length; i++) {
			if (rela.getNotices()[i].getTag().equals("ADOP")) {
				return true;
			}
		}
		return false;
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
}
