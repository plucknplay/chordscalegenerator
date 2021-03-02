/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.draw2d.Label;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.figures.IFigureConstants;

public final class TooltipUtil {

	private static final String WHITE_SPACE = " "; //$NON-NLS-1$

	private TooltipUtil() {
	}

	/**
	 * Sets the tooltip of the given note figure with the absolute name of the
	 * given note.
	 * 
	 * @param absoluteNotes
	 *            <code>true</code> if absolute note names shall be shown,
	 *            <code>false</code> otherwise
	 */
	public static Label getToolTipLabel(final Note note, final boolean absoluteNotes) {

		// determine tooltip text
		final StringBuffer buf = new StringBuffer();
		buf.append(WHITE_SPACE);
		buf.append(absoluteNotes ? note.getAbsoluteName() : note.getRelativeName());
		buf.append(WHITE_SPACE);

		// create tooltip label
		final Label tooltipLabel = new Label();
		tooltipLabel.setBackgroundColor(IFigureConstants.TOOLTIP_YELLOW);
		tooltipLabel.setText(buf.toString());

		return tooltipLabel;
	}
}
