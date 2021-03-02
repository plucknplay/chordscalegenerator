/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.preference.BooleanFieldEditor;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class GeneralPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.generalPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "general_preference_page_context"; //$NON-NLS-1$

	@Override
	protected void createFieldEditors() {

		// show login prompt
		final BooleanFieldEditor showLoginPromptEditor = new BooleanFieldEditor(Preferences.SHOW_LOGIN_PROMPT,
				PreferenceMessages.GeneralPreferencePage_show_activation_dialog, getFieldEditorParent());
		addField(showLoginPromptEditor);

		// show exit prompt
		final BooleanFieldEditor confirmExitEditor = new BooleanFieldEditor(Preferences.CONFIRM_EXIT,
				PreferenceMessages.GeneralPreferencePage_confirm_exit, getFieldEditorParent());
		addField(confirmExitEditor);

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}
}
