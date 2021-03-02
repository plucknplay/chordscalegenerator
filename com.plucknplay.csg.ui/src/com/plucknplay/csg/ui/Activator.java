/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.enums.IntervalNamesMode;
import com.plucknplay.csg.core.model.enums.NoteNamesMode;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.core.util.NameProvider;
import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.sound.SoundMachine;
import com.plucknplay.csg.ui.listeners.ISimpleChangeListener;
import com.plucknplay.csg.ui.util.AdapterFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.plucknplay.csg.ui"; //$NON-NLS-1$

	public static final Object PROP_HAND_CHANGED = new Object();
	public static final Object PROP_RESULTS_FLUSHED = new Object();

	private static Activator plugin;

	private List<ISimpleChangeListener> listeners;
	private boolean leftHander;
	private AdapterFactory adapterFactory;
	private SoundMachine soundMachine;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
		leftHander = false;
		ChordList.getInstance().setCurrentRootNote(
				Factory.getInstance().getNote(getPreferenceStore().getInt(Preferences.ROOT_NOTE)));
		ScaleList.getInstance().setCurrentRootNote(
				Factory.getInstance().getNote(getPreferenceStore().getInt(Preferences.ROOT_NOTE)));
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);

		// register adapter factory
		adapterFactory = new AdapterFactory();
		final IAdapterManager mgr = Platform.getAdapterManager();
		mgr.registerAdapters(adapterFactory, Category.class);
		mgr.registerAdapters(adapterFactory, Instrument.class);
		mgr.registerAdapters(adapterFactory, Chord.class);
		mgr.registerAdapters(adapterFactory, Scale.class);

		// set global options
		final IPreferenceStore prefs = getPreferenceStore();
		setLeftHander(prefs.getBoolean(Preferences.LEFT_HANDER));
		NoteNamesMode.setHName(prefs.getString(Preferences.GENERAL_H_NOTE_NAME));
		NoteNamesMode.setBName(prefs.getString(Preferences.GENERAL_B_NOTE_NAME));
		NamesUtil.setNoteNamesMode(NoteNamesMode.valueOf(prefs.getString(Preferences.ABSOLUTE_NOTE_NAMES_MODE)));
		IntervalNamesMode.setUnisonIntervalName(prefs
				.getBoolean(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME) ? prefs
				.getString(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME) : null);
		IntervalNamesMode.setUseDeltaInMajorIntervalNames(prefs
				.getBoolean(Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS));
		NamesUtil.setIntervalNamesMode(IntervalNamesMode.valueOf(prefs.getString(Preferences.INTERVAL_NAMES_MODE)));

		final NameProvider nameProvider = NamesUtil.getNameProvider();
		nameProvider.setUseScaleNameSeparator(prefs.getBoolean(Preferences.SCALE_NAMES_USE_SEPARATOR));
		nameProvider.setScaleNameSeparatorMode(prefs.getString(Preferences.SCALE_NAMES_SEPARATOR));
		nameProvider.setUseChordNameSeparator(prefs.getBoolean(Preferences.CHORD_NAMES_USE_SEPARATOR));
		nameProvider.setChordNameSeparatorMode(prefs.getString(Preferences.CHORD_NAMES_SEPARATOR));
		nameProvider.setIntervalsShortMode(prefs.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_SHORT_MODE));
		nameProvider.setChordNamePrefix(prefs.getString(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_PREFIX_MODE));
		nameProvider.setBlankSpaceBetweenPrefixAndIntervals(prefs
				.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_PREFIX_AND_INTERVALS));
		nameProvider.setBlankSpaceBetweenIntervals(prefs
				.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_INTERVALS));
		nameProvider.setCompactMode(prefs.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_COMPACT_MODE));
		nameProvider.setIntervalsInBrackets(prefs.getBoolean(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_IN_BRACKETS));
		nameProvider.setBracketsMode(prefs.getString(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BRACKETS_MODE));

		final int rootNote = prefs.getInt(Preferences.ROOT_NOTE);
		ChordList.getInstance().setCurrentRootNote(Factory.getInstance().getNote(rootNote));
		ScaleList.getInstance().setCurrentRootNote(Factory.getInstance().getNote(rootNote));

		Instrument.setFretNumber(prefs.getInt(Preferences.FRET_NUMBER));
		Instrument.setCapoFret(prefs.getInt(Preferences.CAPO_FRET));
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {

		// unregister adapter factory
		Platform.getAdapterManager().unregisterAdapters(adapterFactory);
		adapterFactory = null;

		// save preferences
		try {
			plugin = null;
			savePluginPreferences();
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

	public SoundMachine getSoundMachine() {
		if (soundMachine == null) {
			soundMachine = SoundMachine.getInstance();

			// set sound options
			final IPreferenceStore prefs = getPreferenceStore();
			soundMachine.setMidiInstrument(prefs.getInt(Preferences.SOUND_DEFAULT_MIDI_INSTRUMENT));
			soundMachine.setMidiKeyboard(prefs.getInt(Preferences.SOUND_KEYBOARD_INSTRUMENT));
			soundMachine.setTestToneLength(prefs.getInt(Preferences.SOUND_TEST_TONE_LENGTH));
			soundMachine.setChordToneLength(prefs.getInt(Preferences.SOUND_CHORD_TONE_LENGTH));
			soundMachine.setBlockToneLength(prefs.getInt(Preferences.SOUND_BLOCK_TONE_LENGTH));
			soundMachine.setFretboardToneLength(prefs.getInt(Preferences.SOUND_FRETBOARD_VIEW_TONE_LENGTH));
			soundMachine.setKeyboardToneLength(prefs.getInt(Preferences.SOUND_KEYBOARD_VIEW_TONE_LENGTH));
			soundMachine.setTestToneDistance(prefs.getInt(Preferences.SOUND_TEST_TONE_DISTANCE));
			soundMachine.setChordToneDistance(prefs.getInt(Preferences.SOUND_CHORD_TONE_DISTANCE));
			soundMachine.setBlockToneDistance(prefs.getInt(Preferences.SOUND_BLOCK_TONE_DISTANCE));
			soundMachine.setTestPlayPattern(prefs.getString(Preferences.SOUND_TEST_PLAY_PATTERN));
			soundMachine.setChordPlayPattern(prefs.getString(Preferences.SOUND_CHORD_PLAY_PATTERN));
			soundMachine.setBlockPlayPattern(prefs.getString(Preferences.SOUND_BLOCK_PLAY_PATTERN));
			soundMachine.setVolume(prefs.getInt(Preferences.SOUND_VOLUME));
		}

		return soundMachine;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns an image for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image
	 */
	public Image getImage(final String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			image = Activator.getImageDescriptor(path).createImage();
			getImageRegistry().put(path, image);
		}
		return image;
	}

	/**
	 * Sets the context-sensitive help for the given control.
	 * 
	 * @param control
	 *            the control
	 * @param helpId
	 *            the corresponding context help id
	 */
	public void setHelp(final Control control, final String helpId) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(control, PLUGIN_ID + "." + helpId); //$NON-NLS-1$
	}

	/**
	 * Register Action.
	 * 
	 * @param site
	 *            the workbench part site
	 * @param action
	 *            the action to register
	 */
	public void registerAction(final IWorkbenchPartSite site, final IAction action) {
		site.getKeyBindingService().registerAction(action);
	}

	/* --- left/right hander setting --- */

	/**
	 * Adds a simple change listener to this plugin.
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
	 * Removes a simple change listener from this plugin.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeSimpleChangeListener(final ISimpleChangeListener listener) {
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
	 * Returns true if the person who is using this application is left handed,
	 * false otherwise.
	 * 
	 * @return true if the person who is using this application is left handed,
	 *         false otherwise
	 */
	public boolean isLeftHander() {
		return leftHander;
	}

	/**
	 * Sets whether the user who is using this application is left or right
	 * handed.
	 * 
	 * @param leftHander
	 *            true if the user is left handed, false otherwise
	 */
	public void setLeftHander(final boolean leftHander) {
		if (leftHander != this.leftHander) {
			this.leftHander = leftHander;
			getPreferenceStore().setValue(Preferences.LEFT_HANDER, leftHander);
			notifyListeners(PROP_HAND_CHANGED, Boolean.valueOf(this.leftHander));
		}
	}
}
