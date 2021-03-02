/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.dialogs.InstrumentSelectionDialog;
import com.plucknplay.csg.ui.editors.InstrumentEditor;
import com.plucknplay.csg.ui.editors.input.InstrumentEditorInput;
import com.plucknplay.csg.ui.util.WorkbenchUtil;

public class OpenInstrumentAction extends Action {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.openInstrument"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public OpenInstrumentAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.OpenInstrumentAction_text);
		setToolTipText(ActionMessages.OpenInstrumentdAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.OPEN_INSTRUMENT));
	}

	@Override
	public void run() {
		final InstrumentSelectionDialog dialog = new InstrumentSelectionDialog(window.getShell(), false);

		if (dialog.open() == Dialog.OK) {
			final Instrument instrument = dialog.getSelectedInstrument();
			try {
				final Category category = InstrumentList.getInstance().getRootCategory().getCategory(instrument);
				WorkbenchUtil.showPerspective(window.getWorkbench(),
						Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
				window.getActivePage().openEditor(new InstrumentEditorInput(instrument, category, false),
						InstrumentEditor.ID);
			} catch (final PartInitException e) {
			} catch (final WorkbenchException e) {
			}
		}
	}
}
