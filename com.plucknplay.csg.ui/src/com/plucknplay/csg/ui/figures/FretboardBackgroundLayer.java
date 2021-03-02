/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.FretboardDraft;

public class FretboardBackgroundLayer extends AbstractFretboardLayer {

	public FretboardBackgroundLayer(final FretboardDraft fretboardDraft) {
		super(fretboardDraft);
	}

	@Override
	public void paintFigure(final Graphics g) {
		if (getCurrentInstrument() == null) {
			return;
		}

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final String backgroundMode = prefs.getString(Preferences.FRETBOARD_VIEW_BACKGROUND_MODE);
		final int backgroundIndex = prefs.getInt(Preferences.FRETBOARD_VIEW_BACKGROUND_INDEX);

		if (UIConstants.NO_BACKGROUND.equals(backgroundMode)) {
			return;
		}

		final int offsetY = getCurrentInstrument().hasDoubledStrings() ? IFigureConstants.DOUBLED_STRINGS_DISTANCE / 2
				: 0;
		final int width = IFigureConstants.FRET_WIDTH * getCurrentInstrument().getFretCount();
		final int height = IFigureConstants.FRET_HEIGHT * (getCurrentInstrument().getStringCount() - 1) + 2 * offsetY;

		int offsetX = IFigureConstants.FRETBOARD_OFFSET_X;
		if (Activator.getDefault().getPreferenceStore().getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE)
				|| !Activator.getDefault().isLeftHander()) {
			offsetX += IFigureConstants.FRET_WIDTH;
		}

		if (UIConstants.BACKGROUND_COLOR.equals(backgroundMode)) {

			g.setBackgroundColor(IFigureConstants.FRETBOARD_COLORS[backgroundIndex]);
			g.fillRectangle(offsetX, getOffsetY() - offsetY, width, height);

		} else if (UIConstants.BACKGROUND_IMAGE.equals(backgroundMode)) {

			final Image image = Activator.getDefault().getImage(IImageKeys.WOOD_PATTERN[backgroundIndex]);
			g.drawImage(image, 0, 0, width, height, offsetX, getOffsetY() - offsetY, width, height);

		}
	}
}
