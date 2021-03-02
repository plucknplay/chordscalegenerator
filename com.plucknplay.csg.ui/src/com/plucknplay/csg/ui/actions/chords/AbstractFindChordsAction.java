/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

import com.plucknplay.csg.core.calculation.ICalculator;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractFindAction;
import com.plucknplay.csg.ui.util.CalculatorUtil;
import com.plucknplay.csg.ui.util.WorkbenchUtil;
import com.plucknplay.csg.ui.views.ChordResultsView;
import com.plucknplay.csg.ui.views.ViewMessages;

public abstract class AbstractFindChordsAction extends AbstractFindAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.findChords"; //$NON-NLS-1$

	public AbstractFindChordsAction(final IWorkbenchSite site) {
		super(site);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.FindChordsAction_text);
		setToolTipText(ActionMessages.FindChordsAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.FIND_CHORDS));
		setDisabledImageDescriptor(Activator.getImageDescriptor(IImageKeys.FIND_CHORDS_DISABLED));
	}

	@Override
	public void run() {
		run(getSite(), getGriptables());
	}

	public static void run(final IWorkbenchSite site, final Collection<Griptable> griptables) {
		if (griptables == null || griptables.isEmpty()) {
			return;
		}

		final IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor) throws InterruptedException {

				monitor.beginTask(ViewMessages.AbstractGraphicalCalculationView_calculate_chords, 10000);

				try {
					// start calculation
					monitor.worked(5000);
					final ICalculator calculator = CalculatorUtil.getCalculator();

					final List<Griptable> computedGriptables = new ArrayList<Griptable>();
					final int work = griptables.size() != 0 ? 5000 / griptables.size() : 5000;
					for (final Griptable griptable : griptables) {
						final Set<Chord> computedChords = calculator.calculateCorrespondingChordsOfGriptable(griptable,
								monitor, work);

						for (final Chord chord : computedChords) {
							final Griptable newGriptable = new Griptable(griptable);
							newGriptable.setChord(chord);
							computedGriptables.add(newGriptable);
						}
					}

					final List<Griptable> theComputedGriptables = new ArrayList<Griptable>(computedGriptables);

					// open results view and pass input
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
								List<Griptable> resultList = new ArrayList<Griptable>(theComputedGriptables);

								// check max result number and show prompt if
								// necessary
								final int maxNumbersOfResults = prefs.getInt(Preferences.CALCULATOR_MAX_RESULTS_NUMBER);
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
										Preferences.PERSPECTIVES_BINDING_FIND_CHORDS);
								final ChordResultsView view = (ChordResultsView) window.getActivePage().showView(
										ChordResultsView.ID);
								view.setInput(resultList);
								view.setDistanceUnit(InstrumentList.getInstance().getCurrentInstrument()
										.getScaleLengthUnit());

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

	protected abstract Collection<Griptable> getGriptables();
}
