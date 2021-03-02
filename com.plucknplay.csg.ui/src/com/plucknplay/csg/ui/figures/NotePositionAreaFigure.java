/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.NotePosition;
import com.plucknplay.csg.core.model.enums.Accidental;

public class NotePositionAreaFigure extends RectangleFigure {

	private final NotePosition notePosition;

	public NotePositionAreaFigure(final NotePosition notePosition, final boolean isEditable) {
		this.notePosition = notePosition;

		setForegroundColor(ColorConstants.white);
		setBackgroundColor(IFigureConstants.TOOLTIP_YELLOW_DARK);
		setOutline(false);
		setFill(isEditable);

		// add tooltip
		final Note note = notePosition.getNote();
		final Label tooltipLabel = new Label();
		tooltipLabel.setBackgroundColor(IFigureConstants.TOOLTIP_YELLOW);
		final String whiteSpace = " ";
		final String text = notePosition.getAccidental() == Accidental.SHARP ? whiteSpace + note.getAbsoluteNameAug()
				+ whiteSpace : whiteSpace + note.getAbsoluteNameDim() + whiteSpace;
		tooltipLabel.setText(text);
		setToolTip(tooltipLabel);
	}

	public NotePosition getNotePosition() {
		return notePosition;
	}
}
