/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.sound.SoundConstants;
import com.plucknplay.csg.sound.SoundMachine;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.util.OnlyNumbersKeyListener;

public class SoundPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.soundPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "sound_preference_page_context"; //$NON-NLS-1$

	private static final Object TEST_GROUP = new Object();
	private static final Object CHORD_GROUP = new Object();
	private static final Object BLOCK_GROUP = new Object();

	private IPreferenceStore prefs;

	private List<Integer> midiNumbersList;
	private List<Integer> keyboardMidiNumbersList;
	private List<Note> testNotes;

	private Combo instrumentCombo;
	private Combo keyboardCombo;
	private Text testToneLengthText;
	private Text chordToneLengthText;
	private Text blockToneLengthText;
	private Text testToneDistanceText;
	private Text chordToneDistanceText;
	private Text blockToneDistanceText;
	private Button testAscendingButton;
	private Button chordAscendingButton;
	private Button blockAscendingButton;
	private Button blockDescendingButton;
	private Button testDescendingButton;
	private Button chordDescendingButton;
	private Button testAscendingDescendingButton;
	private Button chordAscendingDescendingButton;
	private Button blockAscendingDescendingButton;
	private Button testDescendingAscendingButton;
	private Button chordDescendingAscendingButton;
	private Button blockDescendingAscendingButton;
	private Text fretboardViewToneLengthText;
	private Text keyboardViewToneLengthText;
	private Scale volumeScale;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(final Composite parent) {

		// main composite
		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(3).applyTo(mainComposite);

		// default MIDI istrument
		final Label instrumentLabel = new Label(mainComposite, SWT.LEFT);
		instrumentLabel.setText(PreferenceMessages.SoundPreferencePage_default_instrument_sound);

		instrumentCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		instrumentCombo.setItems(SoundConstants.MIDI_INSTRUMENTS);
		final int midiInstrumentNumber = prefs.getInt(Preferences.SOUND_DEFAULT_MIDI_INSTRUMENT);
		int index = getMidiNumbersList().indexOf(midiInstrumentNumber);
		instrumentCombo.select(index);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(instrumentCombo);

		final Button testInstrumentButton = new Button(mainComposite, SWT.PUSH);
		testInstrumentButton.setText(PreferenceMessages.SoundPreferencePage_test);
		testInstrumentButton.setSize(IDialogConstants.BUTTON_WIDTH, -1);
		testInstrumentButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int midiInstrumentNumber = SoundConstants.MIDI_INSTRUMENT_NUMBERS[instrumentCombo
						.getSelectionIndex()];
				final int toneLength = Integer.parseInt(testToneLengthText.getText());
				final int toneDistance = Integer.parseInt(testToneDistanceText.getText());
				Activator
						.getDefault()
						.getSoundMachine()
						.play(getTestNotes(), null, null, midiInstrumentNumber, toneLength, toneDistance,
								getTestPlayPattern(), false);
			}
		});
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		Point minSize = testInstrumentButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		widthHint = Math.max(widthHint, minSize.x);
		GridDataFactory.fillDefaults().hint(widthHint, -1).applyTo(testInstrumentButton);

		// keyboard sound
		final Label keyboardLabel = new Label(mainComposite, SWT.LEFT);
		keyboardLabel.setText(PreferenceMessages.SoundPreferencePage_keyboard_sound);

		keyboardCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		keyboardCombo.setItems(SoundConstants.MIDI_KEYBOARD_INSTRUMENTS);
		final int midiKeyboardInstrumentNumber = prefs.getInt(Preferences.SOUND_KEYBOARD_INSTRUMENT);
		index = getKeyboardMidiNumbersList().indexOf(midiKeyboardInstrumentNumber);
		keyboardCombo.select(index);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(keyboardCombo);

		final Button testKeyboardButton = new Button(mainComposite, SWT.PUSH);
		testKeyboardButton.setText(PreferenceMessages.SoundPreferencePage_test);
		testKeyboardButton.setSize(IDialogConstants.BUTTON_WIDTH, -1);
		testKeyboardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int midiInstrumentNumber = SoundConstants.MIDI_KEYBOARD_INSTRUMENT_NUMBERS[keyboardCombo
						.getSelectionIndex()];
				final int toneLength = Integer.parseInt(testToneLengthText.getText());
				final int toneDistance = Integer.parseInt(testToneDistanceText.getText());
				Activator
						.getDefault()
						.getSoundMachine()
						.play(getTestNotes(), null, null, midiInstrumentNumber, toneLength, toneDistance,
								getTestPlayPattern(), false);
			}
		});
		widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		minSize = testKeyboardButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		widthHint = Math.max(widthHint, minSize.x);
		GridDataFactory.fillDefaults().hint(widthHint, -1).applyTo(testKeyboardButton);

		// play group
		final Group playGroup = new Group(mainComposite, SWT.NONE);
		playGroup.setText(PreferenceMessages.SoundPreferencePage_play_modes);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(5).margins(5, 5).applyTo(playGroup);
		GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(playGroup);

		new Label(playGroup, SWT.NONE);

		final Label testLabel = new Label(playGroup, SWT.CENTER);
		testLabel.setText(PreferenceMessages.SoundPreferencePage_test);

		final Label chordLabel = new Label(playGroup, SWT.CENTER);
		chordLabel.setText(PreferenceMessages.SoundPreferencePage_chord);

		final Label blockLabel = new Label(playGroup, SWT.CENTER);
		blockLabel.setText(PreferenceMessages.SoundPreferencePage_block);

		new Label(playGroup, SWT.NONE);

		final Label toneLengthLabel = new Label(playGroup, SWT.LEFT);
		toneLengthLabel.setText(PreferenceMessages.SoundPreferencePage_tone_length);

		testToneLengthText = createText(playGroup, Preferences.SOUND_TEST_TONE_LENGTH, 200, 8000, null);
		chordToneLengthText = createText(playGroup, Preferences.SOUND_CHORD_TONE_LENGTH, 200, 8000, null);
		blockToneLengthText = createText(playGroup, Preferences.SOUND_BLOCK_TONE_LENGTH, 200, 8000, null);

		final Label toneLengthCommentLabel = new Label(playGroup, SWT.LEFT);
		toneLengthCommentLabel.setText("(200..8000)"); //$NON-NLS-1$

		final Label toneDistanceLabel = new Label(playGroup, SWT.LEFT);
		toneDistanceLabel.setText(PreferenceMessages.SoundPreferencePage_tone_distance);

		testToneDistanceText = createText(playGroup, Preferences.SOUND_TEST_TONE_DISTANCE, 0, 8000, TEST_GROUP);
		chordToneDistanceText = createText(playGroup, Preferences.SOUND_CHORD_TONE_DISTANCE, 0, 8000, CHORD_GROUP);
		blockToneDistanceText = createText(playGroup, Preferences.SOUND_BLOCK_TONE_DISTANCE, 0, 8000, BLOCK_GROUP);

		final Label toneDistanceCommentLabel = new Label(playGroup, SWT.LEFT);
		toneDistanceCommentLabel.setText("(0..8000)"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().span(1, 5).applyTo(toneDistanceCommentLabel);

		final Label ascendingLabel = new Label(playGroup, SWT.LEFT);
		ascendingLabel.setText(PreferenceMessages.SoundPreferencePage_ascending);

		testAscendingButton = createButton(playGroup, TEST_GROUP);
		chordAscendingButton = createButton(playGroup, CHORD_GROUP);
		blockAscendingButton = createButton(playGroup, BLOCK_GROUP);

		final Label descendingLabel = new Label(playGroup, SWT.LEFT);
		descendingLabel.setText(PreferenceMessages.SoundPreferencePage_descending);

		testDescendingButton = createButton(playGroup, TEST_GROUP);
		chordDescendingButton = createButton(playGroup, CHORD_GROUP);
		blockDescendingButton = createButton(playGroup, BLOCK_GROUP);

		final Label ascendingDescendingLabel = new Label(playGroup, SWT.LEFT);
		ascendingDescendingLabel.setText(PreferenceMessages.SoundPreferencePage_ascending_descending);

		testAscendingDescendingButton = createButton(playGroup, TEST_GROUP);
		chordAscendingDescendingButton = createButton(playGroup, CHORD_GROUP);
		blockAscendingDescendingButton = createButton(playGroup, BLOCK_GROUP);

		final Label descendingAscendingLabel = new Label(playGroup, SWT.LEFT);
		descendingAscendingLabel.setText(PreferenceMessages.SoundPreferencePage_descending_ascending);

		testDescendingAscendingButton = createButton(playGroup, TEST_GROUP);
		chordDescendingAscendingButton = createButton(playGroup, CHORD_GROUP);
		blockDescendingAscendingButton = createButton(playGroup, BLOCK_GROUP);

		final String testPlayPattern = prefs.getString(Preferences.SOUND_TEST_PLAY_PATTERN);
		final String chordPlayPattern = prefs.getString(Preferences.SOUND_CHORD_PLAY_PATTERN);
		final String blockPlayPattern = prefs.getString(Preferences.SOUND_BLOCK_PLAY_PATTERN);
		initButtons(testPlayPattern, chordPlayPattern, blockPlayPattern);

		// freestyle group
		final Group freestyleGroup = new Group(mainComposite, SWT.NONE);
		freestyleGroup.setText(PreferenceMessages.SoundPreferencePage_freestyle_tone_length);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(3).margins(5, 5).applyTo(freestyleGroup);
		GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(freestyleGroup);

		final Label fretboardViewLabel = new Label(freestyleGroup, SWT.LEFT);
		fretboardViewLabel.setText(PreferenceMessages.SoundPreferencePage_fretboard_view);

		fretboardViewToneLengthText = createText(freestyleGroup, Preferences.SOUND_FRETBOARD_VIEW_TONE_LENGTH, 200,
				8000, null);

		final Label fretboardToneLengthCommentLabel = new Label(freestyleGroup, SWT.LEFT);
		fretboardToneLengthCommentLabel.setText("(200..8000)"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().span(1, 2).applyTo(fretboardToneLengthCommentLabel);

		final Label keyboardViewLabel = new Label(freestyleGroup, SWT.LEFT);
		keyboardViewLabel.setText(PreferenceMessages.SoundPreferencePage_keyboard_view);

		keyboardViewToneLengthText = createText(freestyleGroup, Preferences.SOUND_KEYBOARD_VIEW_TONE_LENGTH, 200, 8000,
				null);

		// volume group
		final Group volumeGroup = new Group(mainComposite, SWT.NONE);
		volumeGroup.setText(PreferenceMessages.SoundPreferencePage_volume);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(3).margins(5, 5).applyTo(volumeGroup);
		GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(volumeGroup);

		final Label softLabel = new Label(volumeGroup, SWT.LEFT);
		softLabel.setText(PreferenceMessages.SoundPreferencePage_soft);

		volumeScale = new Scale(volumeGroup, SWT.HORIZONTAL);
		volumeScale.setMinimum(0);
		volumeScale.setMaximum(100);
		volumeScale.setPageIncrement(10);
		volumeScale.setSelection(prefs.getInt(Preferences.SOUND_VOLUME));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(volumeScale);

		final Label loudLabel = new Label(volumeGroup, SWT.LEFT);
		loudLabel.setText(PreferenceMessages.SoundPreferencePage_loud);

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);

		return mainComposite;
	}

	private Text createText(final Composite parent, final String preferencesID, final int minValue, final int maxValue,
			final Object updateGroup) {
		final Text resultText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		resultText.setText("" + prefs.getInt(preferencesID)); //$NON-NLS-1$
		resultText.addKeyListener(new OnlyNumbersKeyListener());
		resultText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				if ("".equals(resultText.getText())) {
					resultText.setText("0"); //$NON-NLS-1$
				}
				int currentValue = Integer.parseInt(resultText.getText());
				if (currentValue < minValue) {
					currentValue = minValue;
				}
				if (currentValue > maxValue) {
					currentValue = maxValue;
				}
				resultText.setText("" + currentValue); //$NON-NLS-1$
				if (updateGroup != null) {
					enableGroup(updateGroup, currentValue != 0);
				}
			}
		});
		resultText.setTextLimit(4);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(resultText);
		return resultText;
	}

	private void initButtons(final String testPlayPattern, final String chordPlayPattern, final String blockPlayPattern) {
		testAscendingButton.setSelection(testPlayPattern.equals(SoundConstants.ASCENDING_PATTERN));
		testDescendingButton.setSelection(testPlayPattern.equals(SoundConstants.DESCENDING_PATTERN));
		testAscendingDescendingButton.setSelection(testPlayPattern.equals(SoundConstants.ASCENDING_DESCENDING_PATTERN));
		testDescendingAscendingButton.setSelection(testPlayPattern.equals(SoundConstants.DESCENDING_ASCENDING_PATTERN));

		chordAscendingButton.setSelection(chordPlayPattern.equals(SoundConstants.ASCENDING_PATTERN));
		chordDescendingButton.setSelection(chordPlayPattern.equals(SoundConstants.DESCENDING_PATTERN));
		chordAscendingDescendingButton.setSelection(chordPlayPattern
				.equals(SoundConstants.ASCENDING_DESCENDING_PATTERN));
		chordDescendingAscendingButton.setSelection(chordPlayPattern
				.equals(SoundConstants.DESCENDING_ASCENDING_PATTERN));

		blockAscendingButton.setSelection(blockPlayPattern.equals(SoundConstants.ASCENDING_PATTERN));
		blockDescendingButton.setSelection(blockPlayPattern.equals(SoundConstants.DESCENDING_PATTERN));
		blockAscendingDescendingButton.setSelection(blockPlayPattern
				.equals(SoundConstants.ASCENDING_DESCENDING_PATTERN));
		blockDescendingAscendingButton.setSelection(blockPlayPattern
				.equals(SoundConstants.DESCENDING_ASCENDING_PATTERN));

		enableGroup(TEST_GROUP, Integer.parseInt(testToneDistanceText.getText()) != 0);
		enableGroup(CHORD_GROUP, Integer.parseInt(chordToneDistanceText.getText()) != 0);
		enableGroup(BLOCK_GROUP, Integer.parseInt(blockToneDistanceText.getText()) != 0);
	}

	private Button createButton(final Group playGroup, final Object group) {
		final Button resultButton = new Button(playGroup, SWT.RADIO);
		resultButton.setData(group);
		resultButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (e.widget instanceof Button) {
					final Button button = (Button) e.widget;
					if (button.getData() == TEST_GROUP) {
						updateTestButtons(button);
					} else if (button.getData() == CHORD_GROUP) {
						updateChordButtons(button);
					} else if (button.getData() == BLOCK_GROUP) {
						updateBlockButtons(button);
					}
				}
			}
		});
		return resultButton;
	}

	private void updateTestButtons(final Button button) {
		testAscendingButton
				.setSelection(!(button == testDescendingButton || button == testAscendingDescendingButton || button == testDescendingAscendingButton));
		testDescendingButton
				.setSelection(!(button == testAscendingButton || button == testAscendingDescendingButton || button == testDescendingAscendingButton));
		testAscendingDescendingButton
				.setSelection(!(button == testAscendingButton || button == testDescendingButton || button == testDescendingAscendingButton));
		testDescendingAscendingButton
				.setSelection(!(button == testAscendingButton || button == testDescendingButton || button == testAscendingDescendingButton));
	}

	private void updateChordButtons(final Button button) {
		chordAscendingButton
				.setSelection(!(button == chordDescendingButton || button == chordAscendingDescendingButton || button == chordDescendingAscendingButton));
		chordDescendingButton
				.setSelection(!(button == chordAscendingButton || button == chordAscendingDescendingButton || button == chordDescendingAscendingButton));
		chordAscendingDescendingButton
				.setSelection(!(button == chordAscendingButton || button == chordDescendingButton || button == chordDescendingAscendingButton));
		chordDescendingAscendingButton
				.setSelection(!(button == chordAscendingButton || button == chordDescendingButton || button == chordAscendingDescendingButton));
	}

	private void updateBlockButtons(final Button button) {
		blockAscendingButton
				.setSelection(!(button == blockDescendingButton || button == blockAscendingDescendingButton || button == blockDescendingAscendingButton));
		blockDescendingButton
				.setSelection(!(button == blockAscendingButton || button == blockAscendingDescendingButton || button == blockDescendingAscendingButton));
		blockAscendingDescendingButton
				.setSelection(!(button == blockAscendingButton || button == blockDescendingButton || button == blockDescendingAscendingButton));
		blockDescendingAscendingButton
				.setSelection(!(button == blockAscendingButton || button == blockDescendingButton || button == blockAscendingDescendingButton));
	}

	private void enableGroup(final Object updateGroup, final boolean enabled) {

		// enable/disable test radio buttons
		if (updateGroup == TEST_GROUP) {
			testAscendingButton.setEnabled(enabled);
			testDescendingButton.setEnabled(enabled);
			testAscendingDescendingButton.setEnabled(enabled);
			testDescendingAscendingButton.setEnabled(enabled);
			if (!enabled) {
				testAscendingButton.setSelection(true);
				testDescendingButton.setSelection(false);
				testAscendingDescendingButton.setSelection(false);
				testDescendingAscendingButton.setSelection(false);
			}
		}

		// enable/disable chord radio buttons
		else if (updateGroup == CHORD_GROUP) {
			chordAscendingButton.setEnabled(enabled);
			chordDescendingButton.setEnabled(enabled);
			chordAscendingDescendingButton.setEnabled(enabled);
			chordDescendingAscendingButton.setEnabled(enabled);
			if (!enabled) {
				chordAscendingButton.setSelection(true);
				chordDescendingButton.setSelection(false);
				chordAscendingDescendingButton.setSelection(false);
				chordDescendingAscendingButton.setSelection(false);
			}
		}

		// enable/disable block radio buttons
		else if (updateGroup == BLOCK_GROUP) {
			blockAscendingButton.setEnabled(enabled);
			blockDescendingButton.setEnabled(enabled);
			blockAscendingDescendingButton.setEnabled(enabled);
			blockDescendingAscendingButton.setEnabled(enabled);
			if (!enabled) {
				blockAscendingButton.setSelection(true);
				blockDescendingButton.setSelection(false);
				blockAscendingDescendingButton.setSelection(false);
				blockDescendingAscendingButton.setSelection(false);
			}
		}
	}

	/* --- perform methods --- */

	@Override
	public boolean performOk() {
		final SoundMachine soundMachine = Activator.getDefault().getSoundMachine();

		final int midiInstrument = instrumentCombo.getSelectionIndex();
		soundMachine.setMidiInstrument(midiInstrument);
		prefs.setValue(Preferences.SOUND_DEFAULT_MIDI_INSTRUMENT,
				SoundConstants.MIDI_INSTRUMENT_NUMBERS[midiInstrument]);

		final int midiKeyboard = keyboardCombo.getSelectionIndex();
		soundMachine.setMidiKeyboard(midiKeyboard);
		prefs.setValue(Preferences.SOUND_KEYBOARD_INSTRUMENT,
				SoundConstants.MIDI_KEYBOARD_INSTRUMENT_NUMBERS[midiKeyboard]);

		final int testToneLength = Integer.parseInt(testToneLengthText.getText());
		soundMachine.setTestToneLength(testToneLength);
		prefs.setValue(Preferences.SOUND_TEST_TONE_LENGTH, testToneLength);

		final int chordToneLength = Integer.parseInt(chordToneLengthText.getText());
		soundMachine.setChordToneLength(chordToneLength);
		prefs.setValue(Preferences.SOUND_CHORD_TONE_LENGTH, chordToneLength);

		final int blockToneLength = Integer.parseInt(blockToneLengthText.getText());
		soundMachine.setBlockToneLength(blockToneLength);
		prefs.setValue(Preferences.SOUND_BLOCK_TONE_LENGTH, blockToneLength);

		final int testToneDistance = Integer.parseInt(testToneDistanceText.getText());
		soundMachine.setTestToneDistance(testToneDistance);
		prefs.setValue(Preferences.SOUND_TEST_TONE_DISTANCE, testToneDistance);

		final int chordToneDistance = Integer.parseInt(chordToneDistanceText.getText());
		soundMachine.setChordToneDistance(chordToneDistance);
		prefs.setValue(Preferences.SOUND_CHORD_TONE_DISTANCE, chordToneDistance);

		final int blockToneDistance = Integer.parseInt(blockToneDistanceText.getText());
		soundMachine.setBlockToneDistance(blockToneDistance);
		prefs.setValue(Preferences.SOUND_BLOCK_TONE_DISTANCE, blockToneDistance);

		final String testPlayPattern = getTestPlayPattern();
		soundMachine.setTestPlayPattern(testPlayPattern);
		prefs.setValue(Preferences.SOUND_TEST_PLAY_PATTERN, testPlayPattern);

		final String chordPlayPattern = getChordPlayPattern();
		soundMachine.setChordPlayPattern(chordPlayPattern);
		prefs.setValue(Preferences.SOUND_CHORD_PLAY_PATTERN, chordPlayPattern);

		final String blockPlayPattern = getBlockPlayPattern();
		soundMachine.setBlockPlayPattern(blockPlayPattern);
		prefs.setValue(Preferences.SOUND_BLOCK_PLAY_PATTERN, blockPlayPattern);

		final int fretboardToneLength = Integer.parseInt(fretboardViewToneLengthText.getText());
		soundMachine.setFretboardToneLength(fretboardToneLength);
		prefs.setValue(Preferences.SOUND_FRETBOARD_VIEW_TONE_LENGTH, fretboardToneLength);

		final int keyboardToneLength = Integer.parseInt(keyboardViewToneLengthText.getText());
		soundMachine.setKeyboardToneLength(keyboardToneLength);
		prefs.setValue(Preferences.SOUND_KEYBOARD_VIEW_TONE_LENGTH, keyboardToneLength);

		final int volume = volumeScale.getSelection();
		soundMachine.setVolume(volume);
		prefs.setValue(Preferences.SOUND_VOLUME, volume);

		return super.performOk();
	}

	@Override
	protected void performDefaults() {

		int index = getMidiNumbersList().indexOf(prefs.getDefaultInt(Preferences.SOUND_DEFAULT_MIDI_INSTRUMENT));
		instrumentCombo.select(index);

		index = getKeyboardMidiNumbersList().indexOf(prefs.getDefaultInt(Preferences.SOUND_KEYBOARD_INSTRUMENT));
		keyboardCombo.select(index);

		testToneLengthText.setText("" + prefs.getDefaultInt(Preferences.SOUND_TEST_TONE_LENGTH)); //$NON-NLS-1$
		chordToneLengthText.setText("" + prefs.getDefaultInt(Preferences.SOUND_CHORD_TONE_LENGTH)); //$NON-NLS-1$
		blockToneLengthText.setText("" + prefs.getDefaultInt(Preferences.SOUND_BLOCK_TONE_LENGTH)); //$NON-NLS-1$

		testToneDistanceText.setText("" + prefs.getDefaultInt(Preferences.SOUND_TEST_TONE_DISTANCE)); //$NON-NLS-1$
		chordToneDistanceText.setText("" + prefs.getDefaultInt(Preferences.SOUND_CHORD_TONE_DISTANCE)); //$NON-NLS-1$
		blockToneDistanceText.setText("" + prefs.getDefaultInt(Preferences.SOUND_BLOCK_TONE_DISTANCE)); //$NON-NLS-1$
		fretboardViewToneLengthText.setText("" + prefs.getDefaultInt(Preferences.SOUND_FRETBOARD_VIEW_TONE_LENGTH)); //$NON-NLS-1$
		keyboardViewToneLengthText.setText("" + prefs.getDefaultInt(Preferences.SOUND_KEYBOARD_VIEW_TONE_LENGTH)); //$NON-NLS-1$

		initButtons(prefs.getDefaultString(Preferences.SOUND_TEST_PLAY_PATTERN),
				prefs.getDefaultString(Preferences.SOUND_CHORD_PLAY_PATTERN),
				prefs.getDefaultString(Preferences.SOUND_BLOCK_PLAY_PATTERN));

		volumeScale.setSelection(prefs.getDefaultInt(Preferences.SOUND_VOLUME));

		super.performDefaults();
	}

	/* --- helper methods --- */

	protected List<Note> getTestNotes() {
		if (testNotes == null) {
			testNotes = new ArrayList<Note>();
			testNotes.add(Factory.getInstance().getNote(0));
			testNotes.add(Factory.getInstance().getNote(4));
			testNotes.add(Factory.getInstance().getNote(7));
		}
		return testNotes;
	}

	private List<Integer> getMidiNumbersList() {
		if (midiNumbersList == null) {
			midiNumbersList = new ArrayList<Integer>();
			for (final int element : SoundConstants.MIDI_INSTRUMENT_NUMBERS) {
				midiNumbersList.add(element);
			}
		}
		return midiNumbersList;
	}

	private List<Integer> getKeyboardMidiNumbersList() {
		if (keyboardMidiNumbersList == null) {
			keyboardMidiNumbersList = new ArrayList<Integer>();
			for (final int element : SoundConstants.MIDI_KEYBOARD_INSTRUMENT_NUMBERS) {
				keyboardMidiNumbersList.add(element);
			}
		}
		return keyboardMidiNumbersList;
	}

	private String getTestPlayPattern() {
		return testAscendingButton.getSelection() ? SoundConstants.ASCENDING_PATTERN : testDescendingButton
				.getSelection() ? SoundConstants.DESCENDING_PATTERN
				: testAscendingDescendingButton.getSelection() ? SoundConstants.ASCENDING_DESCENDING_PATTERN
						: SoundConstants.DESCENDING_ASCENDING_PATTERN;
	}

	private String getChordPlayPattern() {
		return chordAscendingButton.getSelection() ? SoundConstants.ASCENDING_PATTERN : chordDescendingButton
				.getSelection() ? SoundConstants.DESCENDING_PATTERN
				: chordAscendingDescendingButton.getSelection() ? SoundConstants.ASCENDING_DESCENDING_PATTERN
						: SoundConstants.DESCENDING_ASCENDING_PATTERN;
	}

	private String getBlockPlayPattern() {
		return blockAscendingButton.getSelection() ? SoundConstants.ASCENDING_PATTERN : blockDescendingButton
				.getSelection() ? SoundConstants.DESCENDING_PATTERN
				: blockAscendingDescendingButton.getSelection() ? SoundConstants.ASCENDING_DESCENDING_PATTERN
						: SoundConstants.DESCENDING_ASCENDING_PATTERN;
	}
}
