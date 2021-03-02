/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.listeners;

import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.listeners.IChangeListenerFilter;
import com.plucknplay.csg.ui.views.AbstractCategoryView;

public class CategoryViewFilter implements IChangeListenerFilter {

	@Override
	public boolean passFilter(final IChangeListener listener) {
		return !(listener instanceof AbstractCategoryView);
	}
}
