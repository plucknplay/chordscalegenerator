/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util.enums;

import org.eclipse.swt.graphics.Point;

import com.plucknplay.csg.ui.figures.IFigureConstants;

public enum KeySizeMode {

	SMALL(IFigureConstants.WHITE_KEY_HEIGHT_SMALL, IFigureConstants.BLACK_KEY_HEIGHT_SMALL), MEDIUM(
			IFigureConstants.WHITE_KEY_HEIGHT_MEDIUM, IFigureConstants.BLACK_KEY_HEIGHT_MEDIUM), LARGE(
			IFigureConstants.WHITE_KEY_HEIGHT_LARGE, IFigureConstants.BLACK_KEY_HEIGHT_LARGE), FLEXIBLE(0, 0);

	private Point containerSize;
	private int whiteKeyHeight;
	private int blackKeyHeight;

	private KeySizeMode(final int whiteKeyHeight, final int blackKeyHeight) {
		this.whiteKeyHeight = whiteKeyHeight;
		this.blackKeyHeight = blackKeyHeight;
	}

	private int getWidthOffset() {
		return 2 * IFigureConstants.KEYBOARD_OFFSET_X;
	}

	public int getNormWidth(final int keyNumber) {
		return IFigureConstants.WHITE_KEY_WIDTH * keyNumber + getWidthOffset();
	}

	private int getHeightOffset() {
		return 2 * IFigureConstants.KEYBOARD_OFFSET_Y;
	}

	public int getNormHeight(final int keyNumber) {
		return getHeightOffset() + getWhiteKeyHeight(keyNumber);
	}

	public int getWhiteKeyHeight(final int keyNumber) {

		if (whiteKeyHeight > 0) {
			return whiteKeyHeight;
		}
		if (containerSize == null) {
			return IFigureConstants.WHITE_KEY_HEIGHT_LARGE;
		}

		final double normHeightMax = IFigureConstants.WHITE_KEY_HEIGHT_LARGE + getHeightOffset();
		final double widthRelation = containerSize.x * 1.0d / getNormWidth(keyNumber);
		final double heightRelationMax = containerSize.y * 1.0d / normHeightMax;
		final double scale = Math.max(widthRelation, heightRelationMax);

		whiteKeyHeight = (int) (containerSize.y / Math.max(IFigureConstants.KEYBOARD_MIN_SCALE_FACTOR, scale)) - 2
				* IFigureConstants.KEYBOARD_OFFSET_Y;
		whiteKeyHeight = Math.min(IFigureConstants.WHITE_KEY_HEIGHT_LARGE,
				Math.max(whiteKeyHeight, IFigureConstants.WHITE_KEY_HEIGHT_SMALL));
		return whiteKeyHeight;
	}

	public int getBlackKeyHeight(final int keyNumber) {
		if (blackKeyHeight > 0) {
			return blackKeyHeight;
		}
		if (containerSize == null) {
			return IFigureConstants.BLACK_KEY_HEIGHT_LARGE;
		}

		blackKeyHeight = getWhiteKeyHeight(keyNumber) * IFigureConstants.BLACK_KEY_HEIGHT_LARGE
				/ IFigureConstants.WHITE_KEY_HEIGHT_LARGE;
		return blackKeyHeight;
	}

	public void setContainerSize(final Point size) {
		containerSize = size;
		if (this == FLEXIBLE) {
			whiteKeyHeight = 0;
			blackKeyHeight = 0;
		}
	}
}
