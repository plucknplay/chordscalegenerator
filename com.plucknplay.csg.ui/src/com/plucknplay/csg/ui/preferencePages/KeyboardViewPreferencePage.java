/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.ColorSelector;
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

import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.util.ToneRange;
import com.plucknplay.csg.core.util.ToneRangeMode;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.util.MyBooleanFieldEditor;
import com.plucknplay.csg.ui.util.RGBUtil;
import com.plucknplay.csg.ui.util.enums.BackgroundColorMode;
import com.plucknplay.csg.ui.util.enums.KeySizeMode;

public class KeyboardViewPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.views.keyboardViewPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "keyboard_view_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private Button highlightRootNoteButton;
	private Button highlightRootNoteWithShapeButton;
	private Button highlightRootNoteWithColorButton;
	private ColorSelector colorSelector;
	private Button frameNotesIntervalsButton;
	private Button whiteNotesIntervalsButton;
	private Button coloredNotesIntervalsButton;
	private Button allNotesOnEmptyFretboardButton;
	private Button allNotesForGriptablesButton;
	private Button allNotesForChordsScalesButton;
	private Button allNotesForBlocksButton;
	private Button additionalChordNotesButton;
	private Button additionalBlockNotesButton;
	private Button additionalNotesInBlackButton;

	private Button relativeNotesModeRadioButton;
	private ToneRangeComposite toneRangeComposite;
	private Button activeInstrumentRadioButton;
	private Button onlyOctaveCheckbox;
	private Button keyboard49RadioButton;
	private Button keyboard61RadioButton;
	private Button keyboard76RadioButton;
	private Button pianoRadioButton;
	private Button imperialGrandPianoRadioButton;
	private Button userDefinedRadioButton;
	private Button overlayButton;
	private Button onlyBlockButton;

	private Button flexibleKeySizeButton;
	private Button smallKeySizeButton;
	private Button mediumKeySizeButton;
	private Button largeKeySizeButton;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);

		// add listener and update enablement
		addListener();
		updateNotesIntervalsEnablement();
		updateAdditionalNotesAndBlocksButtons();
	}

	@Override
	protected void createFieldEditors() {

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(getFieldEditorParent());
		PreferenceLinkUtil.createViewsLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer(), false);
		PreferenceLinkUtil.createChordAndScaleNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createNotesAndIntervalNamesLink(linkComposite,
				(IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer(),
				PreferenceMessages.ViewPreferencePage_see_settings_for_blocks, BlockPreferencePage.ID);

		createToneRangeGroup(getFieldEditorParent());
		createKeySizeGroup(getFieldEditorParent());
		createRootNoteSection(getFieldEditorParent());
		createFingeringNotesIntervalsGroup(getFieldEditorParent());
		createAdditionNotesGroup(getFieldEditorParent());
		createBlockGroup(getFieldEditorParent());

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}

	private void createRootNoteSection(final Composite parent) {

		// highlight root note
		final MyBooleanFieldEditor highlightRootNoteEditor = new MyBooleanFieldEditor(
				Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE,
				PreferenceMessages.ViewPreferencePage_highlight_root_note, parent);
		highlightRootNoteButton = highlightRootNoteEditor.getButton(parent);
		addField(highlightRootNoteEditor);

		// root note composite
		final Composite rootNoteComposite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).indent(HORIZONTAL_INDENT, 0).applyTo(rootNoteComposite);
		GridLayoutFactory.fillDefaults().numColumns(3).extendedMargins(0, 0, 0, 10).applyTo(rootNoteComposite);

		// square shape
		highlightRootNoteWithShapeButton = new Button(rootNoteComposite, SWT.CHECK);
		highlightRootNoteWithShapeButton.setText(PreferenceMessages.ViewPreferencePage_highlight_root_note_with_shape);
		highlightRootNoteWithShapeButton.setSelection(prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE));

		// highlight with color
		highlightRootNoteWithColorButton = new Button(rootNoteComposite, SWT.CHECK);
		highlightRootNoteWithColorButton
				.setText(PreferenceMessages.ViewPreferencePage_highlight_root_note_with_foreground_color);
		highlightRootNoteWithColorButton.setSelection(prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR));

		// color editor
		colorSelector = new ColorSelector(rootNoteComposite);
		colorSelector.setColorValue(RGBUtil.convertStringToRGB(prefs
				.getString(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID)));
	}

	private void createFingeringNotesIntervalsGroup(final Composite parent) {

		// notes/intervals group
		final Group fingeringGroup = new Group(parent, SWT.NONE);
		fingeringGroup.setText(PreferenceMessages.KeyboardViewPreferencePage_notes_intervals_settings);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fingeringGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(fingeringGroup);

		// frame notes/intervals
		frameNotesIntervalsButton = new Button(fingeringGroup, SWT.CHECK);
		frameNotesIntervalsButton.setText(PreferenceMessages.KeyboardViewPreferencePage_frame_notes_intervals);
		frameNotesIntervalsButton.setSelection(prefs.getBoolean(Preferences.KEYBOARD_VIEW_FRAME_NOTES_INTERVALS));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(frameNotesIntervalsButton);

		// background color
		final BackgroundColorMode fingeringBackground = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.KEYBOARD_VIEW_NOTES_INTERVALS_BACKGROUND));
		final Label fingeringBackgroundLabel = new Label(fingeringGroup, SWT.RIGHT);
		fingeringBackgroundLabel.setText(PreferenceMessages.ViewPreferencePage_background_color + ":"); //$NON-NLS-1$
		fingeringBackgroundLabel.setFont(frameNotesIntervalsButton.getFont());
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(fingeringBackgroundLabel);
		final Composite fingeringBackgroundComposite = new Composite(fingeringGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(fingeringBackgroundComposite);
		whiteNotesIntervalsButton = new Button(fingeringBackgroundComposite, SWT.RADIO);
		whiteNotesIntervalsButton.setText(PreferenceMessages.ViewPreferencePage_white);
		whiteNotesIntervalsButton.setSelection(fingeringBackground == BackgroundColorMode.WHITE);
		coloredNotesIntervalsButton = new Button(fingeringBackgroundComposite, SWT.RADIO);
		coloredNotesIntervalsButton.setText(PreferenceMessages.ViewPreferencePage_colored);
		coloredNotesIntervalsButton.setSelection(fingeringBackground == BackgroundColorMode.COLORED);
	}

	private void createAdditionNotesGroup(final Composite parent) {

		// additional notes group
		final Group additionalNotesGroup = new Group(parent, SWT.NONE);
		additionalNotesGroup.setText(PreferenceMessages.ViewPreferencePage_additional_notes_settings);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(additionalNotesGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(additionalNotesGroup);

		// all notes
		new Label(additionalNotesGroup, SWT.LEFT).setText(PreferenceMessages.ViewPreferencePage_show_additional_notes);
		final Composite additionalNotesComposite = new Composite(additionalNotesGroup, SWT.NONE);
		GridDataFactory.fillDefaults().span(1, 2).grab(true, false).applyTo(additionalNotesComposite);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(additionalNotesComposite);

		allNotesOnEmptyFretboardButton = new Button(additionalNotesComposite, SWT.CHECK);
		allNotesOnEmptyFretboardButton
				.setText(PreferenceMessages.KeyboardViewPreferencePage_show_additional_notes_on_empty_keyboard);
		allNotesOnEmptyFretboardButton.setSelection(prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_KEYBOARD));

		allNotesForGriptablesButton = new Button(additionalNotesComposite, SWT.CHECK);
		allNotesForGriptablesButton.setText(PreferenceMessages.ViewPreferencePage_show_additional_notes_for_griptable);
		allNotesForGriptablesButton.setSelection(prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE));

		allNotesForChordsScalesButton = new Button(additionalNotesComposite, SWT.CHECK);
		allNotesForChordsScalesButton
				.setText(PreferenceMessages.ViewPreferencePage_show_additional_notes_for_chords_scales);
		allNotesForChordsScalesButton.setSelection(prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE));

		allNotesForBlocksButton = new Button(additionalNotesComposite, SWT.CHECK);
		allNotesForBlocksButton.setText(PreferenceMessages.ViewPreferencePage_show_additional_notes_for_blocks);
		allNotesForBlocksButton.setSelection(prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK));

		// show additional chord notes
		additionalChordNotesButton = new Button(additionalNotesGroup, SWT.CHECK);
		additionalChordNotesButton.setText(PreferenceMessages.ViewPreferencePage_show_additional_chord_notes);
		additionalChordNotesButton
				.setSelection(prefs.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(additionalChordNotesButton);

		// show additional block notes
		additionalBlockNotesButton = new Button(additionalNotesGroup, SWT.CHECK);
		additionalBlockNotesButton.setText(PreferenceMessages.ViewPreferencePage_show_additional_block_notes);
		additionalBlockNotesButton
				.setSelection(prefs.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(additionalBlockNotesButton);

		// show additional notes in black
		additionalNotesInBlackButton = new Button(additionalNotesGroup, SWT.CHECK);
		additionalNotesInBlackButton.setText(PreferenceMessages.ViewPreferencePage_show_additional_notes_in_black);
		additionalNotesInBlackButton.setSelection(prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(additionalNotesInBlackButton);
	}

	private void createBlockGroup(final Composite parent) {

		// block presentation group
		final Group blockGroup = new Group(parent, SWT.NONE);
		blockGroup.setText(PreferenceMessages.FretboardViewPreferencePage_block_presentation);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(blockGroup);
		GridLayoutFactory.fillDefaults().numColumns(3).margins(5, 5).applyTo(blockGroup);

		final String blockPresentation = prefs.getString(Preferences.KEYBOARD_VIEW_BLOCK_PRESENTATION);
		overlayButton = new Button(blockGroup, SWT.RADIO);
		overlayButton.setText(PreferenceMessages.KeyboardViewPreferencePage_block_overlay);
		overlayButton.setSelection(UIConstants.BLOCK_OVERLAY.equals(blockPresentation));
		onlyBlockButton = new Button(blockGroup, SWT.RADIO);
		onlyBlockButton.setText(PreferenceMessages.ViewPreferencePage_block_no_overlay_frame);
		onlyBlockButton.setSelection(UIConstants.BLOCK_NO_OVERLAY_FRAME.equals(blockPresentation));
	}

	private void createToneRangeGroup(final Composite parent) {

		// tone range
		final ToneRangeMode toneRangeMode = ToneRangeMode.valueOf(prefs
				.getString(Preferences.KEYBOARD_VIEW_TONE_RANGE_MODE));
		final Group toneRangeGroup = new Group(parent, SWT.NONE);
		toneRangeGroup.setText(PreferenceMessages.KeyboardViewPreferencePage_tone_range);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(toneRangeGroup);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(3).spacing(10, 5).margins(5, 5)
				.applyTo(toneRangeGroup);

		relativeNotesModeRadioButton = new Button(toneRangeGroup, SWT.RADIO);
		relativeNotesModeRadioButton.setText(PreferenceMessages.KeyboardViewPreferencePage_relative_notes_mode);
		relativeNotesModeRadioButton.setSelection(toneRangeMode == ToneRangeMode.RELATIVE_NOTES_MODE);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(relativeNotesModeRadioButton);

		activeInstrumentRadioButton = new Button(toneRangeGroup, SWT.RADIO);
		activeInstrumentRadioButton.setText(PreferenceMessages.ToneRangeSection_active_instruments_white_keys);
		activeInstrumentRadioButton.setSelection(toneRangeMode == ToneRangeMode.ACTIVE_INSTRUMENT
				|| toneRangeMode == ToneRangeMode.ACTIVE_INSTRUMENT_WHITE_KEYS
				|| toneRangeMode == ToneRangeMode.ACTIVE_INSTRUMENT_FULL_OCTAVES);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(activeInstrumentRadioButton);

		onlyOctaveCheckbox = new Button(toneRangeGroup, SWT.CHECK);
		onlyOctaveCheckbox.setText(PreferenceMessages.ToneRangeSection_only_full_octaves);
		onlyOctaveCheckbox.setSelection(toneRangeMode == ToneRangeMode.ACTIVE_INSTRUMENT_FULL_OCTAVES);
		GridDataFactory.fillDefaults().indent(30, 0).span(3, 1).applyTo(onlyOctaveCheckbox);

		keyboard49RadioButton = new Button(toneRangeGroup, SWT.RADIO);
		keyboard49RadioButton.setText(PreferenceMessages.ToneRangeSection_keyboard_49);
		keyboard49RadioButton.setSelection(toneRangeMode == ToneRangeMode.KEYBOARD_49);
		GridDataFactory.fillDefaults().indent(0, 5).applyTo(keyboard49RadioButton);

		keyboard61RadioButton = new Button(toneRangeGroup, SWT.RADIO);
		keyboard61RadioButton.setText(PreferenceMessages.ToneRangeSection_keyboard_61);
		keyboard61RadioButton.setSelection(toneRangeMode == ToneRangeMode.KEYBOARD_61);

		keyboard76RadioButton = new Button(toneRangeGroup, SWT.RADIO);
		keyboard76RadioButton.setText(PreferenceMessages.ToneRangeSection_keyboard_76);
		keyboard76RadioButton.setSelection(toneRangeMode == ToneRangeMode.KEYBOARD_76);

		pianoRadioButton = new Button(toneRangeGroup, SWT.RADIO);
		pianoRadioButton.setText(PreferenceMessages.ToneRangeSection_piano_88);
		pianoRadioButton.setSelection(toneRangeMode == ToneRangeMode.PIANO_88);

		imperialGrandPianoRadioButton = new Button(toneRangeGroup, SWT.RADIO);
		imperialGrandPianoRadioButton.setText(PreferenceMessages.ToneRangeSection_imperial_grand_piano_97);
		imperialGrandPianoRadioButton.setSelection(toneRangeMode == ToneRangeMode.IMPERIAL_GRAND_97);

		userDefinedRadioButton = new Button(toneRangeGroup, SWT.RADIO);
		userDefinedRadioButton.setText(PreferenceMessages.ToneRangeSection_user_defined);
		userDefinedRadioButton.setSelection(toneRangeMode == ToneRangeMode.USER_DEFINED);

		toneRangeComposite = new ToneRangeComposite(toneRangeGroup, SWT.NONE, true);
		GridDataFactory.fillDefaults().grab(true, false).indent(30, 5).span(3, 1).applyTo(toneRangeComposite);

		final SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateToneRangeCombos();
			}
		};
		relativeNotesModeRadioButton.addSelectionListener(listener);
		onlyOctaveCheckbox.addSelectionListener(listener);
		keyboard49RadioButton.addSelectionListener(listener);
		keyboard61RadioButton.addSelectionListener(listener);
		keyboard76RadioButton.addSelectionListener(listener);
		pianoRadioButton.addSelectionListener(listener);
		imperialGrandPianoRadioButton.addSelectionListener(listener);
		userDefinedRadioButton.addSelectionListener(listener);

		activeInstrumentRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateFullOctaveButtonEnablement();
				updateToneRangeCombos();
			}
		});

		updateFullOctaveButtonEnablement();
		updateToneRangeCombos();
	}

	private void createKeySizeGroup(final Composite parent) {

		// key size group
		final Group keySizeGroup = new Group(parent, SWT.NONE);
		keySizeGroup.setText(PreferenceMessages.KeyboardViewPreferencePage_key_size);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(keySizeGroup);
		GridLayoutFactory.fillDefaults().numColumns(4).margins(5, 5).applyTo(keySizeGroup);

		// key size
		final KeySizeMode keySizeMode = KeySizeMode.valueOf(prefs.getString(Preferences.KEYBOARD_VIEW_KEY_SIZE));
		flexibleKeySizeButton = new Button(keySizeGroup, SWT.RADIO);
		flexibleKeySizeButton.setText(PreferenceMessages.KeyboardViewPreferencePage_flexible);
		flexibleKeySizeButton.setSelection(keySizeMode == KeySizeMode.FLEXIBLE);
		smallKeySizeButton = new Button(keySizeGroup, SWT.RADIO);
		smallKeySizeButton.setText(PreferenceMessages.KeyboardViewPreferencePage_small);
		smallKeySizeButton.setSelection(keySizeMode == KeySizeMode.SMALL);
		mediumKeySizeButton = new Button(keySizeGroup, SWT.RADIO);
		mediumKeySizeButton.setText(PreferenceMessages.KeyboardViewPreferencePage_medium);
		mediumKeySizeButton.setSelection(keySizeMode == KeySizeMode.MEDIUM);
		largeKeySizeButton = new Button(keySizeGroup, SWT.RADIO);
		largeKeySizeButton.setText(PreferenceMessages.KeyboardViewPreferencePage_large);
		largeKeySizeButton.setSelection(keySizeMode == KeySizeMode.LARGE);
	}

	/* --- enablement and selection handling --- */

	private void addListener() {

		final SelectionAdapter updateNotesIntervalsListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateNotesIntervalsEnablement();
			}
		};
		highlightRootNoteButton.addSelectionListener(updateNotesIntervalsListener);
		frameNotesIntervalsButton.addSelectionListener(updateNotesIntervalsListener);

		final SelectionAdapter highlightListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (!highlightRootNoteWithColorButton.getSelection()
						&& !highlightRootNoteWithShapeButton.getSelection()) {
					if (e.widget == highlightRootNoteWithColorButton) {
						highlightRootNoteWithShapeButton.setSelection(true);
					} else {
						highlightRootNoteWithColorButton.setSelection(true);
					}
				}
				updateNotesIntervalsEnablement();
			}
		};
		highlightRootNoteWithColorButton.addSelectionListener(highlightListener);
		highlightRootNoteWithShapeButton.addSelectionListener(highlightListener);

		// block listener
		final SelectionAdapter additionalNotesListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateAdditionalNotesAndBlocksButtons();
			}
		};
		relativeNotesModeRadioButton.addSelectionListener(additionalNotesListener);
		overlayButton.addSelectionListener(additionalNotesListener);
		onlyBlockButton.addSelectionListener(additionalNotesListener);
		allNotesForGriptablesButton.addSelectionListener(additionalNotesListener);
		allNotesForBlocksButton.addSelectionListener(additionalNotesListener);
	}

	private void updateNotesIntervalsEnablement() {

		final boolean highlightRootNote = highlightRootNoteButton.getSelection();
		final boolean highlightRootNoteWithShape = highlightRootNote && highlightRootNoteWithShapeButton.getSelection();
		final boolean highlightRootNoteWithColor = highlightRootNote && highlightRootNoteWithColorButton.getSelection();

		highlightRootNoteWithShapeButton.setEnabled(highlightRootNote);
		highlightRootNoteWithColorButton.setEnabled(highlightRootNote);
		colorSelector.setEnabled(highlightRootNoteWithColor);

		frameNotesIntervalsButton.setEnabled(!highlightRootNoteWithShape);
		if (highlightRootNoteWithShape) {
			frameNotesIntervalsButton.setSelection(true);
		}
		if (highlightRootNoteWithColor) {
			coloredNotesIntervalsButton.setSelection(false);
			whiteNotesIntervalsButton.setSelection(true);
		}

		final boolean frameNotesIntervals = frameNotesIntervalsButton.getSelection();
		whiteNotesIntervalsButton.setEnabled(frameNotesIntervals && !highlightRootNoteWithColor);
		coloredNotesIntervalsButton.setEnabled(frameNotesIntervals && !highlightRootNoteWithColor);
	}

	private void updateAdditionalNotesAndBlocksButtons() {

		final boolean relativeNotes = relativeNotesModeRadioButton.getSelection();

		// block presentation
		overlayButton.setEnabled(!relativeNotes);
		onlyBlockButton.setEnabled(!relativeNotes);
		if (relativeNotes) {
			overlayButton.setSelection(false);
			onlyBlockButton.setSelection(true);
		}

		// additional notes for griptable
		final boolean allNotesForGriptables = allNotesForGriptablesButton.getSelection();
		if (relativeNotes) {
			additionalChordNotesButton.setSelection(false);
		}
		if (allNotesForGriptables) {
			additionalChordNotesButton.setSelection(true);
		}
		additionalChordNotesButton.setEnabled(!allNotesForGriptables && !relativeNotes);

		// additional notes for block
		final boolean allNotesForBlocks = allNotesForBlocksButton.getSelection();
		final boolean onlyBlock = onlyBlockButton.getSelection();
		if (relativeNotes) {
			additionalBlockNotesButton.setSelection(false);
		}
		if (!onlyBlock || allNotesForBlocks) {
			additionalBlockNotesButton.setSelection(true);
		}
		additionalBlockNotesButton.setEnabled(onlyBlock && !allNotesForBlocks && !relativeNotes);
	}

	private void updateToneRangeCombos() {

		// update enablement
		toneRangeComposite.setEnabled(userDefinedRadioButton.getSelection());

		if (userDefinedRadioButton.getSelection()) {
			if (toneRangeComposite.isEmptySelection()) {
				final Note startNote = Factory.getInstance().getNoteByIndex(
						prefs.getInt(Preferences.KEYBOARD_VIEW_TONE_RANGE_START_TONE));
				toneRangeComposite.setStartTone(startNote);
				final Note endNote = Factory.getInstance().getNoteByIndex(
						prefs.getInt(Preferences.KEYBOARD_VIEW_TONE_RANGE_END_TONE));
				toneRangeComposite.setEndTone(endNote);
			}
			return;
		}

		// update setting
		ToneRangeMode toneRangeMode = relativeNotesModeRadioButton.getSelection() ? ToneRangeMode.RELATIVE_NOTES_MODE
				: activeInstrumentRadioButton.getSelection() ? ToneRangeMode.ACTIVE_INSTRUMENT_WHITE_KEYS
						: keyboard49RadioButton.getSelection() ? ToneRangeMode.KEYBOARD_49 : keyboard61RadioButton
								.getSelection() ? ToneRangeMode.KEYBOARD_61
								: keyboard76RadioButton.getSelection() ? ToneRangeMode.KEYBOARD_76 : pianoRadioButton
										.getSelection() ? ToneRangeMode.PIANO_88 : imperialGrandPianoRadioButton
										.getSelection() ? ToneRangeMode.IMPERIAL_GRAND_97 : null;

		if (toneRangeMode == null) {
			activeInstrumentRadioButton.setSelection(true);
			onlyOctaveCheckbox.setSelection(true);
		}

		if (activeInstrumentRadioButton.getSelection() && onlyOctaveCheckbox.getSelection()) {
			toneRangeMode = ToneRangeMode.ACTIVE_INSTRUMENT_FULL_OCTAVES;
		}

		final ToneRange toneRange = toneRangeMode.getToneRange();
		toneRangeComposite.setStartTone(toneRange.getStartTone());
		toneRangeComposite.setEndTone(toneRange.getEndTone());
		toneRangeComposite.setAbsoluteToneCombosVisible(!relativeNotesModeRadioButton.getSelection());
	}

	private void updateFullOctaveButtonEnablement() {
		onlyOctaveCheckbox.setEnabled(activeInstrumentRadioButton.getSelection());
	}

	/* --- perform buttons handling --- */

	@Override
	public boolean performOk() {

		// highlight root note
		prefs.setValue(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR,
				highlightRootNoteWithColorButton.getSelection());
		prefs.setValue(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE,
				highlightRootNoteWithShapeButton.getSelection());
		prefs.setValue(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID,
				RGBUtil.convertRGBToId(colorSelector.getColorValue()));

		// fingering frame/background
		prefs.setValue(Preferences.KEYBOARD_VIEW_FRAME_NOTES_INTERVALS, frameNotesIntervalsButton.getSelection());
		prefs.setValue(Preferences.KEYBOARD_VIEW_NOTES_INTERVALS_BACKGROUND,
				whiteNotesIntervalsButton.getSelection() ? BackgroundColorMode.WHITE.toString()
						: BackgroundColorMode.COLORED.toString());

		// additional notes
		prefs.setValue(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_KEYBOARD,
				allNotesOnEmptyFretboardButton.getSelection());
		prefs.setValue(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE,
				allNotesForGriptablesButton.getSelection());
		prefs.setValue(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE,
				allNotesForChordsScalesButton.getSelection());
		prefs.setValue(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK,
				allNotesForBlocksButton.getSelection());
		prefs.setValue(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES, additionalChordNotesButton.getSelection());
		prefs.setValue(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES, additionalBlockNotesButton.getSelection());
		prefs.setValue(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK,
				additionalNotesInBlackButton.getSelection());

		// block presentation
		prefs.setValue(Preferences.KEYBOARD_VIEW_BLOCK_PRESENTATION,
				overlayButton.getSelection() ? UIConstants.BLOCK_OVERLAY : UIConstants.BLOCK_NO_OVERLAY_FRAME);

		// tone range
		ToneRangeMode toneRangeMode = relativeNotesModeRadioButton.getSelection() ? ToneRangeMode.RELATIVE_NOTES_MODE
				: activeInstrumentRadioButton.getSelection() ? ToneRangeMode.ACTIVE_INSTRUMENT_WHITE_KEYS
						: keyboard49RadioButton.getSelection() ? ToneRangeMode.KEYBOARD_49 : keyboard61RadioButton
								.getSelection() ? ToneRangeMode.KEYBOARD_61
								: keyboard76RadioButton.getSelection() ? ToneRangeMode.KEYBOARD_76 : pianoRadioButton
										.getSelection() ? ToneRangeMode.PIANO_88 : imperialGrandPianoRadioButton
										.getSelection() ? ToneRangeMode.IMPERIAL_GRAND_97 : ToneRangeMode.USER_DEFINED;
		if (activeInstrumentRadioButton.getSelection() && onlyOctaveCheckbox.getSelection()) {
			toneRangeMode = ToneRangeMode.ACTIVE_INSTRUMENT_FULL_OCTAVES;
		}
		prefs.setValue(Preferences.KEYBOARD_VIEW_TONE_RANGE_MODE, toneRangeMode.toString());

		final Note startNote = toneRangeComposite.getStartTone();
		prefs.setValue(Preferences.KEYBOARD_VIEW_TONE_RANGE_START_TONE,
				userDefinedRadioButton.getSelection() ? startNote.getLevel() * 12 + startNote.getValue() : -1);

		final Note endNote = toneRangeComposite.getEndTone();
		prefs.setValue(Preferences.KEYBOARD_VIEW_TONE_RANGE_END_TONE,
				userDefinedRadioButton.getSelection() ? endNote.getLevel() * 12 + endNote.getValue() : -1);

		// key size
		prefs.setValue(
				Preferences.KEYBOARD_VIEW_KEY_SIZE,
				smallKeySizeButton.getSelection() ? KeySizeMode.SMALL.toString()
						: mediumKeySizeButton.getSelection() ? KeySizeMode.MEDIUM.toString() : largeKeySizeButton
								.getSelection() ? KeySizeMode.LARGE.toString() : KeySizeMode.FLEXIBLE.toString());

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		// highlight root note
		highlightRootNoteWithColorButton.setSelection(prefs
				.getDefaultBoolean(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR));
		highlightRootNoteWithShapeButton.setSelection(prefs
				.getDefaultBoolean(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE));
		colorSelector.setColorValue(RGBUtil.convertStringToRGB(prefs
				.getDefaultString(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID)));

		// fingering frame/background
		frameNotesIntervalsButton
				.setSelection(prefs.getDefaultBoolean(Preferences.KEYBOARD_VIEW_FRAME_NOTES_INTERVALS));
		final BackgroundColorMode defaultBackground = BackgroundColorMode.valueOf(prefs
				.getDefaultString(Preferences.KEYBOARD_VIEW_NOTES_INTERVALS_BACKGROUND));
		whiteNotesIntervalsButton.setSelection(defaultBackground == BackgroundColorMode.WHITE);
		coloredNotesIntervalsButton.setSelection(defaultBackground == BackgroundColorMode.COLORED);

		// additional notes
		allNotesOnEmptyFretboardButton.setSelection(prefs
				.getDefaultBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_KEYBOARD));
		allNotesForGriptablesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE));
		allNotesForChordsScalesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE));
		allNotesForBlocksButton.setSelection(prefs
				.getDefaultBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK));
		additionalChordNotesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES));
		additionalBlockNotesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES));
		additionalNotesInBlackButton.setSelection(prefs
				.getDefaultBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK));

		// block presentation
		final String blockDefaultPresentation = prefs.getDefaultString(Preferences.KEYBOARD_VIEW_BLOCK_PRESENTATION);
		overlayButton.setSelection(UIConstants.BLOCK_OVERLAY.equals(blockDefaultPresentation));
		onlyBlockButton.setSelection(UIConstants.BLOCK_NO_OVERLAY_FRAME.equals(blockDefaultPresentation));

		// tone range
		final ToneRangeMode defaultToneRangeMode = ToneRangeMode.valueOf(prefs
				.getDefaultString(Preferences.KEYBOARD_VIEW_TONE_RANGE_MODE));

		relativeNotesModeRadioButton.setSelection(defaultToneRangeMode == ToneRangeMode.RELATIVE_NOTES_MODE);
		activeInstrumentRadioButton.setSelection(defaultToneRangeMode == ToneRangeMode.ACTIVE_INSTRUMENT_WHITE_KEYS
				|| defaultToneRangeMode == ToneRangeMode.ACTIVE_INSTRUMENT_FULL_OCTAVES);
		keyboard49RadioButton.setSelection(defaultToneRangeMode == ToneRangeMode.KEYBOARD_49);
		keyboard61RadioButton.setSelection(defaultToneRangeMode == ToneRangeMode.KEYBOARD_61);
		keyboard76RadioButton.setSelection(defaultToneRangeMode == ToneRangeMode.KEYBOARD_76);
		pianoRadioButton.setSelection(defaultToneRangeMode == ToneRangeMode.PIANO_88);
		imperialGrandPianoRadioButton.setSelection(defaultToneRangeMode == ToneRangeMode.IMPERIAL_GRAND_97);
		userDefinedRadioButton.setSelection(defaultToneRangeMode == ToneRangeMode.USER_DEFINED);
		onlyOctaveCheckbox.setSelection(defaultToneRangeMode == ToneRangeMode.ACTIVE_INSTRUMENT_FULL_OCTAVES);
		if (userDefinedRadioButton.getSelection()) {
			final Note startNote = Factory.getInstance().getNoteByIndex(
					prefs.getDefaultInt(Preferences.KEYBOARD_VIEW_TONE_RANGE_START_TONE));
			toneRangeComposite.setStartTone(startNote);
			final Note endNote = Factory.getInstance().getNoteByIndex(
					prefs.getDefaultInt(Preferences.KEYBOARD_VIEW_TONE_RANGE_END_TONE));
			toneRangeComposite.setEndTone(endNote);
		}

		// key size
		final KeySizeMode defaultKeySize = KeySizeMode.valueOf(prefs
				.getDefaultString(Preferences.KEYBOARD_VIEW_KEY_SIZE));
		flexibleKeySizeButton.setSelection(defaultKeySize == KeySizeMode.FLEXIBLE);
		smallKeySizeButton.setSelection(defaultKeySize == KeySizeMode.SMALL);
		mediumKeySizeButton.setSelection(defaultKeySize == KeySizeMode.MEDIUM);
		largeKeySizeButton.setSelection(defaultKeySize == KeySizeMode.LARGE);

		// update buttons
		updateNotesIntervalsEnablement();
		updateAdditionalNotesAndBlocksButtons();
		updateFullOctaveButtonEnablement();
		updateToneRangeCombos();
	}
}
