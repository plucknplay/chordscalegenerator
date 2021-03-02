/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.util;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import com.plucknplay.csg.core.model.sets.Category;

public abstract class AbstractExportAction extends Action {

	protected abstract Shell getShell();

	protected abstract List<Category> getCategories();

}
