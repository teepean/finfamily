package fi.kaila.suku.report;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.report.style.ChildHeaderText;
import fi.kaila.suku.report.style.ChildListText;
import fi.kaila.suku.report.style.ChildSpouseText;
import fi.kaila.suku.report.style.ImageText;
import fi.kaila.suku.report.style.MainPersonText;
import fi.kaila.suku.report.style.NameIndexText;
import fi.kaila.suku.report.style.SpousePersonText;
import fi.kaila.suku.report.style.SubPersonText;
import fi.kaila.suku.report.style.TableHeaderText;
import fi.kaila.suku.report.style.TableSubHeaderText;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Roman;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * <h1>Report creator</h1>
 * 
 * <p>
 * Reports architecture consist of
 * </p>
 * 
 * <ul>
 * <li>Report logical creator. Example the {@link DescendantReport}</li>
 * <li>Report Interface {@link ReportInterface}</li>
 * <li>Actual output creator. Example {@link JavaReport}</li>
 * </ul>
 * 
 * 
 * Common routines used by report generators
 * 
 * @author Kalle
 * 
 */
public abstract class CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/** The caller. */
	protected ReportWorkerDialog caller;

	/** The types table. */
	protected SukuTypesTable typesTable;

	/** The tables. */
	protected Vector<ReportUnit> tables = new Vector<ReportUnit>();
	/** The tables in a hashmap. */
	protected HashMap<Long, ReportUnit> tabMap = new HashMap<Long, ReportUnit>();
	/** The repo writer. */
	protected ReportInterface repoWriter;

	/** The person references. */
	protected HashMap<Integer, PersonInTables> personReferences = null;
	/** The person tables. */
	protected HashMap<Integer, ReportUnit> personTables = null;

	/** The text references. */
	protected HashMap<String, PersonInTables> textReferences = null;

	private final HashMap<Integer, Integer> mapper = new HashMap<Integer, Integer>();

	private final HashMap<String, PlaceInTables> places = new HashMap<String, PlaceInTables>();

	private final LinkedHashMap<String, Integer> refs = new LinkedHashMap<String, Integer>();

	private final Vector<ImageNotice> imgNotices = new Vector<ImageNotice>();
	private int referencePid = 0;

	private int imageNumber = 0;

	/**
	 * Gets the tables.
	 * 
	 * @return vector of tables
	 */
	public Vector<ReportUnit> getTables() {
		return tables;
	}

	/**
	 * Execute the report Implemented by the derived class.
	 * 
	 * @throws SukuException
	 *             the suku exception
	 */
	public abstract void executeReport() throws SukuException;

	/**
	 * Gets the person references.
	 * 
	 * @return Vector with references
	 */
	public Vector<PersonInTables> getPersonReferences() {

		Vector<PersonInTables> vv = new Vector<PersonInTables>();

		Set<Map.Entry<Integer, PersonInTables>> entriesx = personReferences
				.entrySet();
		Iterator<Map.Entry<Integer, PersonInTables>> eex = entriesx.iterator();
		while (eex.hasNext()) {
			Map.Entry<Integer, PersonInTables> entrx = eex.next();
			PersonInTables pit = entrx.getValue();
			vv.add(pit);
		}

		Set<Map.Entry<String, PersonInTables>> entriesy = textReferences
				.entrySet();
		Iterator<Map.Entry<String, PersonInTables>> eey = entriesy.iterator();
		while (eey.hasNext()) {
			Map.Entry<String, PersonInTables> entry = eey.next();
			PersonInTables pit = entry.getValue();

			String[] parts = entry.getKey().split(",");

			if (parts.length == 2) {
				pit.givenName = parts[1];
				pit.surName = parts[0];
				vv.add(pit);
			} else if (parts.length == 1) {
				pit.surName = parts[0];
				vv.add(pit);
			}
		}

		return vv;
	}

	/**
	 * finds the tables by their owners into personTables
	 */
	public void initPersonTables() {
		personTables = new HashMap<Integer, ReportUnit>();

		for (ReportUnit ru : tables) {
			personTables.put(ru.getPid(), ru);
		}

	}

	/**
	 * Gets the place references.
	 * 
	 * @return the place references
	 */
	public PlaceInTables[] getPlaceReferences() {

		ArrayList<PlaceInTables> vv = new ArrayList<PlaceInTables>();

		Set<Map.Entry<String, PlaceInTables>> entriesx = places.entrySet();
		Iterator<Map.Entry<String, PlaceInTables>> eex = entriesx.iterator();
		while (eex.hasNext()) {
			Map.Entry<String, PlaceInTables> entrx = eex.next();
			PlaceInTables pit = entrx.getValue();
			vv.add(pit);
		}

		PlaceInTables[] pits = vv.toArray(new PlaceInTables[0]);
		Arrays.sort(pits);

		return pits;

	}

	/**
	 * Gets the source list.
	 * 
	 * @return the source list
	 */
	public String[] getSourceList() {

		ArrayList<String> vv = new ArrayList<String>();

		Set<Map.Entry<String, Integer>> entriesx = refs.entrySet();
		Iterator<Map.Entry<String, Integer>> eex = entriesx.iterator();
		while (eex.hasNext()) {
			Map.Entry<String, Integer> entrx = eex.next();
			String src = entrx.getKey();

			vv.add(src);
		}

		return vv.toArray(new String[0]);

	}

	/**
	 * access to report writer.
	 * 
	 * @return the report writer
	 */
	public ReportInterface getWriter() {
		return repoWriter;
	}

	/**
	 * Prints the images.
	 */
	public void printImages() {
		if (imgNotices.size() > 0) {
			BodyText bt = new TableHeaderText();
			bt.addText("\n");
			repoWriter.addText(bt);
			bt.addText(Resurses.getReportString("INDEX_IMAGES"));
			bt.addText("\n");
			repoWriter.addText(bt);
			bt = new MainPersonText();
			for (int i = 0; i < imgNotices.size(); i++) {
				// for (ImageNotice inoti : imgNotices) {
				ImageNotice inoti = imgNotices.get(i);
				UnitNotice nn = inoti.nn;

				float prose = (i * 100f) / imgNotices.size();
				caller.setRunnerValue("" + (int) prose + ";"
						+ nn.getMediaFilename());

				if (caller.showImages()) {
					ImageText imagetx = new ImageText();
					BufferedImage img = null;
					try {
						img = nn.getMediaImage();
					} catch (IOException e) {
						logger.log(Level.WARNING, "printImages", e);

					}

					if (img != null) {
						double imh = img.getHeight();
						double imw = img.getWidth();
						double newh = 300;
						double neww = 300;

						if (imh <= newh) {
							if (imw <= neww) {
								newh = imh;
								neww = imw;

							} else {
								newh = imh * (neww / imw);
								neww = imw;
							}
						} else {
							neww = imw * (newh / imh);
						}

						/*
						 * String imgTitle = ""; if (caller.isNumberingImages())
						 * { imgTitle = Resurses .getReportString("INDEX_IMAGE")
						 * + " " + imageNumber + ". "; } if (nn.getMediaTitle()
						 * != null) { imgTitle += nn.getMediaTitle(); } String
						 * xxx = "0000" + imageNumber; String imgNamePrefix =
						 * xxx.substring(xxx .length() - 4) + "_";
						 * imagetx.setImage( imgs, nn.getMediaData(),
						 * img.getWidth(), img.getHeight(), imgNamePrefix +
						 * nn.getMediaFilename(), imgTitle, nn.getTag());
						 */

						String imgTitle = "";
						if (caller.isNumberingImages()) {
							imgTitle = Resurses.getReportString("INDEX_IMAGE")
									+ " " + inoti.imgNumber + ". ";
						}
						if (nn.getMediaTitle() != null) {
							String titl = trim(nn.getMediaTitle());
							imgTitle += titl + ".";

						}
						String xxx = "0000" + inoti.imgNumber;
						String imgNamePrefix = xxx.substring(xxx.length() - 4)
								+ "_";

						StringBuilder sm = new StringBuilder();
						sm.append(Resurses.getReportString("INDEX_IMAGE"));
						sm.append(" ");
						sm.append(inoti.imgNumber);
						sm.append(" (");
						sm.append(Resurses.getReportString("TABLE")
								.toLowerCase());
						sm.append(" ");
						sm.append(inoti.tabNo);
						sm.append("). ");
						if (nn.getMediaTitle() != null) {
							sm.append(imgTitle);
						}

						imagetx.setImage(img, nn.getMediaData(),
								img.getWidth(), img.getHeight(), imgNamePrefix
										+ nn.getMediaFilename(), sm.toString(),
								nn.getTag());
						imagetx.addText("");

					}

					repoWriter.addText(imagetx);
					if (nn.getNoteText() != null) {
						printText(bt, nn.getNoteText(), null);
					}

				} else {
					bt.addText(Resurses.getReportString("INDEX_IMAGE"), true,
							false);
					bt.addText(" " + inoti.imgNumber + " ", true, false);
					bt.addText(Resurses.getReportString("TABLE").toLowerCase(),
							true, false);
					bt.addText(" " + inoti.tabNo, true, false);
					bt.addText(". ", true, false);
					if (nn.getMediaTitle() != null) {
						bt.addText(nn.getMediaTitle(), true, false);
						bt.addText(". ", true, false);
					}

					repoWriter.addText(bt);
					if (nn.getNoteText() != null) {
						printText(bt, nn.getNoteText(), null);
					}
				}
				// if (img != null) {
				//
				// imagetx.setImage(img, nn.getMediaData(), img.getWidth(),
				// img.getHeight(), nn.getMediaFilename(), nn
				// .getMediaTitle(), nn.getTag());
				// imagetx.addText("");
				// }

				repoWriter.addText(bt);

			}
		}
	}

	/**
	 * Print name index in word document (or html)
	 * 
	 * @throws SukuException
	 */
	public void printNameIndex() throws SukuException {

		if (caller.showIndexNames()) {
			int tableOffset = caller.getDescendantPane().getStartTable();
			if (tableOffset > 0) {
				tableOffset--;
			}
			Vector<PersonInTables> vv = getPersonReferences();

			float runnervalue = 0;
			float mapsize = vv.size();
			HashMap<String, String> nms = new HashMap<String, String>();

			for (int j = 0; j < mapsize; j++) {
				PersonInTables pit = vv.get(j);
				// vv.add(pit);
				if (pit.shortPerson == null) {

					SukuData resp = Suku.kontroller.getSukuData("cmd=person",
							"mode=short", "pid=" + pit.pid);

					if (resp.pers != null) {
						pit.shortPerson = resp.pers[0];

						nms.clear();
						String testName = nv(pit.shortPerson.getPrefix()) + "|"
								+ nv(pit.shortPerson.getSurname()) + "|"
								+ nv(pit.shortPerson.getGivenname()) + "|"
								+ nv(pit.shortPerson.getPatronym()) + "|"
								+ nv(pit.shortPerson.getPostfix());
						nms.put(testName, "1");
						for (int i = 1; i < pit.shortPerson.getNameCount(); i++) {

							PersonInTables pitt = new PersonInTables(
									pit.shortPerson.getPid());
							pitt.asChildren = pit.asChildren;
							pitt.references = pit.references;
							Long[] aa = pit.getOwnerArray();
							for (Long a : aa) {
								pitt.addOwner(a);
							}

							pitt.asParents = pit.asParents;
							PersonShortData p = pit.shortPerson;
							PersonShortData alias = new PersonShortData(
									p.getPid(), p.getGivenname(i),
									p.getPatronym(i), p.getPrefix(i),
									p.getSurname(i), p.getPostfix(i),
									p.getBirtDate(), p.getDeatDate());
							pitt.shortPerson = alias;
							testName = nv(pitt.shortPerson.getPrefix()) + "|"
									+ nv(pitt.shortPerson.getSurname()) + "|"
									+ nv(pitt.shortPerson.getGivenname()) + "|"
									+ nv(pitt.shortPerson.getPatronym()) + "|"
									+ nv(pitt.shortPerson.getPostfix());

							String oldName = nms.put(testName, "1");
							if (oldName == null) {

								vv.add(pitt);
							}
						}
						nms.clear();
					}
					float prose = (runnervalue * 100f) / mapsize;
					if (prose > 100)
						prose = 100;
					caller.setRunnerValue("" + (int) prose + ";"
							+ pit.shortPerson.getAlfaName());
					runnervalue++;
				}
			}
			PersonInTables[] pits = vv.toArray(new PersonInTables[0]);
			Arrays.sort(pits);
			BodyText bt = new TableHeaderText();
			bt.addText("\n");
			repoWriter.addText(bt);
			bt.addText(Resurses.getReportString("INDEX_NAMEINDEX"));
			bt.addText("\n");
			repoWriter.addText(bt);
			bt = new NameIndexText();

			String previousSurname = null;

			for (int i = 0; i < pits.length; i++) {

				PersonInTables pit = pits[i];
				if (pit.shortPerson.getPrivacy() != null) {
					if (pit.shortPerson.getPrivacy().equals("F")) {
						PersonShortData nn = new PersonShortData(
								pit.shortPerson.getPid(),
								typesTable.getTextValue("REPORT_NOMEN_NESCIO"),
								null, null, null, null, null, null);
						pit.shortPerson = nn;
					}
				}
				String mefe = pit.getReferences(0, false, false, true,
						tableOffset);

				StringBuilder tstr = new StringBuilder();
				if (pit.shortPerson.getPrefix() != null) {
					tstr.append(pit.shortPerson.getPrefix());
					tstr.append(" ");
				}
				if (pit.shortPerson.getSurname() != null) {
					tstr.append(pit.shortPerson.getSurname());
				}
				String surname = tstr.toString();
				if (!Utils.nv(previousSurname).equalsIgnoreCase(surname)) {
					bt.addText(surname, true, false);
					repoWriter.addText(bt);
					previousSurname = surname;
				}

				StringBuilder tstg = new StringBuilder();
				if (pit.shortPerson.getGivenname() != null) {
					tstg.append(pit.shortPerson.getGivenname());
				}
				if (pit.shortPerson.getPatronym() != null) {
					if (tstg.length() > 0) {
						tstg.append(" ");
					}
					tstg.append(pit.shortPerson.getPatronym());
				}

				if (pit.shortPerson.getPostfix() != null) {
					tstg.append(" ");
					tstg.append(pit.shortPerson.getPostfix());
				}
				bt.addText("  ");
				printGivenname(bt, tstg.toString(), false);

				//
				// let's add living years here
				//
				if (caller.showIndexYears()) {
					StringBuilder yrs = new StringBuilder();
					if (pit.shortPerson.getBirtYear() > 0
							|| pit.shortPerson.getDeatYear() > 0) {
						yrs.append(" (");
						if (pit.shortPerson.getBirtYear() > 0) {
							yrs.append(pit.shortPerson.getBirtYear());
						}
						if (pit.shortPerson.getDeatYear() > 0) {
							yrs.append("-");
							yrs.append(pit.shortPerson.getDeatYear());
						}

						yrs.append(")");
						bt.addText(yrs.toString());
					}
				}

				String kefe = pit.getReferences(0, true, false, false,
						tableOffset);
				String cefe = pit.getReferences(0, false, true, false,
						tableOffset);
				String refe = kefe;

				if (pit.getOwnerString(tableOffset).isEmpty()) {

					if (refe.isEmpty()) {
						refe = cefe;
					} else {
						if (!cefe.isEmpty()) {
							refe += "," + cefe;
						}
					}
				}

				if (!mefe.isEmpty()) {
					if (!refe.isEmpty()) {
						refe += "," + mefe;
					} else {
						refe = mefe;
					}
				}

				StringBuilder tt = new StringBuilder();
				if (!kefe.isEmpty()) {
					tt.append(kefe);
				} else if (!pit.getOwnerString(tableOffset).isEmpty()) {
					tt.append(pit.getOwnerString(tableOffset));
				}
				if (tt.length() == 0 && !cefe.isEmpty()) {
					tt.append(cefe);
				}
				if (!mefe.isEmpty()) {
					if (tt.length() > 0) {
						tt.append(", ");
					}
					tt.append(mefe);
				}
				if (tt.length() > 0) {
					bt.addText("\t");
					bt.addText(tt.toString());
				}

				repoWriter.addText(bt);

			}

		}
	}

	/**
	 * Instantiates a new common report.
	 * 
	 * @param caller
	 *            the caller
	 * @param typesTable
	 *            the types table
	 * @param repoWriter
	 *            the repo writer
	 */
	protected CommonReport(ReportWorkerDialog caller,
			SukuTypesTable typesTable, ReportInterface repoWriter) {
		this.caller = caller;
		this.typesTable = typesTable;
		this.repoWriter = repoWriter;
	}

	protected void createPidTable(int idx, int[] pidCount) {
		BodyText bt = new TableHeaderText();
		bt.addText(Resurses.getReportString("REPORT.LISTA.TABLE") + " Pid = "
				+ pidCount[idx]);
		repoWriter.addText(bt);

		SukuData pdata = null;
		StringBuilder tabOwner = new StringBuilder();

		try {
			pdata = caller.getKontroller().getSukuData("cmd=person",
					"pid=" + pidCount[idx], "lang=" + Resurses.getLanguage());
		} catch (SukuException e1) {
			logger.log(Level.WARNING, "background reporting", e1);
			JOptionPane.showMessageDialog(caller, e1.getMessage());
			return;
		}

		UnitNotice[] notices = pdata.persLong.getNotices();

		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			if (nn.getTag().equals("NAME")) {
				tabOwner.append(nn.getSurname());
				if (tabOwner.length() > 0)
					tabOwner.append(" ");
				tabOwner.append(nn.getGivenname());
				// break;
			}

			String xxx = nn.getMediaTitle();
			if (xxx == null) {
				xxx = "";
			}

		}
		float prose = (idx * 100f) / pidCount.length;
		caller.setRunnerValue("" + (int) prose + ";" + tabOwner);

		bt = new MainPersonText();
		printName(bt, pdata.persLong, 2);
		repoWriter.addText(bt);

		printNotices(bt, notices, 2, 0);
		// bt = new BodyText();
		// bt.addText("");
		repoWriter.addText(bt);
	}

	/**
	 * create table for descendant report
	 * 
	 * tab sisältää henkilön ReportTableMember[0] puolisoiden
	 * ReportTableMember[1...n] lasten ReportTableMember[0..n-1]
	 * 
	 * persLong sisältää FullPerson katso
	 * fi.kaila.suku.server.SukuServerImpl.getFullPerson
	 * 
	 * personReferences sisältää kaikkien henkilöiden esiintymiset
	 * taulurakenteessa
	 * 
	 * @param idx
	 *            the idx
	 * @param tab
	 *            the tab
	 */
	protected void createDescendantTable(int idx, ReportUnit tab) {

		BodyText bt = null;
		ReportTableMember subjectmember = tab.getParent().get(0);
		SukuData pdata = null;
		boolean forceSpouseNum = false;
		StringBuilder tabOwner = new StringBuilder();
		int tableOffset = caller.getDescendantPane().getStartTable();
		if (tableOffset > 1) {
			tableOffset = tableOffset - 1;
		} else {
			tableOffset = 0;
		}
		try {
			pdata = caller.getKontroller().getSukuData("cmd=person",
					"pid=" + subjectmember.getPid(),
					"lang=" + Resurses.getLanguage());
		} catch (SukuException e1) {
			logger.log(Level.WARNING, "background reporting", e1);
			JOptionPane.showMessageDialog(caller, e1.getMessage());
			return;
		}
		if (pdata.persLong == null)
			return;

		UnitNotice[] xnotices = pdata.persLong.getNotices();

		int tableCount = 0;
		int famtCount = 0;

		for (int i = 0; i < xnotices.length; i++) {
			if (xnotices[i].getTag().equals("TABLE"))
				tableCount++;
			if (xnotices[i].getTag().equals("FAMT"))
				famtCount++;
		}

		UnitNotice[] notices = new UnitNotice[xnotices.length - tableCount
				- famtCount];

		UnitNotice[] tableNotices = new UnitNotice[tableCount];
		UnitNotice[] famtNotices = new UnitNotice[famtCount];

		int xn = 0;
		int xtable = 0;
		int xfamt = 0;

		for (int i = 0; i < xnotices.length; i++) {
			if (xnotices[i].getTag().equals("FAMT")) {
				famtNotices[xfamt++] = xnotices[i];
			} else if (xnotices[i].getTag().equals("TABLE")) {
				tableNotices[xtable++] = xnotices[i];
			} else {
				notices[xn++] = xnotices[i];
			}
		}

		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			if (nn.getTag().equals("NAME")) {
				tabOwner.append(nn.getSurname());
				if (tabOwner.length() > 0)
					tabOwner.append(" ");
				tabOwner.append(nn.getGivenname());
				break;
			}
		}
		float prose = (idx * 100f) / tables.size();
		caller.setRunnerValue("" + (int) prose + ";"
				+ (tab.getTableNo() + tableOffset) + ":" + tabOwner);

		String genText = "";
		if (tab.getGen() > 0) {
			genText = Roman.int2roman(tab.getGen());
		}

		if (tableNotices.length > 0) {
			bt = new MainPersonText();
			repoWriter.addText(bt);
			bt = new MainPersonText();
			printNotices(bt, tableNotices, 2, tab.getTableNo() + tableOffset);
			repoWriter.addText(bt);
			bt = new MainPersonText();
			bt.addText("");
			repoWriter.addText(bt);
		}

		bt = new TableHeaderText();

		bt.addText(typesTable.getTypeText("TABLE"));
		bt.addText("\u00A0" + (tab.getTableNo() + tableOffset));
		repoWriter.addText(bt);
		String fromTable = "";
		String fromSubTable = "";
		PersonInTables ref;

		bt = new MainPersonText();
		if (genText.length() > 0) {
			bt.addText(genText);
			bt.addText(". ");
		}
		if (caller.showRefn() && pdata.persLong.getRefn() != null) {
			bt.addText(pdata.persLong.getRefn());
			bt.addText(" ");
		}
		if (caller.showGroup() && pdata.persLong.getGroupId() != null) {
			bt.addText(pdata.persLong.getGroupId());
			bt.addText(" ");
		}
		if (pdata.persLong.getPrivacy() == null) {
			printName(bt, pdata.persLong, 2);
		} else {
			printNameNn(bt);
		}
		ref = personReferences.get(tab.getPid());
		if (ref != null) {
			fromTable = ref.getReferences(tab.getTableNo(), false, true, false,
					tableOffset);
		}

		if (fromTable.length() > 0) {
			String parts[] = fromTable.split(",");

			bt.addText("(");
			for (int i = 0; i < parts.length; i++) {
				if (i > 0) {
					bt.addText(", ");
				}
				try {
					long refTab = Long.parseLong(parts[i]);
					ReportUnit pare = tabMap.get(refTab - tableOffset);
					if (pare == null) {
						logger.severe("parents tab " + refTab + " not found");
					} else {

						ReportTableMember rm = pare.getParent().get(0);
						String pareSex = rm.getSex();
						if (pareSex.equals("M")) {
							bt.addText(typesTable.getTextValue("Father"));
						} else {
							bt.addText(typesTable.getTextValue("Mother"));
						}
						bt.addText(": ");
						int ppid = rm.getPid();

						SukuData ppdata = printParentReference(bt, ppid);
						//
						// lets see if we need other parent too
						//
						if (caller.getDescendantPane().isBothParents()) {
							// OK. Let's see if we can locate him/her
							String pareTag = (pareSex.equals("M")) ? "MOTH"
									: "FATH";
							int parePid = 0;
							int pareSurety = 0;
							for (int j = 0; j < pdata.relations.length; j++) {
								Relation rel = pdata.relations[j];
								if (rel.getTag().equals(pareTag)) {
									if (rel.getSurety() > pareSurety) {
										// still check for adoptions
										boolean isAdopt = false;
										if (rel.getNotices() != null) {
											for (RelationNotice rn : rel
													.getNotices()) {
												if (rn.getTag().equals("ADOP")) {
													isAdopt = true;
													break;
												}
											}
										}
										if (!isAdopt) {
											pareSurety = rel.getSurety();
											parePid = rel.getRelative();
										}
									}
								}
							}
							if (parePid > 0) {
								// let's still make sure he/she is in the parent
								// table
								boolean isInTab = false;
								for (int j = 0; j < tab.getParent().size(); j++) {
									if (tab.getParent().get(j).getPid() == parePid) {
										isInTab = true;
										break;
									}
								}

								if (isInTab) {
									bt.addText(", ");

									if (pareSex.equals("M")) {
										bt.addText(typesTable
												.getTextValue("Mother"));
									} else {
										bt.addText(typesTable
												.getTextValue("Father"));
									}
									bt.addText(": ");
									printParentReference(bt, parePid);
								}
							}
						}
						if (ppdata.pers != null && ppdata.pers.length > 0) {
							bt.addText(" ");
							bt.addText(typesTable.getTextValue("FROMTABLE")
									.toLowerCase() + " " + refTab, true, false);
						}

					}
				} catch (NumberFormatException ne) {
					// not expected here i.e. program error
					logger.log(Level.WARNING, "pare fetch", ne);

				} catch (SukuException e) {
					logger.log(Level.WARNING, "pare fetch", e);
				}
			}

			bt.addText("). ");
		}

		if (pdata.persLong.getPrivacy() == null) {
			printNotices(bt, notices, 2, tab.getTableNo() + tableOffset);
		}
		fromTable = "";
		ref = personReferences.get(tab.getPid());

		//
		// check references for table owner here
		//
		String childTables = "";

		if (ref != null) {
			childTables = ref.getReferences(tab.getTableNo(), false, true,
					false, tableOffset);
			fromTable = ref.getReferences(tab.getTableNo(), true, false, false,
					tableOffset);
			if (!childTables.isEmpty()) {
				fromTable = "";// if as child then don't refer to as spouse
				// spouse will be referred from the spouse
			}
		}
		if (fromTable.length() == 0) {

			fromTable = ref.getReferences(tab.getTableNo(), false, false, true,
					tableOffset);
		}
		if (fromTable.length() > 0) {
			if (childTables.isEmpty()) {
				bt.addText(typesTable.getTextValue("ALSO") + "\u00A0"
						+ fromTable + ". ", true, false);
			} else {
				bt.addText(typesTable.getTextValue("ISFAMILY") + " "
						+ fromTable + ". ", true, false);
			}
		}

		if (bt.getCount() > 0) {
			repoWriter.addText(bt);

		}

		//
		// here fetch ancestors of table owner
		//
		try {
			for (int i = 0; i < subjectmember.getSubCount(); i++) {
				bt = new SubPersonText();
				bt.addText(subjectmember.getSubDadMom(i) + " ");
				SukuData sub = caller.getKontroller().getSukuData("cmd=person",
						"pid=" + subjectmember.getSubPid(i));
				notices = sub.persLong.getNotices();
				if (sub.persLong.getPrivacy() == null) {
					printName(bt, sub.persLong, 4);
					printNotices(bt, notices, 4, tab.getTableNo() + tableOffset);
				} else {
					printNameNn(bt);
				}
				fromTable = "";
				ref = personReferences.get(subjectmember.getSubPid(i));
				if (ref != null) {
					fromTable = ref.getReferences(tab.getTableNo(), true, true,
							true, tableOffset);
					//
					// here we add references to table owner parents
					// Should come here only for report subject ancestors
					// None should be part of the family (i.e. descendants of
					// himself
					//
					if (fromTable.length() > 0) {
						bt.addText(typesTable.getTextValue("ALSO") + " "
								+ fromTable + ". ", true, false);
					}
				}

				repoWriter.addText(bt);

			}
		} catch (SukuException e1) {
			logger.log(Level.WARNING, "background reporting", e1);
			JOptionPane.showMessageDialog(caller, e1.getMessage());
			return;
		}

		//
		// spouse list
		//
		// do we have a child who's parents are not here
		for (int ix = 0; ix < tab.getChild().size(); ix++) {
			ReportTableMember chi = tab.getChild().get(ix);
			int jx = 0;
			for (jx = 1; jx < tab.getParent().size(); jx++) {
				ReportTableMember chip = tab.getParent().get(jx);
				if (chi.getOtherParentPid() == chip.getPid()) {
					break;
				}

			}
			if (jx == tab.getParent().size()) {
				forceSpouseNum = true;
				break;
			}

		}

		//
		// spouse list is written here
		//
		ReportTableMember spouseMember;
		for (int ispou = 1; ispou < tab.getParent().size(); ispou++) {
			bt = new SpousePersonText();
			spouseMember = tab.getParent().get(ispou);
			int spouNum = 0;
			if (tab.getParent().size() > 2) {
				spouNum = ispou;
			} else if (forceSpouseNum) {
				spouNum = 1;
			}

			try {

				printSpouse(tab.getTableNo(), tab.getPid(), bt, spouseMember,
						spouNum, tableOffset);

			} catch (SukuException e1) {
				logger.log(Level.WARNING, "background reporting", e1);

			}
		}

		if (tab.getChild().size() > 0) {
			bt = new ChildHeaderText();
			genText = Roman.int2roman(tab.getGen() + 1);
			bt.addText(genText + ". ");
			bt.addText(typesTable.getTextValue("Chil"));
			repoWriter.addText(bt);

		}
		//
		// child list
		//
		SukuData cdata;
		ReportTableMember childMember;
		ReportTableMember childSpouseMember;
		String toTable = "";
		boolean hasOwnTable = false;
		for (int ichil = 0; ichil < tab.getChild().size(); ichil++) {
			bt = new ChildListText();
			childMember = tab.getChild().get(ichil);

			try {
				cdata = caller.getKontroller().getSukuData("cmd=person",
						"pid=" + childMember.getPid());
				ref = personReferences.get(childMember.getPid());

				boolean hasSpouses = childMember.getSpouses() != null
						&& childMember.getSpouses().length > 0;

				toTable = "";
				hasOwnTable = false;

				if (ref != null) {
					toTable = ref.getReferences(0, true, false, false,
							tableOffset);
					if (!toTable.isEmpty()) { // && ref.getMyTable() > 0) {
						if (ref.getMyTable() > 0 || !hasSpouses) {
							hasOwnTable = true;
						}
						if (childMember.getMyTable() > 0) {

							toTable = ""
									+ (childMember.getMyTable() + tableOffset);
						} else {
							if (ref.asParents.size() > 0) {
								hasOwnTable = true;
							}

						}

					}

				}

				//
				// let's try to find mother here
				//
				String paretag = "MOTH";
				String ownerTag = "FATH";
				int bid = 0;
				if (pdata.persLong.getSex().equals("F")) {
					paretag = "FATH";
					ownerTag = "MOTH";
				}
				for (int i = 0; i < cdata.relations.length; i++) {
					if (paretag.equals(cdata.relations[i].getTag())) {
						bid = cdata.relations[i].getRelative();
						break;

					}
				}
				String pareTxt = "";
				if (bid > 0) {
					ReportTableMember pareMember;
					for (int isp = 1; isp < tab.getParent().size(); isp++) {
						pareMember = tab.getParent().get(isp);
						if (pareMember.getPid() == bid) {
							if (tab.getParent().size() > 2) {
								pareTxt = "" + isp + ": ";
							} else if (forceSpouseNum) {
								pareTxt = "" + isp + ": ";
							} else {
								pareTxt = "";
							}
							break;
						}
					}
				}

				StringBuilder adopTag = new StringBuilder();

				for (int i = 0; i < cdata.relations.length; i++) {
					if (ownerTag.equals(cdata.relations[i].getTag())) {
						if (tab.getPid() == cdata.relations[i].getRelative()) {
							if (cdata.relations[i].getNotices() != null) {
								for (int j = 0; j < cdata.relations[i]
										.getNotices().length; j++) {
									RelationNotice rn = cdata.relations[i]
											.getNotices()[j];
									String aux = rn.getTag();
									if ("ADOP".equals(aux)) {
										if (adopTag.length() > 0) {
											adopTag.append(", ");
										}
										String tmp = rn.getType();
										if (tmp == null) {
											adopTag.append(typesTable
													.getTextValue(aux));
										} else {
											adopTag.append(rn.getType());
										}
									} else {
										if ("NOTE".equals(aux)
												&& rn.getNoteText() != null) {
											if (adopTag.length() > 0) {
												adopTag.append(", ");
											}
											adopTag.append(rn.getNoteText());
										} else if ("SOUR".equals(aux)
												&& rn.getSource() != null) {
											String srcFormat = caller
													.getSourceFormat();
											if (!ReportWorkerDialog.SET_NO
													.equals(srcFormat)) {

												String src = rn.getSource();

												String srcText = addSource(
														false, srcFormat, src);
												adopTag.append(srcText);
											}
										}
									}
								}
							}
						}
					}
				}

				notices = cdata.persLong.getNotices();
				if (!pareTxt.isEmpty()) {
					bt.addText(pareTxt);
				}
				if (adopTag.length() > 0) {
					bt.addText("(" + adopTag.toString() + ") ");
				}

				if (cdata.persLong.getPrivacy() == null) {

					printName(bt, cdata.persLong, (hasOwnTable ? 3 : 2));
					printNotices(bt, notices, (hasOwnTable ? 3 : 2),
							tab.getTableNo() + tableOffset);
				} else {
					printNameNn(bt);
				}
				if (!toTable.isEmpty()) {
					bt.addText(typesTable.getTextValue("TABLE") + "\u00A0"
							+ toTable + ". ", true, false);

				} else {
					toTable = ref.getReferences(tab.getTableNo(), false, true,
							false, tableOffset);
					if (!toTable.isEmpty()) {
						bt.addText(typesTable.getTextValue("ALSO") + "\u00A0"
								+ toTable + ". ", true, false);
					}
				}
				if (bt.getCount() > 0) {
					repoWriter.addText(bt);
				}

				if (childMember.getSubCount() > 0) {

					HashMap<String, String> submap = new HashMap<String, String>();

					for (int i = 0; i < childMember.getSubCount(); i++) {
						if (childMember.getSubPid(i) != tab.getPid()) {
							bt = new SubPersonText();
							bt.addText(childMember.getSubDadMom(i) + " ");
							SukuData sub = caller.getKontroller().getSukuData(
									"cmd=person",
									"pid=" + childMember.getSubPid(i));
							notices = sub.persLong.getNotices();

							if (sub.persLong.getPrivacy() == null) {

								printName(bt, sub.persLong, 4);
								printNotices(bt, notices, 4, tab.getTableNo()
										+ tableOffset);
							} else {
								printNameNn(bt);
							}
							fromSubTable = "";
							ref = personReferences
									.get(childMember.getSubPid(i));
							if (ref != null) {
								StringBuilder fromsTable = new StringBuilder();
								fromSubTable = ref.getReferences(
										tab.getTableNo(), true, true, true,
										tableOffset);
								if (ref.getMyTable() > 0
										&& tab.getTableNo() != ref.getMyTable()) {
									fromsTable.append(ref.getMyTable());
								} else {
									String[] froms = fromSubTable.split(",");

									for (int j = 0; j < froms.length; j++) {

										String mapx = submap.put(froms[j],
												froms[j]);
										if (mapx == null) {
											if (j > 0) {
												fromsTable.append(",");
											}
											fromsTable.append(froms[j]);
										}
									}
								}
								//
								// this would be now a childs parent
								// none should be related if we come here
								//
								if (fromsTable.length() > 0) {
									bt.addText(typesTable.getTextValue("ALSO")
											+ " " + fromsTable.toString()
											+ ". ", true, false);
								}
							}

							repoWriter.addText(bt);
						}
					}
				}

				if (hasSpouses) {

					if (childMember.getSpouses() != null
							&& childMember.getSpouses().length > 0) {

						for (int j = 0; j < childMember.getSpouses().length; j++) {
							childSpouseMember = childMember.getSpouses()[j];
							bt = new ChildSpouseText();
							int spouNum = 0;
							if (childMember.getSpouses().length > 1) {
								spouNum = j + 1;

							}

							printSpouse(tab.getTableNo(), childMember.getPid(),
									bt, childSpouseMember, spouNum, tableOffset);

						}
					}
				}

				if (bt.getCount() > 0) {
					repoWriter.addText(bt);

				}
			} catch (SukuException e1) {
				logger.log(Level.WARNING, "SukuException", e1);
			}
		}
		if (famtNotices.length > 0) {
			bt = new MainPersonText();
			bt.addText("\n");
			repoWriter.addText(bt);
			bt = new MainPersonText();
			printNotices(bt, famtNotices, 2, tab.getTableNo());
			repoWriter.addText(bt);
		}
	}

	/**
	 * @param bt
	 * @param ppid
	 * @return
	 * @throws SukuException
	 */
	public SukuData printParentReference(BodyText bt, int ppid)
			throws SukuException {
		SukuData ppdata = caller.getKontroller().getSukuData("cmd=person",
				"pid=" + ppid, "mode=short", "lang=" + Resurses.getLanguage());

		if (ppdata.pers != null && ppdata.pers.length > 0) {

			if (ppdata.pers[0].getPrivacy() != null) {
				ppdata.pers[0] = new PersonShortData(ppdata.pers[0].getPid(),
						typesTable.getTextValue("REPORT_NOMEN_NESCIO"), null,
						null, null, null, null, null);

			}
			if (ppdata.pers[0].getGivenname() != null) {
				printGivenname(bt, ppdata.pers[0].getGivenname(), false);
				if (ppdata.pers[0].getPrefix() != null
						|| ppdata.pers[0].getSurname() != null
						|| ppdata.pers[0].getPostfix() != null) {
					bt.addText(" ");
				}
				if (ppdata.pers[0].getPrefix() != null) {
					bt.addText(ppdata.pers[0].getPrefix() + " ");
				}
				if (ppdata.pers[0].getSurname() != null) {
					bt.addText(ppdata.pers[0].getSurname());
				}
				if (ppdata.pers[0].getPostfix() != null) {
					bt.addText(" " + ppdata.pers[0].getPostfix());
				}

			}
		}
		return ppdata;
	}

	private void printNameNn(BodyText bt) {

		UnitNotice[] notices = new UnitNotice[1];
		notices[0] = new UnitNotice("NAME");
		notices[0].setGivenname(typesTable.getTextValue("REPORT_NOMEN_NESCIO"));

		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];

			if (nn.getTag().equals("NAME")) {

				printGivenname(bt, nn.getGivenname(), true);

			}

		}

		bt.addText(". ");
	}

	/**
	 * @param tabMember
	 * @param bt
	 * @param spouseMember
	 * @param spouNum
	 * @return
	 * @throws SukuException
	 */
	private void printSpouse(long tabNo, int memberPid, BodyText bt,
			ReportTableMember spouseMember, int spouNum, int tableOffset)
			throws SukuException {
		UnitNotice[] notices;
		String fromTable;
		SukuData sdata;
		PersonInTables refs;
		sdata = caller.getKontroller().getSukuData("cmd=person",
				"pid=" + spouseMember.getPid());
		String tmp;
		if ("M".equals(sdata.persLong.getSex())) {
			tmp = "HUSB";
		} else {
			tmp = "WIFE";
		}

		if (sdata.persLong.getPrivacy() != null
				&& sdata.persLong.getPrivacy().equals("P")) {
			return;
		}

		String spouType = typesTable.getTextValue(tmp);
		RelationNotice rnn[] = null;
		if (sdata.relations != null) {
			boolean isMarrTag = false;

			for (int i = 0; i < sdata.relations.length; i++) {
				if (sdata.relations[i].getRelative() == memberPid) {
					if (sdata.persLong.getPrivacy() == null) {
						if (sdata.relations[i].getNotices() != null) {
							rnn = sdata.relations[i].getNotices();

							for (RelationNotice rr : rnn) {
								if (rr.getTag().equals("MARR")) {
									isMarrTag = true;
								}
							}
							if (isMarrTag) {

								RelationNotice rn = rnn[0];

								if (rn.getTag().equals("MARR")) {
									// spouType = typesTable.getTextValue(rn
									// .getTag());
									spouType = printRelationNotice(rn,
											spouType, spouNum, true);
								}
							}

						}

					}
				}
			}
			if (!isMarrTag) {
				if (spouNum > 0) {
					spouType += " " + spouNum + ":o";
				}
			}

			bt.addText("- ");
			bt.addText(spouType);
			bt.addText(" ");

			fromTable = "";
			int typesColumn = 2;
			boolean isRelated = false;
			refs = personReferences.get(spouseMember.getPid());
			if (refs != null) {

				typesColumn = refs.getTypesColumn(tabNo, true, true, false);

				String asChild = refs.getReferences(tabNo, false, true, false,
						tableOffset);
				if (!asChild.isEmpty()) {
					isRelated = true;
					if (refs.getMyTable() > 0) {
						typesColumn = 3;
					}
					fromTable = refs.getReferences(tabNo, true, false, false,
							tableOffset);
					if (fromTable.isEmpty()) {
						fromTable = "" + asChild;
					}

				} else {
					fromTable = refs.getReferences(tabNo, true, false, false,
							tableOffset);
				}

			}

			notices = sdata.persLong.getNotices();
			if (sdata.persLong.getPrivacy() == null) {

				printName(bt, sdata.persLong, typesColumn);
				printNotices(bt, notices, typesColumn, tabNo);
			} else {
				printNameNn(bt);
			}
			if (rnn != null) {
				for (int i = 0; i < rnn.length; i++) {
					RelationNotice rn = rnn[i];
					spouType = printRelationNotice(rn, null, 0, false);
					if (spouType.length() > 0) {
						// bt.addText(" ");
						bt.addText(spouType);
						bt.addText(". ");
					}
				}
			}

			if (refs != null) {

				if (fromTable.length() > 0) {
					bt.addText(
							typesTable.getTextValue(isRelated ? "ISFAMILY"
									: "ALSO") + " " + fromTable + ". ", true,
							false);
				}
			}
		}
		if (bt.getCount() > 0) {
			repoWriter.addText(bt);
		}

		for (int i = 0; i < spouseMember.getSubCount(); i++) {
			bt = new SubPersonText();
			String subDad = spouseMember.getSubDadMom(i);
			bt.addText(subDad + " ");
			SukuData sub = caller.getKontroller().getSukuData("cmd=person",
					"pid=" + spouseMember.getSubPid(i));
			notices = sub.persLong.getNotices();
			StringBuilder fromsTable = new StringBuilder();
			boolean referenceFoundEarlier = false;
			refs = personReferences.get(spouseMember.getSubPid(i));
			if (refs != null) {
				fromTable = refs.getReferences(tabNo, true, true, true,
						tableOffset);

				String[] froms = fromTable.split(",");

				for (int j = 0; j < froms.length; j++) {
					long refTab = 0;
					try {
						refTab = Long.parseLong(froms[j]);
					} catch (NumberFormatException ne) {
						refTab = 0;
					}
					if (refTab > 0 && refTab < tabNo) {
						referenceFoundEarlier = true;
					}

					if (j > 0) {
						fromsTable.append(",");
					}
					fromsTable.append(froms[j]);
				}
			}

			if (sub.persLong.getPrivacy() == null) {

				printName(bt, sub.persLong, 4);
				int noticeCol = 4;
				if (referenceFoundEarlier) {
					noticeCol = 3;
				}
				printNotices(bt, notices, noticeCol, tabNo);
			} else {
				printNameNn(bt);
			}
			fromTable = "";

			// refs = personReferences.get(spouseMember.getSubPid(i));
			// if (refs != null) {
			// fromTable = refs.getReferences(tabNo, true, true, true,
			// tableOffset);
			//
			// String[] froms = fromTable.split(",");
			// StringBuilder fromsTable = new StringBuilder();
			// for (int j = 0; j < froms.length; j++) {
			// // String mapx = submap.put(froms[j], subDad);
			// // if (mapx == null || mapx.equals(subDad)) {
			// if (j > 0) {
			// fromsTable.append(",");
			// }
			// fromsTable.append(froms[j]);
			// }
			// }
			if (fromsTable.length() > 0) {
				bt.addText(
						typesTable.getTextValue("ALSO") + " "
								+ fromsTable.toString() + ". ", true, false);
			}
			// }

			repoWriter.addText(bt);

		}
		// return notices;
	}

	private int getTypeColumn(int pid) {
		Integer foundMe = mapper.get(pid);
		if (foundMe != null)
			return 3;
		mapper.put(Integer.valueOf(pid), Integer.valueOf(pid));
		return 2;
	}

	/**
	 * Ancestor report family table is created here.
	 * 
	 * @param idx
	 *            the idx
	 * @param ftab
	 *            the ftab
	 * @param mtab
	 *            the mtab
	 * @param tableNum
	 *            the table num
	 */
	protected void createAncestorTable(int idx, ReportUnit ftab,
			ReportUnit mtab, long tableNum) {
		BodyText bt = null;
		ReportTableMember subjectmember;
		SukuData pappadata = null;
		SukuData mammadata = null;
		ReportUnit mainTab = null;

		// long fatherTable = 0;
		// long motherTable = 0;
		//
		// long currTabNo = 0;
		boolean fams = caller.getAncestorPane().getShowfamily();
		String order = caller.getAncestorPane().getNumberingFormat()
				.getSelection().getActionCommand();
		if (!order.equals("ESPOLIN")
				&& caller.getAncestorPane().getAllBranches()) {
			// currTabNo = tableNum;
		}
		PersonLongData xdata = null;
		// UnitNotice[] xnotices = null;
		StringBuilder tabOwner = new StringBuilder();
		if (ftab != null) {
			// fatherTable = ftab.getTableNo();
			mainTab = ftab;
			subjectmember = ftab.getParent().get(0);

			try {
				pappadata = caller.getKontroller().getSukuData("cmd=person",
						"pid=" + subjectmember.getPid(),
						"lang=" + Resurses.getLanguage());
			} catch (SukuException e1) {
				logger.log(Level.WARNING, "background reporting", e1);
				JOptionPane.showMessageDialog(caller, e1.getMessage());
				return;
			}
			xdata = pappadata.persLong;

		}
		if (mtab != null) {
			// motherTable = mtab.getTableNo();
			subjectmember = mtab.getParent().get(0);

			try {
				mammadata = caller.getKontroller().getSukuData("cmd=person",
						"pid=" + subjectmember.getPid(),
						"lang=" + Resurses.getLanguage());
			} catch (SukuException e1) {
				logger.log(Level.WARNING, "background reporting", e1);
				JOptionPane.showMessageDialog(caller, e1.getMessage());
				return;
			}
			if (pappadata == null) {
				mainTab = mtab;
				xdata = mammadata.persLong;

			}

		}

		for (int i = 0; i < mainTab.getMemberCount(); i++) {
			ReportTableMember mem = mainTab.getMember(i);
			PersonInTables pt = personReferences.get(mem.getPid());
			if (pt != null) {
				if (mainTab.getTableNo() > 0) {
					pt.addOwner(mainTab.getTableNo());
				}
			}
		}

		for (int j = 0; j < xdata.getNotices().length; j++) {
			UnitNotice nn = xdata.getNotices()[j];
			if (nn.getTag().equals("NAME")) {
				tabOwner.append(nn.getSurname());
				if (tabOwner.length() > 0)
					tabOwner.append(" ");
				tabOwner.append(nn.getGivenname());
				break;
			}
		}

		float prose = (idx * 100f) / tables.size();
		caller.setRunnerValue("" + (int) prose + ";" + tableNum + ":"
				+ tabOwner.toString());

		bt = new TableHeaderText();
		String genText = "";
		if (mainTab.getGen() > 0) {
			genText = Roman.int2roman(mainTab.getGen());
		}

		if (mtab != null && ftab != null) {
			bt.addText(typesTable.getTextValue("TABLES") + " ");
			if (!genText.isEmpty()) {
				bt.addText(genText + ".");
			}

			bt.addText(" " + toPrintTable(ftab.getTableNo()));
			bt.addText(", " + toPrintTable(mtab.getTableNo()));
		} else {
			if (tableNum > 0) {
				bt.addText(typesTable.getTextValue("TABLE") + "\u00A0");
				if (!genText.isEmpty()) {
					bt.addText(genText + ".");
				}
				bt.addText(" " + toPrintTable(tableNum));
			}
		}

		if (bt.getCount() > 0) {
			repoWriter.addText(bt);
		}
		ReportUnit tab;
		if (!order.equals("ESPOLIN")) {
			bt = new TableSubHeaderText();
			if (ftab != null) {
				tab = ftab;
			} else {
				tab = mtab;
			}

			HashMap<Long, Long> mp = new HashMap<Long, Long>();
			for (int j = 0; j < tab.getChild().size(); j++) {
				ReportTableMember mom = tab.getChild().get(j);

				long mytab = 0;

				ReportUnit ru = personTables.get(mom.getPid());
				if (ru != null) {
					mytab = ru.getTableNo();
					if (mytab > 0) {
						if (mp.get(mytab) == null) {
							mp.put(mytab, mytab);
						}
					}
				}

			}
			if (mp.size() > 0) {
				bt.addText("(");
				bt.addText(typesTable.getTextValue((mp.size() == 1 ? "FROMTABLE"
						: "FROMTABLES")));
				bt.addText(" ");

				Iterator<Long> ki = mp.keySet().iterator();
				boolean setComma = false;
				while (ki.hasNext()) {
					if (setComma) {
						bt.addText(",");
					}
					setComma = true;
					bt.addText(toPrintTable(ki.next(), true));
				}

				bt.addText(")");
			}

			if (bt.getCount() > 0) {
				repoWriter.addText(bt);
			}
		}
		UnitNotice[] notices = null;
		int fid = 0;
		int mid = 0;

		if (ftab != null) {
			tab = ftab;
			fid = tab.getPid();
			notices = getInternalNotices(pappadata.persLong.getNotices());

			bt = new MainPersonText();
			if (genText.length() > 0) {
				bt.addText(genText);
				bt.addText(". ");
			}
			if (caller.showRefn() && xdata.getRefn() != null) {

				bt.addText(xdata.getRefn());
				bt.addText(" ");
			}
			if (caller.showGroup() && xdata.getGroupId() != null) {

				bt.addText(xdata.getGroupId());
				bt.addText(" ");
			}
			if (pappadata.persLong.getPrivacy() == null) {
				printName(bt, pappadata.persLong, 2);
				if (!order.equals("ESPOLIN")) {
					bt = addParentReference(tab, bt);
				}
				printNotices(bt, notices, getTypeColumn(fid), ftab.getTableNo());
			} else {
				printNameNn(bt);
			}
		}
		if (bt.getCount() > 0) {
			repoWriter.addText(bt);

		}
		if (mtab != null) {
			tab = mtab;
			mid = mtab.getPid();
			if (ftab != null) {
				// now let's look for marriage info

				Relation marr = null;
				if (mammadata.relations != null) {
					for (int i = 0; i < mammadata.relations.length; i++) {
						Relation rr = mammadata.relations[i];
						if (rr.getTag().equals("HUSB")
								&& fid == rr.getRelative()
								&& mid == rr.getPid()) {
							marr = rr;
							break;
						}
					}
				}
				if (marr != null) {
					if (marr.getNotices() != null) {

						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < marr.getNotices().length; i++) {
							RelationNotice rn = marr.getNotices()[i];

							String spouType = printRelationNotice(rn, "", 0,
									false);
							if (!spouType.isEmpty()) {
								sb.append(spouType);
								sb.append(". ");
							}
						}
						bt.addText("\n");
						bt.addText(sb.toString(), false, false, true);
						bt.addText("\n");

						repoWriter.addText(bt);

					}

				}

			}

			notices = getInternalNotices(mammadata.persLong.getNotices());

			bt = new MainPersonText();
			if (genText.length() > 0) {
				bt.addText(genText);
				bt.addText(". ");
			}
			if (caller.showRefn() && mammadata.persLong.getRefn() != null) {

				bt.addText(mammadata.persLong.getRefn());
				bt.addText(" ");
			}
			if (caller.showGroup() && mammadata.persLong.getGroupId() != null) {

				bt.addText(mammadata.persLong.getGroupId());
				bt.addText(" ");
			}

			if (mammadata.persLong.getPrivacy() == null) {
				printName(bt, mammadata.persLong, 2);
				if (!order.equals("ESPOLIN")) {
					bt = addParentReference(tab, bt);
				}
				printNotices(bt, notices,
						getTypeColumn(mammadata.persLong.getPid()),
						mtab.getTableNo());
			} else {
				printNameNn(bt);
			}
		}

		if (bt.getCount() > 0) {
			repoWriter.addText(bt);

		}

		//
		// spouse list
		//
		if (order.equals("ESPOLIN")) {
			SukuData sdata;
			String fromTable;
			PersonInTables ref;
			tab = ftab;
			ReportTableMember spouseMember;
			for (int ispou = 1; ispou < tab.getParent().size(); ispou++) {
				bt = new MainPersonText();
				spouseMember = tab.getParent().get(ispou);
				int spouNum = 0;
				if (tab.getParent().size() > 2) {
					spouNum = ispou;

				}

				try {
					sdata = caller.getKontroller().getSukuData("cmd=person",
							"pid=" + spouseMember.getPid());
					String tmp;
					if ("M".equals(sdata.persLong.getSex())) {
						tmp = "HUSB";
					} else {
						tmp = "WIFE";
					}
					String spouType = typesTable.getTextValue(tmp);
					RelationNotice rnn[] = null;
					if (sdata.relations != null) {

						for (int i = 0; i < sdata.relations.length; i++) {
							if (sdata.relations[i].getRelative() == tab
									.getPid()) {
								if (sdata.relations[i].getNotices() != null) {
									rnn = sdata.relations[i].getNotices();

									RelationNotice rn = rnn[0];
									spouType = printRelationNotice(rn,
											spouType, spouNum, true);

								}

							}

						}
					}

					bt.addText("- ");
					bt.addText(spouType);
					bt.addText(" ");

					fromTable = "";
					int typesColumn = getTypeColumn(spouseMember.getPid());
					ref = personReferences.get(spouseMember.getPid());
					// if (ref != null) {
					// typesColumn = ref.getTypesColumn(tableNum, true, true,
					// false);
					// }

					notices = sdata.persLong.getNotices();
					if (sdata.persLong.getPrivacy() == null) {
						printName(bt, sdata.persLong, typesColumn);
						printNotices(bt, notices, typesColumn, tableNum);
					} else {
						printNameNn(bt);
					}
					if (rnn != null && rnn.length > 1) {
						for (int i = 1; i < rnn.length; i++) {
							RelationNotice rn = rnn[i];
							spouType = printRelationNotice(rn, null, 0, false);
							if (spouType.length() > 0) {
								bt.addText(" ");
								bt.addText(spouType);
								bt.addText(".");
							}
						}
					}

					if (ref != null) {
						fromTable = "";
						if (ref.getOwnerArray().length > 0) {
							fromTable = "" + ref.getOwnerString(0);
						}
						// fromTable = ref.getReferences(tab.getTableNo(), true,
						// true, false);
						if (fromTable.length() > 0) {
							bt.addText(typesTable.getTextValue("TABLE")
									+ "\u00A0" + fromTable + ". ", true, false);
						}
					}

					if (bt.getCount() > 0) {
						repoWriter.addText(bt);

					}

					for (int i = 0; i < spouseMember.getSubCount(); i++) {
						bt = new SubPersonText();
						bt.addText(spouseMember.getSubDadMom(i) + " ");
						SukuData sub = caller.getKontroller().getSukuData(
								"cmd=person",
								"pid=" + spouseMember.getSubPid(i));
						notices = sub.persLong.getNotices();
						if (sub.persLong.getPrivacy() == null) {
							printName(bt, sub.persLong, 4);
							printNotices(bt, notices, 4, tableNum);
						} else {
							printNameNn(bt);
						}
						fromTable = "";
						ref = personReferences.get(spouseMember.getSubPid(i));
						if (ref != null) {
							fromTable = ref.getReferences(tableNum, true, true,
									true, 0);
							if (fromTable.length() > 0) {
								bt.addText(typesTable.getTextValue("ALSO")
										+ "\u00A0" + fromTable + ". ", true,
										false);
							}
						}

						repoWriter.addText(bt);

					}

				} catch (SukuException e1) {
					logger.log(Level.WARNING, "background reporting", e1);

				}
			}
		}

		if (fams) {
			// ensin vanhempien yhteiset lapset
			// tai ainoan vanhemman lapset

			tab = ftab;
			if (tab == null) {
				tab = mtab;
			}
			ArrayList<ReportTableMember> full = new ArrayList<ReportTableMember>();

			for (int i = 0; i < tab.getChild().size(); i++) {
				ReportTableMember mem = tab.getChild().get(i);
				boolean addMe = false;
				if (ftab != null && mtab != null) {
					for (int j = 0; j < mtab.getChild().size(); j++) {
						ReportTableMember mom = mtab.getChild().get(j);
						if (mem.getPid() == mom.getPid()) {
							addMe = true;
							break;
						}
					}
				} else {
					addMe = true;
				}
				if (addMe) {
					full.add(mem);
				}
			}

			SukuData cdata;
			ReportTableMember childMember;

			String toTable = "";

			for (int ichil = 0; ichil < full.size(); ichil++) {
				if (ichil == 0) {
					bt = new ChildHeaderText();
					if (full.size() > 1) {
						bt.addText(typesTable.getTextValue("CHILDREN"));
					} else {
						bt.addText(typesTable.getTextValue("CHIL"));
					}
					bt.addText(":");
					repoWriter.addText(bt);

				}
				bt = new ChildListText();
				// childMember = tab.getChild().get(ichil);
				childMember = full.get(ichil);
				try {
					cdata = caller.getKontroller().getSukuData("cmd=person",
							"pid=" + childMember.getPid());

					String ownerTag = "FATH";

					// check if child is adopted
					String adopTag = null;
					for (int i = 0; i < cdata.relations.length; i++) {
						if (ownerTag.equals(cdata.relations[i].getTag())) {
							if (cdata.relations[i].getNotices() != null) {
								adopTag = cdata.relations[i].getNotices()[0]
										.getTag();

							}
							break;

						}
					}

					notices = cdata.persLong.getNotices();
					// if (!pareTxt.isEmpty()) {
					// bt.addText(pareTxt);
					// }
					if (adopTag != null) {
						bt.addText("(" + typesTable.getTextValue(adopTag)
								+ ") ");
					}
					if (cdata.persLong.getPrivacy() == null) {

						printName(bt, cdata.persLong, (toTable.isEmpty() ? 2
								: 3));

						addChildReference(ftab, mtab, cdata.persLong.getPid(),
								typesTable.getTextValue("TABLE"), bt);

						printNotices(bt, notices,
								getTypeColumn(cdata.persLong.getPid()),
								tab.getTableNo());
					} else {
						printNameNn(bt);
					}
					// else {

					// }

					if (bt.getCount() > 0) {
						repoWriter.addText(bt);

					}
				} catch (SukuException e1) {
					logger.log(Level.WARNING, "SukuException", e1);
				}
			}
			//
			// now children for man and woman only
			//
			boolean isMale = true;
			String parentText;

			if (ftab != null && mtab != null) {
				while (true) {
					if (isMale) {
						parentText = typesTable.getTextValue("FATHERS");
						tab = ftab;
						isMale = false;
					} else {
						parentText = typesTable.getTextValue("MOTHERS");
						tab = mtab;
						isMale = true;
					}

					ArrayList<ReportTableMember> other = new ArrayList<ReportTableMember>();

					for (int i = 0; i < tab.getParent().size(); i++) {
						ReportTableMember mem = tab.getParent().get(i);

						boolean addMe = true;

						if (mem.getPid() == ftab.getPid()
								|| mem.getPid() == mtab.getPid()) {
							addMe = false;
						}

						if (addMe) {
							other.add(mem);
						}
					}

					for (int ichil = 0; ichil < other.size(); ichil++) {
						if (ichil == 0) {
							bt = new ChildHeaderText();
							bt.addText(parentText);
							bt.addText(" ");
							if (other.size() > 1) {
								bt.addText(typesTable.getTextValue("SPOUSES")
										.toLowerCase());
							} else {
								bt.addText(typesTable.getTextValue(
										(isMale) ? "WIFE" : "HUSB")
										.toLowerCase());
							}
							bt.addText(":");
							repoWriter.addText(bt);

						}
						bt = new ChildListText();
						childMember = other.get(ichil);

						try {
							cdata = caller.getKontroller()
									.getSukuData("cmd=person",
											"pid=" + childMember.getPid());

							notices = cdata.persLong.getNotices();
							if (cdata.persLong.getPrivacy() == null) {
								printName(bt, cdata.persLong,
										(toTable.isEmpty() ? 2 : 3));
								addChildReference(ftab, mtab, tab.getPid(),
										typesTable.getTextValue("TABLE"), bt);
								printNotices(bt, notices, tab.getPid(),
										tab.getTableNo());
							} else {
								printNameNn(bt);
							}
							if (bt.getCount() > 0) {
								repoWriter.addText(bt);

							}
						} catch (SukuException e1) {
							logger.log(Level.WARNING, "SukuException", e1);
						}
					}

					other = new ArrayList<ReportTableMember>();

					for (int i = 0; i < tab.getChild().size(); i++) {
						ReportTableMember mem = tab.getChild().get(i);

						boolean addMe = true;

						for (int j = 0; j < full.size(); j++) {
							ReportTableMember mom = full.get(j);
							if (mem.getPid() == mom.getPid()) {
								addMe = false;
								break;
							}
						}
						if (addMe) {
							other.add(mem);
						}
					}

					for (int ichil = 0; ichil < other.size(); ichil++) {
						if (ichil == 0) {
							bt = new ChildHeaderText();
							bt.addText(parentText);
							bt.addText(" ");
							if (other.size() > 1) {
								bt.addText(typesTable.getTextValue("CHILDREN")
										.toLowerCase());
							} else {
								bt.addText(typesTable.getTextValue("CHIL")
										.toLowerCase());
							}
							bt.addText(":");
							repoWriter.addText(bt);

						}
						bt = new ChildListText();
						childMember = other.get(ichil);
						// childMember = tab.getChild().get(ichil);

						try {
							cdata = caller.getKontroller()
									.getSukuData("cmd=person",
											"pid=" + childMember.getPid());

							String ownerTag = "FATH";

							// check if child is adopted
							String adopTag = null;
							for (int i = 0; i < cdata.relations.length; i++) {
								if (ownerTag
										.equals(cdata.relations[i].getTag())) {
									if (cdata.relations[i].getNotices() != null) {
										adopTag = cdata.relations[i]
												.getNotices()[0].getTag();

									}
									break;

								}
							}

							notices = cdata.persLong.getNotices();

							if (adopTag != null) {
								bt.addText("("
										+ typesTable.getTextValue(adopTag)
										+ ") ");
							}
							if (cdata.persLong.getPrivacy() == null) {
								printName(bt, cdata.persLong,
										(toTable.isEmpty() ? 2 : 3));
								addChildReference(ftab, mtab, tab.getPid(),
										typesTable.getTextValue("TABLE"), bt);
								printNotices(bt, notices, tab.getPid(),
										tab.getTableNo());
							} else {
								printNameNn(bt);
							}
							if (bt.getCount() > 0) {
								repoWriter.addText(bt);

							}
						} catch (SukuException e1) {
							logger.log(Level.WARNING, "SukuException", e1);
						}
					}

					if (isMale) {
						break;
					}

				}
			}

			repoWriter.addText(bt);
		}
	}

	private String toPrintTable(long tableNo) {
		return toPrintTable(tableNo, false);
	}

	private String toPrintTable(long tableNo, boolean showGeneration) {
		String order = caller.getAncestorPane().getNumberingFormat()
				.getSelection().getActionCommand();

		long hagerSize = 1;
		int gen = 0;
		while (tableNo >= hagerSize) {
			gen++;
			hagerSize *= 2;
		}
		hagerSize /= 2;
		gen--;

		if (!order.equals("HAGER")) {

			return "" + tableNo;

		}
		if (tableNo == 1) {
			return "1";
		}
		if (!showGeneration) {
			return "" + (1 + tableNo - hagerSize);
		}

		return Roman.int2roman(gen) + "." + (1 + tableNo - hagerSize);

	}

	/**
	 * @param ftab
	 * @param bt
	 * @return
	 */
	private BodyText addParentReference(ReportUnit ftab, BodyText bt) {
		PersonInTables ref;
		ref = personReferences.get(ftab.getPid());
		if (ref != null) {
			StringBuilder sb = new StringBuilder();
			int pareCount = 0;
			if (ref.asChildren.size() > 0) {
				if (sb.length() > 0) {
					sb.append(",");
				}

			}
			for (int i = 0; i < ref.asChildren.size(); i++) {
				if (i > 0) {
					sb.append(",");
				}
				pareCount++;
				long pareTab = ref.asChildren.get(i);
				String partext = typesTable
						.getTextValue((pareTab % 2 == 0) ? "Father" : "Mother");
				if (caller.getAncestorPane().getAllBranches()) {
					long fftab = ftab.getTableNo() * 2;
					if (pareTab % 2 != 0) {
						fftab++;
					}
					sb.append(partext + " " + toPrintTable(fftab, true));
				} else {
					sb.append(partext + " "
							+ toPrintTable(ref.asChildren.get(i), true));
				}
			}
			if (sb.length() > 0) {
				bt.addText("(" + sb.toString() + "). ");
			}
		}
		// FIXME: This method appears to modify a parameter, and then return
		// this parameter as the methods return value. This will be confusing to
		// callers of this method, as it won't be apparent that the 'original'
		// passed in parameter will be changed as well. If the purpose of this
		// method is to change the parameter, it would be more clear to change
		// the method to a have a void return value. If a return type is
		// required due to interface or superclass contract, perhaps a clone of
		// the parameter should be made.
		return bt;
	}

	/**
	 * @param ftab
	 * @param bt
	 * @return
	 */
	private boolean addChildReference(ReportUnit pop, ReportUnit mom, int pid,
			String text, BodyText bt) {

		long momTable = 0;
		long dadTable = 0;

		if (pop != null) {
			dadTable = pop.getTableNo();
		}
		if (mom != null) {
			momTable = mom.getTableNo();
		}

		ReportUnit cu = personTables.get(pid);
		// if has own table then refer only there
		if (cu != null && momTable != cu.getTableNo()
				&& dadTable != cu.getTableNo()) {

			bt.addText("(" + text + " " + cu.getTableNo() + "). ");
			return true;

		}

		PersonInTables ref = personReferences.get(pid);
		if (ref == null) {
			return false;
		}

		StringBuilder sb = new StringBuilder();

		Long ownerArray[] = ref.getOwnerArray();
		if (ownerArray.length > 0) {

			boolean addComma = false;
			for (Long pif : ownerArray) {
				if (pif != momTable && pif != dadTable) {
					if (addComma) {
						sb.append(",");
					}
					addComma = true;
					sb.append(toPrintTable(pif, true));
				}
			}
		}

		if (sb.length() > 0) {

			bt.addText("(" + typesTable.getTextValue("ALSO") + " " + text + " "
					+ sb.toString() + "). ");
			return true;
		}

		return false;
	}

	private UnitNotice[] getInternalNotices(UnitNotice[] xnotices) {

		int tableCount = 0;
		int famtCount = 0;

		for (int i = 0; i < xnotices.length; i++) {
			if (xnotices[i].getTag().equals("TABLE"))
				tableCount++;
			if (xnotices[i].getTag().equals("FAMT"))
				famtCount++;
		}

		UnitNotice[] notices = new UnitNotice[xnotices.length - tableCount
				- famtCount];

		int xn = 0;

		for (int i = 0; i < xnotices.length; i++) {
			if (!xnotices[i].getTag().equals("FAMT")
					&& !xnotices[i].getTag().equals("TABLE")) {
				notices[xn++] = xnotices[i];
			}
		}
		return notices;
	}

	private String printRelationNotice(RelationNotice rn, String defType,
			int spouseNum, boolean isBefore) {

		String showType = caller.getSpouseData().getSelection()
				.getActionCommand();
		if (showType == null) {
			showType = ReportWorkerDialog.SET_SPOUSE_NONE;
		}

		StringBuilder sb = new StringBuilder();
		boolean addSpace = false;
		if (isBefore) {
			if (rn.getType() != null) {
				if (rn.getTag().equals("MARR")) {

					if (!showType.equals(ReportWorkerDialog.SET_SPOUSE_FULL)) {

						sb.append(rn.getType());
						addSpace = true;
						spouseNum = 0;
					} else {
						sb.append(defType);
					}
				}
			} else if (defType != null) {
				sb.append(defType);
			}
		} else {
			if (!rn.getTag().equals("MARR")) {
				if (rn.getTag().equals("DIV")
						|| showType.equals(ReportWorkerDialog.SET_SPOUSE_FULL)) {
					sb.append(typesTable.getTextValue(Resurses
							.getReportString(rn.getTag())));
					addSpace = true;
				}
			}
		}
		// if (sb.length() == 0 && rn.getTag() != null) {
		// sb.append(typesTable.getTextValue(rn.getTag()));
		// }

		if (sb.length() > 0) {
			if (spouseNum > 0) {
				sb.append(" " + spouseNum + ":o");
			}
			addSpace = true;
		}

		if (showType.equals(ReportWorkerDialog.SET_SPOUSE_NONE)) {
			return sb.toString();
		}
		if (showType.equals(ReportWorkerDialog.SET_SPOUSE_YEAR)) {
			if ((isBefore && rn.getTag().equals("MARR"))
					|| (!isBefore && !rn.getTag().equals("MARR"))) {
				String yr = rn.getFromDate();
				if (yr != null && yr.length() >= 4) {
					if (addSpace) {
						sb.append(" ");
					}
					sb.append(yr.substring(0, 4));
				}
			}
			return sb.toString();
		}

		if (showType.equals(ReportWorkerDialog.SET_SPOUSE_DATE)) {

			if ((isBefore && rn.getTag().equals("MARR"))
					|| (!isBefore && !rn.getTag().equals("MARR"))) {
				String yr = rn.getFromDate();
				if (yr != null && yr.length() >= 4) {

					String date = printDate(rn.getDatePrefix(),
							rn.getFromDate(), rn.getToDate());
					if (date.length() > 0) {
						if (addSpace) {
							sb.append(" ");
						}
						sb.append(date);
					}
				}
			}
			return sb.toString();
		}
		if (isBefore) {
			return sb.toString();
		} else if (!showType.equals(ReportWorkerDialog.SET_SPOUSE_FULL)) {
			return sb.toString();
		}
		if (rn.getTag().equals("MARR")) {
			if (rn.getType() == null) {
				sb.append(typesTable.getTextValue(Resurses.getReportString(rn
						.getTag())));
			} else {
				sb.append(rn.getType());
			}
			addSpace = true;
		}
		if (rn.getDescription() != null) {

			if (addSpace) {
				sb.append(" ");
			}
			sb.append("(");
			sb.append(rn.getDescription());
			sb.append(")");
			addSpace = true;

		}

		String date = printDate(rn.getDatePrefix(), rn.getFromDate(),
				rn.getToDate());
		if (date.length() > 0) {
			if (addSpace) {
				sb.append(" ");
			}
			sb.append(date);
			addSpace = true;
		}

		if (rn.getPlace() != null) {
			if (addSpace) {
				sb.append(" ");
			}
			sb.append(rn.getPlace());
			addSpace = true;
		}
		if (rn.getNoteText() != null) {
			if (addSpace) {
				if (rn.getNoteText().charAt(0) != ','
						&& rn.getNoteText().charAt(0) != '.') {
					sb.append(" ");
				}
			}

			sb.append("(");
			sb.append(trim(rn.getNoteText()));
			sb.append(")");

			addSpace = true;
		}
		String srcFormat = caller.getSourceFormat();
		if (!ReportWorkerDialog.SET_NO.equals(srcFormat)) {

			String src = trim(rn.getSource());

			String srcText = addSource(false, srcFormat, src);
			sb.append(srcText);
		}

		return sb.toString();
	}

	private void printNotices(BodyText bt, UnitNotice[] notices, int colType,
			long tableNo) {
		boolean addSpace = false;
		boolean addDot = false;
		String tag;
		String occuTypes = "|OCCU|EDUC|TITL|";
		int minSurety = caller.showMinSurety();
		if (notices.length > 0 && caller.showOnSeparateLines()) {

			repoWriter.addText(bt);
		}

		boolean forceOccuUpperCase = true;
		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			addSpace = false;
			addDot = false;
			tag = nn.getTag();
			if (!tag.equals("NAME")) {
				if ((nn.getPrivacy() == null || nn.getPrivacy().equals(
						Resurses.PRIVACY_TEXT))
						&& nn.getSurety() >= minSurety) {
					if ((typesTable.isType(tag, colType))) {

						if (typesTable.isType(tag, 1)) {

							String noType = nn.getNoticeType();
							if (noType != null) {
								bt.addText(noType);
							} else {

								bt.addText(typesTable.getTypeText(tag));
							}
							addSpace = true;
							addDot = true;
						}

						if (nn.getDescription() != null) {

							if (nn.getTag().equals("TABLE")
									&& bt.getCount() == 0) {
								// for TABLE notice the description contains a
								// header if this is the first text piece for
								// the notice

								BodyText btt = bt;
								bt = new TableHeaderText();

								bt.addText(nn.getDescription(), true, false);
								repoWriter.addText(bt);
								bt = btt;
								repoWriter.addText(bt);
							} else {
								if (addSpace) {
									bt.addText(" ");
								}
								String desc = nn.getDescription();
								if (occuTypes.indexOf(nn.getTag()) > 0) {
									if (forceOccuUpperCase) {
										if (desc.length() > 1) {
											desc = desc.substring(0, 1)
													.toUpperCase()
													+ desc.substring(1);
										}
									}
								}
								bt.addText(desc);
								addSpace = true;
								addDot = true;
							}
						}
						String dd = printDate(nn.getDatePrefix(),
								nn.getFromDate(), nn.getToDate());
						if (dd.length() > 0) {
							if (addSpace) {
								if (typesTable.getTypeText(tag).length() > 1) {
									bt.addText(" ");
								} else {
									bt.addText("\u00A0");
								}
								bt.addText(dd);
							}
							addSpace = true;
							addDot = true;
						}

						if (nn.getPlace() != null
								|| (nn.getTag().equals("RESI") && nn
										.getPostOffice() != null)) {
							if (addSpace)
								bt.addText(" ");
							bt.addText(convertPlace(nn));

							addSpace = true;
							addDot = true;
						}

						if (caller.isCreatePlaceIndexSet()
								&& typesTable.isType(nn.getTag(), 5)) {

							String place = nn.getPlace();
							if (place != null && nn.getTag().equals("RESI")
									&& nn.getPostOffice() != null) {
								place = nn.getPostOffice();
							}
							int idx = -1;
							while (idx > -2) {

								if (idx >= 0) {
									if (nn.getRefPlaces() != null
											&& idx < nn.getRefPlaces().length) {
										place = nn.getRefPlaces()[idx];

									} else {
										idx = -9999;
									}
								}

								if (place != null) {

									PlaceInTables pit = places.get(place
											.toUpperCase());
									if (pit == null) {
										pit = new PlaceInTables(place);
										places.put(place.toUpperCase(), pit);
									}
									pit.addTable(tableNo);
								}
								idx++;
							}
						}

						if ((caller.isShowVillageFarm() && (nn.getVillage() != null
								|| nn.getFarm() != null || nn.getCroft() != null))
								|| nn.getState() != null
								|| nn.getCountry() != null) {
							if (addSpace) {
								bt.addText(" ");
								addSpace = false;
							}
							bt.addText("(");
							if (caller.isShowVillageFarm()
									&& nn.getVillage() != null) {
								bt.addText(nn.getVillage());
								addSpace = true;
							}
							if (caller.isShowVillageFarm()
									&& nn.getFarm() != null) {
								if (addSpace) {
									bt.addText(" ");
									addSpace = true;
								}
								bt.addText(nn.getFarm());
								addSpace = true;
							}
							if (caller.isShowVillageFarm()
									&& nn.getCroft() != null) {
								if (addSpace) {
									bt.addText(" ");
									addSpace = true;
								}
								bt.addText(nn.getCroft());

							}

							if (nn.getState() != null) {
								if (addSpace) {
									bt.addText(" ");
								}
								addSpace = true;
								bt.addText(nn.getState());
							}
							if (nn.getCountry() != null) {
								if (addSpace) {
									bt.addText(", ");
								}
								bt.addText(nn.getCountry());
							}
							addSpace = true;
							addDot = true;
							bt.addText(")");
						}
						if (tag.startsWith("PHOT") && caller.isSeparateImages()) {
							if (addSpace) {
								addSpace = true;
							}
							imageNumber++;
							bt.addText(Resurses.getReportString("INDEX_IMAGE")
									+ " " + imageNumber + ". ", true, false);

							ImageNotice inoti = new ImageNotice(nn,
									imageNumber, tableNo);
							imgNotices.add(inoti);
						} else {

							if (caller.showImages()) {
								ImageText imagetx = new ImageText();
								BufferedImage img;
								try {
									img = nn.getMediaImage();
								} catch (IOException e) {
									logger.log(Level.WARNING, "getMedia", e);

									img = null;
								}
								if (img != null) {
									double imh = img.getHeight();
									double imw = img.getWidth();
									double newh = 300;
									double neww = 300;

									// Dimension xx = caller.getImageMaxSize();

									if (imh <= newh) {
										if (imw <= neww) {
											newh = imh;
											neww = imw;

										} else {
											newh = imh * (neww / imw);
											neww = imw;
										}
									} else {
										neww = imw * (newh / imh);
									}

									imageNumber++;
									String imgTitle = "";
									if (caller.isNumberingImages()) {
										imgTitle = Resurses
												.getReportString("INDEX_IMAGE")
												+ "\u00A0" + imageNumber + ". ";
									}
									if (nn.getMediaTitle() != null) {
										String titl = trim(nn.getMediaTitle());
										imgTitle += titl + ".";
									}
									String xxx = "0000" + imageNumber;
									String imgNamePrefix = xxx.substring(xxx
											.length() - 4) + "_";
									imagetx.setImage(
											img,
											nn.getMediaData(),
											img.getWidth(),
											img.getHeight(),
											imgNamePrefix
													+ nn.getMediaFilename(),
											imgTitle, nn.getTag());
									imagetx.addText("");
								}
								// if (nn.getMediaTitle() != null) {
								// imagetx.addText(nn.getMediaTitle());
								// }
								if (imagetx.getCount() > 0) {
									repoWriter.addText(bt);
									repoWriter.addText(imagetx);

								}
							}
							// }

							if (nn.getNoteText() != null) {
								String trimmed = trim(nn.getNoteText());
								if (addSpace) {

									if (trimmed != null && !trimmed.isEmpty()) {
										if (trimmed.charAt(0) != ','
												&& trimmed.charAt(0) != '.') {
											bt.addText(" ");
										}
									}
								}
								int tlen = printText(bt, trimmed,
										nn.getRefNames());
								if (tlen > 0) {
									addSpace = true;
									addDot = true;
								}
							}
						}
						if (nn.getRefNames() != null) {

							for (int i = 0; i < nn.getRefNames().length; i++) {
								String txtName = nn.getRefNames()[i];
								PersonInTables ppText = textReferences
										.get(txtName);
								if (ppText == null) {
									int refpid = --referencePid;
									ppText = new PersonInTables(refpid);
									String[] parts = txtName.split(",");
									if (parts.length == 2) {
										ppText.shortPerson = new PersonShortData(
												refpid, parts[1], null, null,
												parts[0], null, null, null);
										textReferences.put(txtName, ppText);
									} else if (parts.length == 1) {
										ppText.shortPerson = new PersonShortData(
												refpid, parts[0], null, null,
												null, null, null, null);
										textReferences.put(txtName, ppText);
									}
								}
								ppText.references.add(tableNo);

							}

						}

						if (caller.showAddress()) {

							if (nn.getAddress() != null) {
								if (addSpace)
									bt.addText(" ");
								int tlen = 0;
								String parts[] = nn.getAddress()
										.replaceAll("\\r", "").split("\n");
								for (int i = 0; i < parts.length; i++) {

									if (i > 0) {
										bt.addText(",");
									}
									bt.addText(parts[i]);
									tlen = i + 1;
								}
								if (nn.getPostalCode() != null
										|| nn.getPostOffice() != null) {
									if (tlen++ > 0) {
										bt.addText(",");
									}
									if (nn.getPostalCode() != null) {
										bt.addText(nn.getPostalCode());
										if (nn.getPostOffice() != null) {
											bt.addText(" ");
											bt.addText(nn.getPostOffice());
											tlen++;
										}
									} else {
										bt.addText(nn.getPostOffice());
										tlen++;
									}

								}

								if (nn.getEmail() != null) {
									if (tlen++ > 0) {
										bt.addText(",");
									}
									bt.addText("[" + nn.getEmail() + "]");
								}

								if (tlen > 0) {
									addSpace = true;
									addDot = true;
								}
							}
						}
						String srcFormat = caller.getSourceFormat();
						if (!ReportWorkerDialog.SET_NO.equals(srcFormat)) {

							String src = trim(nn.getSource());

							String text = addSource(addDot, srcFormat, src);
							bt.addText(text);
							if (!text.isEmpty()) {
								addDot = true;
							}
						}
						if (addDot) {

							if (occuTypes.indexOf(tag) > 0) {
								String nxttag = null;
								if (j < notices.length - 1) {
									nxttag = notices[j + 1].getTag();
								}
								if (nxttag != null) {
									if (occuTypes.indexOf(nxttag) > 0) {
										bt.addText(", ");
										forceOccuUpperCase = false;
									} else {
										if (!bt.endsWithText(".")) {

											bt.addText(". ");
										} else {
											bt.addText(" ");
										}
										forceOccuUpperCase = true;
									}
								} else {
									if (!bt.endsWithText(".")) {
										bt.addText(". ");
									} else {
										bt.addText(" ");
									}
									forceOccuUpperCase = true;
								}

							} else {
								if (!bt.endsWithText(".")) {
									bt.addText(". ");
								} else {
									bt.addText(" ");
								}
								forceOccuUpperCase = true;
							}
							if (caller.showOnSeparateLines()) {

								repoWriter.addText(bt);
							}
						}

					}
				}
			}
		}

		// if (caller.showOnSeparateLines()){
		// repoWriter.addText(bt);
		// }

	}

	private String addSource(boolean addDot, String srcFormat, String src) {
		StringBuilder sb = new StringBuilder();
		if (src != null && !src.isEmpty()) {

			if (srcFormat.equals(ReportWorkerDialog.SET_TX1)) {
				if (addDot) {
					sb.append(". ");

				}
				sb.append(" ");
				sb.append(src);

			} else if (srcFormat.equals(ReportWorkerDialog.SET_TX2)) {
				sb.append(" [");
				sb.append(src);
				sb.append("]");

			} else if (srcFormat.equals(ReportWorkerDialog.SET_AFT)) {
				Integer srcId = refs.get(src);
				if (srcId == null) {
					srcId = refs.size() + 1;
					refs.put(src, srcId);
				}
				sb.append(" [");
				sb.append(srcId.toString());
				sb.append("]");
				addDot = true;
			}

		}
		return sb.toString();
	}

	/**
	 * Set 1st paragraph bold (header) if it begins with *
	 * 
	 * @param bt
	 * @param text
	 * @param strings
	 * @return
	 */
	private int printText(BodyText bt, String text, String[] namesin) {
		if (text == null)
			return 0;

		if (namesin != null && caller.showBoldNames()) {
			String names[] = new String[namesin.length];
			for (int i = 0; i < names.length; i++) {
				names[i] = namesin[i];
			}

			int firstName = -1;
			int nameIdx = -1;
			String nameTxt = null;

			TextNamePart txtPart = null;
			for (int i = 0; i < names.length; i++) {
				if (names[i] != null) {
					txtPart = locateName(text, names[i]);

					if (txtPart != null && txtPart.location >= 0) {
						if (firstName < 0 || txtPart.location < firstName) {
							firstName = txtPart.location;
							nameTxt = txtPart.nameText;
							nameIdx = i;
						}
					}
				}
			}
			String nxtText;
			int fullLength = 0;
			if (firstName > 0) {
				nxtText = text.substring(firstName + nameTxt.length());
				int len1 = printText(bt, text.substring(0, firstName), null);
				fullLength += len1;
			} else if (firstName == 0) {
				nxtText = text;
			} else {
				printText(bt, text, null);
				return fullLength + text.length();
			}
			bt.addText(nameTxt, true, false);
			if (nameTxt.equals(text)) {
				return fullLength + nameTxt.length();
			}
			names[nameIdx] = null;
			int len2 = printText(bt, nxtText, names);
			return fullLength + len2;

		}

		StringBuilder sb = new StringBuilder();
		ArrayList<String> v = new ArrayList<String>();
		boolean wasWhite = false;
		boolean wasNl = true;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == ' ' || c == '\r' || c == '\t') {
				if (!wasWhite) {
					sb.append(' ');
					wasWhite = true;
				}
			} else {
				if (c == '\n') {
					if (wasNl) {
						v.add(sb.toString());
						sb.delete(0, sb.length());
						wasNl = false;
					} else {
						sb.append(' ');
						wasNl = true;
					}
				} else {
					wasNl = false;
					wasWhite = false;
					sb.append(c);
				}
			}
		}
		if (sb.length() > 0) {
			v.add(sb.toString());
		}
		for (int i = 0; i < v.size(); i++) {
			String aux = v.get(i);
			if (i == 0 && aux.length() > 0 && aux.charAt(0) == '*') {
				repoWriter.addText(bt);
				int fontSize = bt.getFontSize();
				if (aux.length() > 2 && aux.substring(0, 2).equals("**")) {
					bt.setFontSize(fontSize + 4);
					bt.addText(aux.substring(2), true, false);
				} else {
					bt.setFontSize(fontSize + 2);
					bt.addText(aux.substring(1), true, false);

				}
				// repoWriter.addText(bt);
				bt.setFontSize(fontSize);
			} else if (i > 0) {
				repoWriter.addText(bt);
				bt.addText(aux);
			} else {
				bt.addText(aux);
			}
		}

		return text.length();
	}

	private TextNamePart locateName(String text, String name) {
		TextNamePart tnp = new TextNamePart();

		String parts[] = name.split(",");
		if (parts.length == 2) {

			if (parts[0].isEmpty()) {
				tnp.location = text.indexOf(parts[1]);
				tnp.nameText = parts[1];
			} else if (parts[1].isEmpty()) {
				tnp.location = text.indexOf(parts[0]);
				tnp.nameText = parts[0];
			} else {
				tnp.location = text.indexOf(parts[1] + " " + parts[0]);
				tnp.nameText = parts[1] + " " + parts[0];
			}

		}
		if (tnp.location < 0) {
			tnp.location = text.indexOf(name);
			tnp.nameText = name;
		}

		if (tnp.location >= 0) {
			int j = 0;
			for (j = 0; j < 5; j++) {
				if (text.length() == tnp.location + tnp.nameText.length()) {
					break;
				}
				char c = text.charAt(tnp.location + tnp.nameText.length());
				if (c == ' ') {
					break;
				}
				tnp.nameText += c;
			}

		}

		return tnp;
	}

	private String printDate(String datePrefix, String dateFrom, String dateTo) {
		String mellan = "";
		String framme = "";
		if (dateFrom == null)
			return "";
		StringBuilder sb = new StringBuilder();

		if (datePrefix != null) {
			framme = typesTable.getTextValue(datePrefix);
			if (datePrefix.equals("FROM") && dateTo != null) {
				mellan = typesTable.getTextValue("TO");
			}
			if (datePrefix.equals("BET") && dateTo != null) {
				mellan = typesTable.getTextValue("AND");
			}
		}
		if (!framme.isEmpty()) {
			sb.append(framme);
			sb.append(" ");
		}
		sb.append(toRepoDate(dateFrom));
		if (!mellan.isEmpty() && dateTo != null) {
			sb.append(" ");
			sb.append(mellan);
			sb.append(" ");
			sb.append(toRepoDate(dateTo));
		}
		return sb.toString();

	}

	private String toRepoDate(String date) {
		if (date == null)
			return null;
		String df = caller.getDateFormat();

		int dd = 0;
		int mm = 0;
		int yy = 0;
		try {
			if (date.length() == 4) {
				return date;
			} else if (date.length() >= 6) {
				yy = Integer.parseInt(date.substring(0, 4).trim());
				mm = Integer.parseInt(date.substring(4, 6).trim());
			}
			if (date.length() == 8) {

				dd = Integer.parseInt(date.substring(6, 8).trim());

			}
			if (df.equals("SE")) {
				if (dd == 0) {
					return "" + date.substring(0, 4) + "-"
							+ date.substring(4, 6);
				} else {
					return "" + date.substring(0, 4) + "-"
							+ date.substring(4, 6) + "-" + date.substring(6, 8);
				}
			} else if (df.equals("UK")) {
				if (dd == 0) {
					return "" + mm + "/" + yy;
				} else {
					return "" + dd + "/" + mm + "/" + yy;
				}
			} else if (df.equals("US")) {
				if (dd == 0) {
					return "" + mm + "/" + yy;
				} else {
					return "" + mm + "/" + dd + "/" + yy;
				}
			} else {
				if (dd == 0) {
					return "" + mm + "." + yy;
				} else {
					return "" + dd + "." + mm + "." + yy;
				}
			}
		} catch (NumberFormatException ne) {
			logger.log(Level.WARNING, "toRepoDate", ne);
			return "00.00.00";
		}

	}

	/**
	 * print name to report. First name is always printed regardless of types
	 * settings unless set as privacy = true
	 * 
	 * TODO special underline such as "Anna*-Liisa" (Juha-Pekka*) TODO underline
	 * Anna-Liisa** or Per Erik** to result in two names underlined
	 * 
	 * @param bt
	 * @param notices
	 * @param isMain
	 */
	private void printName(BodyText bt, PersonLongData persLong, int colType) {
		int nameCount = 0;
		String prevGivenname = "";
		String prevPatronyme = "";
		String prevPostfix = "";
		UnitNotice[] notices = persLong.getNotices();
		int minSurety = caller.showMinSurety();
		boolean isDead = false;
		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			if (nn.getTag().equals("DEAT") || nn.getTag().equals("BURI")) {
				isDead = true;
			}
			if (nn.getTag().equals("BIRT")) {
				String bd = nn.getFromDate();
				if (bd != null && bd.length() >= 4) {

					try {
						int by = Integer.parseInt(bd.substring(0, 4));
						Calendar rightNow = Calendar.getInstance();
						int cy = rightNow.get(Calendar.YEAR);
						if (cy > by + 115) {
							isDead = true;
						}
					} catch (NumberFormatException ne) {
						// NumberFormatException ignored
					}

				}
			}
		}

		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			if (nn.getTag().equals("NAME")) {
				if ((nn.getPrivacy() == null || nn.getPrivacy().equals(
						Resurses.PRIVACY_TEXT))
						&& nn.getSurety() >= minSurety) {
					if ((typesTable.isType("NAME", colType) || nameCount == 0)) {

						if (nameCount > 0 && nn.getNoticeType() == null) {
							bt.addText(", ");
							if (typesTable.isType("NAME", 1)) {
								String[] parts = typesTable.getTypeText("NAME")
										.split(";");
								String part = null;
								if (parts != null && parts.length > 1) {
									if (j < notices.length - 1) {
										if (notices[j + 1].getTag().equals(
												"NAME")) {
											part = parts[0];
										}
									}
									if (part == null) {
										if (parts.length < 3 || isDead == false) {
											part = parts[1];
										} else {
											part = parts[2];
										}
									}
									bt.addText(part);
									bt.addText(" ");
								} else if (parts.length == 1) {
									bt.addText(parts[0]);
									bt.addText(" ");
								}
							}
						} else if (nn.getNoticeType() != null) {
							if (nameCount > 0) {
								if (!nn.getNoticeType().equals("/")
										&& !nn.getNoticeType().equals("(")) {
									bt.addText(", ");
									bt.addText(nn.getNoticeType());
									bt.addText(" ");
								} else {
									if (nn.getNoticeType().equals("(")) {
										bt.addText(" ");
									}
									bt.addText(nn.getNoticeType());

								}
							}

						}
						boolean wasName = false;
						if (!prevGivenname.equals(nv(nn.getGivenname()))) {
							prevGivenname = nv(nn.getGivenname());

							printGivenname(bt, prevGivenname, true);
							if (!prevGivenname.isEmpty()) {
								wasName = true;
							}
						}

						if (!prevGivenname.isEmpty()
								&& !nv(nn.getPrefix()).isEmpty()) {
							if (wasName) {
								bt.addText(" ", caller.showBoldNames(), false);
							}
							bt.addText(nn.getPrefix(), caller.showBoldNames(),
									false);
							wasName = true;
						}

						if (!prevPatronyme.equals(nv(nn.getPatronym()))) {
							prevPatronyme = nv(nn.getPatronym());
							if (wasName && !nv(nn.getPatronym()).isEmpty()) {
								bt.addText(" ", caller.showBoldNames(), false);
							}

							if (!nv(nn.getPatronym()).isEmpty()) {
								bt.addText(nn.getPatronym(),
										caller.showBoldNames(), false);
								wasName = true;
							}
						}
						if (wasName && !nv(nn.getSurname()).isEmpty()) {
							bt.addText(" ", caller.showBoldNames(), false);
						}
						if (!nv(nn.getSurname()).isEmpty()) {
							bt.addText(nn.getSurname(), caller.showBoldNames(),
									false);
							wasName = true;
						}
						if (!prevPostfix.equals(nv(nn.getPostfix()))) {
							prevPostfix = nv(nn.getPostfix());
							if (wasName && !nv(nn.getPostfix()).isEmpty()) {
								bt.addText(" ", caller.showBoldNames(), false);
							}

							if (!nv(nn.getPostfix()).isEmpty()) {
								bt.addText(nn.getPostfix(),
										caller.showBoldNames(), false);
							}
						}

						if (nn.getNoticeType() != null
								&& nn.getNoticeType().equals("(")) {
							bt.addText(")");
						}
					}
					nameCount++;
				}
			}
		}
		String srcFormat = caller.getSourceFormat();

		if (!ReportWorkerDialog.SET_NO.equals(srcFormat)) {
			String src = persLong.getSource();
			String text = addSource(true, srcFormat, src);
			bt.addText(text);
		}

		bt.addText(". ");
	}

	private void printGivenname(BodyText bt, String prevGivenname,
			boolean useBoldInfo) {
		String[] nameParts = prevGivenname.split(" ");
		boolean doBold = caller.showBoldNames();
		if (!useBoldInfo) {
			doBold = false;
		}

		for (int k = 0; k < nameParts.length; k++) {
			String namePart = nameParts[k];
			String startChar = "";
			String endChar = "";
			if (namePart.length() > 2
					&& ((namePart.charAt(0) == '(' && namePart.charAt(namePart
							.length() - 1) == ')') || (namePart.charAt(0) == '\"' && namePart
							.charAt(namePart.length() - 1) == '\"'))) {
				char[] c = new char[1];
				c[0] = namePart.charAt(0);
				startChar = new String(c);
				c[0] = namePart.charAt(namePart.length() - 1);
				endChar = new String(c);
				namePart = namePart.substring(1, namePart.length() - 1);

			}
			if (!startChar.isEmpty()) {
				bt.addText(startChar, doBold, false);
			}
			String[] subParts = namePart.split("-");

			int astidx = namePart.indexOf("*");
			int bstidx = namePart.indexOf("**");
			if (bstidx > 0 || subParts.length == 1) {
				if (astidx > 0) {

					if (astidx == namePart.length() - 1) {
						bt.addText(namePart.substring(0, astidx), doBold,
								caller.showUnderlineNames());
					} else {

						if (bstidx > 0) {
							if (bstidx == namePart.length() - 2) {

								bt.addText(namePart.substring(0, bstidx),
										doBold, caller.showUnderlineNames());
							}
						}
					}
				} else {
					bt.addText(namePart, doBold, false);
				}
			} else {

				for (int kk = 0; kk < subParts.length; kk++) {
					String subPart = subParts[kk];
					int cstidx = subPart.indexOf("*");
					if (kk > 0) {
						bt.addText("-", doBold, false);
					}
					if (cstidx >= 0 && cstidx == subPart.length() - 1) {

						bt.addText(subPart.substring(0, cstidx), doBold,
								caller.showUnderlineNames());

					} else {
						bt.addText(subPart, doBold, false);
					}
				}
			}

			if (!endChar.isEmpty()) {
				bt.addText(endChar, doBold, false);
			}
			if (k != nameParts.length - 1) {
				bt.addText(" ", doBold, false);
			}
		}
	}

	private HashMap<String, String> bendMap = null;

	/**
	 * Convert place.
	 * 
	 * @param notice
	 *            the notice
	 * @return the string
	 */
	protected String convertPlace(UnitNotice notice) {
		String place = null;
		if (notice.getPlace() != null) {
			place = notice.getPlace();
		} else if (notice.getTag().equals("RESI")
				&& notice.getPostOffice() != null) {
			place = toProper(notice.getPostOffice());
		} else {
			return null;
		}

		if (caller.isBendPlaces()) {
			if (bendMap == null) {
				bendMap = new HashMap<String, String>();
				try {
					SukuData resp = Suku.kontroller.getSukuData("cmd=get",
							"type=conversions",
							"lang=" + Resurses.getLanguage());
					for (int i = 0; i < resp.vvTexts.size(); i++) {
						String[] cnvx = resp.vvTexts.get(i);
						String key = cnvx[1] + "|" + cnvx[0];
						bendMap.put(key.toLowerCase(), cnvx[2]);
					}
				} catch (SukuException e) {
					JOptionPane.showMessageDialog(caller, e.getMessage());
				}
			}
			String tag = notice.getTag();
			String rule = typesTable.getTypeRule(tag);
			if (rule != null) {
				String key = rule + "|" + place;
				String tmp = bendMap.get(key.toLowerCase());
				if (tmp != null) {
					place = tmp;
				} else {
					tmp = typesTable.getTextValue("CNV_" + rule);
					if (tmp != null && !tmp.isEmpty() && !tmp.equals("x")) {
						place = tmp + " " + place;
					}
				}
			}
		}
		return place;
	}

	private String toProper(String postOffice) {
		if (postOffice == null) {
			return null;
		}
		if (postOffice.indexOf(" ") < 0) {
			String tmp = postOffice.toLowerCase();
			if (tmp.length() < 2) {
				return postOffice;
			}
			String A = tmp.substring(0, 1).toUpperCase();
			return A + tmp.substring(1);
		}
		return postOffice;

	}

	private String trim(String text) {
		if (text == null)
			return null;

		String tek = spaceTrim(text);

		if (tek.equals(".")) {
			return "";
		}
		if (tek.endsWith(".")) {
			tek = tek.substring(0, tek.length() - 1);
		}
		return spaceTrim(tek);
	}

	private String spaceTrim(String text) {
		StringBuilder sb = new StringBuilder();
		int lastSpace = -1;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c != ' ' || sb.length() > 0) {
				sb.append(c);
			}
			if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
				if (lastSpace < 0) {
					lastSpace = sb.length() - 1;
				}
			} else {
				lastSpace = -1;
			}
		}

		if (lastSpace > 0) {
			return sb.toString().substring(0, lastSpace);
		} else if (lastSpace == 0) {
			return "";
		} else {
			return sb.toString();
		}

	}

	/**
	 * Nv.
	 * 
	 * @param text
	 *            the text
	 * @return the string
	 */
	protected String nv(String text) {
		if (text == null)
			return "";
		return text;
	}

	class TextNamePart {
		int location = -1;
		int nameIdx = -1;
		String nameText = null;
	}

	/**
	 * Implemented by derived class.
	 * 
	 * @param b
	 *            the new visible
	 */
	public abstract void setVisible(boolean b);

	/**
	 * Images may be stored in a Vector<ImageNotice> for later print.
	 */
	class ImageNotice {

		/** The nn. */
		UnitNotice nn = null;

		/** The img number. */
		int imgNumber = 0;

		/** The tab no. */
		long tabNo = 0;

		/**
		 * Instantiates a new image notice.
		 * 
		 * @param nn
		 *            the nn
		 * @param imgNumber
		 *            the img number
		 * @param tabNo
		 *            the tab no
		 */
		ImageNotice(UnitNotice nn, int imgNumber, long tabNo) {
			this.nn = nn;
			this.tabNo = tabNo;
			this.imgNumber = imgNumber;
		}
	}

}
