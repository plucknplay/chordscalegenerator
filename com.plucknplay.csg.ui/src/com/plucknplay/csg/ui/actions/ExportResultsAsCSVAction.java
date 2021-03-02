/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Unit;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.views.ChordResultsView;

public class ExportResultsAsCSVAction implements IViewActionDelegate {

	private IViewPart view;
	private Shell shell;

	@Override
	public void init(final IViewPart view) {
		this.view = view;
		shell = view.getSite().getShell();
	}

	@Override
	public void run(final IAction action) {
		if (view instanceof ChordResultsView) {

			final List<Griptable> selectedResults = ((ChordResultsView) view).getSelectedResults();
			final Unit distanceUnit = ((ChordResultsView) view).getDistanceUnit();

			// message dialog if no export is possible
			if (selectedResults.isEmpty()) {
				MessageDialog.openInformation(shell, ActionMessages.ExportResultsAsCSVAction_dialog_title,
						ActionMessages.ExportResultsAsCSVAction_dialog_msg_1
								+ ActionMessages.ExportResultsAsCSVAction_dialog_msg_2);
			} else {
				try {

					final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
					dialog.setText(ActionMessages.ExportResultsAsCSVAction_dialog_text);
					dialog.setFilterExtensions(new String[] { "*.csv" }); //$NON-NLS-1$
					dialog.setFilterNames(new String[] { "CSV" }); //$NON-NLS-1$

					final String result = dialog.open();
					if (result != null) {
						writeResultsToCSVFile(selectedResults, new File(result), distanceUnit);
					}

				} catch (final IOException e) {
					MessageDialog.openError(null, ActionMessages.ExportResultsAsCSVAction_error_title,
							ActionMessages.ExportResultsAsCSVAction_error_msg + e.getMessage());
				}
			}
		}
	}

	private void writeResultsToCSVFile(final List<Griptable> selectedResults, final File file, final Unit distanceUnit)
			throws IOException {

		if (file.createNewFile()) {
			final PrintWriter printWriter = new PrintWriter(new FileWriter(file, false), false);

			final String firstLine = ActionMessages.ExportResultsAsCSVAction_columns_1
					+ ActionMessages.ExportResultsAsCSVAction_columns_2 + " (" + distanceUnit + ");" + //$NON-NLS-1$//$NON-NLS-2$
					ActionMessages.ExportResultsAsCSVAction_columns_3;
			printWriter.println(firstLine);
			printWriter.flush(); // needed for IOExceptions to be thrown.

			final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
			final String notesMode = prefs.getString(Preferences.NOTES_MODE);
			final boolean preferBarrees = prefs.getBoolean(Preferences.CALCULATOR_BARRES_PREFERRED);
			final boolean compactMode = prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_COMPACT_MODE);
			final String sep = ";";

			for (final Griptable griptable : selectedResults) {
				final StringBuffer row = new StringBuffer();

				row.append(griptable.getChord().toString() + sep);
				row.append(UIConstants.LEVELS[griptable.getFingering(preferBarrees).getLevel()] + sep);
				row.append(griptable.isWithout1st() ? ActionMessages.ExportResultsAsCSVAction_yes
						: ActionMessages.ExportResultsAsCSVAction_no + sep);
				row.append(griptable.isWithout3rd() ? ActionMessages.ExportResultsAsCSVAction_yes
						: ActionMessages.ExportResultsAsCSVAction_no + sep);
				row.append(griptable.isWithout5th() ? ActionMessages.ExportResultsAsCSVAction_yes
						: ActionMessages.ExportResultsAsCSVAction_no + sep);
				row.append(griptable.getRelativeBassToneString(notesMode) + sep);
				row.append(griptable.getAbsoluteBassToneString(notesMode) + sep);
				row.append(griptable.getBassIntervalString() + sep);
				row.append(griptable.getRelativeLeadToneString(notesMode) + sep);
				row.append(griptable.getAbsoluteLeadToneString(notesMode) + sep);
				row.append(griptable.getLeadIntervalString() + sep);
				row.append(griptable.getNotesString(notesMode, compactMode) + sep);
				row.append(griptable.getIntervalString(compactMode) + sep);
				row.append(griptable.getFretsString(compactMode) + sep);
				row.append(griptable.getMinString() + sep);
				row.append(griptable.getMaxString() + sep);
				row.append(griptable.getStringSpan() + sep);
				row.append(griptable.getMinFret() + sep);
				row.append(griptable.getMaxFret() + sep);
				row.append(griptable.getFretSpan() + sep);
				row.append(griptable.getFormattedGripDistance(distanceUnit) + sep);
				row.append(griptable.getEmptyStringsCount() + sep);
				row.append(griptable.getMutedStringsCount() + sep);
				row.append(griptable.hasDoubledTones() ? ActionMessages.ExportResultsAsCSVAction_yes
						: ActionMessages.ExportResultsAsCSVAction_no);

				printWriter.println(row.toString());
			}
			printWriter.close();
		}
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		// do nothing
	}
}
