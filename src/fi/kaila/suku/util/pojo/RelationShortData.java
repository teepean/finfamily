package fi.kaila.suku.util.pojo;

import java.io.Serializable;

/**
 * @author FIKAAKAIL
 * 
 *         Short Family POJO
 */
public class RelationShortData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int pid = 0;
	int relationPid = 0;
	String tag = null;
	private String marrDate = null;
	private String divDate = null;
	private String adopted = null;
	int parentArray[] = null;
	int order = 0;
	int auxIndex = 0;
	int surety = 0;

	/**
	 * @param pid
	 * @param relationPid
	 * @param order
	 * @param tag
	 * @param surety
	 */
	public RelationShortData(int pid, int relationPid, int order, String tag,
			int surety) {
		this.pid = pid;
		this.relationPid = relationPid;
		this.tag = tag;
		this.order = order;
		this.surety = surety;
	}

	/**
	 * @param auxIndex
	 */
	public void setAux(int auxIndex) {
		this.auxIndex = auxIndex;
	}

	/**
	 * @return the auxnumber
	 */
	public int getAux() {
		return auxIndex;
	}

	/**
	 * @param rDate
	 */
	public void setMarrDate(String rDate) {
		this.marrDate = rDate;
	}

	/**
	 * @param divDate
	 */
	public void setDivDate(String divDate) {
		this.divDate = divDate;
	}

	/**
	 * @return pid of "owner"
	 */
	public int getPid() {
		return this.pid;
	}

	/**
	 * 
	 * @return surety of relation
	 */
	public int getSurety() {
		return surety;
	}

	/**
	 * @return pid of relative
	 */
	public int getRelationPid() {
		return this.relationPid;
	}

	/**
	 * @return tag
	 */
	public String getTag() {
		return this.tag;
	}

	/**
	 * @return order # in family
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * @return main date of relation
	 */
	public String getDate() {
		return this.marrDate;
	}

	/**
	 * @param parentids
	 */
	public void setParentArray(int[] parentids) {
		parentArray = parentids;
	}

	/**
	 * @return array of parent ids
	 */
	public int[] getParentArray() {
		return parentArray;
	}

	/**
	 * @return divorce date of relation
	 */
	public String getDivDate() {
		return this.divDate;
	}

	public void setAdopted(String adopted) {
		this.adopted = adopted;
	}

	public String getAdopted() {
		return adopted;
	}
}
