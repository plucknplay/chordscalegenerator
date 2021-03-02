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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.FretboardDraft;
import com.plucknplay.csg.ui.util.FontManager;
import com.plucknplay.csg.ui.util.enums.Position;

public class FretNumbersLayer extends AbstractFretboardLayer {

	private final XYLayout contentsLayout;

	public FretNumbersLayer(final FretboardDraft fretboardDraft) {
		super(fretboardDraft);
		contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);
		initFretNumbers();
	}

	public void initFretNumbers() {
		removeAll();

		if (getCurrentInstrument() == null) {
			return;
		}

		final boolean isLeftHander = Activator.getDefault().isLeftHander();
		final int fretCount = getCurrentInstrument().getFretCount();
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean useRomanNumerals = prefs.getString(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_NUMERALS).equals(
				UIConstants.NUMERALS_MODE_ROMAN);
		final boolean reducedMode = prefs.getBoolean(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_REDUCED_MODE);
		final boolean grayColor = prefs.getBoolean(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_GRAY_COLOR);
		final boolean bottomPosition = Position.BOTTOM == Position.valueOf(prefs
				.getString(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_POSITION));

		int start = 0;
		int counterMax = fretCount;
		if (prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE)) {
			counterMax += 1;
		} else if (isLeftHander) {
			start = 1;
			counterMax += 1;
		}

		int x = IFigureConstants.FRETBOARD_OFFSET_X;
		final int y0 = getOffsetY() + IFigureConstants.FRET_HEIGHT * (getCurrentInstrument().getStringCount() - 1);
		final int y = bottomPosition ? y0 + 15 : 20;

		for (int i = start; i <= counterMax; i++) {

			if (!reducedMode || getInlayFrets().contains(i) && !isLeftHander
					|| getInlayFrets().contains(fretCount - i + 1) && isLeftHander) {

				String text = i > fretCount ? "0" : useRomanNumerals ? UIConstants.ROMAN_NUMERALS[i] : "" + i; //$NON-NLS-1$ //$NON-NLS-2$
				if (isLeftHander && i > 0 && i <= fretCount) {
					final int fret = fretCount - i + 1;
					text = useRomanNumerals ? UIConstants.ROMAN_NUMERALS[fret] : "" + fret; //$NON-NLS-1$
				}
				if (reducedMode && "0".equals(text)) {
					text = "";
				}

				final Label fretNumberLabel = new Label(text);
				fretNumberLabel.setForegroundColor(grayColor ? IFigureConstants.DARK_GREY : ColorConstants.black);
				fretNumberLabel.setFont(FontManager.getFont(null, fretNumberLabel,
						"24", IFigureConstants.MAX_FRETBOARD_FRETNUMBER_TEXT_WIDTH, -1, SWT.BOLD)); //$NON-NLS-1$
				fretNumberLabel.setTextAlignment(PositionConstants.CENTER);
				add(fretNumberLabel);
				contentsLayout.setConstraint(fretNumberLabel, new Rectangle(x, y, IFigureConstants.FRET_WIDTH,
						IFigureConstants.FRET_HEIGHT));

			}
			x += IFigureConstants.FRET_WIDTH;
		}
	}
}
