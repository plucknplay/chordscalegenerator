/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class NamesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.namesPreferences"; //$NON-NLS-1$

	@Override
	public void init(final IWorkbench workbench) {
		// do nothing
	}

	@Override
	protected Control createContents(final Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().equalWidth(false).applyTo(composite);

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(composite);
		PreferenceLinkUtil.createChordAndScaleNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createNotesAndIntervalNamesLink(linkComposite,
				(IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createFingeringNotationLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());

		return composite;
	}

	@Override
	public boolean performCancel() {
		return true;
	}

	@Override
	public void performHelp() {
	}

	@Override
	public boolean performOk() {
		return true;
	}

	@Override
	protected void performApply() {
	}

	@Override
	protected void performDefaults() {
	}
}
