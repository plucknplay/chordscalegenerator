/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.editors.input.CategorizableEditorInput;
import com.plucknplay.csg.ui.util.WorkbenchUtil;

public abstract class AbstractAddElementAction extends AbstractAddAction {

	public AbstractAddElementAction(final IViewPart view) {
		super(view);
	}

	@Override
	public void run() {
		try {
			final IWorkbenchWindow window = getViewPart().getSite().getWorkbenchWindow();
			WorkbenchUtil.showPerspective(window.getWorkbench(), Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
			getViewPart().getViewSite().getPage().openEditor(getEditorInput(getSelectedCategory()), getEditorID());
		} catch (final PartInitException e) {
		} catch (final WorkbenchException e) {
		}
	}

	protected abstract String getEditorID();

	protected abstract CategorizableEditorInput getEditorInput(Category category);
}
