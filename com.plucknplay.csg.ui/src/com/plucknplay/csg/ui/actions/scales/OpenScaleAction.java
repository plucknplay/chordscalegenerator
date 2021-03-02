/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.dialogs.IntervalContainerSelectionDialog;
import com.plucknplay.csg.ui.editors.IntervalContainerEditor;
import com.plucknplay.csg.ui.editors.input.ScaleEditorInput;
import com.plucknplay.csg.ui.util.WorkbenchUtil;

public class OpenScaleAction extends Action {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.openScale"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public OpenScaleAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.OpenScaleAction_text);
		setToolTipText(ActionMessages.OpenScaleAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.OPEN_SCALE));
	}

	@Override
	public void run() {

		final IntervalContainerSelectionDialog dialog = new IntervalContainerSelectionDialog(window.getShell(),
				IntervalContainer.TYPE_SCALE, ScaleList.getInstance().getRootCategory().getAllElements());
		dialog.setImage(Activator.getDefault().getImage(IImageKeys.SCALE));

		if (dialog.open() == Dialog.OK) {
			final Scale scale = (Scale) dialog.getResult()[0];
			try {
				final Category category = ScaleList.getInstance().getRootCategory().getCategory(scale);
				WorkbenchUtil.showPerspective(window.getWorkbench(),
						Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
				window.getActivePage().openEditor(new ScaleEditorInput(scale, category, false),
						IntervalContainerEditor.ID);
			} catch (final PartInitException e) {
			} catch (final WorkbenchException e) {
			}
		}
	}
}
