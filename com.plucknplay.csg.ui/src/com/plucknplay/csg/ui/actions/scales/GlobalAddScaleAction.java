/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.editors.IntervalContainerEditor;
import com.plucknplay.csg.ui.editors.input.ScaleEditorInput;
import com.plucknplay.csg.ui.util.LoginUtil;
import com.plucknplay.csg.ui.util.WorkbenchUtil;

public class GlobalAddScaleAction extends Action {

	private static final String ID = "com.plucknplay.csg.ui.actions.global.add.scale"; //$NON-NLS-1$
	private static final String COMMAND_ID = "com.plucknplay.csg.ui.newScale"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public GlobalAddScaleAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setActionDefinitionId(COMMAND_ID);
		setText(NlsUtil.getAction_global_add_scale());
		setImageDescriptor(Activator.getImageDescriptor(NlsUtil.getAction_image_new_scale()));
	}

	@Override
	public void run() {
		if (LoginUtil.isActivated()) {
			try {
				WorkbenchUtil.showPerspective(window.getWorkbench(),
						Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
				window.getActivePage().openEditor(
						new ScaleEditorInput(new Scale(), ScaleList.getInstance().getRootCategory(), true),
						IntervalContainerEditor.ID);
			} catch (final PartInitException e) {
			} catch (final WorkbenchException e) {
			}
		} else {
			LoginUtil.showUnsupportedFeatureInformation(window.getShell());
		}
	}
}
