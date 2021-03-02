/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.Iterator;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.util.ToneRangeMode;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.model.KeyboardDraft;

public abstract class AbstractKeyboardLayer extends AntiAliasedLayer {

	private final KeyboardDraft keyboardDraft;

	public AbstractKeyboardLayer(final KeyboardDraft keyboardDraft) {
		this.keyboardDraft = keyboardDraft;
	}

	public KeyboardDraft getKeyboardDraft() {
		return keyboardDraft;
	}

	public Iterator<Note> toneRangeIterator() {
		return keyboardDraft.getToneRange().iterator();
	}

	public boolean showOnlyRelativeNotes() {
		return ToneRangeMode.valueOf(
				Activator.getDefault().getPreferenceStore().getString(Preferences.KEYBOARD_VIEW_TONE_RANGE_MODE))
				.showOnlyRelativeNotes();
	}

	/**
	 * Refreshs this layer.
	 * 
	 * @param reInitialize
	 *            true if the layer shall be initialized again, false otherwise
	 */
	public void refresh(final boolean reInitialize) {
		if (reInitialize) {
			init();
		}
		drawInput();
	}

	/**
	 * Initializes the layer.
	 */
	protected abstract void init();

	/**
	 * Draws the input.
	 */
	protected abstract void drawInput();
}
