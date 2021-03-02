/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.model.BoxDraft;
import com.plucknplay.csg.ui.util.enums.BoxViewPresentationMode;

public class BoxLayer extends AbstractBoxLayer {

	private static final int DOUBLED_STRINGS_DISTANCE = 4;

	public BoxLayer(final BoxDraft boxDraft, final boolean showFingering, final boolean showFingeringOutside,
			final boolean showNotes, final boolean showNotesOutside, final boolean showIntervals,
			final boolean showIntervalsOutside) {
		super(boxDraft, showFingering, showFingeringOutside, showNotes, showNotesOutside, showIntervals,
				showIntervalsOutside);

		setAntiAliasing(false);
	}

	@Override
	public void paintFigure(final Graphics g) {
		if (getCurrentInstrument() == null) {
			return;
		}

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();

		final BoxViewPresentationMode presentationMode = BoxViewPresentationMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_PRESENTATION_MODE));
		final boolean grayColor = prefs.getBoolean(Preferences.BOX_VIEW_FRAME_GRAY_COLOR);
		final boolean drawFretless = prefs.getBoolean(Preferences.BOX_VIEW_FRAME_DRAW_FRETLESS_FRETS_DOTTED);
		final boolean drawDoubled = prefs.getBoolean(Preferences.BOX_VIEW_FRAME_DRAW_DOUBLED_STRINGS);
		final boolean highlightNut = prefs.getBoolean(Preferences.BOX_VIEW_FRAME_HIGHLIGHT_NUT)
				&& getBoxDraft().getStartFret() == 1;
		final boolean highlightOuterFrets = prefs.getBoolean(Preferences.BOX_VIEW_FRAME_HIGHLIGHT_OUTER_FRETS);

		final boolean isFretless = getCurrentInstrument().isFretless() && drawFretless;
		final boolean isDoubled = getCurrentInstrument().hasDoubledStrings() && drawDoubled;
		final boolean isLeftHander = Activator.getDefault().isLeftHander();
		final int stringCount = getCurrentInstrument().getStringCount();

		g.setLineWidth(1);
		g.setLineCap(SWT.CAP_FLAT);
		g.setLineStyle(SWT.LINE_SOLID);
		g.setBackgroundColor(IFigureConstants.LIGHT_GREY); // outer frets
		g.setForegroundColor(grayColor ? IFigureConstants.DARK_GREY : ColorConstants.black);

		final Point boxPosition = presentationMode.getBoxPosition(getBoxDraft(), getShowFingeringInside(),
				getShowFingeringOutside(), getShowNotesOutside(), getShowNotesInside(), getShowIntervalsOutside(),
				getShowIntervalsInside());
		final int fretWidth = presentationMode.getFretWidth(getShowNotesInside(), getShowIntervalsInside());
		final int fretHeight = presentationMode.getFretHeight(getShowNotesInside(), getShowIntervalsInside());

		int x1 = boxPosition.x;
		int x2 = boxPosition.x;
		int y1 = boxPosition.y;
		int y2 = boxPosition.y;

		final int numberOfOuterFrets = getBoxDraft().getStartFret() + getBoxDraft().getFretWidth()
				- getCurrentInstrument().getFretCount() - 1;
		final int offset = isDoubled ? 2 : 0;

		// (1) HORIZONTAL mode
		if (presentationMode == BoxViewPresentationMode.HORIZONTAL) {

			// (1.0) highlight outer frets
			if (highlightOuterFrets && numberOfOuterFrets > 0) {
				g.fillRectangle(x1 + (getBoxDraft().getFretWidth() - numberOfOuterFrets) * fretWidth, y1 - offset,
						numberOfOuterFrets * fretWidth + 1, fretHeight * (getCurrentInstrument().getStringCount() - 1)
								+ 2 * offset + 1);
			}
			g.setBackgroundColor(grayColor ? IFigureConstants.DARK_GREY : ColorConstants.black);

			// (1.1) draw FRETS
			y2 = y1 + fretHeight * (stringCount - 1);
			if (isDoubled) {
				y1 -= 2;
				y2 += 2;
			}

			// (1.1.1) draw nut / last fret
			g.setLineStyle(SWT.LINE_SOLID);
			drawVerticalFret(g, highlightNut && !isLeftHander, x1, y1, y2);

			// (1.1.2) draw frets
			g.setLineStyle(isFretless ? SWT.LINE_DOT : SWT.LINE_SOLID);
			for (int i = 0; i < getBoxDraft().getFretWidth() - 1; i++) {
				x1 += fretWidth;
				drawVerticalFret(g, false, x1, y1, y2);
			}

			// (1.1.3) draw last fret / nut
			x1 += fretWidth;
			g.setLineStyle(SWT.LINE_SOLID);
			drawVerticalFret(g, highlightNut && isLeftHander, x1, y1, y2);

			// (1.2) draw STRINGS
			x1 = boxPosition.x;
			x2 = x1 + getBoxDraft().getFretWidth() * fretWidth;
			if (highlightNut) {
				if (isLeftHander) {
					x2++;
				} else {
					x1--;
				}
			}

			for (int i = 0; i < stringCount; i++) {
				g.setLineStyle(SWT.LINE_SOLID);
				g.drawLine(x1, y1, x2, y1);
				if (isDoubled) {
					g.drawLine(x1, y1 + DOUBLED_STRINGS_DISTANCE, x2, y1 + DOUBLED_STRINGS_DISTANCE);
				}
				y1 += fretHeight;
			}
		}

		// (2) VERTICAL mode
		else {

			// (2.0) highlight outer frets
			if (highlightOuterFrets && numberOfOuterFrets > 0) {
				g.fillRectangle(x1 - offset, y1 + (getBoxDraft().getFretWidth() - numberOfOuterFrets) * fretHeight,
						fretWidth * (getCurrentInstrument().getStringCount() - 1) + 2 * offset + 1, numberOfOuterFrets
								* fretHeight + 1);
			}
			g.setBackgroundColor(grayColor ? IFigureConstants.DARK_GREY : ColorConstants.black);

			// (2.1) draw FRETS
			x1 = boxPosition.x;
			x2 = x1 + fretWidth * (stringCount - 1);
			y1 = boxPosition.y;
			if (isDoubled) {
				x1 -= 2;
				x2 += 2;
			}

			// (2.1.1) draw nut
			g.setLineStyle(SWT.LINE_SOLID);
			drawHorizontalFret(g, highlightNut, x1, x2, y1);

			// (2.1.2) draw frets
			g.setLineStyle(isFretless ? SWT.LINE_DOT : SWT.LINE_SOLID);
			for (int i = 0; i < getBoxDraft().getFretWidth() - 1; i++) {
				y1 += fretHeight;
				drawHorizontalFret(g, false, x1, x2, y1);
			}

			// (2.1.3) draw last fret
			y1 += fretHeight;
			g.setLineStyle(SWT.LINE_SOLID);
			drawHorizontalFret(g, false, x1, x2, y1);

			// (2.2) draw STRINGS
			y1 = boxPosition.y;
			y2 = y1 + getBoxDraft().getFretWidth() * fretHeight;
			if (highlightNut) {
				y1--;
			}

			for (int i = 0; i < stringCount; i++) {
				g.setLineStyle(SWT.LINE_SOLID);
				g.drawLine(x1, y1, x1, y2);
				if (isDoubled) {
					g.drawLine(x1 + DOUBLED_STRINGS_DISTANCE, y1, x1 + DOUBLED_STRINGS_DISTANCE, y2);
				}
				x1 += fretWidth;
			}
		}
	}

	private void drawVerticalFret(final Graphics g, final boolean highlightNut, final int x, final int y1, final int y2) {
		if (highlightNut) {
			g.fillRectangle(x - 1, y1, 3, y2 - y1 + 1);
		} else {
			g.drawLine(x, y1, x, y2);
		}
	}

	private void drawHorizontalFret(final Graphics g, final boolean highlightNut, final int x1, final int x2,
			final int y) {
		if (highlightNut) {
			g.fillRectangle(x1, y - 1, x2 - x1 + 1, 3);
		} else {
			g.drawLine(x1, y, x2, y);
		}
	}
}
