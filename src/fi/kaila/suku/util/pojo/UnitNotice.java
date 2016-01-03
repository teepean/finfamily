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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.imageio.ImageIO;

/**
 * Container class for UnitNotice table.
 *
 * @author Kalle
 */
public class UnitNotice implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private boolean toBeDeleted = false;
	private boolean toBeUpdated = false;

	/** The pnid. */
	int pnid = 0;

	/** The pid. */
	int pid = 0;

	/** The surety indicator. */
	int surety = 100;

	/** The Row # of the Notice for the unit. */
	int noticeRow = 0;

	/** The tag of the Notice, Mostly Level 1 GEDCOM tags. */
	String tag = null;

	/** The privacy indicator, null = Public. */
	String privacy = null;

	/** The notice type (L). */
	String noticeType = null;

	/** The description or remark (L). */
	String description = null;

	/** The Prefix for the date (beginning date if date period). */
	String datePrefix = null;

	/** The Date for the event described in this notice. */
	String fromDate = null;

	/** The Date for the event described in this notice. */
	String toDate = null;

	/** The place. */
	String place = null;

	/** The village - varchar, -- Kyl NEW. */
	String village = null;

	/** The farm - varchar, -- Talo NEW. */
	String farm = null;

	/** The croft - varchar, -- Torppa NEW. */
	String croft = null;

	/** The address - varchar, -- Address line 1 / Village/Kylä. */
	String address = null;

	/** The post office - varchar, -- Place of the event, Postoffice, City. */
	String postOffice = null;

	/** The postal code - varchar, -- Postal Code. */
	String postalCode = null;

	/** The state - varchar, -- State. */
	String state = null;

	/** The country - varchar, -- Country. */
	String country = null;

	/** The email - varchar, -- Email-address or web-page of person. */
	String email = null;

	/** The note text - varchar, -- Note textfield (L). */
	String noteText = null;

	/** The media filename - varchar, -- Filename of the multimedia file. */
	String mediaFilename = null;

	/** The media data - bytea, -- Container of image. */
	byte[] mediaData = null;

	/**
	 * The media title - varchar, -- text describing the multimedia file (L).
	 */
	String mediaTitle = null;

	/** The media width - integer, -- media width in pixels. */
	int mediaWidth = 0;

	/** The media height - integer, -- media height in pixels. */
	int mediaHeight = 0;

	/** The prefix - varchar, -- Prefix of the surname. */
	String prefix = null;

	/** The surname - varchar, -- Surname. */
	String surname = null;

	/** The givenname - varchar, -- Givenname. */
	String givenname = null;

	/** The patronym - varchar, -- Patronyymi NEW. */
	String patronym = null;

	/** The post fix - varchar, -- Name Postfix. */
	String postFix = null;

	/** The ref names - varchar, -- List of names within notice for index. */
	String[] refNames = null;

	/** The ref places - varchar, -- List of places within notice for index. */
	String[] refPlaces = null;

	/** The source text - varchar , -- Source as text. */
	String sourceText = null;

	/** The private text - varchar, -- Private researcher information. */
	String privateText = null;

	/** The modified - timestamp, -- timestamp modified. */
	Timestamp modified = null;

	/**
	 * The create date - timestamp not null default now() -- timestamp created.
	 */
	Timestamp createDate = null;

	/** The modifiedBy userid. */
	String modifiedBy = null;

	/** The createdBy userid. */
	String createdBy = null;

	private transient BufferedImage image = null;

	private UnitLanguage[] unitlanguages = null;

	/**
	 *
	 * The sr has format select * from unitNotice.
	 *
	 * @param rs
	 *            the rs
	 * @throws SQLException
	 *             the sQL exception
	 */

	public UnitNotice(ResultSet rs) throws SQLException {
		pnid = rs.getInt("pnid");
		pid = rs.getInt("pid");
		surety = rs.getInt("surety");
		noticeRow = rs.getInt("noticerow");
		tag = rs.getString("tag");
		privacy = rs.getString("privacy");
		noticeType = rs.getString("noticetype");
		description = rs.getString("description");
		datePrefix = rs.getString("dateprefix");
		fromDate = rs.getString("fromdate");
		toDate = rs.getString("todate");
		place = rs.getString("place");
		village = rs.getString("village");
		farm = rs.getString("farm");
		croft = rs.getString("croft");
		address = rs.getString("address");
		postOffice = rs.getString("postoffice");
		postalCode = rs.getString("postalcode");
		state = rs.getString("state");
		country = rs.getString("country");
		email = rs.getString("email");
		noteText = rs.getString("notetext");
		mediaFilename = rs.getString("mediafilename");
		mediaData = rs.getBytes("mediadata");
		mediaTitle = rs.getString("mediatitle");
		mediaWidth = rs.getInt("mediawidth");
		mediaHeight = rs.getInt("mediaheight");
		prefix = rs.getString("prefix");
		surname = rs.getString("surname");
		givenname = rs.getString("givenname");
		patronym = rs.getString("patronym");
		postFix = rs.getString("postfix");
		refNames = null;
		if ("NOTE".equals(tag)) {
			Array xx = rs.getArray("refnames");
			if (xx != null) {
				refNames = (String[]) xx.getArray();

			}
			xx = rs.getArray("refplaces");
			if (xx != null) {
				refPlaces = (String[]) xx.getArray();

			}

		}

		sourceText = rs.getString("sourcetext");
		privateText = rs.getString("privatetext");
		modified = rs.getTimestamp("modified");
		createDate = rs.getTimestamp("createDate");
		modifiedBy = rs.getString("modifiedby");
		createdBy = rs.getString("createdby");

	}

	/**
	 * Sets the to be deleted.
	 *
	 * @param value
	 *            true if this is to be deleted
	 */
	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
	}

	/**
	 * reset modifeid flag.
	 *
	 * @param value
	 *            the new modified
	 */
	public void setModified(boolean value) {
		toBeUpdated = value;
	}

	/**
	 * Checks if is to be deleted.
	 *
	 * @return true if this is to be deleted
	 */
	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	/**
	 * Checks if is to be updated.
	 *
	 * @return true if this is to be updated
	 */
	public boolean isToBeUpdated() {
		if (toBeUpdated) {
			return true;
		}
		if (unitlanguages == null) {
			return false;
		}
		for (final UnitLanguage unitlanguage : unitlanguages) {
			if (unitlanguage.isToBeUpdated() || unitlanguage.isToBeDeleted()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Instantiates a new unit notice.
	 *
	 * @param tag
	 *            the tag
	 */
	public UnitNotice(String tag) {
		this.tag = tag;
	}

	/**
	 * Instantiates a new unit notice.
	 *
	 * @param tag
	 *            the tag
	 * @param pid
	 *            the pid
	 */
	public UnitNotice(String tag, int pid) {
		this.tag = tag;
		this.pid = pid;
	}

	/**
	 * Sets the languages.
	 *
	 * @param languages
	 *            = array of language variants
	 */
	public void setLanguages(UnitLanguage[] languages) {
		this.unitlanguages = languages;
	}

	/**
	 * Gets the languages.
	 *
	 * @return the array of existing language varianls
	 */
	public UnitLanguage[] getLanguages() {
		return this.unitlanguages;
	}

	/**
	 * Gets the pnid.
	 *
	 * @return perrson notice id
	 */
	public int getPnid() {
		return pnid;
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
	 * Gets the tag.
	 *
	 * @return tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Gets the surety.
	 *
	 * @return surety
	 */
	public int getSurety() {
		return surety;
	}

	/**
	 * Sets the surety.
	 *
	 * @param surety
	 *            (0,20,40,60,80,100)
	 */
	public void setSurety(int surety) {
		if (this.surety != surety) {
			this.toBeUpdated = true;
			this.surety = surety;
		}
	}

	/**
	 * Gets the privacy.
	 *
	 * @return privact
	 */
	public String getPrivacy() {
		return privacy;
	}

	/**
	 * Sets the privacy.
	 *
	 * @param text
	 *            = null,"P","T" or "I"
	 */
	public void setPrivacy(String text) {
		if (!nv(this.privacy).equals(nv(text))) {
			toBeUpdated = true;
			this.privacy = vn(text);
		}

	}

	/**
	 * Gets the notice type.
	 *
	 * @return notice type
	 */
	public String getNoticeType() {
		return trim(noticeType);
	}

	/**
	 * Sets the notice type.
	 *
	 * @param text
	 *            = notice type
	 */
	public void setNoticeType(String text) {
		if (!nv(this.noticeType).equals(nv(text))) {
			toBeUpdated = true;
			this.noticeType = vn(text);
		}

	}

	/**
	 * Gets the description.
	 *
	 * @return description
	 */
	public String getDescription() {
		return trim(description);
	}

	/**
	 * Sets the description.
	 *
	 * @param text
	 *            the new description
	 */
	public void setDescription(String text) {
		if (!nv(this.description).equals(nv(text))) {
			toBeUpdated = true;
			this.description = vn(text);
		}

	}

	/**
	 * Gets the date prefix.
	 *
	 * @return dateprefix
	 */
	public String getDatePrefix() {
		return datePrefix;
	}

	/**
	 * Sets the date prefix.
	 *
	 * @param text
	 *            dateprefic (see GEDCOM)
	 */
	public void setDatePrefix(String text) {
		if (!nv(this.datePrefix).equals(nv(text))) {
			toBeUpdated = true;
			this.datePrefix = vn(text);
		}

	}

	/**
	 * Gets the from date.
	 *
	 * @return main / first part of date
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * Sets the from date.
	 *
	 * @param text
	 *            main date
	 */
	public void setFromDate(String text) {
		if (!nv(this.fromDate).equals(nv(text))) {
			toBeUpdated = true;
			this.fromDate = vn(text);
		}

	}

	/**
	 * Gets the to date.
	 *
	 * @return second date of date interval
	 */
	public String getToDate() {
		return toDate;
	}

	/**
	 * Sets the to date.
	 *
	 * @param text
	 *            second part of date interval
	 */
	public void setToDate(String text) {
		if (!nv(this.toDate).equals(nv(text))) {
			toBeUpdated = true;
			this.toDate = vn(text);
		}

	}

	/**
	 * Gets the place.
	 *
	 * @return place
	 */
	public String getPlace() {
		return trim(place);
	}

	/**
	 * Sets the place.
	 *
	 * @param text
	 *            the new place
	 */
	public void setPlace(String text) {
		if (!nv(this.place).equals(nv(text))) {
			toBeUpdated = true;
			this.place = vn(text);
		}

	}

	/**
	 * Gets the village.
	 *
	 * @return village
	 */
	public String getVillage() {
		return trim(village);
	}

	/**
	 * Sets the village.
	 *
	 * @param text
	 *            the new village
	 */
	public void setVillage(String text) {
		if (!nv(this.village).equals(nv(text))) {
			toBeUpdated = true;
			this.village = vn(text);
		}

	}

	/**
	 * Gets the farm.
	 *
	 * @return farm
	 */
	public String getFarm() {
		return trim(farm);
	}

	/**
	 * Sets the farm.
	 *
	 * @param text
	 *            the new farm
	 */
	public void setFarm(String text) {
		if (!nv(this.farm).equals(nv(text))) {
			toBeUpdated = true;
			this.farm = vn(text);
		}

	}

	/**
	 * Gets the croft.
	 *
	 * @return croft (torppa)
	 */
	public String getCroft() {
		return trim(croft);
	}

	/**
	 * Sets the croft.
	 *
	 * @param text
	 *            the new croft
	 */
	public void setCroft(String text) {
		if (!nv(this.croft).equals(nv(text))) {
			toBeUpdated = true;
			this.croft = vn(text);
		}

	}

	/**
	 * Gets the address.
	 *
	 * @return adderss
	 */
	public String getAddress() {
		return trim(address);
	}

	/**
	 * Sets the address.
	 *
	 * @param text
	 *            the new address
	 */
	public void setAddress(String text) {
		if (!nv(this.address).equals(nv(text))) {
			toBeUpdated = true;
			this.address = vn(text);
		}

	}

	/**
	 * Sets the ref names.
	 *
	 * @param names
	 *            new namelist
	 */
	public void setRefNames(String[] names) {

		toBeUpdated = true;
		this.refNames = names;
	}

	/**
	 * Sets the ref places.
	 *
	 * @param places
	 *            ne place list
	 */
	public void setRefPlaces(String[] places) {

		toBeUpdated = true;
		this.refPlaces = places;
	}

	/**
	 * Gets the post office.
	 *
	 * @return postoiffixe
	 */
	public String getPostOffice() {
		return trim(postOffice);
	}

	/**
	 * Sets the post office.
	 *
	 * @param text
	 *            the new post office
	 */
	public void setPostOffice(String text) {
		if (!nv(this.postOffice).equals(nv(text))) {
			toBeUpdated = true;
			this.postOffice = vn(text);
		}

	}

	/**
	 * Gets the postal code.
	 *
	 * @return postalcode/zip
	 */
	public String getPostalCode() {
		return trim(postalCode);
	}

	/**
	 * Sets the postal code.
	 *
	 * @param text
	 *            the new postal code
	 */
	public void setPostalCode(String text) {
		if (!nv(this.postalCode).equals(nv(text))) {
			toBeUpdated = true;
			this.postalCode = vn(text);
		}

	}

	/**
	 * Gets the state.
	 *
	 * @return state
	 */
	public String getState() {
		return trim(state);
	}

	/**
	 * Gets the country.
	 *
	 * @return country
	 */
	public String getCountry() {
		return trim(country);
	}

	/**
	 * Sets the state.
	 *
	 * @param text
	 *            the new state
	 */
	public void setState(String text) {
		if (!nv(this.state).equals(nv(text))) {
			toBeUpdated = true;
			this.state = vn(text);
		}

	}

	/**
	 * Sets the country.
	 *
	 * @param text
	 *            the new country
	 */
	public void setCountry(String text) {
		if (!nv(this.country).equals(nv(text))) {
			toBeUpdated = true;
			this.country = vn(text);
		}

	}

	/**
	 * Gets the email.
	 *
	 * @return emailaddress
	 */
	public String getEmail() {
		return trim(email);
	}

	/**
	 * Sets the email.
	 *
	 * @param text
	 *            the new email
	 */
	public void setEmail(String text) {
		if (!nv(this.email).equals(nv(text))) {
			toBeUpdated = true;
			this.email = vn(text);
		}

	}

	/**
	 * Gets the note text.
	 *
	 * @return notetext
	 */
	public String getNoteText() {
		return trim(noteText);
	}

	/**
	 * Sets the note text.
	 *
	 * @param text
	 *            the new note text
	 */
	public void setNoteText(String text) {
		if (!nv(this.noteText).equals(nv(text))) {
			toBeUpdated = true;
			this.noteText = vn(text);
		}

	}

	/**
	 * Gets the media filename.
	 *
	 * @return mediafilename
	 */
	public String getMediaFilename() {
		return trim(mediaFilename);
	}

	/**
	 * Sets the media filename.
	 *
	 * @param text
	 *            the new media filename
	 */
	public void setMediaFilename(String text) {
		if (!nv(this.mediaFilename).equals(nv(text))) {
			toBeUpdated = true;
			this.mediaFilename = vn(text);
		}

	}

	/**
	 * Gets the media title.
	 *
	 * @return media title
	 */
	public String getMediaTitle() {
		return trim(mediaTitle);
	}

	/**
	 * Sets the media title.
	 *
	 * @param text
	 *            the new media title
	 */
	public void setMediaTitle(String text) {
		if (!nv(this.mediaTitle).equals(nv(text))) {
			toBeUpdated = true;
			this.mediaTitle = vn(text);
		}

	}

	/**
	 * Gets the media size.
	 *
	 * @return media size
	 */
	public Dimension getMediaSize() {
		return new Dimension(mediaWidth, mediaHeight);
	}

	/**
	 * Sets the media size.
	 *
	 * @param sz
	 *            the new media size
	 */
	public void setMediaSize(Dimension sz) {
		mediaWidth = sz.width;
		mediaHeight = sz.height;
		toBeUpdated = true;
	}

	/**
	 * Gets the media image.
	 *
	 * @return image
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public BufferedImage getMediaImage() throws IOException {
		if (mediaData == null) {
			return null;
		}
		final ByteArrayInputStream bb = new ByteArrayInputStream(mediaData);

		if (this.image != null) {
			return this.image;
		}

		this.image = ImageIO.read(bb);
		return this.image;

	}

	/**
	 * Sets the media data.
	 *
	 * @param data
	 *            the new media data
	 */
	public void setMediaData(byte[] data) {
		mediaData = data;
		image = null;
		toBeUpdated = true;
	}

	/**
	 * Gets the media data.
	 *
	 * @return media data
	 */
	public byte[] getMediaData() {
		return mediaData;
	}

	/**
	 * Gets the givenname.
	 *
	 * @return givenname
	 */
	public String getGivenname() {

		return trim(givenname);
	}

	/**
	 * Sets the givenname.
	 *
	 * @param text
	 *            the new givenname
	 */
	public void setGivenname(String text) {
		if (!nv(this.givenname).equals(nv(text))) {
			toBeUpdated = true;
			this.givenname = vn(text);
		}

	}

	/**
	 * Gets the patronym.
	 *
	 * @return patronym
	 */
	public String getPatronym() {
		return trim(patronym);
	}

	/**
	 * Sets the patronym.
	 *
	 * @param text
	 *            the new patronym
	 */
	public void setPatronym(String text) {
		if (!nv(this.patronym).equals(nv(text))) {
			toBeUpdated = true;
			this.patronym = vn(text);
		}
	}

	/**
	 * Gets the prefix.
	 *
	 * @return name prefix
	 */
	public String getPrefix() {
		return trim(prefix);
	}

	/**
	 * Sets the prefix.
	 *
	 * @param text
	 *            the new prefix
	 */
	public void setPrefix(String text) {
		if (!nv(this.prefix).equals(nv(text))) {
			toBeUpdated = true;
			this.prefix = vn(text);
		}

	}

	/**
	 * Gets the surname.
	 *
	 * @return surname
	 */
	public String getSurname() {
		return trim(surname);
	}

	/**
	 * Sets the surname.
	 *
	 * @param text
	 *            the new surname
	 */
	public void setSurname(String text) {
		if (!nv(this.surname).equals(nv(text))) {
			toBeUpdated = true;
			this.surname = vn(text);
		}

	}

	/**
	 * Gets the postfix.
	 *
	 * @return name postfix
	 */
	public String getPostfix() {
		return trim(postFix);
	}

	/**
	 * Sets the postfix.
	 *
	 * @param text
	 *            the new postfix
	 */
	public void setPostfix(String text) {
		if (!nv(this.postFix).equals(nv(text))) {
			toBeUpdated = true;
			this.postFix = vn(text);
		}

	}

	/**
	 * Gets the source.
	 *
	 * @return source
	 */
	public String getSource() {
		return trim(sourceText);
	}

	/**
	 * Gets the ref names.
	 *
	 * @return array of names in note text
	 */
	public String[] getRefNames() {
		return refNames;
	}

	/**
	 * Gets the ref places.
	 *
	 * @return array of places in note text
	 */
	public String[] getRefPlaces() {
		return refPlaces;
	}

	/**
	 * Sets the source.
	 *
	 * @param text
	 *            the new source
	 */
	public void setSource(String text) {
		if (!nv(this.sourceText).equals(nv(text))) {
			toBeUpdated = true;
			this.sourceText = vn(text);
		}

	}

	/**
	 * Gets the private text.
	 *
	 * @return private text
	 */
	public String getPrivateText() {
		return trim(privateText);
	}

	/**
	 * Sets the private text.
	 *
	 * @param text
	 *            the new private text
	 */
	public void setPrivateText(String text) {
		if (!nv(this.privateText).equals(nv(text))) {
			toBeUpdated = true;
			this.privateText = vn(text);
		}

	}

	/**
	 * Gets the modified.
	 *
	 * @return when modified
	 */
	public Timestamp getModified() {
		return modified;
	}

	/**
	 * Gets the modified by.
	 *
	 * @return user id of modifier
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * Gets the created.
	 *
	 * @return when created
	 */
	public Timestamp getCreated() {
		return createDate;
	}

	/**
	 * Gets the created by.
	 *
	 * @return userid of creator
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	private String trim(String text) {
		return text;
	}

	private String nv(String text) {
		if (text == null) {
			return "";
		}
		return text;
	}

	private String vn(String text) {
		if ((text == null) || (text.length() == 0)) {
			text = null;
		}

		return text;

	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		if (privacy != null) {
			return false;
		}
		if (noticeType != null) {
			return false;
		}
		if (description != null) {
			return false;
		}
		if (datePrefix != null) {
			return false;
		}
		if (fromDate != null) {
			return false;
		}
		if (toDate != null) {
			return false;
		}
		if (place != null) {
			return false;
		}
		if (village != null) {
			return false;
		}
		if (farm != null) {
			return false;
		}
		if (croft != null) {
			return false;
		}
		if (address != null) {
			return false;
		}
		if (postOffice != null) {
			return false;
		}
		if (postalCode != null) {
			return false;
		}
		if (state != null) {
			return false;
		}
		if (country != null) {
			return false;
		}
		if (email != null) {
			return false;
		}
		if (noteText != null) {
			return false;
		}
		if (mediaFilename != null) {
			return false;
		}
		if (mediaData != null) {
			return false;
		}
		if (mediaTitle != null) {
			return false;
		}
		if (prefix != null) {
			return false;
		}
		if (surname != null) {
			return false;
		}
		if (givenname != null) {
			return false;
		}

		if (patronym != null) {
			return false;
		}
		if (postFix != null) {
			return false;
		}
		if (refNames != null) {
			return false;
		}
		if (refPlaces != null) {
			return false;
		}
		if (sourceText != null) {
			return false;
		}
		if (privateText != null) {
			return false;
		}

		return true;
	}

}
