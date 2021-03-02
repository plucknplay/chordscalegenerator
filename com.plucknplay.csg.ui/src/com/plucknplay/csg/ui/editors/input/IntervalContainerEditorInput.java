/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editors.input;

import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.sets.Category;

public abstract class IntervalContainerEditorInput extends CategorizableEditorInput {

	private final IntervalContainer intervalContainer;

	public IntervalContainerEditorInput(final IntervalContainer intervalContainer, final Category category,
			final boolean isNewElement) {
		super(intervalContainer, category, isNewElement);
		this.intervalContainer = intervalContainer;
	}

	public IntervalContainer getIntervalContainer() {
		return intervalContainer;
	}

	public abstract String getViewID();
}
