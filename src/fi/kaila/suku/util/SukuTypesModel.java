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

package fi.kaila.suku.util;

import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class SukuTypesModel.
 *
 * @author Kalle
 *
 *         Model for Types table use.
 */
public class SukuTypesModel extends AbstractTableModel {

	private String[] typesTags = null;
	private String[] typesValues = null;
	private Object[][] typesData = null;
	private final HashMap<String, Integer> typeTexts = new HashMap<String, Integer>();
	private final HashMap<String, String> typeRule = new HashMap<String, String>();

	/**
	 * Instantiates a new suku types model.
	 */
	SukuTypesModel() {

		initTypes();

	}

	/**
	 * init the types data.
	 */
	public void initTypes() {
		try {
			final SukuData reposet = Suku.kontroller.getSukuData("cmd=get", "type=types",
					"lang=" + Resurses.getLanguage());

			typesValues = new String[reposet.vvTypes.size()];
			for (int i = 0; i < reposet.vvTypes.size(); i++) {
				final String rrr[] = reposet.vvTypes.get(i);
				final String tag = rrr[0];
				typeTexts.put(tag, i);

				typesValues[i] = rrr[1];
				final String rule = rrr[4];
				if (rule != null) {
					typeRule.put(tag, rule);
				}

			}

			typesData = new Object[reposet.vvTypes.size()][7];
			typesTags = new String[reposet.vvTypes.size()];

			for (int i = 0; i < typesTags.length; i++) {
				final String tag = reposet.vvTypes.get(i)[0];
				typesTags[i] = tag;
				typesData[i][0] = reposet.vvTypes.get(i)[1];
				typesData[i][1] = Boolean.FALSE;
				if ("|BIRT|DEAT|CHR|BURI|NAME|".indexOf(tag) > 0) {
					typesData[i][1] = Boolean.TRUE;
				}
				typesData[i][2] = Boolean.TRUE;
				typesData[i][3] = Boolean.FALSE;
				typesData[i][4] = Boolean.FALSE;
				if ("|BIRT|DEAT|OCCU|".indexOf(tag) > 0) {
					typesData[i][3] = Boolean.TRUE;
					typesData[i][4] = Boolean.TRUE;
				}
				typesData[i][5] = Boolean.FALSE;
				typesData[i][6] = reposet.vvTypes.get(i)[2];
				if (typesData[i][6] == null) {
					typesData[i][6] = reposet.vvTypes.get(i)[1];
				}
			}

		} catch (final SukuException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);

			e.printStackTrace();
		}
	}

	private static final long serialVersionUID = 1L;

	private final String[] columnNames = { Resurses.getString("REPORT.TAB.NOTICE"),
			Resurses.getString("REPORT.TAB.NAME"), Resurses.getString("REPORT.TAB.MAINPERSON"),
			Resurses.getString("REPORT.TAB.CHILD"), Resurses.getString("REPORT.TAB.OTHER"),
			Resurses.getString("REPORT.TAB.PLACES"), Resurses.getString("REPORT.TAB.TEXT") };

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return typesData.length;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int col) {
		return typesData[row][col];
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears on screen.
		if (col < 1) {
			return false;
		}
		return true;
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 * int, int)
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {

		typesData[row][col] = value;
		fireTableCellUpdated(row, col);

	}

	/**
	 * Gets the types tags.
	 *
	 * @param idx
	 *            the idx
	 * @return tag at idx
	 */
	public String getTypesTags(int idx) {
		return typesTags[idx];
	}

	/**
	 * Gets the types tags count.
	 *
	 * @return # of tags
	 */
	public int getTypesTagsCount() {
		return typesTags.length;
	}

	/**
	 * Gets the type text.
	 *
	 * @param tag
	 *            the tag
	 * @return the typeTexts
	 */
	public Integer getTypeText(String tag) {
		return typeTexts.get(tag);

	}

	/**
	 * Gets the types data.
	 *
	 * @param row
	 *            the row
	 * @param col
	 *            the col
	 * @return contents of cell
	 */
	public Object getTypesData(int row, int col) {
		if (row < typesData.length) {
			return typesData[row][col];
		}
		return null;
	}

	/**
	 * Gets the types name.
	 *
	 * @param row
	 *            the row
	 * @return name of tag
	 */
	public String getTypesName(int row) {
		if (row < typesData.length) {
			return (String) typesData[row][0];
		}
		return null;
	}

	/**
	 * Gets the types values.
	 *
	 * @return the typesValues
	 */
	public String[] getTypesValues() {
		return typesValues;
	}

	/**
	 * Gets the types tag.
	 *
	 * @param idx
	 *            the idx
	 * @return the tag
	 */
	public String getTypesTag(int idx) {
		return typesTags[idx];
	}

	/**
	 * Gets the types value.
	 *
	 * @param idx
	 *            the idx
	 * @return the text portion
	 */
	public String getTypesValue(int idx) {
		return typesValues[idx];
	}

	/**
	 * Gets the type rule.
	 *
	 * @param type
	 *            the type
	 * @return the typeRule
	 */
	public String getTypeRule(String type) {
		return typeRule.get(type);

	}

}
