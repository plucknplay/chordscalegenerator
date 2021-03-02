/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import com.plucknplay.csg.core.model.sets.CategoryList;

public interface ICategoryAction {

	/**
	 * Returns the category list the action is associated with.
	 * 
	 * @return the category list the action is associated with, never null
	 */
	CategoryList getCategoryList();
}
