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

package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.util.Vector;

/**
 * ReportUnit contains whole structure of one table.
 *
 * @author Kaarle Kaila
 */
public class ReportUnit implements Serializable {
	/**
	 * Container for a report table structure
	 */
	private static final long serialVersionUID = 1L;

	private int pid = 0;

	private int fatherPid = 0;
	private int motherPid = 0;

	private long tableNo = 0;
	private int pageNo = 0;
	private int gen = 0;

	private final Vector<ReportTableMember> asParent = new Vector<ReportTableMember>();
	private final Vector<ReportTableMember> asChild = new Vector<ReportTableMember>();

	/**
	 * Gets the parent.
	 *
	 * @return Vector of parents
	 */
	public Vector<ReportTableMember> getParent() {
		return asParent;
	}

	/**
	 * Gets the child.
	 *
	 * @return Vector of children
	 */
	public Vector<ReportTableMember> getChild() {
		return asChild;
	}

	/**
	 * Gets the member count.
	 *
	 * @return count of members in table
	 */
	public int getMemberCount() {
		return asParent.size() + asChild.size();
	}

	/**
	 * Gets the member.
	 *
	 * @param idx
	 *            the idx
	 * @return member at idx
	 */
	public ReportTableMember getMember(int idx) {
		ReportTableMember mem = null;
		final int pares = asParent.size();
		if (idx < pares) {
			mem = asParent.get(idx);
		} else if ((idx - pares) < asChild.size()) {
			mem = asChild.get(idx - pares);
		}
		return mem;

	}

	/**
	 * Adds the parent.
	 *
	 * @param pare
	 *            at end
	 */
	public void addParent(ReportTableMember pare) {
		for (int i = 0; i < asParent.size(); i++) {
			if (pare.getPid() == asParent.get(i).getPid()) {
				return;
			}
		}
		asParent.add(pare);
	}

	/**
	 * Adds the child.
	 *
	 * @param chil
	 *            at end
	 */
	public void addChild(ReportTableMember chil) {
		for (int i = 0; i < asChild.size(); i++) {
			if (chil.getPid() == asChild.get(i).getPid()) {
				return;
			}
		}
		asChild.add(chil);
	}

	/**
	 * Sets the pid.
	 *
	 * @param pid
	 *            the new pid
	 */
	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * Gets the pid.
	 *
	 * @return pid
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * Sets the table no.
	 *
	 * @param tableNo
	 *            the new table no
	 */
	public void setTableNo(long tableNo) {
		this.tableNo = tableNo;
	}

	/**
	 * Gets the table no.
	 *
	 * @return tableno
	 */
	public long getTableNo() {
		return tableNo;
	}

	/**
	 * Sets the gen.
	 *
	 * @param gen
	 *            = generation
	 */
	public void setGen(int gen) {
		this.gen = gen;
	}

	/**
	 * Gets the gen.
	 *
	 * @return generation
	 */
	public int getGen() {
		return gen;
	}

	/**
	 * Sets the parent table.
	 *
	 * @param parentTab
	 *            tableno of parent table
	 */
	public void setParentTable(long parentTab) {
		// parentTable = parentTab;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		final StringBuilder sb = new StringBuilder();
		sb.append("pid=" + pid + ",tab=" + tableNo + ",gen=" + gen);

		if (fatherPid > 0) {
			sb.append(",father=" + fatherPid);
		}
		if (motherPid > 0) {
			sb.append(",mother=" + motherPid);
		}
		if (asParent.size() > 0) {
			for (int i = 0; i < asParent.size(); i++) {
				final ReportTableMember rm = asParent.get(i);
				if (i == 0) {
					sb.append("\n  asparent=" + rm);
				} else {
					sb.append("," + rm);
				}
			}
		}

		if (asChild.size() > 0) {
			for (int i = 0; i < asChild.size(); i++) {
				final ReportTableMember rm = asChild.get(i);
				if (i == 0) {
					sb.append("\n  aschild=" + rm);
				} else {
					sb.append("," + rm);
				}
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * Sets the father pid.
	 *
	 * @param fatherPid
	 *            the fatherPid to set
	 */
	public void setFatherPid(int fatherPid) {
		this.fatherPid = fatherPid;
	}

	/**
	 * Gets the father pid.
	 *
	 * @return the fatherPid
	 */
	public int getFatherPid() {
		return fatherPid;
	}

	/**
	 * Sets the mother pid.
	 *
	 * @param motherPid
	 *            the motherPid to set
	 */
	public void setMotherPid(int motherPid) {
		this.motherPid = motherPid;
	}

	/**
	 * Gets the mother pid.
	 *
	 * @return the motherPid
	 */
	public int getMotherPid() {
		return motherPid;
	}

	/**
	 * Sets the page no.
	 *
	 * @param pageNo
	 *            the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		if (this.pageNo == 0) {
			this.pageNo = pageNo;
		}
	}

	/**
	 * Gets the page no.
	 *
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}

}
