/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editors.input;

import org.eclipse.jface.resource.ImageDescriptor;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.editors.EditorMessages;

public class InstrumentEditorInput extends CategorizableEditorInput {

	private final Instrument instrument;

	public InstrumentEditorInput(final Instrument instrument, final Category category, final boolean isNewInstrument) {
		super(instrument, category, isNewInstrument);
		this.instrument = instrument;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptor(IImageKeys.INSTRUMENT);
	}

	public Instrument getInstrument() {
		return instrument;
	}

	@Override
	protected String getNewElementName() {
		return EditorMessages.InstrumentEditorInput_new_element_name;
	}

	@Override
	public CategoryList getCategoryList() {
		return InstrumentList.getInstance();
	}
}
