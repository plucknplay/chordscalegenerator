/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.util.APIPrefUtil;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;

@SuppressWarnings("restriction")
public class LockToolbarAction extends Action implements IPropertyChangeListener {

	private static final String COMMAND_ID = "org.eclipse.ui.ToggleToolbar"; //$NON-NLS-1$

	private final WorkbenchWindow window;

	public LockToolbarAction(final IWorkbenchWindow window) {
		this.window = (WorkbenchWindow) window;
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.LockToolbarAction_text);
		setToolTipText(ActionMessages.LockToolbarAction_tooltip);
		APIPrefUtil.getInternalPreferenceStore().addPropertyChangeListener(this);
		update();
		updateEnablement();
	}

	@Override
	public void run() {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean locked = prefs.getBoolean(Preferences.LOCK_TOOLBAR);
		final ICoolBarManager coolBarManager = window.getCoolBarManager2();
		if (coolBarManager != null) {
			coolBarManager.setLockLayout(!locked);
			prefs.setValue(Preferences.LOCK_TOOLBAR, !locked);
			update();
		}
	}

	private void update() {
		setChecked(Activator.getDefault().getPreferenceStore().getBoolean(Preferences.LOCK_TOOLBAR));
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		updateEnablement();
	}

	private void updateEnablement() {
		setEnabled(APIPrefUtil.getInternalPreferenceStore().getBoolean(IPreferenceConstants.COOLBAR_VISIBLE));
	}

	public void dispose() {
		APIPrefUtil.getInternalPreferenceStore().removePropertyChangeListener(this);
	}
}
