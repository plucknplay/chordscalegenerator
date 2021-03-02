/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.model.TabDraft;
import com.plucknplay.csg.ui.util.TabViewUtil;

public class TabLayer extends AbstractTabLayer {

	private static final int DOUBLED_STRINGS_DISTANCE = 4;

	private static Image image;

	static {
		image = Activator.getDefault().getImage(IFigureConstants.TAB_IMAGE);
	}

	public TabLayer(final TabDraft tabDraft) {
		super(tabDraft);
	}

	@Override
	public void paintFigure(final Graphics g) {
		if (getCurrentInstrument() == null) {
			return;
		}

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean drawDoubled = prefs.getBoolean(Preferences.TAB_VIEW_DRAW_DOUBLED_STRINGS);
		final boolean isDoubled = getCurrentInstrument().hasDoubledStrings() && drawDoubled;

		g.setLineWidth(1);
		g.setLineStyle(SWT.LINE_SOLID);
		g.setForegroundColor(IFigureConstants.GREY);
		g.setLineCap(SWT.CAP_FLAT);

		final int s = getCurrentInstrument().getStringCount();

		final int x0 = IFigureConstants.TAB_OFFSET_X;
		int y0 = IFigureConstants.TAB_OFFSET_Y;
		if (isDoubled) {
			y0 -= 2;
		}
		int height = (s - 1) * IFigureConstants.TAB_LINE_DISTANCE;
		if (isDoubled) {
			height += DOUBLED_STRINGS_DISTANCE;
		}

		final int width = TabViewUtil.getTabImageWidth() + 2 * TabViewUtil.getColomnOffsetX()
				+ TabViewUtil.getColumnSpacing() * getTabDraft().getNumberOfColumns();

		// paint vertical lines
		g.drawLine(x0, y0, x0, y0 + height);
		g.drawLine(x0 + width, y0, x0 + width, y0 + height);

		// paint horizontal lines
		for (int i = 0; i < getCurrentInstrument().getStringCount(); i++) {
			g.drawLine(x0, y0 + i * IFigureConstants.TAB_LINE_DISTANCE, x0 + width, y0 + i
					* IFigureConstants.TAB_LINE_DISTANCE);
			if (isDoubled) {
				g.drawLine(x0, y0 + i * IFigureConstants.TAB_LINE_DISTANCE + DOUBLED_STRINGS_DISTANCE, x0 + width, y0
						+ i * IFigureConstants.TAB_LINE_DISTANCE + DOUBLED_STRINGS_DISTANCE);
			}
		}

		// paint tab image
		if (image != null) {
			g.drawImage(image, 0, 0, IFigureConstants.TAB_IMAGE_WIDTH, IFigureConstants.TAB_IMAGE_HEIGHT,
					IFigureConstants.TAB_OFFSET_X, IFigureConstants.TAB_OFFSET_Y, TabViewUtil.getTabImageWidth(),
					height);
		}
	}
}
