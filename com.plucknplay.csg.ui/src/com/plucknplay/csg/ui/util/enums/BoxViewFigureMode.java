/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util.enums;

import org.eclipse.swt.graphics.Color;

import com.plucknplay.csg.ui.figures.IFigureConstants;

public enum BoxViewFigureMode {

	FINGERING(IFigureConstants.RED), NOTE(IFigureConstants.GREEN), INTERVAL(IFigureConstants.YELLOW);

	private Color color;

	private BoxViewFigureMode(final Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
}
