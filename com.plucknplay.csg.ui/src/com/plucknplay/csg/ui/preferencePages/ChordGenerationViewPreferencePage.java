/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class ChordGenerationViewPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.views.chordGenerationViewPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "chord_generation_view_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private List<Button> buttons;
	private Button bassToneButton;
	private Button leadToneButton;
	private Button levelButton;
	private Button stringRangeButton;
	private Button fretRangeButton;
	private Button gripRangeButton;
	private Button toneNumberButton;
	private Button stringsButton;
	private Button mutedButton;
	private Button tonesButton;
	private Button excludedIntervalsButton;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(final Composite parent) {

		// main composite
		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).spacing(5, 5).equalWidth(false).applyTo(mainComposite);

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(mainComposite, 3, 15);
		PreferenceLinkUtil.createChordNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createNotesAndIntervalNamesLink(linkComposite,
				(IWorkbenchPreferenceContainer) getContainer());

		// advanced group
		final Group advancedGroup = new Group(mainComposite, SWT.NONE);
		advancedGroup.setText(PreferenceMessages.ChordGenerationViewPreferencePage_advanced_section_members);
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(advancedGroup);
		GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(advancedGroup);

		buttons = new ArrayList<Button>();
		bassToneButton = addCheckBox(advancedGroup, PreferenceMessages.ChordGenerationViewPreferencePage_bass_tone,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_BASS_TONE);
		leadToneButton = addCheckBox(advancedGroup, PreferenceMessages.ChordGenerationViewPreferencePage_lead_tone,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_LEAD_TONE);
		levelButton = addCheckBox(advancedGroup, PreferenceMessages.ChordGenerationViewPreferencePage_level,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_LEVEL);
		stringRangeButton = addCheckBox(advancedGroup,
				PreferenceMessages.ChordGenerationViewPreferencePage_string_range,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_STRING_RANGE);
		fretRangeButton = addCheckBox(advancedGroup, PreferenceMessages.ChordGenerationViewPreferencePage_fret_range,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_FRET_RANGE);
		gripRangeButton = addCheckBox(advancedGroup, PreferenceMessages.ChordGenerationViewPreferencePage_grip_width,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_GRIP_RANGE);
		toneNumberButton = addCheckBox(advancedGroup,
				PreferenceMessages.ChordGenerationViewPreferencePage_max_single_tone_numbers,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_MAX_SINGLE_TONE_NUMBER);
		stringsButton = addCheckBox(advancedGroup,
				PreferenceMessages.ChordGenerationViewPreferencePage_empty_and_muted_strings,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_EMPTY_MUTED_STRINGS);
		mutedButton = addCheckBox(advancedGroup,
				PreferenceMessages.ChordGenerationViewPreferencePage_only_packed_and_only_single_muted_strings,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_MUTED_STRINGS_INFO);
		tonesButton = addCheckBox(advancedGroup,
				PreferenceMessages.ChordGenerationViewPreferencePage_doubled_tones_and_only_ascending_descending_tones,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_TONES_INFO);
		excludedIntervalsButton = addCheckBox(advancedGroup,
				PreferenceMessages.ChordGenerationViewPreferencePage_without_3rd_and_5th,
				Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_EXCLUDED_INTERVALS);

		// select/deselect all buttons
		GridDataFactory.fillDefaults().grab(true, false).applyTo(new Label(mainComposite, SWT.NONE));
		createSelectionButton(mainComposite, PreferenceMessages.ChordGenerationViewPreferencePage_select_all, true);
		createSelectionButton(mainComposite, PreferenceMessages.ChordGenerationViewPreferencePage_deselect_all, false);

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);

		return mainComposite;
	}

	private Button addCheckBox(final Composite parent, final String text, final String prefId) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText(text);
		button.setSelection(prefs.getBoolean(prefId));
		buttons.add(button);
		return button;
	}

	private Button createSelectionButton(final Composite parent, final String text, final boolean selection) {
		final Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				for (final Button checkbox : buttons) {
					checkbox.setSelection(selection);
				}
			}
		});
		return button;
	}

	/* --- perform methods --- */

	@Override
	public boolean performOk() {
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_BASS_TONE, bassToneButton.getSelection());
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_LEAD_TONE, leadToneButton.getSelection());
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_LEVEL, levelButton.getSelection());
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_STRING_RANGE, stringRangeButton.getSelection());
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_FRET_RANGE, fretRangeButton.getSelection());
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_GRIP_RANGE, gripRangeButton.getSelection());
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_MAX_SINGLE_TONE_NUMBER,
				toneNumberButton.getSelection());
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_EMPTY_MUTED_STRINGS, stringsButton.getSelection());
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_MUTED_STRINGS_INFO, mutedButton.getSelection());
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_TONES_INFO, tonesButton.getSelection());
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_EXCLUDED_INTERVALS,
				excludedIntervalsButton.getSelection());
		performAdvancedSectionChangedHack();
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		bassToneButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_BASS_TONE));
		leadToneButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_LEAD_TONE));
		levelButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_LEVEL));
		stringRangeButton.setSelection(prefs
				.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_STRING_RANGE));
		fretRangeButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_FRET_RANGE));
		gripRangeButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_GRIP_RANGE));
		toneNumberButton.setSelection(prefs
				.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_MAX_SINGLE_TONE_NUMBER));
		stringsButton.setSelection(prefs
				.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_EMPTY_MUTED_STRINGS));
		mutedButton.setSelection(prefs
				.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_MUTED_STRINGS_INFO));
		tonesButton.setSelection(prefs.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_TONES_INFO));
		excludedIntervalsButton.setSelection(prefs
				.getDefaultBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_EXCLUDED_INTERVALS));
		performAdvancedSectionChangedHack();
	}

	private void performAdvancedSectionChangedHack() {
		final boolean changedToggle = prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_SECTION_CHANGED);
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_SECTION_CHANGED, !changedToggle);
	}
}
