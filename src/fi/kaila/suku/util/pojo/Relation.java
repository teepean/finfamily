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
import java.sql.Timestamp;

/**
 * Relation table row as POJO object.
 *
 * @author Kalle
 */
public class Relation implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// select
	// a.rid,a.pid,b.pid,a.tag,rn.tag,rn.surety,rn.relationtype,rn.description,
	// rn.dateprefix,rn.fromdate,rn.todate,rn.notetext,rn.sourcetext,rn.privatetext,a.createdate,rn.modified,rn.createdate
	// from relation a inner join relation b on a.rid=b.rid left join
	// relationnotice rn on a.rid=rn.rid
	// where a.pid <> b.pid and a.pid=3 order by
	// b.pid,a.rid,a.relationrow,rn.noticerow

	private boolean toBeDeleted = false;
	private boolean toBeUpdated = false;

	/** The aid. */
	int aid = 0; // subject pid

	/** The bid. */
	int bid = 0; // relative pid

	/** The rid. */
	int rid = 0;

	/** The rtag. */
	String rtag = null; // subject tag

	/** The surety. */
	int surety = 100; // relation surety

	/** The modified. */
	Timestamp modified = null;

	/** The created. */
	Timestamp created = null;

	/** The modified by userid. */
	String modifiedBy = null;

	/** The created by userid. */
	String createdBy = null;

	/** The notices. */
	RelationNotice[] notices = null;
	//
	// used in RelaticesPane. Does not come from server
	//
	private PersonShortData pers = null;

	/**
	 * Instantiates a new relation.
	 *
	 * @param rid
	 *            the rid
	 * @param aid
	 *            the aid
	 * @param bid
	 *            the bid
	 * @param tag
	 *            the tag
	 * @param surety
	 *            the surety
	 * @param modified
	 *            the modified
	 * @param created
	 *            the created
	 * @param modifiedBy
	 *            the modified by
	 * @param createdBy
	 *            the created by
	 */
	public Relation(int rid, int aid, int bid, String tag, int surety, Timestamp modified, Timestamp created,
			String modifiedBy, String createdBy) {
		this.rid = rid;
		this.aid = aid;
		this.bid = bid;
		this.rtag = tag;
		this.surety = surety;
		this.modified = modified;
		this.created = created;
		this.modifiedBy = modifiedBy;
		this.createdBy = createdBy;
		if (rid == 0) {
			toBeUpdated = true;
		}
	}

	/**
	 * Sets the rid.
	 *
	 * @param rid
	 *            the new rid
	 */
	public void setRid(int rid) {
		if (this.rid == 0) {
			this.rid = rid;
		}
	}

	/**
	 * Gets the adopted.
	 *
	 * @return adopted status
	 */
	public String getAdopted() {
		if (notices != null) {
			for (final RelationNotice notice : notices) {
				if (notice.getTag().equals("ADOP")) {
					return "a";
				}
			}
		}
		return null;
	}

	/**
	 * Set value for relation surety.
	 *
	 * @param surety
	 *            the new surety
	 */
	public void setSurety(int surety) {
		this.surety = surety;
	}

	/**
	 * reset modified status.
	 */
	public void resetModified() {
		toBeUpdated = false;
	}

	/**
	 * Gets the notice.
	 *
	 * @param tag
	 *            the tag
	 * @return first notice of type tag
	 */
	public RelationNotice getNotice(String tag) {
		if (notices == null) {
			return null;
		}
		for (final RelationNotice notice : notices) {
			if (notice.getTag().equals(tag)) {
				return notice;
			}
		}
		return null;

	}

	/**
	 * Sets the notices.
	 *
	 * @param notices
	 *            an array of relationNotice objects for the relation
	 */
	public void setNotices(RelationNotice[] notices) {
		this.notices = notices;
	}

	/**
	 * Gets the notices.
	 *
	 * @return the array of relationnotices
	 */
	public RelationNotice[] getNotices() {
		return notices;
	}

	/**
	 * Sets the to be deleted.
	 *
	 * @param value
	 *            true if this is to be deleted
	 */
	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
		toBeUpdated = true;
	}

	/**
	 * Sets the to be updated.
	 *
	 * @param value
	 *            true if this is to be deleted
	 */
	public void setToBeUpdated(boolean value) {

		toBeUpdated = value;
	}

	/**
	 * Checks if is to be deleted.
	 *
	 * @return true if it is to be deleted
	 */
	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	/**
	 * Checks if is to be updated.
	 *
	 * @return true if is to be updated
	 */
	public boolean isToBeUpdated() {
		return toBeUpdated;
	}

	/**
	 * Gets the tag.
	 *
	 * @return tag
	 */
	public String getTag() {
		return rtag;
	}

	/**
	 * Gets the rid.
	 *
	 * @return rid
	 */
	public int getRid() {
		return rid;
	}

	/**
	 * Gets the relative.
	 *
	 * @return relative pid
	 */
	public int getRelative() {
		return bid;
	}

	/**
	 * Gets the pid.
	 *
	 * @return pid
	 */
	public int getPid() {
		return aid;
	}

	/**
	 * Sets the pid.
	 *
	 * @param pid
	 *            the new pid
	 */
	public void setPid(int pid) {
		if (this.aid != pid) {
			toBeUpdated = true;
		}
		this.aid = pid;
	}

	/**
	 * Sets the relative.
	 *
	 * @param pid
	 *            for relative
	 */
	public void setRelative(int pid) {
		if (this.bid != pid) {
			toBeUpdated = true;
		}
		this.bid = pid;
	}

	/**
	 * Gets the surety.
	 *
	 * @return (0,20,40,60,80,100)
	 */
	public int getSurety() {
		return surety;
	}

	/**
	 * Gets the created.
	 *
	 * @return created time
	 */
	public Timestamp getCreated() {
		return created;
	}

	/**
	 * Gets the created by.
	 *
	 * @return userid of creater
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Gets the modified.
	 *
	 * @return modifieud time
	 */
	public Timestamp getModified() {
		return modified;
	}

	/**
	 * Gets the modified by.
	 *
	 * @return userid of modifier
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * Sets the short person.
	 *
	 * @param pers
	 *            teh short person for the relation
	 */
	public void setShortPerson(PersonShortData pers) {

		this.pers = pers;
	}

	/**
	 * Gets the short person.
	 *
	 * @return get the short person of this relation
	 */
	public PersonShortData getShortPerson() {
		return pers;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "rel " + aid + "/" + bid + "/" + rtag;
	}

}
