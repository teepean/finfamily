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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.report.style.MainPersonText;
import fi.kaila.suku.report.style.TableHeaderText;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * <h1>Descendant report creator</h1>
 *
 * The descendant report structure is creted here.
 *
 * @author Kalle
 */
public class ExportReport extends CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor for Descendant report.
	 *
	 * @param caller
	 *            the caller
	 * @param typesTable
	 *            the types table
	 * @param repoWriter
	 *            the repo writer
	 */
	public ExportReport(ReportWorkerDialog caller, SukuTypesTable typesTable, ReportInterface repoWriter) {
		super(caller, typesTable, repoWriter);

	}

	/**
	 * execute the report.
	 *
	 * @throws SukuException
	 *             the suku exception
	 */
	@Override
	public void executeReport() throws SukuException {

		int pidArray[];
		if (caller.getPid() > 0) {
			pidArray = new int[1];
			pidArray[0] = caller.getPid();
		} else {
			pidArray = new int[caller.getSukuParent().getDatabaseRowCount()];

			for (int idx = 0; idx < pidArray.length; idx++) {
				pidArray[idx] = caller.getSukuParent().getDatbasePerson(idx).getPid();
			}
		}
		logger.info("Lista repo");

		textReferences = new HashMap<String, PersonInTables>();

		for (int idx = 0; idx < pidArray.length; idx++) {
			repoWriter.createReport();
			createExportTable(idx, pidArray);
			repoWriter.closeReport(pidArray[idx]);
		}
		caller.setRunnerValue("100;OK");

	}

	/**
	 * Creates the export table.
	 *
	 * @param idx
	 *            the idx
	 * @param pidCount
	 *            the pid count
	 * @throws SukuException
	 *             the suku exception
	 */
	protected void createExportTable(int idx, int[] pidCount) throws SukuException {
		BodyText bt = new TableHeaderText();

		//
		// Tässä vaan mallin vuoksi otetaan raporttiikkunasta arvo mukaan
		//
		bt.addText(Resurses.getReportString("REPORT.EXPORT.TABLE") + ":" + caller.getExportPane().getAncestors() + ":"
				+ pidCount[idx]);
		repoWriter.addText(bt);

		SukuData pdata = null;
		final StringBuilder tabOwner = new StringBuilder();

		try {
			pdata = caller.getKontroller().getSukuData("cmd=person", "pid=" + pidCount[idx],
					"lang=" + Resurses.getLanguage());
		} catch (final SukuException e1) {
			logger.log(Level.WARNING, "background reporting", e1);
			JOptionPane.showMessageDialog(caller, e1.getMessage());
			return;
		}

		final UnitNotice[] notices = pdata.persLong.getNotices();

		for (final UnitNotice nn : notices) {
			if (nn.getTag().equals("NAME")) {
				tabOwner.append(nn.getSurname());
				if (tabOwner.length() > 0) {
					tabOwner.append(" ");
				}
				tabOwner.append(nn.getGivenname());
				// break;
			}

			String xxx = nn.getMediaTitle();
			if (xxx == null) {
				xxx = "";
			}

		}
		final float prose = (idx * 100f) / pidCount.length;
		caller.setRunnerValue("" + (int) prose + ";" + tabOwner);

		bt = new MainPersonText();
		printName(bt, pdata.persLong, 2);
		repoWriter.addText(bt);

		printNotices(bt, pdata.persLong, 2, 0);
		// bt = new BodyText();
		// bt.addText("");
		repoWriter.addText(bt);
	}

	/**
	 * Used to close / hide the report writer.
	 *
	 * @param b
	 *            the new visible
	 */
	@Override
	public void setVisible(boolean b) {
		if (repoWriter instanceof JFrame) {
			final JFrame ff = (JFrame) repoWriter;
			ff.setVisible(b);
		}

	}

}
