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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class GraphvizReport.
 */
public class GraphvizReport extends CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private int reportType = 0;

	private String filePath = null;
	private static String folderName = "Graphviz_images";
	private final String imageMagickPath;

	/**
	 * Instantiates a new graphviz report.
	 *
	 * @param caller
	 *            the caller
	 * @param typesTable
	 *            the types table
	 * @param reportType
	 *            the report type
	 * @throws SukuException
	 *             the suku exception
	 */
	public GraphvizReport(ReportWorkerDialog caller, SukuTypesTable typesTable, int reportType) throws SukuException {
		super(caller, typesTable, null);
		imageMagickPath = Suku.kontroller.getPref(caller.getSukuParent(), "IMAGEMAGICK", "");
		this.reportType = reportType;

		if (!Suku.kontroller.createLocalFile("jpg;png;svg")) {
			throw new SukuException(Resurses.getString("WARN_REPORT_NOT_SELECTED"));
		}

		final String path = Suku.kontroller.getFilePath();

		final int pIdx = path.lastIndexOf("/");
		filePath = path.substring(0, pIdx);

	}

	/** The ident map. */
	LinkedHashMap<String, PersonShortData> identMap = null;

	/** The rela map. */
	LinkedHashMap<String, String> relaMap = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.report.CommonReport#executeReport()
	 */
	@Override
	public void executeReport() throws SukuException {
		final int descgen = caller.getDescendantPane().getGenerations();
		final int ancgen = caller.getAncestorPane().getGenerations();
		final boolean includeFamily = caller.getAncestorPane().getShowfamily();
		final boolean includeAdopted = caller.getDescendantPane().getAdopted();
		final boolean underlineName = caller.showUnderlineNames();
		final boolean hasDebugState = caller.getDebugState();
		Dimension maxPersonImageSize = caller.getPersonImageMaxSize();
		if (maxPersonImageSize.width == 0) {
			maxPersonImageSize = new Dimension(100, 150);
		} else {
			if (maxPersonImageSize.height == 0) {
				maxPersonImageSize.height = (maxPersonImageSize.width * 3) / 2;
			}
		}
		final boolean nameShow = typesTable.isType("NAME", 2);
		final boolean birtShow = typesTable.isType("BIRT", 2);
		final boolean deatShow = typesTable.isType("DEAT", 2);
		final boolean occuShow = typesTable.isType("OCCU", 2);
		boolean pictShow = typesTable.isType("PHOT", 2);
		if (!caller.showImages()) {
			pictShow = false;
		}
		if (pictShow) {
			final File d = new File(filePath + "/" + folderName);
			if (d.exists()) {

				if (d.isDirectory()) {
					final String[] files = d.list();
					for (final String file : files) {
						final File df = new File(filePath + "/" + file);
						df.delete();
					}
				}
			}
			d.mkdirs();
		}

		identMap = new LinkedHashMap<String, PersonShortData>();
		relaMap = new LinkedHashMap<String, String>();

		try {
			if (caller.getPid() > 0) {
				final SukuData pdata = caller.getKontroller().getSukuData("cmd=person", "pid=" + caller.getPid(),
						"lang=" + Resurses.getLanguage());
				final GraphData subj = new GraphData(pdata);

				identMap.put("I" + subj.getPid(), subj);
				if (reportType == 0) {
					addDescendantRelatives(subj, descgen - 1, includeAdopted);
				} else if (reportType == 1) {
					addAncestorRelatives(subj, ancgen - 1, includeFamily);
				} else if (reportType == 2) {
					addRelations(subj);
				} else {
					throw new SukuException("BAD REPORT TYPE");
				}
				final ByteArrayOutputStream bos = new ByteArrayOutputStream();

				bos.write("graph G {\n".getBytes("UTF-8"));
				final Set<Map.Entry<String, PersonShortData>> setti = identMap.entrySet();

				final Iterator<Map.Entry<String, PersonShortData>> it = setti.iterator();

				while (it.hasNext()) {
					int lineCount = 1;
					final Map.Entry<String, PersonShortData> entry = it.next();
					final StringBuilder sb = new StringBuilder();
					final PersonShortData pp = entry.getValue();
					sb.append("I" + pp.getPid());
					sb.append(" [shape=");
					if (pp.getSex().equals("M")) {
						sb.append("box,color=blue");
					} else {
						sb.append("ellipse,color=red");
					}
					sb.append(",style=bold,label=\"");
					if (nameShow) {
						if (pp.getGivenname() != null) {
							if (!underlineName) {
								final StringBuilder sbx = new StringBuilder();
								for (int l = 0; l < pp.getGivenname().length(); l++) {
									final char c = pp.getGivenname().charAt(l);
									if (c != '*') {
										sbx.append(c);
									}
								}
								sb.append(sbx.toString());
							} else {
								final String parts[] = pp.getGivenname().split(" ");
								String etunimi = parts[0];
								for (final String part : parts) {
									if (part.indexOf("*") > 0) {
										etunimi = part;
										break;
									}
								}
								final StringBuilder sbs = new StringBuilder();
								for (int m = 0; m < etunimi.length(); m++) {
									final char c = etunimi.charAt(m);
									if (c != '*') {
										sbs.append(c);
									}
								}
								sb.append(sbs.toString());
							}
						}
						if (pp.getPatronym() != null) {
							sb.append(" ");
							sb.append(pp.getPatronym());
						}
						if (pp.getPrefix() != null) {
							sb.append(" ");
							sb.append(pp.getPrefix());
						}
						if (pp.getSurname() != null) {
							sb.append(" ");
							sb.append(pp.getSurname());
						}
						if (pp.getPostfix() != null) {
							sb.append(" ");
							sb.append(pp.getPostfix());
						}
					} else {
						// don't show name. Show initials only
						if (pp.getGivenname() != null) {
							final String parts[] = pp.getGivenname().split(" ");
							String firstpart = parts[0];
							for (final String part : parts) {
								if (part.indexOf("*") > 0) {
									firstpart = part;
									break;
								}
							}
							sb.append(firstpart.charAt(0));

						}
						if (pp.getPrefix() != null) {
							sb.append(pp.getPrefix().charAt(0));
						}
						if (pp.getSurname() != null) {
							sb.append(pp.getSurname().charAt(0));
						}
					}
					if (birtShow) {
						if ((pp.getBirtDate() != null) || (pp.getBirtPlace() != null)) {
							sb.append("\\n");
							lineCount++;
							sb.append(typesTable.getTextValue("ANC_BORN"));
							if (pp.getBirtDate() != null) {
								sb.append(" ");
								sb.append(Utils.textDate(pp.getBirtDate(), true));
							}
							if (pp.getBirtPlace() != null) {
								sb.append(" ");
								sb.append(pp.getBirtPlace());
							}
							if (pp.getBirthCountry() != null) {
								sb.append(" ");
								sb.append(pp.getBirthCountry());
							}
						}
					}
					if (deatShow) {
						if ((pp.getDeatDate() != null) || (pp.getDeatPlace() != null)) {
							sb.append("\\n");
							lineCount++;
							sb.append(typesTable.getTextValue("ANC_DIED"));
							if (pp.getDeatDate() != null) {
								sb.append(" ");
								sb.append(Utils.textDate(pp.getDeatDate(), true));
							}
							if (pp.getDeatPlace() != null) {
								sb.append(" ");
								sb.append(pp.getDeatPlace());
							}
							if (pp.getDeatCountry() != null) {
								sb.append(" ");
								sb.append(pp.getDeatCountry());
							}
						}
					}
					if (occuShow) {
						if (pp.getOccupation() != null) {
							sb.append("\\n");
							lineCount++;
							sb.append(pp.getOccupation());
						}
					}
					sb.append("\"");
					if (pictShow) {
						if (pp.getMediaData() != null) {
							if (pp.getMediaFilename() != null) {

								final BufferedImage img = pp.getImage();
								final BufferedImage imgStamp = Utils.scaleImage(imageMagickPath, img,
										maxPersonImageSize.width, maxPersonImageSize.height, lineCount * 25);

								// O P E N
								// converting to bytes : copy-paste from
								// http://mindprod.com/jgloss/imageio.html#TOBYTES
								final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
								byte[] resultImageAsRawBytes = null;
								// W R I T E
								try {
									ImageIO.write(imgStamp, "jpeg", baos);
									// C L O S E
									baos.flush();
									img.flush();
									resultImageAsRawBytes = baos.toByteArray();

								} catch (final IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								final String imgName = pp.getMediaFilename();

								final File ff = new File(filePath + "/" + folderName + "/" + imgName);

								FileOutputStream fos;
								try {

									fos = new FileOutputStream(ff);
									// fos.write(pp.getMediaData());
									fos.write(resultImageAsRawBytes);
									fos.close();

								} catch (final FileNotFoundException e) {
									logger.log(Level.WARNING, "Image", e);
								} catch (final IOException e) {
									logger.log(Level.WARNING, "Image", e);
								}
							}

							sb.append(",image=\"");
							sb.append(folderName);
							sb.append("/");
							sb.append(pp.getMediaFilename());
							sb.append("\"");
							sb.append(",labelloc=b");
						}
					}

					sb.append("];");

					bos.write(sb.toString().getBytes("UTF-8"));
					bos.write('\n');

				}
				final Set<Map.Entry<String, String>> relat = relaMap.entrySet();
				final Iterator<Map.Entry<String, String>> itr = relat.iterator();

				while (itr.hasNext()) {
					final Map.Entry<String, String> entry = itr.next();
					bos.write(entry.getValue().getBytes("UTF-8"));
					bos.write('\n');
				}
				bos.write("}".getBytes("UTF-8"));
				final byte[] buffi = bos.toByteArray();
				final String pathjpg = Suku.kontroller.getFilePath();
				final int dotLoc = pathjpg.lastIndexOf(".");

				String pathMain = pathjpg;
				if (dotLoc >= (pathjpg.length() - 5)) {

					pathMain = pathjpg.substring(0, dotLoc);
				}

				final String pathgv = pathMain + ".gv";
				final FileOutputStream fos = new FileOutputStream(pathMain + ".gv");

				fos.write(buffi);
				fos.close();

				final String exeTask = Suku.kontroller.getPref(caller.getSukuParent(), "GRAPHVIZ", "");
				if (!exeTask.equals("")) {
					final int resu = Utils.graphvizDo(caller.getSukuParent(), exeTask, pathgv, pathjpg);
					if ((resu == 0) && !hasDebugState) {
						final File f = new File(pathgv);
						f.delete();
						if (pictShow) {
							final File d = new File(filePath + "/" + folderName);
							if (d.exists()) {
								if (d.isDirectory()) {
									if (!pathjpg.toLowerCase().endsWith(".svg")) {
										final String[] files = d.list();
										for (final String file : files) {
											final File df = new File(d.getAbsoluteFile() + "/" + file);
											df.delete();
										}
									}
								}
								d.delete();
							}
						}
					}
				}
			} else {
				JOptionPane.showMessageDialog(caller, "dblista");
			}
		} catch (final Exception e) {
			logger.log(Level.WARNING, "creating graphviz file", e);

		}
	}

	/** The ancs. */
	HashMap<Integer, PersonShortData> ancs = new HashMap<Integer, PersonShortData>();

	/** The commons. */
	Vector<PersonShortData> commons = new Vector<PersonShortData>();

	private void addRelations(GraphData subj) throws SukuException {
		final PersonLongData pers = caller.getSukuParent().getSubject();

		addParents(subj.getPid(), 0);
		addParents(pers.getPid(), 0);

		// if (commons.size() > 0) {
		for (int i = 0; i < commons.size(); i++) {
			final PersonShortData cp = commons.get(i);
			drawRelation(cp);
		}
	}

	private void drawRelation(PersonShortData cp) throws SukuException {

		identMap.put("I" + cp.getPid(), cp);
		final Vector<Integer> relas = cp.getRelapath();
		if (relas != null) {
			for (int i = 0; i < relas.size(); i++) {

				final PersonShortData p = ancs.get(relas.get(i));
				if (p != null) {
					// SukuData xdata =
					// Suku.kontroller.getSukuData("cmd=person",
					// "pid=" + relas.get(i), "mode=short");
					// PersonShortData p = xdata.pers[0];
					identMap.put("I" + p.getPid(), p);
					String color = "blue";
					if (!cp.getSex().equals("M")) {
						color = "red";
					}
					final StringBuilder sb = new StringBuilder();
					sb.append(" ");
					sb.append("I" + cp.getPid());
					sb.append(" -- ");
					sb.append("I" + p.getPid());

					sb.append("  [style=bold,color=" + color + "]; ");

					relaMap.put("I" + cp.getPid() + "I" + p.getPid(), sb.toString());
					drawRelation(p);
				}
			}
		}
	}

	private void addParents(int pid, int fromPid) throws SukuException {

		final SukuData xdata = Suku.kontroller.getSukuData("cmd=person", "pid=" + pid, "mode=short");
		final PersonShortData p = xdata.pers[0];

		caller.setRunnerValue(p.getName(true, false));
		p.addToRelapath(fromPid);
		final PersonShortData me = ancs.get(p.getPid());
		if (me != null) {
			me.addToRelapath(fromPid);
			commons.add(me);
			return;
		}

		if (p.getPid() > 0) {

			ancs.put(pid, p);
			if (p.getFatherPid() > 0) {
				addParents(p.getFatherPid(), pid);
			}
			if (p.getMotherPid() > 0) {
				addParents(p.getMotherPid(), pid);
			}
		}

	}

	// private void addParentsForX(int pid) throws SukuException {
	// SukuData xdata = Suku.kontroller.getSukuData("cmd=person",
	// "pid=" + pid, "mode=short");
	// PersonShortData p = xdata.pers[0];
	// if (p.getPid() > 0) {
	// ancs.put(pid, p);
	// if (p.getFatherPid()>0) {
	// addParentsForX(p.getFatherPid());
	// }
	// if (p.getMotherPid()>0) {
	// addParentsForX(p.getMotherPid());
	// }
	// }
	// }

	/** The remover. */
	HashMap<Integer, Integer> remover = new HashMap<Integer, Integer>();

	private void addAncestorRelatives(PersonShortData pers, int generation, boolean includeFamily)
			throws SukuException {
		final SukuData pdata = caller.getKontroller().getSukuData("cmd=person", "pid=" + pers.getPid(),
				"lang=" + Resurses.getLanguage());
		final GraphData gdata = new GraphData(pdata);

		caller.setRunnerValue(gdata.getName(true, false));

		int fatherPid = 0;
		int motherPid = 0;
		for (final Relation spo : gdata.relations) {
			if (spo.getTag().equals("FATH")) {
				fatherPid = spo.getRelative();
				remover.put(spo.getRid(), 1);
			} else if (spo.getTag().equals("MOTH")) {
				motherPid = spo.getRelative();
				remover.put(spo.getRid(), 1);
			}

		}
		if (includeFamily) {
			String spouseTag = "HUSB";
			if (pers.getSex().equals("M")) {
				spouseTag = "WIFE";
			}
			for (final Relation rela : gdata.relations) {
				final Integer wasAlready = remover.put(rela.getRid(), 1);
				if ((wasAlready == null) && rela.getTag().equals(spouseTag)) {
					final PersonShortData spou = gdata.rels.get(rela.getRelative());
					identMap.put("I" + spou.getPid(), spou);

					final StringBuilder sb = new StringBuilder();
					sb.append(" ");
					sb.append("I" + pers.getPid());
					sb.append(" -- ");
					sb.append("I" + spou.getPid());

					sb.append("  [style=bold,color=violet]; ");

					relaMap.put("I" + pers.getPid() + "I" + spou.getPid(), sb.toString());

				}
				if ((wasAlready == null) && rela.getTag().equals("CHIL")) {

					final PersonShortData chil = gdata.rels.get(rela.getRelative());
					identMap.put("I" + chil.getPid(), chil);

					final StringBuilder sb = new StringBuilder();
					sb.append(" ");
					sb.append("I" + pers.getPid());
					sb.append(" -- ");
					sb.append("I" + chil.getPid());

					sb.append("  [style=bold,color=orange]; ");

					relaMap.put("I" + pers.getPid() + "I" + chil.getPid(), sb.toString());

					int otherPid = 0;
					if (spouseTag.equals("HUSB")) {
						otherPid = chil.getFatherPid();
					} else {
						otherPid = chil.getMotherPid();
					}

					for (final Relation othe : gdata.relations) {
						if (othe.getRelative() == otherPid) {
							final StringBuilder ssb = new StringBuilder();
							ssb.append(" ");
							ssb.append("I" + otherPid);
							ssb.append(" -- ");
							ssb.append("I" + chil.getPid());

							ssb.append("  [style=bold,color=orange]; ");

							relaMap.put("I" + otherPid + "I" + chil.getPid(), ssb.toString());
						}
					}

				}

			}
		}
		if (fatherPid > 0) {
			addParentData(pers, gdata, fatherPid, generation, includeFamily);
		}
		if (motherPid > 0) {
			addParentData(pers, gdata, motherPid, generation, includeFamily);
		}

	}

	/**
	 * Adds the parent data.
	 *
	 * @param pers
	 *            of original person
	 * @param gdata
	 *            the gdata
	 * @param parePid
	 *            the pare pid
	 * @param generation
	 *            the generation
	 * @param includeFamily
	 *            the include family
	 * @throws SukuException
	 *             the suku exception
	 */
	public void addParentData(PersonShortData pers, GraphData gdata, int parePid, int generation, boolean includeFamily)
			throws SukuException {

		if (generation > 0) {
			final PersonShortData pare = gdata.rels.get(parePid);

			identMap.put("I" + pare.getPid(), pare);
			final StringBuilder sb = new StringBuilder();
			sb.append(" ");
			sb.append("I" + pare.getPid());
			sb.append(" -- ");
			sb.append("I" + pers.getPid());
			if (pare.getSex().equals("M")) {
				sb.append("  [style=bold,color=blue]; ");
			} else {
				sb.append("  [style=bold,color=red]; ");
			}
			relaMap.put("I" + pers.getPid() + "I" + pare.getPid(), sb.toString());

			addAncestorRelatives(pare, generation - 1, includeFamily);
		}
	}

	private void addDescendantRelatives(PersonShortData pers, int generation, boolean includeAdopted)
			throws SukuException {
		final SukuData pdata = caller.getKontroller().getSukuData("cmd=person", "pid=" + pers.getPid(),
				"lang=" + Resurses.getLanguage());
		final GraphData gdata = new GraphData(pdata);

		caller.setRunnerValue(gdata.getName(true, false));

		for (final Relation spo : gdata.relations) {
			if (spo.getTag().equals("WIFE") || spo.getTag().equals("HUSB")) {
				final PersonShortData spouse = gdata.rels.get(spo.getRelative());

				final SukuData sdata = caller.getKontroller().getSukuData("cmd=person", "pid=" + spouse.getPid(),
						"lang=" + Resurses.getLanguage());
				final GraphData xdata = new GraphData(sdata); // full version of
				// spouse

				identMap.put("I" + spouse.getPid(), spouse);
				final StringBuilder sb = new StringBuilder();
				sb.append(" ");
				sb.append("I" + pers.getPid());
				sb.append(" -- ");
				sb.append("I" + spouse.getPid());
				sb.append("  [style=bold,color=green]; ");
				relaMap.put("I" + pers.getPid() + "I" + spouse.getPid(), sb.toString());
				final int spogen = caller.getDescendantPane().getSpouseAncestors();
				if (spogen > 0) {

					int spoFatherPid = 0;
					int spoMotherPid = 0;
					for (final Relation spox : xdata.relations) {
						if (spox.getTag().equals("FATH")) {
							spoFatherPid = spox.getRelative();
						} else if (spox.getTag().equals("MOTH")) {
							spoMotherPid = spox.getRelative();
						}
					}
					if (spoFatherPid > 0) {
						final PersonShortData fat = xdata.rels.get(spoFatherPid);
						identMap.put("I" + spoFatherPid, fat);

						final StringBuilder sbb = new StringBuilder();
						sbb.append(" ");
						sbb.append("I" + fat.getPid());
						sbb.append(" -- ");
						sbb.append("I" + spouse.getPid());
						sbb.append("  [style=bold,color=blue]; ");
						relaMap.put("I" + fat.getPid() + "I" + spouse.getPid(), sbb.toString());

					}
					if (spoMotherPid > 0) {
						final PersonShortData fat = xdata.rels.get(spoMotherPid);
						identMap.put("I" + spoMotherPid, fat);
						final StringBuilder sbb = new StringBuilder();
						sbb.append(" ");
						sbb.append("I" + fat.getPid());
						sbb.append(" -- ");
						sbb.append("I" + spouse.getPid());
						sbb.append("  [style=bold,color=red]; ");

						relaMap.put("I" + fat.getPid() + "I" + spouse.getPid(), sbb.toString());
					}

				}

			}
		}

		if (generation > 0) {
			for (final Relation chi : gdata.relations) {
				if (chi.getTag().equals("CHIL")) {
					boolean notAdopted = true;
					if (chi.getNotices() != null) {
						for (int adop = 0; adop < chi.getNotices().length; adop++) {
							if (chi.getNotices()[adop].getTag().equals("ADOP")) {
								notAdopted = false;
								break;
							}
						}
					}
					if (notAdopted || includeAdopted) {

						final PersonShortData chil = gdata.rels.get(chi.getRelative());
						final PersonShortData prev = identMap.put("I" + chil.getPid(), chil);

						final StringBuilder sb = new StringBuilder();
						sb.append(" ");
						sb.append("I" + pers.getPid());
						sb.append(" -- ");
						sb.append("I" + chil.getPid());
						if (pers.getSex().equals("M")) {
							sb.append("  [style=bold,color=blue]; ");
						} else {
							sb.append("  [style=bold,color=red]; ");
						}
						relaMap.put("I" + pers.getPid() + "I" + chil.getPid(), sb.toString());

						for (int moth = 1; moth < gdata.relations.length; moth++) {
							if ((gdata.relations[moth].getRelative() == chil.getMotherPid())
									|| (gdata.relations[moth].getRelative() == chil.getFatherPid())) {
								final PersonShortData mother = gdata.rels.get(gdata.relations[moth].getRelative());

								if (mother != null) {
									final StringBuilder sbb = new StringBuilder();
									sbb.append(" ");
									sbb.append("I" + mother.getPid());
									sbb.append(" -- ");
									sbb.append("I" + chil.getPid());
									if (mother.getSex().equals("M")) {
										sbb.append("  [style=bold,color=blue]; ");
									} else {
										sbb.append("  [style=bold,color=red]; ");
									}
									relaMap.put("I" + mother.getPid() + "I" + chil.getPid(), sbb.toString());
								}
							}
						}

						if (prev == null) {
							addDescendantRelatives(chil, generation - 1, includeAdopted);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.report.CommonReport#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b) {

	}

	/**
	 * The Class GraphData.
	 */
	class GraphData extends PersonShortData {
		/**  */
		private static final long serialVersionUID = 1L;

		/** The relations. */
		Relation[] relations;

		/** The rels. */
		HashMap<Integer, PersonShortData> rels = new HashMap<Integer, PersonShortData>();

		/*
		 * (non-Javadoc)
		 *
		 * @see fi.kaila.suku.util.pojo.PersonShortData#getFatherPid()
		 */
		@Override
		public int getFatherPid() {
			final String tag = "FATH";
			return getParent(tag);
		}

		/**
		 * Here get the most probable parent not adopted rather than adopted
		 * First relation of greatest surety is selected
		 *
		 * @param tag
		 * @return
		 */
		private int getParent(String tag) {

			boolean isAdopted = true;
			Relation rr = null;

			if (relations != null) {
				for (final Relation r : relations) {
					if (r.getTag().equals(tag)) {
						if (rr == null) {
							rr = r;
						}
						if (r.getNotice("ADOP") == null) {
							// not adopted
							if (isAdopted) {
								rr = r;
								isAdopted = false;
							}
							if (r.getSurety() > rr.getSurety()) {
								rr = r;
							}
						} else {
							// is adopted
							if (isAdopted) {
								// continue only if adopted
								if (r.getSurety() > rr.getSurety()) {
									rr = r;
								}
							}
						}

					}
				}
				return rr.getRelative();
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see fi.kaila.suku.util.pojo.PersonShortData#getMotherPid()
		 */
		@Override
		public int getMotherPid() {
			final String tag = "MOTH";

			return getParent(tag);
		}

		/**
		 * Instantiates a new graph data.
		 *
		 * @param data
		 *            the data
		 */
		public GraphData(SukuData data) {
			super(data.persLong);
			relations = data.relations;
			for (final PersonShortData per : data.pers) {
				rels.put(per.getPid(), per);
			}
		}
	}

	/**
	 * The Class RelationData.
	 */
	class RelationData extends PersonShortData {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/** The rel path. */
		Vector<Integer> relPath = new Vector<Integer>();

	}

}
