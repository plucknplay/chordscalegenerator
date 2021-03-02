/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.actions.general.AbstractCutAction;
import com.plucknplay.csg.ui.model.sets.Clipboard;
import com.plucknplay.csg.ui.util.LoginUtil;

public class CutChordAction extends AbstractCutAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.cutChordAction"; //$NON-NLS-1$

	public CutChordAction(final IViewPart view) {
		super(view);
	}

	@Override
	protected Object getClipboardType() {
		return Clipboard.TYPE_CHORD;
	}

	@Override
	public CategoryList getCategoryList() {
		return ChordList.getInstance();
	}

	@Override
	public void run() {
		if (LoginUtil.isActivated()) {
			super.run();
		} else {
			LoginUtil.showUnsupportedFeatureInformation(getViewPart().getSite().getShell());
		}
	}

	@Override
	protected String getActionId() {
		return ACTION_ID;
	}
}
