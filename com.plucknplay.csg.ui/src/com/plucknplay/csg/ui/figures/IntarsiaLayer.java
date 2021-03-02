/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.FretboardDraft;
import com.plucknplay.csg.ui.util.enums.Position;

public class IntarsiaLayer extends AbstractFretboardLayer {

	public IntarsiaLayer(final FretboardDraft fretboardDraft) {
		super(fretboardDraft);
	}

	@Override
	public void paintFigure(final Graphics g) {
		if (getCurrentInstrument() == null) {
			return;
		}

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final Position position = Position.valueOf(prefs.getString(Preferences.FRETBOARD_VIEW_INLAYS_POSITION));
		final String shape = prefs.getString(Preferences.FRETBOARD_VIEW_INLAYS_SHAPE);

		final int x = IFigureConstants.FRET_WIDTH / 2 - IFigureConstants.INTARSIA_RADIUS;
		final int diameter = IFigureConstants.INTARSIA_RADIUS * 2;
		final int fretboardHeight = IFigureConstants.FRET_HEIGHT * (getCurrentInstrument().getStringCount() - 1);

		// Top Positon
		int y0 = 30;
		int y1 = 15;
		int y2 = y0;

		// Bottom Position
		if (position == Position.BOTTOM) {
			final int offset = getOffsetY() + fretboardHeight;
			y0 = offset + 25;
			y1 = offset + 40;
			y2 = y0;
		}

		// Center Position
		else if (position == Position.CENTER) {

			y0 = getOffsetY() + fretboardHeight / 2 - IFigureConstants.INTARSIA_RADIUS;
			y1 = y0;
			y2 = y0;

			final int stringCount = getCurrentInstrument().getStringCount();
			if (stringCount > 2) {

				// even number of strings
				if (stringCount % 2 == 0) {
					y1 -= IFigureConstants.FRET_HEIGHT;
					y2 += IFigureConstants.FRET_HEIGHT;
					if (stringCount > 6) {
						y1 -= IFigureConstants.FRET_HEIGHT;
						y2 += IFigureConstants.FRET_HEIGHT;
					}
				}

				// odd number of strings
				else {
					y1 -= IFigureConstants.FRET_HEIGHT / 2;
					y2 += IFigureConstants.FRET_HEIGHT / 2;
					if (stringCount > 3) {
						y1 -= IFigureConstants.FRET_HEIGHT;
						y2 += IFigureConstants.FRET_HEIGHT;
					}
				}
			}
		}

		final Color color = prefs.getBoolean(Preferences.FRETBOARD_VIEW_INLAYS_GRAY_COLOR) ? IFigureConstants.DARK_GREY
				: ColorConstants.black;

		g.setForegroundColor(ColorConstants.white);
		g.setBackgroundColor(color);

		for (int i = 1; i <= getCurrentInstrument().getFretCount(); i++) {

			final boolean leftHander = Activator.getDefault().isLeftHander();
			final int fret = !leftHander ? i : getCurrentInstrument().getFretCount() - i + 1;

			if (getInlayFrets().contains(fret)) {

				// draw circles
				if (UIConstants.INLAYS_SHAPE_CIRCLE.equals(shape)) {
					if (fret != 12 && fret != 24) {
						g.fillOval(x + getXFretPosition(fret), y0, diameter, diameter);
					} else {
						g.fillOval(x + getXFretPosition(fret), y1, diameter, diameter);
						g.fillOval(x + getXFretPosition(fret), y2, diameter, diameter);
					}
				}

				// draw triangle
				else {
					final int xt1 = getXFretPosition(fret)
							+ (fret != 12 && fret != 24 ? IFigureConstants.FRET_WIDTH / 2
									: leftHander ? IFigureConstants.FRET_WIDTH - 10 : 10);
					final int xt2 = getXFretPosition(fret) + (leftHander ? 5 : IFigureConstants.FRET_WIDTH - 5);
					final int yt1 = getOffsetY() + 5;
					final int yt2 = getOffsetY() + fretboardHeight - 5;

					g.fillPolygon(new int[] { xt1, yt2, xt2, yt2, xt2, yt1 });
				}
			}
		}
	}
}
