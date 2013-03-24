package fi.kaila.suku.util;

import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;

/**
 * 
 * Auxiliary class used by ....
 * 
 * @author Markus Ritala
 * 
 */
public class ExportData extends PersonShortData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PersonLongData person = null;
	private Relation[] relations = null;

	/**
	 * Instantiates a new export data.
	 * 
	 * @param person
	 *            the person
	 * @param relations
	 *            the relations
	 */
	public ExportData(PersonLongData person, Relation[] relations) {
		super(person);
		this.person = person;
		this.relations = relations;
	}

	/* (non-Javadoc)
	 * @see fi.kaila.suku.util.pojo.PersonShortData#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Pid=" + getPid());
		sb.append(" ");
		sb.append(getAlfaName());
		if (relations != null) {
			sb.append("# of relations ");
			sb.append(relations.length);
		}
		return sb.toString();
	}

}
