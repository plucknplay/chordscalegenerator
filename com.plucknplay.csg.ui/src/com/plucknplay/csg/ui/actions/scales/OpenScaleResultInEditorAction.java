/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.ScaleResult;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;
import com.plucknplay.csg.ui.editors.IntervalContainerEditor;
import com.plucknplay.csg.ui.editors.input.ScaleEditorInput;
import com.plucknplay.csg.ui.util.WorkbenchUtil;

public class OpenScaleResultInEditorAction extends Action implements IViewSelectionAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.openInEditor"; //$NON-NLS-1$

	private final IWorkbenchPartSite site;
	private ScaleResult selectedScaleResult;

	public OpenScaleResultInEditorAction(final IViewPart view) {
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.OpenInEditorAction_text);
		setToolTipText(ActionMessages.OpenInEditorAction_text);
		setEnabled(false);
		site = view.getSite();
	}

	@Override
	public void run() {
		if (selectedScaleResult != null) {
			final Scale scale = (Scale) ScaleList.getInstance().getElement(selectedScaleResult.getName());

			final Category category = ScaleList.getInstance().getRootCategory().getCategory(scale);
			try {
				WorkbenchUtil.showPerspective(site.getWorkbenchWindow().getWorkbench(),
						Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
				site.getWorkbenchWindow().getActivePage()
						.openEditor(new ScaleEditorInput(scale, category, false), IntervalContainerEditor.ID);
			} catch (final WorkbenchException e) {
			}
		}
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		boolean enabled = false;
		if (selection != null && selection instanceof IStructuredSelection) {
			final IStructuredSelection structered = (IStructuredSelection) selection;
			if (structered.size() == 1 && structered.getFirstElement() instanceof ScaleResult) {
				selectedScaleResult = (ScaleResult) structered.getFirstElement();
				enabled = true;
			}
		}
		setEnabled(enabled);
	}
}
