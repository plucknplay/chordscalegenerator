/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util.enums;

import com.plucknplay.csg.ui.figures.IFigureConstants;

public enum FigureSizeMode {

	SMALL(IFigureConstants.BOX_POINT_WIDTH_SMALL), MEDIUM(IFigureConstants.BOX_POINT_WIDTH_MEDIUM), LARGE(
			IFigureConstants.BOX_POINT_WIDTH_LARGE), SAME(0);

	private int size;

	private FigureSizeMode(final int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
}
