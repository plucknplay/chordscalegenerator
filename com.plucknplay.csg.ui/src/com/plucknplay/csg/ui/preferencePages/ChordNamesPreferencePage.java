/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.util.NameProvider;
import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.activation.NlsUtil;

public class ChordNamesPreferencePage extends AbstractNamesPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.chordNamesPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "chord_names_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private Button blankSpaceBetweenPrefixAndIntervalsButton;
	private Button blankSpaceBetweenIntervalsButton;
	private Button compactModeButton;
	private Button encloseInBracketsButton;
	private Button roundBracketButton;
	private Button squareBracketButton;
	private Button curlyBracketButton;
	private Button angleBracketButton;
	private Button shortIntervalsButton;
	private Button longIntervalsButton;
	private Button prefixNoButton;
	private Button prefixMinusButton;
	private Button prefixHyphenButton;
	private Button prefixOmitButton;
	private Button prefixOButton;
	private Button prefixOPointButton;
	private Button prefixOhneButton;

	@Override
	public void init(final IWorkbench workbench) {
		super.init(workbench);
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected void extendContents(final Composite parent) {

		// excluded intervals group
		final Group excludedIntervalsGroup = new Group(parent, SWT.NONE);
		excludedIntervalsGroup.setText(PreferenceMessages.ChordNamesPreferencePage_excluded_intervals);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(excludedIntervalsGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(excludedIntervalsGroup);

		// enclose in brackets
		final boolean encloseInBrackets = prefs.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_IN_BRACKETS);
		encloseInBracketsButton = new Button(excludedIntervalsGroup, SWT.CHECK);
		encloseInBracketsButton.setText(PreferenceMessages.ChordNamesPreferencePage_enclose_in_brackets + ": "); //$NON-NLS-1$
		encloseInBracketsButton.setSelection(encloseInBrackets);
		encloseInBracketsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				getNameProvider().setIntervalsInBrackets(encloseInBracketsButton.getSelection());
				updateExamples();
				updateBracketButtonEnablement();
			}
		});
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).applyTo(encloseInBracketsButton);
		getNameProvider().setIntervalsInBrackets(encloseInBrackets);

		final String bracket = prefs.getString(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BRACKETS_MODE);
		final Composite bracketsComposite = new Composite(excludedIntervalsGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).applyTo(bracketsComposite);
		roundBracketButton = createBracketRadioButton(bracketsComposite, Constants.BRACKETS_ROUND, bracket);
		squareBracketButton = createBracketRadioButton(bracketsComposite, Constants.BRACKETS_SQUARE, bracket);
		curlyBracketButton = createBracketRadioButton(bracketsComposite, Constants.BRACKETS_CURLY, bracket);
		angleBracketButton = createBracketRadioButton(bracketsComposite, Constants.BRACKETS_ANGLE, bracket);

		getNameProvider().setBracketsMode(bracket);
		updateBracketButtonEnablement();

		// interval names
		final Label intervalNamesLabel = new Label(excludedIntervalsGroup, SWT.NONE);
		intervalNamesLabel.setText(PreferenceMessages.ChordNamesPreferencePage_interval_names + ": "); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).applyTo(intervalNamesLabel);

		final boolean shortMode = prefs.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_SHORT_MODE);
		final Composite intervalsComposite = new Composite(excludedIntervalsGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(intervalsComposite);
		shortIntervalsButton = createIntervalsRadioButton(intervalsComposite,
				PreferenceMessages.ChordNamesPreferencePage_short_interval_names, true, shortMode);
		longIntervalsButton = createIntervalsRadioButton(intervalsComposite,
				PreferenceMessages.ChordNamesPreferencePage_long_interval_names, false, shortMode);
		getNameProvider().setIntervalsShortMode(shortMode);

		// prefix
		final Label prefixLabel = new Label(excludedIntervalsGroup, SWT.NONE);
		prefixLabel.setText(PreferenceMessages.ChordNamesPreferencePage_prefix + ": "); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).applyTo(prefixLabel);

		final String prefixMode = prefs.getString(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_PREFIX_MODE);
		final Composite prefixComposite = new Composite(excludedIntervalsGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(7).equalWidth(false).applyTo(prefixComposite);

		prefixNoButton = createPrefixRadioButton(prefixComposite, Constants.EXCLUDED_INTERVALS_PREFIX_NO, prefixMode);
		prefixMinusButton = createPrefixRadioButton(prefixComposite, Constants.EXCLUDED_INTERVALS_PREFIX_MINUS,
				prefixMode);
		prefixHyphenButton = createPrefixRadioButton(prefixComposite, Constants.EXCLUDED_INTERVALS_PREFIX_HYPHEN,
				prefixMode);
		prefixOmitButton = createPrefixRadioButton(prefixComposite, Constants.EXCLUDED_INTERVALS_PREFIX_OMIT,
				prefixMode);
		if (NlsUtil.isGerman()) {
			prefixOhneButton = createPrefixRadioButton(prefixComposite, Constants.EXCLUDED_INTERVALS_PREFIX_OHNE,
					prefixMode);
		}
		prefixOPointButton = createPrefixRadioButton(prefixComposite, Constants.EXCLUDED_INTERVALS_PREFIX_O_POINT,
				prefixMode);
		prefixOButton = createPrefixRadioButton(prefixComposite, Constants.EXCLUDED_INTERVALS_PREFIX_O, prefixMode);
		getNameProvider().setChordNamePrefix(prefixMode);

		// blank space between prefix and intervals
		final boolean blankSpaceBetweenPrefixAndIntervals = prefs
				.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_PREFIX_AND_INTERVALS);
		blankSpaceBetweenPrefixAndIntervalsButton = new Button(excludedIntervalsGroup, SWT.CHECK);
		blankSpaceBetweenPrefixAndIntervalsButton
				.setText(PreferenceMessages.ChordNamesPreferencePage_blank_space_between_prefix_and_intervals);
		blankSpaceBetweenPrefixAndIntervalsButton.setSelection(blankSpaceBetweenPrefixAndIntervals);
		blankSpaceBetweenPrefixAndIntervalsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				getNameProvider().setBlankSpaceBetweenPrefixAndIntervals(
						blankSpaceBetweenPrefixAndIntervalsButton.getSelection());
				updateExamples();
			}
		});
		GridDataFactory.fillDefaults().span(2, 1).applyTo(blankSpaceBetweenPrefixAndIntervalsButton);
		getNameProvider().setBlankSpaceBetweenPrefixAndIntervals(blankSpaceBetweenPrefixAndIntervals);

		// blank space between intervals
		final boolean blankSpaceBetweenIntervals = prefs
				.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_INTERVALS);
		blankSpaceBetweenIntervalsButton = new Button(excludedIntervalsGroup, SWT.CHECK);
		blankSpaceBetweenIntervalsButton
				.setText(PreferenceMessages.ChordNamesPreferencePage_blank_space_between_intervals);
		blankSpaceBetweenIntervalsButton.setSelection(blankSpaceBetweenIntervals);
		blankSpaceBetweenIntervalsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				getNameProvider().setBlankSpaceBetweenIntervals(blankSpaceBetweenIntervalsButton.getSelection());
				updateExamples();
			}
		});
		GridDataFactory.fillDefaults().span(2, 1).applyTo(blankSpaceBetweenIntervalsButton);
		getNameProvider().setBlankSpaceBetweenIntervals(blankSpaceBetweenIntervals);

		// compact mode
		final boolean compactMode = prefs.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_COMPACT_MODE);
		compactModeButton = new Button(excludedIntervalsGroup, SWT.CHECK);
		compactModeButton.setText(PreferenceMessages.ChordNamesPreferencePage_compact_mode);
		compactModeButton.setSelection(compactMode);
		compactModeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				getNameProvider().setCompactMode(compactModeButton.getSelection());
				updateExamples();
			}
		});
		GridDataFactory.fillDefaults().span(2, 1).applyTo(compactModeButton);
		getNameProvider().setCompactMode(compactMode);

		updateExamples();
	}

	private Button createBracketRadioButton(final Composite parent, final String bracket, final String currentBracket) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setText(bracket);
		button.setSelection(bracket.equals(currentBracket));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				getNameProvider().setBracketsMode(bracket);
				updateExamples();
			}
		});
		return button;
	}

	private Button createIntervalsRadioButton(final Composite parent, final String text, final boolean isShortMode,
			final boolean currentMode) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setText(text);
		button.setSelection(isShortMode == currentMode);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				getNameProvider().setIntervalsShortMode(isShortMode);
				updateExamples();
			}
		});
		return button;
	}

	private Button createPrefixRadioButton(final Composite parent, final String prefix, final String currentPrefix) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setText(prefix);
		button.setSelection(prefix.equals(currentPrefix));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				getNameProvider().setChordNamePrefix(prefix);
				updateExamples();
			}
		});
		return button;
	}

	private void updateBracketButtonEnablement() {
		if (encloseInBracketsButton != null && !encloseInBracketsButton.isDisposed()) {
			final boolean enabled = encloseInBracketsButton.getSelection();
			if (roundBracketButton != null && !roundBracketButton.isDisposed()) {
				roundBracketButton.setEnabled(enabled);
			}
			if (squareBracketButton != null && !squareBracketButton.isDisposed()) {
				squareBracketButton.setEnabled(enabled);
			}
			if (curlyBracketButton != null && !curlyBracketButton.isDisposed()) {
				curlyBracketButton.setEnabled(enabled);
			}
			if (angleBracketButton != null && !angleBracketButton.isDisposed()) {
				angleBracketButton.setEnabled(enabled);
			}
		}
	}

	@Override
	protected String getUseSeparatorString() {
		return PreferenceMessages.ChordNamePreferencePage_use_separator;
	}

	@Override
	protected String getUseSeparatorPreferenceId() {
		return Preferences.CHORD_NAMES_USE_SEPARATOR;
	}

	@Override
	protected String getSeparatorPreferenceId() {
		return Preferences.CHORD_NAMES_SEPARATOR;
	}

	@Override
	protected void setUseSeparator(final boolean useSeparator) {
		getNameProvider().setUseChordNameSeparator(useSeparator);
	}

	@Override
	protected void setSeparator(final String separator) {
		getNameProvider().setChordNameSeparatorMode(separator);
	}

	@Override
	protected String getHelpContext() {
		return HELP_ID;
	}

	@Override
	protected void addExamples(final List<Object> examples) {
		final boolean chordsExist = !ChordList.getInstance().getRootCategory().getAllElements().isEmpty();
		if (chordsExist) {
			if (chordsExist) {
				examples.add(ExampleCreator.createRandomGriptable(false, false, false));
				examples.add(ExampleCreator.createRandomGriptable(false, false, false));
				examples.add(ExampleCreator.createRandomGriptable(false, true, false));
				examples.add(ExampleCreator.createRandomGriptable(false, true, true));
				examples.add(ExampleCreator.createRandomGriptable(true, true, true));
			}
			getRefreshExamplesButton().setEnabled(true);
		} else {
			showExampleErrorMessage(PreferenceMessages.ChordNamesPreferencePage_no_chords_exist);
		}
	}

	@Override
	public boolean performOk() {

		final NameProvider nameProvider = NamesUtil.getNameProvider();

		// enclose in brackets
		final boolean encloseInBrackets = encloseInBracketsButton.getSelection();
		nameProvider.setIntervalsInBrackets(encloseInBrackets);
		prefs.setValue(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_IN_BRACKETS, encloseInBrackets);

		final String bracketsMode = squareBracketButton.getSelection() ? Constants.BRACKETS_SQUARE : curlyBracketButton
				.getSelection() ? Constants.BRACKETS_CURLY
				: angleBracketButton.getSelection() ? Constants.BRACKETS_ANGLE : Constants.BRACKETS_ROUND;
		nameProvider.setBracketsMode(bracketsMode);
		prefs.setValue(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BRACKETS_MODE, bracketsMode);

		// interval names
		final boolean shortIntervalNames = shortIntervalsButton.getSelection();
		nameProvider.setIntervalsShortMode(shortIntervalNames);
		prefs.setValue(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_SHORT_MODE, shortIntervalNames);

		// prefix
		final String prefix = prefixMinusButton.getSelection() ? Constants.EXCLUDED_INTERVALS_PREFIX_MINUS
				: prefixHyphenButton.getSelection() ? Constants.EXCLUDED_INTERVALS_PREFIX_HYPHEN : prefixOmitButton
						.getSelection() ? Constants.EXCLUDED_INTERVALS_PREFIX_OMIT : prefixOhneButton != null
						&& prefixOhneButton.getSelection() ? Constants.EXCLUDED_INTERVALS_PREFIX_OHNE
						: prefixOPointButton.getSelection() ? Constants.EXCLUDED_INTERVALS_PREFIX_O_POINT
								: prefixOButton.getSelection() ? Constants.EXCLUDED_INTERVALS_PREFIX_O
										: Constants.EXCLUDED_INTERVALS_PREFIX_NO;
		nameProvider.setChordNamePrefix(prefix);
		prefs.setValue(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_PREFIX_MODE, prefix);

		// blank spaces
		final boolean blankSpaceBetweenPrefixAndIntervals = blankSpaceBetweenPrefixAndIntervalsButton.getSelection();
		nameProvider.setBlankSpaceBetweenPrefixAndIntervals(blankSpaceBetweenPrefixAndIntervals);
		prefs.setValue(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_PREFIX_AND_INTERVALS,
				blankSpaceBetweenPrefixAndIntervals);

		final boolean blankSpaceBetweenIntervals = blankSpaceBetweenIntervalsButton.getSelection();
		nameProvider.setBlankSpaceBetweenIntervals(blankSpaceBetweenIntervals);
		prefs.setValue(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_INTERVALS,
				blankSpaceBetweenIntervals);

		final boolean compactMode = compactModeButton.getSelection();
		nameProvider.setCompactMode(compactMode);
		prefs.setValue(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_COMPACT_MODE, compactMode);

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		// enclose in brackets
		final boolean defaultEncloseInBrackets = prefs
				.getDefaultBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_IN_BRACKETS);
		encloseInBracketsButton.setSelection(defaultEncloseInBrackets);
		getNameProvider().setIntervalsInBrackets(defaultEncloseInBrackets);

		final String defaultBracket = prefs.getDefaultString(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BRACKETS_MODE);
		roundBracketButton.setSelection(Constants.BRACKETS_ROUND.equals(defaultBracket));
		squareBracketButton.setSelection(Constants.BRACKETS_SQUARE.equals(defaultBracket));
		curlyBracketButton.setSelection(Constants.BRACKETS_CURLY.equals(defaultBracket));
		angleBracketButton.setSelection(Constants.BRACKETS_ANGLE.equals(defaultBracket));
		getNameProvider().setBracketsMode(defaultBracket);

		// interval names
		final boolean defaultShortMode = prefs.getDefaultBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_SHORT_MODE);
		shortIntervalsButton.setSelection(defaultShortMode);
		longIntervalsButton.setSelection(!defaultShortMode);
		getNameProvider().setIntervalsShortMode(defaultShortMode);

		// prefix
		final String defaultPrefix = prefs.getDefaultString(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_PREFIX_MODE);
		prefixNoButton.setSelection(Constants.EXCLUDED_INTERVALS_PREFIX_NO.equals(defaultPrefix));
		prefixMinusButton.setSelection(Constants.EXCLUDED_INTERVALS_PREFIX_MINUS.equals(defaultPrefix));
		prefixHyphenButton.setSelection(Constants.EXCLUDED_INTERVALS_PREFIX_HYPHEN.equals(defaultPrefix));
		prefixOmitButton.setSelection(Constants.EXCLUDED_INTERVALS_PREFIX_OMIT.equals(defaultPrefix));
		if (prefixOhneButton != null) {
			prefixOhneButton.setSelection(Constants.EXCLUDED_INTERVALS_PREFIX_OHNE.equals(defaultPrefix));
		}
		prefixOPointButton.setSelection(Constants.EXCLUDED_INTERVALS_PREFIX_O_POINT.equals(defaultPrefix));
		prefixOButton.setSelection(Constants.EXCLUDED_INTERVALS_PREFIX_O.equals(defaultPrefix));
		getNameProvider().setChordNamePrefix(defaultPrefix);

		// blank spaces
		final boolean defaultBlankSpaceBetweenPrefixAndIntervals = prefs
				.getDefaultBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_PREFIX_AND_INTERVALS);
		blankSpaceBetweenPrefixAndIntervalsButton.setSelection(defaultBlankSpaceBetweenPrefixAndIntervals);
		getNameProvider().setBlankSpaceBetweenPrefixAndIntervals(defaultBlankSpaceBetweenPrefixAndIntervals);

		final boolean defaultBlankSpaceBetweenIntervals = prefs
				.getDefaultBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_INTERVALS);
		blankSpaceBetweenIntervalsButton.setSelection(defaultBlankSpaceBetweenIntervals);
		getNameProvider().setBlankSpaceBetweenIntervals(defaultBlankSpaceBetweenIntervals);

		final boolean defaultCompactMode = prefs
				.getDefaultBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_COMPACT_MODE);
		compactModeButton.setSelection(defaultCompactMode);
		getNameProvider().setCompactMode(defaultCompactMode);

		updateBracketButtonEnablement();
		updateExamples();
	}
}
