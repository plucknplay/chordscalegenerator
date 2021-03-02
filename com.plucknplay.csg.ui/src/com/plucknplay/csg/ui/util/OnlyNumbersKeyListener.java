/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;

/**
 * This key listeners only allows to enter numbers as well as deletion and
 * navigation keys.
 */
public class OnlyNumbersKeyListener extends KeyAdapter {

	private final boolean allowOnePoint;

	public OnlyNumbersKeyListener() {
		allowOnePoint = false;
	}

	public OnlyNumbersKeyListener(final boolean allowOnePoint) {
		this.allowOnePoint = allowOnePoint;
	}

	@Override
	public void keyPressed(final KeyEvent e) {

		if (!Character.isDigit(e.character) && e.keyCode != SWT.BS && e.keyCode != SWT.DEL
				&& e.keyCode != SWT.ARROW_LEFT && e.keyCode != SWT.ARROW_RIGHT) {
			e.doit = false;
		}

		if (allowOnePoint && e.character == '.' && e.widget instanceof Text) {
			final Text text = (Text) e.widget;
			e.doit = !text.getText().contains(".") || text.getSelectionText().contains("."); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
