/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editors.input;

import org.eclipse.jface.resource.ImageDescriptor;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.editors.EditorMessages;
import com.plucknplay.csg.ui.views.ChordsView;

public class ChordEditorInput extends IntervalContainerEditorInput {

	public ChordEditorInput(final Chord chord, final Category category, final boolean isNewChord) {
		super(chord, category, isNewChord);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptor(IImageKeys.CHORD);
	}

	@Override
	protected String getNewElementName() {
		return EditorMessages.ChordEditorInput_new_element_name;
	}

	@Override
	public CategoryList getCategoryList() {
		return ChordList.getInstance();
	}

	@Override
	public String getViewID() {
		return ChordsView.ID;
	}
}
