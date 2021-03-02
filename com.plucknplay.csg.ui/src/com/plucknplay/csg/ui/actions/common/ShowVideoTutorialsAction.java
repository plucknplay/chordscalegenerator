/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.ui.IWorkbenchWindow;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.ActionMessages;

public class ShowVideoTutorialsAction extends AbstractLinkToWebsiteAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.showVideoTutorials"; //$NON-NLS-1$

	public ShowVideoTutorialsAction(final IWorkbenchWindow window) {
		super(window, ActionMessages.ShowVideoTutorialsActions_href);

		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ShowVideoTutorialsActions_text);
		setToolTipText(ActionMessages.ShowVideoTutorialsActions_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.VIDEO_TUTORIALS));
	}
}
