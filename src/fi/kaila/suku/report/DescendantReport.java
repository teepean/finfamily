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
import fi.kaila.suku.report.style.ChildHeaderText;
import fi.kaila.suku.report.style.ChildListText;
import fi.kaila.suku.report.style.ChildSpouseText;
import fi.kaila.suku.report.style.MainPersonText;
import fi.kaila.suku.report.style.SpousePersonText;
import fi.kaila.suku.report.style.SubPersonText;
import fi.kaila.suku.report.style.TableHeaderText;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Roman;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * <h1>Descendant report creator</h1>
 *
 * The descendant report structure is creted here.
 *
 * @author Kalle
 */
public class DescendantReport extends CommonReport {

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
	public DescendantReport(ReportWorkerDialog caller, SukuTypesTable typesTable, ReportInterface repoWriter) {
		super(caller, typesTable, repoWriter);

	}

	/**
	 * execute the report.
	 *
	 */
	@Override
	public void executeReport() {
		SukuData vlist = null;

		if (caller.getDescendantPane().getTableOrder().getSelection() == null) {
			logger.log(Level.INFO,
					Resurses.getString(Resurses.CREATE_REPORT) + Resurses.getString("REPORT_ERROR_ORDERMISSING"));
			JOptionPane.showMessageDialog(caller, Resurses.getString(Resurses.CREATE_REPORT) + ": "
					+ Resurses.getString("REPORT_ERROR_ORDERMISSING"));
			return;
		}
		final String order = caller.getDescendantPane().getTableOrder().getSelection().getActionCommand();

		try {
			vlist = caller.getKontroller().getSukuData("cmd=" + Resurses.CMD_CREATE_TABLES,
					"type=" + Resurses.CMD_DESC_TYPE, "order=" + order,
					"adopted=" + caller.getDescendantPane().getAdopted(),
					"generations=" + caller.getDescendantPane().getGenerations(),
					"spougen=" + caller.getDescendantPane().getSpouseAncestors(),
					"chilgen=" + caller.getDescendantPane().getChildAncestors(), "pid=" + caller.getPid());

			if (vlist != null) {
				logger.info("Descendant repo to " + repoWriter.toString());
				tables = vlist.tables;

				personReferences = Utils.getDescendantToistot(tables);

				if (tables.size() > 0) {

					for (int i = 0; i < tables.size(); i++) {
						final ReportUnit tt = tables.get(i);
						final int pid = tt.getPid();
						final PersonInTables ptt = personReferences.get(pid);
						if (ptt != null) {
							ptt.setMyTable(tt.getTableNo());
						}
					}

					for (int i = 0; i < tables.size(); i++) {
						final ReportUnit tt = tables.get(i);
						if (tt != null) {
							tabMap.put(tt.getTableNo(), tt);
						}
					}

					repoWriter.createReport();
					createReport();
					if (!caller.isCancelRequested()) {
						repoWriter.closeReport();
					}
				}
			}
		} catch (final SukuException e) {
			caller.requestCancel();
			logger.log(Level.WARNING, "background reporting", e);
			String message = Resurses.getString(e.getMessage());
			final int createdIdx = message.toLowerCase().indexOf("the column name");
			if (createdIdx > 0) {
				message += "\n" + Resurses.getString("SUGGEST_UPDATE");
			}
			JOptionPane.showMessageDialog(caller, message);
			return;
		}
	}

	private void createReport() throws SukuException {
		textReferences = new HashMap<String, PersonInTables>();
		// try {
		for (int i = 0; i < tables.size(); i++) {

			final ReportUnit tab = tables.get(i);
			createDescendantTable(i, tab);
			if (caller.isCancelRequested()) {
				return;
			}
		}

		printImages();
		try {
			printNameIndex();
		} catch (final SukuException e) {
			logger.log(Level.WARNING, "NameIndex", e);
			JOptionPane.showMessageDialog(caller, Resurses.getString(Resurses.CREATE_REPORT) + ":" + e.getMessage());
		}
		caller.setRunnerValue("100;OK");

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
	 * @throws SukuException
	 *             the suku exception
	 */
	protected void createDescendantTable(int idx, ReportUnit tab) throws SukuException {

		BodyText bt = null;
		final ReportTableMember subjectmember = tab.getParent().get(0);
		SukuData pdata = null;
		boolean forceSpouseNum = false;
		final StringBuilder tabOwner = new StringBuilder();
		int tableOffset = caller.getDescendantPane().getStartTable();
		if (tableOffset > 1) {
			tableOffset = tableOffset - 1;
		} else {
			tableOffset = 0;
		}

		pdata = caller.getKontroller().getSukuData("cmd=person", "pid=" + subjectmember.getPid(),
				"lang=" + Resurses.getLanguage());

		if (pdata.persLong == null) {
			return;
		}

		final UnitNotice[] xnotices = pdata.persLong.getNotices();

		int tableCount = 0;
		int famtCount = 0;

		for (final UnitNotice xnotice : xnotices) {
			if (xnotice.getTag().equals("TABLE")) {
				tableCount++;
			}
			if (xnotice.getTag().equals("FAMT")) {
				famtCount++;
			}
		}

		UnitNotice[] notices = new UnitNotice[xnotices.length - tableCount - famtCount];

		final UnitNotice[] tableNotices = new UnitNotice[tableCount];
		final UnitNotice[] famtNotices = new UnitNotice[famtCount];

		final PersonLongData dummyPerson = new PersonLongData(0, "INDI", "M");

		int xn = 0;
		int xtable = 0;
		int xfamt = 0;

		for (final UnitNotice xnotice : xnotices) {
			if (xnotice.getTag().equals("FAMT")) {
				famtNotices[xfamt++] = xnotice;
			} else if (xnotice.getTag().equals("TABLE")) {
				tableNotices[xtable++] = xnotice;
			} else {
				notices[xn++] = xnotice;
			}
		}

		for (final UnitNotice nn : notices) {
			if (nn.getTag().equals("NAME")) {
				tabOwner.append(nn.getSurname());
				if (tabOwner.length() > 0) {
					tabOwner.append(" ");
				}
				tabOwner.append(nn.getGivenname());
				break;
			}
		}
		final float prose = (idx * 100f) / tables.size();
		caller.setRunnerValue("" + (int) prose + ";" + (tab.getTableNo() + tableOffset) + ":" + tabOwner);

		String genText = "";
		if (tab.getGen() >= 0) {
			genText = Roman.int2roman(tab.getGen() + 1);
		}

		if (tableNotices.length > 0) {
			bt = new MainPersonText();
			repoWriter.addText(bt);
			bt = new MainPersonText();
			dummyPerson.setNotices(tableNotices);
			printNotices(bt, dummyPerson, 2, tab.getTableNo() + tableOffset);
			repoWriter.addText(bt);
			bt = new MainPersonText();
			bt.addText("");
			repoWriter.addText(bt);
		}

		bt = new TableHeaderText();
		bt.addAnchor("" + (tab.getTableNo() + tableOffset));
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
		if (caller.showRefn() && (pdata.persLong.getRefn() != null)) {
			bt.addText(pdata.persLong.getRefn());
			bt.addText(" ");
		}
		if (caller.showGroup() && (pdata.persLong.getGroupId() != null)) {
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
			fromTable = ref.getReferences(tab.getTableNo(), false, true, false, tableOffset);
		}

		if (fromTable.length() > 0) {
			final String parts[] = fromTable.split(",");

			bt.addText("(");
			for (int i = 0; i < parts.length; i++) {
				if (i > 0) {
					bt.addText(", ");
				}
				try {
					final long refTab = Long.parseLong(parts[i]);
					final ReportUnit pare = tabMap.get(refTab - tableOffset);
					if (pare == null) {
						logger.severe("parents tab " + refTab + " not found");
					} else {

						final ReportTableMember rm = pare.getParent().get(0);
						final String pareSex = rm.getSex();
						if (pareSex.equals("M")) {
							bt.addText(typesTable.getTextValue("Father"));
						} else {
							bt.addText(typesTable.getTextValue("Mother"));
						}
						bt.addText(": ");
						final int ppid = rm.getPid();

						final SukuData ppdata = printParentReference(bt, ppid);
						//
						// lets see if we need other parent too
						//
						if (caller.getDescendantPane().isBothParents()) {
							// OK. Let's see if we can locate him/her
							final String pareTag = pareSex.equals("M") ? "MOTH" : "FATH";
							int parePid = 0;
							int pareSurety = 0;
							for (final Relation rel : pdata.relations) {
								if (rel.getTag().equals(pareTag)) {
									if (rel.getSurety() > pareSurety) {
										// still check for adoptions
										boolean isAdopt = false;
										if (rel.getNotices() != null) {
											for (final RelationNotice rn : rel.getNotices()) {
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
								for (int j = 0; j < pare.getParent().size(); j++) {
									if (pare.getParent().get(j).getPid() == parePid) {
										isInTab = true;
										break;
									}
								}

								if (isInTab) {
									bt.addText(", ");

									if (pareSex.equals("M")) {
										bt.addText(typesTable.getTextValue("Mother"));
									} else {
										bt.addText(typesTable.getTextValue("Father"));
									}
									bt.addText(": ");
									printParentReference(bt, parePid);
								}
							}
						}
						if ((ppdata.pers != null) && (ppdata.pers.length > 0)) {
							bt.addText(" ");
							bt.addLink(typesTable.getTextValue("FROMTABLE").toLowerCase() + " " + refTab, true, false,
									false, "" + refTab);

						}

					}
				} catch (final NumberFormatException ne) {
					// not expected here i.e. program error
					logger.log(Level.WARNING, "pare fetch", ne);

				} catch (final SukuException e) {
					logger.log(Level.WARNING, "pare fetch", e);
				}
			}

			bt.addText("). ");
		}

		if (pdata.persLong.getPrivacy() == null) {
			printNotices(bt, pdata.persLong, 2, tab.getTableNo() + tableOffset);
		}
		fromTable = "";
		ref = personReferences.get(tab.getPid());

		//
		// check references for table owner here
		//
		String childTables = "";

		if (ref != null) {
			childTables = ref.getReferences(tab.getTableNo(), false, true, false, tableOffset);
			fromTable = ref.getReferences(tab.getTableNo(), true, false, false, tableOffset);
			if (!childTables.isEmpty()) {
				fromTable = "";// if as child then don't refer to as spouse
				// spouse will be referred from the spouse
			}
			if (fromTable.length() == 0) {

				fromTable = ref.getReferences(tab.getTableNo(), false, false, true, tableOffset);
			}
		}
		if (fromTable.length() > 0) {
			if (childTables.isEmpty()) {
				bt.addLink(typesTable.getTextValue("ALSO") + "\u00A0" + fromTable + ". ", true, false, false,
						"" + fromTable);
			} else {
				bt.addLink(typesTable.getTextValue("ISFAMILY") + " " + fromTable + ". ", true, false, false,
						"" + fromTable);
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
				final SukuData sub = caller.getKontroller().getSukuData("cmd=person",
						"pid=" + subjectmember.getSubPid(i));
				notices = sub.persLong.getNotices();
				if (sub.persLong.getPrivacy() == null) {
					printName(bt, sub.persLong, 4);
					printNotices(bt, sub.persLong, 4, tab.getTableNo() + tableOffset);
				} else {
					printNameNn(bt);
				}
				fromTable = "";
				ref = personReferences.get(subjectmember.getSubPid(i));
				if (ref != null) {
					fromTable = ref.getReferences(tab.getTableNo(), true, true, true, tableOffset);
					//
					// here we add references to table owner parents
					// Should come here only for report subject ancestors
					// None should be part of the family (i.e. descendants of
					// himself
					//
					if (fromTable.length() > 0) {
						bt.addLink(typesTable.getTextValue("ALSO") + " " + fromTable + ". ", true, false, false,
								"" + fromTable);
					}
				}

				repoWriter.addText(bt);

			}
		} catch (final SukuException e1) {
			logger.log(Level.WARNING, "background reporting", e1);
			JOptionPane.showMessageDialog(caller, e1.getMessage());
			return;
		}

		//
		// spouse list
		//
		// do we have a child who's parents are not here
		for (int ix = 0; ix < tab.getChild().size(); ix++) {
			final ReportTableMember chi = tab.getChild().get(ix);
			int jx = 0;
			for (jx = 1; jx < tab.getParent().size(); jx++) {
				final ReportTableMember chip = tab.getParent().get(jx);
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

				printSpouse(tab.getTableNo(), tab.getPid(), bt, spouseMember, spouNum, tableOffset);

			} catch (final SukuException e1) {
				logger.log(Level.WARNING, "background reporting", e1);

			}
		}

		if (tab.getChild().size() > 0) {
			bt = new ChildHeaderText();
			genText = Roman.int2roman(tab.getGen() + 2);
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
				cdata = caller.getKontroller().getSukuData("cmd=person", "pid=" + childMember.getPid());
				ref = personReferences.get(childMember.getPid());

				final boolean hasSpouses = (childMember.getSpouses() != null) && (childMember.getSpouses().length > 0);

				toTable = "";
				hasOwnTable = false;

				if (ref != null) {
					toTable = ref.getReferences(0, true, false, false, tableOffset);
					if (!toTable.isEmpty()) { // && ref.getMyTable() > 0) {
						if ((ref.getMyTable() > 0) || !hasSpouses) {
							hasOwnTable = true;
						}
						if (childMember.getMyTable() > 0) {

							toTable = "" + (childMember.getMyTable() + tableOffset);
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
				for (final Relation relation : cdata.relations) {
					if (paretag.equals(relation.getTag())) {
						bid = relation.getRelative();
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

				final StringBuilder adopTag = new StringBuilder();

				for (final Relation relation : cdata.relations) {
					if (ownerTag.equals(relation.getTag())) {
						if (tab.getPid() == relation.getRelative()) {
							if (relation.getNotices() != null) {
								for (int j = 0; j < relation.getNotices().length; j++) {
									final RelationNotice rn = relation.getNotices()[j];
									final String aux = rn.getTag();
									if ("ADOP".equals(aux)) {
										if (adopTag.length() > 0) {
											adopTag.append(", ");
										}
										final String tmp = rn.getType();
										if (tmp == null) {
											adopTag.append(typesTable.getTextValue(aux));
										} else {
											adopTag.append(rn.getType());
										}
									} else {
										if ("NOTE".equals(aux) && (rn.getNoteText() != null)) {
											if (adopTag.length() > 0) {
												adopTag.append(", ");
											}
											adopTag.append(rn.getNoteText());
										} else if ("SOUR".equals(aux) && (rn.getSource() != null)) {
											final String srcFormat = caller.getSourceFormat();
											if (!ReportWorkerDialog.SET_NO.equals(srcFormat)) {

												final String src = rn.getSource();

												final String srcText = addSource(false, srcFormat, src);
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
					printNotices(bt, cdata.persLong, (hasOwnTable ? 3 : 2), tab.getTableNo() + tableOffset);
				} else {
					printNameNn(bt);
				}
				if (!toTable.isEmpty()) {
					bt.addLink(typesTable.getTextValue("TABLE") + "\u00A0" + toTable + ". ", true, false, false,
							"" + toTable);

				} else {
					toTable = ref.getReferences(tab.getTableNo(), false, true, false, tableOffset);
					if (!toTable.isEmpty()) {
						bt.addLink(typesTable.getTextValue("ALSO") + "\u00A0" + toTable + ". ", true, false, false,
								toTable);
					}
				}
				if (bt.getCount() > 0) {
					repoWriter.addText(bt);
				}

				if (childMember.getSubCount() > 0) {

					final HashMap<String, String> submap = new HashMap<String, String>();

					for (int i = 0; i < childMember.getSubCount(); i++) {
						if (childMember.getSubPid(i) != tab.getPid()) {
							bt = new SubPersonText();
							bt.addText(childMember.getSubDadMom(i) + " ");
							final SukuData sub = caller.getKontroller().getSukuData("cmd=person",
									"pid=" + childMember.getSubPid(i));
							notices = sub.persLong.getNotices();

							if (sub.persLong.getPrivacy() == null) {

								printName(bt, sub.persLong, 4);
								printNotices(bt, sub.persLong, 4, tab.getTableNo() + tableOffset);
							} else {
								printNameNn(bt);
							}
							fromSubTable = "";
							ref = personReferences.get(childMember.getSubPid(i));
							if (ref != null) {
								final StringBuilder fromsTable = new StringBuilder();
								fromSubTable = ref.getReferences(tab.getTableNo(), true, true, true, tableOffset);
								if ((ref.getMyTable() > 0) && (tab.getTableNo() != ref.getMyTable())) {
									fromsTable.append(ref.getMyTable());
								} else {
									final String[] froms = fromSubTable.split(",");

									for (int j = 0; j < froms.length; j++) {

										final String mapx = submap.put(froms[j], froms[j]);
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
									bt.addLink(typesTable.getTextValue("ALSO") + " " + fromsTable.toString() + ". ",
											true, false, false, fromsTable.toString());
								}
							}

							repoWriter.addText(bt);
						}
					}
				}

				if (hasSpouses) {

					if ((childMember.getSpouses() != null) && (childMember.getSpouses().length > 0)) {

						for (int j = 0; j < childMember.getSpouses().length; j++) {
							childSpouseMember = childMember.getSpouses()[j];
							bt = new ChildSpouseText();
							int spouNum = 0;
							if (childMember.getSpouses().length > 1) {
								spouNum = j + 1;

							}

							printSpouse(tab.getTableNo(), childMember.getPid(), bt, childSpouseMember, spouNum,
									tableOffset);

						}
					}
				}

				if (bt.getCount() > 0) {
					repoWriter.addText(bt);

				}
			} catch (final SukuException e1) {
				logger.log(Level.WARNING, "SukuException", e1);
			}
		}
		if (famtNotices.length > 0) {
			bt = new MainPersonText();
			bt.addText("\n");
			repoWriter.addText(bt);
			bt = new MainPersonText();
			dummyPerson.setNotices(famtNotices);
			printNotices(bt, dummyPerson, 2, tab.getTableNo());
			repoWriter.addText(bt);
		}
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
