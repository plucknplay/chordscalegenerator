/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.model.TabDraft;
import com.plucknplay.csg.ui.model.TabDraftFretboardPosition;
import com.plucknplay.csg.ui.util.TabViewUtil;

public class TabAssignmentLayer extends AbstractTabLayer {

	private Map<TabDraftFretboardPosition, Label> labelMap;
	private final XYLayout contentsLayout;

	public TabAssignmentLayer(final TabDraft tabDraft) {
		super(tabDraft);

		contentsLayout = new XYLayout();

		setLayoutManager(contentsLayout);
		init();
	}

	public void init() {
		removeAll();

		labelMap = new HashMap<TabDraftFretboardPosition, Label>();

		if (getCurrentInstrument() == null) {
			return;
		}

		for (final TabDraftFretboardPosition tabDraftFbp : getTabDraft().getTabDraftFretboardPositions()) {

			final int column = tabDraftFbp.getColumn();
			final FretboardPosition fbp = tabDraftFbp.getFretboardPosition();

			// create label
			final Label label = new Label();
			label.setTextAlignment(PositionConstants.CENTER);
			label.setLabelAlignment(PositionConstants.CENTER);

			// determine bounds
			final int x0 = IFigureConstants.TAB_OFFSET_X + TabViewUtil.getTabImageWidth()
					+ TabViewUtil.getColomnOffsetX();
			final int y0 = IFigureConstants.TAB_OFFSET_Y - IFigureConstants.TAB_LINE_DISTANCE / 2 + 3;
			final Rectangle labelBounds = new Rectangle(x0 + (column - 1) * TabViewUtil.getColumnSpacing(), y0
					+ fbp.getString() * IFigureConstants.TAB_LINE_DISTANCE, IFigureConstants.TAB_LINE_DISTANCE,
					IFigureConstants.TAB_LINE_DISTANCE);

			add(label);
			contentsLayout.setConstraint(label, labelBounds);
			labelMap.put(tabDraftFbp, label);
		}

		updateLabelTexts();
	}

	public TabDraftFretboardPosition getTabDraftFretboardPosition(final Label label) {
		for (final Entry<TabDraftFretboardPosition, Label> entry : labelMap.entrySet()) {
			if (entry.getValue() == label) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Label getLabel(final TabDraftFretboardPosition fbp) {
		return labelMap.get(fbp);
	}

	public void updateLabelTexts() {
		for (final TabDraftFretboardPosition tabDraftFbp : getTabDraft().getTabDraftFretboardPositions()) {
			final FretboardPosition fbp = tabDraftFbp.getFretboardPosition();
			final boolean showMutedStrings = getTabDraft().isEditable()
					|| Activator.getDefault().getPreferenceStore().getBoolean(Preferences.TAB_VIEW_SHOW_MUTED_STRINGS);
			final String mutedStringText = showMutedStrings ? "X" : ""; //$NON-NLS-1$ //$NON-NLS-2$
			final Label label = labelMap.get(tabDraftFbp);
			if (label != null) {
				label.setText(fbp.getFret() == -1 ? mutedStringText : "" + fbp.getFret()); //$NON-NLS-1$
				TabViewUtil.setTooltip(label, fbp);
				TabViewUtil.updateRootNote(getTabDraft(), label, fbp);
			}
		}
	}
}
