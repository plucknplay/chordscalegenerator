/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.util.FontManager;

public class FretboardNotesFigure extends RoundedRectangle2 implements IFigureWithLabel {

	private String mode;
	private final Note note;
	private final FretboardPosition fbp;

	private final Label mainLabel;

	private boolean forceMinTextHeight;

	public FretboardNotesFigure(final boolean isLittleOne, final Note note, final FretboardPosition fbp) {

		this.note = note;
		this.fbp = fbp;

		useLocalCoordinates();
		final XYLayout contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);

		// main label
		mainLabel = new Label();
		mainLabel.setForegroundColor(ColorConstants.black);
		mainLabel.setTextPlacement(PositionConstants.CENTER);
		mainLabel.setTextAlignment(PositionConstants.CENTER);
		mainLabel.setLabelAlignment(PositionConstants.CENTER | PositionConstants.MIDDLE);
		add(mainLabel);
		contentsLayout.setConstraint(mainLabel, new Rectangle(0, 0, isLittleOne ? IFigureConstants.NOTES_HEIGHT
				: IFigureConstants.TWO_NOTES_WIDTH, IFigureConstants.NOTES_HEIGHT));
	}

	/**
	 * Sets the text of this note figure.
	 * 
	 * @param text
	 *            the text, must not be null
	 */
	public void setText(final String text, final String mode) {
		this.mode = mode;
		mainLabel.setText(text);
		updateLabelFonts();
	}

	private void updateLabelFonts() {
		final boolean useSmallFont = forceMinTextHeight
				|| Activator.getDefault().getPreferenceStore().getBoolean(Preferences.FRETBOARD_VIEW_FRAME_FINGERING);

		// main label
		final int maxTextHeight = useSmallFont ? IFigureConstants.MAX_FRETBOARD_NOTE_TEXT_HEIGHT
				: IFigureConstants.MAX_FRETBOARD_NOTE_TEXT_HEIGHT_WITHOUT_FRAME;

		if (mode == null || UIConstants.MODE_FINGERING.equals(mode)) {
			mainLabel.setFont(FontManager.getFont(null, mainLabel, "4", -1, maxTextHeight, SWT.BOLD)); //$NON-NLS-1$
		} else if (mainLabel.getText().length() <= 3) {
			mainLabel.setFont(FontManager.getFont(null, mainLabel, "bb4", -1, maxTextHeight, SWT.BOLD)); //$NON-NLS-1$
		} else if (UIConstants.MODE_NOTES.equals(mode)) {
			mainLabel.setFont(FontManager.getFont(null, mainLabel, "C#/Db", -1, maxTextHeight - 4, SWT.BOLD)); //$NON-NLS-1$
		} else {
			mainLabel.setFont(FontManager.getFont(null, mainLabel, "bb13", -1, maxTextHeight - 5, SWT.BOLD)); //$NON-NLS-1$
		}
	}

	public void setForceMinTextHeight(final boolean forceMinTextHeight) {
		this.forceMinTextHeight = forceMinTextHeight;
		updateLabelFonts();
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
		mainLabel.setForegroundColor(color);
	}

	public Color getLabelForegroundColor() {
		return mainLabel.getForegroundColor();
	}

	public Note getNote() {
		return note;
	}

	public FretboardPosition getFretboardPosition() {
		return fbp;
	}

	@Override
	public Label getMainLabel() {
		return mainLabel;
	}

	@Override
	public String getText() {
		return mainLabel.getText();
	}
}
