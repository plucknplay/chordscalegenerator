/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.AdvancedFretBlock;
import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.core.model.enums.Clef;
import com.plucknplay.csg.core.model.enums.IntervalNamesMode;
import com.plucknplay.csg.core.model.enums.NoteNamesMode;
import com.plucknplay.csg.core.util.ToneRangeMode;
import com.plucknplay.csg.sound.SoundConstants;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.perspectives.ChordsPerspective;
import com.plucknplay.csg.ui.perspectives.ScalesPerspective;
import com.plucknplay.csg.ui.perspectives.SetupPerspective;
import com.plucknplay.csg.ui.util.enums.BackgroundColorMode;
import com.plucknplay.csg.ui.util.enums.BarreMode;
import com.plucknplay.csg.ui.util.enums.BoxViewPresentationMode;
import com.plucknplay.csg.ui.util.enums.FigureSizeMode;
import com.plucknplay.csg.ui.util.enums.FingeringMode;
import com.plucknplay.csg.ui.util.enums.KeySizeMode;
import com.plucknplay.csg.ui.util.enums.Position;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {

		final IEclipsePreferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);

		// general settings
		node.putBoolean(Preferences.SHOW_STATUS_BAR, true);
		node.putBoolean(Preferences.LOCK_TOOLBAR, true);
		node.putBoolean(Preferences.FIRST_START, true);
		node.putBoolean(Preferences.CONFIRM_EXIT, true);
		node.putBoolean(Preferences.SHOW_LOGIN_PROMPT, true);
		node.putBoolean(Preferences.LEFT_HANDER, false);
		node.put(Preferences.NOTES_MODE, Constants.NOTES_MODE_CROSS_AND_B);
		node.putBoolean(Preferences.SHOW_BLOCKS, true);
		node.put(Preferences.BLOCK_MODE, UIConstants.BLOCK_MODE_FRETS);
		node.putInt(Preferences.ROOT_NOTE, 0);
		node.putInt(Preferences.FRET_NUMBER, Constants.DEFAULT_FRET_NUMBER);
		node.putInt(Preferences.CAPO_FRET, 0);

		// graphics
		node.putInt(Preferences.GRAPHICS_ANTI_ALIASING_MODE, SWT.ON);
		node.putInt(Preferences.GRAPHICS_TEXT_ANTI_ALIASING_MODE, SWT.ON);
		node.putInt(Preferences.GRAPHICS_INTERPOLATION_MODE, SWT.HIGH);

		// fingering notation
		node.put(Preferences.GENERAL_FINGERING_MODE, FingeringMode.NUMBERS_T.toString());
		node.put(Preferences.GENERAL_FINGERING_MODE_CUSTOM_NOTATION, "TIMRP"); //$NON-NLS-1$

		// note names
		node.put(Preferences.ABSOLUTE_NOTE_NAMES_MODE, NoteNamesMode.DEFAULT.toString());
		node.put(Preferences.GENERAL_H_NOTE_NAME, ConstantMessages.PreferenceInitializer_default_h_name);
		node.put(Preferences.GENERAL_B_NOTE_NAME, ConstantMessages.PreferenceInitializer_default_b_name);
		NoteNamesMode.setHName(ConstantMessages.PreferenceInitializer_default_h_name);
		NoteNamesMode.setBName(ConstantMessages.PreferenceInitializer_default_b_name);

		// interval names
		node.put(Preferences.INTERVAL_NAMES_MODE, IntervalNamesMode.DEFAULT.toString());
		node.putBoolean(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME, false);
		node.put(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME, "R"); //$NON-NLS-1$
		node.putBoolean(Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS, false);

		// chord / scale names
		node.put(Preferences.SCALE_NAMES_SEPARATOR, Constants.BLANK_SPACE);
		node.putBoolean(Preferences.SCALE_NAMES_USE_SEPARATOR, true);
		node.put(Preferences.CHORD_NAMES_SEPARATOR, Constants.BLANK_SPACE);
		node.putBoolean(Preferences.CHORD_NAMES_USE_SEPARATOR, true);
		node.putBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_SHORT_MODE, true);
		node.put(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_PREFIX_MODE,
				NlsUtil.isGerman() ? Constants.EXCLUDED_INTERVALS_PREFIX_O_POINT
						: Constants.EXCLUDED_INTERVALS_PREFIX_NO);
		node.putBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_INTERVALS, true);
		node.putBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_PREFIX_AND_INTERVALS, false);
		node.putBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_COMPACT_MODE, false);
		node.putBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_IN_BRACKETS, true);
		node.put(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BRACKETS_MODE, Constants.BRACKETS_ROUND);

		// warning prompts
		node.putBoolean(Preferences.WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW, false);
		node.putBoolean(Preferences.WARNINGS_HIDE_PROMPT_TRUNCATE_RESULTS_VIEW, false);
		node.putBoolean(Preferences.WARNINGS_HIDE_PROMPT_LEVEL_CHANGE, false);

		// calculator
		node.putInt(Preferences.CALCULATOR_MAX_RESULTS_NUMBER, 1000);
		node.putBoolean(Preferences.CALCULATOR_BARRES_PREFERRED, true);
		node.putBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_1ST, true);
		node.putBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_3RD, true);
		node.putBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_5TH, true);
		node.putInt(Preferences.CALCULATOR_FIND_CHORDS_RESTRICTIONS,
				Constants.CALCULATOR_RESTRICTION_MAX_1_EXCLUDED_INTERVAL);

		// sound
		node.putInt(Preferences.SOUND_DEFAULT_MIDI_INSTRUMENT, SoundConstants.DEFAULT_MIDI_INSTRUMENT);
		node.putInt(Preferences.SOUND_KEYBOARD_INSTRUMENT, SoundConstants.DEFAULT_MIDI_KEYBOARD);
		node.putInt(Preferences.SOUND_TEST_TONE_LENGTH, SoundConstants.DEFAULT_TONE_LENGTH);
		node.putInt(Preferences.SOUND_TEST_TONE_DISTANCE, SoundConstants.DEFAULT_TONE_DISTANCE);
		node.put(Preferences.SOUND_TEST_PLAY_PATTERN, SoundConstants.DEFAULT_PLAY_PATTERN);
		node.putInt(Preferences.SOUND_CHORD_TONE_LENGTH, SoundConstants.DEFAULT_TONE_LENGTH);
		node.putInt(Preferences.SOUND_CHORD_TONE_DISTANCE, SoundConstants.DEFAULT_CHORD_TONE_DISTANCE);
		node.put(Preferences.SOUND_CHORD_PLAY_PATTERN, SoundConstants.DEFAULT_PLAY_PATTERN);
		node.putInt(Preferences.SOUND_BLOCK_TONE_LENGTH, SoundConstants.DEFAULT_TONE_LENGTH);
		node.putInt(Preferences.SOUND_BLOCK_TONE_DISTANCE, SoundConstants.DEFAULT_TONE_DISTANCE);
		node.put(Preferences.SOUND_BLOCK_PLAY_PATTERN, SoundConstants.DEFAULT_PLAY_PATTERN);
		node.putInt(Preferences.SOUND_FRETBOARD_VIEW_TONE_LENGTH, SoundConstants.DEFAULT_TONE_LENGTH);
		node.putInt(Preferences.SOUND_KEYBOARD_VIEW_TONE_LENGTH, SoundConstants.DEFAULT_TONE_LENGTH);
		node.putInt(Preferences.SOUND_VOLUME, SoundConstants.DEFAULT_VOLUME);

		// perspectives
		node.put(Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING, SetupPerspective.ID);
		node.put(Preferences.PERSPECTIVES_BINDING_CHORD_GENERATION, ChordsPerspective.ID);
		node.put(Preferences.PERSPECTIVES_BINDING_FIND_CHORDS, ChordsPerspective.ID);
		node.put(Preferences.PERSPECTIVES_BINDING_FIND_SCALES, ScalesPerspective.ID);
		node.putBoolean(Preferences.PERSPECTIVES_CLEAR_SELECTION, true);

		// export
		node.put(Preferences.CLIPBOARD_EXPORT_FILE_EXTENSION, UIConstants.FILE_EXTENSION_PNG);
		node.put(Preferences.CLIPBOARD_EXPORT_FIRST_VIEW, UIConstants.EXPORT_BOX);
		node.put(Preferences.CLIPBOARD_EXPORT_SECOND_VIEW, UIConstants.EXPORT_NONE);
		node.put(Preferences.CLIPBOARD_EXPORT_THIRD_VIEW, UIConstants.EXPORT_NONE);
		node.putBoolean(Preferences.CLIPBOARD_EXPORT_REVERSE_ORDER, false);
		node.putBoolean(Preferences.EXPORT_FILENAME_SUGGESTION, true);
		node.putBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_BOX, false);
		node.putBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_TAB, true);
		node.putBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_NOTES, true);
		node.putBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_FRETBOARD, true);
		node.putBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_KEYBOARD, true);
		node.putBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_IN_FRONT, false);
		node.putBoolean(Preferences.EXPORT_FILENAME_REPLACE_WHITE_SPACE, true);
		node.put(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_WHITE_SPACE, Constants.UNDERSCORE);
		node.put(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_ILLEGAL_CHARACTER, Constants.HYPHEN);
		node.put(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT, Constants.UNDERSCORE);

		// instruments view
		node.putBoolean(Preferences.INSTRUMENTS_VIEW_LINKED_WITH_EDITOR, true);

		// chords view
		node.putBoolean(Preferences.CHORDS_VIEW_LINKED_WITH_EDITOR, true);
		node.putBoolean(Preferences.CHORDS_VIEW_SHOW_INTERVAL_SECTION, true);
		node.put(Preferences.CHORDS_VIEW_ORIENTATION, UIConstants.AUTOMATIC_VIEW_ORIENTATION);
		node.putInt(Preferences.CHORDS_VIEW_CHORD_SECTION_WEIGHT, 70);
		node.putInt(Preferences.CHORDS_VIEW_INTERVAL_SECTION_WEIGHT, 30);

		// scales view
		node.putBoolean(Preferences.SCALES_VIEW_LINKED_WITH_EDITOR, true);
		node.putBoolean(Preferences.SCALES_VIEW_SHOW_INTERVAL_SECTION, true);
		node.put(Preferences.SCALES_VIEW_ORIENTATION, UIConstants.AUTOMATIC_VIEW_ORIENTATION);
		node.putInt(Preferences.SCALES_VIEW_SCALE_SECTION_WEIGHT, 70);
		node.putInt(Preferences.SCALES_VIEW_INTERVAL_SECTION_WEIGHT, 30);

		// chord generation view
		final boolean defaultEmptyStrings = true;
		node.putInt(Preferences.CHORD_GENERATION_VIEW_ROOT_NOTE, 0);
		node.put(Preferences.CHORD_GENERATION_VIEW_CHORD, ""); //$NON-NLS-1$
		node.putInt(Preferences.CHORD_GENERATION_VIEW_BASS_TONE, -1);
		node.putInt(Preferences.CHORD_GENERATION_VIEW_LEAD_TONE, -1);
		node.putInt(Preferences.CHORD_GENERATION_VIEW_MIN_LEVEL, 0);
		node.putInt(Preferences.CHORD_GENERATION_VIEW_MAX_LEVEL, 1);
		node.putInt(Preferences.CHORD_GENERATION_VIEW_MIN_STRING, 1);
		node.putInt(Preferences.CHORD_GENERATION_VIEW_MAX_STRING, Constants.MAX_STRING_SIZE);
		node.putInt(Preferences.CHORD_GENERATION_VIEW_MIN_FRET, 1);
		node.putInt(Preferences.CHORD_GENERATION_VIEW_MAX_FRET, 12);
		node.put(Preferences.CHORD_GENERATION_VIEW_GRIP_RANGE, "3"); //$NON-NLS-1$
		node.putInt(Preferences.CHORD_GENERATION_VIEW_GRIP_RANGE_UNIT, 0);
		node.putInt(Preferences.CHORD_GENERATION_VIEW_MAX_SINGLE_TONE_NUMBERS, 0);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_EMPTY_STRINGS, defaultEmptyStrings);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_EMPTY_STRINGS_TEMP, defaultEmptyStrings);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_MUTED_STRINGS, true);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_ONLY_PACKED, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_ONLY_SINGLE_MUTED_STRINGS, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_DOUBLED_TONES, true);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_ONLY_ASCENDING, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_WITHOUT_1ST, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_WITHOUT_3RD, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_WITHOUT_5TH, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_BASS_TONE, true);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_LEAD_TONE, true);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_LEVEL, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_STRING_RANGE, true);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_FRET_RANGE, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_GRIP_RANGE, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_MAX_SINGLE_TONE_NUMBER, true);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_EMPTY_MUTED_STRINGS, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_MUTED_STRINGS_INFO, true);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_TONES_INFO, true);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_EXCLUDED_INTERVALS, true);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_EXCLUDED_INTERVALS_COMPOSITE_EXPANDED, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_SECTION_EXPANDED, false);
		node.putBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_SECTION_CHANGED, false);

		// chord results view
		node.putInt(Preferences.CHORD_RESULTS_VIEW_ENTRIES_PER_PAGE, 100);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_COMPACT_MODE, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_IN_GRAY, true);
		node.put(Preferences.CHORD_RESULTS_VIEW_COLUMN_SORTING_ORDER,
				"1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 0"); //$NON-NLS-1$
		node.put(Preferences.CHORD_RESULTS_VIEW_COLUMN_SORTING_DIRECTIONS,
				"1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 "); //$NON-NLS-1$
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_LEVEL_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_BASS_TONE_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_LEAD_TONE_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_NOTES_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_INTERVALS_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_FRETS_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_STRING_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_STRING_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_STRING_SPAN_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_FRET_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_FRET_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_FRET_SPAN_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_DISTANCE_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_EMPTY_STRINGS_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_COLUMN, true);
		node.putBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_DOUBLED_TONES_COLUMN, true);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_SELECTION_COLUMN_WIDTH, 23);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_CHORD_COLUMN_WIDTH, 120);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_LEVEL_COLUMN_WIDTH, 65);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_BASS_TONE_COLUMN_WIDTH, 80);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_LEAD_TONE_COLUMN_WIDTH, 80);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_NOTES_COLUMN_WIDTH, 180);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_INTERVALS_COLUMN_WIDTH, 180);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_FRETS_COLUMN_WIDTH, 180);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_MIN_STRING_COLUMN_WIDTH, 52);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_MAX_STRING_COLUMN_WIDTH, 52);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_STRING_SPAN_COLUMN_WIDTH, 48);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_MIN_FRET_COLUMN_WIDTH, 52);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_MAX_FRET_COLUMN_WIDTH, 52);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_FRET_SPAN_COLUMN_WIDTH, 48);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_DISTANCE_COLUMN_WIDTH, 100);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_EMPTY_STRINGS_COLUMN_WIDTH, 55);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_MUTED_STRINGS_COLUMN_WIDTH, 55);
		node.putInt(Preferences.CHORD_RESULTS_VIEW_DOUBLED_TONES_COLUMN_WIDTH, 45);

		// fretboard view
		node.put(Preferences.FRETBOARD_VIEW_MODE, UIConstants.MODE_FINGERING);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_BLOCK_NAVIGATION_INFO, true);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_MUTED_STRINGS, false);
		node.putBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE, false);
		node.putBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR, true);
		node.putBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE, false);
		node.putBoolean(Preferences.FRETBOARD_VIEW_FRAME_FINGERING, true);
		node.put(Preferences.FRETBOARD_VIEW_POINTS_BACKGROUND, BackgroundColorMode.BLACK.toString());
		node.put(Preferences.FRETBOARD_VIEW_FINGERING_BACKGROUND, BackgroundColorMode.COLORED.toString());
		node.putBoolean(Preferences.FRETBOARD_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE, false);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_BARRE, true);
		node.put(Preferences.FRETBOARD_VIEW_BARRE_MODE, BarreMode.LINE.toString());
		node.putInt(Preferences.FRETBOARD_VIEW_BARRE_LINE_WIDTH, 3);
		node.put(Preferences.FRETBOARD_VIEW_BARRE_BAR_BACKGROUND, BackgroundColorMode.SAME.toString());
		node.putBoolean(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE, true);
		node.putBoolean(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER, false);
		node.put(Preferences.FRETBOARD_VIEW_SHOW_BLOCK_PRESENTATION, UIConstants.BLOCK_OVERLAY);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_FRETBOARD, false);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE, false);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE, false);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK, false);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES, true);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES, true);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK, false);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE, false);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_FRAME, true);
		node.put(Preferences.FRETBOARD_VIEW_BACKGROUND_MODE, UIConstants.NO_BACKGROUND);
		node.putInt(Preferences.FRETBOARD_VIEW_BACKGROUND_INDEX, 0);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_INLAYS, true);
		node.putBoolean(Preferences.FRETBOARD_VIEW_INLAYS_GRAY_COLOR, true);
		node.put(Preferences.FRETBOARD_VIEW_INLAYS_POSITION, Position.CENTER.toString());
		node.put(Preferences.FRETBOARD_VIEW_INLAYS_SHAPE, UIConstants.INLAYS_SHAPE_TRIANGLE);
		node.putBoolean(Preferences.FRETBOARD_VIEW_SHOW_FRET_NUMBERS, true);
		node.putBoolean(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_GRAY_COLOR, true);
		node.putBoolean(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_REDUCED_MODE, true);
		node.put(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_POSITION, Position.BOTTOM.toString());
		node.put(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_NUMERALS, UIConstants.NUMERALS_MODE_ARABIC);
		node.putInt(Preferences.FRETBOARD_VIEW_EXPORT_HEIGHT, 200);
		node.putBoolean(Preferences.FRETBOARD_VIEW_EXPORT_LIVE_SIZE, false);

		// blocks
		node.putInt(Preferences.FRET_BLOCK_RANGE, FretBlock.DEFAULT_FRET_RANGE);
		node.putBoolean(Preferences.FRET_BLOCK_USE_EMPTY_STRINGS, true);
		node.putInt(Preferences.ADVANCED_FRET_BLOCK_RANGE, AdvancedFretBlock.DEFAULT_FRET_RANGE);
		node.putInt(Preferences.ADVANCED_FRET_BLOCK_STRING_RANGE_DECREASE,
				AdvancedFretBlock.DEFAULT_STRING_RANGE_DECREASE);
		node.putBoolean(Preferences.ADVANCED_FRET_BLOCK_USE_EMPTY_STRINGS, false);
		node.putBoolean(Preferences.OCTAVE_BLOCK_ONLY_ROOT_NOTES, false);

		// keyboard view
		node.put(Preferences.KEYBOARD_VIEW_MODE, UIConstants.MODE_NOTES);
		node.putBoolean(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE, false);
		node.putBoolean(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE, true);
		node.putBoolean(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR, false);
		node.put(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID, "255,0,0"); //$NON-NLS-1$
		node.putBoolean(Preferences.KEYBOARD_VIEW_FRAME_NOTES_INTERVALS, true);
		node.put(Preferences.KEYBOARD_VIEW_NOTES_INTERVALS_BACKGROUND, BackgroundColorMode.COLORED.toString());
		node.putBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_KEYBOARD, false);
		node.putBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE, false);
		node.putBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE, false);
		node.putBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK, false);
		node.putBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES, true);
		node.putBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES, true);
		node.putBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK, false);
		node.put(Preferences.KEYBOARD_VIEW_BLOCK_PRESENTATION, UIConstants.BLOCK_OVERLAY);
		node.put(Preferences.KEYBOARD_VIEW_TONE_RANGE_MODE, ToneRangeMode.ACTIVE_INSTRUMENT_FULL_OCTAVES.toString());
		node.putInt(Preferences.KEYBOARD_VIEW_TONE_RANGE_START_TONE, -1);
		node.putInt(Preferences.KEYBOARD_VIEW_TONE_RANGE_END_TONE, -1);
		node.put(Preferences.KEYBOARD_VIEW_KEY_SIZE, KeySizeMode.FLEXIBLE.toString());
		node.putInt(Preferences.KEYBOARD_VIEW_EXPORT_HEIGHT, 200);
		node.putBoolean(Preferences.KEYBOARD_VIEW_EXPORT_LIVE_SIZE, false);

		// tab view
		node.putBoolean(Preferences.TAB_VIEW_SHOW_MUTED_STRINGS, false);
		node.putBoolean(Preferences.TAB_VIEW_DRAW_DOUBLED_STRINGS, false);
		node.putBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE, false);
		node.putBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_BOLD_FONT, false);
		node.putBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_FRAME, true);
		node.putBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR, false);
		node.put(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID, "255,0,0"); //$NON-NLS-1$
		node.putInt(Preferences.TAB_VIEW_EXPORT_HEIGHT, 200);
		node.putBoolean(Preferences.TAB_VIEW_EXPORT_LIVE_SIZE, false);

		// box view
		node.put(Preferences.BOX_VIEW_PRESENTATION_MODE, BoxViewPresentationMode.HORIZONTAL.toString());
		node.putInt(Preferences.BOX_VIEW_FRAME_MIN_FRET_COUNT, 4);
		node.putInt(Preferences.BOX_VIEW_FRAME_MAX_UNASSIGNED_FRET_COUNT, 2);
		node.putBoolean(Preferences.BOX_VIEW_FRAME_HIGHLIGHT_NUT, true);
		node.putBoolean(Preferences.BOX_VIEW_FRAME_HIGHLIGHT_OUTER_FRETS, false);
		node.putBoolean(Preferences.BOX_VIEW_FRAME_DRAW_DOUBLED_STRINGS, false);
		node.putBoolean(Preferences.BOX_VIEW_FRAME_DRAW_FRETLESS_FRETS_DOTTED, false);
		node.putBoolean(Preferences.BOX_VIEW_FRAME_SMALL_FRETS, true);
		node.putBoolean(Preferences.BOX_VIEW_FRAME_GRAY_COLOR, false);
		node.put(Preferences.BOX_VIEW_FRET_NUMBERS_MODE, UIConstants.NUMERALS_MODE_ARABIC);
		node.put(Preferences.BOX_VIEW_FRET_NUMBERS_HORIZONTAL_POSITION, Position.TOP.toString());
		node.put(Preferences.BOX_VIEW_FRET_NUMBERS_VERTICAL_POSITION, Position.LEFT.toString());
		node.putBoolean(Preferences.BOX_VIEW_FRET_NUMBERS_PLACE_AT_FIRST_FINGER, true);
		node.putBoolean(Preferences.BOX_VIEW_FRET_NUMBERS_VISIBLE_FOR_FIRST_FRET, true);
		node.putBoolean(Preferences.BOX_VIEW_FRET_NUMBERS_FRAMED, true);
		node.putBoolean(Preferences.BOX_VIEW_FRET_NUMBERS_GRAY_COLOR, false);
		node.putBoolean(Preferences.BOX_VIEW_SHOW_MUTED_STRINGS, true);
		node.putBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE, false);
		node.putBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR, true);
		node.putBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE, false);
		node.put(Preferences.BOX_VIEW_POINTS_SIZE, FigureSizeMode.MEDIUM.toString());
		node.put(Preferences.BOX_VIEW_POINTS_BACKGROUND, BackgroundColorMode.BLACK.toString());
		node.putBoolean(Preferences.BOX_VIEW_SHOW_FINGERING_OUTSIDE_BOX, false);
		node.putBoolean(Preferences.BOX_VIEW_SHOW_NOTES_OUTSIDE_BOX, true);
		node.putBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS_OUTSIDE_BOX, true);
		node.putBoolean(Preferences.BOX_VIEW_FRAME_INSIDE, true);
		node.put(Preferences.BOX_VIEW_BACKGROUND_INSIDE, BackgroundColorMode.WHITE.toString());
		node.putBoolean(Preferences.BOX_VIEW_SHOW_BARRE, true);
		node.put(Preferences.BOX_VIEW_BARRE_MODE, BarreMode.LINE.toString());
		node.putInt(Preferences.BOX_VIEW_BARRE_LINE_WIDTH, 3);
		node.put(Preferences.BOX_VIEW_BARRE_BAR_BACKGROUND, BackgroundColorMode.SAME.toString());
		node.putBoolean(Preferences.BOX_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE, true);
		node.putBoolean(Preferences.BOX_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER, false);
		node.putBoolean(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_CLOSE_TO_FRAME, false);
		node.put(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_SIZE, FigureSizeMode.SMALL.toString());
		node.putBoolean(Preferences.BOX_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE, false);
		node.put(Preferences.BOX_VIEW_BACKGROUND_OUTSIDE, BackgroundColorMode.COLORED.toString());
		node.putBoolean(Preferences.BOX_VIEW_FRAME_OUTSIDE, true);
		node.putBoolean(Preferences.BOX_VIEW_SHOW_FINGERING, true);
		node.putBoolean(Preferences.BOX_VIEW_SHOW_NOTES, false);
		node.putBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS, false);
		node.putInt(Preferences.BOX_VIEW_EXPORT_HEIGHT, 200);
		node.putBoolean(Preferences.BOX_VIEW_EXPORT_LIVE_SIZE, false);

		// notes view
		node.put(Preferences.NOTES_VIEW_CLEF, Clef.G_ONE_OCTAVE_DEEPER.toString());
		node.put(Preferences.NOTES_VIEW_DISPLAY_MODE_GRIPTABLES, UIConstants.DISPLAY_AS_BLOCK);
		node.put(Preferences.NOTES_VIEW_DISPLAY_MODE_CHORD_BLOCKS, UIConstants.DISPLAY_AS_ASC_ARPEGGIO);
		node.put(Preferences.NOTES_VIEW_DISPLAY_MODE_CHORD_SCHEMES, UIConstants.DISPLAY_AS_ASC_ARPEGGIO);
		node.put(Preferences.NOTES_VIEW_DISPLAY_MODE_SCALE_BLOCKS, UIConstants.DISPLAY_AS_ASC_ARPEGGIO);
		node.put(Preferences.NOTES_VIEW_DISPLAY_MODE_SCALES, UIConstants.DISPLAY_AS_ASC_ARPEGGIO);
		node.putBoolean(Preferences.NOTES_VIEW_SHOW_CLEF_ANNOTATION, true);
		node.putBoolean(Preferences.NOTES_VIEW_USE_MAX_WIDTH, true);
		node.putBoolean(Preferences.NOTES_VIEW_FLEXIBLE_SPACING, false);
		node.putBoolean(Preferences.NOTES_VIEW_FILTER_CLEF_NOT_WHOLE_STAFF_USED, true);
		node.putBoolean(Preferences.NOTES_VIEW_FILTER_CLEF_NO_NOTE_ON_STAFF, true);
		node.putBoolean(Preferences.NOTES_VIEW_FILTER_CLEF_CHIAVETTE, true);
		node.put(Preferences.NOTES_VIEW_FALLBACK_CLEF, Clef.G_ONE_OCTAVE_DEEPER.toString());
		node.putBoolean(Preferences.NOTES_VIEW_SHOW_ONLY_CHORD_BLOCKS, false);
		node.putBoolean(Preferences.NOTES_VIEW_SHOW_ONLY_SCALE_BLOCKS, false);
		node.putBoolean(Preferences.NOTES_VIEW_OPEN_NOTE_REPRESENTATION, true);
		node.putBoolean(Preferences.NOTES_VIEW_HIGHLIGHT_ROOT_NOTE, true);
		node.putInt(Preferences.NOTES_VIEW_EXPORT_HEIGHT, 200);
		node.putBoolean(Preferences.NOTES_VIEW_EXPORT_LIVE_SIZE, false);

		// scale results view
		node.putBoolean(Preferences.SCALE_RESULTS_VIEW_MARK_SEARCH, true);
		node.putInt(Preferences.SCALE_RESULTS_VIEW_ENTRIES_PER_PAGE, 100);
		node.putBoolean(Preferences.SCALE_RESULTS_VIEW_COMPACT_MODE, true);
		node.putBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_SEPARATORS_IN_GRAY, true);
		node.put(Preferences.SCALE_RESULTS_VIEW_COLUMN_SORTING_ORDER, "4, 0, 1, 2, 3"); //$NON-NLS-1$
		node.put(Preferences.SCALE_RESULTS_VIEW_COLUMN_SORTING_DIRECTIONS, "1, 1, 1, 1, -1"); //$NON-NLS-1$
		node.putBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_INTERVALS_COLUMN, true);
		node.putBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_NOTES_COLUMN, true);
		node.putBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_ROOT_NOTE_COLUMN, true);
		node.putBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_COVERAGE_COLUMN, true);
		node.putInt(Preferences.SCALE_RESULTS_VIEW_SCALE_COLUMN_WIDTH, 130);
		node.putInt(Preferences.SCALE_RESULTS_VIEW_INTERVALS_COLUMN_WIDTH, 180);
		node.putInt(Preferences.SCALE_RESULTS_VIEW_NOTES_COLUMN_WIDTH, 180);
		node.putInt(Preferences.SCALE_RESULTS_VIEW_ROOT_NOTE_COLUMN_WIDTH, 75);
		node.putInt(Preferences.SCALE_RESULTS_VIEW_COVERAGE_COLUMN_WIDTH, 80);

		// scale finder view
		node.put(Preferences.SCALE_FINDER_VIEW_ORIENTATION, UIConstants.AUTOMATIC_VIEW_ORIENTATION);
		node.putBoolean(Preferences.SCALE_FINDER_VIEW_CLEAR_INPUT_AFTER_CALCULATION, false);

		// views
		node.putBoolean(Preferences.VIEWS_SHOW_INFO_INPUT, true);
		node.putBoolean(Preferences.VIEWS_SHOW_INFO_SEARCH_MODE, true);
		node.putBoolean(Preferences.VIEWS_SEARCH_MODE_ENABLE_DOUBLE_CLICK, true);
		node.putBoolean(Preferences.VIEWS_SEARCH_MODE_ENABLE_ESC_KEY, true);
		node.putBoolean(Preferences.VIEWS_SEARCH_MODE_ENABLE_FAST_EDITING, true);
		node.putBoolean(Preferences.VIEWS_SEARCH_MODE_FAST_EDITING_DEEP_TO_HIGH, false);
		node.putBoolean(Preferences.VIEWS_SEARCH_MODE_USE_POINTS_MODE, true);
		node.putBoolean(Preferences.VIEWS_SEARCH_MODE_USE_ALWAYS_POINTS_MODE, false);
		node.putBoolean(Preferences.VIEWS_SEARCH_MODE_RELATIVE_NOTES_MODE_KEY_ALT, false);
	}
}
