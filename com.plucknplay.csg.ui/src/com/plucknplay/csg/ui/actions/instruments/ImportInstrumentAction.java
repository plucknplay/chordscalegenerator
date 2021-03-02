/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractImportAction;
import com.plucknplay.csg.ui.actions.general.XMLExportImport;

public class ImportInstrumentAction extends AbstractImportAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.importImportAction"; //$NON-NLS-1$

	public ImportInstrumentAction(final IViewPart view) {
		super(view);
	}

	@Override
	public CategoryList getCategoryList() {
		return InstrumentList.getInstance();
	}

	@Override
	protected String getType() {
		return XMLExportImport.TYPE_INSTRUMENTS;
	}

	@Override
	protected String getDialogTitle() {
		return ActionMessages.ImportInstrumentAction_dialog_title;
	}

	@Override
	protected String getActionId() {
		return ACTION_ID;
	}

	@Override
	protected String getDataPath() {
		return ActionMessages.Import_data_language_path_name
				+ "/" + ActionMessages.ImportInstrumentAction_data_instruments_path_name; //$NON-NLS-1$
	}
}
