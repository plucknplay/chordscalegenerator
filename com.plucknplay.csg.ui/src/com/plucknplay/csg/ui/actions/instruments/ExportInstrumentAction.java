/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractExportAction;
import com.plucknplay.csg.ui.actions.general.XMLExportImport;

public class ExportInstrumentAction extends AbstractExportAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.exportInstrumentAction"; //$NON-NLS-1$

	public ExportInstrumentAction(final IViewPart view) {
		super(view);
	}

	@Override
	public CategoryList getCategoryList() {
		return InstrumentList.getInstance();
	}

	@Override
	protected String getDialogTitle() {
		return ActionMessages.ExportInstrumentAction_dialog_title;
	}

	@Override
	protected String getTypeName() {
		return ActionMessages.ExportInstrumentAction_type_name;
	}

	@Override
	protected String getType() {
		return XMLExportImport.TYPE_INSTRUMENTS;
	}

	@Override
	protected String getActionId() {
		return ACTION_ID;
	}
}
