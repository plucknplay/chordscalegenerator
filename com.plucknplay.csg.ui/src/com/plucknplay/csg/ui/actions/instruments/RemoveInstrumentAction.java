/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractRemoveAction;
import com.plucknplay.csg.ui.util.WorkbenchUtil;
import com.plucknplay.csg.ui.views.ChordResultsView;

public class RemoveInstrumentAction extends AbstractRemoveAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.removeInstrumentAction"; //$NON-NLS-1$

	public RemoveInstrumentAction(final IViewPart view) {
		super(view);
	}

	@Override
	public CategoryList getCategoryList() {
		return InstrumentList.getInstance();
	}

	@Override
	protected String getActionId() {
		return ACTION_ID;
	}

	@Override
	public void run() {
		super.run();
		InstrumentList.getInstance().setCurrentInstrument(InstrumentList.getInstance().getCurrentInstrument());
	}

	@Override
	protected boolean isDeletionConfirmed(final IStructuredSelection selection) {
		final ChordResultsView chordResultsView = (ChordResultsView) WorkbenchUtil.findView(getViewPart().getSite()
				.getWorkbenchWindow(), ChordResultsView.ID);

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean hidePrompt = prefs.getBoolean(Preferences.WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW);

		boolean isConfirmed;
		if (isCurrentInstrumentSelected(selection) && chordResultsView != null && !chordResultsView.isEmpty()
				&& !hidePrompt) {

			final MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(getViewPart().getSite()
					.getShell(), ActionMessages.RemoveInstrumentAction_confirm_dialog_title,
					ActionMessages.RemoveInstrumentAction_confirm_dialog_msg,
					ActionMessages.RemoveInstrumentAction_confirm_dialog_prompt, hidePrompt, null, null);
			isConfirmed = dialog.getReturnCode() == Dialog.OK;
			if (isConfirmed) {
				prefs.setValue(Preferences.WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW, dialog.getToggleState());
			}
		} else {
			isConfirmed = super.isDeletionConfirmed(selection);
		}

		// flush chord results view if necessary
		if (isCurrentInstrumentSelected(selection) && chordResultsView != null && !chordResultsView.isEmpty()) {
			chordResultsView.flush();
		}

		return isConfirmed;
	}

	private boolean isCurrentInstrumentSelected(final IStructuredSelection selection) {
		final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		for (final Object obj : selection.toArray()) {
			if (obj == currentInstrument || obj instanceof Category
					&& ((Category) obj).getAllElements().contains(currentInstrument)) {
				return true;
			}
		}
		return false;
	}
}
