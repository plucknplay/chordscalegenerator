/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.util;

import org.eclipse.jface.action.IAction;

public class ExportCategoriesAsHtmlAction extends AbstractExportCategoryAction {

	@Override
	public void run(final IAction action) {
		ExportAsHtmlHelper.exportAsHtml(getShell(), getCategories());
	}
}
