/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.NotePosition;
import com.plucknplay.csg.core.model.enums.Accidental;
import com.plucknplay.csg.ui.Activator;

public class AccidentalFigure extends Figure {

	private static final Image SHARP_SIGN_IMAGE;
	private static final Image FLAT_SIGN_IMAGE;
	private static final Image SHARP_SIGN_IMAGE_BLUE;
	private static final Image FLAT_SIGN_IMAGE_BLUE;

	private final NotePosition notePosition;

	static {
		final Activator activator = Activator.getDefault();
		SHARP_SIGN_IMAGE = activator.getImage(IFigureConstants.SHARP_SIGN_IMAGE);
		FLAT_SIGN_IMAGE = activator.getImage(IFigureConstants.FLAT_SIGN_IMAGE);
		SHARP_SIGN_IMAGE_BLUE = activator.getImage(IFigureConstants.SHARP_SIGN_IMAGE_BLUE);
		FLAT_SIGN_IMAGE_BLUE = activator.getImage(IFigureConstants.FLAT_SIGN_IMAGE_BLUE);
	}

	public AccidentalFigure(final NotePosition notePosition, final boolean drawHighlight) {

		this.notePosition = notePosition;

		useLocalCoordinates();
		final XYLayout contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);

		final ImageFigure imageFigure = new ImageFigure();

		if (notePosition.getAccidental() == Accidental.FLAT) {
			imageFigure.setImage(drawHighlight ? FLAT_SIGN_IMAGE_BLUE : FLAT_SIGN_IMAGE);
		}
		if (notePosition.getAccidental() == Accidental.SHARP) {
			imageFigure.setImage(drawHighlight ? SHARP_SIGN_IMAGE_BLUE : SHARP_SIGN_IMAGE);
		}

		add(imageFigure);
		final Rectangle rectangle = new Rectangle(0, 0, IFigureConstants.ACCIDENTAL_IMAGE_WIDTH,
				IFigureConstants.ACCIDENTAL_IMAGE_HEIGHT);
		imageFigure.setBounds(rectangle);
		contentsLayout.setConstraint(imageFigure, rectangle);

		// add tooltip
		final Note note = notePosition.getNote();
		final Label tooltipLabel = new Label();
		tooltipLabel.setBackgroundColor(IFigureConstants.TOOLTIP_YELLOW);
		final String whiteSpace = " ";
		final String text = notePosition.getAccidental() == Accidental.SHARP ? whiteSpace + note.getAbsoluteNameAug()
				+ whiteSpace : whiteSpace + note.getAbsoluteNameDim() + whiteSpace;
		tooltipLabel.setText(text);
		imageFigure.setToolTip(tooltipLabel);
	}

	public NotePosition getNotePosition() {
		return notePosition;
	}
}
