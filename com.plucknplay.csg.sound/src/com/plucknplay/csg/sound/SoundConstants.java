/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.sound;

public interface SoundConstants {

	int[] MIDI_INSTRUMENT_NUMBERS = { 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
			45, 104, 105, 106, 107 };

	String[] MIDI_INSTRUMENTS = { SoundMessages.Instrument_acoustic_guitar_nylon,
			SoundMessages.Instrument_acoustic_giutar_steel, SoundMessages.Instrument_electric_guitar_jazz,
			SoundMessages.Instrument_electric_guitar_clean, SoundMessages.Instrument_electric_guitar_muted,
			SoundMessages.Instrument_overdriven_guitar, SoundMessages.Instrument_distortion_guitar,
			SoundMessages.Instrument_guitar_harmonics, SoundMessages.Instrument_acoustic_bass,
			SoundMessages.Instrument_electric_bass_finger, SoundMessages.Instrument_electric_bass_pick,
			SoundMessages.Instrument_fretless_bass, SoundMessages.Instrument_slap_bass_1,
			SoundMessages.Instrument_slap_bass_2, SoundMessages.Instrument_synth_bass_1,
			SoundMessages.Instrument_synth_bass_2, SoundMessages.Instrument_violin, SoundMessages.Instrument_viola,
			SoundMessages.Instrument_cello, SoundMessages.Instrument_contrabass,
			SoundMessages.Instrument_pizzicato_strings, SoundMessages.Instrument_sitar, SoundMessages.Instrument_banjo,
			SoundMessages.Instrument_shamisen, SoundMessages.Instrument_koto };

	int[] MIDI_KEYBOARD_INSTRUMENT_NUMBERS = { 0, 1, 2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 20 };

	String[] MIDI_KEYBOARD_INSTRUMENTS = { SoundMessages.Instrument_acoustic_grand_piano,
			SoundMessages.Instrument_bright_acoustic_piano, SoundMessages.Instrument_electric_grand_piano,
			SoundMessages.Instrument_honky_tonk_piano, SoundMessages.Instrument_electric_piano_1,
			SoundMessages.Instrument_electric_piano_2, SoundMessages.Instrument_harpsichord,
			SoundMessages.Instrument_clavichord, SoundMessages.Instrument_organ,
			SoundMessages.Instrument_percussive_organ, SoundMessages.Instrument_rock_organ,
			SoundMessages.Instrument_church_organ, SoundMessages.Instrument_reed_organ };

	String ASCENDING_PATTERN = "Ascending"; //$NON-NLS-1$
	String DESCENDING_PATTERN = "Descending"; //$NON-NLS-1$
	String ASCENDING_DESCENDING_PATTERN = "Ascending-Descending"; //$NON-NLS-1$
	String DESCENDING_ASCENDING_PATTERN = "Descending-Ascending"; //$NON-NLS-1$

	int DEFAULT_MIDI_INSTRUMENT = 24;
	int DEFAULT_MIDI_KEYBOARD = 0;
	int DEFAULT_TONE_LENGTH = 2000;
	int DEFAULT_TONE_DISTANCE = 500;
	String DEFAULT_PLAY_PATTERN = ASCENDING_PATTERN;
	int DEFAULT_CHORD_TONE_DISTANCE = 0;
	int DEFAULT_VOLUME = 70;
}
