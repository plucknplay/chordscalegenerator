/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.workingCopies;

import java.util.HashMap;
import java.util.Map;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.sets.Category;

public final class WorkingCopyManager {

	private static WorkingCopyManager instance;

	private final Map<Categorizable, WorkingCopy> categorizableMap;
	private final Map<Categorizable, Integer> categorizableReferenceCounterMap;

	private WorkingCopyManager() {
		categorizableMap = new HashMap<Categorizable, WorkingCopy>();
		categorizableReferenceCounterMap = new HashMap<Categorizable, Integer>();
	}

	public static WorkingCopyManager getInstance() {
		if (instance == null) {
			instance = new WorkingCopyManager();
		}
		return instance;
	}

	/* --- working copies --- */

	public WorkingCopy getWorkingCopy(final Categorizable element, final Category category, final boolean newElement) {
		WorkingCopy workingCopy = categorizableMap.get(element);
		if (workingCopy == null) {
			workingCopy = getNewWorkingCopy(element, category, newElement);
			categorizableMap.put(element, workingCopy);
		}
		// count the references
		Integer counter = categorizableReferenceCounterMap.get(element);
		if (counter == null) {
			counter = Integer.valueOf(1);
		} else {
			counter = Integer.valueOf(counter.intValue() + 1);
		}
		categorizableReferenceCounterMap.put(element, counter);

		return workingCopy;
	}

	private WorkingCopy getNewWorkingCopy(final Categorizable element, final Category category, final boolean newElement) {
		if (element instanceof Instrument) {
			return new InstrumentWorkingCopy((Instrument) element, category, newElement);
		}
		if (element instanceof IntervalContainer) {
			return new IntervalContainerWorkingCopy((IntervalContainer) element, category, newElement);
		}
		throw new IllegalArgumentException();
	}

	public void disposeWorkingCopy(final Categorizable element) {
		Integer counter = categorizableReferenceCounterMap.get(element);
		if (counter == null || counter.intValue() == 1) {
			categorizableReferenceCounterMap.remove(element);
			categorizableMap.remove(element);
		} else {
			counter = Integer.valueOf(counter.intValue() - 1);
			categorizableReferenceCounterMap.put(element, counter);
		}
	}
}
