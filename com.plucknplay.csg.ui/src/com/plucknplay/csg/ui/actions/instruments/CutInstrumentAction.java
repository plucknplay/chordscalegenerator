/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.actions.general.AbstractCutAction;
import com.plucknplay.csg.ui.model.sets.Clipboard;

public class CutInstrumentAction extends AbstractCutAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.cutInstrumentAction"; //$NON-NLS-1$

	public CutInstrumentAction(final IViewPart view) {
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
	protected String getActionId() {
		return ACTION_ID;
	}
}
