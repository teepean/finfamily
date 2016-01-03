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

package fi.kaila.suku.report.style;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 * Base class for styles in Suku11 reports A BodyText style element consists of
 * a vector of Text elements.
 *
 * @author Kalle
 */
public class BodyText {

	/** Align left value for paraAlignment. */
	public static final int ALIGN_LEFT = 0;

	/** Align center value for paraAlignment. */
	public static final int ALIGN_CENTER = 1;

	/** Serif Font value for fontName. */
	public static final String FONT_SERIF = "Times new Roman";

	/** SansSerif Font value for fontName. */
	public static final String FONT_SANS_SERIF = "Arial";

	/** The font name. */
	protected String fontName = FONT_SANS_SERIF;

	/** The font size. */
	protected int fontSize = 10;

	/** The font style. */
	protected int fontStyle = Font.PLAIN;
	// protected boolean fontUnderline=false;

	/** The para alignment. */
	protected int paraAlignment = ALIGN_LEFT;

	/** The para indent left. */
	protected float paraIndentLeft = 6; // left alignment in pt

	/** The para spacing before. */
	protected float paraSpacingBefore = 0; // spacing before in pt

	/** The para spacing after. */
	protected float paraSpacingAfter = 6; // spacing after in pt

	/** The image. */
	protected BufferedImage image = null;

	/** The imagedata. */
	protected byte[] imagedata = null;

	/** The image name. */
	protected String imageName = null;

	private Vector<Text> txt = new Vector<Text>();

	/**
	 * Gets the font name.
	 *
	 * @return fontname for this style
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * font size in pt (1/72) inch return fontSize.
	 *
	 * @return fontsize for this style
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * set (different) font size.
	 *
	 * @param fontSize
	 *            the new font size
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * reset container.
	 */
	public void reset() {
		txt = new Vector<Text>();
	}

	/**
	 * alignment of paragraph ALIGN_LEFT=0 ALIGN_CENTER=1.
	 *
	 * @return paraAlignment
	 */
	public int getParaAlignment() {
		return paraAlignment;
	}

	/**
	 * Left ident of paragraph in cm.
	 *
	 * @return paraIndentLeft
	 */
	public float getParaIndentLeft() {
		return paraIndentLeft;
	}

	/**
	 * Paragraph spacing in pt (1/72 inch).
	 *
	 * @return paraSpacingBefore
	 */
	public float getParaSpacingBefore() {
		return paraSpacingBefore;
	}

	/**
	 * Paragraph spacing in pt (1/72 inch).
	 *
	 * @return paraSpacingAfter
	 */
	public float getParaSpacingAfter() {
		return paraSpacingAfter;
	}

	/**
	 * add text string to style element.
	 *
	 * @param text
	 *            the text
	 */
	public void addText(String text) {
		final Text t = new Text(text);
		txt.add(t);
	}

	/**
	 * add text string with formatting options to style element.
	 *
	 * @param text
	 *            the text
	 * @param isBold
	 *            the is bold
	 * @param isUnderline
	 *            the is underline
	 */
	public void addText(String text, boolean isBold, boolean isUnderline) {
		final Text t = new Text(text);
		t.isBold = isBold;
		t.isUnderline = isUnderline;
		txt.add(t);
	}

	/**
	 * add text string with formatting options to style element.
	 *
	 * @param text
	 *            the text
	 * @param isBold
	 *            the is bold
	 * @param isUnderline
	 *            the is underline
	 * @param isItalic
	 *            the is italic
	 */
	public void addText(String text, boolean isBold, boolean isUnderline, boolean isItalic) {
		final Text t = new Text(text);
		t.isBold = isBold;
		t.isUnderline = isUnderline;
		t.isItalic = isItalic;
		txt.add(t);
	}

	/**
	 * Add link with text.
	 *
	 * @param text
	 *            the text
	 * @param isBold
	 *            the is bold
	 * @param isUnderline
	 *            the is underline
	 * @param isItalic
	 *            the is italic
	 * @param link
	 *            the link
	 */
	public void addLink(String text, boolean isBold, boolean isUnderline, boolean isItalic, String link) {
		Text t = new Text(text);
		t.isBold = isBold;
		t.isUnderline = isUnderline;
		t.isItalic = isItalic;

		final String[] parts = link.split(",");

		if (parts.length == 1) {
			t.link = link;
			txt.add(t);
			return;
		}
		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				t = new Text(",");
				t.isBold = isBold;
				t.isUnderline = isUnderline;
				t.isItalic = isItalic;
				txt.add(t);
			}
			t = new Text(parts[i]);
			t.isBold = isBold;
			t.isUnderline = isUnderline;
			t.isItalic = isItalic;

			t.link = parts[i];

			txt.add(t);
		}

	}

	/**
	 * Adds the anchor.
	 *
	 * @param anchor
	 *            the anchor
	 */
	public void addAnchor(String anchor) {
		final Text t = new Text(null);
		t.anchor = anchor;
		txt.add(t);
	}

	/**
	 * Gets the anchor.
	 *
	 * @param idx
	 *            the idx
	 * @return the anchor
	 */
	public String getAnchor(int idx) {
		final Text t = txt.get(idx);
		return t.anchor;
	}

	/**
	 * Gets the count.
	 *
	 * @return text size in style element
	 */
	public int getCount() {
		return txt.size();
	}

	/**
	 * Gets the text.
	 *
	 * @param idx
	 *            the idx
	 * @return text content of text element
	 */
	public String getText(int idx) {
		final Text t = txt.get(idx);
		return t.text;
	}

	/**
	 * Gets the link.
	 *
	 * @param idx
	 *            the idx
	 * @return link from text element or null
	 */
	public String getLink(int idx) {
		final Text t = txt.get(idx);
		return t.link;
	}

	/**
	 * Checks if is bold.
	 *
	 * @param idx
	 *            the idx
	 * @return true if the indexed text element is bold
	 */
	public boolean isBold(int idx) {
		if ((fontStyle & Font.BOLD) != 0) {
			return true;
		}

		final Text t = txt.get(idx);
		return t.isBold;
	}

	/**
	 * Checks if is underline.
	 *
	 * @param idx
	 *            the idx
	 * @return true if the indexed text elemet is underlined
	 */
	public boolean isUnderline(int idx) {
		final Text t = txt.get(idx);
		return t.isUnderline;
	}

	/**
	 * Checks if is italic.
	 *
	 * @param idx
	 *            the idx
	 * @return true if the indexed text elemet is underlined
	 */
	public boolean isItalic(int idx) {
		final Text t = txt.get(idx);
		return t.isItalic;
	}

	/**
	 * Ends with text.
	 *
	 * @param suffix
	 *            the suffix
	 * @return true, if successful
	 */
	public boolean endsWithText(String suffix) {

		final StringBuilder sb = new StringBuilder();

		for (final Text t : txt) {
			sb.append(t.text);
		}
		if (sb.toString().trim().endsWith(suffix)) {
			return true;
		}
		return false;
	}

	private class Text {
		String text;
		boolean isBold = false;
		boolean isUnderline = false;
		boolean isItalic = false;
		String link = null;
		String anchor = null;

		Text(String text) {
			addTxt(text);
		}

		private void addTxt(String text) {
			if (text == null) {
				this.text = "";
				return;
			}

			final int i = text.indexOf("\n");

			if (i < 0) {
				this.text = text;
				return;
			}
			int lfCount = 0;
			final StringBuilder sb = new StringBuilder();

			for (int j = 0; j < i; j++) {
				if (text.charAt(j) != '\r') {
					sb.append(text.charAt(j));
				}
			}

			for (int j = i; j < text.length(); j++) {
				if (text.charAt(j) != '\r') {
					if (text.charAt(j) == '\n') {
						lfCount++;
						if (lfCount > 1) {
							lfCount = 0;
							sb.append('\n');
						}
					} else if (text.charAt(j) == ' ') {
						if (lfCount == 0) {
							sb.append(' ');
						}
					} else {
						if (lfCount > 0) {
							lfCount = 0;
							sb.append(' ');
						}
						sb.append(text.charAt(j));
					}
				}
			}

			this.text = sb.toString();

		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		if (imageName != null) {
			sb.append("Image: ");
			sb.append(imageName);
			sb.append("\n");
		}

		for (final Text t : txt) {
			sb.append(t.text);
		}
		return sb.toString();
	}

}
