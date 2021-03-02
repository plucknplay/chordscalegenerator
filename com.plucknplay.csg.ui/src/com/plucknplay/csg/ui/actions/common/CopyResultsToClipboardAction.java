/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.ui.actions.AbstractCopyResultsToClipboardAction;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.views.ChordResultsView;

public class CopyResultsToClipboardAction extends AbstractCopyResultsToClipboardAction {

	public CopyResultsToClipboardAction(final IViewPart view) {
		super(view);
	}

	@Override
	protected List<?> determineSelectedResults() {
		return getView() instanceof ChordResultsView ? ((ChordResultsView) getView()).getSelectedResults() : null;
	}

	@Override
	protected void showEmptyResultsInfoDialog() {
		MessageDialog.openInformation(getShell(), ActionMessages.CopyResultsToClipboardAction_copy_to_clipboard,
				ActionMessages.CopyResultsToClipboardAction_no_griptable
						+ ActionMessages.CopyResultsToClipboardAction_please_check);
	}

	@Override
	protected String getMonitorTaskName() {
		return ActionMessages.CopyResultsToClipboardAction_copy_selected_chord_results;
	}
}
