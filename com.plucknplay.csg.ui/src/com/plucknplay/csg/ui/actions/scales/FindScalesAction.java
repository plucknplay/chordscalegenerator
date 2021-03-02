/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;

import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;

public class FindScalesAction extends AbstractFindScalesAction implements IViewSelectionAction {

	private Griptable selectedGriptable;

	public FindScalesAction(final IWorkbenchSite site) {
		super(site);
		setEnabled(false);
	}

	@Override
	protected Collection<Note> getNotes() {
		return selectedGriptable != null ? selectedGriptable.getNotes(true) : new ArrayList<Note>();
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		selectedGriptable = null;
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
