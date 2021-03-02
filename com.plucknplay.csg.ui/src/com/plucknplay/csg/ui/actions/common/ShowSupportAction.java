/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.ui.IWorkbenchWindow;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.ActionMessages;

public class ShowSupportAction extends AbstractLinkToWebsiteAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.showSupport"; //$NON-NLS-1$

	public ShowSupportAction(final IWorkbenchWindow window) {
		super(window, ActionMessages.ShowSupportActions_href);

		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ShowSupportActions_text);
		setToolTipText(ActionMessages.ShowSupportActions_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.SUPPORT));
	}
}
