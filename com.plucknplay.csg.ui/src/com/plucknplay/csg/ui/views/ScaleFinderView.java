/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.actions.OpenSpecificPreferencePageAction;
import com.plucknplay.csg.ui.actions.scales.AbstractFindScalesAction;
import com.plucknplay.csg.ui.actions.scales.FindScalesAction;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.dialogs.IntervalContainerSelectionDialog;
import com.plucknplay.csg.ui.preferencePages.ScaleFinderViewPreferencePage;
import com.plucknplay.csg.ui.util.CategoryListContentProvider;
import com.plucknplay.csg.ui.util.ChordLabelProvider;
import com.plucknplay.csg.ui.util.NotesLabelProvider;
import com.plucknplay.csg.ui.util.StatusLineUtil;
import com.plucknplay.csg.ui.util.WidgetFactory;
import com.plucknplay.csg.ui.views.dnd.GriptableTransfer;
import com.plucknplay.csg.ui.views.dnd.ModelObjectTransfer;

public class ScaleFinderView extends ViewPart implements IChangeListener, IPropertyChangeListener {

	public static final String ID = "com.plucknplay.csg.ui.views.ScaleFinderView"; //$NON-NLS-1$
	public static final String HELP_ID = "scale_finder_view_context"; //$NON-NLS-1$

	private static final String ALIGNMENT_GROUP = "alignment"; //$NON-NLS-1$
	private static final String BOTTOM_GROUP = "bottom"; //$NON-NLS-1$

	private IViewSite site;
	private Instrument currentInstrument;
	private IPreferenceStore prefs;

	private Display display;
	private Composite composite;
	private FormToolkit toolkit;
	private ScrolledForm form;
	private LabelProvider notesLabelProvider;

	private boolean verticalLayout;

	private ComboViewer rootNoteComboViewer;
	private ComboViewer chordComboViewer;
	private TableViewer chordsViewer;
	private Button browseChordsButton;
	private Button addChordButton;
	private Button removeChordButton;
	private Button refreshChordsButton;

	private Section notesSection;
	private Composite notesComposite;
	private Map<Note, Button> noteButtons;
	private Button calculateButton;

	private IAction verticalAlignmentAction;
	private IAction horizontalAlignmentAction;
	private IAction automaticAlignmentAction;
	private IAction clearInputAction;
	private IAction openPreferencesAction;

	private final ControlAdapter formControlListener = new ControlAdapter() {
		@Override
		public void controlResized(final ControlEvent e) {
			if (automaticAlignmentAction != null && automaticAlignmentAction.isChecked()) {
				setViewOrientationHelper(isVertical());
			}
		}
	};

	private final CellLabelProvider cellLabelProvider = new CellLabelProvider() {

		private Font font;

		@Override
		public void update(final ViewerCell cell) {
			final Object element = cell.getElement();
			if (element instanceof Chord) {

				final Chord chord = (Chord) element;

				// name colum
				if (cell.getColumnIndex() == 0) {
					cell.setText(chord.getBeautifiedName(prefs.getString(Preferences.NOTES_MODE)));
					cell.setImage(null);
				}

				// notes column
				else if (cell.getColumnIndex() == 1) {
					final StringBuffer buf = new StringBuffer();
					for (final Iterator<Note> iter = chord.getNotes().iterator(); iter.hasNext();) {
						final Note note = iter.next();
						buf.append(notesLabelProvider.getText(note));
						if (iter.hasNext()) {
							buf.append(", "); //$NON-NLS-1$
						}
					}
					cell.setText(buf.toString());
					cell.setForeground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
					cell.setFont(getFont(cell));
				}
			}
		}

		private Font getFont(final ViewerCell cell) {
			if (font == null || font.isDisposed()) {
				final FontData[] fD = chordsViewer.getTable().getFont().getFontData();
				font = new Font(display, fD[0].getName(), 9, fD[0].getStyle());
			}
			return font;
		}

		@Override
		public void dispose() {
			if (font != null && !font.isDisposed()) {
				font.dispose();
			}
			super.dispose();
		}
	};

	private final ISelectionChangedListener chordChangedListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			updateAddChordButton();
		}
	};

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		this.site = site;

		prefs = Activator.getDefault().getPreferenceStore();
		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		notesLabelProvider = new NotesLabelProvider(false);
		display = getSite().getShell().getDisplay();

		InstrumentList.getInstance().addChangeListener(this);
		ChordList.getInstance().addChangeListener(this);
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	@Override
	public void createPartControl(final Composite parent) {

		// init ui forms
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);

		// fill part control
		contributeToActionBars();
		verticalLayout = verticalAlignmentAction.isChecked() || automaticAlignmentAction.isChecked() && isVertical();
		createPartControl();
		addDropSupport();
		form.addControlListener(formControlListener);

		// set context-sensitive help
		Activator.getDefault().setHelp(parent, HELP_ID);
	}

	private void createPartControl() {

		if (composite != null && !composite.isDisposed()) {
			final Control[] children = composite.getChildren();
			for (final Control element : children) {
				element.dispose();
			}
		} else {
			composite = form.getBody();
		}

		GridLayoutFactory.fillDefaults().margins(15, 15).spacing(5, 10).applyTo(composite);

		// (1) Chords

		final Section chordsSection = toolkit.createSection(composite, Section.EXPANDED);
		chordsSection.setText(ViewMessages.ScaleFinderView_chords_section);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(chordsSection);
		toolkit.createCompositeSeparator(chordsSection);
		final Composite chordsComposite = toolkit.createComposite(chordsSection);
		GridLayoutFactory.fillDefaults().numColumns(4).margins(0, 5).applyTo(chordsComposite);
		chordsSection.setClient(chordsComposite);

		// root note
		rootNoteComboViewer = WidgetFactory.createRelativeNotesComboViewer(chordsComposite);
		rootNoteComboViewer.getCombo().select(0);
		rootNoteComboViewer.addSelectionChangedListener(chordChangedListener);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(rootNoteComboViewer.getCombo());

		// chord
		chordComboViewer = new ComboViewer(chordsComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		chordComboViewer.setLabelProvider(new ChordLabelProvider());
		chordComboViewer.setContentProvider(new CategoryListContentProvider());
		chordComboViewer.setSorter(new ViewerSorter());
		chordComboViewer.setInput(ChordList.getInstance());
		chordComboViewer.addSelectionChangedListener(chordChangedListener);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(chordComboViewer.getCombo());

		// chord browse button
		browseChordsButton = toolkit.createButton(chordsComposite, "", SWT.PUSH); //$NON-NLS-1$
		browseChordsButton.setImage(Activator.getDefault().getImage(IImageKeys.BROWSE));
		browseChordsButton.setToolTipText(ViewMessages.ScaleFinderView_browse_chords);
		browseChordsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				final IntervalContainerSelectionDialog dialog = new IntervalContainerSelectionDialog(browseChordsButton
						.getShell(), IntervalContainer.TYPE_CHORD, ChordList.getInstance().getRootCategory()
						.getAllElements());
				dialog.setImage(Activator.getDefault().getImage(IImageKeys.CHORD));
				dialog.setTitle(ViewMessages.ChordGenerationView_choose_chord);

				if (dialog.open() == Dialog.OK) {
					final Chord chord = (Chord) dialog.getResult()[0];
					chordComboViewer.setSelection(new StructuredSelection(chord));
				}
			}
		});

		// add chord button
		addChordButton = toolkit.createButton(chordsComposite, "", SWT.PUSH);
		addChordButton.setImage(Activator.getDefault().getImage(IImageKeys.ADD));
		addChordButton.setToolTipText(ViewMessages.ScaleFinderView_add_chord);
		addChordButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Chord chord = getSelectedChord();
				if (!isChordSelected(getSelectedChord())) {
					addChord(chord);
					clearInputAction.setEnabled(true);
					calculateButton.setEnabled(true);
					updateAddChordButton();
					updateRefreshButton();
				}
			}
		});

		// chord list
		final Composite tableComposite = toolkit.createComposite(chordsComposite);
		GridDataFactory.fillDefaults().span(3, 2).grab(true, true).applyTo(tableComposite);
		final TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		chordsViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL);
		final Table table = chordsViewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(false);

		final TableViewerColumn nameViewerColumn = new TableViewerColumn(chordsViewer, SWT.LEFT);
		tableColumnLayout.setColumnData(nameViewerColumn.getColumn(), new ColumnWeightData(2, 60, false));
		final TableViewerColumn notesViewerColumn = new TableViewerColumn(chordsViewer, SWT.RIGHT);
		tableColumnLayout.setColumnData(notesViewerColumn.getColumn(), new ColumnWeightData(3, 90, false));

		chordsViewer.setLabelProvider(cellLabelProvider);
		chordsViewer.setContentProvider(ArrayContentProvider.getInstance());
		chordsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				removeChordButton.setEnabled(!event.getSelection().isEmpty());
			}
		});

		// remove chord button
		removeChordButton = toolkit.createButton(chordsComposite, "", SWT.PUSH);
		removeChordButton.setImage(Activator.getDefault().getImage(IImageKeys.REMOVE));
		removeChordButton.setToolTipText(ViewMessages.ScaleFinderView_remove_chords);
		removeChordButton.setEnabled(false);
		removeChordButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final IStructuredSelection selection = (IStructuredSelection) chordsViewer.getSelection();
				final Set<Integer> noteValuesToRemove = new HashSet<Integer>();
				for (final Object obj : selection.toArray()) {
					if (obj instanceof Chord) {
						final Chord chord = (Chord) obj;
						chordsViewer.remove(chord);
						noteValuesToRemove.addAll(chord.getNoteValues());
					}
				}
				// remove notes
				for (final Chord chord : getChords()) {
					noteValuesToRemove.removeAll(chord.getNoteValues());
				}
				for (final Integer noteValue : noteValuesToRemove) {
					noteButtons.get(Factory.getInstance().getNote(noteValue)).setSelection(false);
				}

				// update selection
				if (chordsViewer.getTable().getItemCount() > 0) {
					chordsViewer.setSelection(new StructuredSelection(chordsViewer.getElementAt(0)));
				}
				updateAddChordButton();
				updateClearAction();
				updateRefreshButton();
				updateCalculateButton();
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(removeChordButton);

		// refresh button
		refreshChordsButton = toolkit.createButton(chordsComposite, "", SWT.PUSH);
		refreshChordsButton.setImage(Activator.getDefault().getImage(IImageKeys.REFRESH));
		refreshChordsButton.setToolTipText(ViewMessages.ScaleFinderView_synchronize_chords);
		refreshChordsButton.setEnabled(false);
		refreshChordsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				for (final Button noteButton : noteButtons.values()) {
					noteButton.setSelection(false);
				}
				for (final Chord chord : getChords()) {
					for (final Integer noteValue : chord.getNoteValues()) {
						noteButtons.get(Factory.getInstance().getNote(noteValue)).setSelection(true);
					}
				}
				refreshChordsButton.setEnabled(false);
				updateCalculateButton();
				updateClearAction();
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(refreshChordsButton);

		// (2) Notes

		notesSection = toolkit.createSection(composite, Section.EXPANDED);
		notesSection.setText(ViewMessages.ScaleFinderView_notes_section);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(notesSection);
		toolkit.createCompositeSeparator(notesSection);
		notesComposite = toolkit.createComposite(notesSection);
		GridLayoutFactory.fillDefaults().numColumns(4).margins(0, 5).equalWidth(true).applyTo(notesComposite);
		notesSection.setClient(notesComposite);

		// note buttons
		noteButtons = new HashMap<Note, Button>();
		for (int i = 0; i <= Constants.MAX_NOTES_VALUE; i++) {
			final Note note = Factory.getInstance().getNote(i);
			final Button button = toolkit.createButton(notesComposite, notesLabelProvider.getText(note), SWT.TOGGLE);
			button.setSelection(false);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					updateClearAction();
					updateRefreshButton();
					updateCalculateButton();
				}
			});
			GridDataFactory.fillDefaults().grab(true, false).applyTo(button);
			noteButtons.put(note, button);
		}

		// (3) Calculate Button
		calculateButton = toolkit.createButton(notesComposite, ViewMessages.ScaleFinderView_calculate, SWT.PUSH);
		calculateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				AbstractFindScalesAction.run(getSite(), getNotes());
				if (Activator.getDefault().getPreferenceStore()
						.getBoolean(Preferences.SCALE_FINDER_VIEW_CLEAR_INPUT_AFTER_CALCULATION)) {
					clearInputAction.run();
				}
			}
		});
		GridDataFactory.fillDefaults().span(4, 1).indent(0, 5).applyTo(calculateButton);

		// (4) final stuff
		updateWidgets();
		updateLayout();
		form.reflow(true);
	}

	private boolean isVertical() {
		return form.getBounds().height * 3 / 2 >= form.getBounds().width;
	}

	private void updateLayout() {

		if (verticalLayout) {

			GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(15, 15).spacing(5, 10)
					.applyTo(composite);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(notesSection);
			for (final Button button : noteButtons.values()) {
				GridDataFactory.fillDefaults().grab(true, false).applyTo(button);
			}
			GridDataFactory.fillDefaults().span(4, 1).grab(true, false).indent(0, 5).applyTo(calculateButton);

		} else {

			GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).margins(15, 15).spacing(25, 10)
					.applyTo(composite);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(notesSection);
			for (final Button button : noteButtons.values()) {
				GridDataFactory.fillDefaults().grab(true, true).applyTo(button);
			}
			GridDataFactory.fillDefaults().span(4, 1).grab(true, true).indent(0, 5).applyTo(calculateButton);

		}
	}

	public void setInput(final Collection<Chord> chords, final Collection<Note> notes) {
		clearInputAction.run();
		for (final Chord chord : chords) {
			chordsViewer.add(chord);
		}
		for (final Note note : notes) {
			noteButtons.get(note).setSelection(true);
		}
		updateAddChordButton();
		updateRefreshButton();
		updateClearAction();
		updateCalculateButton();
	}

	private void updateAddChordButton() {
		final Chord selectedChord = getSelectedChord();
		addChordButton.setEnabled(selectedChord != null && !isChordSelected(selectedChord));
	}

	private Chord getSelectedChord() {
		// (1) first check selected note
		final IStructuredSelection noteSelection = (IStructuredSelection) rootNoteComboViewer.getSelection();
		if (noteSelection == null || noteSelection.isEmpty()) {
			return null;
		}

		// (2) than check selected chord
		final IStructuredSelection chordSelection = (IStructuredSelection) chordComboViewer.getSelection();
		if (chordSelection == null || chordSelection.isEmpty()) {
			return null;
		}

		// (3) build chord to return
		final Note rootNote = (Note) noteSelection.getFirstElement();
		final Chord chord = new Chord((Chord) chordSelection.getFirstElement());
		chord.setRootNote(rootNote);
		return chord;
	}

	private boolean isChordSelected(final Chord chord) {
		for (int i = 0; i < chordsViewer.getTable().getItemCount(); i++) {
			final Chord c = (Chord) chordsViewer.getElementAt(i);
			if (c.getName().equals(chord.getName()) && c.getRootNote().getValue() == chord.getRootNote().getValue()) {
				return true;
			}
		}
		return false;
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
				if (chord != null && !isChordSelected(chord)) {
					final Chord chordToAdd = new Chord(chord);
					chordToAdd.setRootNote(chord.getRootNote());
					addChord(chordToAdd);
				}
			}

			private Chord getFirstElement(final List<?> list) {
				for (final Object element : list) {
					if (element instanceof Chord) {
						return (Chord) element;
					}
				}
				return null;
			}
		});
	}

	@Override
	public void setFocus() {
		composite.setFocus();
		updateCalculateButton();
		StatusLineUtil.handleTestVersionWarning(getViewSite(), NlsUtil.getStatusLineWarningForChordsAndScales());
	}

	private void updateCalculateButton() {

		final boolean noScales = ScaleList.getInstance().getRootCategory().getAllElements().isEmpty();
		final boolean noInstrument = currentInstrument == null;

		calculateButton.setEnabled(!noInstrument && !noScales
				&& getNotes().size() >= FindScalesAction.MIN_NUMBER_OF_NOTES);

		// show error message if necessary (no instrument or chord available)
		String errorMessage = null;
		if (noInstrument && !noScales) {
			errorMessage = ViewMessages.ScaleFinderView_error_msg_1;
		}
		if (!noInstrument && noScales) {
			errorMessage = ViewMessages.ScaleFinderView_error_msg_2;
		}
		if (noInstrument && noScales) {
			errorMessage = ViewMessages.ScaleFinderView_error_msg_3;
		}
		site.getActionBars().getStatusLineManager().setErrorMessage(errorMessage);
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {

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
			updateWidgets();
		}

		// (2) chords changed
		if (source instanceof Chord && property == ChordList.PROP_ADDED || property == ChordList.PROP_REMOVED
				|| property == ChordList.PROP_CHANGED_ELEMENT) {
			updateChords();
			updateCalculateButton();
		}
	}

	@Override
	public void dispose() {
		InstrumentList.getInstance().removeChangeListener(this);
		ChordList.getInstance().removeChangeListener(this);
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		if (form != null && !form.isDisposed() && formControlListener != null) {
			form.removeControlListener(formControlListener);
		}
	}

	private void contributeToActionBars() {
		// create actions
		verticalAlignmentAction = new SetVerticalViewOrientationAction();
		horizontalAlignmentAction = new SetHorizontalViewOrientationAction();
		automaticAlignmentAction = new SetAutomaticViewOrientationAction();
		clearInputAction = new ClearInputAction();
		clearInputAction.setEnabled(false);
		openPreferencesAction = new OpenSpecificPreferencePageAction(ScaleFinderViewPreferencePage.ID);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), verticalAlignmentAction);
		activator.registerAction(getSite(), horizontalAlignmentAction);
		activator.registerAction(getSite(), automaticAlignmentAction);
		activator.registerAction(getSite(), clearInputAction);
		activator.registerAction(getSite(), openPreferencesAction);

		// add actions to menu and toolbar
		final IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		bars.getToolBarManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, clearInputAction);

		bars.getMenuManager().add(new Separator(ALIGNMENT_GROUP));
		bars.getMenuManager().add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		bars.getMenuManager().add(new Separator(BOTTOM_GROUP));

		bars.getMenuManager().appendToGroup(ALIGNMENT_GROUP, verticalAlignmentAction);
		bars.getMenuManager().appendToGroup(ALIGNMENT_GROUP, horizontalAlignmentAction);
		bars.getMenuManager().appendToGroup(ALIGNMENT_GROUP, automaticAlignmentAction);
		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, clearInputAction);
		bars.getMenuManager().appendToGroup(BOTTOM_GROUP, openPreferencesAction);
	}

	private List<Chord> getChords() {
		final List<Chord> chords = new ArrayList<Chord>();
		for (int i = 0; i < chordsViewer.getTable().getItemCount(); i++) {
			chords.add((Chord) chordsViewer.getElementAt(i));
		}
		return chords;
	}

	private List<Note> getNotes() {
		final List<Note> notes = new ArrayList<Note>();
		for (final Map.Entry<Note, Button> entry : noteButtons.entrySet()) {
			if (entry.getValue().getSelection()) {
				notes.add(entry.getKey());
			}
		}
		return notes;
	}

	private void addChord(final Chord chord) {
		chordsViewer.add(chord);
		final Set<Integer> noteValues = chord.getNoteValues();
		for (final Integer value : noteValues) {
			noteButtons.get(Factory.getInstance().getNote(value)).setSelection(true);
		}
	}

	/* --- update methods --- */

	/**
	 * Updates this view and all its widgets corresponding to a change of the
	 * current instrument or the chords list.
	 */
	private void updateWidgets() {
		updateChords();
		updateAddChordButton();
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

	private void updateClearAction() {
		for (final Button button : noteButtons.values()) {
			if (button.getSelection()) {
				clearInputAction.setEnabled(true);
				return;
			}
		}
		clearInputAction.setEnabled(false);
	}

	private void updateRefreshButton() {
		final Set<Note> chordNotes = new HashSet<Note>();
		for (final Chord chord : getChords()) {
			chordNotes.addAll(chord.getNotes());
		}
		final List<Note> notes = new ArrayList<Note>(getNotes());
		notes.removeAll(chordNotes);
		chordNotes.removeAll(getNotes());
		refreshChordsButton.setEnabled(!chordNotes.isEmpty() || !notes.isEmpty());
	}

	/* --- preference change listener --- */

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		// general preferences changed (naming problems)
		if (event.getProperty().equals(Preferences.GENERAL_H_NOTE_NAME)
				|| event.getProperty().equals(Preferences.GENERAL_B_NOTE_NAME)
				|| event.getProperty().equals(Preferences.NOTES_MODE)
				|| event.getProperty().equals(Preferences.CHORD_NAMES_USE_SEPARATOR)
				|| event.getProperty().equals(Preferences.CHORD_NAMES_SEPARATOR)) {

			rootNoteComboViewer.refresh(true);
			for (final Map.Entry<Note, Button> entry : noteButtons.entrySet()) {
				entry.getValue().setText(notesLabelProvider.getText(entry.getKey()));
			}
			// refresh each row since global refresh does not work
			for (int i = 0; i < chordsViewer.getTable().getItemCount(); i++) {
				chordsViewer.refresh(chordsViewer.getElementAt(i));
			}
		}
	}

	/**
	 * Action to clear the currently set input of this view.
	 */
	private class ClearInputAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.clearInput"; //$NON-NLS-1$

		public ClearInputAction() {
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.AbstractGraphicalCalculationView_clear_input);
			setToolTipText(ViewMessages.AbstractGraphicalCalculationView_clear_input);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.CLEAR));
			setDisabledImageDescriptor(Activator.getImageDescriptor(IImageKeys.CLEAR_DISABLED));
		}

		@Override
		public void run() {
			chordsViewer.setInput(null);
			for (final Button button : noteButtons.values()) {
				button.setSelection(false);
			}
			calculateButton.setEnabled(false);
			addChordButton.setEnabled(true);
			refreshChordsButton.setEnabled(false);
		}
	}

	/**
	 * Action to set the automatic view orientation.
	 */
	private class SetAutomaticViewOrientationAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.automaticAlignment"; //$NON-NLS-1$

		public SetAutomaticViewOrientationAction() {
			setId("chordGenerator.ui.views.IntervalContainerView.SetAutomaticViewOrientationAction"); //$NON-NLS-1$
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.IntervalContainerView_automatic_view_orientation);
			setToolTipText(ViewMessages.IntervalContainerView_automatic_view_orientation);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.AUTOMATIC_VIEW_ORIENTATION));

			// load preferences
			setChecked(prefs.getString(Preferences.SCALE_FINDER_VIEW_ORIENTATION).equals(
					UIConstants.AUTOMATIC_VIEW_ORIENTATION));
		}

		@Override
		public int getStyle() {
			return AS_RADIO_BUTTON;
		}

		@Override
		public void run() {
			applyAutomaticViewOrientation();
			prefs.setValue(Preferences.SCALE_FINDER_VIEW_ORIENTATION, UIConstants.AUTOMATIC_VIEW_ORIENTATION);
		}
	}

	/**
	 * Action to set the vertical view orientation.
	 */
	private class SetVerticalViewOrientationAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.verticalAlignment"; //$NON-NLS-1$

		public SetVerticalViewOrientationAction() {
			setId(COMMAND_ID);
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.IntervalContainerView_vertical_view_orientation);
			setToolTipText(ViewMessages.IntervalContainerView_vertical_view_orientation);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.VERTIVAL_VIEW_ORIENTATION));

			// load preferences
			setChecked(prefs.getString(Preferences.SCALE_FINDER_VIEW_ORIENTATION).equals(
					UIConstants.VERTICAL_VIEW_ORIENTATION));
		}

		@Override
		public int getStyle() {
			return AS_RADIO_BUTTON;
		}

		@Override
		public void run() {
			setViewOrientation(true);
			prefs.setValue(Preferences.SCALE_FINDER_VIEW_ORIENTATION, UIConstants.VERTICAL_VIEW_ORIENTATION);
		}
	}

	/**
	 * Action to set the horizontal view orientation.
	 */
	private class SetHorizontalViewOrientationAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.horizontalAlignment"; //$NON-NLS-1$

		public SetHorizontalViewOrientationAction() {
			setId(COMMAND_ID);
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.IntervalContainerView_horizontal_view_orientation);
			setToolTipText(ViewMessages.IntervalContainerView_horizontal_view_orientation);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.HORIZONTAL_VIEW_ORIENTATION));

			// load preferences
			setChecked(prefs.getString(Preferences.SCALE_FINDER_VIEW_ORIENTATION).equals(
					UIConstants.HORIZONTAL_VIEW_ORIENTATION));
		}

		@Override
		public int getStyle() {
			return AS_RADIO_BUTTON;
		}

		@Override
		public void run() {
			setViewOrientation(false);
			prefs.setValue(Preferences.SCALE_FINDER_VIEW_ORIENTATION, UIConstants.HORIZONTAL_VIEW_ORIENTATION);
		}
	}

	/**
	 * Sets the view orientation.
	 * 
	 * @param setVertical
	 *            true, if the orientation of the view shall be vertical, false
	 *            otherwise
	 */
	private void setViewOrientation(final boolean setVertical) {
		if (verticalAlignmentAction != null && horizontalAlignmentAction != null && automaticAlignmentAction != null) {
			verticalAlignmentAction.setChecked(setVertical);
			horizontalAlignmentAction.setChecked(!setVertical);
			automaticAlignmentAction.setChecked(false);
		}
		setViewOrientationHelper(setVertical);
	}

	/**
	 * Computes the view orientation automatically.
	 */
	private void applyAutomaticViewOrientation() {
		if (form == null || form.isDisposed()) {
			return;
		}
		if (verticalAlignmentAction != null && horizontalAlignmentAction != null && automaticAlignmentAction != null) {
			verticalAlignmentAction.setChecked(false);
			horizontalAlignmentAction.setChecked(false);
			automaticAlignmentAction.setChecked(true);
		}
		setViewOrientationHelper(isVertical());
	}

	private void setViewOrientationHelper(final boolean vertical) {
		if (verticalLayout != vertical) {
			verticalLayout = vertical;

			// store selections
			final List<Chord> chords = new ArrayList<Chord>();
			for (int i = 0; i < chordsViewer.getTable().getItemCount(); i++) {
				chords.add((Chord) chordsViewer.getElementAt(i));
			}
			final List<Note> notes = new ArrayList<Note>();
			for (final Entry<Note, Button> entry : noteButtons.entrySet()) {
				if (entry.getValue().getSelection()) {
					notes.add(entry.getKey());
				}
			}

			// recreate part control
			createPartControl();

			// apply prior selections
			for (final Chord chord : chords) {
				chordsViewer.add(chord);
			}
			for (final Note note : notes) {
				noteButtons.get(note).setSelection(true);
			}
		}
	}
}
