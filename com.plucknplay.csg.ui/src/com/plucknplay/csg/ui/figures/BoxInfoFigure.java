/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.model.BoxDraft;
import com.plucknplay.csg.ui.util.FontManager;
import com.plucknplay.csg.ui.util.enums.BackgroundColorMode;
import com.plucknplay.csg.ui.util.enums.BoxViewFigureMode;
import com.plucknplay.csg.ui.util.enums.BoxViewPresentationMode;

public class BoxInfoFigure extends RectangleFigure implements IFigureWithLabel {

	static final String HYPHEN = "-"; //$NON-NLS-1$

	private final IPreferenceStore prefs;
	private final BoxViewPresentationMode presentationMode;

	private final BoxViewFigureMode mode;
	private final BoxDraft boxDraft;

	private final XYLayout contentsLayout;
	private int width;
	private int height;

	private final Label mainLabel;
	private Label topLabel;
	private Label bottomLabel;

	private String text;

	public BoxInfoFigure(final BoxViewFigureMode mode, final BoxDraft boxDraft) {

		prefs = Activator.getDefault().getPreferenceStore();
		presentationMode = BoxViewPresentationMode.valueOf(prefs.getString(Preferences.BOX_VIEW_PRESENTATION_MODE));

		this.mode = mode;
		this.boxDraft = boxDraft;

		useLocalCoordinates();
		contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);
		setOutline(false);
		setFill(true);

		// main label
		mainLabel = new Label();
		add(mainLabel);

		if (presentationMode == BoxViewPresentationMode.VERTICAL) {
			// top label
			topLabel = new Label();
			add(topLabel);
			// bottom label
			bottomLabel = new Label();
			add(bottomLabel);
		}
		updateAppearance();
	}

	@Override
	public void setBounds(final Rectangle rect) {
		super.setBounds(rect);

		if (width == 0 || height == 0) {

			width = getSize().width;
			height = getSize().height;

			if (presentationMode == BoxViewPresentationMode.HORIZONTAL) {

				final Rectangle rectangle = new Rectangle(0, 0, width, height);
				mainLabel.setBounds(rectangle);

				final int offset = 10;
				if (width == IFigureConstants.BOX_VIEW_HORIZONTAL_INFO_WIDTH_SMALL + 1) {
					mainLabel.setFont(FontManager.getFont(null, mainLabel,
							"4", width - offset, height - offset, SWT.BOLD)); //$NON-NLS-1$
				} else if (width == IFigureConstants.BOX_VIEW_HORIZONTAL_INFO_WIDTH_MEDIUM + 1) {
					mainLabel.setFont(FontManager.getFont(null, mainLabel,
							"b13", width - offset, height - offset, SWT.BOLD)); //$NON-NLS-1$
				} else {
					mainLabel.setFont(FontManager.getFont(null, mainLabel,
							"G#/Ab", width - offset, height - offset, SWT.BOLD)); //$NON-NLS-1$
				}
				contentsLayout.setConstraint(mainLabel, rectangle);

			} else {

				final int offset = 6;

				// main label
				final Rectangle rectangle = new Rectangle(0, 0, width, height);
				mainLabel.setBounds(rectangle);
				mainLabel
						.setFont(FontManager.getFont(null, mainLabel, "Gb", width - offset, height - offset, SWT.BOLD)); //$NON-NLS-1$
				contentsLayout.setConstraint(mainLabel, rectangle);

				// top label
				final Rectangle topRectangle = new Rectangle(0, 2, width, height / 2);
				topLabel.setBounds(topRectangle);
				topLabel.setFont(FontManager.getFont(null, topLabel, "Gb", width - offset, height - offset, SWT.BOLD)); //$NON-NLS-1$
				contentsLayout.setConstraint(topLabel, topRectangle);

				// bottom label
				final Rectangle bottomRectangle = new Rectangle(0, height / 2 - 2, width, height / 2);
				bottomLabel.setBounds(bottomRectangle);
				bottomLabel.setFont(FontManager
						.getFont(null, topLabel, "Gb", width - offset, height - offset, SWT.BOLD)); //$NON-NLS-1$
				contentsLayout.setConstraint(bottomLabel, bottomRectangle);
			}
		}
	}

	public void updateText(final String text) {
		this.text = text;

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
				topLabel.setVisible(false);
				bottomLabel.setVisible(false);
			} else {
				topLabel.setText(first);
				bottomLabel.setText(second);
				mainLabel.setVisible(false);
				topLabel.setVisible(true);
				bottomLabel.setVisible(true);
			}
		}

		updateAppearance();
	}

	public void updateAppearance() {

		final boolean isFramed = prefs.getBoolean(Preferences.BOX_VIEW_FRAME_OUTSIDE);

		// update visibility
		final boolean visible = isFramed || !HYPHEN.equals(mainLabel.getText());
		mainLabel.setVisible(visible);
		if (presentationMode == BoxViewPresentationMode.VERTICAL) {
			topLabel.setVisible(visible);
			bottomLabel.setVisible(visible);
		}

		// update colors
		final BackgroundColorMode backgroundMode = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_BACKGROUND_OUTSIDE));
		final Color backgroundColor = !isFramed ? getWhite()
				: backgroundMode == BackgroundColorMode.BLACK ? ColorConstants.black
						: backgroundMode == BackgroundColorMode.WHITE ? getWhite() : mode.getColor();
		final Color foregroundColor = !isFramed ? ColorConstants.black
				: backgroundMode != BackgroundColorMode.BLACK ? ColorConstants.black : getWhite();

		// update appearance
		setBorder(isFramed ? new LineBorder() : null);
		setForegroundColor(foregroundColor);
		setBackgroundColor(backgroundColor);
		mainLabel.setForegroundColor(foregroundColor);
		if (presentationMode == BoxViewPresentationMode.VERTICAL) {
			topLabel.setForegroundColor(foregroundColor);
			bottomLabel.setForegroundColor(foregroundColor);
		}

		// update text alignment
		Rectangle topRectangle = new Rectangle(0, 2, width, height / 2);
		Rectangle bottomRectangle = new Rectangle(0, height / 2 - 2, width, height / 2);
		if (isFramed) {
			mainLabel.setLabelAlignment(PositionConstants.CENTER);
			if (presentationMode == BoxViewPresentationMode.VERTICAL) {
				topLabel.setLabelAlignment(PositionConstants.CENTER);
				bottomLabel.setLabelAlignment(PositionConstants.CENTER);
			}
		} else {
			final boolean isLeftHander = Activator.getDefault().isLeftHander();
			if (presentationMode == BoxViewPresentationMode.HORIZONTAL) {
				mainLabel.setLabelAlignment(isLeftHander ? PositionConstants.RIGHT : PositionConstants.LEFT);
			} else {
				mainLabel.setTextAlignment(PositionConstants.TOP);
				topLabel.setTextAlignment(PositionConstants.TOP);
				bottomLabel.setTextAlignment(PositionConstants.TOP);
				topRectangle = new Rectangle(0, 0, width, height / 2);
				bottomRectangle = new Rectangle(0, height / 2 - 4, width, height / 2);
			}
		}
		contentsLayout.setConstraint(topLabel, topRectangle);
		contentsLayout.setConstraint(bottomLabel, bottomRectangle);
	}

	private Color getWhite() {
		return boxDraft.isEditable() ? IFigureConstants.TOOLTIP_YELLOW : ColorConstants.white;
	}

	public BoxViewFigureMode getMode() {
		return mode;
	}

	@Override
	public Label getMainLabel() {
		return mainLabel;
	}

	@Override
	public String getText() {
		return text == null || text == HYPHEN ? "" : text; //$NON-NLS-1$
	}
}
