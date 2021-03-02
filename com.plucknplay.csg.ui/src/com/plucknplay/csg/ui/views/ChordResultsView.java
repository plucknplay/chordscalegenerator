/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;

import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Unit;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.actions.chords.FindAlternateFingeringsAction;
import com.plucknplay.csg.ui.actions.chords.OpenChordResultInEditorAction;
import com.plucknplay.csg.ui.actions.chords.PlayChordResultAction;
import com.plucknplay.csg.ui.actions.common.CopyResultsToClipboardAction;
import com.plucknplay.csg.ui.actions.scales.FindScalesAction;
import com.plucknplay.csg.ui.actions.scales.OpenScaleFinderAction;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.preferencePages.ChordResultsViewPreferencePage;
import com.plucknplay.csg.ui.views.dnd.GriptableDragListener;
import com.plucknplay.csg.ui.views.dnd.GriptableTransfer;

public class ChordResultsView extends AbstractResultsView {

	public static final String ID = "com.plucknplay.csg.ui.views.ChordResultsView"; //$NON-NLS-1$
	public static final String HELP_ID = "chord_results_view_context"; //$NON-NLS-1$

	private TableViewerColumn levelColumn;
	private TableViewerColumn bassColumn;
	private TableViewerColumn leadColumn;
	private TableViewerColumn notesColumn;
	private TableViewerColumn intervalsColumn;
	private TableViewerColumn fretsColumn;
	private TableViewerColumn minStringColumn;
	private TableViewerColumn maxStringColumn;
	private TableViewerColumn stringSpanColumn;
	private TableViewerColumn minFretColumn;
	private TableViewerColumn maxFretColumn;
	private TableViewerColumn fretSpanColumn;
	private TableViewerColumn distanceColumn;
	private TableViewerColumn emptyStringsColumn;
	private TableViewerColumn mutedStringsColumn;
	private TableViewerColumn doubledTonesColumn;

	private IPreferenceStore prefs;

	private Image checkImage;
	private Image uncheckImage;

	private Unit distanceUnit;

	private OpenChordResultInEditorAction openInEditorAction;
	private FindAlternateFingeringsAction findAlternativeFingeringsAction;
	private FindScalesAction findScalesAction;
	private OpenScaleFinderAction openScaleFinderAction;
	private CopyResultsToClipboardAction copyToClipboardAction;
	private PlayChordResultAction playAction;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		distanceUnit = currentInstrument != null ? currentInstrument.getScaleLengthUnit() : Unit.mm;
		checkImage = Activator.getDefault().getImage(IImageKeys.CHECK_ALL);
		uncheckImage = Activator.getDefault().getImage(IImageKeys.UNCHECK_ALL);
		prefs = Activator.getDefault().getPreferenceStore();
		super.init(site);
	}

	@Override
	protected AbstractTableViewerSorter getResultsViewSorter() {
		return new ChordResultsViewSorter();
	}

	@Override
	protected int getEntriesPerPage() {
		return prefs.getInt(Preferences.CHORD_RESULTS_VIEW_ENTRIES_PER_PAGE);
	}

	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);

		updateDistanceTableColumn();
	}

	@Override
	protected void defineTableViewer(final TableViewer viewer) {

		// double click listener
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				final Griptable selectedGriptable = (Griptable) selection.getFirstElement();
				if (selectedGriptable != null) {
					Activator.getDefault().getSoundMachine().play(selectedGriptable);
				}
			}
		});

		// selection listener
		viewer.getTable().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (e.detail == SWT.CHECK) {
					final TableItem item = (TableItem) e.item;
					final Griptable griptable = (Griptable) item.getData();
					griptable.setSelected(item.getChecked());
				}
			}
		});

		// add drag support
		final Transfer[] transfer = new Transfer[] { GriptableTransfer.getInstance() };
		viewer.addDragSupport(DND.DROP_MOVE, transfer, new GriptableDragListener(viewer));

		// add mouse down listener for griptable selection
		viewer.getTable().addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				final Rectangle clientArea = viewer.getTable().getClientArea();
				final Point pt = new Point(event.x, event.y);
				int index = viewer.getTable().getTopIndex();
				while (index < viewer.getTable().getItemCount()) {
					boolean visible = false;
					final TableItem item = viewer.getTable().getItem(index);
					final Rectangle rect = item.getBounds(0);
					if (rect.contains(pt) && item.getData() instanceof Griptable) {
						toggleSelection((Griptable) item.getData());
					}
					if (!visible && rect.intersects(clientArea)) {
						visible = true;
					}
					if (!visible) {
						return;
					}
					index++;
				}
			}
		});
	}

	@Override
	protected void keyPressedEvent(final KeyEvent e) {
		super.keyPressedEvent(e);
		if (e.keyCode == SWT.SPACE) {
			final ISelection selection = getViewer().getSelection();
			if (selection != null && !selection.isEmpty() && selection instanceof IStructuredSelection) {
				final Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				if (firstElement instanceof Griptable) {
					toggleSelection((Griptable) firstElement);
				}
			}
		}
	}

	private void toggleSelection(final Griptable griptable) {
		griptable.setSelected(!griptable.isSelected());
		getViewer().refresh(griptable, true);
	}

	@Override
	protected void createTableColumns(final TableViewer tableViewer) {

		int columnNumber = -1;

		// selection
		final TableViewerColumn selectionColumn = createTableColumn(
				tableViewer,
				"", ViewMessages.ChordResultsView_select_your_favorites, false, SWT.LEFT, ++columnNumber, Preferences.CHORD_RESULTS_VIEW_SELECTION_COLUMN_WIDTH); //$NON-NLS-1$
		selectionColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setImage(((Griptable) cell.getElement()).isSelected() ? checkImage : uncheckImage);
				}
			}
		});

		// chord name
		final TableViewerColumn nameColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_chord,
				ViewMessages.ChordResultsView_chord, true, SWT.LEFT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_CHORD_COLUMN_WIDTH);
		nameColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).getBeautifiedName(prefs
							.getString(Preferences.NOTES_MODE)));
				}
			}
		});

		// level
		levelColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_level,
				ViewMessages.ChordResultsView_level, true, SWT.LEFT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_LEVEL_COLUMN_WIDTH);
		levelColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				final Object element = cell.getElement();
				if (element instanceof Griptable) {
					final Griptable griptable = (Griptable) element;
					final int level = griptable.getFingering(prefs.getBoolean(Preferences.CALCULATOR_BARRES_PREFERRED))
							.getLevel();
					cell.setText(UIConstants.LEVELS[level]);
				}
			}
		});

		// bass note
		bassColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_bass_tone,
				ViewMessages.ChordResultsView_bass_tone, true, SWT.LEFT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_BASS_TONE_COLUMN_WIDTH);
		bassColumn.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					final Griptable griptable = (Griptable) cell.getElement();
					final String absoluteBassToneString = griptable.getAbsoluteBassToneString(prefs
							.getString(Preferences.NOTES_MODE));
					final String bassIntervalString = griptable.getBassIntervalString();
					cell.setText(absoluteBassToneString + " (" + bassIntervalString + ")"); //$NON-NLS-1$ //$NON-NLS-2$

					final StyleRange[] styleRanges = { prefs
							.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_IN_GRAY) ? new StyleRange(
							absoluteBassToneString.length() + 1, bassIntervalString.length() + 2,
							IFigureConstants.GREY, null) : new StyleRange() };
					cell.setStyleRanges(styleRanges);
				}
				super.update(cell);
			}
		});

		// lead note
		leadColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_lead_tone,
				ViewMessages.ChordResultsView_lead_tone, true, SWT.LEFT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_LEAD_TONE_COLUMN_WIDTH);
		leadColumn.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					final Griptable griptable = (Griptable) cell.getElement();
					final String absoluteLeadToneString = griptable.getAbsoluteLeadToneString(prefs
							.getString(Preferences.NOTES_MODE));
					final String leadIntervalString = griptable.getLeadIntervalString();
					cell.setText(absoluteLeadToneString + " (" + leadIntervalString + ")"); //$NON-NLS-1$ //$NON-NLS-2$

					final StyleRange[] styleRanges = { prefs
							.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_IN_GRAY) ? new StyleRange(
							absoluteLeadToneString.length() + 1, leadIntervalString.length() + 2,
							IFigureConstants.GREY, null) : new StyleRange() };
					cell.setStyleRanges(styleRanges);
				}
				super.update(cell);
			}
		});

		// notes
		notesColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_notes,
				ViewMessages.ChordResultsView_notes, true, SWT.LEFT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_NOTES_COLUMN_WIDTH);
		notesColumn.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					final String notesString = ((Griptable) cell.getElement()).getNotesString(
							prefs.getString(Preferences.NOTES_MODE),
							prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_COMPACT_MODE));
					cell.setText(notesString);
					if (prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_IN_GRAY)) {
						setStyleRanges(getStyleRanges(notesString), cell);
					} else {
						cell.setStyleRanges(new StyleRange[0]);
					}
				}
				super.update(cell);
			}
		});

		// intervals
		intervalsColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_intervals,
				ViewMessages.ChordResultsView_intervals, true, SWT.LEFT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_INTERVALS_COLUMN_WIDTH);
		intervalsColumn.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					final String intervalString = ((Griptable) cell.getElement()).getIntervalString(prefs
							.getBoolean(Preferences.CHORD_RESULTS_VIEW_COMPACT_MODE));
					cell.setText(intervalString);
					if (prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_IN_GRAY)) {
						setStyleRanges(getStyleRanges(intervalString), cell);
					} else {
						cell.setStyleRanges(new StyleRange[0]);
					}
				}
				super.update(cell);
			}
		});

		// frets
		fretsColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_frets,
				ViewMessages.ChordResultsView_frets, true, SWT.LEFT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_FRETS_COLUMN_WIDTH);
		fretsColumn.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					final String fretsString = ((Griptable) cell.getElement()).getFretsString(prefs
							.getBoolean(Preferences.CHORD_RESULTS_VIEW_COMPACT_MODE));
					cell.setText(fretsString);
					if (prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_IN_GRAY)) {
						setStyleRanges(getStyleRanges(fretsString), cell);
					} else {
						cell.setStyleRanges(new StyleRange[0]);
					}
				}
				super.update(cell);
			}
		});

		// minimum string number
		minStringColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_min_s,
				ViewMessages.ChordResultsView_minimal_string, true, SWT.RIGHT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_MIN_STRING_COLUMN_WIDTH);
		minStringColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).getMinString() + "");
				}
			}
		});

		// maximum string number
		maxStringColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_max_s,
				ViewMessages.ChordResultsView_maximal_string, true, SWT.RIGHT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_MAX_STRING_COLUMN_WIDTH);
		maxStringColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).getMaxString() + "");
				}
			}
		});

		// string span
		stringSpanColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_ssp,
				ViewMessages.ChordResultsView_string_span, true, SWT.RIGHT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_STRING_SPAN_COLUMN_WIDTH);
		stringSpanColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).getStringSpan() + "");
				}
			}
		});

		// minimum fret number
		minFretColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_min,
				ViewMessages.ChordResultsView_minimal_fret, true, SWT.RIGHT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_MIN_FRET_COLUMN_WIDTH);
		minFretColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).getMinFret() + "");
				}
			}
		});

		// maximum fret number
		maxFretColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_max,
				ViewMessages.ChordResultsView_maximum_fret, true, SWT.RIGHT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_MAX_FRET_COLUMN_WIDTH);
		maxFretColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).getMaxFret() + "");
				}
			}
		});

		// fret span
		fretSpanColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_fsp,
				ViewMessages.ChordResultsView_fret_span, true, SWT.RIGHT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_FRET_SPAN_COLUMN_WIDTH);
		fretSpanColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).getFretSpan() + "");
				}
			}
		});

		// distance
		distanceColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_gw,
				ViewMessages.ChordResultsView_grip_width, true, SWT.RIGHT, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_DISTANCE_COLUMN_WIDTH);
		distanceColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).getFormattedGripDistance(distanceUnit));
				}
			}
		});

		// number of empty strings
		emptyStringsColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_es,
				ViewMessages.ChordResultsView_number_of_empty_strings, true, SWT.CENTER, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_EMPTY_STRINGS_COLUMN_WIDTH);
		emptyStringsColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).getEmptyStringsCount() + "");
				}
			}
		});

		// number of muted strings
		mutedStringsColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_ds,
				ViewMessages.ChordResultsView_number_of_muted_strings, true, SWT.CENTER, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_MUTED_STRINGS_COLUMN_WIDTH);
		mutedStringsColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).getMutedStringsCount() + "");
				}
			}
		});

		// double tones
		doubledTonesColumn = createTableColumn(tableViewer, ViewMessages.ChordResultsView_dt,
				ViewMessages.ChordResultsView_doubled_tones, true, SWT.CENTER, ++columnNumber,
				Preferences.CHORD_RESULTS_VIEW_DOUBLED_TONES_COLUMN_WIDTH);
		doubledTonesColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof Griptable) {
					cell.setText(((Griptable) cell.getElement()).hasDoubledTones() ? ViewMessages.ChordResultsView_yes
							: ViewMessages.ChordResultsView_no);
				}
			}
		});
	}

	@Override
	protected List<StyleRange> getStyleRanges(final String string) {
		final List<StyleRange> styleRanges = super.getStyleRanges(string);

		// style separator
		int startFrom = 0;
		while (string.indexOf("x", startFrom) > -1) {
			startFrom = string.indexOf("x", startFrom);
			styleRanges.add(new StyleRange(startFrom, 1, IFigureConstants.GREY, null));
			startFrom++;
		}
		return styleRanges;
	}

	@Override
	protected void updateColumnVisibility() {
		setColumnVisible(levelColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_LEVEL_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_LEVEL_COLUMN_WIDTH));
		setColumnVisible(bassColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_BASS_TONE_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_BASS_TONE_COLUMN_WIDTH));
		setColumnVisible(leadColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_LEAD_TONE_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_LEAD_TONE_COLUMN_WIDTH));
		setColumnVisible(notesColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_NOTES_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_NOTES_COLUMN_WIDTH));
		setColumnVisible(intervalsColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_INTERVALS_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_INTERVALS_COLUMN_WIDTH));
		setColumnVisible(fretsColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_FRETS_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_FRETS_COLUMN_WIDTH));
		setColumnVisible(minStringColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_STRING_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_MIN_STRING_COLUMN_WIDTH));
		setColumnVisible(maxStringColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_STRING_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_MAX_STRING_COLUMN_WIDTH));
		setColumnVisible(stringSpanColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_STRING_SPAN_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_STRING_SPAN_COLUMN_WIDTH));
		setColumnVisible(minFretColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_FRET_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_MIN_FRET_COLUMN_WIDTH));
		setColumnVisible(maxFretColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_FRET_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_MAX_FRET_COLUMN_WIDTH));
		setColumnVisible(fretSpanColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_FRET_SPAN_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_FRET_SPAN_COLUMN_WIDTH));
		setColumnVisible(distanceColumn, prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_DISTANCE_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_DISTANCE_COLUMN_WIDTH));
		setColumnVisible(emptyStringsColumn,
				prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_EMPTY_STRINGS_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_EMPTY_STRINGS_COLUMN_WIDTH));
		setColumnVisible(mutedStringsColumn,
				prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_MUTED_STRINGS_COLUMN_WIDTH));
		setColumnVisible(doubledTonesColumn,
				prefs.getBoolean(Preferences.CHORD_RESULTS_VIEW_SHOW_DOUBLED_TONES_COLUMN),
				prefs.getInt(Preferences.CHORD_RESULTS_VIEW_DOUBLED_TONES_COLUMN_WIDTH));
	}

	private void updateDistanceTableColumn() {
		final String text = ViewMessages.ChordResultsView_gw + " (" + distanceUnit + ")"; //$NON-NLS-1$//$NON-NLS-2$
		distanceColumn.getColumn().setText(text);
		getViewer().refresh(true);
	}

	@Override
	public void setInput(final Collection<?> input) {
		super.setInput(input);

		// notifiy all listeners when the input size was 0
		if (input.size() == 0) {
			Activator.getDefault().notifyListeners(Activator.PROP_RESULTS_FLUSHED, null);
		}
	}

	public void setDistanceUnit(final Unit distanceUnit) {
		this.distanceUnit = distanceUnit;
		updateDistanceTableColumn();
	}

	@Override
	protected void refreshViewer() {
		super.refreshViewer();

		final Object input = getViewer().getInput();
		if (input == null || !(input instanceof List)) {
			return;
		}
		final List<?> subList = (List<?>) input;

		// update the selection and level background color
		int i = 0;
		for (final Object next : subList) {
			if (next instanceof Griptable) {
				final Griptable griptable = (Griptable) next;
				final TableItem tableItem = getViewer().getTable().getItem(i);
				tableItem.setChecked(griptable.isSelected());
				i++;
			}
		}
	}

	@Override
	protected void createAndRegisterContextActions() {

		// create actions
		openInEditorAction = new OpenChordResultInEditorAction(this);
		findAlternativeFingeringsAction = new FindAlternateFingeringsAction(getSite());
		findScalesAction = new FindScalesAction(getSite());
		openScaleFinderAction = new OpenScaleFinderAction(getSite());
		copyToClipboardAction = new CopyResultsToClipboardAction(this);
		playAction = new PlayChordResultAction();

		// add actions
		addAction(openInEditorAction);
		addAction(findAlternativeFingeringsAction);
		addAction(findScalesAction);
		addAction(openScaleFinderAction);
		addAction(copyToClipboardAction);
		addAction(playAction);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), openInEditorAction);
		activator.registerAction(getSite(), findAlternativeFingeringsAction);
		activator.registerAction(getSite(), findScalesAction);
		activator.registerAction(getSite(), openScaleFinderAction);
		activator.registerAction(getSite(), copyToClipboardAction);
		activator.registerAction(getSite(), playAction);
	}

	@Override
	protected void addToContextMenu(final IMenuManager mgr) {
		mgr.appendToGroup(TOP_GROUP, openInEditorAction);
		mgr.appendToGroup(FIND_GROUP, findAlternativeFingeringsAction);
		mgr.appendToGroup(FIND_GROUP, findScalesAction);
		mgr.appendToGroup(FIND_GROUP, openScaleFinderAction);
		mgr.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, copyToClipboardAction);
		mgr.appendToGroup(SOUND_GROUP, playAction);
	}

	@Override
	protected void contributeToActionBars() {
		super.contributeToActionBars();

		// create actions
		final CheckAllAction checkAllAction = new CheckAllAction();
		final UncheckAllAction uncheckAllAction = new UncheckAllAction();
		final CopyResultsToClipboardAction copyAction = new CopyResultsToClipboardAction(this);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), checkAllAction);
		activator.registerAction(getSite(), uncheckAllAction);
		activator.registerAction(getSite(), copyAction);

		// contribute actions
		final IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().appendToGroup(TOP_GROUP, checkAllAction);
		bars.getMenuManager().appendToGroup(TOP_GROUP, uncheckAllAction);
		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, copyAction);
		bars.getToolBarManager().appendToGroup(END_GROUP, checkAllAction);
		bars.getToolBarManager().appendToGroup(END_GROUP, uncheckAllAction);
	}

	@Override
	protected String getCorrespondingPreferencePageId() {
		return ChordResultsViewPreferencePage.ID;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		final String property = event.getProperty();
		final Object newValue = event.getNewValue();
		final Object oldValue = event.getOldValue();

		// column visibility changed
		if (property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_LEVEL_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_BASS_TONE_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_LEAD_TONE_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_NOTES_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_INTERVALS_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_FRETS_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_STRING_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_STRING_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_STRING_SPAN_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_MIN_FRET_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_MAX_FRET_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_FRET_SPAN_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_DISTANCE_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_EMPTY_STRINGS_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_COLUMN)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_DOUBLED_TONES_COLUMN)) {
			updateColumnVisibility();
		}

		if (property.equals(Preferences.NOTES_MODE) || property.equals(Preferences.GENERAL_H_NOTE_NAME)
				|| property.equals(Preferences.GENERAL_B_NOTE_NAME)
				|| property.equals(Preferences.ABSOLUTE_NOTE_NAMES_MODE)
				|| property.equals(Preferences.INTERVAL_NAMES_MODE)
				|| property.equals(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME)
				|| property.equals(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME)
				|| property.equals(Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_COMPACT_MODE)
				|| property.equals(Preferences.CHORD_RESULTS_VIEW_SHOW_MUTED_STRINGS_IN_GRAY)
				|| property.equals(Preferences.CHORD_NAMES_USE_SEPARATOR)
				|| property.equals(Preferences.CHORD_NAMES_SEPARATOR)
				|| property.equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_SHORT_MODE)
				|| property.equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_PREFIX_MODE)
				|| property.equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_PREFIX_AND_INTERVALS)
				|| property.equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_INTERVALS)
				|| property.equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_COMPACT_MODE)
				|| property.equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_IN_BRACKETS)
				|| property.equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BRACKETS_MODE)) {
			getViewer().refresh(true);
		}

		if (getResults().isEmpty()) {
			return;
		}

		final boolean preferBarrees = prefs.getBoolean(Preferences.CALCULATOR_BARRES_PREFERRED);

		// max results number changed
		if (property.equals(Preferences.CALCULATOR_MAX_RESULTS_NUMBER)) {
			final int newMaxNumber = prefs.getInt(Preferences.CALCULATOR_MAX_RESULTS_NUMBER);
			if (getResults().size() > newMaxNumber) {
				setInput(getResults().subList(0, newMaxNumber));
			}
		}

		// prefer barres changed
		if (property.equals(Preferences.CALCULATOR_BARRES_PREFERRED)) {

			// show warning dialog
			final boolean hidePrompt = prefs.getBoolean(Preferences.WARNINGS_HIDE_PROMPT_LEVEL_CHANGE);
			if (!hidePrompt) {
				final MessageDialogWithToggle dialog = MessageDialogWithToggle.openInformation(getSite().getShell(),
						ViewMessages.ChordResultsView_information_title, ViewMessages.ChordResultsView_1
								+ ViewMessages.ChordResultsView_2 + ViewMessages.ChordResultsView_3
								+ ViewMessages.ChordResultsView_4, ViewMessages.ChordResultsView_information_prompt,
						hidePrompt, null, null);
				if (dialog.getReturnCode() == Dialog.OK) {
					prefs.setValue(Preferences.WARNINGS_HIDE_PROMPT_LEVEL_CHANGE, dialog.getToggleState());
				}
			}

			// update fingerings
			for (final Object next : getResults()) {
				if (next instanceof Griptable) {
					((Griptable) next).updateFingering(preferBarrees);
				}
			}
			getViewer().setSelection(getViewer().getSelection());
			refreshViewer();
		}

		// entries per page changed
		if (property.equals(Preferences.CHORD_RESULTS_VIEW_ENTRIES_PER_PAGE)) {
			setInput(getResults());
		}

		else if (newValue != null
				&& oldValue != null
				&& newValue instanceof Integer
				&& oldValue instanceof Integer
				&& (property.equals(Preferences.FRET_NUMBER) && (Integer) newValue < (Integer) oldValue || property
						.equals(Preferences.CAPO_FRET) && (Integer) newValue > (Integer) oldValue)) {

			// update results list
			boolean changed = false;
			final List<?> results = new ArrayList<Object>(getResults());
			for (final Iterator<?> iter = results.iterator(); iter.hasNext();) {
				final Object result = iter.next();
				if (!(result instanceof Griptable)) {
					continue;
				}

				final Griptable griptable = (Griptable) result;
				if (property.equals(Preferences.FRET_NUMBER)
						&& griptable.getMaxFret() > Instrument.getFretNumber()
						|| property.equals(Preferences.CAPO_FRET)
						&& (griptable.hasEmptyStringNotes() && Instrument.getCapoFret() > 0 || griptable
								.getMinFret(false) <= Instrument.getCapoFret())) {
					iter.remove();
					changed = true;
				}
			}

			if (changed) {
				setInput(results);
			}
		}
	}

	/**
	 * Returns the selected/checked griptables of this view.
	 * 
	 * @return the selected/checked griptables of this view
	 */
	public List<Griptable> getSelectedResults() {
		final List<Griptable> selectedResults = new ArrayList<Griptable>();
		for (final Object next : getResults()) {
			if (next instanceof Griptable) {
				final Griptable current = (Griptable) next;
				if (current.isSelected()) {
					selectedResults.add(current);
				}
			}
		}
		return selectedResults;
	}

	/**
	 * Flushs this view.
	 */
	@Override
	public void flush() {
		super.flush();
		Activator.getDefault().notifyListeners(Activator.PROP_RESULTS_FLUSHED, null);
	}

	/**
	 * Returns the distance unit used actually in this view.
	 * 
	 * @return the distance unit used actually in this view
	 */
	public Unit getDistanceUnit() {
		return distanceUnit;
	}

	@Override
	protected String getHelpId() {
		return HELP_ID;
	}

	/* --- actions --- */

	/**
	 * Action to select/check all griptables.
	 */
	private class CheckAllAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.checkAll"; //$NON-NLS-1$

		public CheckAllAction() {
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.ChordResultsView_check_all);
			setToolTipText(ViewMessages.ChordResultsView_check_all);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.CHECK_ALL));
		}

		@Override
		public void run() {
			for (final Object next : getResults()) {
				if (next instanceof Griptable) {
					((Griptable) next).setSelected(true);
				}
			}
			refreshViewer();
		}
	}

	/**
	 * Action to deselect/uncheck all griptables.
	 */
	private class UncheckAllAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.uncheckAll"; //$NON-NLS-1$

		public UncheckAllAction() {
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.ChordResultsView_uncheck_all);
			setToolTipText(ViewMessages.ChordResultsView_uncheck_all);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.UNCHECK_ALL));
		}

		@Override
		public void run() {
			for (final Object next : getResults()) {
				if (next instanceof Griptable) {
					((Griptable) next).setSelected(false);
				}
			}
			refreshViewer();
		}
	}
}
