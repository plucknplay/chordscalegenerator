/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

public class MutedStringFigure extends Figure {

	private final int lineWidth;

	public MutedStringFigure(final int lineWidth) {
		this.lineWidth = lineWidth;
	}

	@Override
	protected void paintFigure(final Graphics g) {

		g.setLineWidth(lineWidth);

		g.setLineCap(SWT.CAP_ROUND);
		g.setLineStyle(SWT.LINE_SOLID);
		g.setBackgroundColor(ColorConstants.black);
		g.setForegroundColor(ColorConstants.black);

		final int offset = 1;
		final Rectangle b = getBounds();
		g.drawLine(b.x + offset, b.y + offset, b.x + b.width - 2 * offset, b.y + b.height - 2 * offset);
		g.drawLine(b.x + b.width - 2 * offset, b.y + offset, b.x + offset, b.y + b.height - 2 * offset);
	}
}
