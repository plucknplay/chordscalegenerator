/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.actions.general.AbstractPasteAction;
import com.plucknplay.csg.ui.model.sets.Clipboard;

public class PasteInstrumentAction extends AbstractPasteAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.pasteInstrumentAction"; //$NON-NLS-1$

	public PasteInstrumentAction(final IViewPart view) {
		super(view);
	}

	@Override
	protected Object getClipboardType() {
		return Clipboard.TYPE_INSTRUMENT;
	}

	@Override
	public CategoryList getCategoryList() {
		return InstrumentList.getInstance();
	}

	@Override
	public void run() {
		super.run();
		// (+) set current instrument if there's a saved one
		final Instrument currentInstrument = Clipboard.getInstance().getStoredCurrentInstrument();
		if (currentInstrument != null) {
			InstrumentList.getInstance().setCurrentInstrument(currentInstrument);
			Clipboard.getInstance().saveCurrentInstrument(null);
		}
	}

	@Override
	protected String getActionId() {
		return ACTION_ID;
	}
}
