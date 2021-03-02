/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;
import com.plucknplay.csg.ui.editors.IntervalContainerEditor;
import com.plucknplay.csg.ui.editors.input.ChordEditorInput;
import com.plucknplay.csg.ui.util.WorkbenchUtil;

public class OpenChordResultInEditorAction extends Action implements IViewSelectionAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.openInEditor"; //$NON-NLS-1$

	private final IWorkbenchPartSite site;
	private Griptable selectedGriptable;

	public OpenChordResultInEditorAction(final IViewPart view) {
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.OpenInEditorAction_text);
		setToolTipText(ActionMessages.OpenInEditorAction_text);
		site = view.getSite();
		setEnabled(false);
	}

	@Override
	public void run() {
		if (selectedGriptable != null) {
			final Chord chord = (Chord) ChordList.getInstance().getElement(selectedGriptable.getChord().getName());

			final Category category = ChordList.getInstance().getRootCategory().getCategory(chord);
			try {
				WorkbenchUtil.showPerspective(site.getWorkbenchWindow().getWorkbench(),
						Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
				site.getWorkbenchWindow().getActivePage()
						.openEditor(new ChordEditorInput(chord, category, false), IntervalContainerEditor.ID);
			} catch (final WorkbenchException e) {
			}
		}
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		boolean enabled = false;
		if (selection != null && selection instanceof IStructuredSelection) {
			final IStructuredSelection structered = (IStructuredSelection) selection;
			if (structered.size() == 1 && structered.getFirstElement() instanceof Griptable) {
				selectedGriptable = (Griptable) structered.getFirstElement();
				enabled = true;
			}
		}
		setEnabled(enabled);
	}
}
