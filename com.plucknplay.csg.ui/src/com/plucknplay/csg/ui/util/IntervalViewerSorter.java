/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.ui.views.ChordGenerationView;

public class IntervalViewerSorter extends ViewerSorter {

	private IntervalContainer intervalContainer;
	private Collection<IntervalContainer> intervalContainers;

	public IntervalViewerSorter() {
	}

	public IntervalViewerSorter(final IntervalContainer intervalContainer) {
		this.intervalContainer = intervalContainer;
	}

	public IntervalViewerSorter(final Collection<IntervalContainer> intervalContainers) {
		this.intervalContainers = intervalContainers;
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		if (e1 == null || e2 == null) {
			return 0;
		}
		if (e1 == ChordGenerationView.I_DONT_CARE) {
			return -1;
		}
		if (e2 == ChordGenerationView.I_DONT_CARE) {
			return 1;
		}
		if (e1 instanceof Interval && e2 instanceof Interval) {

			final Interval interval1 = (Interval) e1;
			final Interval interval2 = (Interval) e2;

			if (intervalContainers != null) {
				final int octave1 = getMinimumOctave(interval1);
				final int octave2 = getMinimumOctave(interval2);

				if (octave1 != octave2) {
					return Integer.valueOf(octave1).compareTo(Integer.valueOf(octave2));
				}
			}

			else if (intervalContainer != null) {
				final String name1 = intervalContainer.getIntervalName(interval1);
				final String name2 = intervalContainer.getIntervalName(interval2);

				final int octave1 = NamesUtil.getIntervalNameOctaveNumber(interval1, name1);
				final int octave2 = NamesUtil.getIntervalNameOctaveNumber(interval2, name2);

				if (octave1 != octave2) {
					return Integer.valueOf(octave1).compareTo(Integer.valueOf(octave2));
				}
			}

			return Integer.valueOf(interval1.getHalfsteps()).compareTo(Integer.valueOf(interval2.getHalfsteps()));
		}
		return 0;
	}

	private int getMinimumOctave(final Interval interval) {
		if (intervalContainers == null) {
			return 1;
		}

		int octave = 2;
		for (final IntervalContainer intervalContainer2 : intervalContainers) {
			final String name = intervalContainer2.getIntervalName(interval);
			octave = NamesUtil.getIntervalNameOctaveNumber(interval, name);
			if (octave == 1) {
				return octave;
			}
		}

		return octave;
	}
}
