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

public class ChordResultsViewPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.views.chordResultsViewPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "chord_results_view_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private Button levelButton;
	private Button bassToneButton;
	private Button leadToneButton;
	private Button notesButton;
	private Button intervalsButton;
	private Button fretsButton;
	private Button minStringButton;
	private Button maxStringButton;
	private Button stringSpanButton;
	private Button minFretButton;
	private Button maxFretButton;
	private Button fretSpanButton;
	private Button distanceButton;
	private Button emptyStringsButton;
	private Button mutedStringsButton;
	private Button doubledTonesButton;

	@Override
	public void init(final IWorkbench workbench) {
		super.init(workbench);
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected void createFieldEditors() {

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(getFieldEditorParent(), 3, 15);
		PreferenceLinkUtil.createChordNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createNotesAndIntervalNamesLink(linkComposite,
				(IWorkbenchPreferenceContainer) getContainer());

		// entries per page
		final MyIntegerFieldEditor entriesPerPageEditor = new MyIntegerFieldEditor(
				Preferences.CHORD_RESULTS_VIEW_ENTRIES_PER_PAGE,
				PreferenceMessages.ChordResultsViewPreferencePage_entries_per_page, getFieldEditorParent(), 3,
				UIConstants.MIN_ENTRIES_PER_PAGE, UIConstants.MAX_ENTRIES_PER_PAGE);
		addField(entriesPerPageEditor);

		// visibility group
		final Group visibilityGroup = new Group(getFieldEditorParent(), SWT.NONE);
		visibilityGroup.setText(PreferenceMessages.ChordResultsViewPreferencePage_visible_columns);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).margins(5, 5).applyTo(visibilityGroup);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(visibilityGroup);

		levelButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_level,
				Preferences.CHORD_RESULTS_VIEW_SHOW_LEVEL_COLUMN);
		bassToneButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_bass_tone,
				Preferences.CHORD_RESULTS_VIEW_SHOW_BASS_TONE_COLUMN);
		leadToneButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_lead_tone,
				Preferences.CHORD_RESULTS_VIEW_SHOW_LEAD_TONE_COLUMN);
		notesButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_notes,
				Preferences.CHORD_RESULTS_VIEW_SHOW_NOTES_COLUMN);
		intervalsButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_intervals,
				Preferences.CHORD_RESULTS_VIEW_SHOW_INTERVALS_COLUMN);
		fretsButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_frets,
				Preferences.CHORD_RESULTS_VIEW_SHOW_FRETS_COLUMN);
		minStringButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_min_string,
				Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_STRING_COLUMN);
		maxStringButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_max_string,
				Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_STRING_COLUMN);
		stringSpanButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_string_span,
				Preferences.CHORD_RESULTS_VIEW_SHOW_STRING_SPAN_COLUMN);
		minFretButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_min_fret,
				Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_FRET_COLUMN);
		maxFretButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_max_fret,
				Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_FRET_COLUMN);
		fretSpanButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_fret_span,
				Preferences.CHORD_RESULTS_VIEW_SHOW_FRET_SPAN_COLUMN);
		distanceButton = addCheckBox(visibilityGroup, PreferenceMessages.ChordResultsViewPreferencePage_grip_width,
				Preferences.CHORD_RESULTS_VIEW_SHOW_DISTANCE_COLUMN);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(distanceButton);
		emptyStringsButton = addCheckBox(visibilityGroup,
				PreferenceMessages.ChordResultsViewPreferencePage_number_of_empty_strings,
				Preferences.CHORD_RESULTS_VIEW_SHOW_EMPTY_STRINGS_COLUMN);
		mutedStringsButton = addCheckBox(visibilityGroup,
				PreferenceMessages.ChordResultsViewPreferencePage_number_of_muted_strings,
				Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_COLUMN);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(mutedStringsButton);
		doubledTonesButton = addCheckBox(visibilityGroup,
				PreferenceMessages.ChordResultsViewPreferencePage_doubled_tones,
				Preferences.CHORD_RESULTS_VIEW_SHOW_DOUBLED_TONES_COLUMN);

		// compact mode for notes, intervals and frets columns
		final BooleanFieldEditor compactModeEditor = new BooleanFieldEditor(
				Preferences.CHORD_RESULTS_VIEW_COMPACT_MODE,
				PreferenceMessages.ChordResultsViewPreferencePage_compact_mode, getFieldEditorParent());
		addField(compactModeEditor);

		// show muted strings and separators in gray
		final BooleanFieldEditor grayModeEditor = new BooleanFieldEditor(
				Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_IN_GRAY,
				PreferenceMessages.ChordResultsViewPreferencePage_show_muted_strings_in_gray, getFieldEditorParent());
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
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_LEVEL_COLUMN, levelButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_BASS_TONE_COLUMN, bassToneButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_LEAD_TONE_COLUMN, leadToneButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_NOTES_COLUMN, notesButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_INTERVALS_COLUMN, intervalsButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_FRETS_COLUMN, fretsButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_STRING_COLUMN, minStringButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_STRING_COLUMN, maxStringButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_STRING_SPAN_COLUMN, stringSpanButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_FRET_COLUMN, minFretButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_FRET_COLUMN, maxFretButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_FRET_SPAN_COLUMN, fretSpanButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_DISTANCE_COLUMN, distanceButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_EMPTY_STRINGS_COLUMN, emptyStringsButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_COLUMN, mutedStringsButton.getSelection());
		prefs.setValue(Preferences.CHORD_RESULTS_VIEW_SHOW_DOUBLED_TONES_COLUMN, doubledTonesButton.getSelection());
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		levelButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_LEVEL_COLUMN));
		bassToneButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_BASS_TONE_COLUMN));
		leadToneButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_LEAD_TONE_COLUMN));
		notesButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_NOTES_COLUMN));
		intervalsButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_INTERVALS_COLUMN));
		fretsButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_FRETS_COLUMN));
		minStringButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_STRING_COLUMN));
		maxStringButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_STRING_COLUMN));
		stringSpanButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_STRING_SPAN_COLUMN));
		minFretButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_FRET_COLUMN));
		maxFretButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_FRET_COLUMN));
		fretSpanButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_FRET_SPAN_COLUMN));
		distanceButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_DISTANCE_COLUMN));
		emptyStringsButton.setSelection(prefs
				.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_EMPTY_STRINGS_COLUMN));
		mutedStringsButton.setSelection(prefs
				.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_COLUMN));
		doubledTonesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_DOUBLED_TONES_COLUMN));
	}
}
