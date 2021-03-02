/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;
import com.plucknplay.csg.ui.model.sets.Clipboard;
import com.plucknplay.csg.ui.util.WorkbenchUtil;
import com.plucknplay.csg.ui.views.ChordResultsView;

public class SetCurrentInstrumentAction extends Action implements IViewSelectionAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.addInstrumentCategoryAction"; //$NON-NLS-1$
	private static final String COMMAND_ID = "com.plucknplay.csg.ui.activateInstrument"; //$NON-NLS-1$

	private final IViewPart view;
	private Instrument selectedInstrument;

	public SetCurrentInstrumentAction(final IViewPart view) {
		this.view = view;

		setId(ACTION_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.SetCurrentInstrumentAction_text);
		setToolTipText(ActionMessages.SetCurrentInstrumentAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.CURRENT_INSTRUMENT));
	}

	@Override
	public void run() {
		setCurrentInstrument(selectedInstrument, view.getSite().getWorkbenchWindow());
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structured = (IStructuredSelection) selection;
			setEnabled(structured.size() == 1);
			final Object first = structured.getFirstElement();
			if (first instanceof Instrument) {
				selectedInstrument = (Instrument) first;
				setEnabled(true);
				return;
			}
		}
		setEnabled(false);
	}

	public static void setCurrentInstrument(final Instrument instrument, final IWorkbenchWindow window) {
		if (instrument != null && InstrumentList.getInstance().getCurrentInstrument() != instrument) {

			boolean performSetCurrent = true;
			final ChordResultsView chordResultsView = (ChordResultsView) WorkbenchUtil.findView(window,
					ChordResultsView.ID);

			final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
			final boolean hidePrompt = prefs.getBoolean(Preferences.WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW);

			MessageDialogWithToggle dialog = null;
			if (chordResultsView != null && !chordResultsView.isEmpty() && !hidePrompt) {

				dialog = MessageDialogWithToggle.openOkCancelConfirm(window.getShell(),
						ActionMessages.SetCurrentInstrumentAction_confirm_dialog_title,
						ActionMessages.SetCurrentInstrumentAction_confirm_dialog_msg,
						ActionMessages.SetCurrentInstrumentAction_confirm_dialog_prompt, hidePrompt, null, null);
				performSetCurrent = dialog.getReturnCode() == Dialog.OK;
			}

			if (performSetCurrent) {
				if (dialog != null) {
					prefs.setValue(Preferences.WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW, dialog.getToggleState());
				}
				if (chordResultsView != null) {
					chordResultsView.flush();
				}
				InstrumentList.getInstance().setCurrentInstrument(instrument);
				Clipboard.getInstance().saveCurrentInstrument(null);
			}
		}
	}
}
