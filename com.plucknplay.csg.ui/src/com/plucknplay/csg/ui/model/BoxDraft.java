/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class BoxDraft extends FretDraft {

	/**
	 * Property which indicates that the start fret of a box draft has changed.
	 * 
	 * <p>
	 * The new start fret will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_START_FRET_CHANGED = new Object();

	/**
	 * Property which indicates that the fret width of a box draft has changed.
	 * 
	 * <p>
	 * The new fret width will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_FRET_WIDTH_CHANGED = new Object();

	/**
	 * The start fret of this draft.
	 */
	private int startFret;

	/**
	 * The width of frets of this draft.
	 */
	private int fretWidth;

	/**
	 * key: info box column number; value: list of notes
	 */
	private Map<Integer, List<Note>> infoBoxNotes;

	/**
	 * The default constructor.
	 * 
	 * <p>
	 * Creates an empty box draft with start fret 1 and fret width 4.
	 * </p>
	 */
	public BoxDraft() {
		super();
		defaultCreation();
		setModifiedInput(false);
	}

	/**
	 * The constructor.
	 * 
	 * @param other
	 *            another box draft
	 */
	public BoxDraft(final BoxDraft other) {
		super(other);
		startFret = other.startFret;
		fretWidth = other.fretWidth;

		// clone info box notes
		infoBoxNotes = null;
		if (other.infoBoxNotes != null) {
			infoBoxNotes = new HashMap<Integer, List<Note>>();
			for (final Entry<Integer, List<Note>> entry : other.infoBoxNotes.entrySet()) {
				infoBoxNotes.put(entry.getKey(), new ArrayList<Note>(entry.getValue()));
			}
		}
	}

	/**
	 * The constructor.
	 * 
	 * <p>
	 * Creates a box draft out of a griptable.
	 * </p>
	 * 
	 * @param input
	 *            the input, must not be null
	 */
	public BoxDraft(final IBlock input) {
		super(input);

		if (input == null) {
			throw new IllegalArgumentException();
		}

		if (getCurrentInstrument() == null) {
			return;
		}

		if (input.getFretSpan() > 8) {
			defaultCreation();
		} else {
			initAssignments(input);
		}
		initRootNote(input);
		setModifiedInput(false);
	}

	private void defaultCreation() {
		if (getCurrentInstrument() == null) {
			return;
		}
		setStartFret(getCurrentInstrument().getMinFret() + 1);
		setFretWidth(getDefaultFretCount());
		clear();
	}

	@Override
	protected void initStartFretAndSpan(final IBlock block) {
		final int fretSpan = block.getFretSpan();
		fretWidth = Math.max(getDefaultFretCount(), fretSpan);

		int minFret = block.getMinFret();
		if (minFret < 1) {
			minFret = 1;
		}

		startFret = minFret;
		final int possibleEmptyFrets = fretWidth - fretSpan;
		final int actualDistanceToNut = minFret - 1;
		if (actualDistanceToNut <= getDefaultMaxEmtpyStartingFretsCount() && actualDistanceToNut <= possibleEmptyFrets) {
			startFret = 1;
		}
	}

	/**
	 * Sets the start fret of this box draft.
	 * 
	 * @param startFret
	 *            the new start fret, must be a value between 1 and the current
	 *            instruments fret count - fret width + 1
	 */
	public void setStartFret(final int startFret) {
		if (getCurrentInstrument() == null) {
			return;
		}

		if (startFret < 1 || startFret > getCurrentInstrument().getFretCount() - fretWidth + 1) {
			throw new IllegalArgumentException();
		}

		if (this.startFret != startFret) {
			this.startFret = startFret;
			setModifiedInput(true);
			setRootNote(false);
			notifyListeners(PROP_START_FRET_CHANGED, Integer.valueOf(startFret));
		}
	}

	@Override
	public int getStartFret() {
		return startFret;
	}

	/**
	 * Sets the fret width of this box draft.
	 * 
	 * @param fretWidth
	 *            the new fret width, must be a value between 3 and
	 *            {@value FretBlock#MAX_FRET_RANGE}
	 */
	public void setFretWidth(final int fretWidth) {
		if (getCurrentInstrument() == null) {
			return;
		}

		if (fretWidth < 3 || fretWidth > FretBlock.MAX_FRET_RANGE) {
			throw new IllegalArgumentException();
		}

		final int tempSize = getFretboardPositions().size();
		if (this.fretWidth != fretWidth) {
			this.fretWidth = fretWidth;
			notifyListeners(PROP_FRET_WIDTH_CHANGED, Integer.valueOf(fretWidth));

			// adjust start fret if necessary
			final int fretCount = getCurrentInstrument().getFretCount();
			int shift = 0;
			if (startFret + this.fretWidth - 1 > fretCount) {
				final int oldStartFret = getStartFret();
				setStartFret(fretCount - this.fretWidth + 1);
				shift = oldStartFret - getStartFret();
			}
			adjustAssignments(shift);
			if (tempSize != getFretboardPositions().size()) {
				setModifiedInput(true);
			}
		}
	}

	@Override
	public int getFretWidth() {
		return fretWidth;
	}

	/**
	 * Returns the maximal number of assignments at a string for this box draft.
	 * 
	 * @param includeOpenStrings
	 *            <code>true</code> if assignments of open string shall be
	 *            added, <code>false</code> otherwise
	 * 
	 * @return the maximal number of assignments at a string
	 */
	public int getMaxAssignmentsNumber(final boolean includeOpenStrings) {
		int result = 0;
		for (int s = 1; s <= getCurrentInstrument().getStringCount(); s++) {
			final int currentResult = getAssignments(s, includeOpenStrings).size();
			if (currentResult > result) {
				result = currentResult;
			}
		}
		return result;
	}

	/**
	 * Returns the notes that constitutes the info box column with the given
	 * number.
	 * 
	 * @param infoBoxColumnNumber
	 *            the string, must be a value between 1 and the current
	 *            instruments string count
	 * 
	 * @return the notes that constitutes the info box column with the given
	 *         number
	 */
	public List<Note> getNotes(final int infoBoxColumnNumber) {
		if (getCurrentInstrument() == null) {
			return new ArrayList<Note>();
		}

		if (infoBoxNotes == null) {
			infoBoxNotes = new HashMap<Integer, List<Note>>();
			for (int s = 0; s < getCurrentInstrument().getStringCount(); s++) {
				int column = 0;
				for (int f = 0; f <= fretWidth; f++) {
					if (!NO_FINGER.equals(getAssignment(f, s + 1))) {
						final FretboardPosition fbp = new FretboardPosition(s, f == 0 ? getCurrentInstrument()
								.getCapoFret(s + 1) : f + startFret - 1);
						final Note note = getCurrentInstrument().getNote(fbp);

						List<Note> list = infoBoxNotes.get(column);
						if (list == null) {
							list = new ArrayList<Note>();
							infoBoxNotes.put(column, list);
						}
						list.add(note);
						column++;
					}
				}
			}
		}

		final List<Note> result = infoBoxNotes.get(infoBoxColumnNumber);
		return result != null ? result : new ArrayList<Note>();
	}

	@Override
	public void setEditable(final boolean editable) {
		super.setEditable(editable);
		if (getCurrentInstrument() == null) {
			return;
		}

		// adjust start fret if necessary
		if (editable) {
			final int fretCount = getCurrentInstrument().getFretCount();
			final Note tempRootNote = getRootNote();
			if (getStartFret() + getFretWidth() - 1 > fretCount) {
				final int oldStartFret = getStartFret();
				setStartFret(fretCount - getFretWidth() + 1);
				final int shift = oldStartFret - getStartFret();
				if (shift > 0) {
					adjustAssignments(shift);
					setRootNote(tempRootNote);
				}
			}
		}
	}

	private int getDefaultFretCount() {
		return Activator.getDefault().getPreferenceStore().getInt(Preferences.BOX_VIEW_FRAME_MIN_FRET_COUNT);
	}

	private int getDefaultMaxEmtpyStartingFretsCount() {
		return Activator.getDefault().getPreferenceStore().getInt(Preferences.BOX_VIEW_FRAME_MAX_UNASSIGNED_FRET_COUNT);
	}

	@Override
	protected void setModifiedInput(final boolean modifiedInput) {
		super.setModifiedInput(modifiedInput);
		infoBoxNotes = null;
	}
}
