/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui;

public interface Preferences {

	/* --- general settings --- */

	String SHOW_STATUS_BAR = "chordGenerator.preferences.general.show.status.bar"; //$NON-NLS-1$
	String LOCK_TOOLBAR = "chordGenerator.preferences.general.lock.toolbar"; //$NON-NLS-1$
	String FIRST_START = "chordGenerator.preferences.general.first.start"; //$NON-NLS-1$
	String CONFIRM_EXIT = "chordGenerator.preferences.general.confirm.exit"; //$NON-NLS-1$
	String SHOW_LOGIN_PROMPT = "chordGenerator.preferences.general.show.login.prompt"; //$NON-NLS-1$
	String LEFT_HANDER = "chordGenerator.preferences.general.right.hander"; //$NON-NLS-1$
	String NOTES_MODE = "chordGenerator.preferences.general.notes.mode"; //$NON-NLS-1$
	String SHOW_BLOCKS = "chordGenerator.preferences.views.fretboard.view.show.scale.blocks"; //$NON-NLS-1$
	String BLOCK_MODE = "chordGenerator.preferences.scale.blocks.default.mode"; //$NON-NLS-1$
	String ROOT_NOTE = "chordGenerator.preferences.general.fundamental.tone"; //$NON-NLS-1$
	String FRET_NUMBER = "chordGenerator.preferences.general.fret.number"; //$NON-NLS-1$
	String CAPO_FRET = "chordGenerator.preferences.general.capo.fret"; //$NON-NLS-1$

	/* --- graphics --- */

	String GRAPHICS_ANTI_ALIASING_MODE = "chordGenerator.preferences.general.graphics.anti.aliasing.mode"; //$NON-NLS-1$
	String GRAPHICS_TEXT_ANTI_ALIASING_MODE = "chordGenerator.preferences.general.graphics.text.anti.aliasing.mode"; //$NON-NLS-1$
	String GRAPHICS_INTERPOLATION_MODE = "chordGenerator.preferences.general.graphics.interpolation.mode"; //$NON-NLS-1$

	/* --- fingering notation --- */

	String GENERAL_FINGERING_MODE = "chordGenerator.preferences.general.fingering.mode"; //$NON-NLS-1$
	String GENERAL_FINGERING_MODE_CUSTOM_NOTATION = "chordGenerator.preferences.general.fingering.mode.custom.notation"; //$NON-NLS-1$

	/* --- note names --- */

	String ABSOLUTE_NOTE_NAMES_MODE = "chordGenerator.preferences.general.absolute.note.names.mode"; //$NON-NLS-1$
	String GENERAL_H_NOTE_NAME = "chordGenerator.preferences.general.h.note.name"; //$NON-NLS-1$
	String GENERAL_B_NOTE_NAME = "chordGenerator.preferences.general.b.note.name"; //$NON-NLS-1$

	/* --- interval names --- */

	String INTERVAL_NAMES_MODE = "chordGenerator.preferences.interval.names.mode"; //$NON-NLS-1$
	String INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME = "chordGenerator.preferences.interval.names.use.different.root.interval.name"; //$NON-NLS-1$
	String INTERVAL_NAMES_ROOT_INTERVAL_NAME = "chordGenerator.preferences.interval.names.root.interval.name"; //$NON-NLS-1$
	String INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS = "chordGenerator.preferences.interval.names.use.delta.in.major.intervals"; //$NON-NLS-1$

	/* --- chord / scale names --- */

	String SCALE_NAMES_SEPARATOR = "chordGenerator.preferences.scale.names.separator"; //$NON-NLS-1$
	String SCALE_NAMES_USE_SEPARATOR = "chordGenerator.preferences.scale.names.use.separator"; //$NON-NLS-1$
	String CHORD_NAMES_SEPARATOR = "chordGenerator.preferences.chord.names.separator"; //$NON-NLS-1$
	String CHORD_NAMES_USE_SEPARATOR = "chordGenerator.preferences.chord.names.use.separator"; //$NON-NLS-1$
	String CHORD_NAMES_EXCLUDED_INTERVALS_SHORT_MODE = "chordGenerator.preferences.chord.names.excluded.intervals.short.mode"; //$NON-NLS-1$
	String CHORD_NAMES_EXCLUDED_INTERVALS_PREFIX_MODE = "chordGenerator.preferences.chord.names.excluded.intervals.prefix.mode"; //$NON-NLS-1$
	String CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_INTERVALS = "chordGenerator.preferences.chord.names.excluded.intervals.whitespace.between.intervals"; //$NON-NLS-1$
	String CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_PREFIX_AND_INTERVALS = "chordGenerator.preferences.chord.names.excluded.intervals.whitespace.between.prefix.and.intervals"; //$NON-NLS-1$
	String CHORD_NAMES_EXCLUDED_INTERVALS_COMPACT_MODE = "chordGenerator.preferences.chord.names.excluded.intervals.compact.mode"; //$NON-NLS-1$
	String CHORD_NAMES_EXCLUDED_INTERVALS_IN_BRACKETS = "chordGenerator.preferences.chord.names.excluded.intervals.in.brackets"; //$NON-NLS-1$
	String CHORD_NAMES_EXCLUDED_INTERVALS_BRACKETS_MODE = "chordGenerator.preferences.chord.names.excluded.intervals.brackets.mode"; //$NON-NLS-1$

	/* --- warnings (prompts) --- */

	String WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW = "chordGenerator.preferences.flush.results.view"; //$NON-NLS-1$
	String WARNINGS_HIDE_PROMPT_TRUNCATE_RESULTS_VIEW = "chordGenerator.preferences.truncate.results.view"; //$NON-NLS-1$
	String WARNINGS_HIDE_PROMPT_LEVEL_CHANGE = "chordGenerator.preferences.level.change.may.occur"; //$NON-NLS-1$

	/* --- calculator --- */

	String CALCULATOR_MAX_RESULTS_NUMBER = "chordGenerator.preferences.calculator.max.results.numbers"; //$NON-NLS-1$
	String CALCULATOR_BARRES_PREFERRED = "chordGenerator.preferences.calculator.barres.preferred"; //$NON-NLS-1$
	String CALCULATOR_FIND_CHORDS_WITHOUT_1ST = "chordGenerator.preferences.calculator.find.chords.without.1st"; //$NON-NLS-1$
	String CALCULATOR_FIND_CHORDS_WITHOUT_3RD = "chordGenerator.preferences.calculator.find.chords.without.3rd"; //$NON-NLS-1$
	String CALCULATOR_FIND_CHORDS_WITHOUT_5TH = "chordGenerator.preferences.calculator.find.chords.without.5th"; //$NON-NLS-1$
	String CALCULATOR_FIND_CHORDS_RESTRICTIONS = "chordGenerator.preferences.calculator.find.chords.restrictions"; //$NON-NLS-1$

	/* --- sound --- */

	String SOUND_DEFAULT_MIDI_INSTRUMENT = "chordGenerator.sound.default.midi.instrument"; //$NON-NLS-1$
	String SOUND_KEYBOARD_INSTRUMENT = "chordGenerator.sound.keyboard.instrument"; //$NON-NLS-1$
	String SOUND_TEST_TONE_LENGTH = "chordGenerator.sound.test.tone.length"; //$NON-NLS-1$
	String SOUND_TEST_TONE_DISTANCE = "chordGenerator.sound.test.tone.distance"; //$NON-NLS-1$
	String SOUND_TEST_PLAY_PATTERN = "chordGenerator.sound.test.play.pattern"; //$NON-NLS-1$
	String SOUND_CHORD_TONE_LENGTH = "chordGenerator.sound.chord.tone.length"; //$NON-NLS-1$
	String SOUND_CHORD_TONE_DISTANCE = "chordGenerator.sound.chord.tone.distance"; //$NON-NLS-1$
	String SOUND_CHORD_PLAY_PATTERN = "chordGenerator.sound.chord.play.pattern"; //$NON-NLS-1$
	String SOUND_BLOCK_TONE_LENGTH = "chordGenerator.sound.scale.tone.length"; //$NON-NLS-1$
	String SOUND_BLOCK_TONE_DISTANCE = "chordGenerator.sound.scale.tone.distance"; //$NON-NLS-1$
	String SOUND_BLOCK_PLAY_PATTERN = "chordGenerator.sound.scale.play.pattern"; //$NON-NLS-1$
	String SOUND_FRETBOARD_VIEW_TONE_LENGTH = "chordGenerator.sound.fretboard.view.tone.length"; //$NON-NLS-1$
	String SOUND_KEYBOARD_VIEW_TONE_LENGTH = "chordGenerator.sound.keyboard.view.tone.length"; //$NON-NLS-1$
	String SOUND_VOLUME = "chordGenerator.sound.volume"; //$NON-NLS-1$

	/* --- perspectives --- */

	String PERSPECTIVES_BINDING_ELEMENT_EDITING = "chordGenerator.perspectives.binding.element.editing"; //$NON-NLS-1$
	String PERSPECTIVES_BINDING_CHORD_GENERATION = "chordGenerator.perspectives.binding.chord.generation"; //$NON-NLS-1$
	String PERSPECTIVES_BINDING_FIND_CHORDS = "chordGenerator.perspectives.binding.find.chord"; //$NON-NLS-1$
	String PERSPECTIVES_BINDING_FIND_SCALES = "chordGenerator.perspectives.binding.find.scale"; //$NON-NLS-1$
	String PERSPECTIVES_CLEAR_SELECTION = "chordGenerator.perspectives.clear.selection"; //$NON-NLS-1$

	/* --- export --- */

	String CLIPBOARD_EXPORT_FILE_EXTENSION = "chordGenerator.clipboard.export.file.extension"; //$NON-NLS-1$
	String CLIPBOARD_EXPORT_FIRST_VIEW = "chordGenerator.clipboard.export.first.view"; //$NON-NLS-1$
	String CLIPBOARD_EXPORT_SECOND_VIEW = "chordGenerator.clipboard.export.second.view"; //$NON-NLS-1$
	String CLIPBOARD_EXPORT_THIRD_VIEW = "chordGenerator.clipboard.export.third.view"; //$NON-NLS-1$
	String CLIPBOARD_EXPORT_REVERSE_ORDER = "chordGenerator.clipboard.export.reverse.order"; //$NON-NLS-1$

	/* --- export - filename --- */

	String EXPORT_FILENAME_SUGGESTION = "chordGenerator.export.filename.suggestion"; //$NON-NLS-1$
	String EXPORT_FILENAME_ADD_VIEW_BOX = "chordGenerator.export.filename.add.view.box"; //$NON-NLS-1$
	String EXPORT_FILENAME_ADD_VIEW_TAB = "chordGenerator.export.filename.add.view.tab"; //$NON-NLS-1$
	String EXPORT_FILENAME_ADD_VIEW_NOTES = "chordGenerator.export.filename.add.view.notes"; //$NON-NLS-1$
	String EXPORT_FILENAME_ADD_VIEW_FRETBOARD = "chordGenerator.export.filename.add.view.fretboard"; //$NON-NLS-1$
	String EXPORT_FILENAME_ADD_VIEW_KEYBOARD = "chordGenerator.export.filename.add.view.keyboard"; //$NON-NLS-1$
	String EXPORT_FILENAME_ADD_VIEW_IN_FRONT = "chordGenerator.export.filename.add.view.in.front"; //$NON-NLS-1$
	String EXPORT_FILENAME_REPLACE_WHITE_SPACE = "chordGenerator.export.filename.replace.white.space"; //$NON-NLS-1$
	String EXPORT_FILENAME_REPLACEMENT_FOR_WHITE_SPACE = "chordGenerator.export.filename.replacement.for.white.space"; //$NON-NLS-1$
	String EXPORT_FILENAME_REPLACEMENT_FOR_ILLEGAL_CHARACTER = "chordGenerator.export.filename.replacement.for.illegal.character"; //$NON-NLS-1$
	String EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT = "chordGenerator.export.filename.replacement.for.logical.unit"; //$NON-NLS-1$

	/* --- instruments view --- */

	String INSTRUMENTS_VIEW_LINKED_WITH_EDITOR = "chordGenerator.preferences.views.instruments.view.linked.with.editor"; //$NON-NLS-1$

	/* --- chords view --- */

	String CHORDS_VIEW_LINKED_WITH_EDITOR = "chordGenerator.preferences.views.chords.view.linked.with.editor"; //$NON-NLS-1$
	String CHORDS_VIEW_SHOW_INTERVAL_SECTION = "chordGenerator.preferences.views.chords.view.show.intervals.section"; //$NON-NLS-1$
	String CHORDS_VIEW_ORIENTATION = "chordGenerator.preferences.views.chords.view.orientation"; //$NON-NLS-1$
	String CHORDS_VIEW_CHORD_SECTION_WEIGHT = "chordGenerator.preferences.views.chords.view.chord.section.weight"; //$NON-NLS-1$
	String CHORDS_VIEW_INTERVAL_SECTION_WEIGHT = "chordGenerator.preferences.views.chords.view.interval.section.weight"; //$NON-NLS-1$

	/* --- scales view --- */

	String SCALES_VIEW_LINKED_WITH_EDITOR = "chordGenerator.preferences.views.scales.view.linked.with.editor"; //$NON-NLS-1$
	String SCALES_VIEW_SHOW_INTERVAL_SECTION = "chordGenerator.preferences.views.scales.view.show.intervals.section"; //$NON-NLS-1$
	String SCALES_VIEW_ORIENTATION = "chordGenerator.preferences.views.scales.view.orientation"; //$NON-NLS-1$
	String SCALES_VIEW_SCALE_SECTION_WEIGHT = "chordGenerator.preferences.views.scales.view.scale.section.weight"; //$NON-NLS-1$
	String SCALES_VIEW_INTERVAL_SECTION_WEIGHT = "chordGenerator.preferences.views.scales.view.interval.section.weight"; //$NON-NLS-1$

	/* --- chord generation view --- */

	String CHORD_GENERATION_VIEW_ROOT_NOTE = "chordGenerator.preferences.views.chord.generation.view.fundamental.tone"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_CHORD = "chordGenerator.preferences.views.chord.generation.view.chord"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_BASS_TONE = "chordGenerator.preferences.views.chord.generation.view.bass.tone"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_LEAD_TONE = "chordGenerator.preferences.views.chord.generation.view.lead.tone"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_MIN_LEVEL = "chordGenerator.preferences.views.chord.generation.view.start.level"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_MAX_LEVEL = "chordGenerator.preferences.views.chord.generation.view.end.level"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_MIN_STRING = "chordGenerator.preferences.views.chord.generation.view.start.string"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_MAX_STRING = "chordGenerator.preferences.views.chord.generation.view.end.string"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_MIN_FRET = "chordGenerator.preferences.views.chord.generation.view.start.fret"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_MAX_FRET = "chordGenerator.preferences.views.chord.generation.view.end.fret"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_GRIP_RANGE = "chordGenerator.preferences.views.chord.generation.view.grip.range"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_GRIP_RANGE_UNIT = "chordGenerator.preferences.views.chord.generation.view.grip.range.unit"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_MAX_SINGLE_TONE_NUMBERS = "chordGenerator.preferences.views.chord.generation.view.max.single.tone.numbers"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_EMPTY_STRINGS = "chordGenerator.preferences.views.chord.generation.view.empty.strings"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_EMPTY_STRINGS_TEMP = "chordGenerator.preferences.views.chord.generation.view.empty.strings.temp"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_MUTED_STRINGS = "chordGenerator.preferences.views.chord.generation.view.damped.strings"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_ONLY_PACKED = "chordGenerator.preferences.views.chord.generation.view.only.packed"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_ONLY_SINGLE_MUTED_STRINGS = "chordGenerator.preferences.views.chord.generation.view.only.single.damped.strings"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_DOUBLED_TONES = "chordGenerator.preferences.views.chord.generation.view.doubled.tones"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_ONLY_ASCENDING = "chordGenerator.preferences.views.chord.generation.view.only.ascending"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_WITHOUT_1ST = "chordGenerator.preferences.views.chord.generation.view.without.1st"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_WITHOUT_3RD = "chordGenerator.preferences.views.chord.generation.view.without.3rd"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_WITHOUT_5TH = "chordGenerator.preferences.views.chord.generation.view.without.5th"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_BASS_TONE = "chordGenerator.preferences.views.chord.generation.view.is.advanced.bass.tone"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_LEAD_TONE = "chordGenerator.preferences.views.chord.generation.view.is.advanced.lead.tone"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_LEVEL = "chordGenerator.preferences.views.chord.generation.view.is.advanced.level"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_STRING_RANGE = "chordGenerator.preferences.views.chord.generation.view.is.advanced.string.range"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_FRET_RANGE = "chordGenerator.preferences.views.chord.generation.view.is.advanced.fret.range"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_GRIP_RANGE = "chordGenerator.preferences.views.chord.generation.view.is.advanced.grip.range"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_MAX_SINGLE_TONE_NUMBER = "chordGenerator.preferences.views.chord.generation.view.is.advanced.max.single.tone.number"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_EMPTY_MUTED_STRINGS = "chordGenerator.preferences.views.chord.generation.view.is.advanced.empty.damped.strings"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_MUTED_STRINGS_INFO = "chordGenerator.preferences.views.chord.generation.view.is.advanced.damped.strings.info"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_TONES_INFO = "chordGenerator.preferences.views.chord.generation.view.is.advanced.tones.info"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_EXCLUDED_INTERVALS = "chordGenerator.preferences.views.chord.generation.view.is.advanced.without.row"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_EXCLUDED_INTERVALS_COMPOSITE_EXPANDED = "chordGenerator.preferences.views.chord.generation.view.is.without.row.expanded"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_SECTION_EXPANDED = "chordGenerator.preferences.views.chord.generation.view.is.advanced.section.expanded"; //$NON-NLS-1$
	String CHORD_GENERATION_VIEW_IS_ADVANCED_SECTION_CHANGED = "chordGenerator.preferences.views.chord.generation.view.is.advanced.section.changed.hack"; //$NON-NLS-1$

	/* --- chord results view --- */

	String CHORD_RESULTS_VIEW_ENTRIES_PER_PAGE = "chordGenerator.preferences.views.chord.results.view.entries.per.page"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_COMPACT_MODE = "chordGenerator.preferences.views.chord.results.view.compact.mode"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_IN_GRAY = "chordGenerator.preferences.views.chord.results.view.show.muted.strings.in.gray"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_COLUMN_SORTING_ORDER = "chordGenerator.preferences.views.chord.results.view.sorting.order"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_COLUMN_SORTING_DIRECTIONS = "chordGenerator.preferences.views.chord.results.view.sorting.directions"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_LEVEL_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.level.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_BASS_TONE_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.bass.tone.and.interval.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_LEAD_TONE_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.lead.tone.and.interval.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_NOTES_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.notes.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_INTERVALS_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.intervals.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_FRETS_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.frets.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_MIN_STRING_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.min.string.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_MAX_STRING_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.max.string.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_STRING_SPAN_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.string.span.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_MIN_FRET_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.min.fret.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_MAX_FRET_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.max.fret.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_FRET_SPAN_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.fret.span.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_DISTANCE_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.distance.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_EMPTY_STRINGS_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.empty.strings.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.damped.strings.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SHOW_DOUBLED_TONES_COLUMN = "chordGenerator.preferences.views.chord.results.view.show.doubled.tones.column"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_SELECTION_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.selection.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_CHORD_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.chord.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_LEVEL_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.level.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_BASS_TONE_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.absolute.bass.tone.and.interval.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_LEAD_TONE_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.absolute.lead.tone.and.interval.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_NOTES_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.notes.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_INTERVALS_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.intervals.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_FRETS_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.frets.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_MIN_STRING_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.min.string.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_MAX_STRING_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.max.string.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_STRING_SPAN_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.string.span.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_MIN_FRET_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.min.fret.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_MAX_FRET_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.max.fret.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_FRET_SPAN_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.fret.span.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_DISTANCE_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.distance.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_EMPTY_STRINGS_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.empty.strings.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_MUTED_STRINGS_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.damped.strings.column.width"; //$NON-NLS-1$
	String CHORD_RESULTS_VIEW_DOUBLED_TONES_COLUMN_WIDTH = "chordGenerator.preferences.views.chord.results.view.doubled.tones.column.width"; //$NON-NLS-1$

	/* --- fretboard view --- */

	String FRETBOARD_VIEW_MODE = "chordGenerator.preferences.views.fretboard.view.default.mode"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_BLOCK_NAVIGATION_INFO = "chordGenerator.preferences.views.fretboard.view.show.scale.block.navigation.info"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_MUTED_STRINGS = "chordGenerator.preferences.views.fretboard.view.show.muted.strings"; //$NON-NLS-1$
	String FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE = "chordGenerator.preferences.views.fretboard.view.highlight.root.note"; //$NON-NLS-1$
	String FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR = "chordGenerator.preferences.views.fretboard.view.highlight.root.note.with.color"; //$NON-NLS-1$
	String FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE = "chordGenerator.preferences.views.fretboard.view.highlight.root.note.with.shape"; //$NON-NLS-1$
	String FRETBOARD_VIEW_POINTS_BACKGROUND = "chordGenerator.preferences.views.fretboard.view.points.background"; //$NON-NLS-1$
	String FRETBOARD_VIEW_FRAME_FINGERING = "chordGenerator.preferences.views.fretboard.view.frame.fingering"; //$NON-NLS-1$
	String FRETBOARD_VIEW_FINGERING_BACKGROUND = "chordGenerator.preferences.views.fretboard.view.fingering.background"; //$NON-NLS-1$
	String FRETBOARD_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE = "chordGenerator.preferences.views.fretboard.view.empty.strings.background.white"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_BARRE = "chordGenerator.preferences.views.fretboard.view.draw.barre"; //$NON-NLS-1$
	String FRETBOARD_VIEW_BARRE_MODE = "chordGenerator.preferences.views.fretboard.view.barre.mode"; //$NON-NLS-1$
	String FRETBOARD_VIEW_BARRE_LINE_WIDTH = "chordGenerator.preferences.views.fretboard.view.barre.line.width"; //$NON-NLS-1$
	String FRETBOARD_VIEW_BARRE_BAR_BACKGROUND = "chordGenerator.preferences.views.fretboard.view.barre.bar.background"; //$NON-NLS-1$
	String FRETBOARD_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE = "chordGenerator.preferences.views.fretboard.view.barre.bar.show.fingering"; //$NON-NLS-1$
	String FRETBOARD_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER = "chordGenerator.preferences.views.fretboard.view.barre.bar.show.fingering.single.mode"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_BLOCK_PRESENTATION = "chordGenerator.preferences.views.fretboard.view.show.block.presentation"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_FRETBOARD = "chordGenerator.preferences.views.fretboard.view.show.unused.notes.on.empty.fretboard"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE = "chordGenerator.preferences.views.fretboard.view.show.unused.notes.for.griptable"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE = "chordGenerator.preferences.views.fretboard.view.show.unused.notes.for.chord.scale"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK = "chordGenerator.preferences.views.fretboard.view.show.unused.notes.for.block"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES = "chordGenerator.preferences.views.fretboard.view.show.unused.chord.notes"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES = "chordGenerator.preferences.views.fretboard.view.show.unused.block.notes"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK = "chordGenerator.preferences.views.fretboard.view.show.unused.notes.in.black"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE = "chordGenerator.preferences.views.fretboard.view.show.empty.strings.twice"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_FRAME = "chordGenerator.preferences.views.fretboard.view.show.empty.strings.frame"; //$NON-NLS-1$
	String FRETBOARD_VIEW_BACKGROUND_MODE = "chordGenerator.preferences.views.fretboard.view.background.mode"; //$NON-NLS-1$
	String FRETBOARD_VIEW_BACKGROUND_INDEX = "chordGenerator.preferences.views.fretboard.view.background.index"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_INLAYS = "chordGenerator.preferences.views.fretboard.view.show.intarsia"; //$NON-NLS-1$
	String FRETBOARD_VIEW_INLAYS_GRAY_COLOR = "chordGenerator.preferences.views.fretboard.view.inlays.gray.color"; //$NON-NLS-1$
	String FRETBOARD_VIEW_INLAYS_POSITION = "chordGenerator.preferences.views.fretboard.view.inlays.position"; //$NON-NLS-1$
	String FRETBOARD_VIEW_INLAYS_SHAPE = "chordGenerator.preferences.views.fretboard.view.inlays.mode"; //$NON-NLS-1$
	String FRETBOARD_VIEW_SHOW_FRET_NUMBERS = "chordGenerator.preferences.views.fretboard.view.show.fret.numbers"; //$NON-NLS-1$
	String FRETBOARD_VIEW_FRET_NUMBERS_GRAY_COLOR = "chordGenerator.preferences.views.fretboard.view.fret.numbers.gray.color"; //$NON-NLS-1$
	String FRETBOARD_VIEW_FRET_NUMBERS_REDUCED_MODE = "chordGenerator.preferences.views.fretboard.view.fret.numbers.reduced.mode"; //$NON-NLS-1$
	String FRETBOARD_VIEW_FRET_NUMBERS_POSITION = "chordGenerator.preferences.views.fretboard.view.fret.numbers.position"; //$NON-NLS-1$
	String FRETBOARD_VIEW_FRET_NUMBERS_NUMERALS = "chordGenerator.preferences.views.fretboard.view.fret.numbers.mode"; //$NON-NLS-1$
	String FRETBOARD_VIEW_EXPORT_HEIGHT = "chordGenerator.preferences.views.fretboard.view.export.height"; //$NON-NLS-1$
	String FRETBOARD_VIEW_EXPORT_LIVE_SIZE = "chordGenerator.preferences.views.fretboard.view.export.live.size"; //$NON-NLS-1$

	/* --- blocks --- */

	String FRET_BLOCK_RANGE = "chordGenerator.preferences.scale.blocks.fret.block.range"; //$NON-NLS-1$
	String FRET_BLOCK_USE_EMPTY_STRINGS = "chordGenerator.preferences.scale.blocks.fret.block.use.empty.strings"; //$NON-NLS-1$
	String ADVANCED_FRET_BLOCK_RANGE = "chordGenerator.preferences.scale.blocks.advanced.fret.block.range"; //$NON-NLS-1$
	String ADVANCED_FRET_BLOCK_STRING_RANGE_DECREASE = "chordGenerator.preferences.scale.blocks.advanced.fret.block.string.range"; //$NON-NLS-1$
	String ADVANCED_FRET_BLOCK_USE_EMPTY_STRINGS = "chordGenerator.preferences.scale.blocks.advanced.fret.block.use.empty.strings"; //$NON-NLS-1$
	String OCTAVE_BLOCK_ONLY_ROOT_NOTES = "chordGenerator.preferences.scale.blocks.octave.block.only.fundamental.tones"; //$NON-NLS-1$

	/* --- keyboard view --- */

	String KEYBOARD_VIEW_MODE = "chordGenerator.preferences.views.keyboard.view.default.mode"; //$NON-NLS-1$
	String KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE = "chordGenerator.preferences.views.keyboard.view.highlight.root.note"; //$NON-NLS-1$
	String KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE = "chordGenerator.preferences.views.keyboard.view.highlight.root.note.with.shape"; //$NON-NLS-1$
	String KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR = "chordGenerator.preferences.views.keyboard.view.highlight.root.note.with.color"; //$NON-NLS-1$
	String KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID = "chordGenerator.preferences.views.keyboard.view.highlight.root.note.color.id"; //$NON-NLS-1$
	String KEYBOARD_VIEW_FRAME_NOTES_INTERVALS = "chordGenerator.preferences.views.keyboard.view.frame.notes.intervals"; //$NON-NLS-1$
	String KEYBOARD_VIEW_NOTES_INTERVALS_BACKGROUND = "chordGenerator.preferences.views.keyboard.view.notes.intervals.background"; //$NON-NLS-1$
	String KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_KEYBOARD = "chordGenerator.preferences.views.keyboard.view.show.unused.notes.on.empty.keyboard"; //$NON-NLS-1$
	String KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE = "chordGenerator.preferences.views.keyboard.view.show.unused.notes.for.griptable"; //$NON-NLS-1$
	String KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE = "chordGenerator.preferences.views.keyboard.view.show.unused.notes.for.chord.scale"; //$NON-NLS-1$
	String KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK = "chordGenerator.preferences.views.keyboard.view.show.unused.notes.for.block"; //$NON-NLS-1$
	String KEYBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES = "chordGenerator.preferences.views.keyboard.view.show.unused.chord.notes"; //$NON-NLS-1$
	String KEYBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES = "chordGenerator.preferences.views.keyboard.view.show.unused.block.notes"; //$NON-NLS-1$
	String KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK = "chordGenerator.preferences.views.keyboard.view.show.unused.notes.in.black"; //$NON-NLS-1$
	String KEYBOARD_VIEW_BLOCK_PRESENTATION = "chordGenerator.preferences.views.keyboard.view.block.presentation"; //$NON-NLS-1$
	String KEYBOARD_VIEW_TONE_RANGE_MODE = "chordGenerator.preferences.views.keyboard.view.tone.range.mode"; //$NON-NLS-1$
	String KEYBOARD_VIEW_TONE_RANGE_START_TONE = "chordGenerator.preferences.views.keyboard.view.tone.range.start.tone"; //$NON-NLS-1$
	String KEYBOARD_VIEW_TONE_RANGE_END_TONE = "chordGenerator.preferences.views.keyboard.view.tone.range.end.tone"; //$NON-NLS-1$
	String KEYBOARD_VIEW_KEY_SIZE = "chordGenerator.preferences.views.keyboard.view.key.size"; //$NON-NLS-1$
	String KEYBOARD_VIEW_EXPORT_HEIGHT = "chordGenerator.preferences.views.keyboard.view.export.height"; //$NON-NLS-1$
	String KEYBOARD_VIEW_EXPORT_LIVE_SIZE = "chordGenerator.preferences.views.keyboard.view.export.live.size"; //$NON-NLS-1$

	/* --- tab view --- */

	String TAB_VIEW_SHOW_MUTED_STRINGS = "chordGenerator.preferences.views.tab.view.show.damped.strings"; //$NON-NLS-1$
	String TAB_VIEW_DRAW_DOUBLED_STRINGS = "chordGenerator.preferences.views.tab.view.draw.doubled.strings"; //$NON-NLS-1$
	String TAB_VIEW_HIGHLIGHT_ROOT_NOTE = "chordGenerator.preferences.views.tab.view.highlight.root.note"; //$NON-NLS-1$
	String TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_BOLD_FONT = "chordGenerator.preferences.views.tab.view.highlight.root.note.with.bold.font"; //$NON-NLS-1$
	String TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_FRAME = "chordGenerator.preferences.views.tab.view.highlight.root.note.with.frame"; //$NON-NLS-1$
	String TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR = "chordGenerator.preferences.views.tab.view.highlight.root.note.with.color"; //$NON-NLS-1$
	String TAB_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID = "chordGenerator.preferences.views.tab.view.highlight.root.note.color.id"; //$NON-NLS-1$
	String TAB_VIEW_EXPORT_HEIGHT = "chordGenerator.preferences.views.tab.view.export.height"; //$NON-NLS-1$
	String TAB_VIEW_EXPORT_LIVE_SIZE = "chordGenerator.preferences.views.tab.view.export.live.size"; //$NON-NLS-1$

	/* --- scale results view --- */

	String SCALE_RESULTS_VIEW_MARK_SEARCH = "chordGenerator.preferences.views.scale.results.view.mark.search"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_ENTRIES_PER_PAGE = "chordGenerator.preferences.views.scale.results.view.entries.per.page"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_COMPACT_MODE = "chordGenerator.preferences.views.scale.results.view.compact.mode"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_SHOW_SEPARATORS_IN_GRAY = "chordGenerator.preferences.views.scale.results.view.show.separators.in.gray"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_COLUMN_SORTING_ORDER = "chordGenerator.preferences.views.scale.results.view.sorting.order"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_COLUMN_SORTING_DIRECTIONS = "chordGenerator.preferences.views.scale.results.view.column.sorting.directions"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_SHOW_INTERVALS_COLUMN = "chordGenerator.preferences.views.scale.results.view.show.intervals.column"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_SHOW_NOTES_COLUMN = "chordGenerator.preferences.views.scale.results.view.show.notes.column"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_SHOW_ROOT_NOTE_COLUMN = "chordGenerator.preferences.views.scale.results.view.show.fundamental.tone.column"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_SHOW_COVERAGE_COLUMN = "chordGenerator.preferences.views.scale.results.view.show.coverage.column"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_SCALE_COLUMN_WIDTH = "chordGenerator.preferences.views.scale.results.view.scale.column.width"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_INTERVALS_COLUMN_WIDTH = "chordGenerator.preferences.views.scale.results.view.intervals.column.width"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_NOTES_COLUMN_WIDTH = "chordGenerator.preferences.views.scale.results.view.notes.column.width"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_ROOT_NOTE_COLUMN_WIDTH = "chordGenerator.preferences.views.scale.results.view.fundamental.tone.column.width"; //$NON-NLS-1$
	String SCALE_RESULTS_VIEW_COVERAGE_COLUMN_WIDTH = "chordGenerator.preferences.views.scale.results.view.coverage.column.width"; //$NON-NLS-1$

	/* --- scale finder view --- */

	String SCALE_FINDER_VIEW_ORIENTATION = "chordGenerator.preferences.views.scale.finder.view.orientation"; //$NON-NLS-1$
	String SCALE_FINDER_VIEW_CLEAR_INPUT_AFTER_CALCULATION = "chordGenerator.preferences.views.scale.finder.view.clear.input.after.installation"; //$NON-NLS-1$

	/* --- box view --- */

	String BOX_VIEW_PRESENTATION_MODE = "chordGenerator.preferences.views.box.view.presentation.mode"; //$NON-NLS-1$
	String BOX_VIEW_FRAME_MIN_FRET_COUNT = "chordGenerator.preferences.views.box.view.frame.min.fret.count"; //$NON-NLS-1$
	String BOX_VIEW_FRAME_MAX_UNASSIGNED_FRET_COUNT = "chordGenerator.preferences.views.box.view.frame.max.unassigned.fret.count"; //$NON-NLS-1$
	String BOX_VIEW_FRAME_HIGHLIGHT_NUT = "chordGenerator.preferences.views.box.view.frame.highlight.nut"; //$NON-NLS-1$
	String BOX_VIEW_FRAME_HIGHLIGHT_OUTER_FRETS = "chordGenerator.preferences.views.box.view.frame.outer.frets"; //$NON-NLS-1$
	String BOX_VIEW_FRAME_DRAW_DOUBLED_STRINGS = "chordGenerator.preferences.views.box.view.frame.draw.doubled.strings"; //$NON-NLS-1$
	String BOX_VIEW_FRAME_DRAW_FRETLESS_FRETS_DOTTED = "chordGenerator.preferences.views.box.view.frame.draw.fretless.frets.dotted"; //$NON-NLS-1$
	String BOX_VIEW_FRAME_SMALL_FRETS = "chordGenerator.preferences.views.box.view.frame.compact"; //$NON-NLS-1$
	String BOX_VIEW_FRAME_GRAY_COLOR = "chordGenerator.preferences.views.box.view.frame.gray.color"; //$NON-NLS-1$
	String BOX_VIEW_FRET_NUMBERS_MODE = "chordGenerator.preferences.views.box.view.fret.numbers.mode"; //$NON-NLS-1$
	String BOX_VIEW_FRET_NUMBERS_HORIZONTAL_POSITION = "chordGenerator.preferences.views.box.view.fret.numbers.horizontal.position"; //$NON-NLS-1$
	String BOX_VIEW_FRET_NUMBERS_VERTICAL_POSITION = "chordGenerator.preferences.views.box.view.fret.numbers.vertical.position"; //$NON-NLS-1$
	String BOX_VIEW_FRET_NUMBERS_PLACE_AT_FIRST_FINGER = "chordGenerator.preferences.views.box.view.fret.numbers.locate.at.first.finger"; //$NON-NLS-1$
	String BOX_VIEW_FRET_NUMBERS_VISIBLE_FOR_FIRST_FRET = "chordGenerator.preferences.views.box.view.fret.numbers.visible.for.first.fret"; //$NON-NLS-1$
	String BOX_VIEW_FRET_NUMBERS_FRAMED = "chordGenerator.preferences.views.box.view.fret.numbers.framed"; //$NON-NLS-1$
	String BOX_VIEW_FRET_NUMBERS_GRAY_COLOR = "chordGenerator.preferences.views.box.view.fret.numbers.gray.color"; //$NON-NLS-1$
	String BOX_VIEW_SHOW_MUTED_STRINGS = "chordGenerator.preferences.views.box.view.fingering.show.damped.strings"; //$NON-NLS-1$
	String BOX_VIEW_HIGHLIGHT_ROOT_NOTE = "chordGenerator.preferences.views.box.view.highlight.root.note"; //$NON-NLS-1$
	String BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR = "chordGenerator.preferences.views.box.view.highlight.root.note.with.color"; //$NON-NLS-1$
	String BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE = "chordGenerator.preferences.views.box.view.highlight.root.note.with.shape"; //$NON-NLS-1$
	String BOX_VIEW_POINTS_SIZE = "chordGenerator.preferences.views.box.view.points.size"; //$NON-NLS-1$
	String BOX_VIEW_POINTS_BACKGROUND = "chordGenerator.preferences.views.box.view.points.background"; //$NON-NLS-1$
	String BOX_VIEW_SHOW_FINGERING_OUTSIDE_BOX = "chordGenerator.preferences.views.box.view.fingering.show.outside.box"; //$NON-NLS-1$
	String BOX_VIEW_SHOW_NOTES_OUTSIDE_BOX = "chordGenerator.preferences.views.box.view.notes.show.outside.box"; //$NON-NLS-1$
	String BOX_VIEW_SHOW_INTERVALS_OUTSIDE_BOX = "chordGenerator.preferences.views.box.view.intervals.show.outside.box"; //$NON-NLS-1$
	String BOX_VIEW_FRAME_INSIDE = "chordGenerator.preferences.views.box.view.fingering.encircled"; //$NON-NLS-1$
	String BOX_VIEW_BACKGROUND_INSIDE = "chordGenerator.preferences.views.box.view.fingering.background"; //$NON-NLS-1$
	String BOX_VIEW_SHOW_BARRE = "chordGenerator.preferences.views.box.view.draw.barre"; //$NON-NLS-1$
	String BOX_VIEW_BARRE_MODE = "chordGenerator.preferences.views.box.view.barre.mode"; //$NON-NLS-1$
	String BOX_VIEW_BARRE_LINE_WIDTH = "chordGenerator.preferences.views.box.view.barre.line.width"; //$NON-NLS-1$
	String BOX_VIEW_BARRE_BAR_BACKGROUND = "chordGenerator.preferences.views.box.view.barre.bar.background"; //$NON-NLS-1$
	String BOX_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE = "chordGenerator.preferences.views.box.view.barre.bar.show.fingering"; //$NON-NLS-1$
	String BOX_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER = "chordGenerator.preferences.views.box.view.barre.bar.show.fingering.single.mode"; //$NON-NLS-1$
	String BOX_VIEW_EMPTY_AND_MUTED_STRINGS_CLOSE_TO_FRAME = "chordGenerator.preferences.views.box.view.empty.and.muted.strings.close.to.frame"; //$NON-NLS-1$
	String BOX_VIEW_EMPTY_AND_MUTED_STRINGS_SIZE = "chordGenerator.preferences.views.box.view.empty.and.muted.strings.size"; //$NON-NLS-1$
	String BOX_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE = "chordGenerator.preferences.views.box.view.empty.strings.background.white"; //$NON-NLS-1$
	String BOX_VIEW_BACKGROUND_OUTSIDE = "chordGenerator.preferences.views.box.view.notes.intervals.background"; //$NON-NLS-1$
	String BOX_VIEW_FRAME_OUTSIDE = "chordGenerator.preferences.views.box.view.notes.intervals.framed"; //$NON-NLS-1$
	String BOX_VIEW_SHOW_FINGERING = "chordGenerator.preferences.views.box.view.default.show.fingering"; //$NON-NLS-1$
	String BOX_VIEW_SHOW_NOTES = "chordGenerator.preferences.views.box.view.default.show.notes"; //$NON-NLS-1$
	String BOX_VIEW_SHOW_INTERVALS = "chordGenerator.preferences.views.box.view.default.show.intervals"; //$NON-NLS-1$
	String BOX_VIEW_EXPORT_HEIGHT = "chordGenerator.preferences.views.box.view.export.height"; //$NON-NLS-1$
	String BOX_VIEW_EXPORT_LIVE_SIZE = "chordGenerator.preferences.views.box.view.export.live.size"; //$NON-NLS-1$

	/* --- notes view --- */

	String NOTES_VIEW_CLEF = "chordGenerator.preferences.views.notes.view.clef"; //$NON-NLS-1$
	String NOTES_VIEW_DISPLAY_MODE_GRIPTABLES = "chordGenerator.preferences.views.notes.view.display.mode.griptables"; //$NON-NLS-1$
	String NOTES_VIEW_DISPLAY_MODE_CHORD_BLOCKS = "chordGenerator.preferences.views.notes.view.display.mode.chord.blocks"; //$NON-NLS-1$
	String NOTES_VIEW_DISPLAY_MODE_CHORD_SCHEMES = "chordGenerator.preferences.views.notes.view.display.mode.chord.schemes"; //$NON-NLS-1$
	String NOTES_VIEW_DISPLAY_MODE_SCALE_BLOCKS = "chordGenerator.preferences.views.notes.view.display.mode.scale.blocks"; //$NON-NLS-1$
	String NOTES_VIEW_DISPLAY_MODE_SCALES = "chordGenerator.preferences.views.notes.view.display.mode.scales"; //$NON-NLS-1$
	String NOTES_VIEW_SHOW_CLEF_ANNOTATION = "chordGenerator.preferences.views.notes.view.show.clef.annotation"; //$NON-NLS-1$
	String NOTES_VIEW_USE_MAX_WIDTH = "chordGenerator.preferences.views.notes.view.use.max.width"; //$NON-NLS-1$
	String NOTES_VIEW_FLEXIBLE_SPACING = "chordGenerator.preferences.views.notes.view.flexible.spacing"; //$NON-NLS-1$
	String NOTES_VIEW_FILTER_CLEF_NOT_WHOLE_STAFF_USED = "chordGenerator.preferences.views.notes.view.filter.clef.not.whole.staff.used"; //$NON-NLS-1$
	String NOTES_VIEW_FILTER_CLEF_NO_NOTE_ON_STAFF = "chordGenerator.preferences.views.notes.view.filter.clef.no.note.on.staff"; //$NON-NLS-1$
	String NOTES_VIEW_FILTER_CLEF_CHIAVETTE = "chordGenerator.preferences.views.notes.view.filter.clef.chiavette"; //$NON-NLS-1$
	String NOTES_VIEW_FALLBACK_CLEF = "chordGenerator.preferences.views.notes.view.fallback.clef"; //$NON-NLS-1$
	String NOTES_VIEW_SHOW_ONLY_CHORD_BLOCKS = "chordGenerator.preferences.views.notes.view.show.only.chord.blocks"; //$NON-NLS-1$
	String NOTES_VIEW_SHOW_ONLY_SCALE_BLOCKS = "chordGenerator.preferences.views.notes.view.show.only.scale.blocks"; //$NON-NLS-1$
	String NOTES_VIEW_OPEN_NOTE_REPRESENTATION = "chordGenerator.preferences.views.notes.view.open.note.representation"; //$NON-NLS-1$
	String NOTES_VIEW_HIGHLIGHT_ROOT_NOTE = "chordGenerator.preferences.views.notes.view.highlight.root.note"; //$NON-NLS-1$
	String NOTES_VIEW_EXPORT_HEIGHT = "chordGenerator.preferences.views.notes.view.export.height"; //$NON-NLS-1$
	String NOTES_VIEW_EXPORT_LIVE_SIZE = "chordGenerator.preferences.views.notes.view.export.live.size"; //$NON-NLS-1$

	/* --- views --- */

	String VIEWS_SHOW_INFO_INPUT = "chordGenerator.preferences.views.show.info.input"; //$NON-NLS-1$
	String VIEWS_SHOW_INFO_SEARCH_MODE = "chordGenerator.preferences.views.show.info.editing.mode"; //$NON-NLS-1$
	String VIEWS_SEARCH_MODE_ENABLE_DOUBLE_CLICK = "chordGenerator.preferences.views.editing.mode.enable.double.click"; //$NON-NLS-1$
	String VIEWS_SEARCH_MODE_ENABLE_ESC_KEY = "chordGenerator.preferences.views.editing.mode.enable.esc.key"; //$NON-NLS-1$
	String VIEWS_SEARCH_MODE_ENABLE_FAST_EDITING = "chordGenerator.preferences.views.editing.mode.enable.fast.editing"; //$NON-NLS-1$
	String VIEWS_SEARCH_MODE_FAST_EDITING_DEEP_TO_HIGH = "chordGenerator.preferences.views.editing.mode.fast.editing.deep.to.high"; //$NON-NLS-1$
	String VIEWS_SEARCH_MODE_USE_POINTS_MODE = "chordGenerator.preferences.views.editing.mode.use.points.mode"; //$NON-NLS-1$;
	String VIEWS_SEARCH_MODE_USE_ALWAYS_POINTS_MODE = "chordGenerator.preferences.views.editing.mode.use.always.points.mode"; //$NON-NLS-1$;
	String VIEWS_SEARCH_MODE_RELATIVE_NOTES_MODE_KEY_ALT = "chordGenerator.preferences.views.editing.mode.relative.notes.mode.key.alt"; //$NON-NLS-1$;
}
