/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.SWT;

import com.plucknplay.csg.core.model.Note;

public class KeyFigure extends RectangleFigure implements INoteFigure {

	private final Note note;
	private final boolean isWhiteKey;

	public KeyFigure(final Note note, final boolean isWhiteKey) {
		this.note = note;
		this.isWhiteKey = isWhiteKey;

		this.setForegroundColor(ColorConstants.black);
		this.setLineStyle(SWT.LINE_SOLID);
	}

	public void resetBackgroundColor(final boolean isEditable) {
		this.setBackgroundColor(!isWhiteKey ? ColorConstants.black : isEditable ? IFigureConstants.TOOLTIP_YELLOW
				: ColorConstants.white);
	}

	public void setBlockColor() {
		this.setBackgroundColor(isWhiteKey ? IFigureConstants.LIGHT_BLUE : IFigureConstants.DARK_BLUE);
	}

	/**
	 * Returns the corresponding note of this figure.
	 * 
	 * @return the corresponding note of this figure
	 */
	@Override
	public Note getNote() {
		return note;
	}

	/**
	 * Returns true if this key figure represents a white key, or false
	 * otherwise.
	 * 
	 * @return true if this key figure represents a white key, or false
	 *         otherwise
	 */
	public boolean isWhiteKey() {
		return isWhiteKey;
	}
}
