/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.model.FretboardDraft;

public class FretboardLayer extends AbstractFretboardLayer {

	private static final Font FONT = new Font(null, "Arial", 16, SWT.NONE); //$NON-NLS-1$

	private boolean isLeftHander;

	private final IPreferenceStore prefs;

	public FretboardLayer(final FretboardDraft fretboardDraft) {
		super(fretboardDraft);
		setAntiAliasing(false);
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void paintFigure(final Graphics g) {
		if (getCurrentInstrument() == null) {
			return;
		}

		isLeftHander = Activator.getDefault().isLeftHander();

		g.setAlpha(128);
		paintIllegalArea(g);

		g.setAlpha(255);
		paintFrets(g);
		paintStrings(g);
		paintCapotasto(g);
	}

	/**
	 * Paints illegal area. Illegal area is all the space behind the capotasto.
	 * 
	 * @param g
	 *            the Graphics object used for painting
	 */
	private void paintIllegalArea(final Graphics g) {
		if (getCurrentInstrument().getMinFret() == 0) {
			return;
		}

		g.setBackgroundColor(IFigureConstants.GREY);

		// left hander
		if (isLeftHander) {
			int factor = getCurrentInstrument().getFretCount() - getCurrentInstrument().getMinFret();
			if (prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE)) {
				factor += 1;
			}
			g.fillRectangle(IFigureConstants.FRETBOARD_OFFSET_X + factor * IFigureConstants.FRET_WIDTH, getOffsetY(),
					IFigureConstants.FRET_WIDTH * getCurrentInstrument().getMinFret(), IFigureConstants.FRET_HEIGHT
							* (getCurrentInstrument().getStringCount() - 1));
		}
		// right hander
		else {
			g.fillRectangle(IFigureConstants.FRETBOARD_OFFSET_X + IFigureConstants.FRET_WIDTH, getOffsetY(),
					IFigureConstants.FRET_WIDTH * getCurrentInstrument().getMinFret(), IFigureConstants.FRET_HEIGHT
							* (getCurrentInstrument().getStringCount() - 1));
		}
	}

	/**
	 * Paints all frets inclusive the neck.
	 * 
	 * @param g
	 *            the Graphics object used for painting
	 */
	private void paintFrets(final Graphics g) {

		final boolean isFretless = getCurrentInstrument().isFretless();
		final boolean showEmptyStringsTwice = prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE);
		final boolean showEmptyStringsFrame = prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_FRAME);

		int x = IFigureConstants.FRETBOARD_OFFSET_X;
		int y0 = getOffsetY();
		if (getCurrentInstrument().hasDoubledStrings()) {
			y0 -= 2;
		}
		int y1 = y0 + IFigureConstants.FRET_HEIGHT * (getCurrentInstrument().getStringCount() - 1);
		if (getCurrentInstrument().hasDoubledStrings()) {
			y1 += 4;
		}

		g.setForegroundColor(ColorConstants.black);

		// draw dotted empty string fret
		if (showEmptyStringsTwice || !isLeftHander) {
			if (showEmptyStringsFrame) {
				g.setLineWidth(1);
				g.setLineStyle(SWT.LINE_DOT);
				g.setLineCap(SWT.CAP_FLAT);
				g.drawLine(x, y0, x, y1);
			}
			x += IFigureConstants.FRET_WIDTH;
		}

		// draw neck / first fret
		g.setLineWidth(isLeftHander ? 2 : 5);
		g.setLineStyle(SWT.LINE_SOLID);
		g.setLineCap(SWT.CAP_ROUND);
		g.drawLine(x, y0, x, y1);

		// draw frets
		x += IFigureConstants.FRET_WIDTH;
		g.setLineDash(new int[] { 1, 4 });
		g.setLineStyle(isFretless ? SWT.LINE_CUSTOM : SWT.LINE_SOLID);
		g.setLineWidth(2);

		for (int i = 0; i < getCurrentInstrument().getFretCount() - 1; i++) {
			g.drawLine(x, y0, x, y1);
			x += IFigureConstants.FRET_WIDTH;
		}

		// last fret / draw neck
		g.setLineWidth(isLeftHander ? 5 : 2);
		g.setLineStyle(SWT.LINE_SOLID);
		g.drawLine(x, y0, x, y1);
		x += IFigureConstants.FRET_WIDTH;

		// draw last dotted empty string fret
		if (showEmptyStringsFrame && (showEmptyStringsTwice || isLeftHander)) {
			g.setLineWidth(1);
			g.setLineStyle(SWT.LINE_DOT);
			g.setLineCap(SWT.CAP_FLAT);
			g.drawLine(x, y0, x, y1);
		}
	}

	/**
	 * Paints all strings.
	 * 
	 * @param g
	 *            the Graphics object used for painting
	 */
	private void paintStrings(final Graphics g) {

		final boolean showEmptyStringsTwice = prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE);
		final boolean showEmptyStringsFrame = prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_FRAME);

		int y = getOffsetY();
		if (getCurrentInstrument().hasDoubledStrings()) {
			y -= 2;
		}

		final int x0 = IFigureConstants.FRETBOARD_OFFSET_X;
		int x1 = x0;
		if (showEmptyStringsTwice || !isLeftHander) {
			x1 = x0 + IFigureConstants.FRET_WIDTH;
		}
		final int x2 = x1 + getCurrentInstrument().getFretCount() * IFigureConstants.FRET_WIDTH;
		final int x3 = x1 + (getCurrentInstrument().getFretCount() + 1) * IFigureConstants.FRET_WIDTH;

		g.setLineCap(SWT.CAP_FLAT);

		for (int i = 0; i < getCurrentInstrument().getStringCount(); i++) {

			// draw first empty strings section
			if (showEmptyStringsFrame && (showEmptyStringsTwice || !isLeftHander)) {

				// draw star for doubled string with octave jump
				if (getCurrentInstrument().isDoubledStringWithOctaveJump(i + 1)) {
					g.setFont(FONT);
					g.drawText("*", x0 / 2 - 2, y - 5); //$NON-NLS-1$
				}

				// draw dotted line
				g.setLineWidth(1);
				g.setLineStyle(SWT.LINE_DOT);
				g.drawLine(x0, y, x1, y);
				if (getCurrentInstrument().hasDoubledStrings()) {
					g.drawLine(x0, y + IFigureConstants.DOUBLED_STRINGS_DISTANCE, x1, y
							+ IFigureConstants.DOUBLED_STRINGS_DISTANCE);
				}
			}

			// draw solid line
			g.setLineWidth(2);
			if (getCurrentInstrument().hasDoubledStrings()) {
				g.setLineWidth(1);
			}
			g.setLineStyle(SWT.LINE_SOLID);
			g.drawLine(x1, y, x2, y);
			if (getCurrentInstrument().hasDoubledStrings()) {
				g.drawLine(x1, y + IFigureConstants.DOUBLED_STRINGS_DISTANCE, x2, y
						+ IFigureConstants.DOUBLED_STRINGS_DISTANCE);
			}

			// draw second empty string section
			if (showEmptyStringsFrame && (showEmptyStringsTwice || isLeftHander)) {

				// draw dotted line
				g.setLineWidth(1);
				g.setLineStyle(SWT.LINE_DOT);
				g.drawLine(x2, y, x3, y);
				if (getCurrentInstrument().hasDoubledStrings()) {
					g.drawLine(x2, y + IFigureConstants.DOUBLED_STRINGS_DISTANCE, x3, y
							+ IFigureConstants.DOUBLED_STRINGS_DISTANCE);
				}

				// draw star for doubled string with octave jump
				if (getCurrentInstrument().isDoubledStringWithOctaveJump(i + 1)) {
					g.setFont(FONT);
					g.drawText("*", x3 + x0 / 2 - 2, y - 5); //$NON-NLS-1$
				}
			}

			y += IFigureConstants.FRET_HEIGHT;
		}
	}

	/**
	 * Paints the capotasto if necessary.
	 * 
	 * @param g
	 *            the Graphics object used for painting
	 */
	private void paintCapotasto(final Graphics g) {
		if (getCurrentInstrument().getMinFret() == 0) {
			return;
		}

		g.setBackgroundColor(IFigureConstants.GREY);
		g.setForegroundColor(ColorConstants.black);
		g.setLineStyle(SWT.LINE_SOLID);
		g.setLineWidth(1);

		final int y0 = getOffsetY();
		int lastCapoFret = -1;
		int lastString = 0;
		for (int i = 0; i <= getCurrentInstrument().getStringCount(); i++) {
			final int currentCapoFret = getCurrentInstrument().getCapoFret(i + 1);

			if (currentCapoFret != lastCapoFret && currentCapoFret != -1
					|| i == getCurrentInstrument().getStringCount()) {
				final int height = i - lastString;
				if (lastCapoFret > 0) {
					g.fillRoundRectangle(new Rectangle(getXFretPosition(lastCapoFret) + IFigureConstants.CAPO_OFFSET_X,
							y0 + lastString * IFigureConstants.FRET_HEIGHT + IFigureConstants.CAPO_OFFSET_Y,
							IFigureConstants.CAPO_WIDTH + 1, height * IFigureConstants.FRET_HEIGHT + 1),
							IFigureConstants.FRET_HEIGHT, IFigureConstants.FRET_HEIGHT);
					g.drawRoundRectangle(new Rectangle(getXFretPosition(lastCapoFret) + IFigureConstants.CAPO_OFFSET_X,
							y0 + lastString * IFigureConstants.FRET_HEIGHT + IFigureConstants.CAPO_OFFSET_Y,
							IFigureConstants.CAPO_WIDTH, height * IFigureConstants.FRET_HEIGHT),
							IFigureConstants.FRET_HEIGHT, IFigureConstants.FRET_HEIGHT);
				}
				lastCapoFret = currentCapoFret;
				lastString = i;
			}
		}
	}
}
