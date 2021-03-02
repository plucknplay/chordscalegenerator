/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.ui.progress.UIJob;

import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.figures.TabAssignmentLayer;
import com.plucknplay.csg.ui.figures.TabLayer;
import com.plucknplay.csg.ui.model.TabDraft;
import com.plucknplay.csg.ui.model.TabDraftFretboardPosition;
import com.plucknplay.csg.ui.util.TabViewUtil;

public class TabDraftEditPart extends AbstractDraftEditPart {

	private static final Object TAB_LAYER = new Object();
	private static final Object LABEL_LAYER = new Object();

	private TabAssignmentLayer assignmentLayer;
	private TabLabelEditManager lastTabLabelEditManager;

	@Override
	public void activate() {
		super.activate();
		getCastedModel().addSimpleChangeListener(this);
	}

	/**
	 * Returns the model object casted to TabDraft.
	 * 
	 * @return the model object casted to TabDraft
	 */
	private TabDraft getCastedModel() {
		return (TabDraft) getModel();
	}

	@Override
	protected void addLayers(final ScalableLayeredPane pane) {
		// add first layer - tab
		final Layer tabLayer = new TabLayer(getCastedModel());
		pane.add(tabLayer, TAB_LAYER, 0);

		// add second layer - labels
		assignmentLayer = new TabAssignmentLayer(getCastedModel());
		pane.addLayerAfter(assignmentLayer, LABEL_LAYER, TAB_LAYER);
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
		return 2 * IFigureConstants.TAB_OFFSET_X + TabViewUtil.getTabImageWidth() + 2 * TabViewUtil.getColomnOffsetX()
				+ TabViewUtil.getColumnSpacing() * getCastedModel().getNumberOfColumns();
	}

	@Override
	public int getNormHeight() {
		if (getCurrentInstrument() == null) {
			return 0;
		}
		final int height = (getCurrentInstrument().getStringCount() - 1) * IFigureConstants.TAB_LINE_DISTANCE;
		return 2 * IFigureConstants.TAB_OFFSET_Y + height;
	}

	@Override
	public void refresh() {
		assignmentLayer.init();
		super.refresh();
	}

	@Override
	public void notifyChange(final Object property, final Object value) {
		if (!getCastedModel().isEditable()) {
			return;
		}

		if (TabDraft.PROP_COLUMN_NUMBER_CHANGED.equals(property)) {
			refresh();
		}

		if (TabDraft.PROP_ROOT_NOTE_CHANGED.equals(property)
				&& (Activator.getDefault().getPreferenceStore().getBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE) || getCastedModel()
						.isEditable())) {
			assignmentLayer.updateLabelTexts();
		}
	}

	@Override
	public void deactivate() {
		getCastedModel().removeSimpleChangeListener(this);
		super.deactivate();
	}

	@Override
	public void performRequest(final Request request) {

		if (!isEditable() || !(request instanceof SelectionRequest)) {
			return;
		}

		final Point location = ((SelectionRequest) request).getLocation().getCopy();
		final IFigure figure = getFigure();
		figure.translateToRelative(location);
		final IFigure selectedFigure = figure.findFigureAt(location);

		if (selectedFigure instanceof Label) {
			final Label label = (Label) selectedFigure;
			final TabDraftFretboardPosition fbp = assignmentLayer.getTabDraftFretboardPosition(label);
			edit(fbp, label);
		}
	}

	public void edit(final TabDraftFretboardPosition fbp, final Label label) {
		if (fbp != null) {
			final Label theLabel = label == null ? assignmentLayer.getLabel(fbp) : label;
			if (theLabel != null) {
				final TabLabelEditManager manager = new TabLabelEditManager(this, theLabel, getCastedModel(), fbp,
						getScale());
				lastTabLabelEditManager = manager;
				manager.show();
			}
		}
	}

	@Override
	public void setEditManager() {
		final UIJob uiJob = new UIJob("") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				if (InstrumentList.getInstance().getCurrentInstrument() != null) {
					final List<TabDraftFretboardPosition> tabDraftFbps = getCastedModel()
							.getTabDraftFretboardPositions(1);
					edit(Activator.getDefault().getPreferenceStore()
							.getBoolean(Preferences.VIEWS_SEARCH_MODE_FAST_EDITING_DEEP_TO_HIGH) ? tabDraftFbps.get(InstrumentList
							.getInstance().getCurrentInstrument().getStringCount() - 1)
							: tabDraftFbps.get(0), null);
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule(FAST_EDITING_DELAY);
	}

	@Override
	public void bringDownEditManager() {
		if (lastTabLabelEditManager != null) {
			lastTabLabelEditManager.bringDown();
		}
	}
}
