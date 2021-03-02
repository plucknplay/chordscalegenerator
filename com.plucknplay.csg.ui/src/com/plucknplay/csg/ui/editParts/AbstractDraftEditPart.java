/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.SelectionRequest;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.figures.ScalableLayeredPane2;
import com.plucknplay.csg.ui.listeners.ISimpleChangeListener;
import com.plucknplay.csg.ui.model.Draft;

public abstract class AbstractDraftEditPart extends AbstractGraphicalEditPart implements ISimpleChangeListener {

	protected static final int FAST_EDITING_DELAY = 100;

	private XYLayout paneLayout;
	private ScalableLayeredPane pane;
	private XYLayout rootLayout;

	private Figure root;

	@Override
	protected IFigure createFigure() {

		root = new Figure();
		rootLayout = new XYLayout();
		root.setLayoutManager(rootLayout);

		// adds the scalable layered pane to the root figure
		pane = new ScalableLayeredPane2();
		paneLayout = new XYLayout();
		pane.setLayoutManager(paneLayout);
		root.add(pane, 0);

		// add layers to the scalable layered pane
		addLayers(pane);

		// update
		updateSize(false);
		updateConstraints();

		return root;
	}

	protected abstract void addLayers(ScalableLayeredPane pane);

	/**
	 * Updates the pane layout contraints for all layers of this view.
	 */
	private void updateConstraints() {
		final Rectangle rectangle = new Rectangle(0, 0, getNormWidth(), getNormHeight());
		for (final Object obj : pane.getChildren()) {
			if (obj instanceof Layer) {
				final Layer layer = (Layer) obj;
				layer.setBounds(rectangle);
				paneLayout.setConstraint(layer, rectangle);
			}
		}
	}

	/**
	 * Updates the sizing information of this view.
	 * 
	 * @param forceZoomUpdate
	 *            forces a scale update regardless whether was a change, this is
	 *            necessary for the repaintAndValidateAll() method to repaint
	 *            properly
	 */
	protected void updateSize(final boolean forceZoomUpdate) {
		final int viewWidth = getViewer().getControl().getSize().x;
		final int viewHeight = getViewer().getControl().getSize().y;

		final double widthRelation = viewWidth * 1.0d / getNormWidth() * 1.0d;
		final double heightRelation = viewHeight * 1.0d / getNormHeight() * 1.0d;
		double scale = Math.min(widthRelation, heightRelation);
		if (scale < getMinScaleFactor()) {
			scale = getMinScaleFactor();
		}

		if (pane.getScale() != scale || forceZoomUpdate) {
			pane.setScale(scale);
			final Rectangle scaledRectangle = new Rectangle(0, 0, (int) (getNormWidth() * scale),
					(int) (getNormHeight() * scale));
			pane.setBounds(scaledRectangle);
			rootLayout.setConstraint(pane, scaledRectangle);
		}
	}

	@Override
	public void refresh() {
		updateConstraints();
		updateSize(true);
		super.refresh();
		pane.revalidate();
		pane.repaint();
	}

	public double getScale() {
		return pane.getScale();
	}

	protected boolean isEditable() {
		final Object model = getModel();
		return model != null && model instanceof Draft ? ((Draft) model).isEditable() : false;
	}

	protected boolean addRemoveAllNotes(final SelectionRequest selectionRequest) {
		if (selectionRequest == null) {
			return false;
		}
		if (Activator.getDefault().getPreferenceStore()
				.getBoolean(Preferences.VIEWS_SEARCH_MODE_RELATIVE_NOTES_MODE_KEY_ALT)) {
			return selectionRequest.isAltKeyPressed();
		}
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			return selectionRequest.isCommandKeyPressed();
		}
		return selectionRequest.isControlKeyPressed();
	}

	protected abstract double getMinScaleFactor();

	public abstract int getNormWidth();

	public abstract int getNormHeight();

	@Override
	protected void createEditPolicies() {
	}

	protected Instrument getCurrentInstrument() {
		return InstrumentList.getInstance().getCurrentInstrument();
	}

	public void setEditManager() {
	}

	public void bringDownEditManager() {
	}
}
