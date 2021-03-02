/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.actions.AbstractImageExportAction;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.util.ExportFilenameRecommender;
import com.plucknplay.csg.ui.views.AbstractGraphicalCalculationView;
import com.plucknplay.csg.ui.views.BoxView;
import com.plucknplay.csg.ui.views.FretboardView;
import com.plucknplay.csg.ui.views.KeyboardView;
import com.plucknplay.csg.ui.views.NotesView;
import com.plucknplay.csg.ui.views.TabView;

public class ExportAsImageFileAction extends AbstractImageExportAction {

	private static final String COMMAND_ID = "org.eclipse.ui.file.export"; //$NON-NLS-1$

	private final String viewAddition;

	private final ExportFilenameRecommender filenameRecommender;

	public ExportAsImageFileAction(final IViewPart view) {
		super(view);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ExportAsImageFileAction_text);
		setToolTipText(ActionMessages.ExportAsImageFileAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.EXPORT));

		filenameRecommender = new ExportFilenameRecommender();
		viewAddition = view instanceof BoxView ? ExportFilenameRecommender.VIEW_BOX
				: view instanceof TabView ? ExportFilenameRecommender.VIEW_TAB
						: view instanceof NotesView ? ExportFilenameRecommender.VIEW_NOTES
								: view instanceof FretboardView ? ExportFilenameRecommender.VIEW_FRETBOARD
										: view instanceof KeyboardView ? ExportFilenameRecommender.VIEW_KEYBOARD : "";
	}

	@Override
	public String getFileName() {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final String extension = prefs.getString(Preferences.CLIPBOARD_EXPORT_FILE_EXTENSION);

		final FileDialog dialog = new FileDialog(getSite().getShell(), SWT.SAVE);
		dialog.setText(ActionMessages.ExportAsImageFileAction_choose_an_image_file);
		dialog.setFilterExtensions(new String[] { "*.bmp", "*.jpg", "*.png" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dialog.setFilterNames(new String[] { "BMP", "JPEG", "PNG" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dialog.setFilterIndex(extension.equals(UIConstants.FILE_EXTENSION_JPG) ? 1 : extension
				.equals(UIConstants.FILE_EXTENSION_PNG) ? 2 : 0);

		if (prefs.getBoolean(Preferences.EXPORT_FILENAME_SUGGESTION)
				&& getView() instanceof AbstractGraphicalCalculationView) {
			filenameRecommender.loadPreferences();
			final Object input = ((AbstractGraphicalCalculationView) getView()).getExportInput();
			if (input != null) {
				dialog.setFileName(filenameRecommender.suggestFilename(input, viewAddition));
			} else if (getView() instanceof FretboardView) {
				final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
				if (currentInstrument != null) {
					String filename = filenameRecommender.processName(currentInstrument.getName());
					if (prefs.getBoolean(Preferences.EXPORT_FILENAME_ADD_VIEW_FRETBOARD)) {
						filename = filenameRecommender.addString(filename, ExportFilenameRecommender.VIEW_FRETBOARD);
					}
					dialog.setFileName(filename);
				}
			}
		}
		return dialog.open();
	}
}
