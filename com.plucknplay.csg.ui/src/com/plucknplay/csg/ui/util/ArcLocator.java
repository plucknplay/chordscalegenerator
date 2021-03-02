/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A locator for arcs, so that child figures can properly position themselves on
 * arcs.
 * 
 * <p>
 * Copyright 2005, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </p>
 * 
 * Contributors: The Chisel Group, University of Victoria
 */
class ArcLocator extends ConnectionLocator {

	public ArcLocator(final Connection connection, final int location) {
		super(connection, location);
	}

	@Override
	public void relocate(final IFigure target) {
		if (!(target instanceof RotatableDecoration)) {
			return;
		}
		if (!(getConnection() instanceof ArcConnection)) {
			return;
		}
		final Point alignmentPoint = getAlignementPoint();
		((RotatableDecoration) target).setLocation(alignmentPoint);
		rotate((RotatableDecoration) target);
	}

	protected int getQuadrant(final Point p) {
		final ArcConnection conn = (ArcConnection) getConnection();
		final int centerx = conn.getArcBounds().x + conn.getArcBounds().width / 2;
		final int centery = conn.getArcBounds().y + conn.getArcBounds().height / 2;
		if (p.y > centery) {
			if (p.x > centerx) {
				return 4;
			} else {
				return 3;
			}
		} else {
			if (p.x < centerx) {
				return 2;
			} else {
				return 1;
			}
		}
	}

	protected Point getAlignementPoint() {
		Point point = null;
		final ArcConnection connection = (ArcConnection) getConnection();
		if (getAlignment() == SOURCE) {
			point = connection.getPoints().getFirstPoint().getCopy();
		} else if (getAlignment() == TARGET) {
			point = connection.getPoints().getLastPoint().getCopy();
		} else {
			point = connection.getPoints().getMidpoint().getCopy();
		}
		return point;
	}

	protected void rotate(final RotatableDecoration target) {

		final ArcConnection connection = (ArcConnection) getConnection();
		final Point point = getAlignementPoint();
		final Rectangle arcBounds = connection.getArcBounds();

		// normalize the coordinates.
		final double bx = arcBounds.x;
		final double by = arcBounds.y;
		final double bw = arcBounds.width;
		final double bh = arcBounds.height;

		final int q = getQuadrant(point);
		final Rectangle tbounds = target.getBounds();

		// the new location of the arrow will depend on what quadrant it is in
		switch (q) {
		case 1:
			point.x = point.x + tbounds.width;
			point.y = point.y + tbounds.height;
			break;
		case 2:
			point.x = point.x + tbounds.width;
			point.y = point.y - tbounds.height;
			break;
		case 3:
			point.x = point.x - tbounds.width;
			point.y = point.y - tbounds.height;
			break;
		case 4:
			point.x = point.x - tbounds.width;
			point.y = point.y + tbounds.height;
			break;
		default:
			break;
		}

		final double normx = point.x - (bx + bw / 2);
		final double normy = point.y - (by + bh / 2);
		double th = Math.atan(normy / normx);

		// adjust theta according to quadrant
		switch (q) {
		case 2:
		case 3:
		case 4:
			th = th + Math.PI;
			break;
		default:
			break;
		}

		// translate back from polar coordinates.
		final double nx = bw / 2 * Math.cos(th) + bx + bw / 2;
		final double ny = bh / 2 * Math.sin(th) + by + bh / 2;
		target.setReferencePoint(new Point((int) Math.round(nx), (int) Math.round(ny)));
	}
}
