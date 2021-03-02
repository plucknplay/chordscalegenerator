/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.SelectionRequest;

import com.plucknplay.csg.core.model.NotePosition;
import com.plucknplay.csg.core.model.enums.Accidental;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.figures.AccidentalFigure;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.figures.LinesLayer;
import com.plucknplay.csg.ui.figures.NotePositionAreaFigure;
import com.plucknplay.csg.ui.figures.NotePositionAreasLayer;
import com.plucknplay.csg.ui.figures.NotePositionFigure;
import com.plucknplay.csg.ui.figures.NotesLayer;
import com.plucknplay.csg.ui.model.NotesDraft;
import com.plucknplay.csg.ui.model.NotesDraftUtil;
import com.plucknplay.csg.ui.views.NotesView;

public class NotesDraftEditPart extends AbstractDraftEditPart {

	private static final Object AREAS_LAYER = new Object();
	private static final Object LINES_LAYER = new Object();
	private static final Object NOTES_LAYER = new Object();

	private final NotesView view;

	private NotePositionAreasLayer areasLayer;
	private LinesLayer linesLayer;
	private NotesLayer notesLayer;

	private String displayMode;

	public NotesDraftEditPart(final NotesView view, final String displayMode) {
		this.view = view;
		this.displayMode = displayMode;
	}

	public void setDisplayMode(final String displayMode) {
		this.displayMode = displayMode;
		areasLayer.setDisplayMode(displayMode);
		linesLayer.setDisplayMode(displayMode);
		notesLayer.setDisplayMode(displayMode);
	}

	@Override
	public void activate() {
		super.activate();
		getCastedModel().addSimpleChangeListener(this);
	}

	/**
	 * Returns the model object casted to NotesDraft.
	 * 
	 * @return the model object casted to NotesDraft
	 */
	private NotesDraft getCastedModel() {
		return (NotesDraft) getModel();
	}

	@Override
	protected void addLayers(final ScalableLayeredPane pane) {
		// add first layer - areas
		areasLayer = new NotePositionAreasLayer(getCastedModel(), displayMode);
		pane.add(areasLayer, AREAS_LAYER, 0);

		// add second layer - lines
		linesLayer = new LinesLayer(getCastedModel(), displayMode);
		pane.addLayerAfter(linesLayer, LINES_LAYER, AREAS_LAYER);

		// add third layer - notes label
		notesLayer = new NotesLayer(getCastedModel(), displayMode);
		pane.addLayerAfter(notesLayer, NOTES_LAYER, LINES_LAYER);
	}

	@Override
	public IFigure getContentPane() {
		return areasLayer;
	}

	@Override
	protected double getMinScaleFactor() {
		return IFigureConstants.NOTES_MIN_SCALE_FACTOR;
	}

	@Override
	public int getNormWidth() {
		return NotesDraftUtil.getNormWidth(getCastedModel().isEditable() ? UIConstants.DISPLAY_AS_BLOCK : displayMode,
				getCastedModel());
	}

	@Override
	public int getNormHeight() {
		return NotesDraftUtil.getNormHeight(getCastedModel());
	}

	@Override
	public void refresh() {
		super.refresh();
		areasLayer.init();
		linesLayer.repaint();
		notesLayer.init();
	}

	@Override
	public void notifyChange(final Object property, final Object value) {

		if (NotesDraft.PROP_CLEF_CHANGED.equals(property) || NotesDraft.PROP_NOTE_POSITION_ADDED.equals(property)
				|| NotesDraft.PROP_NOTE_POSITION_REMOVED.equals(property)
				|| NotesDraft.PROP_NOTE_POSITIONS_CHANGED.equals(property)
				|| NotesDraft.PROP_EDITABLE_STATE_CHANGED.equals(property)) {
			refresh();
		}

		if (NotesDraft.PROP_ROOT_NOTE_CHANGED.equals(property)
				&& (Activator.getDefault().getPreferenceStore().getBoolean(Preferences.NOTES_VIEW_HIGHLIGHT_ROOT_NOTE) || getCastedModel()
						.isEditable())) {
			notesLayer.refresh();
		}
	}

	@Override
	public void deactivate() {
		getCastedModel().removeSimpleChangeListener(this);
		super.deactivate();
	}

	@Override
	public void performRequest(final Request request) {
		if (view == null || !isEditable() || !(request instanceof SelectionRequest)) {
			return;
		}

		final SelectionRequest selectionRequest = (SelectionRequest) request;
		final Point location = selectionRequest.getLocation().getCopy();

		final IFigure figure = getFigure();
		figure.translateToRelative(location);
		final IFigure selectedFigure = figure.findFigureAt(location);
		IFigure parentFigure = null;
		if (selectedFigure != null) {
			parentFigure = selectedFigure.getParent();
		}

		// check if existing note was clicked
		NotePosition notePosition = null;
		if (selectedFigure != null && selectedFigure instanceof ImageFigure && parentFigure != null
				&& parentFigure instanceof NotePositionFigure) {
			notePosition = new NotePosition(((NotePositionFigure) parentFigure).getNotePosition());
		} else if (selectedFigure != null && selectedFigure instanceof ImageFigure && parentFigure != null
				&& parentFigure instanceof AccidentalFigure) {
			notePosition = new NotePosition(((AccidentalFigure) parentFigure).getNotePosition());
		}

		// remove selected note
		if (notePosition != null) {
			getCastedModel().removeNotePosition(notePosition, addRemoveAllNotes(selectionRequest));
			return;
		}

		// add new note
		areasLayer.translateToRelative(location);
		final IFigure areasFigure = areasLayer.findFigureAt(location);
		if (areasFigure != null && areasFigure instanceof NotePositionAreaFigure) {
			notePosition = new NotePosition(((NotePositionAreaFigure) areasFigure).getNotePosition());
			notePosition.setAccidental(!view.isLockSignModeActivated() ? Accidental.NONE : getCastedModel()
					.isSharpSignOn() ? Accidental.SHARP : Accidental.FLAT);
			getCastedModel().addNotePosition(notePosition, addRemoveAllNotes(selectionRequest));
		}
	}
}
