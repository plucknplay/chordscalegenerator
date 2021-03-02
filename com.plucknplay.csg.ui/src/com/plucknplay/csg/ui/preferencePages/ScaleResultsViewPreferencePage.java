/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.util.MyIntegerFieldEditor;

public class ScaleResultsViewPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.views.scaleResultsViewPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "scale_results_view_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private Button intervalsButton;
	private Button notesButton;
	private Button rootNoteButton;
	private Button coverageButton;

	@Override
	public void init(final IWorkbench workbench) {
		super.init(workbench);
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected void createFieldEditors() {

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(getFieldEditorParent(), 3, 15);
		PreferenceLinkUtil.createScaleNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createNotesAndIntervalNamesLink(linkComposite,
				(IWorkbenchPreferenceContainer) getContainer());

		// entries per page
		final MyIntegerFieldEditor entriesPerPageEditor = new MyIntegerFieldEditor(
				Preferences.SCALE_RESULTS_VIEW_ENTRIES_PER_PAGE,
				PreferenceMessages.ScaleResultsViewPreferencePage_entries_per_page, getFieldEditorParent(), 3,
				UIConstants.MIN_ENTRIES_PER_PAGE, UIConstants.MAX_ENTRIES_PER_PAGE);
		addField(entriesPerPageEditor);

		// visibility group
		final Group visibilityGroup = new Group(getFieldEditorParent(), SWT.NONE);
		visibilityGroup.setText(PreferenceMessages.ScaleResultsViewPreferencePage_visible_columns);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).spacing(30, 10)
				.applyTo(visibilityGroup);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(visibilityGroup);

		intervalsButton = addCheckBox(visibilityGroup, PreferenceMessages.ScaleResultsViewPreferencePage_intervals,
				Preferences.SCALE_RESULTS_VIEW_SHOW_INTERVALS_COLUMN);
		notesButton = addCheckBox(visibilityGroup, PreferenceMessages.ScaleResultsViewPreferencePage_notes,
				Preferences.SCALE_RESULTS_VIEW_SHOW_NOTES_COLUMN);
		rootNoteButton = addCheckBox(visibilityGroup, PreferenceMessages.ScaleResultsViewPreferencePage_root_note,
				Preferences.SCALE_RESULTS_VIEW_SHOW_ROOT_NOTE_COLUMN);
		coverageButton = addCheckBox(visibilityGroup, PreferenceMessages.ScaleResultsViewPreferencePage_coverage,
				Preferences.SCALE_RESULTS_VIEW_SHOW_COVERAGE_COLUMN);

		// compact mode for notes and intervals columns
		final BooleanFieldEditor compactModeEditor = new BooleanFieldEditor(
				Preferences.SCALE_RESULTS_VIEW_COMPACT_MODE,
				PreferenceMessages.ScaleResultsViewPreferencePage_compact_mode, getFieldEditorParent());
		addField(compactModeEditor);

		// show separators in gray
		final BooleanFieldEditor grayModeEditor = new BooleanFieldEditor(
				Preferences.SCALE_RESULTS_VIEW_SHOW_SEPARATORS_IN_GRAY,
				PreferenceMessages.ScaleResultsViewPreferencePage_show_separators_in_gray, getFieldEditorParent());
		addField(grayModeEditor);

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}

	private Button addCheckBox(final Composite parent, final String text, final String prefId) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText(text);
		button.setSelection(prefs.getBoolean(prefId));
		return button;
	}

	/* --- perform methods --- */

	@Override
	public boolean performOk() {
		prefs.setValue(Preferences.SCALE_RESULTS_VIEW_SHOW_INTERVALS_COLUMN, intervalsButton.getSelection());
		prefs.setValue(Preferences.SCALE_RESULTS_VIEW_SHOW_NOTES_COLUMN, notesButton.getSelection());
		prefs.setValue(Preferences.SCALE_RESULTS_VIEW_SHOW_ROOT_NOTE_COLUMN, rootNoteButton.getSelection());
		prefs.setValue(Preferences.SCALE_RESULTS_VIEW_SHOW_COVERAGE_COLUMN, coverageButton.getSelection());
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		intervalsButton.setSelection(prefs.getDefaultBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_INTERVALS_COLUMN));
		notesButton.setSelection(prefs.getDefaultBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_NOTES_COLUMN));
		rootNoteButton.setSelection(prefs.getDefaultBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_ROOT_NOTE_COLUMN));
		coverageButton.setSelection(prefs.getDefaultBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_COVERAGE_COLUMN));
	}
}
