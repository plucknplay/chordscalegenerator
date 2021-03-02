/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.OpenSpecificPreferencePageAction;
import com.plucknplay.csg.ui.actions.chords.AbstractFindChordsAction;
import com.plucknplay.csg.ui.actions.common.CopyToClipboardAction;
import com.plucknplay.csg.ui.actions.common.ExportAsImageFileAction;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;
import com.plucknplay.csg.ui.actions.scales.AbstractFindScalesAction;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.editParts.AbstractDraftEditPart;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.listeners.ISimpleChangeListener;
import com.plucknplay.csg.ui.model.BlockManager;
import com.plucknplay.csg.ui.model.Draft;
import com.plucknplay.csg.ui.model.FretDraft;
import com.plucknplay.csg.ui.util.ButtonAction;
import com.plucknplay.csg.ui.util.LoginUtil;
import com.plucknplay.csg.ui.util.MyFocusListener;
import com.plucknplay.csg.ui.util.StatusLineUtil;

public abstract class AbstractGraphicalCalculationView extends ViewPart implements ISelectionListener, IChangeListener,
		ISimpleChangeListener, IPropertyChangeListener {

	protected static final String BAR_ADDITIONS_LEFT = "additions_left"; //$NON-NLS-1$
	protected static final String BAR_ADDITIONS_RIGHT = "additions_right"; //$NON-NLS-1$

	protected static final String POPUP_TOP_GROUP = "topGroup"; //$NON-NLS-1$
	protected static final String POPUP_CENTER_GROUP = "centerGroup"; //$NON-NLS-1$
	protected static final String POPUP_BOTTOM_GROUP = "bottomGroup"; //$NON-NLS-1$

	protected static final Point BUTTON_SIZE_SMALL = new Point(22, 22);
	protected static final Point BUTTON_SIZE_BIG = new Point(30, 30);
	protected static final int VERTICAL_INDENT = 10;

	private Point buttonMode = BUTTON_SIZE_BIG;
	private boolean indent = true;

	private Composite composite;
	private Composite buttonsComposite;
	private ButtonAction findChordsButtonAction;
	private ButtonAction findScalesButtonAction;
	private ButtonAction changeRootNoteButtonAction;
	private ButtonAction noBarreButtonAction;
	private Label separatorLabel;

	/**
	 * The edit domain of this view.
	 * 
	 * <p>
	 * An edit domain logically bundles an editor, viewers and tools. Therefore
	 * it defines the real editor application. Besides the edit domain provides
	 * the command stack which keeps track of all executed commands.
	 * </p>
	 */
	private EditDomain editDomain;

	/**
	 * The graphical viewer of this editor.
	 * 
	 * <p>
	 * A graphical viewer provides a seamless integration of edit parts into the
	 * eclipse workbench.
	 * </p>
	 */
	private GraphicalViewer graphicalViewer;

	private final ControlAdapter graphicalViewerControlListener = new ControlAdapter() {
		@Override
		public void controlResized(final ControlEvent e) {
			preControlResized();
			final EditPart contents = graphicalViewer.getContents();
			if (contents != null) {
				contents.refresh();
				if (contents instanceof AbstractDraftEditPart) {
					((AbstractDraftEditPart) contents).bringDownEditManager();
				}
			}
		}
	};

	private final ControlAdapter buttonCompositeControlListener = new ControlAdapter() {
		@Override
		public void controlResized(final ControlEvent e) {
			updateButtonSizes();
		}
	};

	private final IPerspectiveListener perspectiveListener = new IPerspectiveListener() {
		@Override
		public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
			if (Activator.getDefault().getPreferenceStore().getBoolean(Preferences.PERSPECTIVES_CLEAR_SELECTION)) {
				setInput(null);
			}
		}

		@Override
		public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
				final String changeId) {
			// do nothing
		}
	};

	private KeyListener escKeyListener;

	private Object input;
	private Draft content;

	private IViewSite site;
	private Instrument currentInstrument;

	private SearchModeAction enableSearchAction;
	private ClearInputAction clearInputAction;
	private OpenSpecificPreferencePageAction openPreferencesAction;

	private List<IViewSelectionAction> actions;

	private Draft tempDraft;

	private final MouseAdapter mouseDoubleClickListener = new MouseAdapter() {
		@Override
		public void mouseDoubleClick(final MouseEvent e) {
			if (!getSearchMode()
					&& Activator.getDefault().getPreferenceStore()
							.getBoolean(Preferences.VIEWS_SEARCH_MODE_ENABLE_DOUBLE_CLICK)) {
				enableSearchAction.setChecked(true);
				setSearchMode(true);
			}
		}
	};

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);

		this.site = site;
		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();

		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		InstrumentList.getInstance().addChangeListener(this);
		ChordList.getInstance().addChangeListener(this);
		ScaleList.getInstance().addChangeListener(this);
		Activator.getDefault().addSimpleChangeListener(this);

		getSite().getWorkbenchWindow().addPerspectiveListener(perspectiveListener);
	}

	@Override
	public void createPartControl(final Composite parent) {

		composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).spacing(0, 0).margins(0, 0).applyTo(composite);

		// create search mode button
		buttonsComposite = new Composite(composite, SWT.NONE);
		buttonsComposite.addControlListener(buttonCompositeControlListener);
		buttonsComposite.setBackground(IFigureConstants.TOOLTIP_YELLOW);
		buttonsComposite.setVisible(false);
		buttonsComposite.addKeyListener(getEscKeyListener());
		GridLayoutFactory.fillDefaults().numColumns(1).spacing(0, 0).margins(10, 10).applyTo(buttonsComposite);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(buttonsComposite);
		((GridData) buttonsComposite.getLayoutData()).exclude = true;

		// fill buttons composite
		fillButtonsComposite(buttonsComposite);

		// create separator between search mode buttons and graphical viewer
		separatorLabel = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		separatorLabel.setVisible(false);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(separatorLabel);
		((GridData) separatorLabel.getLayoutData()).exclude = true;

		// create graphical viewer
		graphicalViewer = createGraphicalViewer(composite);
		graphicalViewer.getControl().addControlListener(graphicalViewerControlListener);
		graphicalViewer.getControl().addMouseListener(mouseDoubleClickListener);
		graphicalViewer.getControl().addKeyListener(getEscKeyListener());
		graphicalViewer.getControl().addFocusListener(new MyFocusListener(this));

		GridDataFactory.fillDefaults().grab(true, true).applyTo(graphicalViewer.getControl());

		// last initializations
		getSite().getPage().addSelectionListener(this);
		contributeToActionBars();
		createContextMenu();

		// set context-sensitive help
		Activator.getDefault().setHelp(parent, getHelpId());
	}

	protected void fillButtonsComposite(final Composite composite) {

		// find chords button
		findChordsButtonAction = new ButtonAction(getSite(), composite, SWT.PUSH | SWT.FLAT,
				ActionMessages.FindChordsAction_text, IImageKeys.FIND_CHORDS, "com.plucknplay.csg.ui.findChords") { //$NON-NLS-1$
			@Override
			public void run() {
				enableSearchAction.setChecked(false);
				setSearchMode(false);
				AbstractFindChordsAction.run(getSite(), getContent().getGriptables());
			};
		};
		findChordsButtonAction.getButton().addKeyListener(getEscKeyListener());
		GridDataFactory.fillDefaults().hint(BUTTON_SIZE_BIG).applyTo(findChordsButtonAction.getButton());

		// find scales button
		findScalesButtonAction = new ButtonAction(getSite(), composite, SWT.PUSH | SWT.FLAT,
				ActionMessages.FindScalesAction_text, IImageKeys.FIND_SCALES, "com.plucknplay.csg.ui.findScales") { //$NON-NLS-1$
			@Override
			public void run() {
				enableSearchAction.setChecked(false);
				setSearchMode(false);
				AbstractFindScalesAction.run(getSite(), getContent().getRelativeNotes());
			}
		};
		findScalesButtonAction.getButton().addKeyListener(getEscKeyListener());
		GridDataFactory.fillDefaults().hint(BUTTON_SIZE_BIG).applyTo(findScalesButtonAction.getButton());
	}

	protected void updateButtonSizes() {

		if (!getSearchMode() || buttonsComposite == null || buttonsComposite.isDisposed()) {
			return;
		}
		final Control[] children = buttonsComposite.getChildren();
		if (children.length <= 0) {
			return;
		}

		// determine last visible child
		Control lastChild = null;
		int numberOfVisibleChildren = 0;
		for (int i = children.length - 1; i >= 0; i--) {
			if (children[i].isVisible()) {
				if (lastChild == null) {
					lastChild = children[i];
				}
				numberOfVisibleChildren++;
			}
		}
		if (lastChild == null) {
			return;
		}

		// determine heights
		final int offset = 12;
		final int smallWithoutIndent = offset + numberOfVisibleChildren * BUTTON_SIZE_SMALL.x;
		final int smallWithIndent = smallWithoutIndent + getButtonsWithIndent().length * VERTICAL_INDENT;
		final int bigWithoutIndent = offset + numberOfVisibleChildren * BUTTON_SIZE_BIG.x;
		final int bigWithIndent = bigWithoutIndent + getButtonsWithIndent().length * VERTICAL_INDENT;
		final int compositeHeight = buttonsComposite.getSize().y;

		// determine possible new button size
		Point newButtonMode = BUTTON_SIZE_SMALL;
		boolean newIndent = false;

		if (compositeHeight > bigWithIndent) {
			newButtonMode = BUTTON_SIZE_BIG;
			newIndent = true;
		} else if (compositeHeight > bigWithoutIndent) {
			newButtonMode = BUTTON_SIZE_BIG;
		} else if (compositeHeight > smallWithIndent) {
			newIndent = true;
		}

		// update buttons (if necessary)
		if (buttonMode != newButtonMode || indent != newIndent) {
			buttonMode = newButtonMode;
			indent = newIndent;

			for (final Control control : children) {
				final Object layoutData = control.getLayoutData();
				if (layoutData instanceof GridData) {
					final GridData gridData = (GridData) layoutData;
					gridData.widthHint = newButtonMode.x;
					gridData.heightHint = newButtonMode.y;
					gridData.verticalIndent = newIndent && isButtonWithIndent(control) ? VERTICAL_INDENT : 0;
				}
			}
		}

		buttonsComposite.layout(true, true);
		composite.layout(true, true);
	}

	private boolean isButtonWithIndent(final Control control) {
		for (final Button button : getButtonsWithIndent()) {
			if (control == button) {
				return true;
			}
		}
		return false;
	}

	protected abstract Button[] getButtonsWithIndent();

	protected ButtonAction createChangeRootNoteAction(final Composite composite) {

		changeRootNoteButtonAction = new ButtonAction(getSite(), composite, SWT.PUSH | SWT.FLAT,
				ViewMessages.AbstractGraphicalCalculationView_change_root_note, null,
				"com.plucknplay.csg.ui.changeRootNote") { //$NON-NLS-1$
			@Override
			public void run() {
				getContent().setRootNote(true);
			}
		};
		changeRootNoteButtonAction.getButton().addKeyListener(getEscKeyListener());
		GridDataFactory.fillDefaults().indent(0, VERTICAL_INDENT).hint(BUTTON_SIZE_BIG)
				.applyTo(changeRootNoteButtonAction.getButton());

		updateChangeRootNoteImage();
		updateChangeRootNoteButton();

		return changeRootNoteButtonAction;
	}

	private void updateChangeRootNoteImage() {
		if (changeRootNoteButtonAction == null) {
			return;
		}

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		String rootIntervalName = "1"; //$NON-NLS-1$
		if (prefs.getBoolean(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME)) {
			rootIntervalName = prefs.getString(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME);
		}
		changeRootNoteButtonAction.setImagePath("R".equals(rootIntervalName) ? IImageKeys.ROOT_NOTE_R //$NON-NLS-1$
				: "U".equals(rootIntervalName) ? IImageKeys.ROOT_NOTE_U //$NON-NLS-1$
						: IImageKeys.ROOT_NOTE_1);
	}

	protected void updateChangeRootNoteButton() {
		if (changeRootNoteButtonAction != null) {
			changeRootNoteButtonAction.setEnabled(getContent().getRelativeNotes().size() > 1);
		}
	}

	protected ButtonAction createNoBarreAction(final Composite composite) {

		noBarreButtonAction = new ButtonAction(getSite(), composite, SWT.TOGGLE | SWT.FLAT,
				ViewMessages.BoxView_no_barre, IImageKeys.NO_BARRE, "com.plucknplay.csg.ui.noBarre") { //$NON-NLS-1$
			@Override
			public void run() {
				super.run();
				if (getContent() != null && getContent() instanceof FretDraft) {
					((FretDraft) getContent()).setShowBarre(!noBarreButtonAction.getButton().getSelection());
				}
			}
		};
		noBarreButtonAction.getButton().addKeyListener(getEscKeyListener());
		GridDataFactory.fillDefaults().hint(BUTTON_SIZE_BIG).applyTo(noBarreButtonAction.getButton());
		updateShowBarreButton();

		return noBarreButtonAction;
	}

	protected void updateShowBarreButton() {
		if (getContent() != null && getContent() instanceof FretDraft) {
			final FretDraft fretDraft = (FretDraft) getContent();
			noBarreButtonAction.getButton().setVisible(
					Activator.getDefault().getPreferenceStore().getBoolean(Preferences.BOX_VIEW_SHOW_BARRE));
			noBarreButtonAction.getButton().setSelection(!fretDraft.getShowBarre());
			noBarreButtonAction.setEnabled(fretDraft.isPotentialGriptable());
			updateButtonSizes();
		}
	}

	protected final KeyListener getEscKeyListener() {
		if (escKeyListener == null) {
			escKeyListener = new KeyAdapter() {
				@Override
				public void keyReleased(final KeyEvent e) {
					if (getSearchMode()
							&& e.character == SWT.ESC
							&& Activator.getDefault().getPreferenceStore()
									.getBoolean(Preferences.VIEWS_SEARCH_MODE_ENABLE_ESC_KEY)) {
						enableSearchAction.setChecked(false);
						setSearchMode(false);
					}
				}
			};
		}
		return escKeyListener;
	}

	/**
	 * Returns the help id for this view.
	 * 
	 * @return the help id for this view
	 */
	protected abstract String getHelpId();

	/**
	 * Creates a new graphical viewer, configures, registers and initializes it.
	 * 
	 * @param parent
	 *            the parent composite
	 * 
	 * @return a new graphical viewer
	 */
	private GraphicalViewer createGraphicalViewer(final Composite parent) {

		// create graphical viewer
		final GraphicalViewer viewer = new ScrollingGraphicalViewer();
		viewer.createControl(parent);

		// configure the viewer
		viewer.getControl().setBackground(ColorConstants.white);
		viewer.setRootEditPart(new ScalableRootEditPart());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

		// hook the viewer into the EditDomain
		getEditDomain().addViewer(viewer);
		final ToolEntry toolEntry = new SelectionToolEntry();
		getEditDomain().setDefaultTool(toolEntry.createTool());

		// initialize the viewer with input
		viewer.setEditPartFactory(getEditPartFactory());
		viewer.setContents(getContent());

		// drag support
		viewer.addDragSourceListener(new TransferDragSourceListener() {
			@Override
			public Transfer getTransfer() {
				return FileTransfer.getInstance();
			}

			@Override
			public void dragFinished(final DragSourceEvent event) {
				// do nothing
			}

			@Override
			public void dragSetData(final DragSourceEvent event) {
				final CopyToClipboardAction action = new CopyToClipboardAction(AbstractGraphicalCalculationView.this,
						false);
				action.run();
				final String[] data = new String[1];
				data[0] = action.getFileName();
				event.data = action.getData();
			}

			@Override
			public void dragStart(final DragSourceEvent event) {
				event.doit = InstrumentList.getInstance().getCurrentInstrument() != null;
			}
		});

		return viewer;
	}

	/**
	 * Returns the edit part factory this view uses to create its edit parts.
	 * 
	 * @return the edit part factory this view uses to create its edit parts
	 */
	protected abstract EditPartFactory getEditPartFactory();

	/**
	 * Returns the graphical viewer of this editor.
	 * 
	 * @return the graphical viewer of this editor
	 */
	public GraphicalViewer getGraphicalViewer() {
		return graphicalViewer;
	}

	public ScalableLayeredPane getScalableLayeredPane() {
		final AbstractGraphicalEditPart editPart = (AbstractGraphicalEditPart) getGraphicalViewer().getContents();
		final IFigure theFigure = editPart.getFigure();
		final IFigure first = (IFigure) theFigure.getChildren().get(0);
		return first instanceof ScalableLayeredPane ? (ScalableLayeredPane) first : null;
	}

	public int getNormWidth() {
		final EditPart editPart = getGraphicalViewer().getContents();
		return editPart instanceof AbstractDraftEditPart ? ((AbstractDraftEditPart) editPart).getNormWidth() : 0;
	}

	public int getNormHeight() {
		final EditPart editPart = getGraphicalViewer().getContents();
		return editPart instanceof AbstractDraftEditPart ? ((AbstractDraftEditPart) editPart).getNormHeight() : 0;
	}

	public abstract int getExportHeight();

	protected void preControlResized() {
	}

	/**
	 * Refreshs this view.
	 */
	protected void refresh() {
		if (graphicalViewer != null && graphicalViewer.getContents() != null) {
			graphicalViewer.getContents().refresh();
		}
	}

	/**
	 * Returns the edit domain used by this editor.
	 * 
	 * <p>
	 * An edit domain logically bundles an editor, viewers and tools. Therefore
	 * it defines the real editor application. Besides the edit domain provides
	 * the command stack which keeps track of all executed commands.
	 * </p>
	 * 
	 * @return the edit domain used by this editor
	 */
	public EditDomain getEditDomain() {
		if (editDomain == null) {
			editDomain = new EditDomain();
		}
		return editDomain;
	}

	/**
	 * Contributes to the action bars. This method is intended to be extended
	 * via subclassing since there are already defined the following
	 * GroupMarkers: BAR_ADDITIONS_LEFT, BAR_ADDITIONS_RIGHT and the default one
	 * IWorkbenchActionConstants.MB_ADDITIONS.
	 */
	protected void contributeToActionBars() {

		// create actions
		enableSearchAction = new SearchModeAction();
		enableSearchAction.setChecked(false);

		clearInputAction = new ClearInputAction();
		clearInputAction.setEnabled(input != null);

		openPreferencesAction = new OpenSpecificPreferencePageAction(getPreferencePageID());

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), enableSearchAction);
		activator.registerAction(getSite(), clearInputAction);
		activator.registerAction(getSite(), openPreferencesAction);

		// add action to menu
		final IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(new Separator(BAR_ADDITIONS_LEFT));
		bars.getMenuManager().add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		bars.getMenuManager().add(new Separator(BAR_ADDITIONS_RIGHT));

		bars.getToolBarManager().add(new Separator(BAR_ADDITIONS_LEFT));
		bars.getToolBarManager().add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		bars.getToolBarManager().add(new Separator(BAR_ADDITIONS_RIGHT));

		// add actions to action bars
		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, enableSearchAction);
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, enableSearchAction);

		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, clearInputAction);
		bars.getToolBarManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, clearInputAction);

		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_RIGHT, openPreferencesAction);
	}

	/**
	 * Creates the context menu.
	 */
	private void createContextMenu() {
		// create actions
		final ExportAsImageFileAction exportAsImageAction = new ExportAsImageFileAction(this);
		final CopyToClipboardAction copyToClipboardAction = new CopyToClipboardAction(this);
		addAction(exportAsImageAction);
		addAction(copyToClipboardAction);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), exportAsImageAction);
		activator.registerAction(getSite(), copyToClipboardAction);

		// create menu manager
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager mgr) {
				mgr.add(new Separator(POPUP_TOP_GROUP));
				mgr.add(new Separator(POPUP_CENTER_GROUP));
				mgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				mgr.add(new Separator(POPUP_BOTTOM_GROUP));

				// add actions
				mgr.appendToGroup(POPUP_TOP_GROUP, exportAsImageAction);
				mgr.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, copyToClipboardAction);
			}
		});

		// create menu
		final Menu menu = menuMgr.createContextMenu(getGraphicalViewer().getControl());
		getGraphicalViewer().getControl().setMenu(menu);

		// register menu for extension
		getSite().registerContextMenu(menuMgr, getGraphicalViewer());
	}

	/**
	 * Returns the id of the views preference page.
	 * 
	 * @return the id of the views preference page
	 */
	protected abstract String getPreferencePageID();

	@Override
	public void setFocus() {
		composite.setFocus();
		updateFindButtons();
		updateClearAction();
		updateContentDescription(true);
		StatusLineUtil.clearStatusLine(getViewSite());
	}

	/**
	 * Sets the input for this view.
	 * 
	 * @param input
	 *            the new input, must be a block
	 */
	protected void setInput(final Object input) {
		if (input != null && !(input instanceof IBlock)) {
			throw new IllegalArgumentException();
		}

		this.input = input;

		getContent().removeSimpleChangeListener(this);
		content = createDraftContent(input);
		getContent().addSimpleChangeListener(this);
		getContent().setEditable(enableSearchAction.isChecked());
		graphicalViewer.setContents(getContent());

		updateFindButtons();
		updateChangeRootNoteButton();
		updateClearAction();

		tempDraft = createContentClone();
		updateContentDescription(true);
	}

	protected void updateContentDescription(final boolean isSimilar) {

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final String notesMode = prefs.getString(Preferences.NOTES_MODE);
		if (!isSimilar || getContent().isEditable() || getInput() == null
				|| !prefs.getBoolean(Preferences.VIEWS_SHOW_INFO_INPUT)) {
			showSearchModeInfo();
		} else if (getInput() instanceof Griptable) {
			setContentDescription(((Griptable) getInput()).getBeautifiedName(notesMode));
		} else if (getInput() instanceof Block) {
			if (showBlockInfo()) {
				setContentDescription(((Block) getInput()).getBeautifiedName(notesMode));
			} else {
				setContentDescription(((Block) getInput()).getIntervalContainer().getBeautifiedName(
						prefs.getString(Preferences.NOTES_MODE)));
			}
		}
		refresh();
	}

	protected boolean showBlockInfo() {
		return showBlocks() && getContent() != null && !getContent().isModifiedInput();
	}

	protected final boolean showBlocks() {
		return Activator.getDefault().getPreferenceStore().getBoolean(Preferences.SHOW_BLOCKS);
	}

	public Object getExportInput() {
		return getContent().isEditable() ? null : !showBlockInfo() && getInput() != null
				&& getInput() instanceof IBlock ? ((IBlock) getInput()).getIntervalContainer() : getInput();
	}

	private void showSearchModeInfo() {
		String text = ""; // //$NON-NLS-1$
		if (Activator.getDefault().getPreferenceStore().getBoolean(Preferences.VIEWS_SHOW_INFO_SEARCH_MODE)) {
			text = getContent().isEditable() ? ViewMessages.AbstractGraphicalCalculationView_search_mode_active_info
					: ViewMessages.AbstractGraphicalCalculationView_search_mode_inactive_info;
		}
		setContentDescription(text);
		refresh();
	}

	/**
	 * Returns the root content of the graphical viewer of this view.
	 * 
	 * @return the root content of the graphical viewer of this view
	 */
	protected Draft getContent() {
		if (content == null) {
			content = createDraftContent(null);
			content.addSimpleChangeListener(this);
		}
		return content;
	}

	protected abstract Draft createContentClone();

	/**
	 * Creates the (draft) content of this view.
	 * 
	 * @param input
	 *            the input which drives the content creation
	 * 
	 * @return the (draft) content of this view
	 */
	protected abstract Draft createDraftContent(Object input);

	/**
	 * Returns the input of this view.
	 * 
	 * @return the input of this view
	 */
	public Object getInput() {
		return input;
	}

	protected void addAction(final IViewSelectionAction action) {
		if (actions == null) {
			actions = new ArrayList<IViewSelectionAction>();
		}
		actions.add(action);
	}

	@Override
	public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
		if (enableSearchAction.isChecked()) {
			return;
		}
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structured = (IStructuredSelection) selection;
			final Object first = structured.getFirstElement();
			if (first != null && first instanceof IntervalContainer) {
				setInput(BlockManager.getInstance().getDefaultBlock((IntervalContainer) first));
			} else if (first != null && first instanceof IBlock) {
				setInput(first);
			}
		}
		for (final IViewSelectionAction action : actions) {
			action.selectionChanged(selection);
		}
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {
		// repaint all when the current instrument changes
		if (property == InstrumentList.PROP_CURRENT_INSTRUMENT_CHANGED
				|| property == InstrumentList.PROP_CHANGED_ELEMENT && source == currentInstrument) {
			currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
			setInput(null);

			if (currentInstrument == null) {
				enableSearchAction.setChecked(false);
				enableSearchAction.setEnabled(false);
				setSearchMode(false);
			} else {
				enableSearchAction.setEnabled(true);
			}
		}

		// update find buttons if the chord or scale list are changed
		if (property == ChordList.PROP_ADDED || property == ChordList.PROP_CHANGED_WHOLE_LIST
				|| property == ChordList.PROP_REMOVED || property == ScaleList.PROP_ADDED
				|| property == ScaleList.PROP_CHANGED_WHOLE_LIST || property == ScaleList.PROP_REMOVED) {
			updateFindButtons();
		}

		// set input to null if selected input was removed
		if ((property == ScaleList.PROP_REMOVED || property == ChordList.PROP_REMOVED) && getInput() != null
				&& getInput() instanceof Block && ((Block) getInput()).getIntervalContainer() == source) {
			setInput(null);
		}
	}

	@Override
	public void notifyChange(final Object property, final Object value) {
		// set input to null if chord results view has been flushed
		if (property == Activator.PROP_RESULTS_FLUSHED && input instanceof Griptable) {
			setInput(null);
		}

		if (property == Draft.PROP_ROOT_NOTE_CHANGED) {
			updateChangeRootNoteButton();
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {

		// block preferences changed
		if (event.getProperty().equals(Preferences.BLOCK_MODE)
				|| event.getProperty().equals(Preferences.FRET_BLOCK_RANGE)
				|| event.getProperty().equals(Preferences.FRET_BLOCK_USE_EMPTY_STRINGS)
				|| event.getProperty().equals(Preferences.ADVANCED_FRET_BLOCK_RANGE)
				|| event.getProperty().equals(Preferences.ADVANCED_FRET_BLOCK_STRING_RANGE_DECREASE)
				|| event.getProperty().equals(Preferences.ADVANCED_FRET_BLOCK_USE_EMPTY_STRINGS)
				|| event.getProperty().equals(Preferences.OCTAVE_BLOCK_ONLY_ROOT_NOTES)) {

			if (getInput() instanceof Block) {
				setInput(BlockManager.getInstance().getDefaultBlock(((Block) getInput()).getIntervalContainer()));
			}
		}

		// interval names changed
		else if (event.getProperty().equals(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME)
				|| event.getProperty().equals(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME)) {
			getContent().updateIntervalNames();
			updateChangeRootNoteImage();
		} else if (event.getProperty().equals(Preferences.INTERVAL_NAMES_MODE)
				|| event.getProperty().equals(Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS)) {
			getContent().updateIntervalNames();
		}

		// root note has changed
		else if (event.getProperty().equals(Preferences.ROOT_NOTE) && getInput() != null && getInput() instanceof Block) {
			setInput(BlockManager.getInstance().getDefaultBlock(((Block) getInput()).getIntervalContainer()));
		}

		// graphics preferences changed
		else if (event.getProperty().equals(Preferences.GRAPHICS_ANTI_ALIASING_MODE)
				|| event.getProperty().equals(Preferences.GRAPHICS_TEXT_ANTI_ALIASING_MODE)
				|| event.getProperty().equals(Preferences.GRAPHICS_INTERPOLATION_MODE)) {
			refresh();
		}

		// content description preferences changed
		else if (event.getProperty().equals(Preferences.VIEWS_SHOW_INFO_SEARCH_MODE)
				|| event.getProperty().equals(Preferences.VIEWS_SHOW_INFO_INPUT)
				|| event.getProperty().equals(Preferences.GENERAL_H_NOTE_NAME)
				|| event.getProperty().equals(Preferences.GENERAL_B_NOTE_NAME)
				|| event.getProperty().equals(Preferences.NOTES_MODE)
				|| event.getProperty().equals(Preferences.SCALE_NAMES_USE_SEPARATOR)
				|| event.getProperty().equals(Preferences.SCALE_NAMES_SEPARATOR)
				|| event.getProperty().equals(Preferences.CHORD_NAMES_USE_SEPARATOR)
				|| event.getProperty().equals(Preferences.CHORD_NAMES_SEPARATOR)
				|| event.getProperty().equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_SHORT_MODE)
				|| event.getProperty().equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_PREFIX_MODE)
				|| event.getProperty().equals(
						Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_PREFIX_AND_INTERVALS)
				|| event.getProperty().equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BLANK_SPACE_BETWEEN_INTERVALS)
				|| event.getProperty().equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_COMPACT_MODE)
				|| event.getProperty().equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_IN_BRACKETS)
				|| event.getProperty().equals(Preferences.CHORD_NAMES_EXCLUDED_INTERVALS_BRACKETS_MODE)) {
			updateContentDescription(true);
		}

		else if (event.getProperty().equals(Preferences.FRET_NUMBER)
				|| event.getProperty().equals(Preferences.CAPO_FRET)) {

			if (getInput() == null) {
				setInput(null);
			} else if (getContent().isEditable()) {
				setInput(null);
			} else if (getInput() instanceof Block) {
				setInput(BlockManager.getInstance().getDefaultBlock(((Block) getInput()).getIntervalContainer()));
			} else if (getInput() instanceof Griptable) {
				final Griptable griptable = (Griptable) getInput();
				if (griptable.getMaxFret() > Instrument.getFretNumber() || griptable.hasEmptyStringNotes()
						&& Instrument.getCapoFret() > 0 || griptable.getMinFret(false) <= Instrument.getCapoFret()) {
					setInput(null);
				} else {
					setInput(getInput());
				}
			} else {
				setInput(null);
			}
		}
	}

	@Override
	public void dispose() {
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		InstrumentList.getInstance().removeChangeListener(this);
		ChordList.getInstance().removeChangeListener(this);
		ScaleList.getInstance().removeChangeListener(this);
		Activator.getDefault().removeSimpleChangeListener(this);
		if (graphicalViewer != null && graphicalViewer.getControl() != null
				&& !graphicalViewer.getControl().isDisposed()) {
			graphicalViewer.getControl().removeControlListener(graphicalViewerControlListener);
		}
		if (buttonsComposite != null && !buttonsComposite.isDisposed()) {
			buttonsComposite.removeControlListener(buttonCompositeControlListener);
		}
		if (findChordsButtonAction != null) {
			findChordsButtonAction.dispose();
		}
		if (findScalesButtonAction != null) {
			findScalesButtonAction.dispose();
		}
		if (changeRootNoteButtonAction != null) {
			changeRootNoteButtonAction.dispose();
		}
		if (noBarreButtonAction != null) {
			noBarreButtonAction.dispose();
		}
		getSite().getPage().removeSelectionListener(this);
		getSite().getWorkbenchWindow().removePerspectiveListener(perspectiveListener);
		super.dispose();
	}

	protected boolean setSearchMode(final boolean searchMode) {

		if (!LoginUtil.isActivated()) {
			LoginUtil.showUnsupportedFeatureInformation(getSite().getShell());
			enableSearchAction.setChecked(false);
			return false;
		}

		buttonsComposite.setVisible(searchMode);
		separatorLabel.setVisible(searchMode);
		((GridData) buttonsComposite.getLayoutData()).exclude = !searchMode;
		((GridData) separatorLabel.getLayoutData()).exclude = !searchMode;

		if (searchMode) {
			graphicalViewer.getControl().removeMouseListener(mouseDoubleClickListener);
		} else {
			graphicalViewer.getControl().addMouseListener(mouseDoubleClickListener);
		}
		getContent().setEditable(searchMode);
		graphicalViewer.getContents().refresh();

		final Color backgroundColor = searchMode ? IFigureConstants.TOOLTIP_YELLOW : ColorConstants.white;
		graphicalViewer.getControl().setBackground(backgroundColor);

		updateFindButtons();
		updateClearAction();
		updateContentDescription(content != null && tempDraft != null && content.isSimilar(tempDraft));
		updateChangeRootNoteButton();
		updateButtonSizes();

		composite.layout(true, true);

		return true;
	};

	protected boolean getSearchMode() {
		return enableSearchAction == null ? false : enableSearchAction.isChecked();
	}

	protected void checkFastEditing() {
		if (getSearchMode()
				&& Activator.getDefault().getPreferenceStore()
						.getBoolean(Preferences.VIEWS_SEARCH_MODE_ENABLE_FAST_EDITING)
				&& getGraphicalViewer().getContents() instanceof AbstractDraftEditPart) {
			((AbstractDraftEditPart) getGraphicalViewer().getContents()).setEditManager();
		}
	}

	protected boolean usePointsMode() {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean usePointsMode = prefs.getBoolean(Preferences.VIEWS_SEARCH_MODE_USE_POINTS_MODE);
		final boolean useAlwaysPointsMode = prefs.getBoolean(Preferences.VIEWS_SEARCH_MODE_USE_ALWAYS_POINTS_MODE);
		return usePointsMode && (useAlwaysPointsMode || !useAlwaysPointsMode && getContent().isEmpty());
	}

	protected void updateFindButtons() {

		// update find actions enablement
		findChordsButtonAction.setEnabled(currentInstrument != null && getContent().isEditable()
				&& !ChordList.getInstance().getRootCategory().isEmpty() && getContent().isPotentialGriptable()
				&& getContent().getRelativeNotes().size() >= 2);
		findScalesButtonAction.setEnabled(currentInstrument != null && getContent().isEditable()
				&& !ScaleList.getInstance().getRootCategory().isEmpty() && getContent().getRelativeNotes().size() >= 2);

		// update error message
		String errorMessage = null;
		if (getContent().isEditable()) {
			if (currentInstrument == null) {
				errorMessage = ViewMessages.AbstractGraphicalCalculationView_error_msg_1;
			}
			if (currentInstrument != null && ChordList.getInstance().getRootCategory().isEmpty()) {
				errorMessage = ViewMessages.AbstractGraphicalCalculationView_error_msg_2;
			}
			if (currentInstrument != null && ScaleList.getInstance().getRootCategory().isEmpty()) {
				errorMessage = ViewMessages.AbstractGraphicalCalculationView_error_msg_3;
			}
			if (currentInstrument != null && ChordList.getInstance().getRootCategory().isEmpty()
					&& ScaleList.getInstance().getRootCategory().isEmpty()) {
				errorMessage = ViewMessages.AbstractGraphicalCalculationView_error_msg_4;
			}
		}
		site.getActionBars().getStatusLineManager().setErrorMessage(errorMessage);
	}

	protected void updateClearAction() {
		clearInputAction.setEnabled(!getContent().isEmpty());
	}

	/* --- actions --- */

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
			setInput(null);
		}
	}

	/**
	 * Action to enable/disable the search mode of this view.
	 */
	private class SearchModeAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.searchMode"; //$NON-NLS-1$

		public SearchModeAction() {
			setActionDefinitionId(COMMAND_ID);
			setText(NlsUtil.getAction_search_mode());
			setToolTipText(NlsUtil.getAction_search_mode());
			setImageDescriptor(Activator.getImageDescriptor(NlsUtil.getAction_image_search_mode()));
		}

		@Override
		public int getStyle() {
			return AS_CHECK_BOX;
		}

		@Override
		public void run() {
			setSearchMode(this.isChecked());
		}
	}

	protected Instrument getCurrentInstrument() {
		return currentInstrument;
	}
}
