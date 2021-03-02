/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;

import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;
import com.plucknplay.csg.ui.util.CalculatorUtil;

public class FindAlternateFingeringsAction extends AbstractFindChordsAction implements IViewSelectionAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.findAlternativeFingerings"; //$NON-NLS-1$

	private Griptable selectedGriptable;

	public FindAlternateFingeringsAction(final IWorkbenchSite site) {
		super(site);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.FindAlternativeFingeringsAction_text);
		setToolTipText(ActionMessages.FindAlternativeFingeringsAction_text);
		setEnabled(false);
	}

	@Override
	public void run() {
		if (selectedGriptable != null) {
			super.run();
		}
	}

	@Override
	protected Collection<Griptable> getGriptables() {
		return selectedGriptable != null ? CalculatorUtil.getCalculator()
				.calculateCorrespondingGriptablesOfSetOfAbsoluteNotes(selectedGriptable.getNotes(false))
				: new ArrayList<Griptable>();
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
