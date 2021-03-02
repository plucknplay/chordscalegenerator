/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.OpenSpecificPreferencePageAction;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.util.DefaultCollectionContentProvider;
import com.plucknplay.csg.ui.util.MyFocusListener;
import com.plucknplay.csg.ui.util.StatusLineUtil;

public abstract class AbstractResultsView extends ViewPart implements IPropertyChangeListener,
		ISelectionChangedListener {

	protected static final String TOP_GROUP = "topGroup";
	protected static final String CENTER_GROUP = "centerGroup";
	protected static final String FIND_GROUP = "findGroup";
	protected static final String SOUND_GROUP = "soundGroup";
	protected static final String BOTTOM_GROUP = "bottomGroup";
	protected static final String END_GROUP = "endGroup";

	private boolean savePreferenceWidth;

	private TableViewer viewer;

	private List<?> results;
	private int activePage;
	private int lastPageNumber;

	private AbstractTableViewerSorter sorter;
	private int entriesPerPage;

	private PageDisplayContributionItem pageDisplay;
	private IAction pageBeginningAction;
	private IAction pageBackAction;
	private IAction pageForwardAction;
	private IAction pageEndAction;
	private OpenSpecificPreferencePageAction openPreferencesAction;

	private List<IViewSelectionAction> actions;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);

		results = new ArrayList<Object>();
		sorter = getResultsViewSorter();
		activePage = 0;
		lastPageNumber = 0;
		savePreferenceWidth = true;

		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);

		entriesPerPage = getEntriesPerPage();
	}

	protected abstract AbstractTableViewerSorter getResultsViewSorter();

	protected abstract int getEntriesPerPage();

	@Override
	public void createPartControl(final Composite parent) {

		// create table
		viewer = new TableViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		// add key listener to handle smooth scrolling beyond pages
		viewer.getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(final KeyEvent e) {
				keyPressedEvent(e);
			}
		});

		getSite().setSelectionProvider(viewer);

		defineTableViewer(viewer);
		createTableColumns(viewer);

		// content provider
		viewer.setContentProvider(new DefaultCollectionContentProvider());
		viewer.addSelectionChangedListener(this);
		viewer.getControl().addFocusListener(new MyFocusListener(this));

		updateColumnVisibility();

		createContextMenu();
		contributeToActionBars();
		updateActions();
		updateContentDescritpion();
		updateSortIndicator();

		// set context-sensitive help
		Activator.getDefault().setHelp(parent, getHelpId());
	}

	protected void keyPressedEvent(final KeyEvent e) {
		final int index = viewer.getTable().getSelectionIndex();
		if (index == entriesPerPage - 1 && activePage < lastPageNumber
				&& (e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.PAGE_DOWN)) {

			jumpToNextPage();
			selectFirstEntry();
			e.doit = false;
		} else if (index == 0 && activePage > 0 && (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.PAGE_UP)) {

			jumpToPreviousPage();
			selectLastEntry();
			e.doit = false;
		} else if (e.keyCode == SWT.HOME) {
			jumpToFirstPage();
			selectFirstEntry();
			e.doit = false;
		} else if (e.keyCode == SWT.END) {
			jumpToLastPage();
			selectLastEntry();
			e.doit = false;
		}
	}

	/**
	 * Returns the help id for this view.
	 * 
	 * @return the help id for this view
	 */
	protected abstract String getHelpId();

	protected abstract void defineTableViewer(TableViewer viewer);

	protected abstract void createTableColumns(TableViewer viewer);

	protected abstract void updateColumnVisibility();

	public TableViewer getViewer() {
		return viewer;
	}

	protected void setColumnVisible(final TableViewerColumn viewerColumn, final boolean visible, final int columnWidth) {
		savePreferenceWidth = false;
		viewerColumn.getColumn().setWidth(visible ? columnWidth : 0);
		viewerColumn.getColumn().setResizable(visible);
		savePreferenceWidth = true;
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
		StatusLineUtil.clearStatusLine(getViewSite());
	}

	public void setInput(final Collection<?> input) {
		entriesPerPage = getEntriesPerPage();
		results = new ArrayList<Object>(input);
		updateContentDescritpion();
		sortTable();
	}

	protected void updateContentDescritpion() {
		setContentDescription(NLS.bind(ViewMessages.AbstractGraphicalCalculationView_found_results, results.size()));
	}

	protected void sortTable() {

		final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		final Object selectedElement = selection.getFirstElement();

		Collections.sort(results, sorter);

		int currentIndex = selectedElement == null ? 0 : results.indexOf(selectedElement);
		if (currentIndex == -1) {
			currentIndex = 0;
		}
		activePage = currentIndex / entriesPerPage;
		lastPageNumber = (results.size() - 1) / entriesPerPage;

		updateActions();
		refreshViewer();
		updateSortIndicator();

		if (!results.isEmpty()) {
			viewer.setSelection(new StructuredSelection(results.get(currentIndex)), true);
		}
	}

	protected void updateSortIndicator() {
		viewer.getTable().setSortColumn(viewer.getTable().getColumn(sorter.getColumn()));
		viewer.getTable().setSortDirection(sorter.getDirection(sorter.getColumn()) == 1 ? SWT.UP : SWT.DOWN);
	}

	protected void refreshViewer() {
		final int from = entriesPerPage * activePage;
		final int to = activePage == lastPageNumber ? results.size() : from + entriesPerPage;
		final List<?> subList = results.subList(from, to);
		viewer.setInput(subList);
	}

	@Override
	public void dispose() {
		getSite().setSelectionProvider(null);
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		super.dispose();
	}

	protected TableViewerColumn createTableColumn(final TableViewer viewer, final String name, final String tooltip,
			final boolean resizable, final int alignment, final int columnNumber, final String widthPreferenceId) {

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, alignment);
		final TableColumn column = viewerColumn.getColumn();
		final Table table = viewer.getTable();
		column.setText(name);
		column.setToolTipText(tooltip);
		column.setWidth(prefs.getInt(widthPreferenceId));
		column.setResizable(resizable);
		column.setMoveable(false);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Cursor loadCursor = new Cursor(table.getDisplay(), SWT.CURSOR_WAIT);
				table.setCursor(loadCursor);
				sorter.setColumn(columnNumber);
				sortTable();
				table.setCursor(null);
				loadCursor.dispose();
			}
		});
		if (widthPreferenceId != null) {
			column.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(final ControlEvent e) {
					if (savePreferenceWidth) {
						prefs.setValue(widthPreferenceId, column.getWidth());
					}
				}
			});
		}
		return viewerColumn;
	}

	/**
	 * Creates the context menu.
	 */
	protected void createContextMenu() {

		createAndRegisterContextActions();

		// create menu manager
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager mgr) {
				mgr.add(new Separator(TOP_GROUP));
				mgr.add(new Separator(FIND_GROUP));
				mgr.add(new Separator(CENTER_GROUP));
				mgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				mgr.add(new Separator(SOUND_GROUP));
				addToContextMenu(mgr);
			}
		});

		// create menu
		final Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		// register menu for extension
		getSite().registerContextMenu(menuMgr, viewer);
	}

	protected abstract void createAndRegisterContextActions();

	protected abstract void addToContextMenu(IMenuManager mgr);

	protected void addAction(final IViewSelectionAction action) {
		if (actions == null) {
			actions = new ArrayList<IViewSelectionAction>();
		}
		actions.add(action);
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		if (event != null) {
			for (final IViewSelectionAction action : actions) {
				action.selectionChanged(event.getSelection());
			}
		}
	}

	/**
	 * Contribute to menu and toolbar.
	 */
	protected void contributeToActionBars() {

		// create actions
		pageDisplay = new PageDisplayContributionItem();
		pageBeginningAction = new PageBeginningAction();
		pageBackAction = new PageBackAction();
		pageForwardAction = new PageForwardAction();
		pageEndAction = new PageEndAction();
		openPreferencesAction = new OpenSpecificPreferencePageAction(getCorrespondingPreferencePageId());

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), pageBeginningAction);
		activator.registerAction(getSite(), pageBackAction);
		activator.registerAction(getSite(), pageForwardAction);
		activator.registerAction(getSite(), pageEndAction);
		activator.registerAction(getSite(), openPreferencesAction);

		// contribute actions
		final IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(new Separator(TOP_GROUP));
		bars.getMenuManager().add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		bars.getMenuManager().add(new Separator(BOTTOM_GROUP));
		bars.getMenuManager().appendToGroup(BOTTOM_GROUP, openPreferencesAction);

		bars.getToolBarManager().add(pageDisplay);
		bars.getToolBarManager().add(new Separator());
		bars.getToolBarManager().add(pageBeginningAction);
		bars.getToolBarManager().add(pageBackAction);
		bars.getToolBarManager().add(pageForwardAction);
		bars.getToolBarManager().add(pageEndAction);
		bars.getToolBarManager().add(new Separator(END_GROUP));
		bars.getToolBarManager().add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	protected abstract String getCorrespondingPreferencePageId();

	protected void updateActions() {
		pageBeginningAction.setEnabled(activePage > 0 && lastPageNumber > 0);
		pageBackAction.setEnabled(activePage > 0 && lastPageNumber > 0);
		pageForwardAction.setEnabled(activePage < lastPageNumber && lastPageNumber > 0);
		pageEndAction.setEnabled(activePage < lastPageNumber && lastPageNumber > 0);
		pageDisplay.updateText();
	}

	/**
	 * Returns the displayed results of this view.
	 * 
	 * @return the displayed results of this view
	 */
	public List<?> getResults() {
		return results;
	}

	/**
	 * Returns true if this view doen't show any results at the moment, false
	 * otherwise.
	 * 
	 * @return true if this view doen't show any results at the moment, false
	 *         otherwise
	 */
	public boolean isEmpty() {
		return results == null || results.isEmpty();
	}

	protected List<StyleRange> getStyleRanges(final String string) {
		final List<StyleRange> styleRanges = new ArrayList<StyleRange>();

		// style hyphen
		int startFrom = 0;
		while (string.indexOf("-", startFrom) > -1) {
			startFrom = string.indexOf("-", startFrom);
			styleRanges.add(new StyleRange(startFrom, 1, IFigureConstants.GREY, null));
			startFrom++;
		}

		return styleRanges;
	}

	protected void setStyleRanges(final List<StyleRange> styleRanges, final ViewerCell cell) {
		cell.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
	}

	/**
	 * Flushs this view.
	 */
	public void flush() {
		setInput(new ArrayList<Object>());
	}

	private void jumpToPreviousPage() {
		activePage--;
		updateViewer();
	}

	private void jumpToNextPage() {
		activePage++;
		updateViewer();
	}

	private void jumpToFirstPage() {
		activePage = 0;
		updateViewer();
	}

	private void jumpToLastPage() {
		activePage = lastPageNumber;
		updateViewer();
	}

	private void updateViewer() {
		updateActions();
		refreshViewer();
	}

	private void selectFirstEntry() {
		final Object firstElement = viewer.getElementAt(0);
		viewer.setSelection(new StructuredSelection(firstElement), true);
	}

	private void selectLastEntry() {
		final Object lastElement = viewer.getElementAt(viewer.getTable().getItemCount() - 1);
		viewer.setSelection(new StructuredSelection(lastElement), true);
	}

	/* --- actions --- */

	/**
	 * Action to step one page back.
	 */
	private class PageBackAction extends Action {

		private static final String COMMAND_ID = "org.eclipse.ui.part.previousPage"; //$NON-NLS-1$

		public PageBackAction() {
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.AbstractResultsView_previous_page);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.PAGE_BACK));
			setToolTipText(ViewMessages.AbstractResultsView_previous_page);
		}

		@Override
		public void run() {
			jumpToPreviousPage();
			selectFirstEntry();
		}
	}

	/**
	 * Action to step one page forward.
	 */
	private class PageForwardAction extends Action {

		private static final String COMMAND_ID = "org.eclipse.ui.part.nextPage"; //$NON-NLS-1$

		public PageForwardAction() {
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.AbstractResultsView_next_page);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.PAGE_FORWARD));
			setToolTipText(ViewMessages.AbstractResultsView_next_page);
		}

		@Override
		public void run() {
			jumpToNextPage();
			selectFirstEntry();
		}
	}

	/**
	 * Action to step to the beginning.
	 */
	private class PageBeginningAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.firstPage"; //$NON-NLS-1$

		public PageBeginningAction() {
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.AbstractResultsView_first_page);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.PAGE_BEGINNING));
			setToolTipText(ViewMessages.AbstractResultsView_first_page);
		}

		@Override
		public void run() {
			jumpToFirstPage();
			selectFirstEntry();
		}
	}

	/**
	 * Action to step to the end.
	 */
	private class PageEndAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.lastPage"; //$NON-NLS-1$

		public PageEndAction() {
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.AbstractResultsView_last_page);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.PAGE_END));
			setToolTipText(ViewMessages.AbstractResultsView_last_page);
		}

		@Override
		public void run() {
			jumpToLastPage();
			selectFirstEntry();
		}
	}

	/**
	 * Contribution item to display the active page number.
	 */
	private class PageDisplayContributionItem extends ContributionItem {

		private CLabel label;
		private ToolItem toolItem;

		@Override
		public void fill(final ToolBar parent, final int index) {
			toolItem = new ToolItem(parent, SWT.SEPARATOR);
			label = new CLabel(parent, SWT.RIGHT);
			toolItem.setWidth(200);
			toolItem.setControl(label);
			updateText();
		}

		public void updateText() {
			if (label != null && !label.isDisposed()) {
				label.setText(results.size() == 0 || results.size() < entriesPerPage ? "" : activePage + 1 + "/" + (lastPageNumber + 1) + "  "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}
}
