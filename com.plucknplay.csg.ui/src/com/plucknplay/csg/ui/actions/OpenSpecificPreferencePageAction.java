/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class OpenSpecificPreferencePageAction extends Action {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.showSpecificPreferences"; //$NON-NLS-1$

	private final String pageId;

	public OpenSpecificPreferencePageAction(final String pageId) {
		this.pageId = pageId;

		setId(IActionIds.ACT_OPEN_PREFERENCES_PAGE);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.OpenSpecificPreferencePageAction_text);
		setToolTipText(ActionMessages.OpenSpecificPreferencePageAction_text);
	}

	@Override
	public void run() {
		PreferencesUtil.createPreferenceDialogOn(null, pageId, null, null).open();
	}
}
