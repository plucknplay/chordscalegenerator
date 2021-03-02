/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class MyBooleanFieldEditor extends BooleanFieldEditor {

	public MyBooleanFieldEditor(final String name, final String labelText, final Composite parent) {
		super(name, labelText, parent);
	}

	public Button getButton(final Composite parent) {
		return getChangeControl(parent);
	}
}
