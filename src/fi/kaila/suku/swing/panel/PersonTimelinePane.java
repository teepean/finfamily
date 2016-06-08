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

package fi.kaila.suku.swing.panel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import fi.kaila.suku.swing.doc.SukuDocument;
import fi.kaila.suku.swing.text.DocumentSukuFilter;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.TimelineData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * database draft text is shown here.
 *
 * @author halonmi
 */
public class PersonTimelinePane extends JTextPane {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private AbstractDocument doc;
	private int currentPid = 0;
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * default constructor.
	 */
	public PersonTimelinePane() {

		doc = new SukuDocument();
		setDocument(doc);
		final StyledDocument styledDoc = getStyledDocument();

		if (styledDoc instanceof AbstractDocument) {
			doc = (AbstractDocument) styledDoc;
			doc.setDocumentFilter(new DocumentSukuFilter());
		} else {
			JOptionPane.showMessageDialog(this, "FAILED TO START DOCUMENT", Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
			logger.log(Level.WARNING, "CLOSE");
			return;

		}
	}

	/**
	 * Gets the doc.
	 *
	 * @return the doc
	 */
	public AbstractDocument getTimelineDoc() {
		return doc;
	}

	private void append(String text, AttributeSet a) throws BadLocationException {

		doc.insertString(doc.getLength(), text, a);

	}

	/**
	 * Gets the current pid.
	 *
	 * @return current pid for page
	 */
	public int getCurrentPid() {
		return currentPid;
	}

	/**
	 * init the pane.
	 *
	 * @param pers
	 *            the pers
	 * @param relations
	 *            the relations
	 * @param namlist
	 *            the namlist
	 */
	public void initPerson(PersonLongData pers, Relation[] relations, PersonShortData[] namlist) {

		final SimpleAttributeSet headerArial = new SimpleAttributeSet();
		StyleConstants.setFontFamily(headerArial, "Arial");
		StyleConstants.setFontSize(headerArial, 16);

		final SimpleAttributeSet bodyText = new SimpleAttributeSet();
		StyleConstants.setFontFamily(bodyText, "Times New Roman");
		StyleConstants.setFontSize(bodyText, 12);

		final SimpleAttributeSet bodyBold = new SimpleAttributeSet();
		StyleConstants.setFontFamily(bodyBold, "Times New Roman");
		StyleConstants.setFontSize(bodyBold, 12);
		StyleConstants.setBold(bodyBold, true);

		final HashMap<String, TimelineData> timelines = new HashMap<String, TimelineData>();
		TimelineData[] timelinea = null;
		TimelineData timeline;

		try {
			doc.remove(0, doc.getLength());

			if (pers == null) {
				currentPid = 0;
				return;
			}
			currentPid = pers.getPid();

			doc.insertString(0, Resurses.getString("TEXT_HEADER") + "\n", headerArial);

			final UnitNotice[] notices = pers.getNotices();
			String bl = "";
			String blx = "";
			boolean blb = false;
			for (final UnitNotice notice : notices) {
				blx = notice.getTag();
				if ((notice.getFromDate() == null) || notice.getFromDate().isEmpty()) {
					blb = false;
				} else {
					blb = true;
				}
				if (blx.equals("NAME")) {
					bl = appendName(notice.getGivenname(), notice.getPatronym(), notice.getPrefix(),
							notice.getSurname(), notice.getPostfix());
					append(bl, bodyBold);
				} else if (blx.equals("BIRT") && blb) {
					timeline = new TimelineData(
							Utils.textDate(notice.getFromDate(), true) + ", " + Resurses.getString("DATA_BIRT") + "\n",
							notice.getFromDate());
					timelines.put(notice.getFromDate(), timeline);
				} else if (blx.equals("CHR") && blb) {
					timeline = new TimelineData(
							Utils.textDate(notice.getFromDate(), true) + ", " + Resurses.getString("DATA_CHR") + "\n",
							notice.getFromDate());
					timelines.put(notice.getFromDate(), timeline);
				} else if (blx.equals("OCCU") && blb) {
					timeline = new TimelineData(Utils.textDate(notice.getFromDate(), true) + ", "
							+ Resurses.getString("DATA_OCCU") + ", " + notice.getDescription() + "\n",
							notice.getFromDate());
					timelines.put(notice.getFromDate(), timeline);
				} else if (blx.equals("DEAT") && blb) {
					timeline = new TimelineData(
							Utils.textDate(notice.getFromDate(), true) + ", " + Resurses.getString("DATA_DEAT") + "\n",
							notice.getFromDate());
					timelines.put(notice.getFromDate(), timeline);
				} else if (blx.equals("BURI") && blb) {
					timeline = new TimelineData(
							Utils.textDate(notice.getFromDate(), true) + ", " + Resurses.getString("DATA_BURI") + "\n",
							notice.getFromDate());
					timelines.put(notice.getFromDate(), timeline);
				}

				bl = "";
			}

			// do relations now

			if ((relations != null) && (relations.length > 0) && (namlist != null)) {

				final HashMap<Integer, PersonShortData> map = new HashMap<Integer, PersonShortData>();

				for (final PersonShortData element : namlist) {
					map.put(Integer.valueOf(element.getPid()), element);
				}
				append("\n", bodyText);

				Relation rel;
				PersonShortData relative;
				RelationNotice[] relNotices;

				for (final Relation relation : relations) {
					rel = relation;
					if (rel.getTag().equals("WIFE") || rel.getTag().equals("HUSB")) {
						relative = map.get(Integer.valueOf(rel.getRelative()));
						bl = "";
						blx = appendName(relative.getGivenname(), relative.getPatronym(), relative.getPrefix(),
								relative.getSurname(), relative.getPostfix());

						RelationNotice rn;
						final StringBuilder wife = new StringBuilder();
						relNotices = rel.getNotices();
						if (relNotices != null) {
							for (final RelationNotice relNotice : relNotices) {
								rn = relNotice;
								if (rn.getDatePrefix() != null) {
									wife.append(bl + Resurses.getString("DATE_" + rn.getDatePrefix()));
									bl = ", ";
								}
								if (rn.getFromDate() != null) {
									wife.append(bl + Utils.textDate(rn.getFromDate(), true));
									bl = ", ";
								}
								if (rn.getToDate() != null) {
									if (rn.getDatePrefix() != null) {
										if (rn.getDatePrefix().equals("BET")) {
											wife.append(bl + Resurses.getString("DATE_AND"));
										} else if (rn.getDatePrefix().equals("FROM")) {
											wife.append(bl + Resurses.getString("DATE_TO"));
										}
										wife.append(bl + Utils.textDate(rn.getToDate(), true));
									}
									bl = ", ";
								}
								if (rn.getType() != null) {
									wife.append(bl + rn.getType());
									bl = ", ";
								}
								if (rn.getDescription() != null) {
									wife.append(bl + rn.getDescription());
								}

								timeline = new TimelineData(wife.toString() + ", " + blx, rn.getFromDate());
								timelines.put(relative.getBirtDate(), timeline);
							}
						}
						append("\n", bodyText);
					}
				}

				for (final Relation relation : relations) {
					rel = relation;
					if (rel.getTag().equals("CHIL")) {
						relative = map.get(Integer.valueOf(rel.getRelative()));
						bl = " ";
						bl = appendName(relative.getGivenname(), relative.getPatronym(), relative.getPrefix(),
								relative.getSurname(), relative.getPostfix());
						timeline = new TimelineData(Utils.textDate(relative.getBirtDate(), true) + ", "
								+ Resurses.getString("REPORT.TAB.CHILD") + ", " + bl, relative.getBirtDate());
						timelines.put(relative.getBirtDate(), timeline);
					}
				}
			}

			timelinea = new TimelineData[timelines.size()];

			final Iterator<String> it = timelines.keySet().iterator();
			int idx = 0;
			while (it.hasNext()) {
				timelinea[idx] = timelines.get(it.next());
				idx++;
			}

			final int x = timelinea.length;
			quicksort(timelinea, 0, x - 1);

			for (int xx = 0; xx < x; xx++) {
				append(timelinea[xx].getTimelineData(), bodyText);
			}

		} catch (final BadLocationException e) {
			logger.log(Level.WARNING, "FAILED", e);

		}
	}

	/**
	 * Quicksort.
	 *
	 * @param array
	 *            the array
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 */
	private static void quicksort(TimelineData array[], int left, int right) {
		int leftIdx = left;
		int rightIdx = right;
		TimelineData temp;

		if (((right - left) + 1) > 1) {
			int pivot = (left + right) / 2;
			while ((leftIdx <= pivot) && (rightIdx >= pivot)) {
				while ((array[leftIdx].getTimelineDate().compareTo(array[pivot].getTimelineDate()) < 0)
						&& (leftIdx <= pivot)) {
					leftIdx = leftIdx + 1;
				}
				while ((array[rightIdx].getTimelineDate().compareTo(array[pivot].getTimelineDate()) > 0)
						&& (rightIdx >= pivot)) {
					rightIdx = rightIdx - 1;
				}
				temp = array[leftIdx];
				array[leftIdx] = array[rightIdx];
				array[rightIdx] = temp;
				leftIdx = leftIdx + 1;
				rightIdx = rightIdx - 1;
				if ((leftIdx - 1) == pivot) {
					pivot = rightIdx = rightIdx + 1;
				} else if ((rightIdx + 1) == pivot) {
					pivot = leftIdx = leftIdx - 1;
				}
			}
			quicksort(array, left, pivot - 1);
			quicksort(array, pivot + 1, right);
		}
	}

	private String appendName(String givenname, String prefix, String patronym, String surname, String postfix)
			throws BadLocationException {

		final StringBuilder name = new StringBuilder();
		String bl = "";
		if (givenname != null) {

			final String[] parts = givenname.split(" ");

			for (int j = 0; j < parts.length; j++) {
				if (j > 0) {
					name.append(" ");
					bl = "";
				}
				if (parts[j].endsWith("*")) {
					name.append(bl + parts[j].substring(0, parts[j].length() - 1));
				} else {
					name.append(bl + parts[j]);
				}
			}

			bl = " ";
		}
		if (patronym != null) {
			name.append(bl + patronym);
			bl = " ";
		}

		if (prefix != null) {
			name.append(bl + prefix);
			bl = " ";
		}

		if (surname != null) {
			name.append(bl + surname);
			bl = " ";
		}

		if (postfix != null) {
			name.append(bl + postfix);
			bl = " ";
		}

		return name.toString() + "\n";

	}

}
