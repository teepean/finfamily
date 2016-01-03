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

package fi.kaila.suku.swing.text;

import java.io.Serializable;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Aux class for java type text.
 *
 * @author Kalle
 */
public class DocumentSukuFilter extends DocumentFilter implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The DEBUG. */
	boolean DEBUG = false;

	/**
	 * Instantiates a new document suku filter.
	 */
	public DocumentSukuFilter() {
		// TODO: Empty
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.text.DocumentFilter#insertString(javax.swing.text.
	 * DocumentFilter .FilterBypass, int, java.lang.String,
	 * javax.swing.text.AttributeSet)
	 */
	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		if (DEBUG) {
			System.out.println("in DocumentSizeFilter's insertString [" + offs + "]:" + str);
		}

		// This rejects the entire insertion if it would make
		// the contents too long. Another option would be
		// to truncate the inserted string so the contents
		// would be exactly maxCharacters in length.
		// if ((fb.getDocument().getLength() + str.length()) <= maxCharacters)
		super.insertString(fb, offs, str, a);
		// else
		// Toolkit.getDefaultToolkit().beep();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.swing.text.DocumentFilter#replace(javax.swing.text.DocumentFilter
	 * .FilterBypass, int, int, java.lang.String, javax.swing.text.AttributeSet)
	 */
	@Override
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		if (DEBUG) {
			System.out.println("in DocumentSizeFilter's replace [" + offs + ";" + length + "]:" + str);
		}
		// This rejects the entire replacement if it would make
		// the contents too long. Another option would be
		// to truncate the replacement string so the contents
		// would be exactly maxCharacters in length.
		// if ((fb.getDocument().getLength() + str.length()
		// - length) <= maxCharacters)
		super.replace(fb, offs, length, str, a);
		// else
		// Toolkit.getDefaultToolkit().beep();
	}

}
