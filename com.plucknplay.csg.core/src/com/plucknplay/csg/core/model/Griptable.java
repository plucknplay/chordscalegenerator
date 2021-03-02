/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.calculation.CalculatorFactory;
import com.plucknplay.csg.core.util.NamesUtil;

/**
 * This class represents a griptable.
 */
public class Griptable extends IBlock {

	// -1 .. muted string
	// 0 .. empty string
	private final byte[] assignment;
	private Chord chord;
	private boolean selected;
	private IFingering fingering;

	/* --- constructors --- */

	/**
	 * The constructor.
	 * 
	 * @param chord
	 *            the chord that is associated with this griptable, must not be
	 *            null
	 */
	public Griptable(final Chord chord) {
		super(chord);
		this.chord = chord;
		assignment = new byte[getCurrentInstrument().getStringCount()];
		selected = false;
		for (int i = 0; i < assignment.length; i++) {
			assignment[i] = -1;
		}
	}

	/**
	 * The constructor.
	 * 
	 * @param griptable
	 *            another griptable, must not be null
	 */
	public Griptable(final Griptable griptable) {
		super(griptable);
		chord = griptable.chord;
		assignment = new byte[getCurrentInstrument().getStringCount()];
		selected = false;
		for (int i = 0; i < assignment.length; i++) {
			assignment[i] = griptable.assignment[i];
		}
	}

	/* --- getter --- */

	/**
	 * Returns a printable string of notes of this griptable.
	 * 
	 * @param notesMode
	 *            the mode each shall be represented, must not be null, valid
	 *            values are '# and b', 'only b' and 'only #'
	 * @param compactMode
	 *            <code>true</code> if notes shall only be separated with a
	 *            hypen, <code>false</code> if white spaces shall additionally
	 *            be added
	 * 
	 * @return a printable string of notes of this griptable
	 */
	public String getNotesString(final String notesMode, final boolean compactMode) {
		if (notesMode == null || !notesMode.equals(Constants.NOTES_MODE_CROSS_AND_B)
				&& !notesMode.equals(Constants.NOTES_MODE_ONLY_CROSS) && !notesMode.equals(Constants.NOTES_MODE_ONLY_B)) {
			throw new IllegalArgumentException();
		}

		final StringBuffer buf = new StringBuffer();
		for (int i = assignment.length - 1; i >= 0; i--) {
			if (assignment[i] == -1) {
				buf.append("x"); //$NON-NLS-1$
			} else {
				final Note currentNote = getCurrentInstrument().getNote(new FretboardPosition(i, assignment[i]));
				if (notesMode.equals(Constants.NOTES_MODE_ONLY_CROSS)) {
					buf.append(currentNote.getRelativeNameAug());
				} else if (notesMode.equals(Constants.NOTES_MODE_ONLY_B)) {
					buf.append(currentNote.getRelativeNameDim());
				} else {
					buf.append(currentNote.getRelativeName());
				}
			}
			if (i > 0) {
				buf.append(compactMode ? "-" : " - "); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return buf.toString();
	}

	/**
	 * Returns a printable string of intervals of this griptable.
	 * 
	 * @param compactMode
	 *            <code>true</code> if intervals shall only be separated with a
	 *            hypen, <code>false</code> if white spaces shall additionally
	 *            be added
	 * 
	 * @return a printable string of intervals of this griptable
	 */
	public String getIntervalString(final boolean compactMode) {
		final StringBuffer buf = new StringBuffer();
		for (final Iterator<Interval> iter = getIntervals(true).iterator(); iter.hasNext();) {
			final Interval interval = iter.next();
			buf.append(interval == null ? "x" : chord.getIntervalName(interval)); //$NON-NLS-1$
			if (iter.hasNext()) {
				buf.append(compactMode ? "-" : " - "); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return buf.toString();
	}

	/**
	 * Returns a printable string of the relative bass tone of this griptable.
	 * 
	 * @param notesMode
	 *            the mode each shall be represented, must not be null, valid
	 *            values are '# and b', 'only b' and 'only #'
	 * 
	 * @return a printable string of the relative bass tone of this griptable
	 */
	public String getRelativeBassToneString(final String notesMode) {
		return getToneString(getBassTone(), false, notesMode);
	}

	/**
	 * Returns a printable string of the absolute bass tone of this griptable.
	 * 
	 * @param notesMode
	 *            the mode each shall be represented, must not be null, valid
	 *            values are '# and b', 'only b' and 'only #'
	 * 
	 * @return a printable string of the absolute bass tone of this griptable
	 */
	public String getAbsoluteBassToneString(final String notesMode) {
		return getToneString(getBassTone(), true, notesMode);
	}

	/**
	 * Returns a printable string of the bass tone interval of this griptable.
	 * 
	 * @return a printable string of the bass tone interval of this griptable
	 */
	public String getBassIntervalString() {
		return chord.getIntervalName(getBassInterval());
	}

	/**
	 * Returns a printable string of the relative lead tone of this griptable.
	 * 
	 * @param notesMode
	 *            the mode each shall be represented, must not be null, valid
	 *            values are '# and b', 'only b' and 'only #'
	 * 
	 * @return a printable string of the relative lead tone of this griptable
	 */
	public String getRelativeLeadToneString(final String notesMode) {
		return getToneString(getLeadTone(), false, notesMode);
	}

	/**
	 * Returns a printable string of the absolute lead tone of this griptable.
	 * 
	 * @param notesMode
	 *            the mode each shall be represented, must not be null, valid
	 *            values are '# and b', 'only b' and 'only #'
	 * 
	 * @return a printable string of the absolute lead tone of this griptable
	 */
	public String getAbsoluteLeadToneString(final String notesMode) {
		return getToneString(getLeadTone(), true, notesMode);
	}

	/**
	 * Returns a printable string of the lead tone interval of this griptable.
	 * 
	 * @return a printable string of the lead tone interval of this griptable
	 */
	public String getLeadIntervalString() {
		return chord.getIntervalName(getLeadInterval());
	}

	private String getToneString(final Note note, final boolean absolute, final String notesMode) {
		if (notesMode == null || !notesMode.equals(Constants.NOTES_MODE_CROSS_AND_B)
				&& !notesMode.equals(Constants.NOTES_MODE_ONLY_CROSS) && !notesMode.equals(Constants.NOTES_MODE_ONLY_B)) {
			throw new IllegalArgumentException();
		}

		if (Constants.NOTES_MODE_ONLY_CROSS.equals(notesMode)) {
			return absolute ? note.getAbsoluteNameAug() : note.getRelativeNameAug();
		} else if (Constants.NOTES_MODE_ONLY_B.equals(notesMode)) {
			return absolute ? note.getAbsoluteNameDim() : note.getRelativeNameDim();
		}
		return absolute ? note.getAbsoluteName() : note.getRelativeName();
	}

	/**
	 * Returns a list of intervals of this griptable beginning with the deepest
	 * tone. If there is a muted string it will be included a null value.
	 * 
	 * @param mutedStrings
	 *            <code>true</code> if <code>null</code> shall be included for a
	 *            muted string, <code>false</code> otherwise
	 * 
	 * @return a list of intervals of this griptable, never null
	 */
	public List<Interval> getIntervals(final boolean mutedStrings) {
		final List<Interval> result = new ArrayList<Interval>();
		for (int i = assignment.length - 1; i >= 0; i--) {
			if (assignment[i] == -1) {
				if (mutedStrings) {
					result.add(null);
				}
			} else {
				final Note currentNote = getCurrentInstrument().getNote(new FretboardPosition(i, assignment[i]));
				result.add(chord.getRootNote().calcInterval(currentNote));
			}
		}
		return result;
	}

	/**
	 * Returns a printable string of frets of this griptable.
	 * 
	 * @param compactMode
	 *            <code>true</code> if frets shall only be separated with a
	 *            hypen, <code>false</code> if white spaces shall additionally
	 *            be added
	 * 
	 * @return a printable string of frets of this griptable
	 */
	public String getFretsString(final boolean compactMode) {
		final StringBuffer buf = new StringBuffer();
		for (int i = assignment.length - 1; i >= 0; i--) {
			buf.append(assignment[i] == -1 ? "x" : "" + assignment[i]); //$NON-NLS-1$ //$NON-NLS-2$
			if (i > 0) {
				buf.append(compactMode ? "-" : " - "); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return buf.toString();
	}

	/**
	 * Returns the bass tone of this griptable, or null if there is none.
	 * 
	 * @return the bass tone of this griptable, or null if there is none
	 */
	public Note getBassTone() {
		final List<Note> notes = new ArrayList<Note>(getNotes(false));
		if (notes.isEmpty()) {
			return null;
		}
		Collections.sort(notes);
		return notes.get(0);
	}

	/**
	 * Returns the bass interval of this griptable.
	 * 
	 * @return the bass interval of this griptable
	 */
	public Interval getBassInterval() {
		return chord.getRootNote().calcInterval(getBassTone());
	}

	/**
	 * Returns the lead tone of this griptable, or null if there is none.
	 * 
	 * @return the lead tone of this griptable, or null if there is none
	 */
	public Note getLeadTone() {
		final List<Note> notes = new ArrayList<Note>(getNotes(false));
		if (notes.isEmpty()) {
			return null;
		}
		Collections.sort(notes);
		return notes.get(notes.size() - 1);
	}

	/**
	 * Returns the lead interval of this griptable.
	 * 
	 * @return the lead interval of this griptable
	 */
	public Interval getLeadInterval() {
		return chord.getRootNote().calcInterval(getLeadTone());
	}

	/**
	 * Returns the associated chord of this griptable.
	 * 
	 * @return the associated chord of this griptable
	 */
	public Chord getChord() {
		return chord;
	}

	/**
	 * Returns true if this griptable contains no root interval (first) and the
	 * corresponding chord would have to contain the root interval (first), or
	 * false otherwise.
	 * 
	 * @return true if this griptable contains no root interval (first) and the
	 *         corresponding chord would have to contain the root interval
	 *         (first), or false otherwise
	 */
	public boolean isWithout1st() {
		final Interval the1st = Factory.getInstance().getInterval(0);

		final boolean chordContains1st = getChord().getIntervals().contains(the1st);
		final boolean griptableContains1st = getIntervals(false).contains(the1st);

		return chordContains1st && !griptableContains1st;
	}

	/**
	 * Returns true if this griptable contains no third (interval) and the
	 * corresponding chord would have to contain the third (interval), or false
	 * otherwise.
	 * 
	 * @return true if this griptable contains no third (interval) and the
	 *         corresponding chord would have to contain the third (interval),
	 *         or false otherwise
	 */
	public boolean isWithout3rd() {
		final Interval minor3rd = Factory.getInstance().getInterval(3);
		final Interval major3rd = Factory.getInstance().getInterval(4);

		final boolean chordContains3rd = getChord().getIntervals().contains(minor3rd)
				|| getChord().getIntervals().contains(major3rd);
		final boolean griptableContains3rd = getIntervals(false).contains(minor3rd)
				|| getIntervals(true).contains(major3rd);

		return chordContains3rd && !griptableContains3rd;
	}

	/**
	 * Returns true if this griptable contains no fifth (interval) and the
	 * corresponding chord would have to contain the fifth (interval), or false
	 * otherwise.
	 * 
	 * @return true if this griptable contains no fifth (interval) and the
	 *         corresponding chord would have to contain the fifth (interval),
	 *         or false otherwise
	 */
	public boolean isWithout5th() {
		final Interval the5th = Factory.getInstance().getInterval(7);

		final boolean chordContains5th = getChord().getIntervals().contains(the5th);
		final boolean griptableContains5th = getIntervals(false).contains(the5th);

		return chordContains5th && !griptableContains5th;
	}

	public boolean hasExcludedIntervals() {
		return isWithout1st() || isWithout3rd() || isWithout5th();
	}

	@Override
	public List<FretboardPosition> getFretboardPositions() {
		return getFretboardPositions(false);
	}

	/**
	 * Returns the fretboard positions of this griptable.
	 * 
	 * @param mutedStrings
	 *            true if the fretboard positions for muted strings shall be
	 *            returned as well, or false otherwise
	 * 
	 * @return the fretboard positions of this griptable
	 */
	public List<FretboardPosition> getFretboardPositions(final boolean mutedStrings) {
		final List<FretboardPosition> result = new ArrayList<FretboardPosition>();
		for (int i = 0; i < assignment.length; i++) {
			if (assignment[i] > -1 || mutedStrings) {
				result.add(new FretboardPosition(i, assignment[i]));
			}
		}
		return result;
	}

	/**
	 * Returns the fretboard position for the given string.
	 * 
	 * @param string
	 *            the string, minimum string number is 0
	 * 
	 * @return the fretboard position for the given string, or null
	 */
	public FretboardPosition getFretboardPosition(final int string) {
		if (string < 0 || string > assignment.length) {
			throw new IllegalArgumentException();
		}
		return new FretboardPosition(string, assignment[string]);
	}

	/**
	 * Returns the minimal string of this griptable.
	 * 
	 * @return the minimal string of this griptable
	 */
	@Override
	public int getMinString() {
		int minString = 1;
		for (int i = 0; i < assignment.length; i++) {
			minString = i + 1;
			if (assignment[i] > -1) {
				break;
			}
		}
		return minString;
	}

	/**
	 * Returns the maximal string of this griptable.
	 * 
	 * @return the maximal string of this griptable
	 */
	@Override
	public int getMaxString() {
		int maxString = assignment.length;
		for (int i = assignment.length - 1; i >= 0; i--) {
			maxString = i + 1;
			if (assignment[i] > -1) {
				break;
			}
		}
		return maxString;
	}

	/**
	 * Returns the string span of this griptable.
	 * 
	 * @return the string span of this griptable
	 */
	public int getStringSpan() {
		return getMaxString() - getMinString() + 1;
	}

	/**
	 * Returns the fret span of this griptable.
	 * 
	 * @return the fret span of this griptable
	 */
	@Override
	public int getFretSpan() {
		final int result = getMaxFret() - getMinFret() + 1;
		if (result == 1 && getMaxFret() == 0) {
			return 0;
		}
		return result;
	}

	@Override
	public int getMinFret(final boolean useCapoFrets) {
		int minFret = Constants.MAX_FRET_NUMBER;
		boolean found = false;
		if (useCapoFrets) {
			for (int i = 0; i < assignment.length; i++) {
				if (assignment[i] > getCurrentInstrument().getCapoFret(i + 1) && minFret > assignment[i]) {
					minFret = assignment[i];
					found = true;
				}
			}
		} else {
			for (final byte element : assignment) {
				if (element > 0 && minFret > element) {
					minFret = element;
					found = true;
				}
			}
		}
		if (!found) {
			minFret = 0;
		}
		return minFret;
	}

	/**
	 * Returns the maximum grapped fret of this griptable.
	 * 
	 * @return the maximum grapped fret of this griptable
	 */
	@Override
	public int getMaxFret() {
		int maxFret = 0;
		for (int i = 0; i < assignment.length; i++) {
			if (maxFret < assignment[i] && assignment[i] > getCurrentInstrument().getCapoFret(i + 1)) {
				maxFret = assignment[i];
			}
		}
		return maxFret;
	}

	/**
	 * Returns the grip distance in the specified unit.
	 * 
	 * @param distanceUnit
	 *            the unit the value should be displayed, must not be null
	 * 
	 * @return the grip distance in the specified unit
	 */
	public double getGripDistance(final Unit distanceUnit) {
		if (distanceUnit == null) {
			throw new IllegalArgumentException();
		}

		final double distance = getGripDistance();
		final Unit instrumentUnit = getCurrentInstrument().getScaleLengthUnit();
		return distanceUnit.equals(instrumentUnit) ? distance : Unit.convert(instrumentUnit, distance, distanceUnit);
	}

	public String getFormattedGripDistance(final Unit distanceUnit) {
		final double value = getGripDistance(distanceUnit);

		final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		final DecimalFormat decimalFormat = new DecimalFormat("#.####", decimalFormatSymbols); //$NON-NLS-1$
		final int fractionDigits = distanceUnit == Unit.mm ? 0 : 4;
		decimalFormat.setMinimumFractionDigits(fractionDigits);
		decimalFormat.setMaximumFractionDigits(fractionDigits);
		return decimalFormat.format(value);
	}

	/**
	 * Returns the grip distance in the unit of the current instrument.
	 * 
	 * @return the grip distance in the unit of the current instrument
	 */
	public double getGripDistance() {
		return getCurrentInstrument().getDistance(getMinFret(), getMaxFret());
	}

	/**
	 * Returns the number of empty strings of this griptable.
	 * 
	 * @return the number of empty strings of this griptable
	 */
	public int getEmptyStringsCount() {
		int counter = 0;
		for (final byte element : assignment) {
			if (element == 0) {
				counter++;
			}
		}
		return counter;
	}

	@Override
	public boolean hasEmptyStringNotes() {
		for (final byte element : assignment) {
			if (element == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the number of muted strings of this griptable.
	 * 
	 * @return the number of muted strings of this griptable
	 */
	public int getMutedStringsCount() {
		return getMutedStrings().size();
	}

	/**
	 * Returns a list of muted string numbers. Note the highest note is declared
	 * as 1.
	 * 
	 * @return a list of muted string numbers
	 */
	public List<Integer> getMutedStrings() {
		final List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < assignment.length; i++) {
			if (assignment[i] == -1) {
				result.add(i + 1);
			}
		}
		return result;
	}

	/**
	 * Returns true if the given string is muted on this griptable, or false
	 * otherwise.
	 * 
	 * @param string
	 *            the string, minimum string number is 0
	 * 
	 * @return true if the given string is muted on this griptable, or false
	 *         otherwise
	 */
	public boolean isMutedString(final int string) {
		return getFretboardPosition(string).getFret() == -1;
	}

	/**
	 * Returns true if this griptable has doubled tones, or false otherwise.
	 * 
	 * @return true if this griptable has doubled tones, or false otherwise
	 */
	public boolean hasDoubledTones() {
		for (int i = 0; i < assignment.length; i++) {
			if (assignment[i] == -1) {
				continue;
			}
			final Note iNote = getCurrentInstrument().getNote(new FretboardPosition(i, assignment[i]));
			for (int j = 0; j < assignment.length; j++) {
				if (assignment[j] == -1) {
					continue;
				}
				final Note jNote = getCurrentInstrument().getNote(new FretboardPosition(j, assignment[j]));
				if (i != j && iNote.equals(jNote)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if this griptable represents an ascending or descending tone
	 * sequence, or false otherwise.
	 * 
	 * Note that here doubled tones are allowed.
	 * 
	 * @return true if this griptable represents an ascending or descending tone
	 *         sequence, or false otherwise
	 */
	public boolean isAscendingDescendingToneSequence() {
		int mode = 0;
		Note lastNote = null;
		for (int i = 0; i < assignment.length - 1; i++) {

			// determine note1
			if (assignment[i] == -1 && lastNote == null) {
				continue;
			}
			if (assignment[i] != -1) {
				lastNote = getCurrentInstrument().getNote(new FretboardPosition(i, assignment[i]));
			}

			// determine note2
			if (assignment[i + 1] == -1) {
				continue;
			}
			final Note note2 = getCurrentInstrument().getNote(new FretboardPosition(i + 1, assignment[i + 1]));

			final int compare = lastNote.compareTo(note2);
			if (compare == 0) {
				continue;
			}
			if (mode == 0) {
				mode = compare;
				continue;
			}
			if (mode != compare) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the selected state of this griptable.
	 * 
	 * @return the selected state of this griptable
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Returns the fingering of this griptable.
	 * 
	 * @param preferBarres
	 *            true if the fingering shall use a barree if possible, false
	 *            otherwise
	 * 
	 * @return the fingering of this griptable
	 */
	public IFingering getFingering(final boolean preferBarres) {
		if (fingering == null) {
			updateFingering(preferBarres);
		}
		return fingering;
	}

	/**
	 * (Re)Calulated the fingering for this griptable.
	 */
	public void updateFingering(final boolean preferBarres) {
		fingering = CalculatorFactory.getCalculator(false, false, false, preferBarres, 1000,
				Constants.CALCULATOR_RESTRICTION_NO).calculateFingeringOfGriptable(this);
	}

	// --- setter --- //

	/**
	 * Sets the new value for the given string of this griptable.
	 * 
	 * @param value
	 *            the new value
	 * @param string
	 *            the string
	 */
	public void setValue(final byte value, final int string) {
		assignment[(string < 0 ? 0 : string) % getCurrentInstrument().getStringCount()] = value;
	}

	/**
	 * Sets the new chord this griptable is associated with.
	 * 
	 * @param chord
	 *            the chord this griptable is associated with, must not be null
	 */
	public void setChord(final Chord chord) {
		if (chord == null) {
			throw new IllegalArgumentException();
		}
		setIntervalContainer(chord);
		this.chord = chord;
	}

	/**
	 * Sets the new selected state of this griptable. This is necessary to
	 * enable the selection sorting for the chord results view table.
	 * 
	 * @param selected
	 *            the new selected state
	 */
	public void setSelected(final boolean selected) {
		this.selected = selected;
	}

	/**
	 * Returns the beautified name of this griptable.
	 * 
	 * @param notesMode
	 *            the notes mode that shall be used to render this name, must be
	 *            of Constants.NOTES_MODE_*
	 * 
	 * @return the beautified name of this griptable
	 */
	@Override
	public String getBeautifiedName(final String notesMode) {
		return NamesUtil.getNameProvider().getName(this, notesMode);
	}

	// --- object methods --- //

	@Override
	public String toString() {
		return getFretsString(true);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Griptable other = (Griptable) obj;
		return this.getFretsString(true).equals(other.getFretsString(true));
	}

	@Override
	public int hashCode() {
		return getFretsString(true).hashCode();
	}
}
