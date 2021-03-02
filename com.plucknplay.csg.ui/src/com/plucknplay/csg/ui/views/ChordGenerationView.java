/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.calculation.CalculationDescriptor;
import com.plucknplay.csg.core.calculation.ICalculator;
import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.Unit;
import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.actions.OpenSpecificPreferencePageAction;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.dialogs.IntervalContainerSelectionDialog;
import com.plucknplay.csg.ui.preferencePages.ChordGenerationViewPreferencePage;
import com.plucknplay.csg.ui.util.CalculatorUtil;
import com.plucknplay.csg.ui.util.CategoryListContentProvider;
import com.plucknplay.csg.ui.util.ChordLabelProvider;
import com.plucknplay.csg.ui.util.DefaultCollectionContentProvider;
import com.plucknplay.csg.ui.util.IntervalTableViewerLabelProvider;
import com.plucknplay.csg.ui.util.IntervalViewerSorter;
import com.plucknplay.csg.ui.util.LayoutUtil;
import com.plucknplay.csg.ui.util.OnlyNumbersKeyListener;
import com.plucknplay.csg.ui.util.StatusLineUtil;
import com.plucknplay.csg.ui.util.ViewUtil;
import com.plucknplay.csg.ui.util.WidgetFactory;
import com.plucknplay.csg.ui.util.WorkbenchUtil;
import com.plucknplay.csg.ui.views.dnd.GriptableTransfer;
import com.plucknplay.csg.ui.views.dnd.ModelObjectTransfer;

public class ChordGenerationView extends ViewPart implements IChangeListener, IPropertyChangeListener {

	public static final String ID = "com.plucknplay.csg.ui.views.ChordGenerationView"; //$NON-NLS-1$
	public static final String HELP_ID = "chord_generation_view_context"; //$NON-NLS-1$

	public static final String I_DONT_CARE = ViewMessages.ChordGenerationView_i_dont_care;

	private static final int HORIZONAL_SPACING = 20;

	private static final int TOO_LESS_INTERVALS = -1;
	private static final int TOO_MANY_INTERVALS = 0;
	private static final int PASSES_FILTER = 1;

	private IViewSite site;
	private Instrument currentInstrument;
	private IPreferenceStore prefs;

	private Object lastBassInterval = I_DONT_CARE;
	private Object lastLeadInterval = I_DONT_CARE;

	private Composite composite;
	private FormToolkit toolkit;
	private ScrolledForm form;
	private ComboViewer rootNoteComboViewer;
	private ComboViewer chordComboViewer;
	private ComboViewer bassIntervalComboViewer;
	private ComboViewer leadIntervalComboViewer;
	private ComboViewer minStringComboViewer;
	private ComboViewer maxStringComboViewer;
	private Combo minLevelCombo;
	private Combo maxLevelCombo;
	private Text minFretText;
	private Text maxFretText;
	private Text rangeText;
	private Combo unitCombo;
	private Combo maxToneNumberCombo;
	private Button emptyStringsButton;
	private Button mutedStringsButton;
	private Button onlyPackedButton;
	private Button onlySingleMutpedStringsButton;
	private Button doubledTonesButton;
	private Button ascendingDescendingButton;
	private Button without1Button;
	private Button without3Button;
	private Button without5Button;
	private Button calculateButton;
	private Button chordBrowseButton;
	private Label fretRangeInfoLabel;
	private Label gripRangeInfoLabel;

	private boolean minFretTextGainedFocus;
	private boolean maxFretTextGainedFocus;
	private boolean rangeTextGainedFocus;

	private int maxFirstColumnWidth;
	private int minFirstColumnWidth;
	private int maxSecondColumnWidth;
	private int minSecondColumnWidth;

	private Section advancedSection;

	private final KeyListener keyListener = new OnlyNumbersKeyListener();
	private final KeyListener keyListener2 = new OnlyNumbersKeyListener(true);

	private OpenSpecificPreferencePageAction openPreferencesAction;

	private final ISelectionChangedListener minStringSelectionChangedListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			updateStringRange(false, true);
			updateChords();
			updateToneNumbers();
		}
	};

	private final ISelectionChangedListener maxStringSelectionChangedListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			updateStringRange(true, false);
			updateChords();
			updateToneNumbers();
		}
	};

	private class WithoutIntervalsSelectionListener extends SelectionAdapter {

		private final int[] halfstepsToExclude;
		private final String prefId;

		public WithoutIntervalsSelectionListener(final int[] halfstepsToExlude, final String prefId) {
			halfstepsToExclude = halfstepsToExlude;
			this.prefId = prefId;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			updateChords();

			// update bass tone
			if (isExcluded(lastBassInterval)) {
				lastBassInterval = I_DONT_CARE;
				bassIntervalComboViewer.setSelection(new StructuredSelection(lastBassInterval));
			}
			bassIntervalComboViewer.refresh();

			// update lead tone
			if (isExcluded(lastLeadInterval)) {
				lastLeadInterval = I_DONT_CARE;
				leadIntervalComboViewer.setSelection(new StructuredSelection(lastLeadInterval));
			}
			leadIntervalComboViewer.refresh();

			updateToneNumbers();

			prefs.setValue(prefId, ((Button) e.widget).getSelection());
		}

		private boolean isExcluded(final Object interval) {
			if (interval == I_DONT_CARE) {
				return false;
			}
			if (interval instanceof Interval) {
				final Interval theInterval = (Interval) interval;
				for (final int currentHalfsteps : halfstepsToExclude) {
					if (theInterval.getHalfsteps() == currentHalfsteps) {
						return true;
					}
				}
			}
			return false;
		}
	};

	/**
	 * Content provider for the bass and lead interval combo viewer.
	 */
	private static class IntervalContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof Chord) {
				final Chord chord = (Chord) inputElement;
				final List<Object> result = new ArrayList<Object>(chord.getIntervals());
				result.add(I_DONT_CARE);
				return result.toArray();
			}
			return new Object[0];
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
	}

	/**
	 * Viewer filter for the bass and lead interval combo viewer.
	 */
	private class IntervalViewerFilter extends ViewerFilter {
		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			if (element instanceof Interval) {
				final Interval interval = (Interval) element;
				if (without1Button != null && !without1Button.isDisposed() && without1Button.getSelection()
						&& interval.equals(Factory.getInstance().getInterval(0))) {
					return false;
				}
				if (without3Button != null
						&& !without3Button.isDisposed()
						&& without3Button.getSelection()
						&& (interval.equals(Factory.getInstance().getInterval(3)) || interval.equals(Factory
								.getInstance().getInterval(4)))) {
					return false;
				}
				if (without5Button != null && !without5Button.isDisposed() && without5Button.getSelection()
						&& interval.equals(Factory.getInstance().getInterval(7))) {
					return false;
				}
			}
			return true;
		}
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		this.site = site;

		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		InstrumentList.getInstance().addChangeListener(this);
		ChordList.getInstance().addChangeListener(this);
		prefs = Activator.getDefault().getPreferenceStore();

		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	@Override
	public void createPartControl(final Composite parent) {

		// init ui forms
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);

		form.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				updateCheckboxes();
			}
		});

		// populate form body
		populateFormBody(true);

		contributeToActionBars();

		// set context-sensitive help
		Activator.getDefault().setHelp(parent, HELP_ID);
	}

	/**
	 * Populates the form body.
	 * 
	 * @param addDropSupport
	 *            true if drop support shall be added, or false if otherwise
	 */
	private void populateFormBody(final boolean addDropSupport) {

		if (composite != null && !composite.isDisposed()) {
			final Control[] children = composite.getChildren();
			for (final Control element : children) {
				element.dispose();
			}
		} else {
			composite = form.getBody();
		}

		GridLayoutFactory.fillDefaults().numColumns(5).margins(15, 15).spacing(5, 10).applyTo(composite);

		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 55;

		// (0) get advanced section info
		final boolean isBassToneAdvanced = prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_BASS_TONE);
		final boolean isLeadToneAdvanced = prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_LEAD_TONE);
		final boolean isLevelAdvanced = prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_LEVEL);
		final boolean isStringRangeAdvanced = prefs
				.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_STRING_RANGE);
		final boolean isFretRangeAdvanced = prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_FRET_RANGE);
		final boolean isGripRangeAdvanced = prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_GRIP_RANGE);
		final boolean isMaxSingleToneNumberAdvanced = prefs
				.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_MAX_SINGLE_TONE_NUMBER);
		final boolean isEmptyMutedStringsAdvanced = prefs
				.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_EMPTY_MUTED_STRINGS);
		final boolean isMutedStringInfoAdvanced = prefs
				.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_MUTED_STRINGS_INFO);
		final boolean isToneInfoAdvanced = prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_TONES_INFO);
		final boolean isExcludedIntervalsAdvanced = prefs
				.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_EXCLUDED_INTERVALS);

		final boolean isAdvancedSectionExpanded = prefs
				.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_SECTION_EXPANDED);
		final boolean isAdvancedSectionNecessary = isBassToneAdvanced || isLeadToneAdvanced || isLevelAdvanced
				|| isStringRangeAdvanced || isFretRangeAdvanced || isGripRangeAdvanced || isMaxSingleToneNumberAdvanced
				|| isEmptyMutedStringsAdvanced || isMutedStringInfoAdvanced || isToneInfoAdvanced
				|| isExcludedIntervalsAdvanced;

		// (1) first create all non-advanced widget rows

		createChordRow(composite, gridData);

		if (!isBassToneAdvanced) {
			createBassToneRow(composite, gridData);
		}
		if (!isLeadToneAdvanced) {
			createLeadToneRow(composite, gridData);
		}
		if (!isLevelAdvanced) {
			createLevelRow(composite, gridData);
		}
		if (!isStringRangeAdvanced) {
			createStringRangeRow(composite, gridData);
		}
		if (!isFretRangeAdvanced) {
			createFretRangeRow(composite, gridData);
		}
		if (!isGripRangeAdvanced) {
			createGripRangeRow(composite, gridData);
		}
		if (!isMaxSingleToneNumberAdvanced) {
			createMaxSingleToneNumberRow(composite, gridData);
		}

		if (!isEmptyMutedStringsAdvanced || !isMutedStringInfoAdvanced || !isToneInfoAdvanced
				|| !isExcludedIntervalsAdvanced) {

			final Composite checkBoxComposite = toolkit.createComposite(composite);
			GridDataFactory.fillDefaults().grab(true, false).span(5, 1).applyTo(checkBoxComposite);
			GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 5)
					.spacing(HORIZONAL_SPACING, 5).applyTo(checkBoxComposite);

			if (!isEmptyMutedStringsAdvanced) {
				createStringCheckBoxesRow(checkBoxComposite);
			}
			if (!isMutedStringInfoAdvanced) {
				createMutedStringsCheckBoxesRow(checkBoxComposite);
			}
			if (!isToneInfoAdvanced) {
				createAdvancedCheckBoxesRow(checkBoxComposite);
			}
			if (!isExcludedIntervalsAdvanced) {
				createExcludedIntervalsRow(checkBoxComposite);
			}
		}

		// (2) create advanced section

		if (isAdvancedSectionNecessary) {

			// (2.1) create advanced section
			advancedSection = toolkit.createSection(composite, Section.TWISTIE | Section.EXPANDED);
			advancedSection.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(final ExpansionEvent e) {
					form.reflow(true);
					prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_SECTION_EXPANDED, e.getState());
				}
			});
			advancedSection.setText(ViewMessages.ChordGenerationView_advanced_section);
			advancedSection.setExpanded(isAdvancedSectionExpanded);
			GridDataFactory.fillDefaults().grab(true, false).span(5, 1).applyTo(advancedSection);
			toolkit.createCompositeSeparator(advancedSection);

			// (2.2) create advanced composite for the section
			final Composite advancedComposite = toolkit.createComposite(advancedSection);
			GridLayoutFactory.fillDefaults().numColumns(5).margins(0, 10).spacing(5, 10).applyTo(advancedComposite);
			advancedSection.setClient(advancedComposite);

			// (2.3) create advanced widget rows
			if (isBassToneAdvanced) {
				createBassToneRow(advancedComposite, gridData);
			}
			if (isLeadToneAdvanced) {
				createLeadToneRow(advancedComposite, gridData);
			}
			if (isLevelAdvanced) {
				createLevelRow(advancedComposite, gridData);
			}
			if (isStringRangeAdvanced) {
				createStringRangeRow(advancedComposite, gridData);
			}
			if (isFretRangeAdvanced) {
				createFretRangeRow(advancedComposite, gridData);
			}
			if (isGripRangeAdvanced) {
				createGripRangeRow(advancedComposite, gridData);
			}
			if (isMaxSingleToneNumberAdvanced) {
				createMaxSingleToneNumberRow(advancedComposite, gridData);
			}

			if (isEmptyMutedStringsAdvanced || isMutedStringInfoAdvanced || isToneInfoAdvanced
					|| isExcludedIntervalsAdvanced) {

				final Composite advancedCheckBoxComposite = toolkit.createComposite(advancedComposite);
				GridDataFactory.fillDefaults().grab(true, false).span(5, 1).applyTo(advancedCheckBoxComposite);
				GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 5).spacing(20, 5)
						.applyTo(advancedCheckBoxComposite);

				if (isEmptyMutedStringsAdvanced) {
					createStringCheckBoxesRow(advancedCheckBoxComposite);
				}
				if (isMutedStringInfoAdvanced) {
					createMutedStringsCheckBoxesRow(advancedCheckBoxComposite);
				}
				if (isToneInfoAdvanced) {
					createAdvancedCheckBoxesRow(advancedCheckBoxComposite);
				}
				if (isExcludedIntervalsAdvanced) {
					createExcludedIntervalsRow(advancedCheckBoxComposite);
				}
			}
		}

		// (3) create calculate button
		calculateButton = toolkit.createButton(composite, ViewMessages.ChordGenerationView_calculate, SWT.PUSH);
		calculateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				// Check necessary (see Redmine-Bug #210)
				if (minFretTextGainedFocus) {
					minFretTextFocusLost();
				}
				if (maxFretTextGainedFocus) {
					maxFretTextFocusLost();
				}
				if (rangeTextGainedFocus) {
					rangeTextFocusLost();
				}
				startCalculation();
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(false, false).span(5, 1).indent(0, 5)
				.applyTo(calculateButton);

		// (4) adjust form
		form.reflow(true);

		// (5) init with stored values

		// (5.1) init string range
		if (currentInstrument != null) {
			int minStringValue = prefs.getInt(Preferences.CHORD_GENERATION_VIEW_MIN_STRING);
			int maxStringValue = prefs.getInt(Preferences.CHORD_GENERATION_VIEW_MAX_STRING);

			if (maxStringValue > currentInstrument.getStringCount()) {
				maxStringValue = currentInstrument.getStringCount();
			}
			if (minStringValue >= maxStringValue) {
				minStringValue = maxStringValue - 1;
			}

			minStringComboViewer.setSelection(new StructuredSelection(Integer.valueOf(minStringValue)));
			maxStringComboViewer.setSelection(new StructuredSelection(Integer.valueOf(maxStringValue)));
		}

		// (5.2) init root note
		rootNoteComboViewer.setSelection(new StructuredSelection(Factory.getInstance().getNote(
				prefs.getInt(Preferences.CHORD_GENERATION_VIEW_ROOT_NOTE))));

		// (5.3) init chord, bass and lead tone
		chordComboViewer.setInput(ChordList.getInstance());
		final int bassToneValue = prefs.getInt(Preferences.CHORD_GENERATION_VIEW_BASS_TONE);
		lastBassInterval = I_DONT_CARE;
		if (bassToneValue >= 0) {
			lastBassInterval = Factory.getInstance().getInterval(bassToneValue);
		}

		final int leadToneValue = prefs.getInt(Preferences.CHORD_GENERATION_VIEW_LEAD_TONE);
		lastLeadInterval = I_DONT_CARE;
		if (leadToneValue >= 0) {
			lastLeadInterval = Factory.getInstance().getInterval(leadToneValue);
		}

		final int maxSingleToneNumber = prefs.getInt(Preferences.CHORD_GENERATION_VIEW_MAX_SINGLE_TONE_NUMBERS);

		String chordName = prefs.getString(Preferences.CHORD_GENERATION_VIEW_CHORD);
		if ("".equals(chordName)) {
			chordName = Constants.BLANK_CHORD_NAME;
		}
		final Categorizable chord = ChordList.getInstance().getElement(chordName);
		if (chord != null) {
			chordComboViewer.setSelection(new StructuredSelection(chord));
		} else if (chordComboViewer.getCombo().getItemCount() != 0) {
			chordComboViewer.setSelection(new StructuredSelection(chordComboViewer.getElementAt(0)));
		}

		bassIntervalComboViewer.setSelection(new StructuredSelection(lastBassInterval));
		leadIntervalComboViewer.setSelection(new StructuredSelection(lastLeadInterval));

		// (5.4) init level
		minLevelCombo.select(prefs.getInt(Preferences.CHORD_GENERATION_VIEW_MIN_LEVEL));
		maxLevelCombo.select(prefs.getInt(Preferences.CHORD_GENERATION_VIEW_MAX_LEVEL));

		// (5.5) init fret range
		minFretText.setText("" + prefs.getInt(Preferences.CHORD_GENERATION_VIEW_MIN_FRET)); //$NON-NLS-1$
		maxFretText.setText("" + prefs.getInt(Preferences.CHORD_GENERATION_VIEW_MAX_FRET)); //$NON-NLS-1$

		// (5.6) init grip range
		unitCombo.select(prefs.getInt(Preferences.CHORD_GENERATION_VIEW_GRIP_RANGE_UNIT));
		rangeText.setText(prefs.getString(Preferences.CHORD_GENERATION_VIEW_GRIP_RANGE));
		updateGripRangeText(false);

		// (5.7) init max. single tone number
		maxToneNumberCombo.select(maxSingleToneNumber);

		// (5.8) init check boxes
		emptyStringsButton.setSelection(prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_EMPTY_STRINGS));
		mutedStringsButton.setSelection(prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_MUTED_STRINGS));
		onlyPackedButton.setSelection(prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_ONLY_PACKED));
		onlySingleMutpedStringsButton.setSelection(prefs
				.getBoolean(Preferences.CHORD_GENERATION_VIEW_ONLY_SINGLE_MUTED_STRINGS));
		doubledTonesButton.setSelection(prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_DOUBLED_TONES));
		ascendingDescendingButton.setSelection(prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_ONLY_ASCENDING));
		without1Button.setSelection(prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_WITHOUT_1ST));
		without3Button.setSelection(prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_WITHOUT_3RD));
		without5Button.setSelection(prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_WITHOUT_5TH));

		// (6) final stuff
		updateWidgets();
		if (addDropSupport) {
			addDropSupport();
		}
	}

	/**
	 * Creates the chord row.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param gridData
	 *            the grid data object to copy from
	 */
	private void createChordRow(final Composite parent, final GridData gridData) {

		toolkit.createLabel(parent, ViewMessages.ChordGenerationView_chord, SWT.NONE);

		rootNoteComboViewer = WidgetFactory.createRelativeNotesComboViewer(parent);
		rootNoteComboViewer.getCombo().select(0);
		rootNoteComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final ISelection selection = event.getSelection();
				if (selection == null || !(selection instanceof IStructuredSelection)) {
					return;
				}
				final IStructuredSelection structured = (IStructuredSelection) selection;
				final Object first = structured.getFirstElement();
				if (first == null || !(first instanceof Note)) {
					return;
				}
				final Note selectedNote = (Note) first;
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_ROOT_NOTE, selectedNote.getValue());
				// updateBassLeadCombos();
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(rootNoteComboViewer.getCombo());

		toolkit.createLabel(parent, "", SWT.NONE); //$NON-NLS-1$

		chordComboViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		chordComboViewer.setLabelProvider(new ChordLabelProvider());
		chordComboViewer.setContentProvider(new CategoryListContentProvider());
		chordComboViewer.setSorter(new ViewerSorter());
		chordComboViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				if (currentInstrument == null) {
					return true;
				}
				if (element instanceof Chord) {
					return passFilter((Chord) element) == PASSES_FILTER;
				}
				return false;
			}
		});
		chordComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				updateBassLeadCombos();
				updateToneNumbers();
			}
		});
		chordComboViewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_CHORD, chordComboViewer.getCombo().getText());
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(chordComboViewer.getCombo());

		// chord browse button
		chordBrowseButton = toolkit.createButton(parent, "", SWT.PUSH); //$NON-NLS-1$
		chordBrowseButton.setImage(Activator.getDefault().getImage(IImageKeys.BROWSE));
		chordBrowseButton.setToolTipText(ViewMessages.ScaleFinderView_browse_chords);
		chordBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				// create filtered input
				int tooLessIntervalsCounter = 0;
				int tooManyIntervalsCounter = 0;
				final List<Categorizable> input = new ArrayList<Categorizable>();
				for (final Categorizable categorizable : ChordList.getInstance().getRootCategory().getAllElements()) {
					final Chord chord = (Chord) categorizable;
					final int filterResult = passFilter(chord);
					if (filterResult == PASSES_FILTER) {
						input.add(chord);
					} else if (filterResult == TOO_LESS_INTERVALS) {
						tooLessIntervalsCounter++;
					} else if (filterResult == TOO_MANY_INTERVALS) {
						tooManyIntervalsCounter++;
					}
				}

				final IntervalContainerSelectionDialog dialog = new IntervalContainerSelectionDialog(chordBrowseButton
						.getShell(), IntervalContainer.TYPE_CHORD, input, tooLessIntervalsCounter,
						tooManyIntervalsCounter);
				dialog.setImage(Activator.getDefault().getImage(IImageKeys.CHORD));
				dialog.setTitle(ViewMessages.ChordGenerationView_choose_chord);

				if (dialog.open() == Dialog.OK) {
					final Chord chord = (Chord) dialog.getResult()[0];
					chordComboViewer.setSelection(new StructuredSelection(chord));
				}
			}
		});
	}

	/**
	 * Creates the bass tone row.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param gridData
	 *            the grid data object to copy from
	 */
	private void createBassToneRow(final Composite parent, final GridData gridData) {
		toolkit.createLabel(parent, ViewMessages.ChordGenerationView_bass_tone, SWT.NONE);

		bassIntervalComboViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		bassIntervalComboViewer.getCombo().setVisibleItemCount(7);
		bassIntervalComboViewer.setLabelProvider(new IntervalTableViewerLabelProvider());
		bassIntervalComboViewer.setContentProvider(new IntervalContentProvider());
		bassIntervalComboViewer.setSorter(new IntervalViewerSorter());
		bassIntervalComboViewer.addFilter(new IntervalViewerFilter());
		bassIntervalComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					lastBassInterval = ((IStructuredSelection) event.getSelection()).getFirstElement();
				}
				if (lastBassInterval == null) {
					lastBassInterval = I_DONT_CARE;
				}
				storeBassTone();
			}
		});
		bassIntervalComboViewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				storeBassTone();
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(bassIntervalComboViewer.getCombo());
		GridDataFactory.fillDefaults().span(3, 1).applyTo(toolkit.createLabel(parent, "")); //$NON-NLS-1$
	}

	private void storeBassTone() {
		int valueToStore = -1;
		if (lastBassInterval instanceof Interval) {
			valueToStore = ((Interval) lastBassInterval).getHalfsteps();
		}
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_BASS_TONE, valueToStore);
	}

	/**
	 * Creates the lead tone row.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param gridData
	 *            the grid data object to copy from
	 */
	private void createLeadToneRow(final Composite parent, final GridData gridData) {
		toolkit.createLabel(parent, ViewMessages.ChordGenerationView_lead_tone, SWT.NONE);

		leadIntervalComboViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		leadIntervalComboViewer.getCombo().setVisibleItemCount(7);
		leadIntervalComboViewer.setLabelProvider(new IntervalTableViewerLabelProvider());
		leadIntervalComboViewer.setContentProvider(new IntervalContentProvider());
		leadIntervalComboViewer.setSorter(new IntervalViewerSorter());
		leadIntervalComboViewer.addFilter(new IntervalViewerFilter());
		leadIntervalComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					lastLeadInterval = ((IStructuredSelection) event.getSelection()).getFirstElement();
				}
				if (lastLeadInterval == null) {
					lastLeadInterval = I_DONT_CARE;
				}
				storeLeadTone();
			}
		});
		leadIntervalComboViewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				storeLeadTone();
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(leadIntervalComboViewer.getCombo());
		GridDataFactory.fillDefaults().span(3, 1).applyTo(toolkit.createLabel(parent, "")); //$NON-NLS-1$
	}

	private void storeLeadTone() {
		int valueToStore = -1;
		if (lastLeadInterval instanceof Interval) {
			valueToStore = ((Interval) lastLeadInterval).getHalfsteps();
		}
		prefs.setValue(Preferences.CHORD_GENERATION_VIEW_LEAD_TONE, valueToStore);
	}

	/**
	 * Creates the level row.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param gridData
	 *            the grid data object to copy from
	 */
	private void createLevelRow(final Composite parent, final GridData gridData) {
		toolkit.createLabel(parent, ViewMessages.ChordGenerationView_level, SWT.NONE);

		minLevelCombo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		minLevelCombo.setItems(UIConstants.LEVELS);
		minLevelCombo.setVisibleItemCount(UIConstants.LEVELS.length);
		minLevelCombo.setText(UIConstants.LEVELS[0]);
		minLevelCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int min = minLevelCombo.getSelectionIndex();
				final int max = maxLevelCombo.getSelectionIndex();
				if (max < min) {
					maxLevelCombo.select(min);
				}
			}
		});
		minLevelCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_MIN_LEVEL, minLevelCombo.getSelectionIndex());
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(minLevelCombo);

		toolkit.createLabel(parent, "-", SWT.NONE); //$NON-NLS-1$

		maxLevelCombo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		maxLevelCombo.setItems(UIConstants.LEVELS);
		maxLevelCombo.setVisibleItemCount(UIConstants.LEVELS.length);
		maxLevelCombo.setText(UIConstants.LEVELS[1]);
		maxLevelCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int min = minLevelCombo.getSelectionIndex();
				final int max = maxLevelCombo.getSelectionIndex();
				if (min > max) {
					minLevelCombo.select(max);
				}
			}
		});
		maxLevelCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_MAX_LEVEL, maxLevelCombo.getSelectionIndex());
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(maxLevelCombo);

		toolkit.createLabel(parent, "", SWT.NONE); //$NON-NLS-1$
	}

	/**
	 * Creates the string range row.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param gridData
	 *            the grid data object to copy from
	 */
	private void createStringRangeRow(final Composite parent, final GridData gridData) {
		toolkit.createLabel(parent, ViewMessages.ChordGenerationView_string_range, SWT.NONE);

		minStringComboViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		minStringComboViewer.getCombo().setVisibleItemCount(Constants.MAX_STRING_SIZE - 1);
		minStringComboViewer.setLabelProvider(new StringLabelProvider());
		minStringComboViewer.setContentProvider(new DefaultCollectionContentProvider());
		minStringComboViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				if (currentInstrument == null || !(element instanceof Integer)) {
					return false;
				}
				final int stringNumber = ((Integer) element).intValue();
				return stringNumber < currentInstrument.getStringCount() ? true : false;
			}
		});
		minStringComboViewer.setSorter(new ViewerSorter());
		minStringComboViewer.addSelectionChangedListener(minStringSelectionChangedListener);
		minStringComboViewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				int value = 1;
				final Object first = ((IStructuredSelection) minStringComboViewer.getSelection()).getFirstElement();
				if (first != null && first instanceof Integer) {
					value = ((Integer) first).intValue();
				}
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_MIN_STRING, value);
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(minStringComboViewer.getCombo());

		toolkit.createLabel(parent, "-", SWT.NONE); //$NON-NLS-1$

		maxStringComboViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		maxStringComboViewer.getCombo().setVisibleItemCount(Constants.MAX_STRING_SIZE - 1);
		maxStringComboViewer.setLabelProvider(new StringLabelProvider());
		maxStringComboViewer.setContentProvider(new DefaultCollectionContentProvider());
		maxStringComboViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				if (currentInstrument == null || !(element instanceof Integer)) {
					return false;
				}
				final int stringNumber = ((Integer) element).intValue();
				return stringNumber <= currentInstrument.getStringCount() ? true : false;
			}
		});
		maxStringComboViewer.setSorter(new ViewerSorter());
		maxStringComboViewer.addSelectionChangedListener(maxStringSelectionChangedListener);
		maxStringComboViewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				int value = Constants.MAX_STRING_SIZE;
				final Object first = ((IStructuredSelection) maxStringComboViewer.getSelection()).getFirstElement();
				if (first != null && first instanceof Integer) {
					value = ((Integer) first).intValue();
				}
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_MAX_STRING, value);
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(maxStringComboViewer.getCombo());

		toolkit.createLabel(parent, "", SWT.NONE); //$NON-NLS-1$

		final List<Integer> minStringList = new ArrayList<Integer>();
		final List<Integer> maxStringList = new ArrayList<Integer>();
		for (int i = 1; i < Constants.MAX_STRING_SIZE; i++) {
			if (i < Constants.MAX_STRING_SIZE) {
				minStringList.add(i);
			}
			if (i > 1) {
				maxStringList.add(i);
			}
		}
		minStringComboViewer.setInput(minStringList);
		maxStringComboViewer.setInput(maxStringList);
	}

	/**
	 * Creates the fret range row.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param gridData
	 *            the grid data object to copy from
	 */
	private void createFretRangeRow(final Composite parent, final GridData gridData) {
		toolkit.createLabel(parent, ViewMessages.ChordGenerationView_fret_range, SWT.NONE);

		minFretText = toolkit.createText(parent, "0", SWT.BORDER | SWT.SINGLE); //$NON-NLS-1$
		minFretText.setTextLimit(2);
		minFretText.addKeyListener(keyListener);
		minFretText.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent e) {
				minFretTextGainedFocus = true;
			}

			@Override
			public void focusLost(final FocusEvent e) {
				minFretTextFocusLost();
			}
		});
		minFretText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_MIN_FRET, ViewUtil.getIntValue(minFretText));
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(minFretText);

		toolkit.createLabel(parent, "-", SWT.NONE); //$NON-NLS-1$

		maxFretText = toolkit.createText(parent, "12", SWT.BORDER | SWT.SINGLE); //$NON-NLS-1$
		maxFretText.setTextLimit(2);
		maxFretText.addKeyListener(keyListener);
		maxFretText.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent e) {
				maxFretTextGainedFocus = true;
			}

			@Override
			public void focusLost(final FocusEvent e) {
				maxFretTextFocusLost();
			}
		});
		maxFretText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_MAX_FRET, ViewUtil.getIntValue(maxFretText));
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(maxFretText);

		fretRangeInfoLabel = toolkit.createLabel(parent, "(xx..xx)", SWT.NONE); //$NON-NLS-1$
	}

	private void minFretTextFocusLost() {
		if ("".equals(minFretText.getText().trim())) {
			minFretText.setText(currentInstrument == null ? "1" : "" + (currentInstrument.getMinFret() + 1)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		updateFretRange(true, false);
		updateGripRange(true);
		minFretTextGainedFocus = false;
	}

	private void maxFretTextFocusLost() {
		if ("".equals(maxFretText.getText().trim())) {
			maxFretText.setText(currentInstrument == null ? "12" : "" + currentInstrument.getFretCount()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		updateFretRange(false, true);
		updateGripRange(true);
		maxFretTextGainedFocus = false;
	}

	/**
	 * Creates the grip range row.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param gridData
	 *            the grid data object to copy from
	 */
	private void createGripRangeRow(final Composite parent, final GridData gridData) {
		toolkit.createLabel(parent, ViewMessages.ChordGenerationView_grip_width, SWT.NONE);
		rangeText = toolkit.createText(parent, "" + Constants.DEFAULT_FRET_GRIP_RANGE, SWT.BORDER | SWT.SINGLE); //$NON-NLS-1$
		rangeText.setTextLimit(1);
		rangeText.addKeyListener(keyListener);
		rangeText.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent e) {
				rangeTextGainedFocus = true;
			}

			@Override
			public void focusLost(final FocusEvent e) {
				rangeTextFocusLost();
			}
		});
		rangeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_GRIP_RANGE, rangeText.getText());
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(rangeText);

		toolkit.createLabel(parent, "", SWT.NONE); //$NON-NLS-1$

		unitCombo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		final String[] units = Unit.printableValues();
		final String[] items = new String[units.length + 1];
		items[0] = ViewMessages.ChordGenerationView_frets;
		for (int i = 0; i < units.length; i++) {
			items[i + 1] = units[i];
		}
		unitCombo.setItems(items);
		unitCombo.setVisibleItemCount(Unit.values().length + 1);
		unitCombo.select(0);
		unitCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateGripRangeText(true);
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_GRIP_RANGE_UNIT, unitCombo.getSelectionIndex());
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(unitCombo);

		gripRangeInfoLabel = toolkit.createLabel(parent, "(x..xxx)", SWT.NONE); //$NON-NLS-1$
	}

	private void rangeTextFocusLost() {
		final int defaultFretRange = computeDefaultFretRange();
		if ("".equals(rangeText.getText().trim())) {
			rangeText.setText("" + defaultFretRange); //$NON-NLS-1$
		}
		updateGripRange(false);
		rangeTextGainedFocus = false;
	}

	/**
	 * Creates the max. single tone number row.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param gridData
	 *            the grid data object to copy from
	 */
	private void createMaxSingleToneNumberRow(final Composite parent, final GridData gridData) {
		final Label toneNumbersLabel = toolkit.createLabel(parent,
				ViewMessages.ChordGenerationView_max_single_tone_number, SWT.WRAP | SWT.LEFT);
		GridDataFactory.fillDefaults().hint(70, SWT.DEFAULT).applyTo(toneNumbersLabel);

		maxToneNumberCombo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		maxToneNumberCombo.setItems(new String[] { I_DONT_CARE });
		maxToneNumberCombo.setVisibleItemCount(6);
		maxToneNumberCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateMutedStings();
			}
		});
		maxToneNumberCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_MAX_SINGLE_TONE_NUMBERS,
						maxToneNumberCombo.getSelectionIndex());
			}
		});
		GridDataFactory.createFrom(gridData).applyTo(maxToneNumberCombo);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(toolkit.createLabel(parent, "")); //$NON-NLS-1$
	}

	/**
	 * Creates the check boxes row for the string settings.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createStringCheckBoxesRow(final Composite parent) {

		emptyStringsButton = new Button(parent, SWT.CHECK);
		emptyStringsButton.setText(ViewMessages.ChordGenerationView_empty_strings);
		emptyStringsButton.setEnabled(currentInstrument == null ? true : currentInstrument.hasEmptyStrings());
		emptyStringsButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		emptyStringsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_EMPTY_STRINGS, emptyStringsButton.getSelection());
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_EMPTY_STRINGS_TEMP, emptyStringsButton.getSelection());
			}
		});
		emptyStringsButton.setSelection(currentInstrument == null ? true : currentInstrument.hasEmptyStrings());
		GridDataFactory.fillDefaults().applyTo(emptyStringsButton);

		mutedStringsButton = new Button(parent, SWT.CHECK);
		mutedStringsButton.setText(ViewMessages.ChordGenerationView_muted_strings);
		mutedStringsButton.setSelection(false);
		mutedStringsButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		mutedStringsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateMutedStringsInfoButton();
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_MUTED_STRINGS, mutedStringsButton.getSelection());
			}
		});
		GridDataFactory.fillDefaults().applyTo(mutedStringsButton);
	}

	/**
	 * Creates the check boxes row for the muted strings settings.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createMutedStringsCheckBoxesRow(final Composite parent) {

		onlyPackedButton = new Button(parent, SWT.CHECK | SWT.WRAP);
		onlyPackedButton.setText(ViewMessages.ChordGenerationView_only_packed);
		onlyPackedButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		onlyPackedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_ONLY_PACKED, onlyPackedButton.getSelection());
			}
		});
		GridDataFactory.fillDefaults().applyTo(onlyPackedButton);

		onlySingleMutpedStringsButton = new Button(parent, SWT.CHECK | SWT.WRAP);
		onlySingleMutpedStringsButton.setText(ViewMessages.ChordGenerationView_only_single_muted_strings);
		onlySingleMutpedStringsButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		onlySingleMutpedStringsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_ONLY_SINGLE_MUTED_STRINGS,
						onlySingleMutpedStringsButton.getSelection());
			}
		});
		GridDataFactory.fillDefaults().applyTo(onlySingleMutpedStringsButton);
	}

	/**
	 * Creates the check boxes row for the advanced tone settings.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createAdvancedCheckBoxesRow(final Composite parent) {

		doubledTonesButton = new Button(parent, SWT.CHECK | SWT.WRAP);
		doubledTonesButton.setText(ViewMessages.ChordGenerationView_doubled_tones);
		doubledTonesButton.setSelection(true);
		doubledTonesButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		doubledTonesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_DOUBLED_TONES, doubledTonesButton.getSelection());
			}
		});
		GridDataFactory.fillDefaults().applyTo(doubledTonesButton);

		ascendingDescendingButton = new Button(parent, SWT.CHECK | SWT.WRAP);
		ascendingDescendingButton.setText(ViewMessages.ChordGenerationView_only_ascending_descending_tones);
		ascendingDescendingButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		ascendingDescendingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_ONLY_ASCENDING,
						ascendingDescendingButton.getSelection());
			}
		});
		GridDataFactory.fillDefaults().applyTo(ascendingDescendingButton);
	}

	/**
	 * Creates the check boxes row for the without interval settings.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createExcludedIntervalsRow(final Composite parent) {

		final ExpandableComposite exComposite = toolkit.createExpandableComposite(parent, ExpandableComposite.TREE_NODE
				| ExpandableComposite.CLIENT_INDENT);
		exComposite.setText(ViewMessages.ChordGenerationView_excluded_intervals);
		GridDataFactory.fillDefaults().span(2, 1).indent(1, 10).grab(true, false).applyTo(exComposite);

		final Composite withoutGroup = toolkit.createComposite(exComposite);
		exComposite.setClient(withoutGroup);
		exComposite.setExpanded(prefs
				.getBoolean(Preferences.CHORD_GENERATION_VIEW_IS_EXCLUDED_INTERVALS_COMPOSITE_EXPANDED));
		exComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(final ExpansionEvent e) {
				form.reflow(true);
				prefs.setValue(Preferences.CHORD_GENERATION_VIEW_IS_EXCLUDED_INTERVALS_COMPOSITE_EXPANDED, e.getState());
			}
		});
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(withoutGroup);

		without1Button = toolkit.createButton(withoutGroup, ViewMessages.ChordGenerationView_without_1st, SWT.CHECK);
		without1Button.addSelectionListener(new WithoutIntervalsSelectionListener(new int[] { 1 },
				Preferences.CHORD_GENERATION_VIEW_WITHOUT_1ST));
		GridDataFactory.fillDefaults().indent(0, 5).applyTo(without1Button);

		without3Button = toolkit.createButton(withoutGroup, ViewMessages.ChordGenerationView_without_3rd, SWT.CHECK);
		without3Button.addSelectionListener(new WithoutIntervalsSelectionListener(new int[] { 3, 4 },
				Preferences.CHORD_GENERATION_VIEW_WITHOUT_3RD));
		GridDataFactory.fillDefaults().indent(0, 5).applyTo(without3Button);

		without5Button = toolkit.createButton(withoutGroup, ViewMessages.ChordGenerationView_without_5th, SWT.CHECK);
		without5Button.addSelectionListener(new WithoutIntervalsSelectionListener(new int[] { 7 },
				Preferences.CHORD_GENERATION_VIEW_WITHOUT_5TH));
		GridDataFactory.fillDefaults().indent(0, 5).applyTo(without5Button);
	}

	/**
	 * Adds the drop support for chords from the chords view.
	 */
	private void addDropSupport() {

		// define transfer and operations
		final int operations = DND.DROP_MOVE | DND.DROP_COPY;
		final DropTarget target = new DropTarget(form, operations);
		target.setTransfer(new Transfer[] { ModelObjectTransfer.getInstance(ModelObjectTransfer.TYPE_CHORD),
				GriptableTransfer.getInstance() /* , TextTransfer.getInstance() */});

		// drop adapter
		target.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(final DropTargetEvent event) {
				if (!(event.data instanceof List)) {
					return;
				}
				final List<?> toDrop = (List<?>) event.data;
				if (toDrop == null || toDrop.isEmpty()) {
					return;
				}
				final Chord chord = getFirstElement(toDrop);
				if (chord != null) {
					final Chord theRealChord = (Chord) ChordList.getInstance().getElement(chord.getName());
					chordComboViewer.setSelection(new StructuredSelection(theRealChord));
					rootNoteComboViewer.setSelection(new StructuredSelection(chord.getRootNote()));
				}
			}

			private Chord getFirstElement(final List<?> list) {
				for (final Object element : list) {
					if (element instanceof Chord && isValid((Chord) element)) {
						return (Chord) element;
					}
				}
				return null;
			}

			private boolean isValid(final Chord chord) {
				String name = chord.getName();
				if (name.equals(Constants.BLANK_CHORD_NAME)) {
					name = ""; //$NON-NLS-1$
				}
				return chordComboViewer.getCombo().indexOf(name) != -1;
			}
		});

		/*
		 * target.addDropListener(new DropTargetAdapter() { public void
		 * drop(DropTargetEvent event) { if (!(event.data instanceof String))
		 * return; // implement Bug 127 } });
		 */
	}

	private int passFilter(final Chord chord) {
		final Collection<Interval> intervals = chord.getIntervals();
		int numberOfIntervals = intervals.size();
		final int stringCount = getStringRange();
		if (without1Button != null && !without1Button.isDisposed() && without1Button.getSelection()
				&& intervals.contains(Factory.getInstance().getInterval(0))) {
			numberOfIntervals--;
		}
		if (without3Button != null
				&& !without3Button.isDisposed()
				&& without3Button.getSelection()
				&& (intervals.contains(Factory.getInstance().getInterval(3)) || intervals.contains(Factory
						.getInstance().getInterval(4)))) {
			numberOfIntervals--;
		}
		if (without5Button != null && !without5Button.isDisposed() && without5Button.getSelection()
				&& intervals.contains(Factory.getInstance().getInterval(7))) {
			numberOfIntervals--;
		}
		if (numberOfIntervals < 2) {
			return TOO_LESS_INTERVALS;
		}
		if (numberOfIntervals > stringCount) {
			return TOO_MANY_INTERVALS;
		}
		return PASSES_FILTER;
	}

	/**
	 * Contribute to menu and toolbar.
	 */
	private void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();
		openPreferencesAction = new OpenSpecificPreferencePageAction(ChordGenerationViewPreferencePage.ID);
		Activator.getDefault().registerAction(getSite(), openPreferencesAction);
		bars.getMenuManager().add(openPreferencesAction);
	}

	@Override
	public void setFocus() {
		composite.setFocus();
		updateCalculateButton();
		StatusLineUtil.handleTestVersionWarning(getViewSite(), NlsUtil.getStatusLineWarningForChords());
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {

		final int oldStringCount = currentInstrument != null ? currentInstrument.getStringCount() : 0;
		boolean instrumentChanged = false;

		// (1) instrument changed
		if (property == InstrumentList.PROP_CURRENT_INSTRUMENT_CHANGED) {
			currentInstrument = (Instrument) source;
			instrumentChanged = true;
		}
		if (property == InstrumentList.PROP_CHANGED_ELEMENT && source == currentInstrument) {
			instrumentChanged = true;
		}
		if (instrumentChanged) {
			adjustStringRange(oldStringCount);
			updateWidgets();
		}

		// (2) chords changed
		if (source instanceof Chord && property == ChordList.PROP_ADDED || property == ChordList.PROP_REMOVED
				|| property == ChordList.PROP_CHANGED_ELEMENT) {
			updateChords();
			updateBrowseButton();
			updateBassLeadCombos();
			updateToneNumbers();
			updateCalculateButton();
		}
	}

	private void adjustStringRange(final int oldStringCount) {

		int minValue = getCurrentMinStringValue();
		int maxValue = getCurrentMaxStringValue();
		final int newStringCount = currentInstrument != null ? currentInstrument.getStringCount() : 0;

		if (newStringCount != oldStringCount) {
			if (newStringCount == 0) {
				minValue = 0;
				maxValue = 0;
			}
			if (oldStringCount == 0) {
				minValue = 1;
				maxValue = newStringCount;
			} else if (newStringCount < oldStringCount) {
				if (maxValue > newStringCount) {
					maxValue = newStringCount;
				}
				if (minValue != 1) {
					minValue = maxValue - (oldStringCount - newStringCount) + 1;
				}
				if (minValue < 1) {
					minValue = 1;
				}
			} else if (newStringCount > oldStringCount) {
				if (maxValue == oldStringCount) {
					if (minValue != 1) {
						minValue = minValue + newStringCount - oldStringCount;
					}
					maxValue = newStringCount;
				}
			}
		}

		// refresh combo viewers in order to update the filter
		minStringComboViewer.refresh(true);
		maxStringComboViewer.refresh(true);

		// set the new values
		minStringComboViewer.setSelection(new StructuredSelection(Integer.valueOf(minValue)));
		maxStringComboViewer.setSelection(new StructuredSelection(Integer.valueOf(maxValue)));
	}

	@Override
	public void dispose() {
		InstrumentList.getInstance().removeChangeListener(this);
		ChordList.getInstance().removeChangeListener(this);
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	/* --- update methods --- */

	/**
	 * Updates this view and all its widgets corresponding to a change of the
	 * current instrument or the chords list.
	 * 
	 * <p>
	 * Note:
	 * </p>
	 * <li>(grip) fret range depends on fret number and capodasto</li> <li>empty
	 * strings check box can be disable when a full capodasto is defined</li>
	 * <li>number of valid chords depends on string number</li> <li>chords list
	 * may be changed (chords changed, removed, added)</li>
	 */
	private void updateWidgets() {
		updateStringRange(true, true);
		updateChords();
		updateFretRangeInfoLabel();
		updateFretRange(true, true);
		updateGripRange(true);
		updateToneNumbers();
		updateBrowseButton();
		updateEmptyStrings();
		updateMutedStringsInfoButton();
		updateCalculateButton();
	}

	private void updateChords() {

		final Chord chord1 = (Chord) ((IStructuredSelection) chordComboViewer.getSelection()).getFirstElement();
		chordComboViewer.refresh(true);
		final Chord chord2 = (Chord) ((IStructuredSelection) chordComboViewer.getSelection()).getFirstElement();

		if ((chord2 == null || chord1 != chord2) && chordComboViewer.getCombo().getItems().length > 0) {
			chordComboViewer.setSelection(new StructuredSelection(chordComboViewer.getElementAt(0)));
		}

		if (chord1 == null && chord2 != null || chord1 != null && chord2 == null) {
			updateCalculateButton();
		}
	}

	private void updateBassLeadCombos() {

		if (bassIntervalComboViewer != null && !bassIntervalComboViewer.getCombo().isDisposed()
				&& leadIntervalComboViewer != null && !leadIntervalComboViewer.getCombo().isDisposed()) {

			final Object first = ((IStructuredSelection) chordComboViewer.getSelection()).getFirstElement();
			if (first instanceof Chord) {
				final Chord chord = (Chord) first;
				// chord.setRootNote((Note) ((IStructuredSelection)
				// rootNoteComboViewer.getSelection()).getFirstElement());
				// update bass interval
				bassIntervalComboViewer.setLabelProvider(new IntervalTableViewerLabelProvider(chord, false));
				bassIntervalComboViewer.setSorter(new IntervalViewerSorter(chord));
				bassIntervalComboViewer.setInput(chord);
				if (!chord.getIntervals().contains(lastBassInterval)) {
					lastBassInterval = I_DONT_CARE;
				}
				bassIntervalComboViewer.setSelection(new StructuredSelection(lastBassInterval));
				// update lead interval
				leadIntervalComboViewer.setLabelProvider(new IntervalTableViewerLabelProvider(chord, false));
				leadIntervalComboViewer.setSorter(new IntervalViewerSorter(chord));
				leadIntervalComboViewer.setInput(chord);
				if (!chord.getIntervals().contains(lastLeadInterval)) {
					lastLeadInterval = I_DONT_CARE;
				}
				leadIntervalComboViewer.setSelection(new StructuredSelection(lastLeadInterval));
			}
		}
	}

	private void updateFretRangeInfoLabel() {
		if (currentInstrument == null) {
			return;
		}
		final String result = "(" + (currentInstrument.getMinFret() + 1) + //$NON-NLS-1$
				".." + currentInstrument.getFretCount() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		fretRangeInfoLabel.setText(result);
	}

	private void updateStringRange(final boolean updateMin, final boolean updateMax) {
		if (currentInstrument == null) {
			return;
		}

		final int currentStringCount = currentInstrument.getStringCount();
		int minValue = getCurrentMinStringValue();
		int maxValue = getCurrentMaxStringValue();
		if (minValue == 0 || maxValue == 0) {
			return;
		}

		if (updateMin && minValue < 1) {
			minValue = 1;
		}
		if (updateMin && minValue > currentStringCount - 1) {
			minValue = currentStringCount - 1;
		}
		if (updateMax && maxValue < 2) {
			maxValue = 2;
		}
		if (updateMax && maxValue > currentStringCount) {
			maxValue = currentStringCount;
		}
		if (updateMin && minValue >= maxValue) {
			minValue = maxValue - 1;
		}
		if (updateMax && maxValue <= minValue) {
			maxValue = minValue + 1;
		}

		minStringComboViewer.removeSelectionChangedListener(minStringSelectionChangedListener);
		maxStringComboViewer.removeSelectionChangedListener(maxStringSelectionChangedListener);
		minStringComboViewer.setSelection(new StructuredSelection(Integer.valueOf(minValue)));
		maxStringComboViewer.setSelection(new StructuredSelection(Integer.valueOf(maxValue)));
		minStringComboViewer.addSelectionChangedListener(minStringSelectionChangedListener);
		maxStringComboViewer.addSelectionChangedListener(maxStringSelectionChangedListener);
	}

	private void updateFretRange(final boolean updateMin, final boolean updateMax) {
		if (currentInstrument == null) {
			return;
		}
		int currentMinValue = ViewUtil.getIntValue(minFretText);
		int currentMaxValue = ViewUtil.getIntValue(maxFretText);
		if (updateMin && currentMinValue < currentInstrument.getMinFret() + 1) {
			currentMinValue = currentInstrument.getMinFret() + 1;
		}
		if (updateMin && currentMinValue > currentInstrument.getFretCount()) {
			currentMinValue = currentInstrument.getFretCount();
		}
		if (updateMax && currentMaxValue < currentInstrument.getMinFret() + 1) {
			currentMaxValue = currentInstrument.getMinFret() + 1;
		}
		if (updateMax && currentMaxValue > currentInstrument.getFretCount()) {
			currentMaxValue = currentInstrument.getFretCount();
		}
		if (updateMin && currentMaxValue < currentMinValue) {
			currentMinValue = currentMaxValue;
		}
		if (updateMax && currentMinValue > currentMaxValue) {
			currentMaxValue = currentMinValue;
		}
		minFretText.setText("" + currentMinValue); //$NON-NLS-1$
		maxFretText.setText("" + currentMaxValue); //$NON-NLS-1$
	}

	private void updateGripRangeText(final boolean updateText) {
		// frets selected
		if (isFretGripRangeSelected()) {
			rangeText.setTextLimit(1);
			rangeText.removeKeyListener(keyListener2);
			rangeText.addKeyListener(keyListener);
			if (updateText) {
				rangeText.setText("" + Constants.DEFAULT_FRET_GRIP_RANGE); //$NON-NLS-1$
			}
		}
		// mm selected
		else if (Unit.valueOf(unitCombo.getText()).equals(Unit.mm)) {
			rangeText.setTextLimit(3);
			rangeText.removeKeyListener(keyListener2);
			rangeText.addKeyListener(keyListener);
			if (updateText) {
				rangeText.setText("" + UIConstants.DEFAULT_GRIP_RANGE_IN_MM); //$NON-NLS-1$
			}
		}
		// inch selected
		else if (Unit.valueOf(unitCombo.getText()).equals(Unit.inch)) {
			rangeText.setTextLimit(7);
			rangeText.removeKeyListener(keyListener);
			rangeText.addKeyListener(keyListener2);
			if (updateText) {
				rangeText.setText("" + UIConstants.DEFAULT_GRIP_RANGE_IN_INCH); //$NON-NLS-1$
			}
		}
		updateGripRange(true);
	}

	private void updateGripRange(final boolean updateInfoLabel) {
		// frets selected
		if (isFretGripRangeSelected()) {
			final int minFretRange = computeMinFretGripRange();
			final int maxFretRange = computeMaxFretGripRange();
			final int currentValue = ViewUtil.getIntValue(rangeText);
			if (currentValue < minFretRange) {
				rangeText.setText("" + minFretRange); //$NON-NLS-1$
			}
			if (currentValue > maxFretRange) {
				rangeText.setText("" + maxFretRange); //$NON-NLS-1$
			}
			if (updateInfoLabel) {
				gripRangeInfoLabel.setText("(" + minFretRange + ".." + maxFretRange + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		// mm selected
		else if (Unit.valueOf(unitCombo.getText()).equals(Unit.mm)) {
			final int currentValue = ViewUtil.getIntValue(rangeText);
			if (currentValue < UIConstants.MIN_GRIP_RANGE_IN_MM) {
				rangeText.setText("" + UIConstants.MIN_GRIP_RANGE_IN_MM); //$NON-NLS-1$
			}
			if (currentValue > UIConstants.MAX_GRIP_RANGE_IN_MM) {
				rangeText.setText("" + UIConstants.MAX_GRIP_RANGE_IN_MM); //$NON-NLS-1$
			}
			if (updateInfoLabel) {
				gripRangeInfoLabel
						.setText("(" + UIConstants.MIN_GRIP_RANGE_IN_MM + ".." + UIConstants.MAX_GRIP_RANGE_IN_MM + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		// inch selected
		else if (Unit.valueOf(unitCombo.getText()).equals(Unit.inch)) {
			final double currentValue = ViewUtil.getDoubleValue(rangeText);
			if (currentValue < UIConstants.MIN_GRIP_RANGE_IN_INCH) {
				rangeText.setText("" + UIConstants.MIN_GRIP_RANGE_IN_INCH); //$NON-NLS-1$
			}
			if (currentValue > UIConstants.MAX_GRIP_RANGE_IN_INCH) {
				rangeText.setText("" + UIConstants.MAX_GRIP_RANGE_IN_INCH); //$NON-NLS-1$
			}
			if (updateInfoLabel) {
				gripRangeInfoLabel
						.setText("(" + (int) UIConstants.MIN_GRIP_RANGE_IN_INCH + ".." + (int) UIConstants.MAX_GRIP_RANGE_IN_INCH + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}

	private int computeMinFretGripRange() {
		final int maxValue = ViewUtil.getIntValue(maxFretText);
		final int minValue = ViewUtil.getIntValue(minFretText);
		return Math.min(maxValue - minValue + 1, Constants.MIN_FRET_GRIP_RANGE);
	}

	private int computeDefaultFretRange() {
		final int maxValue = ViewUtil.getIntValue(maxFretText);
		final int minValue = ViewUtil.getIntValue(minFretText);
		return Math.min(maxValue - minValue + 1, Constants.DEFAULT_FRET_GRIP_RANGE);
	}

	private int computeMaxFretGripRange() {
		final int maxValue = ViewUtil.getIntValue(maxFretText);
		final int minValue = ViewUtil.getIntValue(minFretText);
		return Math.min(maxValue - minValue + 1, Constants.MAX_FRET_GRIP_RANGE);
	}

	private void updateToneNumbers() {
		if (currentInstrument == null) {
			return;
		}

		final int lastSelection = maxToneNumberCombo.getSelectionIndex();

		final List<String> items = new ArrayList<String>();
		items.add(I_DONT_CARE);
		final int stringCount = getStringRange();
		final int intervalsCount = bassIntervalComboViewer.getCombo().getItemCount() - 1;
		final int maxNumber = stringCount - intervalsCount;
		for (int i = 1; i <= maxNumber; i++) {
			items.add("" + i); //$NON-NLS-1$
		}

		final String[] theItems = new String[items.size()];

		// disable combo box if there's no choice
		if (theItems.length == 1) {
			maxToneNumberCombo.setEnabled(false);
			maxToneNumberCombo.setItems(new String[] {});
		}
		// enable combo box and add updated items
		else {
			maxToneNumberCombo.setEnabled(true);
			maxToneNumberCombo.setItems(items.toArray(theItems));
			final int newIndex = lastSelection == -1 || lastSelection > maxNumber ? 0 : lastSelection;
			maxToneNumberCombo.select(newIndex);
		}

		// update muted strings
		updateMutedStings();
	}

	private void updateMutedStings() {
		if (currentInstrument == null) {
			return;
		}

		final int stringCount = getStringRange();
		final int intervalsCount = bassIntervalComboViewer.getCombo().getItemCount() - 1;
		final int index = maxToneNumberCombo.getSelectionIndex();

		if (intervalsCount == stringCount) {
			mutedStringsButton.setSelection(false);
			mutedStringsButton.setEnabled(false);
		} else if (index > 0 && index * intervalsCount < stringCount) {
			mutedStringsButton.setSelection(true);
			mutedStringsButton.setEnabled(false);
		} else {
			mutedStringsButton.setEnabled(true);
		}
		updateMutedStringsInfoButton();
	}

	private void updateEmptyStrings() {
		if (currentInstrument == null) {
			return;
		}
		emptyStringsButton.setEnabled(currentInstrument.hasEmptyStrings());
		if (!currentInstrument.hasEmptyStrings()) {
			emptyStringsButton.setSelection(false);
		}
	}

	private void updateBrowseButton() {
		chordBrowseButton.setEnabled(chordComboViewer.getCombo().getItemCount() > 0);
	}

	private void updateMutedStringsInfoButton() {
		// update - only packed
		onlyPackedButton.setEnabled(mutedStringsButton.getSelection());
		if (!onlyPackedButton.isEnabled()) {
			onlyPackedButton.setSelection(false);
		}

		// update - only single muted strings
		onlySingleMutpedStringsButton
				.setEnabled(mutedStringsButton.getSelection()
						&& !(currentInstrument != null && (getCurrentMinStringValue() > 2 || getCurrentMaxStringValue() < currentInstrument
								.getStringCount() - 1)));
		if (!onlySingleMutpedStringsButton.isEnabled()) {
			onlySingleMutpedStringsButton.setSelection(false);
		}
	}

	private void updateCalculateButton() {
		calculateButton.setEnabled(currentInstrument != null && chordComboViewer.getCombo().getItemCount() > 0);

		String errorMessage = null;
		if (currentInstrument == null && chordComboViewer.getCombo().getItemCount() > 0) {
			errorMessage = ViewMessages.ChordGenerationView_error_msg_1;
		}
		if (currentInstrument != null && chordComboViewer.getCombo().getItemCount() == 0) {
			errorMessage = ViewMessages.ChordGenerationView_error_msg_2;
		}
		if (currentInstrument == null && chordComboViewer.getCombo().getItemCount() == 0) {
			errorMessage = ViewMessages.ChordGenerationView_error_msg_3;
		}

		site.getActionBars().getStatusLineManager().setErrorMessage(errorMessage);
	}

	/**
	 * This method returns the number of strings within the defined string
	 * range.
	 * 
	 * @return the number of strings within the defined string range
	 */
	private int getStringRange() {
		final int minValue = getCurrentMinStringValue();
		final int maxValue = getCurrentMaxStringValue();
		return maxValue - minValue + 1;
	}

	private int getCurrentMinStringValue() {
		return getCurrentStringValueHelper(minStringComboViewer);
	}

	private int getCurrentMaxStringValue() {
		return getCurrentStringValueHelper(maxStringComboViewer);
	}

	private int getCurrentStringValueHelper(final ComboViewer viewer) {
		if (viewer == null || viewer.getCombo().isDisposed()) {
			return 0;
		}
		final ISelection selection = viewer.getSelection();
		if (selection == null || !(selection instanceof IStructuredSelection) || selection.isEmpty()) {
			return 0;
		}
		return ((Integer) ((IStructuredSelection) selection).getFirstElement()).intValue();
	}

	/* --- preference change listener --- */

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		// that's an ugly little hack but so we suppress to rebuild the view for
		// each preference change
		final String property = event.getProperty();

		if (property.equals(Preferences.CHORD_GENERATION_VIEW_IS_ADVANCED_SECTION_CHANGED)) {
			populateFormBody(false);

		} else if (property.equals(Preferences.GENERAL_H_NOTE_NAME) || property.equals(Preferences.GENERAL_B_NOTE_NAME)
				|| property.equals(Preferences.NOTES_MODE)) {
			rootNoteComboViewer.refresh(true);

		} else if (property.equals(Preferences.INTERVAL_NAMES_MODE)
				|| property.equals(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME)
				|| property.equals(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME)
				|| property.equals(Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS)) {
			bassIntervalComboViewer.refresh(true);
			leadIntervalComboViewer.refresh(true);

		} else if (event.getProperty().equals(Preferences.FRET_NUMBER)
				|| event.getProperty().equals(Preferences.CAPO_FRET)) {

			if (event.getOldValue() != null && event.getNewValue() != null && event.getOldValue() instanceof Integer
					&& event.getNewValue() instanceof Integer) {

				final int oldValue = (Integer) event.getOldValue();
				final int newValue = (Integer) event.getNewValue();

				if (event.getProperty().equals(Preferences.FRET_NUMBER)) {

					// update max fret
					final int maxFret = ViewUtil.getIntValue(maxFretText);
					if (maxFret == oldValue || maxFret > newValue) {
						maxFretText.setText(newValue + ""); //$NON-NLS-1$
					}

				} else if (event.getProperty().equals(Preferences.CAPO_FRET)) {

					// update min fret
					final int minFret = ViewUtil.getIntValue(minFretText);
					if (minFret == oldValue + 1 || minFret < newValue + 1) {
						minFretText.setText(newValue + 1 + ""); //$NON-NLS-1$
					}

					// update empty strings
					emptyStringsButton.setEnabled(Instrument.getCapoFret() == 0);
					emptyStringsButton.setSelection(Instrument.getCapoFret() == 0
							&& prefs.getBoolean(Preferences.CHORD_GENERATION_VIEW_EMPTY_STRINGS_TEMP));
				}
			}

			updateFretRangeInfoLabel();
			updateGripRange(true);
		}
	}

	private void updateCheckboxes() {

		if (emptyStringsButton == null || emptyStringsButton.isDisposed() || mutedStringsButton == null
				|| mutedStringsButton.isDisposed() || onlyPackedButton == null || onlyPackedButton.isDisposed()
				|| doubledTonesButton == null || doubledTonesButton.isDisposed()
				|| onlySingleMutpedStringsButton == null || onlySingleMutpedStringsButton.isDisposed()
				|| ascendingDescendingButton == null || ascendingDescendingButton.isDisposed()) {
			return;
		}

		if (minFirstColumnWidth == 0) {
			minFirstColumnWidth = LayoutUtil
					.determineMinButtonWidth(Arrays.asList(new Button[] { emptyStringsButton }));
		}

		if (maxFirstColumnWidth == 0) {
			maxFirstColumnWidth = LayoutUtil.determineMaxButtonWidth(Arrays.asList(new Button[] { emptyStringsButton,
					onlyPackedButton, doubledTonesButton }));
		}

		if (minSecondColumnWidth == 0) {
			minSecondColumnWidth = LayoutUtil.determineMinButtonWidth(Arrays
					.asList(new Button[] { mutedStringsButton }));
		}

		if (maxSecondColumnWidth == 0) {
			maxSecondColumnWidth = LayoutUtil.determineMaxButtonWidth(Arrays.asList(new Button[] { mutedStringsButton,
					onlySingleMutpedStringsButton, ascendingDescendingButton }));
		}

		final int formWidth = form.getSize().x - 65;
		final int minColumnWidth = minFirstColumnWidth + minSecondColumnWidth + HORIZONAL_SPACING;
		int maxColumnsWidth = maxFirstColumnWidth + maxSecondColumnWidth + HORIZONAL_SPACING;
		if (maxColumnsWidth == 0) {
			maxColumnsWidth = 1;
		}

		// first column
		final int newFirstColumnWidth = formWidth < maxColumnsWidth ? (int) (Math.max(formWidth, minColumnWidth)
				* maxFirstColumnWidth / maxColumnsWidth) : maxFirstColumnWidth;
		((GridData) emptyStringsButton.getLayoutData()).widthHint = newFirstColumnWidth;
		((GridData) onlyPackedButton.getLayoutData()).widthHint = newFirstColumnWidth;
		((GridData) doubledTonesButton.getLayoutData()).widthHint = newFirstColumnWidth;

		// second column
		final int newSecondColumnWidth = formWidth < maxColumnsWidth ? (int) (Math.max(formWidth, minColumnWidth)
				* maxSecondColumnWidth / maxColumnsWidth) : maxSecondColumnWidth;
		((GridData) mutedStringsButton.getLayoutData()).widthHint = newSecondColumnWidth;
		((GridData) onlySingleMutpedStringsButton.getLayoutData()).widthHint = newSecondColumnWidth;
		((GridData) ascendingDescendingButton.getLayoutData()).widthHint = newSecondColumnWidth;

		onlyPackedButton.getParent().layout(true, true);
		doubledTonesButton.getParent().layout(true, true);

		form.reflow(true);
	}

	/* --- calculation --- */

	private void startCalculation() {

		// 1) define descriptor
		final CalculationDescriptor descriptor = new CalculationDescriptor();

		// 1.1) chord
		final IStructuredSelection selection = (IStructuredSelection) rootNoteComboViewer.getSelection();
		final Note rootNote = (Note) selection.getFirstElement();
		final Chord chord = (Chord) ((IStructuredSelection) chordComboViewer.getSelection()).getFirstElement();
		chord.setRootNote(rootNote);
		descriptor.setChord(chord);

		// 1.2) bass tone
		final Object bassInterval = ((IStructuredSelection) bassIntervalComboViewer.getSelection()).getFirstElement();
		Note bassTone = null;
		if (bassInterval != I_DONT_CARE && bassInterval instanceof Interval) {
			bassTone = rootNote.calcNote((Interval) bassInterval);
		}
		descriptor.setBassTone(bassTone);

		// 1.3) lead tone
		final Object leadInterval = ((IStructuredSelection) leadIntervalComboViewer.getSelection()).getFirstElement();
		Note leadTone = null;
		if (leadInterval != I_DONT_CARE && leadInterval instanceof Interval) {
			leadTone = rootNote.calcNote((Interval) leadInterval);
		}
		descriptor.setLeadTone(leadTone);

		// 1.4) level
		descriptor.setMinLevel(minLevelCombo.getSelectionIndex());
		descriptor.setMaxLevel(maxLevelCombo.getSelectionIndex());

		// 1.5) string range
		descriptor.setMinString(getCurrentMinStringValue());
		descriptor.setMaxString(getCurrentMaxStringValue());

		// 1.5) (grip) fret range
		descriptor.setMinFret(ViewUtil.getIntValue(minFretText));
		descriptor.setMaxFret(ViewUtil.getIntValue(maxFretText));
		final Unit distanceUnit;
		if (isFretGripRangeSelected()) {
			descriptor.setFretGripRange(ViewUtil.getIntValue(rangeText));
			distanceUnit = currentInstrument.getScaleLengthUnit();
		} else {
			distanceUnit = Unit.valueOf(unitCombo.getText());
			descriptor.setGripRange(ViewUtil.getDoubleValue(rangeText));
			descriptor.setGripRangeUnit(distanceUnit);
		}

		// 1.6) tone numbers
		final int selectedIndex = maxToneNumberCombo.getSelectionIndex();
		final Integer value = selectedIndex > 0 ? Integer.valueOf(selectedIndex) : null;
		descriptor.setToneNumber(value);

		// 1.7) check boxes
		descriptor.setEmptyStrings(emptyStringsButton.getSelection());
		descriptor.setMutedStrings(mutedStringsButton.getSelection());
		descriptor.setOnlyPacked(onlyPackedButton.getSelection());
		descriptor.setOnlySingleMutedStrings(onlySingleMutpedStringsButton.getSelection());
		descriptor.setDoubledTones(doubledTonesButton.getSelection());
		descriptor.setAscendingDescending(ascendingDescendingButton.getSelection());
		descriptor.setWithout1st(without1Button.getSelection());
		descriptor.setWithout3rd(without3Button.getSelection());
		descriptor.setWithout5th(without5Button.getSelection());

		// 2) start calculation
		final IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor) throws InterruptedException {

				monitor.beginTask(ViewMessages.ChordGenerationView_calculate_chords, 1000);

				try {
					// start calculation
					final ICalculator calculator = CalculatorUtil.getCalculator();
					final Set<Griptable> computedGriptables = calculator.calculateCorrespondingGriptablesOfChord(
							descriptor, monitor, 1000);

					// open results view and pass input
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								List<Griptable> resultList = new ArrayList<Griptable>(computedGriptables);

								// check max result number and show prompt if
								// necessary
								final int maxNumbersOfResults = prefs.getInt(Preferences.CALCULATOR_MAX_RESULTS_NUMBER);
								if (resultList.size() > maxNumbersOfResults) {
									resultList = resultList.subList(0, maxNumbersOfResults);

									final boolean hidePrompt = prefs
											.getBoolean(Preferences.WARNINGS_HIDE_PROMPT_TRUNCATE_RESULTS_VIEW);
									if (!hidePrompt) {
										final MessageDialogWithToggle dialog = MessageDialogWithToggle.openInformation(
												getSite().getShell(),
												ViewMessages.ChordGenerationView_information_title,
												ViewMessages.ChordGenerationView_information_msg_1
														+ ViewMessages.ChordGenerationView_information_msg_2,
												ViewMessages.ChordGenerationView_information_prompt, hidePrompt, null,
												null);
										if (dialog.getReturnCode() == Dialog.OK) {
											prefs.setValue(Preferences.WARNINGS_HIDE_PROMPT_TRUNCATE_RESULTS_VIEW,
													dialog.getToggleState());
										}
									}
								}

								// set input
								final IWorkbenchWindow window = ChordGenerationView.this.getSite().getWorkbenchWindow();
								WorkbenchUtil.showPerspective(window.getWorkbench(),
										Preferences.PERSPECTIVES_BINDING_CHORD_GENERATION);
								final ChordResultsView view = (ChordResultsView) window.getActivePage().showView(
										ChordResultsView.ID);
								view.setInput(resultList);
								view.setDistanceUnit(distanceUnit);

							} catch (final PartInitException e) {
							} catch (final WorkbenchException e) {
							}
						}
					});

				} catch (final InterruptedException e) {
				} catch (final Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							monitor.done();
							MessageDialog.openError(calculateButton.getShell(),
									ViewMessages.ChordGenerationView_error_title,
									ViewMessages.ChordGenerationView_error_2_msg_1
											+ ViewMessages.ChordGenerationView_error_2_msg_2);
						}
					});
					throw new InterruptedException();
				} finally {
					monitor.done();
				}
			}
		};

		try {
			new ProgressMonitorDialog(calculateButton.getShell()).run(true, true, op);
		} catch (final InvocationTargetException e1) {
		} catch (final InterruptedException e1) {
		}
	}

	private boolean isFretGripRangeSelected() {
		return unitCombo.getText().equals(ViewMessages.ChordGenerationView_frets);
	}

	private class StringLabelProvider extends LabelProvider {

		@Override
		public String getText(final Object element) {
			if (element instanceof Integer) {
				if (currentInstrument == null) {
					return ""; //$NON-NLS-1$
				}
				int stringNumber = ((Integer) element).intValue();
				if (stringNumber < 1) {
					stringNumber = 1;
				} else if (stringNumber > currentInstrument.getStringCount()) {
					stringNumber = currentInstrument.getStringCount();
				}
				return stringNumber + " - " + currentInstrument.getNoteOfEmptyString(stringNumber).getAbsoluteName(); //$NON-NLS-1$
			}
			return super.getText(element);
		}
	}
}
