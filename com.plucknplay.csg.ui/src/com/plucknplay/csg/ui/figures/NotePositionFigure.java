/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.NotePosition;
import com.plucknplay.csg.core.model.enums.Accidental;
import com.plucknplay.csg.ui.Activator;

public class NotePositionFigure extends Figure {

	private static final Image OPEN_NOTE_IMAGE;
	private static final Image OPEN_NOTE_IMAGE_BLUE;
	private static final Image CLOSED_NOTE_IMAGE;
	private static final Image CLOSED_NOTE_IMAGE_BLUE;

	private final NotePosition notePosition;

	private final XYLayout contentsLayout;
	private final ImageFigure imageFigure;

	static {
		final Activator activator = Activator.getDefault();
		OPEN_NOTE_IMAGE = activator.getImage(IFigureConstants.OPEN_NOTE_IMAGE);
		OPEN_NOTE_IMAGE_BLUE = activator.getImage(IFigureConstants.OPEN_NOTE_IMAGE_BLUE);
		CLOSED_NOTE_IMAGE = activator.getImage(IFigureConstants.CLOSED_NOTE_IMAGE);
		CLOSED_NOTE_IMAGE_BLUE = activator.getImage(IFigureConstants.CLOSED_NOTE_IMAGE_BLUE);
	}

	/**
	 * The constructor.
	 * 
	 * @param notePosition
	 *            the note position
	 * @param numberOfLedgerLines
	 *            the number of ledger lines needed by this note position
	 * @param isAboveStaff
	 *            <code>true</code> if the note position is above the staff,
	 *            <code>false</code> otherwise.<br/>
	 *            Note: <code>numberOfLedgerLines</code> > 0 and
	 *            <code>false</code> means note position is below staff.
	 * @param isOnTopOfLine
	 *            <code>true</code> if the note position sits on top of a staff
	 *            or ledger line, <code>false</code> otherwise
	 */
	public NotePositionFigure(final NotePosition notePosition, final int numberOfLedgerLines,
			final boolean isAboveStaff, final boolean isOnTopOfLine) {

		final boolean aboveStaff = isAboveStaff && numberOfLedgerLines > 0;
		final boolean belowStaff = !isAboveStaff && numberOfLedgerLines > 0;

		this.notePosition = notePosition;

		useLocalCoordinates();
		contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);

		int y = 0;

		// draw ledger lines below
		if (belowStaff) {
			for (int i = 0; i < numberOfLedgerLines; i++) {
				y += IFigureConstants.NOTE_LINE_DISTANCE;
				createLedgerLine(-1, y);
			}
			if (isOnTopOfLine) {
				y -= IFigureConstants.NOTE_LINE_DISTANCE / 2;
			}
		}

		// draw note
		imageFigure = new ImageFigure();
		add(imageFigure);
		final Rectangle imageBounds = new Rectangle(IFigureConstants.LEDGER_LINE_OFFSET / 2, y,
				IFigureConstants.NOTE_WIDTH, IFigureConstants.NOTE_LINE_DISTANCE);
		imageFigure.setBounds(imageBounds);
		contentsLayout.setConstraint(imageFigure, imageBounds);

		// add tooltip
		final Note note = notePosition.getNote();
		final Label tooltipLabel = new Label();
		tooltipLabel.setBackgroundColor(IFigureConstants.TOOLTIP_YELLOW);
		final String whiteSpace = " ";
		final String text = notePosition.getAccidental() == Accidental.SHARP ? whiteSpace + note.getAbsoluteNameAug()
				+ whiteSpace : whiteSpace + note.getAbsoluteNameDim() + whiteSpace;
		tooltipLabel.setText(text);
		imageFigure.setToolTip(tooltipLabel);

		// draw ledger lines above
		y += isOnTopOfLine ? IFigureConstants.NOTE_LINE_DISTANCE / 2 : IFigureConstants.NOTE_LINE_DISTANCE;
		if (aboveStaff) {
			for (int i = 1; i <= numberOfLedgerLines; i++) {
				createLedgerLine(0, y);
				y += IFigureConstants.NOTE_LINE_DISTANCE;
			}
		}
	}

	private void createLedgerLine(final int index, final int y) {

		// create ledger line
		final RectangleFigure ledgerLine = new RectangleFigure();
		ledgerLine.setForegroundColor(getForegroundColor());
		ledgerLine.setBackgroundColor(getBackgroundColor());
		ledgerLine.setLineStyle(SWT.LINE_SOLID);
		ledgerLine.setFill(true);

		// add ledger line
		add(ledgerLine, index);

		// set bounds
		final Rectangle lineBounds = new Rectangle(0, y, IFigureConstants.NOTE_WIDTH
				+ IFigureConstants.LEDGER_LINE_OFFSET, 1);
		ledgerLine.setBounds(lineBounds);
		contentsLayout.setConstraint(ledgerLine, lineBounds);
	}

	public NotePosition getNotePosition() {
		return notePosition;
	}

	public void setImage(final boolean drawHighlighted, final boolean openNotes, final boolean rootNote) {
		Image image = null;
		if (drawHighlighted) {
			image = openNotes && !rootNote || !openNotes && rootNote ? OPEN_NOTE_IMAGE_BLUE : CLOSED_NOTE_IMAGE_BLUE;
		} else {
			image = openNotes && !rootNote || !openNotes && rootNote ? OPEN_NOTE_IMAGE : CLOSED_NOTE_IMAGE;
		}
		imageFigure.setImage(image);
	}
}
