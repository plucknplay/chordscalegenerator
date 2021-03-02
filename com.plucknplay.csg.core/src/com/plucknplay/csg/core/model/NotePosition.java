/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import com.plucknplay.csg.core.model.enums.Accidental;

public class NotePosition {

	private int position;
	private Accidental accidental;

	/**
	 * The constructor.
	 * 
	 * @param position
	 *            the position
	 * @param accidental
	 *            the accidental
	 */
	public NotePosition(final int position, final Accidental accidental) {
		setPosition(position);
		setAccidental(accidental);
	}

	/**
	 * The constructor.
	 * 
	 * @param notePosition
	 *            another note position, must not be <code>null</code>
	 */
	public NotePosition(final NotePosition notePosition) {
		if (notePosition == null) {
			throw new IllegalArgumentException();
		}

		setPosition(notePosition.getPosition());
		setAccidental(notePosition.getAccidental());
	}

	/**
	 * The constructor.
	 * 
	 * @param note
	 *            the note, must not be <code>null</code>
	 * @param isSharpSignOn
	 *            <code>true</code> if sharp symbols shall be used,
	 *            <code>false</code> if flat symbols shall be used
	 */
	public NotePosition(final Note note, final boolean isSharpSignOn) {
		if (note == null) {
			throw new IllegalArgumentException();
		}

		int thePosition = 0;
		Accidental theAccidental = Accidental.NONE;

		switch (note.getValue()) {
		case 0:
			thePosition = 0;
			break;
		case 1:
			thePosition = isSharpSignOn ? 0 : 1;
			theAccidental = isSharpSignOn ? Accidental.SHARP : Accidental.FLAT;
			break;
		case 2:
			thePosition = 1;
			break;
		case 3:
			thePosition = isSharpSignOn ? 1 : 2;
			theAccidental = isSharpSignOn ? Accidental.SHARP : Accidental.FLAT;
			break;
		case 4:
			thePosition = 2;
			break;
		case 5:
			thePosition = 3;
			break;
		case 6:
			thePosition = isSharpSignOn ? 3 : 4;
			theAccidental = isSharpSignOn ? Accidental.SHARP : Accidental.FLAT;
			break;
		case 7:
			thePosition = 4;
			break;
		case 8:
			thePosition = isSharpSignOn ? 4 : 5;
			theAccidental = isSharpSignOn ? Accidental.SHARP : Accidental.FLAT;
			break;
		case 9:
			thePosition = 5;
			break;
		case 10:
			thePosition = isSharpSignOn ? 5 : 6;
			theAccidental = isSharpSignOn ? Accidental.SHARP : Accidental.FLAT;
			break;
		case 11:
			thePosition = 6;
			break;
		default:
			thePosition = 0;
			break;
		}

		setPosition(note.getLevel() * 7 + thePosition);
		setAccidental(theAccidental);
	}

	/**
	 * Returns the corresponding note of this note position.
	 * 
	 * @return the corresponding note of this note position
	 */
	public Note getNote() {

		int level = position / 7;
		final int relativeValue = position % 7;

		int value = 0;
		switch (relativeValue) {
		case 0:
			value = 0;
			break;
		case 1:
			value = 2;
			break;
		case 2:
			value = 4;
			break;
		case 3:
			value = 5;
			break;
		case 4:
			value = 7;
			break;
		case 5:
			value = 9;
			break;
		case 6:
			value = 11;
			break;
		default:
			value = 0;
			break;
		}

		if (accidental == Accidental.SHARP) {
			value++;
		} else if (accidental == Accidental.FLAT) {
			value--;
		}

		if (value < 0) {
			level--;
			value = 11;
		} else if (value > 11) {
			level++;
			value = 0;
		}

		return Factory.getInstance().getNote(value, level);
	}

	/**
	 * Returns the position of this note position.
	 * 
	 * @return the position of this note position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Sets the position of this note position.
	 * 
	 * @param position
	 *            the position
	 */
	public void setPosition(final int position) {
		this.position = position;
	}

	/**
	 * Returns the accidental of this note position.
	 * 
	 * @return the accidental of this note position
	 */
	public Accidental getAccidental() {
		return accidental;
	}

	/**
	 * Sets the accidental of this note position.
	 * 
	 * @param accidental
	 *            the accidental
	 */
	public void setAccidental(final Accidental accidental) {
		this.accidental = accidental;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NotePosition)) {
			return false;
		}
		final NotePosition other = (NotePosition) obj;
		return position == other.position && accidental == other.accidental;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return "(" + position + ", " + accidental + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
