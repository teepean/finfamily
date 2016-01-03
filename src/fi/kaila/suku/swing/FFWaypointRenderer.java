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

package fi.kaila.suku.swing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

/**
 * A FinFamily waypoint painter.
 */
public class FFWaypointRenderer implements WaypointRenderer<FFWaypoint> {
	private final Map<Color, BufferedImage> map = new HashMap<Color, BufferedImage>();

	private BufferedImage origImage;

	/**
	 * Uses a default waypoint image.
	 */
	public FFWaypointRenderer() {
		final URL resource = getClass().getResource("/images/waypoint_white.png");

		try {
			origImage = ImageIO.read(resource);
		} catch (final Exception ex) {
		}
	}

	private BufferedImage convert(BufferedImage loadImg, Color newColor) {
		final int w = loadImg.getWidth();
		final int h = loadImg.getHeight();
		final BufferedImage imgOut = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		final BufferedImage imgColor = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		final Graphics2D g = imgColor.createGraphics();
		g.setColor(newColor);
		g.fillRect(0, 0, w + 1, h + 1);
		g.dispose();

		final Graphics2D graphics = imgOut.createGraphics();
		graphics.drawImage(loadImg, 0, 0, null);
		graphics.setComposite(MultiplyComposite.Default);
		graphics.drawImage(imgColor, 0, 0, null);
		graphics.dispose();

		return imgOut;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jxmapviewer.viewer.WaypointRenderer#paintWaypoint(java.awt.
	 * Graphics2D, org.jxmapviewer.JXMapViewer, java.lang.Object)
	 */
	@Override
	public void paintWaypoint(Graphics2D g, JXMapViewer viewer, FFWaypoint w) {
		g = (Graphics2D) g.create();

		if (origImage == null) {
			return;
		}

		BufferedImage myImg = map.get(w.getColor());

		if (myImg == null) {
			myImg = convert(origImage, w.getColor());
			map.put(w.getColor(), myImg);
		}

		final Point2D point = viewer.getTileFactory().geoToPixel(w.getPosition(), viewer.getZoom());

		final int x = (int) point.getX();
		final int y = (int) point.getY();

		g.drawImage(myImg, x - (myImg.getWidth() / 2), y - myImg.getHeight(), null);

		final String label = w.getLabel();

		final FontMetrics metrics = g.getFontMetrics();
		final int tw = metrics.stringWidth(label);
		final int th = 30 + metrics.getAscent();

		g.drawString(label, x - (tw / 2), (y + th) - myImg.getHeight());

		g.dispose();
	}
}
