/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.util.FontManager;

public class KeyboardNoteFigure extends RoundedRectangle2 implements INoteFigure, IFigureWithLabel {

	public static final Object SIZE_LITTLE = new Object();
	public static final Object SIZE_BIG = new Object();

	private final Label firstPartLabel;
	private Label secondPartLabel;

	private final Note note;
	private String text;

	/**
	 * The constructor.
	 * 
	 * @param size
	 *            the size, must not be null, must be on of the constants SIZE_*
	 * @param note
	 *            the corresponding note of this figure, must not be null
	 */
	public KeyboardNoteFigure(final Object size, final Note note) {
		if (size == null || note == null) {
			throw new IllegalArgumentException();
		}

		this.note = note;

		useLocalCoordinates();
		final XYLayout contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);

		// first part label
		firstPartLabel = new Label();
		firstPartLabel.setForegroundColor(ColorConstants.black);
		firstPartLabel.setFont(FontManager.getFont(null, firstPartLabel, null,
				IFigureConstants.MAX_KEYBOARD_NOTE_TEXT_WIDTH, -1, SWT.BOLD));
		firstPartLabel.setTextPlacement(PositionConstants.CENTER);
		firstPartLabel.setTextAlignment(PositionConstants.CENTER);
		firstPartLabel.setLabelAlignment(PositionConstants.CENTER | PositionConstants.MIDDLE);
		add(firstPartLabel);
		contentsLayout.setConstraint(firstPartLabel, new Rectangle(0, 0, IFigureConstants.KEYBOARD_NOTES_HEIGHT,
				IFigureConstants.KEYBOARD_NOTES_HEIGHT));

		// second part label
		if (size == SIZE_BIG) {
			secondPartLabel = new Label();
			secondPartLabel.setForegroundColor(ColorConstants.black);
			secondPartLabel.setFont(FontManager.getFont(null, firstPartLabel, null,
					IFigureConstants.MAX_KEYBOARD_NOTE_TEXT_WIDTH, -1, SWT.BOLD));
			secondPartLabel.setTextPlacement(PositionConstants.CENTER);
			secondPartLabel.setTextAlignment(PositionConstants.CENTER);
			secondPartLabel.setLabelAlignment(PositionConstants.CENTER | PositionConstants.MIDDLE);
			add(secondPartLabel);
			contentsLayout.setConstraint(secondPartLabel, new Rectangle(0, IFigureConstants.KEYBOARD_NOTES_HEIGHT / 2,
					IFigureConstants.KEYBOARD_NOTES_HEIGHT, IFigureConstants.KEYBOARD_NOTES_HEIGHT));
		}

		// first init
		setCornerDimensions(new Dimension(IFigureConstants.KEYBOARD_NOTES_HEIGHT,
				IFigureConstants.KEYBOARD_NOTES_HEIGHT));
		setForegroundColor(ColorConstants.black);
		setBackgroundColor(ColorConstants.white);
		setLineStyle(SWT.LINE_SOLID);
		setLineWidth(1);
		setText(note.getRelativeName(), UIConstants.MODE_NOTES);
	}

	/**
	 * Sets the text of this note figure.
	 * 
	 * @param text
	 *            the text, must not be null
	 * @param mode
	 *            the mode, UIConstants.MODE_*
	 */
	public void setText(final String text, final String mode) {
		this.text = text;

		// Points Mode
		if (UIConstants.MODE_POINTS.equals(mode)) {
			firstPartLabel.setText(""); //$NON-NLS-1$
			if (secondPartLabel != null) {
				secondPartLabel.setText(""); //$NON-NLS-1$
			}
		}

		// Notes or Intervals Mode
		else {

			final boolean isNotesMode = UIConstants.MODE_NOTES.equals(mode);
			final String firstPart = isNotesMode ? NamesUtil.getFirstNotePart(text) : NamesUtil
					.getFirstIntervalPart(text);
			final String secondPart = isNotesMode ? NamesUtil.getSecondNotePart(text) : NamesUtil
					.getSecondIntervalPart(text);

			// draw separated Strings .. set text of first and second part label
			if (!firstPart.equals(secondPart)) {
				firstPartLabel.setText(firstPart);
				if (secondPartLabel != null) {
					secondPartLabel.setText(secondPart);
				}
			}

			// set text of main label
			else {
				firstPartLabel.setText(text);
				if (secondPartLabel != null) {
					secondPartLabel.setText(""); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Sets the foreground color of the label of this note figure.
	 * 
	 * @param color
	 *            the new foreground color, must not be null
	 */
	public void setLabelForegroundColor(final Color color) {
		if (color == null) {
			throw new IllegalArgumentException();
		}

		firstPartLabel.setForegroundColor(color);
		if (secondPartLabel != null) {
			secondPartLabel.setForegroundColor(color);
		}
	}

	/**
	 * Returns the foreground color of the label(s) of this note figure.
	 * 
	 * @return the foreground color of the label(s)
	 */
	public Color getLabelForegroundColor() {
		return firstPartLabel.getForegroundColor();
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

	@Override
	public Label getMainLabel() {
		return firstPartLabel;
	}

	@Override
	public String getText() {
		return text;
	}
}
