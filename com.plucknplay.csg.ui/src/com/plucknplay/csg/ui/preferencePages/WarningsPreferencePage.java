/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class WarningsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.warningPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "warnings_preference_page_context"; //$NON-NLS-1$

	private static final int MIN_LABEL_WIDTH = 200;

	private IPreferenceStore prefs;

	private Composite mainComposite;

	private Button instrumentChangeButton;
	private Button levelChangeButton;
	private Button truncationButton;

	private Label instrumentChangeLabel;
	private Label levelChangeLabel;
	private Label truncationLabel;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(final Composite parent) {

		// main composite
		mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(mainComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(mainComposite);

		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				updateLabels();
			}
		});

		// hide flush prompt
		instrumentChangeButton = new Button(mainComposite, SWT.CHECK);
		instrumentChangeButton.setText(PreferenceMessages.WarningsPreferencePage_hide_warning);
		instrumentChangeButton.setSelection(prefs.getBoolean(Preferences.WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW));
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(instrumentChangeButton);

		instrumentChangeLabel = new Label(mainComposite, SWT.LEFT | SWT.WRAP);
		instrumentChangeLabel.setText(PreferenceMessages.WarningsPreferencePage_the_change_of_the_active_instrument);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).hint(MIN_LABEL_WIDTH, SWT.DEFAULT)
				.applyTo(instrumentChangeLabel);

		// hide level change prompt
		levelChangeButton = new Button(mainComposite, SWT.CHECK);
		levelChangeButton.setText(PreferenceMessages.WarningsPreferencePage_hide_warning);
		levelChangeButton.setSelection(prefs.getBoolean(Preferences.WARNINGS_HIDE_PROMPT_LEVEL_CHANGE));
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(levelChangeButton);

		levelChangeLabel = new Label(mainComposite, SWT.LEFT | SWT.WRAP);
		levelChangeLabel.setText(PreferenceMessages.WarningsPreferencePage_the_change_of_the_prefer_barre_option);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).hint(MIN_LABEL_WIDTH, SWT.DEFAULT)
				.applyTo(levelChangeLabel);

		// hide truncation prompt
		truncationButton = new Button(mainComposite, SWT.CHECK);
		truncationButton.setText(PreferenceMessages.WarningsPreferencePage_hide_warning);
		truncationButton.setSelection(prefs.getBoolean(Preferences.WARNINGS_HIDE_PROMPT_TRUNCATE_RESULTS_VIEW));
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(truncationButton);

		truncationLabel = new Label(mainComposite, SWT.LEFT | SWT.WRAP);
		truncationLabel.setText(PreferenceMessages.WarningsPreferencePage_the_generated_results);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).hint(MIN_LABEL_WIDTH, SWT.DEFAULT)
				.applyTo(truncationLabel);

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);

		return mainComposite;
	}

	private void updateLabels() {
		if (mainComposite == null || mainComposite.isDisposed() || mainComposite.getParent() == null
				|| mainComposite.getParent().isDisposed() || instrumentChangeButton == null
				|| instrumentChangeButton.isDisposed() || instrumentChangeLabel == null
				|| instrumentChangeLabel.isDisposed() || levelChangeLabel == null || levelChangeLabel.isDisposed()
				|| truncationLabel == null || truncationLabel.isDisposed()) {
			return;
		}

		final int compositeWidth = mainComposite.getParent().getSize().x - 50;
		final int buttonWidth = instrumentChangeButton.getSize().x;
		final int labelWidth = Math.max(compositeWidth - buttonWidth, MIN_LABEL_WIDTH);

		((GridData) instrumentChangeLabel.getLayoutData()).widthHint = labelWidth;
		((GridData) levelChangeLabel.getLayoutData()).widthHint = labelWidth;
		((GridData) truncationLabel.getLayoutData()).widthHint = labelWidth;
	}

	@Override
	public boolean performOk() {
		prefs.setValue(Preferences.WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW, instrumentChangeButton.getSelection());
		prefs.setValue(Preferences.WARNINGS_HIDE_PROMPT_LEVEL_CHANGE, levelChangeButton.getSelection());
		prefs.setValue(Preferences.WARNINGS_HIDE_PROMPT_TRUNCATE_RESULTS_VIEW, truncationButton.getSelection());
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		instrumentChangeButton.setSelection(prefs.getDefaultBoolean(Preferences.WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW));
		levelChangeButton.setSelection(prefs.getDefaultBoolean(Preferences.WARNINGS_HIDE_PROMPT_LEVEL_CHANGE));
		truncationButton.setSelection(prefs.getDefaultBoolean(Preferences.WARNINGS_HIDE_PROMPT_TRUNCATE_RESULTS_VIEW));
		super.performDefaults();
	}
}
