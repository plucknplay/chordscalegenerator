/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.enums.IntervalNamesMode;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.listeners.ISimpleChangeListener;

public abstract class Draft {

	/**
	 * Property which indicates that the editable state has changed.
	 * 
	 * <p>
	 * The new editable state will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_EDITABLE_STATE_CHANGED = new Object();

	/**
	 * Property which indicates that the root note of a draft has changed.
	 * 
	 * <p>
	 * The new root note will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_ROOT_NOTE_CHANGED = new Object();

	/**
	 * Property which indicates that an interval name of a draft has been
	 * changed.
	 * 
	 * <p>
	 * The new interval name will be passed while the notification.
	 * </p>
	 */
	public static final Object PROP_INTERVAL_NAME_CHANGED = new Object();

	/**
	 * Flag which indicates whether this draft is editable.
	 */
	private boolean editable;

	/**
	 * This flag indicates whether or not the input has been modified after
	 * creation.
	 */
	private boolean modifiedInput;

	/**
	 * The initial or later modified root note of this draft.
	 */
	private Note rootNote;

	/**
	 * Map that stores the corresponding name of an interval.
	 */
	private Map<Interval, String> intervalNamesMap;

	/**
	 * The interval names mode that is currently in use for this draft.
	 */
	private IntervalNamesMode intervalNameMode;

	/**
	 * The list of simple change listeners.
	 */
	private List<ISimpleChangeListener> listeners;

	/**
	 * The initial input (Griptable or Block) of this draft, or
	 * <code>null</code>.
	 */
	private final IBlock input;

	/**
	 * The constructor.
	 * 
	 * @param input
	 *            the input, or <code>null</code>
	 */
	protected Draft(final IBlock input) {
		this.input = input;
		intervalNameMode = IntervalNamesMode.valueOf(Activator.getDefault().getPreferenceStore()
				.getString(Preferences.INTERVAL_NAMES_MODE));
		initRootNote(input);
	}

	/**
	 * The constructor.
	 * 
	 * @param other
	 *            another draft
	 */
	public Draft(final Draft other) {
		input = other.input;
		editable = other.editable;
		intervalNameMode = other.intervalNameMode;
		modifiedInput = other.modifiedInput;
		rootNote = other.rootNote;

		// clone listeners
		listeners = null;
		// if (other.listeners != null) {
		// listeners = new ArrayList<ISimpleChangeListener>(other.listeners);
		// }

		// clone interval names map
		intervalNamesMap = null;
		if (other.intervalNamesMap != null) {
			intervalNamesMap = new HashMap<Interval, String>();
			for (final Entry<Interval, String> entry : other.intervalNamesMap.entrySet()) {
				intervalNamesMap.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public IBlock getInput() {
		return input;
	}

	/**
	 * Returns true if this draft represents a griptable, or false otherwise.
	 * 
	 * @return true if this draft represents a griptable, or false otherwise
	 */
	public boolean isGriptable() {
		return input != null && !isModifiedInput() && input instanceof Griptable;
	}

	/**
	 * Returns true if this draft represents a griptable, or false otherwise.
	 * 
	 * @return true if this draft represents a griptable, or false otherwise
	 */
	public boolean isBlock() {
		return input != null && !isModifiedInput() && input instanceof Block;
	}

	/**
	 * Returns true if this draft is editable, or false otherwise.
	 * 
	 * @return true if this draft is editable, or false otherwise
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Sets the editable state of this draft.
	 * 
	 * @param editable
	 *            the new editable state
	 */
	public void setEditable(final boolean editable) {
		if (this.editable != editable) {
			this.editable = editable;
			notifyListeners(PROP_EDITABLE_STATE_CHANGED, Boolean.valueOf(editable));
		}
	}

	/**
	 * Marks this draft as modified.
	 * 
	 * @param modifiedInput
	 *            <code>true</code> if this draft has been modified,
	 *            <code>false</code> otherwise
	 */
	protected void setModifiedInput(final boolean modifiedInput) {
		this.modifiedInput = modifiedInput;
	}

	/**
	 * Returns <code>true</code> if this draft was modified, or
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this draft was modified, or
	 *         <code>false</code> otherwise
	 */
	public boolean isModifiedInput() {
		return modifiedInput;
	}

	protected void initRootNote(final IBlock block) {
		setRootNote(input != null ? input.getIntervalContainer().getRootNote() : null);
	}

	/**
	 * Sets the root note depended on the relative notes of this draft.
	 * 
	 * @param toggleRootNote
	 *            <code>true</code> if the current root note shall be toggled,
	 *            or <code>false</code> if the first relative note of this draft
	 *            shall be set as root note
	 */
	public void setRootNote(final boolean toggleRootNote) {

		// determine sorted list of notes
		final List<Note> notes = new ArrayList<Note>(getRelativeNotes());
		Collections.sort(notes);

		// update root note
		if (notes.isEmpty()) {
			setRootNote(null);
		} else if (rootNote == null || !notes.contains(rootNote)) {
			setRootNote(notes.get(0));
		} else if (toggleRootNote) {
			int index = notes.indexOf(rootNote) + 1;
			if (index >= notes.size()) {
				index = 0;
			}
			setModifiedInput(true);
			setRootNote(notes.get(index));
		}
	}

	/**
	 * Sets the new root note of this draft.
	 * 
	 * @param newRootNote
	 *            the new root note, may be <code>null</code>
	 */
	public void setRootNote(final Note newRootNote) {
		final int oldRootNoteValue = rootNote != null ? rootNote.getValue() : -1;
		final int newRootNoteValue = newRootNote != null ? newRootNote.getValue() : -1;
		if (oldRootNoteValue != newRootNoteValue) {
			rootNote = newRootNote;
			notifyListeners(PROP_ROOT_NOTE_CHANGED, rootNote);
		}
	}

	public Note getRootNote() {
		return rootNote;
	}

	public boolean isRootNote(final FretboardPosition fretboardPosition) {
		final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		if (currentInstrument != null && getRootNote() != null && fretboardPosition != null) {
			return getRootNote().getValue() == currentInstrument.getNote(fretboardPosition).getValue();
		}
		return false;
	}

	public boolean isRootNote(final Note note) {
		final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		if (currentInstrument != null && getRootNote() != null && note != null) {
			return getRootNote().getValue() == note.getValue();
		}
		return false;
	}

	public String getIntervalName(final Interval interval) {
		if (intervalNamesMap == null) {
			intervalNamesMap = new HashMap<Interval, String>();
			for (int i = 0; i < Constants.INTERVALS_NUMBER; i++) {
				final Interval currentInterval = Factory.getInstance().getInterval(i);
				intervalNamesMap.put(currentInterval, input != null && input.getIntervalContainer() != null ? input
						.getIntervalContainer().getIntervalName(currentInterval) : currentInterval.getDefaultName());
			}
		}
		return intervalNamesMap.get(Factory.getInstance().getInterval(interval.getHalfsteps()));
	}

	public String getIntervalName(final Note note) {
		return getRootNote() != null ? getIntervalName(getRootNote().calcInterval(note)) : "?";
	}

	public String getIntervalName(final FretboardPosition fretboardPosition) {
		final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		if (currentInstrument != null) {
			return getIntervalName(currentInstrument.getNote(fretboardPosition.getFret() == currentInstrument
					.getFretCount() + 1 ? new FretboardPosition(fretboardPosition.getString(), 0) : fretboardPosition));
		}
		return "?";
	}

	public void setIntervalName(final Interval interval, final String name) {
		if (interval == null || name == null) {
			throw new IllegalArgumentException();
		}
		final Interval theInterval = Factory.getInstance().getInterval(interval.getHalfsteps());
		final String oldValue = intervalNamesMap.get(theInterval);
		if (!oldValue.equals(name)) {
			intervalNamesMap.put(theInterval, name);
			notifyListeners(PROP_INTERVAL_NAME_CHANGED, name);
		}
	}

	public void updateIntervalNames() {
		if (intervalNamesMap == null) {
			return;
		}
		final IntervalNamesMode currentIntervalNameMode = IntervalNamesMode.valueOf(Activator.getDefault()
				.getPreferenceStore().getString(Preferences.INTERVAL_NAMES_MODE));
		final Set<Interval> keySet = intervalNamesMap.keySet();
		for (final Interval interval : keySet) {
			final String currentName = intervalNameMode.translateTo(currentIntervalNameMode,
					intervalNamesMap.get(interval));
			intervalNamesMap.put(interval, currentName);
		}
		intervalNameMode = currentIntervalNameMode;
	}

	/**
	 * Adds a simple change listener to this draft.
	 * 
	 * @param listener
	 *            the listener to add, must not be null
	 */
	public void addSimpleChangeListener(final ISimpleChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		if (listeners == null) {
			listeners = new ArrayList<ISimpleChangeListener>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes a simple change listener from this draft.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeSimpleChangeListener(final ISimpleChangeListener listener) {
		if (listeners == null) {
			return;
		}
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			listeners = null;
		}
	}

	/**
	 * Notifies all the registered listeners that a change has occured.
	 * 
	 * @param property
	 *            the property to specify which kind of change has happened
	 * @param value
	 *            the new value if there is one
	 */
	public void notifyListeners(final Object property, final Object value) {
		if (listeners == null) {
			return;
		}
		final List<ISimpleChangeListener> theListeners = new ArrayList<ISimpleChangeListener>(listeners);
		for (final ISimpleChangeListener listener : theListeners) {
			listener.notifyChange(property, value);
		}
	}

	/**
	 * Returns true if this draft could be a potential griptable, or false
	 * otherwise.
	 * 
	 * <p>
	 * Note a draft can not represent a griptable if there is more than one
	 * fretboard position defined for one string.
	 * </p>
	 * 
	 * @return true if this draft could be a potential griptable, or false
	 *         otherwise
	 */
	public abstract boolean isPotentialGriptable();

	/**
	 * Returns the griptables this draft may represent.
	 * 
	 * @return the griptables this draft may represent, or null if this draft
	 *         can't be a griptable
	 */
	public abstract Collection<Griptable> getGriptables();

	/**
	 * Returns the defined relative notes of this draft.
	 * 
	 * @return the defined relative notes of this draft, never <code>null</code>
	 */
	public abstract Collection<Note> getRelativeNotes();

	/**
	 * Returns true if this draft is empty, or false otherwise.
	 * 
	 * @return true if this draft is empty, or false otherwise
	 */
	public abstract boolean isEmpty();

	public boolean isSimilar(final Draft other) {
		return other != null
				&& getClass().equals(other.getClass())
				&& (rootNote == null && other.rootNote == null || rootNote != null
						&& rootNote.equals(other.getRootNote())) && hasEqualRelativeNotes(other);
	}

	protected boolean hasEqualRelativeNotes(final Draft other) {
		final List<Note> notes = new ArrayList<Note>(getRelativeNotes());
		final List<Note> otherNotes = new ArrayList<Note>(other.getRelativeNotes());
		Collections.sort(notes);
		Collections.sort(otherNotes);
		if (notes.size() != otherNotes.size()) {
			return false;
		}
		for (int i = 0; i < notes.size(); i++) {
			if (!notes.get(i).equals(otherNotes.get(i))) {
				return false;
			}
		}
		return true;
	}
}
