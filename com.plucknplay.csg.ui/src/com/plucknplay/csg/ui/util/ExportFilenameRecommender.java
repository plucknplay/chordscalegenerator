/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.preferencePages.PreferenceMessages;

public class ExportFilenameRecommender {

	public static final String VIEW_BOX = PreferenceMessages.ExportFilenamePreferencePage_box;
	public static final String VIEW_TAB = PreferenceMessages.ExportFilenamePreferencePage_tab;
	public static final String VIEW_NOTES = PreferenceMessages.ExportFilenamePreferencePage_notes;
	public static final String VIEW_FRETBOARD = PreferenceMessages.ExportFilenamePreferencePage_fretboard;
	public static final String VIEW_KEYBOARD = PreferenceMessages.ExportFilenamePreferencePage_keyboard;

	private static final String ILLEGAL_CHARACTERS = "[:.;,\"´'`/|!?*<>|\\\\]"; //$NON-NLS-1$
	private static final String WHITE_SPACE_CHARACTERS = "[\n\r\t\f]"; //$NON-NLS-1$
	private static final String REMOVABLE_CHARACTERS = "[\0]"; //$NON-NLS-1$

	private final IPreferenceStore prefs;

	private String notesMode;

	private boolean replaceWhiteSpaces;
	private String whiteSpaceReplacement;
	private String illegalCharReplacement;
	private boolean putViewAdditionInFront;
	private String logicalUnitsSeparation;
	private boolean addViewBox;
	private boolean addViewTab;
	private boolean addViewNotes;
	private boolean addViewFretboard;
	private boolean addViewKeyboard;

	public ExportFilenameRecommender() {
		prefs = Activator.getDefault().getPreferenceStore();
		loadPreferences();
	}

	public void loadPreferences() {
		notesMode = prefs.getString(Preferences.NOTES_MODE);

		replaceWhiteSpaces = prefs.getBoolean(Preferences.EXPORT_FILENAME_REPLACE_WHITE_SPACE);
		whiteSpaceReplacement = prefs.getString(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_WHITE_SPACE);
		illegalCharReplacement = prefs.getString(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_ILLEGAL_CHARACTER);
		logicalUnitsSeparation = prefs.getString(Preferences.EXPORT_FILENAME_REPLACEMENT_FOR_LOGICAL_UNIT);

		addViewBox = prefs.getBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_BOX);
		addViewTab = prefs.getBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_TAB);
		addViewNotes = prefs.getBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_NOTES);
		addViewFretboard = prefs.getBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_FRETBOARD);
		addViewKeyboard = prefs.getBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_KEYBOARD);
		putViewAdditionInFront = prefs.getBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_IN_FRONT);
	}

	/**
	 * Suggests a filename for the given input. This method takes all export
	 * filename preferences into account (except the general preference
	 * {@link Preferences.PREF_EXPORT_FILENAME_SUGGESTION}).
	 * 
	 * @param input
	 *            the input, must be instanceof {@link IBlock} or
	 *            {@link IntervalContainer}
	 * @param viewAddition
	 *            the source of the to be exported file, must be one of the
	 *            constants VIEW_*
	 * 
	 * @return filename
	 */
	public String suggestFilename(final Object input, final String viewAddition) {
		if (input == null || !(input instanceof IBlock || input instanceof IntervalContainer)) {
			return "";
		}
		final NotesLabelProvider noteLabelProvider = new NotesLabelProvider(false);

		// (0) init
		String original = ""; //$NON-NLS-1$
		String rootNote = ""; //$NON-NLS-1$
		String name = ""; //$NON-NLS-1$
		String extraSection = ""; //$NON-NLS-1$

		IntervalContainer intervalContainer = null;
		if (input instanceof IBlock) {
			final IBlock block = (IBlock) input;
			original = block.getBeautifiedName(notesMode);
			intervalContainer = block.getIntervalContainer();
		} else {
			intervalContainer = (IntervalContainer) input;
			original = intervalContainer.getBeautifiedName(notesMode);
		}
		original = original.trim();
		rootNote = noteLabelProvider.getText(intervalContainer.getRootNote());
		rootNote = rootNote.replace("/", illegalCharReplacement);
		rootNote = rootNote.trim();
		if (!Constants.BLANK_CHORD_NAME.equals(intervalContainer.getName())) {
			name = intervalContainer.getName();
		}

		// (1) first extract extra section

		if (input instanceof Block || input instanceof Griptable && ((Griptable) input).hasExcludedIntervals()) {

			// extract excluded intervals section of griptable
			final boolean inBrackets = prefs.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_IN_BRACKETS);
			final String bracketMode = prefs.getString(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BRACKETS_MODE);
			final String prefixMode = prefs.getString(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_PREFIX_MODE);

			final StringBuffer extraSectionStart = new StringBuffer(" "); //$NON-NLS-1$
			if (inBrackets) {
				extraSectionStart.append(bracketMode.charAt(0));
			}
			extraSectionStart.append(prefixMode);

			final int withoutSectionIndex = original.lastIndexOf(extraSectionStart.toString());

			if (withoutSectionIndex > -1) {
				extraSection = original.substring(withoutSectionIndex);
				extraSection = extraSection.replaceAll("[.:]", ""); //$NON-NLS-1$ //$NON-NLS-2$
				extraSection = extraSection.replaceAll(", ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
				extraSection = extraSection.trim();
			}
		}

		// (2) process name and extra section
		name = processName(name);
		if (replaceWhiteSpaces) {
			extraSection = extraSection.replaceAll(" ", whiteSpaceReplacement); //$NON-NLS-1$
		}

		// (3) put all together again
		final StringBuffer buf = new StringBuffer();
		buf.append(rootNote);
		if (!"".equals(name)) { //$NON-NLS-1$
			buf.append(logicalUnitsSeparation).append(name);
		}
		if (!"".equals(extraSection)) { //$NON-NLS-1$
			buf.append(logicalUnitsSeparation).append(extraSection);
		}

		// (4) add view addition (if necessary)
		final String addition = addViewBox && viewAddition.equals(VIEW_BOX) ? VIEW_BOX : addViewTab
				&& viewAddition.equals(VIEW_TAB) ? VIEW_TAB
				: addViewNotes && viewAddition.equals(VIEW_NOTES) ? VIEW_NOTES : addViewFretboard
						&& viewAddition.equals(VIEW_FRETBOARD) ? VIEW_FRETBOARD : addViewKeyboard
						&& viewAddition.equals(VIEW_KEYBOARD) ? VIEW_KEYBOARD : null;
		return addString(buf.toString(), addition);
	}

	/**
	 * Processes the given name according to the preferences defined in the
	 * ExportFilenamePreferencePage.
	 * 
	 * @param name
	 *            the name to process
	 * 
	 * @return a resulting name where illegal as well as white space characters
	 *         are replaced according to defined preferences
	 */
	public String processName(final String name) {
		if (name == null || "".equals(name)) {
			return "";
		}

		String result = name.trim();

		// (1) remove removable characters
		result = result.replaceAll(REMOVABLE_CHARACTERS, ""); //$NON-NLS-1$

		// (2) remove illegal characters
		result = result.replaceAll(ILLEGAL_CHARACTERS, illegalCharReplacement);

		// (3) replace illegal white space characters
		result = result.replaceAll(WHITE_SPACE_CHARACTERS, " "); //$NON-NLS-1$
		while (result.contains("  ")) { //$NON-NLS-1$
			result = result.replaceAll("  ", " "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		result = result.trim();

		// (4) replace white space characters
		if (replaceWhiteSpaces) {
			result = result.replaceAll(" ", whiteSpaceReplacement); //$NON-NLS-1$
		}

		// (5) do some additional checks
		while (result.contains("  ") || result.contains("__") || result.contains("--") || result.contains("_-") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				|| result.contains("-_") || result.contains(" - -") || result.contains("- - ") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				|| result.contains(" _ _") || result.contains("_ _ ")) { //$NON-NLS-1$ //$NON-NLS-2$
			result = result.replace("  ", " "); //$NON-NLS-1$ //$NON-NLS-2$
			result = result.replaceAll("__", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			result = result.replaceAll("--", "-"); //$NON-NLS-1$ //$NON-NLS-2$
			result = result.replaceAll("_-", replaceWhiteSpaces ? whiteSpaceReplacement : illegalCharReplacement); //$NON-NLS-1$
			result = result.replaceAll("-_", replaceWhiteSpaces ? whiteSpaceReplacement : illegalCharReplacement); //$NON-NLS-1$
			result = result.replaceAll(" - -", " - "); //$NON-NLS-1$ //$NON-NLS-2$
			result = result.replaceAll("- - ", " - "); //$NON-NLS-1$ //$NON-NLS-2$
			result = result.replaceAll(" _ _", " _ "); //$NON-NLS-1$ //$NON-NLS-2$
			result = result.replaceAll("_ _ ", " _ "); //$NON-NLS-1$ //$NON-NLS-2$
		}

		result = result.trim();
		if (result.startsWith("_ ") || result.startsWith("- ")) { //$NON-NLS-1$ //$NON-NLS-2$
			result = result.substring(2);
		}
		if (result.startsWith("_") || result.startsWith("-")) { //$NON-NLS-1$ //$NON-NLS-2$
			result = result.substring(1);
		}
		if (result.endsWith(" _") || result.endsWith(" -")) { //$NON-NLS-1$ //$NON-NLS-2$
			result = result.substring(0, result.length() - 2);
		}
		if (result.endsWith("_") || result.endsWith("-")) { //$NON-NLS-1$ //$NON-NLS-2$
			result = result.substring(0, result.length() - 1);
		}
		result = result.trim();
		if ("_".equals(result) || "-".equals(result)) { //$NON-NLS-1$ //$NON-NLS-2$
			result = ""; //$NON-NLS-1$
		}

		return result;
	}

	public String addString(final String name, final String addition) {
		final StringBuffer buf = new StringBuffer(name);
		if (addition != null && !"".equals(addition)) { //$NON-NLS-1$
			if (putViewAdditionInFront) {
				buf.insert(0, addition + logicalUnitsSeparation);
			} else {
				buf.append(logicalUnitsSeparation).append(addition);
			}
		}
		return buf.toString();
	}

	/* --- Setter --- */

	public void setReplaceWhiteSpaces(final boolean replaceWhiteSpaces) {
		this.replaceWhiteSpaces = replaceWhiteSpaces;
	}

	public void setWhiteSpaceReplacement(final String whiteSpaceReplacement) {
		this.whiteSpaceReplacement = whiteSpaceReplacement;
	}

	public void setIllegalCharReplacement(final String illegalCharReplacement) {
		this.illegalCharReplacement = illegalCharReplacement;
	}

	public void setPutViewAdditionInFront(final boolean putViewAdditionInFront) {
		this.putViewAdditionInFront = putViewAdditionInFront;
	}

	public void setLogicalUnitsSeparation(final String logicalUnitsSeparation) {
		this.logicalUnitsSeparation = logicalUnitsSeparation;
	}

	public void setAddViewBox(final boolean addViewBox) {
		this.addViewBox = addViewBox;
	}

	public void setAddViewTab(final boolean addViewTab) {
		this.addViewTab = addViewTab;
	}

	public void setAddViewNotes(final boolean addViewNotes) {
		this.addViewNotes = addViewNotes;
	}

	public void setAddViewFretboard(final boolean addViewFretboard) {
		this.addViewFretboard = addViewFretboard;
	}

	public void setAddViewKeyboard(final boolean addViewKeyboard) {
		this.addViewKeyboard = addViewKeyboard;
	}
}
