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

package fi.kaila.suku.report;

import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.util.SukuException;

/**
 *
 * Report writer implements the report interface. Report writers are planned for
 * Word (possibly Word 2003 xml-format as in Suku 2004) and html A report writer
 * using Java textpane as output is used for preview
 *
 * <h1>Introduction to ReportInterface</h1>
 *
 * <p>
 * To create a report file using the ReportInterface is done by collecting the
 * text into Styles that extend the <b>fi.kaila.suku.report.style.BodyText</b>.
 * </p>
 *
 * <p>
 * Collect the text to the style using their various addText(String text,...) or
 * addLink(String text,boolean isBold,boolean isUnderline,boolean isItalic,
 * String link) methods. Add anchors to the text if you need to link to
 * locations within a file using addAnchor(String anchor) method.
 * </p>
 *
 * <p>
 * One Style should be used for one Chapter. To end the Chapter write the
 * contents of the style to the ReportInterface using the addText(BodyText bt)
 * method. You cannot change the style before you write the previous style
 * contents first. (It will be lost else).
 * </p>
 *
 * <p>
 * Note, that Images are managed a bit differently. A main reason are some
 * restrictions in images when printing to preview (JavaReport).
 * </p>
 *
 * <p>
 * The <b>fi.kaila.suku.report.XmlReport</b> class that implements the
 * ReportInterface creates a xml dom-tree. The idea is that each style creates a
 * chapter element with the attribute style=(BodyText class name) to write out
 * one chapter of text. Within the chapter it is possible to change the font
 * attributes bold,underline,italic and size.
 * </p>
 *
 * <p>
 * Start each report file using the createReport method and write the file using
 * closeReport() for a single output file (word 2003, html or preview) or using
 * closeReport(long tableNo) if several output files are desired for each file.
 * </p>
 *
 * <p>
 * The closeReport() method transforms the dom-tree to a single file and
 * closeReport(long tableNo) method transforms the dom-tree to a single
 * html-file.
 * </p>
 *
 *
 *
 * @author Kalle
 *
 */
public interface ReportInterface {

	/**
	 * Add text to the report.
	 *
	 * @param text
	 *            as a {@link BodyText } based object.
	 */
	public void addText(BodyText text);

	/**
	 * close the report.
	 *
	 * @throws SukuException
	 *             the suku exception
	 */
	public void closeReport() throws SukuException;

	/**
	 * close report part.
	 *
	 * @param tabNo
	 *            the tab no
	 * @throws SukuException
	 *             the suku exception
	 */
	public void closeReport(long tabNo) throws SukuException;

	/**
	 * create the report.
	 *
	 * @throws SukuException
	 *             the suku exception
	 */
	public void createReport() throws SukuException;

}
