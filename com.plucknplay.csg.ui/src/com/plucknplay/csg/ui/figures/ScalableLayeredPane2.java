/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.ScaledGraphics;

public class ScalableLayeredPane2 extends ScalableLayeredPane {

	@Override
	protected void paintClientArea(final Graphics graphics) {
		if (getChildren().isEmpty()) {
			return;
		}
		if (getScale() == 1.0) {
			super.paintClientArea(graphics);
		} else {
			final ScaledGraphics g = new ScaledGraphics(graphics) {
				@Override
				public float getLineWidthFloat() {
					return graphics.getLineWidth();
				}

				@Override
				public void setLineWidth(final int width) {
					graphics.setLineWidth(width);
				};

				@Override
				public int getLineWidth() {
					return graphics.getLineWidth();
				};
			};
			final boolean optimizeClip = getBorder() == null || getBorder().isOpaque();
			if (!optimizeClip) {
				g.clipRect(getBounds().getCropped(getInsets()));
			}
			g.scale(getScale());
			g.pushState();
			paintChildren(g);
			g.dispose();
			graphics.restoreState();
		}
	}
}
