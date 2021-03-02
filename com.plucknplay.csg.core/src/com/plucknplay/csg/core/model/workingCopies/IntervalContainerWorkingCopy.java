/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.workingCopies;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.ScaleList;

public class IntervalContainerWorkingCopy extends WorkingCopy {

	public static final Object PROP_INTERVAL_ADDED = new Object();
	public static final Object PROP_INTERVALS_ADDED = new Object();
	public static final Object PROP_INTERVAL_REMOVED = new Object();
	public static final Object PROP_INTERVALS_REMOVED = new Object();
	public static final Object PROP_ALSO_KNOWN_AS_STRING_CHANGED = new Object();

	private IntervalContainer workingCopy;
	private final IntervalContainer intervalContainer;
	private final Category category;
	private CategoryList categoryList;
	private boolean newIntervalContainer;

	public IntervalContainerWorkingCopy(final IntervalContainer intervalContainer, final Category category,
			final boolean newIntervalContainer) {
		super();

		if (intervalContainer instanceof Chord) {
			workingCopy = new Chord((Chord) intervalContainer);
			categoryList = ChordList.getInstance();
		} else if (intervalContainer instanceof Scale) {
			workingCopy = new Scale((Scale) intervalContainer);
			categoryList = ScaleList.getInstance();
		}
		this.intervalContainer = intervalContainer;
		this.category = category;
		this.newIntervalContainer = newIntervalContainer;
	}

	protected Categorizable getWorkingCopy(final Categorizable element) {
		if (element instanceof Chord) {
			return new Chord((Chord) element);
		} else if (element instanceof Scale) {
			return new Scale((Scale) element);
		}
		throw new IllegalArgumentException();
	}

	/* --- save --- */

	@Override
	public boolean save() {

		if (!isValidName() || !isValidIntervalList()) {
			return true;
		}
		if (isInvalidName()) {
			return false;
		}

		intervalContainer.setName(workingCopy.getName());
		intervalContainer.setAlsoKnownAsString(workingCopy.getAlsoKnownAsString(), true);
		intervalContainer.setComment(workingCopy.getComment());
		intervalContainer.clearIntervals();
		intervalContainer.addIntervals(workingCopy.getIntervalNames());

		// save instrument
		if (newIntervalContainer) {
			categoryList.addElement(intervalContainer, category);
		} else {
			categoryList.changedElement(intervalContainer);
		}

		setDirty(false);
		newIntervalContainer = false;

		return true;
	}

	@Override
	public boolean saveName() {

		if (isInvalidName()) {
			return false;
		}

		intervalContainer.setName(workingCopy.getName());
		intervalContainer.setAlsoKnownAsString(workingCopy.getAlsoKnownAsString(), true);
		categoryList.changedElement(intervalContainer);
		checkDirty();

		return true;
	}

	/**
	 * Check whether another instrument with this name already exists.
	 * 
	 * @return true if the current name is invalid, false otherwise
	 */
	private boolean isInvalidName() {
		final IntervalContainer intervalContainerWithSameName = (IntervalContainer) categoryList.getElement(workingCopy
				.getName());
		return intervalContainerWithSameName != null && intervalContainerWithSameName != intervalContainer;
	}

	/* --- setters --- */

	public void addInterval(final Interval interval, final String intervalName) {
		workingCopy.addInterval(interval, intervalName);
		notifyListeners(interval, PROP_INTERVAL_ADDED);
	}

	public void addIntervals(final List<Interval> intervals, final List<String> intervalNames) {
		workingCopy.addIntervals(intervals, intervalNames);
		notifyListeners(intervals, PROP_INTERVALS_ADDED);
	}

	public void addIntervals(final Map<Interval, String> intervalNameMap) {
		workingCopy.addIntervals(intervalNameMap);
		notifyListeners(intervalNameMap.keySet(), PROP_INTERVALS_ADDED);
	}

	public void removeInterval(final Interval interval) {
		workingCopy.removeInterval(interval);
		notifyListeners(interval, PROP_INTERVAL_REMOVED);
	}

	public void removeIntervals(final Collection<Interval> intervals) {
		workingCopy.removeIntervals(intervals);
		notifyListeners(intervals, PROP_INTERVALS_REMOVED);
	}

	public void updateIntervalNames() {
		workingCopy.updateIntervalNames();
	}

	public void setAlsoKnownAsString(final String alsoKnownAsString, final boolean updateString) {
		workingCopy.setAlsoKnownAsString(alsoKnownAsString, updateString);

		if (updateString) {
			workingCopy.getAlsoKnownAsNamesList();
		}

		notifyListeners(workingCopy.getAlsoKnownAsString(), PROP_ALSO_KNOWN_AS_STRING_CHANGED);
	}

	public void setName(final String name, final boolean updateString) {
		super.setName(name);
		if (updateString) {
			setAlsoKnownAsString(workingCopy.getAlsoKnownAsString(), updateString);
		}
	}

	/* --- dirty state handling --- */

	@Override
	protected void checkDirty() {
		boolean dirty = !workingCopy.getName().equals(intervalContainer.getName())
				|| !workingCopy.getAlsoKnownAsString().equals(intervalContainer.getAlsoKnownAsString())
				|| !workingCopy.getComment().equals(intervalContainer.getComment())
				|| workingCopy.getIntervals().size() != intervalContainer.getIntervals().size()
				|| !workingCopy.getIntervals().containsAll(intervalContainer.getIntervals());

		if (!dirty) {
			for (final Interval interval : workingCopy.getIntervals()) {
				if (!workingCopy.getIntervalName(interval).equals(intervalContainer.getIntervalName(interval))) {
					dirty = true;
					break;
				}
			}

		}

		setDirty(dirty);
	}

	/* --- getters --- */

	@Override
	protected Categorizable getWorkingCopy() {
		return workingCopy;
	}

	public IntervalContainer getIntervalContainer() {
		return intervalContainer;
	}

	public Collection<Interval> getIntervals() {
		return workingCopy.getIntervals();
	}

	public String getAlsoKnownAsString() {
		return workingCopy.getAlsoKnownAsString();
	}

	public String getIntervalName(final Interval interval) {
		return workingCopy.getIntervalName(interval);
	}

	public boolean isValidIntervalList() {
		return workingCopy.getIntervals().size() > 1;
	}
}
