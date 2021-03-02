/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.actions.general.AbstractDuplicateAction;

public class DuplicateChordAction extends AbstractDuplicateAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.duplicateChordAction"; //$NON-NLS-1$

	public DuplicateChordAction(final IViewPart view) {
		super(view);
	}

	@Override
	public CategoryList getCategoryList() {
		return ChordList.getInstance();
	}

	@Override
	protected String getActionId() {
		return ACTION_ID;
	}
}
