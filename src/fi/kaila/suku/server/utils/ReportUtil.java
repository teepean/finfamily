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

package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.report.PersonInTables;
import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.RelationShortData;
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Server side class for report creator.
 *
 * @author Kalle
 */
public class ReportUtil {

	private static Logger logger = Logger.getLogger(ReportUtil.class.getName());

	private Connection con = null;

	/** The runner. */
	ReportWorkerDialog runner = null;

	/**
	 * Constructor.
	 *
	 * @param con
	 *            connection instance to the PostgreSQL database
	 */
	public ReportUtil(Connection con) {
		this.con = con;
		this.runner = ReportWorkerDialog.getRunner();
	}

	private HashMap<Integer, ReportUnit> unitMap = new HashMap<Integer, ReportUnit>();

	//
	// collect here females for whom table must not be builot
	//
	private HashMap<Integer, ReportTableMember> females = null;

	private final HashMap<Integer, ReportTableMember> pidmap = new HashMap<Integer, ReportTableMember>();

	/** The person references. */
	HashMap<Integer, PersonInTables> personReferences = null;

	/** The regs1. */
	Vector<ReportUnit> regs1 = null;

	/** The regs2. */
	Vector<ReportUnit> regs2 = null;

	/**
	 * Create descendant table structure.
	 *
	 * @param pid
	 *            the pid
	 * @param generations
	 *            the generations
	 * @param spouGen
	 *            the spou gen
	 * @param chilGen
	 *            the chil gen
	 * @param order
	 *            the order
	 * @param adopted
	 *            the adopted
	 * @return result of process
	 * @throws SQLException
	 *             the sQL exception
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData createDescendantStructure(int pid, int generations, int spouGen, int chilGen, String order,
			boolean adopted) throws SQLException, SukuException {
		final SukuData fam = new SukuData();
		unitMap = new HashMap<Integer, ReportUnit>();

		females = null;
		if (order.equals(ReportWorkerDialog.SET_ORDER_FIRSTMALE)) {
			females = new HashMap<Integer, ReportTableMember>();
		}

		final ReportTableMember chi = new ReportTableMember();
		chi.setPid(pid);

		@SuppressWarnings("unused")
		final int lasttab = createDescendantTables(0, 0, chi, 1, generations, order, adopted, 0);

		if (females != null) {
			final int round = females.size();
			if (round > 0) {
				unitMap = new HashMap<Integer, ReportUnit>();
				createDescendantTables(0, 0, chi, 1, generations, ReportWorkerDialog.SET_ORDER_NEWMALE, adopted, round);
			}
		}

		logger.fine("Descendant repo");

		setRunnerValue(Resurses.getString("REPORT_DESC_COUNTING"));
		tableNo = 1;
		final int gen = 0;
		final ReportUnit unit = unitMap.get(pid);
		unit.setTableNo(tableNo);
		unit.setGen(gen);
		tables.add(unit);

		if (order.equals(ReportWorkerDialog.SET_ORDER_REG)) {
			regs1 = new Vector<ReportUnit>();

			regs1.add(unit);
			calculateRegistryTableNumbers(regs1, unitMap);
		} else {
			calculateDescendantTableNumbers(unit, unitMap);
		}

		// logUnits("laskettu taulut");

		setRunnerValue(Resurses.getString("REPORT_DESC_ALSO_IN"));

		personReferences = Utils.getDescendantToistot(tables);
		logUnits("laskettu toistot");

		// System.out.println("SPOU/CHIL = " + spouGen + "/" + chilGen);
		if (spouGen > 0) {
			addSpouseAncestors(spouGen);

		}
		if (chilGen > 0) {
			addChildAncestors(chilGen);
		}
		logUnits("laskettu muutkin");
		fam.tables = tables;

		fam.reportUnits = unitMap;
		return fam;

	}

	private void addChildAncestors(int gen) throws SQLException {

		for (int i = 0; i < tables.size(); i++) {
			final ReportUnit unit = tables.get(i);
			// String sex = null;
			String pareSex = null;
			final ReportTableMember famParent = unit.getParent().get(0);
			if (famParent == null) {
				return;
			}
			pareSex = famParent.getSex();
			for (int j = 0; j < unit.getChild().size(); j++) {

				final ReportTableMember member = unit.getChild().get(j);
				// if (j == 0) {
				// sex = member.getSex();
				// }
				// HashMap<Integer,PersonInTables> personReferences
				// long strado=2;
				// if (unit.getMember(0).getSex().equals("M")){
				// strado=3;
				// }

				addAncestorsToMember(unit, member, 1, gen, pareSex);
				// spouse not a relative

			}
		}

	}

	private void addSpouseAncestors(int gen) throws SQLException {
		String fromTable;
		for (int i = 0; i < tables.size(); i++) {
			final ReportUnit unit = tables.get(i);
			int startIdx = 1;
			if (unit.getGen() == 0) {
				startIdx = 0;
			}
			for (int j = startIdx; j < unit.getParent().size(); j++) {
				final ReportTableMember member = unit.getParent().get(j);
				// HashMap<Integer,PersonInTables> personReferences
				final PersonInTables ref = personReferences.get(member.getPid());
				fromTable = "";
				if (ref != null) {
					fromTable = ref.getReferences(unit.getTableNo(), true, true, false, 0);
				}
				if (fromTable.isEmpty()) {
					addAncestorsToMember(unit, member, 1, gen, null);
					// spouse not a relative
				}
			}
		}
		for (int i = 0; i < tables.size(); i++) {
			final ReportUnit unit = tables.get(i);
			for (int j = 0; j < unit.getChild().size(); j++) {
				final ReportTableMember member = unit.getChild().get(j);

				final ReportTableMember spouses[] = member.getSpouses();
				if (spouses != null) {
					for (final ReportTableMember spouse : spouses) {
						final PersonInTables ref = personReferences.get(spouse.getPid());
						fromTable = "";
						if (ref != null) {
							fromTable = ref.getReferences(unit.getTableNo(), true, true, false, 0);
						}
						if (fromTable.isEmpty()) {
							addAncestorsToMember(unit, spouse, 1, gen, null);
							// spouse not a relative
						}
					}
				}
			}
		}
	}

	private void addAncestorsToMember(ReportUnit unit, ReportTableMember member, long strado, int gen, String parentSex)
			throws SQLException {

		final int pid = member.getPid();

		addMemberParents(unit, member, pid, strado, parentSex, 0, gen);

		member.sortSubs();

	}

	private void addMemberParents(ReportUnit unit, ReportTableMember member, int pid, long strado, String parentSex,
			int gen, int maxGen) throws SQLException {
		if (gen >= maxGen) {
			return;
		}
		final String sql = "select p.aid,p.bid,p.tag "
				+ "from parent as p left join relationnotice as r on p.rid=r.rid " + "where aid=? and r.tag is null";

		int fatherPid = 0;
		int motherPid = 0;
		String tag;
		final PreparedStatement stm = con.prepareStatement(sql);
		stm.setInt(1, pid);

		final ResultSet rs = stm.executeQuery();

		while (rs.next()) {
			tag = rs.getString(3);
			if (tag.equals("FATH")) {
				fatherPid = rs.getInt(2);
			} else {
				motherPid = rs.getInt(2);
			}
		}
		rs.close();
		stm.close();

		if ((fatherPid == 0) && (motherPid == 0)) {
			return;
		}

		if (fatherPid > 0) {

			if (personReferences.get(fatherPid) == null) {
				member.addSub(fatherPid, "M", strado * 2);
				addMemberParents(unit, member, fatherPid, strado * 2, "M", gen + 1, maxGen);

			}
		}
		if (motherPid > 0) {

			if (personReferences.get(motherPid) == null) {
				member.addSub(motherPid, "F", (strado * 2) + 1);
				addMemberParents(unit, member, motherPid, (strado * 2) + 1, "F", gen + 1, maxGen);
			}
		}

	}

	/**
	 *
	 * @param parentTableNo
	 *            table # of the parent
	 * @param nexttab
	 *            table # of next table if none is created
	 * @param chi
	 *            child for whom table is top be created
	 * @param gen
	 *            current generation
	 * @param generations
	 *            max generations
	 * @param order
	 *            # from report settings 0 = normal order, 1 = men order, 2 =
	 *            women order 3 = men first order, 4 = register order
	 * @param adopted
	 *            true if also adopted children are to be included
	 * @return
	 * @throws SQLException
	 * @throws SukuException
	 */
	private int createDescendantTables(long parentTableNo, int nexttab, ReportTableMember chi, int gen, int generations,
			String order, boolean adopted, int round) throws SQLException, SukuException {

		final ReportUnit pidTable = unitMap.get(chi.getPid());

		if (pidTable != null) {
			return nexttab;
		}

		if (gen > generations) {
			return nexttab;
		}

		// create a table for the child
		final ReportUnit unit = createOneTable(nexttab, chi, gen, adopted, true, false);

		if ((nexttab == 0) && (unit.getChild().size() == 0)) {
			unitMap.put(unit.getPid(), unit);
			return 0;
		}

		//
		// if child has no children then copy spouses to child at parent
		//
		if (unit.getChild().size() == 0) {
			if (unit.getParent().size() > 1) {
				int idx = 0;
				final ReportTableMember[] spouses = new ReportTableMember[unit.getParent().size() - 1];
				for (int i = 0; i < unit.getParent().size(); i++) {
					if (unit.getPid() != unit.getParent().get(i).getPid()) {
						spouses[idx++] = unit.getParent().get(i);
					}
				}
				chi.setSpouses(spouses);
			}
			return nexttab; // and continue at nexttab

		}
		boolean childrenAlreadyListed = false;
		ReportUnit spoUnits = null;
		for (int i = 1; i < unit.getParent().size(); i++) {
			spoUnits = unitMap.get(unit.getParent().get(i).getPid());
			if (spoUnits != null) {
				for (int k = 0; k < unit.getChild().size(); k++) {
					final ReportTableMember ktm = unit.getChild().get(k);
					int j = 0;
					for (j = 0; j < spoUnits.getChild().size(); j++) {
						final ReportTableMember stm = spoUnits.getChild().get(j);
						if (stm.getPid() == ktm.getPid()) {
							break;
						}
					}
					if (j == spoUnits.getChild().size()) {
						childrenAlreadyListed = false;
						break;
					}
					childrenAlreadyListed = true;
				}

			} else {
				break;
			}

		}
		if (childrenAlreadyListed) {

			if (order.equals(ReportWorkerDialog.SET_ORDER_FIRSTMALE) && chi.getSex().equals("M")) {
				final ReportTableMember rtm = spoUnits.getParent().get(0);
				females.put(spoUnits.getPid(), rtm);

			}

			return nexttab;
		}
		//
		// check possible table of spouses
		//
		for (int i = 0; i < unit.getParent().size(); i++) {

			final int parePid = unit.getParent().get(i).getPid();

			final ReportUnit pareUnits = unitMap.get(parePid);
			// .get(unit.getParent().get(i).getPid());

			//
			// got table of spouse (if such exists)
			//
			if (pareUnits != null) {
				final int jsize = pareUnits.getChild().size();
				final int ksize = unit.getChild().size();

				//
				// check that all children are part of other parents unit
				//
				if (ksize > jsize) {
					int j = 0;
					for (j = 0; j < jsize; j++) {
						final int nxtSpouseChildPid = pareUnits.getChild().get(j).getPid();
						int k = 0;
						for (k = 0; k < ksize; k++) {
							final int nxtMyChildPid = pareUnits.getChild().get(k).getPid();
							if (nxtMyChildPid == nxtSpouseChildPid) {
								break; // this says child found
							}
						}
						if (k == ksize) {
							break; // here when child not found
						}
					}
					if (j == jsize) { // all children found from other spouse

						// halonmi 20130311 >>>
						j = 0;
						for (j = 0; (j < ksize) && (j < jsize); j++) {
							if (unit.getChild().get(j).getPid() != pareUnits.getChild().get(j).getPid()) {
								break;
							}
						}
						if (j < ksize) {
							break;
						}
						// halonmi 20130311 <<<
						if (order.equals(ReportWorkerDialog.SET_ORDER_FIRSTMALE) && chi.getSex().equals("M")) {
							// FIRSTMALE report doesn't worked correctly.
							// halonmi 20130309
							females.put(unit.getPid(), chi);
							// females.put(pareUnits.getPid(),pareUnits.getMember(0));
						}
						//
						// if spouse has table then if spouse and subject
						// childlist
						// match
						// then spouse does not get own table
						//
						return nexttab;
					}
				}
			}
		}

		if ((females != null) && ReportWorkerDialog.SET_ORDER_NEWMALE.equals(order)
				&& (females.get(chi.getPid()) != null)) {
			return nexttab;
		}

		int tableNo = nexttab + 1;
		if (round > 0) {
			setRunnerValue(Resurses.getString("REPORT_TABSTRUCT_INITMALE") + " [" + round + "]: " + tableNo);
		} else {
			setRunnerValue(Resurses.getString("REPORT_TABSTRUCT_INIT") + " " + tableNo);
		}
		unitMap.put(unit.getPid(), unit);

		ReportTableMember mymem = new ReportTableMember();
		mymem.setPid(unit.getPid());
		pidmap.put(unit.getPid(), mymem);

		for (int i = 0; i < unit.getMemberCount(); i++) {
			mymem = unit.getMember(i);
			pidmap.put(mymem.getPid(), mymem);
		}

		int nxttab = 0;
		for (int rowno = 0; rowno < unit.getChild().size(); rowno++) {
			final ReportTableMember chix = unit.getChild().get(rowno);

			if (order.equals(ReportWorkerDialog.SET_ORDER_TAB) || order.equals(ReportWorkerDialog.SET_ORDER_FIRSTMALE)
					|| order.equals(ReportWorkerDialog.SET_ORDER_NEWMALE)
					|| order.equals(ReportWorkerDialog.SET_ORDER_REG)
					|| (order.equals(ReportWorkerDialog.SET_ORDER_MALE) && "M".equals(chix.getSex()))
					|| (order.equals(ReportWorkerDialog.SET_ORDER_FEMALE) && "F".equals(chix.getSex()))) {

				nxttab = createDescendantTables(tableNo, tableNo, chix, gen + 1, generations, order, adopted, round);
				if (nxttab > tableNo) {
					tableNo = nxttab;
				}
			}
		}

		return tableNo;

	}

	private int createAncestorTables(int nexttab, ReportTableMember chi, int gen, int generations, boolean family,
			String order) throws SQLException {

		final ReportUnit pidTable = unitMap.get(chi.getPid());

		if (pidTable != null) {
			return nexttab;
		}

		if (gen > generations) {
			return nexttab;
		}
		// create a table for the child
		final ReportUnit unit = createOneTable(nexttab, chi, gen, true, family, true);

		unitMap.put(unit.getPid(), unit);

		if (unit.getFatherPid() > 0) {
			final ReportTableMember chif = new ReportTableMember();
			chif.setPid(unit.getFatherPid());
			createAncestorTables(nexttab * 2, chif, gen + 1, generations, family, order);
		}

		if (unit.getMotherPid() > 0) {
			final ReportTableMember chim = new ReportTableMember();
			chim.setPid(unit.getMotherPid());
			createAncestorTables((nexttab * 2) + 1, chim, gen + 1, generations, family, order);
		}

		return 0;

	}

	private ReportUnit createOneTable(int tabno, ReportTableMember chi, int gen, boolean adopted, boolean includeFamily,
			boolean includeParents) throws SQLException {

		String sql;

		PreparedStatement stm;
		final int pid = chi.getPid();

		ResultSet rs;

		String sex = null;
		sql = "select sex from unit where pid = ?";

		stm = con.prepareStatement(sql);
		stm.setInt(1, pid);

		rs = stm.executeQuery();
		while (rs.next()) {
			sex = rs.getString("sex");
		}
		rs.close();
		stm.close();

		final ReportUnit unit = new ReportUnit();
		unit.setPid(pid);

		ReportTableMember member = new ReportTableMember();

		member.setPid(pid);
		member.setRowNo(0);
		member.setSex(sex);
		unit.addParent(member);

		if (includeParents) {
			sql = "select bid,p.tag,r.tag from parent as p left join relationnotice as r on p.rid=r.rid where aid=?";
			stm = con.prepareStatement(sql);
			stm.setInt(1, pid);

			rs = stm.executeQuery();
			while (rs.next()) {
				final int parep = rs.getInt(1);
				final String pare = rs.getString(2);
				final String adop = rs.getString(3);
				if (adop == null) {
					if ("FATH".equals(pare)) {
						unit.setFatherPid(parep);
					} else {
						unit.setMotherPid(parep);
					}
				}
			}
			rs.close();
			stm.close();
		}

		if (!includeFamily) {
			return unit;
		}
		sql = "select bid,s.tag,u.sex " + "from spouse as s inner join unit as u on s.bid=u.pid "
				+ "where s.aid=? order by relationrow";

		stm = con.prepareStatement(sql);
		stm.setInt(1, pid);
		int spousenum = 0;
		rs = stm.executeQuery();
		while (rs.next()) {
			final int bid = rs.getInt("bid");
			final String stag = rs.getString("tag");
			final String ssex = rs.getString("sex");
			spousenum++;

			member = new ReportTableMember();

			member.setPid(bid);
			member.setRowNo(spousenum);
			member.setSex(ssex);
			member.setTag(stag);
			unit.addParent(member);

		}
		rs.close();
		stm.close();

		int childno = 0;
		final PreparedStatement adopstm = con
				.prepareStatement("select count(*) from relationnotice where tag='ADOP' and rid = ?");

		final PreparedStatement pareStm = con.prepareStatement("select * from parent where aid=? and bid <> ? ");

		// String adoptext = "";
		// if (!adopted) {
		// adoptext = "and r.tag is null";
		// }

		sql = "select c.bid,u.sex,c.rid " + "from (child as c inner join unit as u on c.bid=u.pid ) "
				+ "where aid=? order by relationrow";

		stm = con.prepareStatement(sql);
		stm.setInt(1, pid);

		rs = stm.executeQuery();
		// TODO
		while (rs.next()) {
			final int bid = rs.getInt("bid");
			final String csex = rs.getString("sex");

			final int rid = rs.getInt(3);
			final boolean isAdopted = isChildRelationAdopted(adopstm, rid);
			final int otherParentPid = getParentPid(pareStm, bid, pid);
			member = new ReportTableMember();
			member.setPid(bid);
			member.setSex(csex);
			member.setRowNo(spousenum + childno);
			member.setOtherParentPid(otherParentPid);
			member.setTag("CHIL");
			if (isAdopted) {
				member.setRelTag("ADOP");
			}
			unit.addChild(member);
			childno++;

		}

		rs.close();
		stm.close();

		return unit;

	}

	private int getParentPid(PreparedStatement pst, int bid, int pid) throws SQLException {
		pst.setInt(1, bid);
		pst.setInt(2, pid);
		final ResultSet rs = pst.executeQuery();
		int parePid = 0;
		while (rs.next()) {
			parePid = rs.getInt("bid");
			break;
		}
		rs.close();
		return parePid;
	}

	private boolean isChildRelationAdopted(PreparedStatement pst, int rid) throws SQLException {

		pst.setInt(1, rid);

		int counter = 0;
		final ResultSet rs = pst.executeQuery();
		while (rs.next()) {
			counter = rs.getInt(1);

		}
		rs.close();
		return counter > 0;

	}

	private int tableNo;

	/** The tables. */
	Vector<ReportUnit> tables = new Vector<ReportUnit>();

	private void calculateDescendantTableNumbers(ReportUnit tab, HashMap<Integer, ReportUnit> unitMap) {

		for (int i = 0; i < tab.getChild().size(); i++) {
			final ReportTableMember asChi = tab.getChild().get(i);

			if (asChi.getMyTable() == 0) {
				final ReportUnit asOwner = unitMap.get(asChi.getPid());
				if (asOwner != null) {
					if (asOwner.getTableNo() == 0) {

						tableNo++;
						asOwner.setTableNo(tableNo);
						asOwner.setGen(tab.getGen() + 1);
						asChi.setMyTable(tableNo);
						tables.add(asOwner);
						calculateDescendantTableNumbers(asOwner, unitMap);
					}
					asOwner.setParentTable(asChi.getMyTable());
				}
			} else {
				asChi.addAsChild(tab.getTableNo());
			}
		}

	}

	private void calculateRegistryTableNumbers(Vector<ReportUnit> regs, HashMap<Integer, ReportUnit> unitMap) {
		regs2 = new Vector<ReportUnit>();

		for (int j = 0; j < regs.size(); j++) {
			final ReportUnit tab = regs.get(j);
			// if (j == 0) {
			// System.out.println("generation for " + tab.getPid() + "("
			// + regs.size() + ") = " + tab.getGen());
			// }

			for (int i = 0; i < tab.getChild().size(); i++) {
				final ReportTableMember asChi = tab.getChild().get(i);

				if (asChi.getMyTable() == 0) {
					final ReportUnit asOwner = unitMap.get(asChi.getPid());
					if (asOwner != null) {
						if (asOwner.getTableNo() == 0) {

							tableNo++;
							asOwner.setTableNo(tableNo);
							asOwner.setGen(tab.getGen() + 1);
							asChi.setMyTable(tableNo);
							tables.add(asOwner);
							regs2.add(asOwner);
						}
						asOwner.setParentTable(asChi.getMyTable());
					}
				} else {
					asChi.addAsChild(tab.getTableNo());
				}
			}

		}
		if (regs2.size() > 0) {
			regs1 = regs2;
			calculateRegistryTableNumbers(regs1, unitMap);
		}
	}

	private void logUnits(String text) {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer(text);
			for (int i = 0; i < tables.size(); i++) {
				final ReportUnit tab = tables.get(i);
				logger.finer(tab.toString());
			}
		}
	}

	/**
	 * Create data for the descendant list (Excel report).
	 *
	 * @param pid
	 *            the pid
	 * @return result in Sukudata pers = list of persons, generalArray 0 tag,
	 *         pidarray = generation of corresponding persons
	 * @throws SukuException
	 *             the suku exception
	 * @throws SQLException
	 *             the sQL exception
	 */
	public SukuData createDescendantLista(int pid) throws SukuException, SQLException {
		descListaPersons = new Vector<PersonShortData>();
		descListaText = new Vector<String>();
		descListaGen = new Vector<Integer>();
		multicheck = new HashMap<Integer, PersonShortData>();
		// descListaRelations= new Vector<RelationShortData> ();
		final PersonShortData p = new PersonShortData(this.con, pid, true);
		multicheck.put(p.getPid(), p);
		descListaPersons.add(p);
		descListaGen.add(0);
		descListaText.add("SUBJ");
		descListaCounter = 0;
		insertIntoDescendantLista(pid, 0);
		final SukuData ddd = new SukuData();

		ddd.pers = descListaPersons.toArray(new PersonShortData[0]);
		ddd.generalArray = descListaText.toArray(new String[0]);
		ddd.pidArray = new int[descListaGen.size()];
		for (int i = 0; i < ddd.pidArray.length; i++) {
			ddd.pidArray[i] = descListaGen.get(i);
		}
		return ddd;

	}

	/** The desc lista persons. */
	Vector<PersonShortData> descListaPersons = null;

	/** The desc lista text. */
	Vector<String> descListaText = null;

	/** The desc lista gen. */
	Vector<Integer> descListaGen = null;

	/** The desc lista counter. */
	int descListaCounter = 0;

	/** The multicheck. */
	HashMap<Integer, PersonShortData> multicheck = null;

	private void insertIntoDescendantLista(int pid, int gen) throws SQLException, SukuException {
		final ArrayList<RelationShortData> rr = new ArrayList<RelationShortData>();
		descListaCounter++;
		setRunnerValue(Resurses.getString("REPORT.LISTA.DESCLISTA") + " [" + descListaCounter + "/" + gen + "] ");
		String sql;
		PreparedStatement stm;
		ResultSet rs;

		sql = "select bid,relationrow,tag,surety from spouse_all where aid=? order by relationrow";
		stm = con.prepareStatement(sql);
		stm.setInt(1, pid);
		rs = stm.executeQuery();
		while (rs.next()) {
			final RelationShortData rel = new RelationShortData(pid, rs.getInt(1), rs.getInt(2), rs.getString(3),
					rs.getInt(4));
			rr.add(rel);
		}
		rs.close();
		stm.close();

		sql = "select bid,relationrow,tag,surety from child_all where aid=? order by relationrow";
		stm = con.prepareStatement(sql);
		stm.setInt(1, pid);
		rs = stm.executeQuery();
		while (rs.next()) {
			final RelationShortData rel = new RelationShortData(pid, rs.getInt(1), rs.getInt(2), rs.getString(3),
					rs.getInt(4));
			rr.add(rel);
		}
		stm.close();

		for (int i = 0; i < rr.size(); i++) {
			final RelationShortData rel = rr.get(i);

			final PersonShortData p = new PersonShortData(this.con, rel.getRelationPid(), true);
			descListaPersons.add(p);
			int mygen = gen;
			if (rel.getTag().equals("CHIL")) {
				mygen++;
			}
			descListaGen.add(mygen);
			descListaText.add(rel.getTag());
			if (rel.getTag().equals("CHIL")) {
				if (multicheck.put(p.getPid(), p) == null) {
					insertIntoDescendantLista(p.getPid(), gen + 1);
				}
			}
		}

	}

	/**
	 * Creates the ancestor structure.
	 *
	 * @param pid
	 *            the pid
	 * @param generations
	 *            the generations
	 * @param family
	 *            the family
	 * @param order
	 *            the order
	 * @return ancestor structure
	 * @throws SQLException
	 *             the sQL exception
	 */
	public SukuData createAncestorStructure(int pid, int generations, boolean family, String order)
			throws SQLException {
		final SukuData fam = new SukuData();
		unitMap = new HashMap<Integer, ReportUnit>();

		final ReportTableMember chi = new ReportTableMember();
		chi.setPid(pid);

		createAncestorTables(1, chi, 1, generations, family, order);
		Vector<ReportUnit> curvec = new Vector<ReportUnit>();
		Vector<ReportUnit> nxtvec = null;
		if (order.equals(ReportWorkerDialog.SET_ANC_ESPOLIN)) {

			final ReportUnit cu = unitMap.get(pid);
			cu.setTableNo(0);
			cu.setGen(0);
			curvec.add(cu);
			long epsotab = 0;
			while (curvec.size() > 0) {
				nxtvec = new Vector<ReportUnit>();
				for (int i = 0; i < curvec.size(); i++) {
					final ReportUnit cux = curvec.get(i);
					if (cux.getTableNo() == 0) {
						if ((cux.getFatherPid() == 0) && (cux.getMotherPid() == 0)) {
							continue;
						}
						epsotab++;
						cux.setTableNo(epsotab);
						tables.add(cux);

						ReportUnit cuxx = cux;
						ReportUnit moxx;
						while (cuxx.getFatherPid() > 0) {
							// int prepid=cuxx.getPid();
							final int pregen = cuxx.getGen();
							final int mopid = cuxx.getMotherPid();
							final int fapid = cuxx.getFatherPid();
							cuxx = unitMap.get(fapid);
							if (cuxx.getTableNo() == 0) {
								cuxx.setTableNo(cux.getTableNo());
								cuxx.setGen(pregen + 1);
								cuxx.setTableNo(epsotab);
								tables.add(cuxx);
							}
							if (mopid > 0) {
								moxx = unitMap.get(mopid);
								moxx.setGen(pregen + 1);
								nxtvec.add(moxx);
							}
						}
					}
				}
				curvec = nxtvec;
			}

		} else {

			final ReportUnit cu = unitMap.get(pid);
			cu.setTableNo(1);
			cu.setGen(0);
			curvec.add(cu);

			while (curvec.size() > 0) {
				nxtvec = new Vector<ReportUnit>();
				for (int i = 0; i < curvec.size(); i++) {

					final ReportUnit cux = curvec.get(i);
					if (cux != null) {
						tables.add(cux);
						final int fid = cux.getFatherPid();
						if (fid > 0) {
							final ReportUnit cuf = unitMap.get(fid);
							if ((cuf != null) && (cuf.getTableNo() == 0)) {
								cuf.setTableNo(cux.getTableNo() * 2);
								cuf.setGen(cux.getGen() + 1);
								// tables.add(cuf);
								nxtvec.add(cuf);
							}

						}

						final int mid = cux.getMotherPid();
						if (mid > 0) {
							final ReportUnit cum = unitMap.get(mid);
							if ((cum != null) && (cum.getTableNo() == 0)) {
								cum.setTableNo((cux.getTableNo() * 2) + 1);
								cum.setGen(cux.getGen() + 1);
								// tables.add(cum);
								nxtvec.add(cum);
							}

						}
					}
				}
				curvec = nxtvec;

			}

		}

		fam.reportUnits = unitMap;
		fam.tables = tables;
		return fam;
	}

	private void setRunnerValue(String juttu) throws SukuException {
		if (runner != null) {
			this.runner.setRunnerValue(juttu);
		}
	}
}
