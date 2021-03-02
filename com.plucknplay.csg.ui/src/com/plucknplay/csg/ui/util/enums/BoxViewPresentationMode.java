/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util.enums;

import java.util.List;

import org.eclipse.swt.graphics.Point;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.model.BoxDraft;

public enum BoxViewPresentationMode {

	HORIZONTAL(40, 30, 60, 30, Preferences.BOX_VIEW_FRET_NUMBERS_HORIZONTAL_POSITION), VERTICAL(30, 40, 30, 60,
			Preferences.BOX_VIEW_FRET_NUMBERS_VERTICAL_POSITION);

	private static final int MARGIN_SMALL = 10;
	private static final int MARGIN_BIG = 30;
	private static final int INFO_BAR_SPACING = 12;

	private int fretWidthSmall;
	private int fretHeightSmall;
	private int fretWidthBig;
	private int fretHeightBig;

	private String positionPrefName;

	private BoxViewPresentationMode(final int fretWidthSmall, final int fretHeightSmall, final int fretWidthBig,
			final int fretHeightBig, final String positionPrefName) {

		this.fretWidthSmall = fretWidthSmall;
		this.fretWidthBig = fretWidthBig;
		this.fretHeightSmall = fretHeightSmall;
		this.fretHeightBig = fretHeightBig;

		this.positionPrefName = positionPrefName;
	}

	public int getFretWidthSmall() {
		return fretWidthSmall;
	}

	public int getFretWidthBig() {
		return fretWidthBig;
	}

	public int getFretHeightSmall() {
		return fretHeightSmall;
	}

	public int getFretHeightBig() {
		return fretHeightBig;
	}

	public int getFretWidth(final boolean showNotesInside, final boolean showIntervalsInside) {
		return Activator.getDefault().getPreferenceStore().getBoolean(Preferences.BOX_VIEW_FRAME_SMALL_FRETS)
				&& areSmallFretsPossible(showNotesInside, showIntervalsInside) ? fretWidthSmall : fretWidthBig;
	}

	public int getFretHeight(final boolean showNotesInside, final boolean showIntervalsInside) {
		return Activator.getDefault().getPreferenceStore().getBoolean(Preferences.BOX_VIEW_FRAME_SMALL_FRETS)
				&& areSmallFretsPossible(showNotesInside, showIntervalsInside) ? fretHeightSmall : fretHeightBig;
	}

	public int getFretNumberWidth(final boolean showNotesInside, final boolean showIntervalsInside) {
		return this == HORIZONTAL ? getFretWidth(showNotesInside, showIntervalsInside) : getFretWidth(showNotesInside,
				showIntervalsInside) * 3 / 2;
	}

	public int getFretNumberHeight(final boolean showNotesInside, final boolean showIntervalsInside) {
		return getFretHeight(showNotesInside, showIntervalsInside);
	}

	public int getFretNumberSpacing(final boolean showNotesInside, final boolean showIntervalsInside) {
		if (this == HORIZONTAL) {
			return getFretHeight(showNotesInside, showIntervalsInside);
		}
		return getFretWidth(showNotesInside, showIntervalsInside);
	}

	private int getInfoBarSizeHelper(final BoxDraft boxDraft, final int index, final BoxViewFigureMode mode,
			final int minSize, final int midSize, final int maxSize) {

		int result = minSize;
		if (mode == BoxViewFigureMode.NOTE) {
			if (Activator.getDefault().getPreferenceStore().getString(Preferences.NOTES_MODE)
					.equals(Constants.NOTES_MODE_CROSS_AND_B)) {
				final List<Note> notes = boxDraft.getNotes(index);
				for (final Note note : notes) {
					if (note.hasAccidental()) {
						result = maxSize;
						break;
					}
				}
			}
		} else if (mode == BoxViewFigureMode.INTERVAL) {
			for (final Note note : boxDraft.getNotes(index)) {
				final Interval interval = boxDraft.getRootNote().calcInterval(note);
				final int length = boxDraft.getIntervalName(interval).length();
				if (length > 3) {
					result = maxSize;
					break;
				}
				if (length > 2) {
					result = midSize;
				}
			}
		}
		return result;
	}

	public int getInfoBarWidth(final BoxDraft boxDraft, final boolean showNotesInside,
			final boolean showIntervalsInside, final int index, final BoxViewFigureMode mode) {
		if (this == VERTICAL) {
			return getFretWidth(showNotesInside, showIntervalsInside);
		}
		return getInfoBarSizeHelper(boxDraft, index, mode, IFigureConstants.BOX_VIEW_HORIZONTAL_INFO_WIDTH_SMALL,
				IFigureConstants.BOX_VIEW_HORIZONTAL_INFO_WIDTH_MEDIUM,
				IFigureConstants.BOX_VIEW_HORIZONTAL_INFO_WIDTH_BIG);
	}

	public int getInfoBarHeight(final BoxDraft boxDraft, final boolean showNotesInside,
			final boolean showIntervalsInside, final int index, final BoxViewFigureMode mode) {
		if (this == HORIZONTAL) {
			return getFretHeight(showNotesInside, showIntervalsInside);
		}
		return getInfoBarSizeHelper(boxDraft, index, mode, IFigureConstants.BOX_VIEW_VERTICAL_INFO_HEIGHT_SMALL,
				IFigureConstants.BOX_VIEW_VERTICAL_INFO_HEIGHT_MEDIUM,
				IFigureConstants.BOX_VIEW_VERTICAL_INFO_HEIGHT_BIG);
	}

	private int getHorizontalBarWidth(final BoxDraft boxDraft, final boolean showFingeringOutside,
			final boolean showNotesOutside, final boolean showNotesInside, final boolean showIntervalsOutside,
			final boolean showIntervalsInside) {

		if (this != HORIZONTAL) {
			throw new UnsupportedOperationException();
		}

		int result = 0;
		if (showFingeringOutside || showNotesOutside || showIntervalsOutside) {
			result += INFO_BAR_SPACING;
		}
		if (showFingeringOutside) {
			result += getInfoBarWidth(boxDraft, showNotesInside, showIntervalsInside, 0, BoxViewFigureMode.FINGERING)
					* boxDraft.getMaxAssignmentsNumber(false);
		}
		for (int i = 0; i < boxDraft.getMaxAssignmentsNumber(true); i++) {
			if (showNotesOutside) {
				result += getInfoBarWidth(boxDraft, showNotesInside, showIntervalsInside, i, BoxViewFigureMode.NOTE);
			}
			if (showIntervalsOutside) {
				result += getInfoBarWidth(boxDraft, showNotesInside, showIntervalsInside, i, BoxViewFigureMode.INTERVAL);
			}
		}
		return result;
	}

	private int getVerticalBarHeight(final BoxDraft boxDraft, final boolean showFingeringOutside,
			final boolean showNotesOutside, final boolean showNotesInside, final boolean showIntervalsOutside,
			final boolean showIntervalsInside) {

		if (this != VERTICAL) {
			throw new UnsupportedOperationException();
		}

		int result = 0;
		if (showFingeringOutside || showNotesOutside || showIntervalsOutside) {
			result += INFO_BAR_SPACING;
		}
		if (showFingeringOutside) {
			result += getInfoBarHeight(boxDraft, showNotesInside, showIntervalsInside, 0, BoxViewFigureMode.FINGERING)
					* boxDraft.getMaxAssignmentsNumber(false);
		}
		for (int i = 0; i < boxDraft.getMaxAssignmentsNumber(true); i++) {
			if (showNotesOutside) {
				result += getInfoBarHeight(boxDraft, showNotesInside, showIntervalsInside, i, BoxViewFigureMode.NOTE);
			}
			if (showIntervalsOutside) {
				result += getInfoBarHeight(boxDraft, showNotesInside, showIntervalsInside, i,
						BoxViewFigureMode.INTERVAL);
			}
		}
		return result;
	}

	public int getWidth(final BoxDraft boxDraft, final boolean showFingeringOutside, final boolean showFingeringInside,
			final boolean showNotesOutside, final boolean showNotesInside, final boolean showIntervalsOutside,
			final boolean showIntervalsInside, final int fretCount, final int stringCount) {

		// margin left
		int result = getMargin(showFingeringInside, showNotesInside, showIntervalsInside).x;

		// margin right
		result += this == HORIZONTAL && isLeftHander() ? MARGIN_SMALL : MARGIN_BIG;

		// frets and info bars
		if (this == HORIZONTAL) {
			result += (fretCount + 1) * getFretWidth(showNotesInside, showIntervalsInside);
			result += getHorizontalBarWidth(boxDraft, showFingeringOutside, showNotesOutside, showNotesInside,
					showIntervalsOutside, showIntervalsInside);
		} else {
			result += getFretNumberWidth(showNotesInside, showIntervalsInside)
					+ getFretNumberSpacing(showNotesInside, showIntervalsInside) + (stringCount - 1)
					* getFretWidth(showNotesInside, showIntervalsInside)
					+ getFretWidth(showNotesInside, showIntervalsInside) / 2;
		}
		return result;
	}

	public int getHeight(final BoxDraft boxDraft, final boolean showFingeringOutside,
			final boolean showFingeringInside, final boolean showNotesOutside, final boolean showNotesInside,
			final boolean showIntervalsOutside, final boolean showIntervalsInside, final int fretCount,
			final int stringCount) {

		// margin top (dependent on mode) + margin bottom (fix)
		int result = getMargin(showFingeringInside, showNotesInside, showIntervalsInside).y + MARGIN_BIG;

		// frets and info bar
		if (this == HORIZONTAL) {
			result += getFretNumberHeight(showNotesInside, showIntervalsInside)
					+ getFretNumberSpacing(showNotesInside, showIntervalsInside) + (stringCount - 1)
					* getFretHeight(showNotesInside, showIntervalsInside)
					+ getFretHeight(showNotesInside, showIntervalsInside) / 2;
		} else {
			result += (fretCount + 1) * getFretHeight(showNotesInside, showIntervalsInside);
			result += getVerticalBarHeight(boxDraft, showFingeringOutside, showNotesOutside, showNotesInside,
					showIntervalsOutside, showIntervalsInside);
		}
		return result;
	}

	private Point getMargin(final boolean showFingeringInside, final boolean showNotesInside,
			final boolean showIntervalsInside) {
		final boolean bigFrets = areBigFretsInUse(showFingeringInside, showNotesInside, showIntervalsInside);
		final int x = this == HORIZONTAL && !isLeftHander() && bigFrets ? MARGIN_SMALL : MARGIN_BIG;
		final int y = this == VERTICAL && bigFrets ? MARGIN_SMALL : MARGIN_BIG;
		return new Point(x, y);
	}

	public Point getBoxPosition(final BoxDraft boxDraft, final boolean showFingeringInside,
			final boolean showFingeringOutside, final boolean showNotesOutside, final boolean showNotesInside,
			final boolean showIntervalsOutside, final boolean showIntervalsInside) {

		final Position fretNumberPosition = getFretNumberPosition();
		final boolean isLeftHander = isLeftHander();

		final Point margin = getMargin(showFingeringInside, showNotesInside, showIntervalsInside);
		int x = margin.x;
		int y = margin.y;

		if (this == HORIZONTAL) {
			if (isLeftHander) {
				x += getHorizontalBarWidth(boxDraft, showFingeringOutside, showNotesOutside, showNotesInside,
						showIntervalsOutside, showIntervalsInside);
			} else {
				x += getFretWidth(showNotesInside, showIntervalsInside);
			}
			y += fretNumberPosition == Position.TOP ? getFretNumberHeight(showNotesInside, showIntervalsInside)
					+ getFretNumberSpacing(showNotesInside, showIntervalsInside) : getFretHeight(showNotesInside,
					showIntervalsInside) / 2;
		} else {
			x += fretNumberPosition == Position.LEFT ? getFretNumberWidth(showNotesInside, showIntervalsInside)
					+ getFretNumberSpacing(showNotesInside, showIntervalsInside) : getFretWidth(showNotesInside,
					showIntervalsInside) / 2;
			y += getFretHeight(showNotesInside, showIntervalsInside);
		}
		return new Point(x, y);
	}

	public Point getFretNumberPosition(final BoxDraft boxDraft, final boolean showFingeringInside,
			final boolean showFingeringOutside, final boolean showNotesOutside, final boolean showNotesInside,
			final boolean showIntervalsOutside, final boolean showIntervalsInside, final int fretCount,
			final int stringCount) {

		final Position fretNumberPosition = getFretNumberPosition();
		final boolean isLeftHander = isLeftHander();
		final Point boxPosition = getBoxPosition(boxDraft, showFingeringInside, showFingeringOutside, showNotesOutside,
				showNotesInside, showIntervalsOutside, showIntervalsInside);

		int x = boxPosition.x;
		int y = boxPosition.y;

		if (this == HORIZONTAL) {
			y = fretNumberPosition == Position.TOP ? y
					- (getFretNumberSpacing(showNotesInside, showIntervalsInside) + getFretNumberHeight(
							showNotesInside, showIntervalsInside)) : y + (stringCount - 1)
					* getFretHeight(showNotesInside, showIntervalsInside)
					+ getFretNumberSpacing(showNotesInside, showIntervalsInside);
			if (isLeftHander) {
				x += (fretCount - 1) * getFretWidth(showNotesInside, showIntervalsInside);
			}
		} else {
			x = fretNumberPosition == Position.LEFT ? x
					- (getFretNumberSpacing(showNotesInside, showIntervalsInside) + getFretNumberWidth(showNotesInside,
							showIntervalsInside)) : x + (stringCount - 1)
					* getFretWidth(showNotesInside, showIntervalsInside)
					+ getFretNumberSpacing(showNotesInside, showIntervalsInside);
		}
		return new Point(x, y);
	}

	public Point getEmptyStringPositon(final Point boxPosition, final boolean showNotesInside,
			final boolean showIntervalsInside, final int stringCount, final int fretCount, final int string,
			final int figureSize, final boolean closeToFrame) {

		final Point assPos = getAssignmentPosition(boxPosition, showNotesInside, showIntervalsInside, stringCount,
				fretCount, string, 0);
		final Point result = new Point(assPos.x - figureSize / 2, assPos.y - figureSize / 2);

		if (closeToFrame) {
			if (this == HORIZONTAL) {
				result.x = isLeftHander() ? assPos.x - getFretWidth(showNotesInside, showIntervalsInside) / 2 + 4
						: assPos.x + getFretWidth(showNotesInside, showIntervalsInside) / 2 - figureSize - 2;
			} else {
				result.y = assPos.y + getFretHeight(showNotesInside, showIntervalsInside) / 2 - figureSize - 2;
			}
		}
		return result;
	}

	public Point getAssignmentPosition(final Point boxPosition, final boolean showNotesInside,
			final boolean showIntervalsInside, final int stringCount, final int fretCount, final int string,
			final int fret) {

		int x = boxPosition.x;
		int y = boxPosition.y;
		final boolean isLeftHander = isLeftHander();

		if (this == HORIZONTAL) {
			y += string * getFretHeight(showNotesInside, showIntervalsInside);
			x -= getFretWidth(showNotesInside, showIntervalsInside) / 2;
			final int factor = isLeftHander ? fretCount - fret + 1 : fret;
			x += factor * getFretWidth(showNotesInside, showIntervalsInside);
		} else {
			final int factor = isLeftHander ? string : stringCount - string - 1;
			x += factor * getFretWidth(showNotesInside, showIntervalsInside);
			y -= getFretHeight(showNotesInside, showIntervalsInside) / 2;
			y += fret * getFretHeight(showNotesInside, showIntervalsInside);
		}
		return new Point(x, y);
	}

	private Point getBarPosition(final BoxDraft boxDraft, final int fretCount, final boolean showFingeringInside,
			final boolean showFingeringOutside, final boolean showNotesOutside, final boolean showNotesInside,
			final boolean showIntervalsOutside, final boolean showIntervalsInside) {

		final boolean isLeftHander = isLeftHander();
		final Point boxPosition = getBoxPosition(boxDraft, showFingeringInside, showFingeringOutside, showNotesOutside,
				showNotesInside, showIntervalsOutside, showIntervalsInside);

		int x = boxPosition.x;
		int y = boxPosition.y;

		if (this == HORIZONTAL) {
			y = y - getFretHeight(showNotesInside, showIntervalsInside) / 2;
			if (isLeftHander) {
				x -= getHorizontalBarWidth(boxDraft, showFingeringOutside, showNotesOutside, showNotesInside,
						showIntervalsOutside, showIntervalsInside);
			} else {
				x += fretCount * getFretWidth(showNotesInside, showIntervalsInside) + INFO_BAR_SPACING;
			}
		} else {
			x -= getFretWidth(showNotesInside, showIntervalsInside) / 2;
			y += fretCount * getFretHeight(showNotesInside, showIntervalsInside) + INFO_BAR_SPACING;
		}
		return new Point(x, y);
	}

	public Point getInfoPosition(final BoxDraft boxDraft, final int fretCount, final int stringCount, final int string,
			final boolean showFingeringInside, final boolean showFingeringOutside, final boolean showNotesOutside,
			final boolean showNotesInside, final boolean showIntervalsOutside, final boolean showIntervalsInside,
			final BoxViewFigureMode mode) {

		final Point barPosition = getBarPosition(boxDraft, fretCount, showFingeringInside, showFingeringOutside,
				showNotesOutside, showNotesInside, showIntervalsOutside, showIntervalsInside);
		int x = barPosition.x;
		int y = barPosition.y;
		final boolean isLeftHander = isLeftHander();

		final int maxAssigmentsNumber = boxDraft.getMaxAssignmentsNumber(false);
		final int maxNotesNumber = boxDraft.getMaxAssignmentsNumber(true);
		if (this == HORIZONTAL) {
			if (mode == BoxViewFigureMode.FINGERING) {
				for (int i = 0; i < maxNotesNumber; i++) {
					x += isLeftHander && showIntervalsOutside ? getInfoBarWidth(boxDraft, showNotesInside,
							showIntervalsInside, i, BoxViewFigureMode.INTERVAL) : 0;
					x += isLeftHander && showNotesOutside ? getInfoBarWidth(boxDraft, showNotesInside,
							showIntervalsInside, i, BoxViewFigureMode.NOTE) : 0;
				}
			} else if (mode == BoxViewFigureMode.NOTE) {
				x += !isLeftHander && showFingeringOutside ? getInfoBarWidth(boxDraft, showNotesInside,
						showIntervalsInside, 0, BoxViewFigureMode.FINGERING) * maxAssigmentsNumber : 0;
				for (int i = 0; i < maxNotesNumber; i++) {
					x += isLeftHander && showIntervalsOutside ? getInfoBarWidth(boxDraft, showNotesInside,
							showIntervalsInside, i, BoxViewFigureMode.INTERVAL) : 0;
				}
			} else if (mode == BoxViewFigureMode.INTERVAL) {
				x += !isLeftHander && showFingeringOutside ? getInfoBarWidth(boxDraft, showNotesInside,
						showIntervalsInside, 0, BoxViewFigureMode.FINGERING) * maxAssigmentsNumber : 0;
				for (int i = 0; i < maxNotesNumber; i++) {
					x += !isLeftHander && showNotesOutside ? getInfoBarWidth(boxDraft, showNotesInside,
							showIntervalsInside, i, BoxViewFigureMode.NOTE) : 0;
				}
			}
			y += string
					* getInfoBarHeight(boxDraft, showNotesInside, showIntervalsInside, 0, BoxViewFigureMode.FINGERING);
		} else {
			final int factor = isLeftHander ? string : stringCount - string - 1;
			x += factor
					* getInfoBarWidth(boxDraft, showNotesInside, showIntervalsInside, 0, BoxViewFigureMode.FINGERING);
			if (mode != BoxViewFigureMode.FINGERING && showFingeringOutside) {
				y += getInfoBarHeight(boxDraft, showNotesInside, showIntervalsInside, 0, BoxViewFigureMode.FINGERING)
						* maxAssigmentsNumber;
			}
			if (mode == BoxViewFigureMode.INTERVAL && showNotesOutside) {
				for (int i = 0; i < maxNotesNumber; i++) {
					y += getInfoBarHeight(boxDraft, showNotesInside, showIntervalsInside, i, BoxViewFigureMode.NOTE);
				}
			}
		}
		return new Point(x, y);
	}

	private Position getFretNumberPosition() {
		return Position.valueOf(Activator.getDefault().getPreferenceStore().getString(positionPrefName));
	}

	private boolean isLeftHander() {
		return Activator.getDefault().isLeftHander();
	}

	public boolean areSmallFretsPossible(final boolean showNotesInside, final boolean showIntervalsInside) {
		return !showNotesInside && !showIntervalsInside;
	}

	public boolean areBigFretsInUse(final boolean showFingeringInside, final boolean showNotesInside,
			final boolean showIntervalsInside) {
		return showNotesInside || showIntervalsInside
				|| !Activator.getDefault().getPreferenceStore().getBoolean(Preferences.BOX_VIEW_FRAME_SMALL_FRETS);
	}
}
