/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.Action;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public abstract class AbstractSignModeAction extends Action {

	private final String value;

	private Action action1;
	private Action action2;

	public AbstractSignModeAction(final String commandId, final String value) {
		this.value = value;
		setId(commandId);
		setActionDefinitionId(commandId);
		setChecked(Activator.getDefault().getPreferenceStore().getString(Preferences.NOTES_MODE).equals(value));
	}

	@Override
	public void run() {
		Activator.getDefault().getPreferenceStore().setValue(Preferences.NOTES_MODE, value);

		setChecked(true);
		if (action1 != null && action2 != null) {
			action1.setChecked(false);
			action2.setChecked(false);
		}
	}

	public void setActions(final Action action1, final Action action2) {
		this.action1 = action1;
		this.action2 = action2;
	}

	@Override
	public int getStyle() {
		return AS_RADIO_BUTTON;
	}
}
