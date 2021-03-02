/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.ui.IWorkbenchPart;

public class MyFocusListener extends FocusAdapter {

	private IWorkbenchPart workbenchPart;

	public MyFocusListener(final IWorkbenchPart workbenchPart) {
		this.workbenchPart = workbenchPart;
	}

	@Override
	public void focusGained(final FocusEvent e) {
		if (workbenchPart != null) {
			workbenchPart.setFocus();
		}
	}
}
