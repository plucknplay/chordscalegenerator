/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.enums;

import java.util.ArrayList;
import java.util.List;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.ModelMessages;

public enum IntervalNamesMode {

	DEFAULT(ModelMessages.Interval_name_mode_default, Constants.INTERVAL_NAMES_DEFAULT, ""), ENGLISH(
			ModelMessages.Interval_name_mode_english, Constants.INTERVAL_NAMES_ENGLISH, "M"), SPANISH(
			ModelMessages.Interval_name_mode_spanish, Constants.INTERVAL_NAMES_SPANISH, "M"), GERMAN(
			ModelMessages.Interval_name_mode_german, Constants.INTERVAL_NAMES_GERMAN, "g");

	public static final String DELTA = "\u0394"; //$NON-NLS-1$
	private static final String MAJOR = "M"; //$NON-NLS-1$
	private static final String MAJOR_DE = "g"; //$NON-NLS-1$

	private static String unisonIntervalName;
	private static List<String> unisonNames;
	private static boolean useDeltaInMajorIntervals;
	private static List<String> majorIntervalNames;

	private List<String> mostImportantIntervalNames;
	private List<String> minorImportantIntervalNames;

	private final String[][] names;
	private String modeName;
	private String majorLetter;

	private IntervalNamesMode(final String name, final String[][] intervalNames, final String majorLetter) {
		modeName = name;
		names = intervalNames;
		this.majorLetter = majorLetter;
	}

	static {

		unisonNames = new ArrayList<String>();
		unisonNames.add("R"); //$NON-NLS-1$
		unisonNames.add("U"); //$NON-NLS-1$

		final List<String> majorIntervalNamesTemp = new ArrayList<String>();
		for (final IntervalNamesMode mode : IntervalNamesMode.values()) {
			unisonNames.add(mode.names[0][0]);
			majorIntervalNamesTemp.add(mode.names[2][1]);
			majorIntervalNamesTemp.add(mode.names[2][5]);
			majorIntervalNamesTemp.add(mode.names[4][2]);
			majorIntervalNamesTemp.add(mode.names[4][3]);
			majorIntervalNamesTemp.add(mode.names[9][2]);
			majorIntervalNamesTemp.add(mode.names[9][3]);
			majorIntervalNamesTemp.add(mode.names[11][0]);
			majorIntervalNamesTemp.add(mode.names[11][4]);
		}
		majorIntervalNames = new ArrayList<String>(majorIntervalNamesTemp);
		for (final String name : majorIntervalNamesTemp) {
			majorIntervalNames.add(name.contains(MAJOR) ? name.replaceAll(MAJOR, DELTA)
					: name.contains(MAJOR_DE) ? name.replaceAll(MAJOR_DE, DELTA) : DELTA + name);
		}

		unisonIntervalName = null;
		useDeltaInMajorIntervals = false;
	}

	/**
	 * Sets the new name for the unison interval, or <code>null</code> if no new
	 * name shall be used.
	 * 
	 * @param unisonIntervalName
	 *            the new interval name for the unison, or <code>null</code>
	 */
	public static void setUnisonIntervalName(final String unisonIntervalName) {
		IntervalNamesMode.unisonIntervalName = unisonIntervalName;
		clearIntervalLists();
	}

	public static void setUseDeltaInMajorIntervalNames(final boolean useDeltaInMajorIntervalNames) {
		IntervalNamesMode.useDeltaInMajorIntervals = useDeltaInMajorIntervalNames;
		clearIntervalLists();
	}

	private static void clearIntervalLists() {
		for (final IntervalNamesMode mode : IntervalNamesMode.values()) {
			mode.mostImportantIntervalNames = null;
			mode.minorImportantIntervalNames = null;
		}
	}

	public String update(final String name) {
		return update(name, false);
	}

	public String update(final String name, final boolean forceUpdate) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		if (unisonNames.contains(name)) {
			return unisonIntervalName != null ? unisonIntervalName : names[0][0];
		}
		if (majorIntervalNames.contains(name)) {
			if (useDeltaInMajorIntervals || forceUpdate) {
				return name.contains(MAJOR) ? name.replaceAll(MAJOR, DELTA) : name.contains(MAJOR_DE) ? name
						.replaceAll(MAJOR_DE, DELTA) : name.contains(DELTA) ? name : DELTA + name;
			} else {
				return name.replaceAll(DELTA, majorLetter);
			}
		}
		return name;
	}

	public String getDefaultName(final Interval interval) {
		final int halfsteps = interval.getHalfsteps();
		switch (halfsteps) {
		case 0:
		case 5:
		case 10:
		case 11:
			return update(names[halfsteps][0]);
		case 1:
		case 2:
		case 6:
		case 7:
		case 8:
			return update(names[halfsteps][1]);
		case 3:
		case 4:
		case 9:
			return update(names[halfsteps][2]);
		default:
			return "";
		}
	}

	public List<String> getNames(final Interval interval, final boolean update) {
		final List<String> result = new ArrayList<String>();
		for (final String name : names[interval.getHalfsteps()]) {
			result.add(update ? update(name) : name);
		}
		return result;
	}

	public String translateTo(final IntervalNamesMode mode, final String name) {
		final Interval interval = getInterval(name);
		if (interval != null) {
			final String[] fromModeIntervalNames = names[interval.getHalfsteps()];
			for (int i = 0; i < fromModeIntervalNames.length; i++) {
				final String fromName = fromModeIntervalNames[i];
				if (update(fromName).equals(update(name))) {
					return update(mode.names[interval.getHalfsteps()][i]);
				}
			}
		}
		return name;
	}

	public List<String> getMostImportantNames() {
		if (mostImportantIntervalNames == null) {
			mostImportantIntervalNames = new ArrayList<String>();
			mostImportantIntervalNames.add(update(names[0][0]));
			mostImportantIntervalNames.add(update(names[1][5]));
			mostImportantIntervalNames.add(update(names[2][1]));
			mostImportantIntervalNames.add(update(names[2][5]));
			mostImportantIntervalNames.add(update(names[3][2]));
			mostImportantIntervalNames.add(update(names[4][2]));
			mostImportantIntervalNames.add(update(names[5][0]));
			mostImportantIntervalNames.add(update(names[5][4]));
			mostImportantIntervalNames.add(update(names[6][1]));
			mostImportantIntervalNames.add(update(names[7][1]));
			mostImportantIntervalNames.add(update(names[8][1]));
			mostImportantIntervalNames.add(update(names[8][3]));
			mostImportantIntervalNames.add(update(names[9][2]));
			mostImportantIntervalNames.add(update(names[9][3]));
			mostImportantIntervalNames.add(update(names[10][0]));
			mostImportantIntervalNames.add(update(names[11][0]));
		}
		return mostImportantIntervalNames;
	}

	public List<String> getMinorImportantNames() {
		if (minorImportantIntervalNames == null) {
			minorImportantIntervalNames = new ArrayList<String>();
			minorImportantIntervalNames.add(update(names[1][1]));
			minorImportantIntervalNames.add(update(names[3][5]));
			minorImportantIntervalNames.add(update(names[4][0]));
			minorImportantIntervalNames.add(update(names[6][0]));
			minorImportantIntervalNames.add(update(names[6][4]));
			minorImportantIntervalNames.add(update(names[8][2]));
			minorImportantIntervalNames.add(update(names[9][0]));
		}
		return minorImportantIntervalNames;
	}

	public Interval getInterval(final String name) {
		if (name != null && !"".equals(name)) {
			if (unisonNames.contains(name)) {
				return Factory.getInstance().getInterval(0);
			}
			for (int i = 0; i < names.length; i++) {
				for (int j = 0; j < names[i].length; j++) {
					if (update(names[i][j]).equals(update(name))) {
						return Factory.getInstance().getInterval(i);
					}
				}
			}
		}
		return null;
	}

	public String getModeName() {
		return modeName;
	}
}
