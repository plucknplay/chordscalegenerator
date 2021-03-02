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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.util.ExportFilenameRecommender;
import com.plucknplay.csg.ui.util.MyBooleanFieldEditor;

public class ExportFilenamePreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.export.filename"; //$NON-NLS-1$
	public static final String HELP_ID = "export_filename_preference_page_context"; //$NON-NLS-1$

	private static final String[] VIEWS = { ExportFilenameRecommender.VIEW_BOX, ExportFilenameRecommender.VIEW_TAB,
			ExportFilenameRecommender.VIEW_NOTES, ExportFilenameRecommender.VIEW_FRETBOARD,
			ExportFilenameRecommender.VIEW_KEYBOARD };

	private IPreferenceStore prefs;
	private ExportFilenameRecommender filenameRecommender;

	private Button boxButton;
	private Button tabButton;
	private Button notesButton;
	private Button fretboardButton;
	private Button keyboardButton;

	private Button inFrontButton;
	private Button atTheEndButton;

	private MyBooleanFieldEditor suggestFilenameEditor;
	private MyBooleanFieldEditor replaceWhiteSpaceEditor;

	private Button replaceWhiteSpaceCharWithUnderscoreButton;
	private Button replaceWhiteSpaceCharWithHyphenButton;

	private Group illegalCharGroup;
	private Button replaceIllegalCharWithUnderscoreButton;
	private Button replaceIllegalCharWithHyphenButton;
	private Button replaceIllegalCharWithSpaceButton;

	private Group logicalUnitsGroup;
	private Button separateUnitsWithUnderscoreButton;
	private Button separateUnitsWithDoubleUnderscoreButton;
	private Button separateUnitsWithHyphenButton;
	private Button separateUnitsWithSpaceAndHyphenButton;
	private Button separateUnitsWithSpaceButton;

	private List<Object> examples;
	private List<Label> exampleLabels;
	private Button refreshExamplesButton;

	@Override
	public void init(final IWorkbench workbench) {
		super.init(workbench);
		prefs = Activator.getDefault().getPreferenceStore();
		filenameRecommender = new ExportFilenameRecommender();
	}

	@Override
	protected void createFieldEditors() {

		// preference page links
		PreferenceLinkUtil.createChordAndScaleNamesLink(
				PreferenceLinkUtil.createMainLinkComposite(getFieldEditorParent()),
				(IWorkbenchPreferenceContainer) getContainer());

		// suggest filename
		suggestFilenameEditor = new MyBooleanFieldEditor(Preferences.EXPORT_FILENAME_SUGGESTION,
				PreferenceMessages.ExportFilenamePreferencePage_suggest_filename, getFieldEditorParent());
		addField(suggestFilenameEditor);
		GridDataFactory.fillDefaults().indent(5, 0)
				.applyTo(suggestFilenameEditor.getDescriptionControl(getFieldEditorParent()));

		// view group
		final Group viewGroup = new Group(getFieldEditorParent(), SWT.NONE);
		viewGroup.setText(PreferenceMessages.ExportFilenamePreferencePage_add_view);
		GridLayoutFactory.fillDefaults().numColumns(5).equalWidth(false).margins(5, 5).applyTo(viewGroup);
		GridDataFactory.fillDefaults().grab(true, false).indent(0, 20).applyTo(viewGroup);

		boxButton = addCheckBox(viewGroup, PreferenceMessages.ExportFilenamePreferencePage_box,
				Preferences.EXPORT_FILENAME_ADD_VIEW_BOX);
		boxButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setAddViewBox(boxButton.getSelection());
				updateExamples();
			}
		});

		tabButton = addCheckBox(viewGroup, PreferenceMessages.ExportFilenamePreferencePage_tab,
				Preferences.EXPORT_FILENAME_ADD_VIEW_TAB);
		tabButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setAddViewTab(tabButton.getSelection());
				updateExamples();
			}
		});

		notesButton = addCheckBox(viewGroup, PreferenceMessages.ExportFilenamePreferencePage_notes,
				Preferences.EXPORT_FILENAME_ADD_VIEW_NOTES);
		notesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setAddViewNotes(notesButton.getSelection());
				updateExamples();
			}
		});

		fretboardButton = addCheckBox(viewGroup, PreferenceMessages.ExportFilenamePreferencePage_fretboard,
				Preferences.EXPORT_FILENAME_ADD_VIEW_FRETBOARD);
		fretboardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setAddViewFretboard(fretboardButton.getSelection());
				updateExamples();
			}
		});

		keyboardButton = addCheckBox(viewGroup, PreferenceMessages.ExportFilenamePreferencePage_keyboard,
				Preferences.EXPORT_FILENAME_ADD_VIEW_KEYBOARD);
		keyboardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setAddViewKeyboard(keyboardButton.getSelection());
				updateExamples();
			}
		});

		final Composite viewPositionComposite = new Composite(viewGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(viewPositionComposite);
		GridDataFactory.fillDefaults().grab(true, false).span(5, 1).applyTo(viewPositionComposite);

		inFrontButton = new Button(viewPositionComposite, SWT.RADIO);
		inFrontButton.setText(PreferenceMessages.ExportFilenamePreferencePage_in_front);
		inFrontButton.setSelection(prefs.getBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_IN_FRONT));
		inFrontButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setPutViewAdditionInFront(inFrontButton.getSelection());
				updateExamples();
			}
		});

		atTheEndButton = new Button(viewPositionComposite, SWT.RADIO);
		atTheEndButton.setText(PreferenceMessages.ExportFilenamePreferencePage_at_the_end);
		atTheEndButton.setSelection(!prefs.getBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_IN_FRONT));
		atTheEndButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setPutViewAdditionInFront(!atTheEndButton.getSelection());
				updateExamples();
			}
		});

		// replace white space characters
		replaceWhiteSpaceEditor = new MyBooleanFieldEditor(Preferences.EXPORT_FILENAME_REPLACE_WHITE_SPACE,
				PreferenceMessages.ExportFilenamePreferencePage_replace_white_space, getFieldEditorParent());
		replaceWhiteSpaceEditor.getButton(getFieldEditorParent()).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setReplaceWhiteSpaces(replaceWhiteSpaceEditor.getButton(getFieldEditorParent())
						.getSelection());
			}
		});
		addField(replaceWhiteSpaceEditor);
		GridDataFactory.fillDefaults().grab(true, false).indent(5, 10)
				.applyTo(replaceWhiteSpaceEditor.getDescriptionControl(getFieldEditorParent()));
		final Group whiteSpaceCharGroup = new Group(getFieldEditorParent(), SWT.NONE);
		whiteSpaceCharGroup.setText(""); //$NON-NLS-1$
		GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).margins(5, 5).applyTo(whiteSpaceCharGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(whiteSpaceCharGroup);

		replaceWhiteSpaceCharWithUnderscoreButton = addRadioButton1(whiteSpaceCharGroup,
				PreferenceMessages.ExportFilenamePreferencePage_underscore,
				Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_WHITE_SPACE, Constants.UNDERSCORE);
		replaceWhiteSpaceCharWithHyphenButton = addRadioButton1(whiteSpaceCharGroup,
				PreferenceMessages.ExportFilenamePreferencePage_hyphen,
				Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_WHITE_SPACE, Constants.HYPHEN);

		// replace illegal characters
		illegalCharGroup = new Group(getFieldEditorParent(), SWT.NONE);
		illegalCharGroup.setText(PreferenceMessages.ExportFilenamePreferencePage_replace_illegal_characters);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).margins(5, 5).applyTo(illegalCharGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(illegalCharGroup);

		replaceIllegalCharWithUnderscoreButton = addRadioButton2(illegalCharGroup,
				PreferenceMessages.ExportFilenamePreferencePage_underscore,
				Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_ILLEGAL_CHARACTER, Constants.UNDERSCORE);
		replaceIllegalCharWithHyphenButton = addRadioButton2(illegalCharGroup,
				PreferenceMessages.ExportFilenamePreferencePage_hyphen,
				Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_ILLEGAL_CHARACTER, Constants.HYPHEN);
		replaceIllegalCharWithSpaceButton = addRadioButton2(illegalCharGroup,
				PreferenceMessages.ExportFilenamePreferencePage_white_space,
				Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_ILLEGAL_CHARACTER, Constants.BLANK_SPACE);

		// separate logic units
		logicalUnitsGroup = new Group(getFieldEditorParent(), SWT.NONE);
		logicalUnitsGroup.setText(PreferenceMessages.ExportFilenamePreferencePage_separate_logic_units);
		GridLayoutFactory.fillDefaults().numColumns(5).equalWidth(false).margins(5, 5).applyTo(logicalUnitsGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(logicalUnitsGroup);

		separateUnitsWithUnderscoreButton = addRadioButton3(logicalUnitsGroup,
				PreferenceMessages.ExportFilenamePreferencePage_underscore,
				Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT, Constants.UNDERSCORE);
		separateUnitsWithDoubleUnderscoreButton = addRadioButton3(logicalUnitsGroup,
				PreferenceMessages.ExportFilenamePreferencePage_double_underscore,
				Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT, Constants.DOUBLE_UNDERSCORE);
		separateUnitsWithHyphenButton = addRadioButton3(logicalUnitsGroup,
				PreferenceMessages.ExportFilenamePreferencePage_hyphen,
				Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT, Constants.HYPHEN);
		separateUnitsWithSpaceAndHyphenButton = addRadioButton3(logicalUnitsGroup,
				PreferenceMessages.ExportFilenamePreferencePage_white_spaces_and_hyphen,
				Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT, Constants.BLANK_SPACE_AND_HYPHEN);
		separateUnitsWithSpaceButton = addRadioButton3(logicalUnitsGroup,
				PreferenceMessages.ExportFilenamePreferencePage_white_space,
				Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT, Constants.BLANK_SPACE);

		// examples
		final Composite exampleHeaderComposite = new Composite(getFieldEditorParent(), SWT.NONE);
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

		final Composite exampleComposite = new Composite(getFieldEditorParent(), SWT.BORDER);
		exampleComposite.setBackground(getFieldEditorParent().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayoutFactory.fillDefaults().margins(0, 0).spacing(0, 5).applyTo(exampleComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(exampleComposite);

		exampleLabels = new ArrayList<Label>();
		for (int i = 0; i < 5; i++) {
			final Label label = new Label(exampleComposite, SWT.LEFT);
			label.setFont(logicalUnitsGroup.getFont());
			label.setBackground(getFieldEditorParent().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			exampleLabels.add(label);
		}
		createExamples();

		// add selection listener
		suggestFilenameEditor.getButton(getFieldEditorParent()).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateEnablement(null, null);
			}
		});

		replaceWhiteSpaceEditor.getButton(getFieldEditorParent()).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateEnablement(null, null);
				updateVisibility(null);
				updateExamples();
			}
		});

		updateEnablement(prefs.getBoolean(Preferences.EXPORT_FILENAME_SUGGESTION),
				prefs.getBoolean(Preferences.EXPORT_FILENAME_REPLACE_WHITE_SPACE));
		updateVisibility(!prefs.getBoolean(Preferences.EXPORT_FILENAME_REPLACE_WHITE_SPACE));
		updateExamples();

		// finally fix layout problems (bug #88)
		replaceWhiteSpaceEditor.getDescriptionControl(getFieldEditorParent()).setFont(logicalUnitsGroup.getFont());

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}

	private void createExamples() {
		examples = new ArrayList<Object>();

		final boolean chordsExist = !ChordList.getInstance().getRootCategory().getAllElements().isEmpty();
		final boolean scalesExist = !ScaleList.getInstance().getRootCategory().getAllElements().isEmpty();

		if (chordsExist || scalesExist) {
			if (chordsExist) {
				examples.add(ExampleCreator.createRandomGriptable());
				examples.add(ExampleCreator.createRandomGriptable());
			}
			if (scalesExist) {
				examples.add(ExampleCreator.createRandomBlock(0));
				examples.add(ExampleCreator.createRandomBlock(1));
				examples.add(ExampleCreator.createRandomBlock(2));
			}
			refreshExamplesButton.setEnabled(true);

		} else {
			final Label firstLabel = exampleLabels.get(0);
			firstLabel.setText(PreferenceMessages.ExportFilenamePreferencePage_no_chords_or_scale_exist);
			firstLabel.setForeground(refreshExamplesButton.getDisplay().getSystemColor(SWT.COLOR_RED));
			for (int i = 1; i < 5; i++) {
				firstLabel.setText("");
			}
			refreshExamplesButton.setEnabled(false);
		}
	}

	private void updateExamples() {
		int i = 0;
		if (examples != null) {
			for (final Object example : examples) {
				exampleLabels.get(i).setText(filenameRecommender.suggestFilename(example, VIEWS[i]));
				i++;
			}
		}
		exampleLabels.get(0).getParent().layout(true, true);
	}

	protected void updateVisibility(final Boolean setVisible) {

		final boolean visible = setVisible == null ? !replaceWhiteSpaceEditor.getBooleanValue() : setVisible;

		// hide white space replacement (if necessary)
		replaceIllegalCharWithSpaceButton.setVisible(visible);
		if (!visible && replaceIllegalCharWithSpaceButton.getSelection()) {
			replaceIllegalCharWithSpaceButton.setSelection(false);
			replaceIllegalCharWithUnderscoreButton.setSelection(true);
			filenameRecommender.setIllegalCharReplacement(Constants.UNDERSCORE);
		}

		// hide white space separation (if necessary)
		separateUnitsWithSpaceAndHyphenButton.setVisible(visible);
		separateUnitsWithSpaceButton.setVisible(visible);
		if (!visible
				&& (separateUnitsWithSpaceAndHyphenButton.getSelection() || separateUnitsWithSpaceButton.getSelection())) {
			separateUnitsWithSpaceAndHyphenButton.setSelection(false);
			separateUnitsWithSpaceButton.setSelection(false);
			separateUnitsWithUnderscoreButton.setSelection(true);
			filenameRecommender.setLogicalUnitsSeparation(Constants.UNDERSCORE);
		}

		updateExamples();
	}

	private void updateEnablement(final Boolean setEnabled, final Boolean setEnabled2) {

		final boolean enabled = setEnabled == null ? suggestFilenameEditor.getButton(getFieldEditorParent())
				.getSelection() : setEnabled;
		final boolean enabled2 = setEnabled2 == null ? replaceWhiteSpaceEditor.getButton(getFieldEditorParent())
				.getSelection() : setEnabled2;

		boxButton.setEnabled(enabled);
		tabButton.setEnabled(enabled);
		notesButton.setEnabled(enabled);
		fretboardButton.setEnabled(enabled);
		keyboardButton.setEnabled(enabled);
		inFrontButton.setEnabled(enabled);
		atTheEndButton.setEnabled(enabled);
		replaceWhiteSpaceEditor.setEnabled(enabled, getFieldEditorParent());
		replaceWhiteSpaceCharWithUnderscoreButton.setEnabled(enabled && enabled2);
		replaceWhiteSpaceCharWithHyphenButton.setEnabled(enabled && enabled2);
		replaceIllegalCharWithUnderscoreButton.setEnabled(enabled);
		replaceIllegalCharWithHyphenButton.setEnabled(enabled);
		replaceIllegalCharWithSpaceButton.setEnabled(enabled);
		separateUnitsWithUnderscoreButton.setEnabled(enabled);
		separateUnitsWithDoubleUnderscoreButton.setEnabled(enabled);
		separateUnitsWithHyphenButton.setEnabled(enabled);
		separateUnitsWithSpaceAndHyphenButton.setEnabled(enabled);
		separateUnitsWithSpaceButton.setEnabled(enabled);
		refreshExamplesButton.setEnabled(enabled);
	}

	private Button addCheckBox(final Composite parent, final String text, final String prefId) {
		final Button button = addButton(parent, SWT.CHECK, text, prefId);
		button.setSelection(prefs.getBoolean(prefId));
		return button;
	}

	private Button addRadioButton(final Composite parent, final String text, final String prefId, final String value) {
		final Button button = addButton(parent, SWT.RADIO, text, prefId);
		button.setSelection(prefs.getString(prefId).equals(value));
		return button;
	}

	private Button addRadioButton1(final Composite parent, final String text, final String prefId, final String value) {
		final Button button = addRadioButton(parent, text, prefId, value);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setWhiteSpaceReplacement(value);
				updateExamples();
			}
		});
		return button;
	}

	private Button addRadioButton2(final Composite parent, final String text, final String prefId, final String value) {
		final Button button = addRadioButton(parent, text, prefId, value);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setIllegalCharReplacement(value);
				updateExamples();
			}
		});
		return button;
	}

	private Button addRadioButton3(final Composite parent, final String text, final String prefId, final String value) {
		final Button button = addRadioButton(parent, text, prefId, value);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				filenameRecommender.setLogicalUnitsSeparation(value);
				updateExamples();
			}
		});
		return button;
	}

	private Button addButton(final Composite parent, final int style, final String text, final String prefId) {
		final Button button = new Button(parent, style);
		button.setText(text);
		return button;
	}

	@Override
	public boolean performOk() {

		// view addition
		prefs.setValue(Preferences.EXPORT_FILENAME_ADD_VIEW_BOX, boxButton.getSelection());
		prefs.setValue(Preferences.EXPORT_FILENAME_ADD_VIEW_TAB, tabButton.getSelection());
		prefs.setValue(Preferences.EXPORT_FILENAME_ADD_VIEW_NOTES, notesButton.getSelection());
		prefs.setValue(Preferences.EXPORT_FILENAME_ADD_VIEW_FRETBOARD, fretboardButton.getSelection());
		prefs.setValue(Preferences.EXPORT_FILENAME_ADD_VIEW_KEYBOARD, keyboardButton.getSelection());

		// view position
		prefs.setValue(Preferences.EXPORT_FILENAME_ADD_VIEW_IN_FRONT, inFrontButton.getSelection());

		// white space character replacement
		String whiteSpaceCharValue = replaceWhiteSpaceCharWithUnderscoreButton.getSelection() ? Constants.UNDERSCORE
				: replaceWhiteSpaceCharWithHyphenButton.getSelection() ? Constants.HYPHEN : null;
		if (whiteSpaceCharValue == null) {
			whiteSpaceCharValue = prefs.getDefaultString(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_WHITE_SPACE);
		}
		prefs.setValue(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_WHITE_SPACE, whiteSpaceCharValue);

		// illegal character replacement
		String illegalCharValue = replaceIllegalCharWithUnderscoreButton.getSelection() ? Constants.UNDERSCORE
				: replaceIllegalCharWithHyphenButton.getSelection() ? Constants.HYPHEN
						: replaceIllegalCharWithSpaceButton.getSelection() ? Constants.BLANK_SPACE : null;
		if (illegalCharValue == null) {
			illegalCharValue = prefs.getDefaultString(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_ILLEGAL_CHARACTER);
		}
		prefs.setValue(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_ILLEGAL_CHARACTER, illegalCharValue);

		// logical units separation
		String logicUnitsValue = separateUnitsWithUnderscoreButton.getSelection() ? Constants.UNDERSCORE
				: separateUnitsWithDoubleUnderscoreButton.getSelection() ? Constants.DOUBLE_UNDERSCORE
						: separateUnitsWithHyphenButton.getSelection() ? Constants.HYPHEN
								: separateUnitsWithSpaceAndHyphenButton.getSelection() ? Constants.BLANK_SPACE_AND_HYPHEN
										: separateUnitsWithSpaceButton.getSelection() ? Constants.BLANK_SPACE : null;
		if (logicUnitsValue == null) {
			logicUnitsValue = prefs.getDefaultString(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT);
		}
		prefs.setValue(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT, logicUnitsValue);

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		// view addition
		final boolean defaultAddBoxView = prefs.getDefaultBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_BOX);
		boxButton.setSelection(defaultAddBoxView);
		filenameRecommender.setAddViewBox(defaultAddBoxView);

		final boolean defaultAddTabView = prefs.getDefaultBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_TAB);
		tabButton.setSelection(defaultAddTabView);
		filenameRecommender.setAddViewTab(defaultAddTabView);

		final boolean defaultAddNotesView = prefs.getDefaultBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_NOTES);
		notesButton.setSelection(defaultAddNotesView);
		filenameRecommender.setAddViewNotes(defaultAddNotesView);

		final boolean defaultAddFretboardView = prefs.getDefaultBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_FRETBOARD);
		fretboardButton.setSelection(defaultAddFretboardView);
		filenameRecommender.setAddViewFretboard(defaultAddFretboardView);

		final boolean defaultAddKeyboardView = prefs.getDefaultBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_KEYBOARD);
		keyboardButton.setSelection(defaultAddKeyboardView);
		filenameRecommender.setAddViewKeyboard(defaultAddKeyboardView);

		// view position
		final boolean defaultPutViewInFront = prefs.getDefaultBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_IN_FRONT);
		inFrontButton.setSelection(defaultPutViewInFront);
		atTheEndButton.setSelection(!defaultPutViewInFront);
		filenameRecommender.setPutViewAdditionInFront(defaultPutViewInFront);

		// illegal character replacement
		final String defaultWhiteSpaceReplacement = prefs
				.getDefaultString(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_WHITE_SPACE);
		replaceWhiteSpaceCharWithUnderscoreButton.setSelection(defaultWhiteSpaceReplacement
				.equals(Constants.UNDERSCORE));
		replaceWhiteSpaceCharWithHyphenButton.setSelection(defaultWhiteSpaceReplacement.equals(Constants.HYPHEN));
		filenameRecommender.setWhiteSpaceReplacement(defaultWhiteSpaceReplacement);

		// illegal character replacement
		final String defaultIllegalCharReplacement = prefs
				.getDefaultString(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_ILLEGAL_CHARACTER);
		replaceIllegalCharWithUnderscoreButton.setSelection(defaultIllegalCharReplacement.equals(Constants.UNDERSCORE));
		replaceIllegalCharWithHyphenButton.setSelection(defaultIllegalCharReplacement.equals(Constants.HYPHEN));
		replaceIllegalCharWithSpaceButton.setSelection(defaultIllegalCharReplacement.equals(Constants.BLANK_SPACE));
		filenameRecommender.setIllegalCharReplacement(defaultIllegalCharReplacement);

		// logical units separation
		final String defaultLogicUnitSeparation = prefs
				.getDefaultString(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT);
		separateUnitsWithUnderscoreButton.setSelection(defaultLogicUnitSeparation.equals(Constants.UNDERSCORE));
		separateUnitsWithDoubleUnderscoreButton.setSelection(defaultLogicUnitSeparation
				.equals(Constants.DOUBLE_UNDERSCORE));
		separateUnitsWithHyphenButton.setSelection(defaultLogicUnitSeparation.equals(Constants.HYPHEN));
		separateUnitsWithSpaceAndHyphenButton.setSelection(defaultLogicUnitSeparation
				.equals(Constants.BLANK_SPACE_AND_HYPHEN));
		separateUnitsWithSpaceButton.setSelection(defaultLogicUnitSeparation.equals(Constants.BLANK_SPACE));
		filenameRecommender.setLogicalUnitsSeparation(defaultLogicUnitSeparation);

		filenameRecommender.setReplaceWhiteSpaces(prefs
				.getDefaultBoolean(Preferences.EXPORT_FILENAME_REPLACE_WHITE_SPACE));

		updateEnablement(null, null);
		updateVisibility(null);
		updateExamples();
	}
}
