/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.model.BoxDraft;

public abstract class AbstractBoxLayer extends AntiAliasedLayer {

	private final BoxDraft boxDraft;

	private boolean showFingering;
	private boolean showFingeringOutside;
	private boolean showNotes;
	private boolean showNotesOutside;
	private boolean showIntervals;
	private boolean showIntervalsOutside;

	public AbstractBoxLayer(final BoxDraft boxDraft, final boolean showFingering, final boolean showFingeringOutside,
			final boolean showNotes, final boolean showNotesOutside, final boolean showIntervals,
			final boolean showIntervalsOutside) {

		this.boxDraft = boxDraft;
		this.showFingering = showFingering;
		this.showFingeringOutside = showFingeringOutside;
		this.showNotes = showNotes;
		this.showNotesOutside = showNotesOutside;
		this.showIntervals = showIntervals;
		this.showIntervalsOutside = showIntervalsOutside;
	}

	public BoxDraft getBoxDraft() {
		return boxDraft;
	}

	/*
	 * Fingering
	 */

	public boolean getShowFingering() {
		return showFingering;
	}

	public boolean getShowFingeringOutside() {
		return showFingering && showFingeringOutside && !isEditable();
	}

	public boolean getShowFingeringInside() {
		return showFingering && (!showFingeringOutside || isEditable());
	}

	public void setShowFingering(final boolean showFingering) {
		this.showFingering = showFingering;
	}

	public void setShowFingeringOutside(final boolean showFingeringOutside) {
		this.showFingeringOutside = showFingeringOutside;
	}

	/*
	 * Notes
	 */

	public boolean getShowNotes() {
		return showNotes;
	}

	public boolean getShowNotesOutside() {
		return showNotes && (showNotesOutside || isEditable());
	}

	public boolean getShowNotesInside() {
		return showNotes && !showNotesOutside && !isEditable();
	}

	public void setShowNotes(final boolean showNotes) {
		this.showNotes = showNotes;
	}

	public void setShowNotesOutside(final boolean showNotesOutside) {
		this.showNotesOutside = showNotesOutside;
	}

	/*
	 * Intervals
	 */

	public boolean getShowIntervals() {
		return showIntervals;
	}

	public boolean getShowIntervalsOutside() {
		return showIntervals && (showIntervalsOutside || isEditable());
	}

	public boolean getShowIntervalsInside() {
		return showIntervals && !showIntervalsOutside && !isEditable();
	}

	public void setShowIntervals(final boolean showIntervals) {
		this.showIntervals = showIntervals;
	}

	public void setShowIntervalsOutside(final boolean showIntervalsOutside) {
		this.showIntervalsOutside = showIntervalsOutside;
	}

	/*
	 * Common Stuff
	 */

	public boolean getShowInside() {
		return getShowFingeringInside() || getShowNotesInside() || getShowIntervalsInside();
	}

	private boolean isEditable() {
		return boxDraft.isEditable();
	}

	/**
	 * Convenience method to retrieve the current instrument.
	 * 
	 * @return the current instrument
	 */
	protected Instrument getCurrentInstrument() {
		return InstrumentList.getInstance().getCurrentInstrument();
	}
}
