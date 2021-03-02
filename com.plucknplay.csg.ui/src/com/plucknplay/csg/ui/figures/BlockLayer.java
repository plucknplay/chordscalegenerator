/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;

import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.FretboardDraft;

public class BlockLayer extends AbstractFretboardLayer {

	public BlockLayer(final FretboardDraft fretboardDraft) {
		super(fretboardDraft);
	}

	@Override
	public void paintFigure(final Graphics g) {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final String blockPresentation = prefs.getString(Preferences.FRETBOARD_VIEW_SHOW_BLOCK_PRESENTATION);
		final boolean showEmptyStringsTwice = prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE);

		if (getCurrentInstrument() == null || getFretboardDraft().getInput() == null
				|| !(getFretboardDraft().getInput() instanceof Block) || getFretboardDraft().isEditable()
				|| getFretboardDraft().isModifiedInput() || !prefs.getBoolean(Preferences.SHOW_BLOCKS)
				|| blockPresentation.equals(UIConstants.BLOCK_NO_OVERLAY_FRAME)) {
			return;
		}

		final Block block = (Block) getFretboardDraft().getInput();
		final int minString = block.getMinString();
		final int maxString = block.getMaxString();
		final boolean isLeftHander = Activator.getDefault().isLeftHander();

		// create points list
		PointList points = new PointList();
		addPoints(points, new Point(getX1(block, minString, isLeftHander, showEmptyStringsTwice), getY(minString - 1)),
				new Point(getX2(block, minString, isLeftHander, showEmptyStringsTwice), getY(minString - 1)));

		if (block instanceof FretBlock) {
			addPoints(points, new Point(getX1(block, maxString, isLeftHander, showEmptyStringsTwice), getY(maxString)),
					new Point(getX2(block, maxString, isLeftHander, showEmptyStringsTwice), getY(maxString)));
		} else {
			for (int s = block.getMinString(); s <= maxString; s++) {
				if (block.getMinFret(s) == -1) {
					draw(g, points, blockPresentation.equals(UIConstants.BLOCK_OVERLAY));
					points = new PointList();
					addPoints(points, new Point(getX1(block, s + 1, isLeftHander, showEmptyStringsTwice), getY(s)),
							new Point(getX2(block, s + 1, isLeftHander, showEmptyStringsTwice), getY(s)));
					continue;
				}
				addPoints(points, new Point(getX1(block, s, isLeftHander, showEmptyStringsTwice), getY(s)), new Point(
						getX2(block, s, isLeftHander, showEmptyStringsTwice), getY(s)));
			}
		}

		draw(g, points, blockPresentation.equals(UIConstants.BLOCK_OVERLAY));
	}

	private void addPoints(final PointList points, final Point p1, final Point p2) {
		if (points.size() != 0 && points.getFirstPoint().x != p1.x) {
			points.insertPoint(new Point(p1.x, points.getFirstPoint().y), 0);
		}
		if (points.size() != 0 && points.getLastPoint().x != p2.x) {
			points.addPoint(new Point(p2.x, points.getLastPoint().y));
		}
		points.insertPoint(p1, 0);
		points.addPoint(p2);
	}

	private int getX1(final Block block, final int string, final boolean isLeftHander,
			final boolean showEmptyStringsTwice) {
		final int minFret = block instanceof FretBlock ? block.getMinFret() : block.getMinFret(string);
		final int maxFret = block instanceof FretBlock ? block.getMaxFret() : block.getMaxFret(string);

		final int x0 = IFigureConstants.FRETBOARD_OFFSET_X;
		final int correction = showEmptyStringsTwice ? 1 : 0;
		return !isLeftHander ? x0 + minFret * IFigureConstants.FRET_WIDTH : x0
				+ (getCurrentInstrument().getFretCount() - maxFret + correction) * IFigureConstants.FRET_WIDTH;
	}

	private int getX2(final Block block, final int string, final boolean isLeftHander,
			final boolean showEmptyStringsTwice) {
		final int minFret = block instanceof FretBlock ? block.getMinFret() : block.getMinFret(string);
		final int maxFret = block instanceof FretBlock ? block.getMaxFret() : block.getMaxFret(string);

		final int x0 = IFigureConstants.FRETBOARD_OFFSET_X;
		final int correction = showEmptyStringsTwice ? 2 : 1;
		return !isLeftHander ? x0 + (maxFret + 1) * IFigureConstants.FRET_WIDTH : x0
				+ (getCurrentInstrument().getFretCount() - minFret + correction) * IFigureConstants.FRET_WIDTH;
	}

	private int getY(final int string) {
		final int y0 = getOffsetY() - IFigureConstants.FRET_HEIGHT / 2;
		return y0 + string * IFigureConstants.FRET_HEIGHT;
	}

	private void draw(final Graphics g, final PointList points, final boolean fill) {

		g.setLineWidth(5);
		g.setLineStyle(SWT.LINE_SOLID);
		g.setLineCap(SWT.CAP_FLAT);
		g.setForegroundColor(IFigureConstants.LIGHT_BLUE);
		g.setBackgroundColor(IFigureConstants.LIGHT_BLUE);

		if (fill) {
			g.setAlpha(220);
			g.fillPolygon(points);
		} else {
			g.setAlpha(255);
			g.drawPolygon(points);
		}
	}
}
