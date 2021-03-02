/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.plucknplay.csg.core.util.NamesUtil;

/**
 * This class is an abstract container class for intervals.
 * 
 * Interval container may be chords or scales.
 */
public abstract class IntervalContainer extends Categorizable {

	public static final Object TYPE_CHORD = new Object();
	public static final Object TYPE_SCALE = new Object();

	private static final long serialVersionUID = -6995710346080037838L;

	protected Note rootNote;

	private Map<Interval, String> intervalNameMap = new HashMap<Interval, String>();
	private String alsoKnownAsString;
	private List<String> alsoKnownAsList;

	/* --- constructors --- */

	/**
	 * The default constructor.
	 */
	public IntervalContainer() {
		this(""); //$NON-NLS-1$
	}

	/**
	 * The constructor. The root note will be set to 'C'.
	 * 
	 * @param name
	 *            the name of this interval container, must not be null
	 */
	public IntervalContainer(final String name) {
		this(name, null);
	}

	/**
	 * The constructor.
	 * 
	 * @param name
	 *            name of this interval container, must not be null
	 * @param rootNote
	 *            the root note of this interval container, must not be null
	 */
	public IntervalContainer(final String name, final Note rootNote) {
		super(name);
		this.rootNote = rootNote;
		final Interval rootInterval = Factory.getInstance().getInterval(0);
		intervalNameMap.put(rootInterval, NamesUtil.getDefaultIntervalName(rootInterval));
		alsoKnownAsString = ""; //$NON-NLS-1$
	}

	/**
	 * The constructor.
	 * 
	 * @param intervalContainer
	 *            another interval container, must not be null
	 */
	public IntervalContainer(final IntervalContainer intervalContainer) {
		super(intervalContainer);
		rootNote = intervalContainer.rootNote;
		intervalNameMap = new HashMap<Interval, String>(intervalContainer.intervalNameMap);
		alsoKnownAsString = intervalContainer.alsoKnownAsString;
	}

	/* --- intervals --- */

	/**
	 * Adds an <code>Interval</code> to this interval container.
	 * 
	 * @param interval
	 *            an interval, must not be null
	 * @param intervalName
	 *            the name of the given interval, must not be null
	 */
	public void addInterval(final Interval interval, final String intervalName) {
		if (interval == null || intervalName == null) {
			throw new IllegalArgumentException();
		}
		intervalNameMap.put(interval, intervalName);
	}

	/**
	 * Adds a list of <code>Interval</code>'s to this interval container.
	 * 
	 * @param intervals
	 *            a list of intervals, must not be null
	 * @param intervalNames
	 *            the corresponing list of interval names, must not be null,
	 *            must have the same amount of entries
	 */
	public void addIntervals(final List<Interval> intervals, final List<String> intervalNames) {
		if (intervals == null || intervalNames == null || intervals.size() != intervalNames.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < intervals.size(); i++) {
			addInterval(intervals.get(0), intervalNames.get(0));
		}
	}

	/**
	 * Adds a list of of intervals with its corresponding names to this interval
	 * container.
	 * 
	 * @param intervalNameMap
	 *            the intervals with its names, must not be null
	 */
	public void addIntervals(final Map<Interval, String> intervalNameMap) {
		if (intervalNameMap == null) {
			throw new IllegalArgumentException();
		}

		this.intervalNameMap = new HashMap<Interval, String>(intervalNameMap);
	}

	/**
	 * Removes an <code>Interval</code> from the list of intervals.
	 * 
	 * <p>
	 * Note the prime must not be removed.
	 * </p>
	 * 
	 * @param interval
	 *            an interval, must not be null
	 */
	public void removeInterval(final Interval interval) {
		if (interval == null) {
			throw new IllegalArgumentException();
		}

		if (interval.getHalfsteps() != 0) {
			intervalNameMap.remove(interval);
		}
	}

	/**
	 * Removes a list of <code>Interval</code>'s from the list of intervals.
	 * 
	 * @param intervals
	 *            a collection of intervals, must not be null
	 */
	public void removeIntervals(final Collection<Interval> intervals) {
		if (intervals == null) {
			throw new IllegalArgumentException();
		}

		for (final Interval interval : intervals) {
			removeInterval(interval);
		}
	}

	/**
	 * Removes all intervals of this interval container.
	 */
	public void clearIntervals() {
		intervalNameMap = new HashMap<Interval, String>();
	}

	/**
	 * Returns a collection of the intervals of this interval container.
	 * 
	 * @return a collection of the intervals of this interval container
	 */
	public Collection<Interval> getIntervals() {
		return intervalNameMap.keySet();
	}

	public Map<Interval, String> getIntervalNames() {
		return intervalNameMap;
	}

	/**
	 * Returns true if the given interval container contains exactly the same
	 * intervals, false otherwise.
	 * 
	 * @param other
	 *            the other interval container to compare with, must not be null
	 * @return true if the given interval container contains exactly the same
	 *         intervals, false otherwise
	 */
	public boolean hasSameIntervals(final IntervalContainer other) {
		if (other == null) {
			throw new IllegalArgumentException();
		}

		return getIntervals().size() == other.getIntervals().size() && getIntervals().containsAll(other.getIntervals());
	}

	/**
	 * Returns the name of the given interval.
	 * 
	 * @param interval
	 *            the interval, must not be null, must be one of this interval
	 *            containers intervals
	 * 
	 * @return the name of the given interval
	 */
	public String getIntervalName(final Interval interval) {
		if (interval == null) {
			throw new IllegalArgumentException();
		}

		final String intervalName = intervalNameMap.get(interval);
		return intervalName == null ? interval.getDefaultName() : intervalName;
	}

	public void updateIntervalNames() {
		for (final Entry<Interval, String> entry : intervalNameMap.entrySet()) {
			entry.setValue(NamesUtil.translateIntervalName(entry.getValue()));
		}
	}

	/* --- getter & setter --- */

	/**
	 * Returns the readable string for all the names this interval container is
	 * also known as.
	 * 
	 * @return the readable string for all the names this interval container is
	 *         also known as
	 */
	public String getAlsoKnownAsString() {
		return alsoKnownAsString;
	}

	/**
	 * Sets the readable string for all the names this interval container is
	 * also known as.
	 * 
	 * @param alsoKnownAsString
	 *            the new string, must not be null
	 * @param updateString
	 *            true if the given string shall be cleaned up before set
	 */
	public void setAlsoKnownAsString(final String alsoKnownAsString, final boolean updateString) {
		if (alsoKnownAsString == null) {
			throw new IllegalArgumentException();
		}

		this.alsoKnownAsString = alsoKnownAsString;
		alsoKnownAsList = null;

		// update aka string if necessary
		if (updateString) {
			updateAkaString();
		}
	}

	/**
	 * Adds the given also known as name to the aka list.
	 * 
	 * @param akaName
	 *            the also known as name to be added, must not be null
	 */
	public void addAkaName(final String akaName) {
		if (akaName == null) {
			throw new IllegalArgumentException();
		}

		final String aka = akaName.trim().replaceAll(",", "").replaceAll(";", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		if (alsoKnownAsList == null) {
			alsoKnownAsList = new ArrayList<String>();
		}
		if (!alsoKnownAsList.contains(aka) && !getName().equals(aka)) {
			alsoKnownAsList.add(aka);
		}

		createAkaString();
	}

	/**
	 * Removes the given also known as name to the aka list.
	 * 
	 * @param akaName
	 *            the also known as name to be remove, must not be null
	 */
	public void removeAkaName(final String akaName) {
		if (akaName == null) {
			throw new IllegalArgumentException();
		}

		if (alsoKnownAsList == null) {
			return;
		}
		alsoKnownAsList.remove(akaName);
		if (alsoKnownAsList.isEmpty()) {
			alsoKnownAsList = null;
			alsoKnownAsString = ""; //$NON-NLS-1$
		} else {
			createAkaString();
		}
	}

	/**
	 * Creates the also known as string using the corresponding list.
	 */
	private void createAkaString() {
		if (alsoKnownAsList == null || alsoKnownAsList.isEmpty()) {
			return;
		}

		final StringBuffer buffer = new StringBuffer();
		for (final Iterator<String> iter = alsoKnownAsList.iterator(); iter.hasNext();) {
			final String akaName = iter.next();
			buffer.append(akaName);
			if (iter.hasNext()) {
				buffer.append(", "); //$NON-NLS-1$
			}
		}
		alsoKnownAsString = buffer.toString();
	}

	/**
	 * Returns the list of names this interval container is also known as.
	 * 
	 * @return the list of names this interval container is also known as
	 */
	public List<String> getAlsoKnownAsNamesList() {
		if (alsoKnownAsList == null) {
			alsoKnownAsList = new ArrayList<String>();
			if ("".equals(alsoKnownAsString)) {
				return alsoKnownAsList;
			}

			boolean finished = false;
			int startIndex = 0;
			while (!finished) {
				final int endIndex = alsoKnownAsString.indexOf(", ", startIndex); //$NON-NLS-1$
				final String currentAka = endIndex > 0 ? alsoKnownAsString.substring(startIndex, endIndex)
						: alsoKnownAsString.substring(startIndex);
				if (!alsoKnownAsList.contains(currentAka)) {
					alsoKnownAsList.add(currentAka);
				}
				startIndex = endIndex + 2;
				finished = startIndex > alsoKnownAsString.length() || startIndex == 1;
			}
		}
		return alsoKnownAsList;
	}

	/**
	 * This method updates the readable string for all the names this interval
	 * container is also known as. The names shall be comma separated.
	 */
	private void updateAkaString() {
		alsoKnownAsString = alsoKnownAsString.trim();
		// remove all spaces before or after a comma
		while (alsoKnownAsString.contains(", ")) { //$NON-NLS-1$
			alsoKnownAsString = alsoKnownAsString.replaceAll(", ", ","); //$NON-NLS-1$ //$NON-NLS-2$
		}
		while (alsoKnownAsString.contains(" ,")) { //$NON-NLS-1$
			alsoKnownAsString = alsoKnownAsString.replaceAll(" ,", ","); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// trim leading comma
		while (alsoKnownAsString.startsWith(",")) { //$NON-NLS-1$
			alsoKnownAsString = alsoKnownAsString.replaceFirst(",", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// trim ending comma
		while (alsoKnownAsString.endsWith(",")) { //$NON-NLS-1$
			alsoKnownAsString = alsoKnownAsString.substring(0, alsoKnownAsString.length() - 1);
		}
		// remove all doubled commas
		while (alsoKnownAsString.contains(",,")) { //$NON-NLS-1$
			alsoKnownAsString = alsoKnownAsString.replaceAll(",,", ","); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// add space after each comma
		alsoKnownAsString = alsoKnownAsString.replaceAll(",", ", "); //$NON-NLS-1$ //$NON-NLS-2$

		// check whether an aka-name is equal to the main name
		final List<String> namesList = getAlsoKnownAsNamesList();

		// it may happen that more than one main name is included
		while (namesList.contains(getName())) {
			namesList.remove(getName());
		}

		// rebuild string
		alsoKnownAsString = ""; //$NON-NLS-1$
		for (final Iterator<String> iter = namesList.iterator(); iter.hasNext();) {
			final String aka = iter.next();
			alsoKnownAsString += aka;
			if (iter.hasNext()) {
				alsoKnownAsString += ", "; //$NON-NLS-1$
			}
		}
	}

	/**
	 * Returns the notes of this interval container.
	 * 
	 * @return the notes of this interval container
	 */
	public Set<Note> getNotes() {
		return getNotes(Collections.<Interval> emptyList());
	}

	/**
	 * Returns the notes of this interval container.
	 * 
	 * @param excludedIntervals
	 *            the collection of intervals which should be excluded, must not
	 *            be null
	 * 
	 * @return the notes of this interval container
	 */
	public Set<Note> getNotes(final Collection<Interval> excludedIntervals) {
		if (excludedIntervals == null) {
			throw new IllegalArgumentException();
		}

		if (getRootNote() == null) {
			return new HashSet<Note>();
		}

		final Set<Note> result = new HashSet<Note>();
		if (!excludedIntervals.contains(Factory.getInstance().getInterval(0))) {
			result.add(getRootNote());
		}
		for (final Interval next : getIntervals()) {
			if (!excludedIntervals.contains(next)) {
				final Note calcNote = getRootNote().calcNote(next);
				result.add(Factory.getInstance().getNote(calcNote.getValue()));
			}
		}
		return result;
	}

	/**
	 * Returns the note values of this interval container.
	 * 
	 * @return the note values of this interval container
	 */
	public Set<Integer> getNoteValues() {
		return getNoteValues(Collections.<Interval> emptyList());
	}

	/**
	 * Returns the note values of this interval container.
	 * 
	 * @param excludedIntervals
	 *            the collection of intervals which should be excluded, must not
	 *            be null
	 * 
	 * @return the note values of this interval container
	 */
	public Set<Integer> getNoteValues(final Collection<Interval> excludedIntervals) {
		if (excludedIntervals == null) {
			throw new IllegalArgumentException();
		}

		final Set<Integer> result = new HashSet<Integer>();
		for (final Note note : getNotes(excludedIntervals)) {
			result.add(note.getValue());
		}
		return result;
	}

	public abstract Note getRootNote();

	/**
	 * Sets the root note of this interval containerinterval container.
	 * 
	 * @param rootNote
	 *            the new root note, must not be null
	 */
	public void setRootNote(final Note rootNote) {
		if (rootNote == null) {
			throw new IllegalArgumentException();
		}
		this.rootNote = rootNote;
	}

	/**
	 * Returns the type of this interval container. Use IntervalContainer.TYPE_*
	 * as constants.
	 * 
	 * @return the type of this interval container
	 */
	public abstract Object getType();

	/**
	 * Returns the beautified name of this interval container.
	 * 
	 * @param notesMode
	 *            the notes mode that shall be used to render this name, must be
	 *            of Constants.NOTES_MODE_*
	 * 
	 * @return the beautified name of this interval container
	 */
	public String getBeautifiedName(final String notesMode) {
		return NamesUtil.getNameProvider().getName(this, notesMode);
	}

	/* --- object methods --- */

	@Override
	public String toString() {
		String result = getRootNote().toString();
		if (!"".equals(getName())) {
			result += " - " + getName(); //$NON-NLS-1$
		}
		return result;
	}
}
