/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.swt.graphics.Color;

public class StyleTemp {

	private final Color fgColor;
	private final Color labelFgColor;
	private final Color bgColor;
	private final boolean outline;
	private final boolean fill;

	public StyleTemp(final Color fgColor, final Color labelFgColor, final Color bgColor, final boolean outline,
			final boolean fill) {
		this.fgColor = fgColor;
		this.labelFgColor = labelFgColor;
		this.bgColor = bgColor;
		this.outline = outline;
		this.fill = fill;
	}

	public Color getForegroundColor() {
		return fgColor;
	}

	public Color getLabelForegroundColor() {
		return labelFgColor;
	}

	public Color getBackgroundColor() {
		return bgColor;
	}

	public boolean getOutline() {
		return outline;
	}

	public boolean getFill() {
		return fill;
	}
}
