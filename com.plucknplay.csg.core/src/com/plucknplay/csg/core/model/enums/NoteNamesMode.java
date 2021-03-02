/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.enums;

import java.util.HashMap;
import java.util.Map;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.util.NamesUtil;

public enum NoteNamesMode {

	DEFAULT(false, false, true), MODE_1(false, true, true), MODE_2(false, false, false), MODE_3(false, true, false), NUMBERED(
			true, false, false);

	public static final String SEPARATOR = "/"; //$NON-NLS-1$

	private static final String[] RELATIVE_NOTE_NAMES = {
			"C", "C#/Db", "D", "D#/Eb", "E", "F", "F#/Gb", "G", "G#/Ab", "A", "A#/Hb", "H" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$

	private static final String[] NUMBERS = {
			"\u2082", "\u2081", "", "", "\u00B9", "\u00B2", "\u00B3", "\u2074", "\u2075" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
	private static final String[] COMMATA = { ",,", ",", "", "", "'", "''", "'''", "''''", "'''''" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$

	private static String hName;
	private static String bName;

	private boolean absoluteNumbers;
	private boolean useNumbers;
	private boolean suffixForSubContra;

	private Map<String, String> namesMap;

	private NoteNamesMode(final boolean absoluteNumbers, final boolean useNumbers, final boolean suffixForSubContra) {
		this.absoluteNumbers = absoluteNumbers;
		this.useNumbers = useNumbers;
		this.suffixForSubContra = suffixForSubContra;
		namesMap = new HashMap<String, String>();
	}

	public static void setHName(final String name) {
		hName = name;
	}

	public static void setBName(final String name) {
		bName = Constants.B_NOTE_NAME_HB.equals(name) ? "A#/Hb" : Constants.B_NOTE_NAME_B.equals(name) ? "A#/B" : "A#/Bb"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public String getName(final int value) {
		if (value < 0 || value > Constants.MAX_NOTES_VALUE) {
			throw new IllegalArgumentException();
		}
		return value == 10 ? bName : value == 11 ? hName : RELATIVE_NOTE_NAMES[value];
	}

	public String getName(final int value, final int level) {
		if (level < 0 || level > Constants.MAX_NOTES_LEVEL) {
			throw new IllegalArgumentException();
		}

		String result = null;
		final String key = getKey(value, level);
		if (value < 10) {
			result = namesMap.get(key);
		}

		if (result == null) {
			result = getName(value);
			if (absoluteNumbers) {
				result = result + level;
				result = result.replaceAll(SEPARATOR, level + SEPARATOR);
			} else {
				if (level > 2) {
					result = result.toLowerCase();
				}
				final String addition = useNumbers ? NUMBERS[level] : COMMATA[level];
				if (level < 2 && suffixForSubContra) {
					result = addition + result;
					result = result.replaceAll(SEPARATOR, SEPARATOR + addition);
				} else {
					result = result + addition;
					result = result.replaceAll(SEPARATOR, addition + SEPARATOR);
				}
			}
		}
		namesMap.put(key, result);
		return result;
	}

	private String getKey(final int value, final int level) {
		return new StringBuffer().append(value).append("-").append(level).toString();
	}

	public static int getNoteValue(final String noteName) {
		for (int value = 0; value < RELATIVE_NOTE_NAMES.length; value++) {
			final String currentName = RELATIVE_NOTE_NAMES[value];
			if (noteName.equals(currentName) || noteName.equals(NamesUtil.getFirstNotePart(currentName))
					|| noteName.equals(NamesUtil.getSecondNotePart(currentName))) {
				return value;
			}
		}
		throw new IllegalArgumentException();
	}
}
