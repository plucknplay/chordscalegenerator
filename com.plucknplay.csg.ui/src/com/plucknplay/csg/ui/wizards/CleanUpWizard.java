/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.wizards;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.wizard.Wizard;

import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.core.model.workingCopies.IntervalContainerWorkingCopy;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopyManager;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.listeners.CategoryViewFilter;

public class CleanUpWizard extends Wizard {

	private final Object type;
	private final CleanUpWizardPage[] wizardPages;

	public CleanUpWizard(final List<Set<IntervalContainer>> result, final Object type) {

		final String title = type == IntervalContainer.TYPE_CHORD ? WizardMessages.CleanUpWizard_clean_up_chords_title
				: WizardMessages.CleanUpWizard_clean_up_scales_title;
		setWindowTitle(title);
		setNeedsProgressMonitor(true);

		this.type = type;
		wizardPages = new CleanUpWizardPage[result.size()];

		int i = 0;
		for (final Set<IntervalContainer> set : result) {
			final String pageName = title + " " + i; //$NON-NLS-1$
			wizardPages[i] = new CleanUpWizardPage(set, pageName, title, null, type);
			i++;
		}
	}

	@Override
	public void addPages() {
		for (final CleanUpWizardPage wizardPage : wizardPages) {
			addPage(wizardPage);
		}

		// set context-sensitive help
		final String helpId = type == IntervalContainer.TYPE_CHORD ? "cleanup_chords_context" : "cleanup_scales_context"; //$NON-NLS-1$ //$NON-NLS-2$
		Activator.getDefault().setHelp(getContainer().getShell(), helpId);

		// fixed bug 150
		getContainer().getShell().setSize(500, 400);
	}

	@Override
	public boolean performFinish() {

		final CategoryList categoryList = type == IntervalContainer.TYPE_CHORD ? ChordList.getInstance() : ScaleList
				.getInstance();

		// suppress notification
		final CategoryViewFilter filter = new CategoryViewFilter();
		categoryList.addChangeListenerFilter(filter);

		for (final CleanUpWizardPage page : wizardPages) {
			// find element to keep
			final Set<IntervalContainer> intervalContainers = page.getIntervalContainers();
			final String selectedName = page.getSelectedName();
			IntervalContainer theElement = null;
			for (final IntervalContainer current : intervalContainers) {
				if (current.getName().equals(selectedName)) {
					theElement = current;
					break;
				} else if (theElement == null && current.getAlsoKnownAsNamesList().contains(selectedName)) {
					theElement = current;
				}
			}

			// delete all other interval containers
			for (final IntervalContainer current : intervalContainers) {
				if (current != theElement) {
					categoryList.removeElement(current);
				}
			}

			// rename remained element
			final Category category = categoryList.getRootCategory().getCategory(theElement);
			final IntervalContainerWorkingCopy workingCopy = (IntervalContainerWorkingCopy) WorkingCopyManager
					.getInstance().getWorkingCopy(theElement, category, false);
			workingCopy.setAlsoKnownAsString(page.getAkaNames(), false);
			workingCopy.setName(selectedName);
			workingCopy.saveName();
			WorkingCopyManager.getInstance().disposeWorkingCopy(theElement);
		}

		// rollback notification state
		categoryList.removeChangeListenerFilter(filter);
		categoryList.changedWholeList();

		return true;
	}
}
