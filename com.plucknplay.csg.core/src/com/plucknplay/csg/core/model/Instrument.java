/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.Set;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.enums.Clef;

/**
 * This class represents an instrument.
 * 
 * Note: There are supported two types of the fret number: a global and local
 * one. However, right now, the globally/static defined fret number is used
 * application wide. The local fret number might be interesting in the future if
 * advanced editing capabilities are supported where different f.i. fretboards
 * shall be presented in a document.
 */
public class Instrument extends Categorizable {

	private static final long serialVersionUID = 9040941480561898462L;

	private static final Note HIGHEST_POSSIBLE_NOTE_ON_FRETBOARD = Factory.getInstance().getNote(0, 6);

	private static int fretNumber = Constants.DEFAULT_FRET_NUMBER;
	private static int capoFret;

	// attributes
	private boolean useGlobalFretNumber;
	private int stringCount;
	private int fretCount;
	private Note[] notesOfEmptyStrings;
	private Note[][] notes;

	private double scaleLength;
	private Unit scaleLengthUnit;
	private double[] distances;

	private boolean doubledStrings;
	private boolean[] doubledStringsWithOctaveJump;
	private boolean lefty;
	private boolean fretless;
	private boolean simpleCapotasto;
	private int[] capoFrets;

	private int midiInstrumentNumber;
	private Clef clef;

	/**
	 * The constructor.
	 */
	public Instrument() {
		this(""); //$NON-NLS-1$
	}

	/**
	 * The constructor.
	 * 
	 * @param name
	 *            the name of the instrument, must not be null
	 */
	public Instrument(final String name) {
		super(name);

		useGlobalFretNumber = true;
		setStringCount(Constants.DEFAULT_STRING_SIZE);
		setFretCount(Constants.DEFAULT_FRET_NUMBER);

		// fill the notes array
		fillNotesArray();

		// advanced attributes
		scaleLength = 625.0f;
		scaleLengthUnit = Unit.mm;
		doubledStrings = false;
		doubledStringsWithOctaveJump = new boolean[stringCount];
		lefty = false;
		fretless = false;
		simpleCapotasto = true;
		capoFrets = new int[stringCount];
		midiInstrumentNumber = Constants.DEFAULT_MIDI_INSTRUMENT_NUMBER;
		clef = Clef.NONE;

		// update distances
		updateDistances();
	}

	/**
	 * The constructor.
	 * 
	 * @param instrument
	 *            another instrument
	 */
	public Instrument(final Instrument instrument) {
		super(instrument);

		useGlobalFretNumber = instrument.isUseGlobalFretNumber();
		stringCount = instrument.getStringCount();
		fretCount = instrument.getFretCount();
		setNotesOfEmptyStrings(instrument.getNotesOfEmptyStrings());

		// advanced attributes
		scaleLength = instrument.getScaleLength();
		scaleLengthUnit = instrument.getScaleLengthUnit();
		doubledStrings = instrument.hasDoubledStrings();
		setDoubledStringsWithOctaveJump(instrument.getDoubledStringsWithOctaveJump());
		lefty = instrument.isLefty();
		fretless = instrument.isFretless();
		simpleCapotasto = instrument.isSimpleCapotasto();
		setCapoFrets(instrument.getCapoFrets());
		midiInstrumentNumber = instrument.getMidiInstrumentNumber();
		clef = instrument.getClef();

		// update distances
		updateDistances();
	}

	/**
	 * This method calculates the notes of the fretboard depending on the
	 * defined empty string notes.
	 */
	private void fillNotesArray() {
		notes = new Note[stringCount][Constants.MAX_FRET_NUMBER + 1];
		int value, level;
		for (int string = 0; string < stringCount; string++) {
			final Note note = notesOfEmptyStrings[string];
			value = note.getValue();
			level = note.getLevel();
			for (int fret = 0; fret <= Constants.MAX_FRET_NUMBER; fret++) {
				notes[string][fret] = Factory.getInstance().getNote(value, level);
				value++;
			}
		}
	}

	/* --- getter & setter --- */

	/**
	 * Returns the globally used fret number of all instruments.
	 * 
	 * @return the globally used fret number of all instruments
	 */
	public static int getFretNumber() {
		return Instrument.fretNumber;
	}

	/**
	 * Sets the globally used fret number.
	 * 
	 * @param fretNumber
	 *            the fret number
	 */
	public static void setFretNumber(final int fretNumber) {
		if (fretNumber < 0) {
			Instrument.fretNumber = 0;
		} else if (fretNumber > Constants.MAX_FRET_NUMBER) {
			Instrument.fretNumber = Constants.MAX_FRET_NUMBER;
		} else {
			Instrument.fretNumber = fretNumber;
		}
	}

	/**
	 * Returns the fret position of the globally used (simple) capotasto.
	 * 
	 * @return the fret position of the globally used (simple) capotasto
	 */
	public static int getCapoFret() {
		return Instrument.capoFret;
	}

	/**
	 * Sets the fret position of the globally used (simple) capotasto.
	 * 
	 * @param capoFret
	 *            the fret position of the globally used (simple) capotasto
	 */
	public static void setCapoFret(final int capoFret) {
		if (capoFret < 0) {
			Instrument.capoFret = 0;
		} else if (capoFret > Instrument.fretNumber - Constants.MIN_ACTIVE_FRET_NUMBER) {
			Instrument.capoFret = Instrument.fretNumber - Constants.MIN_ACTIVE_FRET_NUMBER;
		} else {
			Instrument.capoFret = capoFret;
		}
	}

	/**
	 * Returns <code>true</code> if the globally defined fret number and
	 * (simple) capotasto will be used for this instrument, or
	 * <code>false</code> if the instruments own fret number is used.
	 * 
	 * @return <code>true</code> if the globally defined fret number and
	 *         (simple) capotasto will be used for this instrument,
	 *         <code>false</code> otherwise
	 */
	public boolean isUseGlobalFretNumber() {
		return useGlobalFretNumber;
	}

	/**
	 * Sets whether or not the globally defined fret number and (simple)
	 * capotasto shall be used for this instrument
	 * 
	 * @param useGlobalFretNumber
	 *            <code>true</code> if the globally defined fret number and
	 *            (simple) capotasto shall be used for this instrument,
	 *            <code>false</code> otherwise
	 */
	public void setUseGlobalFretNumber(final boolean useGlobalFretNumber) {
		this.useGlobalFretNumber = useGlobalFretNumber;
	}

	/**
	 * Returns the number of strings.
	 * 
	 * @return number of strings
	 */
	public int getStringCount() {
		return stringCount;
	}

	/**
	 * Sets the number of strings.
	 * 
	 * @param stringCount
	 *            the new number of strings
	 */
	public void setStringCount(final int stringCount) {
		if (this.stringCount != stringCount) {
			this.stringCount = stringCount;

			// check value
			if (this.stringCount < Constants.MIN_STRING_SIZE) {
				this.stringCount = Constants.MIN_STRING_SIZE;
			}
			if (this.stringCount > Constants.MAX_STRING_SIZE) {
				this.stringCount = Constants.MAX_STRING_SIZE;
			}

			// update empty notes array
			notesOfEmptyStrings = new Note[stringCount];
			for (int i = 0; i < notesOfEmptyStrings.length; i++) {
				notesOfEmptyStrings[i] = Factory.getInstance().getNote("C"); //$NON-NLS-1$
			}

			// update capo frets array
			doubledStringsWithOctaveJump = new boolean[stringCount];
			capoFrets = new int[stringCount];

			// update notes array
			fillNotesArray();
		}
	}

	/**
	 * Returns the number of frets.
	 * 
	 * @return number of frets
	 */
	public int getFretCount() {
		return useGlobalFretNumber ? fretNumber : fretCount;
	}

	/**
	 * Sets the number of frets.
	 * 
	 * @param fretCount
	 *            the number of frets
	 */
	public void setFretCount(final int fretCount) {
		if (this.fretCount != fretCount) {
			this.fretCount = fretCount;

			// check value
			if (this.fretCount < Constants.MIN_FRET_NUMBER) {
				this.fretCount = Constants.MIN_FRET_NUMBER;
			}
			if (this.fretCount > Constants.MAX_FRET_NUMBER) {
				this.fretCount = Constants.MAX_FRET_NUMBER;
			}
		}
	}

	/**
	 * Returns the empty string note of the string with the given number. Note
	 * the highest (first) string is defined with stringNumber = 1.
	 * 
	 * @return the note of the empty string
	 */
	public Note getNoteOfEmptyString(final int stringNumber) {
		// prevent illegal string numbers
		int s = stringNumber;
		if (s < 1) {
			s = 1;
		} else if (s > stringCount) {
			s = stringCount;
		}

		// return note
		return notesOfEmptyStrings[s - 1];
	}

	/**
	 * Sets the empty string note of the string with the given number. Note the
	 * highest (first) string is defined with stringNumber = 1.
	 * 
	 * @param note
	 *            the note of the empty string, must not be <code>null</code>,
	 *            must be a valid note of an empty string (meaning that the note
	 *            on the 24th fret is still in range, not higher than C8)
	 * @param stringNumber
	 *            the string the note should be advised to
	 * 
	 * @see #isValidNoteOfEmptyString(Note)
	 */
	public void setNoteOfEmptyString(final Note note, final int stringNumber) {
		if (note == null || !isValidNoteOfEmptyString(note)) {
			throw new IllegalArgumentException();
		}

		// prevent illegal string numbers
		int s = stringNumber;
		if (s < 1) {
			s = 1;
		} else if (s > stringCount) {
			s = stringCount;
		}

		// set the new note
		notesOfEmptyStrings[s - 1] = note;
		fillNotesArray();
	}

	/**
	 * Sets the empty notes of this instrument.
	 * 
	 * <p>
	 * Note the number of notes must be equal to the string count.
	 * </p>
	 * 
	 * @param notes
	 *            the new empty notes of this instrument, must not be null
	 */
	public void setNotesOfEmptyStrings(final Note[] notes) {
		if (notes == null || notes.length != stringCount) {
			throw new IllegalArgumentException();
		}
		notesOfEmptyStrings = new Note[notes.length];
		for (int i = 0; i < notes.length; i++) {
			if (!isValidNoteOfEmptyString(notes[i])) {
				throw new IllegalArgumentException();
			}
			notesOfEmptyStrings[i] = notes[i];
		}
		fillNotesArray();
	}

	/**
	 * Returns the empty notes of this instrument.
	 * 
	 * @return the empty notes of this instrument
	 */
	public Note[] getNotesOfEmptyStrings() {
		return notesOfEmptyStrings;
	}

	/**
	 * Returns the capotasto fret of the string with the given number. Note the
	 * highest (first) string is defined with stringNumber = 1.
	 * 
	 * @return the capotasto fret of the string with the given number
	 */
	public int getCapoFret(final int stringNumber) {
		// prevent illegal string numbers
		int s = stringNumber;
		if (s < 1) {
			s = 1;
		} else if (s > stringCount) {
			s = stringCount;
		}

		// return note
		return useGlobalFretNumber ? capoFret : capoFrets[s - 1];
	}

	/**
	 * Sets the capotasto fret of the string with the given number. Note the
	 * highest (first) string is defined with stringNumber = 1.
	 * 
	 * @param fret
	 *            the capotasto fret
	 * @param stringNumber
	 *            the string the capotasto fret should be advised to
	 */
	public void setCapoFret(final int fret, final int stringNumber) {
		// prevent illegal string numbers
		int s = stringNumber;
		if (s < 1) {
			s = 1;
		} else if (s > stringCount) {
			s = stringCount;
		}

		int f = fret;
		if (f < 0) {
			f = 0;
		} else if (f > getFretCount() - Constants.MIN_ACTIVE_FRET_NUMBER) {
			f = getFretCount() - Constants.MIN_ACTIVE_FRET_NUMBER;
		}

		// set the new fret
		capoFrets[s - 1] = f;

		// if it's a simple capo set all capo frets
		if (s == 1 && isSimpleCapotasto()) {
			for (int i = 0; i < capoFrets.length; i++) {
				capoFrets[i] = f;
			}
		}
	}

	/**
	 * Returns the capo frets of this instrument.
	 * 
	 * @return the capo frets of this instrument
	 */
	public int[] getCapoFrets() {
		return capoFrets;
	}

	/**
	 * Sets the capo frets of this instrument.
	 * 
	 * @param capoFrets
	 *            the capo frets of this instrument
	 */
	public void setCapoFrets(final int[] capoFrets) {
		if (capoFrets == null || capoFrets.length != stringCount) {
			throw new IllegalArgumentException();
		}
		this.capoFrets = new int[capoFrets.length];
		for (int i = 0; i < capoFrets.length; i++) {
			this.capoFrets[i] = capoFrets[i];
		}
	}

	/**
	 * Returns true if this instrument has at least one empty string. An empty
	 * string means that there is no capo tasto which damps this string.
	 * 
	 * @return true if this instrument has at least one empty string, or false
	 *         otherwise
	 */
	public boolean hasEmptyStrings() {
		if (isSimpleCapotasto() && capoFrets[0] > 0) {
			return false;
		}
		for (final int currentCapoFret : capoFrets) {
			if (currentCapoFret == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if this instrument has doubled strings, false otherwise.
	 * 
	 * @return true if this instrument has doubled strings, false otherwise
	 */
	public boolean hasDoubledStrings() {
		return doubledStrings;
	}

	/**
	 * Sets if this instrument has doubled strings.
	 * 
	 * @param doubledStrings
	 *            true if this instrument has doubled strings, false otherwise
	 */
	public void setDoubledStrings(final boolean doubledStrings) {
		this.doubledStrings = doubledStrings;
		doubledStringsWithOctaveJump = new boolean[stringCount];
	}

	/**
	 * Returns true if the given string is a doubled one with an octave jump,
	 * false otherwise. Note the highest (first) string is defined with
	 * stringNumber = 1.
	 * 
	 * @return true if the given string is a doubled one with an octave jump,
	 *         false otherwise
	 */
	public boolean isDoubledStringWithOctaveJump(final int stringNumber) {
		if (!doubledStrings) {
			return false;
		}

		// prevent illegal string numbers
		int s = stringNumber;
		if (s < 1) {
			s = 1;
		} else if (s > stringCount) {
			s = stringCount;
		}

		// return note
		return doubledStringsWithOctaveJump[s - 1];
	}

	/**
	 * Sets the octave jump state of the string with the given number. Note the
	 * highest (first) string is defined with stringNumber = 1.
	 * 
	 * @param octaveJump
	 *            the new octave jump state of the string with the given number
	 * @param stringNumber
	 *            the string the octave jump state should be advised to
	 */
	public void setDoubledStringWithOctaveJump(final boolean octaveJump, final int stringNumber) {
		// prevent illegal string numbers
		int s = stringNumber;
		if (s < 1) {
			s = 1;
		} else if (s > stringCount) {
			s = stringCount;
		}

		// set the new fret
		doubledStringsWithOctaveJump[s - 1] = octaveJump;
	}

	/**
	 * Returns the octave jump states of this instrument.
	 * 
	 * @return the octave jump states of this instrument
	 */
	public boolean[] getDoubledStringsWithOctaveJump() {
		return doubledStringsWithOctaveJump;
	}

	/**
	 * Sets the octave jump states of this instrument.
	 * 
	 * @param octaveJumps
	 *            the new octave jump states of this instrument
	 */
	public void setDoubledStringsWithOctaveJump(final boolean[] octaveJumps) {
		if (octaveJumps == null || octaveJumps.length != stringCount) {
			throw new IllegalArgumentException();
		}
		doubledStringsWithOctaveJump = new boolean[octaveJumps.length];
		for (int i = 0; i < octaveJumps.length; i++) {
			doubledStringsWithOctaveJump[i] = octaveJumps[i];
		}
	}

	/**
	 * Returns true if this instrument is a lefty (or righty), false otherwise.
	 * 
	 * @return true if this instrument is a lefty (or righty), false otherwise
	 */
	public boolean isLefty() {
		return lefty;
	}

	/**
	 * Sets if this instrument is a lefty (or righty).
	 * 
	 * @param lefty
	 *            true if this instrument is a lefty (or righty), false
	 *            otherwise
	 */
	public void setLefty(final boolean lefty) {
		this.lefty = lefty;
	}

	/**
	 * Sets the fretless state of this instrument.
	 * 
	 * @param fretless
	 *            true if this instrument is fretless, or false otherwise
	 */
	public void setFretless(final boolean fretless) {
		this.fretless = fretless;
	}

	/**
	 * Returns true if this instrument is fretless, or false otherwise.
	 * 
	 * @return true if this instrument is fretless, or false otherwise
	 */
	public boolean isFretless() {
		return fretless;
	}

	/**
	 * Sets the MIDI instrument number of this instrument.
	 * 
	 * @param midiInstrumentNumber
	 *            the new MIDI instrument number, must be a value between 0 and
	 *            120
	 */
	public void setMidiInstrumentNumber(final int midiInstrumentNumber) {
		if (midiInstrumentNumber < 0 || midiInstrumentNumber > 120) {
			throw new IllegalArgumentException();
		}

		this.midiInstrumentNumber = midiInstrumentNumber;
	}

	/**
	 * Returns the MIDI instrument number of this instrument.
	 * 
	 * @return the MIDI instrument number of this instrument
	 */
	public int getMidiInstrumentNumber() {
		return midiInstrumentNumber;
	}

	/**
	 * Sets the clef this instrument is associated with.
	 * 
	 * @param clef
	 *            the clef
	 */
	public void setClef(final Clef clef) {
		this.clef = clef;
	}

	/**
	 * Returns the clef this instrument is associated with.
	 * 
	 * @return the clef this instrument is associated with
	 */
	public Clef getClef() {
		return clef;
	}

	/**
	 * Returns the scale length of this instrument. The scale length is
	 * necessary to determine the real dimensions of the instrument.
	 * 
	 * @return the scale length of this instrument
	 */
	public double getScaleLength() {
		return scaleLength;
	}

	/**
	 * Sets the scale length of this instrument.
	 * 
	 * @param scaleLength
	 *            the scale length, must not be a positive value
	 */
	public void setScaleLength(final double scaleLength) {
		if (scaleLength < 0.0) {
			throw new IllegalArgumentException();
		}

		if (this.scaleLength != scaleLength) {
			this.scaleLength = scaleLength;
			updateDistances();
		}
	}

	/**
	 * Updates the distances array.
	 */
	private void updateDistances() {
		distances = new double[Constants.MAX_FRET_NUMBER + 1];
		distances[0] = 0.0d;
		double x = 0.0d;
		double distance = 0.0d;
		for (int i = 1; i <= Constants.MAX_FRET_NUMBER; i++) {
			x = (scaleLength - distance) / 17.817d;
			distance = distance + x;
			distances[i] = distance;
		}
	}

	/**
	 * Returns the unit of the scale length.
	 * 
	 * @return the unit of the scale length.
	 */
	public Unit getScaleLengthUnit() {
		return scaleLengthUnit;
	}

	/**
	 * Sets the unit of the scale length.
	 * 
	 * @param scaleLengthUnit
	 *            the unit of the scale length
	 */
	public void setScaleLengthUnit(final Unit scaleLengthUnit) {
		if (!this.scaleLengthUnit.equals(scaleLengthUnit)) {
			setScaleLength(Unit.convert(this.scaleLengthUnit, scaleLength, scaleLengthUnit));
			this.scaleLengthUnit = scaleLengthUnit;
		}
	}

	/**
	 * Returns the distance between the given frets.
	 * 
	 * @param lowerFret
	 *            the lower fret
	 * @param higherFret
	 *            the higher fret
	 * 
	 * @return the distance between the given frets
	 */
	public double getDistance(final int lowerFret, final int higherFret) {
		if (lowerFret > higherFret || lowerFret < 0 || higherFret < 0 || lowerFret > Constants.MAX_FRET_NUMBER
				|| higherFret > Constants.MAX_FRET_NUMBER) {
			throw new IllegalArgumentException();
		}

		if (distances == null) {
			updateDistances();
		}

		final double result = distances[higherFret] - distances[lowerFret];

		// format result
		final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		final double formattedResult = Double
				.parseDouble(new DecimalFormat("#.##", decimalFormatSymbols).format(result)); //$NON-NLS-1$

		return formattedResult;
	}

	/**
	 * Returns true if this instrument uses a simple capo tasto or none, false
	 * otherwise.
	 * 
	 * @return true if this instrument uses a simple capo tasto or none, false
	 *         otherwise
	 */
	public boolean isSimpleCapotasto() {
		return useGlobalFretNumber || simpleCapotasto;
	}

	/**
	 * Set if this instrument uses a simple capo tasto or none.
	 * 
	 * @param simpleCapotasto
	 *            true if this instrument uses a simple capo tasto or none,
	 *            false otherwise
	 */
	public void setSimpleCapotasto(final boolean simpleCapotasto) {
		this.simpleCapotasto = simpleCapotasto;

		// if it's a simple capo set all capo frets
		if (simpleCapotasto) {
			for (int i = 1; i < capoFrets.length; i++) {
				capoFrets[i] = capoFrets[0];
			}
		}
	}

	/**
	 * Returns the minimal valid fret due to the defined capotasto.
	 * 
	 * Note that the maximum capotasto fret is the minimal valid one.
	 * 
	 * @return the minimal valid fret due to the defined capotasto
	 */
	public int getMinFret() {
		if (isSimpleCapotasto()) {
			return useGlobalFretNumber ? capoFret : capoFrets[0];
		}
		int maxValue = 0;
		for (final int currentValue : capoFrets) {
			if (currentValue > maxValue) {
				maxValue = currentValue;
			}
		}
		return maxValue;
	}

	/**
	 * Returns the corresponding note to a given fretboard position.
	 * 
	 * @param fp
	 *            the fretboard position
	 * @return the corresponding note to the given fretboard position
	 */
	public Note getNote(final FretboardPosition fp) {
		return getNote(fp.getString(), fp.getFret());
	}

	/**
	 * Returns the corresponding note to a given string and fret number.
	 * 
	 * @param string
	 *            the string number, note the highest string number is 0
	 * @param fret
	 *            the fret number
	 * 
	 * @return the corresponding note to a given string and fret number
	 */
	private Note getNote(final int string, final int fret) {
		int stringIndex = string % stringCount;
		if (stringIndex < 0) {
			stringIndex = 0;
		}

		int fretIndex = fret % (getFretCount() + 1);
		if (fretIndex < 0) {
			fretIndex = 0;
		}

		return isLefty() ? notes[stringCount - stringIndex - 1][fretIndex] : notes[stringIndex][fretIndex];
	}

	/**
	 * Returns the corresponding fretboard positions to a given note.
	 * 
	 * @param note
	 *            the note
	 * @param compareOnlyNoteValue
	 *            true if only the note value shall be taken into consideration,
	 *            false if also the level has importance
	 * 
	 * @return set of FretboardPositions
	 */
	public Set<FretboardPosition> getFretboardPositions(final Note note, final boolean compareOnlyNoteValue) {
		return getFretboardPositions(note, 0, getFretCount(), compareOnlyNoteValue);
	}

	/**
	 * Returns the corresponding fretboard positions to a given note inside a
	 * specific range.
	 * 
	 * @param note
	 *            the note
	 * @param minFret
	 *            the minimum fret, must be a value between 0 and fretCount,
	 *            must be <= maxFret
	 * @param maxFret
	 *            the maximum fret, must be a value between 0 and fretCount,
	 *            must be >= minFret
	 * @param compareOnlyNoteValue
	 *            true if only the note value shall be taken into consideration,
	 *            false if also the level has importance
	 * 
	 * @return set of FretboardPositions
	 */
	public Set<FretboardPosition> getFretboardPositions(final Note note, final int minFret, final int maxFret,
			final boolean compareOnlyNoteValue) {
		if (minFret < 0 || minFret > getFretCount() || maxFret < 0 || maxFret > getFretCount() || minFret > maxFret) {
			throw new IllegalArgumentException();
		}

		final Set<FretboardPosition> result = new HashSet<FretboardPosition>();
		for (int string = 0; string < stringCount; string++) {
			for (int fret = minFret; fret <= maxFret; fret++) {
				if (getNote(string, fret).hasSameValue(note) && compareOnlyNoteValue
						|| getNote(string, fret).equals(note) && !compareOnlyNoteValue) {
					result.add(new FretboardPosition(string, fret));
				}
			}
		}
		return result;
	}

	/**
	 * Returns true if the given note could be a valid empty string note, or
	 * false otherwise. A given note can't be a valid empty string note if this
	 * note would imply that an invalid (out of range) note has to be placed on
	 * the fretboard. That means that only notes that are lower or equal to c'''
	 * are valid since the hightest possible note in range on the 24th fret is
	 * c''''.
	 * 
	 * @param note
	 *            the note to be checked, must not be <code>null</code>
	 * 
	 * @return <code>true</code> if the given note could be a valid empty string
	 *         note, <code>false</code> otherwise.
	 */
	public boolean isValidNoteOfEmptyString(final Note note) {
		if (note == null) {
			throw new IllegalArgumentException();
		}
		return HIGHEST_POSSIBLE_NOTE_ON_FRETBOARD.compareTo(note) >= 0;
	}

	/**
	 * Returns true if the given note is located on the fretboard, false
	 * otherwise.
	 * 
	 * @param note
	 *            the note to be checked, must not be null
	 * 
	 * @return true if the given note is located on the fretboard, false
	 *         otherwise
	 */
	public boolean isValidNote(final Note note) {
		for (int string = 1; string <= getStringCount(); string++) {
			// (1) check for capo or empty string respectively
			if (getNote(string - 1, getCapoFret(string)).equals(note)) {
				return true;
			}
			// (2) start comparison with min fret since min fret might not match
			// the capo fret
			if (getNote(string - 1, getMinFret()).compareTo(note) >= 0
					&& getNote(string - 1, getFretCount()).compareTo(note) <= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the highest note of this instrument.
	 * 
	 * @return the highest note of this instrument
	 */
	public Note getHighestNote() {
		int highestString = 1;
		Note note = getNoteOfEmptyString(highestString);
		for (int string = 2; string <= getStringCount(); string++) {
			final Note current = getNoteOfEmptyString(string);
			if (current.compareTo(note) > 0) {
				note = current;
				highestString = string;
			}
		}
		return getNote(highestString - 1, getFretCount());
	}

	/**
	 * Returns the deepest note of this instrument.
	 * 
	 * @return the deepest note of this instrument
	 */
	public Note getDeepestNote() {
		Note result = getNote(getStringCount() - 1, getCapoFret(getStringCount()));
		for (int string = getStringCount() - 1; string > 0; string--) {
			final Note current = getNote(string - 1, getCapoFret(string));
			if (current.compareTo(result) < 0) {
				result = current;
			}
		}
		return result;
	}

	/**
	 * Returns the maximum fret you can reach starting form the given fret with
	 * the given distance.
	 * 
	 * @param startFret
	 *            the starting fret, must be a valid fret of this instrument
	 * @param distance
	 *            the distance, must be a positive value
	 * 
	 * @return the maximum fret you can reach starting form the given fret with
	 *         the given distance
	 */
	public int getFretInDistance(final int startFret, final double distance) {
		if (startFret < 0 || startFret > getFretCount() || distance < 0.0) {
			throw new IllegalArgumentException();
		}

		for (int i = startFret; i <= getFretCount(); i++) {
			final double currentDistance = getDistance(startFret, i);
			if (currentDistance > distance) {
				return i - 1;
			}
		}

		// should never happen
		return getFretCount();
	}

	/* --- Object methods --- */

	@Override
	public String toString() {
		return getName();
	}
}
