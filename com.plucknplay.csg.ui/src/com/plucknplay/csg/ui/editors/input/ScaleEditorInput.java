/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editors.input;

import org.eclipse.jface.resource.ImageDescriptor;

import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.editors.EditorMessages;
import com.plucknplay.csg.ui.views.ScalesView;

public class ScaleEditorInput extends IntervalContainerEditorInput {

	public ScaleEditorInput(final Scale scale, final Category category, final boolean isNewScale) {
		super(scale, category, isNewScale);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptor(IImageKeys.SCALE);
	}

	@Override
	protected String getNewElementName() {
		return EditorMessages.ScaleEditorInput_new_element_name;
	}

	@Override
	public CategoryList getCategoryList() {
		return ScaleList.getInstance();
	}

	@Override
	public String getViewID() {
		return ScalesView.ID;
	}
}
