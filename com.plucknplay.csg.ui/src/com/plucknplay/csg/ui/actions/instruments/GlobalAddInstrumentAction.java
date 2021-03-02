/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.editors.InstrumentEditor;
import com.plucknplay.csg.ui.editors.input.InstrumentEditorInput;
import com.plucknplay.csg.ui.util.WorkbenchUtil;

public class GlobalAddInstrumentAction extends Action {

	private static final String ID = "com.plucknplay.csg.ui.actions.global.add.instrument"; //$NON-NLS-1$
	private static final String COMMAND_ID = "com.plucknplay.csg.ui.newInstrument"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public GlobalAddInstrumentAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.GlobalAddInstrumentAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.NEW_INSTRUMENT));
	}

	@Override
	public void run() {
		try {
			WorkbenchUtil.showPerspective(window.getWorkbench(), Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
			final Instrument newInstrument = new Instrument();
			newInstrument.setMidiInstrumentNumber(Activator.getDefault().getPreferenceStore()
					.getInt(Preferences.SOUND_DEFAULT_MIDI_INSTRUMENT));
			window.getActivePage().openEditor(
					new InstrumentEditorInput(newInstrument, InstrumentList.getInstance().getRootCategory(), true),
					InstrumentEditor.ID);
		} catch (final PartInitException e) {
		} catch (final WorkbenchException e) {
		}
	}
}
