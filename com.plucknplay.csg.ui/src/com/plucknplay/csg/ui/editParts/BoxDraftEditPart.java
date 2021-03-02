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
import com.plucknplay.csg.core.model.enums.IntervalNamesMode;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.figures.BoxAssignmentFigure;
import com.plucknplay.csg.ui.figures.BoxAssignmentLayer;
import com.plucknplay.csg.ui.figures.BoxInfoFigure;
import com.plucknplay.csg.ui.figures.BoxLayer;
import com.plucknplay.csg.ui.model.BoxDraft;
import com.plucknplay.csg.ui.model.FretDraft;
import com.plucknplay.csg.ui.util.enums.BoxViewFigureMode;
import com.plucknplay.csg.ui.util.enums.BoxViewPresentationMode;
import com.plucknplay.csg.ui.util.enums.Position;

public class BoxDraftEditPart extends FretDraftEditPart {

	private static final Object BOX_LAYER = new Object();
	private static final Object ASSIGNMENT_LAYER = new Object();

	private final IPreferenceStore prefs;

	private BoxLayer boxLayer;
	private BoxAssignmentLayer assignmentLayer;

	private StartFretLabelEditManager lastStartFretLabelEditManager;
	private FingerNumberEditManager lastFingerNumberEditManager;
	private IntervalNameEditManager lastIntervalNameEditManager;

	private boolean showFingering;
	private boolean showFingeringOutside;
	private boolean showNotes;
	private boolean showNotesOutside;
	private boolean showIntervals;
	private boolean showIntervalsOutside;

	public BoxDraftEditPart(final boolean showFingering, final boolean showFingeringOutside, final boolean showNotes,
			final boolean showNotesOutside, final boolean showIntervals, final boolean showIntervalsOutside) {

		this.showFingering = showFingering;
		this.showFingeringOutside = showFingeringOutside;
		this.showNotes = showNotes;
		this.showNotesOutside = showNotesOutside;
		this.showIntervals = showIntervals;
		this.showIntervalsOutside = showIntervalsOutside;

		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void activate() {
		super.activate();
		getCastedModel().addSimpleChangeListener(this);
		Activator.getDefault().addSimpleChangeListener(this);
	}

	/**
	 * Returns the model object casted to BoxDraft.
	 * 
	 * @return the model object casted to BoxDraft
	 */
	private BoxDraft getCastedModel() {
		return (BoxDraft) getModel();
	}

	@Override
	protected void addLayers(final ScalableLayeredPane pane) {

		// add first layer - box frame
		boxLayer = new BoxLayer(getCastedModel(), showFingering, showFingeringOutside, showNotes, showNotesOutside,
				showIntervals, showIntervalsOutside);
		pane.add(boxLayer, BOX_LAYER, 0);

		// add second layer - assignments + start fret label
		assignmentLayer = new BoxAssignmentLayer(getCastedModel(), showFingering, showFingeringOutside, showNotes,
				showNotesOutside, showIntervals, showIntervalsOutside);
		pane.addLayerAfter(assignmentLayer, ASSIGNMENT_LAYER, BOX_LAYER);
	}

	@Override
	public IFigure getContentPane() {
		return assignmentLayer;
	}

	@Override
	protected double getMinScaleFactor() {
		return 0.5;
	}

	@Override
	public int getNormWidth() {
		if (getCurrentInstrument() == null) {
			return 0;
		}
		return BoxViewPresentationMode.valueOf(prefs.getString(Preferences.BOX_VIEW_PRESENTATION_MODE)).getWidth(
				getCastedModel(), showFingering && showFingeringOutside && !isEditable(),
				showFingering && (!showFingeringOutside || !isEditable()),
				showNotes && (showNotesOutside || isEditable()), showNotes && !showNotesOutside && !isEditable(),
				showIntervals && (showIntervalsOutside || isEditable()),
				showIntervals && !showIntervalsOutside && !isEditable(), getCastedModel().getFretWidth(),
				getCurrentInstrument().getStringCount());
	}

	@Override
	public int getNormHeight() {
		if (getCurrentInstrument() == null) {
			return 0;
		}
		return BoxViewPresentationMode.valueOf(prefs.getString(Preferences.BOX_VIEW_PRESENTATION_MODE)).getHeight(
				getCastedModel(), showFingering && showFingeringOutside && !isEditable(),
				showFingering && (!showFingeringOutside || !isEditable()),
				showNotes && (showNotesOutside || isEditable()), showNotes && !showNotesOutside && !isEditable(),
				showIntervals && (showIntervalsOutside || isEditable()),
				showIntervals && !showIntervalsOutside && !isEditable(), getCastedModel().getFretWidth(),
				getCurrentInstrument().getStringCount());
	}

	@Override
	public void refresh() {
		boxLayer.revalidate();
		boxLayer.repaint();
		assignmentLayer.init();
		super.refresh();
	}

	@Override
	public void notifyChange(final Object property, final Object value) {
		if (!getCastedModel().isEditable()) {
			return;
		}

		if (BoxDraft.PROP_START_FRET_CHANGED.equals(property) || BoxDraft.PROP_ASSIGNMENT_CHANGED.equals(property)
				|| BoxDraft.PROP_ROOT_NOTE_CHANGED.equals(property)
				|| BoxDraft.PROP_SHOW_BARRE_CHANGED.equals(property)
				|| BoxDraft.PROP_INTERVAL_NAME_CHANGED.equals(property)) {
			refresh();
		}

		if (Activator.PROP_HAND_CHANGED.equals(property)) {
			bringDownEditManager();
		}
	}

	@Override
	public void deactivate() {
		Activator.getDefault().removeSimpleChangeListener(this);
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

			final Label label = (Label) selectedFigure;
			final IFigure parent = label.getParent();

			if (assignmentLayer.isStartFretLabel(label)) {

				// edit start fret
				final StartFretLabelEditManager manager = new StartFretLabelEditManager(this, label, getCastedModel(),
						getScale());
				lastStartFretLabelEditManager = manager;
				manager.show();

			} else if (parent != null && !(parent instanceof BoxAssignmentLayer)) {

				// in case, label of f.i. box assignments or box info
				// figure is found reset selectedFigure
				selectedFigure = parent;

			} else {

				// add/remove assigments
				final FretboardPosition fbp = assignmentLayer.getRelativeFretboardPosition(label);
				if (showFingering && fbp != null && fbp.getFret() != 0) {
					editFingering(fbp);
				} else if (fbp != null) {
					getCastedModel().toggleAssignment(fbp.getFret(), fbp.getString() + 1,
							addRemoveAllNotes(selectionRequest));
				}
			}
		}

		if (selectedFigure instanceof BoxAssignmentFigure) {

			// change finger number
			editFingering(assignmentLayer.getRelativeFretboardPosition((BoxAssignmentFigure) selectedFigure),
					selectedFigure);
		}

		if (selectedFigure instanceof BoxInfoFigure) {

			// change interval name
			final BoxInfoFigure boxInfoFigure = (BoxInfoFigure) selectedFigure;
			if (boxInfoFigure.getMode() == BoxViewFigureMode.INTERVAL
					&& !"".equals(boxInfoFigure.getText())
					&& IntervalNamesMode.valueOf(prefs.getString(Preferences.INTERVAL_NAMES_MODE))
							.getInterval(boxInfoFigure.getText()).getHalfsteps() != 0) {

				// determine aligment
				int alignment = SWT.CENTER;
				final BoxViewPresentationMode boxViewPresentationMode = BoxViewPresentationMode.valueOf(prefs
						.getString(Preferences.BOX_VIEW_PRESENTATION_MODE));
				if (boxViewPresentationMode == BoxViewPresentationMode.HORIZONTAL) {
					alignment = Activator.getDefault().isLeftHander() ? SWT.LEFT : SWT.RIGHT;
				} else {
					alignment = Position.valueOf(prefs.getString(Preferences.BOX_VIEW_FRET_NUMBERS_VERTICAL_POSITION)) == Position.LEFT ? SWT.RIGHT
							: SWT.LEFT;
				}

				// create and show edit manager
				final IntervalNameEditManager manager = new IntervalNameEditManager(this, boxInfoFigure,
						getCastedModel(), getScale(), alignment);
				lastIntervalNameEditManager = manager;
				manager.show();
			}
		}
	}

	@Override
	public void editFingering(final FretboardPosition fbp, final IFigure figure) {
		if (fbp != null && (figure == null || figure instanceof BoxAssignmentFigure)) {
			final BoxAssignmentFigure boxAssignmentFigure = figure == null ? assignmentLayer.getAssignmentFigure(fbp)
					: (BoxAssignmentFigure) figure;
			if (boxAssignmentFigure != null) {
				final FingerNumberEditManager manager = new FingerNumberEditManager(this, boxAssignmentFigure,
						getCastedModel(), fbp, getScale());
				lastFingerNumberEditManager = manager;
				manager.show();
			}
		}
	}

	public void setShowFingering(final boolean showFingering) {
		this.showFingering = showFingering;
		assignmentLayer.setShowFingering(showFingering);
		if (!showFingering) {
			bringDownFingerNumberEditManager();
		}
	}

	public void setShowFingeringOutside(final boolean showFingeringOutside) {
		this.showFingeringOutside = showFingeringOutside;
		boxLayer.setShowFingeringOutside(showFingeringOutside);
		assignmentLayer.setShowFingeringOutside(showFingeringOutside);
	}

	public void setShowNotes(final boolean showNotes) {
		this.showNotes = showNotes;
		boxLayer.setShowNotes(showNotes);
		assignmentLayer.setShowNotes(showNotes);
		bringDownEditManager();
	}

	public void setShowNotesOutside(final boolean showNotesOutside) {
		this.showNotesOutside = showNotesOutside;
		boxLayer.setShowNotesOutside(showNotesOutside);
		assignmentLayer.setShowNotesOutside(showNotesOutside);
	}

	public void setShowIntervals(final boolean showIntervals) {
		this.showIntervals = showIntervals;
		boxLayer.setShowIntervals(showIntervals);
		assignmentLayer.setShowIntervals(showIntervals);
		bringDownEditManager();
	}

	public void setShowIntervalsOutside(final boolean showIntervalsOutside) {
		this.showIntervalsOutside = showIntervalsOutside;
		boxLayer.setShowIntervalsOutside(showIntervalsOutside);
		assignmentLayer.setShowIntervalsOutside(showIntervalsOutside);
	}

	@Override
	public void bringDownEditManager() {
		bringDownStartFretLabelEditManager();
		bringDownFingerNumberEditManager();
		bringDownIntervalNameEditManager();
	}

	private void bringDownStartFretLabelEditManager() {
		if (lastStartFretLabelEditManager != null) {
			lastStartFretLabelEditManager.bringDown();
		}
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
	protected FretDraft getFretDraft() {
		return getCastedModel();
	}

	@Override
	protected boolean showFingering() {
		return showFingering;
	}
}
