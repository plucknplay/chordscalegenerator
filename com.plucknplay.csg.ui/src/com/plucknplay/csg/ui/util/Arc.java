/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A shape representation of an arc, so that arcs can be used in editparts.
 * 
 * <p>
 * Copyright 2005, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </p>
 * 
 * Contributors: The Chisel Group, University of Victoria
 */
public class Arc extends Shape {

	/**
	 * The closure type for an arc closed by drawing straight line segments from
	 * the start of the arc segment to the center of the full ellipse and from
	 * that point to the end of the arc segment.
	 */
	public static final int PIE = 2;

	private int offset;
	private int length;

	/**
	 * There are times when the clipping bounds and the bounds of the arc will
	 * be different allow for this.
	 */
	private Rectangle arcBounds;

	public Arc() {
		super();
		setBounds(new Rectangle(0, 0, 0, 0));
		arcBounds = new Rectangle(0, 0, 0, 0);
	}

	public Arc(final Rectangle r, final int offset, final int length) {
		super();
		setBounds(r);
		arcBounds = new Rectangle(r);
		this.offset = offset;
		this.length = length;
	}

	public Arc(final int x, final int y, final int width, final int height, final int offset, final int length) {
		this(new Rectangle(x, y, width, height), offset, length);
	}

	@Override
	protected void fillShape(final Graphics graphics) {
		graphics.fillArc(getArcBounds(), offset, length);
	}

	@Override
	protected void outlineShape(final Graphics graphics) {
		graphics.drawArc(getArcBounds(), offset, length);
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(final int offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(final int length) {
		this.length = length;
	}

	@Override
	public boolean containsPoint(final int x, final int y) {
		final Rectangle r = getArcBounds();
		final double ellw = r.width;
		if (ellw <= 0.0) {
			return false;
		}
		final double normx = (x - r.x) / ellw - 0.5;
		final double ellh = r.height;
		if (ellh <= 0.0) {
			return false;
		}
		final double normy = (y - r.y) / ellh - 0.5;
		final double distSq = normx * normx + normy * normy;
		if (distSq >= 0.25) {
			return false;
		}
		final double angExt = Math.abs(getLength());
		if (angExt >= 360.0) {
			return true;
		}
		final boolean inarc = containsAngle(-Math.toDegrees(Math.atan2(normy, normx)));
		return inarc;

	}

	public boolean containsAngle(final double angle) {
		double angExt = getLength();
		final boolean backwards = angExt < 0.0;
		if (backwards) {
			angExt = -angExt;
		}
		if (angExt >= 360.0) {
			return true;
		}
		double theAngle = normalizeDegrees(angle) - normalizeDegrees(getOffset());
		if (backwards) {
			theAngle = -angle;
		}
		if (angle < 0.0) {
			theAngle += 360.0;
		}

		return theAngle >= 0.0 && theAngle < angExt;
	}

	/*
	 * Normalizes the specified angle into the range -180 to 180.
	 */
	static double normalizeDegrees(final double angle) {
		double theAngle = angle;

		if (theAngle > 180.0) {
			if (theAngle <= 180.0 + 360.0) {
				theAngle = angle - 360.0;
			} else {
				theAngle = Math.IEEEremainder(theAngle, 360.0);
				// IEEEremainder can return -180 here for some input values...
				if (theAngle == -180.0) {
					theAngle = 180.0;
				}
			}
		} else if (theAngle <= -180.0) {
			if (theAngle > -180.0 - 360.0) {
				theAngle = angle + 360.0;
			} else {
				theAngle = Math.IEEEremainder(theAngle, 360.0);
				// IEEEremainder can return -180 here for some input values...
				if (theAngle == -180.0) {
					theAngle = 180.0;
				}
			}
		}
		return angle;
	}

	public Rectangle getArcBounds() {
		return arcBounds;
	}

	protected void setArcBounds(final Rectangle bounds) {
		arcBounds = bounds;
	}
}
