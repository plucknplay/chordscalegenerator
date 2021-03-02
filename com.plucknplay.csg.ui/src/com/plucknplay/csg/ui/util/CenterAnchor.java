/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class CenterAnchor extends AbstractConnectionAnchor {

	public CenterAnchor(final IFigure owner) {
		super(owner);
	}

	@Override
	public Point getLocation(final Point reference) {
		final Rectangle r = getOwner().getBounds();
		final Point p = new Point(r.x + r.width / 2 + 1, r.y + r.height / 2 + 1);
		getOwner().translateToAbsolute(p);
		return p;
	}
}
