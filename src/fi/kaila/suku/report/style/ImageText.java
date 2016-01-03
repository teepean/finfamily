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

import java.awt.image.BufferedImage;

import fi.kaila.suku.util.Utils;

/**
 * Images are implemented as this special styles.
 *
 * @author Kalle
 */
public class ImageText extends BodyText {

	/**
	 * Style contains only an image with optional title.
	 */
	public ImageText() {
		fontName = FONT_SERIF;
		paraAlignment = ALIGN_CENTER;
	}

	private boolean isPersonImage = true;
	private byte[] data = null;
	private int width = 0;
	private int height = 0;
	private String imageName = null;
	private String imageTitle = null;

	/**
	 * Set the image for reports.
	 *
	 * @param img
	 *            the img
	 * @param data
	 *            the data
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param name
	 *            the name
	 * @param title
	 *            the title
	 * @param tag
	 *            the tag
	 */
	public void setImage(BufferedImage img, byte[] data, int width, int height, String name, String title, String tag) {
		image = img;
		this.data = data;
		this.width = width;
		this.height = height;
		this.imageName = name;
		this.imageTitle = title;
		this.isPersonImage = Utils.nv(tag).equals("PHOT") ? true : false;

	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the image name.
	 *
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * Gets the image title.
	 *
	 * @return the imageTitle
	 */
	public String getImageTitle() {
		return imageTitle;
	}

	/**
	 * Checks if is person image.
	 *
	 * @return the isPersonImage
	 */
	public boolean isPersonImage() {
		return isPersonImage;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return imageName + ":" + imageTitle;

	}

}
