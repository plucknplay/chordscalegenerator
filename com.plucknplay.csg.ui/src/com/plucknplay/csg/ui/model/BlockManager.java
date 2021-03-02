/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.model;

import java.util.Collections;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.model.AdvancedFretBlock;
import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.OctaveBlock;
import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;

public final class BlockManager {

	public static final Object LEFT = new Object();
	public static final Object RIGHT = new Object();
	public static final Object UP = new Object();
	public static final Object DOWN = new Object();

	/**
	 * The singleton instance of the block manager.
	 */
	private static BlockManager instance;

	private Block block;

	private Instrument currentInstrument;
	private FretboardPosition startFretboardPosition;

	private Instrument tempCurrentInstrument;
	private FretboardPosition tempStartFretboardPosition;

	/**
	 * The preferences.
	 */
	private final IPreferenceStore prefs;

	/**
	 * Change listener for instrument changes.
	 */
	private final IChangeListener instrumentListChangeListener = new IChangeListener() {
		@Override
		public void notifyChange(final Object source, final Object parentSource, final Object property) {
			if (property == InstrumentList.PROP_CURRENT_INSTRUMENT_CHANGED
					|| property == InstrumentList.PROP_CHANGED_ELEMENT && source == currentInstrument) {
				reset();
			}
		}
	};

	/**
	 * The private default constructor.
	 */
	private BlockManager() {
		prefs = Activator.getDefault().getPreferenceStore();
		reset();
		InstrumentList.getInstance().addChangeListener(instrumentListChangeListener);
	}

	/**
	 * Returns the singleton instance of the block manager.
	 * 
	 * @return the singleton instance of the block manager
	 */
	public static BlockManager getInstance() {
		if (instance == null) {
			instance = new BlockManager();
		}
		return instance;
	}

	/**
	 * Resets the block manager.
	 */
	public void reset() {
		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		startFretboardPosition = null;
		block = null;
	}

	/**
	 * Stores the current data so that it is possible to go back to this state.
	 */
	public void storeCurrentData() {
		tempCurrentInstrument = currentInstrument;
		tempStartFretboardPosition = startFretboardPosition;
	}

	/**
	 * Resets to the state od the stored datas.
	 */
	public void resetToStoredData() {
		if (tempCurrentInstrument == null || tempStartFretboardPosition == null
				|| tempCurrentInstrument != InstrumentList.getInstance().getCurrentInstrument()) {
			reset();
		} else {
			startFretboardPosition = new FretboardPosition(tempStartFretboardPosition);
		}
	}

	/**
	 * Returns the default block of the given interval container.
	 * 
	 * @param intervalContainer
	 *            the intervalContainer, must not be null
	 * 
	 * @return the default block of the given interval container
	 */
	public Block getDefaultBlock(final IntervalContainer intervalContainer) {
		if (intervalContainer == null) {
			throw new IllegalArgumentException();
		}
		final String mode = prefs.getString(Preferences.BLOCK_MODE);

		// create fret block
		if (mode.equals(UIConstants.BLOCK_MODE_FRETS)) {

			int fretRange = prefs.getInt(Preferences.FRET_BLOCK_RANGE);
			final int maxInstrumentRange = getCurrentInstrument().getFretCount() - getCurrentInstrument().getMinFret();
			if (maxInstrumentRange < fretRange) {
				fretRange = maxInstrumentRange;
			}
			final boolean useEmptyStringNotes = prefs.getBoolean(Preferences.FRET_BLOCK_USE_EMPTY_STRINGS);
			final FretBlock fretBlock = new FretBlock(intervalContainer, fretRange, useEmptyStringNotes);
			startFretboardPosition = new FretboardPosition(currentInstrument.getStringCount() - 1,
					fretBlock.getMinFret());
			block = fretBlock;
		}

		// create advanced fret block
		else if (mode.equals(UIConstants.BLOCK_MODE_FRETS_ADVANCED)) {

			int fretRange = prefs.getInt(Preferences.ADVANCED_FRET_BLOCK_RANGE);
			final int maxInstrumentRange = getCurrentInstrument().getFretCount() - getCurrentInstrument().getMinFret();
			if (maxInstrumentRange < fretRange) {
				fretRange = maxInstrumentRange;
			}
			int stringRangeDecrease = prefs.getInt(Preferences.ADVANCED_FRET_BLOCK_STRING_RANGE_DECREASE);
			final boolean useEmptyStringNotes = prefs.getBoolean(Preferences.ADVANCED_FRET_BLOCK_USE_EMPTY_STRINGS);

			final int maxInstrumentStringRange = getCurrentInstrument().getStringCount() - 1;
			if (stringRangeDecrease > maxInstrumentStringRange) {
				stringRangeDecrease = maxInstrumentStringRange;
			}
			final AdvancedFretBlock advancedFretBlock = new AdvancedFretBlock(intervalContainer, fretRange,
					stringRangeDecrease, useEmptyStringNotes);
			startFretboardPosition = new FretboardPosition(advancedFretBlock.getMaxString() - 1,
					advancedFretBlock.getMinFret());
			block = advancedFretBlock;
		}

		// create octave block
		else {

			final FretboardPosition startPosition = determineOctaveBlockDefaultStartPosition(intervalContainer);
			final OctaveBlock octaveBlock = new OctaveBlock(intervalContainer, startPosition);
			startFretboardPosition = getClosestFretboardPosition(intervalContainer);
			block = octaveBlock;
		}
		return block;
	}

	private FretboardPosition determineOctaveBlockDefaultStartPosition(final IntervalContainer intervalContainer) {
		final boolean onlyRootNote = prefs.getBoolean(Preferences.OCTAVE_BLOCK_ONLY_ROOT_NOTES);

		final Set<Integer> noteValues = onlyRootNote ? Collections
				.singleton(intervalContainer.getRootNote().getValue()) : intervalContainer.getNoteValues();

		for (int s = currentInstrument.getStringCount() - 1; s >= 0; s--) {
			for (int f = currentInstrument.getMinFret() + 1; f <= currentInstrument.getFretCount(); f++) {
				final FretboardPosition fbp = new FretboardPosition(s, f);
				if (noteValues.contains(currentInstrument.getNote(fbp).getValue())) {
					return fbp;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the closest start fretboard position to the currently stored one
	 * in regard of the given interval container.
	 * 
	 * @param intervalContainer
	 *            the intervalContainer, must not be null
	 * 
	 * @return the closest start fretboard position to the currently stored one
	 *         in regard of the given interval container
	 */
	private FretboardPosition getClosestFretboardPosition(final IntervalContainer intervalContainer) {
		if (intervalContainer == null) {
			throw new IllegalArgumentException();
		}

		final boolean onlyRootNote = prefs.getBoolean(Preferences.OCTAVE_BLOCK_ONLY_ROOT_NOTES);
		final Set<Integer> noteValues = onlyRootNote ? Collections
				.singleton(intervalContainer.getRootNote().getValue()) : intervalContainer.getNoteValues();

		startFretboardPosition = new FretboardPosition(currentInstrument.getStringCount() - 1,
				currentInstrument.getMinFret() + 1);
		if (noteValues.contains(currentInstrument.getNote(startFretboardPosition).getValue())) {
			return startFretboardPosition;
		}

		FretboardPosition resultPosition = null;

		int distance = Integer.MAX_VALUE;
		for (int f = currentInstrument.getMinFret() + 1; f <= currentInstrument.getFretCount(); f++) {
			for (int s = currentInstrument.getStringCount() - 1; s >= 0; s--) {
				final FretboardPosition fbp = new FretboardPosition(s, f);
				final Note note = currentInstrument.getNote(fbp);
				if (noteValues.contains(note.getValue())) {
					final int currentFretDistance = startFretboardPosition.getFret() - fbp.getFret();
					final int currentStringDistance = startFretboardPosition.getString() - fbp.getString();
					// determine manhattan distance
					final int currentDistance = Math.abs(currentFretDistance) + Math.abs(currentStringDistance);
					if (currentDistance < distance) {
						distance = currentDistance;
						resultPosition = fbp;
					}
				}
			}
		}

		startFretboardPosition = new FretboardPosition(resultPosition);
		return startFretboardPosition;
	}

	/**
	 * Moves the given block into the specified direction.
	 * 
	 * @param block
	 *            the block to move, must not be null
	 * @param direction
	 *            the direction, must be one of the following constants LEFT,
	 *            RIGHT, UP, DOWN
	 * 
	 * @return true if the block was moved, false otherwise
	 */
	public boolean moveBlock(final Block block, final Object direction) {

		// move advanced fret block
		if (block instanceof AdvancedFretBlock) {
			final AdvancedFretBlock advancedFretBlock = (AdvancedFretBlock) block;

			int currentStartString = advancedFretBlock.getMaxString();
			final int tempStartString = currentStartString;
			if (direction == DOWN && getCurrentInstrument().getStringCount() > currentStartString) {
				currentStartString++;
			} else if (direction == UP
					&& currentStartString
							- (getCurrentInstrument().getStringCount() - advancedFretBlock.getStringRangeDecrease()) > 0) {
				currentStartString--;
			}
			startFretboardPosition = new FretboardPosition(currentStartString, advancedFretBlock.getMinFret());
			advancedFretBlock.setStartString(currentStartString);
			if (direction == DOWN || direction == UP) {
				return tempStartString != currentStartString;
			}
		}

		// move (advanced) fret block
		if (block instanceof FretBlock) {
			final FretBlock fretBlock = (FretBlock) block;
			int currentStartFret = fretBlock.getMinFret();
			final int tempStartFret = currentStartFret;
			if (direction == LEFT
					&& getCurrentInstrument().getMinFret() < (fretBlock.useEmptyStrings() ? currentStartFret - 1
							: currentStartFret)) {
				currentStartFret--;
			} else if (direction == RIGHT
					&& currentStartFret + fretBlock.getFretSpan() <= getCurrentInstrument().getFretCount()) {
				currentStartFret++;
			}
			startFretboardPosition = new FretboardPosition(fretBlock.getMaxString(), currentStartFret);
			fretBlock.setMinFret(currentStartFret);
			return tempStartFret != currentStartFret;
		}

		// move octave block
		else if (block instanceof OctaveBlock) {
			final OctaveBlock octaveBlock = (OctaveBlock) block;
			final IntervalContainer intervalContainer = octaveBlock.getIntervalContainer();
			final boolean onlyRootNote = prefs.getBoolean(Preferences.OCTAVE_BLOCK_ONLY_ROOT_NOTES);
			final Set<Integer> noteValues = onlyRootNote ? Collections.singleton(intervalContainer.getRootNote()
					.getValue()) : intervalContainer.getNoteValues();
			final FretboardPosition currentStartPosition = octaveBlock.getStartFretboardPosition();
			FretboardPosition resultPosition = null;

			final int fret = currentStartPosition.getFret();
			// move left
			if (direction == LEFT) {
				for (int f = fret - 1; f > getCurrentInstrument().getMinFret(); f--) {
					final FretboardPosition fbp = new FretboardPosition(currentStartPosition.getString(), f);
					if (noteValues.contains(getCurrentInstrument().getNote(fbp).getValue())) {
						resultPosition = fbp;
						break;
					}
				}
			}
			// move right
			else if (direction == RIGHT) {
				for (int f = fret + 1; f <= getCurrentInstrument().getFretCount(); f++) {
					final FretboardPosition fbp = new FretboardPosition(currentStartPosition.getString(), f);
					if (noteValues.contains(getCurrentInstrument().getNote(fbp).getValue())) {
						resultPosition = fbp;
						break;
					}
				}
			}
			// move up
			else if (direction == UP) {
				final int fMax = Math.max(fret - getCurrentInstrument().getMinFret(), getCurrentInstrument()
						.getFretCount() - fret);
				for (int s = currentStartPosition.getString() - 1; s >= 0; s--) {
					for (int f = 0; f <= fMax; f++) {
						FretboardPosition fbp;
						if (fret - f > getCurrentInstrument().getMinFret()) {
							fbp = new FretboardPosition(s, fret - f);
							if (noteValues.contains(getCurrentInstrument().getNote(fbp).getValue())) {
								resultPosition = fbp;
								break;
							}
						}
						if (fret + f <= getCurrentInstrument().getFretCount()) {
							fbp = new FretboardPosition(s, fret + f);
							if (noteValues.contains(getCurrentInstrument().getNote(fbp).getValue())) {
								resultPosition = fbp;
								break;
							}
						}
					}
					if (resultPosition != null) {
						break;
					}
				}
			}
			// move down
			else if (direction == DOWN) {
				final int fMax = Math.max(fret - getCurrentInstrument().getMinFret(), getCurrentInstrument()
						.getFretCount() - fret);
				for (int s = currentStartPosition.getString() + 1; s < getCurrentInstrument().getStringCount(); s++) {
					for (int f = 0; f <= fMax; f++) {
						FretboardPosition fbp;
						if (fret - f > getCurrentInstrument().getMinFret()) {
							fbp = new FretboardPosition(s, fret - f);
							if (noteValues.contains(getCurrentInstrument().getNote(fbp).getValue())) {
								resultPosition = fbp;
								break;
							}
						}
						if (fret + f <= getCurrentInstrument().getFretCount()) {
							fbp = new FretboardPosition(s, fret + f);
							if (noteValues.contains(getCurrentInstrument().getNote(fbp).getValue())) {
								resultPosition = fbp;
								break;
							}
						}
					}
					if (resultPosition != null) {
						break;
					}
				}
			}
			if (resultPosition != null) {
				startFretboardPosition = resultPosition;
				octaveBlock.setStartFretboardPosition(resultPosition);
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Returns the current instrument.
	 * 
	 * @return the current instrument
	 */
	private Instrument getCurrentInstrument() {
		return InstrumentList.getInstance().getCurrentInstrument();
	}

	public void dispose() {
		InstrumentList.getInstance().removeChangeListener(instrumentListChangeListener);
	}
}
