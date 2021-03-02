/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractAddElementAction;
import com.plucknplay.csg.ui.editors.InstrumentEditor;
import com.plucknplay.csg.ui.editors.input.CategorizableEditorInput;
import com.plucknplay.csg.ui.editors.input.InstrumentEditorInput;

public class AddInstrumentAction extends AbstractAddElementAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.addInstrumentAction"; //$NON-NLS-1$
	private static final String COMMAND_ID = "com.plucknplay.csg.ui.newInstrument"; //$NON-NLS-1$

	public AddInstrumentAction(final IViewPart view, final boolean popup) {
		super(view);
		setId(ACTION_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(popup ? ActionMessages.AddInstrumentPopupAction_text : ActionMessages.AddInstrumentAction_text);
		setToolTipText(ActionMessages.AddInstrumentAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.NEW_INSTRUMENT));
	}

	@Override
	protected String getEditorID() {
		return InstrumentEditor.ID;
	}

	@Override
	protected CategorizableEditorInput getEditorInput(final Category category) {
		final Instrument instrument = new Instrument();
		instrument.setMidiInstrumentNumber(Activator.getDefault().getPreferenceStore()
				.getInt(Preferences.SOUND_DEFAULT_MIDI_INSTRUMENT));
		return new InstrumentEditorInput(instrument, category, true);
	}

	@Override
	public CategoryList getCategoryList() {
		return InstrumentList.getInstance();
	}
}
