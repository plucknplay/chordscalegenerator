/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.util.DefaultCollectionContentProvider;
import com.plucknplay.csg.ui.util.OnlyNumbersKeyListener;

public class CalculatorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.calcultatorPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "calculations_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private Text numberOfResultsText;
	private Button preferBarresButton;

	private Button without1stButton;
	private Button without3rdButton;
	private Button without5thButton;
	private ComboViewer restrictionsViewer;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(final Composite parent) {

		// main composite
		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(mainComposite);

		// max number of results
		final Label numberOfResultsLabel = new Label(mainComposite, SWT.LEFT);
		numberOfResultsLabel.setText(PreferenceMessages.CalculatorPreferencePage_max_number_of_chord_results);

		numberOfResultsText = new Text(mainComposite, SWT.BORDER | SWT.SINGLE);
		numberOfResultsText.setText("" + prefs.getInt(Preferences.CALCULATOR_MAX_RESULTS_NUMBER)); //$NON-NLS-1$
		numberOfResultsText.addKeyListener(new OnlyNumbersKeyListener());
		numberOfResultsText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				if ("".equals(numberOfResultsText.getText())) {
					numberOfResultsText.setText("0"); //$NON-NLS-1$
				}
				final int currentValue = Integer.parseInt(numberOfResultsText.getText());
				if (currentValue < UIConstants.MIN_MAX_RESULTS_NUMBER) {
					numberOfResultsText.setText("" + UIConstants.MIN_MAX_RESULTS_NUMBER); //$NON-NLS-1$
				}
				if (currentValue > UIConstants.MAX_MAX_RESULTS_NUMBER) {
					numberOfResultsText.setText("" + UIConstants.MAX_MAX_RESULTS_NUMBER); //$NON-NLS-1$
				}
			}
		});
		numberOfResultsText.setTextLimit(5);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(numberOfResultsText);

		final Label numberOfResultsCommentLabel = new Label(mainComposite, SWT.LEFT);
		numberOfResultsCommentLabel
				.setText("(" + UIConstants.MIN_MAX_RESULTS_NUMBER + ".." + UIConstants.MAX_MAX_RESULTS_NUMBER + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// prefer barres
		preferBarresButton = new Button(mainComposite, SWT.CHECK);
		preferBarresButton.setText(PreferenceMessages.CalculatorPreferencePage_prefer_barre);
		preferBarresButton.setSelection(prefs.getBoolean(Preferences.CALCULATOR_BARRES_PREFERRED));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(3, 1).grab(true, false)
				.applyTo(preferBarresButton);

		// find chord group
		final Group findChordGroup = new Group(mainComposite, SWT.NONE);
		findChordGroup.setText(PreferenceMessages.CalculatorPreferencePage_find_chord);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(2).margins(5, 5).applyTo(findChordGroup);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(3, 1).grab(true, false).applyTo(findChordGroup);

		without1stButton = new Button(findChordGroup, SWT.CHECK);
		without1stButton.setText(PreferenceMessages.CalculatorPreferencePage_consider_chord_without_1st);
		without1stButton.setSelection(prefs.getBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_1ST));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(without1stButton);

		without3rdButton = new Button(findChordGroup, SWT.CHECK);
		without3rdButton.setText(PreferenceMessages.CalculatorPreferencePage_consider_chord_without_3rd);
		without3rdButton.setSelection(prefs.getBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_3RD));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(without3rdButton);

		without5thButton = new Button(findChordGroup, SWT.CHECK);
		without5thButton.setText(PreferenceMessages.CalculatorPreferencePage_consider_chords_without_5th);
		without5thButton.setSelection(prefs.getBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_5TH));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(without5thButton);

		final Label restrictionLabel = new Label(findChordGroup, SWT.NONE);
		restrictionLabel.setText(PreferenceMessages.CalculatorPreferencePage_restrictions);
		restrictionsViewer = new ComboViewer(findChordGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		restrictionsViewer.getCombo().setVisibleItemCount(3);
		restrictionsViewer.setContentProvider(new DefaultCollectionContentProvider());
		restrictionsViewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				if (e1 instanceof Integer && e2 instanceof Integer) {
					return ((Integer) e1).compareTo((Integer) e2);
				}
				return super.compare(viewer, e1, e2);
			}
		});
		restrictionsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof Integer) {
					final int value = ((Integer) element).intValue();
					if (value == Constants.CALCULATOR_RESTRICTION_NO) {
						return PreferenceMessages.CalculatorPreferencePage_no_restrictions;
					}
					if (value == Constants.CALCULATOR_RESTRICTION_MAX_1_EXCLUDED_INTERVAL) {
						return PreferenceMessages.CalculatorPreferencePage_only_1_excluded_interval;
					}
					if (value == Constants.CALCULATOR_RESTRICTION_MAX_2_EXCLUDED_INTERVALS) {
						return PreferenceMessages.CalculatorPreferencePage_max_2_excluded_intervals;
					}
				}
				return super.getText(element);
			}
		});
		restrictionsViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				if (element instanceof Integer) {
					final int value = ((Integer) element).intValue();
					if (value == Constants.CALCULATOR_RESTRICTION_MAX_2_EXCLUDED_INTERVALS) {
						final int numberOfSelectedCheckboxes = getNumberOfSelectedCheckboxes();
						if (numberOfSelectedCheckboxes < 3) {
							return false;
						}
					}
					return true;
				}
				return false;
			}
		});
		final List<Integer> input = new ArrayList<Integer>();
		input.add(Constants.CALCULATOR_RESTRICTION_NO);
		input.add(Constants.CALCULATOR_RESTRICTION_MAX_1_EXCLUDED_INTERVAL);
		input.add(Constants.CALCULATOR_RESTRICTION_MAX_2_EXCLUDED_INTERVALS);
		restrictionsViewer.setInput(input);
		restrictionsViewer.setSelection(new StructuredSelection(Integer.valueOf(prefs
				.getInt(Preferences.CALCULATOR_FIND_CHORDS_RESTRICTIONS))));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(restrictionsViewer.getControl());

		// add selection listener to
		final SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateViewer();
			}
		};
		without1stButton.addSelectionListener(listener);
		without3rdButton.addSelectionListener(listener);
		without5thButton.addSelectionListener(listener);
		updateViewer();

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);

		return mainComposite;
	}

	private void updateViewer() {
		restrictionsViewer.refresh();
		final int numberOfSelectedCheckboxes = getNumberOfSelectedCheckboxes();
		restrictionsViewer.getCombo().setEnabled(numberOfSelectedCheckboxes > 1);
		if (numberOfSelectedCheckboxes < 2 || restrictionsViewer.getSelection().isEmpty()) {
			restrictionsViewer.setSelection(new StructuredSelection(Integer
					.valueOf(Constants.CALCULATOR_RESTRICTION_NO)));
		}
	}

	private int getNumberOfSelectedCheckboxes() {
		int result = 0;
		if (without1stButton.getSelection()) {
			result++;
		}
		if (without3rdButton.getSelection()) {
			result++;
		}
		if (without5thButton.getSelection()) {
			result++;
		}
		return result;
	}

	private int getRestrictionSelection() {
		final ISelection selection = restrictionsViewer.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			final Object first = ((IStructuredSelection) selection).getFirstElement();
			if (first instanceof Integer) {
				final int value = ((Integer) first).intValue();
				if (value == Constants.CALCULATOR_RESTRICTION_NO
						|| value == Constants.CALCULATOR_RESTRICTION_MAX_1_EXCLUDED_INTERVAL
						|| value == Constants.CALCULATOR_RESTRICTION_MAX_2_EXCLUDED_INTERVALS) {
					return value;
				}
			}
		}
		return Constants.CALCULATOR_RESTRICTION_NO;
	}

	@Override
	public boolean performOk() {
		prefs.setValue(Preferences.CALCULATOR_MAX_RESULTS_NUMBER, Integer.parseInt(numberOfResultsText.getText()));
		prefs.setValue(Preferences.CALCULATOR_BARRES_PREFERRED, preferBarresButton.getSelection());
		prefs.setValue(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_1ST, without1stButton.getSelection());
		prefs.setValue(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_3RD, without3rdButton.getSelection());
		prefs.setValue(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_5TH, without5thButton.getSelection());
		prefs.setValue(Preferences.CALCULATOR_FIND_CHORDS_RESTRICTIONS, getRestrictionSelection());
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		numberOfResultsText.setText("" + prefs.getDefaultInt(Preferences.CALCULATOR_MAX_RESULTS_NUMBER)); //$NON-NLS-1$
		preferBarresButton.setSelection(prefs.getDefaultBoolean(Preferences.CALCULATOR_BARRES_PREFERRED));
		without1stButton.setSelection(prefs.getDefaultBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_1ST));
		without3rdButton.setSelection(prefs.getDefaultBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_3RD));
		without5thButton.setSelection(prefs.getDefaultBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_5TH));
		restrictionsViewer.setSelection(new StructuredSelection(Integer.valueOf(prefs
				.getDefaultInt(Preferences.CALCULATOR_FIND_CHORDS_RESTRICTIONS))));
		updateViewer();
		super.performDefaults();
	}
}
