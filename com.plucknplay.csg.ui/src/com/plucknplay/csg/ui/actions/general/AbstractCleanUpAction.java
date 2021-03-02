/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.wizards.CleanUpWizard;

public abstract class AbstractCleanUpAction extends Action {

	private final IWorkbenchWindow window;

	public AbstractCleanUpAction(final IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run() {

		final List<Set<IntervalContainer>> result = determineDirtyIntervalContainers();

		if (result.isEmpty()) {
			MessageDialog.openInformation(window.getShell(), ActionMessages.AbstractCleanUpAction_dialog_title,
					ActionMessages.AbstractCleanUpAction_there_is_nothing_to_clean_up);
		} else {
			final IWizard wizard = new CleanUpWizard(result, getType());
			final WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
			dialog.open();
		}
	}

	private List<Set<IntervalContainer>> determineDirtyIntervalContainers() {

		final List<Set<IntervalContainer>> result = new ArrayList<Set<IntervalContainer>>();

		final List<?> list1 = new ArrayList<Categorizable>(getAllElements());
		final List<?> list2 = new ArrayList<Categorizable>(getAllElements());

		for (final Object name : list1) {
			boolean foundDuplicates = false;
			final Set<IntervalContainer> partResult = new HashSet<IntervalContainer>();
			final IntervalContainer element1 = (IntervalContainer) name;
			for (final Object name2 : list2) {
				final IntervalContainer element2 = (IntervalContainer) name2;
				if (element1.hasSameIntervals(element2) && element1 != element2) {
					foundDuplicates = true;
					partResult.add(element1);
					partResult.add(element2);
				}
			}
			if (foundDuplicates) {
				list2.removeAll(partResult);
				result.add(partResult);
			}
		}
		return result;
	}

	/**
	 * Returns all elements which should be included into the clean up
	 * operation.
	 * 
	 * @return all elements which should be included into the clean up operation
	 */
	protected abstract List<Categorizable> getAllElements();

	/**
	 * Returns the type of interval container which is used. See
	 * IntervalContainer.TYPE_*.
	 * 
	 * @return the type of interval container which is used
	 */
	protected abstract Object getType();
}
