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
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.util.ToneRange;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.util.CalculatorUtil;

public class KeyboardDraft extends Draft {

	/**
	 * Property which indicates that a note has been added to the keyboard
	 * draft.
	 * 
	 * <p>
	 * The added note will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_KEYBOARD_NOTE_ADDED = new Object();

	/**
	 * Property which indicates that a note has been removed from the keyboard
	 * draft.
	 * 
	 * <p>
	 * The remove note will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_KEYBOARD_NOTE_REMOVED = new Object();

	private Set<Note> absoluteNotes;

	private Set<Note> relativeNotes;

	private ToneRange toneRange;

	private int keyNumber;

	/**
	 * The default constructor.
	 */
	public KeyboardDraft(final ToneRange toneRange) {
		super((IBlock) null);
		defaultCreation(toneRange);
		setModifiedInput(false);
	}

	/**
	 * The constructor.
	 * 
	 * @param other
	 *            another keyboard draft
	 */
	public KeyboardDraft(final KeyboardDraft other) {
		super(other);
		keyNumber = other.keyNumber;

		// clone tone range
		toneRange = null;
		if (other.toneRange != null) {
			toneRange = new ToneRange(other.toneRange);
		}

		// clone absolute and relative notes
		absoluteNotes = null;
		if (other.absoluteNotes != null) {
			absoluteNotes = new HashSet<Note>(other.absoluteNotes);
		}
		relativeNotes = null;
		if (other.relativeNotes != null) {
			relativeNotes = new HashSet<Note>(other.relativeNotes);
		}
	}

	/**
	 * The constructor.
	 * 
	 * @param block
	 *            the input, must not be null
	 * @param toneRange
	 *            the tone range
	 */
	public KeyboardDraft(final IBlock block, final ToneRange toneRange) {
		super(block);
		defaultCreation(toneRange);

		if (block == null) {
			throw new IllegalArgumentException();
		}

		relativeNotes = new HashSet<Note>(block.getNotes(true));

		if (block instanceof Block && !Activator.getDefault().getPreferenceStore().getBoolean(Preferences.SHOW_BLOCKS)) {
			for (final Iterator<Note> iter = toneRange.iterator(); iter.hasNext();) {
				final Note currentNote = iter.next();
				if (relativeNotes.contains(Factory.getInstance().getNote(currentNote.getValue()))) {
					absoluteNotes.add(currentNote);
				}
			}
		} else {
			absoluteNotes = new HashSet<Note>(block.getNotes(false));
		}

		setModifiedInput(false);
	}

	private void defaultCreation(final ToneRange toneRange) {
		this.toneRange = toneRange;
		keyNumber = determineKeyNumber();
		absoluteNotes = new HashSet<Note>();
		relativeNotes = new HashSet<Note>();
	}

	private int determineKeyNumber() {
		int result = 0;
		for (final Iterator<Note> iter = toneRange.iterator(); iter.hasNext();) {
			if (!iter.next().hasAccidental()) {
				result++;
			}
		}
		return result;
	}

	public ToneRange getToneRange() {
		return toneRange;
	}

	public int getKeyNumber() {
		return keyNumber;
	}

	@Override
	public boolean isPotentialGriptable() {
		return absoluteNotes.size() <= InstrumentList.getInstance().getCurrentInstrument().getStringCount();
	}

	@Override
	public Collection<Griptable> getGriptables() {
		return CalculatorUtil.getCalculator().calculateCorrespondingGriptablesOfSetOfAbsoluteNotes(absoluteNotes);
	}

	@Override
	public Collection<Note> getRelativeNotes() {
		return relativeNotes;
	}

	public Collection<Note> getAbsoluteNotes() {
		return absoluteNotes;
	}

	@Override
	public boolean isEmpty() {
		return absoluteNotes.isEmpty();
	}

	public void setAbsoluteNote(final Note note, final boolean addAllNotesWithValue) {

		// remove note(s)
		if (absoluteNotes.contains(note)) {
			boolean removeRelativeNote = true;

			// absolute mode
			if (!addAllNotesWithValue) {
				absoluteNotes.remove(note);
				for (final Note absoluteNote : absoluteNotes) {
					if (absoluteNote.getValue() == note.getValue()) {
						removeRelativeNote = false;
						break;
					}
				}
			}

			// relative mode
			else {
				for (final Iterator<Note> iter = toneRange.iterator(); iter.hasNext();) {
					final Note currentNote = iter.next();
					if (note.getValue() == currentNote.getValue()) {
						absoluteNotes.remove(currentNote);
					}
				}
			}

			// remove relative note
			if (removeRelativeNote) {
				relativeNotes.remove(Factory.getInstance().getNote(note.getValue()));
			}
			notifyListeners(PROP_KEYBOARD_NOTE_REMOVED, note);
		}

		// add note(s)
		else {

			// absolute mode
			if (!addAllNotesWithValue) {
				absoluteNotes.add(note);
			}

			// relative mode
			else {
				for (final Iterator<Note> iter = toneRange.iterator(); iter.hasNext();) {
					final Note currentNote = iter.next();
					if (note.getValue() == currentNote.getValue()) {
						absoluteNotes.add(currentNote);
					}
				}
			}

			// add relative note
			relativeNotes.add(Factory.getInstance().getNote(note.getValue()));
			notifyListeners(PROP_KEYBOARD_NOTE_ADDED, note);
		}

		// update root note
		setRootNote(false);
		setModifiedInput(true);
	}
}
