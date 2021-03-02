/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.actions.ShowFingeringAction;
import com.plucknplay.csg.ui.actions.ShowIntervalsAction;
import com.plucknplay.csg.ui.actions.ShowNotesAction;
import com.plucknplay.csg.ui.editPartFactories.FretboardEditPartFactory;
import com.plucknplay.csg.ui.editParts.FretboardDraftEditPart;
import com.plucknplay.csg.ui.model.BlockManager;
import com.plucknplay.csg.ui.model.Draft;
import com.plucknplay.csg.ui.model.FretDraft;
import com.plucknplay.csg.ui.model.FretboardDraft;
import com.plucknplay.csg.ui.preferencePages.FretboardViewPreferencePage;

public class FretboardView extends AbstractGraphicalCalculationView implements ISelectionProvider, IModeView {

	public static final String ID = "com.plucknplay.csg.ui.views.FretboardView"; //$NON-NLS-1$
	public static final String HELP_ID = "fretboard_view_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private Block block;
	private boolean blockChanged;
	private IntervalContainer intervalContainer;
	private boolean deactivatedFingeringMode;

	private List<ISelectionChangedListener> listeners;
	private ISelection selection;

	private ShowFingeringAction showFingeringAction;
	private ShowNotesAction showNotesAction;
	private ShowIntervalsAction showIntervalsAction;

	private Button changeRootNoteButton;

	/**
	 * The edit part factory of this editor.
	 */
	private EditPartFactory editPartFactory;

	private final KeyListener blockKeyListener = new KeyAdapter() {
		@Override
		public void keyPressed(final KeyEvent e) {
			if (intervalContainer == null || !showBlocks() || getContent().isEditable()) {
				return;
			}

			Object direction = e.keyCode == SWT.ARROW_UP ? BlockManager.UP
					: e.keyCode == SWT.ARROW_DOWN ? BlockManager.DOWN : e.keyCode == SWT.ARROW_LEFT ? BlockManager.LEFT
							: e.keyCode == SWT.ARROW_RIGHT ? BlockManager.RIGHT : null;

			if (Activator.getDefault().isLeftHander()) {
				if (direction == BlockManager.LEFT) {
					direction = BlockManager.RIGHT;
				} else if (direction == BlockManager.RIGHT) {
					direction = BlockManager.LEFT;
				}
			}

			if (direction == null) {
				return;
			}

			if (BlockManager.getInstance().moveBlock(block, direction)) {
				blockChanged = true;
				setInput(block);
			}
		}
	};

	/**
	 * Returns the edit part factory that the graphical viewer will use.
	 * 
	 * @return the edit part factory that the graphical viewer will use
	 */
	@Override
	protected EditPartFactory getEditPartFactory() {
		if (editPartFactory == null) {
			editPartFactory = new FretboardEditPartFactory();
		}
		return editPartFactory;
	}

	private FretboardDraftEditPart getEditPart() {
		return (FretboardDraftEditPart) getGraphicalViewer().getContents();
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);

		prefs = Activator.getDefault().getPreferenceStore();

		getSite().setSelectionProvider(this);
	}

	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		getGraphicalViewer().getControl().addKeyListener(blockKeyListener);
	}

	@Override
	protected void fillButtonsComposite(final Composite composite) {
		super.fillButtonsComposite(composite);

		// change root note & no barre button
		changeRootNoteButton = createChangeRootNoteAction(composite).getButton();
		createNoBarreAction(composite);
	}

	@Override
	protected Button[] getButtonsWithIndent() {
		return new Button[] { changeRootNoteButton };
	}

	@Override
	protected Draft createDraftContent(final Object input) {
		Draft result = null;
		if (input == null) {
			result = new FretboardDraft();
		} else if (input instanceof IBlock) {
			result = new FretboardDraft((IBlock) input);
		}
		return result;
	}

	@Override
	protected Draft createContentClone() {
		final Draft content = getContent();
		return content != null ? new FretboardDraft((FretboardDraft) content) : null;
	}

	@Override
	protected void setInput(final Object input) {

		// griptable
		if (input == null || input instanceof Griptable) {
			block = null;
			intervalContainer = null;
			setSelection(new StructuredSelection());
			super.setInput(input);
		}

		// bloc
		else if (input instanceof Block && (input != block || blockChanged)) {
			blockChanged = false;
			block = (Block) input;
			intervalContainer = block.getIntervalContainer();
			setSelection(new StructuredSelection(block));
			super.setInput(block);
		}

		// scale or chord
		else if (input instanceof IntervalContainer) {
			intervalContainer = (IntervalContainer) input;
			block = BlockManager.getInstance().getDefaultBlock((IntervalContainer) input);
			setSelection(new StructuredSelection(block));
			super.setInput(block);
		}
	}

	@Override
	protected void updateContentDescription(final boolean isSimilar) {

		if (block != null && showBlockInfo()) {
			final StringBuffer buf = new StringBuffer();
			boolean needSeparator = false;

			// (1) Block Name
			if (prefs.getBoolean(Preferences.VIEWS_SHOW_INFO_INPUT) && isSimilar) {
				buf.append(block.getBeautifiedName(prefs.getString(Preferences.NOTES_MODE)));
				needSeparator = true;
			}

			// (2) Block Navigation Info
			if (prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_BLOCK_NAVIGATION_INFO)) {
				if (needSeparator) {
					buf.append(" ... "); //$NON-NLS-1$
				}
				buf.append(ViewMessages.FretboardView_block_navigation_info);
			}

			setContentDescription(buf.toString());
			refresh();

		} else {
			super.updateContentDescription(isSimilar);
		}
	}

	@Override
	protected boolean setSearchMode(final boolean searchMode) {
		if (!super.setSearchMode(searchMode)) {
			return false;
		}

		if (searchMode) {
			if (showFingeringAction.isChecked() && usePointsMode()) {
				deactivatedFingeringMode = true;
				setMode(UIConstants.MODE_POINTS);
			}
			checkFastEditing();
		} else {
			if (deactivatedFingeringMode && !showNotesAction.isChecked() && !showIntervalsAction.isChecked()) {
				setMode(UIConstants.MODE_FINGERING);
			}
			deactivatedFingeringMode = false;
		}

		return true;
	};

	@Override
	public void dispose() {
		InstrumentList.getInstance().removeChangeListener(this);
		ScaleList.getInstance().removeChangeListener(this);
		ChordList.getInstance().removeChangeListener(this);
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		Activator.getDefault().removeSimpleChangeListener(this);
		super.dispose();
	}

	@Override
	protected String getHelpId() {
		return HELP_ID;
	}

	/* --- actions --- */

	/**
	 * Contribute to menu and toolbar.
	 */
	@Override
	protected void contributeToActionBars() {
		super.contributeToActionBars();

		String mode = prefs.getString(Preferences.FRETBOARD_VIEW_MODE);
		if (!UIConstants.MODE_POINTS.equals(mode) && !UIConstants.MODE_FINGERING.equals(mode)
				&& !UIConstants.MODE_NOTES.equals(mode) && !UIConstants.MODE_INTERVALS.equals(mode)) {
			mode = UIConstants.MODE_FINGERING;
		}

		// create actions
		showFingeringAction = new ShowFingeringAction(this, UIConstants.MODE_FINGERING);
		showFingeringAction.setChecked(UIConstants.MODE_FINGERING.equals(mode));
		showNotesAction = new ShowNotesAction(this, UIConstants.MODE_NOTES);
		showNotesAction.setChecked(UIConstants.MODE_NOTES.equals(mode));
		showIntervalsAction = new ShowIntervalsAction(this, UIConstants.MODE_INTERVALS);
		showIntervalsAction.setChecked(UIConstants.MODE_INTERVALS.equals(mode));

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), showFingeringAction);
		activator.registerAction(getSite(), showNotesAction);
		activator.registerAction(getSite(), showIntervalsAction);

		// add actions to menu and toolbar
		final IActionBars bars = getViewSite().getActionBars();

		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, new Separator());
		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, showFingeringAction);
		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, showNotesAction);
		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, showIntervalsAction);

		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, new Separator());
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, showFingeringAction);
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, showNotesAction);
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, showIntervalsAction);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);
		final String property = event.getProperty();

		final FretboardDraftEditPart editPart = getEditPart();

		// appearance or position of inlays or fret numbers changed
		if (property.equals(Preferences.FRETBOARD_VIEW_SHOW_INLAYS)
				|| property.equals(Preferences.FRETBOARD_VIEW_SHOW_FRET_NUMBERS)
				|| property.equals(Preferences.FRETBOARD_VIEW_INLAYS_POSITION)
				|| property.equals(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_POSITION)) {
			editPart.setInlaysVisible(prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_INLAYS));
			editPart.setFretNumbersVisible(prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_FRET_NUMBERS));
			refresh();
		}

		// inlays changed
		else if (property.equals(Preferences.FRETBOARD_VIEW_INLAYS_GRAY_COLOR)
				|| property.equals(Preferences.FRETBOARD_VIEW_INLAYS_SHAPE)) {
			editPart.refreshInlays();
		}

		// fret numbers mode changed
		else if (property.equals(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_NUMERALS)
				|| property.equals(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_GRAY_COLOR)
				|| property.equals(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_REDUCED_MODE)) {
			editPart.refreshFretNumbers();
		}

		// preferences changed
		else if (Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_FRETBOARD.equals(property)
				|| Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE.equals(property)
				|| Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE.equals(property)
				|| Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK.equals(property)
				|| Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES.equals(property)
				|| Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES.equals(property)
				|| Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK.equals(property)
				|| Preferences.FRETBOARD_VIEW_POINTS_BACKGROUND.equals(property)
				|| Preferences.FRETBOARD_VIEW_FRAME_FINGERING.equals(property)
				|| Preferences.FRETBOARD_VIEW_FINGERING_BACKGROUND.equals(property)
				|| Preferences.FRETBOARD_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE.equals(property)
				|| Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE.equals(property)
				|| Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR.equals(property)
				|| Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE.equals(property)
				|| Preferences.FRETBOARD_VIEW_SHOW_BARRE.equals(property)
				|| Preferences.FRETBOARD_VIEW_BARRE_LINE_WIDTH.equals(property)
				|| Preferences.FRETBOARD_VIEW_BARRE_BAR_BACKGROUND.equals(property)
				|| Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE.equals(property)
				|| Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER.equals(property)
				|| Preferences.NOTES_MODE.equals(property) || Preferences.INTERVAL_NAMES_MODE.equals(property)
				|| Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME.equals(property)
				|| Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME.equals(property)
				|| Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS.equals(property)) {
			editPart.refreshNotes(false);
		}

		else if (Preferences.FRETBOARD_VIEW_BARRE_MODE.equals(property)) {
			editPart.refreshNotes(true);
		}

		// background changed
		else if (property.equals(Preferences.FRETBOARD_VIEW_BACKGROUND_MODE)
				|| property.equals(Preferences.FRETBOARD_VIEW_BACKGROUND_INDEX)) {
			editPart.refreshBackground();
		}

		// block presentation changed
		else if (property.equals(Preferences.FRETBOARD_VIEW_SHOW_BLOCK_PRESENTATION)) {
			editPart.refreshBlock();
			editPart.refreshNotes(false);
		}

		// empty string frame changed
		else if (property.equals(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_FRAME)) {
			editPart.refreshFretboard();
		}

		// block preferences changed
		else if (property.equals(Preferences.BLOCK_MODE) || property.equals(Preferences.FRET_BLOCK_RANGE)
				|| property.equals(Preferences.FRET_BLOCK_USE_EMPTY_STRINGS)
				|| property.equals(Preferences.ADVANCED_FRET_BLOCK_RANGE)
				|| property.equals(Preferences.ADVANCED_FRET_BLOCK_STRING_RANGE_DECREASE)
				|| property.equals(Preferences.ADVANCED_FRET_BLOCK_USE_EMPTY_STRINGS)
				|| property.equals(Preferences.OCTAVE_BLOCK_ONLY_ROOT_NOTES)
				|| property.equals(Preferences.SHOW_BLOCKS) || property.equals(Preferences.ROOT_NOTE)) {

			if (intervalContainer != null && (getContent() == null || !getContent().isEditable())) {
				block = BlockManager.getInstance().getDefaultBlock(intervalContainer);
				setSelection(new StructuredSelection(block));
				super.setInput(block);
			}
			updateContentDescription(true);
		}

		// general preferences changed (naming problems)
		else if (Preferences.GENERAL_FINGERING_MODE.equals(property)
				|| Preferences.GENERAL_FINGERING_MODE_CUSTOM_NOTATION.equals(property)
				|| Preferences.GENERAL_H_NOTE_NAME.equals(property) || Preferences.GENERAL_B_NOTE_NAME.equals(property)
				|| Preferences.ABSOLUTE_NOTE_NAMES_MODE.equals(property)
				|| Preferences.FRETBOARD_VIEW_SHOW_MUTED_STRINGS.equals(property)
				|| property.equals(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE)) {
			refresh();
		}

		// content description changed
		else if (property.equals(Preferences.FRETBOARD_VIEW_SHOW_BLOCK_NAVIGATION_INFO)) {
			updateContentDescription(true);
		}
	}

	@Override
	public void notifyChange(final Object property, final Object value) {

		// update find chord action when draft has changed
		if (property == FretDraft.PROP_ASSIGNMENT_CHANGED) {
			updateFindButtons();
			updateClearAction();
			updateChangeRootNoteButton();
			updateShowBarreButton();
		}

		if (property == FretDraft.PROP_SHOW_BARRE_CHANGED) {
			updateShowBarreButton();
		}

		// hand changed
		if (property == Activator.PROP_HAND_CHANGED) {
			refresh();
		}
	}

	@Override
	public int getExportHeight() {
		final int exportHeight = prefs.getInt(Preferences.FRETBOARD_VIEW_EXPORT_HEIGHT);
		final boolean liveSize = prefs.getBoolean(Preferences.FRETBOARD_VIEW_EXPORT_LIVE_SIZE);
		return liveSize ? -1 : exportHeight;
	}

	/* --- selection provider --- */

	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<ISelectionChangedListener>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public ISelection getSelection() {
		return selection;
	}

	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		if (listeners == null) {
			return;
		}
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			listeners = null;
		}
	}

	@Override
	public void setSelection(final ISelection selection) {
		this.selection = selection;
		if (listeners == null) {
			return;
		}
		final List<ISelectionChangedListener> tempListeners = new ArrayList<ISelectionChangedListener>(listeners);
		for (final ISelectionChangedListener iSelectionChangedListener : tempListeners) {
			iSelectionChangedListener.selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}

	@Override
	protected String getPreferencePageID() {
		return FretboardViewPreferencePage.ID;
	}

	@Override
	public void setMode(final String mode) {
		getEditPart().setMode(mode);

		// store preference
		prefs.setValue(Preferences.FRETBOARD_VIEW_MODE, mode);

		// update check state
		// (necessary if actions was performed via key binding)
		showFingeringAction.setChecked(UIConstants.MODE_FINGERING.equals(mode));
		showNotesAction.setChecked(UIConstants.MODE_NOTES.equals(mode));
		showIntervalsAction.setChecked(UIConstants.MODE_INTERVALS.equals(mode));

		checkFastEditing();
	}
}
