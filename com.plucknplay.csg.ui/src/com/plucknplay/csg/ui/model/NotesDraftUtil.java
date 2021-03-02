/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.model;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.figures.IFigureConstants;

public final class NotesDraftUtil {

	private static Point containerSize;

	private NotesDraftUtil() {
	}

	public static void setContainerSize(final Point containerSize) {
		NotesDraftUtil.containerSize = containerSize;
	}

	private static int getWidthOffset() {
		return 2 * IFigureConstants.NOTES_OFFSET_X;
	}

	public static int getNormWidth(final String mode, final NotesDraft notesDraft) {
		return getWidthOffset() + getStaffWidth(mode, notesDraft);
	}

	public static int getStaffWidth(final String mode, final NotesDraft notesDraft) {

		int width = IFigureConstants.CLEF_IMAGE_WIDTH + 3 * IFigureConstants.COMMON_NOTE_SPACING;

		if (UIConstants.DISPLAY_AS_BLOCK.equals(mode)) {
			width += 7 * IFigureConstants.ACCIDENTAL_IMAGE_WIDTH;
			width += IFigureConstants.ACCIDENTAL_ACCIDENTAL_SPACING;
			width += 4 * IFigureConstants.NOTE_WIDTH;
			width += 3 * IFigureConstants.LEDGER_LINE_OFFSET;
		}

		else {
			width += notesDraft.getNotePositions().size()
					* (IFigureConstants.ACCIDENTAL_IMAGE_WIDTH + IFigureConstants.NOTE_WIDTH
							+ IFigureConstants.LEDGER_LINE_OFFSET + IFigureConstants.NOTE_ACCIDENTAL_SPACING);
		}

		if (containerSize != null
				&& Activator.getDefault().getPreferenceStore().getBoolean(Preferences.NOTES_VIEW_USE_MAX_WIDTH)) {

			final double scale = containerSize.y * 1.0d / getNormHeight(notesDraft);
			final int maxWidth = (int) (containerSize.x / Math.max(IFigureConstants.NOTES_MIN_SCALE_FACTOR, scale))
					- getWidthOffset();

			if (width < maxWidth) {
				width = maxWidth;
			}
		}
		return width;
	}

	private static int getHeightOffset() {
		return 2 * IFigureConstants.NOTES_OFFSET_Y;
	}

	private static int getHeight(final NotesDraft notesDraft) {
		final int spaces = 4
				+ Math.max(notesDraft.getNumberOfLedgerLinesBelow() + 1,
						IFigureConstants.MIN_NUMBER_OF_SPACES_ABOVE_OR_BELOW)
				+ Math.max(notesDraft.getNumberOfLedgerLinesAbove() + 1,
						IFigureConstants.MIN_NUMBER_OF_SPACES_ABOVE_OR_BELOW);
		return spaces * IFigureConstants.NOTE_LINE_DISTANCE;
	}

	public static int getNormHeight(final NotesDraft notesDraft) {
		return getHeightOffset() + getHeight(notesDraft);
	}

	/**
	 * Returns the display mode for the given notes draft depending on the
	 * stored settings in the prefeneces.
	 * 
	 * @param draft
	 *            the notes draft, must not be <code>null</code>
	 * 
	 * @return the display mode for the given notes draft
	 */
	public static String getDisplayMode(final NotesDraft draft) {
		if (draft == null) {
			throw new IllegalArgumentException();
		}

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean showBlocks = prefs.getBoolean(Preferences.SHOW_BLOCKS);

		String displayMode = UIConstants.DISPLAY_AS_BLOCK;
		if (NotesDraft.TYPE_GRIPTABLE.equals(draft.getType())) {

			displayMode = prefs.getString(Preferences.NOTES_VIEW_DISPLAY_MODE_GRIPTABLES);

		} else if (NotesDraft.TYPE_CHORD.equals(draft.getType())) {

			displayMode = showBlocks && prefs.getBoolean(Preferences.NOTES_VIEW_SHOW_ONLY_CHORD_BLOCKS) ? prefs
					.getString(Preferences.NOTES_VIEW_DISPLAY_MODE_CHORD_BLOCKS) : prefs
					.getString(Preferences.NOTES_VIEW_DISPLAY_MODE_CHORD_SCHEMES);

		} else if (NotesDraft.TYPE_SCALE.equals(draft.getType())) {

			displayMode = showBlocks && prefs.getBoolean(Preferences.NOTES_VIEW_SHOW_ONLY_SCALE_BLOCKS) ? prefs
					.getString(Preferences.NOTES_VIEW_DISPLAY_MODE_SCALE_BLOCKS) : prefs
					.getString(Preferences.NOTES_VIEW_DISPLAY_MODE_SCALES);
		}

		return displayMode;
	}
}
