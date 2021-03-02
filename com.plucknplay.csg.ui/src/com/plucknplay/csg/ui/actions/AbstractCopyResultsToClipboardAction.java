/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.enums.Clef;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.editPartFactories.BoxEditPartFactory;
import com.plucknplay.csg.ui.editPartFactories.NotesEditPartFactory;
import com.plucknplay.csg.ui.editPartFactories.TabEditPartFactory;
import com.plucknplay.csg.ui.editParts.AbstractDraftEditPart;
import com.plucknplay.csg.ui.model.BoxDraft;
import com.plucknplay.csg.ui.model.Draft;
import com.plucknplay.csg.ui.model.NotesDraft;
import com.plucknplay.csg.ui.model.TabDraft;

public abstract class AbstractCopyResultsToClipboardAction extends AbstractImageExportAction {

	private static final String COMMAND_ID = "org.eclipse.ui.edit.copy"; //$NON-NLS-1$

	public AbstractCopyResultsToClipboardAction(final IViewPart view) {
		super(view);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.CopyToClipboardAction_text);
		setToolTipText(ActionMessages.CopyToClipboardAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.COPY));
	}

	@Override
	public void run() {

		final List<?> selectedResults = determineSelectedResults();

		// message dialog if no copy is possible
		if (selectedResults == null) {
			return;
		}
		if (selectedResults.isEmpty()) {
			showEmptyResultsInfoDialog();
		} else {

			final Shell shell = getShell();

			// create graphical viewer
			final int style = shell.getStyle();
			final Shell newShell = new Shell((style & SWT.MIRRORED) != 0 ? SWT.RIGHT_TO_LEFT : SWT.NONE);
			final GraphicalViewer viewer = new ScrollingGraphicalViewer();
			viewer.createControl(newShell);
			viewer.getControl().setBounds(0, 0, 300, 300);
			viewer.setEditDomain(new DefaultEditDomain(null));
			viewer.setRootEditPart(new ScalableRootEditPart());
			viewer.flush();

			final IRunnableWithProgress op = new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) throws InterruptedException {

					final int totalWork = 100000;

					monitor.beginTask(getMonitorTaskName(), totalWork);

					// get necessary export preferences
					final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
					final String first = prefs.getString(Preferences.CLIPBOARD_EXPORT_FIRST_VIEW);
					final String second = prefs.getString(Preferences.CLIPBOARD_EXPORT_SECOND_VIEW);
					final String third = prefs.getString(Preferences.CLIPBOARD_EXPORT_THIRD_VIEW);

					final List<String> exportModes = new ArrayList<String>();
					exportModes.add(first);
					if (!second.equals(UIConstants.EXPORT_NONE)) {
						exportModes.add(second);
					}
					if (!third.equals(UIConstants.EXPORT_NONE)) {
						exportModes.add(third);
					}

					// initialize data array
					final int size = selectedResults.size() * exportModes.size();
					final String[] data = new String[size];
					final int workPerImage = totalWork / size;

					// create all temp image files
					int i = 0;

					if (prefs.getBoolean(Preferences.CLIPBOARD_EXPORT_REVERSE_ORDER)) {
						Collections.reverse(selectedResults);
						Collections.reverse(exportModes);
					}

					for (final Object result : selectedResults) {

						for (final String exportMode : exportModes) {
							if (monitor.isCanceled()) {
								throw new InterruptedException();
							}

							monitor.subTask(NLS.bind(ActionMessages.CopyResultsToClipboardAction_copy_image, i + 1,
									size));

							final String filename = getTempFile("_" + i); //$NON-NLS-1$

							newShell.getDisplay().syncExec(new Runnable() {
								@Override
								public void run() {
									final Draft draft = getDraft(exportMode, result);
									viewer.setEditPartFactory(getEditPartFactory(exportMode));
									viewer.setContents(draft);

									// get root figure & edit part
									final AbstractDraftEditPart editPart = (AbstractDraftEditPart) viewer.getContents();
									final IFigure theFigure = editPart.getFigure();
									final IFigure firstFigure = (IFigure) theFigure.getChildren().get(0);
									final ScalableLayeredPane rootFigure = (ScalableLayeredPane) firstFigure;

									// create image
									createImageFile(rootFigure, editPart.getNormWidth(), editPart.getNormHeight(),
											getExportHeight(exportMode), filename);
								}
							});

							// store filename
							data[size - i - 1] = filename;
							i++;

							monitor.worked(workPerImage);
						}
					}

					// perform file transfer
					newShell.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							final FileTransfer fileTransfer = FileTransfer.getInstance();
							final Clipboard clipboard = new Clipboard(newShell.getDisplay());
							clipboard.setContents(new Object[] { data }, new Transfer[] { fileTransfer });
							clipboard.dispose();
						}
					});

					monitor.done();
				}
			};

			try {
				new ProgressMonitorDialog(shell).run(true, true, op);
			} catch (final InvocationTargetException e) {
			} catch (final InterruptedException e) {
			}
		}
	}

	protected abstract void showEmptyResultsInfoDialog();

	protected abstract List<?> determineSelectedResults();

	/**
	 * Returns the corresponding edit part factory of the given export mode.
	 * 
	 * @param exportMode
	 *            the export mode, must not be null, must be one of the
	 *            constants: EXPORT_BOX, EXPORT_TAB or EXPORT_NOTES
	 * 
	 * @return the corresponding edit part factory of the given export mode
	 */
	private EditPartFactory getEditPartFactory(final String exportMode) {
		if (exportMode == null) {
			throw new IllegalArgumentException();
		}

		final EditPartFactory result = exportMode.equals(UIConstants.EXPORT_BOX) ? new BoxEditPartFactory()
				: exportMode.equals(UIConstants.EXPORT_TAB) ? new TabEditPartFactory() : exportMode
						.equals(UIConstants.EXPORT_NOTES) ? new NotesEditPartFactory(null) : null;

		if (result == null) {
			throw new IllegalArgumentException();
		}

		return result;
	}

	/**
	 * Returns the corresponding draft of the given export mode.
	 * 
	 * @param exportMode
	 *            the export mode, must not be null, must be one of the
	 *            constants: EXPORT_BOX, EXPORT_TAB or EXPORT_NOTES
	 * @param result
	 *            the griptable, must not be null
	 * 
	 * @return the corresponding draft of the given export mode
	 */
	private Draft getDraft(final String exportMode, final Object result) {
		if (exportMode == null || result == null) {
			throw new IllegalArgumentException();
		}

		if (!(result instanceof Block) && !(result instanceof Griptable)) {
			throw new IllegalArgumentException();
		}

		Draft draft = exportMode.equals(UIConstants.EXPORT_BOX) ? getBoxDraft(result) : exportMode
				.equals(UIConstants.EXPORT_TAB) ? getTabDraft(result) : null;

		if (exportMode.equals(UIConstants.EXPORT_NOTES)) {
			final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
			final Clef clef = Clef.valueOf(prefs.getString(Preferences.NOTES_VIEW_CLEF));
			final boolean sharpSignOn = !prefs.getString(Preferences.NOTES_MODE).equals(Constants.NOTES_MODE_ONLY_B);
			draft = getNotesDraft(result, clef, sharpSignOn);
		}

		if (draft == null) {
			throw new IllegalArgumentException();
		}

		return draft;
	}

	private BoxDraft getBoxDraft(final Object result) {
		return result instanceof Griptable ? new BoxDraft((Griptable) result) : new BoxDraft((Block) result);
	}

	private TabDraft getTabDraft(final Object result) {
		return result instanceof Griptable ? new TabDraft((Griptable) result) : new TabDraft((Block) result);
	}

	private NotesDraft getNotesDraft(final Object result, final Clef clef, final boolean sharpSignOn) {
		return result instanceof Griptable ? new NotesDraft((Griptable) result, clef, sharpSignOn) : new NotesDraft(
				(Block) result, clef, sharpSignOn, true);
	}

	/**
	 * Returns the corresponding export height of the given export mode.
	 * 
	 * @param exportMode
	 *            the export mode, must not be null, must be one of the
	 *            constants: EXPORT_BOX, EXPORT_TAB or EXPORT_NOTES
	 * 
	 * @return the corresponding export height of the given export mode
	 */
	private int getExportHeight(final String exportMode) {
		if (exportMode == null) {
			throw new IllegalArgumentException();
		}

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final int result = exportMode.equals(UIConstants.EXPORT_BOX) ? prefs.getInt(Preferences.BOX_VIEW_EXPORT_HEIGHT)
				: exportMode.equals(UIConstants.EXPORT_TAB) ? prefs.getInt(Preferences.TAB_VIEW_EXPORT_HEIGHT)
						: exportMode.equals(UIConstants.EXPORT_NOTES) ? prefs
								.getInt(Preferences.NOTES_VIEW_EXPORT_HEIGHT) : -1;

		if (result == -1) {
			throw new IllegalArgumentException();
		}

		return result;
	}

	@Override
	public String getFileName() {
		return null; // not necessary
	}

	protected abstract String getMonitorTaskName();
}
