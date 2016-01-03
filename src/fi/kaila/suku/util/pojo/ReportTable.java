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

/**
 * The Class ReportTable.
 *
 * @author Kalle
 * @deprecated
 */
@Deprecated
public class ReportTable {
	private int tid = 0;
	private int tableNo = 0;
	private int parentTableNo = 0;
	private int gen = 0;
	private int spouseCount = 0;
	private int childCount = 0;
	private String type = null;

	private ReportTableMember[] members = null;

	/**
	 * Sets the tid.
	 *
	 * @param tid
	 *            the tid to set
	 */
	public void setTid(int tid) {
		this.tid = tid;
	}

	/**
	 * Gets the tid.
	 *
	 * @return the tid
	 */
	public int getTid() {
		return tid;
	}

	/**
	 * Sets the table no.
	 *
	 * @param tableNo
	 *            the tableNo to set
	 */
	public void setTableNo(int tableNo) {
		this.tableNo = tableNo;
	}

	/**
	 * Gets the table no.
	 *
	 * @return the tableNo
	 */
	public int getTableNo() {
		return tableNo;
	}

	/**
	 * Sets the gen.
	 *
	 * @param gen
	 *            the gen to set
	 */
	public void setGen(int gen) {
		this.gen = gen;
	}

	/**
	 * Gets the gen.
	 *
	 * @return the gen
	 */
	public int getGen() {
		return gen;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the spouse count.
	 *
	 * @param spouseCount
	 *            the spouseCount to set
	 */
	public void setSpouseCount(int spouseCount) {
		this.spouseCount = spouseCount;
	}

	/**
	 * Gets the spouse count.
	 *
	 * @return the spouseCount
	 */
	public int getSpouseCount() {
		return spouseCount;
	}

	/**
	 * Sets the child count.
	 *
	 * @param childCount
	 *            the childCount to set
	 */
	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	/**
	 * Gets the child count.
	 *
	 * @return the childCount
	 */
	public int getChildCount() {
		return childCount;
	}

	/**
	 * Sets the parent table no.
	 *
	 * @param parentTableNo
	 *            the parentTableNo to set
	 */
	public void setParentTableNo(int parentTableNo) {
		this.parentTableNo = parentTableNo;
	}

	/**
	 * Gets the parent table no.
	 *
	 * @return the parentTableNo
	 */
	public int getParentTableNo() {
		return parentTableNo;
	}

	/**
	 * Sets the members.
	 *
	 * @param members
	 *            the members to set
	 */
	public void setMembers(ReportTableMember[] members) {
		this.members = members;
	}

	/**
	 * Gets the members.
	 *
	 * @return the members
	 */
	public ReportTableMember[] getMembers() {
		return members;
	}

}
