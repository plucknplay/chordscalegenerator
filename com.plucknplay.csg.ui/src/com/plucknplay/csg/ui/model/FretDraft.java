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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.IFingerNumberProvider;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public abstract class FretDraft extends Draft {

	/**
	 * Property which indicates that the assignment of a draft has changed.
	 * 
	 * <p>
	 * The new assignment will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_ASSIGNMENT_CHANGED = new Object();

	/**
	 * Property which indicates that the show barre setting of a draft has
	 * changed.
	 * 
	 * <p>
	 * The new setting will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_SHOW_BARRE_CHANGED = new Object();

	public static final Integer NO_FINGER = Integer.valueOf(5);

	public static final Integer UNKNOWN_FINGER = Integer.valueOf(6);

	/**
	 * The assignment of this draft.
	 * 
	 * Key: the relative fret Value: the list of finger numbers
	 */
	private List<List<Integer>> assignments;

	/**
	 * The instrument this draft was created with.
	 */
	private final Instrument currentInstrument;

	private boolean showBarre = true;

	private List<Barre> barres;

	private Comparator<FretboardPosition> fretboardPositionComparator;

	public FretDraft(final FretDraft other) {
		super(other);
		currentInstrument = other.currentInstrument;
		showBarre = other.showBarre;

		// clone barres
		barres = null;
		if (other.barres != null) {
			barres = new ArrayList<FretDraft.Barre>(other.barres);
		}

		// clone assignments
		assignments = null;
		if (other.assignments != null) {
			assignments = new ArrayList<List<Integer>>();
			for (final List<Integer> list : other.assignments) {
				assignments.add(new ArrayList<Integer>(list));
			}
		}
	}

	protected FretDraft() {
		this((IBlock) null);
	}

	protected FretDraft(final IBlock input) {
		super(input);
		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
	}

	protected void initAssignments(final IBlock block) {
		initStartFretAndSpan(block);
		clear();

		// assign finger numbers
		final IFingerNumberProvider fingerNumberProvider = (IFingerNumberProvider) (block instanceof Griptable ? ((Griptable) block)
				.getFingering(Activator.getDefault().getPreferenceStore()
						.getBoolean(Preferences.CALCULATOR_BARRES_PREFERRED)) : block);

		for (final FretboardPosition fbp : block.getFretboardPositions()) {

			int relativeFret = fbp.getFret() - getStartFret() + 1;
			if (relativeFret < 0) {
				relativeFret = 0;
			}

			final Byte fingerNumber = fingerNumberProvider.getFingerNumber(fbp);
			setAssignment(relativeFret, fbp.getString() + 1,
					fingerNumber == null ? UNKNOWN_FINGER : Integer.valueOf(fingerNumber));
		}
		setShowBarre(block instanceof Griptable);
	}

	protected void initStartFretAndSpan(final IBlock block) {
	}

	public Instrument getCurrentInstrument() {
		return currentInstrument;
	}

	/**
	 * Returns the start fret of this draft.
	 * 
	 * @return the start fret of this draft
	 */
	public abstract int getStartFret();

	/**
	 * Returns the fret width of this draft.
	 * 
	 * @return the fret width of this draft
	 */
	public abstract int getFretWidth();

	protected void adjustAssignments(final int shift) {

		if (assignments == null) {
			clear();
		}

		final int difference = getFretWidth() - assignments.size() + 1;

		// remove last frets
		if (difference < 0) {
			for (int i = 0; i < Math.abs(difference); i++) {
				assignments.remove(assignments.size() - 1);
			}
		}

		// add new frets
		else if (difference > 0) {
			for (int i = 0; i < difference; i++) {
				if (shift > 0) {
					assignments.add(1, getInitialList());
				} else {
					assignments.add(getInitialList());
				}
			}
		} else if (difference == 0) {
			for (int i = 0; i < shift; i++) {
				assignments.add(1, getInitialList());
				assignments.remove(assignments.size() - 1);
			}
		}

		// update root note
		setRootNote(false);

		// notify listener
		notifyListeners(PROP_ASSIGNMENT_CHANGED, assignments);
	}

	/**
	 * Clears this draft.
	 */
	protected void clear() {
		assignments = new ArrayList<List<Integer>>();
		for (int i = 0; i <= getFretWidth(); i++) {
			assignments.add(getInitialList());
		}
		setModifiedInput(true);
		setRootNote(null);

		// notify listener
		notifyListeners(PROP_ASSIGNMENT_CHANGED, assignments);
	}

	/**
	 * Returns an inital list for the assignment map.
	 * 
	 * @return an inital list for the assignment map
	 */
	private List<Integer> getInitialList() {
		final ArrayList<Integer> result = new ArrayList<Integer>();
		if (currentInstrument == null) {
			return result;
		}
		for (int i = 0; i < currentInstrument.getStringCount(); i++) {
			result.add(NO_FINGER);
		}
		return result;
	}

	/**
	 * Sets a single assignment to this draft.
	 * 
	 * @param relativeFret
	 *            the relative fret, must be a value between 0 and fret width
	 * @param string
	 *            the string number, must be a value between 1 and the current
	 *            instruments string count
	 * @param fingerNumber
	 *            the finger number of this assignment, must not be null, must
	 *            be a value between 1 and 4 or UNKNOWN_FINGER or NO_FINGER
	 */
	public void setAssignment(final int relativeFret, final int string, final Integer fingerNumber) {
		if (currentInstrument == null) {
			return;
		}

		final int theRelativeFret = getFret(relativeFret);
		if (theRelativeFret < 0 || theRelativeFret > getFretWidth() || string < 1
				|| string > currentInstrument.getStringCount()
				|| (fingerNumber.intValue() < 0 || fingerNumber.intValue() > 4) && !UNKNOWN_FINGER.equals(fingerNumber)
				&& !NO_FINGER.equals(fingerNumber)) {
			throw new IllegalArgumentException();
		}

		final int oldFingerNumber = assignments.get(theRelativeFret).get(string - 1);
		if (fingerNumber != oldFingerNumber) {
			assignments.get(theRelativeFret).set(string - 1, fingerNumber);
			setModifiedInput(true);
			setRootNote(false);
			notifyListeners(PROP_ASSIGNMENT_CHANGED, assignments);
		}
	}

	/**
	 * Toggles a single assignment of this draft.
	 * 
	 * @param relativeFret
	 *            the relative fret, must be a value between 0 and fret width
	 * @param string
	 *            the string number, must be a value between 1 and the current
	 *            instruments string count
	 * @param relativeMode
	 *            <code>true</code> if all relative notes shall be additionally
	 *            assigned/removed to/from this draft, <code>false</code>
	 *            otherwise
	 */
	public void toggleAssignment(final int relativeFret, final int string, final boolean relativeMode) {
		final Integer currentFingerNumber = getAssignment(relativeFret, string);
		final Integer newFingerNumber = NO_FINGER.equals(currentFingerNumber) ? UNKNOWN_FINGER : NO_FINGER;

		if (!relativeMode) {
			setAssignment(getFret(relativeFret), string, newFingerNumber);
		} else {
			final Note note = getNote(relativeFret, string - 1);
			for (int s = 0; s < currentInstrument.getStringCount(); s++) {
				for (int f = 0; f <= getFretWidth(); f++) {
					if (note.getValue() == getNote(f, s).getValue()) {
						setAssignment(f, s + 1, newFingerNumber);
					}
				}
			}
		}
	}

	private Note getNote(final int relativeFret, final int string) {
		return currentInstrument.getNote(new FretboardPosition(string, relativeFret > 0 ? getStartFret()
				+ getFret(relativeFret) - 1 : 0));
	}

	protected int getFret(final int relativeFret) {
		return relativeFret;
	}

	/**
	 * Returns the assignments of this draft.
	 * 
	 * @return the assignments of this draft
	 */
	public List<List<Integer>> getAssignments() {
		return assignments;
	}

	@Override
	public Collection<Griptable> getGriptables() {
		final Collection<Griptable> results = new ArrayList<Griptable>();
		final Chord firstChord = (Chord) ChordList.getInstance().getRootCategory().getFirstElement();
		if (firstChord == null || !isPotentialGriptable()) {
			return results;
		}

		final Griptable result = new Griptable(firstChord);
		for (final FretboardPosition fbp : getFretboardPositions()) {
			result.setValue((byte) fbp.getFret(), fbp.getString());
		}

		results.add(result);
		return results;
	}

	@Override
	public Collection<Note> getRelativeNotes() {
		final List<Note> result = new ArrayList<Note>();
		for (final FretboardPosition fbp : getFretboardPositions()) {
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
	public boolean isPotentialGriptable() {
		if (currentInstrument == null) {
			return false;
		}
		final Map<Integer, Boolean> helperMap = new HashMap<Integer, Boolean>();
		for (int i = 0; i < currentInstrument.getStringCount(); i++) {
			helperMap.put(i, false);
		}
		for (int i = 0; i <= getFretWidth(); i++) {
			final List<Integer> list = assignments.get(i);
			for (int s = 0; s < currentInstrument.getStringCount(); s++) {
				final Integer fingerNumber = list.get(s);
				if (!NO_FINGER.equals(fingerNumber)) {
					if (helperMap.get(s)) {
						return false;
					}
					helperMap.put(s, true);
				}
			}
		}
		return true;
	}

	/**
	 * Returns the assignments of the given string. Note: the list does not
	 * contain {@link #NO_FINGER} assignents.
	 * 
	 * @param string
	 *            the string, must be a value between 1 and the current
	 *            instruments string count
	 * @param includeOpenStrings
	 *            <code>true</code> if assignments of open string shall be
	 *            added, <code>false</code> otherwise
	 * 
	 * @return the assignments of the given string
	 */
	public List<Integer> getAssignments(final int string, final boolean includeOpenStrings) {
		final List<Integer> result = new ArrayList<Integer>();
		if (currentInstrument == null) {
			return result;
		}

		for (int f = 0; f <= getFretWidth(); f++) {
			if (!includeOpenStrings && f == 0) {
				continue;
			}
			final Integer fingerNumber = getAssignment(f, string);
			if (!NO_FINGER.equals(fingerNumber)) {
				result.add(fingerNumber);
			}
		}
		return result;
	}

	/**
	 * Returns a list with the strings that are occupied with an assignment.
	 * 
	 * <p>
	 * Note: The list is sorted in reverse order.
	 * </p>
	 * 
	 * 
	 * @param fret
	 *            the relative fret number, must be a value between 0 and
	 *            {@link #getFretWidth()}.
	 * 
	 * @return a list with the strings that are occupied with an assignment,
	 *         never <code>null</code>
	 */
	public List<Integer> getOccupiedStrings(final int fret) {
		final List<Integer> result = new ArrayList<Integer>();
		if (currentInstrument == null || fret < 0 || fret > getFretWidth()) {
			return result;
		}

		for (int s = currentInstrument.getStringCount(); s > 0; s--) {
			final Integer assignment = getAssignment(fret, s);
			if (assignment != null && !NO_FINGER.equals(assignment)) {
				result.add(s);
			}
		}
		return result;
	}

	/**
	 * Returns the defined fretboard positions of this draft.
	 * 
	 * @return the defined fretboard positions of this draft, never
	 *         <code>null</code>
	 */
	public List<FretboardPosition> getFretboardPositions() {
		return getFretboardPositions(true);
	}

	public List<FretboardPosition> getFretboardPositions(final boolean includeOpenStrings) {
		final List<FretboardPosition> result = new ArrayList<FretboardPosition>();
		if (currentInstrument == null || assignments == null) {
			return result;
		}

		// check empty fret
		List<Integer> list;
		if (includeOpenStrings) {
			list = assignments.get(0);
			for (int s = 0; s < currentInstrument.getStringCount(); s++) {
				if (!NO_FINGER.equals(list.get(s))) {
					result.add(new FretboardPosition(s, currentInstrument.getCapoFret(s + 1)));
				}
			}
		}

		// check other frets
		for (int f = 1; f <= getFretWidth(); f++) {
			list = assignments.get(f);
			for (int s = 0; s < currentInstrument.getStringCount(); s++) {
				if (!NO_FINGER.equals(list.get(s))) {
					result.add(new FretboardPosition(s, f + getStartFret() - 1));
				}
			}
		}

		return result;
	}

	/**
	 * Returns the assignment of the given relative fret and string.
	 * 
	 * @param relativeFret
	 *            must be a value between 0 and the fret width
	 * @param string
	 *            the string, must be a value between 1 and the current
	 *            instruments string count
	 * 
	 * @return the assignment of the given relative fret and string
	 */
	public Integer getAssignment(final int relativeFret, final int string) {
		if (currentInstrument == null) {
			return Integer.valueOf(0);
		}

		final int theRelativeFret = getFret(relativeFret);
		if (theRelativeFret < 0 || theRelativeFret > getFretWidth() || string < 1
				|| string > currentInstrument.getStringCount()) {
			throw new IllegalArgumentException();
		}

		final List<Integer> list = assignments.get(theRelativeFret);
		return list.get(string - 1);
	}

	@Override
	public boolean isEmpty() {
		if (assignments == null) {
			return true;
		}
		for (final List<Integer> list : assignments) {
			for (final Integer value : list) {
				if (!NO_FINGER.equals(value)) {
					return false;
				}
			}
		}
		return true;
	}

	public void setShowBarre(final boolean showBarre) {
		final boolean oldValue = this.showBarre;
		this.showBarre = showBarre;
		if (oldValue != this.showBarre) {
			notifyListeners(PROP_SHOW_BARRE_CHANGED, this.showBarre);
		}
	}

	public boolean getShowBarre() {
		return showBarre;
	}

	@Override
	protected void setModifiedInput(final boolean modifiedInput) {
		super.setModifiedInput(modifiedInput);
		barres = null;
	}

	public List<Barre> getBarres() {
		if (barres == null) {
			barres = new ArrayList<Barre>();
			if (currentInstrument == null) {
				return barres;
			}

			final int stringCount = currentInstrument.getStringCount();
			final Set<Integer> occupiedStrings = new HashSet<Integer>(getOccupiedStrings(0));

			for (int f = 1; f <= getFretWidth(); f++) {

				Integer lastAssignment = null;
				int minString = stringCount + 1;
				int maxString = -1;

				for (int s = stringCount; s > 0; s--) {

					final Integer currentAssignment = getAssignment(f, s);
					if (FretDraft.NO_FINGER.equals(currentAssignment)) {
						if (occupiedStrings.contains(s)) {
							createBarreHelper(f, minString, maxString);
							lastAssignment = null;
							minString = stringCount + 1;
							maxString = -1;
						}
						continue;
					}
					if (lastAssignment == null || !lastAssignment.equals(currentAssignment)) {
						createBarreHelper(f, minString, maxString);
						lastAssignment = currentAssignment;
						minString = stringCount + 1;
						maxString = -1;
					}
					if (s > maxString) {
						maxString = s;
					}
					if (s < minString) {
						minString = s;
					}
					occupiedStrings.add(s);
				}
				createBarreHelper(f, minString, maxString);
			}
		}
		return barres;
	}

	private void createBarreHelper(final int relativeFret, final int minString, final int maxString) {
		if (maxString > minString) {
			final Barre barreHelper = new Barre(relativeFret, minString, maxString);
			barres.add(barreHelper);
		}
	}

	public static class Barre {

		private final int relativeFret;
		private final int minString;
		private final int maxString;

		public Barre(final int relativeFret, final int minString, final int maxString) {
			this.relativeFret = relativeFret;
			this.minString = minString;
			this.maxString = maxString;
		}

		public int getRelativeFret() {
			return relativeFret;
		}

		public int getMinString() {
			return minString;
		}

		public int getMaxString() {
			return maxString;
		}

		public Set<Integer> getOccupiedStrings() {
			final Set<Integer> result = new HashSet<Integer>();
			for (int s = minString; s <= maxString; s++) {
				result.add(s);
			}
			return result;
		}
	}

	public FretboardPosition getNextRelativeFretboardPosition(final FretboardPosition currentFbp) {

		// determine sort list of fretboard positions
		final List<FretboardPosition> fretboardPositions = getFretboardPositions(false);
		Collections.sort(fretboardPositions, getFretboardPositionComparator());

		// determine next fretboard position
		final int index = fretboardPositions.indexOf(new FretboardPosition(currentFbp.getString(), currentFbp.getFret()
				+ getStartFret() - 1));
		if (index >= 0 && fretboardPositions.size() > 1) {
			final FretboardPosition nextFbp = fretboardPositions.get(index < fretboardPositions.size() - 1 ? index + 1
					: 0);
			return new FretboardPosition(nextFbp.getString(), nextFbp.getFret() - getStartFret() + 1);
		}
		return null;
	}

	public Comparator<FretboardPosition> getFretboardPositionComparator() {
		if (fretboardPositionComparator == null) {
			fretboardPositionComparator = new Comparator<FretboardPosition>() {
				@Override
				public int compare(final FretboardPosition fbp1, final FretboardPosition fbp2) {
					int result = Integer.valueOf(fbp1.getString()).compareTo(Integer.valueOf(fbp2.getString()));
					if (Activator.getDefault().getPreferenceStore()
							.getBoolean(Preferences.VIEWS_SEARCH_MODE_FAST_EDITING_DEEP_TO_HIGH)) {
						result = result * -1;
					}
					if (result == 0) {
						result = Integer.valueOf(fbp1.getFret()).compareTo(Integer.valueOf(fbp2.getFret()));
					}
					return result;
				}
			};
		}
		return fretboardPositionComparator;
	}
}
