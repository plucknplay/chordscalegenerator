/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class TabDraft extends Draft {

	/**
	 * Property which indicates that several fretboard positions of a tab draft
	 * were changed.
	 * 
	 * <p>
	 * The whole changed tab draft will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_FRETBOARD_POSITIONS_CLEARED = new Object();

	/**
	 * Property which indicates that a fretboard position was added to a tab
	 * draft.
	 * 
	 * <p>
	 * The added fretboard position will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_FRETBOARD_POSITION_CHANGED = new Object();

	/**
	 * Property which indicates that the column number of a tab draft has
	 * changed.
	 * 
	 * <p>
	 * The new column number will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_COLUMN_NUMBER_CHANGED = new Object();

	/**
	 * The number of columns this tab draft is composed of.
	 */
	private int numberOfColumns;

	/**
	 * <ul>
	 * <li>Key: the column number (value between 1 and
	 * {@value FretBlock#MAX_FRET_RANGE})</li>
	 * <li>Value: list of fretboard positions</li>
	 * </ul>
	 */
	private Map<Integer, List<FretboardPosition>> fretboardPositions;

	/**
	 * The default constructor.
	 * 
	 * <p>
	 * Creates a tab draft composed of one (empty) column.
	 * </p>
	 */
	public TabDraft() {
		super((IBlock) null);
		setNumberOfColumns(1);
		setModifiedInput(false);
	}

	/**
	 * The constructor.
	 * 
	 * @param other
	 *            another tab draft
	 */
	public TabDraft(final TabDraft other) {
		super(other);
		numberOfColumns = other.numberOfColumns;

		// clone fretboard positions
		fretboardPositions = null;
		if (other.fretboardPositions != null) {
			fretboardPositions = new HashMap<Integer, List<FretboardPosition>>();
			for (final Entry<Integer, List<FretboardPosition>> entry : other.fretboardPositions.entrySet()) {
				fretboardPositions.put(entry.getKey(), new ArrayList<FretboardPosition>(entry.getValue()));
			}
		}
	}

	/**
	 * The constructor.
	 * 
	 * <p>
	 * Creates a tab draft out of a griptable.
	 * </p>
	 * 
	 * @param griptable
	 *            the griptable, must not be null
	 */
	public TabDraft(final Griptable griptable) {
		super(griptable);

		if (griptable == null) {
			throw new IllegalArgumentException();
		}

		setNumberOfColumns(1);
		fretboardPositions.put(1, new ArrayList<FretboardPosition>(griptable.getFretboardPositions(true)));
		initRootNote(griptable);
		setModifiedInput(false);
	}

	/**
	 * The constructor.
	 * 
	 * <p>
	 * Creates a tab draft out of a block.
	 * </p>
	 * 
	 * @param block
	 *            the block, must not be null
	 */
	public TabDraft(final Block block) {
		super(block);

		if (block == null) {
			throw new IllegalArgumentException();
		}

		final boolean hasEmptyStringNotes = block.hasEmptyStringNotes();
		int fretRange = block.getFretSpan();
		if (hasEmptyStringNotes) {
			fretRange++;
		}
		setNumberOfColumns(fretRange);

		for (final FretboardPosition fbp : block.getFretboardPositions()) {
			int relativeFret = fbp.getFret() - block.getMinFret() + 1;
			if (hasEmptyStringNotes) {
				relativeFret++;
			}
			if (relativeFret < 1) {
				relativeFret = 1;
			}

			final List<FretboardPosition> list = fretboardPositions.get(relativeFret);
			if (!list.contains(fbp)) {
				list.set(fbp.getString(), fbp);
			}
		}
		initRootNote(block);
		setModifiedInput(false);
	}

	/**
	 * Sets a fretboard position og this tab draft.
	 * 
	 * <p>
	 * Note if the given fretboard position is already added to this tab draft
	 * this method has no effect.
	 * </p>
	 * 
	 * <p>
	 * Note if this tab draft already contains a fretboard position for the
	 * given column number with the same string the given fretboard position
	 * will replace the existing one.
	 * </p>
	 * 
	 * @param column
	 *            the column where the fretboard position shall be added, must
	 *            be a value between 1 and the column number of this tab draft
	 * @param newFbp
	 *            the new fretboard position, must not be null
	 */
	public void setFretboardPostion(final int column, final FretboardPosition newFbp) {

		if (column < 1 || column > numberOfColumns || newFbp == null) {
			throw new IllegalArgumentException();
		}

		// add fretboard position
		final FretboardPosition currentFbp = fretboardPositions.get(column).get(newFbp.getString());
		if (currentFbp.getFret() == newFbp.getFret()) {
			return;
		}
		currentFbp.setFret(newFbp.getFret());
		notifyListeners(PROP_FRETBOARD_POSITION_CHANGED, currentFbp);

		setRootNote(false);
	}

	/**
	 * Returns the number of columns this tab draft is composed of.
	 * 
	 * @return the number of columns this tab draft is composed of
	 */
	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	/**
	 * Sets the number of columns this tab draft is composed of.
	 * 
	 * <p>
	 * Note this method discards all previously set fretboard positions for
	 * higher column number than the given one.
	 * </p>
	 * 
	 * @param numberOfColumns
	 *            the number of columns, must be a value between 1 and
	 *            {@value FretBlock#MAX_FRET_RANGE}
	 */
	public void setNumberOfColumns(final int numberOfColumns) {

		if (numberOfColumns < 1 || numberOfColumns > FretBlock.MAX_FRET_RANGE + 1) {
			throw new IllegalArgumentException();
		}

		if (this.numberOfColumns == numberOfColumns) {
			return;
		}

		// init fretboard positions map if necessary
		if (fretboardPositions == null) {
			fretboardPositions = new HashMap<Integer, List<FretboardPosition>>();
		}

		// init new columns
		if (this.numberOfColumns < numberOfColumns) {
			for (int i = this.numberOfColumns + 1; i <= numberOfColumns; i++) {
				fretboardPositions.put(i, getInitialList());
			}
		}

		// set new value
		this.numberOfColumns = numberOfColumns;

		// remove truncated columns
		for (int i = numberOfColumns + 1; i <= FretBlock.MAX_FRET_RANGE + 1; i++) {
			fretboardPositions.remove(i);
		}

		// notify listeners
		notifyListeners(PROP_COLUMN_NUMBER_CHANGED, Integer.valueOf(this.numberOfColumns));

		setRootNote(false);
	}

	/**
	 * Returns a list of (muted) muted positions.
	 * 
	 * @return a list of (muted) muted positions
	 */
	private List<FretboardPosition> getInitialList() {
		final ArrayList<FretboardPosition> result = new ArrayList<FretboardPosition>();
		if (InstrumentList.getInstance().getCurrentInstrument() == null) {
			return result;
		}

		for (int s = 0; s < InstrumentList.getInstance().getCurrentInstrument().getStringCount(); s++) {
			result.add(new FretboardPosition(s, -1));
		}
		return result;
	}

	/**
	 * Returns a list of all tab draft fretboard positions of this tab draft.
	 * 
	 * @return a list of all tab draft fretboard positions of this tab draft
	 */
	public List<TabDraftFretboardPosition> getTabDraftFretboardPositions() {
		final List<TabDraftFretboardPosition> result = new ArrayList<TabDraftFretboardPosition>();
		for (int c = 1; c <= numberOfColumns; c++) {
			result.addAll(getTabDraftFretboardPositions(c));
		}
		return result;
	}

	public List<TabDraftFretboardPosition> getTabDraftFretboardPositions(final int column) {
		if (column < 1 || column > numberOfColumns) {
			throw new IllegalArgumentException();
		}
		final List<TabDraftFretboardPosition> result = new ArrayList<TabDraftFretboardPosition>();
		for (final FretboardPosition fbp : fretboardPositions.get(column)) {
			result.add(new TabDraftFretboardPosition(this, column, fbp));
		}
		return result;
	}

	@Override
	public boolean isPotentialGriptable() {
		if (InstrumentList.getInstance().getCurrentInstrument() == null) {
			return false;
		}

		final Map<Integer, Boolean> helperMap = new HashMap<Integer, Boolean>();
		for (int i = 0; i < InstrumentList.getInstance().getCurrentInstrument().getStringCount(); i++) {
			helperMap.put(i, false);
		}
		for (final TabDraftFretboardPosition tabDraftFretboardPosition : getTabDraftFretboardPositions()) {
			final FretboardPosition fbp = tabDraftFretboardPosition.getFretboardPosition();
			if (helperMap.get(fbp.getString()) && fbp.getFret() != -1) {
				return false;
			}
			if (fbp.getFret() != -1) {
				helperMap.put(fbp.getString(), true);
			}
		}
		return true;
	}

	@Override
	public Collection<Griptable> getGriptables() {
		final Collection<Griptable> results = new ArrayList<Griptable>();

		final Chord firstChord = (Chord) ChordList.getInstance().getRootCategory().getFirstElement();
		if (firstChord == null) {
			return results;
		}
		if (!isPotentialGriptable()) {
			return results;
		}

		final Griptable result = new Griptable(firstChord);
		for (final List<FretboardPosition> fbps : fretboardPositions.values()) {
			for (final FretboardPosition fbp : fbps) {
				if (fbp.getFret() != -1) {
					result.setValue((byte) fbp.getFret(), fbp.getString());
				}
			}
		}

		results.add(result);
		return results;
	}

	@Override
	public Collection<Note> getRelativeNotes() {
		final List<Note> result = new ArrayList<Note>();
		for (final TabDraftFretboardPosition tabDraftFretboardPosition : getTabDraftFretboardPositions()) {
			final FretboardPosition fbp = tabDraftFretboardPosition.getFretboardPosition();
			if (fbp.getFret() == -1) {
				continue;
			}
			final Note note = Factory.getInstance().getNote(
					InstrumentList.getInstance().getCurrentInstrument().getNote(fbp).getValue());
			if (!result.contains(note)) {
				result.add(note);
			}
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		for (final List<FretboardPosition> list : fretboardPositions.values()) {
			for (final FretboardPosition fbp : list) {
				if (fbp.getFret() != -1) {
					return false;
				}
			}
		}
		return true;
	}

	public TabDraftFretboardPosition getNextTabDraftFretboardPosition(final TabDraftFretboardPosition currentTabDraftFbp) {
		final boolean deepToHigh = Activator.getDefault().getPreferenceStore()
				.getBoolean(Preferences.VIEWS_SEARCH_MODE_FAST_EDITING_DEEP_TO_HIGH);

		// determine sort list of fretboard positions
		final List<TabDraftFretboardPosition> fbps = getTabDraftFretboardPositions();
		Collections.sort(fbps, new Comparator<TabDraftFretboardPosition>() {
			@Override
			public int compare(final TabDraftFretboardPosition tabDraftFbp1,
					final TabDraftFretboardPosition tabDraftFbp2) {
				int result = Integer.valueOf(tabDraftFbp1.getFretboardPosition().getString()).compareTo(
						Integer.valueOf(tabDraftFbp2.getFretboardPosition().getString()));
				if (deepToHigh) {
					result = result * -1;
				}
				if (result == 0) {
					result = Integer.valueOf(tabDraftFbp1.getColumn()).compareTo(
							Integer.valueOf(tabDraftFbp2.getColumn()));
				}
				return result;
			}
		});

		// determine next fretboard position
		final int index = fbps.indexOf(currentTabDraftFbp);
		if (index >= 0 && fbps.size() > 1) {
			return fbps.get(index < fbps.size() - 1 ? index + 1 : 0);
		}
		return null;
	}
}
