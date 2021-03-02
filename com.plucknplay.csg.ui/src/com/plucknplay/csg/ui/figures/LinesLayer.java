/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.plucknplay.csg.core.model.enums.Clef;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.NotesDraft;
import com.plucknplay.csg.ui.model.NotesDraftUtil;

public class LinesLayer extends AbstractNotesLayer {

	private static Map<Clef, Image> clefImageMap;

	static {
		final Activator activator = Activator.getDefault();
		clefImageMap = new HashMap<Clef, Image>();
		clefImageMap.put(Clef.F_ONE_OCTAVE_DEEPER, activator.getImage(IFigureConstants.CLEF_IMAGE_F_1));
		clefImageMap.put(Clef.F_STANDARD, activator.getImage(IFigureConstants.CLEF_IMAGE_F_2));
		clefImageMap.put(Clef.F_CHIAVETTE, activator.getImage(IFigureConstants.CLEF_IMAGE_F_3));
		clefImageMap.put(Clef.C_BARITON, activator.getImage(IFigureConstants.CLEF_IMAGE_C_5));
		clefImageMap.put(Clef.C_TENOR, activator.getImage(IFigureConstants.CLEF_IMAGE_C_4));
		clefImageMap.put(Clef.C_ALTO, activator.getImage(IFigureConstants.CLEF_IMAGE_C_3));
		clefImageMap.put(Clef.C_MEZZO_SOPRANO, activator.getImage(IFigureConstants.CLEF_IMAGE_C_2));
		clefImageMap.put(Clef.C_SOPRANO, activator.getImage(IFigureConstants.CLEF_IMAGE_C_1));
		clefImageMap.put(Clef.G_ONE_OCTAVE_DEEPER, activator.getImage(IFigureConstants.CLEF_IMAGE_G_1));
		clefImageMap.put(Clef.G_STANDARD, activator.getImage(IFigureConstants.CLEF_IMAGE_G_2));
		clefImageMap.put(Clef.G_CHIAVETTE, activator.getImage(IFigureConstants.CLEF_IMAGE_G_3));
		clefImageMap.put(Clef.G_ONE_OCTAVE_HIGHER, activator.getImage(IFigureConstants.CLEF_IMAGE_G_4));
		clefImageMap.put(Clef.G_TWO_OCTAVES_HIGHER, activator.getImage(IFigureConstants.CLEF_IMAGE_G_5));
	}

	public LinesLayer(final NotesDraft notesDraft, final String displayMode) {
		super(notesDraft, displayMode);
	}

	@Override
	public void paintFigure(final Graphics g) {

		// get all necessary preferences
		final boolean showClefAnnotation = Activator.getDefault().getPreferenceStore()
				.getBoolean(Preferences.NOTES_VIEW_SHOW_CLEF_ANNOTATION);
		final String mode = getNotesDraft().isEditable() ? UIConstants.DISPLAY_AS_BLOCK : getDisplayMode();

		g.setLineWidth(1);
		g.setLineStyle(SWT.LINE_SOLID);
		g.setForegroundColor(ColorConstants.black);
		g.setLineCap(SWT.CAP_FLAT);

		final int spacesAbove = Math.max(getNotesDraft().getNumberOfLedgerLinesAbove() + 1,
				IFigureConstants.MIN_NUMBER_OF_SPACES_ABOVE_OR_BELOW);

		int x0 = IFigureConstants.NOTES_OFFSET_X;
		int y0 = IFigureConstants.NOTES_OFFSET_Y + spacesAbove * IFigureConstants.NOTE_LINE_DISTANCE;

		final int height = IFigureConstants.NUMBER_OF_SPACES_INSIDE_STAFF * IFigureConstants.NOTE_LINE_DISTANCE;
		int width = NotesDraftUtil.getStaffWidth(mode, getNotesDraft());

		// paint vertical lines
		g.drawLine(x0, y0, x0, y0 + height);
		g.drawLine(x0 + width, y0, x0 + width, y0 + height);

		// paint horizontal lines
		for (int i = 0; i < IFigureConstants.NUMBER_OF_STAFF_LINES; i++) {
			g.drawLine(x0, y0 + i * IFigureConstants.NOTE_LINE_DISTANCE, x0 + width, y0 + i
					* IFigureConstants.NOTE_LINE_DISTANCE);
		}

		// paint clef image
		Clef clef = getNotesDraft().getClef();
		if (!showClefAnnotation) {
			if (clef == Clef.F_ONE_OCTAVE_DEEPER) {
				clef = Clef.F_STANDARD;
			} else if (clef == Clef.G_ONE_OCTAVE_DEEPER || clef == Clef.G_ONE_OCTAVE_HIGHER
					|| clef == Clef.G_TWO_OCTAVES_HIGHER) {
				clef = Clef.G_STANDARD;
			}
		}
		final Image image = clefImageMap.get(clef);
		if (image != null) {
			final int imageY = y0 - 190;
			g.drawImage(image, 0, 0, IFigureConstants.CLEF_IMAGE_WIDTH, IFigureConstants.CLEF_IMAGE_HEIGHT, x0
					+ IFigureConstants.COMMON_NOTE_SPACING, imageY, IFigureConstants.CLEF_IMAGE_WIDTH,
					IFigureConstants.CLEF_IMAGE_HEIGHT);
		}

		// paint grey help lines
		if (getNotesDraft().isEditable()) {

			int offset = 2 - getNotesDraft().getNumberOfLedgerLinesAbove();
			if (offset < 0) {
				offset = 0;
			}

			x0 = IFigureConstants.NOTES_OFFSET_X + 2 * IFigureConstants.COMMON_NOTE_SPACING
					+ IFigureConstants.CLEF_IMAGE_WIDTH;
			y0 = IFigureConstants.NOTES_OFFSET_Y;
			width = width - 3 * IFigureConstants.COMMON_NOTE_SPACING - IFigureConstants.CLEF_IMAGE_WIDTH;

			g.setForegroundColor(IFigureConstants.GREY);

			// draw ledger lines above staff
			int start = 1 + offset;
			int end = getNotesDraft().getNumberOfLedgerLinesAbove() + offset;
			for (int i = start; i <= end; i++) {
				g.drawLine(x0, y0 + i * IFigureConstants.NOTE_LINE_DISTANCE, x0 + width, y0 + i
						* IFigureConstants.NOTE_LINE_DISTANCE);
				start++;
			}

			// draw ledger lines below staff
			start += IFigureConstants.NUMBER_OF_SPACES_INSIDE_STAFF + 1;
			end = start + getNotesDraft().getNumberOfLedgerLinesBelow();
			for (int i = start; i < end; i++) {
				g.drawLine(x0, y0 + i * IFigureConstants.NOTE_LINE_DISTANCE, x0 + width, y0 + i
						* IFigureConstants.NOTE_LINE_DISTANCE);
			}
		}
	}
}
