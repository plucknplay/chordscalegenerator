/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.sound;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	public static final String PLUGIN_ID = "com.plucknplay.csg.sound"; //$NON-NLS-1$

	private static Activator plugin;

	private Synthesizer synthesizer;
	private Sequencer sequencer;

	public Activator() {
		plugin = this;
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);

		// Load Synthesizer & Sequencer
		try {
			sequencer = MidiSystem.getSequencer(false);
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			sequencer.open();
			sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
			sequencer.setTempoInBPM(60);

		} catch (final MidiUnavailableException e) {
			return;
		} catch (final SecurityException e) {
			return;
		}

		// Load Soundbank
		Soundbank soundbank = getDeluxeSoundbank();
		if (soundbank == null || !synthesizer.isSoundbankSupported(soundbank)) {
			soundbank = synthesizer.getDefaultSoundbank();
		}
		if (soundbank != null) {
			synthesizer.loadAllInstruments(soundbank);
		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {

			SoundMachine.getInstance().dispose();

			if (synthesizer != null) {
				synthesizer.close();
			}
			if (sequencer != null) {
				sequencer.close();
			}
			sequencer = null;
			synthesizer = null;
			plugin = null;

		} finally {
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static Activator getDefault() {
		return plugin;
	}

	private Soundbank getDeluxeSoundbank() {
		Soundbank result = null;
		try {
			final InputStream inputStream = FileLocator.openStream(getBundle(), new Path(
					"soundbanks/soundbank-deluxe.gm"), false); //$NON-NLS-1$
			if (inputStream != null) {
				result = MidiSystem.getSoundbank(inputStream);
			}
		} catch (final IOException e) {
		} catch (final InvalidMidiDataException e) {
		}
		return result;
	}

	/* package */Synthesizer getSynthesizer() {
		return synthesizer;
	}

	/* package */Sequencer getSequencer() {
		return sequencer;
	}
}
