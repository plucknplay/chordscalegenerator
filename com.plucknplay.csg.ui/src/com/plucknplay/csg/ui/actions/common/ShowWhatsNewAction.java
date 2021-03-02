/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.ui.IWorkbenchWindow;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.ActionMessages;

public class ShowWhatsNewAction extends AbstractLinkToWebsiteAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.showWhatsNew"; //$NON-NLS-1$

	public ShowWhatsNewAction(final IWorkbenchWindow window) {
		super(window, ActionMessages.ShowWhatsNewActions_href);

		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ShowWhatsNewActions_text);
		setToolTipText(ActionMessages.ShowWhatsNewActions_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.WHATS_NEW));
	}
}
