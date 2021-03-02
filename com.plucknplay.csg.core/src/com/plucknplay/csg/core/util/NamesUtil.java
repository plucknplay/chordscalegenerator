/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.util;

import java.util.ArrayList;
import java.util.List;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.enums.IntervalNamesMode;
import com.plucknplay.csg.core.model.enums.NoteNamesMode;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.ScaleList;

/**
 * This class provides some useful utilty methods concerning chord and interval
 * names.
 */
public final class NamesUtil {

	private static NoteNamesMode noteNamesMode;
	private static IntervalNamesMode intervalNamesMode;
	private static NameProvider nameProvider;

	static {
		noteNamesMode = NoteNamesMode.DEFAULT;
		intervalNamesMode = IntervalNamesMode.DEFAULT;
		nameProvider = new NameProvider();
	}

	private NamesUtil() {
	}

	/* --- chord, scale and block names --- */

	public static NameProvider getNameProvider() {
		return nameProvider;
	}

	/* --- note names --- */

	public static void setNoteNamesMode(final NoteNamesMode noteNamesMode) {
		NamesUtil.noteNamesMode = noteNamesMode;
	}

	public static String getNoteName(final int value) {
		return noteNamesMode.getName(value);
	}

	public static String getNoteName(final int value, final int level) {
		int theValue = value % Constants.INTERVALS_NUMBER;
		if (theValue < 0) {
			theValue = Constants.INTERVALS_NUMBER + theValue;
		}
		return noteNamesMode.getName(theValue, level);
	}

	public static int getNoteValue(final String noteName) {
		if (noteName == null || "".equals(noteName)) {
			throw new IllegalArgumentException();
		}
		return NoteNamesMode.getNoteValue(noteName);
	}

	public static String getFirstNotePart(final String s) {
		final int index = s.indexOf(NoteNamesMode.SEPARATOR);
		if (index < 0) {
			return s;
		}
		return s.substring(0, index);
	}

	public static String getSecondNotePart(final String s) {
		final int index = s.indexOf(NoteNamesMode.SEPARATOR);
		if (index < 0) {
			return s;
		}
		return s.substring(index + 1);
	}

	/* --- interval names --- */

	public static void setIntervalNamesMode(final IntervalNamesMode intervalNamesMode) {
		NamesUtil.intervalNamesMode = intervalNamesMode;
		ChordList.getInstance().updateIntervalNames();
		ScaleList.getInstance().updateIntervalNames();
	}

	public static String getDefaultIntervalName(final Interval interval) {
		return intervalNamesMode.getDefaultName(interval);
	}

	public static List<String> getIntervalNames(final Interval interval) {
		return intervalNamesMode.getNames(interval, true);
	}

	public static List<String> getMostImportantIntervalNames() {
		return intervalNamesMode.getMostImportantNames();
	}

	public static List<String> getMinorImportantIntervalNames() {
		return intervalNamesMode.getMinorImportantNames();
	}

	public static String translateIntervalName(final String name) {
		return translateIntervalName(name, intervalNamesMode);
	}

	public static String translateIntervalName(final String name, final IntervalNamesMode toMode) {
		return translateIntervalName(name, toMode, true);
	}

	public static String translateIntervalName(final String name, final IntervalNamesMode toMode, final boolean update) {
		if (name == null || toMode == null) {
			throw new IllegalArgumentException();
		}

		final String theName = alterName(name);
		for (final IntervalNamesMode mode : IntervalNamesMode.values()) {
			for (int i = 0; i < Constants.INTERVALS_NUMBER; i++) {
				final Interval interval = Factory.getInstance().getInterval(i);
				final List<String> intervalNames = mode.getNames(interval, false);
				final int index = intervalNames.indexOf(theName);
				if (index > -1) {
					return toMode.getNames(interval, update).get(index);
				}
			}
		}
		return theName;
	}

	public static List<String> translateIntervalNames(final List<String> names) {
		return translateIntervalNames(names, intervalNamesMode);
	}

	public static List<String> translateIntervalNames(final List<String> names, final IntervalNamesMode toMode) {
		final List<String> result = new ArrayList<String>(names.size());
		for (final String name : names) {
			result.add(translateIntervalName(name, toMode));
		}
		return result;
	}

	public static int getIntervalNameOctaveNumber(final Interval interval, final String intervalName) {
		final String theName = alterName(intervalName);
		for (final IntervalNamesMode mode : IntervalNamesMode.values()) {
			final List<String> names = mode.getNames(interval, false);
			final int index = names.indexOf(theName);
			if (index == -1) {
				continue;
			}
			return index < 3 ? 1 : 2;
		}
		return 1;
	}

	private static String alterName(final String name) {
		String theName = name;
		if ("R".equals(name) || "U".equals(name)) { //$NON-NLS-1$ //$NON-NLS-2$
			theName = "1"; //$NON-NLS-1$
		} else if (name.contains(IntervalNamesMode.DELTA)) {
			theName = theName.replaceAll(IntervalNamesMode.DELTA, ""); //$NON-NLS-1$
		}
		return theName;
	}

	public static String getFirstIntervalPart(final String s) {
		if (s.length() <= 2) {
			return s;
		}

		final StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if (intervalNamesMode == IntervalNamesMode.SPANISH) {
				if (Character.isDigit(s.charAt(i))) {
					buf.append(s.charAt(i));
				} else {
					break;
				}
			} else {
				if (!Character.isDigit(s.charAt(i))) {
					buf.append(s.charAt(i));
				} else {
					break;
				}
			}
		}
		return buf.toString();
	}

	public static String getSecondIntervalPart(final String s) {
		if (s.length() <= 2) {
			return s;
		}

		final StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if (intervalNamesMode == IntervalNamesMode.SPANISH) {
				if (Character.isDigit(s.charAt(i))) {
					continue;
				} else {
					buf.append(s.charAt(i));
				}
			} else {
				if (!Character.isDigit(s.charAt(i))) {
					continue;
				} else {
					buf.append(s.charAt(i));
				}
			}
		}
		return buf.toString();
	}
}
