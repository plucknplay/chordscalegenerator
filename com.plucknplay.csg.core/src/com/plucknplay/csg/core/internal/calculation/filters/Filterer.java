/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.calculation.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.plucknplay.csg.core.model.Griptable;

public class Filterer {

	private List<IFilter> filters;

	/**
	 * Adds the given filter to this filterer.
	 * 
	 * @param filter
	 *            the filter to add, must not be null
	 */
	public void addFilter(final IFilter filter) {
		if (filter == null) {
			throw new IllegalArgumentException();
		}
		if (filters == null) {
			filters = new ArrayList<IFilter>();
		}
		if (!filters.contains(filter)) {
			filters.add(filter);
		}
	}

	/**
	 * Removes the given filter from this filterer.
	 * 
	 * @param filter
	 *            the filter to remove, must not be null
	 */
	public void removeFilter(final IFilter filter) {
		if (filter == null) {
			throw new IllegalArgumentException();
		}
		filters.remove(filter);
		if (filters.isEmpty()) {
			filters = null;
		}
	}

	/**
	 * Starts the filtering process.
	 * 
	 * @param griptables
	 *            the griptables to filter, must not be null
	 * @param monitor
	 *            the monitor, must not be null
	 * 
	 * @throws InterruptedException
	 */
	public void startFiltering(final Collection<Griptable> griptables, final IProgressMonitor monitor)
			throws InterruptedException {
		if (filters == null || filters.isEmpty()) {
			return;
		}

		if (griptables == null || monitor == null) {
			throw new IllegalArgumentException();
		}

		for (final Iterator<Griptable> iter = griptables.iterator(); iter.hasNext();) {
			final Griptable griptable = iter.next();
			for (final IFilter iFilter : filters) {
				if (monitor.isCanceled()) {
					throw new InterruptedException();
				}
				if (!iFilter.passFilter(griptable)) {
					iter.remove();
					break;
				}
			}
		}
	}
}
