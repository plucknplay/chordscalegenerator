/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.util;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.Scale;

public class NameProvider {

	private boolean useScaleNameSeparator;
	private String scaleNameSeparator;
	private boolean useChordNameSeparator;
	private String chordNameSeparator;
	private boolean intervalsShortMode;
	private String chordNamePrefix;
	private boolean blankSpaceBetweenIntervals;
	private boolean blankSpaceBetweenPrefixAndIntervals;
	private boolean compactMode;
	private boolean intervalsInBrackets;
	private String bracketsMode;

	public NameProvider() {
		useScaleNameSeparator = true;
		scaleNameSeparator = Constants.BLANK_SPACE;
		useChordNameSeparator = true;
		chordNameSeparator = Constants.BLANK_SPACE;
		intervalsShortMode = true;
		chordNamePrefix = Constants.EXCLUDED_INTERVALS_PREFIX_NO;
		blankSpaceBetweenIntervals = true;
		blankSpaceBetweenPrefixAndIntervals = false;
		compactMode = false;
		intervalsInBrackets = true;
		bracketsMode = Constants.BRACKETS_ROUND;
	}

	/**
	 * Returns the beautified name of the given interval container. This method
	 * takes all chord and scale name preferences into account.
	 * 
	 * @param intervalContainer
	 *            the interval container, must not be <code>null</code>
	 * 
	 * @return the beautified name of the given intervalContainer
	 */
	public String getName(final IntervalContainer intervalContainer, final String notesMode) {
		if (intervalContainer instanceof Chord) {
			return getName((Chord) intervalContainer, notesMode);
		} else if (intervalContainer instanceof Scale) {
			return getName((Scale) intervalContainer, notesMode);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Returns the beautified name of the given chord. This method takes all
	 * chord name preferences into account.
	 * 
	 * @param chord
	 *            the chord, must not be <code>null</code>
	 * 
	 * @return the beautified name of the given chord
	 */
	public String getName(final Chord chord, final String notesMode) {
		if (chord == null) {
			throw new IllegalArgumentException();
		}
		return getIntervalContainerName(chord, useChordNameSeparator, chordNameSeparator, notesMode);
	}

	/**
	 * Returns the beautified name of the given griptable. This method takes all
	 * chord name preferences into account.
	 * 
	 * @param griptable
	 *            the griptable, must not be <code>null</code>
	 * 
	 * @return the beautified name of the given griptable
	 */
	public String getName(final Griptable griptable, final String notesMode) {
		if (griptable == null) {
			throw new IllegalArgumentException();
		}

		final String name = getName(griptable.getChord(), notesMode);
		if (griptable.hasExcludedIntervals()) {

			final boolean no1 = griptable.isWithout1st();
			final boolean no3 = griptable.isWithout3rd();
			final boolean no5 = griptable.isWithout5th();

			final StringBuffer buf = new StringBuffer(name);
			buf.append(Constants.BLANK_SPACE);

			if (intervalsInBrackets) {
				buf.append(getBracket(true));
			}

			boolean addComma = false;
			if (no1) {
				buf.append(getExcludedInterval(intervalsShortMode ? "1" : "1st", true));
				addComma = true;
			}
			if (no3) {
				if (addComma) {
					buf.append(getComma());
				}
				buf.append(getExcludedInterval(intervalsShortMode ? "3" : "3rd", !compactMode || !no1));
				addComma = true;
			}
			if (no5) {
				if (addComma) {
					buf.append(getComma());
				}
				buf.append(getExcludedInterval(intervalsShortMode ? "5" : "5th", !compactMode || !(no1 || no3)));
			}

			if (intervalsInBrackets) {
				buf.append(getBracket(false));
			}
			return buf.toString();
		}
		return name;
	}

	/**
	 * Returns the beautified name of the given scale. This method takes all
	 * scale name preferences into account.
	 * 
	 * @param scale
	 *            the scale
	 * 
	 * @return the beautified name of the given scale
	 */
	public String getName(final Scale scale, final String notesMode) {
		if (scale == null) {
			throw new IllegalArgumentException();
		}
		return getIntervalContainerName(scale, useScaleNameSeparator, scaleNameSeparator, notesMode);
	}

	private String getIntervalContainerName(final IntervalContainer intervalContainer, final boolean useSeparator,
			final String separator, final String notesMode) {

		final StringBuffer buf = new StringBuffer();

		// (1) Fundemental Tone
		final Note rootNote = intervalContainer.getRootNote();
		if (notesMode.equals(Constants.NOTES_MODE_CROSS_AND_B)) {
			buf.append(rootNote.getRelativeName());
		} else if (notesMode.equals(Constants.NOTES_MODE_ONLY_B)) {
			buf.append(rootNote.getRelativeNameDim());
		} else {
			buf.append(rootNote.getRelativeNameAug());
		}

		// (2) Separator + Name
		final String name = intervalContainer.getName();
		if (!name.equals(Constants.BLANK_CHORD_NAME)) {
			if (useSeparator) {
				buf.append(separator);
			}
			buf.append(name);
		}

		return buf.toString();
	}

	/* --- Helper --- */

	private char getBracket(final boolean openingBracket) {
		if (Constants.BRACKETS_ROUND.equals(bracketsMode)) {
			return openingBracket ? '(' : ')';
		} else if (Constants.BRACKETS_SQUARE.equals(bracketsMode)) {
			return openingBracket ? '[' : ']';
		} else if (Constants.BRACKETS_CURLY.equals(bracketsMode)) {
			return openingBracket ? '{' : '}';
		} else if (Constants.BRACKETS_ANGLE.equals(bracketsMode)) {
			return openingBracket ? '<' : '>';
		}
		return 0;
	}

	private String getComma() {
		return blankSpaceBetweenIntervals ? ", " : ","; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String getExcludedInterval(final String intervalName, final boolean addPrefix) {
		final StringBuffer buf = new StringBuffer(addPrefix ? chordNamePrefix : "");
		if (addPrefix && blankSpaceBetweenPrefixAndIntervals) {
			buf.append(" "); //$NON-NLS-1$
		}
		buf.append(intervalName);
		return buf.toString();
	}

	/* --- Setter --- */

	public void setUseScaleNameSeparator(final boolean useScaleNameSeparator) {
		this.useScaleNameSeparator = useScaleNameSeparator;
	}

	public void setScaleNameSeparatorMode(final String scaleNameSeparatorMode) {
		scaleNameSeparator = scaleNameSeparatorMode;
	}

	public void setUseChordNameSeparator(final boolean useChordNameSeparator) {
		this.useChordNameSeparator = useChordNameSeparator;
	}

	public void setChordNameSeparatorMode(final String chordNameSeparatorMode) {
		chordNameSeparator = chordNameSeparatorMode;
	}

	public void setIntervalsShortMode(final boolean intervalsShortMode) {
		this.intervalsShortMode = intervalsShortMode;
	}

	public void setChordNamePrefix(final String chordNamePrefix) {
		this.chordNamePrefix = chordNamePrefix;
	}

	public void setBlankSpaceBetweenIntervals(final boolean blankSpaceBetweenIntervals) {
		this.blankSpaceBetweenIntervals = blankSpaceBetweenIntervals;
	}

	public void setBlankSpaceBetweenPrefixAndIntervals(final boolean blankSpaceBetweenPrefixAndIntervals) {
		this.blankSpaceBetweenPrefixAndIntervals = blankSpaceBetweenPrefixAndIntervals;
	}

	public void setCompactMode(final boolean compactMode) {
		this.compactMode = compactMode;
	}

	public void setIntervalsInBrackets(final boolean intervalsInBrackets) {
		this.intervalsInBrackets = intervalsInBrackets;
	}

	public void setBracketsMode(final String bracketsMode) {
		this.bracketsMode = bracketsMode;
	}
}
