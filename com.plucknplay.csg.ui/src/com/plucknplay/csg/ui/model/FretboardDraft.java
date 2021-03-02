/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class FretboardDraft extends FretDraft {

	private Set<FretboardPosition> additionalNotes;

	/**
	 * The default constructor.
	 */
	public FretboardDraft() {
		super();
		clear();
		setModifiedInput(false);
	}

	/**
	 * The constructor.
	 * 
	 * @param other
	 *            another fretboard draft
	 */
	public FretboardDraft(final FretboardDraft other) {
		super(other);

		// clone additional notes
		additionalNotes = null;
		if (other.additionalNotes != null) {
			additionalNotes = new HashSet<FretboardPosition>(other.additionalNotes);
		}
	}

	/**
	 * The constructor.
	 * 
	 * @param input
	 *            the input
	 */
	public FretboardDraft(final IBlock input) {
		super(input);

		if (getCurrentInstrument() == null) {
			return;
		}

		initAssignments(input);
		initRootNote(input);
		setModifiedInput(false);
	}

	@Override
	protected void initAssignments(final IBlock block) {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();

		final Set<FretboardPosition> fbps = getFretboardPosition(block.getIntervalContainer().getRootNote(),
				block instanceof Griptable ? ((Griptable) block).getIntervals(false) : block.getIntervalContainer()
						.getIntervals());

		if (block instanceof Griptable || prefs.getBoolean(Preferences.SHOW_BLOCKS)) {

			super.initAssignments(block);

			for (final FretboardPosition fbp : fbps) {
				if (NO_FINGER.equals(getAssignment(fbp.getFret(), fbp.getString() + 1))) {
					additionalNotes.add(fbp);
				}
			}

		} else {

			clear();
			for (final FretboardPosition fbp : fbps) {
				if (NO_FINGER.equals(getAssignment(fbp.getFret(), fbp.getString() + 1))) {
					setAssignment(fbp.getFret(), fbp.getString() + 1, UNKNOWN_FINGER);
				}
			}
		}
	}

	@Override
	public int getFret(final int relativeFret) {
		return relativeFret == getCurrentInstrument().getFretCount() + 1 ? 0 : relativeFret;
	}

	@Override
	protected void clear() {
		super.clear();
		additionalNotes = new HashSet<FretboardPosition>();
	}

	public Set<FretboardPosition> getAdditionalNotes() {
		return additionalNotes;
	}

	@Override
	public int getStartFret() {
		return 1;
	}

	@Override
	public int getFretWidth() {
		return getCurrentInstrument() == null ? 0 : getCurrentInstrument().getFretCount();
	}

	private Set<FretboardPosition> getFretboardPosition(final Note rootNote, final Collection<Interval> intervals) {
		if (intervals == null || rootNote == null) {
			throw new IllegalArgumentException();
		}

		final Set<FretboardPosition> result = new HashSet<FretboardPosition>();

		for (final Interval interval : intervals) {
			final Note note = rootNote.calcNote(interval);
			final Set<FretboardPosition> fretboardPositions = getCurrentInstrument().getFretboardPositions(note, true);
			for (final FretboardPosition fp : fretboardPositions) {
				if (fp.getFret() < getCurrentInstrument().getMinFret()
						|| fp.getFret() == getCurrentInstrument().getMinFret()
						&& fp.getFret() != getCurrentInstrument().getCapoFret(fp.getString() + 1)) {
					continue;
				}
				result.add(fp);
			}
		}
		return result;
	}
}
