/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.ScaleResult;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.common.CopyBlocksToClipboardAction;
import com.plucknplay.csg.ui.actions.scales.OpenScaleResultInEditorAction;
import com.plucknplay.csg.ui.actions.scales.PlayScaleResultAction;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.model.BlockManager;
import com.plucknplay.csg.ui.preferencePages.ScaleResultsViewPreferencePage;

public class ScaleResultsView extends AbstractResultsView {

	public static final String ID = "com.plucknplay.csg.ui.views.ScaleResultsView"; //$NON-NLS-1$
	public static final String HELP_ID = "scale_results_view_context"; //$NON-NLS-1$

	private TableViewerColumn intervalsColumn;
	private TableViewerColumn notesColumn;
	private TableViewerColumn rootNoteColumn;
	private TableViewerColumn coverageColumn;

	private MarkSearchAction markSearchAction;
	private OpenScaleResultInEditorAction openInEditorAction;
	private CopyBlocksToClipboardAction copyToClipboardAction;
	private PlayScaleResultAction playAction;

	@Override
	protected AbstractTableViewerSorter getResultsViewSorter() {
		return new ScaleResultsViewSorter();
	}

	@Override
	protected int getEntriesPerPage() {
		return Activator.getDefault().getPreferenceStore().getInt(Preferences.SCALE_RESULTS_VIEW_ENTRIES_PER_PAGE);
	}

	@Override
	protected void defineTableViewer(final TableViewer viewer) {

		// double click listener
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				final ScaleResult selectedScaleResult = (ScaleResult) selection.getFirstElement();
				if (selectedScaleResult != null) {
					Activator.getDefault().getSoundMachine()
							.play(BlockManager.getInstance().getDefaultBlock(selectedScaleResult));
				}
			}
		});
	}

	@Override
	protected void contributeToActionBars() {
		super.contributeToActionBars();

		// create actions
		markSearchAction = new MarkSearchAction();
		markSearchAction.setChecked(Activator.getDefault().getPreferenceStore()
				.getBoolean(Preferences.SCALE_RESULTS_VIEW_MARK_SEARCH));

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), markSearchAction);

		// contribute actions
		final IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().appendToGroup(TOP_GROUP, markSearchAction);
		bars.getToolBarManager().appendToGroup(END_GROUP, markSearchAction);

	}

	@Override
	protected void createTableColumns(final TableViewer viewer) {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		int columnNumber = -1;

		// name
		final TableViewerColumn nameColumn = createTableColumn(viewer, ViewMessages.ScaleResultsView_scale,
				ViewMessages.ScaleResultsView_scale, true, SWT.LEFT, ++columnNumber,
				Preferences.SCALE_RESULTS_VIEW_SCALE_COLUMN_WIDTH);
		nameColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof ScaleResult) {
					cell.setText(((ScaleResult) cell.getElement()).getBeautifiedName(prefs
							.getString(Preferences.NOTES_MODE)));
				}
			}
		});

		// intervals
		intervalsColumn = createTableColumn(viewer, ViewMessages.ScaleResultsView_intervals,
				ViewMessages.ScaleResultsView_intervals, true, SWT.LEFT, ++columnNumber,
				Preferences.SCALE_RESULTS_VIEW_INTERVALS_COLUMN_WIDTH);
		intervalsColumn.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof ScaleResult) {
					final ScaleResult scaleResult = (ScaleResult) cell.getElement();

					int start = 0;
					final StringBuffer buf = new StringBuffer();
					final List<StyleRange> searchStyleRanges = new ArrayList<StyleRange>();
					final List<Interval> referenceIntervals = scaleResult.getReferenceIntervals();
					final boolean compactMode = prefs.getBoolean(Preferences.SCALE_RESULTS_VIEW_COMPACT_MODE);

					for (final Iterator<Interval> iter = scaleResult.getSortedIntervals().iterator(); iter.hasNext();) {
						final Interval interval = iter.next();
						if (interval == null) {
							continue;
						}
						final String intervalName = scaleResult.getIntervalName(interval);
						buf.append(intervalName);
						if (referenceIntervals.contains(interval) && markSearchAction.isChecked()) {
							searchStyleRanges.add(new StyleRange(start, intervalName.length(), null,
									IFigureConstants.YELLOW));
						}
						if (iter.hasNext()) {
							buf.append(compactMode ? "-" : " - "); //$NON-NLS-1$ //$NON-NLS-2$
						}
						start = buf.length();
					}
					cell.setText(buf.toString());

					// gray separators
					if (prefs.getBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_SEPARATORS_IN_GRAY)) {
						final List<StyleRange> styleRanges = getStyleRanges(buf.toString());
						// gray separators + marked search
						if (markSearchAction.isChecked()) {
							styleRanges.addAll(searchStyleRanges);
						}
						setStyleRanges(styleRanges, cell);
					}

					// black separators + marked search
					else if (markSearchAction.isChecked()) {
						setStyleRanges(searchStyleRanges, cell);
					}

					// no styling: black separators + no marked search
					else {
						cell.setStyleRanges(new StyleRange[0]);
					}
				}
				super.update(cell);
			}
		});

		// notes
		notesColumn = createTableColumn(viewer, ViewMessages.ScaleResultsView_notes,
				ViewMessages.ScaleResultsView_notes, true, SWT.LEFT, ++columnNumber,
				Preferences.SCALE_RESULTS_VIEW_NOTES_COLUMN_WIDTH);
		notesColumn.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof ScaleResult) {
					final ScaleResult scaleResult = (ScaleResult) cell.getElement();

					int start = 0;
					final StringBuffer buf = new StringBuffer();
					final List<StyleRange> searchStyleRanges = new ArrayList<StyleRange>();
					final List<Note> referenceNotes = scaleResult.getReferenceNotes();
					final String notesMode = prefs.getString(Preferences.NOTES_MODE);
					final boolean compactMode = prefs.getBoolean(Preferences.SCALE_RESULTS_VIEW_COMPACT_MODE);

					for (final Iterator<Note> iter = scaleResult.getSortedNotes().iterator(); iter.hasNext();) {
						final Note note = iter.next();
						if (note == null) {
							continue;
						}
						final String noteName = notesMode.equals(Constants.NOTES_MODE_ONLY_CROSS) ? note
								.getRelativeNameAug() : notesMode.equals(Constants.NOTES_MODE_ONLY_B) ? note
								.getRelativeNameDim() : note.getRelativeName();
						buf.append(noteName);
						if (referenceNotes.contains(note) && markSearchAction.isChecked()) {
							searchStyleRanges.add(new StyleRange(start, noteName.length(), null,
									IFigureConstants.YELLOW));
						}
						if (iter.hasNext()) {
							buf.append(compactMode ? "-" : " - "); //$NON-NLS-1$ //$NON-NLS-2$
						}
						start = buf.length();
					}
					cell.setText(buf.toString());

					// gray separators
					if (prefs.getBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_SEPARATORS_IN_GRAY)) {
						final List<StyleRange> styleRanges = getStyleRanges(buf.toString());
						// gray separators + marked search
						if (markSearchAction.isChecked()) {
							styleRanges.addAll(searchStyleRanges);
						}
						setStyleRanges(styleRanges, cell);
					}

					// black separators + marked search
					else if (markSearchAction.isChecked()) {
						setStyleRanges(searchStyleRanges, cell);
					}

					// no styling: black separators + no marked search
					else {
						cell.setStyleRanges(new StyleRange[0]);
					}
				}
				super.update(cell);
			}
		});

		// root note
		rootNoteColumn = createTableColumn(viewer, ViewMessages.ScaleResultsView_ft,
				ViewMessages.ScaleResultsView_root_note, true, SWT.CENTER, ++columnNumber,
				Preferences.SCALE_RESULTS_VIEW_ROOT_NOTE_COLUMN_WIDTH);
		rootNoteColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof ScaleResult) {
					cell.setText(((ScaleResult) cell.getElement()).isRootNoteContained() ? ViewMessages.ScaleResultsView_yes
							: ViewMessages.ScaleResultsView_no);
				}
			}
		});

		// coverage
		coverageColumn = createTableColumn(viewer, ViewMessages.ScaleResultsView_coverage,
				ViewMessages.ScaleResultsView_coverage_in_percent, true, SWT.CENTER, ++columnNumber,
				Preferences.SCALE_RESULTS_VIEW_COVERAGE_COLUMN_WIDTH);
		coverageColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				if (cell.getElement() instanceof ScaleResult) {
					cell.setText(((ScaleResult) cell.getElement()).getCoverage() + "%"); //$NON-NLS-1$
				}
			}
		});
	}

	@Override
	protected void updateColumnVisibility() {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		setColumnVisible(intervalsColumn, prefs.getBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_INTERVALS_COLUMN),
				prefs.getInt(Preferences.SCALE_RESULTS_VIEW_INTERVALS_COLUMN_WIDTH));
		setColumnVisible(notesColumn, prefs.getBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_NOTES_COLUMN),
				prefs.getInt(Preferences.SCALE_RESULTS_VIEW_NOTES_COLUMN_WIDTH));
		setColumnVisible(rootNoteColumn, prefs.getBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_ROOT_NOTE_COLUMN),
				prefs.getInt(Preferences.SCALE_RESULTS_VIEW_ROOT_NOTE_COLUMN_WIDTH));
		setColumnVisible(coverageColumn, prefs.getBoolean(Preferences.SCALE_RESULTS_VIEW_SHOW_COVERAGE_COLUMN),
				prefs.getInt(Preferences.SCALE_RESULTS_VIEW_COVERAGE_COLUMN_WIDTH));
	}

	@Override
	protected void createAndRegisterContextActions() {
		// create actions
		openInEditorAction = new OpenScaleResultInEditorAction(this);
		copyToClipboardAction = new CopyBlocksToClipboardAction(this, true);
		playAction = new PlayScaleResultAction();
		addAction(openInEditorAction);
		addAction(copyToClipboardAction);
		addAction(playAction);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), openInEditorAction);
		activator.registerAction(getSite(), copyToClipboardAction);
		activator.registerAction(getSite(), playAction);
	}

	@Override
	protected void addToContextMenu(final IMenuManager mgr) {
		mgr.appendToGroup(TOP_GROUP, openInEditorAction);
		mgr.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, copyToClipboardAction);
		mgr.appendToGroup(SOUND_GROUP, playAction);
	}

	@Override
	protected String getCorrespondingPreferencePageId() {
		return ScaleResultsViewPreferencePage.ID;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		final String property = event.getProperty();

		// column visibility changed
		if (property.equals(Preferences.SCALE_RESULTS_VIEW_SHOW_INTERVALS_COLUMN)
				|| property.equals(Preferences.SCALE_RESULTS_VIEW_SHOW_NOTES_COLUMN)
				|| property.equals(Preferences.SCALE_RESULTS_VIEW_SHOW_ROOT_NOTE_COLUMN)
				|| property.equals(Preferences.SCALE_RESULTS_VIEW_SHOW_COVERAGE_COLUMN)) {
			updateColumnVisibility();
		}

		if (property.equals(Preferences.NOTES_MODE) || property.equals(Preferences.GENERAL_H_NOTE_NAME)
				|| property.equals(Preferences.GENERAL_B_NOTE_NAME)
				|| property.equals(Preferences.SCALE_RESULTS_VIEW_COMPACT_MODE)
				|| property.equals(Preferences.SCALE_RESULTS_VIEW_SHOW_SEPARATORS_IN_GRAY)
				|| property.equals(Preferences.SCALE_NAMES_USE_SEPARATOR)
				|| property.equals(Preferences.SCALE_NAMES_SEPARATOR)) {
			getViewer().refresh(true);
		}

		if (property.equals(Preferences.INTERVAL_NAMES_MODE)
				|| property.equals(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME)
				|| property.equals(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME)
				|| property.equals(Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS)) {
			if (!isEmpty()) {
				for (final Object result : getResults()) {
					if (result instanceof ScaleResult) {
						((ScaleResult) result).updateIntervalNames();
					}
				}
			}
			getViewer().refresh(true);
		}

		if (getResults().isEmpty()) {
			return;
		}

		// max results number changed
		if (property.equals(Preferences.CALCULATOR_MAX_RESULTS_NUMBER)) {
			final int newMaxNumber = Activator.getDefault().getPreferenceStore()
					.getInt(Preferences.CALCULATOR_MAX_RESULTS_NUMBER);
			if (getResults().size() > newMaxNumber) {
				setInput(getResults().subList(0, newMaxNumber));
			}
		}

		// entries per page changed
		if (property.equals(Preferences.SCALE_RESULTS_VIEW_ENTRIES_PER_PAGE)) {
			setInput(getResults());
		}
	}

	@Override
	protected String getHelpId() {
		return HELP_ID;
	}

	/* --- actions --- */

	/**
	 * Action to mark intervals and notes of search in corresponding columns.
	 */
	private class MarkSearchAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.markSearch"; //$NON-NLS-1$

		public MarkSearchAction() {
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.ScaleResultsView_mark_search);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.SEARCH_MARKER));
			setToolTipText(ViewMessages.ScaleResultsView_mark_search);
		}

		@Override
		public void run() {
			Activator.getDefault().getPreferenceStore()
					.setValue(Preferences.SCALE_RESULTS_VIEW_MARK_SEARCH, isChecked());
			getViewer().refresh(true);
		}

		@Override
		public int getStyle() {
			return AS_CHECK_BOX;
		}
	}
}
