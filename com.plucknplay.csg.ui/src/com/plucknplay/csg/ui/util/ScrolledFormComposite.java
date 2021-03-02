/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

public class ScrolledFormComposite extends SharedScrolledComposite {

	private final Composite composite;

	public ScrolledFormComposite(final Composite parent, final FormToolkit toolkit) {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		composite = toolkit.createComposite(this);
		super.setContent(composite);
	}

	public ScrolledFormComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style);
		composite = toolkit.createComposite(this);
		setContent(composite);
	}

	@Override
	public final void setContent(final Control c) {
	}

	/**
	 * Sets the foreground color of the scrolled form text.
	 * 
	 * @param fg
	 *            the foreground color
	 */
	@Override
	public void setForeground(final Color fg) {
		super.setForeground(fg);
		if (composite != null) {
			composite.setForeground(fg);
		}
	}

	/**
	 * Sets the background color of the scrolled form text.
	 * 
	 * @param bg
	 *            the background color
	 */
	@Override
	public void setBackground(final Color bg) {
		super.setBackground(bg);
		if (composite != null) {
			composite.setBackground(bg);
		}
	}
}
