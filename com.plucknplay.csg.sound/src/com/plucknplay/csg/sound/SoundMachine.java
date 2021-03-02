/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.InstrumentList;

public final class SoundMachine implements MetaEventListener {

	public static final int META_TYPE_NOTE_INFO = 0x01;

	/**
	 * Pulses per quarter second.
	 */
	private static final int PPQS = 100;

	private static final byte NOTE_OFF = 0;
	private static final byte NOTE_ON = 1;

	/**
	 * The singleton instance of the sound machine.
	 */
	private static SoundMachine instance;

	private int midiInstrument = SoundConstants.DEFAULT_MIDI_INSTRUMENT;
	private int midiKeyboard = SoundConstants.DEFAULT_MIDI_KEYBOARD;
	private int testToneLength = SoundConstants.DEFAULT_TONE_LENGTH;
	private int testToneDistance = SoundConstants.DEFAULT_TONE_DISTANCE;
	private String testPlayPattern = SoundConstants.DEFAULT_PLAY_PATTERN;
	private int chordToneLength = SoundConstants.DEFAULT_TONE_LENGTH;
	private int chordToneDistance = SoundConstants.DEFAULT_CHORD_TONE_DISTANCE;
	private String chordPlayPattern = SoundConstants.DEFAULT_PLAY_PATTERN;
	private int blockToneLength = SoundConstants.DEFAULT_TONE_LENGTH;
	private int blockToneDistance = SoundConstants.DEFAULT_TONE_DISTANCE;
	private String blockPlayPattern = SoundConstants.DEFAULT_PLAY_PATTERN;
	private int fretboardToneLength = SoundConstants.DEFAULT_TONE_LENGTH;
	private int keyboardToneLength = SoundConstants.DEFAULT_TONE_LENGTH;
	private int volume = SoundConstants.DEFAULT_VOLUME;

	/**
	 * The job to play the sound in a background thread.
	 */
	private PlaySoundJob job;

	/**
	 * The registered sound change listeners.
	 */
	private List<ISoundListener> listeners;

	private Sequencer sequencer;

	/**
	 * The private default constructor.
	 */
	private SoundMachine() {
	}

	/**
	 * Returns the singleton instance of the sound machine.
	 * 
	 * @return the singleton instance of the sound machine
	 */
	public static SoundMachine getInstance() {
		if (instance == null) {
			instance = new SoundMachine();
		}
		return instance;
	}

	/* --- listener handling --- */

	/**
	 * Adds a sound listener to the sound machine. Listeners will be notified
	 * when a note is played.
	 * 
	 * @param listener
	 *            the listener to add, must not be null
	 */
	public void addChangeListener(final ISoundListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		if (listeners == null) {
			listeners = new ArrayList<ISoundListener>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes a sound listener from this element list.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeChangeListener(final ISoundListener listener) {
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			listeners = null;
		}
	}

	/**
	 * Notifies all the registered listeners that a sound change has happened.
	 */
	protected void notifyListeners(final boolean noteOn, final Note note, final FretboardPosition fretboardPosition) {
		if (listeners == null) {
			return;
		}
		final List<ISoundListener> theListeners = new ArrayList<ISoundListener>(listeners);
		for (final ISoundListener listener : theListeners) {
			listener.soundChanged(noteOn, note, fretboardPosition);
		}
	}

	/**
	 * Notifies all the registered listeners that a sound change has happened.
	 */
	protected void notifyListenersAboutJobStop() {
		if (listeners == null) {
			return;
		}
		final List<ISoundListener> theListeners = new ArrayList<ISoundListener>(listeners);
		for (final ISoundListener listener : theListeners) {
			listener.stopSound();
		}
	}

	/* --- play sound handling --- */

	/**
	 * Plays the empty strings of the given instrument.
	 * 
	 * @param instrument
	 *            the instrument, must not be null
	 */
	public void play(final Instrument instrument) {
		if (instrument == null) {
			throw new IllegalArgumentException();
		}

		// determine notes
		final List<Note> notes = new ArrayList<Note>();
		final List<FretboardPosition> fretboardPositions = new ArrayList<FretboardPosition>();
		for (int s = instrument.getStringCount() - 1; s >= 0; s--) {
			final int fret = instrument.getCapoFret(s);
			final FretboardPosition fbp = new FretboardPosition(s, fret);
			fretboardPositions.add(fbp);
			notes.add(instrument.getNote(fbp));
		}

		// play
		play(notes, fretboardPositions, instrument, instrument.getMidiInstrumentNumber(), testToneLength,
				testToneDistance, testPlayPattern, false);
	}

	/**
	 * Plays the given griptable on the current instrument.
	 * 
	 * @param griptable
	 *            the griptable, must not be null
	 */
	public void play(final Griptable griptable) {
		if (griptable == null) {
			throw new IllegalArgumentException();
		}

		final Instrument instrument = InstrumentList.getInstance().getCurrentInstrument();
		if (instrument == null) {
			return;
		}

		// determine notes
		final List<Note> notes = new ArrayList<Note>();
		final List<FretboardPosition> fretboardPositions = griptable.getFretboardPositions();
		Collections.reverse(fretboardPositions);
		for (final FretboardPosition fbp : fretboardPositions) {
			notes.add(instrument.getNote(fbp));
		}

		// determine and handle sequence
		try {
			final Sequence sequence = determineSequence(notes, fretboardPositions, instrument,
					instrument.getMidiInstrumentNumber(), chordToneLength, chordToneDistance, chordPlayPattern, true);
			play(sequence);
		} catch (final InvalidMidiDataException e) {
		}
	}

	/**
	 * Plays the given block on the current instrument.
	 * 
	 * @param block
	 *            the block, must not be null
	 */
	public void play(final Block block) {
		if (block == null) {
			throw new IllegalArgumentException();
		}

		final Instrument instrument = InstrumentList.getInstance().getCurrentInstrument();
		if (instrument == null) {
			return;
		}

		// determine notes
		final List<Note> notes = new ArrayList<Note>();
		final List<FretboardPosition> fretboardPositions = new ArrayList<FretboardPosition>(
				block.getFretboardPositions());
		for (final FretboardPosition fretboardPosition : fretboardPositions) {
			notes.add(instrument.getNote(fretboardPosition));
		}

		// determine and handle sequence
		try {
			final Sequence sequence = determineSequence(notes, fretboardPositions, instrument,
					instrument.getMidiInstrumentNumber(), blockToneLength, blockToneDistance, blockPlayPattern, true);
			play(sequence);
		} catch (final InvalidMidiDataException e) {
		}
	}

	/**
	 * Plays the given list of notes.
	 * 
	 * <p>
	 * Note the instrument and fretboard positions may be null. These parameters
	 * are only necessary to add potential doubled string notes to the sequence.
	 * </p>
	 * 
	 * @param notes
	 *            the list of notes to play, must not be null
	 * @param fretboardPositions
	 *            the list of the corresponding fretboard postions of the list
	 *            of notes
	 * @param instrument
	 *            the instrument
	 * @param midiInstrumentNumber
	 *            the MIDI instrument number
	 * @param toneLength
	 *            the tone length
	 * @param toneDistance
	 *            the tone distance
	 * @param playPattern
	 *            the play pattern
	 * @param addMetaMessages
	 *            true if the meta messages shall be added, or false otherwise
	 */
	public void play(final List<Note> notes, final List<FretboardPosition> fretboardPositions,
			final Instrument instrument, final int midiInstrumentNumber, final int toneLength, final int toneDistance,
			final String playPattern, final boolean addMetaMessages) {

		try {
			final Sequence sequence = determineSequence(notes, fretboardPositions, instrument, midiInstrumentNumber,
					toneLength, toneDistance, playPattern, addMetaMessages);
			if (sequence != null) {
				play(sequence);
			}
		} catch (final InvalidMidiDataException e) {
		}
	}

	/**
	 * Plays the given seqence.
	 * 
	 * @param sequence
	 *            the sequence to play, must not be null
	 */
	private void play(final Sequence sequence) {
		if (sequence == null) {
			throw new IllegalArgumentException();
		}
		getJob(sequence).schedule();
	}

	/**
	 * Determines a sequence out of the given parameters.
	 * 
	 * <p>
	 * Note the instrument and fretboard positions may be null. These parameters
	 * are only necessary to add potential doubled string notes to the sequence.
	 * </p>
	 * 
	 * @param notes
	 *            the list of notes to play, must not be null
	 * @param fretboardPositions
	 *            the list of the corresponding fretboard postions of the list
	 *            of notes
	 * @param instrument
	 *            the instrument
	 * @param midiInstrumentNumber
	 *            the MIDI instrument number
	 * @param toneLength
	 *            the tone length
	 * @param toneDistance
	 *            the tone distance
	 * @param playPattern
	 *            the play pattern
	 * @param addMetaMessages
	 *            true if the meta messages shall be added, or false otherwise
	 * @return a sequence out of the given parameters
	 * 
	 * @throws InvalidMidiDataException
	 */
	private Sequence determineSequence(final List<Note> notes, final List<FretboardPosition> fretboardPositions,
			final Instrument instrument, final int midiInstrumentNumber, final int toneLength, final int toneDistance,
			final String playPattern, final boolean addMetaMessages) throws InvalidMidiDataException {

		if (notes == null) {
			throw new IllegalArgumentException();
		}

		// build sequence
		final Sequence sequence = new Sequence(Sequence.PPQ, PPQS);
		final Track track = sequence.createTrack();
		long currentTick = 0;
		ShortMessage msg;

		// calculate real tone values
		final int theToneLength = (int) (toneLength / 1000.0 * PPQS);
		final int theToneDistance = (int) (toneDistance / 1000.0 * PPQS);

		// set instrument
		msg = new ShortMessage();
		msg.setMessage(ShortMessage.PROGRAM_CHANGE, 0, midiInstrumentNumber, 0);
		track.add(new MidiEvent(msg, currentTick));

		if (playPattern.equals(SoundConstants.ASCENDING_PATTERN)
				|| playPattern.equals(SoundConstants.ASCENDING_DESCENDING_PATTERN)) {
			for (int i = 0; i < notes.size(); i++) {
				final Note note = notes.get(i);
				final FretboardPosition fbp = fretboardPositions != null ? fretboardPositions.get(i) : null;
				currentTick = addNoteToTrack(note, fbp, instrument, track, currentTick, theToneLength, theToneDistance,
						volume, addMetaMessages);
			}
		}

		if (!playPattern.equals(SoundConstants.ASCENDING_PATTERN)) {
			for (int i = notes.size() - 1; i >= 0; i--) {
				if (i == notes.size() - 1 && playPattern.equals(SoundConstants.ASCENDING_DESCENDING_PATTERN)) {
					continue;
				}
				final Note note = notes.get(i);
				final FretboardPosition fbp = fretboardPositions != null ? fretboardPositions.get(i) : null;
				currentTick = addNoteToTrack(note, fbp, instrument, track, currentTick, theToneLength, theToneDistance,
						volume, addMetaMessages);
			}
		}

		if (playPattern.equals(SoundConstants.DESCENDING_ASCENDING_PATTERN)) {
			for (int i = 1; i < notes.size(); i++) {
				final Note note = notes.get(i);
				final FretboardPosition fbp = fretboardPositions != null ? fretboardPositions.get(i) : null;
				currentTick = addNoteToTrack(note, fbp, instrument, track, currentTick, theToneLength, theToneDistance,
						volume, addMetaMessages);
			}
		}

		return sequence;
	}

	/**
	 * A job to play sound.
	 */
	private class PlaySoundJob extends Job {

		private final Sequence sequence;

		public PlaySoundJob(final Sequence sequence) {
			super(SoundMessages.SoundMachine_job_name);
			this.sequence = sequence;
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {

			try {

				if (sequencer == null) {
					sequencer = Activator.getDefault().getSequencer();
					if (sequencer != null) {
						sequencer.addMetaEventListener(SoundMachine.this);
					} else {
						return Status.CANCEL_STATUS;
					}
				}

				// play sequence
				sequencer.setSequence(sequence);
				sequencer.start();

				while (true) {
					wakeUp(100);
					if (!sequencer.isRunning()) {
						break;
					}
				}

				// stop sequencer
				if (sequencer.isRunning()) {
					sequencer.stop();
				}

			} catch (final InvalidMidiDataException e) {
			}
			return Status.OK_STATUS;
		}

		public void stop() {
			if (sequencer != null && sequencer.isRunning()) {
				sequencer.stop();
			}
		}

	}

	@Override
	public void meta(final MetaMessage msg) {
		if (msg.getType() != META_TYPE_NOTE_INFO) {
			return;
		}

		final byte[] data = msg.getData();
		final boolean noteOn = data[0] == NOTE_ON;
		final Note note = Factory.getInstance().getNote(data[2], data[1]);
		final FretboardPosition fbp = data[3] != -1 && data[4] != -1 ? new FretboardPosition(data[3], data[4]) : null;

		notifyListeners(noteOn, note, fbp);
	}

	/**
	 * Returns a new instance of a play sound job.
	 * 
	 * @param sequence
	 *            the sequence to play, must not be null
	 * 
	 * @return a new instance of a play sound job
	 */
	private Job getJob(final Sequence sequence) {
		if (sequence == null) {
			throw new IllegalArgumentException();
		}

		if (job != null && job.getState() == Job.RUNNING) {
			job.stop();
			notifyListenersAboutJobStop();
		}
		job = new PlaySoundJob(sequence);
		return job;
	}

	/* --- helper methods --- */

	private long addNoteToTrack(final Note note, final FretboardPosition fretboardPosition,
			final Instrument instrument, final Track track, final long currentTick, final int toneLength,
			final int theToneDistance, final int volume, final boolean addMetaMessages) throws InvalidMidiDataException {

		long tick = currentTick;
		final int noteNumber = getMidiNoteNumber(note);
		final byte[] noteInfo = { NOTE_ON, (byte) note.getLevel(), (byte) note.getValue(),
				fretboardPosition != null ? (byte) fretboardPosition.getString() : -1,
				fretboardPosition != null ? (byte) fretboardPosition.getFret() : -1 };

		MetaMessage meta;
		if (addMetaMessages) {
			meta = new MetaMessage();
			meta.setMessage(META_TYPE_NOTE_INFO, noteInfo, noteInfo.length);
			track.add(new MidiEvent(meta, tick));
		}

		ShortMessage msg = new ShortMessage();
		msg.setMessage(ShortMessage.NOTE_ON, 0, noteNumber, volume);
		track.add(new MidiEvent(msg, tick));

		msg = new ShortMessage();
		msg.setMessage(ShortMessage.NOTE_OFF, 0, noteNumber, 0);
		track.add(new MidiEvent(msg, tick + toneLength));

		if (addMetaMessages) {
			meta = new MetaMessage();
			noteInfo[0] = NOTE_OFF;
			meta.setMessage(META_TYPE_NOTE_INFO, noteInfo, noteInfo.length);
			track.add(new MidiEvent(meta, tick + toneLength));
		}

		// add doubled emptry string note (with octave jump) if existing
		if (instrument != null && instrument.hasDoubledStrings() && fretboardPosition != null) {

			final int doubledNoteNumber = instrument.isDoubledStringWithOctaveJump(fretboardPosition.getString() + 1) ? getMidiNoteNumber(Factory
					.getInstance().getNote(note.getValue(), note.getLevel() + 1)) : noteNumber;

			if (instrument.isDoubledStringWithOctaveJump(fretboardPosition.getString() + 1)) {
				noteInfo[1] = (byte) (noteInfo[1] + 1);
			}

			if (addMetaMessages) {
				meta = new MetaMessage();
				noteInfo[0] = NOTE_ON;
				meta.setMessage(META_TYPE_NOTE_INFO, noteInfo, noteInfo.length);
				track.add(new MidiEvent(meta, tick));
			}

			msg = new ShortMessage();
			msg.setMessage(ShortMessage.NOTE_ON, 0, doubledNoteNumber, volume);
			track.add(new MidiEvent(msg, tick));

			msg = new ShortMessage();
			msg.setMessage(ShortMessage.NOTE_OFF, 0, doubledNoteNumber, 0);
			track.add(new MidiEvent(msg, tick + toneLength));

			if (addMetaMessages) {
				meta = new MetaMessage();
				noteInfo[0] = NOTE_OFF;
				meta.setMessage(META_TYPE_NOTE_INFO, noteInfo, noteInfo.length);
				track.add(new MidiEvent(meta, tick + toneLength));
			}
		}

		tick += theToneDistance;
		return tick;
	}

	/**
	 * Returns the corresponding midi note number for a given note.
	 * 
	 * @param note
	 *            the note, must not be null
	 * 
	 * @return the corresponding midi note number for a given note
	 */
	private int getMidiNoteNumber(final Note note) {
		if (note == null) {
			throw new IllegalArgumentException();
		}

		return (note.getLevel() + 1) * 12 + note.getValue();
	}

	public void dispose() {
		if (sequencer != null) {
			sequencer.removeMetaEventListener(this);
		}
	}

	/* --- getter & setter methods --- */

	public int getMidiInstrument() {
		return midiInstrument;
	}

	public void setMidiInstrument(final int midiInstrument) {
		this.midiInstrument = midiInstrument;
	}

	public int getMidiKeyboard() {
		return midiKeyboard;
	}

	public void setMidiKeyboard(final int midiKeyboard) {
		this.midiKeyboard = midiKeyboard;
	}

	public int getTestToneLength() {
		return testToneLength;
	}

	public void setTestToneLength(final int testToneLength) {
		this.testToneLength = testToneLength;
	}

	public int getTestToneDistance() {
		return testToneDistance;
	}

	public void setTestToneDistance(final int testToneDistance) {
		this.testToneDistance = testToneDistance;
	}

	public String getTestPlayPattern() {
		return testPlayPattern;
	}

	public void setTestPlayPattern(final String testPlayPattern) {
		this.testPlayPattern = testPlayPattern;
	}

	public int getChordToneLength() {
		return chordToneLength;
	}

	public void setChordToneLength(final int chordToneLength) {
		this.chordToneLength = chordToneLength;
	}

	public int getChordToneDistance() {
		return chordToneDistance;
	}

	public void setChordToneDistance(final int chordToneDistance) {
		this.chordToneDistance = chordToneDistance;
	}

	public String getChordPlayPattern() {
		return chordPlayPattern;
	}

	public void setChordPlayPattern(final String chordPlayPattern) {
		this.chordPlayPattern = chordPlayPattern;
	}

	public int getBlockToneLength() {
		return blockToneLength;
	}

	public void setBlockToneLength(final int blockToneLength) {
		this.blockToneLength = blockToneLength;
	}

	public int getBlockToneDistance() {
		return blockToneDistance;
	}

	public void setBlockToneDistance(final int blockToneDistance) {
		this.blockToneDistance = blockToneDistance;
	}

	public String getBlockPlayPattern() {
		return blockPlayPattern;
	}

	public void setBlockPlayPattern(final String blockPlayPattern) {
		this.blockPlayPattern = blockPlayPattern;
	}

	public int getFretboardToneLength() {
		return fretboardToneLength;
	}

	public void setFretboardToneLength(final int fretboardToneLength) {
		this.fretboardToneLength = fretboardToneLength;
	}

	public int getKeyboardToneLength() {
		return keyboardToneLength;
	}

	public void setKeyboardToneLength(final int keyboardToneLength) {
		this.keyboardToneLength = keyboardToneLength;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(final int volume) {
		this.volume = volume;
	}
}
