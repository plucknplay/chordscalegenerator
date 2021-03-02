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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.util.FontManager;
import com.plucknplay.csg.ui.util.enums.BoxViewFigureMode;
import com.plucknplay.csg.ui.util.enums.BoxViewPresentationMode;
import com.plucknplay.csg.ui.util.enums.FigureSizeMode;

public class BoxAssignmentFigure extends RoundedRectangle2 implements IFigureWithLabel {

	private final IPreferenceStore prefs;
	private final BoxViewPresentationMode presentationMode;

	private final BoxViewFigureMode mode;
	private String text;

	private final XYLayout contentsLayout;
	private final Label mainLabel;
	private Label topLabel;
	private Label bottomLabel;

	private boolean forceMinTextHeight;

	public BoxAssignmentFigure(final BoxViewFigureMode mode) {

		prefs = Activator.getDefault().getPreferenceStore();
		presentationMode = BoxViewPresentationMode.valueOf(prefs.getString(Preferences.BOX_VIEW_PRESENTATION_MODE));

		this.mode = mode;

		useLocalCoordinates();
		contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);

		setCornerDimensions(new Dimension(FigureSizeMode.LARGE.getSize(), FigureSizeMode.LARGE.getSize()));
		setForegroundColor(ColorConstants.black);

		// main label
		mainLabel = new Label();
		mainLabel.setLabelAlignment(PositionConstants.CENTER);
		add(mainLabel);

		if (presentationMode == BoxViewPresentationMode.VERTICAL) {
			// top label
			topLabel = new Label();
			topLabel.setLabelAlignment(PositionConstants.BOTTOM);
			add(topLabel);
			// bottom label
			bottomLabel = new Label();
			bottomLabel.setLabelAlignment(PositionConstants.TOP);
			add(bottomLabel);
		}
	}

	@Override
	public void setBounds(final Rectangle rect) {
		super.setBounds(rect);

		final int width = getSize().width;
		final int height = getSize().height;

		if (presentationMode == BoxViewPresentationMode.HORIZONTAL) {

			final Rectangle rectangle = new Rectangle(0, 0, width, height);
			mainLabel.setBounds(rectangle);
			contentsLayout.setConstraint(mainLabel, rectangle);

		} else {

			// main label
			final Rectangle rectangle = new Rectangle(0, 0, width, height);
			mainLabel.setBounds(rectangle);
			contentsLayout.setConstraint(mainLabel, rectangle);

			// top label
			final Rectangle topRectangle = new Rectangle(0, 3, width, height / 2);
			topLabel.setBounds(topRectangle);
			contentsLayout.setConstraint(topLabel, topRectangle);

			// bottom label
			final Rectangle bottomRectangle = new Rectangle(0, height / 2 - 3, width, height / 2);
			bottomLabel.setBounds(bottomRectangle);
			contentsLayout.setConstraint(bottomLabel, bottomRectangle);
		}
	}

	/**
	 * Sets the text of this note figure.
	 * 
	 * @param text
	 *            the text, must not be null
	 */
	public void setText(final String text) {
		this.text = text;

		updateAppearance();

		if (presentationMode == BoxViewPresentationMode.HORIZONTAL) {

			mainLabel.setText(text);

		} else {

			final String first = mode == BoxViewFigureMode.NOTE ? NamesUtil.getFirstNotePart(text)
					: mode == BoxViewFigureMode.INTERVAL ? NamesUtil.getFirstIntervalPart(text) : text;
			final String second = mode == BoxViewFigureMode.NOTE ? NamesUtil.getSecondNotePart(text)
					: mode == BoxViewFigureMode.INTERVAL ? NamesUtil.getSecondIntervalPart(text) : text;

			if (first.equals(second)) {
				mainLabel.setText(text);
				mainLabel.setVisible(true);
				topLabel.setText("");
				topLabel.setVisible(false);
				bottomLabel.setText("");
				bottomLabel.setVisible(false);
			} else {
				mainLabel.setText("");
				mainLabel.setVisible(false);
				topLabel.setText(first);
				topLabel.setVisible(true);
				bottomLabel.setText(second);
				bottomLabel.setVisible(true);
			}
		}

		updateLabelFonts();
	}

	private void updateLabelFonts() {
		final boolean useSmallFont = forceMinTextHeight || prefs.getBoolean(Preferences.BOX_VIEW_FRAME_INSIDE);
		final int maxTextWidth = getBounds().width - (useSmallFont ? 2 : 0);

		// main label
		final int maxTextHeight = useSmallFont ? IFigureConstants.BOX_ASSIGNMENT_MAX_TEXT_HEIGHT
				: IFigureConstants.BOX_ASSIGNMENT_MAX_TEXT_HEIGHT_WITHOUT_FRAME;
		if (mode == BoxViewFigureMode.FINGERING) {
			mainLabel.setFont(FontManager.getFont(null, mainLabel, "4", maxTextWidth, maxTextHeight, SWT.BOLD)); //$NON-NLS-1$
		} else if (mainLabel.getText().length() <= 3) {
			mainLabel.setFont(FontManager.getFont(null, mainLabel, "bb4", maxTextWidth, maxTextHeight, SWT.BOLD)); //$NON-NLS-1$
		} else if (mode == BoxViewFigureMode.NOTE) {
			mainLabel.setFont(FontManager.getFont(null, mainLabel, "C#/Db", maxTextWidth, maxTextHeight - 8, SWT.BOLD)); //$NON-NLS-1$
		} else {
			mainLabel.setFont(FontManager.getFont(null, mainLabel, "bb13", maxTextWidth, maxTextHeight - 5, SWT.BOLD)); //$NON-NLS-1$
		}

		// top & buttom label
		if (presentationMode == BoxViewPresentationMode.VERTICAL) {
			final Font font = FontManager.getFont(null, topLabel, "Gb", maxTextWidth, -1, SWT.BOLD); //$NON-NLS-1$
			if (mode != BoxViewFigureMode.FINGERING) {
				mainLabel.setFont(font);
			}
			topLabel.setFont(font);
			bottomLabel.setFont(font);
		}
	}

	private void updateAppearance() {
		final boolean hideCircle = !prefs.getBoolean(Preferences.BOX_VIEW_FRAME_INSIDE);
		setFill(!hideCircle);
		setOutline(!hideCircle);
	}

	public void setLabelForegroundColor(final Color fg) {
		mainLabel.setForegroundColor(fg);
		if (presentationMode == BoxViewPresentationMode.VERTICAL) {
			topLabel.setForegroundColor(fg);
			bottomLabel.setForegroundColor(fg);
		}
	}

	public void setForceMinTextHeight(final boolean forceMinTextHeight) {
		this.forceMinTextHeight = forceMinTextHeight;
		updateLabelFonts();
	}

	@Override
	public Label getMainLabel() {
		return mainLabel;
	}

	@Override
	public String getText() {
		return text;
	}
}
