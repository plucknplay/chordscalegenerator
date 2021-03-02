/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.ModelObject;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.util.ExportFilenameRecommender;
import com.plucknplay.csg.ui.util.Util;

public abstract class AbstractExportAction extends Action implements ICategoryAction, IViewSelectionAction {

	private static final String COMMAND_ID = "org.eclipse.ui.file.export"; //$NON-NLS-1$

	private final IViewPart view;
	private IStructuredSelection selection;

	private final ExportFilenameRecommender filenameRecommender;

	public AbstractExportAction(final IViewPart view) {
		this.view = view;

		setId(getActionId());
		setActionDefinitionId(COMMAND_ID);
		setText(NlsUtil.getAction_export());
		setToolTipText(NlsUtil.getAction_export());
		setImageDescriptor(Activator.getImageDescriptor(NlsUtil.getAction_image_export()));

		filenameRecommender = new ExportFilenameRecommender();
	}

	protected abstract String getActionId();

	@Override
	public void run() {

		final List<?> selectedElements = Util.validateSelection(selection);
		if (selectedElements != null && !selectedElements.isEmpty()) {

			final FileDialog dialog = new FileDialog(view.getSite().getShell(), SWT.SAVE);
			dialog.setText(getDialogTitle());
			dialog.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
			dialog.setFilterNames(new String[] { "XML" }); //$NON-NLS-1$

			final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
			if (prefs.getBoolean(Preferences.EXPORT_FILENAME_SUGGESTION)) {
				if (selectedElements.size() == 1) {
					final Object first = selectedElements.get(0);
					final String name = first instanceof ModelObject ? ((ModelObject) first).getName() : null;
					filenameRecommender.loadPreferences();
					dialog.setFileName(filenameRecommender.processName(name));
				} else {
					dialog.setFileName(getTypeName());
				}
			}

			final String result = dialog.open();

			if (result != null) {
				final XMLExportImport exporter = new XMLExportImport();
				exporter.setIgnoreCurrentInstrument(true);
				try {
					exporter.storeToXML(getType(), new File(result), selectedElements);
				} catch (final Exception e) {
					MessageDialog.openError(view.getSite().getShell(), ActionMessages.AbstractExportAction_error_title,
							ActionMessages.AbstractExportAction_error_msg);
				}
			}
		}
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
			setEnabled(!this.selection.isEmpty());
		}
	}

	protected abstract String getDialogTitle();

	protected abstract String getType();

	protected abstract String getTypeName();
}
