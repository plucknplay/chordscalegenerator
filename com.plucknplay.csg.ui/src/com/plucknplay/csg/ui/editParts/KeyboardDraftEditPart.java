/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.enums.IntervalNamesMode;
import com.plucknplay.csg.sound.ISoundListener;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.figures.INoteFigure;
import com.plucknplay.csg.ui.figures.KeyboardKeysLayer;
import com.plucknplay.csg.ui.figures.KeyboardMouseListener;
import com.plucknplay.csg.ui.figures.KeyboardNoteFigure;
import com.plucknplay.csg.ui.figures.KeyboardNotesLayer;
import com.plucknplay.csg.ui.model.KeyboardDraft;
import com.plucknplay.csg.ui.util.enums.KeySizeMode;

public class KeyboardDraftEditPart extends AbstractDraftEditPart implements ISoundListener {

	private static final Object KEYS_LAYER = new Object();
	private static final Object NOTES_LAYER = new Object();

	private final IPreferenceStore prefs;

	private String mode;

	private KeyboardKeysLayer keysLayer;
	private KeyboardNotesLayer notesLayer;

	private IntervalNameEditManager lastIntervalNameEditManager;

	public KeyboardDraftEditPart(final String mode) {
		this.mode = mode;
		prefs = Activator.getDefault().getPreferenceStore();
	}

	public void setMode(final String mode) {
		this.mode = mode;
		notesLayer.setMode(mode);
		if (!UIConstants.MODE_INTERVALS.equals(mode)) {
			bringDownIntervalNameEditManager();
		}
	}

	@Override
	public void activate() {
		super.activate();
		getCastedModel().addSimpleChangeListener(this);
		Activator.getDefault().getSoundMachine().addChangeListener(this);
	}

	/**
	 * Returns the model object casted to KeyboardDraft.
	 * 
	 * @return the model object casted to KeyboardDraft
	 */
	private KeyboardDraft getCastedModel() {
		return (KeyboardDraft) getModel();
	}

	@Override
	protected void addLayers(final ScalableLayeredPane pane) {

		// add first layer - keyboard
		keysLayer = new KeyboardKeysLayer(getCastedModel());
		pane.add(keysLayer, KEYS_LAYER, 0);

		// add second layer - notes
		notesLayer = new KeyboardNotesLayer(getCastedModel());
		notesLayer.setMode(mode);
		pane.addLayerAfter(notesLayer, NOTES_LAYER, KEYS_LAYER);

		pane.addMouseListener(new KeyboardMouseListener(null) {
			@Override
			public void mousePressed(final MouseEvent me) {
				if (!isEditable()) {
					IFigure figure = pane.findFigureAt(me.x, me.y);
					if (figure instanceof Label) {
						figure = figure.getParent();
					}
					if (figure instanceof INoteFigure) {
						setNote(((INoteFigure) figure).getNote());
						super.mousePressed(me);
					}
				}
			}
		});
	}

	@Override
	public IFigure getContentPane() {
		return notesLayer;
	}

	@Override
	protected double getMinScaleFactor() {
		return IFigureConstants.KEYBOARD_MIN_SCALE_FACTOR;
	}

	@Override
	public int getNormWidth() {
		return KeySizeMode.valueOf(prefs.getString(Preferences.KEYBOARD_VIEW_KEY_SIZE)).getNormWidth(
				getCastedModel().getKeyNumber());
	}

	@Override
	public int getNormHeight() {
		return KeySizeMode.valueOf(prefs.getString(Preferences.KEYBOARD_VIEW_KEY_SIZE)).getNormHeight(
				getCastedModel().getKeyNumber());
	}

	@Override
	public void refresh() {
		keysLayer.refresh(true);
		notesLayer.refresh(true);
		super.refresh();
	}

	public void refreshKeys(final boolean reInitialize) {
		keysLayer.refresh(reInitialize);
	}

	public void refreshNotes(final boolean reInitialize) {
		notesLayer.refresh(reInitialize);
	}

	@Override
	public void notifyChange(final Object property, final Object value) {
		if (!getCastedModel().isEditable()) {
			return;
		}

		if (KeyboardDraft.PROP_ROOT_NOTE_CHANGED.equals(property)
				|| KeyboardDraft.PROP_INTERVAL_NAME_CHANGED.equals(property)
				|| KeyboardDraft.PROP_KEYBOARD_NOTE_ADDED.equals(property)
				|| KeyboardDraft.PROP_KEYBOARD_NOTE_REMOVED.equals(property)) {
			notesLayer.refresh(false);
		}
	}

	@Override
	public void soundChanged(final boolean noteOn, final Note note, final FretboardPosition fbp) {
		if (!getCastedModel().isEditable()) {
			notesLayer.playNote(noteOn, note);
		}
	}

	@Override
	public void stopSound() {
		notesLayer.drawInputInUIJob();
	}

	@Override
	public void deactivate() {
		Activator.getDefault().getSoundMachine().removeChangeListener(this);
		getCastedModel().removeSimpleChangeListener(this);
		super.deactivate();
	}

	@Override
	public void performRequest(final Request request) {

		if (!isEditable() || !(request instanceof SelectionRequest)) {
			return;
		}

		final SelectionRequest selectionRequest = (SelectionRequest) request;
		final Point location = selectionRequest.getLocation().getCopy();
		final IFigure figure = getFigure();
		figure.translateToRelative(location);
		IFigure selectedFigure = figure.findFigureAt(location);

		if (selectedFigure instanceof Label) {
			selectedFigure = selectedFigure.getParent();
		}

		if (selectedFigure instanceof INoteFigure) {
			final Note note = ((INoteFigure) selectedFigure).getNote();

			if (selectedFigure instanceof KeyboardNoteFigure && getCastedModel().getAbsoluteNotes().contains(note)
					&& UIConstants.MODE_INTERVALS.equals(mode)) {

				final KeyboardNoteFigure notesFigure = (KeyboardNoteFigure) selectedFigure;
				final String text = notesFigure.getText();
				if (text != null
						&& !"".equals(text)
						&& IntervalNamesMode.valueOf(prefs.getString(Preferences.INTERVAL_NAMES_MODE))
								.getInterval(text).getHalfsteps() != 0) {

					final IntervalNameEditManager manager = new IntervalNameEditManager(this, notesFigure,
							getCastedModel(), getScale(), SWT.CENTER);
					lastIntervalNameEditManager = manager;
					manager.show();
				}

			} else {
				getCastedModel().setAbsoluteNote(note, addRemoveAllNotes(selectionRequest));
			}
		}
	}

	@Override
	public void bringDownEditManager() {
		bringDownIntervalNameEditManager();
	}

	private void bringDownIntervalNameEditManager() {
		if (lastIntervalNameEditManager != null) {
			lastIntervalNameEditManager.bringDown();
		}
	}
}
