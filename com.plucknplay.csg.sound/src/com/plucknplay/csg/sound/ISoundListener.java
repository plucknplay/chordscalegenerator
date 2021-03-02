/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.sound;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Note;

public interface ISoundListener {

	/**
	 * Notifies the listener that a sound change has happened.
	 * 
	 * @param noteOn
	 *            true if a note on event happened, or false for a note off
	 *            event
	 * @param note
	 *            the note
	 * @param fbp
	 *            the corresponding fretboard position, may be null
	 */
	void soundChanged(boolean noteOn, Note note, FretboardPosition fbp);

	/**
	 * Notifies the listeners that the sound was stopped.
	 */
	void stopSound();
}
