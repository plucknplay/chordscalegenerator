/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractExportAction;
import com.plucknplay.csg.ui.actions.general.XMLExportImport;

public class ExportChordAction extends AbstractExportAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.exportChordAction"; //$NON-NLS-1$

	public ExportChordAction(final IViewPart view) {
		super(view);
	}

	@Override
	public CategoryList getCategoryList() {
		return ChordList.getInstance();
	}

	@Override
	protected String getDialogTitle() {
		return ActionMessages.ExportChordAction_dialog_title;
	}

	@Override
	protected String getTypeName() {
		return ActionMessages.ExportChordAction_type_name;
	}

	@Override
	protected String getType() {
		return XMLExportImport.TYPE_CHORDS;
	}

	@Override
	protected String getActionId() {
		return ACTION_ID;
	}
}
