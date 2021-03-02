/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import com.plucknplay.csg.ui.actions.general.AbstractCopyAction;
import com.plucknplay.csg.ui.model.sets.Clipboard;

public class CopyInstrumentAction extends AbstractCopyAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.copyInstrumentAction"; //$NON-NLS-1$

	@Override
	public void run() {
		Clipboard.getInstance().saveCurrentInstrument(null);
		super.run();
	}

	@Override
	protected Object getClipboardType() {
		return Clipboard.TYPE_INSTRUMENT;
	}

	@Override
	protected String getActionId() {
		return ACTION_ID;
	}
}
