/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.AbstractImageExportAction;
import com.plucknplay.csg.ui.actions.ActionMessages;

public class CopyToClipboardAction extends AbstractImageExportAction {

	private static final String COMMAND_ID = "org.eclipse.ui.edit.copy"; //$NON-NLS-1$

	private final boolean copyToClipboard;
	private String[] data;

	public CopyToClipboardAction(final IViewPart view) {
		this(view, true);
	}

	public CopyToClipboardAction(final IViewPart view, final boolean copyToClipboard) {
		super(view);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.CopyToClipboardAction_text);
		setToolTipText(ActionMessages.CopyToClipboardAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.COPY));
		this.copyToClipboard = copyToClipboard;
	}

	@Override
	public void run() {
		super.run();

		// copy created file to clipboard
		data = new String[1];
		data[0] = getFileName();

		if (!copyToClipboard) {
			return;
		}

		final FileTransfer fileTransfer = FileTransfer.getInstance();
		final Clipboard clipboard = new Clipboard(getSite().getShell().getDisplay());
		clipboard.setContents(new Object[] { data }, new Transfer[] { fileTransfer });
		clipboard.dispose();
	}

	public String[] getData() {
		return data;
	}

	@Override
	public String getFileName() {
		return getTempFile(""); //$NON-NLS-1$
	}
}
