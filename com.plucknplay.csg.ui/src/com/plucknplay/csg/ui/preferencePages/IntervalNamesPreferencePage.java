/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.plucknplay.csg.core.model.enums.IntervalNamesMode;
import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class IntervalNamesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.intervalNamesPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "interval_names_preference_page_context"; //$NON-NLS-1$

	private static final String[] UNISON_NAMES = new String[] { "R", "U" }; //$NON-NLS-1$  //$NON-NLS-2$

	private IPreferenceStore prefs;

	private Group intervalNamesGroup;
	private List<Button> intervalNameButtons;
	private Button useDifferentNameForUnisonButton;
	private Combo unisonNameCombo;
	private Button useDeltaInMajorIntervalsButton;

	private Map<IntervalNamesMode, String> rootNoteNameMap;
	private Map<IntervalNamesMode, String> secondWithDeltaNameMap;
	private Map<IntervalNamesMode, String> secondWithoutDeltaNameMap;
	private Map<IntervalNamesMode, Label> rootNoteLabelMap;
	private Map<IntervalNamesMode, Label> secondLabelMap;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(final Composite parent) {

		// main composite
		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).applyTo(mainComposite);

		// interval names group
		intervalNamesGroup = new Group(mainComposite, SWT.NONE);
		intervalNamesGroup.setText(PreferenceMessages.IntervalNamesPreferencePage_interval_names);
		GridLayoutFactory.fillDefaults().numColumns(17).spacing(2, 5).margins(5, 5).applyTo(intervalNamesGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(intervalNamesGroup);

		intervalNameButtons = new ArrayList<Button>();
		rootNoteNameMap = new HashMap<IntervalNamesMode, String>();
		secondWithDeltaNameMap = new HashMap<IntervalNamesMode, String>();
		secondWithoutDeltaNameMap = new HashMap<IntervalNamesMode, String>();
		rootNoteLabelMap = new HashMap<IntervalNamesMode, Label>();
		secondLabelMap = new HashMap<IntervalNamesMode, Label>();

		final String[] intervalNames = { "1", "bb2", "#1", "b2", "##1", "2", "bb3", "bb4" }; //$NON-NLS-1$  //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
		for (final IntervalNamesMode mode : IntervalNamesMode.values()) {
			intervalNameButtons.add(createIntervalNamesButton(intervalNamesGroup, mode));

			for (int i = 0; i < intervalNames.length; i++) {

				// create labels
				if (i > 0) {
					new Label(intervalNamesGroup, SWT.NONE).setText("-"); //$NON-NLS-1$
				}
				final Label label = new Label(intervalNamesGroup, SWT.NONE);
				label.setText(NamesUtil.translateIntervalName(intervalNames[i], mode));

				// handle root note
				if (i == 0) {
					rootNoteNameMap.put(mode, NamesUtil.translateIntervalName(intervalNames[i], mode, false));
					rootNoteLabelMap.put(mode, label);
				}

				if (i == 5) {
					final String noneDeltaName = NamesUtil.translateIntervalName(intervalNames[i], mode, false);
					secondWithDeltaNameMap.put(mode, mode.update(noneDeltaName, true));
					secondWithoutDeltaNameMap.put(mode, noneDeltaName);
					secondLabelMap.put(mode, label);
				}
			}
			new Label(intervalNamesGroup, SWT.NONE).setText("..."); //$NON-NLS-1$
		}

		// special interval names group
		final Group specialIntervalNamesGroup = new Group(mainComposite, SWT.NONE);
		specialIntervalNamesGroup.setText(PreferenceMessages.IntervalNamesPreferencePage_special_interval_names);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(specialIntervalNamesGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(specialIntervalNamesGroup);

		useDifferentNameForUnisonButton = createSpecialIntervalNameButton(specialIntervalNamesGroup,
				PreferenceMessages.IntervalNamesPreferencePage_use_different_name_for_unison,
				Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME, 1);

		unisonNameCombo = new Combo(specialIntervalNamesGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		unisonNameCombo.setItems(UNISON_NAMES);
		unisonNameCombo.setText(prefs.getString(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(unisonNameCombo);

		useDeltaInMajorIntervalsButton = createSpecialIntervalNameButton(specialIntervalNamesGroup,
				PreferenceMessages.IntervalNamesPreferencePage_use_delta_in_major_intervals,
				Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS, 2);

		// add listener
		useDifferentNameForUnisonButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateComboEnablement();
				updateRootNoteLabels();
			}
		});
		unisonNameCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateRootNoteLabels();
			}
		});
		useDeltaInMajorIntervalsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateSecondLabels();
			}
		});
		updateComboEnablement();

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);

		return mainComposite;
	}

	private Button createIntervalNamesButton(final Composite parent, final IntervalNamesMode mode) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setText(mode.getModeName() + ":"); //$NON-NLS-1$
		button.setSelection(mode == IntervalNamesMode.valueOf(prefs.getString(Preferences.INTERVAL_NAMES_MODE)));
		button.setData(mode);
		return button;
	}

	private Button createSpecialIntervalNameButton(final Composite parent, final String text, final String prefKey,
			final int hSpan) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText(text + (hSpan == 1 ? ":" : "")); //$NON-NLS-1$ //$NON-NLS-2$
		button.setSelection(prefs.getBoolean(prefKey));
		GridDataFactory.fillDefaults().grab(hSpan == 2, false).span(hSpan, 1).applyTo(button);
		return button;
	}

	private void updateComboEnablement() {
		unisonNameCombo.setEnabled(useDifferentNameForUnisonButton.getSelection());
	}

	private void updateRootNoteLabels() {
		for (final IntervalNamesMode mode : IntervalNamesMode.values()) {
			final Label rootNoteLabel = rootNoteLabelMap.get(mode);
			if (useDifferentNameForUnisonButton.getSelection()) {
				rootNoteLabel.setText(unisonNameCombo.getText());
			} else {
				rootNoteLabel.setText(rootNoteNameMap.get(mode));
			}
		}
		intervalNamesGroup.layout(true, true);
	}

	private void updateSecondLabels() {
		for (final IntervalNamesMode mode : IntervalNamesMode.values()) {
			final Label secondLabel = secondLabelMap.get(mode);
			secondLabel.setText(useDeltaInMajorIntervalsButton.getSelection() ? secondWithDeltaNameMap.get(mode)
					: secondWithoutDeltaNameMap.get(mode));
		}
		intervalNamesGroup.layout(true, true);
	}

	@Override
	public boolean performOk() {

		// set different unison interval name
		final boolean useDifferentNameForUnison = useDifferentNameForUnisonButton.getSelection();
		final String unisonName = unisonNameCombo.getText();
		IntervalNamesMode.setUnisonIntervalName(useDifferentNameForUnison ? unisonName : null);

		// set use delta in major intervals
		final boolean useDeltaInMajorIntervals = useDeltaInMajorIntervalsButton.getSelection();
		IntervalNamesMode.setUseDeltaInMajorIntervalNames(useDeltaInMajorIntervals);

		// set interval names mode
		for (final Button button : intervalNameButtons) {
			if (button.getSelection()) {
				final IntervalNamesMode mode = (IntervalNamesMode) button.getData();
				NamesUtil.setIntervalNamesMode(mode);
				prefs.setValue(Preferences.INTERVAL_NAMES_MODE, mode.toString());
			}
		}
		prefs.setValue(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME, useDifferentNameForUnison);
		prefs.setValue(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME, unisonName);
		prefs.setValue(Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS, useDeltaInMajorIntervals);

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		for (final Button button : intervalNameButtons) {
			button.setSelection(button.getData() == IntervalNamesMode.valueOf(prefs
					.getDefaultString(Preferences.INTERVAL_NAMES_MODE)));
		}
		useDifferentNameForUnisonButton.setSelection(prefs
				.getDefaultBoolean(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME));
		unisonNameCombo.setText(prefs.getDefaultString(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME));
		useDeltaInMajorIntervalsButton.setSelection(prefs
				.getDefaultBoolean(Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS));
		updateComboEnablement();
		updateRootNoteLabels();
		updateSecondLabels();

		super.performDefaults();
	}
}
