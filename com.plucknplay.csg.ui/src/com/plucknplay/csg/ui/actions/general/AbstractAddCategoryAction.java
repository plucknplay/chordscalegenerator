/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.views.IRenameableView;

public abstract class AbstractAddCategoryAction extends AbstractAddAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.newCategory"; //$NON-NLS-1$

	public AbstractAddCategoryAction(final IViewPart view, final boolean popup) {
		super(view);

		setActionDefinitionId(COMMAND_ID);
		setText(popup ? NlsUtil.getAction_new_category() : NlsUtil.getAction_add_category());
		setToolTipText(popup ? NlsUtil.getAction_new_category() : NlsUtil.getAction_add_category());
		setImageDescriptor(Activator.getImageDescriptor(NlsUtil.getAction_image_new_category()));
	}

	@Override
	public void run() {
		final Category category = new Category(ActionMessages.AbstractAddCategoryAction_new_category);
		getCategoryList().addCategory(category, getSelectedCategory());
		if (getViewPart() instanceof IRenameableView) {
			final IRenameableView renameableView = (IRenameableView) getViewPart();
			renameableView.expandElement(category, getSelectedCategory());
			renameableView.performRenaming();
		}
	}
}
