/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.NotePosition;
import com.plucknplay.csg.core.model.enums.Accidental;
import com.plucknplay.csg.core.model.enums.Clef;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.util.NotesIterator;
import com.plucknplay.csg.core.util.ToneRange;
import com.plucknplay.csg.core.util.ToneRangeMode;
import com.plucknplay.csg.ui.util.CalculatorUtil;

public class NotesDraft extends Draft {

	/**
	 * Property which indicates that the clef of a notes draft has changed.
	 * 
	 * <p>
	 * The new clef will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_CLEF_CHANGED = new Object();

	/**
	 * Property which indicates that the sign mode of a notes draft has changed.
	 * 
	 * <p>
	 * The new sign mode will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_SIGN_MODE_CHANGED = new Object();

	/**
	 * Property which indicates that a note position has been added to a notes
	 * draft.
	 * 
	 * <p>
	 * The new note position will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_NOTE_POSITION_ADDED = new Object();

	/**
	 * Property which indicates that a note position has been removed from a
	 * notes draft.
	 * 
	 * <p>
	 * The removed note position will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_NOTE_POSITION_REMOVED = new Object();

	/**
	 * Property which indicates that the note positions of a notes draft has
	 * changed.
	 * 
	 * <p>
	 * The whole changed notes draft will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_NOTE_POSITIONS_CHANGED = new Object();

	public static final Object TYPE_GRIPTABLE = new Object();
	public static final Object TYPE_CHORD = new Object();
	public static final Object TYPE_SCALE = new Object();

	private Object type;

	private ToneRange toneRange;
	private Clef clef;
	private boolean sharpSignOn;

	private Set<NotePosition> notePositions;
	private Set<NotePosition> highlightedNotePositions;

	private NotePosition startNotePosition;
	private NotePosition endNotePosition;
	private NotePosition firstLineNotePosition;
	private NotePosition lastLineNotePosition;

	/**
	 * The constructor.
	 * 
	 * @param clef
	 *            the clef
	 * @param sharpSignOn
	 *            true if the sharp sign mode shall be activated, or false
	 *            otherwise
	 */
	public NotesDraft(final Clef clef, final boolean sharpSignOn) {
		super((IBlock) null);
		defaultCreation(clef, sharpSignOn);
		setModifiedInput(false);
	}

	/**
	 * The constructor.
	 * 
	 * @param other
	 *            another notes draft
	 */
	public NotesDraft(final NotesDraft other) {
		super(other);
		type = other.type;
		clef = other.clef;
		sharpSignOn = other.sharpSignOn;

		// clone tone range
		toneRange = null;
		if (other.toneRange != null) {
			toneRange = new ToneRange(other.toneRange);
		}

		// clone note positions
		notePositions = null;
		if (other.notePositions != null) {
			notePositions = new HashSet<NotePosition>(other.notePositions);
		}
		highlightedNotePositions = null;
		if (other.highlightedNotePositions != null) {
			highlightedNotePositions = new HashSet<NotePosition>(other.highlightedNotePositions);
		}
		startNotePosition = null;
		if (other.startNotePosition != null) {
			startNotePosition = new NotePosition(other.startNotePosition);
		}
		endNotePosition = null;
		if (other.endNotePosition != null) {
			endNotePosition = new NotePosition(other.endNotePosition);
		}
		firstLineNotePosition = null;
		if (other.firstLineNotePosition != null) {
			firstLineNotePosition = new NotePosition(other.firstLineNotePosition);
		}
		lastLineNotePosition = null;
		if (other.lastLineNotePosition != null) {
			lastLineNotePosition = new NotePosition(other.lastLineNotePosition);
		}
	}

	/**
	 * The constructor.
	 * 
	 * @param griptable
	 *            the griptable, must not be null;
	 * @param clef
	 *            the clef
	 * @param sharpSignOn
	 *            true if the sharp sign mode shall be activated, or false
	 *            otherwise
	 */
	public NotesDraft(final Griptable griptable, final Clef clef, final boolean sharpSignOn) {
		super(griptable);
		defaultCreation(clef, sharpSignOn);

		if (griptable == null) {
			throw new IllegalArgumentException();
		}

		for (final Note currentNote : griptable.getNotes(false)) {
			addNotePosition(new NotePosition(currentNote, sharpSignOn), false);
		}
		type = TYPE_GRIPTABLE;

		initRootNote(griptable);
		setModifiedInput(false);
	}

	/**
	 * The constructor.
	 * 
	 * @param block
	 *            the block, must not be null;
	 * @param clef
	 *            the clef
	 * @param sharpSignOn
	 *            true if the sharp sign mode shall be activated, or false
	 *            otherwise
	 * @param onlyBlock
	 *            true if only the block shall define this notes draft, or false
	 *            otherwise
	 */
	public NotesDraft(final Block block, final Clef clef, final boolean sharpSignOn, final boolean onlyBlock) {
		super(block);
		defaultCreation(clef, sharpSignOn);

		if (block == null) {
			throw new IllegalArgumentException();
		}

		type = block.getIntervalContainer() instanceof Chord ? TYPE_CHORD : TYPE_SCALE;

		// TODO #416 ggf. anders realisieren mit notePositions und
		// additionalNotePositions oder ggf. analog zur Keyboard View mit
		// absoluteNotes and relativeNotes (könnte dann in einer gemeinsamen
		// Superklasse behandelt werden)

		// only block
		for (final FretboardPosition fbp : block.getFretboardPositions()) {
			final Note currentNote = InstrumentList.getInstance().getCurrentInstrument().getNote(fbp);
			addNotePosition(new NotePosition(currentNote, sharpSignOn), false);

			// add one octave higher note if necessary
			if (InstrumentList.getInstance().getCurrentInstrument().isDoubledStringWithOctaveJump(fbp.getString() + 1)) {
				final Note currentNote2 = Factory.getInstance().getNote(currentNote.getValue(),
						currentNote.getLevel() + 1);
				addNotePosition(new NotePosition(currentNote2, sharpSignOn), false);
			}
		}

		// whole block
		if (!onlyBlock) {

			highlightedNotePositions = new HashSet<NotePosition>(notePositions);

			final Set<Integer> noteValues = block.getIntervalContainer().getNoteValues();
			for (final NotesIterator iter = new NotesIterator(toneRange.getStartTone(), toneRange.getEndTone()); iter
					.hasNext();) {
				final Note currentNote = iter.next();
				if (noteValues.contains(currentNote.getValue())) {
					addNotePosition(new NotePosition(currentNote, sharpSignOn), false);
				}
			}
		}

		initRootNote(block);
		setModifiedInput(false);
	}

	private void defaultCreation(final Clef clef, final boolean sharpSignOn) {
		this.clef = clef;
		this.sharpSignOn = sharpSignOn;
		type = TYPE_GRIPTABLE;
		highlightedNotePositions = null;
		toneRange = ToneRangeMode.ACTIVE_INSTRUMENT.getToneRange();
	}

	/**
	 * Returns the clef of this notes draft.
	 * 
	 * @return the clef of this notes draft
	 */
	public Clef getClef() {
		return clef;
	}

	/**
	 * Sets the clef of this notes draft.
	 * 
	 * @param clef
	 *            the clef
	 */
	public void setClef(final Clef clef) {
		if (this.clef != clef) {
			this.clef = clef;
			clearNotePositions();
			notifyListeners(PROP_CLEF_CHANGED, clef);
		}
	}

	public ToneRange getToneRange() {
		return toneRange;
	}

	/**
	 * Returns <code>true</code> if the sharp sign mode is activated, or
	 * <code>false</code> if the flat sign mode is activated.
	 * 
	 * @return <code>true</code> if the sharp sign mode is activated, or
	 *         <code>false</code> if the flat sign mode is activated
	 */
	public boolean isSharpSignOn() {
		return sharpSignOn;
	}

	/**
	 * Sets the sign mode.
	 * 
	 * @param sharpSignOn
	 *            <code>true</code> if the sharp sign mode shall be activated,
	 *            or <code>false</code> otherwise
	 */
	public void setSharpSignOn(final boolean sharpSignOn) {
		if (this.sharpSignOn != sharpSignOn) {
			this.sharpSignOn = sharpSignOn;
			clearNotePositions();
			notifyListeners(PROP_SIGN_MODE_CHANGED, Boolean.valueOf(this.sharpSignOn));
			notePositions = upateSigns(notePositions);
			highlightedNotePositions = upateSigns(highlightedNotePositions);
			notifyListeners(PROP_NOTE_POSITIONS_CHANGED, this);
		}
	}

	/**
	 * Updates the sign values of the given set of note positions.
	 * 
	 * @return the updated set of note positions
	 */
	private Set<NotePosition> upateSigns(final Set<NotePosition> notePositionsToUpdate) {
		if (notePositionsToUpdate == null) {
			return notePositionsToUpdate;
		}

		final HashSet<NotePosition> result = new HashSet<NotePosition>();
		for (final NotePosition current : notePositionsToUpdate) {
			final NotePosition newNP = updateSign(current);
			if (newNP != null) {
				result.add(newNP);
			}
		}
		return result;
	}

	/**
	 * Updates the sign value of the given note position regarding to the
	 * current sign mode.
	 * 
	 * @param notePosition
	 *            the note position to update, must not be null
	 * 
	 * @return the sign value of the given note position regarding to the
	 *         current sign mode, may ne null
	 */
	private NotePosition updateSign(final NotePosition notePosition) {
		if (notePosition == null) {
			throw new IllegalArgumentException();
		}

		if (notePosition.getAccidental() == Accidental.NONE) {
			return new NotePosition(notePosition);
		}
		if (sharpSignOn && notePosition.getAccidental() == Accidental.SHARP) {
			return new NotePosition(notePosition);
		}
		if (!sharpSignOn && notePosition.getAccidental() == Accidental.FLAT) {
			return new NotePosition(notePosition);
		}

		final int relPos = notePosition.getPosition() % 7;

		Accidental newAccidental = sharpSignOn ? Accidental.SHARP : Accidental.FLAT;

		if (sharpSignOn && (relPos == 0 || relPos == 3)) {
			newAccidental = Accidental.NONE;
		}
		if (!sharpSignOn && (relPos == 2 || relPos == 6)) {
			newAccidental = Accidental.NONE;
		}

		final int newPosition = sharpSignOn ? notePosition.getPosition() - 1 : notePosition.getPosition() + 1;

		return newPosition < 0 || newPosition > 56 ? null : new NotePosition(newPosition, newAccidental);
	}

	/**
	 * Adds a new note position to this notes draft.
	 * 
	 * @param notePosition
	 *            the new note position, must not be <code>null</code>
	 * @param addAllNotesWithValue
	 *            <code>true</code> if all notes with the same value shall be
	 *            added, <code>false</code> otherwise
	 */
	public void addNotePosition(final NotePosition notePosition, final boolean addAllNotesWithValue) {
		if (notePosition == null || sharpSignOn && notePosition.getAccidental() == Accidental.FLAT || !sharpSignOn
				&& notePosition.getAccidental() == Accidental.SHARP) {
			throw new IllegalArgumentException();
		}

		// do not add the invalid note positions (,,Cb and c'''''#)
		if (notePosition.getPosition() == 0 && notePosition.getAccidental() == Accidental.FLAT
				|| notePosition.getPosition() == 56 && notePosition.getAccidental() == Accidental.SHARP
				|| notePosition.getPosition() > 56) {
			return;
		}

		// do not add note positions thar are outside of the tone range
		if (!toneRange.isInside(notePosition.getNote())) {
			return;
		}

		// add note position
		if (notePositions == null) {
			notePositions = new HashSet<NotePosition>();
		}

		// absolute mode
		if (!addAllNotesWithValue) {
			notePositions.add(notePosition);
		}

		// relative mode
		else {
			final Note note = notePosition.getNote();
			for (final Iterator<Note> iter = toneRange.iterator(); iter.hasNext();) {
				final Note currentNote = iter.next();
				if (note.getValue() == currentNote.getValue()) {
					notePositions.add(new NotePosition(currentNote, sharpSignOn));
				}
			}
		}

		notifyListeners(PROP_NOTE_POSITION_ADDED, notePosition);
		setRootNote(false);
		setModifiedInput(true);
	}

	/**
	 * Removes the given note position from this notes draft.
	 * 
	 * @param notePosition
	 *            the note position to remove, must not be <code>null</code>
	 * @param removeAllNotesWithValue
	 *            <code>true</code> if all notes with the same value shall be
	 *            removed, <code>false</code> otherwise
	 */
	public void removeNotePosition(final NotePosition notePosition, final boolean removeAllNotesWithValue) {
		if (notePosition == null || sharpSignOn && notePosition.getAccidental() == Accidental.FLAT || !sharpSignOn
				&& notePosition.getAccidental() == Accidental.SHARP) {
			throw new IllegalArgumentException();
		}

		if (notePositions == null) {
			return;
		}

		// absolute mode
		if (!removeAllNotesWithValue) {
			notePositions.remove(notePosition);
		}

		// relative mode
		else {
			final Note note = notePosition.getNote();
			for (final Iterator<Note> iter = toneRange.iterator(); iter.hasNext();) {
				final Note currentNote = iter.next();
				if (note.getValue() == currentNote.getValue()) {
					notePositions.remove(new NotePosition(currentNote, sharpSignOn));
				}
			}
		}

		if (notePositions.isEmpty()) {
			notePositions = null;
		}

		notifyListeners(PROP_NOTE_POSITION_REMOVED, notePosition);
		setRootNote(false);
		setModifiedInput(true);
	}

	/**
	 * Returns the note positions of this notes draft.
	 * 
	 * @return the note positions of this notes draft, never <code>null</code>
	 */
	public Set<NotePosition> getNotePositions() {
		return notePositions != null ? notePositions : new HashSet<NotePosition>();
	}

	/**
	 * Returns the note positions which shall be presented highlighted.
	 * 
	 * @return the note positions which shall be presented highlighted, never
	 *         <code>null</code>
	 */
	public Set<NotePosition> getHighlightedNotePositions() {
		return highlightedNotePositions != null ? highlightedNotePositions : new HashSet<NotePosition>();
	}

	@Override
	public Collection<Griptable> getGriptables() {
		return CalculatorUtil.getCalculator().calculateCorrespondingGriptablesOfSetOfAbsoluteNotes(getAbsoluteNotes());
	}

	@Override
	public Collection<Note> getRelativeNotes() {
		final Set<Note> result = new HashSet<Note>();
		for (final Note current : getAbsoluteNotes()) {
			result.add(Factory.getInstance().getNote(current.getValue()));
		}
		return result;
	}

	/**
	 * Returns the absolute notes this notes draft represent.
	 * 
	 * @return the absolute notes this notes draft represent
	 */
	public Collection<Note> getAbsoluteNotes() {
		final Set<Note> result = new HashSet<Note>();
		if (notePositions == null) {
			return result;
		}
		for (final NotePosition current : notePositions) {
			result.add(current.getNote());
		}
		return result;
	}

	@Override
	public boolean isPotentialGriptable() {
		return getAbsoluteNotes().size() <= InstrumentList.getInstance().getCurrentInstrument().getStringCount();
	}

	/**
	 * Returns the type of this notes draft.
	 * 
	 * @return either {@link #TYPE_GRIPTABLE}, {@link #TYPE_CHORD} or
	 *         {@link #TYPE_SCALE}
	 */
	public Object getType() {
		return type;
	}

	@Override
	public boolean isEmpty() {
		return notePositions == null || notePositions.isEmpty();
	}

	// --- Note Positioning --- //

	/**
	 * Returns <code>true</code> if the given note sits on top of a staff or
	 * ledger line.
	 * 
	 * @param note
	 *            the note
	 * 
	 * @return <code>true</code> if the given note sits on top of a staff or
	 *         ledger line, <code>false</code> otherwise
	 */
	public boolean isOnTopOfLine(final Note note) {
		return isOnTopOfLine(new NotePosition(note, sharpSignOn));
	}

	/**
	 * Returns <code>true</code> if the given note position sits on top of a
	 * staff or ledger line.
	 * 
	 * @param notePosition
	 *            the note position
	 * 
	 * @return <code>true</code> if the given note position sits on top of a
	 *         staff or ledger line, <code>false</code> otherwise
	 */
	public boolean isOnTopOfLine(final NotePosition notePosition) {
		return Math.abs(getFirstLineNotePosition().getPosition() - notePosition.getPosition()) % 2 == 0;
	}

	/**
	 * Returns the number of ledger lines that are needed to draw this note.
	 * 
	 * @param note
	 *            the note
	 * 
	 * @return the number of ledger lines that are needed to draw this note
	 */
	public int getNumberOfLedgerLines(final Note note) {
		return getNumberOfLedgerLines(new NotePosition(note, sharpSignOn));
	}

	/**
	 * Returns the number of ledger lines that are needed to draw this note
	 * position.
	 * 
	 * @param notePosition
	 *            the note position
	 * 
	 * @return the number of ledger lines that are needed to draw this note
	 *         position
	 */
	public int getNumberOfLedgerLines(final NotePosition notePosition) {
		if (isBelowStaff(notePosition)) {
			return (getFirstLineNotePosition().getPosition() - notePosition.getPosition()) / 2;
		}
		if (isAboveStaff(notePosition)) {
			return (notePosition.getPosition() - getLastLineNotePosition().getPosition()) / 2;
		}
		return 0;
	}

	/**
	 * Returns <code>true</code> if the given note is inside the staff or at
	 * least touches it.
	 * 
	 * <p>
	 * Thus, this method returns <code>true</code> if the given note is neither
	 * above or below the staff.
	 * </p>
	 * 
	 * @param note
	 *            the note
	 * 
	 * @return <code>true</code> if the given note is inside the staff or at
	 *         least touches it, <code>false</code> otherwise
	 */
	public boolean isInsideStaff(final Note note) {
		return isInsideStaff(new NotePosition(note, sharpSignOn));
	}

	/**
	 * Returns <code>true</code> if the given note position is inside the staff
	 * or at least touches it.
	 * 
	 * <p>
	 * Thus, this method returns <code>true</code> if the given note position is
	 * neither above or below the staff.
	 * </p>
	 * 
	 * @param notePosition
	 *            the note position
	 * 
	 * @return <code>true</code> if the given note position is inside the staff
	 *         or at least touches it, <code>false</code> otherwise
	 */
	public boolean isInsideStaff(final NotePosition notePosition) {
		return !isBelowStaff(notePosition) && !isAboveStaff(notePosition);
	}

	/**
	 * Returns <code>true</code> if the given note is below the staff and does
	 * note touch the staff (needs at least one ledger line).
	 * 
	 * @param note
	 *            the note
	 * 
	 * @return <code>true</code> if the given note is below the staff,
	 *         <code>false</code> otherwise
	 */
	public boolean isBelowStaff(final Note note) {
		return isBelowStaff(new NotePosition(note, sharpSignOn));
	}

	/**
	 * Returns <code>true</code> if the given note position is below the staff
	 * and does note touch the staff (needs at least one ledger line).
	 * 
	 * @param notePosition
	 *            the note position
	 * 
	 * @return <code>true</code> if the given note position is below the staff,
	 *         <code>false</code> otherwise
	 */
	public boolean isBelowStaff(final NotePosition notePosition) {
		return getFirstLineNotePosition().getPosition() - notePosition.getPosition() > 1;
	}

	/**
	 * Returns <code>true</code> if the given note is above the staff and does
	 * note touch the staff (needs at least one ledger line).
	 * 
	 * @param note
	 *            the note
	 * 
	 * @return <code>true</code> if the given note is above the staff,
	 *         <code>false</code> otherwise
	 */
	public boolean isAboveStaff(final Note note) {
		return isAboveStaff(new NotePosition(note, sharpSignOn));
	}

	/**
	 * Returns <code>true</code> if the given note position is above the staff
	 * and does note touch the staff (needs at least one ledger line).
	 * 
	 * @param notePosition
	 *            the note position
	 * 
	 * @return <code>true</code> if the given note is above the staff,
	 *         <code>false</code> otherwise
	 */
	public boolean isAboveStaff(final NotePosition notePosition) {
		return notePosition.getPosition() - getLastLineNotePosition().getPosition() > 1;
	}

	// --- Tone Range Note Positions --- //

	public NotePosition getStartNotePosition() {
		if (startNotePosition == null) {
			startNotePosition = new NotePosition(toneRange.getStartTone(), sharpSignOn);
		}
		return startNotePosition;
	}

	public NotePosition getEndNotePosition() {
		if (endNotePosition == null) {
			endNotePosition = new NotePosition(toneRange.getEndTone(), sharpSignOn);
		}
		return endNotePosition;
	}

	public NotePosition getFirstLineNotePosition() {
		if (firstLineNotePosition == null) {
			firstLineNotePosition = new NotePosition(clef.getFirstLineNote(), sharpSignOn);
		}
		return firstLineNotePosition;
	}

	public NotePosition getLastLineNotePosition() {
		if (lastLineNotePosition == null) {
			lastLineNotePosition = new NotePosition(clef.getLastLineNote(), sharpSignOn);
		}
		return lastLineNotePosition;
	}

	private void clearNotePositions() {
		startNotePosition = null;
		endNotePosition = null;
		firstLineNotePosition = null;
		lastLineNotePosition = null;
	}

	// --- Ledger Lines and Spaces BELOW Staff --- //

	private int getDiffPositionsBelow() {
		return getFirstLineNotePosition().getPosition() - getStartNotePosition().getPosition();
	}

	public int getNumberOfLedgerLinesBelow() {
		final int diffPosition = getDiffPositionsBelow();
		return diffPosition < 2 ? 0 : diffPosition / 2;
	}

	public int getNumberOfSpacesBelow() {
		final int diffPosition = getDiffPositionsBelow();
		return diffPosition < 1 ? 0 : diffPosition / 2 + diffPosition % 2;
	}

	// --- Ledger Lines and Spaces ABOVE Staff --- //

	private int getDiffPositionsAbove() {
		return getEndNotePosition().getPosition() - getLastLineNotePosition().getPosition();
	}

	public int getNumberOfLedgerLinesAbove() {
		final int diffPosition = getDiffPositionsAbove();
		return diffPosition < 2 ? 0 : diffPosition / 2;
	}

	public int getNumberOfSpacesAbove() {
		final int diffPosition = getDiffPositionsAbove();
		return diffPosition < 1 ? 0 : diffPosition / 2 + diffPosition % 2;
	}
}
