/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class ScaleFinderViewPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.views.scaleFinderViewPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "scale_finder_view_preference_page_context"; //$NON-NLS-1$

	@Override
	protected void createFieldEditors() {

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(getFieldEditorParent());
		PreferenceLinkUtil.createChordAndScaleNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createNoteNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());

		// clear input after calculation
		final BooleanFieldEditor showMutedStringsEditor = new BooleanFieldEditor(
				Preferences.SCALE_FINDER_VIEW_CLEAR_INPUT_AFTER_CALCULATION,
				PreferenceMessages.ScaleFinderViewPreferencePage_clear_input_after_calculation, getFieldEditorParent());
		addField(showMutedStringsEditor);
		GridDataFactory.fillDefaults().applyTo(showMutedStringsEditor.getDescriptionControl(getFieldEditorParent()));

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}
}
