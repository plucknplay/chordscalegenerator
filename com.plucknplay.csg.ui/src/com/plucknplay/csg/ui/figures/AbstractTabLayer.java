/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.model.TabDraft;

public abstract class AbstractTabLayer extends AntiAliasedLayer {

	private final TabDraft tabDraft;

	public AbstractTabLayer(final TabDraft tabDraft) {
		this.tabDraft = tabDraft;
	}

	public TabDraft getTabDraft() {
		return tabDraft;
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
