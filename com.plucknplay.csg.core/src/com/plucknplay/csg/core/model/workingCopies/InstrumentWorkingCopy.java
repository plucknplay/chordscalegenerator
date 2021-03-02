/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.workingCopies;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.Unit;
import com.plucknplay.csg.core.model.enums.Clef;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.InstrumentList;

public class InstrumentWorkingCopy extends WorkingCopy {

	public static final Object PROP_STRING_COUNT_CHANGED = new Object();
	public static final Object PROP_FRET_COUNT_CHANGED = new Object();
	public static final Object PROP_PITCH_CHANGED = new Object();
	public static final Object PROP_CAPO_FRETS_CHANGED = new Object();
	public static final Object PROP_DOUBLED_STRINGS_CHANGED = new Object();
	public static final Object PROP_DOUBLED_STRINGS_WITH_OCTAVE_JUMP_CHANGED = new Object();
	public static final Object PROP_LEFTY_CHANGED = new Object();
	public static final Object PROP_FRETLESS_CHANGED = new Object();
	public static final Object PROP_SCALE_LENGTH_CHANGED = new Object();
	public static final Object PROP_SCALE_LENGTH_UNIT_CHANGED = new Object();
	public static final Object PROP_SIMPLE_CAPO_CHANGED = new Object();
	public static final Object PROP_MIDI_INSTRUMENT_NUMBER_CHANGED = new Object();
	public static final Object PROP_CLEF_CHANGED = new Object();

	private final Instrument workingCopy;
	private final Instrument instrument;
	private final Category category;
	private boolean newInstrument;

	public InstrumentWorkingCopy(final Instrument instrument, final Category category, final boolean newInstrument) {
		super();
		workingCopy = new Instrument(instrument);
		this.instrument = instrument;
		this.category = category;
		this.newInstrument = newInstrument;
	}

	/* --- save --- */

	/**
	 * Returns true if this working copy can be saved, false otherwise.
	 * 
	 * @return true if this working copy can be saved, false otherwise
	 */
	public boolean canBeSaved() {
		return !isInvalidName();
	}

	@Override
	public boolean save() {

		if (!canBeSaved()) {
			return false;
		}

		instrument.setName(workingCopy.getName());
		instrument.setComment(workingCopy.getComment());
		instrument.setStringCount(workingCopy.getStringCount());
		instrument.setFretCount(workingCopy.getFretCount());
		instrument.setScaleLengthUnit(workingCopy.getScaleLengthUnit());
		instrument.setScaleLength(workingCopy.getScaleLength());
		instrument.setNotesOfEmptyStrings(workingCopy.getNotesOfEmptyStrings());
		instrument.setDoubledStrings(workingCopy.hasDoubledStrings());
		instrument.setDoubledStringsWithOctaveJump(workingCopy.getDoubledStringsWithOctaveJump());
		instrument.setLefty(workingCopy.isLefty());
		instrument.setFretless(workingCopy.isFretless());
		instrument.setMidiInstrumentNumber(workingCopy.getMidiInstrumentNumber());
		instrument.setClef(workingCopy.getClef());
		instrument.setSimpleCapotasto(workingCopy.isSimpleCapotasto());
		instrument.setCapoFrets(workingCopy.getCapoFrets());

		// save instrument
		if (newInstrument) {
			InstrumentList.getInstance().addElement(instrument, category);
		} else {
			InstrumentList.getInstance().changedElement(instrument);
		}

		setDirty(false);
		newInstrument = false;

		return true;
	}

	@Override
	public boolean saveName() {

		if (isInvalidName()) {
			return false;
		}

		instrument.setName(workingCopy.getName());
		InstrumentList.getInstance().changedElement(instrument);
		checkDirty();

		return true;
	}

	public void saveClef() {

		instrument.setClef(workingCopy.getClef());
		checkDirty();
	}

	/**
	 * Check whether another instrument with this name already exists.
	 * 
	 * @return true if the current name is invalid, false otherwise
	 */
	private boolean isInvalidName() {
		final Instrument instrumentWithSameName = (Instrument) InstrumentList.getInstance().getElement(
				workingCopy.getName(), category);
		return instrumentWithSameName != null && instrumentWithSameName != instrument;
	}

	/* --- setters --- */

	public void setStringCount(final int stringCount) {
		workingCopy.setStringCount(stringCount);
		notifyListeners(Integer.valueOf(stringCount), PROP_STRING_COUNT_CHANGED);
		notifyListeners(null, PROP_CAPO_FRETS_CHANGED);
		notifyListeners(null, PROP_DOUBLED_STRINGS_WITH_OCTAVE_JUMP_CHANGED);
	}

	public void setFretCount(final int fretCount) {
		workingCopy.setFretCount(fretCount);
		notifyListeners(Integer.valueOf(fretCount), PROP_FRET_COUNT_CHANGED);
		notifyListeners(null, PROP_PITCH_CHANGED);
	}

	public void setNoteOfEmptyString(final Note note, final int stringNumber) {
		workingCopy.setNoteOfEmptyString(note, stringNumber);
		notifyListeners(null, PROP_PITCH_CHANGED);
	}

	public void setNotesOfEmptyStrings(final Note[] notes) {
		workingCopy.setNotesOfEmptyStrings(notes);
		notifyListeners(null, PROP_PITCH_CHANGED);
	}

	public void setCapoFret(final int fret, final int stringNumber) {
		workingCopy.setCapoFret(fret, stringNumber);
		notifyListeners(null, PROP_CAPO_FRETS_CHANGED);
	}

	public void setCapoFrets(final int[] capoFrets) {
		workingCopy.setCapoFrets(capoFrets);
		notifyListeners(null, PROP_CAPO_FRETS_CHANGED);
	}

	public void setDoubledStrings(final boolean doubledStrings) {
		workingCopy.setDoubledStrings(doubledStrings);
		notifyListeners(Boolean.valueOf(doubledStrings), PROP_DOUBLED_STRINGS_CHANGED);
		notifyListeners(Boolean.valueOf(doubledStrings), PROP_DOUBLED_STRINGS_WITH_OCTAVE_JUMP_CHANGED);
	}

	public void setDoubledStringWithOctaveJump(final boolean octaveJump, final int stringNumber) {
		workingCopy.setDoubledStringWithOctaveJump(octaveJump, stringNumber);
		notifyListeners(null, PROP_DOUBLED_STRINGS_WITH_OCTAVE_JUMP_CHANGED);
	}

	public void setDoubledStringsWithOctaveJump(final boolean[] doubledStringsWithOctaveJump) {
		workingCopy.setDoubledStringsWithOctaveJump(doubledStringsWithOctaveJump);
		notifyListeners(null, PROP_DOUBLED_STRINGS_WITH_OCTAVE_JUMP_CHANGED);
	}

	public void setLefty(final boolean lefty) {
		workingCopy.setLefty(lefty);
		notifyListeners(Boolean.valueOf(lefty), PROP_LEFTY_CHANGED);
	}

	public void setFretless(final boolean fretless) {
		workingCopy.setFretless(fretless);
		notifyListeners(Boolean.valueOf(fretless), PROP_FRETLESS_CHANGED);
	}

	public void setMidiInstrumentNumber(final int midiInstrumentNumber) {
		workingCopy.setMidiInstrumentNumber(midiInstrumentNumber);
		notifyListeners(Integer.valueOf(midiInstrumentNumber), PROP_MIDI_INSTRUMENT_NUMBER_CHANGED);
	}

	public void setClef(final Clef clef) {
		workingCopy.setClef(clef);
		notifyListeners(clef, PROP_CLEF_CHANGED);
	}

	public void setScaleLength(final double scaleLength) {
		workingCopy.setScaleLength(scaleLength);
		notifyListeners(new Double(scaleLength), PROP_SCALE_LENGTH_CHANGED);
	}

	public void setScaleLengthUnit(final Unit scaleLengthUnit) {
		workingCopy.setScaleLengthUnit(scaleLengthUnit);
		notifyListeners(scaleLengthUnit, PROP_SCALE_LENGTH_UNIT_CHANGED);
		notifyListeners(new Double(workingCopy.getScaleLength()), PROP_SCALE_LENGTH_CHANGED);
	}

	public void setSimpleCapotasto(final boolean simpleCapotasto) {
		workingCopy.setSimpleCapotasto(simpleCapotasto);
		notifyListeners(Boolean.valueOf(simpleCapotasto), PROP_SIMPLE_CAPO_CHANGED);
	}

	public boolean isValidNoteOfEmptyString(final Note note) {
		return workingCopy.isValidNoteOfEmptyString(note);
	}

	/* --- dirty state handling --- */

	@Override
	protected void checkDirty() {
		final boolean dirty = !workingCopy.getName().equals(instrument.getName())
				|| !workingCopy.getComment().equals(instrument.getComment())
				|| workingCopy.getStringCount() != instrument.getStringCount()
				|| workingCopy.getFretCount() != instrument.getFretCount()
				|| workingCopy.isLefty() != instrument.isLefty() || workingCopy.isFretless() != instrument.isFretless()
				|| workingCopy.getMidiInstrumentNumber() != instrument.getMidiInstrumentNumber()
				|| workingCopy.getClef() != instrument.getClef()
				|| workingCopy.hasDoubledStrings() != instrument.hasDoubledStrings()
				|| workingCopy.isSimpleCapotasto() != instrument.isSimpleCapotasto()
				|| workingCopy.getScaleLength() != instrument.getScaleLength()
				|| workingCopy.getScaleLengthUnit() != instrument.getScaleLengthUnit();

		// if not dirty till now check also the doubled strings with octave jump
		if (!dirty && workingCopy.hasDoubledStrings()) {
			for (int i = 1; i <= workingCopy.getStringCount(); i++) {
				final boolean b1 = workingCopy.isDoubledStringWithOctaveJump(i);
				final boolean b2 = instrument.isDoubledStringWithOctaveJump(i);
				if (b1 != b2) {
					setDirty(true);
					return;
				}
			}
		}

		// if not dirty till now check also the pitch
		if (!dirty) {
			for (int i = 1; i <= workingCopy.getStringCount(); i++) {
				final Note noteOfEmptyString = workingCopy.getNoteOfEmptyString(i);
				final Note noteOfEmptyString2 = instrument.getNoteOfEmptyString(i);
				if (!noteOfEmptyString.equals(noteOfEmptyString2)) {
					setDirty(true);
					return;
				}
			}
		}

		// if not dirty till now check also the capo frets
		if (!dirty) {
			final int stringCount = workingCopy.isSimpleCapotasto() ? 1 : workingCopy.getStringCount();
			for (int i = 1; i <= stringCount; i++) {
				final int capoFretOfString = workingCopy.getCapoFret(i);
				final int capoFretOfString2 = instrument.getCapoFret(i);
				if (capoFretOfString != capoFretOfString2) {
					setDirty(true);
					return;
				}
			}
		}

		setDirty(dirty);
	}

	/* --- getters --- */

	@Override
	protected Categorizable getWorkingCopy() {
		return workingCopy;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public int getStringCount() {
		return workingCopy.getStringCount();
	}

	public int getFretCount() {
		return workingCopy.getFretCount();
	}

	public double getScaleLength() {
		return workingCopy.getScaleLength();
	}

	public Unit getScaleLengthUnit() {
		return workingCopy.getScaleLengthUnit();
	}

	public boolean hasDoubledStrings() {
		return workingCopy.hasDoubledStrings();
	}

	public boolean isDoubledStringWithOctaveJump(final int stringNumber) {
		return workingCopy.isDoubledStringWithOctaveJump(stringNumber);
	}

	public boolean[] getDoubledStringsWithOctaveJump() {
		return workingCopy.getDoubledStringsWithOctaveJump();
	}

	public boolean isLefty() {
		return workingCopy.isLefty();
	}

	public boolean isFretless() {
		return workingCopy.isFretless();
	}

	public int getMidiInstrumentNumber() {
		return workingCopy.getMidiInstrumentNumber();
	}

	public Clef getClef() {
		return workingCopy.getClef();
	}

	public boolean isSimpleCapotasto() {
		return workingCopy.isSimpleCapotasto();
	}

	public int getCapoFret(final int stringNumber) {
		return workingCopy.getCapoFret(stringNumber);
	}

	public int[] getCapoFrets() {
		return workingCopy.getCapoFrets();
	}

	public Note getNoteOfEmptyString(final int stringNumber) {
		return workingCopy.getNoteOfEmptyString(stringNumber);
	}

	public Note[] getNotesOfEmptyStrings() {
		return workingCopy.getNotesOfEmptyStrings();
	}
}
