/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractImportAction;
import com.plucknplay.csg.ui.actions.general.XMLExportImport;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.util.LoginUtil;

public class ImportScaleAction extends AbstractImportAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.importScaleAction"; //$NON-NLS-1$

	public ImportScaleAction(final IViewPart view) {
		super(view);
	}

	@Override
	public CategoryList getCategoryList() {
		return ScaleList.getInstance();
	}

	@Override
	protected String getType() {
		return XMLExportImport.TYPE_SCALES;
	}

	@Override
	protected String getDialogTitle() {
		return ActionMessages.ImportScaleAction_dialog_title;
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
		return NlsUtil.getDataLanguagePath() + "/" + NlsUtil.getDataScalePath(); //$NON-NLS-1$
	}
}
