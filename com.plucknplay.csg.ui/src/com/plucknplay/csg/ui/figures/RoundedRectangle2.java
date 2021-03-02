/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;

public class RoundedRectangle2 extends RoundedRectangle {

	private boolean fill;
	private boolean outline;

	@Override
	public Rectangle getBounds() {
		return Rectangle.SINGLETON.setBounds(super.getBounds());
	}

	@Override
	public void setFill(final boolean fill) {
		super.setFill(fill);
		this.fill = fill;
	}

	public boolean getFill() {
		return fill;
	}

	@Override
	public void setOutline(final boolean outline) {
		super.setOutline(outline);
		this.outline = outline;
	}

	public boolean getOutline() {
		return outline;
	}
}
