/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core;

/**
 * This class stores all necessary Constants of the project.
 */
public interface Constants {

	/* --- names --- */

	int INTERVALS_NUMBER = 12;
	int MAX_NOTES_VALUE = 11;
	int MAX_NOTES_LEVEL = 8;

	String[][] INTERVAL_NAMES_DEFAULT = { { "1", "bb2", "", "#7", "8", "bb9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "#1", "b2", "", "##7", "#8", "b9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "##1", "2", "bb3", "bb10", "##8", "9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "bb4", "#2", "b3", "b10", "bb11", "#9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "b4", "##2", "3", "10", "b11", "##9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "4", "bb5", "#3", "#10", "11", "bb12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "#4", "b5", "##3", "##10", "#11", "b12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "##4", "5", "bb6", "bb13", "##11", "12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "", "#5", "b6", "b13", "", "#12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "bb7", "##5", "6", "13", "bb14", "##12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "b7", "bb8", "#6", "#13", "b14", "bb15" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "7", "b8", "##6", "##13", "14", "b15" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	String[][] INTERVAL_NAMES_ENGLISH = { { "P1", "d2", "", "A7", "P8", "d9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "A1", "m2", "", "DA7", "A8", "m9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "DA1", "M2", "d3", "d10", "DA8", "M9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "Dd4", "A2", "m3", "m10", "Dd11", "A9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "d4", "DA2", "M3", "M10", "d11", "DA9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "P4", "Dd5", "A3", "A10", "P11", "Dd12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "A4", "d5", "DA3", "DA10", "A11", "d12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "DA4", "P5", "d6", "d13", "DA11", "P12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "", "A5", "m6", "m13", "", "A12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "d7", "DA5", "M6", "M13", "d14", "DA12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "m7", "Dd8", "A6", "A13", "m14", "Dd15" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "M7", "d8", "DA6", "DA13", "M14", "d15" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	String[][] INTERVAL_NAMES_SPANISH = { { "1J", "2d", "", "7A", "8J", "9d" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "1A", "2m", "", "7AA", "8A", "9m" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "1AA", "2M", "3d", "10d", "8AA", "9M" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "4dd", "2A", "3m", "10m", "11dd", "9A" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "4d", "2AA", "3M", "10M", "11d", "9AA" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "4J", "5dd", "3A", "10A", "11J", "12dd" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "4A", "5d", "3AA", "10AA", "11A", "12d" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "4AA", "5J", "6d", "13d", "11AA", "12J" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "", "5A", "6m", "13m", "", "12A" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "7d", "5AA", "6M", "13M", "14d", "12AA" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "7m", "8dd", "6A", "13A", "14m", "15dd" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "7M", "8d", "6AA", "13DA", "14M", "15d" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	String[][] INTERVAL_NAMES_GERMAN = { { "r1", "v2", "", "\u00fc7", "r8", "v9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "\u00fc1", "k2", "", "d\u00fc7", "\u00fc8", "k9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "d\u00fc1", "g2", "v3", "v10", "d\u00fc8", "g9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "dv4", "\u00fc2", "k3", "k10", "dv11", "\u00fc9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "v4", "d\u00fc2", "g3", "g10", "v11", "d\u00fc9" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "r4", "dv5", "\u00fc3", "\u00fc10", "r11", "dv12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "\u00fc4", "v5", "d\u00fc3", "d\u00fc10", "\u00fc11", "v12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "d\u00fc4", "r5", "v6", "v13", "d\u00fc11", "r12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "", "\u00fc5", "k6", "k13", "", "\u00fc12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "v7", "d\u00fc5", "g6", "g13", "v14", "d\u00fc12" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "k7", "dv8", "\u00fc6", "\u00fc13", "k14", "dv15" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			{ "g7", "v8", "d\u00fc6", "d\u00fc13", "g14", "v15" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	String BLANK_CHORD_NAME = "{blank}"; //$NON-NLS-1$

	int MIN_LEVEL = 0;
	int MAX_LEVEL = 4;

	int DEFAULT_MIDI_INSTRUMENT_NUMBER = 24;

	/* --- min, maxs & defaults --- */

	int DEFAULT_NOTE_LEVEL = 2;

	int MIN_STRING_SIZE = 2;
	int DEFAULT_STRING_SIZE = 6;
	int MAX_STRING_SIZE = 12;

	int MIN_FRET_NUMBER = 12;
	int DEFAULT_FRET_NUMBER = 12;
	int MAX_FRET_NUMBER = 24;
	int MIN_ACTIVE_FRET_NUMBER = 3;

	int MIN_FRET_GRIP_RANGE = 1;
	int DEFAULT_FRET_GRIP_RANGE = 3;
	int MAX_FRET_GRIP_RANGE = 6;

	/* --- preferences values --- */

	String NOTES_MODE_ONLY_CROSS = "notes.mode.only.cross"; //$NON-NLS-1$
	String NOTES_MODE_ONLY_B = "notes.mode.only.b"; //$NON-NLS-1$
	String NOTES_MODE_CROSS_AND_B = "notes.mode.cross.and.b"; //$NON-NLS-1$

	String H_NOTE_NAME_H = "H"; //$NON-NLS-1$
	String H_NOTE_NAME_B = "B"; //$NON-NLS-1$
	String B_NOTE_NAME_HB = "Hb"; //$NON-NLS-1$
	String B_NOTE_NAME_B = "B"; //$NON-NLS-1$
	String B_NOTE_NAME_BB = "Bb"; //$NON-NLS-1$

	String DOUBLE_UNDERSCORE = "__"; //$NON-NLS-1$
	String HYPHEN = "-"; //$NON-NLS-1$
	String UNDERSCORE = "_"; //$NON-NLS-1$
	String BLANK_SPACE = " "; //$NON-NLS-1$
	String BLANK_SPACE_AND_HYPHEN = " - "; //$NON-NLS-1$

	String EXCLUDED_INTERVALS_PREFIX_NO = "no"; //$NON-NLS-1$
	String EXCLUDED_INTERVALS_PREFIX_MINUS = "minus"; //$NON-NLS-1$
	String EXCLUDED_INTERVALS_PREFIX_HYPHEN = "-"; //$NON-NLS-1$
	String EXCLUDED_INTERVALS_PREFIX_OMIT = "omit"; //$NON-NLS-1$
	String EXCLUDED_INTERVALS_PREFIX_OHNE = "ohne"; //$NON-NLS-1$
	String EXCLUDED_INTERVALS_PREFIX_O_POINT = "o."; //$NON-NLS-1$
	String EXCLUDED_INTERVALS_PREFIX_O = "o"; //$NON-NLS-1$

	String BRACKETS_ROUND = "(...)"; //$NON-NLS-1$
	String BRACKETS_SQUARE = "[...]"; //$NON-NLS-1$
	String BRACKETS_CURLY = "{...}"; //$NON-NLS-1$
	String BRACKETS_ANGLE = "<...>"; //$NON-NLS-1$

	/* --- calculator restrictions --- */

	int CALCULATOR_RESTRICTION_NO = 0;
	int CALCULATOR_RESTRICTION_MAX_1_EXCLUDED_INTERVAL = 1;
	int CALCULATOR_RESTRICTION_MAX_2_EXCLUDED_INTERVALS = 2;

}
