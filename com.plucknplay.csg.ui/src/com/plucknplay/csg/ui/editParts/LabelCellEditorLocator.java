/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Scrollable;

public final class LabelCellEditorLocator implements CellEditorLocator {

	private final Label label;
	private final Dimension dimension;

	private boolean rightAlignment;
	private boolean centerAlignment;

	public LabelCellEditorLocator(final Label label) {
		this(label, null, SWT.CENTER);
	}

	public LabelCellEditorLocator(final Label label, final Dimension dimension) {
		this(label, dimension, SWT.CENTER);
	}

	/**
	 * The constructor.
	 * 
	 * @param label
	 *            the label, must not be <code>null</code>
	 * @param dimension
	 *            the preferred dimension or <code>null</code>
	 * @param alignment
	 *            the alignment, {@link SWT#LEFT}, {@link SWT#CENTER} or
	 *            {@link SWT#RIGHT}
	 */
	public LabelCellEditorLocator(final Label label, final Dimension dimension, final int alignment) {
		if (label == null) {
			throw new IllegalArgumentException();
		}
		this.label = label;
		this.dimension = dimension;
		if (alignment == SWT.RIGHT) {
			rightAlignment = true;
		} else if (alignment == SWT.CENTER) {
			centerAlignment = true;
		}
	}

	@Override
	public void relocate(final CellEditor cellEditor) {
		if (cellEditor != null && cellEditor.getControl() != null && cellEditor.getControl() instanceof Scrollable) {
			final Scrollable scrollable = (Scrollable) cellEditor.getControl();
			final Rectangle rect = label.getClientArea();
			label.translateToAbsolute(rect);
			final org.eclipse.swt.graphics.Rectangle trim = scrollable.computeTrim(0, 0, 0, 0);
			rect.translate(trim.x, trim.y);
			rect.width += trim.width;
			rect.height += trim.height;
			if (dimension != null) {
				if (dimension.width != -1) {
					if (centerAlignment) {
						rect.x = rect.x + rect.width / 2 - dimension.width / 2 + 1;
					} else if (rightAlignment) {
						rect.x = rect.x + rect.width - dimension.width + 1;
					}
					rect.width = dimension.width;
				}
				if (dimension.height != -1) {
					rect.y = rect.y + rect.height / 2 - dimension.height / 2 - 1;
					rect.height = dimension.height;
				}
			}
			scrollable.setBounds(rect.x, rect.y, rect.width, rect.height);
		}
	}
}
