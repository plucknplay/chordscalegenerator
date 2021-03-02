/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
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
import com.plucknplay.csg.ui.figures.BlockLayer;
import com.plucknplay.csg.ui.figures.FretNumbersLayer;
import com.plucknplay.csg.ui.figures.FretboardBackgroundLayer;
import com.plucknplay.csg.ui.figures.FretboardLayer;
import com.plucknplay.csg.ui.figures.FretboardNotesFigure;
import com.plucknplay.csg.ui.figures.FretboardNotesLayer;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.figures.IntarsiaLayer;
import com.plucknplay.csg.ui.model.FretDraft;
import com.plucknplay.csg.ui.model.FretboardDraft;
import com.plucknplay.csg.ui.util.enums.Position;

public class FretboardDraftEditPart extends FretDraftEditPart implements ISoundListener {

	private static final Object BACKGROUND_LAYER = new Object();
	private static final Object FRETBOARD_LAYER = new Object();
	private static final Object INLAYS_LAYER = new Object();
	private static final Object FRET_NUMBERS_LAYER = new Object();
	private static final Object BLOCK_LAYER = new Object();
	private static final Object NOTES_LAYER = new Object();

	private final IPreferenceStore prefs;

	private String mode;

	private FretboardBackgroundLayer backgroundLayer;
	private FretboardLayer fretboardLayer;
	private IntarsiaLayer inlaysLayer;
	private FretNumbersLayer fretNumbersLayer;
	private BlockLayer blockLayer;
	private FretboardNotesLayer notesLayer;

	private FingerNumberEditManager lastFingerNumberEditManager;
	private IntervalNameEditManager lastIntervalNameEditManager;

	private SelectionRequest selectionRequest;

	public FretboardDraftEditPart(final String mode) {
		this.mode = mode;
		prefs = Activator.getDefault().getPreferenceStore();
	}

	public void setMode(final String mode) {
		this.mode = mode;
		notesLayer.setMode(mode);
		if (!UIConstants.MODE_FINGERING.equals(mode)) {
			bringDownFingerNumberEditManager();
		}
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
	 * Returns the model object casted to FretboardDraft.
	 * 
	 * @return the model object casted to FretboardDraft
	 */
	private FretboardDraft getCastedModel() {
		return (FretboardDraft) getModel();
	}

	@Override
	protected void addLayers(final ScalableLayeredPane pane) {

		// add first layer - background
		backgroundLayer = new FretboardBackgroundLayer(getCastedModel());
		pane.add(backgroundLayer, BACKGROUND_LAYER, 0);

		// add second layer - inlays
		inlaysLayer = new IntarsiaLayer(getCastedModel());
		pane.addLayerAfter(inlaysLayer, INLAYS_LAYER, BACKGROUND_LAYER);
		inlaysLayer.setVisible(prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_INLAYS));

		// add third layer - fretboard
		fretboardLayer = new FretboardLayer(getCastedModel());
		pane.addLayerAfter(fretboardLayer, FRETBOARD_LAYER, INLAYS_LAYER);

		// add fourth layer - block
		blockLayer = new BlockLayer(getCastedModel());
		pane.addLayerAfter(blockLayer, BLOCK_LAYER, FRETBOARD_LAYER);

		// add fifth layer - fret numbers
		fretNumbersLayer = new FretNumbersLayer(getCastedModel());
		pane.addLayerAfter(fretNumbersLayer, FRET_NUMBERS_LAYER, BLOCK_LAYER);
		fretNumbersLayer.setVisible(prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_FRET_NUMBERS));

		// add sixth layer - notes
		notesLayer = new FretboardNotesLayer(getCastedModel());
		notesLayer.setMode(mode);
		pane.addLayerAfter(notesLayer, NOTES_LAYER, FRET_NUMBERS_LAYER);
	}

	@Override
	public IFigure getContentPane() {
		return notesLayer;
	}

	@Override
	protected double getMinScaleFactor() {
		return 0.6;
	}

	@Override
	public int getNormWidth() {
		if (getCurrentInstrument() == null) {
			return 0;
		}
		final int fretcount = getCurrentInstrument().getFretCount()
				+ (prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE) ? 2 : 1);
		return 2 * IFigureConstants.FRETBOARD_OFFSET_X + fretcount * IFigureConstants.FRET_WIDTH;
	}

	@Override
	public int getNormHeight() {
		if (getCurrentInstrument() == null) {
			return 0;
		}
		int result = 2 * IFigureConstants.FRETBOARD_OFFSET_Y + (getCurrentInstrument().getStringCount() - 1)
				* IFigureConstants.FRET_HEIGHT;

		if (prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_INLAYS)
				&& Position.valueOf(prefs.getString(Preferences.FRETBOARD_VIEW_INLAYS_POSITION)) != Position.CENTER) {
			result += IFigureConstants.FRETBOARD_OFFSET_Y2;
		}
		if (prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_FRET_NUMBERS)) {
			result += IFigureConstants.FRETBOARD_OFFSET_Y2;
		}

		return result;
	}

	@Override
	public void refresh() {
		refreshBackground();
		refreshInlays();
		refreshFretboard();
		refreshBlock();
		refreshFretNumbers();
		refreshNotes(true);
		super.refresh();
	}

	public void refreshBackground() {
		backgroundLayer.repaint();
	}

	public void setInlaysVisible(final boolean visible) {
		inlaysLayer.setVisible(visible);
	}

	public void setFretNumbersVisible(final boolean visible) {
		fretNumbersLayer.setVisible(visible);
	}

	public void refreshInlays() {
		inlaysLayer.repaint();
	}

	public void refreshFretboard() {
		fretboardLayer.repaint();
	}

	public void refreshBlock() {
		blockLayer.repaint();
	}

	public void refreshFretNumbers() {
		fretNumbersLayer.initFretNumbers();
	}

	public void refreshNotes(final boolean reInitialize) {
		notesLayer.refresh(reInitialize);
	}

	@Override
	public void notifyChange(final Object property, final Object value) {
		if (!getCastedModel().isEditable()) {
			return;
		}

		if (FretboardDraft.PROP_ASSIGNMENT_CHANGED.equals(property)
				|| FretboardDraft.PROP_ROOT_NOTE_CHANGED.equals(property)
				|| FretboardDraft.PROP_SHOW_BARRE_CHANGED.equals(property)
				|| FretboardDraft.PROP_INTERVAL_NAME_CHANGED.equals(property)) {
			refresh();
		}

		if (Activator.PROP_HAND_CHANGED.equals(property)) {
			bringDownEditManager();
		}
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

		selectionRequest = (SelectionRequest) request;
		final Point location = selectionRequest.getLocation().getCopy();
		final IFigure figure = getFigure();
		figure.translateToRelative(location);
		IFigure selectedFigure = figure.findFigureAt(location);

		if (selectedFigure instanceof Label) {

			final Label label = (Label) selectedFigure;
			final IFigure parent = label.getParent();

			if (parent != null && !(parent instanceof FretboardNotesLayer)) {
				// in case, label of notes figure was found, reset
				// selectedFigure
				selectedFigure = parent;
			} else {
				editFingering(notesLayer.getFretboardPosition(label));
			}
		}

		if (selectedFigure instanceof FretboardNotesFigure) {
			editFingering(notesLayer.getFretboardPosition((FretboardNotesFigure) selectedFigure), selectedFigure);
		}
	}

	@Override
	public void editFingering(final FretboardPosition fbp, final IFigure figure) {

		// edit fingering
		if (fbp != null && fbp.getFret() != 0 && fbp.getFret() != getCurrentInstrument().getFretCount() + 1
				&& UIConstants.MODE_FINGERING.equals(mode)
				&& (figure == null || figure instanceof FretboardNotesFigure)) {
			final FretboardNotesFigure fretboardNotesFigure = figure == null ? notesLayer.getFretboardNotesFigure(fbp)
					: (FretboardNotesFigure) figure;
			if (fretboardNotesFigure != null) {
				final FingerNumberEditManager manager = new FingerNumberEditManager(this, fretboardNotesFigure,
						getCastedModel(), fbp, getScale());
				lastFingerNumberEditManager = manager;
				manager.show();
			}
		}

		// edit interval
		else if (UIConstants.MODE_INTERVALS.equals(mode) && figure != null && figure instanceof FretboardNotesFigure) {
			final FretboardNotesFigure notesFigure = (FretboardNotesFigure) figure;
			if (!"".equals(notesFigure.getText())
					&& IntervalNamesMode.valueOf(prefs.getString(Preferences.INTERVAL_NAMES_MODE))
							.getInterval(notesFigure.getText()).getHalfsteps() != 0) {

				final IntervalNameEditManager manager = new IntervalNameEditManager(this, notesFigure,
						getCastedModel(), getScale(), SWT.CENTER);
				lastIntervalNameEditManager = manager;
				manager.show();
			}

			// add/remove assignment
		} else if (fbp != null) {
			getCastedModel().toggleAssignment(fbp.getFret(), fbp.getString() + 1, addRemoveAllNotes(selectionRequest));
		}
	}

	@Override
	public void bringDownEditManager() {
		bringDownFingerNumberEditManager();
		bringDownIntervalNameEditManager();
	}

	private void bringDownFingerNumberEditManager() {
		if (lastFingerNumberEditManager != null) {
			lastFingerNumberEditManager.bringDown();
		}
	}

	private void bringDownIntervalNameEditManager() {
		if (lastIntervalNameEditManager != null) {
			lastIntervalNameEditManager.bringDown();
		}
	}

	@Override
	public void soundChanged(final boolean noteOn, final Note note, final FretboardPosition fbp) {
		if (!getCastedModel().isEditable()) {
			notesLayer.playNote(noteOn, fbp);
		}
	}

	@Override
	public void stopSound() {
		if (!getCastedModel().isEditable()) {
			notesLayer.drawInputInUIJob();
		}
	}

	@Override
	protected FretDraft getFretDraft() {
		return getCastedModel();
	}

	@Override
	protected boolean showFingering() {
		return UIConstants.MODE_FINGERING.equals(mode);
	}
}
