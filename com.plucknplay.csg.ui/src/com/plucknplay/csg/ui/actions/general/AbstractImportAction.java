/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.xml.sax.SAXException;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.activation.NlsUtil;

public abstract class AbstractImportAction extends AbstractAddAction {

	private static final String COMMAND_ID = "org.eclipse.ui.file.import"; //$NON-NLS-1$

	public AbstractImportAction(final IViewPart view) {
		super(view);

		setId(getActionId());
		setActionDefinitionId(COMMAND_ID);
		setText(NlsUtil.getAction_import());
		setToolTipText(NlsUtil.getAction_import());
		setImageDescriptor(Activator.getImageDescriptor(NlsUtil.getAction_image_import()));
	}

	protected abstract String getActionId();

	@Override
	public void run() {

		// create file dialog
		final FileDialog dialog = new FileDialog(getViewPart().getSite().getShell(), SWT.OPEN | SWT.MULTI);
		dialog.setText(getDialogTitle());
		dialog.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
		dialog.setFilterNames(new String[] { "XML" }); //$NON-NLS-1$

		// set filter path
		final URL url = FileLocator.find(com.plucknplay.csg.data.Activator.getDefault().getBundle(),
				new Path("data/"), null); //$NON-NLS-1$
		try {
			final URL fileURL = FileLocator.toFileURL(url);
			if (fileURL != null) {
				dialog.setFilterPath(fileURL.getPath() + getDataPath());
			}
		} catch (final IOException e) {
		}

		// open dialog
		final String result = dialog.open();

		if (result != null) {
			final XMLExportImport importer = new XMLExportImport();
			importer.setIgnoreCurrentInstrument(true);

			for (int i = 0; i < dialog.getFileNames().length; i++) {
				try {
					importer.loadFromXML(getType(), new File(dialog.getFilterPath() + "/" + dialog.getFileNames()[i]),
							getSelectedCategory());
				} catch (final ParserConfigurationException e) {
					reportError();
				} catch (final SAXException e) {
					reportError();
				} catch (final IOException e) {
					reportError();
				}
			}
		}
	}

	private void reportError() {
		MessageDialog.openError(getViewPart().getSite().getShell(), ActionMessages.AbstractImportAction_error_title,
				ActionMessages.AbstractImportAction_error_msg);
	}

	protected abstract String getDataPath();

	protected abstract String getDialogTitle();

	protected abstract String getType();
}
