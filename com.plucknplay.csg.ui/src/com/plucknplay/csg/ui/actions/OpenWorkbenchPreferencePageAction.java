/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

public class OpenWorkbenchPreferencePageAction extends OpenSpecificPreferencePageAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.showWorkbenchPreferences"; //$NON-NLS-1$

	public OpenWorkbenchPreferencePageAction() {
		super("com.plucknplay.csg.ui.workbenchPreferences"); //$NON-NLS-1$
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.OpenWorkbenchPreferencePageAction_text);
		setToolTipText(ActionMessages.OpenWorkbenchPreferencePageAction_text);
	}
}
