/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractImportAction;
import com.plucknplay.csg.ui.actions.general.XMLExportImport;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.util.LoginUtil;

public class ImportChordAction extends AbstractImportAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.importChordAction"; //$NON-NLS-1$

	public ImportChordAction(final IViewPart view) {
		super(view);
	}

	@Override
	public CategoryList getCategoryList() {
		return ChordList.getInstance();
	}

	@Override
	protected String getType() {
		return XMLExportImport.TYPE_CHORDS;
	}

	@Override
	protected String getDialogTitle() {
		return ActionMessages.ImportChordAction_dialog_title;
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

	@Override
	protected String getDataPath() {
		return NlsUtil.getDataLanguagePath() + "/" + NlsUtil.getDataChordPath(); //$NON-NLS-1$
	}
}
