/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.ScaleResult;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractFindAction;
import com.plucknplay.csg.ui.util.WorkbenchUtil;
import com.plucknplay.csg.ui.views.ScaleResultsView;
import com.plucknplay.csg.ui.views.ViewMessages;

public abstract class AbstractFindScalesAction extends AbstractFindAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.findScales"; //$NON-NLS-1$

	public AbstractFindScalesAction(final IWorkbenchSite site) {
		super(site);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.FindScalesAction_text);
		setToolTipText(ActionMessages.FindScalesAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.FIND_SCALES));
		setDisabledImageDescriptor(Activator.getImageDescriptor(IImageKeys.FIND_SCALES_DISABLED));
	}

	@Override
	public void run() {
		run(getSite(), getNotes());
	}

	public static void run(final IWorkbenchSite site, final Collection<Note> notes) {
		if (notes == null || notes.size() < MIN_NUMBER_OF_NOTES) {
			return;
		}

		final IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor) throws InterruptedException {

				monitor.beginTask(ActionMessages.FindScalesAction_text, 10000);

				try {
					// start calculation
					final List<ScaleResult> computedScaleResults = new ArrayList<ScaleResult>();
					final List<Categorizable> allScales = ScaleList.getInstance().getRootCategory().getAllElements();
					if (allScales.size() == 0) {
						throw new InterruptedException();
					}
					final int work = 10000 / allScales.size();
					for (final Categorizable categorizable : allScales) {
						final Scale scale = (Scale) categorizable;
						monitor.subTask(ViewMessages.AbstractGraphicalCalculationView_check + scale.getName());
						for (int i = 0; i <= Constants.MAX_NOTES_VALUE; i++) {
							if (monitor.isCanceled()) {
								throw new InterruptedException();
							}

							final Note note = Factory.getInstance().getNote(i);
							final ScaleResult scaleResult = new ScaleResult(scale, note);
							scaleResult.addReferenceNotes(notes);
							if (scaleResult.isValid()) {
								computedScaleResults.add(scaleResult);
							}
						}
						monitor.worked(work);
					}

					// open results view and pass input
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
								List<ScaleResult> resultList = new ArrayList<ScaleResult>(computedScaleResults);

								// check max result number and show prompt if
								// necessary
								final int maxNumbersOfResults = prefs
										.getInt(Preferences.CALCULATOR_MAX_RESULTS_NUMBER);
								if (resultList.size() > maxNumbersOfResults) {
									resultList = resultList.subList(0, maxNumbersOfResults);

									final boolean hidePrompt = prefs
											.getBoolean(Preferences.WARNINGS_HIDE_PROMPT_TRUNCATE_RESULTS_VIEW);
									if (!hidePrompt) {
										final MessageDialogWithToggle dialog = MessageDialogWithToggle.openInformation(
												site.getShell(),
												ViewMessages.AbstractGraphicalCalculationView_information_title,
												ViewMessages.AbstractGraphicalCalculationView_information_msg,
												ViewMessages.AbstractGraphicalCalculationView_information_prompt,
												hidePrompt, null, null);
										if (dialog.getReturnCode() == Dialog.OK) {
											prefs.setValue(Preferences.WARNINGS_HIDE_PROMPT_TRUNCATE_RESULTS_VIEW,
													dialog.getToggleState());
										}
									}
								}

								// set input
								final IWorkbenchWindow window = site.getWorkbenchWindow();
								WorkbenchUtil.showPerspective(window.getWorkbench(),
										Preferences.PERSPECTIVES_BINDING_FIND_SCALES);
								final ScaleResultsView view = (ScaleResultsView) window.getActivePage().showView(
										ScaleResultsView.ID);
								view.setInput(resultList);

							} catch (final PartInitException e) {
							} catch (final WorkbenchException e) {
							}
						}
					});
				} catch (final InterruptedException e) {
				} catch (final Exception e) {
					throw new InterruptedException();
				} finally {
					monitor.done();
				}
			}
		};

		try {
			new ProgressMonitorDialog(site.getShell()).run(true, true, op);
		} catch (final InvocationTargetException e1) {
		} catch (final InterruptedException e1) {
		}
	}

	protected abstract Collection<Note> getNotes();
}
