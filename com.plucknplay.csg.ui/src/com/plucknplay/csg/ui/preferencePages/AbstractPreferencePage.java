/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.plucknplay.csg.ui.Activator;

public abstract class AbstractPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	protected static final int HORIZONTAL_INDENT = 30;
	protected static final String SEPARATOR = "/"; //$NON-NLS-1$
	protected static final String WHITE_SPACE = " "; //$NON-NLS-1$

	public AbstractPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	public void init(final IWorkbench workbench) {
		// do nothing
	}

	@Override
	public boolean performOk() {
		Activator.getDefault().savePluginPreferences();
		return super.performOk();
	}
}
