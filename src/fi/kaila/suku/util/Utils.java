package fi.kaila.suku.util;

import java.util.HashMap;
import java.util.Vector;

import fi.kaila.suku.kontroller.SukuKontroller;
import fi.kaila.suku.report.PersonInTables;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;

/**
 * general static utilities
 * 
 * @author FIKAAKAIL
 *
 */
public class Utils {

	
	public enum PersonSource { DATABASE, PARENT, SPOUSE, CHILD }
	
	/**
	 * Get boolean preference from local repository
	 * @param o
	 * @param key
	 * @param def
	 * @return true or false
	 */
	public static boolean getBooleanPref(Object o,String key,boolean def){
		String resu;
		String sdef = "false";
		if (def) {
			sdef="true";
		}
		
		SukuKontroller kontroller = Suku.getKontroller();
		
		resu = kontroller.getPref(o, key, sdef);
		
		if (resu == null) {
			return def;
		}
		
		if (resu.equals("true")){
			return true;
		}
		return false;
		
	}

	/**
	 * Put boolean preference into local repository
	 * 
	 * @param o
	 * @param key
	 * @param value
	 */
	public static void putBooleanPref(Object o,String key,boolean value) {
		String svalue="false";
		if (value) svalue="true";
		SukuKontroller kontroller = Suku.getKontroller();

		kontroller.putPref(o, key, svalue);
	}
	
	/**
	 * convert dbdate date to viewable textformat
	 * 
	 * @param dbDate
	 * @param trimDate
	 * @return in text format
	 */
	public static String textDate(String dbDate,boolean trimDate){
		String df = Resurses.getDateFormat();
		if (dbDate == null) return null;
		if (dbDate.length() == 4){
			return dbDate;
		}
//		int y=0;
		int m=0;
		int d = 0;
		String yy = null;
		String mm = null;
		String dd = null;
		if (dbDate.length()==6) {
			yy = dbDate.substring(0,4);
			mm = dbDate.substring(4);
		} else if (dbDate.length()==8){
			yy = dbDate.substring(0,4);
			mm = dbDate.substring(4,6);
			dd = dbDate.substring(6);
		}
		try {
//			y = Integer.parseInt(yy);
			m = Integer.parseInt(mm);
			if (dd != null) {
				d = Integer.parseInt(dd);
			}
			if (!df.equals("SE") && trimDate){
				mm = "" + m;
				if (dd != null){
					dd = "" + d;
				}
			}
		} catch (NumberFormatException ne) {
			
		}
		
		if (dbDate.length() == 6){
			
			if (df.equals("SE")){
				return yy + "-" + mm;
			}
			else if (df.equals("FI")){
				return  mm +"." + yy;
			} else {
				return mm + "/" + yy;
			}  
		} else if (dbDate.length()==8) {
			if (df.equals("SE")){
				return yy + "-" + mm + "-" + dd;
			}
			else if (df.equals("FI")){
				return dd + "." + mm + "." + yy;
			} else if (df.equals("GB")){
				return dd + "/" + mm + "/" + yy;
			}  else {
				return mm + "/" + dd + "/" + yy;
			}
			
		}
		return dbDate;
	}
	
	/**
	 * convert viewable textdate to dbformat
	 * @param textDate
	 * @return date in dbformat
	 * @throws SukuDateException if bad dateformat 
	 */
	public static String dbDate(String textDate) throws SukuDateException {
		if (textDate == null || textDate.equals("")) return null;
		String df = Resurses.getDateFormat();
//		String separator = "\\.";
//		if (df.equals("SE")){
//			separator = "-";
//		} else if (df.equals("GB") || df.equals("US")) {
//			separator = "/";		
//		}
		
//		String parts[] = textDate.split(separator);
		String parts[] = textDate.split("\\.|/|-");
		
		for (int i = 0; i < parts.length; i++) {
			try {
				@SuppressWarnings("unused")
				int j = Integer.parseInt(parts[i]);
			} catch (NumberFormatException ne) {
				throw new SukuDateException(textDate);
			}
		}
		
		
		if (parts.length == 1){
			return strl(parts[0],4);
		}
		
		if (parts.length == 2){
			if (df.equals("SE")){
				return strl(parts[0],4) + strl(parts[1],2);
			} else {
				return strl(parts[1],2) + strl(parts[0],4);
			}
		}
		if (parts.length == 3){
			if (df.equals("SE")){
				return strl(parts[0],4) + strl(parts[1],2)+strl(parts[2],2);
			} else if (df.equals("US")){
				return strl(parts[2],4) + strl(parts[0],2)+strl(parts[1],2);				
			}
			return strl(parts[2],4) + strl(parts[1],2)+strl(parts[0],2);

		}
		throw new SukuDateException(textDate);
		
		
	}
	
	private static String strl(String text,int len) {
		if (text == null) return null;
		if (text.length() == len){
			return text;
		}
		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < len; j++){
			sb.append("0");	
		}
		sb.append(text);
		
		return sb.toString().substring(sb.length()-len);
		
		
		
	}
	
	/**
	 * 
	 * Create from report tables a HasMap of persons in the report
	 * 
	 * @param tables Vector that contains all tables for report
	 * @return  a HashMap containing a list of all persons in report 
	 * 			with information on tables where they exis
	 */
	public static HashMap<Integer,PersonInTables>  getDescendantToistot(Vector<ReportUnit> tables) {
		HashMap<Integer,PersonInTables> personReferences = new HashMap<Integer, PersonInTables>();

		for (int i = 0; i < tables.size(); i++) {
			ReportUnit tab = tables.get(i);
			PersonInTables ref;
			ReportTableMember member;
			ReportTableMember subMember;
			
	
			for (int j = 0; j < tab.getChild().size(); j++) {
				member = tab.getChild().get(j);
				ref = personReferences.get(member.getPid());
				if (ref == null) {
					ref = new PersonInTables(member.getPid());

					if (tab.getChild().size()>0){
						ref.asChildren.add(new Long(tab.getTableNo()));
					} 
					personReferences.put(new Integer(member.getPid()), ref);
				} else {
					if (tab.getChild().size()>0){
						ref.asChildren.add(new Long(tab.getTableNo()));
					} 
				}

				if (member.getSpouses() != null) {
					ReportTableMember []subMembers= member.getSpouses();
					for (int k = 0; k < subMembers.length; k++) {
						subMember = subMembers[k];
						ref = personReferences.get(subMember.getPid());
						if (ref == null) {
							ref = new PersonInTables(subMember.getPid());
							ref.references.add(new Long(tab.getTableNo()));
							personReferences.put(new Integer(subMember.getPid()), ref);
						} else {
							ref.references.add(tab.getTableNo());
						}
					}
				}



			}
			for (int j = 0; j < tab.getParent().size(); j++) {
				member = tab.getParent().get(j);
				ref = personReferences.get(member.getPid());
				if (ref == null) {
					ref = new PersonInTables(member.getPid());

					if (tab.getChild().size()>0){
						ref.asParents.add(new Long(tab.getTableNo()));
					} 
					personReferences.put(new Integer(member.getPid()), ref);
				} else {
					if (tab.getChild().size()>0){
						ref.asParents.add(new Long(tab.getTableNo()));
					} 
				}
			}
		}
		return personReferences;
	}
	
	
	
}
