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

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.util.NameProvider;
import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public abstract class AbstractNamesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private IPreferenceStore prefs;
	private NameProvider nameProvider;

	private Button useSeparatorButton;
	private Button blankSpaceButton;
	private Button hyphenButton;
	private Button blankSpaceAndHyphenButton;
	private Button refreshExamplesButton;
	private ArrayList<Label> exampleLabels;
	private List<Object> examples;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
		nameProvider = new NameProvider();
	}

	protected NameProvider getNameProvider() {
		return nameProvider;
	}

	@Override
	protected Control createContents(final Composite parent) {

		// main composite
		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().equalWidth(false).applyTo(mainComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(mainComposite);

		// use separator
		final boolean useSeparator = prefs.getBoolean(getUseSeparatorPreferenceId());
		useSeparatorButton = new Button(mainComposite, SWT.CHECK);
		useSeparatorButton.setText(getUseSeparatorString());
		useSeparatorButton.setSelection(useSeparator);
		useSeparatorButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				setUseSeparator(useSeparatorButton.getSelection());
				updateExamples();
				updateButtonEnablement();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).indent(1, 10).applyTo(useSeparatorButton);
		setUseSeparator(useSeparator);

		// separator group
		final String separator = prefs.getString(getSeparatorPreferenceId());
		final Group separatorGroup = new Group(mainComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(separatorGroup);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).margins(5, 5).applyTo(separatorGroup);

		blankSpaceButton = createSeparatorRadioBox(separatorGroup,
				PreferenceMessages.ChordNamesPreferencePage_blank_space, Constants.BLANK_SPACE, separator);
		hyphenButton = createSeparatorRadioBox(separatorGroup, PreferenceMessages.ExportFilenamePreferencePage_hyphen,
				Constants.HYPHEN, separator);
		blankSpaceAndHyphenButton = createSeparatorRadioBox(separatorGroup,
				PreferenceMessages.ExportFilenamePreferencePage_white_spaces_and_hyphen,
				Constants.BLANK_SPACE_AND_HYPHEN, separator);

		updateButtonEnablement();
		extendContents(mainComposite);

		// examples
		final Composite exampleHeaderComposite = new Composite(mainComposite, SWT.NONE);
		GridDataFactory.fillDefaults().indent(0, 20).applyTo(exampleHeaderComposite);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(exampleHeaderComposite);

		final Label examplesHeaderlabel = new Label(exampleHeaderComposite, SWT.LEFT);
		examplesHeaderlabel.setText(PreferenceMessages.ExportFilenamePreferencePage_examples);

		final Label dummyLabel = new Label(exampleHeaderComposite, SWT.LEFT);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(dummyLabel);

		refreshExamplesButton = new Button(exampleHeaderComposite, SWT.PUSH | SWT.FLAT);
		refreshExamplesButton.setText(PreferenceMessages.ExportFilenamePreferencePage_refresh_examples);
		refreshExamplesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				createExamples();
				updateExamples();
			}
		});

		final Composite exampleComposite = new Composite(mainComposite, SWT.BORDER);
		exampleComposite.setBackground(mainComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayoutFactory.fillDefaults().margins(0, 0).spacing(0, 5).applyTo(exampleComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(exampleComposite);

		exampleLabels = new ArrayList<Label>();
		for (int i = 0; i < 5; i++) {
			final Label label = new Label(exampleComposite, SWT.LEFT);
			label.setFont(separatorGroup.getFont());
			label.setBackground(mainComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			exampleLabels.add(label);
		}
		createExamples();
		updateExamples();

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), getHelpContext());

		return mainComposite;
	}

	private Button createSeparatorRadioBox(final Composite parent, final String text, final String separator,
			final String currentSeparator) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setText(text);
		button.setSelection(separator.equals(currentSeparator));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				setSeparator(separator);
				updateExamples();
			}
		});
		setSeparator(currentSeparator);
		return button;
	}

	private void updateButtonEnablement() {
		if (useSeparatorButton != null && !useSeparatorButton.isDisposed()) {
			final boolean useSeparator = useSeparatorButton.getSelection();
			blankSpaceButton.setEnabled(useSeparator);
			hyphenButton.setEnabled(useSeparator);
			blankSpaceAndHyphenButton.setEnabled(useSeparator);
		}
	}

	private void createExamples() {
		examples = new ArrayList<Object>();
		addExamples(examples);
	}

	protected void updateExamples() {
		if (exampleLabels == null || exampleLabels.isEmpty()) {
			return;
		}

		final String notesMode = prefs.getString(Preferences.NOTES_MODE);

		int i = 0;
		for (final Object example : examples) {
			final Label label = exampleLabels.get(i);
			if (example instanceof Griptable) {
				label.setText(nameProvider.getName((Griptable) example, notesMode));
			} else if (example instanceof Scale) {
				label.setText(nameProvider.getName((Scale) example, notesMode));
			} else {
				example.toString();
			}
			i++;
		}
		exampleLabels.get(0).getParent().layout(true, true);
	}

	protected Button getRefreshExamplesButton() {
		return refreshExamplesButton;
	}

	protected void showExampleErrorMessage(final String message) {
		final Label firstLabel = exampleLabels.get(0);
		firstLabel.setText(message);
		firstLabel.setForeground(refreshExamplesButton.getDisplay().getSystemColor(SWT.COLOR_RED));
		for (int i = 1; i < 5; i++) {
			firstLabel.setText("");
		}
		refreshExamplesButton.setEnabled(false);
	}

	protected void extendContents(final Composite parent) {
	}

	protected abstract String getUseSeparatorString();

	protected abstract String getUseSeparatorPreferenceId();

	protected abstract String getSeparatorPreferenceId();

	protected abstract String getHelpContext();

	protected abstract void addExamples(List<Object> examples);

	protected abstract void setUseSeparator(boolean useSeparator);

	protected abstract void setSeparator(String separator);

	@Override
	public boolean performOk() {
		final NameProvider globalNameProvider = NamesUtil.getNameProvider();

		// separator
		final boolean useSeparator = useSeparatorButton.getSelection();
		final String separator = hyphenButton.getSelection() ? Constants.HYPHEN : blankSpaceAndHyphenButton
				.getSelection() ? Constants.BLANK_SPACE_AND_HYPHEN : Constants.BLANK_SPACE;
		if (Preferences.CHORD_NAMES_USE_SEPARATOR.equals(getUseSeparatorPreferenceId())) {
			globalNameProvider.setUseChordNameSeparator(useSeparator);
			globalNameProvider.setChordNameSeparatorMode(separator);
		} else if (Preferences.SCALE_NAMES_USE_SEPARATOR.equals(getUseSeparatorPreferenceId())) {
			globalNameProvider.setUseScaleNameSeparator(useSeparator);
			globalNameProvider.setScaleNameSeparatorMode(separator);
		}
		prefs.setValue(getUseSeparatorPreferenceId(), useSeparator);
		prefs.setValue(getSeparatorPreferenceId(), separator);

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		// use separator
		final boolean defaultUseSeparator = prefs.getDefaultBoolean(getUseSeparatorPreferenceId());
		useSeparatorButton.setSelection(defaultUseSeparator);
		setUseSeparator(defaultUseSeparator);

		// separator
		final String defaultSeparator = prefs.getDefaultString(getSeparatorPreferenceId());
		blankSpaceButton.setSelection(Constants.BLANK_SPACE.equals(defaultSeparator));
		hyphenButton.setSelection(Constants.HYPHEN.equals(defaultSeparator));
		blankSpaceAndHyphenButton.setSelection(Constants.BLANK_SPACE_AND_HYPHEN.equals(defaultSeparator));
		setSeparator(defaultSeparator);

		updateButtonEnablement();
	}
}
